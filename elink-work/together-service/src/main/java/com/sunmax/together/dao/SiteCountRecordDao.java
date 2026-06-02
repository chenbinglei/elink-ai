package com.sunmax.together.dao;

import com.sunmax.together.entity.SiteCountRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
public interface SiteCountRecordDao extends JpaRepository<SiteCountRecordEntity, String>, JpaSpecificationExecutor<SiteCountRecordEntity> {

    /**
     * 根据站点id和统计日期查询站点统计记录
     * @param siteIds 多个站点id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点统计记录数据
     */
    List<SiteCountRecordEntity> findAllBySiteIdInAndCountDateBetween(List<String> siteIds, LocalDate startDate, LocalDate endDate);
}
