import { useMeta2dStore } from '@/stores/index';

/*
* 保存、发布图模的时候 进行数据处理
* */
export function handleCanvasMeta2dDataFun() {
    let canvasMeta2dFileData = {};
    let canvasDrawingOptions = {};
    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = meta2dStore.canvasMeta2d;
    const canvasMeta2dData = meta2dStore.canvasMeta2dData;
    const customCanvasOptionsList = meta2dStore.customCanvasOptionsList;

    let {siteId, siteName} = canvasMeta2dData;
    let canvasDrawingData = canvasMeta2d.data(); // 画布所有数据
    let canvasMeta2dOptions = canvasMeta2d.getOptions(); // 当前画布配置信息

    // 保存部分画布配置信息
    for (let i = 0; i < customCanvasOptionsList.length; i++) {
        let activeFieldValue = customCanvasOptionsList[i].default;
        if (canvasMeta2dOptions[customCanvasOptionsList[i].fieldName] !== undefined) {
            activeFieldValue = canvasMeta2dOptions[customCanvasOptionsList[i].fieldName];
        }
        canvasDrawingOptions[customCanvasOptionsList[i].fieldName] = activeFieldValue;
    }

    // 获取画布数据配置信息
    const requestParamsList = []; // 图元自定义请求接口数据列表
    const deviceVariables = {}; //  设备 变量标识 && 设备功能点
    const {pens} = canvasDrawingData; // 获取所有画笔对象

    // console.log(pens);
    if (pens && pens.length) {
        pens.forEach(item => {
            let {requestParams} = item;

            // websocket 参数处理
            if (requestParams?.type === 1) {
                // 获取所有实时数据信息
                if (item.realTimes && item.realTimes.length) {
                    for (let i = 0; i < item.realTimes.length; i++) {
                        try {
                            if (item.realTimes[i].variableType && item.realTimes[i].variableType !== 3) {

                                let fieldName = "";
                                if (item.realTimes[i].variableType === 1) fieldName = "nodeList";      // 设备变量标识
                                if (item.realTimes[i].variableType === 2) fieldName = "functionList";  // 设备功能点

                                if (!deviceVariables[item.realTimes[i].siteOrDeviceId]) {
                                    deviceVariables[item.realTimes[i].siteOrDeviceId] = {
                                        nodeList: [],
                                        functionList: []
                                    };
                                }

                                if (fieldName) {
                                    let findIndex = deviceVariables[item.realTimes[i].siteOrDeviceId][fieldName].findIndex(varItem => varItem === item.realTimes[i].variableId);
                                    if (findIndex === -1) deviceVariables[item.realTimes[i].siteOrDeviceId][fieldName].push(item.realTimes[i].variableId);
                                }
                            }
                        } catch (e) {
                            console.log("websocket实时数据信息参数失败", e);
                        }
                    }
                }
            }

            // 处理画布固定接口请求参数列表 （代表需要http请求几次）
            if (requestParams?.type === 2) {
                if (requestParams.countPeriod && requestParams.countPeriodType && (requestParams.dateType || requestParams.dateType === 0)) {
                    let request_params_obj = {
                        requestValue: {
                            dateType: requestParams.dateType,  // 查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年
                            timeInterval: requestParams.countPeriod + requestParams.countPeriodType, // 时间间隔 m-分钟;h-小时;d-天;n-月;y-年
                        },
                        variableDataMap: {}, // 设备变量、功能点等
                        dynamicField: {requestMethod: "POST", requestInterval: requestParams.requestInterval || 300}
                    };

                    let findIndex = requestParamsList.findIndex(requestItem => JSON.stringify({
                        requestValue: requestItem.requestValue,
                        dynamicField: requestItem.dynamicField
                    }) === JSON.stringify({
                        requestValue: request_params_obj.requestValue,
                        dynamicField: request_params_obj.dynamicField
                    }));
                    if (findIndex === -1) requestParamsList.push(request_params_obj); // 未找到当前请求

                    if (item.realTimes && item.realTimes.length) {
                        let activeFindIndex = findIndex !== -1 ? findIndex : (requestParamsList.length - 1); // 当前操作的数据下标
                        // console.log(activeFindIndex);
                        for (let i = 0; i < item.realTimes.length; i++) {
                            try {
                                if (item.realTimes[i].variableType && item.realTimes[i].variableType !== 3) {
                                    let fieldName = "";
                                    if (item.realTimes[i].variableType === 1) fieldName = "nodeList";      // 设备变量标识
                                    if (item.realTimes[i].variableType === 2) fieldName = "functionList";  // 设备功能点

                                    if (item.realTimes[i].siteOrDeviceId) {
                                        if (!requestParamsList[activeFindIndex].variableDataMap[item.realTimes[i].siteOrDeviceId]) {
                                            requestParamsList[activeFindIndex].variableDataMap[item.realTimes[i].siteOrDeviceId] = {
                                                nodeList: [],
                                                functionList: []
                                            };
                                        }

                                        if (fieldName) {
                                            let findIndex = requestParamsList[activeFindIndex].variableDataMap[item.realTimes[i].siteOrDeviceId][fieldName].findIndex(varItem => varItem === item.realTimes[i].variableId);
                                            if (findIndex === -1) requestParamsList[activeFindIndex].variableDataMap[item.realTimes[i].siteOrDeviceId][fieldName].push(item.realTimes[i].variableId);
                                        }
                                    }
                                }
                            } catch (e) {
                                console.log("http实时数据信息参数失败", e);
                            }
                        }
                    }
                }
            }
        })
    }

    const deviceVariablesArr = Object.keys(deviceVariables);
    const isRequestWebsocket = !!deviceVariablesArr.length; // 是否自动请求 Websocket
    canvasMeta2dFileData = {...canvasDrawingData, ...canvasDrawingOptions, requestParamsList, siteId, siteName, isRequestWebsocket};
    // console.log(canvasMeta2dFileData);
    // console.log(deviceVariables);

    return {canvasMeta2dFileData, deviceVariables}
}