package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChannelDao extends JpaRepository<ChannelEntity, String>, JpaSpecificationExecutor<ChannelEntity> {

    //根据设备id查询通道信息数据
    List<ChannelEntity> findAllByDeviceId(String deviceId);

    //根据设备id和通道名称查询通道信息数据
    List<ChannelEntity> findAllByDeviceIdAndChannelName(String deviceId, String channelName);

    //根据设备id和协议类型查询通道信息数据
    List<ChannelEntity> findAllByDeviceIdAndProtocolType(String deviceId, String protocolType);

}
