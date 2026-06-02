package com.sunmax.protocol.util.platform;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlatformDemandModel {
    private String pileCode;//桩编号
    private String gunCode;//枪编号
    private Boolean recFlag;//接收标志
    private Integer recCode;//执行结果 -1-超时 0-成功 1-启动失败 255-其他原因
    private Object recMsg;//执行结果信息
    private Boolean timeOut;//超时标志
    private LocalDateTime createTime;//创建时间 异常缓存没清时 以免启动不了
    private String serialNum;//流水号

}
