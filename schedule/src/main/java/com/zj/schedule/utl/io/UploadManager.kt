package com.zj.schedule.utl.io

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.zj.api.ZApi
import com.zj.api.exception.ApiException
import com.zj.api.uploader.FileInfo
import com.zj.api.uploader.FileUploadListener
import com.zj.api.uploader.task.SimpleUploadTask
import com.zj.compress.CompressUtils
import com.zj.schedule.R
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.files.FileUploadingFragment
import com.zj.schedule.files.l.UploadListener
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.sp.FileSPHelper
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

internal object UploadManager {

    private val ls = ConcurrentLinkedQueue<UploadListener>()
    private val tsk = ConcurrentHashMap<String, SimpleUploadTask>()
    private val handler = Handler(Looper.getMainLooper())
    private var reasons = MutableLiveData<MutableSet<String>?>()

    fun cancelAll() {
        tsk.values.forEach {
            it.cancel()
        }
    }

    fun cancel(uri: String) {
        tsk[uri]?.cancel()
    }

    fun addListener(v: UploadListener): Boolean {
        if (!ls.contains(v)) ls.add(v)
        return tsk.isEmpty()
    }

    fun removeListener(v: UploadListener) {
        ls.remove(v)
    }

    fun upload(context: Context, meetingId: String, u: Uri?) {
        val header = Utl.config?.getHeader() ?: mutableMapOf()

        //理论上这里应该先检查本地缓存是否存在 Q 处理，因时间关系留待以后优化。
        CompressUtils.with(context).load(u).transForAndroidQ { s ->
            try {
                val uri = s.originalPath?.toString()
                if (s?.path != null && !uri.isNullOrEmpty()) {
                    val f = File(s.path)
                    val spInfo = FileNetInfo(0, f.name, uri, s.path, null)
                    spInfo.withId = meetingId
                    notifyToListeners(spInfo.uploadId) { onStart(spInfo.uploadId, spInfo) }
                    FileSPHelper.putFileInfo(uri, spInfo)
                    if (!f.exists()) {
                        notifyToListeners(spInfo.uploadId) { onError(spInfo.uploadId, ApiException(uri, null, NullPointerException("Failed to get file from : ${s.path}")), null) }
                        return@transForAndroidQ
                    }

                    val fInfo = FileInfo(f.name, "file", f, path = f.path, fileId = spInfo.uploadId)
                    val param = mutableMapOf(Pair("meetingId", meetingId))
                    val uploadTask = ZApi.Uploader.with("${Utl.config?.getApiHost()}/app/resource/file/upload-meeting-file").observerOn(ZApi.MAIN).setFileInfo(fInfo).addParams(param).callId(spInfo.uploadId).header(header).deleteFileAfterUpload(false).start(object : FileUploadListener {

                        override fun onProgress(uploadId: String, fileInfo: FileInfo?, progress: Int, contentLength: Long) {
                            notifyToListeners(uploadId) { onProgress(uploadId, progress, contentLength) }
                        }

                        override fun onCompleted(uploadId: String, fileInfo: FileInfo?, totalBytes: Long) {
                            tsk.remove(uri)
                            notifyToListeners(uploadId) { onCompleted(uploadId, totalBytes) }
                        }

                        override fun onSuccess(uploadId: String, body: Any?, totalBytes: Long) {
                            val gs = JSONObject.wrap(body)?.toString()
                            val info = com.google.gson.Gson().fromJson(gs, ScheduleFileInfo::class.java)
                            spInfo.state = FileState.Success.name
                            spInfo.url = info.url
                            FileSPHelper.putFileInfo(uri, spInfo)
                            Utl.toastFileStateForApp(R.string.Upload, fInfo.name)
                            notifyToListeners(uploadId) { onSuccess(uploadId, info, totalBytes) }
                        }

                        override fun onError(uploadId: String, fileInfo: FileInfo?, exception: ApiException?, errorBody: Any?) {
                            spInfo.state = FileState.Failed.name
                            FileSPHelper.putFileInfo(uri, spInfo)
                            notifyToListeners(uploadId) { onError(uploadId, exception, errorBody) }
                        }
                    })
                    tsk[uri] = uploadTask
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun observeDots(lo: LifecycleOwner, observer: Observer<MutableSet<String>?>) {
        reasons.observe(lo, observer)
    }

    fun removeDotsObserver(observer: Observer<MutableSet<String>?>) {
        reasons.removeObserver(observer)
    }

    private fun newDotsPatched(uploadId: String) {
        if (Utl.getCurTopFragment() !is FileUploadingFragment) {
            var set = reasons.value
            if (set == null) set = mutableSetOf()
            set.add(uploadId)
            reasons.postValue(set)
        }
    }

    fun removeADot(uploadId: String) {
        var set = reasons.value
        if (set == null) set = mutableSetOf()
        set.remove(uploadId)
        reasons.postValue(set)
    }

    fun clearAllDots() {
        reasons.postValue(null)
    }

    fun notifyToListeners(uploadId: String, run: UploadListener.() -> Unit) {
        handler.post {
            kotlin.runCatching {
                newDotsPatched(uploadId)
                ls.forEach { run(it) }
            }
        }
    }
}