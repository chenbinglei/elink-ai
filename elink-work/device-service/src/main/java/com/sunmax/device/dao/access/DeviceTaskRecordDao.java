package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.DeviceTaskRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface DeviceTaskRecordDao extends JpaRepository<DeviceTaskRecordEntity, String>, JpaSpecificationExecutor<DeviceTaskRecordEntity> {

    //根据多个任务id查询设备任务记录数据
    List<DeviceTaskRecordEntity> findAllByTaskIdIn(List<String> taskId);

    //根据任务id和多个设备编号查询设备任务记录
    List<DeviceTaskRecordEntity> findAllByTaskIdAndDeviceNumberIn(String taskId, Set<String> deviceNumbers);

}
