package com.ugokoli.styroview

import android.graphics.*
import com.ugokoli.styroview.constants.Finger
import com.ugokoli.styroview.constants.Hand
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/25/2020
 */

class HandPalm {
    fun draw(canvas: Canvas?, hand: Hand, fingersTouchArea: HashMap<Finger, RectF>, touchingFinger: Finger, touching: Boolean, highLightPaint: Paint, linePaint: Paint) {
        var fingerPaint = linePaint

        val thumb = fingersTouchArea[Finger.THUMB]!!
        val index = fingersTouchArea[Finger.INDEX]!!
        val middle = fingersTouchArea[Finger.MIDDLE]!!
        val pinky = fingersTouchArea[Finger.PINKY]!!

        val tipPadding = (middle.bottom - middle.top) / 10
        val thumbHeight = (thumb.bottom - thumb.top)
        val halfThumbHeight = thumbHeight / 2
        val rightCurvePoint = thumb.top + (halfThumbHeight / 2)
        val extensionBottom = thumb.bottom + (halfThumbHeight / 2)

        for(finger in fingersTouchArea) {
            val rect = finger.value
            if(touching && touchingFinger == finger.key) {
                fingerPaint = highLightPaint
            }

            if(finger.key != Finger.THUMB) {
                val paddedTop = rect.top + tipPadding

                //Finger tip: OTHERS
                val tipOval = RectF(rect.left, rect.top, rect.right, rect.top + (tipPadding * 2))
                canvas?.drawArc(tipOval, 180f, 180f, false, fingerPaint)
                //Finger sides: OTHERS
                canvas?.drawLine(rect.left, paddedTop, rect.left, rect.bottom, fingerPaint)
                canvas?.drawLine(rect.right, paddedTop, rect.right, rect.bottom, fingerPaint)
            }

            fingerPaint = linePaint
        }

        if(touching && touchingFinger == Finger.THUMB) {
            fingerPaint = highLightPaint
        }

        when(hand) {
            Hand.LEFT -> {
                //Extend INDEX left side to THUMB
                canvas?.drawLine(index.left, index.bottom, thumb.right, rightCurvePoint, linePaint)

                //Finger tip: THUMB
                drawCurve(canvas, thumb.left, thumb.top, thumb.right, rightCurvePoint, 90, fingerPaint)
                //Finger sides: THUMB
                canvas?.drawLine(thumb.left, thumb.top, thumb.left, thumb.bottom, fingerPaint)
                canvas?.drawLine(thumb.right, rightCurvePoint, thumb.right, thumb.bottom, fingerPaint)

                //PINKY extension lines
                canvas?.drawLine(pinky.right, pinky.bottom, pinky.right, thumb.bottom, linePaint)

                //Palm Butt
                val buttOval = RectF(thumb.left, thumb.bottom - thumbHeight, pinky.right, thumb.bottom + thumbHeight)
                canvas?.drawArc(buttOval, 180f, -180f, false, linePaint)

                //Thumb chick
                drawCurve(canvas, thumb.right, thumb.bottom, middle.right, extensionBottom, 90, linePaint)
            }
            Hand.RIGHT -> {
                //Extend INDEX right side to THUMB
                canvas?.drawLine(index.right, index.bottom, thumb.left, rightCurvePoint, linePaint)

                //Finger tip: THUMB
                drawCurve(canvas, thumb.right, thumb.top, thumb.left, rightCurvePoint, -90, fingerPaint)
                //Finger sides: THUMB
                canvas?.drawLine(thumb.left, rightCurvePoint, thumb.left, thumb.bottom, fingerPaint)
                canvas?.drawLine(thumb.right, thumb.top, thumb.right, thumb.bottom, fingerPaint)

                //PINKY extension lines
                canvas?.drawLine(pinky.left, pinky.bottom, pinky.left, thumb.bottom, linePaint)

                //Palm Butt
                val buttOval = RectF(pinky.left, thumb.bottom - thumbHeight, thumb.right, thumb.bottom + thumbHeight)
                canvas?.drawArc(buttOval, 180f, -180f, false, linePaint)

                //Thumb chick
                drawCurve(canvas, thumb.left, thumb.bottom, middle.left, extensionBottom, -90, linePaint)
            }
        }
    }

    private fun drawCurve(canvas: Canvas?, aX: Float, aY: Float, bX: Float, bY: Float, curveRadius: Int, paint: Paint) {
        val path = Path()
        val midX = aX + (bX - aX) / 2
        val midY = aY + (bY - aY) / 2
        val xDiff = midX - aX
        val yDiff = midY - aY
        val angle = atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) - 90
        val angleRadians = Math.toRadians(angle)
        val pointX = (midX + curveRadius * cos(angleRadians))
        val pointY = (midY + curveRadius * sin(angleRadians))
        path.moveTo(aX, aY)
        path.cubicTo(aX, aY, pointX.toFloat(), pointY.toFloat(), bX, bY)
        canvas?.drawPath(path, paint)
    }
}