package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.ChargerPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChargerPriceDao extends JpaRepository<ChargerPriceEntity, String>, JpaSpecificationExecutor<ChargerPriceEntity> {

    //根据多个价格id查询价格配置信息
    List<ChargerPriceEntity> findAllByPriceIdIn(List<String> pirceIdList);

    //根据价格id删除所有价格配置信息
    void deleteAllByPriceId(String pirceId);
}
