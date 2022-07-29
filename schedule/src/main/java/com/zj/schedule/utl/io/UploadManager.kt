package com.zj.schedule.utl.io

import android.content.Context
import com.zj.api.ZApi
import com.zj.api.exception.ApiException
import com.zj.api.uploader.FileInfo
import com.zj.api.uploader.FileUploadListener
import com.zj.api.uploader.task.SimpleUploadTask
import com.zj.compress.CompressUtils
import com.zj.schedule.R
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.sp.FileSPHelper
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

internal object UploadManager {

    private val ls = ConcurrentLinkedQueue<FileUploadListener>()
    private val tsk = ConcurrentHashMap<String, SimpleUploadTask>()

    fun cancelAll() {
        tsk.values.forEach {
            it.cancel()
        }
    }

    fun cancel(uri: String) {
        tsk[uri]?.cancel()
    }

    fun addListener(v: FileUploadListener): Boolean {
        ls.add(v)
        return tsk.isEmpty()
    }

    fun removeListener(v: FileUploadListener) {
        ls.remove(v)
    }

    fun upload(context: Context, uri: String?) {

        val header = Utl.config?.getHeader() ?: mutableMapOf()

        //理论上这里应该先检查本地缓存是否存在 Q 处理，因时间关系留待以后优化。
        CompressUtils.with(context).load(uri).transForAndroidQ { s ->
            if (s?.path != null && !uri.isNullOrEmpty()) {
                val f = File(s.path)
                if (!f.exists()) {
                    ls.forEach {
                        it.onError(uri, null, ApiException(uri, null, NullPointerException("Failed to get file from : ${s.path}")), null)
                    }
                    return@transForAndroidQ
                }
                val spInfo = FileNetInfo(0, uri, s.path, null)
                FileSPHelper.putFileInfo(uri, spInfo)
                val fInfo = FileInfo(f.name, "file", path = f.path, fileId = uri)
                val uploadTask = ZApi.Uploader.with("/app/resource/file/upload-meeting-file").setFileInfo(fInfo).callId(uri).header(header).deleteFileAfterUpload(false).start(object : FileUploadListener {

                    override fun onProgress(uploadId: String, fileInfo: FileInfo?, progress: Int, contentLength: Long) {
                        ls.forEach { it.onProgress(uploadId, fileInfo, progress, contentLength) }
                    }

                    override fun onCompleted(uploadId: String, fileInfo: FileInfo?, totalBytes: Long) {
                        tsk.remove(uploadId)
                        ls.forEach { it.onCompleted(uploadId, fileInfo, totalBytes) }
                    }

                    override fun onSuccess(uploadId: String, body: Any?, totalBytes: Long) {
                        spInfo.state = FileState.Success.name
                        FileSPHelper.putFileInfo(uri, spInfo)
                        Utl.toastFileStateForApp(R.string.Upload, fInfo.name)
                        ls.forEach { it.onSuccess(uploadId, body, totalBytes) }
                    }

                    override fun onError(uploadId: String, fileInfo: FileInfo?, exception: ApiException?, errorBody: Any?) {
                        spInfo.state = FileState.Failed.name
                        FileSPHelper.putFileInfo(uri, spInfo)
                        ls.forEach { it.onError(uploadId, fileInfo, exception, errorBody) }
                    }
                })
                tsk[uri] = uploadTask
            }
        }
    }
}