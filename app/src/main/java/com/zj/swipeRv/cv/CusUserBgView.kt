package com.zj.swipeRv.cv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.zj.swipeRv.R
import com.zj.swipeRv.cv.i.MeetingMemberIn

class CusUserBgView @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : AppCompatImageView(c, attr, def) {

    private var strokeColor: Int = 0
    private var stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, c.resources.displayMetrics)
    private var data: MeetingMemberIn? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val ta = c.obtainStyledAttributes(attr, R.styleable.CusUserBgView)
        try {
            strokeColor = ta.getColor(R.styleable.CusUserBgView_strokeColor, Color.BLACK)
            stroke = ta.getDimension(R.styleable.CusUserBgView_strokeWidth, stroke)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ta.recycle()
        }
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt()
        setPadding(padding, padding, padding, padding)
    }

    fun setData(d: MeetingMemberIn) {
        this.data = d
        invalidate()
        Glide.with(this).load(d.getAvatar()).transform(CircleCrop()).override(width, height).into(this)
    }

    override fun draw(canvas: Canvas?) {
        data?.let {
            if (width > 0 && height > 0) {
                canvas?.save()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = stroke
                paint.color = strokeColor
                canvas?.drawCircle(width / 2f, height / 2f, width / 2f, paint)
                paint.style = Paint.Style.FILL
                paint.color = it.hashColor()
                canvas?.drawCircle(width / 2f, height / 2f, width / 2f, paint)
                canvas?.restore()
            }
        }
        super.draw(canvas)
    }
}