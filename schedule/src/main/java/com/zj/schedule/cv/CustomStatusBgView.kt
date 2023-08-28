package com.zj.schedule.cv

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.zj.schedule.cv.i.Status
import kotlin.math.acos

open class CustomStatusBgView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : ConstraintLayout(context, attributeSet, def) {

    open var status: Status? = null
        set(value) {
            if (value != null && field != value) {
                field = value
                onStatusChanged(value)
                postInvalidate()
            }
        }

    private var curHitAnimAlpha = 0f
    private var inPressed = false
    private var anim: ValueAnimator? = null

    private var animListener = ValueAnimator.AnimatorUpdateListener { p0 -> curHitAnimAlpha = p0?.animatedFraction ?: 1f;postInvalidate() }

    open fun onStatusChanged(status: Status) {}

    open fun showHintAnim() {
        anim?.end()
        anim?.cancel()
        anim = ValueAnimator.ofFloat(0.2f, 1.0f).setDuration(400)
        anim?.repeatCount = 5
        anim?.addUpdateListener(animListener)
        anim?.repeatMode = ValueAnimator.REVERSE
        anim?.start()
    }

    open fun stopHintAnim() {
        anim?.end()
        anim?.cancel()
        anim = null
        curHitAnimAlpha = 0f
        postInvalidate()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.save()
        if (canvas != null && width > 0 && height > 0) {
            drawBg(canvas)
        }
        canvas?.restore()
        super.dispatchDraw(canvas)
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        inPressed = pressed
        invalidate()
    }

    private fun drawBg(canvas: Canvas) {
        val height = this.height * 1.0f
        val bgPaint = Paint()
        val round = dp2px(12f)
        val rf = RectF(0f, 0f, width * 1.0f, height)

        /*-- draw bg --*/
        bgPaint.reset()
        bgPaint.isAntiAlias = true
        bgPaint.textAlign = Paint.Align.CENTER
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = Color.parseColor("#FF141414")
        bgPaint.alpha = if (inPressed) 170 else 255
        canvas.drawRoundRect(rf, round, round, bgPaint)

        /*-- draw tag --*/
        val tagWidth = dp2px(4f)
        val cos = (round - tagWidth) / round
        val ac = acos(cos)
        val thr = ac / (Math.PI / 180f).toFloat()
        val topArc = RectF(0f, 0f, round * 2f, round * 2f)
        val bottomArc = RectF(0f, height - round * 2, round * 2f, height)
        val path = Path()
        path.moveTo(0f, round)
        path.arcTo(topArc, 180f, thr, false)
        path.lineTo(tagWidth, height - round)
        path.arcTo(bottomArc, 180f - thr, thr, false)
        path.close()
        val tagPaint = Paint()
        tagPaint.isAntiAlias = true
        tagPaint.style = Paint.Style.FILL
        tagPaint.textAlign = Paint.Align.CENTER
        tagPaint.color = status?.color ?: 0
        tagPaint.alpha = if (inPressed) 170 else 255
        canvas.drawPath(path, tagPaint)

        /*-- draw hint --*/
        if (curHitAnimAlpha > 0f) {
            val strokePath = Path()
            strokePath.addRect(rf, Path.Direction.CW)
            bgPaint.isAntiAlias = true
            bgPaint.style = Paint.Style.STROKE
            val width = dp2px(2f)
            bgPaint.strokeWidth = width
            bgPaint.color = Color.GRAY
            bgPaint.alpha = (curHitAnimAlpha * 255f).toInt()
            val gradient = SweepGradient(2f, -2f, intArrayOf(Color.WHITE, Color.TRANSPARENT, Color.WHITE), null)
            bgPaint.shader = gradient
            val efc = CornerPathEffect(round)
            bgPaint.pathEffect = efc
            canvas.drawPath(strokePath, bgPaint)
        }
    }

    private fun dp2px(dp: Float): Float {
        return Resources.getSystem().displayMetrics.density * dp
    }
}