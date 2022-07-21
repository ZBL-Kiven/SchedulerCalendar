package com.zj.swipeRv.cv

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zj.swipeRv.R


class CollapsedRecyclerView @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : RecyclerView(c, attr, def) {

    private var translationView: View? = null
    private var offset = (context.resources.displayMetrics.density * 8f + 0.5f).toInt()

    var collapsed = false
        set(value) {
            if (field != value) {
                field = value
                refreshDrawableState()
            }
        }

    init {
        if (attr != null) {
            val ta = c.obtainStyledAttributes(attr, R.styleable.CollapsedRecyclerView)
            try {
                collapsed = ta.getBoolean(R.styleable.CollapsedRecyclerView_collapsed, false)
            } finally {
                ta.recycle()
            }
        }
    }

    fun translationWith(v: View?) {
        this.translationView = v
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) onContentReady()
    }

    override fun setTranslationY(translationY: Float) {
        super.setTranslationY(translationY)
        onContentReady()
    }

    override fun onDisplayHint(hint: Int) {
        super.onDisplayHint(hint)
        onContentReady()
    }

    private fun onContentReady() {
        if (visibility == View.VISIBLE && !isAnimating) {
            translationView?.let {
                if (width <= 0) return@onContentReady
                val r = Rect()
                getGlobalVisibleRect(r)
                it.layoutParams?.let { lp ->
                    lp.width = this.width
                    lp.height = r.height() - paddingTop + offset
                    it.layoutParams = lp
                }
            }
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val st = super.onCreateDrawableState(extraSpace + 1)
        if (collapsed) {
            mergeDrawableStates(st, intArrayOf(R.attr.collapsed))
        }
        return st
    }
}