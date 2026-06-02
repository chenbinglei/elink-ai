package com.sunmax.webapp.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppOrderListQueryVo;
import com.sunmax.webapp.dto.AppOrderListDto;

public interface MyOrderService {

    /**
     * 根据订单编号查询订单详情信息
     * @param orderNum
     * @return
     */
    ResponseResult<OrderDetailDto> queryOrderDetailByOrderNum(String orderNum);

    /**
     * 查询我的订单列表
     * @param appOrderListQueryVo
     * @return
     */
    ResponseResult<PageDto<AppOrderListDto>> queryMyOrderListByPage(AppOrderListQueryVo appOrderListQueryVo);
}
