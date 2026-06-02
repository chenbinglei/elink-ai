package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteDao extends JpaRepository<SiteEntity, String>, JpaSpecificationExecutor<SiteEntity> {

    /**
     * 根据删除状态查询所有站点数据
     * @param isDelete
     * @return
     */
    List<SiteEntity> findAllByIsDelete(int isDelete);

    List<SiteEntity> findAllByIdInAndIsDelete(List<String> siteIdList, int isDelete);
}
