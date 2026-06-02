package com.sunmax.device.dao.access;


import com.sunmax.device.entity.access.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface ProvinceDao extends JpaRepository<ProvinceEntity,Long>, JpaSpecificationExecutor<ProvinceEntity> {

    //根据多个省编号获取省数据
    List<ProvinceEntity> findAllByProvinceIdIn(List<String> provinceIds);

    //根据省id获取省名称
    ProvinceEntity findByProvinceId(String provinceId);
}
