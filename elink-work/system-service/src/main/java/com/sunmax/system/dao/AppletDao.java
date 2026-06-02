package com.sunmax.system.dao;


import com.sunmax.system.entity.AppletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppletDao extends JpaRepository<AppletEntity, String>, JpaSpecificationExecutor<AppletEntity> {

}
