package com.zj.swipeRv.cv

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.zj.swipeRv.R
import com.zj.swipeRv.cv.i.MeetingItemIn
import com.zj.swipeRv.cv.i.Status
import com.zj.dtv.DrawableTextView

class ScheduleNavBar @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : LinearLayout(c, attr, def) {

    private lateinit var vInvite: DrawableTextView
    private lateinit var vStart: DrawableTextView
    private lateinit var vEdit: DrawableTextView
    private lateinit var vancel: DrawableTextView
    private lateinit var vJoin: DrawableTextView

    init {
        inflate(c, R.layout.calendar_schedule_detail_nav_layout, this)
        initView()
    }

    private fun initView() {
        vInvite = findViewById(R.id.calendar_schedule_detail_nav_invite)
        vStart = findViewById(R.id.calendar_schedule_detail_nav_start)
        vEdit = findViewById(R.id.calendar_schedule_detail_nav_edit)
        vancel = findViewById(R.id.calendar_schedule_detail_nav_cancel)
        vJoin = findViewById(R.id.calendar_schedule_detail_nav_join)
    }

    fun setData(item: MeetingItemIn) {
        if (item.getStatus() == Status.Ended || item.getStatus() == Status.Canceled) {
            visibility = GONE
        } else {
            visibility = VISIBLE
            val all = item.getStatus() == Status.Progressing && item.selfIsOwner()
            children.forEach {
                it.isVisible = all || false
            }
            if (all) return
            vInvite.isVisible = true
            vEdit.isVisible = item.selfIsOwner()
            vJoin.isVisible = item.selfIsOwner() || item.getStatus() == Status.Progressing
        }
    }
}