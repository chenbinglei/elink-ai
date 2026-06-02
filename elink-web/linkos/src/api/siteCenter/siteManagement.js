import request from "@/utils/request";

// 分页查询站点列表
export function querySiteListByPage(data) {
    return request({
        url: "/device/siteInfo/querySiteListByPage",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据站点id删除站点相关信息
export function deleteSiteInfoById(data) {
    return request({
        url: "/device/siteInfo/deleteSiteInfoById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增或编辑站点数据
export function saveOrUpdateSiteInfo(data) {
    return request({
        url: "/device/siteInfo/saveOrUpdateSiteInfo",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据地址获取经纬度
export function getLonAndLatByAddress(data) {
    return request({
        url: "/device/siteInfo/getLonAndLatByAddress",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据用户id查询站点列表信息
export function findSiteInfoListByUserId(data) {
    return request({
        url: "/device/siteInfo/findSiteInfoListByUserId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}





// 查询全国省份列表
export function queryProvinceData(data) {
    return request({
        url: "/device/siteInfo/queryProvinceData",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据全国省编码id查询下面市级数据
export function queryCityDataByProvinceId(data) {
    return request({
        url: "/device/siteInfo/queryCityDataByProvinceId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据全国市编码id查询下面区县级数据
export function queryAreaDataByCityId(data) {
    return request({
        url: "/device/siteInfo/queryAreaDataByCityId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据坐标获取区域地址
export function getAreaAddressByCoordinates(data) {
    return request({
        url: "/device/siteInfo/getAreaAddressByCoordinates",
        portNum: 60003,
        method: "post",
        data: data,
    });
}