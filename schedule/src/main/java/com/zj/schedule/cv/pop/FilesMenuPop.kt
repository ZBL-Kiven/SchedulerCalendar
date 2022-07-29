package com.zj.schedule.cv.pop

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.zj.api.call
import com.zj.cusPop.CusPop
import com.zj.schedule.R
import com.zj.schedule.data.api.Body
import com.zj.schedule.data.api.ScheduleApi
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.sp.FileSPHelper
import java.lang.ref.WeakReference


object FilesMenuPop {

    private var pop: WeakReference<FileMenuPop>? = null

    fun dismiss() {
        pop?.get()?.dismiss()
    }

    fun show(view: View, m: ScheduleFileInfo?, p: Int, onDelete: (Boolean, Boolean, Int, ScheduleFileInfo?) -> Unit) {
        dismiss()
        if (m == null) return
        pop = WeakReference(FileMenuPop(view, m, p, onDelete).show())
    }

    internal class FileMenuPop(val view: View, private val m: ScheduleFileInfo, private val p: Int, private val onDelete: (Boolean, Boolean, Int, ScheduleFileInfo?) -> Unit) : PopupWindow.OnDismissListener {

        private var pop: CusPop? = null

        fun show(): FileMenuPop {
            view.isSelected = true
            val r = Rect()
            view.getGlobalVisibleRect(r)
            val topOffset = r.top
            pop = CusPop.create(view).contentId(R.layout.files_menu_pop_layout).offset(0, topOffset).dimColor(android.R.color.transparent).focusAble(true).outsideTouchAble(true).outsideTouchDismiss(true).instance()
            pop?.setOnDismissListener(this)
            pop?.show(Gravity.CENTER) { v, pop ->
                v.findViewById<View>(R.id.files_menu_pop_layout_tv_delete)?.setOnClickListener {
                    pop.dismiss()
                    view.isSelected = false
                    onConfirm(view, m, p, onDelete)
                }
            }
            pop?.setOnDismissListener(this)
            return this
        }

        private fun onConfirm(v: View, m: ScheduleFileInfo, p: Int, onDelete: (Boolean, Boolean, Int, ScheduleFileInfo?) -> Unit) {
            pop = CusPop.create(v).contentId(R.layout.confirm_pop_layout).overrideContentGravity(Gravity.CENTER).dimColor(android.R.color.transparent).focusAble(true).outsideTouchAble(false).instance()
            pop?.setOnDismissListener(this)
            pop?.show { v1, pop ->
                v1.findViewById<View>(R.id.confirm_pop_tv_cancel)?.setOnClickListener {
                    pop.dismiss()
                }
                v1.findViewById<View>(R.id.confirm_pop_tv_confirm)?.setOnClickListener {
                    pop.dismiss()
                    deleteFile(m, p, onDelete)
                }
            }
        }

        private fun deleteFile(info: ScheduleFileInfo, p: Int, onDelete: (Boolean, Boolean, Int, ScheduleFileInfo?) -> Unit) {
            onDelete(true, false, p, info)
            ScheduleApi.deleteMeetingFile(info.meetingId, info.id).call { isSuccess, _, _, _ ->
                if (isSuccess) {
                    info.url?.let { FileSPHelper.removeFileInfo(it) }
                }
                onDelete(false, isSuccess, p, info)
            }
        }

        fun dismiss() {
            pop?.dismiss()
        }

        override fun onDismiss() {
            view.isSelected = false
        }
    }
}