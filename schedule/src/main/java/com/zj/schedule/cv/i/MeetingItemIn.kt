package com.zj.schedule.cv.i

import java.io.Serializable


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
    fun getMeetingOwnerName(): String
    fun getHostName(): String
    fun getMeetingOwnerId(): Long
    fun getHostId(): Long
}