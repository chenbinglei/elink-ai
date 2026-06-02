package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface SiteInfoDao extends JpaRepository<SiteInfoEntity, String>, JpaSpecificationExecutor<SiteInfoEntity> {

    //查询指定伪删除状态所有站点数据
    List<SiteInfoEntity> findAllByIsDelete(Integer isDetele);

    /**
     * 根据多个站点id，查询指定伪删除状态数据
     * @param siteIdList
     * @param isDetele
     * @return
     */
    List<SiteInfoEntity> findAllByIdInAndIsDelete(Collection<String> siteIdList, Integer isDetele);

    //根据站点编码查询站点数据
    SiteInfoEntity findBySiteCodeAndIsDelete(String siteCode, int isDelete);

    //根据站点名称查询站点数据
    SiteInfoEntity findBySiteNameAndIsDelete(String siteCode, int isDelete);
}
