package com.sunmax.together.service;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.SiteAccountDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.common.vo.together.OrderUpdateVo;
import com.sunmax.common.vo.together.RefundRecordChangeVo;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WebAppFeignService {

    /**
     * 根据充电桩编号生成订单号
     * @param pilesCode 电桩编号
     * @param serialNumber 序列号
     * @return 订单编号
     */
    ResponseResult<String> generateOrderNum(String pilesCode, String serialNumber);

    /**
     * 根据多个小程序用户id查询小程序用户信息
     * @param appletUserIds 多个小程序用户id
     * @return 小程序用户信息
     */
    ResponseResult<Map<String, AppletUserInfoDto>> findAppletUserByIds(Set<String> appletUserIds);

    /**
     * 创建充电订单
     * @param orderChangeVo 订单信息
     * @return 订单信息
     */
    ResponseResult<Void> createPileOrder(OrderChangeVo orderChangeVo);

    /**
     * 根据站点id和类型查询站点账户信息
     * @param siteIds 多个站点id
     * @param type 类型 1-收款账户 2-付款账户 3-分帐账户
     * @return 站点账户数据
     */
    ResponseResult<Map<String, List<SiteAccountDto>>> findSiteAccountListBySiteIds(Set<String> siteIds, Integer type);

    /**
     * 更新电桩失败订单信息
     * @param orderUpdateVo 电桩订单信息
     * @return 状态码
     */
    ResponseResult<Void> updatePileFailOrder(OrderUpdateVo orderUpdateVo);

    /**
     * 根据多个站点id查询充放电费率列表
     * @param siteIds 多个站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @param deviceType 设备类型 1-直流 2-交流
     * @return
     */
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(List<String> siteIds, Integer priceType, Integer deviceType);

    /**
     * 根据设备id查询充放电和占桩价格信息
     * @param deviceId 设备id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType);

    /**
     * 更新用户钱包信息
     * @param userDisWalletVo 用户钱包信息
     * @return 用户余额
     */
    ResponseResult<BigDecimal> updateUserDisWallet(UserDisWalletChangeVo userDisWalletVo);

    /**
     * 保存退款记录
     * @param refundRecordChangeVo
     * @return
     */
    ResponseResult<String> saveRefundRecord(RefundRecordChangeVo refundRecordChangeVo);
}
