import request from '@/utils/request'

// 新建或编辑图模数据
export function saveGraph(data) {
    return request({
        url: '/configure/graph/saveGraph',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 查询图模管理列表
export function queryGraphList(data) {
    return request({
        url: '/configure/graph/queryGraphList',
        portNum: "60008",
        method: 'post',
        data: data
    })
}


// 根据多个id删除数据
export function deleteGraphByIds(data) {
    return request({
        url: '/configure/graph/deleteGraphByIds',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 克隆图模
export function cloneGraph(data) {
    return request({
        url: '/configure/graph/cloneGraph',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 根据用户id查询站点列表
export function findSiteListByUserId(data) {
    return request({
        url: '/configure/graph/findSiteListByUserId',
        portNum: "60008",
        method: 'post',
        data: data
    })
}