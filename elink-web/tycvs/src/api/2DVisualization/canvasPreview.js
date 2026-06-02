import request from '@/utils/request'
import {objectToUrlParams} from "@/utils";
import canvasMeta2dRequest from '@/utils/canvasMeta2dRequest'

// 根据图模id查询图模接口数据
export function findGraphDataListByGraphId(data) {
    return request({
        url: '/configure/graph/findGraphDataListByGraphId',
        noLoginRequired: true, // 不需要登录
        portNum: "60008",
        method: 'post',
        data: data
    })
}

// 自定义请求接口
export function meta2dCustomRequest(data) {
    return canvasMeta2dRequest({
        data: data.requestData,
        dynamicField: data.dynamicField,
        method: data.dynamicField.requestMethod,
        url: data.dynamicField.requestMethod === "GET" ? `${data.requestUrl}?${objectToUrlParams(data.requestData)}` : data.requestUrl,
    })
}