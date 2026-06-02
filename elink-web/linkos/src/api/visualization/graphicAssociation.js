import request from "@/utils/request";

// 根据设备id查询图形列表
export function findGraphListByDeviceId(data) {
    return request({
        url: "/device/visual/findGraphListByDeviceId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}


// 新增或编辑图形
export function saveGraph(data) {
    return request({
        url: "/device/visual/saveGraph",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 删除图形
export function deleteGraphById(data) {
    return request({
        url: "/device/visual/deleteGraphById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 获取站点设备树形结构
export function getSiteAssetsTreeList(data) {
    return request({
        url: "/device/device/getSiteAssetsTreeList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}