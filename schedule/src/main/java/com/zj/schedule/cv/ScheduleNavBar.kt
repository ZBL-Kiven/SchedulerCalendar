package com.zj.schedule.cv

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.zj.schedule.cv.i.MeetingItemIn
import com.zj.schedule.cv.i.Status
import com.zj.dtv.DrawableTextView
import com.zj.schedule.R
import com.zj.schedule.utl.Utl

class ScheduleNavBar @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : ConstraintLayout(c, attr, def) {

    private lateinit var vInvite: DrawableTextView
    private lateinit var vStart: DrawableTextView
    private lateinit var vEdit: DrawableTextView
    private lateinit var vCancel: DrawableTextView
    private lateinit var vJoin: DrawableTextView

    init {
        inflate(c, R.layout.calendar_schedule_detail_nav_layout, this)
        initView()
    }

    private fun initView() {
        vInvite = findViewById(R.id.calendar_schedule_detail_nav_invite)
        vStart = findViewById(R.id.calendar_schedule_detail_nav_start)
        vEdit = findViewById(R.id.calendar_schedule_detail_nav_edit)
        vCancel = findViewById(R.id.calendar_schedule_detail_nav_cancel)
        vJoin = findViewById(R.id.calendar_schedule_detail_nav_join)
    }

    fun setData(item: MeetingItemIn) {
        if (item.getStatus() == Status.Ended || item.getStatus() == Status.Canceled) {
            visibility = GONE
        } else {
            visibility = VISIBLE
            children.forEach {
                it.isVisible = true
            }
            val all = item.getStatus() == Status.Progressing && item.selfIsOwner()
            if (all) return
            vStart.isVisible = item.selfIsOwner() && item.getStatus() == Status.Feature
            vEdit.isVisible = item.selfIsOwner()
            vCancel.isVisible = item.selfIsOwner()
            vJoin.isVisible = item.selfIsOwner() || item.getStatus() == Status.Progressing
        }
        initListener(item)
    }

    private fun initListener(item: MeetingItemIn) {
        vInvite.setOnClickListener {
            Utl.config?.meetingFuncIn?.invite(item.getMeetingId().toLong())
        }
        vStart.setOnClickListener {
            Utl.config?.meetingFuncIn?.start(item.getMeetingId().toLong())
        }
        vEdit.setOnClickListener {
            Utl.config?.meetingFuncIn?.edit(item.getMeetingId().toLong())
        }
        vCancel.setOnClickListener {
            Utl.config?.meetingFuncIn?.cancel(item.getMeetingId().toLong())
        }
        vJoin.setOnClickListener {
            Utl.config?.meetingFuncIn?.joinMeeting(item.getMeetingId().toLong())
        }
    }
}