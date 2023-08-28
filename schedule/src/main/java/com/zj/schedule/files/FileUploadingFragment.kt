package com.zj.schedule.files

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.schedule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zj.api.exception.ApiException
import com.zj.loading.DisplayMode
import com.zj.loading.ZRotateLoadingView
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.files.l.UploadListener
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.io.FileState
import com.zj.schedule.utl.io.UploadManager
import com.zj.schedule.utl.sp.FileSPHelper

@Constrain(id = "FileUploadingFragment", backMode = 1)
class FileUploadingFragment : ConstrainFragment() {

    private var uploaded: MutableList<FileNetInfo>? = null
    private var meetingId: Long? = null
    private var adapter = FileUploadingAdapter { m ->
        if (m.state == FileState.Failed.name) {
            FileSPHelper.removeFileInfo(m.originalPath)
            removeAt(m)
        }
    }
    private var vBack: View? = null
    private var rvContent: RecyclerView? = null
    private var vUpload: View? = null
    private var blv: ZRotateLoadingView? = null

    private val onUploadingListener = object : UploadListener {

        override fun onStart(uploadId: String, fInfo: FileNetInfo) {
            if (adapter.data.any { it.uploadId == uploadId }.not()) {
                adapter.add(fInfo)
            }
        }

        override fun onProgress(uploadId: String, progress: Int, contentLength: Long) {
            adapter.updateProgress(uploadId, progress)
        }

        override fun onSuccess(uploadId: String, body: ScheduleFileInfo?, totalBytes: Long) {
            adapter.updateState(uploadId, FileState.Success)
        }

        override fun onError(uploadId: String, exception: ApiException?, errorBody: Any?) {
            adapter.updateState(uploadId, FileState.Failed)

        }
    }

    private fun removeAt(m: FileNetInfo) {
        rvContent?.post { adapter.remove(m) }
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.files_uploading_fragment_layout, container, false)
    }

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        val data = bundle?.getString("lst")
        meetingId = bundle?.getLong("meetingId")
        uploaded = Gson().fromJson<Array<FileNetInfo>>(data, TypeToken.getArray(FileNetInfo::class.java).type).toMutableList()
    }

    override fun onCreate() {
        super.onCreate()
        UploadManager.clearAllDots()
        val uploading = FileSPHelper.getAllFiles {
            it.state != FileState.Success.name && it.withId == "$meetingId"
        }
        vBack = find(R.id.files_uploading_fragment_layout_iv_back)
        rvContent = find(R.id.files_uploading_fragment_layout_rv)
        vUpload = find(R.id.files_uploading_fragment_layout_upload)
        blv = find(R.id.files_uploading_fragment_layout_blv)
        rvContent?.adapter = adapter
        vBack?.setOnClickListener { finish() }
        vUpload?.setOnClickListener {
            startFragment(FileSelector::class.java)
        }
        val lst = mutableListOf<FileNetInfo>()
        lst.addAll(uploading)
        uploaded?.map {
            it.progress = 100;it
        }?.let {
            lst.addAll(it)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                blv?.setMode(if (adapter.data.size > 0) DisplayMode.NORMAL else DisplayMode.NO_DATA)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                blv?.setMode(if (adapter.data.size > 0) DisplayMode.NORMAL else DisplayMode.NO_DATA)
            }
        })
        adapter.add(lst)
        UploadManager.addListener(onUploadingListener)
    }

    override fun onFragmentResult(bundle: Bundle?) {
        super.onFragmentResult(bundle)
        val uri = bundle?.getParcelable<Uri>("uri")
        if (uri != null) {
            UploadManager.upload(this.requireContext(), "$meetingId", uri)
        }
    }

    override fun finish(onFinished: ((success: Boolean, inTopOfStack: Boolean) -> Unit)?) {
        UploadManager.removeListener(onUploadingListener)
        super.finish(onFinished)
    }
}