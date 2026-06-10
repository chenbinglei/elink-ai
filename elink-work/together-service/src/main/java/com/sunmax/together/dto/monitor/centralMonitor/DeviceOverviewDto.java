package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "设备概览信息返回实体类")
public class DeviceOverviewDto {

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String typeName;

    /**
     * 设备数据列表
     */
    @Schema(description = "设备数据列表")
    private List<DeviceDto> deviceList = Lists.newArrayList();

}
