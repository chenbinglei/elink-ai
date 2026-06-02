import request from '@/utils/request';

// 查询运营商下拉列表
export function findOperatorInfoByTenantId(data) {
    return request({
        url: '/together/order/findOperatorInfoByTenantId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询订单记录列表信息
export function findOrderRecordList(data) {
    return request({
        url: "/together/order/findOrderRecordList",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 分页查询订单记录列表信息
export function findOrderRecordListByPage(data) {
    return request({
        url: '/together/order/findOrderRecordListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询占桩订单记录列表信息
export function findOccupyPileRecordListByPage(data) {
    return request({
        url: '/together/order/findOccupyPileRecordListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据订单id查询基本信息
export function findOrderRecordInfoById(data) {
    return request({
        url: '/together/order/findOrderRecordInfoById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据占桩订单id查询基本信息
export function findOccupyPileRecordInfoById(data) {
    return request({
        url: '/together/order/findOccupyPileRecordInfoById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据充放电订单id查询过程分析曲线数据
export function findProcessAnalysisByOrderId(data) {
    return request({
        url: '/together/order/findProcessAnalysisByOrderId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询充电平台列表信息
export function findPlatformInfoListByPage(data) {
    return request({
        url: "/system/configureCenter/findPlatformInfoListByPage",
        // portNum: 60002,
        method: "post",
        data: data,
    });
}

// 校验站点密码是否正确
export function checkSitePassword(data) {
    return request({
        url: "/together/order/checkSitePassword",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 根据订单id查询订单交易金额
export function findOrderTradeMoneyById(data) {
    return request({
        url: "/together/order/findOrderTradeMoneyById",
        portNum: 60009,
        method: "post",
        data: data,
    });
}


// 人工退款 / 补 单
export function orderRefund(data) {
    return request({
        url: "/together/order/orderRefund",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 修改订单状态
export function updateOrderStatus(data) {
    return request({
        url: "/together/order/updateOrderStatus",
        portNum: 60009,
        method: "post",
        data: data,
    });
}