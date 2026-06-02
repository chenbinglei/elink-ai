package com.sunmax.crontab.vo.mqtt;

import lombok.Data;

/**
 * 数据采集
 * 通⽤信息说明
 * topic
 * 所有类型的数据采集统⼀采⽤相同的topic：/[vendor]/[GW_SN]/service
 * vendor为⼚商标识，⽤于区分不同的接⼊⼚商
 * GW_SN为可以唯⼀标识负控终端的编码，⼀般为序列号
 * 上报频率为1分钟上报1次
 */
@Data
public class HDTopicVo {

    /**
     * 云边对接情况下，代表下挂设备的唯⼀标识，⼀般为序列号。
     * 保持⼀致。
     */
    private String sn;

    /**
     * 采样时间，13位时间戳精确到毫秒
     */
    private Long time;

    /**
     * 服务标识
     * 负荷用电信息采集的标识为elecInfoAcq
     * 充电站信息采集的标识为chgStatInfoAcq
     * 储能信息采集的标识为esInfoAcq
     * 光伏站信息采集的标识为pvInfoAcq
     */
    private String identifier;

    /**
     * 指标名称和指标值的集合
     */
    private Object tags;

}
