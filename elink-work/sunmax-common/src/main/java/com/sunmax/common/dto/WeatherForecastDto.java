package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "WeatherForecastDto", description = "天气预报信息返回实体类")
public class WeatherForecastDto {

    /**
     * 当前时间
     */
    @ApiModelProperty(value = "当前时间")
    private String dt;

    /**
     * 日出时间
     */
    @ApiModelProperty(value = "日出时间")
    private String sunrise;

    /**
     * 日落时间
     */
    @ApiModelProperty(value = "日落时间")
    private String sunset;

    /**
     * 温度
     */
    @ApiModelProperty(value = "温度")
    private Double temp;

    /**
     * 体感温度
     */
    @ApiModelProperty(value = "体感温度")
    private Double feels_like;

    /**
     * 海平面大气压 hPa
     */
    @ApiModelProperty(value = "海平面大气压 hPa")
    private Double pressure;

    /**
     * 湿度 %
     */
    @ApiModelProperty(value = "湿度 %")
    private Double humidity;

    /**
     * 大气温度
     */
    @ApiModelProperty(value = "大气温度")
    private Double dew_point;

    /**
     * 混浊度 %
     */
    @ApiModelProperty(value = "混浊度 %")
    private Double clouds;

    /**
     * 当前紫外线指数
     */
    @ApiModelProperty(value = "当前紫外线指数")
    private Double uvi;

    /**
     * 平均能见度 米
     */
    @ApiModelProperty(value = "平均能见度 米")
    private Integer visibility;

    /**
     * 风速
     */
    @ApiModelProperty(value = "风速")
    private Double wind_speed;

    /**
     * 风向度数
     */
    @ApiModelProperty(value = "风向度数")
    private Double wind_deg;

    /**
     * 风向
     */
    @ApiModelProperty(value = "风向")
    private String windDirection;

    /**
     * 天气状况
     */
    @ApiModelProperty(value = "天气状况")
    private List<WeatherForecastDto.weather> weather;

    /**
     * 预测天气列表
     */
    @ApiModelProperty(value = "预测天气列表")
    private List<WeatherForecastDto.daily> daily;

    /**
     * 天气状况信息
     */
    @Data
    public static class weather {
        /**
         * 天气状况ID
         */
        @ApiModelProperty(value = "天气状况ID")
        private Integer id;

        /**
         * 天气参数组
         */
        @ApiModelProperty(value = "天气参数组")
        private String main;

        /**
         * 天气状况
         */
        @ApiModelProperty(value = "天气状况")
        private String description;

        /**
         * 天气图标id
         */
        @ApiModelProperty(value = "天气图标id")
        private String icon;
    }

    /**
     * 每日预报天气数据
     */
    @Data
    public static class daily {
        /**
         * 预测时间
         */
        @ApiModelProperty(value = "预测时间")
        private String dt;

        /**
         * 日出时间
         */
        @ApiModelProperty(value = "日出时间")
        private String sunrise;

        /**
         * 日落时间
         */
        @ApiModelProperty(value = "日落时间")
        private String sunset;

        /**
         * 月出时间
         */
        @ApiModelProperty(value = "月出时间")
        private String moonrise;

        /**
         * 月落时间
         */
        @ApiModelProperty(value = "月落时间")
        private String moonset;

        /**
         * 当天天气状况的人类可读描述
         */
        @ApiModelProperty(value = "当天天气状况的人类可读描述")
        private String summary;

        /**
         * 预测温度
         */
        @ApiModelProperty(value = "预测温度")
        private WeatherForecastDto.temp temp;

        /**
         * 预测体感温度
         */
        @ApiModelProperty(value = "预测体感温度")
        private WeatherForecastDto.feelsLike feels_like;

        /**
         * 海平面大气压 hPa
         */
        @ApiModelProperty(value = "海平面大气压 hPa")
        private Double pressure;

        /**
         * 湿度 %
         */
        @ApiModelProperty(value = "湿度 %")
        private Double humidity;

        /**
         * 大气温度
         */
        @ApiModelProperty(value = "大气温度")
        private Double dew_point;

        /**
         * 混浊度 %
         */
        @ApiModelProperty(value = "混浊度 %")
        private Double clouds;

        /**
         * 风速
         */
        @ApiModelProperty(value = "风速")
        private Double wind_speed;

        /**
         * 风向度数
         */
        @ApiModelProperty(value = "风向度数")
        private Double wind_deg;

        /**
         * 风向
         */
        @ApiModelProperty(value = "风向")
        private String windDirection;

        /**
         * 降水概率
         */
        @ApiModelProperty(value = "降水概率")
        private Double pop;

        /**
         * 预测天气状况
         */
        @ApiModelProperty(value = "预测天气状况")
        private List<WeatherForecastDto.weather> weather;
    }

    /**
     * 预测温度对象
     */
    @Data
    public static class temp {
        /**
         * 早晨的温度
         */
        @ApiModelProperty(value = "早晨的温度")
        private Double morn;

        /**
         * 日温
         */
        @ApiModelProperty(value = "日温")
        private Double day;

        /**
         * 傍晚温度
         */
        @ApiModelProperty(value = "傍晚温度")
        private Double eve;

        /**
         * 夜间温度
         */
        @ApiModelProperty(value = "夜间温度")
        private Double night;

        /**
         * 最低日温度
         */
        @ApiModelProperty(value = "最低日温度")
        private Double min;

        /**
         * 最高日温度
         */
        @ApiModelProperty(value = "最高日温度")
        private Double max;
    }

    /**
     * 预测体感温度对象
     */
    @Data
    public static class feelsLike {
        /**
         * 早晨的温度
         */
        @ApiModelProperty(value = "早晨的温度")
        private Double morn;

        /**
         * 日温
         */
        @ApiModelProperty(value = "日温")
        private Double day;

        /**
         * 傍晚温度
         */
        @ApiModelProperty(value = "傍晚温度")
        private Double eve;

        /**
         * 夜间温度
         */
        @ApiModelProperty(value = "夜间温度")
        private Double night;
    }
}
