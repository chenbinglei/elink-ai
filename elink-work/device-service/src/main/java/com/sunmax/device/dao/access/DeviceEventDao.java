package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.DeviceEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DeviceEventDao extends JpaRepository<DeviceEventEntity, String>, JpaSpecificationExecutor<DeviceEventEntity> {

    //根据多个设备id和事件状态查询设备事件数据
    List<DeviceEventEntity> findAllByDeviceIdIn(Iterable<String> deviceIds);

    //根据多个设备id和事件状态查询设备事件数据
    List<DeviceEventEntity> findAllByDeviceIdInAndEventStatus(Iterable<String> deviceIds, Integer eventStatus);

}
