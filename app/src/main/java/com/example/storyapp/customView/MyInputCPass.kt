package com.example.storyapp.customview

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

class MyInputCPass : AppCompatEditText, View.OnTouchListener {
    var isCPassValid: Boolean = false
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
                checkCPass()
                checkPassHandler.postDelayed(this, 1000)
            }
        }
        checkPassHandler.postDelayed(checkPassRunnable, 1000)
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

    private var isClicked = false

    private fun checkCPass() {
        val pass = text?.trim()
        when {
            pass.isNullOrEmpty() -> {
                isCPassValid = false
                if (isClicked) {
                    error = resources.getString(R.string.pass_not_match)
                }
            }
            pass.length < 8 -> {
                isCPassValid = false
                if (isClicked) {
                    error = resources.getString(R.string.pass_not_match)
                }
            }
            else -> {
                isCPassValid = true
                error = null
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        isClicked = true
        return false
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) checkCPass()
    }


}