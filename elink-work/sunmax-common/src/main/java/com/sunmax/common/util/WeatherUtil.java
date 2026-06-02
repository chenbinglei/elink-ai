package com.sunmax.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunmax.common.config.cache.LocalCacheUtil;
import com.sunmax.common.dto.WeatherForecastDto;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.utcTimestampConversion;
import static com.sunmax.common.util.NauticalUtil.sendHttpGet;

/**
 * 天气预报工具类
 */
@Slf4j
public class WeatherUtil {

    //OpenWeather的key
    public static final String appid = "ae7b797ef8c20f6c776ed5150b23f3f0";

    //OpenWeather的调用url
    public static final String url = "https://api.openweathermap.org/data/3.0/onecall";

    //OpenWeather的语言类型
    public static final String language = "zh_cn";

    //OpenWeather的测量单位类型(这里以摄氏度为单位的温度和以米/秒为单位的风速)
    public static final String units = "metric";

    //OpenWeather的天气图标获取url
    public static final String iconUrl = "https://openweathermap.org/img/wn/";

    //OpenWeather的天气图标参数类型(写死)
    public static final String iconParam = "@2x.png";


    //风向描述类型
    private static final String[] directArr = new String[]{
            "北", "东北偏北", "东北", "东北偏东",
            "东", "东南偏东", "东南", "东南偏南",
            "南", "西南偏南", "西南", "西南偏西",
            "西", "西北偏西", "西北", "西北偏北"
    };

    /**
     * 根据经纬度获取当前天气以及未来8天预测天气数据
     * @param longitude 经度
     * @param latitude  维度
     * @return
     */
    public static ResponseResult<WeatherForecastDto> getWeatherForecast(String longitude, String latitude) {
        WeatherForecastDto forecastDto = new WeatherForecastDto();
        if (StringUtil.isNotEmpty(longitude) && StringUtil.isNotEmpty(latitude)) {
            //本地缓存key
            String localKey = "weather_" + longitude + "_" + latitude;
            //先在本地缓存中查询，如果查到了则返回缓存数据，如果没查到则调用api接口，并且把数据存到本地缓存中
            String cacheData = LocalCacheUtil.get(localKey);
            if (StringUtil.isNotEmpty(cacheData)) {
                forecastDto = JSON.parseObject(cacheData, WeatherForecastDto.class);
            } else {
                //请求天气api接口
                String result = sendHttpGet(url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + appid + "&lang=" + language + "&units=" + units);
                if (StringUtil.isNotEmpty(result)) {
                    JSONObject resultJson = JSON.parseObject(result);
                    if (StringUtil.isNotEmpty(resultJson.getString("current"))) {
                        //当前天气数据
                        String current = resultJson.getString("current");
                        forecastDto = JSONObject.parseObject(current, WeatherForecastDto.class);
                        //根据风速度数获取风向
                        if (StringUtil.isNotEmpty(forecastDto.getWind_deg())) {
                            forecastDto.setWindDirection(windDirectionSwitch(forecastDto.getWind_deg()));
                        }
                        forecastDto.setDt(utcTimestampConversion(Long.parseLong(forecastDto.getDt())));
                        forecastDto.setSunrise(utcTimestampConversion(Long.parseLong(forecastDto.getSunrise())));
                        forecastDto.setSunset(utcTimestampConversion(Long.parseLong(forecastDto.getSunset())));
                        //未来8天预测天气数据
                        String daily = resultJson.getString("daily");
                        if (StringUtil.isNotEmpty(daily)) {
                            forecastDto.setDaily(JSONObject.parseArray(daily, WeatherForecastDto.daily.class).stream().peek( directArr -> {
                                directArr.setDt(utcTimestampConversion(Long.parseLong(directArr.getDt())));
                                directArr.setSunrise(utcTimestampConversion(Long.parseLong(directArr.getSunrise())));
                                directArr.setSunset(utcTimestampConversion(Long.parseLong(directArr.getSunset())));
                                directArr.setMoonrise(utcTimestampConversion(Long.parseLong(directArr.getMoonrise())));
                                directArr.setMoonset(utcTimestampConversion(Long.parseLong(directArr.getMoonset())));
                                //根据风速度数获取风向
                                if (StringUtil.isNotEmpty(directArr.getWind_deg())) {
                                    directArr.setWindDirection(windDirectionSwitch(directArr.getWind_deg()));
                                }
                            }).collect(Collectors.toList()));
                        }
                    } else if (StringUtil.isNotEmpty(resultJson.getString("cod"))) {
                        String cod = resultJson.getString("cod");
                        String message = resultJson.getString("message");
                        return ResponseResult.error(message, Integer.parseInt(cod), forecastDto);
                    }
                }
                LocalCacheUtil.put(localKey, JSON.toJSONString(forecastDto), 60 * 60);
            }
        }
        return ResponseResult.ok(forecastDto);
    }


    /**
     * 风向角度转具体风向
     * @param degrees 风向角度 0 <= degrees <= 360
     * @return 具体风向
     */
    public static String windDirectionSwitch(Double degrees) {
        int index = 0;
        if (348.75 <= degrees && degrees <= 360) {
            index = 0;
        } else if (0 <= degrees && degrees <= 11.25) {
            index = 0;
        } else if (11.25 < degrees && degrees <= 33.75) {
            index = 1;
        } else if (33.75 < degrees && degrees <= 56.25) {
            index = 2;
        } else if (56.25 < degrees && degrees <= 78.75) {
            index = 3;
        } else if (78.75 < degrees && degrees <= 101.25) {
            index = 4;
        } else if (101.25 < degrees && degrees <= 123.75) {
            index = 5;
        } else if (123.75 < degrees && degrees <= 146.25) {
            index = 6;
        } else if (146.25 < degrees && degrees <= 168.75) {
            index = 7;
        } else if (168.75 < degrees && degrees <= 191.25) {
            index = 8;
        } else if (191.25 < degrees && degrees <= 213.75) {
            index = 9;
        } else if (213.75 < degrees && degrees <= 236.25) {
            index = 10;
        } else if (236.25 < degrees && degrees <= 258.75) {
            index = 11;
        } else if (258.75 < degrees && degrees <= 281.25) {
            index = 12;
        } else if (281.25 < degrees && degrees <= 303.75) {
            index = 13;
        } else if (303.75 < degrees && degrees <= 326.25) {
            index = 14;
        } else if (326.25 < degrees && degrees < 348.75) {
            index = 15;
        } else {
            log.error("风向角度[{}] 大于 360", degrees);
        }
        return directArr[index];
    }
}
