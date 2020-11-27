package com.zhoujie.myandroid.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import com.zhoujie.myandroid.R
import com.zhoujie.myandroid.px
import kotlin.math.abs

class VideoRangeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0f
    private var mHeight = 0f

    private val mCursorColor: Int
    private val mCursorWidth: Float
    private var mStrokeColor: Int
    private var mStrokeWidth: Float
    private val mTouchRange: Float   // 可触摸范围（20dp default）

    private val cursorPaint = Paint()
    private val cursorStrokePaint = Paint()
    private val strokePaint = Paint()

    private var startCursorPosition = 0f
    private var endCursorPosition = 0f
    private var touchPosition = 0f

    private var touchStart = false
    private var touchEnd = false

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.VideoRangeSlider, 0, 0).apply {
            try {
                mCursorColor =
                    getColor(
                        R.styleable.VideoRangeSlider_cursorColor,
                        Color.parseColor("#FF4949")
                    )
                mCursorWidth = getDimension(R.styleable.VideoRangeSlider_cursorWidth, 20.fpx())
                mStrokeColor = getColor(R.styleable.VideoRangeSlider_strokeColor, Color.WHITE)
                mStrokeWidth = getDimension(R.styleable.VideoRangeSlider_strokeColor, 2.fpx())
                mTouchRange = getDimension(R.styleable.VideoRangeSlider_touchRange, 48.fpx())
            } finally {
                recycle()
            }
        }

        cursorPaint.apply {
            color = mCursorColor
            style = Paint.Style.FILL
        }

        cursorStrokePaint.apply {
            color = mCursorColor
            style = Paint.Style.FILL
            strokeWidth = mStrokeWidth
        }

        strokePaint.apply {
            color = mStrokeColor
            style = Paint.Style.FILL
            strokeWidth = mStrokeWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        endCursorPosition = mWidth
        setMeasuredDimension(w, h)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        event?.let {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchPosition = event.x
                    performClick()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    moveCursor(event.x)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    true
                }
                else -> false
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        when {
            abs(touchPosition - startCursorPosition) <= mTouchRange -> {
                touchStart = true
                touchEnd = false
            }
            abs(touchPosition - endCursorPosition) <= mTouchRange -> {
                touchEnd = true
                touchStart = false
            }
            else -> {
                touchStart = false
                touchEnd = false
            }
        }
        return true
    }

    private fun moveCursor(position: Float) {
        if (touchStart) {
            if (position < endCursorPosition - mCursorWidth * 3 && position > 0) {
                startCursorPosition = position
                invalidate()
            }
        }
        if (touchEnd) {
            if (position > startCursorPosition + mCursorWidth * 3 && position < mWidth) {
                endCursorPosition = position
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawFrame(it)
        }
    }

    private fun drawFrame(canvas: Canvas) {
        // 画左边的矩形
        canvas.withTranslation(startCursorPosition, 0f) {
            canvas.drawRect(0f, 0f, mCursorWidth, mHeight, cursorPaint)

            // 画左边的里面的线
            canvas.withTranslation(mCursorWidth / 2, mHeight / 2) {
                drawLine(
                    0f,
                    -mHeight / 4,
                    0f,
                    mHeight / 4,
                    strokePaint
                )
            }
        }
        // 画上面的线
        canvas.drawLine(
            startCursorPosition,
            mStrokeWidth / 2,
            endCursorPosition,
            mStrokeWidth / 2,
            cursorStrokePaint
        )
        // 画下面的线
        canvas.withTranslation(0f, mHeight) {
            drawLine(
                startCursorPosition,
                -mStrokeWidth / 2,
                endCursorPosition,
                -mStrokeWidth / 2,
                cursorStrokePaint
            )
        }
        // 画右边的矩形
        canvas.withTranslation(endCursorPosition, 0f) {
            drawRect(0f, 0f, -mCursorWidth, mHeight, cursorPaint)
            // 画右边里面的线
            withTranslation(-mCursorWidth / 2, mHeight / 2) {
                drawLine(0f, -mHeight / 4, 0f, mHeight / 4, strokePaint)
            }
        }
    }

    private fun Int.fpx() = this.px(context).toFloat()
}