package com.zj.schedule.files

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.zj.schedule.R
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.io.FileState
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import java.lang.StringBuilder

internal class FileUploadingAdapter(private val onRemoveTextClick: (FileNetInfo) -> Unit) : BaseAdapter<FileNetInfo>(R.layout.calendar_files_uploading_item) {

    fun updateProgress(uploadId: String, progress: Int) {
        val index = data.indexOfFirst {
            val isSame = it.uploadId == uploadId
            if (isSame) it.progress = progress
            isSame
        }
        if (index in 0..maxPosition) {
            notifyItemChanged(index, "uploadProgress")
        }
    }

    fun updateState(uploadId: String, state: FileState) {
        val index = data.indexOfFirst {
            val isSame = it.uploadId == uploadId
            if (isSame) {
                it.state = state.name
                if (state == FileState.Success) it.progress = 100
                if (state == FileState.Failed) it.progress = 0
            }
            isSame
        }
        if (index in 0..maxPosition) {
            notifyItemChanged(index, "uploadProgress")
        }
    }

    override fun initData(holder: BaseViewHolder<FileNetInfo>?, position: Int, module: FileNetInfo?, payloads: MutableList<Any>?) {
        if (module == null) return
        val ext = Utl.getFileExt(module.name)
        val hasUpdate = payloads?.contains("uploadProgress") ?: false
        if (payloads.isNullOrEmpty() || hasUpdate) {
            holder?.getView<TextView>(R.id.calendar_files_uploading_item_tv_state)?.let {
                when (module.state) {
                    FileState.Success.name -> {
                        it.text = ""
                        it.setBackgroundResource(R.mipmap.schedule_file_list_uploading_ok)
                        it.setTag(R.id.upload_state_tag, 1)
                    }
                    FileState.Failed.name -> {
                        it.text = ""
                        it.setBackgroundResource(R.mipmap.schedule_file_list_uploading_failed)
                        it.setTag(R.id.upload_state_tag, 2)
                    }
                    FileState.Waiting.name -> {
                        it.background = null
                        it.text = StringBuilder("${module.progress}%")
                        it.setTag(R.id.upload_state_tag, 0)
                    }
                }
                it.setOnClickListener {
                    onRemoveTextClick(module)
                }
            }
            holder?.getView<SeekBar>(R.id.calendar_files_uploading_item_sb)?.let {
                it.progress = module.progress
            }
        }
        if (hasUpdate) return
        holder?.getView<ImageView>(R.id.calendar_files_uploading_item_iv_type)?.let {
            Glide.with(it).load(ext.iconRes).into(it)
        }
        holder?.getView<TextView>(R.id.calendar_files_uploading_item_tv_name)?.let {
            val ssb = SpannableStringBuilder(ext.name)
            ssb.append(".${ext.extension}")
            val span = ForegroundColorSpan(ContextCompat.getColor(it.context, R.color.c_6fff))
            ssb.setSpan(span, ext.name.length, ssb.length, SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
            it.text = ssb
        }
    }
}