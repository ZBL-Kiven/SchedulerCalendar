package com.zj.schedule.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zj.api.uploader.FileUploadListener
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.loading.DisplayMode
import com.zj.loading.ZRotateLoadingView
import com.zj.schedule.R
import com.zj.schedule.data.api.ScheduleApi
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.io.FileState
import com.zj.schedule.utl.io.UploadManager
import com.zj.schedule.utl.sp.FileSPHelper
import com.zj.views.list.listeners.ItemClickListener
import kotlinx.coroutines.launch


/**
 * Required params "meetingId"
 * */
@Constrain(id = "file_list_fragment", backMode = 1)
class FileListFragment : ConstrainFragment(), FileUploadListener {

    private var meetingId: Long = -1
    private var ivBack: View? = null
    private var rvContent: RecyclerView? = null
    private var blv: ZRotateLoadingView? = null
    private var vUpload: TextView? = null
    private var vUploading: View? = null
    private var adapter = FileListAdapter()

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        meetingId = bundle?.getLong("meetingId") ?: -1
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.files_list_fragment_layout, container)
    }

    override fun onCreate() {
        super.onCreate()
        ivBack = find(R.id.files_list_fragment_layout_iv_back)
        rvContent = find(R.id.files_list_fragment_layout_rv)
        blv = find(R.id.files_list_fragment_layout_blv)
        vUpload = find(R.id.files_list_fragment_layout_upload)
        vUploading = find(R.id.files_list_fragment_layout_v_uploading)
        rvContent?.adapter = adapter
        ivBack?.setOnClickListener {
            finish()
        }
        vUpload?.setOnClickListener {
            selectFileToUpload()
        }
        if (meetingId < 0) {
            blv?.setMode(DisplayMode.NO_DATA)
        } else {
            getMeetingFiles()
        }
        vUploading?.setOnClickListener {
            it.isSelected = false
            startUploadingPage()
        }
        adapter.setOnItemClickListener(object : ItemClickListener<ScheduleFileInfo>() {
            override fun onItemClick(position: Int, v: View?, m: ScheduleFileInfo?) {
                val b = Bundle()
                b.putSerializable("fileInfo", m)
                startFragment(FilePreviewFragment::class.java, b)
            }
        })
    }

    private fun getMeetingFiles() {
        blv?.setMode(DisplayMode.LOADING)
        val hasUploading = UploadManager.addListener(this@FileListFragment)
        lifecycleScope.launch {
            val files = ScheduleApi.getMeetingFiles("$meetingId")
            if (files.error == null || files.data.isNullOrEmpty()) {
                adapter.clear()
                blv?.setMode(DisplayMode.NO_DATA)
            } else {
                adapter.change(files.data)
                blv?.setMode(DisplayMode.NORMAL.delay(300))
            }
            vUploading?.isVisible = (files.data?.any {
                it.uploaderId == Utl.config?.getUserId()
            } ?: false) || hasUploading
        }
    }


    private fun selectFileToUpload() {
        startFragment(FileSelector::class.java)
    }

    private fun startUploadingPage() {
        val data = adapter.data.filter { it.uploaderId == Utl.config?.getUserId() }.map {
            val fi = it.url?.let { u ->
                FileSPHelper.getFileInfo(u)
            }
            if (fi != null) return@map fi
            FileNetInfo(0, "", "", it.url, FileState.Success.name)
        }
        val b = Bundle()
        b.putString("lst", Gson().toJson(data))
        startFragment(FileUploadingFragment::class.java)
    }

    override fun onFragmentResult(bundle: Bundle?) {
        super.onFragmentResult(bundle)
        val uri = bundle?.getString("uri")
        vUploading?.isSelected = true
        UploadManager.upload(this.requireContext(), uri)
        startUploadingPage()
    }

    override fun finish(onFinished: ((success: Boolean, inTopOfStack: Boolean) -> Unit)?) {
        UploadManager.removeListener(this)
        super.finish(onFinished)
    }

    override fun onSuccess(uploadId: String, body: Any?, totalBytes: Long) {
        vUploading?.isSelected = true
    }
}

