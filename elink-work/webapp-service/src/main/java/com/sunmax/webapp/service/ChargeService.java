package com.sunmax.webapp.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.webapp.vo.AppletChargeStartVo;

public interface ChargeService {

    /**
     * 小程序启动充放电
     * @param appletStartVo 小程序启动参数
     * @return 响应数据
     */
    ResponseResult<?> appletChargeStart(AppletChargeStartVo appletStartVo);

    /**
     * 小程序充电退款
     * @param appletRefundVo 小程序充电退款参数
     * @return 状态码
     */
    ResponseResult<Void> appletChargeRefund(AppletChargeRefundVo appletRefundVo);

    /**
     * 电站启动接口
     * @param orderNum 订单号
     * @param appletStartVo 小程序启动参数
     * @return 状态码
     */
    Boolean pileStart(String orderNum, AppletChargeStartVo appletStartVo);

}
