package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "DeviceEventChangeVo", description = "设备事件编辑参数实体类")
public class DeviceEventChangeVo {

    /**
     * 关联设备id
     */
    @ApiModelProperty(value = "关联设备id", required = true)
    private String deviceId;

    /**
     * 关联模型事件id
     */
    @ApiModelProperty(value = "关联模型事件id", required = true)
    private String eventId;

    /**
     * 事件来源
     */
    @ApiModelProperty(value = "事件来源", required = true)
    private String eventSource;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复", required = true)
    private Integer eventStatus;

}
