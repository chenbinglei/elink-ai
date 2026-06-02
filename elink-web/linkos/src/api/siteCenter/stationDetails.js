import request from "@/utils/request";

// 根据站点id查询基本详情数据
export function findSiteInfoById(data) {
    return request({
        url: "/device/siteInfo/findSiteInfoById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 能源信息 ------------------------------------ start》

// 新增或编辑站点能源场景信息数据
export function saveOrUpdateSiteScenarioType(data) {
    return request({
        url: "/device/siteInfo/saveOrUpdateSiteScenarioType",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据能源场景id删除指定能源信息
export function deleteSiteScenarioTypeById(data) {
    return request({
        url: "/device/siteInfo/deleteSiteScenarioTypeById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 能源信息 ------------------------------------ end》


// 关联方管理 ------------------------------------ start》

// 根据站点id查询关联方列表信息
export function findAffiliatesListBySiteId(data) {
    return request({
        url: "/device/siteInfo/findAffiliatesListBySiteId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据关联方id删除指定关联方信息
export function deleteAffiliatesInfoById(data) {
    return request({
        url: "/device/siteInfo/deleteAffiliatesInfoById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增或编辑关联方信息
export function saveOrUpdateAffiliatesInfo(data) {
    return request({
        url: "/device/siteInfo/saveOrUpdateAffiliatesInfo",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
// 关联方管理 ------------------------------------ end》



// 设置 ------------------------------------ start》

// 根据站点id查询站点设置
export function findSiteSetUpBySiteId(data) {
    return request({
        url: "/device/siteInfo/findSiteSetUpBySiteId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 编辑站点设置
export function updateSiteSetUp(data) {
    return request({
        url: "/device/siteInfo/updateSiteSetUp",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 设置 ------------------------------------ end》



// 拓扑节点 ------------------------------------ start》

// 根据站点id查询拓扑节点列表
export function findTopoNodeListBySiteId(data) {
    return request({
        url: "/device/siteInfo/findTopoNodeListBySiteId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据站点id查询设备列表
export function findDeviceListBySiteId(data) {
    return request({
        url: "/device/siteInfo/findDeviceListBySiteId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增或编辑拓扑节点信息
export function saveOrUpdateTopoNodeInfo(data) {
    return request({
        url: "/device/siteInfo/saveOrUpdateTopoNodeInfo",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据拓扑节点id删除拓扑信息
export function deleteTopoNodeInfoById(data) {
    return request({
        url: "/device/siteInfo/deleteTopoNodeInfoById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据拓扑节点id查询拓扑信息
export function findTopoNodeInfoById(data) {
    return request({
        url: "/device/siteInfo/findTopoNodeInfoById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}
// 拓扑节点 ------------------------------------ end》


// 根据站点id查询拓扑节点列表
export function findTopNodeListBySiteId(data) {
    return request({
        url: "/device/siteInfo/findTopNodeListBySiteId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}
// 根据节点类型查询拓扑节点默认项数据

 export function getTopItemList(data) {
    return request({
        url: "/device/siteInfo/getTopItemList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
// 新增或编辑拓扑节点信息
 export function saveSiteTopNode(data) {
    return request({
        url: "/device/siteInfo/saveSiteTopNode",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据拓扑节点id删除拓扑信息
 export function deleteTopNodeInfoById(data) {
    return request({
        url: "/device/siteInfo/deleteTopNodeInfoById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据拓扑节点id查询拓扑信息
 export function findTopNodeInfoById(data) {
    return request({
        url: "/device/siteInfo/findTopNodeInfoById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}