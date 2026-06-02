package com.sunmax.webapp.service;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.ChargeDeviceInfoDto;
import com.sunmax.webapp.dto.SiteDetailsDataDto;
import com.sunmax.webapp.dto.SitePileDetailsDto;
import com.sunmax.webapp.vo.SiteQueryVo;

import java.util.List;

public interface FindPileService {

    /**
     * 查询站点列表
     * @param siteQueryVo
     * @return
     */
    ResponseResult<?> querySiteList(SiteQueryVo siteQueryVo);

    /**
     * 根据站点id查询详情信息
     * @param siteId
     * @return
     */
    ResponseResult<SiteDetailsDataDto> querySiteDetailsById(String siteId);

    /**
     * 根据站点id查询站点电桩详情信息
     * @param siteId
     * @return
     */
    ResponseResult<SitePileDetailsDto> querySitePileDetailsById(String siteId);

    /**
     * 根据站点id查询站点充放电计费策略数据
     * @param siteId 站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    ResponseResult<List<ChargerPriceRateDto>> findBillStrategyById(String siteId, Integer priceType);

    /**
     * 输入终端编号获取设备详情
     * @param pileCode 电桩编码
     * @param appletUserId 小程序用户id
     * @param appletKey 小程序登录标识
     * @return
     */
    ResponseResult<ChargeDeviceInfoDto> queryDeviceInfoByPileCode(String pileCode, String appletUserId, String appletKey);

    /**
     * 根据设备id查询充放电和占桩价格信息
     * @param deviceId 设备id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType);
}
