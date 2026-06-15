import request from '@/utils/request';

// 统计运营总览数据
export function countOperationOverview(data) {
    return request({
        url: '/together/operationAnalysis/countOperationOverview',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 统计运营总览曲线数据
export function countOperationCurve(data) {
    return request({
        url: '/together/operationAnalysis/countOperationCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// // 根据站点id查询站点收益测算数据
export function findSiteIncomeBySiteId(data) {
    return request({
        url: '/together/chargeAnalysis/findSiteIncomeBySiteId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 统计站点投资收益概况
export function countSiteInvestIncome(data) {
    return request({
        url: '/together/chargeAnalysis/countSiteInvestIncome',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 根据日期统计站点经营收益概况
export function countSiteOperateIncome(data) {
    return request({
        url: '/together/chargeAnalysis/countSiteOperateIncome',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 添加或编辑站点收益测算数据
export function saveSiteIncome(data) {
    return request({
        url: '/together/chargeAnalysis/saveSiteIncome',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

