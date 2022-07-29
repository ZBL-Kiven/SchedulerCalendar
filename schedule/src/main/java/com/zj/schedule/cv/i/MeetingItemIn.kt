package com.zj.schedule.cv.i

import java.io.Serializable
import java.util.*


interface MeetingItemIn : Serializable {
    fun getMeetingStartTime(): Long
    fun getMeetingEndTime(): Long
    fun getDuration(): Long
    fun getStatus(): Status
    fun getMeetingName(): String
    fun selfIsOwner(): Boolean
    fun hasBeenRemoved(): Boolean
    fun hasFiles(): Boolean
    fun getMeetingId(): String
    fun getMeetingKey(): String
    fun getMeetingSecret(): String
    fun getMeetingOwnerName(): String
    fun getHostName(): String
    fun getMeetingOwnerId(): Long
    fun getHostId(): Long

    fun getStartCalendar(): Calendar {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = getMeetingStartTime()
        return startCalendar
    }

    fun getEndCalendar(): Calendar {
        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = getMeetingEndTime()
        return endCalendar
    }
}