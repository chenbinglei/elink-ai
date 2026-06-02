package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "WeatherDayDto", description = "每日天气预报信息返回实体类")
public class WeatherDayDto {

    /**
     * 当前API的最近更新时间
     */
    @ApiModelProperty(value = "当前API的最近更新时间")
    private String updateTime;

    /**
     * 预报日期
     */
    @ApiModelProperty(value = "预报日期")
    private String fxDate;

    /**
     * 最高温度
     */
    @ApiModelProperty(value = "最高温度")
    private String tempMax;

    /**
     * 最低温度
     */
    @ApiModelProperty(value = "最低温度")
    private String tempMin;

    /**
     * 白天天气状况图标标码
     */
    @ApiModelProperty(value = "白天天气状况图标标码")
    private String iconDay;

    /**
     * 白天天气状况文字描述
     */
    @ApiModelProperty(value = "白天天气状况文字描述")
    private String textDay;

    /**
     * 夜间天气状况图标标码
     */
    @ApiModelProperty(value = "夜间天气状况图标标码")
    private String iconNight;

    /**
     * 夜间天气状况文字描述
     */
    @ApiModelProperty(value = "夜间天气状况文字描述")
    private String textNight;

}
