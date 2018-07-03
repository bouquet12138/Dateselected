package com.example.datechioce.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.datechioce.R;
import com.example.datechioce.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BottomPopDateSelect extends Dialog {

    private List<String> mYearList, mMonthList, mDayList;//存放年月日的集合

    private TextView mCancelText;//取消
    private TextView mCurrentDate;//显示当前年月的文本
    private TextView mConfirmText;//确定文本
    private DatePickerView mYearDatePicker;//年
    private DatePickerView mMonthDatePicker;//月
    private DatePickerView mDayDatePicker;//日选择器


    private int mStartYear;//开始的年份，结束的年份
    private Calendar mCalendar;
    private int mCurrentYear, mCurrentMonth;

    public BottomPopDateSelect(@NonNull Context context) {
        super(context, R.style.ActionSheetDialogStyle);

        setContentView(R.layout.date_selector);//设置一下内容

        initView();
        initData();

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = 0.9f;//透明度为0.9
        layoutParams.gravity = Gravity.BOTTOM;//设置居低并且宽度全屏
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(layoutParams);

        initListener();

    }

    /**
     * 初始化view
     */
    private void initView() {
        mCancelText = findViewById(R.id.cancelText);
        mCurrentDate = findViewById(R.id.currentDate);
        mConfirmText = findViewById(R.id.confirmText);
        mYearDatePicker = findViewById(R.id.yearDatePicker);
        mMonthDatePicker = findViewById(R.id.monthDatePicker);
        mDayDatePicker = findViewById(R.id.dayDatePicker);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        mCalendar = Calendar.getInstance();//得到日历示例
        mCurrentYear = mCalendar.get(Calendar.YEAR);//结束年
        mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;//当前月


        mStartYear = mCurrentYear - 140;//开始的年份 最长寿的人140
        mYearList = new ArrayList<>();//年
        mMonthList = new ArrayList<>();//月
        mDayList = new ArrayList<>();//日

        for (int i = mStartYear; i <= mCurrentYear; i++) {
            mYearList.add(i + "");
        }
        mYearDatePicker.setDateList(mYearList);

        for (int i = 1; i <= 12; i++) {
            if (i < 10)
                mMonthList.add("0" + i);
            else
                mMonthList.add(i + "");//一年12个月
        }
        mMonthDatePicker.setDateList(mMonthList);

        for (int i = 1; i <= 31; i++) {
            if (i < 10)
                mDayList.add("0" + i);
            else
                mDayList.add(i + "");
        }
        mDayDatePicker.setDateList(mDayList);//设置一下日期picker的数据

    }

    /**
     * 初始化监听
     */
    private void initListener() {

        //设置确定按钮的监听
        mConfirmText.setOnClickListener((v) -> {
                    String currentDate = mYearList.get(mYearDatePicker.getCurrentSelected()) + "-";
                    currentDate += mMonthList.get(mMonthDatePicker.getCurrentSelected()) + "-";
                    currentDate += mDayList.get(mDayDatePicker.getCurrentSelected());
                    if (mOnConfirmListener != null)//确定监听不为空
                        mOnConfirmListener.onConfirm(currentDate);//将当前日期返回出去
                }
        );

        mCancelText.setOnClickListener((view) -> {
                    if (mOnCancelListener == null) {
                        dismiss();//让弹窗消失
                    } else {
                        mOnCancelListener.onCancel();//随便拿去
                    }
                }
        );

        mYearDatePicker.setOnSelectChangeListener((currentSelectedIndex) -> {

            int year = currentSelectedIndex + mStartYear;
            int monthLength = (year == mCurrentYear) ? mCurrentMonth : 12;

            if (mMonthList.size() == monthLength) ;//相等啥也不做
            else {
                if (monthLength <= mMonthDatePicker.getCurrentSelected()) {
                    mMonthDatePicker.setCurrentSelected(monthLength - 1);//选中最后一个
                }
                changeListSize(mMonthList, monthLength);//设置一下长度
                mMonthDatePicker.setDateList(mMonthList);
            }

            int days = DateUtil.getDays(year, monthLength);//根据年月得到天数

            if (mDayList.size() == days) ;//相等啥也不做
            else {
                if (days <= mDayDatePicker.getCurrentSelected()) {
                    mDayDatePicker.setCurrentSelected(days - 1);//选最后一个
                }
                changeListSize(mDayList, days);
                mDayDatePicker.setDateList(mDayList);
            }

        });

        mMonthDatePicker.setOnSelectChangeListener((currentSelectIndex) -> {

            int year = mYearDatePicker.getCurrentSelected() + mStartYear;
            int moth = mMonthDatePicker.getCurrentSelected() + 1;

            int days = DateUtil.getDays(year, moth);//根据年月得到天数

            if (mDayList.size() == days) ;//相等啥也不做
            else {
                if (days <= mDayDatePicker.getCurrentSelected()) {
                    mDayDatePicker.setCurrentSelected(days - 1);//选最后一个
                }
                changeListSize(mDayList, days);
                mDayDatePicker.setDateList(mDayList);
            }

        });

        mYearDatePicker.setCurrentSelected((mCurrentYear - mStartYear - 20));//默认选中的年份是20岁
        mMonthDatePicker.setCurrentSelected(0);//设置一下当前选中
        mDayDatePicker.setCurrentSelected(0);
    }

    //点击确定后的监听
    public interface OnConfirmListener {
        void onConfirm(String currentDate);//当用户点击了确定之后
    }

    private OnConfirmListener mOnConfirmListener;

    /**
     * 设置确定监听
     *
     * @param onConfirmListener
     */
    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
    }

    //点击取消按钮后的监听
    public interface OnCancelListener {
        void onCancel();
    }

    public OnCancelListener mOnCancelListener;

    /**
     * 设置取消监听
     *
     * @param onCancelListener
     */
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    /**
     * 改变list集合的长度
     *
     * @param dateList
     * @param newLength
     */
    private void changeListSize(List<String> dateList, int newLength) {

        if (dateList.size() == newLength)//如果长度相等直接返回
            return;

        if (dateList.size() > newLength) {
            for (int i = dateList.size() - 1; i >= newLength; i--)//移除多余数据
                dateList.remove(i);
        } else {
            for (int i = dateList.size() + 1; i <= newLength; i++) {
                if (i < 10)
                    dateList.add("0" + i);
                else
                    dateList.add(i + "");
            }
        }

    }

}
