package com.zj.schedule.utl

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import com.zj.cf.managers.ConstrainFragmentManager
import com.zj.schedule.R
import com.zj.schedule.files.FilePreviewFragment
import com.zj.schedule.files.FileUploadingFragment
import com.zj.schedule.utl.sp.FileSPHelper
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

internal object Utl {

    const val CACHE_NAME = "uTown/download/files/"

    sealed class ExType(var extension: String, val iconRes: Int, var name: String = "")
    class Pic(ex: String) : ExType(ex, R.mipmap.schedule_file_pic)
    class Wps(ex: String) : ExType(ex, R.mipmap.schedule_file_wps)
    class Xls(ex: String) : ExType(ex, R.mipmap.schedule_file_xls)
    class Pdf(ex: String) : ExType(ex, R.mipmap.schedule_file_pdf)
    class UnKnown(ex: String) : ExType(ex, R.mipmap.schedule_file_unknow)

    var config: Config? = null; private set

    var app: Application? = null

    lateinit var frgManager: ConstrainFragmentManager

    fun setConfig(config: Config) {
        this.config = config
    }

    fun init(context: Context) {
        this.app = context.applicationContext as Application
        FileSPHelper.init("schedules${config?.getNickName()}", app)
    }

    fun getFileExt(fileName: String?): ExType {
        val fex = run setExifRun@{
            val exif = fileName?.split(".")
            if (exif.isNullOrEmpty()) return@setExifRun null
            exif.last()
        }
        val name = fileName?.lastIndexOf(".")?.let {
            fileName.substring(0, it)
        } ?: ""
        val ext = when (fex) {
            "jpg", "png", "jpeg", "bmp", "JPG", "PNG", "JPEG", "BMP" -> Pic(fex)
            "doc", "wps" -> Wps(fex)
            "xls", "number" -> Xls(fex)
            "pdf" -> Pdf(fex)
            else -> UnKnown(fex ?: "")
        }
        ext.name = name
        return ext
    }

    @Suppress("DEPRECATION")
    fun toastFileStateForApp(type: Int, fileName: String?) {
        val frg = frgManager.getTopOfStack()
        if (type == R.string.Upload && frg is FileUploadingFragment) return
        if (type == R.string.Download && frg is FilePreviewFragment) return
        val context = frg?.context ?: app
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER, 0, 0)
        val ctx = toast.view?.context
        val v = View.inflate(ctx, R.layout.files_toast_hint, null)
        v.findViewById<TextView>(R.id.files_toast_hint_tv)?.let {
            val t = ctx?.getString(type)
            it.text = ctx?.getString(R.string.Success_for_U_or_D, fileName, t)
        }
        toast.view = v
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    fun getDisplayTimeStr(compare: Long, cur: Long, stay: Int = 0): String {
        val startCalendar = getStartCalendar(compare)
        val endCalendar = getEndCalendar(cur)
        val y1 = startCalendar.get(Calendar.YEAR)
        val m1 = startCalendar.get(Calendar.MONTH) + 1
        val day1 = startCalendar.get(Calendar.DAY_OF_MONTH)
        val y2 = endCalendar.get(Calendar.YEAR)
        val m2 = endCalendar.get(Calendar.MONTH) + 1
        val day2 = endCalendar.get(Calendar.DAY_OF_MONTH)
        val sb = StringBuilder()
        if (y1 != y2 || stay >= 3) {
            sb.append("yyyy-")
        }
        if (y1 != y2 || m1 != m2 || stay >= 2) {
            sb.append("MM-")
        }
        if (day1 != day2 || stay >= 1) {
            sb.append("dd ")
        }
        sb.append("HH:mm")
        return SimpleDateFormat(sb.toString(), Locale.getDefault()).format(Date(cur))
    }

    fun tintDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }

    private fun getStartCalendar(ts: Long): Calendar {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = ts
        return startCalendar
    }

    private fun getEndCalendar(ts: Long): Calendar {
        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = ts
        return endCalendar
    }

    fun parseFileSize(fileSize: Long): String {

        fun format(size: Float): String {
            return DecimalFormat("#.#").format(size)
        }

        val size = fileSize.coerceAtLeast(0)
        var fSize: Float
        if (size < 1024) {
            return size.toString() + "B"
        } else {
            fSize = size / 1024f
        }
        if (fSize < 1024f) {
            return format(fSize) + "KB"
        } else {
            fSize /= 1024f
        }
        return if (fSize < 1024) {
            format(fSize) + "MB"
        } else {
            fSize /= 1024f
            format(fSize) + "GB"
        }
    }
}