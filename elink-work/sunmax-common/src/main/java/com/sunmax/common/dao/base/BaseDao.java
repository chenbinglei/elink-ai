package com.sunmax.common.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 批量增加
     * @param entities 批量增加实体
     * @param <S> 实体
     */
    <S extends T> Iterable<S> batchInsert(Iterable<S> entities);


    /**
     * 批量更新
     * @param entities 批量更新的实体
     * @param <S> 实体
     */
    <S extends T> Iterable<S> batchUpdate(Iterable<S> entities);
}
