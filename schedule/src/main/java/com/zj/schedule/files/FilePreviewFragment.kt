package com.zj.schedule.files


import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zj.api.downloader.DownloadListener
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.schedule.R
import com.zj.schedule.cv.ProgressBarView
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.io.DownloadManager
import com.zj.schedule.utl.FileOpenHelper
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.io.FileState
import com.zj.schedule.utl.sp.FileSPHelper
import java.lang.StringBuilder

@Constrain(id = "file_preview_fragment", backMode = 1)
class FilePreviewFragment : ConstrainFragment(), DownloadListener {

    private var data: ScheduleFileInfo? = null
    private var ivBack: View? = null
    private var tvName: TextView? = null
    private var ivIcon: AppCompatImageView? = null
    private var tvOwner: TextView? = null
    private var tvSize: TextView? = null
    private var pb: ProgressBarView? = null
    private var tvProgress: TextView? = null
    private var vFailed: View? = null
    private var tvFailed: TextView? = null
    private var tvDownload: TextView? = null

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.files_preview_fragment_layout, container, false)
    }

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        data = bundle?.getSerializable("fileInfo") as? ScheduleFileInfo
    }

    override fun onCreate() {
        super.onCreate()
        ivBack = find(R.id.files_preview_fragment_layout_iv_back)
        tvName = find(R.id.files_preview_fragment_layout_tv_name)
        ivIcon = find(R.id.files_preview_fragment_layout_iv_type)
        tvOwner = find(R.id.files_preview_fragment_layout_tv_owner)
        tvSize = find(R.id.files_preview_fragment_layout_tv_size)
        pb = find(R.id.files_preview_fragment_layout_pb)
        tvProgress = find(R.id.files_preview_fragment_layout_tv_progress)
        vFailed = find(R.id.files_preview_fragment_layout_v_failed)
        tvFailed = find(R.id.files_preview_fragment_layout_tv_failed)
        tvDownload = find(R.id.files_preview_fragment_layout_dtv)
        ivBack?.setOnClickListener {
            finish()
        }
        val ext = Utl.getFileExt(data?.name)
        val ssb = SpannableStringBuilder(ext.name)
        ssb.append(".${ext.extension}")
        val span = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.c_6fff))
        ssb.setSpan(span, ext.name.length, ssb.length, SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        tvName?.text = ssb
        ivIcon?.setImageResource(ext.iconRes)
        tvOwner?.text = context?.getString(R.string.Owner_, data?.uploaderName)
        tvSize?.text = context?.getString(R.string.FileSize, Utl.parseFileSize(data?.size ?: 0))
        updateFileBtn()
        tvDownload?.setOnClickListener {
            val url = data?.url ?: return@setOnClickListener
            when (it.getTag(R.id.download_state_tag)) {
                0 -> {
                    DownloadManager.cancel(url)
                }
                1 -> {
                    DownloadManager.startDownload(it.context, data, this)
                }
                2 -> {
                    openFile(url)
                }
            }
        }
    }

    private fun updateFileBtn() {
        val url = data?.url ?: return
        val cached = FileSPHelper.findLocalFromUrl(url)
        if (cached != null) {
            when (cached.state) {
                FileState.Failed.name -> {
                    setFailed()
                }
                FileState.Success.name -> {
                    setDownloaded()
                }
                FileState.Waiting.name -> {
                    if (DownloadManager.updateListenerIfExists(url, this)) {
                        setDownloading()
                    } else {
                        setNormal()
                    }
                }
            }
        } else {
            setNormal()
        }
    }

    override suspend fun onCompleted(callId: String, absolutePath: String) {
        setDownloaded()
    }

    override suspend fun onStart(callId: String) {
        setDownloading()
    }

    override suspend fun onError(callId: String, e: Throwable?, isCanceled: Boolean) {
        setFailed()
    }

    override suspend fun onProgress(callId: String, progress: Int) {
        pb?.isVisible = true
        tvProgress?.isVisible = true
        pb?.progress = progress
        tvProgress?.text = StringBuilder("$progress%")
    }

    private fun setDownloaded() {
        pb?.isVisible = false
        tvProgress?.isVisible = false
        vFailed?.isVisible = false
        tvFailed?.isVisible = false
        tvDownload?.setBackgroundResource(R.drawable.radius_12_22bb3b)
        tvDownload?.setTag(R.id.download_state_tag, 2)
        tvDownload?.text = getString(R.string.OpenFile)
    }

    private fun setDownloading() {
        pb?.isVisible = true
        tvProgress?.isVisible = true
        vFailed?.isVisible = false
        tvFailed?.isVisible = false
        tvDownload?.setBackgroundResource(R.drawable.radius_12_1af6)
        tvDownload?.setTag(R.id.download_state_tag, 0)
        tvDownload?.text = getString(R.string.CancelDownload)
    }

    private fun setFailed() {
        tvProgress?.text = ""
        pb?.progress = 0
        vFailed?.isVisible = true
        tvFailed?.isVisible = true
        tvProgress?.isVisible = false
        pb?.isVisible = false
        tvDownload?.setBackgroundResource(R.drawable.radius_12_f65f2a)
        tvDownload?.setTag(R.id.download_state_tag, 1)
        tvDownload?.text = getString(R.string.Download)
    }

    private fun setNormal() {
        pb?.progress = 0
        tvProgress?.text = ""
        vFailed?.isVisible = false
        tvFailed?.isVisible = false
        tvProgress?.isVisible = false
        pb?.isVisible = false
        tvDownload?.setBackgroundResource(R.drawable.radius_12_f65f2a)
        tvDownload?.setTag(R.id.download_state_tag, 1)
        tvDownload?.text = getString(R.string.Download)
    }

    private fun openFile(url: String) {
        val cached = FileSPHelper.findLocalFromUrl(url)
        if (cached == null || cached.localPath.isEmpty()) return
        FileOpenHelper.openFile(requireContext(), cached.localPath)
    }
}