import request from '@/utils/request';

// 根据站点id查询电价策略配置列表
export function queryElectConfigList(data) {
    return request({
        url: `/together/electConfig/queryElectConfigList`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 批量把电价策略应用到其它站点
export function applyElectConfigToOtherSites(data) {
    return request({
        url: `/together/electConfig/applyElectConfigToOtherSite`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 批量删除电价策略配置
export function deleteAllElectConfigByIds(data) {
    return request({
        url: `/together/electConfig/deleteAllElectConfigByIds`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 根据电价策略配置id查询电价策略配置数据
export function findElectConfigById(data) {
    return request({
        url: `/together/electConfig/findElectConfigById`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// 新增或编辑电价策略配置数据
export function saveElectConfig(data) {
    return request({
        url: `/together/electConfig/saveElectConfig`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}