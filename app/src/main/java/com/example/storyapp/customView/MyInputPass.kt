package com.example.storyapp.customView

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class MyInputPass : AppCompatEditText, View.OnTouchListener {
    var isPassValid: Boolean = false
    private lateinit var lockIcon: Drawable
    private var checkPassHandler = Handler()
    private lateinit var checkPassRunnable: Runnable
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        lockIcon = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        transformationMethod = PasswordTransformationMethod.getInstance()
        onShowVisibilityIcon(lockIcon)

        checkPassRunnable = object : Runnable {
            override fun run() {
                checkPass()
                checkPassHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

    }


    private fun checkPass() {
        val pass = text?.trim()
        when {
            pass.isNullOrEmpty() -> {
                isPassValid = false
                error = resources.getString(R.string.input_pass)
            }
            pass.length < 8 -> {
                isPassValid = false
                error = resources.getString(R.string.pass_length)
            }
            else -> {
                isPassValid = true
            }
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            // Start checking the password when the field gains focus
            checkPassHandler.postDelayed(checkPassRunnable, 1000)
        } else {
            // Stop checking the password when the field loses focus
            checkPassHandler.removeCallbacks(checkPassRunnable)
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}