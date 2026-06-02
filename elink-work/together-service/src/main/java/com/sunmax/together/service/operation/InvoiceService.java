package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.together.dto.operation.invoice.InvoiceRecordDto;
import com.sunmax.together.dto.operation.invoice.InvoiceDetailDto;
import com.sunmax.together.dto.operation.invoice.InvoiceListDto;
import com.sunmax.together.dto.operation.invoice.InvoiceOrderDto;
import com.sunmax.together.vo.operation.invoice.InvoiceQueryVo;
import com.sunmax.together.vo.operation.invoice.InvoiceStatusVo;
import com.sunmax.together.vo.operation.invoice.InvoiceRecordVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InvoiceService {

    /****
     * 查询发票管理列表
     *
     * @param invoiceQueryVo 发票管理查询参数
     * @return 发票管理列表
     */
    ResponseResult<PageDto<InvoiceListDto>> queryInvoiceList(InvoiceQueryVo invoiceQueryVo);

    /***
     * 根据发票申请单号查询订单详情数据(管理平台)
     * @param id 发票申请单号
     * @return 订单详情数据
     */
    ResponseResult<InvoiceOrderDto> findInvoiceOrderById(String id);

    /***
     * 修改发票状态(管理平台)
     *
     * @param invoiceStatusVo 发票状态参数
     * @param invoiceFile 发票文件(pdf格式)
     * @return 状态码
     */
    ResponseResult<Void> updateInvoiceStatus(InvoiceStatusVo invoiceStatusVo, MultipartFile invoiceFile);

    /**
     * 根据发票申请单号查询开票详情数据
     *
     * @param id 发票申请单号
     * @return 开票详情数据
     */
    ResponseResult<InvoiceDetailDto> findInvoiceDetailById(String id);

    /***
     * 查询操作记录列表(管理平台)
     *
     * @param invoiceRecordVo 发票记录查询参数
     * @return 发票记录列表
     */
    ResponseResult<PageDto<InvoiceRecordDto>> queryInvoiceRecordList(InvoiceRecordVo invoiceRecordVo);

    /****
     * 添加或编辑发票抬头(小程序）
     *
     * @param invoiceTitleVo 发票抬头入参参数
     * @return 状态码
     */
    ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo);

    /**
     * 删除发票抬头(小程序）
     *
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteInvoiceTitleById(String id);

    /***
     * 查询发票抬头列表(小程序）
     *
     * @param appletUserId 小程序用户id
     * @return 发票抬头列表
     */
    ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId);

    /***
     * 根据用户id查询订单(小程序)
     *
     * @param appletUserId 小程序用户id
     * @param queryDate 查询日期
     * @return 订单列表
     */
    ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate);

    /**
     * 申请开票(小程序)
     *
     * @param orderInvoicesVo 申请开票参数
     * @return 状态码
     */
    ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo);

    /**
     * 根据小程序用户id查询开票记录列表(小程序）
     *
     * @param appletUserId 小程序用户id
     * @return 开票记录列表
     */
    ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId);

    /**
     * 根据发票申请单号查询开票详情数据(小程序)
     *
     * @param id 发票申请单号
     * @return 开票详情数据
     */
    ResponseResult<AppInvoiceDetailDto> findAppInvoiceDetailById(String id);

    /**
     * 撤销开票(小程序）
     *
     * @param id 发票申请单号
     * @return 状态码
     */
    ResponseResult<Void> revokeInvoice(String id);

}
