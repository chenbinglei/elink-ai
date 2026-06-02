package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.PointTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface PointTableDao extends JpaRepository<PointTableEntity, String>, JpaSpecificationExecutor<PointTableEntity> {

    //根据通道id查询点表数据
    List<PointTableEntity> findAllByChannelId(String channelId);

    //根据多个设备id查询点表数据
    List<PointTableEntity> findAllByDeviceIdIn(Set<String> deviceIds);

}
