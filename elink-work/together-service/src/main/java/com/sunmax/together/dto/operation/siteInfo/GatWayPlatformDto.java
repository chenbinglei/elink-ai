package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "GatWayPlatformDto", description = "网关关联平台信息返回实体类")
public class GatWayPlatformDto {

    /**
     * 平台id
     */
    @ApiModelProperty(value = "平台id")
    private String platformId;

    /**
     * 平台标识
     */
    @ApiModelProperty(value = "平台标识")
    private String platformLogo;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称")
    private String platformName;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    private String ipAddress;

    /**
     * 端口号
     */
    @ApiModelProperty("端口号")
    private String portNumber;

    /**
     * 电桩状态列表
     */
    @ApiModelProperty("电桩状态列表")
    private List<PileStateInfo> pileStateInfoList;

    /**
     * 电桩下发状态信息
     */
    @Data
    public static class PileStateInfo {

        /**
         * 电桩编码
         */
        @ApiModelProperty("电桩编码")
        private String pileCode;

        /**
         * 电桩状态 true-连接 false-未连接
         */
        @ApiModelProperty(value = "电桩状态 true-连接 false-未连接")
        private Boolean flag;
    }
}
