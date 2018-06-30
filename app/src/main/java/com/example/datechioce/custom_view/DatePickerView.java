package com.example.datechioce.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.example.datechioce.bean.DatePickerProperty;
import com.example.datechioce.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class DatePickerView extends View {

    private static final int DRAW = 1;//绘制
    private static final int INERTIA_DRAW = 2;//惯性绘制

    public static final float MARGIN_ALPHA = 2.8f;//text 之间间距和minTextSize之比
    public float mMoveSpeed = 10;//自动滚回中间的速度

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DRAW:
                    if (Math.abs(mMoveLen) < mMoveSpeed) {
                        mMoveLen = 0;//归零
                    } else {
                        mMoveLen -= mMoveLen / Math.abs(mMoveLen) * mMoveSpeed;
                        mHandler.sendEmptyMessageDelayed(DRAW, 10);//向自身发消息
                    }
                    invalidate();//重绘
                    break;
                case INERTIA_DRAW:
                    move(mLastEventY);//模拟滑动
                    inertiaScroll();//继续滑
                    break;
            }
        }
    };

    private List<String> mDateList = new ArrayList<>();//存放数据的

    private float mViewHeight = 0;
    private float mViewWidth = 0;

    private Paint mPaint1, mPaint2;//用于绘制的画笔

    private int mCurrentSelected = 0;//当前选中的位置
    private float mMoveLen = 0;//滑动距离
    private float mLastDownY;//最后一次按下的Y坐标
    private float mLastEventY;//最后一次抬起的位置

    private DatePickerProperty mDatePickerPro = new DatePickerProperty();//属性有关

    private int mLastVelocity = 0;//最后的速度
    private VelocityTracker mVTracker = null;//用于监听用户滑动速度的
    private int mMinInertVelocity;

    public DatePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        mDatePickerPro.getAttrs(context, attrs);
        //第一个画笔
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setTextAlign(Paint.Align.CENTER);
        mPaint1.setColor(mDatePickerPro.getData1Color());

        //第二个画笔
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setTextAlign(Paint.Align.CENTER);
        mPaint2.setColor(mDatePickerPro.getData2Color());
        mMinInertVelocity = DensityUtil.dipToPx(getContext(), 750);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mViewHeight = h;
        mViewWidth = w;
        mDatePickerPro.setMaxTextSize(mViewHeight / 7f);//设置最大文字尺寸
        mDatePickerPro.setMinTextSize(mDatePickerPro.getMaxTextSize() / 2.2f);//最小文字尺寸
        mMoveSpeed = h / DensityUtil.dipToPx(getContext(), 16);//自动滚回中间的速度
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mDatePickerPro.isCanScroll())//是否可滚动
            return false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(DRAW);//取消这个消息
                mHandler.removeMessages(INERTIA_DRAW);//取消惯性滑动消息
                mLastDownY = event.getY();

                mLastVelocity = 0;  //速度监听
                if (mVTracker == null)
                    mVTracker = VelocityTracker.obtain();
                else
                    mVTracker.clear();
                mVTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                move(event.getY());
                mVTracker.addMovement(event);
                mVTracker.computeCurrentVelocity(1000);
                mLastVelocity = (int) mVTracker.getYVelocity();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mLastVelocity) < mMinInertVelocity) {
                    doUp();
                } else {
                    mLastEventY = (int) event.getY();
                    inertiaScroll();//去惯性滑动吧
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVTracker != null) {
                    mVTracker.recycle();
                    mVTracker = null;
                }
                break;
        }

        return true;
    }

    /**
     * 惯性滑动
     */
    private void inertiaScroll() {

        int ratio = (mLastVelocity > 0) ? 1 : -1;

        float offsetY = (float) (ratio * Math.sqrt((Math.abs(mLastVelocity) - mMinInertVelocity)) / DensityUtil.dipToPx(getContext(), 1));

        if (Math.abs(offsetY) < mMoveSpeed)
            offsetY = ratio * mMoveSpeed;

        mLastEventY += offsetY;

        mLastVelocity -= mMinInertVelocity / 6f * ratio;//速度每次都会减
        if (Math.abs(mLastVelocity) >= mMinInertVelocity) {
            mHandler.sendEmptyMessageDelayed(INERTIA_DRAW, 20);//惯性绘制 每过20毫秒发送惯性滑动的消息
        } else {
            doUp();//归下位
        }
    }

    /**
     * 根据eventY逐渐移动
     *
     * @param eventY
     */
    private void move(float eventY) {

        if (mCurrentSelected == 0) {
            if (mMoveLen >= (mViewHeight / 2) * (0.8f) && (eventY - mLastDownY) > 0) {//如果到底了并且还想往下滑

                if (Math.abs(mLastVelocity) >= mMinInertVelocity)//到大顶部速度一下变到最小 并且滑不动
                    mLastVelocity = mLastVelocity / Math.abs(mLastVelocity) * mMinInertVelocity;
                return;
            }
            if ((eventY - mLastEventY) > 0)//往下滑受到阻碍
                mMoveLen += (eventY - mLastDownY) * Math.abs(mMoveLen - mViewHeight / 2) / (mViewHeight / 2);//移动受限
            else//往上滑没影响
                mMoveLen += (eventY - mLastDownY);//移动距离

        } else if (mCurrentSelected == mDateList.size() - 1) {//最后一个
            if (-mMoveLen >= (mViewHeight / 2) * (0.8f) && (eventY - mLastDownY) < 0) {//到底顶了 并且还想往上划
                if (Math.abs(mLastVelocity) >= mMinInertVelocity)//到大顶部速度 变到最小
                    mLastVelocity = mLastVelocity / Math.abs(mLastVelocity) * mMinInertVelocity;
                return;
            }

            if ((eventY - mLastEventY) < 0)//往上滑受到阻碍
                mMoveLen += (eventY - mLastDownY) * Math.abs(mMoveLen - mViewHeight / 2) / (mViewHeight / 2);//移动受限
            else
                mMoveLen += (eventY - mLastDownY);//移动距离

        } else {
            mMoveLen += (eventY - mLastDownY);//移动距离
        }

        if (mMoveLen > MARGIN_ALPHA * mDatePickerPro.getMinTextSize() / 2) {

            if (mCurrentSelected == 0) {
                mLastDownY = eventY;
                invalidate();//重新绘制
                return;
            }

            mCurrentSelected--;//当前选中减减

            if (mOnSelectChangeListener != null)
                mOnSelectChangeListener.onSelectChange(mCurrentSelected);

            mMoveLen -= MARGIN_ALPHA * mDatePickerPro.getMinTextSize();//减去
        } else if (mMoveLen < -MARGIN_ALPHA * mDatePickerPro.getMinTextSize() / 2) {
            if (mCurrentSelected == mDateList.size() - 1) {
                mLastDownY = eventY;
                invalidate();//重绘
                return;//忘加return了
            }
            mCurrentSelected++;//当前选中加加

            if (mOnSelectChangeListener != null)
                mOnSelectChangeListener.onSelectChange(mCurrentSelected);

            mMoveLen += MARGIN_ALPHA * mDatePickerPro.getMinTextSize();
        }

        mLastDownY = eventY;
        invalidate();//重新绘制
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mViewHeight == 0 || mViewWidth == 0)//测量还没完成就先别绘制了
            return;
        drawData(canvas);
    }

    /**
     * 绘制数据
     *
     * @param canvas
     */
    private void drawData(Canvas canvas) {
        if (mDateList.isEmpty())//如果为空直接返回
            return;
        float scale = parabola(mViewHeight / 4f, mMoveLen);
        float size = mDatePickerPro.getTextSizeDValue() * scale
                + mDatePickerPro.getMinTextSize();//得到文字尺寸

        mPaint1.setTextSize(size);
        mPaint1.setAlpha((int) (mDatePickerPro.getTextAlphaDValue() * scale
                + mDatePickerPro.getMinTextAlpha()));

        float x = (float) (mViewWidth / 2.0);//绘制的x
        float y = (float) (mViewHeight / 2.0 + mMoveLen);//绘制的y

        Paint.FontMetricsInt fmi = mPaint1.getFontMetricsInt();
        float baseLine = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mDateList.get(mCurrentSelected), x, baseLine, mPaint1); //绘制一下文字

        for (int i = 1; (mCurrentSelected - i) >= 0 && i <= 5; i++) {//只绘制能显示的 能显示的差不多只有5个提升效率
            drawOtherText(canvas, i, -1);
        }

        for (int i = 1; (mCurrentSelected + i) < mDateList.size() && i <= 5; i++) { //只绘制能显示的 能显示的差不多只有5个提升效率
            drawOtherText(canvas, i, 1);
        }

    }

    /**
     * 绘制其他文字
     *
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {

        float d = MARGIN_ALPHA * mDatePickerPro.getMinTextSize() * position + type * mMoveLen;//distance间距
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mDatePickerPro.getTextSizeDValue()) * scale +
                mDatePickerPro.getMinTextSize();
        mPaint2.setTextSize(size);

        int alpha = (int) (mDatePickerPro.getTextAlphaDValue() * scale
                + mDatePickerPro.getMinTextAlpha());

        mPaint2.setAlpha(alpha);
        float y = (float) (mViewHeight / 2.0 + type * d);

        Paint.FontMetricsInt fmi = mPaint2.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mDateList.get(mCurrentSelected + type * position),
                (float) (mViewWidth / 2.0), baseline, mPaint2);

    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    /**
     * 设置数据列表
     *
     * @param dateList
     */
    public void setDateList(@Nullable List<String> dateList) {
        mDateList = dateList;
        invalidate();//重绘一下
    }

    /**
     * 设置当前选择的位子
     *
     * @param currentSelected
     */
    public void setCurrentSelected(int currentSelected) {

        if (currentSelected < 0 || currentSelected >= mDateList.size())
            return;
        mCurrentSelected = currentSelected;
        if (mOnSelectChangeListener != null)
            mOnSelectChangeListener.onSelectChange(mCurrentSelected);
        invalidate();//重绘
    }

    /**
     * 得到当前选中的索引
     *
     * @return
     */
    public int getCurrentSelected() {
        return mCurrentSelected;
    }

    /**
     * 当用户手指抬起时要做的事情
     */
    private void doUp() {
        if (Math.abs(mMoveLen) < 0.001) {
            mMoveLen = 0;
            return;
        }
        mHandler.sendEmptyMessage(DRAW);
    }

    public interface OnSelectChangeListener {
        void onSelectChange(int currentSelect);
    }

    private OnSelectChangeListener mOnSelectChangeListener;

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        mOnSelectChangeListener = onSelectChangeListener;
    }
}
