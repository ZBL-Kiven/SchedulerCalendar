package com.zj.schedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.zj.calendar.Calendar
import com.zj.calendar.CalendarLayout
import com.zj.calendar.CalendarView
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.cf.managers.ConstrainFragmentManager
import com.zj.cf.startFragment
import com.zj.loading.DisplayMode
import com.zj.loading.ZRotateLoadingView
import com.zj.schedule.cv.CollapsedRecyclerView
import com.zj.schedule.cv.ScheduleStatusItemView
import com.zj.schedule.cv.i.ScheduleItemIn
import com.zj.schedule.data.ScheduleInfoReqQueue
import com.zj.schedule.data.entity.CalendarMeetingInfo
import com.zj.schedule.utl.Config
import com.zj.schedule.utl.InitScheduleInfo
import com.zj.schedule.utl.Utl
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.listeners.ItemClickListener
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

@Constrain(id = "CalendarFragment", backMode = 1)
class CalendarFragment : ConstrainFragment(), CalendarView.OnCalendarSelectListener, CalendarView.OnYearChangeListener, CalendarView.OnViewChangeListener, ScheduleInfoReqQueue.ScheduleReq {

    companion object {

        private const val SCHEDULE_ID = "schedule_id"

        fun start(act: AppCompatActivity, vg: ViewGroup, config: Config, scheduleInfo: InitScheduleInfo? = null): ConstrainFragmentManager {
            Utl.config = config
            val b = bundleOf(Pair(SCHEDULE_ID, scheduleInfo))
            return act.startFragment(CalendarFragment::class.java, vg, b, { true }, null)
        }
    }

    private var mTextMonthDay: TextView? = null
    private var mTextYear: TextView? = null
    private var mCalendarView: CalendarView? = null
    private var mYear = 0
    private var mCalendarLayout: CalendarLayout? = null
    private var mIvBack: ImageView? = null
    private var mIvPre: View? = null
    private var mIvNext: View? = null
    private var scheduleInfo: InitScheduleInfo? = null
    private var mRvSchedule: CollapsedRecyclerView? = null
    private var blv: ZRotateLoadingView? = null
    private var adapter = ScheduleAdapter()
    private var curReqKey: String = ""

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.calendar_schedule_layout, container, false)
    }

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        scheduleInfo = bundle?.getSerializable(SCHEDULE_ID) as InitScheduleInfo?
    }

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        mTextMonthDay = find(R.id.calendar_tv_month)
        mTextYear = find(R.id.calendar_tv_year)
        mCalendarLayout = find(R.id.calendar_calendar_layout)
        mCalendarView = find(R.id.calendar_schedule_calendar_view)
        mIvBack = find(R.id.calendar_iv_back)
        mIvPre = find(R.id.calendar_iv_pre)
        mIvNext = find(R.id.calendar_iv_next)
        mRvSchedule = find(R.id.calendar_rv_schedule)
        blv = find(R.id.calendar_blv_loading)
        mIvPre?.setOnClickListener { mCalendarView?.scrollToPre() }
        mIvNext?.setOnClickListener { mCalendarView?.scrollToNext() }
        mTextYear?.setOnClickListener {
            if (mCalendarLayout?.isExpand == false) {
                mCalendarLayout?.expand()
            } else {
                mCalendarView?.showMonthOfYearLayout(mYear)
            }
        }
        mRvSchedule?.translationWith(blv)
        mCalendarView?.setOnCalendarSelectListener(this)
        mCalendarView?.setOnYearChangeListener(this)
        mCalendarView?.setOnViewChangeListener(this)
        mRvSchedule?.adapter = adapter
        mYear = mCalendarView?.curYear ?: 0
        mTextMonthDay?.text = mCalendarView?.monthName
        mTextYear?.text = "$mYear"
    }

    //test
    private fun initData() {
        scheduleInfo?.let {
            mCalendarView?.scrollToCalendar(Date(it.time))
        }
        ScheduleInfoReqQueue.setSeq(this)
        mIvBack?.setOnClickListener {
            finish()
        }
        mCalendarView?.setOnYearViewChangeListener(object : CalendarView.OnYearViewChangeListener {

            override fun onYearViewChange(isClose: Boolean) {
                this@CalendarFragment.blv?.let {
                    if ((it.getTag(1) as? Boolean) != true && isClose) {
                        it.setTag(it.id, true)
                        it.postDelayed({
                            it.translationX = 0f
                            it.setTag(it.id, false)
                        }, 500)
                    }
                }
            }

            override fun startChangeYearView(show: Boolean, duration: Long) {
                this@CalendarFragment.blv?.let {
                    if (!show) {
                        it.post {
                            it.translationX = 2000f
                        }
                    }
                }
            }
        })
    }

    override fun onYearChange(year: Int) {
        mTextMonthDay?.text = year.toString()
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        mTextYear?.visibility = View.VISIBLE
        mTextMonthDay?.text = calendar.getMonthName(this.context)
        mTextYear?.text = "${calendar.year}"
        mYear = calendar.year
        val monthRange = calendar.monthDayRange
        val req = ScheduleInfoReqQueue.ScheduleReqBean(monthRange[0], monthRange[1])
        curReqKey = req.getReqKey()
        ScheduleInfoReqQueue.request(req)
    }

    override fun onViewChange(isMonthView: Boolean) {
        mRvSchedule?.collapsed = !isMonthView
    }

    override fun req(bean: ScheduleInfoReqQueue.ScheduleReqBean, success: Boolean, data: List<CalendarMeetingInfo>?, t: Throwable?) {
        if (isStop || bean.getReqKey() != curReqKey) return
        if (!success || data.isNullOrEmpty()) {
            blv?.setMode(DisplayMode.NO_DATA)
        } else {
            try {
                val cs = mCalendarView?.selectedCalendar ?: throw IllegalArgumentException("no selected calendar!!")
                setScheduleData(data)
                val range = cs.dayRange
                val filter = data.filter {
                    it.getMeetingStartTime() in range[0]..range[1]
                }
                adapter.change(filter)
                if (filter.isEmpty()) {
                    blv?.setMode(DisplayMode.NO_DATA)
                } else {
                    checkAnim()
                    blv?.setMode(DisplayMode.NORMAL)
                }
            } catch (e: Exception) {
                blv?.setMode(DisplayMode.NO_DATA)
            }
        }
    }

    override fun onCanceled(bean: ScheduleInfoReqQueue.ScheduleReqBean) {
        if (!isStop) {
            if (bean.getReqKey() != curReqKey) return
            blv?.setMode(DisplayMode.NO_DATA)
        }
    }

    override fun start(bean: ScheduleInfoReqQueue.ScheduleReqBean) {
        if (!isStop) {
            if (bean.getReqKey() != curReqKey) return
            adapter.clear()
            blv?.setMode(DisplayMode.LOADING)
        }
    }

    private fun setScheduleData(data: List<ScheduleItemIn>?) {
        if (data.isNullOrEmpty()) {
            adapter.clear()
            mCalendarView?.clearScheduleDate()
        } else {
            val schedules = arrayListOf<Calendar>()
            data.forEach {
                val c = Calendar()
                c.timeInMillis = it.getMeetingStartTime()
                c.newSchedule(Color.WHITE, it.getScheduleName())
                schedules.add(c)
            }
            mCalendarView?.setScheduleDate(schedules)
        }
    }

    private fun checkAnim() {
        if (scheduleInfo?.scheduleId.isNullOrEmpty()) return
        val rv = mRvSchedule ?: return
        val index = adapter.data?.indexOfFirst {
            it.getMeetingId() == scheduleInfo?.scheduleId
        } ?: return
        if (index in 0..adapter.itemCount) {
            rv.scrollToPosition(index)
            adapter.notifyItemChanged(index, "checkAnim")
        }
    }

    inner class ScheduleAdapter : BaseAdapter<ScheduleItemIn>(R.layout.calendar_item_schedule) {

        init {
            setOnItemClickListener(object : ItemClickListener<ScheduleItemIn>() {
                override fun onItemClick(position: Int, v: View?, m: ScheduleItemIn?) {
                    if (v == null || m == null) return
                    val b = bundleOf(Pair("MeetingItem", m))
                    this@CalendarFragment.startFragment(MeetingDetailFragment::class.java, b)
                }
            })
        }

        override fun initData(holder: BaseViewHolder<ScheduleItemIn>?, position: Int, module: ScheduleItemIn?, payloads: MutableList<Any>?) {
            if (module == null) return
            holder?.getView<TextView>(R.id.calendar_item_tv_start_time)?.let {
                val cs = mCalendarView?.selectedCalendar?.timeInMillis ?: module.getMeetingStartTime()
                it.text = Utl.getDisplayTimeStr(cs, module.getMeetingStartTime())
            }
            holder?.getView<TextView>(R.id.calendar_item_tv_end_time)?.let {
                it.text = Utl.getDisplayTimeStr(module.getMeetingStartTime(), module.getMeetingEndTime())
            }
            holder?.getView<ScheduleStatusItemView<ScheduleItemIn>>(R.id.calendar_item_cl)?.let {
                it.setData(module)
                if (payloads?.contains("checkAnim") == true && module.getMeetingId() == scheduleInfo?.scheduleId) {
                    it.showHintAnim()
                    scheduleInfo = null
                } else {
                    it.stopHintAnim()
                }
            }
        }
    }

    override fun finish(onFinished: ((success: Boolean, inTopOfStack: Boolean) -> Unit)?) {
        super.finish(onFinished)
        ScheduleInfoReqQueue.cancelAll()
    }
}