import request from '@/utils/request';

// 查询商户列表
export function findAccountList(data) {
    return request({
        url: '/together/dataReport/findAccountList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询电站充放电报表
export function findSiteChargeReport(data) {
    return request({
        url: '/together/dataReport/findSiteChargeReport',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询电站运行报表
export function findSiteRunReport(data) {
    return request({
        url: '/together/dataReport/findSiteRunReport',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询电桩充放电报表
export function findPileChargeReport(data) {
    return request({
        url: '/together/dataReport/findPileChargeReport',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询电桩运行报表
export function findPileRunReport(data) {
    return request({
        url: '/together/dataReport/findPileRunReport',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询渠道汇总统计数据
export function findSummaryCountData(data) {
    return request({
        url: '/together/dataReport/findSummaryCountData',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询渠道充电明细列表
export function findPlatformDetailsList(data) {
    return request({
        url: '/together/dataReport/findPlatformDetailsList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
