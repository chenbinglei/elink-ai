package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 4.4 CMD_CBINFO_NOTIFY,//硬件控制板软硬件信息通知 CMD=4
 * 发送方向：前置服务--->平台服务
 */
@Data
public class CbInfoNotifyVo {

    /**
     * 控制板个数
     */
    @ApiModelProperty(value = "控制板个数", required = true)
    private Integer cbNum;

    /**
     * 控制板固件信息数据
     */
    @ApiModelProperty(value = "控制板固件信息数据", required = true)
    private List<ControlBoardInfo> cbInfos;

    @Data
    public static class ControlBoardInfo {

        /**
         * 硬件名称
         */
        @ApiModelProperty(value = "硬件名称", required = true)
        private String hardwareName;

        /**
         * 控制板类型
         * 1-V2G_1.0 控制板
         * 2-V2G_2.0 控制板
         * 3-V2G_3.0 控制板
         * 4-V2G_4.0 控制板
         * 5-V2G_5.0 控制板
         * 6-V2G_6.0 控制板
         * 7-V2G_7.0 控制板
         * 8-V2G_8.0 控制板
         */
        @ApiModelProperty(value = "控制板类型", required = true)
        private Integer cbType;

        /**
         * 序号
         */
        @ApiModelProperty(value = "序号", required = true)
        private Integer seq;

        /**
         * 硬件主版本号
         */
        @ApiModelProperty(value = "硬件主版本号", required = true)
        private Integer hMajorNo;

        /**
         * 硬件次版本号
         */
        @ApiModelProperty(value = "硬件次版本号", required = true)
        private Integer hChildNo;

        /**
         * 固件名称
         */
        @ApiModelProperty(value = "固件名称", required = true)
        private String fwName;

        /**
         * 固件主版本号
         */
        @ApiModelProperty(value = "固件主版本号", required = true)
        private Integer fwMajorNo;

        /**
         * 固件次版本号
         */
        @ApiModelProperty(value = "固件次版本号", required = true)
        private Integer fwChildNo;

        /**
         * 固件内测版本号
         */
        @ApiModelProperty(value = "固件内测版本号", required = true)
        private Integer fwBetaNo;

    }

}
