package com.sunmax.webapp.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppOrderListQueryVo;
import com.sunmax.webapp.dto.AppOrderListDto;
import com.sunmax.webapp.service.MyOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("myOrder")
@Api(tags = "订单管理")
public class MyOrderController {

    @Autowired
    private MyOrderService myOrderService;

    @PostMapping(value = "queryOrderDetailByOrderNum")
    @ApiOperation("根据订单编号查询订单详情信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<OrderDetailDto> queryOrderDetailByOrderNum(String orderNum) {
        return myOrderService.queryOrderDetailByOrderNum(orderNum);
    }

    @PostMapping(value = "queryMyOrderListByPage")
    @ApiOperation("查询我的订单列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<AppOrderListDto>> queryMyOrderListByPage(AppOrderListQueryVo appOrderListQueryVo) {
        return myOrderService.queryMyOrderListByPage(appOrderListQueryVo);
    }
}
