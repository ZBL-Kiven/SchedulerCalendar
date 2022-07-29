package com.zj.schedule.utl.sp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.io.FileState
import java.io.File

internal object FileSPHelper {

    private const val ALL_FILES_INFO_KEYS = "all_files_info_keys"

    operator fun <T : Any> get(key: String, defaultValue: T): T? {
        return Preference.get(key, defaultValue)
    }

    fun putFileInfo(key: String, t: FileNetInfo?) {
        Preference.put(key, Gson().toJson(t))
    }

    fun removeFileInfo(key: String) {
        val cached = getFileInfo(key)
        cached?.translationPath?.let {
            File(it).delete()
        }
        Preference.put(key, null)
    }

    fun getFileInfo(key: String): FileNetInfo? {
        val s = Preference.get<String>(key, "")
        return Gson().fromJson(s, FileNetInfo::class.java)
    }

    fun init(name: String, context: Context?) {
        context?.let {
            Preference.init(name, context)
            val newArray = mutableListOf<FileNetInfo>()
            getAllFiles().forEach {
                if (it.state == FileState.Waiting.name) {
                    it.state = FileState.Failed.name
                }
                newArray.add(it)
            }
            Preference.put(ALL_FILES_INFO_KEYS, Gson().toJson(newArray))
        }
    }

    fun getAllFiles(): MutableList<FileNetInfo> {
        val s = get(ALL_FILES_INFO_KEYS, "")
        if (s.isNullOrEmpty()) return mutableListOf()
        val type = TypeToken.getArray(FileNetInfo::class.java).type
        val l = Gson().fromJson<Array<FileNetInfo>>(s, type)
        return if (l.isNullOrEmpty()) mutableListOf() else l.toMutableList()
    }
}