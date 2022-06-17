package com.zj.swipeRv

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.zj.calendar.CalendarLayout

class CalendarScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : LinearLayout(context, attrs, def), CalendarLayout.CalendarScrollView {

    override fun isScrollToTop(): Boolean {
        repeat(childCount) {
            val v = getChildAt(it)
            if (v is RecyclerView || v is NestedScrollView) {

                /*只找第一个，多嵌套滚动用不上，按目前需求来说，只需要支持 recyclerView ，后期有需要变更的找我兼容*/
                return !v.canScrollVertically(-1)
            }
        }
        return true
    }
}