/*
 * Ugonna Okoli
 * http://ugokoli.com
 * Copyright (c) 2019.
 */
package com.ugokoli.styroview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatEditText

class SplitEditText : AppCompatEditText {
    private var mCharSize = 0f
    private var mNumChars = 4f
    private var mLineSpacing = 8f //8dp by default, height of the text from our lines
    private val mMaxLength = 4
    //Attribute values
    private var mMask: String? = "*"
    private var mMaskCode = true
    private var mFieldShape = 1 //1:line, 2:rectangle, 3:circle
    private var mGroup = 1 //How many chars in a box
    private var mSpace = 24f //24dp by default, space between the lines
    private var mDefaultColor = Color.WHITE
    private var mSelectedColor = Color.YELLOW //App accent color should do by default
    private var mFilledColor = mDefaultColor
    private var mCodeColor = Color.GRAY
    private var mClickListener: OnClickListener? = null
    private var mLineStroke = 1f //1dp by default
    private var mLineStrokeSelected = 2f //2dp by default
    private var mLinesPaint: Paint? = null
    var mStates = arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf(android.R.attr.state_selected), intArrayOf(android.R.attr.state_checked))
    var mColorsNew = intArrayOf(
            mDefaultColor,
            mSelectedColor,
            mFilledColor)
    var mColorStates = ColorStateList(mStates, mColorsNew)

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val multi = context.resources.displayMetrics.density
        mLineStroke = multi * mLineStroke
        mLineStrokeSelected = multi * mLineStrokeSelected
        mLinesPaint = Paint(paint)
        mLinesPaint!!.strokeWidth = mLineStroke
        setBackgroundResource(0)
        mSpace = multi * mSpace //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing //convert to pixels for our density
        mNumChars = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", mMaxLength).toFloat()
        //get custom attributes
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SplitEditText, 0, 0)
        try {
            setMask(a.getString(R.styleable.SplitEditText_mask))
            setMaskCode(a.getBoolean(R.styleable.SplitEditText_maskCode, mMaskCode))
            setSpace(a.getFloat(R.styleable.SplitEditText_space, mSpace))
            setGroup(a.getInteger(R.styleable.SplitEditText_group, mGroup))
            setFieldShape(a.getInteger(R.styleable.SplitEditText_fieldShape, mFieldShape))
            setDefaultColor(Color.parseColor(a.getString(R.styleable.SplitEditText_defaultColor)))
            setSelectedColor(Color.parseColor(a.getString(R.styleable.SplitEditText_selectedColor)))
            setFilledColor(Color.parseColor(a.getString(R.styleable.SplitEditText_filledColor)))
            setCodeColor(Color.parseColor(a.getString(R.styleable.SplitEditText_codeColor)))
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }
        //Disable copy paste
        super.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }
        })
        // When tapped, move cursor to end of text.
        super.setOnClickListener { v ->
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
    }

    fun setMask(mask: String?) {
        mMask = mask
    }

    fun setMaskCode(maskCode: Boolean) {
        mMaskCode = maskCode
    }

    fun setGroup(group: Int) {
        mGroup = group
    }

    fun setSpace(space: Float) {
        mSpace = space
    }

    fun setFieldShape(shape: Int) {
        mFieldShape = shape
    }

    fun setCodeColor(color: Int) {
        mCodeColor = color
    }

    fun setDefaultColor(color: Int) {
        mDefaultColor = color
        resetColorStateList()
    }

    fun setSelectedColor(color: Int) {
        mSelectedColor = color
        resetColorStateList()
    }

    fun setFilledColor(color: Int) {
        mFilledColor = color
        resetColorStateList()
    }

    private fun resetColorStateList() { //Re initialize values
        mColorsNew = intArrayOf(
                mDefaultColor,
                mSelectedColor,
                mFilledColor)
        mColorStates = ColorStateList(mStates, mColorsNew)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas) { //super.onDraw(canvas);
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        var startX = paddingLeft
        val startY = paddingTop
        val endY = height - paddingBottom
        //Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(text, 0, textLength, textWidths)
        //Set the text code paint
        val textPaint: Paint = paint
        textPaint.color = mCodeColor
        //duplicate text
        val maskedText = Editable.Factory.getInstance().newEditable(text)
        if (mMaskCode) {
            maskedText.replace(0, textLength, String(CharArray(textLength)).replace("\u0000", mMask!!, false))
        }
        //Actual rendering of each code view
        var i = 0
        while (i < mNumChars) {
            var state = android.R.attr.state_focused //focused|default
            if (i < textLength) {
                state = android.R.attr.state_checked //checked
            } else if (i == textLength) {
                state = android.R.attr.state_selected //selected
            }
            updateColorForLines(state)
            val radius = mCharSize / 2
            when (mFieldShape) {
                3 -> canvas.drawCircle(startX + radius, startY + radius, radius, mLinesPaint!!)
                2 -> canvas.drawRect(startX.toFloat(), startY.toFloat(), startX + mCharSize, height.toFloat(), mLinesPaint!!)
                else -> canvas.drawLine(startX.toFloat(), height.toFloat(), startX + mCharSize, height.toFloat(), mLinesPaint!!)
            }
            //Draw text code
            if (textLength > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(maskedText, i, i + 1, middle - textWidths[0] / 2, endY - mLineStroke, textPaint)
            }
            if (mSpace < 0) {
                startX += mCharSize.toInt() * 2
            } else {
                startX += mCharSize.toInt() + mSpace.toInt()
            }
            i++
        }
    }

    private fun getColorForState(vararg states: Int): Int {
        return mColorStates.getColorForState(states, Color.WHITE)
    }

    /**
     * @param state Is the current state of the current input box
     */
    private fun updateColorForLines(state: Int) { //        if (isFocused()) {
//            mLinesPaint.setStrokeWidth(mLineStrokeSelected);
//            mLinesPaint.setColor(getColorForState(state));
//        } else {
//            mLinesPaint.setStrokeWidth(mLineStroke);
//            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
//        }
        mLinesPaint!!.strokeWidth = mLineStrokeSelected
        if (!isFocused && state == android.R.attr.state_selected) {
            mLinesPaint!!.color = getColorForState(android.R.attr.state_focused)
        } else {
            mLinesPaint!!.color = getColorForState(state)
        }
    }

    companion object {
        const val XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"
    }
}