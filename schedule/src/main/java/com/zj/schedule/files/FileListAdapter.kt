package com.zj.schedule.files

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zj.schedule.R
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.Utl
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class FileListAdapter : BaseAdapter<ScheduleFileInfo>(R.layout.calendar_schedule_detail_item_files) {

    override fun initData(holder: BaseViewHolder<ScheduleFileInfo>?, position: Int, module: ScheduleFileInfo?, payloads: MutableList<Any>?) {
        if (module == null) return
        val ext = Utl.getFileExt(module.name)
        holder?.getView<ImageView>(R.id.calendar_schedule_detail_files_item_iv_type)?.setImageResource(ext.iconRes)
        holder?.getView<TextView>(R.id.calendar_schedule_detail_files_item_tv_name)?.let {
            val ssb = SpannableStringBuilder(ext.name)
            ssb.append(".${ext.extension}")
            val span = ForegroundColorSpan(ContextCompat.getColor(it.context, R.color.c_6fff))
            ssb.setSpan(span, ext.name.length, ssb.length, SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
            it.text = ssb
        }
        holder?.getView<TextView>(R.id.calendar_schedule_detail_files_item_tv_desc)?.let {
            val sd = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(module.updateTime))
            val owner = it.context.getString(R.string.Owner_, module.uploaderName)
            it.text = StringBuilder(sd).append("\t\t\t").append(owner)
        }
        holder?.getView<TextView>(R.id.calendar_schedule_detail_files_item_tv_size)?.let {
            it.text = Utl.parseFileSize(module.size)
        }
    }
}