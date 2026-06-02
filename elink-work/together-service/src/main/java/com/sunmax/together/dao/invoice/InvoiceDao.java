package com.sunmax.together.dao.invoice;

import com.sunmax.together.entity.invoice.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvoiceDao extends JpaRepository<InvoiceEntity, String>, JpaSpecificationExecutor<InvoiceEntity> {

    //根据小程序用户id查询开票记录数据
    List<InvoiceEntity> findAllByAppletUserId(String appletUserId);

}
