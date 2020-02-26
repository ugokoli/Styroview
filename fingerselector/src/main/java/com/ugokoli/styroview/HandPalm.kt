package com.ugokoli.styroview

import android.graphics.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/25/2020
 */

class HandPalm {
    fun draw(canvas: Canvas?, hand: Hand, fingersTouchArea: HashMap<Finger, RectF>, paint: Paint) {
        val thumb = fingersTouchArea[Finger.THUMB]!!
        val index = fingersTouchArea[Finger.INDEX]!!
        val middle = fingersTouchArea[Finger.MIDDLE]!!
        val pinky = fingersTouchArea[Finger.PINKY]!!

        val tipPadding = (middle.bottom - middle.top) / 10
        val thumbHeight = (thumb.bottom - thumb.top)
        val halfThumbHeight = thumbHeight / 2

        for(finger in fingersTouchArea) {
            val rect = finger.value

            if(finger.key == Finger.THUMB) {
                //Finger sides: THUMB
                canvas?.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint)
                canvas?.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint)
            } else {
                val paddedTop = rect.top + tipPadding
                //Finger sides: OTHERS
                canvas?.drawLine(rect.left, paddedTop, rect.left, rect.bottom, paint)
                canvas?.drawLine(rect.right, paddedTop, rect.right, rect.bottom, paint)

                //Finger tip: OTHERS
                val tipOval = RectF(rect.left, rect.top, rect.right, rect.top + (tipPadding * 2))
                canvas?.drawArc(tipOval, 180f, 180f, false, paint)
            }
        }

        when(hand) {
            Hand.LEFT -> {
                //Extend INDEX left side to THUMB
                canvas?.drawLine(index.left, index.bottom, thumb.right, thumb.top, paint)

                //Finger tip: THUMB
                val rightCurvePoint = thumb.top + (halfThumbHeight / 2)
                drawCurve(canvas, thumb.left, thumb.top, thumb.right, rightCurvePoint, 90, paint)

                //PINKY extension lines
                canvas?.drawLine(pinky.right, pinky.bottom, pinky.right, thumb.bottom, paint)

                //Palm Butt
                val buttOval = RectF(thumb.left, thumb.bottom - thumbHeight, pinky.right, thumb.bottom + thumbHeight)
                canvas?.drawArc(buttOval, 180f, -180f, false, paint)

                //Thumb chick
                val extensionBottom = thumb.bottom + (halfThumbHeight / 2)
                drawCurve(canvas, thumb.right, thumb.bottom, middle.right, extensionBottom, 90, paint)
            }
            Hand.RIGHT -> {
                //Extend INDEX right side to THUMB
                canvas?.drawLine(index.right, index.bottom, thumb.left, thumb.top, paint)

                //Finger tip: THUMB
                val rightCurvePoint = thumb.top + (halfThumbHeight / 2)
                drawCurve(canvas, thumb.right, thumb.top, thumb.left, rightCurvePoint, -90, paint)

                //PINKY extension lines
                canvas?.drawLine(pinky.left, pinky.bottom, pinky.left, thumb.bottom, paint)

                //Palm Butt
                val buttOval = RectF(pinky.left, thumb.bottom - thumbHeight, thumb.right, thumb.bottom + thumbHeight)
                canvas?.drawArc(buttOval, 180f, -180f, false, paint)

                //Thumb chick
                val extensionBottom = thumb.bottom + (halfThumbHeight / 2)
                drawCurve(canvas, thumb.left, thumb.bottom, middle.left, extensionBottom, -90, paint)
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