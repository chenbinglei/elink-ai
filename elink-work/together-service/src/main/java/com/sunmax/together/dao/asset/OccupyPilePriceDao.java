package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.OccupyPilePriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OccupyPilePriceDao extends JpaRepository<OccupyPilePriceEntity, String>, JpaSpecificationExecutor<OccupyPilePriceEntity> {

    //根据站点id查询相关占桩价格信息
    List<OccupyPilePriceEntity> findBySiteIdAndIsDelete(String siteId, Integer isDelete);

    //根据多个站点id查询占桩计费信息
    List<OccupyPilePriceEntity> findAllBySiteIdInAndIsDelete(List<String> siteIdList, Integer isDetele);
}
