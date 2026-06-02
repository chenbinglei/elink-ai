package com.sunmax.device.dao.access;

import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.entity.access.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface DeviceDao extends JpaRepository<DeviceEntity, String>, JpaSpecificationExecutor<DeviceEntity> {

    //根据站点id查询设备数据
    List<DeviceEntity> findAllBySiteIdAndIsDelete(String siteId, Integer isDelete);
    
    //根据多个模型id查询设备数据
    List<DeviceEntity> findAllByModelIdInAndIsDelete(Set<String> modelIds, Integer isDelete);

    //根据模型id删除设备数据
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    void deleteAllByModelIdAndIsDelete(String modelId, Integer isDelete);

    //根据多个站点id和接入类型查询设备数据
    List<DeviceEntity> findAllBySiteIdInAndAccessTypeNotAndIsDelete(Set<String> siteIds, Integer accessType, Integer isDelete);

    //根据多个主键id查询设备数据
    List<DeviceEntity> findAllByIdInAndIsDelete(Collection<String> ids, Integer isDelete);

    //根据设备名称查询设备数据
    List<DeviceEntity> findAllByDeviceNameAndIsDelete(String deviceName, Integer isDelete);

    //根据设备序列号查询设备数据
    List<DeviceEntity> findAllByDeviceNumberAndIsDelete(String deviceNumber, Integer isDelete);

    //根据接入类型查询设备数据
    List<DeviceEntity> findAllByAccessTypeAndIsDelete(Integer accessType, Integer isDelete);

    /**
     * 根据多个站点id查询设备数据
     * @param siteIdList
     * @param isDelete
     * @return
     */
    List<DeviceEntity> findAllBySiteIdInAndIsDelete(Collection<String> siteIdList, Integer isDelete);

    //根据多个设备编码查询正常状态设备数据
    List<DeviceEntity> findAllByDeviceNumberInAndIsDelete(List<String> deviceCodeList, Integer isDelete);

    /**
     * 查询所有设备数据
     * @param isDelete
     * @return
     */
    List<DeviceEntity> findAllByTypeIdInAndIsDelete(Collection<String> typeIds, Integer isDelete);

    /**
     * 根据多个父级id查询设备数据
     * @param parentIdList
     * @param isDelete
     * @return
     */
    List<DeviceEntity> findAllByParentIdInAndIsDelete(List<String> parentIdList, Integer isDelete);
}
