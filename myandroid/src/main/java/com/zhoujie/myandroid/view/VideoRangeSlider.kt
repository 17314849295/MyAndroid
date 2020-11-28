package com.zhoujie.myandroid.view

import android.content.Context
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import com.zhoujie.myandroid.R
import com.zhoujie.myandroid.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

class VideoRangeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0f
    private var mHeight = 0f

    // 可配置的属性
    private val mCursorColor: Int
    private val mCursorWidth: Float
    private var mStrokeColor: Int
    private var mStrokeWidth: Float
    private val mTouchRange: Float   // 可触摸范围（20dp default）
    private val mFrameCount: Int
    private val mPreviewHeight: Float
    private val mPreviewMarginTop: Float

    // 可设置的属性
    private var mPreviewList: List<Bitmap>? = null
    private var mVideoRangeChangedListener: Listener? = null

    private val cursorPaint = Paint()
    private val cursorStrokePaint = Paint()
    private val strokePaint = Paint()

    private var startCursorPosition = 0f
    private var endCursorPosition = 0f
    private var touchPosition = 0f
    private var touchStart = false
    private var touchEnd = false

    private var previewWidth = 0f
    private var unitDuration = 0L

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
                mFrameCount = getInteger(R.styleable.VideoRangeSlider_frameCount, 10)
                mPreviewHeight = getDimension(R.styleable.VideoRangeSlider_previewHeight, 48.fpx())
                mPreviewMarginTop =
                    getDimension(R.styleable.VideoRangeSlider_previewHeight, 24.fpx())
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
        previewWidth = mWidth.div(mFrameCount)
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
        mVideoRangeChangedListener?.onVideoRangeChanged?.invoke(
            (startCursorPosition * unitDuration).toLong(),
            (endCursorPosition * unitDuration).toLong()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawFrame(it)
        }
    }

    private fun drawFrame(canvas: Canvas) {
        // 画预览的bitmap列表
        mPreviewList?.let {
            it.forEachIndexed { index, bitmap ->
                val dx = index * previewWidth
                canvas.withTranslation(dx) {
                    drawBitmap(bitmap, 0f, 0f, null)
                }
            }
        }

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

    fun setDataSource(videoUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmapList = mutableListOf<Bitmap>()
            MediaMetadataRetriever().apply {
                setDataSource(context, videoUri)
                val duration =
                    extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                unitDuration = duration?.div(mWidth)?.toLong() ?: 0L
                val perDuration = duration?.div(mFrameCount) ?: 0L
                for (index in 1..mFrameCount) {
                    getFrameAtTime(
                        index * perDuration,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )?.let {
                        bitmapList += it.scale()
                    }
                }
                mPreviewList = bitmapList
                release()
                if (bitmapList.isNotEmpty()) invalidate()
            }
        }
    }

    fun setVideoCallback(videoRangeListener: Listener.() -> Unit) {
        mVideoRangeChangedListener = Listener().also(videoRangeListener)
    }

    inner class Listener {
        internal var onVideoRangeChanged: ((Long, Long) -> Unit)? = null
        fun onVideoRangeChanged(block: (Long, Long) -> Unit) {
            onVideoRangeChanged = block
        }
    }

    private fun Bitmap.scale(): Bitmap {
        val result = Bitmap.createBitmap(previewWidth.toInt(), mWidth.toInt(), config)
        val canvas = Canvas(result)
        val scaleX = previewWidth.div(width)
        val scaleY = mHeight.div(height)
        val scale = max(scaleX, scaleY)
        val w = (width * scale).toInt()
        val h = (height * scale).toInt()
        val srcRect = Rect(0, 0, width, height)
        val destRect = Rect(((previewWidth - w) / 2).toInt(), ((mHeight - h) / 2).toInt(), w, h)
        canvas.drawBitmap(this, srcRect, destRect, null)
        return result
    }
}