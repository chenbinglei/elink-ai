import request from '@/utils/request'


// 查询数据源管理列表
export function queryDataSourceList(data) {
    return request({
        url: '/configure/datasource/queryDataSourceList',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 新建或编辑数据源
export function saveDataSource(data) {
    return request({
        url: '/configure/datasource/saveDataSource',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 根据多个id删除数据源
export function deleteDataSourceByIds(data) {
    return request({
        url: '/configure/datasource/deleteDataSourceByIds',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 根据id查询数据源详情数据
export function findDataSourceById(data) {
    return request({
        url: '/configure/datasource/findDataSourceById',
        portNum: "60008",
        method: 'post',
        data: data
    })
}