package com.zj.swipeRv.cv.i

import java.io.Serializable
import java.util.*


interface MeetingItemIn : Serializable {
    fun getStartTime(): Long
    fun getEndTime(): Long
    fun getStatus(): Status
    fun getMeetingName(): String
    fun selfIsOwner(): Boolean
    fun hasFiles(): Boolean
    fun getMeetingId(): String
    fun getMeetingKey(): String
    fun getOwnerName(): String
    fun getHostName(): String
    fun getOwnerId(): Long
    fun getHostId(): Long

    fun getStartCalendar(): Calendar {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = getStartTime()
        return startCalendar
    }

    fun getEndCalendar(): Calendar {
        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = getEndTime()
        return endCalendar
    }
}