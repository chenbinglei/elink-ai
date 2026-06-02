package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.DeviceTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DeviceTaskDao extends JpaRepository<DeviceTaskEntity, String>, JpaSpecificationExecutor<DeviceTaskEntity> {

    //根据多个任务状态查询设备任务数据
    List<DeviceTaskEntity> findAllByTaskStatusIn(List<Integer> taskStatusList);

}
