package com.sunmax.together.controller.operation;

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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("invoice")
@Api(tags = "发票管理")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("queryInvoiceList")
    @ApiOperation("查询发票管理列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<InvoiceListDto>> queryInvoiceList(InvoiceQueryVo invoiceQueryVo) {
        return invoiceService.queryInvoiceList(invoiceQueryVo);
    }

    @PostMapping("findInvoiceOrderById")
    @ApiOperation("根据发票申请单号查询发票订单详情")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "id", value = "发票申请单号", required = true)
    public ResponseResult<InvoiceOrderDto> findInvoiceOrderById(String id) {
        return invoiceService.findInvoiceOrderById(id);
    }

    @PostMapping("updateInvoiceStatus")
    @ApiOperation("修改发票状态")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "invoiceStatusVo", value = "发票状态参数", required = true),
            @ApiImplicitParam(name = "invoiceFile", value = "发票文件(pdf格式)")
    })
    public ResponseResult<Void> updateInvoiceStatus(InvoiceStatusVo invoiceStatusVo, MultipartFile invoiceFile) {
        return invoiceService.updateInvoiceStatus(invoiceStatusVo, invoiceFile);
    }

    @PostMapping("findInvoiceDetailById")
    @ApiOperation("根据发票申请单号查询开票详情数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "发票申请单号", required = true)
    public ResponseResult<InvoiceDetailDto> findInvoiceDetailById(String id) {
        return invoiceService.findInvoiceDetailById(id);
    }

    @PostMapping("queryInvoiceRecordList")
    @ApiOperation("查询操作记录列表")
    @ApiOperationSupport(order = 5)
    ResponseResult<PageDto<InvoiceRecordDto>> queryInvoiceRecordList(InvoiceRecordVo invoiceRecordVo) {
        return invoiceService.queryInvoiceRecordList(invoiceRecordVo);
    }

}
