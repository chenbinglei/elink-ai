import request from '@/utils/request'

// 查询小程序列表
export function queryAppletList(data) {
    return request({
        url: '/system/applet/queryAppletList',
        portNum: 60002,
        method: 'post',
        data: data
    })
}

// 新增或编辑小程序信息
export function saveApplet(data) {
    return request({
        url: '/system/applet/saveApplet',
        portNum: 60002,
        method: 'post',
        data: data
    })
}

// 根据主键id查询小程序详情数据
export function findAppletDetailById(data) {
    return request({
        url: '/system/applet/findAppletDetailById',
        portNum: 60002,
        method: 'post',
        data: data
    })
}