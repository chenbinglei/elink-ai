package com.sunmax.webapp.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.webapp.service.InvoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("invoice")
@Tag(name = "充电开票")
public class InvoiceController {

    @Autowired
    private InvoiceService chargeInvoiceService;

    @PostMapping("saveInvoiceTitle")
    @Operation(summary = "添加或编辑发票抬头")
    
    public ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo) {
        return chargeInvoiceService.saveInvoiceTitle(invoiceTitleVo);
    }

    @PostMapping("deleteInvoiceTitleById")
    @Operation(summary = "根据主键id删除发票抬头")
    
    public ResponseResult<Void> deleteInvoiceTitleById(String id) {
        return chargeInvoiceService.deleteInvoiceTitleById(id);
    }

    @PostMapping("findInvoiceTitleList")
    @Operation(summary = "根据小程序用户id查询发票抬头信息列表")
    
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId) {
        return chargeInvoiceService.findInvoiceTitleList(appletUserId);
    }

    @PostMapping("findOrderAppShowList")
    @Operation(summary = "根据月份查询订单号")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "queryDate", description = "查询日期(格式：yyyy-MM)")
    })
    public ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate) {
        return chargeInvoiceService.findOrderAppShowList(appletUserId, queryDate);
    }

    @PostMapping("applyInvoice")
    @Operation(summary = "申请开票")
    
    public ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo) {
        return chargeInvoiceService.applyInvoice(orderInvoicesVo);
    }

    @PostMapping("findInvoiceRecordList")
    @Operation(summary = "根据小程序用户id查询开票记录列表")
    
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId) {
        return chargeInvoiceService.findInvoiceRecordList(appletUserId);
    }

    @PostMapping("findInvoiceDetailById")
    @Operation(summary = "查询开票详情数据")
    
    @Parameter(name = "id", description = "发票申请单号")
    public ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(String id) {
        return chargeInvoiceService.findInvoiceDetailById(id);
    }

    @PostMapping("revokeInvoice")
    @Operation(summary = "撤销开票")
    
    @Parameter(name = "id", description = "发票申请单号")
    public ResponseResult<Void> revokeInvoice(String id) {
        return chargeInvoiceService.revokeInvoice(id);
    }

}
