package com.zj.schedule.utl.io

import android.content.Context
import com.zj.api.ZApi
import com.zj.api.downloader.DownloadCompo
import com.zj.api.downloader.DownloadListener
import com.zj.schedule.R
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.Utl
import com.zj.schedule.utl.sp.FileSPHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

object DownloadManager {

    private val downloadingTasks = ConcurrentHashMap<String, Download>()
    private val ls = ConcurrentHashMap<String, DownloadListener>()

    private fun getDownloadFile(context: Context, name: String?, url: String?): File? {
        val dir = File(context.filesDir, Utl.CACHE_NAME)
        if (!dir.exists() && !dir.mkdirs()) {
            return null
        }
        val ext = Utl.getFileExt(name)
        val f = File(dir, "${url.hashCode()}-${ext.name}.${ext.extension}")
        if (!f.exists() && !f.createNewFile()) {
            return null
        }
        return f
    }

    fun startDownload(context: Context, info: ScheduleFileInfo?, l: DownloadListener) {
        if (info == null || info.url.isNullOrEmpty()) return
        val url = info.url ?: return
        val fileForInfo = getDownloadFile(context, info.name, info.url)
        if (fileForInfo == null) {
            MainScope().launch {
                l.onError(info.url ?: "", IllegalArgumentException("Cannot create target file for ${info.name}"), true)
            }
            return
        }
        ls[url] = l
        if (downloadingTasks.containsKey(url)) {
            return
        } else {
            downloadingTasks[url] = Download(fileForInfo, info)
        }
    }

    fun updateListenerIfExists(url: String, l: DownloadListener): Boolean {
        if (downloadingTasks.containsKey(url)) {
            ls[url] = l
            MainScope().launch {
                l.onStart(url)
            }
            return true
        }
        return false
    }

    fun cancel(url: String) {
        downloadingTasks[url]?.cancel()
    }

    class Download(file: File, info: ScheduleFileInfo) {

        private val compo: DownloadCompo

        init {
            val url = info.url ?: ""
            val headers = Utl.config?.getHeader() ?: mutableMapOf()
            val fName = file.name
            compo = ZApi.Downloader.with(url, file).header(headers).observerOn(ZApi.MAIN).callId(url).start(object : DownloadListener {
                override suspend fun onStart(callId: String) {
                    val f = FileNetInfo(1, fName, callId, file.path, url, FileState.Waiting.name)
                    FileSPHelper.putFileInfo(callId, f)
                    ls[callId]?.onStart(callId)
                }

                override suspend fun onCompleted(callId: String, absolutePath: String) {
                    val f = FileNetInfo(1, fName, callId, absolutePath, url, FileState.Success.name)
                    downloadingTasks.remove(url)
                    FileSPHelper.putFileInfo(callId, f)
                    Utl.toastFileStateForApp(R.string.Download, fName)
                    ls[callId]?.onCompleted(callId, absolutePath)
                    ls.remove(callId)
                }

                override suspend fun onProgress(callId: String, progress: Int) {
                    ls[callId]?.onProgress(callId, progress)
                }

                override suspend fun onError(callId: String, e: Throwable?, isCanceled: Boolean) {
                    val f = FileNetInfo(1, fName, callId, file.path, url, FileState.Failed.name)
                    downloadingTasks.remove(url)
                    FileSPHelper.putFileInfo(callId, f)
                    ls[callId]?.onError(callId, e, isCanceled)
                    ls.remove(callId)
                }
            })
        }

        fun cancel() {
            compo.cancel("From user called!")
        }
    }
}