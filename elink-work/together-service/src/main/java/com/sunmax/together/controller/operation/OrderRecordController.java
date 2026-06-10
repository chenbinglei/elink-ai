package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("order")
@Tag(name = "订单管理")
public class OrderRecordController {

    @Autowired
    private OrderRecordService orderRecordService;

    @PostMapping("findOrderRecordListByPage")
    @Operation(summary = "分页查询订单记录列表信息")
    
    public ResponseResult<OrderRecordListDto> findOrderRecordListByPage(OrderRecordQueryVo orderRecordQueryVo) {
        return orderRecordService.findOrderRecordListByPage(orderRecordQueryVo);
    }

    @PostMapping("findOrderRecordInfoById")
    @Operation(summary = "根据订单id查询基本信息")
    
    @Parameter(name = "orderId", description = "订单id")
    public ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(String orderId) {
        return orderRecordService.findOrderRecordInfoById(orderId);
    }

    @PostMapping("findOccupyPileRecordListByPage")
    @Operation(summary = "分页查询占桩订单记录列表信息")
    
    public ResponseResult<OccupyPileRecordListDto> findOccupyPileRecordListByPage(OccupyPileRecordListQueryVo occupyPileRecordListQueryVo, String userId) {
        return orderRecordService.findOccupyPileRecordListByPage(occupyPileRecordListQueryVo, userId);
    }

    @PostMapping("findOccupyPileRecordInfoById")
    @Operation(summary = "根据占桩订单id查询基本信息")
    
    @Parameter(name = "occupyId", description = "占桩订单id")
    public ResponseResult<OccupyPileRecordInfoDto> findOccupyPileRecordInfoById(String occupyId) {
        return orderRecordService.findOccupyPileRecordInfoById(occupyId);
    }

    @PostMapping("findOperatorListByUserId")
    @Operation(summary = "查询运营商下拉列表")
    
    @Parameter(name = "userId", description = "当前登录用户id")
    public ResponseResult<List<AffiliatesInfoDto>> findOperatorListByUserId(String userId) {
        return orderRecordService.findOperatorListByUserId(userId);
    }

    @PostMapping("findSiteBasicInfoByTenantId")
    @Operation(summary = "查询站点下拉列表")
    
    @Parameter(name = "tenantId", description = "当前登录用户所属租户id")
    public ResponseResult<List<SiteInfoDto>> findSiteBasicInfoByTenantId(String tenantId, String userId) {
        return orderRecordService.findSiteBasicInfoByTenantId(tenantId, userId);
    }

    @PostMapping("findOrderRecordListByPileCodes")
    @Operation(summary = "根据多个充电桩编码查询订单充放电记录")
    
    public ResponseResult<List<OrderRecordDto>> findOrderRecordListByPileCodes(List<String> pileCodeList, String startTime, String endTime) {
        return orderRecordService.findOrderRecordListByPileCodes(pileCodeList, startTime, endTime);
    }

    @PostMapping("findOccupyPileRecordListByOrderIds")
    @Operation(summary = "根据多个订单记录id查询占桩订单记录")
    
    public ResponseResult<List<OccupyPileRecordDto>> findOccupyPileRecordListByOrderIds(List<String> orderIdList, String startTime, String endTime) {
        return orderRecordService.findOccupyPileRecordListByOrderIds(orderIdList, startTime, endTime);
    }

    @PostMapping("findProcessAnalysisByOrderId")
    @Operation(summary = "根据充放电订单id查询过程分析曲线数据")
    
    @Parameters({
            @Parameter(name = "orderId", description = "订单id"),
            @Parameter(name = "functionLogos", description = "多个功能点标识(多个以逗号分割)")
    })
    public ResponseResult<Map<String, Object>> findProcessAnalysisByOrderId(String orderId, String functionLogos) {
        return orderRecordService.findProcessAnalysisByOrderId(orderId, Arrays.stream(functionLogos.split(",")).map(String::trim).collect(Collectors.toList()));
    }

    @PostMapping("findOrderRecordList")
    @Operation(summary = "查询订单记录列表信息")
    
    public ResponseResult<List<OrderRecordExportDto>> findOrderRecordList(OrderRecordQueryVo orderRecordQueryVo) {
        return orderRecordService.findOrderRecordList(orderRecordQueryVo);
    }

    @PostMapping("checkSitePassword")
    @Operation(summary = "校验站点密码是否正确")
    @WebLog("订单管理-校验站点密码是否正确")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "password", description = "站点密码")
    })
    public ResponseResult<Integer> checkSitePassword(String siteId, String password) {
        return orderRecordService.checkSitePassword(siteId, password);
    }

    @PostMapping("findOrderTradeMoneyById")
    @Operation(summary = "根据订单id查询订单交易金额")
    
    @Parameter(name = "orderId", description = "站点id")
    public ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(String orderId) {
        return orderRecordService.findOrderTradeMoneyById(orderId);
    }

    @PostMapping("orderRefund")
    @Operation(summary = "人工退款/补单")
    @WebLog("订单管理-人工退款/补单")
    
    public ResponseResult<Void> orderRefund(OrderRefundVo orderRefundVo) {
        return orderRecordService.orderRefund(orderRefundVo);
    }

    @PostMapping("updateOrderStatus")
    @Operation(summary = "修改订单状态")
    @WebLog("订单管理-修改订单状态")
    
    @Parameters({
            @Parameter(name = "orderId", description = "订单id"),
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<Void> updateOrderStatus(String orderId, String userId) {
        return orderRecordService.updateOrderStatus(orderId, userId);
    }

}
