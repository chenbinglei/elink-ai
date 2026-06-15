import request from "@/utils/request";

// 新增编辑图形分类
export function saveGraphType(data) {
    return request({
        url: "/device/visual/saveGraphType",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据资产分类id查询图形分类列表
export function findGraphTypeListByTypeId(data) {
    return request({
        url: "/device/visual/findGraphTypeListByTypeId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 删除图形分类
export function deleteGraphTypeById(data) {
    return request({
        url: "/device/visual/deleteGraphTypeById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}