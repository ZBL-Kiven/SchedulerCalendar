package com.zj.swipeRv.data

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zj.api.call
import com.zj.api.interfaces.RequestCancelable
import com.zj.swipeRv.data.api.ScheduleApi
import com.zj.swipeRv.data.entity.CalendarMeetingInfo
import com.zj.swipeRv.utl.Utl
import io.reactivex.schedulers.Schedulers
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

object ScheduleInfoReqQueue {

    private const val REQ_KEY = 0x1121
    private const val cacheAble = true
    private var curData = ConcurrentHashMap<String, List<CalendarMeetingInfo>>()
    private var reqCancelAbleData = mutableMapOf<String, RequestCancelable>()
    private val requesting = mutableMapOf<String, ScheduleReqBean>()
    private var req: ScheduleReq? = null

    private val antiShakeHandler = Handler(Looper.getMainLooper()) {
        val bean = it.obj as ScheduleReqBean
        if (!cacheAble) {
            startReq(bean)
        } else {
            if (curData.containsKey(bean.getReqKey())) {
                requesting.remove(bean.getReqKey())
                req?.req(bean, true, curData[bean.getReqKey()], null)
            } else {
                startReq(bean)
            }
        }
        return@Handler false
    }

    internal fun setSeq(req: ScheduleReq) {
        this.req = req
    }

    internal fun request(bean: ScheduleReqBean) {
        antiShakeHandler.removeMessages(REQ_KEY)
        val msg = Message.obtain()
        msg.obj = bean
        antiShakeHandler.sendMessageDelayed(msg, 1000)
        req?.start(bean)
    }

    private fun startReq(bean: ScheduleReqBean, req: Boolean = false) {
        var rqb = requesting[bean.getReqKey()]
        if (rqb == null) {
            rqb = bean
            requesting[bean.getReqKey()] = rqb
            startReq(bean, true)
            return
        }
        if (req) {
            ScheduleFetcher(bean)
        }
    }

    internal fun cancelAll() {
        reqCancelAbleData.forEach { (s, requestCancelable) ->
            requestCancelable.cancel("$s has been canceled by user")
        }
        requesting.forEach { (_, scheduleReqBean) ->
            req?.onCanceled(scheduleReqBean)
        }
        requesting.clear()
        reqCancelAbleData.clear()
    }

    private class ScheduleFetcher(private val bean: ScheduleReqBean) {
        init {
            val cancelable = ScheduleApi.getMeetingList(bean.startTime, bean.endTime).call(observableSchedulers = Schedulers.io()) { isSuccess, data, throwable ->
                antiShakeHandler.post {
                    if (isSuccess && !data.isNullOrEmpty()) {
                        curData[bean.getReqKey()] = data
                    }
                    requesting.remove(bean.getReqKey())
                    req?.req(bean, isSuccess, data, throwable)
                }
            }
            reqCancelAbleData[bean.getReqKey()] = cancelable
        }
    }

    internal interface ScheduleReq {
        fun req(bean: ScheduleReqBean, success: Boolean, data: List<CalendarMeetingInfo>?, t: Throwable?)
        fun onCanceled(bean: ScheduleReqBean)
        fun start(bean: ScheduleReqBean)
    }

    data class ScheduleReqBean(val startTime: Long, val endTime: Long) : Serializable {

        fun getReqKey(): String {
            return "${Utl.config?.getUserId()}&$startTime&$endTime"
        }
    }
}