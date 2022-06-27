package com.zj.swipeRv.cv

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zj.swipeRv.R
import com.zj.swipeRv.cv.i.MeetingItemIn
import com.zj.swipeRv.cv.i.MeetingMemberIn
import com.zj.swipeRv.cv.i.Status
import com.zj.swipeRv.utl.Utl
import com.zj.dtv.DrawableTextView
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class ScheduleHeaderView @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : ConstraintLayout(c, attr, def) {

    private lateinit var tvName: TextView
    private lateinit var tvMember: TextView
    private lateinit var tvFolder: TextView
    private lateinit var tvStatus: TextView
    private lateinit var dtvTime: DrawableTextView
    private lateinit var dtvId: DrawableTextView
    private lateinit var dtvMeetingKey: DrawableTextView
    private lateinit var dtvOrganizer: DrawableTextView
    private lateinit var dtvHost: DrawableTextView
    private lateinit var clUsers: CollapsedLayout
    private lateinit var llMembers: LinearLayout
    private val maxHeadPicSize = 6

    init {
        inflate(c, R.layout.calendar_schedule_detail_header_layout, this)
        initView()
    }

    private fun initView() {
        tvName = findViewById(R.id.calendar_schedule_detail_tv_name)
        tvMember = findViewById(R.id.calendar_schedule_detail_tv_member)
        tvFolder = findViewById(R.id.calendar_schedule_detail_tv_files)
        tvStatus = findViewById(R.id.calendar_schedule_detail_tv_status)
        dtvTime = findViewById(R.id.calendar_schedule_detail_dtv_time)
        dtvId = findViewById(R.id.calendar_schedule_detail_dtv_id)
        dtvMeetingKey = findViewById(R.id.calendar_schedule_detail_dtv_key)
        dtvOrganizer = findViewById(R.id.calendar_schedule_detail_dtv_owner)
        dtvHost = findViewById(R.id.calendar_schedule_detail_dtv_host)
        llMembers = findViewById(R.id.calendar_schedule_detail_ll_member)
        clUsers = findViewById(R.id.calendar_schedule_detail_cl_ivs)
    }

    fun setData(item: MeetingItemIn) {
        tvName.text = item.getMeetingName()
        tvFolder.isVisible = item.hasFiles()
        val startCalendar = item.getStartCalendar()
        val endCalendar = item.getEndCalendar()
        val y1 = startCalendar.get(Calendar.YEAR)
        val m1 = startCalendar.get(Calendar.MONTH)
        val w1 = startCalendar.get(Calendar.DAY_OF_WEEK)
        val day1 = startCalendar.get(Calendar.DAY_OF_MONTH)
        val y2 = endCalendar.get(Calendar.YEAR)
        val m2 = endCalendar.get(Calendar.MONTH)
        val day2 = endCalendar.get(Calendar.DAY_OF_MONTH)
        val w2 = endCalendar.get(Calendar.DAY_OF_WEEK)
        val date1 = startCalendar.time ?: 0
        val date2 = endCalendar.time ?: 0
        val s1 = SimpleDateFormat("dd, HH:mm", Locale.getDefault()).format(date1)
        val sb = StringBuilder("${Utl.getWeekString(w1)}, ${Utl.getMonthString(m1)} $s1 - ")
        if (y1 != y2 || m1 != m2 || day1 != day2) {
            sb.append("${Utl.getWeekString(w2)}, ${Utl.getMonthString(m2)} ${Utl.getDigitsDay(day2)}, ")
        }
        sb.append(SimpleDateFormat("HH:mm", Locale.getDefault()).format(date2))
        dtvTime.text = sb.toString()
        dtvMeetingKey.text = context.getString(R.string.MeetingKey_, item.getMeetingKey())
        dtvId.text = context.getString(R.string.ID_, item.getMeetingId())
        dtvOrganizer.text = context.getString(R.string.Organizer_, item.getOwnerName())
        val d = tvStatus.background
        tvStatus.background = Utl.tintDrawable(d, item.getStatus().color)
        val color = if (item.getStatus() == Status.Canceled || item.getStatus() == Status.Ended) ContextCompat.getColor(context, R.color.c_6fff) else Color.WHITE
        tvStatus.setTextColor(color)
        tvStatus.text = context.getString(item.getStatus().strId)
        dtvHost.text = context.getString(R.string.host_, item.getHostName())
        dtvHost.isVisible = item.getHostName().isNotEmpty()
        tvFolder.text = context.getString(R.string.Meeting_Folder_, 0)
    }

    fun setFoldersCount(count: Int) {
        tvFolder.text = context.getString(R.string.Meeting_Folder_, count)
    }

    fun <T : MeetingMemberIn> setMembers(m: List<T>) {
        if (clUsers.height <= 0) clUsers.post { obtainMembers(m) } else obtainMembers(m)
    }

    private fun <T : MeetingMemberIn> obtainMembers(m: List<T>) {
        clUsers.removeAllViews()
        llMembers.isVisible = true
        if (m.isEmpty()) {
            llMembers.isVisible = false
            return
        }
        val needMoreText = m.size > maxHeadPicSize
        tvMember.text = context.getString(R.string.Member_, m.size)
        repeat(m.size.coerceAtMost(maxHeadPicSize)) {
            val avatarView = CusUserBgView(context)
            clUsers.addView(avatarView, clUsers.height, clUsers.height)
            avatarView.setData(m[it])
        }
        if (needMoreText) {
            val tv = TextView(context)
            tv.background = ContextCompat.getDrawable(context, R.drawable.circle_f65f2a)
            val more = m.size - maxHeadPicSize
            val sb = StringBuilder()
            if (more > 99) {
                sb.append("$more+")
            } else {
                sb.append("+$more")
            }
            tv.text = sb
            tv.gravity = Gravity.CENTER
            val ps = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics).toInt()
            tv.setPadding(ps, 0, 0, 0)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            tv.setTextColor(Color.WHITE)
            clUsers.addView(tv, clUsers.height, clUsers.height)
        }
    }
}