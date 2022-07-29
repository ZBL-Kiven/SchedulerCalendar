package com.zj.schedule.utl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.zj.schedule.R
import java.io.File

@Suppress("SpellCheckingInspection")
object FileOpenHelper {

    fun openFile(context: Context, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val f = File(path)
        val type = getMIMEType(path)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "com.zj.schedule.own.file_provider", f)
        } else {
            Uri.fromFile(f)
        }
        intent.setDataAndTypeAndNormalize(data, type)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.Cannot_Open_file), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMIMEType(fName: String): String {
        var type = "*/*"
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        val end = fName.substring(dotIndex, fName.length).lowercase()
        if (TextUtils.isEmpty(end)) {
            return type
        }
        for (i in mimeMapTable.indices) {
            if (end == mimeMapTable[i][0]) {
                type = mimeMapTable[i][1]
                break
            }
        }
        return type
    }

    private val mimeMapTable = arrayOf( //
        arrayOf(".3gp", "video/3gpp"), // 
        arrayOf(".apk", "application/vnd.android.package-archive"), // 
        arrayOf(".asf", "video/x-ms-asf"), //
        arrayOf(".avi", "video/x-msvideo"), // 
        arrayOf(".bin", "application/octet-stream"), //
        arrayOf(".bmp", "image/bmp"), // 
        arrayOf(".c", "text/plain"), //
        arrayOf(".class", "application/octet-stream"), //
        arrayOf(".conf", "text/plain"), // 
        arrayOf(".cpp", "text/plain"), // 
        arrayOf(".doc", "application/msword"), //
        arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"), // 
        arrayOf(".xls", "application/vnd.ms-excel"), //
        arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), // 
        arrayOf(".exe", "application/octet-stream"), //
        arrayOf(".gif", "image/gif"), //
        arrayOf(".gtar", "application/x-gtar"), //
        arrayOf(".gz", "application/x-gzip"), //
        arrayOf(".h", "text/plain"), // 
        arrayOf(".htm", "text/html"), //
        arrayOf(".html", "text/html"), //
        arrayOf(".jar", "application/java-archive"), // 
        arrayOf(".java", "text/plain"), //
        arrayOf(".jpeg", "image/jpeg"), //
        arrayOf(".jpg", "image/jpeg"), // 
        arrayOf(".js", "application/x-javascript"), //
        arrayOf(".log", "text/plain"), //
        arrayOf(".m3u", "audio/x-mpegurl"), //
        arrayOf(".m4a", "audio/mp4a-latm"), // 
        arrayOf(".m4b", "audio/mp4a-latm"), // 
        arrayOf(".m4p", "audio/mp4a-latm"), // 
        arrayOf(".m4u", "video/vnd.mpegurl"), // 
        arrayOf(".m4v", "video/x-m4v"), //
        arrayOf(".mov", "video/quicktime"), //
        arrayOf(".mp2", "audio/x-mpeg"), // 
        arrayOf(".mp3", "audio/x-mpeg"), //
        arrayOf(".mp4", "video/mp4"), //
        arrayOf(".mpc", "application/vnd.mpohun.certificate"), // 
        arrayOf(".mpe", "video/mpeg"), // 
        arrayOf(".mpeg", "video/mpeg"), //
        arrayOf(".mpg", "video/mpeg"), // 
        arrayOf(".mpg4", "video/mp4"), // 
        arrayOf(".mpga", "audio/mpeg"), //
        arrayOf(".msg", "application/vnd.ms-outlook"), //
        arrayOf(".ogg", "audio/ogg"), // 
        arrayOf(".pdf", "application/pdf"), //
        arrayOf(".png", "image/png"), // 
        arrayOf(".pps", "application/vnd.ms-powerpoint"), // 
        arrayOf(".ppt", "application/vnd.ms-powerpoint"), // 
        arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"), // 
        arrayOf(".prop", "text/plain"), // 
        arrayOf(".rc", "text/plain"), // 
        arrayOf(".rmvb", "audio/x-pn-realaudio"), // 
        arrayOf(".rtf", "application/rtf"), // 
        arrayOf(".sh", "text/plain"), // 
        arrayOf(".tar", "application/x-tar"), //
        arrayOf(".tgz", "application/x-compressed"), // 
        arrayOf(".txt", "text/plain"), //
        arrayOf(".wav", "audio/x-wav"), // 
        arrayOf(".wma", "audio/x-ms-wma"), // 
        arrayOf(".wmv", "audio/x-ms-wmv"), // 
        arrayOf(".wps", "application/vnd.ms-works"), //
        arrayOf(".xml", "text/plain"), // 
        arrayOf(".z", "application/x-compress"), // 
        arrayOf(".zip", "application/x-zip-compressed"), // 
        arrayOf("", "*/*"))
}