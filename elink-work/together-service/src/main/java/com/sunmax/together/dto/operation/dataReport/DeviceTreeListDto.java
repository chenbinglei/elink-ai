package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备树列表返回实体类")
public class DeviceTreeListDto {

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String id;

    /**
     * 编号
     */
    @Schema(description = "编号")
    private String code;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

    /**
     * 资产分类id
     */
    @Schema(description = "资产分类id")
    private String typeId;
}
