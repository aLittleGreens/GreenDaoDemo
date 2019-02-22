package yu.cai.greendaodemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期和时间工具
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeUtil {

    public final static String DATE_FORMAT = "yyyy年MM月dd日    HH:mm:ss";
    public final static String DATE_FORMAT_YMDHMSA = "yyyy-MM-dd hh:mm:ss a";
    public final static String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH-mm-ss";
    public final static String DATE_FORMAT_YMDHMSMS = "yyyy-MM-dd-HH-mm-ss-SSS";
    public final static String DATE_FORMAT_YMDHMA = "yyyy-MM-dd hh:mm a";
    public final static String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public final static String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public final static String DATE_FORMAT_HMA = "hh:mm a";
    public final static String DATE_FORMAT_HM = "HH:mm";
    public final static String DATE_FORMAT_CRASH = "yyyy-MM-dd-HH-mm-ss";
    /**
     * 精确到毫秒
     */
    public static final String FORMART = DATE_FORMAT_YMDHMSMS;
    /**
     * utc 格式
     */
    public static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.000+0000'";
    /**
     * 自定义格式yyyy年MM月dd日 HH:mm
     */
    public static final String FORMAT1 = "yyyy年MM月dd日  HH:mm";

    /**
     * 将UTC时间转换为本地的时间
     *
     * @param utcTime UTC时间字符串
     * @return 结果时间格式
     */
    public static String getTimeByUtc(String utcTime, String resultFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(UTC_FORMAT);
        SimpleDateFormat dspFmt = new SimpleDateFormat(resultFormat);
        String convertTime;
        Date result_date;
        long result_time = 0;
        // 如果传入参数异常，使用本地时间
        if (null == utcTime) {
            result_time = System.currentTimeMillis();
        } else {
            // 将输入时间字串转换为UTC时间
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = sdf.parse(utcTime);
                result_time = result_date.getTime();
            } catch (Exception e) {
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                dspFmt.setTimeZone(TimeZone.getDefault());
                convertTime = dspFmt.format(result_time);
                return convertTime;
            }
        }
        // 设定时区
        dspFmt.setTimeZone(TimeZone.getDefault());
        convertTime = dspFmt.format(result_time);
        return convertTime;
    }

    /**
     * 获取utc时间
     *
     * @param timeString 本地时间字符串
     * @param timeFormat 时间格式
     * @return
     */
    public static String getUtcTime(String timeString, String timeFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        Date date;
        try {
            date = simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return (String) DateFormat.format(UTC_FORMAT, cal.getTime());
    }

    /**
     * 获取UTC时间
     *
     * @param date
     * @return
     */
    public static String getUtcTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return (String) DateFormat.format(UTC_FORMAT, cal.getTime());
    }

    /**
     * 获取UTC时间
     *
     * @param calendar
     * @return
     */
    public static String getUtcTime(Calendar calendar) {
        // 2、取得时间偏移量：
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return (String) DateFormat.format(UTC_FORMAT, calendar.getTime());
    }

    public static String getUtcTime(long systemTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(systemTime);
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return (String) DateFormat.format(UTC_FORMAT, cal.getTime());
    }

    /**
     * 获取日期时间信息
     *
     * @param format 日期时间样式 如:"kk:mm" ,yyyy年M月d日, yyyy-MM-dd-kk-mm-ss
     *               详细请参考SimpleDateFormat 年-月-日-时-分-秒 刚方法可以频繁调用,不会严重延时.
     */
    public static String getSystemDateTime(String format) {
        return (String) DateFormat.format(format, System.currentTimeMillis());
    }

    public static String getSystemDateTime(String format, long systemTime) {
        return (String) DateFormat.format(format, systemTime);
    }

    /**
     * 系统时间是否是24小时制
     */
    public static boolean get24HourMode(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    /**
     * 查看当前时间是上午还是下午
     */
    public static String[] getTimeAMorPm() {
        /** 查看当前时间是上午还是下午 */
        String[] ampm = new DateFormatSymbols().getAmPmStrings();
        /*
         * mAmString = ampm[0]; mPmString = ampm[1];
		 */
        return ampm;
    }

    static String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六",};
    static String[] weeks3 = {"日", "一", "二", "三", "四", "五", "六",};
    static String[] weeks2 = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",};

    /**
     * 通过日期获取星期
     *
     * @param date
     * @return
     */
    public static String getWeek(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Calendar mCalendar = Calendar.getInstance();
        try {
            mCalendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return weeks[0];
        }
        int number = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks[number];
    }

    public static String getWeek(Date date) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        int number = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks2[number];
    }

    public static String getWeek3(Calendar mCalendar) {
        int number = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks3[number];
    }

    public static long strToLong(String str, String format) throws ParseException {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        if (TextUtils.isEmpty(format)) {
            format = FORMART;
        }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(str);
            return date.getTime();
    }

    public static String date2Str(Calendar c, String format) {
        if (c == null) {
            return null;
        }
        return date2Str(c.getTime(), format);
    }

    public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
        if (d == null) {
            return null;
        }
        if (TextUtils.isEmpty(format)) {
            format = FORMART;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(d);
        return s;
    }

    /**
     * 将秒转换成:x小时y分钟
     *
     * @param second
     * @return
     */
    public static String getTime(int second) {
        if (second > 0) {
            int minute = second / 60;
            int hour = minute / 60;
            int minute2 = minute % 60;
            return hour + "小时" + minute2 + "分钟";
        }
        return "0小时0分钟";
    }

    /**
     * 将秒转换成:x分y秒
     *
     * @param second 秒数
     * @return 字符串
     */
    public static String getTimeMS(int second) {
        if (second > 0) {
            int minute = second / 60;
            int seconds = second % 60;
            return minute + "分" + seconds + "秒";
        }
        return "0分0秒";
    }

    public static long intervalTime(String creatTime,long lastmodifyTime){

        try {
            return lastmodifyTime - strToLong(creatTime, DATE_FORMAT_YMDHMSMS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
