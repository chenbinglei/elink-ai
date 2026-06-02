import request from "@/utils/request";


// 查询模型扩展属性列表
export function querySeaList(data) {
    return request({
        url: "/device/rea/querySeaList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增编辑模型扩展属性
export function saveSea(data) {
    return request({
        url: "/device/rea/saveSea",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据主键id查询扩展属性详情
export function findSeaDetailById(data) {
    return request({
        url: "/device/rea/findSeaDetailById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 删除模型扩展属性
export function deleteSeaById(data) {
    return request({
        url: "/device/rea/deleteSeaById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
