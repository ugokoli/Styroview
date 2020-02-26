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
    private val drawHand = HandPalm()
    // Positional parameters defaults are derived from Hand.LEFT
    private lateinit var selectedFinger: Finger
    private lateinit var selectedFingerRect: RectF
    private lateinit var fingersTouchArea: HashMap<Finger, RectF>
    private var defaultFingerprint: Bitmap? = drawableToBitmap(resources.getDrawable(R.drawable.ic_fingerprint_black_120dp))
    var fingerprintImg = defaultFingerprint
    var hand = Hand.LEFT
        set(value) {
            field = value
        }

    //DistalPhalanx
    private fun getFingerWidth(): Float {
        return (width - paddingLeft - paddingRight).toFloat() / 5
    }

    private fun getFingerTouchZoneRect(widthToLeftFactor: Int, widthToTopPercentage: Float, widthToHeightFactor: Float): RectF {
        val leftX = widthToLeftFactor * getFingerWidth()
        var x = leftX + paddingLeft

        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = width - leftX - getFingerWidth() + paddingRight
        }

        val leftY = (widthToTopPercentage * width / 100) + paddingTop

        return RectF(x, leftY, (x + getFingerWidth()), (leftY + (getFingerWidth() * widthToHeightFactor)))
    }

    private fun init() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 5

        hand = Hand.LEFT
        selectedFinger = Finger.NONE
        selectedFingerRect = RectF()
        fingerprintImg = defaultFingerprint

        initializeFingersTouchArea()
    }

    private fun initializeFingersTouchArea() {
        fingersTouchArea = HashMap()

        fingersTouchArea[Finger.THUMB] = getFingerTouchZoneRect(0, 54.69f, 2.5f)
        fingersTouchArea[Finger.INDEX] = getFingerTouchZoneRect(1, 11.72f, 3.2f)
        fingersTouchArea[Finger.MIDDLE] = getFingerTouchZoneRect(2, 0f, 4f)
        fingersTouchArea[Finger.RING] = getFingerTouchZoneRect(3, 5.86f, 3.7f)
        fingersTouchArea[Finger.PINKY] = getFingerTouchZoneRect(4, 27.34f, 2.5f)
    }

    private fun drawSelectedFingerprint(canvas: Canvas?) {
        if(selectedFinger != Finger.NONE) {
            if(fingerprintImg != null) {
                //canvas?.drawBitmap(fingerprintImg!!, selectedFingerRect, selectedFingerRect, paint)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //drawBitmapHand(canvas)
        drawHand.draw(canvas, hand, fingersTouchArea, paint)
        drawSelectedFingerprint(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}