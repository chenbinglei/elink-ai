
import request from "@/utils/request";

// 获取微信签名
export function getWechatSignature(data) {
    return request({
        url: "/webapp/wechat/getWechatSignature",
        method: "post",
        data: data,
    });
}

// 根据桩编号获取支付宝小程序id
export function getAlipayAppletId(data) {
    return request({
        url: "/webapp/scancodecharge/getAlipayAppletId",
        method: "post",
        data: data,
    });
}

// 输入终端编号获取设备详情
export function queryDeviceInfoByPileCode(data) {
    return request({
        url: "/webapp/scancodecharge/queryDeviceInfoByPileCode",
        method: "post",
        data: data,
    });
}
