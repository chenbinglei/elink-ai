package com.sunmax.protocol.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotNull;

/**
 * 请求结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PlatformRequestVo {

    /**
     * 平台标识
     */
    @NotNull(message="platformId不能为空！")
    @JSONField(name = "platformId")
    @JsonProperty("platformId")
    private String platformId;

    /**
     * 数据
     */
    @NotNull(message="data不能为空！")
    @JSONField(name = "data")
    @JsonProperty("data")
    private String data;

}
