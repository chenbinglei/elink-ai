import request from '@/utils/request';

// 分页查询光伏站点列表
export function findPvSiteListByPage(data) {
    return request({
        url: '/together/siteInfo/findPvSiteListByPage',
        // portNum: 60009,
        method: 'post',
        data: data
    });
}

// 统计光伏运营运行分析数据
export function countPvOperationAnalysis(data) {
    return request({
        url: '/together/operationAnalysis/countPvOperationAnalysis',
        // portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询光伏站点报表列表数据
export function findPvSiteReportList(data) {
    return request({
        url: '/together/dataReport/findPvSiteReportList',
        // portNum: 60009,
        method: 'post',
        data: data
    });
}


// 逆变器报表   start--------------------------------------------------》

// 根据登录用户id查询逆变器设备列表
export function findInverterListByUserId(data) {
    return request({
        url: '/together/dataReport/findInverterListByUserId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询逆变器报表列表数据
export function findPvInverterReportList(data) {
    return request({
        url: '/together/dataReport/findPvInverterReportList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 逆变器报表   end--------------------------------------------------》