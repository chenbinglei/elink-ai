package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.NodeLogInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NodeLogInfoDao extends JpaRepository<NodeLogInfoEntity, String>, JpaSpecificationExecutor<NodeLogInfoEntity> {

    /**
     * 查询指定
     * @param nodeId
     * @param startTimme
     * @param endTime
     * @return
     */
    List<NodeLogInfoEntity> findAllByNodeIdAndTsTimeBetween(String nodeId, String startTimme, String endTime);

    /**
     * 删除指定节点全部日志
     * @param nodeId
     */
    void deleteAllByNodeId(String nodeId);
}
