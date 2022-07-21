package com.zj.schedule.cv

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
import com.zj.dtv.DrawableTextView
import com.zj.schedule.R
import com.zj.schedule.cv.i.MeetingItemIn
import com.zj.schedule.cv.i.MeetingMemberIn
import com.zj.schedule.cv.i.Status
import com.zj.schedule.utl.Utl

class ScheduleHeaderView @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : ConstraintLayout(c, attr, def) {

    private lateinit var tvName: TextView
    private lateinit var tvMember: TextView
    private lateinit var tvFolder: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvRemoved: TextView
    private lateinit var dtvTime: DrawableTextView
    private lateinit var dtvId: DrawableTextView
    private lateinit var dtvMeetingKey: DrawableTextView
    private lateinit var dtvOrganizer: DrawableTextView
    private lateinit var dtvHost: DrawableTextView
    private lateinit var dtvDuration: DrawableTextView
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
        dtvDuration = findViewById(R.id.calendar_schedule_detail_dtv_duration)
        llMembers = findViewById(R.id.calendar_schedule_detail_ll_member)
        clUsers = findViewById(R.id.calendar_schedule_detail_cl_ivs)
        tvRemoved = findViewById(R.id.calendar_schedule_detail_tv_removed)
    }

    fun setData(item: MeetingItemIn) {
        tvName.text = item.getMeetingName()
        val sb = StringBuilder()
        sb.append(Utl.getDisplayTimeStr(item.getMeetingStartTime(), item.getMeetingStartTime(), 2))
        sb.append(" ~ ")
        sb.append(Utl.getDisplayTimeStr(item.getMeetingStartTime(), item.getMeetingEndTime()))
        dtvTime.text = sb.toString()
        val d = tvStatus.background
        tvStatus.background = Utl.tintDrawable(d, item.getStatus().color)
        val color = if (item.getStatus() == Status.Canceled || item.getStatus() == Status.Ended) ContextCompat.getColor(context, R.color.c_6fff) else Color.WHITE
        tvStatus.setTextColor(color)
        tvStatus.text = context.getString(item.getStatus().strId)
        dtvMeetingKey.isVisible = !item.hasBeenRemoved()
        dtvHost.isVisible = item.getHostName().isNotEmpty() && !item.hasBeenRemoved()
        dtvId.isVisible = !item.hasBeenRemoved()
        dtvOrganizer.isVisible = !item.hasBeenRemoved()
        tvFolder.isVisible = item.hasFiles() && !item.hasBeenRemoved()
        tvRemoved.isVisible = item.hasBeenRemoved()
        dtvDuration.isVisible = if (item.getStatus() == Status.Ended && item.getDuration() > 0) {
            val sb1 = StringBuilder()
            val diff = item.getDuration() / 1000
            val h = (diff / 60f / 60f).toInt()
            if (h > 0) {
                sb1.append("$h" + "h")
            }
            val m = (diff / 60f).toInt() % 60
            if (m > 0) {
                sb1.append("$m" + "m")
            }
            val s = diff % 60
            if (s > 0) {
                sb1.append("$s" + "s")
            }
            dtvDuration.text = sb1.toString()
            sb.isNotEmpty()
        } else false
        if (item.hasBeenRemoved()) return
        dtvMeetingKey.setOnBadgeClickListener {
            it.isSelected = !it.isSelected
            updateMeetingKeyState(item)
        }
        updateMeetingKeyState(item)
        dtvHost.text = context.getString(R.string.host_, item.getHostName())
        dtvId.text = context.getString(R.string.ID_, item.getMeetingId())
        dtvOrganizer.text = context.getString(R.string.Organizer_, item.getMeetingOwnerName())
        tvFolder.text = context.getString(R.string.Meeting_Folder_, 0)
    }

    fun setFoldersCount(count: Int) {
        tvFolder.isVisible = count > 0
        tvFolder.text = context.getString(R.string.Meeting_Folder_, count)
    }

    fun <T : MeetingMemberIn> setMembers(m: List<T>?) {
        if (clUsers.height <= 0) clUsers.post { obtainMembers(m) } else obtainMembers(m)
    }

    private fun updateMeetingKeyState(item: MeetingItemIn) {
        if (dtvMeetingKey.isSelected) {
            val key = item.getMeetingKey().let {
                val sb = StringBuilder()
                repeat(it.length) {
                    sb.append("✲")
                }
                sb.toString()
            }
            dtvMeetingKey.textSelected = context.getString(R.string.MeetingKey_, key)
        } else {
            val key = item.getMeetingKey()
            dtvMeetingKey.text = context.getString(R.string.MeetingKey_, key)
        }
    }

    private fun <T : MeetingMemberIn> obtainMembers(m: List<T>?) {
        clUsers.removeAllViews()
        llMembers.isVisible = true
        if (m.isNullOrEmpty()) {
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