package com.macro.mall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String FORMAT_STYLE_1 = "yyyy-MM-dd HH:mm:ss";

    public static String FORMAT_STYLE_2 = "yyyy/MM/dd hh:mm";

    public static String FORMAT_STYLE_3 = "yyyy/MM/dd HH:mm";

    public static String FORMAT_EXPORT = "yyyy/MM/dd HH:mm:ss";

    public static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 将Date转换成String
     *
     * @param date
     * @return
     */
    public static String getString(Date date) {
        // 日期格式
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STYLE_1);
        return sdf.format(date);
    }

    public static String getStringByStyle3(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STYLE_3);
        return sdf.format(date);
    }

    public static String getFormatExport(Date date) {
        // 日期格式
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_EXPORT);
        return sdf.format(date);
    }
    //获取当前月
    public static String getNowYYYYMM() {
        String result = "";
        SimpleDateFormat l_sdf = new SimpleDateFormat("yyyyMM");
        result = l_sdf.format(new Date());
        return result;
    }

    //获取当前日
    public static String getNowYYYYMMDD() {
        String result = "";
        SimpleDateFormat l_sdf = new SimpleDateFormat("yyyyMMdd");
        result = l_sdf.format(new Date());
        return result;
    }

    public static String getNowYYYYMMDDHHMMSS() {
        String result = "";
        SimpleDateFormat l_sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        result = l_sdf.format(new Date());
        return result;
    }

    public static String getYYYYMMDD(Date date) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    //pmdb的history功能需要获取指定格式的时间(12小时制)
    public static String changeTo12Date(Date date) {
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STYLE_2);
        StringBuilder time=new StringBuilder();
        time.append(sdf.format(date));

        if(hour>12){
            time.append(" PM");
        }else{
            time.append(" AM");
        }
        return time.toString();
    }

    /**
     * @Author: zy
     * @Description: 前端传递yyyy-MM-dd 格式的字符串，需要转成对应Date类型
     * @Date: 2018/9/3_18:02
     **/
    public static Date changeToyyyyMMddDate(String strdate) {
        DateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
        Date mydate = null;
        if (strdate != null && !strdate.equals("") && !"null".equals(strdate)) {
            try {
                mydate = fmt2.parse(strdate);
            } catch (ParseException e) {
                logger.error(e.toString());
            }
        }
        return mydate;
    }

    public static Date changeToYYYYMMDDHHMMSSDate(String strdate) {
        DateFormat fmt2 = new SimpleDateFormat(FORMAT_STYLE_1);
        Date mydate = null;
        if (strdate != null && !strdate.equals("") && !"null".equals(strdate)) {
            try {
                mydate = fmt2.parse(strdate);
            } catch (ParseException e) {
                logger.error(e.toString());
            }
        }
        return mydate;
    }
}
