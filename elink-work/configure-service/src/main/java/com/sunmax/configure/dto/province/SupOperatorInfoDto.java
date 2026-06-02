package com.sunmax.configure.dto.province;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SupOperatorInfoDto", description = "省平台运营商信息实体类")
public class SupOperatorInfoDto {

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID", required = true)
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称", required = true)
    @JSONField(name = "OperatorName")
    private String operatorName;

    /**
     * 运营商统一社会信用代码
     */
    @ApiModelProperty(value = "运营商统一社会信用代码", required = true)
    @JSONField(name = "OperatorUSCID")
    private String operatorCreditCode;

    /**
     * 运营商电话1
     */
    @ApiModelProperty(value = "运营商电话1", required = true)
    @JSONField(name = "OperatorTel1")
    private String operatorTel1;

    /**
     * 运营商电话2
     */
    @ApiModelProperty(value = "运营商电话2")
    @JSONField(name = "OperatorTel2")
    private String operatorTel2;

    /**
     * 运营商注册地址
     */
    @ApiModelProperty(value = "运营商注册地址")
    @JSONField(name = "OperatorRegAddress")
    private String operatorRegAddress;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @JSONField(name = "OperatorNote")
    private String operatorNote;

}
