package com.sunmax.configure.dto.storage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StationStatusDto", description = "推送储能状态统计数据实体类")
public class StationStatusDto {

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号", required = true)
    private String stationNo;

    /**
     * 数据时间 时间戳（timestamp），毫秒级
     */
    @ApiModelProperty(value = "数据时间", required = true)
    private Long dataTime;

    /**
     * 状态编码 0-平台通讯正常,站端通讯正常 1-平台通讯正常,站端通讯中断
     */
    @ApiModelProperty(value = "状态编码 0-平台通讯正常,站端通讯正常 1-平台通讯正常,站端通讯中断", required = true)
    private String stationStatus;

}
