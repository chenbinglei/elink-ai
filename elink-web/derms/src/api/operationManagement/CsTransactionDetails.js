import request from '@/utils/request';

// 查询充电交易列表
export function queryRechargeTradeList(data) {
    return request({
        url: "/together/platformTrade/queryRechargeTradeList",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 根据主键id查询充电交易详情
export function findRechargeTradeById(data) {
    return request({
        url: "/together/platformTrade/findRechargeTradeById",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 查询V2G钱包交易列表
export function queryDischargeTradeList(data) {
    return request({
        url: "/together/platformTrade/queryDischargeTradeList",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 根据主键id查询V2G钱包交易详情
export function findDischargeTradeById(data) {
    return request({
        url: "/together/platformTrade/findDischargeTradeById",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

