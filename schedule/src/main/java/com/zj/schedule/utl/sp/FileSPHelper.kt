package com.zj.schedule.utl.sp

import android.content.Context
import com.google.gson.Gson
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.io.FileState
import java.io.File

internal object FileSPHelper {

    operator fun <T : Any> get(key: String, defaultValue: T): T? {
        return Preference.get(key, defaultValue)
    }

    fun putFileInfo(key: String, t: FileNetInfo?) {
        Preference.put(key, Gson().toJson(t))
    }

    fun removeFileInfo(key: String) {
        val cached = getFileInfo(key)
        cached?.localPath?.let {
            File(it).delete()
        }
        Preference.put(key, null)
    }

    fun getFileInfo(key: String): FileNetInfo? {
        val s = Preference.get<String>(key, "")
        return Gson().fromJson(s, FileNetInfo::class.java)
    }

    fun findLocalFromUrl(url: String): FileNetInfo? {
        val lst = getAllFiles()
        lst.filter {
            it.url == url || it.originalPath == url
        }
        return lst.firstOrNull()
    }

    fun init(name: String, context: Context?) {
        context?.let {
            Preference.init(name, context)
            val newArray = mutableListOf<FileNetInfo>()
            getAllFiles().forEach {
                if (it.state == FileState.Waiting.name) {
                    it.state = FileState.Failed.name
                }
                var needAdd = true
                if (it.state == FileState.Success.name) {
                    val f = File(it.localPath)
                    if (it.localPath.isEmpty() || !f.exists() || f.length() <= 0) {
                        needAdd = false
                    }
                }
                if (needAdd) newArray.add(it)
            }
            Preference.clear()
            newArray.forEach {
                run run@{
                    val k = if (it.type == 0) it.originalPath else it.url ?: return@run
                    Preference.put(k, Gson().toJson(it))
                }
            }
        }
    }

    fun getAllFiles(predicate: ((FileNetInfo) -> Boolean)? = null): MutableList<FileNetInfo> {
        val s = Preference.getAll()
        if (s.isNullOrEmpty()) return mutableListOf()
        val gson = Gson()
        return s.mapNotNullTo(mutableListOf()) {
            kotlin.runCatching {
                val fn = gson.fromJson(it.value.toString(), FileNetInfo::class.java)
                if (predicate == null) fn else if (predicate(fn)) fn else null
            }.getOrNull()
        }
    }
}