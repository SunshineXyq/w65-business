package com.bluedatax.wdpms.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xuyuanqiang on 7/9/16.
 */
public class GetTime {
    private static String CurrentTime;
    private static String dayTime;
    private static String otherDay;

    public static String getCurrentTime() {
        Date date = new Date();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制

        CurrentTime = sdformat.format(date);

//        Log.d("静态方法当前时间为", CurrentTime);

        return CurrentTime;
    }

    public static String getCurrentDay() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dayTime = simpleDateFormat.format(date);
        return dayTime;
    }

    public static String getOtherDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - 10);
        otherDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        return otherDay;
    }
}
