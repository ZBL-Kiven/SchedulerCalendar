package com.zj.swipeRv.cv.i

import android.graphics.Color
import com.zj.swipeRv.R
import java.io.Serializable

enum class Status(val id: Int, val color: Int, val strId: Int) : Serializable {
    Ended(2, Color.parseColor("#FF898989"), R.string.Ended), //
    Progressing(1, Color.parseColor("#FF22BB3B"), R.string.In_Progress), //
    Feature(0, Color.parseColor("#FF3680FF"), R.string.Pending), //
    Canceled(3, Color.parseColor("#FF898989"), R.string.Canceled);

    companion object {
        fun getWithId(id: Int): Status {
            return values().firstOrNull { it.id == id } ?: Ended
        }
    }
}
