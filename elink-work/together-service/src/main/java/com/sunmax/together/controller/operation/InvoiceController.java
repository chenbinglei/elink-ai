package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.invoice.InvoiceDetailDto;
import com.sunmax.together.dto.operation.invoice.InvoiceListDto;
import com.sunmax.together.dto.operation.invoice.InvoiceOrderDto;
import com.sunmax.together.dto.operation.invoice.InvoiceRecordDto;
import com.sunmax.together.service.operation.InvoiceService;
import com.sunmax.together.vo.operation.invoice.InvoiceQueryVo;
import com.sunmax.together.vo.operation.invoice.InvoiceRecordVo;
import com.sunmax.together.vo.operation.invoice.InvoiceStatusVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("invoice")
@Tag(name = "发票管理")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("queryInvoiceList")
    @Operation(summary = "查询发票管理列表")
    
    public ResponseResult<PageDto<InvoiceListDto>> queryInvoiceList(InvoiceQueryVo invoiceQueryVo) {
        return invoiceService.queryInvoiceList(invoiceQueryVo);
    }

    @PostMapping("findInvoiceOrderById")
    @Operation(summary = "根据发票申请单号查询发票订单详情")
    
    @Parameter(name = "id", description = "发票申请单号")
    public ResponseResult<InvoiceOrderDto> findInvoiceOrderById(String id) {
        return invoiceService.findInvoiceOrderById(id);
    }

    @PostMapping("updateInvoiceStatus")
    @Operation(summary = "修改发票状态")
    
    @Parameters({
            @Parameter(name = "invoiceStatusVo", description = "发票状态参数"),
            @Parameter(name = "invoiceFile", description = "发票文件(pdf格式)")
    })
    public ResponseResult<Void> updateInvoiceStatus(InvoiceStatusVo invoiceStatusVo, MultipartFile invoiceFile) {
        return invoiceService.updateInvoiceStatus(invoiceStatusVo, invoiceFile);
    }

    @PostMapping("findInvoiceDetailById")
    @Operation(summary = "根据发票申请单号查询开票详情数据")
    
    @Parameter(name = "id", description = "发票申请单号")
    public ResponseResult<InvoiceDetailDto> findInvoiceDetailById(String id) {
        return invoiceService.findInvoiceDetailById(id);
    }

    @PostMapping("queryInvoiceRecordList")
    @Operation(summary = "查询操作记录列表")
    
    ResponseResult<PageDto<InvoiceRecordDto>> queryInvoiceRecordList(InvoiceRecordVo invoiceRecordVo) {
        return invoiceService.queryInvoiceRecordList(invoiceRecordVo);
    }

}
