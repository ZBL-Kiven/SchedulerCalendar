package com.zj.swipeRv.test

import com.zj.swipeRv.cv.i.ScheduleItemIn
import com.zj.swipeRv.cv.i.Status
import java.util.*

class TestScheduleData private constructor(var st: Long, var et: Long) : ScheduleItemIn {

    override fun getStartTime(): Long {
        return st
    }

    override fun getEndTime(): Long {
        return et
    }

    override fun getStatus(): Status {
        return Status.Progressing
    }

    override fun getMeetingName(): String {
        return "TEST 01 MEETING"
    }

    override fun selfIsOwner(): Boolean {
        return true
    }

    override fun getScheduleName(): String {
        return "TEST 01"
    }

    override fun hasFiles(): Boolean {
        return true
    }

    override fun getMeetingId(): String {
        return "1000001"
    }

    override fun getMeetingKey(): String {
        return "2322 3322 234"
    }

    override fun getOwnerName(): String {
        return "Master"
    }

    override fun getHostName(): String {
        return "Worker"
    }

    override fun getOwnerId(): Long {
        return 1000356
    }

    override fun getHostId(): Long {
        return 1000356
    }

    companion object {

        fun getCurDayCalendarInfo(index: Int): TestScheduleData {
            val c = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val startHour = index * 2
            val endHour = index * 2 + 2
            val cs = Calendar.getInstance()
            val ce = Calendar.getInstance()
            cs.set(year, month, day, startHour, Random().nextInt(60), 0)
            ce.set(year, month, day, endHour, Random().nextInt(60), 0)
            return TestScheduleData(cs.timeInMillis, ce.timeInMillis)
        }
    }
}