import request from '@/utils/request';

// 查询组件库列表
export function findModuleLibraryList(data) {
    return request({
        url: '/together/seriesInfo/findModuleLibraryList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 新增或编辑组件库信息
export function saveOrUpdateModuleLibrary(data) {
    return request({
        url: '/together/seriesInfo/saveOrUpdateModuleLibrary',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 批量删除组件库信息
export function batchDeleteModuleLibrary(data) {
    return request({
        url: '/together/seriesInfo/batchDeleteModuleLibrary',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 导入组件库列表数据
export function importModuleLibraryList(data) {
    return request({
        url: '/together/seriesInfo/importModuleLibraryList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 组串配置 接口---------------------------------->>>>>>>>>>>>>>>>>>

// 根据站点id查询逆变器设备型号列表
export function findInverterDeviceModelList(data) {
    return request({
        url: '/together/seriesInfo/findInverterDeviceModelList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 根据站点id查询逆变器设备列表
export function findInverterDeviceList(data) {
    return request({
        url: '/together/seriesInfo/findInverterDeviceList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据多个设备id清除组串配置列表
export function purgeSeriesConfigById(data) {
    return request({
        url: '/together/seriesInfo/purgeSeriesConfigById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据设备id查询组串配置信息
export function findSeriesConfigInfo(data) {
    return request({
        url: '/together/seriesInfo/findSeriesConfigInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 保存组件配置列表
export function saveSeriesConfigList(data) {
    return request({
        url: '/together/seriesInfo/saveSeriesConfigList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}