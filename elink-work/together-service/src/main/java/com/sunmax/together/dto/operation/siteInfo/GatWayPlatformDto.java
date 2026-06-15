package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "网关关联平台信息返回实体类")
public class GatWayPlatformDto {

    /**
     * 平台id
     */
    @Schema(description = "平台id")
    private String platformId;

    /**
     * 平台标识
     */
    @Schema(description = "平台标识")
    private String platformLogo;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * ip地址
     */
    @Schema(description = "ip地址")
    private String ipAddress;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    private String portNumber;

    /**
     * 电桩状态列表
     */
    @Schema(description = "电桩状态列表")
    private List<PileStateInfo> pileStateInfoList;

    /**
     * 电桩下发状态信息
     */
    @Data
    public static class PileStateInfo {

        /**
         * 电桩编码
         */
        @Schema(description = "电桩编码")
        private String pileCode;

        /**
         * 电桩状态 true-连接 false-未连接
         */
        @Schema(description = "电桩状态 true-连接 false-未连接")
        private Boolean flag;
    }
}
