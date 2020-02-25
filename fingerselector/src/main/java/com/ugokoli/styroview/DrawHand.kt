package com.ugokoli.styroview

import android.graphics.*

/**
 * Author Ugonna Okoli
 * www.ugokoli.com
 * 2/25/2020
 */

class DrawHand {
    fun draw(canvas: Canvas?, hand: Hand, fingersTouchArea: HashMap<Finger, Rect>, paint: Paint) {
        val thumb = fingersTouchArea[Finger.THUMB]!!
        val pinky = fingersTouchArea[Finger.PINKY]!!

        for(finger in fingersTouchArea) {
            val rect = finger.value

            val tipPadding = (rect.bottom - rect.top) / 10
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

//                val path = Path()
//                path.addArc(tipOval, 90f, 90f)
//                canvas?.drawPath(path, paint)

                //Palm Butt
                //canvas?.drawArc(thumb.left.toFloat(), thumb.top.toFloat(), pinky.right.toFloat(), pinky.top.toFloat(), 180f, -180f, false, paint)
            }
            Hand.RIGHT -> {

            }
        }
    }
}