package com.sunmax.device.dao.access;


import com.sunmax.device.entity.access.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CityDao extends JpaRepository<CityEntity,Long>, JpaSpecificationExecutor<CityEntity> {

    //根据市编号获取市数据
    List<CityEntity> findAllByCityIdIn(List<String> cityIds);

    CityEntity findByCityId(String cityId);

    /**
     * 根据多个省份编码id查询下面市级数据
     * @param provinceId
     * @return
     */
    List<CityEntity> findAllByProvinceIdIn(List<String> provinceId);
}
