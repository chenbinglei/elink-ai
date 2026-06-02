package com.sunmax.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.util.oss.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Slf4j
public class WeatherHfUtil {

    // 替换为你的和风天气 API Key
    private static final String API_KEY = "247899181d7c410881d2eb3dae2b7f2b";
    private static final String FORECAST_URL = "https://devapi.qweather.com/v7/weather/";

    /**
     * 根据经纬度获取最近每天的天气
     * @param longitude 经度
     * @param latitude  纬度
     */
    public static List<WeatherDayDto> getWeatherDay(double longitude, double latitude, String days) {
        //返回的集合
        List<WeatherDayDto> resultList = Lists.newArrayList();

        HttpURLConnection conn = null;
        try {
            String urlString = FORECAST_URL + days + "?location=" + longitude + FileUtil.COMMA + latitude + "&key=" + API_KEY;
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 获取响应
            InputStream inputStream = conn.getInputStream();
            String encoding = conn.getContentEncoding();

            // 如果是 GZIP 压缩，解压
            if ("gzip".equalsIgnoreCase(encoding)) {
                inputStream = new GZIPInputStream(inputStream);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            // 解析 JSON
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            //修改时间
            String updateTime = json.get("updateTime").getAsString();
            String code = json.get("code").getAsString();
            if ("200".equals(code) && json.has("daily")) {
                JsonArray dailyList = json.getAsJsonArray("daily");
                if (dailyList != null && dailyList.size() > 0) {
                    dailyList.forEach(daily -> {
                        JsonObject dailyObject = daily.getAsJsonObject();
                        //对象
                        WeatherDayDto result = new WeatherDayDto();
                        if (StringUtil.isNotEmpty(updateTime)) {
                            result.setUpdateTime(DateUtil.iso8601ToStr(updateTime));
                        }
                        result.setFxDate(dailyObject.get("fxDate").getAsString());
                        result.setTempMax(dailyObject.get("tempMax").getAsString());
                        result.setTempMin(dailyObject.get("tempMin").getAsString());
                        result.setIconDay(dailyObject.get("iconDay").getAsString());
                        result.setTextDay(dailyObject.get("textDay").getAsString());
                        result.setIconNight(dailyObject.get("iconNight").getAsString());
                        result.setTextNight(dailyObject.get("textNight").getAsString());
                        resultList.add(result);
                    });
                }
            } else {
                log.error("天气查询失败，错误码: {}", code);
                log.error("详细信息: {}", json.get("message").getAsString());
            }
            return resultList;
        } catch (Exception e) {
            log.error("获取天气数据失败", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // 测试
    public static void main(String[] args) {
        // 示例：北京
        LocalDate nowDate = LocalDate.now();
        List<WeatherDayDto> weatherDayList = WeatherHfUtil.getWeatherDay(120.749196, 30.772760, "3d");
        if (CollectionUtils.isNotEmpty(weatherDayList)) {
            WeatherDayDto weatherDay = weatherDayList.stream().collect(Collectors.toMap(WeatherDayDto::getFxDate,
                    a -> a)).get(DateUtil.localDateToStr(nowDate));
            if (weatherDay != null) {
                System.out.println(weatherDay.getTempMin());
                System.out.println(weatherDay.getTempMax());
                System.out.println(weatherDay.getIconDay());
            }
        }
    }
}
