package com.zj.schedule.cv

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zj.schedule.R

class UploadingItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : ConstraintLayout(context, attributeSet, def) {

    init {
        inflate(context, R.layout.calendar_files_uploading_item, this)
        initView()
    }

    private var ivType: ImageView? = null
    private var tvName: TextView? = null
    private var tvProgress: TextView? = null
    private var tvState: TextView? = null

    private fun initView() {
        ivType = findViewById(R.id.calendar_files_uploading_item_iv_type)
        tvName = findViewById(R.id.calendar_files_uploading_item_tv_name)
        tvProgress = findViewById(R.id.calendar_files_uploading_item_sb)
        tvState = findViewById(R.id.calendar_files_uploading_item_tv_state)
    }



}