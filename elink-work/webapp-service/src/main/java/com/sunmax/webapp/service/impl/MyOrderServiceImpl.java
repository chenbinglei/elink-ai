package com.sunmax.webapp.service.impl;

import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.dto.webapp.OrderInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.webapp.AppOrderListQueryVo;
import com.sunmax.webapp.dto.AppOrderListDto;
import com.sunmax.webapp.service.MyOrderService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DoubleUtil.getToBigDecimal;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Slf4j
@Service
public class MyOrderServiceImpl implements MyOrderService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private SystemService systemService;

    /**
     * 根据订单编号查询订单详情信息
     *
     * @param orderNum
     * @return
     */
    @Override
    public ResponseResult<OrderDetailDto> queryOrderDetailByOrderNum(String orderNum) {
        return togetherService.findOrderDetailByOrderNum(orderNum);
    }

    /**
     * 查询我的订单列表
     *
     * @param appOrderListQueryVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<AppOrderListDto>> queryMyOrderListByPage(AppOrderListQueryVo appOrderListQueryVo) {
        List<AppOrderListDto> resultList = Lists.newArrayList();

        //根据小程序登录标识查询站点id列表
        ResponseResult<AppletDto> appletByAppletCode = systemService.findAppletByAppletCode(appOrderListQueryVo.getAppletKey());
        if (appletByAppletCode.isSuccess() && StringUtil.isNotEmpty(appletByAppletCode.getData()) && !appletByAppletCode.getData().getTenantSiteIdMap().isEmpty()) {
            //有权限查看的站点id列表
            List<String> siteIdList = appletByAppletCode.getData().getTenantSiteIdMap().values().stream().flatMap(Set::stream).collect(Collectors.toList());
            //根据小程序用户id查询小程序手机号
            AppletUserInfoDto appletUserInfo = togetherService.findAppletUserByIds(Collections.singleton(appOrderListQueryVo.getAppletUserId()))
                    .getData().get(appOrderListQueryVo.getAppletUserId());
            if (appletUserInfo != null) {
                //根据多个站点id查询订单信息
                ResponseResult<List<OrderInfoDto>> orderListBySiteId = togetherService.findOrderListBySiteIdS(siteIdList, appletUserInfo.getPhoneNum(), appOrderListQueryVo.getOrderType());
                if (orderListBySiteId.isSuccess() && CollectionUtils.isNotEmpty(orderListBySiteId.getData())) {
                    List<OrderInfoDto> orderInfoDtoList = orderListBySiteId.getData();
                    //根据创建时间过滤
                    if (StringUtil.isNotEmpty(appOrderListQueryVo.getStartTime()) && StringUtil.isNotEmpty(appOrderListQueryVo.getEndTime())) {
                        orderInfoDtoList = orderInfoDtoList.stream().filter(o -> o.getCreateTime().isAfter(strToLocalDateTime(appOrderListQueryVo.getStartTime())) && o.getCreateTime().isBefore(strToLocalDateTime(appOrderListQueryVo.getEndTime()))).collect(Collectors.toList());
                    }
                    //根据订单状态查询
                    if (StringUtil.isNotEmpty(appOrderListQueryVo.getOrderLogo())) {
                        orderInfoDtoList = orderInfoDtoList.stream().filter(o -> Objects.equals(o.getOrderStatus(), appOrderListQueryVo.getOrderLogo())).collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(orderInfoDtoList)) {
                        //电桩实时数据
                        Map<String, PileRealModel> pileRealModelMap = Maps.newHashMap();
                        //根据多个电桩编码查询电桩实时数据
                        List<PileRealModel> pileRealModelList = getPileRealModelList(orderInfoDtoList.stream().map(OrderInfoDto::getPileCode).collect(Collectors.toList()));
                        if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                            pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, Function.identity(), (k1, k2) -> k1));
                        }
                        Map<String, PileRealModel> finalPileRealModelMap = pileRealModelMap;
                        resultList = orderInfoDtoList.stream().map(orderInfoDto -> {
                            AppOrderListDto appOrderListDto = new AppOrderListDto();
                            BeanUtils.copyProperties(orderInfoDto, appOrderListDto);
                            String startTime = orderInfoDto.getStartTime();
                            String endTime = orderInfoDto.getEndTime();
                            if (StringUtil.isNotEmpty(orderInfoDto.getOrderStatus()) && orderInfoDto.getOrderStatus() == 1) {
                                endTime = localDateTimeToStr(LocalDateTime.now());
                                //根据订单状态判断，如果是在进行充电中的订单，则取实时状态中的数据
                                if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(orderInfoDto.getPileCode())) {
                                    PileRealModel pileRealModel = finalPileRealModelMap.get(orderInfoDto.getPileCode());
                                    if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                                        if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(String.valueOf(orderInfoDto.getGunCode()))) {
                                            PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(String.valueOf(orderInfoDto.getGunCode()));
                                            appOrderListDto.setTotalQt(getToDouble(gunRealModel.getTotalQt()));
                                            appOrderListDto.setTotalCost(gunRealModel.getTotalCost() != null ? getToBigDecimal(gunRealModel.getTotalCost()) : null);
                                        }
                                    }
                                }
                            }
                            //根据开始结束时间计算订单持续了多长时间
                            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                                appOrderListDto.setChargeDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
                            }
                            return appOrderListDto;
                        }).collect(Collectors.toList());
                        resultList = resultList.stream().sorted(Comparator.comparing(AppOrderListDto::getCreateTime).reversed()).collect(Collectors.toList());
                    }
                }
            }
        }

        return ResponseResult.ok(new PageDto<>(resultList, appOrderListQueryVo.getPage(), appOrderListQueryVo.getSize()));
    }
}
