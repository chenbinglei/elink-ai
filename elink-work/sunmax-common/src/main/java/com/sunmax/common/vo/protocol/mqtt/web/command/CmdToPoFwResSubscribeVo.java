package com.sunmax.common.vo.protocol.mqtt.web.command;

import lombok.Data;

import java.util.List;

/**
 * 控制板信息响应
 */
@Data
public class CmdToPoFwResSubscribeVo {

    private List<FwInfo> fwInfos;

    @Data
    public static class FwInfo {

        /**
         * 电桩编码
         */
        private String pilesCode;

        /**
         * 控制板个数
         */
        private Integer cbNum;

        /**
         * 控制板信息数据
         */
        private List<CbInfo> cbInfos;

    }

    @Data
    public static class CbInfo {
        private String hardwareName;

        private Integer cbType;

        private Integer seq;

        private Integer hMajorNo;

        private Integer hChildNo;

        private String fwName;

        private Integer fwMajorNo;

        private Integer fwChildNo;

        private Integer fwBetaNo;
    }
}
