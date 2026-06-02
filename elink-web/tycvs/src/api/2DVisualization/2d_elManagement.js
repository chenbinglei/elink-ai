import request from '@/utils/request'

// 查询图元管理列表
export function queryPelList(data) {
    return request({
        url: '/configure/pel/queryPelList',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 新建或编辑图元数据
export function saveGraphPel(data) {
    return request({
        url: '/configure/pel/saveGraphPel',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 根据多个图元id删除图元数据
export function deletePelByIds(data) {
    return request({
        url: '/configure/pel/deletePelByIds',
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 根据id查询图元详情数据
export function findPelById(data) {
    return request({
        url: '/configure/pel/findPelById',
        portNum: "60008",
        method: 'post',
        data: data
    })
}
// 导入图模
export function importGraph(data) {
    return request({
        url: '/configure/graph/importGraph',
        portNum: "60008",
        method: 'post',
        data: data
    })
}
