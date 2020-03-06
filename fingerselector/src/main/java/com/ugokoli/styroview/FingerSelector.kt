package com.ugokoli.styroview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.ugokoli.styroview.constants.Finger
import com.ugokoli.styroview.constants.Hand
import kotlin.math.min


/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/16/2020
 */
class FingerSelector : View, ValueAnimator.AnimatorUpdateListener {
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

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var touching: Boolean = false

    private val paint = Paint()
    private val highLightPaint = Paint()
    private val drawHand = HandPalm()
    // Positional parameters defaults are derived from Hand.LEFT
    private lateinit var touchingFinger: Finger
    private lateinit var downTouchingPoint: PointF
    private lateinit var fingersTouchArea: HashMap<Finger, RectF>
    private var defaultFingerprint: Bitmap? = drawableToBitmap(resources.getDrawable(R.drawable.ic_fingerprint_black_120dp))
    private var mOrientation = 1 //1:up, 2:down
    var fingerprintImg = defaultFingerprint
    var hand = Hand.LEFT
        set(value) {
            field = value
            refresh()
            invalidate()
            emitSelected()
        }
    var finger = Finger.INDEX
        set(value) {
            field = value
            refresh()
            invalidate()
            emitSelected()
        }

    var fingerSelectedListener: FingerSelectedListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        //get custom attributes
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FingerSelector, 0, 0)

        try {
            mOrientation = a.getInteger(R.styleable.FingerSelector_orientation, mOrientation)
            hand = Hand.valueOf(a.getInteger(R.styleable.FingerSelector_defaultHand, hand.ordinal))
            finger = Finger.valueOf(a.getInteger(R.styleable.FingerSelector_defaultFinger, finger.ordinal))
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }
    }

    private fun refresh() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density * 3

        highLightPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        highLightPaint.isAntiAlias = true
        highLightPaint.style = Paint.Style.FILL_AND_STROKE
        highLightPaint.strokeWidth = resources.displayMetrics.density * 5

        downTouchingPoint = PointF()
        touchingFinger = Finger.NONE
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

    //DistalPhalanx
    private fun getFingerWidth(): Float {
        return (viewWidth - paddingLeft - paddingRight).toFloat() / 5
    }

    private fun getFingerTouchZoneRect(widthToLeftFactor: Int, widthToTopPercentage: Float, widthToHeightFactor: Float): RectF {
        val leftX = widthToLeftFactor * getFingerWidth()
        var x = leftX + paddingLeft

        // Mirror LEFT to RIGHT across x-axis
        if(hand == Hand.RIGHT) {
            x = viewWidth - leftX - getFingerWidth() - paddingRight
        }

        val leftY = (widthToTopPercentage * viewWidth / 100) + paddingTop

        return RectF(x, leftY, (x + getFingerWidth()), (leftY + (getFingerWidth() * widthToHeightFactor)))
    }

    private fun drawSelectedFingerprint(canvas: Canvas?) {
        if(finger != Finger.NONE) {
            if(fingerprintImg != null) {
                val rectF = fingersTouchArea[finger]!!
                val newBottom = rectF.top + rectF.right - rectF.left
                val desRectF = RectF(rectF.left, rectF.top, rectF.right, newBottom)
                val srcRect = Rect(0, 0, fingerprintImg!!.width, fingerprintImg!!.height)
                canvas?.drawBitmap(fingerprintImg!!, srcRect, desRectF, paint)
            }
        }
    }

    private fun getTouchedFingerFor(x: Float?, y: Float?): Finger {
        for(thisFinger in fingersTouchArea) {
            if(thisFinger.value.contains(x!!, y!!)) {
                return thisFinger.key
            }
        }

        return Finger.NONE
    }

    private fun emitSelected() {
        fingerSelectedListener?.onFingerSelected(hand, finger)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        refresh()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        viewWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        viewHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        // Recalculate viewHeight from contents(hand palm) boundries
        initializeFingersTouchArea()
        if(fingersTouchArea.containsKey(Finger.THUMB)) {
            val thumb = fingersTouchArea[Finger.THUMB]
            val thumbHeight = thumb!!.bottom - thumb.top

            viewHeight = thumb.bottom.toInt() + thumbHeight.toInt() + paddingBottom
        }

        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawHand.draw(canvas, hand, fingersTouchArea, touchingFinger, touching, highLightPaint, paint)
        drawSelectedFingerprint(canvas)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return false
        }
        val x = event?.x
        val y = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downTouchingPoint = PointF(x!!, y!!)
                touchingFinger = getTouchedFingerFor(x, y)
                touching = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                touching = false
                invalidate()

                //Evaluate finger select
                val finalTouchedFinger = getTouchedFingerFor(x, y)
                if (
                        touchingFinger != Finger.NONE
                        && touchingFinger != finger
                        && touchingFinger == finalTouchedFinger
                ) {
                    finger = finalTouchedFinger
                    emitSelected()
                    performClick()
                }

                //Evaluate hand swipe toggle
                val xSwipeDistance  = if(downTouchingPoint.x > x!!) {
                    downTouchingPoint.x - x
                } else {
                    x - downTouchingPoint.x
                }
                val xSwipeTime = event.eventTime - event.downTime

                //Toggle when x swipe distance spans across 2 fingers in less than a second
                if(xSwipeDistance > getFingerWidth() * 2 && xSwipeTime < 1000) {
                    hand = if(hand == Hand.RIGHT) {
                        Hand.LEFT
                    } else {
                        Hand.RIGHT
                    }
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