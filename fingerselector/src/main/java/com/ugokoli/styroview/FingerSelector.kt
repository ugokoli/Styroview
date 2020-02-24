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
    private lateinit var fingersTouchArea: HashMap<Finger, Rect>
    private var handLeftImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_hand_profile_left)
    private var handRightImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_hand_profile_right)
    private var defaultFingerprint: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_fingerprint_black_120dp)
    var fingerprintImg = defaultFingerprint
    var hand = Hand.LEFT
        set(value) {
            field = value
            init()
        }

    //DistalPhalanx
    private fun getFingerWidth(): Int {
        return width / 5
    }

    private fun getFingerTouchZoneRect(widthToLeftFactor: Int, widthToTopPercentage: Float, widthToHeightFactor: Float = 3f): Rect {
        val leftX = widthToLeftFactor * getFingerWidth()
        var x = leftX

        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = (width/1) - leftX - getFingerWidth()
        }

        val leftY = (widthToTopPercentage * width / 100).toInt()

        return Rect(x, leftY, (x + getFingerWidth()), (leftY + (getFingerWidth() * widthToHeightFactor)).toInt())
    }

    private fun init() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 5

        hand = Hand.LEFT
        selectedFinger = Finger.NONE
        selectedFingerRect = Rect()
        fingerprintImg = defaultFingerprint

        initializeFingersTouchArea()
    }

    private fun initializeFingersTouchArea() {
        fingersTouchArea = HashMap()

        fingersTouchArea[Finger.THUMB] = getFingerTouchZoneRect(0, 54.69f)
        fingersTouchArea[Finger.INDEX] = getFingerTouchZoneRect(1, 11.72f, 3.2f)
        fingersTouchArea[Finger.MIDDLE] = getFingerTouchZoneRect(2, 0f, 4f)
        fingersTouchArea[Finger.RING] = getFingerTouchZoneRect(3, 5.86f, 3.7f)
        fingersTouchArea[Finger.PINKY] = getFingerTouchZoneRect(4, 27.34f, 2.5f)
    }

    private fun drawHand(canvas: Canvas?) {
        when(hand) {
            Hand.LEFT -> {
                canvas?.drawBitmap(handLeftImg, 0f, 0f, paint)
            }
            Hand.RIGHT -> {
                canvas?.drawBitmap(handRightImg, 0f, 0f, paint)
            }
        }
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