package com.sunmax.common.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 设备字段查询数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "设备字段查询数据")
public class DeviceFieldQueryVo {

    /**
     * 多个设备id
     */
    @Schema(description = "多个设备id")
    private Set<String> deviceIds;

    /**
     * 多个功能点标识
     */
    @Schema(description = "多个功能点标识")
    private Set<String> functionLogos;

}
