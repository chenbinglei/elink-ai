package com.sunmax.common.dto.protocol;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlatformStatusDto {

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 平台数据(平台标识->电桩数据)
     */
    @ApiModelProperty(value = "平台数据(平台标识->电桩数据)")
    private Map<String, List<Pile>> platformPileMap = Maps.newHashMap();

    /**
     * 下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因
     */
    @ApiModelProperty(value = "下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因")
    private Integer issuedStatus;

    @Data
    @ApiModel(value = "Pile", description = "电桩数据列表")
    public static class Pile {

        /**
         * 设备编号
         */
        @ApiModelProperty(value = "设备编号")
        private String devId;

        /**
         * 电桩状态 true-连接 false-未连接
         */
        @ApiModelProperty(value = "电桩状态 true-连接 false-未连接")
        private Boolean flag;

    }

}
