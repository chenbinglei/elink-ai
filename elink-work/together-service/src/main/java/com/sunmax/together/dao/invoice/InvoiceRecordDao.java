package com.sunmax.together.dao.invoice;

import com.sunmax.together.entity.invoice.InvoiceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRecordDao extends JpaRepository<InvoiceRecordEntity, String>, JpaSpecificationExecutor<InvoiceRecordEntity> {
}
