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

    private val WIDTH_TO_LEFT_FACTOR_THUMB = 0
    private val WIDTH_TO_LEFT_FACTOR_INDEX = 1
    private val WIDTH_TO_LEFT_FACTOR_MIDDLE = 2
    private val WIDTH_TO_LEFT_FACTOR_RING = 3
    private val WIDTH_TO_LEFT_FACTOR_PINKY = 4

    private val WIDTH_TO_TOP_PERCENTAGE_THUMB = 54.69f
    private val WIDTH_TO_TOP_PERCENTAGE_INDEX = 11.72f
    private val WIDTH_TO_TOP_PERCENTAGE_MIDDLE = 0f
    private val WIDTH_TO_TOP_PERCENTAGE_RING = 5.86f
    private val WIDTH_TO_TOP_PERCENTAGE_PINKY = 27.34f

    private val WIDTH_TO_HEIGHT_FACTOR_THUMB = 2.5f
    private val WIDTH_TO_HEIGHT_FACTOR_INDEX = 3.2f
    private val WIDTH_TO_HEIGHT_FACTOR_MIDDLE = 4f
    private val WIDTH_TO_HEIGHT_FACTOR_RING = 3.7f
    private val WIDTH_TO_HEIGHT_FACTOR_PINKY = 2.5f

    private var touching: Boolean = false

    private val paint = Paint()
    private val highLightPaint = Paint()
    private val drawHand = HandPalm()
    // Positional parameters defaults are derived from Hand.LEFT
    private lateinit var touchingFinger: Finger
    private lateinit var selectedFinger: Finger
    private lateinit var fingersTouchArea: HashMap<Finger, RectF>
    private var defaultFingerprint: Bitmap? = drawableToBitmap(resources.getDrawable(R.drawable.ic_fingerprint_black_120dp))
    var fingerprintImg = defaultFingerprint
    var hand = Hand.LEFT
        set(value) {
            field = value
        }

    var squarePressListener: FingerSelectedListener? = null

    //DistalPhalanx
    private fun getFingerWidth(): Float {
        return (width - paddingLeft - paddingRight).toFloat() / 5
    }

    private fun getFingerTouchZoneRect(widthToLeftFactor: Int, widthToTopPercentage: Float, widthToHeightFactor: Float): RectF {
        val leftX = widthToLeftFactor * getFingerWidth()
        var x = leftX + paddingLeft

        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = width - leftX - getFingerWidth() - paddingRight
        }

        val leftY = (widthToTopPercentage * width / 100) + paddingTop

        return RectF(x, leftY, (x + getFingerWidth()), (leftY + (getFingerWidth() * widthToHeightFactor)))
    }

    private fun init() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 3

        highLightPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        highLightPaint.isAntiAlias = true
        highLightPaint.style = Paint.Style.FILL_AND_STROKE
        highLightPaint.strokeWidth = resources.displayMetrics.density * 5

        hand = Hand.RIGHT
        touchingFinger = Finger.NONE
        selectedFinger = Finger.NONE
        fingerprintImg = defaultFingerprint

        initializeFingersTouchArea()
    }

    private fun initializeFingersTouchArea() {
        fingersTouchArea = HashMap()

        fingersTouchArea[Finger.THUMB] = getFingerTouchZoneRect(WIDTH_TO_LEFT_FACTOR_THUMB, WIDTH_TO_TOP_PERCENTAGE_THUMB, WIDTH_TO_HEIGHT_FACTOR_THUMB)
        fingersTouchArea[Finger.INDEX] = getFingerTouchZoneRect(WIDTH_TO_LEFT_FACTOR_INDEX, WIDTH_TO_TOP_PERCENTAGE_INDEX, WIDTH_TO_HEIGHT_FACTOR_INDEX)
        fingersTouchArea[Finger.MIDDLE] = getFingerTouchZoneRect(WIDTH_TO_LEFT_FACTOR_MIDDLE, WIDTH_TO_TOP_PERCENTAGE_MIDDLE, WIDTH_TO_HEIGHT_FACTOR_MIDDLE)
        fingersTouchArea[Finger.RING] = getFingerTouchZoneRect(WIDTH_TO_LEFT_FACTOR_RING, WIDTH_TO_TOP_PERCENTAGE_RING, WIDTH_TO_HEIGHT_FACTOR_RING)
        fingersTouchArea[Finger.PINKY] = getFingerTouchZoneRect(WIDTH_TO_LEFT_FACTOR_PINKY, WIDTH_TO_TOP_PERCENTAGE_PINKY, WIDTH_TO_HEIGHT_FACTOR_PINKY)
    }

    private fun drawSelectedFingerprint(canvas: Canvas?) {
        if(selectedFinger != Finger.NONE) {
            if(fingerprintImg != null) {
                val rectF = fingersTouchArea[selectedFinger]!!
                val newBottom = rectF.top + rectF.right - rectF.left
                val desRectF = RectF(rectF.left, rectF.top, rectF.right, newBottom)
                val srcRect = Rect(0, 0, fingerprintImg!!.width, fingerprintImg!!.height)
                canvas?.drawBitmap(fingerprintImg!!, srcRect, desRectF, paint)
            }
        }
    }

    private fun getTouchedFingerFor(x: Float?, y: Float?): Finger {
        for(finger in fingersTouchArea) {
            if(finger.value.contains(x!!, y!!)) {
                return finger.key
            }
        }

        return Finger.NONE
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

        drawHand.draw(canvas, hand, fingersTouchArea, touchingFinger, touching, highLightPaint, paint)
        drawSelectedFingerprint(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return false
        }
        val x = event?.x
        val y = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchingFinger = getTouchedFingerFor(x, y)
                touching = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                touching = false
                invalidate()

                val finalTouchedFinger = getTouchedFingerFor(x, y)
                if (touchingFinger != Finger.NONE && touchingFinger == finalTouchedFinger) {
                    selectedFinger = finalTouchedFinger
                    squarePressListener?.onFingerSelected(hand, finalTouchedFinger)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                touching = false
            }
        }
        return true
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        TODO("not implemented")
    }

    interface FingerSelectedListener {
        fun onFingerSelected(hand: Hand, finger: Finger)
    }
}