package com.sunmax.device.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备批量添加参数实体类")
public class DeviceBatchChangeVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 资产分类id
     */
    @Schema(description = "资产分类id")
    private String typeId;

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    private String modelId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 额定功率
     */
    @Schema(description = "额定功率")
    private Double ratedPower;

    /**
     * 设备出厂编码
     */
    @Schema(description = "设备出厂编码")
    private String factoryCode;

    /**
     * 额定容量
     */
    @Schema(description = "额定容量")
    private Double ratedCap;

}
