package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "省平台运营商信息实体类")
public class SupOperatorInfoDto {

    /**
     * 运营商ID
     */
    @Schema(description = "运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    @JSONField(name = "OperatorName")
    private String operatorName;

    /**
     * 运营商统一社会信用代码
     */
    @Schema(description = "运营商统一社会信用代码")
    @JSONField(name = "OperatorUSCID")
    private String operatorCreditCode;

    /**
     * 运营商电话1
     */
    @Schema(description = "运营商电话1")
    @JSONField(name = "OperatorTel1")
    private String operatorTel1;

    /**
     * 运营商电话2
     */
    @Schema(description = "运营商电话2")
    @JSONField(name = "OperatorTel2")
    private String operatorTel2;

    /**
     * 运营商注册地址
     */
    @Schema(description = "运营商注册地址")
    @JSONField(name = "OperatorRegAddress")
    private String operatorRegAddress;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @JSONField(name = "OperatorNote")
    private String operatorNote;

}
