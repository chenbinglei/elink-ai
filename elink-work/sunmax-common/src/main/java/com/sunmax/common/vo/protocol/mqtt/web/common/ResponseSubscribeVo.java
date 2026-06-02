package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * response主题工具类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSubscribeVo implements Serializable {

    private String cmd;

    private Object paras;

}
