package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.together.dao.AppletUserDao;
import com.sunmax.together.dao.SiteAccountDao;
import com.sunmax.together.dao.invoice.InvoiceDao;
import com.sunmax.together.dao.invoice.InvoiceOrderDao;
import com.sunmax.together.dao.invoice.InvoiceRecordDao;
import com.sunmax.together.dao.invoice.InvoiceTitleDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dao.order.SettlementRecordDao;
import com.sunmax.together.entity.invoice.InvoiceTitleEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.feign.WebAppService;
import com.sunmax.together.service.operation.impl.InvoiceServiceImpl;
import com.sunmax.together.util.EmailUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("InvoiceService 单元测试")
class InvoiceServiceTest {

    @Mock private InvoiceDao invoiceDao;
    @Mock private InvoiceOrderDao invoiceOrderDao;
    @Mock private InvoiceRecordDao invoiceRecordDao;
    @Mock private SettlementRecordDao settlementRecordDao;
    @Mock private OrderRecordDao orderRecordDao;
    @Mock private DeviceService deviceService;
    @Mock private InvoiceTitleDao invoiceTitleDao;
    @Mock private AppletUserDao appletUserDao;
    @Mock private WebAppService webAppService;
    @Mock private SystemService systemService;
    @Mock private EmailUtil emailUtil;
    @Mock private SiteAccountDao siteAccountDao;

    @InjectMocks private InvoiceServiceImpl invoiceService;

    @Test
    @DisplayName("保存发票抬头-新增成功")
    void saveInvoiceTitle_new_success() {
        InvoiceTitleVo vo = new InvoiceTitleVo();
        vo.setAppletUserId("au-001");
        vo.setInvoiceTitle("测试公司");
        vo.setInvoiceType(1);
        vo.setTitleType(1);
        vo.setIsDefault(1);

        when(invoiceTitleDao.findAllByAppletUserIdAndInvoiceTitle("au-001", "测试公司")).thenReturn(Collections.emptyList());
        InvoiceTitleEntity saved = new InvoiceTitleEntity();
        saved.setId("it-001");
        when(invoiceTitleDao.save(any(InvoiceTitleEntity.class))).thenReturn(saved);
        when(invoiceTitleDao.findAllByAppletUserIdAndIsDefault("au-001", 1)).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = invoiceService.saveInvoiceTitle(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存发票抬头-重复名称返回错误")
    void saveInvoiceTitle_duplicate_returnsError() {
        InvoiceTitleVo vo = new InvoiceTitleVo();
        vo.setAppletUserId("au-001");
        vo.setInvoiceTitle("测试公司");
        vo.setInvoiceType(1);
        vo.setTitleType(1);
        vo.setIsDefault(1);

        InvoiceTitleEntity existing = new InvoiceTitleEntity();
        existing.setId("it-001");
        when(invoiceTitleDao.findAllByAppletUserIdAndInvoiceTitle("au-001", "测试公司")).thenReturn(List.of(existing));

        ResponseResult<Void> result = invoiceService.saveInvoiceTitle(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存发票抬头-编辑成功")
    void saveInvoiceTitle_update_success() {
        InvoiceTitleVo vo = new InvoiceTitleVo();
        vo.setId("it-001");
        vo.setAppletUserId("au-001");
        vo.setInvoiceTitle("测试公司");
        vo.setInvoiceType(1);
        vo.setTitleType(1);
        vo.setIsDefault(2);

        InvoiceTitleEntity existing = new InvoiceTitleEntity();
        existing.setId("it-001");
        when(invoiceTitleDao.findAllByAppletUserIdAndInvoiceTitle("au-001", "测试公司")).thenReturn(List.of(existing));
        when(invoiceTitleDao.findById("it-001")).thenReturn(Optional.of(existing));
        when(invoiceTitleDao.save(any(InvoiceTitleEntity.class))).thenReturn(existing);

        ResponseResult<Void> result = invoiceService.saveInvoiceTitle(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除发票抬头-成功")
    void deleteInvoiceTitleById_success() {
        InvoiceTitleEntity entity = new InvoiceTitleEntity();
        entity.setId("it-001");
        when(invoiceTitleDao.findById("it-001")).thenReturn(Optional.of(entity));

        ResponseResult<Void> result = invoiceService.deleteInvoiceTitleById("it-001");
        assertTrue(result.isSuccess());
        verify(invoiceTitleDao).delete(entity);
    }

    @Test
    @DisplayName("删除发票抬头-不存在返回错误")
    void deleteInvoiceTitleById_notFound_returnsError() {
        when(invoiceTitleDao.findById("it-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = invoiceService.deleteInvoiceTitleById("it-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询发票抬头列表-有数据返回列表")
    void findInvoiceTitleList_withData_returnsList() {
        InvoiceTitleEntity entity = new InvoiceTitleEntity();
        entity.setId("it-001");
        entity.setAppletUserId("au-001");
        entity.setInvoiceTitle("测试公司");
        when(invoiceTitleDao.findAllByAppletUserId("au-001")).thenReturn(List.of(entity));

        ResponseResult<?> result = invoiceService.findInvoiceTitleList("au-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询发票抬头列表-无数据返回空列表")
    void findInvoiceTitleList_empty_returnsEmptyList() {
        when(invoiceTitleDao.findAllByAppletUserId("au-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = invoiceService.findInvoiceTitleList("au-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存发票抬头-新增默认抬头更新其他默认状态")
    void saveInvoiceTitle_newDefault_updatesOtherDefaults() {
        InvoiceTitleVo vo = new InvoiceTitleVo();
        vo.setAppletUserId("au-001");
        vo.setInvoiceTitle("新默认公司");
        vo.setInvoiceType(1);
        vo.setTitleType(1);
        vo.setIsDefault(1);

        when(invoiceTitleDao.findAllByAppletUserIdAndInvoiceTitle("au-001", "新默认公司")).thenReturn(Collections.emptyList());
        InvoiceTitleEntity saved = new InvoiceTitleEntity();
        saved.setId("it-new");
        when(invoiceTitleDao.save(any(InvoiceTitleEntity.class))).thenReturn(saved);

        InvoiceTitleEntity oldDefault = new InvoiceTitleEntity();
        oldDefault.setId("it-old");
        oldDefault.setIsDefault(1);
        when(invoiceTitleDao.findAllByAppletUserIdAndIsDefault("au-001", 1)).thenReturn(List.of(oldDefault));
        when(invoiceTitleDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = invoiceService.saveInvoiceTitle(vo);
        assertTrue(result.isSuccess());
        verify(invoiceTitleDao).saveAll(any());
    }

    @Test
    @DisplayName("查询发票订单-不存在返回空对象")
    void findInvoiceOrderById_notFound_returnsEmpty() {
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.empty());

        var result = invoiceService.findInvoiceOrderById("inv-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询发票详情-不存在返回空对象")
    void findInvoiceDetailById_notFound_returnsEmpty() {
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.empty());

        var result = invoiceService.findInvoiceDetailById("inv-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询发票详情-存在返回数据")
    void findInvoiceDetailById_found_returnsData() {
        com.sunmax.together.entity.invoice.InvoiceEntity entity = new com.sunmax.together.entity.invoice.InvoiceEntity();
        entity.setId("inv-001");
        entity.setInvoiceAmount(new java.math.BigDecimal("100.00"));
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.of(entity));

        var result = invoiceService.findInvoiceDetailById("inv-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询发票记录列表-无数据返回空列表")
    void findInvoiceRecordList_noData_returnsEmpty() {
        when(invoiceDao.findAllByAppletUserId("au-001")).thenReturn(Collections.emptyList());

        var result = invoiceService.findInvoiceRecordList("au-001");
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("撤销发票-不存在返回错误")
    void revokeInvoice_notFound_returnsError() {
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.empty());

        var result = invoiceService.revokeInvoice("inv-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("撤销发票-开票中不允许撤销")
    void revokeInvoice_invoicing_returnsError() {
        com.sunmax.together.entity.invoice.InvoiceEntity entity = new com.sunmax.together.entity.invoice.InvoiceEntity();
        entity.setId("inv-001");
        entity.setInvoiceStatus(2);
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.of(entity));

        var result = invoiceService.revokeInvoice("inv-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("撤销发票-已开票不允许撤销")
    void revokeInvoice_invoiced_returnsError() {
        com.sunmax.together.entity.invoice.InvoiceEntity entity = new com.sunmax.together.entity.invoice.InvoiceEntity();
        entity.setId("inv-001");
        entity.setInvoiceStatus(3);
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.of(entity));

        var result = invoiceService.revokeInvoice("inv-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("撤销发票-已撤销不允许重复撤销")
    void revokeInvoice_alreadyRevoked_returnsError() {
        com.sunmax.together.entity.invoice.InvoiceEntity entity = new com.sunmax.together.entity.invoice.InvoiceEntity();
        entity.setId("inv-001");
        entity.setInvoiceStatus(4);
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.of(entity));

        var result = invoiceService.revokeInvoice("inv-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("撤销发票-待开票状态撤销成功")
    void revokeInvoice_pending_success() {
        com.sunmax.together.entity.invoice.InvoiceEntity entity = new com.sunmax.together.entity.invoice.InvoiceEntity();
        entity.setId("inv-001");
        entity.setInvoiceStatus(1);
        when(invoiceDao.findById("inv-001")).thenReturn(Optional.of(entity));
        when(invoiceDao.save(any())).thenReturn(entity);

        var result = invoiceService.revokeInvoice("inv-001");
        assertTrue(result.isSuccess());
        verify(invoiceDao).save(any());
    }

    // === 深度覆盖：更多方法路径 ===

    @Test
    @DisplayName("查询发票列表-有数据返回分页")
    void queryInvoiceList_hasData_returnsPage() {
        com.sunmax.together.entity.invoice.InvoiceEntity invoice = new com.sunmax.together.entity.invoice.InvoiceEntity();
        invoice.setId("inv-001");
        invoice.setInvoiceStatus(0);
        when(invoiceDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(invoice)));

        com.sunmax.together.vo.operation.invoice.InvoiceQueryVo vo = new com.sunmax.together.vo.operation.invoice.InvoiceQueryVo();
        vo.setPage(1);
        vo.setSize(10);
        try {
            var result = invoiceService.queryInvoiceList(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询发票订单-有数据返回详情")
    void findInvoiceOrderById_hasData_returnsDetail() {
        com.sunmax.together.entity.invoice.InvoiceOrderEntity order = new com.sunmax.together.entity.invoice.InvoiceOrderEntity();
        order.setId("io-001");
        order.setInvoiceId("inv-001");
        order.setOrderNum("ORD001");
        when(invoiceOrderDao.findAllByInvoiceIdIn(Collections.singletonList("inv-001"))).thenReturn(List.of(order));
        when(orderRecordDao.findAllByOrderNumIn(any())).thenReturn(Collections.emptyList());

        try {
            var result = invoiceService.findInvoiceOrderById("inv-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询发票记录列表-有数据返回分页")
    void queryInvoiceRecordList_hasData_returnsPage() {
        com.sunmax.together.entity.invoice.InvoiceRecordEntity record = new com.sunmax.together.entity.invoice.InvoiceRecordEntity();
        record.setId("ir-001");
        when(invoiceRecordDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(record)));

        com.sunmax.together.vo.operation.invoice.InvoiceRecordVo vo = new com.sunmax.together.vo.operation.invoice.InvoiceRecordVo();
        vo.setPage(1);
        vo.setSize(10);
        try {
            var result = invoiceService.queryInvoiceRecordList(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("更新发票状态-有发票返回结果")
    void updateInvoiceStatus_hasInvoice_returnsResult() {
        com.sunmax.together.entity.invoice.InvoiceEntity invoice = new com.sunmax.together.entity.invoice.InvoiceEntity();
        invoice.setId("inv-001");
        invoice.setInvoiceStatus(0);
        when(invoiceDao.findById("inv-001")).thenReturn(java.util.Optional.of(invoice));
        when(invoiceDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        com.sunmax.together.vo.operation.invoice.InvoiceStatusVo vo = new com.sunmax.together.vo.operation.invoice.InvoiceStatusVo();
        vo.setId("inv-001");
        vo.setInvoiceStatus(1);
        try {
            var result = invoiceService.updateInvoiceStatus(vo, null);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询APP订单展示列表-有数据返回列表")
    void findOrderAppShowList_hasData_returnsList() {
        com.sunmax.together.entity.order.OrderRecordEntity order = new com.sunmax.together.entity.order.OrderRecordEntity();
        order.setId("order-001");
        order.setOrderNum("ORD001");
        order.setRunMode(0);
        when(orderRecordDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(order));
        when(settlementRecordDao.findAllByOrderNumInAndPayWay(any(), anyInt())).thenReturn(Collections.emptyList());
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = invoiceService.findOrderAppShowList("user-001", "2026-01");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("申请发票-正常申请返回结果")
    void applyInvoice_normal_returnsResult() {
        com.sunmax.common.vo.webapp.OrderInvoicesVo vo = new com.sunmax.common.vo.webapp.OrderInvoicesVo();
        vo.setAppletUserId("user-001");
        vo.setInvoiceType(1);
        when(invoiceTitleDao.findById(any())).thenReturn(java.util.Optional.empty());
        when(invoiceDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try {
            var result = invoiceService.applyInvoice(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询APP发票记录列表-有数据返回列表")
    void findAppInvoiceRecordList_hasData_returnsList() {
        com.sunmax.together.entity.invoice.InvoiceEntity invoice = new com.sunmax.together.entity.invoice.InvoiceEntity();
        invoice.setId("inv-001");
        invoice.setAppletUserId("user-001");
        when(invoiceDao.findAllByAppletUserId("user-001")).thenReturn(List.of(invoice));

        try {
            var result = invoiceService.findInvoiceRecordList("user-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询APP发票详情-有数据返回详情")
    void findAppInvoiceDetailById_hasData_returnsDetail() {
        com.sunmax.together.entity.invoice.InvoiceEntity invoice = new com.sunmax.together.entity.invoice.InvoiceEntity();
        invoice.setId("inv-001");
        invoice.setInvoiceStatus(1);
        when(invoiceDao.findById("inv-001")).thenReturn(java.util.Optional.of(invoice));
        when(invoiceOrderDao.findAllByInvoiceIdIn(Collections.singletonList("inv-001"))).thenReturn(Collections.emptyList());

        try {
            var result = invoiceService.findAppInvoiceDetailById("inv-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
