package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2016:53
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "StationDispatchInfoDto", description = "功率控制命令实体类")
public class StationDispatchInfoDto {

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 调度站
     */
    @ApiModelProperty(value = "调度站")
    @JSONField(name = "DispatchStation")
    private String dispatchStation;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    @JSONField(name = "EquipmentId")
    private String equipmentId;

    /**
     * 执行数据
     */
    @ApiModelProperty(value = "执行数据")
    @JSONField(name = "OperDate")
    private Double operDate;
}
