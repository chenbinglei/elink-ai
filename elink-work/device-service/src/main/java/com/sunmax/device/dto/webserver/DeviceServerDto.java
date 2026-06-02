package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "DeviceServerDto", description = "设备服务数据返回实体类")
public class DeviceServerDto {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 设备字段数据列表
     */
    @ApiModelProperty(value = "设备字段数据列表")
    private List<FieldDataDto> fieldDataList = Lists.newArrayList();

}
