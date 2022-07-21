package com.zj.schedule.utl

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import java.text.SimpleDateFormat
import java.util.*

internal object Utl {

    var config: Config? = null


    fun getDisplayTimeStr(compare: Long, cur: Long, stay: Int = 0): String {
        val startCalendar = getStartCalendar(compare)
        val endCalendar = getEndCalendar(cur)
        val y1 = startCalendar.get(Calendar.YEAR)
        val m1 = startCalendar.get(Calendar.MONTH) + 1
        val day1 = startCalendar.get(Calendar.DAY_OF_MONTH)
        val y2 = endCalendar.get(Calendar.YEAR)
        val m2 = endCalendar.get(Calendar.MONTH) + 1
        val day2 = endCalendar.get(Calendar.DAY_OF_MONTH)
        val sb = StringBuilder()
        if (y1 != y2 || stay >= 3) {
            sb.append("yyyy-")
        }
        if (y1 != y2 || m1 != m2 || stay >= 2) {
            sb.append("MM-")
        }
        if (day1 != day2 || stay >= 1) {
            sb.append("dd ")
        }
        sb.append("HH:mm")
        return SimpleDateFormat(sb.toString(), Locale.getDefault()).format(Date(cur))
    }

    fun tintDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }

    private fun getStartCalendar(ts: Long): Calendar {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = ts
        return startCalendar
    }

    private fun getEndCalendar(ts: Long): Calendar {
        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = ts
        return endCalendar
    }

}