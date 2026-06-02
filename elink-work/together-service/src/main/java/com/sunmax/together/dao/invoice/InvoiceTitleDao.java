package com.sunmax.together.dao.invoice;

import com.sunmax.together.entity.invoice.InvoiceTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceTitleDao extends JpaRepository<InvoiceTitleEntity, String>, JpaSpecificationExecutor<InvoiceTitleEntity> {

    //根据小程序用户id查询发票抬头数据
    List<InvoiceTitleEntity> findAllByAppletUserId(String appletUserId);

    //根据小程序用户id和发票抬头名称查询发票抬头数据
    List<InvoiceTitleEntity> findAllByAppletUserIdAndInvoiceTitle(String appletUserId, String invoiceTitle);

    //根据小程序用户id和是否默认查询发票抬头数据
    List<InvoiceTitleEntity> findAllByAppletUserIdAndIsDefault(String appletUserId, Integer isDefault);

}
