package com.zj.swipeRv.cv

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zj.swipeRv.R
import com.zj.swipeRv.cv.i.ScheduleItemIn
import com.zj.swipeRv.cv.i.Status
import com.zj.swipeRv.utl.Utl.tintDrawable
import com.zj.dtv.DrawableTextView

class ScheduleStatusItemView<T : ScheduleItemIn> @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : CustomStatusBgView(context, attributeSet, def) {

    init {
        inflate(context, R.layout.calendar_schedule_item_status_layout, this)
        initView()
    }

    private lateinit var scheduleName: TextView
    private lateinit var scheduleStatus: TextView
    private lateinit var scheduleFiles: View
    private lateinit var scheduleJoin: DrawableTextView
    private var data: T? = null
    private var selfIsOwner = false

    private fun initView() {
        scheduleName = findViewById(R.id.calendar_item_tv_name)
        scheduleStatus = findViewById(R.id.calendar_item_tv_status)
        scheduleFiles = findViewById(R.id.calendar_item_v_files)
        scheduleJoin = findViewById(R.id.calendar_item_dtv_join)
    }

    fun setData(d: T?) {
        this.data = d
        this.scheduleName.text = d?.getScheduleName()
        this.scheduleFiles.isVisible = d?.hasFiles() ?: false
        this.selfIsOwner = d?.selfIsOwner() ?: false
        super.status = d?.getStatus() ?: Status.Ended
    }

    override fun onStatusChanged(status: Status) {
        when (status) {
            Status.Ended -> {
                this.scheduleJoin.isVisible = false
                val d = scheduleStatus.background
                scheduleStatus.background = tintDrawable(d, status.color)
                scheduleName.setTextColor(Color.WHITE)
                scheduleStatus.setTextColor(ContextCompat.getColor(context, R.color.c_6fff))
            }
            Status.Feature -> {
                this.scheduleJoin.isVisible = selfIsOwner
                val d = scheduleStatus.background
                scheduleStatus.background = tintDrawable(d, status.color)
                scheduleName.setTextColor(Color.WHITE)
                scheduleStatus.setTextColor(Color.WHITE)
            }
            Status.Progressing -> {
                this.scheduleJoin.isVisible = true
                val d = scheduleStatus.background
                scheduleStatus.background = tintDrawable(d, status.color)
                scheduleName.setTextColor(Color.WHITE)
                scheduleStatus.setTextColor(Color.WHITE)
            }
            Status.Canceled -> {
                val d = scheduleStatus.background
                scheduleStatus.background = tintDrawable(d, status.color)
                scheduleName.setTextColor(ContextCompat.getColor(context, R.color.c_9fff))
                scheduleStatus.setTextColor(ContextCompat.getColor(context, R.color.c_6fff))
            }
        }
        scheduleStatus.text = context.getString(status.strId)
    }
}