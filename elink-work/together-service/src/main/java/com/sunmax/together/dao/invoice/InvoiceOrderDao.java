package com.sunmax.together.dao.invoice;

import com.sunmax.together.entity.invoice.InvoiceOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface InvoiceOrderDao extends JpaRepository<InvoiceOrderEntity, String>, JpaSpecificationExecutor<InvoiceOrderEntity> {

    // 根据多个申请单号id查询发票单号关联数据
    List<InvoiceOrderEntity> findAllByInvoiceIdIn(Collection<String> invoiceIds);

    // 根据多个订单id查询发票单号关联数据
    List<InvoiceOrderEntity> findAllByOrderNumIn(Collection<String> orderNums);

}
