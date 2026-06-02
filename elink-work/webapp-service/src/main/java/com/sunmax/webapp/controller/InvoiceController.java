package com.sunmax.webapp.controller;

import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.webapp.service.InvoiceService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("invoice")
@Api(tags = "充电开票")
public class InvoiceController {

    @Autowired
    private InvoiceService chargeInvoiceService;

    @PostMapping("saveInvoiceTitle")
    @ApiOperation(value = "添加或编辑发票抬头")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo) {
        return chargeInvoiceService.saveInvoiceTitle(invoiceTitleVo);
    }

    @PostMapping("deleteInvoiceTitleById")
    @ApiOperation(value = "根据主键id删除发票抬头")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> deleteInvoiceTitleById(String id) {
        return chargeInvoiceService.deleteInvoiceTitleById(id);
    }

    @PostMapping("findInvoiceTitleList")
    @ApiOperation(value = "根据小程序用户id查询发票抬头信息列表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId) {
        return chargeInvoiceService.findInvoiceTitleList(appletUserId);
    }

    @PostMapping("findOrderAppShowList")
    @ApiOperation(value = "根据月份查询订单号")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appletUserId", value = "小程序用户id", required = true),
            @ApiImplicitParam(name = "queryDate", value = "查询日期(格式：yyyy-MM)")
    })
    public ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate) {
        return chargeInvoiceService.findOrderAppShowList(appletUserId, queryDate);
    }

    @PostMapping("applyInvoice")
    @ApiOperation(value = "申请开票")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo) {
        return chargeInvoiceService.applyInvoice(orderInvoicesVo);
    }

    @PostMapping("findInvoiceRecordList")
    @ApiOperation(value = "根据小程序用户id查询开票记录列表")
    @ApiOperationSupport(order = 6)
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId) {
        return chargeInvoiceService.findInvoiceRecordList(appletUserId);
    }

    @PostMapping("findInvoiceDetailById")
    @ApiOperation(value = "查询开票详情数据")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParam(name = "id", value = "发票申请单号", required = true)
    public ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(String id) {
        return chargeInvoiceService.findInvoiceDetailById(id);
    }

    @PostMapping("revokeInvoice")
    @ApiOperation(value = "撤销开票")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "id", value = "发票申请单号", required = true)
    public ResponseResult<Void> revokeInvoice(String id) {
        return chargeInvoiceService.revokeInvoice(id);
    }

}
