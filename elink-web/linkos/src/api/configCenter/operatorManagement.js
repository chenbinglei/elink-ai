import request from '@/utils/request'

// 分页查询运营商信息
export function findOperatorInfoByPage(data) {
    return request({
        url: '/system/configureCenter/findOperatorInfoByPage',
        portNum: 60002,
        method: 'post',
        data: data
    })
}

// 保存或编辑运营商信息
export function saveOrUpdateOperatorInfo(data) {
    return request({
        url: '/system/configureCenter/saveOrUpdateOperatorInfo',
        portNum: 60002,
        method: 'post',
        data: data
    })
}

// 根据id查询运营商信息
export function findOperatorDetailsById(data) {
    return request({
        url: '/system/configureCenter/findOperatorDetailsById',
        portNum: 60002,
        method: 'post',
        data: data
    })
}


// 根据id删除运营商信息
export function deleteOperatorInfoById(data) {
    return request({
        url: '/system/configureCenter/deleteOperatorInfoById',
        portNum: 60002,
        method: 'post',
        data: data
    })
}


// 查询所有运营商列表
export function findAllOperatorInfoList(data) {
    return request({
        url: "/system/configureCenter/findAllOperatorInfoList",
        portNum: 60002,
        method: "post",
        data: data
    });
}