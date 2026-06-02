package com.sunmax.device.dao.access;

import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.entity.access.DeviceTopologyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface DeviceTopologyDao extends JpaRepository<DeviceTopologyEntity, String>, JpaSpecificationExecutor<DeviceTopologyEntity> {

    //根据左节点设备id和多个节点id查询关联节点数据
    List<DeviceTopologyEntity> findAllByLeftDeviceIdAndLeftNodeIdIn(String deviceId, Set<String> nodeIds);

    //根据右节点设备id和多个右节点id查询关联节点数据
    List<DeviceTopologyEntity> findAllByRightDeviceIdAndRightNodeIdIn(String deviceId, Set<String> nodeIds);

    //根据设备id删除关联节点数据
    void deleteAllByLeftDeviceIdOrRightDeviceId(String leftDeviceId, String rightDeviceId);

    //根据多个节点id删除设备关联节点数据
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    void deleteAllByLeftNodeIdInOrRightNodeIdIn(Set<String> leftNodeIds, Set<String> rightNodeIds);

}
