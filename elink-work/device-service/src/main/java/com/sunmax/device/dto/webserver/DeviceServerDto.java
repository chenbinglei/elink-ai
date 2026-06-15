package com.sunmax.device.dto.webserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "设备服务数据返回实体类")
public class DeviceServerDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备字段数据列表
     */
    @Schema(description = "设备字段数据列表")
    private List<FieldDataDto> fieldDataList = Lists.newArrayList();

}
