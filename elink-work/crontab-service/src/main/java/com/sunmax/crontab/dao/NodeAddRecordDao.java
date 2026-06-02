package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.NodeAddRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NodeAddRecordDao extends JpaRepository<NodeAddRecordEntity, String>, JpaSpecificationExecutor<NodeAddRecordEntity> {

    /**
     * 查询指定设备下的节点补录数据
     * @param deviceId
     * @return
     */
    List<NodeAddRecordEntity> findAllByDeviceId(String deviceId);
}
