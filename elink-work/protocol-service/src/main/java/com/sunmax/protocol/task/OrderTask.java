package com.sunmax.protocol.task;

import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.protocol.dao.OrderRecordDao;
import com.sunmax.protocol.entity.OrderRecordEntity;
import com.sunmax.protocol.util.PileRecordUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class OrderTask {

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateOrderStatus() {

        //查询充电中和预约中的订单
        List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByOrderStatusIn(Arrays.asList(0, 1, 6));
        if (CollectionUtils.isNotEmpty(orderRecordList)) {
            orderRecordList = orderRecordList.stream().filter(order -> {
                Integer gunCode = order.getGunCode();
                if (StringUtil.isNotEmpty(order.getPileCode()) && StringUtil.isNotEmpty(gunCode)) {
                    switch (order.getOrderStatus()) {
                        case 0:
                            //未进行中的订单
                            return this.checkOrderStatus(order.getOrderNum(), order.getPileCode(), gunCode);
                        case 1:
                            //充电中的订单
                            return this.checkOrderStatus(order.getOrderNum(), order.getPileCode(), gunCode);
                        case 6:
                            //预约中的订单
                            if (StringUtil.isNotEmpty(order.getClockingTime())) {
                                if (this.checkOrderStatus(order.getOrderNum(), order.getPileCode(), gunCode)) {
                                    return true;
                                }
                                LocalDateTime clockingTime = DateUtil.strToLocalDateTime(order.getClockingTime());
                                //往前推30秒
                                return clockingTime.isBefore(LocalDateTime.now().minusSeconds(30));
                            }
                    }
                }
                return false;
            }).peek(order -> order.setOrderStatus(4)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orderRecordList)) {
                //对订单挂起的数据创建补单记录
                PileRecordUtil.batchAddRepairOrderRecord(orderRecordList);
                orderRecordDao.batchUpdate(orderRecordList);
            }
        }

        //查询订单挂起的订单
        List<OrderRecordEntity> orderHangRecordList = orderRecordDao.findAllByOrderStatusAndUpdateTimeBetween(4, LocalDateTime.now().minusDays(2), LocalDateTime.now());
        if (CollectionUtils.isNotEmpty(orderHangRecordList)) {
            //定义电枪状态(充电中,放电中)
            List<Integer> gunStatusList = Arrays.asList(2, 5);
            //检测订单挂起订单的状态的桩是否是运行中
            orderHangRecordList = orderHangRecordList.stream().filter(order -> {
                Integer gunCode = order.getGunCode();
                if (StringUtil.isNotEmpty(order.getPileCode()) && StringUtil.isNotEmpty(order.getGunCode())) {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(order.getPileCode());
                    if (pileRealModel != null && pileRealModel.getWorkStatus() != 88) {
                        if (pileRealModel.getGunRealModelMap().containsKey(String.valueOf(gunCode))) {
                            PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(String.valueOf(gunCode));
                            if (gunStatusList.contains(gunRealModel.getGunStatus())) {
                                return StringUtil.isNotEmpty(gunRealModel.getSerialNum()) && Objects.equals(gunRealModel.getSerialNum(), order.getOrderNum());
                            }
                        }
                    }
                }
                return false;
            }).peek(order -> order.setOrderStatus(1)).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(orderHangRecordList)) {
            //对订单挂起的数据修改补单记录
            PileRecordUtil.batchUpdateRepairOrderRecord(orderHangRecordList);
            orderRecordDao.batchUpdate(orderHangRecordList);
        }
    }

    private Boolean checkOrderStatus(String orderNum, String pileCode, Integer gunCode) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null) {
            if (pileRealModel.getWorkStatus() == 88) {
                return true;
            }
            if (pileRealModel.getGunRealModelMap().containsKey(String.valueOf(gunCode))) {
                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(String.valueOf(gunCode));
                if (Objects.equals(gunRealModel.getGunStatus(), 7)) {
                    return StringUtil.isNotEmpty(gunRealModel.getSerialNum()) && !Objects.equals(gunRealModel.getSerialNum(), orderNum);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

}
