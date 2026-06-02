package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileFaultChangeVo", description = "电桩故障编辑参数实体类")
public class PileFaultChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 故障编码
     */
    @ApiModelProperty(value = "故障编码", required = true)
    private Integer faultCode;

    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称", required = true)
    private String eventName;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警", required = true)
    private Integer eventLevel;

    /**
     * 关联模型表id
     */
    @ApiModelProperty(value = "关联模型表id", required = true)
    private String modelId;

}
