package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2011:47
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电运营商信息实体类")
public class OperatorInfoDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 运营商ID
     */
    @Schema(description = "运营商ID")
    private String operatorId;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operatorName;

    /**
     * 运营商简称
     */
    @Schema(description = "运营商简称")
    private String operatorShortName;

    /**
     * 运营商统一社会信用代码
     */
    @Schema(description = "运营商统一社会信用代码")
    private String operatorCreditCode;

    /**
     * 联系人
     */
    @Schema(description = "联系人")
    private String operatorContact;

    /**
     * 运营商电话1
     */
    @Schema(description = "运营商电话1")
    private String operatorTel1;

    /**
     * 运营商电话2
     */
    @Schema(description = "运营商电话2")
    private String operatorTel2;

    /**
     * 运营商注册地址
     */
    @Schema(description = "运营商注册地址")
    private String operatorRegAddress;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String operatorNote;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 最后修改时间
     */
    @Schema(description = "最后修改时间")
    private String updateTime;
}
