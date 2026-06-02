package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.OccupyPileRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.order.OrderRecordExportDto;
import com.sunmax.together.dto.operation.order.*;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.vo.operation.orderRecord.OccupyPileRecordListQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRecordQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRefundVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("order")
@Api(tags = "订单管理")
public class OrderRecordController {

    @Autowired
    private OrderRecordService orderRecordService;

    @PostMapping("findOrderRecordListByPage")
    @ApiOperation("分页查询订单记录列表信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<OrderRecordListDto> findOrderRecordListByPage(OrderRecordQueryVo orderRecordQueryVo) {
        return orderRecordService.findOrderRecordListByPage(orderRecordQueryVo);
    }

    @PostMapping("findOrderRecordInfoById")
    @ApiOperation("根据订单id查询基本信息")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "String", required = true)
    public ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(String orderId) {
        return orderRecordService.findOrderRecordInfoById(orderId);
    }

    @PostMapping("findOccupyPileRecordListByPage")
    @ApiOperation("分页查询占桩订单记录列表信息")
    @ApiOperationSupport(order = 3)
    public ResponseResult<OccupyPileRecordListDto> findOccupyPileRecordListByPage(OccupyPileRecordListQueryVo occupyPileRecordListQueryVo, String userId) {
        return orderRecordService.findOccupyPileRecordListByPage(occupyPileRecordListQueryVo, userId);
    }

    @PostMapping("findOccupyPileRecordInfoById")
    @ApiOperation("根据占桩订单id查询基本信息")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "occupyId", value = "占桩订单id", dataType = "String", required = true)
    public ResponseResult<OccupyPileRecordInfoDto> findOccupyPileRecordInfoById(String occupyId) {
        return orderRecordService.findOccupyPileRecordInfoById(occupyId);
    }

    @PostMapping("findOperatorListByUserId")
    @ApiOperation("查询运营商下拉列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "userId", value = "当前登录用户id", dataType = "String", required = true)
    public ResponseResult<List<AffiliatesInfoDto>> findOperatorListByUserId(String userId) {
        return orderRecordService.findOperatorListByUserId(userId);
    }

    @PostMapping("findSiteBasicInfoByTenantId")
    @ApiOperation("查询站点下拉列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "tenantId", value = "当前登录用户所属租户id", dataType = "String", required = true)
    public ResponseResult<List<SiteInfoDto>> findSiteBasicInfoByTenantId(String tenantId, String userId) {
        return orderRecordService.findSiteBasicInfoByTenantId(tenantId, userId);
    }

    @PostMapping("findOrderRecordListByPileCodes")
    @ApiOperation("根据多个充电桩编码查询订单充放电记录")
    @ApiOperationSupport(order = 7)
    public ResponseResult<List<OrderRecordDto>> findOrderRecordListByPileCodes(List<String> pileCodeList, String startTime, String endTime) {
        return orderRecordService.findOrderRecordListByPileCodes(pileCodeList, startTime, endTime);
    }

    @PostMapping("findOccupyPileRecordListByOrderIds")
    @ApiOperation("根据多个订单记录id查询占桩订单记录")
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<OccupyPileRecordDto>> findOccupyPileRecordListByOrderIds(List<String> orderIdList, String startTime, String endTime) {
        return orderRecordService.findOccupyPileRecordListByOrderIds(orderIdList, startTime, endTime);
    }

    @PostMapping("findProcessAnalysisByOrderId")
    @ApiOperation("根据充放电订单id查询过程分析曲线数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "query", required = true),
            @ApiImplicitParam(name = "functionLogos", value = "多个功能点标识(多个以逗号分割)", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, Object>> findProcessAnalysisByOrderId(String orderId, String functionLogos) {
        return orderRecordService.findProcessAnalysisByOrderId(orderId, Arrays.stream(functionLogos.split(",")).map(String::trim).collect(Collectors.toList()));
    }

    @PostMapping("findOrderRecordList")
    @ApiOperation("查询订单记录列表信息")
    @ApiOperationSupport(order = 10)
    public ResponseResult<List<OrderRecordExportDto>> findOrderRecordList(OrderRecordQueryVo orderRecordQueryVo) {
        return orderRecordService.findOrderRecordList(orderRecordQueryVo);
    }

    @PostMapping("checkSitePassword")
    @ApiOperation("校验站点密码是否正确")
    @WebLog("订单管理-校验站点密码是否正确")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "站点密码", paramType = "query", required = true)
    })
    public ResponseResult<Integer> checkSitePassword(String siteId, String password) {
        return orderRecordService.checkSitePassword(siteId, password);
    }

    @PostMapping("findOrderTradeMoneyById")
    @ApiOperation("根据订单id查询订单交易金额")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "orderId", value = "站点id", paramType = "query", required = true)
    public ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(String orderId) {
        return orderRecordService.findOrderTradeMoneyById(orderId);
    }

    @PostMapping("orderRefund")
    @ApiOperation("人工退款/补单")
    @WebLog("订单管理-人工退款/补单")
    @ApiOperationSupport(order = 13)
    public ResponseResult<Void> orderRefund(OrderRefundVo orderRefundVo) {
        return orderRecordService.orderRefund(orderRefundVo);
    }

    @PostMapping("updateOrderStatus")
    @ApiOperation("修改订单状态")
    @WebLog("订单管理-修改订单状态")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true)
    })
    public ResponseResult<Void> updateOrderStatus(String orderId, String userId) {
        return orderRecordService.updateOrderStatus(orderId, userId);
    }

}
