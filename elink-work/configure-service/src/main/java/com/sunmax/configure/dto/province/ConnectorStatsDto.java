package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:40
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "ConnectorStatsDto", description = "充电设备接口统计实体类")
public class ConnectorStatsDto {

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电设备接口累计电量
     */
    @ApiModelProperty(value = "充电设备接口累计电量")
    @JSONField(name = "ConnectorElectricity")
    private Double connectorElectricity;
}
