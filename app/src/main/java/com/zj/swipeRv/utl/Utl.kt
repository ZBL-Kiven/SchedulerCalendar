package com.zj.swipeRv.utl

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

object Utl {


    fun getMonthString(month: Int?): String {
        return when (month) {
            0 -> "Jan"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dec"
            else -> "NAN"
        }
    }

    fun getDigitsDay(day: Int): String {
        return if (day < 10) "0$day" else "$day"
    }

    fun getWeekString(week: Int?): String {
        return when (week) {
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thu"
            5 -> "Fri"
            6 -> "Sat"
            7 -> "Sun"
            else -> ""
        }
    }

    fun tintDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }

}