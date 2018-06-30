package com.example.datechioce.bean;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.example.datechioce.R;
import com.example.datechioce.util.DensityUtil;

public class DatePickerProperty {

    private float mMaxTextSize;//最大文字尺寸
    private float mMinTextSize;//最小文字尺寸
    private int mMaxTextAlpha;//文字最大透明度
    private int mMinTextAlpha;//文字最小透明度


    private int mData1Color;
    private int mData2Color;
    private boolean mCanScroll;

    public void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DatePickerView);

        mData1Color = typedArray.getColor(R.styleable.DatePickerView_data1Color, 0xffdda4e4);
        mData2Color = typedArray.getColor(R.styleable.DatePickerView_data2Color, 0xff444444);
        mCanScroll = typedArray.getBoolean(R.styleable.DatePickerView_canScroll, true);

        mMaxTextSize = DensityUtil.dipToPx(context, 40);//最大文字的尺寸
        mMinTextSize = DensityUtil.dipToPx(context, 20);//最小文字尺寸

        mMaxTextAlpha = 255;//文字的最大透明度
        mMinTextAlpha = 100;//最小透明度

        typedArray.recycle();
    }

    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
    }

    public float getMinTextSize() {
        return mMinTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
    }

    public int getMaxTextAlpha() {
        return mMaxTextAlpha;
    }

    public void setMaxTextAlpha(int maxTextAlpha) {
        mMaxTextAlpha = maxTextAlpha;
    }

    public int getMinTextAlpha() {
        return mMinTextAlpha;
    }

    public void setMinTextAlpha(int minTextAlpha) {
        mMinTextAlpha = minTextAlpha;
    }

    public int getData1Color() {
        return mData1Color;
    }

    public void setData1Color(int data1Color) {
        mData1Color = data1Color;
    }

    public int getData2Color() {
        return mData2Color;
    }

    public void setData2Color(int data2Color) {
        mData2Color = data2Color;
    }

    public boolean isCanScroll() {
        return mCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    /**
     * 得到文字尺寸的差值
     * @return
     */
    public float getTextSizeDValue(){
        return mMaxTextSize - mMinTextSize;
    }

    /**
     * 得到文字透明度的差值
     * @return
     */
    public int getTextAlphaDValue(){
        return mMaxTextAlpha - mMinTextAlpha;
    }
}
