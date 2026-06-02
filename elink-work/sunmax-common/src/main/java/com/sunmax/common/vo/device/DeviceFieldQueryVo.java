package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "DeviceFieldQueryVo", description = "设备字段查询数据")
public class DeviceFieldQueryVo {

    /**
     * 多个设备id
     */
    @ApiModelProperty("多个设备id")
    private Set<String> deviceIds;

    /**
     * 多个功能点标识
     */
    @ApiModelProperty("多个功能点标识")
    private Set<String> functionLogos;

}
