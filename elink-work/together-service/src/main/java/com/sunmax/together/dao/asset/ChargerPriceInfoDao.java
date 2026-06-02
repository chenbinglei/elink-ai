package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.ChargerPriceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChargerPriceInfoDao extends JpaRepository<ChargerPriceInfoEntity, String>, JpaSpecificationExecutor<ChargerPriceInfoEntity> {

    //根据价格状态查询所有充放电价格信息
    List<ChargerPriceInfoEntity> findAllByPriceState(Integer priceState);

    //根据站点id、设备类型、价格类型查询生效中的价格信息
    @Query(value = "select c from ChargerPriceInfoEntity c WHERE c.siteId = ?1 AND c.deviceType = ?2 AND c.priceType = ?3 AND c.priceState = 1")
    List<ChargerPriceInfoEntity> findByChargerPriceInfoSiteId(String siteId, Integer deviceType, Integer priceType);

    //根据站点id、价格类型查询生效中的价格信息
    @Query(value = "select c from ChargerPriceInfoEntity c WHERE c.siteId = ?1 AND c.priceType = ?2 AND c.priceState = 1")
    List<ChargerPriceInfoEntity> findByInEffectInfoSiteId(String siteId, Integer priceType);

    //查询指定站点指定价格类型所有定价记录
    List<ChargerPriceInfoEntity> findAllBySiteIdAndPriceType(String siteId, Integer priceType);

    //根据所属站点id和价格类型查询站点下生效中充放电价格信息数据
    List<ChargerPriceInfoEntity> findAllBySiteIdInAndPriceStateAndPriceType(List<String> siteIdList, Integer priceState, Integer priceType);

    //根据所属站点id和价格类型查询站点下生效中充放电价格信息数据
    List<ChargerPriceInfoEntity> findAllBySiteIdInAndPriceStateAndPriceTypeAndDeviceType(List<String> siteIds, Integer priceState, Integer priceType, Integer deviceType);

    //根据所属站点id查询站点下生效中充放电价格信息数据
    List<ChargerPriceInfoEntity> findAllBySiteIdInAndPriceState(List<String> siteIdList, Integer priceState);

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    @Modifying
    @Query(value = "insert into ChargerPriceInfoEntity (id,siteId,takeType,takeTime,deviceType,fixedType,priceType,priceState,createTime) values (:#{#cp.id},:#{#cp.siteId},:#{#cp.takeType},:#{#cp.takeTime},:#{#cp.deviceType},:#{#cp.fixedType},:#{#cp.priceType},:#{#cp.priceState},:#{#cp.createTime})", nativeQuery = true)
    String insertChargerPrice(ChargerPriceInfoEntity cp);
}
