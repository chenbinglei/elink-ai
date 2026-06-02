package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.VariableNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VariableNodeDao extends JpaRepository<VariableNodeEntity, String>, JpaSpecificationExecutor<VariableNodeEntity> {

    /**
     * 根据多个变量id查询关联实例数据
     * @param varIdList
     * @return
     */
    List<VariableNodeEntity> findAllByVarIdIn(List<String> varIdList);

    /**
     * 删除指定变量关联的所有实例数据
     * @param varId
     */
    void deleteAllByVarId(String varId);

    /**
     * 根据节点id删除关联变量信息
     * @param nodeId
     */
    void deleteAllByNodeId(String nodeId);

    /**
     * 根据多个变量id和模型id查询已关联指定模型的数据
     * @param modelId
     * @param varIdList
     * @return
     */
    List<VariableNodeEntity> findAllByDeviceIdAndVarIdIn(String modelId, List<String> varIdList);

    List<VariableNodeEntity> findAllByDeviceId(String deviceId);

    List<VariableNodeEntity> findAllByDeviceIdIn(List<String> deviceIdList);
}
