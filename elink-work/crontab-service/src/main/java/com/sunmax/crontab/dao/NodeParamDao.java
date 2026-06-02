package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.NodeParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NodeParamDao extends JpaRepository<NodeParamEntity, String>, JpaSpecificationExecutor<NodeParamEntity> {

    /**
     * 查询指定节点下的参数信息
     * @param nodeId
     * @return
     */
    List<NodeParamEntity> findAllByNodeId(String nodeId);

    /**
     * 删除指定节点所有参数信息
     * @param nodeId
     */
    void deleteAllByNodeId(String nodeId);

    /**
     * 根据多个节点id查询节点参数信息
     * @param nodeIdList
     * @return
     */
    List<NodeParamEntity> findAllByNodeIdIn(List<String> nodeIdList);
}
