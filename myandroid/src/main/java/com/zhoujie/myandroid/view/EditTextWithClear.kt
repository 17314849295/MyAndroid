package com.zhoujie.myandroid.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.zhoujie.myandroid.R

class EditTextWithClear @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var clearIcon: Drawable? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.EditTextWithClear) {
            val clearIconId =
                getResourceId(R.styleable.EditTextWithClear_clearIcon, 0)
            clearIcon = ContextCompat.getDrawable(context, clearIconId)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            clearIcon?.let {
                if (event.action == MotionEvent.ACTION_UP
                    && event.x > width - it.intrinsicWidth - 20
                    && event.x < width + 20
                    && event.y > height / 2 - it.intrinsicHeight / 2 - 20
                    && event.y < height / 2 + it.intrinsicHeight / 2 + 20
                ) {
                    text?.clear()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        toggleClearIcon()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        toggleClearIcon()
    }

    private fun toggleClearIcon() {
        val icon = if (isFocused && text?.isNotEmpty() == true) clearIcon else null
        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)

    }
}