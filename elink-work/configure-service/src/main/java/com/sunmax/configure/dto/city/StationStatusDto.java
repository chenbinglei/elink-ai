package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:29
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电站状态实体类")
public class StationStatusDto {

    /**
     * 充电站编码
     */
    @Schema(description = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 接口状态列表
     */
    @Schema(description = "接口状态列表")
    @JSONField(name = "ConnectorStatusInfos")
    private List<ConnectorStatusDto> connectorStatusInfos;
}
