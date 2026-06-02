package com.sunmax.common.vo.protocol.mqtt.web.command;

import lombok.Data;

import java.util.List;

/**
 * 控制板信息请求
 */
@Data
public class CmdToPoFwResPublishVo {

    private List<String> pilesCode;
}
