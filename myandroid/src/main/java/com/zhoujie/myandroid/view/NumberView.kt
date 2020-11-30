package com.zhoujie.myandroid.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import com.zhoujie.myandroid.px
import com.zhoujie.myandroid.sp2px
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class NumberView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var time = 0L

    init {
        isClickable = true
    }

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#F1F1F1")
        style = Paint.Style.FILL
    }

    private val operatorPaint = Paint().apply {
        color = Color.parseColor("#666666")
        style = Paint.Style.STROKE
        strokeWidth = 1.fpx()
    }

    private val dividerPaint = Paint().apply {
        color = Color.parseColor("#D8D8D8")
        style = Paint.Style.STROKE
        strokeWidth = 1.fpx()
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#666666")
        textSize = 14.sp2px(context).toFloat()
        textAlign = Paint.Align.CENTER
    }

    private fun drawBg(canvas: Canvas) {
        canvas.drawRoundRect(
            RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat()),
            7.fpx(),
            7.fpx(),
            bgPaint
        )
    }

    private fun drawMinus(canvas: Canvas) {
        canvas.withTranslation(8.fpx(), (measuredHeight / 2).toFloat()) {
            drawLine(0f, 0f, 10.fpx(), 0f, operatorPaint)
        }
    }

    private fun drawPlus(canvas: Canvas) {
        canvas.withTranslation(measuredWidth - 8.fpx() - 5.fpx(), (measuredHeight / 2).toFloat()) {
            drawLine((-5).fpx(), 0f, 5.fpx(), 0f, operatorPaint)
            drawLine(0f, (-5).fpx(), 0f, 5.fpx(), operatorPaint)
        }
    }

    private fun drawDivider(canvas: Canvas) {
        canvas.withTranslation(26.fpx(), (measuredHeight / 2).toFloat()) {
            drawLine(0f, (-8).fpx(), 0f, 8.fpx(), dividerPaint)
        }
        canvas.withTranslation(88.fpx(), (measuredHeight / 2).toFloat()) {
            drawLine(0f, (-8).fpx(), 0f, 8.fpx(), dividerPaint)
        }
    }

    private fun drawTime(canvas: Canvas) {
        canvas.withTranslation((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat()) {
            drawText(
                getCurrentDuration(),
                0f,
                abs((textPaint.ascent() + textPaint.descent()) / 2),
                textPaint
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawBg(it)
            drawDivider(it)
            drawMinus(it)
            drawPlus(it)
            drawTime(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    when (event.x) {
                        in 0F..(measuredWidth / 2).toFloat() -> {
                            if (time >= 1000) {
                                time -= 1000
                                invalidate()
                            }
                        }
                        in (measuredWidth / 2).toFloat()..measuredWidth.toFloat() -> {
                            time += 1000
                            invalidate()
                        }
                    }
                    true
                }
                else -> false
            }
        }
        return false
    }

    private fun getCurrentDuration(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+00:00")
        }.format(time)
    }

    private fun Int.fpx() = this.px(context).toFloat()
}