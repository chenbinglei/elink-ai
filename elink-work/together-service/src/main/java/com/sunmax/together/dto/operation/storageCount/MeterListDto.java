package com.sunmax.together.dto.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "并网表列表返回实体类")
public class MeterListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

}
