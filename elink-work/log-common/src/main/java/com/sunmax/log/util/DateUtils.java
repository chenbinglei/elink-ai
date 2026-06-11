package com.sunmax.log.util;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class DateUtils {

    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式(HH:mm:ss)
     */
    public final static String TIME_PATTERN = "HH:mm:ss";

    //开始时间
    public static String min = " 00:00:00";
    //结束时间
    public static String max = " 23:59:59";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd HH:mm:ss格式日期
     */
    public static String format(Date date) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
            return df.format(date);
        }
        return null;
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param timestamp 秒
     * @return 返回yyyy-MM-dd HH:mm:ss格式日期
     */
    public static String format(Long timestamp) {
        if (timestamp != null) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
            return df.format(new Date(timestamp * 1000L));
        }
        return null;
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param timestamp  秒
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Long timestamp, String pattern) {
        if (timestamp != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(new Date(timestamp * 1000L));
        }
        return null;
    }

    /**
     * 时间转换 将不为整5分钟的时间转化为5的倍数  比如 14:21转化为 14:20 便于数据查询
     *
     * @param dateTime
     * @return
     */
    public static String getDate(Date dateTime) {
        String datetime = new SimpleDateFormat("yyyyMMdd HHmm").format(dateTime);
        String date = datetime.substring(0, 8);
        String time = datetime.substring(9, 13);
        int timestamp = Integer.parseInt(time.substring(2, 4)) % 5;
        if (timestamp != 0) {
            if (Integer.parseInt(time.substring(2, 4)) < 5) {
                time = time.substring(0, 2) + "00";
            } else if (Integer.parseInt(time.substring(2, 4)) < 10 && Integer.parseInt(time.substring(2, 4)) >= 5) {
                time = time.substring(0, 2) + "05";
            } else {
                timestamp = Integer.parseInt(time.substring(2, 4)) - timestamp;
                time = time.substring(0, 2) + timestamp;
            }
        }
        time = time + "00";
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " " + time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
    }


    /**
     * 时间转换 将不为整5分钟的时间转化为5的倍数  比如 14:21转化为 14:20 便于数据查询
     *
     * @param dateTime
     * @return
     */
    public static String getLocalDate(LocalDateTime dateTime) {
        String datetime = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm"));
        String date = datetime.substring(0, 8);
        String time = datetime.substring(9, 13);
        int timestamp = Integer.parseInt(time.substring(2, 4)) % 5;
        if (timestamp != 0) {
            if (Integer.parseInt(time.substring(2, 4)) < 5) {
                time = time.substring(0, 2) + "00";
            } else if (Integer.parseInt(time.substring(2, 4)) < 10 && Integer.parseInt(time.substring(2, 4)) >= 5) {
                time = time.substring(0, 2) + "05";
            } else {
                timestamp = Integer.parseInt(time.substring(2, 4)) - timestamp;
                time = time.substring(0, 2) + timestamp;
            }
        }
        time = time + "00";
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " " + time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
    }

    public static Date strToDate(String dateTime) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        try {
            return dateFormat.parse(dateTime);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static LocalDateTime getLocalDateTime(LocalDateTime dateTime) {
        String localDate = getLocalDate(dateTime);
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return LocalDateTime.parse(localDate, df);
    }

    public static LocalDate strToLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static String localDateToStr(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime strToLocalDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static String localDateTimeToStr(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static LocalTime strToLocalTime(String dateStr) {
        return LocalTime.parse(dateStr, DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    public static String localTimeToStr(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    /**
     * 两个时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static Double twoTimeHourDiff(LocalDateTime startTime, LocalDateTime endTime) {
        return BigDecimal.valueOf((double) Duration.between(startTime, endTime).toMinutes() / 60).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    /**
     * 生成指定范围，指定小数位数的随机数
     *
     * @param max   最大值
     * @param min   最小值
     * @param scale 小数位数
     * @return
     */
    public static Double makeRandom(float max, float min, int scale) {
        BigDecimal cha = new BigDecimal(Math.random() * (max - min) + min);
        return cha.setScale(scale, RoundingMode.HALF_UP).doubleValue();//保留 scale 位小数，并四舍五入
    }


    /**
     * 获取几分钟的整点时间
     *
     * @param interval 时间间隔
     * @return Novalidate
     */
    public static LocalDateTime getToIntegralPoint(LocalDateTime localDateTime, Integer interval) {

        int minute = localDateTime.getMinute();
        long time = minute % interval;
        if (time != 0) {
            localDateTime = localDateTime.minusMinutes(time);
        }

        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.of(localDateTime.getHour(), localDateTime.getMinute()));
    }

    public static LocalDateTime getToSecondPoint(LocalDateTime localDateTime, Integer interval) {

        int second = localDateTime.getSecond();
        long time = second % interval;
        if (time != 0) {
            localDateTime = localDateTime.minusSeconds(time);
        }

        return localDateTime;
    }

    /**
     * 获取当前六个小时之内5分钟的整点数据
     */
    public static List<String> getDate6Hour(String dateTime) {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(dateTime);
            for (int i = 0; i < 72; i++) {
                /*if (i == 0) {
                    dateList.add(dateTime);
                }*/
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MINUTE, 5);
                date = cal.getTime();
                String s1 = format.format(date);
                dateList.add(s1);
            }
        } catch (ParseException e) {
            e.getStackTrace();
        }
        return dateList;
    }

    /**
     * 获取开始时间，结束时间之间的整点数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔 分钟
     */
    public static List<String> getDateBetweenMinutes(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        LocalDateTime localEnd = getLocalDateTime(endTime);

        List<String> list = new ArrayList<>();
        for (LocalDateTime dateTime = startTime; dateTime.isBefore(localEnd.plusMinutes(interval)); dateTime = dateTime.plusMinutes(interval)) {
            list.add(DateUtils.localDateTimeToStr(dateTime));
        }
        return list.stream().filter(d -> !strToLocalDateTime(d).isAfter(endTime)).collect(Collectors.toList());
    }

    /**
     * 获取开始时间，结束时间之间的整点数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔 月
     */
    public static List<String> getDateBetweenMonth(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        LocalDateTime localEnd = getLocalDateTime(endTime);

        List<String> list = new ArrayList<>();
        for (LocalDateTime dateTime = startTime; dateTime.isBefore(localEnd.plusMonths(interval)); dateTime = dateTime.plusMonths(interval)) {
            list.add(DateUtils.localDateTimeToStr(dateTime));
        }
        return list.stream().filter(d -> !strToLocalDateTime(d).isAfter(endTime)).collect(Collectors.toList());
    }

    /**
     * 获取开始时间，结束时间之间的整点数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔
     */
    public static List<String> getDateBetweenSeconds(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        LocalDateTime localEnd = getLocalDateTime(endTime);

        List<String> list = new ArrayList<>();
        for (LocalDateTime dateTime = startTime; dateTime.isBefore(localEnd.plusSeconds(interval)); dateTime = dateTime.plusSeconds(interval)) {
            list.add(DateUtils.localDateTimeToStr(dateTime));
        }
        return list.stream().filter(d -> !strToLocalDateTime(d).isAfter(endTime)).collect(Collectors.toList());
    }

    /**
     * 获取开始时间，结束时间之间的5分整点数据
     */
    public static List<String> getDateBetweenMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime localEnd = getLocalDateTime(endTime);

        List<String> list = new ArrayList<>();
        for (LocalDateTime dateTime = startTime; dateTime.isBefore(localEnd.plusMinutes(5L)); dateTime = dateTime.plusMinutes(5L)) {
            list.add(DateUtils.localDateTimeToStr(dateTime));
        }
        return list.stream().filter(d -> !strToLocalDateTime(d).isAfter(endTime)).collect(Collectors.toList());
    }

    /**
     * 获取开始时间，结束时间之间的15分整点数据(注意：取一整天的话最后一位会是第二天0点的时间)
     */
    public static List<String> getDateBetweenFifteenMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        List<String> list = new ArrayList<>();
        for (LocalDateTime dateTime = startTime; dateTime.isBefore(endTime.plusMinutes(15L)); dateTime = dateTime.plusMinutes(15L)) {
            list.add(DateUtils.localDateTimeToStr(dateTime));
        }
        return list.stream().filter(d -> !strToLocalDateTime(d).isAfter(endTime)).collect(Collectors.toList());
    }

    /**
     * 获取过去一周的每天日期(年月日)
     */
    public static List<String> getWeekPast(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.minusDays(6L); date.isBefore(localDate.plusDays(1L)); date = date.plusDays(1L)) {
            resultList.add(DateTimeFormatter.ofPattern(DATE_PATTERN).format(date));
        }
        return resultList;
    }

    /**
     * 获取过去一个月的每天日期(年月日)
     */
    public static List<String> getMonthPast(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.minusMonths(1L).plusDays(1L); date.isBefore(localDate.plusDays(1L)); date = date.plusDays(1L)) {
            resultList.add(DateTimeFormatter.ofPattern(DATE_PATTERN).format(date));
        }
        return resultList;
    }

    /**
     * 获取过去一年的每月日期(年月)
     */
    public static List<String> getYearPast(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.minusYears(1L).plusMonths(1L); date.isBefore(localDate.plusMonths(1L)); date = date.plusMonths(1L)) {
            resultList.add(DateTimeFormatter.ofPattern("yyyy-MM").format(date));
        }
        return resultList;
    }

    /**
     * 获取本周的每天日期(年月日)
     */
    public static List<String> getWeekThis(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        LocalDate startDate = localDate.with(DayOfWeek.MONDAY);
        for (LocalDate date = startDate; date.isBefore(startDate.plusDays(7L)); date = date.plusDays(1L)) {
            resultList.add(DateTimeFormatter.ofPattern(DATE_PATTERN).format(date));
        }
        return resultList;
    }

    /**
     * 获取本月的每天日期(年月日)
     */
    public static List<String> getMonthThis(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        LocalDate startDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        for (LocalDate date = startDate; (date.isBefore(endDate) || date.equals(endDate)); date = date.plusDays(1L)) {
            resultList.add(DateTimeFormatter.ofPattern(DATE_PATTERN).format(date));
        }
        return resultList;
    }

    /**
     * 获取本年的每月日期(年月)
     */
    public static List<String> getYearThis(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        LocalDate startDate = localDate.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endDate = localDate.with(TemporalAdjusters.lastDayOfYear());
        for (LocalDate date = startDate; (date.isBefore(endDate) || date.equals(endDate)); date = date.plusMonths(1L)) {
            resultList.add(DateTimeFormatter.ofPattern("yyyy-MM").format(date));
        }
        return resultList;
    }

    /**
     * 获取前几个月的每月日期(年月)
     */
    public static List<String> getMonthBySeveral(LocalDate localDate, Integer several) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.minusMonths(several); date.isBefore(localDate.plusMonths(1L)); date = date.plusMonths(1L)) {
            resultList.add(DateTimeFormatter.ofPattern("yyyy-MM").format(date));
        }
        return resultList;
    }

    /**
     * 获取当月的每天日期(年月日)
     *
     * @param localDate 查询时间 年月日
     */
    public static List<String> getMonthCurrent(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().toLocalDate();
             date.isBefore(localDate.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().toLocalDate().plusDays(1L)); date = date.plusDays(1L)) {
            resultList.add(DateTimeFormatter.ofPattern(DATE_PATTERN).format(date));
        }
        return resultList;
    }

    /**
     * 获取当年的每月日期(年月)
     *
     * @param localDate 查询时间 年月日
     */
    public static List<String> getYearCurrent(LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        for (LocalDate date = localDate.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay().toLocalDate();
             (date.isBefore(localDate.with(TemporalAdjusters.lastDayOfYear()).atStartOfDay().toLocalDate().plusDays(1L)));
             date = date.plusMonths(1L)) {
            resultList.add(DateTimeFormatter.ofPattern("yyyy-MM").format(date));
        }
        return resultList;
    }

    /**
     * 比较两个时间之间的相差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差时间 毫秒
     */
    public static Long compareDiffBetweenMillis(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).toMillis();
    }

    /**
     * 比较两个时间之间的相差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差时间 秒
     */
    public static Long compareDiffBetweenSecond(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).getSeconds();
    }

    /**
     * 比较两个时间之间的相差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差时间 分钟
     */
    public static Long compareDiffBetweenMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * 比较两个时间之间的相差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差时间 小时
     */
    public static Long compareDiffBetweenHours(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).toHours();
    }

    /**
     * 获取相差分钟的展示数据
     */
    public static String getValueByMinutes(Long minutes) {
        if (minutes <= 0) {
            return "-1";
        } else if (minutes < 60) {
            return "最近使用" + minutes + "分钟前";
        } else if (minutes < 120) {
            return "最近使用1小时前";
        } else if (minutes < 180) {
            return "最近使用2小时前";
        } else if (minutes < 240) {
            return "最近使用3小时前";
        } else if (minutes < 300) {
            return "最近使用4小时前";
        } else if (minutes < 360) {
            return "最近使用5小时前";
        } else if (minutes < 420) {
            return "最近使用6小时前";
        } else if (minutes < 480) {
            return "最近使用7小时前";
        } else if (minutes < 540) {
            return "最近使用8小时前";
        } else if (minutes < 600) {
            return "最近使用9小时前";
        } else if (minutes < 660) {
            return "最近使用10小时前";
        } else if (minutes < 720) {
            return "最近使用11小时前";
        } else if (minutes < 780) {
            return "最近使用12小时前";
        } else {
            return "-1";
        }
    }

    /**
     * 把秒转换成时分秒
     *
     * @param time 秒值
     * @return
     */
    public static String secToTime(long time) {
        String timeStr;
        long second;
        if (time <= 0) {
            return "00:00:00";
        }
        long minute = time / 60;
        if (minute < 60) {
            second = time % 60;
            timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
        } else {
            long hour = minute / 60;
//                if (hour > 99) return "59:59:59";
            minute = minute % 60;
            second = time - hour * 3600 - minute * 60;
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        }
        return timeStr;
    }

    private static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 获取每天整点的时间
     *
     * @return 整点时间
     */
    public static List<String> getHourDayTime() {
        List<String> resultList = new ArrayList<>();
        //格式化时间
        DateTimeFormatter mh = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 1; i <= 24; i++) {
            resultList.add(mh.format(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusHours(i)));
        }
        return resultList;
    }

    /**
     * 获取指定日期一天整点的时间
     *
     * @return 整点时间
     */
    public static List<String> getHourDayTime(String date) {
        List<String> resultList = new ArrayList<>();
        //格式化时间
        DateTimeFormatter mh = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < 24; i++) {
            resultList.add(mh.format(LocalDateTime.of(strToLocalDate(date), LocalTime.MIN).plusHours(i)));
        }
        return resultList;
    }

    /**
     * 获取每天半个小时的整点时间
     *
     * @return 整点时间
     */
    public static List<String> getMinuteTime30() {
        List<String> resultList = new ArrayList<>();
        //格式化时间
        DateTimeFormatter mh = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (int i = 0; i < 48; i++) {
            resultList.add(mh.format(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusMinutes(i * 30)));
        }
        return resultList;
    }

    /**
     * 获取每天半个小时的整点时间
     *
     * @return 整点时间
     */
    public static List<String> getMinuteTime30Format() {
        List<String> resultList = new ArrayList<>();
        //格式化时间
        DateTimeFormatter mh = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < 48; i++) {
            resultList.add(mh.format(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusMinutes(i * 30)));
        }
        return resultList;
    }

    /**
     * 判断一个日期是否是当天
     *
     * @param date 日期
     * @return
     */
    public static boolean isDay(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = format.parse(date);
        Date nowdate = new Date();
        Date d2 = format.parse(format.format(nowdate));
        long day = (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);
        return day == 0;
    }

    /**
     * 判断选择的日期是否是本月
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static boolean isThisMonth(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return isThisTime(format.parse(time), "yyyy-MM");
    }

    /**
     * 判断选择的日期是否是本年
     *
     * @param time
     * @return
     */
    public static boolean isThisYear(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return isThisTime(format.parse(time), "yyyy");
    }

    private static boolean isThisTime(Date time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(time);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }

    //判断选择的日期是否是本月
    public static boolean isMonth(String dateStr) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sf.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return isThisTimes(date.getTime(), "yyyy-MM");
    }

    public static boolean isThisTimes(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }

    /**
     * 获取昨天的时间
     *
     * @param date
     * @return
     */
    public static String getLastDayTime(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.add(Calendar.DATE, -1);
        String date2 = format.format(calendar.getTime());
        return date2;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        String nowFormat = now.format(dateTimeFormatter);
        return nowFormat;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String nowFormat = now.format(dateTimeFormatter);
        return nowFormat;
    }

    /**
     * 获取当天的00:00:00
     *
     * @return
     */
    public static String getDayStart(String date) {
        return date + " 00:00:00";
    }


    /**
     * 获取当天的23:59:59
     *
     * @return
     */
    public static String getDayEnd(String date) {
        return date + " 23:59:59";
    }

    /**
     * 获取指定周的开始时间
     *
     * @return
     */
    public static String getStartTimeOfCurrentWeek(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf1.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(myDate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setMinTimeOfDay(calendar);
        String format = sdf1.format(calendar.getTime());
        return format;
    }

    /**
     * 获取指定周的结束时间
     *
     * @return
     */
    public static String getEndTimeOfCurrentWeek(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf1.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(myDate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setMaxTimeOfDay(calendar);
        String format = sdf1.format(calendar.getTime());
        return format;
    }

    /**
     * 设置当天的开始时间
     *
     * @param calendar
     */
    private static void setMinTimeOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 设置当天的结束时间
     *
     * @param calendar
     */
    private static void setMaxTimeOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    /**
     * 获取指定月的开始时间
     *
     * @return
     */
    public static String getStartTimeOfCurrentMonth(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf1.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(myDate);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
//        setMinTimeOfDay(calendar);
        Date time = calendar.getTime();
        String strDate1 = sdf1.format(time);
        return strDate1;
    }

    /**
     * 获取指定月的结束时间
     *
     * @return
     */
    public static String getEndTimeOfCurrentMonth(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf1.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(myDate);
        int maxMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), maxMonthDay);
//        setMaxTimeOfDay(calendar);
        Date time = calendar.getTime();
        String strDate1 = sdf1.format(time);
        return strDate1;
    }

    /**
     * 获取上个月的开始时间
     *
     * @param date
     * @return
     */
    public static String getLastMonthStartTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String format1 = format.format(calendar.getTime());
        return format1;
    }

    /**
     * 获取上个月的结束时间
     *
     * @param date
     * @return
     */
    public static String getLastMonthEndTime(String date) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        int lastMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay);
        //按格式输出
        String gtime = sdf.format(c.getTime());
        return gtime;
    }


    /**
     * 获取上年的开始时间
     *
     * @param date
     * @return
     */
    public static String getLastYearStartTime(String date) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
        Date myDate = null;
        try {
            myDate = sdf.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        String gtime2 = sdf.format(myDate);
        return gtime2;
    }

    /**
     * 获取上年的结束时间
     *
     * @param date
     * @return
     */
    public static String getLastYearEndTime(String date) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        int lastMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay);
        //按格式输出
        String gtime = sdf.format(c.getTime());
        return gtime;
    }

    /**
     * 获取指定时间当年的开始时间
     *
     * @param date
     * @return
     */
    public static String getYearStartTime(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        cal.setTime(myDate);
        //设置月和日都为1，即为开始时间（注：月份是从0开始;日中0表示上个月最后一天，1表示本月开始第一天）
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        //将小时置为0
        cal.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟置为0
        cal.set(Calendar.MINUTE, 0);
        //将秒置为0
        cal.set(Calendar.SECOND, 0);
        //将毫秒置为0
        cal.set(Calendar.MILLISECOND, 0);
        String gtime = sdf.format(cal.getTime());
        return gtime;
    }


    /**
     * 获取指定时间当年的结束时间
     *
     * @param date
     * @return
     */
    public static String getYearEndTime(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        cal.setTime(myDate);
        //设置月为12，月份从0开始
        cal.set(Calendar.MONTH, 11);
        //设置为当月最后一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时置为23
        cal.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟置为59
        cal.set(Calendar.MINUTE, 59);
        //将秒置为59
        cal.set(Calendar.SECOND, 59);
        //将毫秒置为999
        cal.set(Calendar.MILLISECOND, 999);
        String gtime = sdf.format(cal.getTime());
        return gtime;
    }

    /**
     * 获取指定时间15分钟之前的时间
     *
     * @param date
     * @return
     */
    public static String getBeforeFifteenMinutes(String date) {
        Calendar beforeTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date myDate = null;
        try {
            myDate = sdf.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        beforeTime.setTime(myDate);
        beforeTime.add(Calendar.MINUTE, -15);// 5分钟之前的时间
        Date beforeD = beforeTime.getTime();
        String before15 = sdf.format(beforeD);
        return before15;
    }

    /**
     * 获取某天每小时的时间
     *
     * @param dateStr
     * @return
     */
    public static List<String> getDayPerHourgeDate(String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date myDate = null;
//        if (isDay(dateStr)) {
//            String currentTime = getCurrentTime();
//            dateStr = currentTime;
//        } else {
//        }
        dateStr = dateStr + " 00:00:00";
        try {
            myDate = df.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        List<String> dates = Lists.newArrayList();
        dates.add(dateStr);
        for (int i = 0; i < 24; i++) {
            if (i != 23) {
                Calendar cal = Calendar.getInstance();
                assert myDate != null;
                cal.setTime(myDate);
                cal.add(Calendar.HOUR, 1);
                myDate = cal.getTime();
                String s1 = format.format(myDate);
                dates.add(s1);
            }
        }
        return dates;
    }

    /**
     * 获取当前时间 零点零时零分零秒
     */
    public static long getZeroTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toEpochSecond(ZoneOffset.of("+8")) + 8 * 3600; //转换成秒 加8小时
    }

    /**
     * 根据指定时间获取近一年开始时间
     *
     * @param date
     * @return
     */
    public static String getNearlyYearStartTime(String date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date myDate = null;
        try {
            myDate = df.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        calendar.setTime(myDate);
        calendar.set(Calendar.HOUR_OF_DAY, -8760);
        calendar.add(Calendar.MONTH, +1);
        String format = df.format(calendar.getTime());
        return format;
    }

    /**
     * 获取指定时间近一年每月的月份
     *
     * @param date
     * @return
     */
    public static List<String> getNearlyYearPerMonthDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //建一个容器
        List<String> months = new ArrayList<>();
        //获取日历对象
        Calendar calendar = Calendar.getInstance();
        Date myDate = null;
        try {
            myDate = df.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        calendar.setTime(myDate);
        //-11是因为也要算上本月
        calendar.add(Calendar.MONTH, -11);
        //循环12次获取12个月份
        for (int i = 0; i < 12; i++) {
            //日历对象转为Date对象
            Date dateTime = calendar.getTime();
            //将date转为字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String dateStr = sdf.format(dateTime);
            //向list集合中添加
            months.add(dateStr);
            //每次月份+1
            calendar.add(Calendar.MONTH, 1);
        }
        return months;
    }

    /**
     * 获取两个日期之间的所有日期
     *
     * @param dateType  日期类型 1-日 2-月 3-年
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public static List<String> getDateBetween(Integer dateType, String startDate, String endDate) {
        List<String> resultList = Lists.newArrayList();
        LocalDate endLocalDate = DateUtils.strToLocalDate(endDate);
        switch (dateType) {
            case 1: //日
                resultList.add(startDate);
                for (int i = 0; i < ChronoUnit.DAYS.between(DateUtils.strToLocalDate(startDate), endLocalDate); i++) {
                    resultList.add(DateUtils.localDateToStr(DateUtils.strToLocalDate(startDate).plusDays(i + 1)));
                }
                break;
            case 2: //月
                LocalDate monthStartLocalDate = DateUtils.strToLocalDate(startDate).with(TemporalAdjusters.firstDayOfMonth());
                resultList.add(monthStartLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                for (int i = 0; i < ChronoUnit.MONTHS.between(monthStartLocalDate, endLocalDate); i++) {
                    resultList.add(monthStartLocalDate.plusMonths(i + 1).format(DateTimeFormatter.ofPattern("yyyy-MM")));
                }
                break;
            case 3: //年
                LocalDate dayStartLocalDate = DateUtils.strToLocalDate(startDate).with(TemporalAdjusters.firstDayOfYear());
                resultList.add(dayStartLocalDate.format(DateTimeFormatter.ofPattern("yyyy")));
                for (int i = 0; i < ChronoUnit.YEARS.between(dayStartLocalDate, endLocalDate); i++) {
                    resultList.add(dayStartLocalDate.plusYears(i + 1).format(DateTimeFormatter.ofPattern("yyyy")));
                }
                break;
        }
        return resultList;
    }

    /**
     * 查询两个时间间隔了几小时几分钟
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getTimeInterval(String startTime, String endTime) {
        LocalDateTime startLocalDateTime = strToLocalDateTime(startTime);
        LocalDateTime endLocalDateTime = strToLocalDateTime(endTime);
        LocalDateTime tempDateTime = LocalDateTime.from(startLocalDateTime);

        long days = tempDateTime.until(endLocalDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);


        long hours = tempDateTime.until(endLocalDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(endLocalDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(endLocalDateTime, ChronoUnit.SECONDS);
        String interval = hours + "时" + minutes + "分" + seconds + "秒";
        return interval;
    }

    /**
     * 获取时间区间的所有整点时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 整点时间集合
     */
    public static List<String> getHourList(String startTime, String endTime) {
        List<String> hourList = new ArrayList<>();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH");
        SimpleDateFormat format3 = new SimpleDateFormat("HH:00");
        try {
            Date startTime1 = format2.parse(startTime);
            Date endTime1 = format2.parse(endTime);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(startTime1);
            while (endTime1.getTime() >= startTime1.getTime()) {
                hourList.add(format3.format(startTime1));
                //有多种模式可以增加或减去相应的时间
                tempStart.add(Calendar.HOUR_OF_DAY, 1);
                startTime1 = tempStart.getTime();
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return hourList;
    }

    /**
     * 获取时间区间的所有整点时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 整点时间集合
     */
    public static List<String> getHourTimeList(String startTime, String endTime) {
        List<String> hourList = new ArrayList<>();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date startTime1 = format2.parse(startTime);
            Date endTime1 = format2.parse(endTime);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(startTime1);
            while (endTime1.getTime() >= startTime1.getTime()) {
                hourList.add(format2.format(startTime1));
                //有多种模式可以增加或减去相应的时间
                tempStart.add(Calendar.HOUR_OF_DAY, 1);
                startTime1 = tempStart.getTime();
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return hourList;
    }

    /**
     * 判断当前时间是否在两个时分区间
     *
     * @param nowTimeStr   当前时间
     * @param startTimeStr 开始时间
     * @param endTimeStr   结束时间
     * @return
     */
    public static boolean isEffectiveTime(String nowTimeStr, String startTimeStr, String endTimeStr) {
        Date nowTime = null;
        Date startTime = null;
        Date endTime = null;
        try {
            nowTime = new SimpleDateFormat("HH:mm").parse(nowTimeStr);
            startTime = new SimpleDateFormat("HH:mm").parse(startTimeStr);
            endTime = new SimpleDateFormat("HH:mm").parse(endTimeStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 判断当前时分是否在两个时间区间
     *
     * @param nowTimeStr   当前时间
     * @param startTimeStr 开始时间
     * @param endTimeStr   结束时间
     * @return
     */
    public static boolean isEffectiveDate(String nowTimeStr, String startTimeStr, String endTimeStr) {
        Date nowTime = null;
        Date startTime = null;
        Date endTime = null;
        try {
            nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nowTimeStr);
            startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeStr);
            endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 计算两个时间间隔了几年
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getYearDifference(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        return period.getYears() + period.getMonths() / 12;
    }

    /**
     * 计算两个时间间隔了几个月
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getMonthDifference(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        return period.getYears() * 12 + period.getMonths() + 1;
    }

    /**
     * 计算两个时间中间隔了多少周
     *
     * @param time
     * @param timeOne
     * @return
     */
    public static double getDateSubWeeks(String time, String timeOne) {
        //计算相差天数，并保留两位小数
        double minutes = getDateSubDays(time, timeOne);
        BigDecimal b = new BigDecimal(minutes / 7.0D);
        return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算两个时间中间隔了多少天
     *
     * @param time
     * @param timeOne
     * @return
     */
    public static double getDateSubDays(String time, String timeOne) {
        //计算相差小时数，并保留两位小数
        double minutes = getDateSubHours(time, timeOne);
        BigDecimal b = new BigDecimal(minutes / 24.0D);
        return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算两个时间中间隔了多少小时
     *
     * @param time
     * @param timeOne
     * @return
     */
    public static double getDateSubHours(String time, String timeOne) {
        //计算相差小时数，并保留两位小数
        double minutes = getDateSubMinutes(time, timeOne);
        BigDecimal b = new BigDecimal(minutes / 60.0D);
        return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算两个时间中间隔了多少分钟
     *
     * @param time
     * @param timeOne
     * @return
     */
    public static double getDateSubMinutes(String time, String timeOne) {
        //计算相差分钟数，并保留两位小数
        long seconds = getDateSubSeconds(time, timeOne);
        BigDecimal b = new BigDecimal(seconds / 60.0D);
        return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算两个时间中间隔了多少秒
     *
     * @param time
     * @param timeOne
     * @return
     */
    public static long getDateSubSeconds(String time, String timeOne) {
        //计算秒数
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime localDateTimeOne = LocalDateTime.parse(timeOne, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return Duration.between(localDateTime, localDateTimeOne).toMillis() / 1000;
    }


    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取指定月份的每周的开始日期和结束日期
     *
     * @param date
     * @return
     */
    public static List<Map> getWeekDateList(String date) {
        String[] timeStrs = date.split("-");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(timeStrs[0]));
        c.set(Calendar.MONTH, Integer.parseInt(timeStrs[1]) - 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int weeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);

        LocalDate localDateate = LocalDate.parse(date, dateTimeFormatter);
        //月份第一周的起始时间和结束时间
        LocalDate firstDay = localDateate.with(TemporalAdjusters.firstDayOfMonth());
        String firstDayStr = firstDay.format(dateTimeFormatter);
        String sunStr = getSunOfWeek(firstDayStr);

        List<Map> weekInfos = new ArrayList<>();
        for (int i = 1; i <= weeks; i++) {
            Map<String, Comparable> weekInfo = new HashMap();
            //第一周的起始时间就是当月的1号，结束时间就是周日
            if (i == 1) {
                weekInfo.put("start", firstDayStr);
                weekInfo.put("end", sunStr);
                weekInfo.put("order", i);
                //计算接下来每周的周一和周日
            } else if (i < weeks) {
                //由于sunStr是上一周的周日，所以取周一要取sunStr的下一周的周一
                String monDay = getLastMonOfWeek(sunStr);
                sunStr = getSunOfWeek(monDay);
                weekInfo.put("start", monDay);
                weekInfo.put("end", sunStr);
                weekInfo.put("order", i);
                //由于最后一周可能结束时间不是周日，所以要单独处理
            } else {
                String monDay = getLastMonOfWeek(sunStr);
                //结束时间肯定就是当前月的最后一天
                LocalDate lastDay = localDateate.with(TemporalAdjusters.lastDayOfMonth());
                String endDay = lastDay.format(dateTimeFormatter);
                weekInfo.put("start", monDay);
                weekInfo.put("end", endDay);
                weekInfo.put("order", i);
            }

            weekInfos.add(weekInfo);

        }
        return weekInfos;
    }


    //算出所在周的周日
    public static String getSunOfWeek(String time) {
        LocalDate localDateate = LocalDate.parse(time, dateTimeFormatter);
        LocalDate endday = localDateate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        String endDayStr = endday.format(dateTimeFormatter);
        return endDayStr;
    }

    //下一周的周一
    public static String getLastMonOfWeek(String time) {
        LocalDate localDateate = LocalDate.parse(time, dateTimeFormatter);
        LocalDate endday = localDateate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        String endDayStr = endday.format(dateTimeFormatter);
        return endDayStr;
    }

    /**
     * 每年季度开始结束时间列表(按照严格划分春夏秋冬)
     *
     * @param date
     * @return
     */
    public static List<Map> yearQuarterList(String date) {
        List<Map> result = new ArrayList<>();
        String year = date.substring(0, 4);
        Map hashMap1 = Maps.newHashMap();
        hashMap1.put("start", year + "-03-01 00:00:00");
        hashMap1.put("end", year + "-05-31 23:59:59");
        hashMap1.put("order", 1);
        Map hashMap2 = Maps.newHashMap();
        hashMap2.put("start", year + "-06-01 00:00:00");
        hashMap2.put("end", year + "-08-31 23:59:59");
        hashMap2.put("order", 2);
        Map hashMap3 = Maps.newHashMap();
        hashMap3.put("start", year + "-09-01 00:00:00");
        hashMap3.put("end", year + "-11-30 23:59:59");
        hashMap3.put("order", 3);
        Map hashMap4 = Maps.newHashMap();
        hashMap4.put("start", year + "-12-01 00:00:00");
        hashMap4.put("end", year + "-02-28 23:59:59");
        hashMap4.put("order", 4);
        result.add(hashMap1);
        result.add(hashMap2);
        result.add(hashMap3);
        result.add(hashMap4);
        return result;
    }

    /**
     * 把指定日期减去指定秒
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，秒
     * @return 开始时间-时间段
     * @throws ParseException
     */
    public static String getBeforeSecondDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, -timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期减去指定分钟
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，分钟
     * @return 开始时间-时间段
     * @throws ParseException
     */
    public static String getBeforeDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期加上指定分钟
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，分钟
     * @return 开始时间+时间段
     * @throws ParseException
     */
    public static String getAfterDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, +timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期加上指定小时
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，小时
     * @return 开始时间+时间段
     * @throws ParseException
     */
    public static String getHourAfterDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, +timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期减去指定日
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，日
     * @return 开始时间-时间段
     * @throws ParseException
     */
    public static String getBeforeDayDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期加上指定日
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，日
     * @return 开始时间+时间段
     * @throws ParseException
     */
    public static String getAfterDayDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, +timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期加上指定分钟
     *
     * @param startTime 开始时间
     * @param timeSolt  时间段，分钟
     * @return 开始时间+时间段
     * @throws ParseException
     */
    public static String getAfterTime(String startTime, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startTime) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, +timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /**
     * 把指定日期减去指定月
     *
     * @param startDate 开始时间
     * @param timeSolt  时间段，月
     * @return 开始时间-时间段
     * @throws ParseException
     */
    public static String getBeforeMonthDate(String startDate, Integer timeSolt) {
        if (org.apache.commons.lang.StringUtils.isEmpty(startDate) || null == timeSolt) {
            return null;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -timeSolt);
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    /***
     * 获取该时间上一个间隔后的整点时间 00 05 10 ..50 55
     * @param time 时间
     * @param interval 时间间隔
     **/
    public static String getLastIntervalTime(LocalDateTime time, Integer interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDateTimeToDate(time));
        calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE) % interval);
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nowTime = calendar.getTimeInMillis();
        return simpleDateFormat.format(nowTime);
    }

    /***
     * 获取该时间下一个间隔后的整点时间 00 05 10 ..50 55
     * @param time 时间
     * @param interval 时间间隔
     **/
    public static String getNextIntervalTime(LocalDateTime time, Integer interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDateTimeToDate(time));
        calendar.add(Calendar.MINUTE, (interval - calendar.get(Calendar.MINUTE) % interval));
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nowTime = calendar.getTimeInMillis();
        return simpleDateFormat.format(nowTime);
    }

    /**
     * 在指定日期基础上减去指定天数
     *
     * @param date
     * @param dayNumber
     * @return
     * @throws ParseException
     */
    public static String getAppointDate(String date, Integer dayNumber) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.add(Calendar.DATE, -dayNumber);
        return format.format(calendar.getTime());
    }


    /**
     * 获取时间段内的每分钟的集合
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getMiBetweenDateRange(String startTime, String endTime) {
        List<String> dateList = new ArrayList<String>();
        Calendar tt = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            tt.setTime(simple.parse(startTime));
            Calendar t2 = Calendar.getInstance();
            t2.setTime(simple.parse(endTime));
            for (; tt.compareTo(t2) < 0; tt.add(Calendar.MINUTE, 1)) {
                dateList.add(simple.format(tt.getTime()));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return dateList;
    }

    /**
     * 获取该月工作日的日期
     *
     * @param year  年
     * @param month 月
     * @param type  类型 1-全时段 2-工作日 3-周末
     */
    public static List<LocalDate> getMonthDayByType(int year, int month, int type) {
        List<LocalDate> resultList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);
        while (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) < month) {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (type == 1) { //全时段
                resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
            } else if (type == 2) { //工作日
                if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
                    resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                }
            } else if (type == 3) { //周末
                if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
                    resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                }
            }
            cal.add(Calendar.DATE, 1);
        }
        return resultList;
    }

    /**
     * 获取该月周末的日期
     *
     * @param year     年
     * @param month    月
     * @param weekDays 多个周几
     */
    public static List<LocalDate> getMonthDayByWeekDays(int year, int month, List<Integer> weekDays) {
        List<LocalDate> resultList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);
        while (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) < month) {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            weekDays.forEach(weekDay -> {
                switch (weekDay) {
                    case 1: //周一
                        if (day == Calendar.MONDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 2: //周二
                        if (day == Calendar.TUESDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 3: //周三
                        if (day == Calendar.WEDNESDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 4: //周四
                        if (day == Calendar.THURSDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 5: //周五
                        if (day == Calendar.FRIDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 6: //周六
                        if (day == Calendar.SATURDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                    case 7: //周日
                        if (day == Calendar.SUNDAY) {
                            resultList.add(DateUtils.dateToLocalDateTime(cal.getTime()).toLocalDate());
                        }
                        break;
                }
            });
            cal.add(Calendar.DATE, 1);
        }
        return resultList;
    }

    /**
     * date2比date1多的天数
     *
     * @param dateStr1
     * @param dateStr2
     * @return
     */
    public static int differentDays(String dateStr1, String dateStr2) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        try {
            cal1.setTime(df.parse(dateStr1));
            cal2.setTime(df.parse(dateStr2));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //同一年
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                //闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {
            return day2 - day1;
        }
    }

    /**
     * 获取两个时间每半个小时的时间集合
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getMinuteBetweenDate(String startTime, String endTime) {
        List<String> dateList = new ArrayList<String>();
        Calendar tt = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer minute = Integer.valueOf(startTime.substring(14, 16));
        if (minute > 30) {
            startTime = startTime.substring(0, 14) + "30:00";
        } else {
            startTime = startTime.substring(0, 14) + "00:00";
        }
        try {
            tt.setTime(simple.parse(startTime));
            Calendar t2 = Calendar.getInstance();
            t2.setTime(simple.parse(endTime));
            for (; tt.compareTo(t2) < 0; tt.add(Calendar.MINUTE, 30)) {
                dateList.add(simple.format(tt.getTime()));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return dateList;
    }

    /**
     * 获取当前自然周的周一的日期
     *
     * @param dateStr
     * @return
     */
    public static String getCurrentMondayDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        // 转为calendar格式
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        // 如果是周日
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        /**
         * calendar.get(Calendar.MONTH)+1  calendar中的月份以0开头
         * Calendar.DAY_OF_WEEK 当前日期是所在周的第几天（以周日为一周的第一天）
         * Calendar.DATE 当前日期是几号
         *  */
        // 获取当前日期是当周的第i天
        int i = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        // 获取当前日期所在周的第一天
        calendar.add(Calendar.DATE, -i + 1);
        String format = sdf.format(calendar.getTime());
        return format;
    }

    /**
     * 获取指定时间下一个月的第一天.
     *
     * @return
     */
    public static String getPerFirstDayOfMonth(String dateStr) {
        SimpleDateFormat dft = new SimpleDateFormat(DATE_PATTERN);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dft.parse(dateStr));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 获取天 第一天或最后一天
     *
     * @param weeks   0本周，1下周，-1上周 以此类推
     * @param isFirst true获取开始时间 false获取结束时间
     * @return java.lang.String
     */
    public static String getStartOrEndDayOfDay(String today, long weeks, Boolean isFirst) {
        LocalDate time = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate resDate = time.plusDays(weeks);
        String date;
        if (isFirst) {
            date = LocalDate.of(resDate.getYear(), resDate.getMonth(), resDate.getDayOfMonth()) + min;
        } else {
            date = LocalDate.of(resDate.getYear(), resDate.getMonth(), resDate.getDayOfMonth()) + max;
        }
        return date;
    }


    /**
     * 获取周 第一天或最后一天
     *
     * @param weeks   0本周，1下周，-1上周 以此类推
     * @param isFirst true获取开始时间 false获取结束时间
     * @return java.lang.String
     */
    public static String getStartOrEndDayOfWeek(String today, long weeks, Boolean isFirst) {
        LocalDate time = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate resDate = time.plusWeeks(weeks);
        DayOfWeek week = resDate.getDayOfWeek();
        int value = week.getValue();
        String date;
        if (isFirst) {
            date = resDate.minusDays(value - 1) + min;
        } else {
            date = resDate.plusDays(7 - value) + max;
        }
        return date;
    }


    /**
     * 获取月份 第一天或最后一天
     *
     * @param months  0本月，1下月，-1上月 以此类推
     * @param isFirst true获取开始时间 false获取结束时间
     * @return java.lang.String
     */
    public static String getStartOrEndDayOfMonth(String today, long months, Boolean isFirst) {
        LocalDate time = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate resDate = time.plusMonths(months);
        Month month = resDate.getMonth();
        int length = month.length(resDate.isLeapYear());
        String date;
        if (isFirst) {
            date = LocalDate.of(resDate.getYear(), month, 1) + min;
        } else {
            date = LocalDate.of(resDate.getYear(), month, length) + max;
        }
        return date;
    }


    /**
     * 获取季度 第一天或最后一天
     *
     * @param quarters 0本季度，1下季度，-1上季度 以此类推
     * @param isFirst  true获取开始时间 false获取结束时间
     * @return java.lang.String
     */
    public static String getStartOrEndDayOfQuarter(String today, long quarters, Boolean isFirst) {
        LocalDate time = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate resDate = time.plusMonths(quarters * 3);
        Month month = resDate.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        String date;
        if (isFirst) {
            date = LocalDate.of(resDate.getYear(), firstMonthOfQuarter, 1) + min;
        } else {
            date = LocalDate.of(resDate.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(resDate.isLeapYear())) + max;
        }
        return date;
    }


    /**
     * 获取年份 第一天或最后一天
     *
     * @param years   0本今年，1明年，-1去年 以此类推
     * @param isFirst true获取开始时间 false获取结束时间
     * @return java.lang.String
     */
    public static String getStartOrEndDayOfYear(String today, long years, Boolean isFirst) {
        LocalDate time = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate resDate = time.plusYears(years);
        String date;
        if (isFirst) {
            date = LocalDate.of(resDate.getYear(), Month.JANUARY, 1) + min;
        } else {
            date = LocalDate.of(resDate.getYear(), Month.DECEMBER, Month.DECEMBER.length(resDate.isLeapYear())) + max;
        }
        return date;
    }

    /**
     * 获取指定时间的000毫秒
     *
     * @return
     */
    public static String getStartMill(String time) {
        return time + ".000";
    }

    /**
     * 获取指定时间的999毫秒
     *
     * @return
     */
    public static String getEndMill(String time) {
        return time + ".999";
    }

    /**
     * 根据时间类型加或减指定数量获取时间
     *
     * @param startDate 指定时间 yyyy-MM-dd HH:mm:ss
     * @param timeSolt  时间数量
     * @param dateType  1-分钟 2-小时 3-日 4-周 5-月 6-年
     * @param markType  1加 2-减
     * @return
     */
    public static String getDateByType(String startDate, Integer timeSolt, Integer dateType, Integer markType) {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforeDate = null;
        try {
            Date date = simple.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            switch (dateType) {
                case 1: //分钟
                    if (markType == 1) {
                        calendar.add(Calendar.MINUTE, +timeSolt);
                    } else {
                        calendar.add(Calendar.MINUTE, -timeSolt);
                    }
                    break;
                case 2: //小时
                    if (markType == 1) {
                        calendar.add(Calendar.HOUR_OF_DAY, +timeSolt);
                    } else {
                        calendar.add(Calendar.HOUR_OF_DAY, -timeSolt);
                    }
                    break;
                case 3: //日
                    if (markType == 1) {
                        calendar.add(Calendar.DAY_OF_MONTH, +timeSolt);
                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, -timeSolt);
                    }
                    break;
                case 4: //周
                    if (markType == 1) {
                        calendar.add(Calendar.WEEK_OF_MONTH, +timeSolt);
                    } else {
                        calendar.add(Calendar.WEEK_OF_MONTH, -timeSolt);
                    }
                    break;
                case 5: //月
                    if (markType == 1) {
                        calendar.add(Calendar.MONTH, +timeSolt);
                    } else {
                        calendar.add(Calendar.MONTH, -timeSolt);
                    }
                    break;
                case 6: //年
                    if (markType == 1) {
                        calendar.add(Calendar.YEAR, +timeSolt);
                    } else {
                        calendar.add(Calendar.YEAR, -timeSolt);
                    }
                    break;
            }
            beforeDate = calendar.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return simple.format(beforeDate);
    }

    public static String getFormatIntervalPattern(Integer formatInterval) {
        switch (formatInterval) {
            case 1: //yyyy-MM-dd HH:mm:ss
                return "yyyy-MM-dd HH:mm:ss";
            case 2: //yyyy-MM-dd
                return "yyyy-MM-dd";
            case 3: //yyyy-MM
                return "yyyy-MM";
            case 4: //yyyy
                return "yyyy";
            case 5: //HH:mm:ss
                return "HH:mm:ss";
            case 6: //HH:mm
                return "HH:mm";
        }
        return "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * 根据查询时间类型，查询指定开始时间
     * @param dateType 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年
     *                 -1-昨日 0-当日；1-本周；2-本月；3-本年
     * @return
     */
    public static String getStartTimeByQueryType(Integer dateType) {
        if (StringUtils.isNotEmpty(dateType)) {
            LocalDate today = LocalDate.now();
            switch (dateType) {
                case 0:
                    return localDateTimeToStr(LocalDateTime.of(today, LocalTime.MIN));
                case 1:
                    //获取昨日开始时间
                    return localDateTimeToStr(today.minusDays(1).atStartOfDay());
                case 2:
                    return localDateTimeToStr(today.minusDays(6).atStartOfDay());
                case 3:
                    return localDateTimeToStr(today.minusDays(29).atStartOfDay());
                case 4:
                    LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfMonth, LocalTime.MIN));
                case 5:
                    LocalDate firstDayOfLastMonth = today.minusMonths(1).withDayOfMonth(1);
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfLastMonth, LocalTime.MIN));
                case 6:
                    LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfYear, LocalTime.MIN));
            }
        }
        return null;
    }

    /**
     * 根据查询时间类型，查询指定结束时间
     * @param dateType 查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年
     * @return
     */
    public static String getEndTimeByQueryType(Integer dateType) {
        if (StringUtils.isNotEmpty(dateType)) {
            LocalDate today = LocalDate.now();
            switch (dateType) {
                case 0:
                    return localDateTimeToStr(LocalDateTime.of(today, LocalTime.MAX));
                case 1:
                    return localDateTimeToStr(today.minusDays(1).atTime(23, 59, 59));
                case 2:
                case 3:
//                    return localDateTimeToStr(today.atTime(23, 59, 59));
                    return localDateTimeToStr(LocalDateTime.now());
                case 4:
                    LocalDate firstDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfMonth, LocalTime.MAX));
                case 5:
                    LocalDate firstDayOfLastMonth = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfLastMonth, LocalTime.MAX));
                case 6:
                    LocalDate firstDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
                    return localDateTimeToStr(LocalDateTime.of(firstDayOfYear, LocalTime.MAX));
            }
        }
        return null;
    }

    /**
     * 判断当前日期是否是工作日或周末
     * @param dateStr 日期
     * @param type 类型 1-工作日 2-周末
     * @return
     */
    public static Boolean isWorkDayOrWeek(String dateStr, Integer type) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        // 转为calendar格式
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        boolean isWorkingDay = (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY);

        if (isWorkingDay) {
            return type == 1;
        } else {
            return type == 2;
        }
    }

    /**
     * utc时间戳转换成日期
     * @param timestamp utc时间戳
     * @return
     */
    public static String utcTimestampConversion(Long timestamp) {
        Long unixtimestamp = timestamp + 8*60*60;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(new Date(unixtimestamp * 1000));
    }
}
