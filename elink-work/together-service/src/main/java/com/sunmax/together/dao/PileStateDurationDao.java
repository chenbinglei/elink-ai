package com.sunmax.together.dao;

import com.sunmax.together.entity.PileStateDurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PileStateDurationDao extends JpaRepository<PileStateDurationEntity, String>, JpaSpecificationExecutor<PileStateDurationEntity> {

    /**
     * 查询指定日期电枪状态数据
     * @param dayDate
     * @return
     */
    List<PileStateDurationEntity> findAllByCountDate(String dayDate);

    //根据多个电桩编码查询指定时间段内的电枪状态数据
    List<PileStateDurationEntity> findAllByPileCodeInAndCountDateBetween(List<String> pileCodeList, String startDate, String endDate);

    List<PileStateDurationEntity> findAllBySiteIdInAndCountDateBetween(List<String> siteIdList, String startDate, String endDate);

    List<PileStateDurationEntity> findAllByPileCodeInAndCountDateBetweenAndWorkState(List<String> pileCodeList, String startDate, String endDate, Integer workState);
}
