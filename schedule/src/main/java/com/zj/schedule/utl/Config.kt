package com.zj.schedule.utl

import com.zj.schedule.cv.i.MeetingFuncIn

interface Config {

    fun getApiHost(): String

    fun getUserId(): Long

    fun getToken(): String

    fun getNickName(): String

    fun getAvatar(): String

    fun getHeader(): Map<String, String>

    val meetingFuncIn: MeetingFuncIn
}