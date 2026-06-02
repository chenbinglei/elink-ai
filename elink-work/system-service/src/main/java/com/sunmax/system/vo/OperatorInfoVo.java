package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/10/715:54
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "OperatorInfoVo", description = "运营商新增或编辑参数")
public class OperatorInfoVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID", required = true)
    private String operatorId;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称", required = true)
    private String operatorName;

    /**
     * 运营商简称
     */
    @ApiModelProperty(value = "运营商简称")
    private String operatorShortName;

    /**
     * 运营商统一社会信用代码
     */
    @ApiModelProperty(value = "运营商统一社会信用代码", required = true)
    private String operatorCreditCode;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人", required = true)
    private String operatorContact;

    /**
     * 运营商电话1
     */
    @ApiModelProperty(value = "运营商电话1", required = true)
    private String operatorTel1;

    /**
     * 运营商电话2
     */
    @ApiModelProperty(value = "运营商电话2")
    private String operatorTel2;

    /**
     * 运营商注册地址
     */
    @ApiModelProperty(value = "运营商注册地址")
    private String operatorRegAddress;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String operatorNote;
}
