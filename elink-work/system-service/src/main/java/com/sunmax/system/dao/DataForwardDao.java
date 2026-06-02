package com.sunmax.system.dao;

import com.sunmax.system.entity.DataForwardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DataForwardDao extends JpaRepository<DataForwardEntity, String>, JpaSpecificationExecutor<DataForwardEntity> {

    //根据协议编号查询数据转发数据
    List<DataForwardEntity> findAllByProtocolCode(String protocolCode);

}
