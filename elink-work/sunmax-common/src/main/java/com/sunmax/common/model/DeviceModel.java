package com.sunmax.common.model;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash
@ApiModel(value = "DeviceModel", description = "设备模型缓存实体类")
public class DeviceModel {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private String modelId;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus;

    /**
     * 功能点数据
     */
    @ApiModelProperty(value = "功能点数据")
    private Map<String, FunctionModel> functionMap = Maps.newConcurrentMap();

    /**
     * 通道数据
     */
    @ApiModelProperty(value = "通道数据")
    private Map<String, ChannelModel> channelMap = Maps.newConcurrentMap();

}
