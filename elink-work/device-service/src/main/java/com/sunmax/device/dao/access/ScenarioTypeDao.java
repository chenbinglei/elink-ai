package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.ScenarioTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface ScenarioTypeDao extends JpaRepository<ScenarioTypeEntity, String>, JpaSpecificationExecutor<ScenarioTypeEntity> {

    //根据多个站点id查询能源场景信息
    List<ScenarioTypeEntity> findAllBySiteIdIn(Collection<String> siteIdList);
}
