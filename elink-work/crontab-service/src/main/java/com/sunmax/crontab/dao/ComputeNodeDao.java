package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.ComputeNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author: yqz
 * @Date: 2023/6/515:22
 * @version: 1.0
 * @注释:
 */
public interface ComputeNodeDao extends JpaRepository<ComputeNodeEntity, String>, JpaSpecificationExecutor<ComputeNodeEntity> {

    /**
     * 查询指定站点/设备下计算节点数据
     * @param deviceId
     * @return
     */
    List<ComputeNodeEntity> findAllByDeviceId(String deviceId);

    //根据多个设备id查询计算节点数据
    List<ComputeNodeEntity> findAllByDeviceIdIn(List<String> deviceIds);

    List<ComputeNodeEntity> findAllByDeviceIdAndNodeCodeIn(String deviceId, Set<String> nodeCodes);

    /**
     * 根据站点/设备和实例类型查询计算节点数据
     * @param deviceId
     * @param exampleType
     * @return
     */
    List<ComputeNodeEntity> findAllBySiteIdAndExampleType(String deviceId, Integer exampleType);

    /**
     * 查询指定站点下所有计算节点数据
     * @param siteId
     * @return
     */
    List<ComputeNodeEntity> findAllBySiteId(String siteId);

    /**
     * 查询数据库中最新的一个存储id
     * @return
     */
    @Query(nativeQuery = true, value = "select MAX(storage_id) from b_compute_node")
    Integer findNodeStorageId();

    //根据站点id和多个节点编码查询计算节点数据
    List<ComputeNodeEntity> findAllBySiteIdAndNodeCodeIn(String siteId, Collection<String> nodeCode);

    //根据节点id查询计算节点id
    @Query(nativeQuery = true, value = "select id from b_compute_node where storage_id = ?1")
    String findIdByStorageId(Long storageId);
}
