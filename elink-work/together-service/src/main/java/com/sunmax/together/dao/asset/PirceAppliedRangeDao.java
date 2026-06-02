package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.PirceAppliedRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PirceAppliedRangeDao extends JpaRepository<PirceAppliedRangeEntity, String>, JpaSpecificationExecutor<PirceAppliedRangeEntity> {

    //根据价格id查询应用范围数据
    List<PirceAppliedRangeEntity> findAllByPriceId(String pirceId);
}
