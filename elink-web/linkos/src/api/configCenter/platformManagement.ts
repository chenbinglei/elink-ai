import request from "@/utils/request";

// 添加或修改平台信息
export function saveOrUpdatePlatformInfo(data) {
    return request({
        url: "/system/configureCenter/saveOrUpdatePlatformInfo",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 分页查询充电平台列表信息
export function findPlatformInfoListByPage(data) {
    return request({
        url: "/system/configureCenter/findPlatformInfoListByPage",
        portNum: "60002",
        method: "post",
        data: data,
    });
}

// 根据平台id删除平台信息
export function deletePlatformInfoById(data) {
    return request({
        url: "/system/configureCenter/deletePlatformInfoById",
        portNum: "60002",
        method: "post",
        data: data,
    });
}