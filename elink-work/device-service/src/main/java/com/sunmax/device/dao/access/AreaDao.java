package com.sunmax.device.dao.access;


import com.sunmax.device.entity.access.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AreaDao extends JpaRepository<AreaEntity,Long>, JpaSpecificationExecutor<AreaEntity> {

    //根据多个区域id获取区域数据
    List<AreaEntity> findAllByAreaIdIn(List<String> areaIds);

    //根据区域id获取区域数据
    AreaEntity findByAreaId(String areaId);

    /**
     * 根据多个市级编码id查询下面区县级数据
     * @param cityId
     * @return
     */
    List<AreaEntity> findAllByCityIdIn(List<String> cityId);
}
