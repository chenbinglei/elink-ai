package com.sunmax.webapp.service.impl;

import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.webapp.service.InvoiceService;
import com.sunmax.webapp.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private TogetherService togetherService;

    @Override
    public ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo) {
        return togetherService.saveInvoiceTitle(invoiceTitleVo);
    }

    @Override
    public ResponseResult<Void> deleteInvoiceTitleById(String id) {
        return togetherService.deleteInvoiceTitleById(id);
    }

    @Override
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId) {
        return togetherService.findInvoiceTitleList(appletUserId);
    }

    @Override
    public ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate) {
        return togetherService.findOrderAppShowList(appletUserId, queryDate);
    }

    @Override
    public ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo) {
        return togetherService.applyInvoice(orderInvoicesVo);
    }

    @Override
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId) {
        return togetherService.findInvoiceRecordList(appletUserId);
    }

    @Override
    public ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(String id) {
        return togetherService.findInvoiceDetailById(id);
    }

    @Override
    public ResponseResult<Void> revokeInvoice(String id) {
        return togetherService.revokeInvoice(id);
    }
}
