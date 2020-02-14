/*
 * Ugonna Okoli
 * http://ugokoli.com
 * Copyright (c) 2019.
 */

package ltd.uxlab.styroview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import ltd.uxlab.styroview.R;

public class SplitEditText extends AppCompatEditText {
    public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

    private float mCharSize;
    private float mNumChars = 4;
    private float mLineSpacing = 8; //8dp by default, height of the text from our lines
    private int mMaxLength = 4;
    //Attribute values
    private String mMask = "*";
    private boolean mMaskCode = true;
    private int mFieldShape = 1;//1:line, 2:rectangle, 3:circle
    private int mGroup = 1;//How many chars in a box
    private float mSpace = 24; //24dp by default, space between the lines
    private int mDefaultColor = Color.WHITE;
    private int mSelectedColor = Color.YELLOW;//App accent color should do by default
    private int mFilledColor = mDefaultColor;
    private int mCodeColor = Color.GRAY;

    private OnClickListener mClickListener;

    private float mLineStroke = 1; //1dp by default
    private float mLineStrokeSelected = 2; //2dp by default
    private Paint mLinesPaint;

    int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_focused}, // focused
            new int[]{android.R.attr.state_selected}, // selected
            new int[]{android.R.attr.state_checked}, // checked
    };
    int[] mColorsNew = new int[]{
            mDefaultColor,
            mSelectedColor,
            mFilledColor,
    };
    ColorStateList mColorStates = new ColorStateList(mStates, mColorsNew);

    public SplitEditText(Context context) {
        super(context);
    }

    public SplitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SplitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLineStrokeSelected = multi * mLineStrokeSelected;
        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);

        setBackgroundResource(0);

        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels for our density
        mNumChars = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", mMaxLength);

        //get custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SplitEditText, 0, 0);
        try {
            setMask(a.getString(R.styleable.SplitEditText_mask));
            setMaskCode(a.getBoolean(R.styleable.SplitEditText_maskCode, mMaskCode));
            setSpace(a.getFloat(R.styleable.SplitEditText_space, mSpace));
            setGroup(a.getInteger(R.styleable.SplitEditText_group, mGroup));
            setFieldShape(a.getInteger(R.styleable.SplitEditText_fieldShape, mFieldShape));
            setDefaultColor(Color.parseColor(a.getString(R.styleable.SplitEditText_defaultColor)));
            setSelectedColor(Color.parseColor(a.getString(R.styleable.SplitEditText_selectedColor)));
            setFilledColor(Color.parseColor(a.getString(R.styleable.SplitEditText_filledColor)));
            setCodeColor(Color.parseColor(a.getString(R.styleable.SplitEditText_codeColor)));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }finally{
            a.recycle();
        }

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // When tapped, move cursor to end of text.
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
    }

    public void setMask(String mask){
        mMask = mask;
    }
    public void setMaskCode(boolean maskCode){
        mMaskCode = maskCode;
    }
    public void setGroup(int group){
        mGroup = group;
    }
    public void setSpace(float space){
        mSpace = space;
    }
    public void setFieldShape(int shape){
        mFieldShape = shape;
    }
    public void setCodeColor(int color){
        mCodeColor = color;
    }

    public void setDefaultColor(int color){
        mDefaultColor = color;
        resetColorStateList();
    }
    public void setSelectedColor(int color){
        mSelectedColor = color;
        resetColorStateList();
    }
    public void setFilledColor(int color){
        mFilledColor = color;
        resetColorStateList();
    }

    private void resetColorStateList(){
        //Re initialize values
        mColorsNew = new int[]{
                mDefaultColor,
                mSelectedColor,
                mFilledColor,
        };
        mColorStates = new ColorStateList(mStates, mColorsNew);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int startY = getPaddingTop();
        int endY = getHeight() - getPaddingBottom();

        //Text Width
        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(text, 0, textLength, textWidths);

        //Set the text code paint
        Paint textPaint = getPaint();
        textPaint.setColor(mCodeColor);
        //duplicate text
        Editable maskedText = Editable.Factory.getInstance().newEditable(text);
        if(mMaskCode) {
            maskedText.replace(0, textLength, new String(new char[textLength]).replace("\0", mMask));
        }

        //Actual rendering of each code view
        for (int i = 0; i < mNumChars; i++) {
            int state = android.R.attr.state_focused;//focused|default
            if(i < textLength){
                state = android.R.attr.state_checked;//checked
            }else if(i == textLength){
                state = android.R.attr.state_selected;//selected
            }

            updateColorForLines(state);
            float radius = mCharSize / 2;
            switch (mFieldShape){
                case 3://Draw Circle
                    canvas.drawCircle(startX + radius, startY + radius, radius, mLinesPaint);
                    break;
                case 2://Draw Rectangle
                    canvas.drawRect(startX, startY, startX + mCharSize, getHeight(), mLinesPaint);
                    break;
                default://Draw Line by default
                    canvas.drawLine(startX, getHeight(), startX + mCharSize, getHeight(), mLinesPaint);
                    break;
            }

            //Draw text code
            if (textLength > i) {
                float middle = startX + mCharSize / 2;

                canvas.drawText(maskedText, i, i + 1, middle - textWidths[0] / 2, endY - mLineStroke, textPaint);
            }

            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }


    private int getColorForState(int... states) {
        return mColorStates.getColorForState(states, Color.WHITE);
    }

    /**
     * @param state Is the current state of the current input box
     */
    private void updateColorForLines(int state) {
//        if (isFocused()) {
//            mLinesPaint.setStrokeWidth(mLineStrokeSelected);
//            mLinesPaint.setColor(getColorForState(state));
//        } else {
//            mLinesPaint.setStrokeWidth(mLineStroke);
//            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
//        }

        mLinesPaint.setStrokeWidth(mLineStrokeSelected);
        if(!isFocused() && state == android.R.attr.state_selected){
            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
        }else {
            mLinesPaint.setColor(getColorForState(state));
        }
    }
}