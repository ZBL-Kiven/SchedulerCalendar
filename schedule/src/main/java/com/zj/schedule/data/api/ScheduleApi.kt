package com.zj.schedule.data.api

import com.zj.api.ZApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.LogLevel
import com.zj.api.interceptor.UrlProvider
import com.zj.api.interceptor.plus
import com.zj.schedule.utl.Utl

private val scheduleHeader = object : HeaderProvider {
    override fun headers(): Map<out String, String?> {
        return Utl.config?.getHeader() ?: hashMapOf()
    }
}

private val scheduleUrl = object : UrlProvider() {
    override fun url(): String {
        return Utl.config?.getApiHost() ?: ""
    }
}

internal val ScheduleApi by lazy {
    val logLevel = LogLevel.RESULT_BODY + LogLevel.REQUEST_BODY + LogLevel.BASIC + LogLevel.HEADERS
    ZApi.create(ScheduleService::class.java).timeOut(5000).header(scheduleHeader).logLevel(logLevel).baseUrl(scheduleUrl).build()
}

internal object Body {

    data class DeleteFiles(val meetingId: Long, val fileId: Long)
}

internal object Resp {

    data class SuccessBoolean(var success: Boolean)


}