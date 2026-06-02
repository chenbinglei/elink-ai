package com.sunmax.protocol.vo.virtual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileQtVo", description = "电桩电量参数实体类")
public class VirtualPileQtVo {

    /**
     * 多个充电桩编号(多个用逗号隔开)
     */
    @ApiModelProperty(value = "多个充电桩编号(多个用逗号隔开)", required = true)
    private String pileCodes;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String endTime;

}
