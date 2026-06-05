package com.sunmax.configure.dto.interflow;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2024/8/16 13:53
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "ConnectorStatusInfoDto", description = "充电设备接口信息实体类")
public class ConnectorStatusInfoDto {

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 接口状态
     *
     * 0:离网；
     * 1:空闲；
     * 2:占用（未充电）;
     * 3:占用（充电中）；
     * 4:占用（预约锁定）；
     * 255:故障；
     */
    @ApiModelProperty(value = "接口状态")
    @JSONField(name = "Status")
    private Integer status;

    /**
     * 车位状态
     *
     * 0:未知；
     * 10:空闲；
     * 50:占用
     */
    @ApiModelProperty(value = "车位状态")
    @JSONField(name = "ParkStatus")
    private Integer parkStatus;

    /**
     * 地锁状态
     *
     * 0:未知；
     * 10:已解锁；
     * 50:已上锁
     */
    @ApiModelProperty(value = "地锁状态")
    @JSONField(name = "LockStatus")
    private Integer lockStatus;
}
