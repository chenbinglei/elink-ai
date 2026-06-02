package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.SystemVariableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface SystemVariableDao extends JpaRepository<SystemVariableEntity, String>, JpaSpecificationExecutor<SystemVariableEntity> {

    //根据多个变量编码查询指定实例下关联的变量
    List<SystemVariableEntity> findAllByVarCodeIn(List<String> varCodeList);

    //根据多个变量编码查询指定实例下关联的变量
    List<SystemVariableEntity> findAllByVarCodeInAndVarTypeAndDataSource(Set<String> varCodeList, Integer varType, Integer dataSource);
}
