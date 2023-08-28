package com.zj.schedule.cv.i

interface MeetingFuncIn {

    fun joinMeeting(meetingId: Long)

    fun invite(meetingId: Long)

    fun start(meetingId: Long)

    fun edit(meetingId: Long)

    fun cancel(meetingId: Long)
}