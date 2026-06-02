package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.OccupyPileRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OccupyPileRecordDao extends JpaRepository<OccupyPileRecordEntity, String>, JpaSpecificationExecutor<OccupyPileRecordEntity> {

    /**
     * 根据多个电桩编码查询占桩订单记录数据
     * @param pileCodeList
     * @return
     */
    List<OccupyPileRecordEntity> findAllByPileCodeIn(List<String> pileCodeList);

    //根据多个订单记录id查询指定时间段内的占桩订单
    List<OccupyPileRecordEntity> findAllByOrderIdInAndStartTimeBetween(List<String> orderIdList, String startTime, String endTime);

    //根据多个订单记录id查询相关占桩记录
    List<OccupyPileRecordEntity> findAllByOrderIdIn(List<String> orderIdList);

    //根据多个电桩编码查询指定时段内的占桩订单
    List<OccupyPileRecordEntity> findAllByPileCodeInAndStartTimeBetween(List<String> pileCodeList, String startTime, String endTime);
}
