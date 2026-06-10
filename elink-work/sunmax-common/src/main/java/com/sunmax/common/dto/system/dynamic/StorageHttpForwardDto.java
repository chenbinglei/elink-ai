package com.sunmax.common.dto.system.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "储能http数据转发类")
public class StorageHttpForwardDto {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private String appId;

    /**
     * 公钥
     */
    @Schema(description = "公钥")
    private String publicKey;

    /**
     * 私钥
     */
    @Schema(description = "私钥")
    private String privateKey;

}
