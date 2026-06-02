package com.sunmax.webapp.service;

import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;

import java.util.List;

public interface InvoiceService {

    /**
     * 添加或编辑发票抬头
     *
     * @param invoiceTitleVo 发票抬头入参参数
     * @return 状态码
     */
    ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo);

    /**
     * 删除发票抬头
     *
     * @param id 发票抬头id
     * @return 状态码
     */
    ResponseResult<Void> deleteInvoiceTitleById(String id);

    /**
     * 查询用户发票抬头列表
     *
     * @param appletUserId 小程序用户id
     * @return 发票抬头列表
     */
    ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId);

    /**
     * 查询用户订单列表
     *
     * @param appletUserId 用户id
     * @param queryDate    查询日期(格式：yyyy-MM)
     * @return 订单列表
     */
    ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate);

    /**
     * 申请开票
     *
     * @param orderInvoicesVo 申请开票参数
     * @return 状态码
     */
    ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo);

    /**
     * 根据小程序用户id查询开票记录列表
     *
     * @param appletUserId 小程序用户id
     * @return 开票记录列表
     */
    ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId);

    /**
     * 根据发票申请单号查询开票详情数据
     *
     * @param id 开票记录id
     * @return 开票详情
     */
    ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(String id);

    /**
     * 撤销开票申请
     *
     * @param id 发票申请单号
     * @return 状态码
     */
    ResponseResult<Void> revokeInvoice(String id);

}
