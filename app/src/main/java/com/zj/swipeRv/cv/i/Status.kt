package com.zj.swipeRv.cv.i

import android.graphics.Color
import com.zj.swipeRv.R
import java.io.Serializable

enum class Status(val color: Int, val strId: Int) : Serializable {
    Ended(Color.parseColor("#FF898989"), R.string.Ended), //
    Progressing(Color.parseColor("#FF22BB3B"), R.string.In_Progress), //
    Feature(Color.parseColor("#FF3680FF"), R.string.Pending), //
    Canceled(Color.parseColor("#FF898989"), R.string.Canceled);
}
