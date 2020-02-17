package com.ugokoli.styroview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/16/2020
 */
class FingerSelector : View, ValueAnimator.AnimatorUpdateListener {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)

    // Positional parameters defaults are derived from Hand.LEFT
    private lateinit var distalPhalanxAreas: HashMap<Finger, Rect>
    var hand = Hand.LEFT
        set(value) {
            field = value
            reset()
        }

    private fun getDistalPhalanxWidth(): Int {
        return width / 12
    }

    private fun getDistalPhalanxRect(leftX: Int, leftY: Int): Rect {
        var x = leftX
        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = (width/1) - leftX - getDistalPhalanxWidth()
        }

        return Rect(x, leftY, (x + getDistalPhalanxWidth()), (leftY + getDistalPhalanxWidth()))
    }

    private fun reset() {
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawHand(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}