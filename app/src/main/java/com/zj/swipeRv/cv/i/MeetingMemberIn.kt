package com.zj.swipeRv.cv.i

import android.graphics.Color

interface MeetingMemberIn {

    fun getHeadPic(): String

    fun getId(): Long

    fun hashColor(): Int {
        val index = getId().hashCode() % colorList.size
        return Color.parseColor(colorList[index])
    }

    companion object {

        private val colorList = arrayOf("#9586FB", "#ECB0CD", "#5DD8D0", "#FBAB4D", "#FF9B91", "#628CFF", "#DBE16A", "#95E284", "#8B93DC", "#D98CC2")

    }

}