package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.DeviceGunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface DeviceGunDao extends JpaRepository<DeviceGunEntity, String>, JpaSpecificationExecutor<DeviceGunEntity> {

    //根据设备id和枪编号查询枪数据
    List<DeviceGunEntity> findAllByDeviceIdAndGunCode(String deviceId, String gunCode);

    //根据设备id查询模型枪数据
    List<DeviceGunEntity> findAllByDeviceId(String deviceId);

    //根据多个设备id查询相关电枪模型数据
    List<DeviceGunEntity> findAllByDeviceIdIn(Collection<String> deviceIds);

    //根据多个设备id删除电枪信息
    void deleteAllByDeviceIdIn(List<String> deviceIdList);
}
