package com.example.datechioce.util;

import java.util.Calendar;

public class DateUtil {

    /**
     * 工具类构造器私有
     */
    private DateUtil() {
    }

    private static int mCurrentYear;//当前年
    private static int mCurrentMonth;//当前月
    private static int mCurrentDay;//当前日

    static {
        Calendar calendar = Calendar.getInstance();//得到日期实例
        mCurrentYear = calendar.get(Calendar.YEAR);//年
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1;//月
        mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);//得到日
    }


    //得到日期
    public static int getDays(int yearInt, int monthInt) {

        //如果年份等于当前年份，月份等于当前月份返回当前天数
        if (yearInt == mCurrentYear && monthInt == mCurrentMonth)
            return mCurrentDay;

        switch (monthInt) {

            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if ((yearInt % 4 == 0 && yearInt % 100 != 0) || yearInt % 400 == 0)
                    return 29;
                else return 28;
        }
        return 30;
    }


}
