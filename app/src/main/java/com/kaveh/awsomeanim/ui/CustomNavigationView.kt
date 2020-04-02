package com.kaveh.awsomeanim.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.view.iterator
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomNavigationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {
    private val mPaint = Paint()
    private val mLinePaint = Paint()
    private lateinit var mPoint: Pair<Float, Float>
    private lateinit var mOldPoint: Pair<Float, Float>
    private lateinit var mListener: OnNavigationItemSelectedListener
    private var isListenerBound = false
    private var isLineAnimationRun = false
    private var mIndicatorRadius = 10F
    private var mIndicatorColor = Color.parseColor("#03DAC5")

    init {
        this.setOnNavigationItemSelectedListener { item ->
            if (::mListener.isInitialized) mListener.onNavigationItemSelected(item)
            moveIndicatorXType2((2 * findSelectedItem(item.itemId) + 1) * (width / (this.menu.size() * 2F)))
            true
        }
        isListenerBound = true

        mPaint.style = Paint.Style.FILL
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.color = mIndicatorColor
        mLinePaint.style = Paint.Style.FILL
        mLinePaint.strokeCap = Paint.Cap.ROUND
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = mIndicatorRadius
        mLinePaint.color = mIndicatorColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPoint = Pair((2 * findSelectedItem(this.selectedItemId) + 1) * (width / (this.menu.size() * 2F)), height * .7F)
        mOldPoint = mPoint
    }

    private fun moveIndicatorX(location: Float) {
        val clickEffectAnim = ObjectAnimator.ofFloat(
                this, "indicatorLocationX", mPoint.first, location)
        clickEffectAnim.duration = 200
        clickEffectAnim.interpolator = AccelerateInterpolator()
        clickEffectAnim.start()
    }

    private fun moveIndicatorXType2(location: Float) {
        val clickEffectAnim = ObjectAnimator.ofFloat(
                this, "indicatorLocationX", mPoint.first, location)
        clickEffectAnim.duration = 150
        clickEffectAnim.interpolator = AccelerateInterpolator()
        clickEffectAnim.start()
        val clickEffectAnim2 = ObjectAnimator.ofFloat(
                this, "indicatorOldLocationX", mOldPoint.first, location)
        clickEffectAnim2.duration = 150
        clickEffectAnim2.interpolator = AccelerateInterpolator()
        isLineAnimationRun = true
        clickEffectAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                clickEffectAnim2.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        clickEffectAnim2.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isLineAnimationRun = false
                mOldPoint = mPoint
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    var indicatorLocationX: Float
        get() = mPoint.first
        set(location) {
            if (mPoint.first != location) {
                mPoint = Pair(location, mPoint.second)
                invalidate()
            }
        }

    var indicatorOldLocationX: Float
        get() = mOldPoint.first
        set(location) {
            if (mOldPoint.first != location) {
                mOldPoint = Pair(location, mOldPoint.second)
                invalidate()
            }
        }

    private fun findSelectedItem(itemId: Int): Int {
        var index = 0
        for (i in this.menu) {
            if (i.itemId == itemId) {
                return index
            }
            index++
        }
        return 0
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        if (listener != null) {
            mListener = listener
        }
        if (!isListenerBound)
            super.setOnNavigationItemSelectedListener(listener)
    }

    override fun onDraw(canvas: Canvas) {
        if (isLineAnimationRun) {
            canvas.drawLine(mOldPoint.first, mOldPoint.second, mPoint.first, mPoint.second, mLinePaint)
        } else {
            canvas.drawCircle(mPoint.first, mPoint.second, mIndicatorRadius, mPaint)
        }
    }
}