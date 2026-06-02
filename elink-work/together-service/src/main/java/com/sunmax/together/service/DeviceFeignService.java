package com.sunmax.together.service;

import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface DeviceFeignService {

    /**
     * 根据多个站点id和时间查询站点订单列表数据
     *
     * @param siteIds   多个站点id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 站点id -> 订单统计列表
     */
    ResponseResult<Map<String, List<OrderCountDto>>> findOrderRecordListBySiteIds(List<String> siteIds, String startTime, String endTime);

    /**
     * 根据多个充电桩编码和时间查询充电桩订单列表数据
     *
     * @param pileCodes 多个充电桩编码
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 充电桩编码 -> 订单统计列表
     */
    ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(List<String> pileCodes, String startTime, String endTime);

    /**
     * 根据多个充电桩编码和时间查询充电桩订单列表数据
     *
     * @param pileCodes 充电桩编码
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dateType  时间类型 1-日 2-月 3-年
     * @return 日期 -> 订单统计列表
     */
    ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(List<String> pileCodes, String startTime, String endTime, Integer dateType);

}
