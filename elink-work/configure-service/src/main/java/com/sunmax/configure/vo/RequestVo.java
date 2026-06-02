package com.sunmax.configure.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * 请求结果返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RequestVo {

    /**
     * 运营商标识
     */
    @NotNull(message="OperatorID不能为空！")
    @JSONField(name = "OperatorID")
    @JsonProperty("OperatorID")
    private String operatorId;

    /**
     * 数据
     */
    @NotNull(message="Data不能为空！")
    @JSONField(name = "Data")
    @JsonProperty("Data")
    private String data;

    /**
     * 时间戳 格式为yyyyMMddHHmmss
     */
    @NotNull(message="TimeStamp不能为空！")
    @JSONField(name = "TimeStamp")
    @JsonProperty("TimeStamp")
    private String timeStamp;

    /**
     * 自增序列 4 位自增序列取自时间戳， 同一秒内按序列自增长， 新秒重计。 如 0001
     */
    @NotNull(message="Seq不能为空！")
    @JSONField(name = "Seq")
    @JsonProperty("Seq")
    private String seq;

    /**
     * 参数签名
     */
    @JSONField(name = "Sig")
    @JsonProperty("Sig")
    private String sig;

}
