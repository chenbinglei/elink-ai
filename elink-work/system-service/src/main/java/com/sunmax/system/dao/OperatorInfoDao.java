package com.sunmax.system.dao;

import com.sunmax.system.entity.OperatorInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OperatorInfoDao extends JpaRepository<OperatorInfoEntity, String>, JpaSpecificationExecutor<OperatorInfoEntity> {

    //根据多个运营商id查询运营商信息
    List<OperatorInfoEntity> findAllByOperatorIdIn(List<String> operatorIds);
}
