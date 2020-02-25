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

class DrawHand {
    fun draw(canvas: Canvas?, hand: Hand, fingersTouchArea: HashMap<Finger, Rect>, paint: Paint) {
        val thumb = fingersTouchArea[Finger.THUMB]!!
        val middle = fingersTouchArea[Finger.MIDDLE]!!
        val pinky = fingersTouchArea[Finger.PINKY]!!

        val tipPadding = (middle.bottom - middle.top) / 10
        val thumbHeight = (thumb.bottom - thumb.top)
        val halfThumbHeight = thumbHeight / 2

        for(finger in fingersTouchArea) {
            val rect = finger.value

            val paddedTop = rect.top.toFloat() + tipPadding

            canvas?.drawLine(rect.left.toFloat(), paddedTop, rect.left.toFloat(), rect.bottom.toFloat(), paint)
            canvas?.drawLine(rect.right.toFloat(), paddedTop, rect.right.toFloat(), rect.bottom.toFloat(), paint)
            //Finger tip: OTHERS
            if(finger.key != Finger.THUMB) {
                val tipOval = RectF(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.top.toFloat() + (tipPadding * 2))
                canvas?.drawArc(tipOval, 180f, 180f, false, paint)
            }
        }

        when(hand) {
            Hand.LEFT -> {
                //Finger tip: THUMB
                val tipOval = RectF(thumb.left.toFloat(), thumb.top.toFloat(), thumb.right.toFloat(), thumb.bottom.toFloat())
                canvas?.drawArc(tipOval, 180f, 180f, false, paint)

                //Thumb finger extension lines
                val extensionBottom = thumb.bottom.toFloat() + halfThumbHeight
                canvas?.drawLine(thumb.left.toFloat(), thumb.bottom.toFloat(), thumb.left.toFloat(), extensionBottom, paint)
                canvas?.drawLine(pinky.right.toFloat(), pinky.bottom.toFloat(), pinky.right.toFloat(), extensionBottom, paint)

                //Palm Butt
                val buttOval = RectF(thumb.left.toFloat(), extensionBottom - thumbHeight, pinky.right.toFloat(), extensionBottom + thumbHeight)
                canvas?.drawArc(buttOval, 180f, -180f, false, paint)

                //Thumb chick
                drawThumbChick(canvas, thumb.right.toFloat(), thumb.bottom.toFloat(), middle.right.toFloat(), extensionBottom, paint)
            }
            Hand.RIGHT -> {

            }
        }
    }

    private fun drawThumbChick(canvas: Canvas?, aX: Float, aY: Float, bX: Float, bY: Float, paint: Paint) {
        val path = Path()
        val curveRadius = 50
        val midX = aX + (bX - aX) / 2
        val midY = aY + (bY - aY) / 2
        val xDiff = midX - aX
        val yDiff = midY - aY
        val angle = atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) - 90
        val angleRadians = Math.toRadians(angle)
        val pointX = (midX + curveRadius * cos(angleRadians)).toFloat()
        val pointY = (midY + curveRadius * sin(angleRadians)).toFloat()
        path.moveTo(aX, aY)
        path.cubicTo(aX, aY, pointX, pointY, bX, bY)
        canvas?.drawPath(path, paint)
    }
}