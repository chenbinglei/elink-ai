package com.sunmax.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.CronExpression;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CronUtil {
    /**
     * 年
     */
    public static final String YEAR = "y";

    /**
     * 月
     */
    public static final String MONTH = "n";

    /**
     * 周
     */
    public static final String WEEK = "w";

    /**
     * 日
     */
    public static final String DAY = "d";

    /**
     * 时
     */
    public static final String HOUR = "h";

    /**
     * 分
     */
    public static final String MINUTE = "m";

    /**
     * 秒
     */
    public static final String SECOND = "s";

    /**
     * 获取cron表达式
     * @param str
     * @param type
     * @return
     */
    public static String getCronByType(Integer str, String type) {
        switch (type) {
            case SECOND: //秒
                return "/" + str + " * * * * ? *";
            case MINUTE: //分
                return "0 /" + str + " * * * ? *";
            case HOUR: //时
                return "0 0 /" + str + " * * ? *";
            case DAY: //日
                return "0 0 0 /" + str + " * ? *";
            case MONTH: //月
                if (str != null) { //那个月执行一次
                    return "0 0 0 1 " + str + " ? *";
                } else { //每月1号0点执行
                    return "0 0 0 1 * ? *";
                }
        }
        return null;
    }

    /**
     * 获取cron表达式
     * @param str
     * @param type
     * @return
     */
    public static String getCronTriggerByType(Integer str, String type) {
        switch (type) {
            case SECOND: //秒
                return "0/" + str + " * * * * ?";
            case MINUTE: //分
                return "0 0/" + str + " * * * ?";
            case HOUR: //时
                return "0 0 0/" + str + " * * ?";
            case DAY: //日
                return "0 0 0 1/" + str + " * ?";
            case WEEK: //每周一执行
                return "0 0 0 ? * MON";
            case MONTH: //每月1号0点执行
                return "0 0 0 1 * ?";
            case YEAR: //每年1月1号0点执行
                return "0 0 0 1 1 ?";
        }
        return null;
    }

    public static Long getTimeByType(Long str, String type) {
        switch (type) {
            case SECOND: //秒
                return str * 1000;
            case MINUTE: //分
                return str * 1000 * 60;
            case HOUR: //时
                return str * 1000 * 60 * 60;
            case DAY: //日
                return str * 1000 * 60 * 60 * 24;
            case MONTH: //月
                return str * 1000 * 60 * 60 * 24 * 30;
            case YEAR: //年
                return str * 1000 * 60 * 60 * 24 * 365;
        }
        return null;
    }

    public static String getTimeBefore(String cronExpress) {
        try{
            CronExpression cronExpression =new CronExpression(cronExpress);//导包import org.quartz.CronExpression;
            Date date = cronExpression.getPrevFireTime(new Date());
            //将date转换为指定日期格式字符串
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dataFormat.format(date);
            //dateString为转换后的日期格式
        }catch (Exception e){
            log.error("cron获取上次执行时间异常", e);
        }
        return null;
    }

    public static String getTimeAfter(String cronExpress) {
        try{
            CronExpression cronExpression =new CronExpression(cronExpress );//导包import org.quartz.CronExpression;
            Date date = cronExpression.getTimeAfter(new Date());
            //将date转换为指定日期格式字符串
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dataFormat.format(date);
            //dateString为转换后的日期格式
        }catch (Exception e){
            log.error("cron获取下次执行时间异常", e);
        }
        return null;
    }

}
