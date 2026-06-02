package com.sunmax.configure.dto.province;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:29
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "StationStatusDto", description = "充电站状态实体类")
public class StationStatusDto {

    /**
     * 充电站编码
     */
    @ApiModelProperty(value = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商ID
     *
     * 充电站的设备所属方ID（原组织机构代码），为个人时填写999999999
     */
    @ApiModelProperty(value = "充电服务运营商ID")
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 站点状态
     * 0： 未知
     * 1： 建设中
     * 5： 关闭下线
     * 6： 维护中
     * 50：正常使用
     */
    @ApiModelProperty(value = "站点状态")
    @JSONField(name = "StationStatus")
    private Integer stationStatus;

    /**
     * 接口状态列表
     */
    @ApiModelProperty(value = "接口状态列表")
    @JSONField(name = "ConnectorStatusInfos")
    private List<ConnectorStatusDto> connectorStatusInfos;
}
