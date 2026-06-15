package com.sunmax.configure.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "推送储能状态统计数据实体类")
public class StationStatusDto {

    /**
     * 项目编号
     */
    @Schema(description = "项目编号")
    private String stationNo;

    /**
     * 数据时间 时间戳（timestamp），毫秒级
     */
    @Schema(description = "数据时间")
    private Long dataTime;

    /**
     * 状态编码 0-平台通讯正常,站端通讯正常 1-平台通讯正常,站端通讯中断
     */
    @Schema(description = "状态编码 0-平台通讯正常,站端通讯正常 1-平台通讯正常,站端通讯中断")
    private String stationStatus;

}
