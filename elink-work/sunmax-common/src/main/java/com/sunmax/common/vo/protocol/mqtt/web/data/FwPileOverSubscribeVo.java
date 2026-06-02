package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.util.List;

//电桩接收固件完成响应
@Data
public class FwPileOverSubscribeVo {
    /**
     * 充电桩编号
     */
    private List<String> pilesCode;
    /**
     *固件类型
     */
    private Integer deviceType;

    /**
     *固件主版本号
     */
    private Integer majorNo;

    /**
     *固件副版本号
     */
    private Integer childNo;

    /**
     *固件内测版本号
     */
    private Integer betaNo;

    public List<FwPileUpdateInfo> reasonList;

    @Data
    public static class FwPileUpdateInfo{
        String pilesCode;
        Integer failReason;
    }

}


