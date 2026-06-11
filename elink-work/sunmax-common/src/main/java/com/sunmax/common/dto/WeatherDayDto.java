package com.sunmax.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "每日天气预报信息返回实体类")
public class WeatherDayDto {

    /**
     * 当前API的最近更新时间
     */
    @Schema(description = "当前API的最近更新时间")
    private String updateTime;

    /**
     * 预报日期
     */
    @Schema(description = "预报日期")
    private String fxDate;

    /**
     * 最高温度
     */
    @Schema(description = "最高温度")
    private String tempMax;

    /**
     * 最低温度
     */
    @Schema(description = "最低温度")
    private String tempMin;

    /**
     * 白天天气状况图标标码
     */
    @Schema(description = "白天天气状况图标标码")
    private String iconDay;

    /**
     * 白天天气状况文字描述
     */
    @Schema(description = "白天天气状况文字描述")
    private String textDay;

    /**
     * 夜间天气状况图标标码
     */
    @Schema(description = "夜间天气状况图标标码")
    private String iconNight;

    /**
     * 夜间天气状况文字描述
     */
    @Schema(description = "夜间天气状况文字描述")
    private String textNight;

}
