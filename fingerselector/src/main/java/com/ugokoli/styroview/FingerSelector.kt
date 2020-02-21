package com.ugokoli.styroview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/16/2020
 */
class FingerSelector : View, ValueAnimator.AnimatorUpdateListener {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)

    private val paint = Paint()
    // Positional parameters defaults are derived from Hand.LEFT
    private lateinit var selectedFinger: Finger
    private lateinit var selectedFingerRect: Rect
    private lateinit var distalPhalanxAreas: HashMap<Finger, Rect>
    private var defaultFingerprint: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_fingerprint_black_120dp)
    var fingerprintImg = defaultFingerprint
    var hand = Hand.LEFT
        set(value) {
            field = value
            init()
        }

    private fun getDistalPhalanxWidth(): Int {
        return width / 5
    }

    private fun getDistalPhalanxRect(leftX: Int, leftY: Int): Rect {
        var x = leftX
        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = (width/1) - leftX - getDistalPhalanxWidth()
        }

        return Rect(x, leftY, (x + getDistalPhalanxWidth()), (leftY + getDistalPhalanxWidth()))
    }

    private fun init() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 5

        selectedFinger = Finger.NONE
        selectedFingerRect = Rect()
        fingerprintImg = defaultFingerprint

        initializeHandFingersDistalPhalanxArea()
    }

    private fun initializeHandFingersDistalPhalanxArea() {
        distalPhalanxAreas = HashMap()

        distalPhalanxAreas[Finger.THUMB] = getDistalPhalanxRect(0, 0)
        distalPhalanxAreas[Finger.INDEX] = getDistalPhalanxRect(2, 0)
        distalPhalanxAreas[Finger.MIDDLE] = getDistalPhalanxRect(0, 3)
        distalPhalanxAreas[Finger.RING] = getDistalPhalanxRect(0, 0)
        distalPhalanxAreas[Finger.PINKY] = getDistalPhalanxRect(0, 0)
    }

    private fun drawHand(canvas: Canvas?) {
        //canvas.drawBitmap()
    }

    private fun drawSelectedFingerprint(canvas: Canvas?) {
        if(selectedFinger != Finger.NONE) {
            canvas?.drawBitmap(fingerprintImg, selectedFingerRect, selectedFingerRect, paint)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawHand(canvas)
        drawSelectedFingerprint(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}