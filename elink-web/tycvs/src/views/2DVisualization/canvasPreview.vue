<template>
  <div v-loading="loading" :style="themeStyle" class="app-container">
    <div id="meta2dCanvas" v-resize="meta2dResizeFun" class="meta2dCanvas"></div>
  </div>
</template>

<script>
import {useRoute} from "vue-router";
import {Meta2d} from '@meta2d/core';
import {ElMessage} from "element-plus";
import {readOSSFile} from "@/common/readOSSFile";
import {portNum, requestPath} from "@/utils/requestPath";
import {generateUUID, getFileNameFromPath} from "@/utils";
import {canvasMeta2dRegisterFun} from "@/packages/register";
import {reactive, toRefs, onMounted, onUnmounted, watch, ref, nextTick} from "vue";
import {findGraphDataListByGraphId, meta2dCustomRequest} from "@/api/2DVisualization/canvasPreview";

export default {
  name: 'canvasPreview',
  setup() {

    const route = useRoute();
    const that = reactive({
      loading: false,
      themeStyle: {},
      isLocalTest: false, // 是否本地测试等  打包需改成 false

      returnDataInfo: {},
      canvasMeta2d: null, // 画布实例
      dataSourceList: [], // 图纸上的所有接口
      publishFileData: {}, //默认图纸信息
      clientId: generateUUID(), // 唯一标识符
      domainId: route.params.id, // 图的域名id
      canvasOptions: {
        rule: false, // boolean	是否显示标尺
        hoverColor: "", // 鼠标移动到画笔上的颜色
        hoverBackground: "",  // 鼠标移动到画笔上的颜色
      }, // 画布配置
      webSocketObject: {}, // 数据来源的所有 webSocket 的对象
      httpTimerObject: {}, // http 轮询时间对象
      customRequestList: [], // http 所有请求
      canvasReturnDataList: [], // 所有接口返回的数据
    })

    // 初始化画布
    const initMeta2dFun = () => {
      that.canvasMeta2d = new Meta2d('meta2dCanvas', that.canvasOptions);
      that.canvasMeta2d.on('*', bindMeta2dCanvasFun);
      canvasMeta2dRegisterFun(); // 注册画布资源
      that.canvasMeta2d.lock(1); // 锁定画布
      queryGraphDataListByGraphId();
    }

    // 根据图模id查询图模接口数据
    const queryGraphDataListByGraphId = async () => {
      that.loading = true;
      findGraphDataListByGraphId({id: that.domainId, type: 2}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        returnDataInfo.fileName = getFileNameFromPath(returnDataInfo.publicFilePath); // 获取保存文件名称
        that.dataSourceList = returnDataInfo.dataSourceList ? returnDataInfo.dataSourceList : []; // 获取该文件所有自定义接口列表
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        if (returnDataInfo.fileName) queryGraphNamByFileDataFun();
      }).catch(() => {
        that.loading = false;
      })
    }

    // 根据文件名称获取文件内容
    const queryGraphNamByFileDataFun = () => {
      readOSSFile({fileName: that.returnDataInfo.fileName, isMsg: true}).then(result_file => {
        let publishFileData = result_file.data ? JSON.parse(result_file.data) : {};
        // console.log("publishFileData", publishFileData);
        // 查询站点设备组件历史数据
        if (publishFileData.requestParamsList && publishFileData.requestParamsList.length) {
          for (let i = 0; i < publishFileData.requestParamsList.length; i++) {
            let requestPathHttp = requestPath.replace(portNum, ":60006");  // 测试使用
            let requestData = {
              type: 2, //  1: websocket  2: http
              variableType: "systemVarAndFunction", // 代表是系统变量 跟功能点的数据
              dynamicField: JSON.stringify(publishFileData.requestParamsList[i].dynamicField),
              url: `${that.isLocalTest ? requestPathHttp : requestPath}/scrontab/configFuncPoint/findSiteDeviceDataList`,
              requestValue: JSON.stringify({
                ...publishFileData.requestParamsList[i].requestValue,
                variableDataMap: publishFileData.requestParamsList[i].variableDataMap,
              })
            }
            that.dataSourceList.push(requestData);
          }
        }

        // 查看画布是否需要请求 websocket
        if (publishFileData.isRequestWebsocket) {
          let websocketUrl = requestPath.replace('http', 'ws');
          if (that.isLocalTest) websocketUrl = websocketUrl.replace(portNum, ":60006");  // 测试使用
          let requestData = {
            type: 1, //  1: websocket  2: http
            variableType: "systemVarAndFunction", // 代表是系统变量 跟功能点的数据
            url: `${websocketUrl}/scrontab/configFuncVarWebSocket/{clientId}/{domainId}`,
            requestValue: JSON.stringify({clientId: that.clientId, domainId: that.domainId})
          }
          that.dataSourceList.push(requestData);
        }
        // console.log(that.dataSourceList);
        // console.log(publishFileData);
        that.publishFileData = JSON.parse(JSON.stringify(publishFileData));
        canvasMeta2dOpenFun();   // 打开新的画布
      }).catch(() => {
        that.loading = false;
      })
    }

    // 打开新的画布
    const canvasMeta2dOpenFun = () => {
      // console.log(that.publishFileData);
      if (JSON.stringify(that.publishFileData) !== "{}") {
        // 配置画布属性
        that.canvasMeta2d.setOptions({
          scroll: that.publishFileData.isScroll, // 显示滚动条
          disableScale: that.publishFileData.isDisableScale, // 禁止缩放
          disableTranslate: that.publishFileData.isDisableTranslate, // 禁止移动
        })

        delete that.publishFileData.width;
        delete that.publishFileData.height;
        that.canvasMeta2d.open(that.publishFileData, true); // 打开新的画布
      } else {
        that.loading = false;
      }
    }

    // 绑定事件
    const bindMeta2dCanvasFun = (event) => {
      // 画布打开成功
      if (event === "opened") {
        resizeCanvasMeta2dFun();
        that.canvasMeta2d.lock(1); // 锁定画布
        createDataRequestFun(); // 创建数据请求对象 集合
        that.loading = false;
      }
    }

    // 画布进行缩放处理等
    const resizeCanvasMeta2dFun = () => {
      try {
        if (that.publishFileData?.scaleMode >= 2) {
          let worH = that.publishFileData?.scaleMode === "2"; //  true 宽度铺满全屏   false 高度铺满全屏
          that.canvasMeta2d.screenView(0, worH);
        } else {
          that.canvasMeta2d.fitView(true, 0); //  自动铺满全屏
        }
      } catch (e) {
        console.log("画布缩放失败！！", e);
      }
    }

    // 创建数据请求对象 集合
    const createDataRequestFun = () => {
      if (that.dataSourceList && that.dataSourceList.length) {
        for (let i = 0; i < that.dataSourceList.length; i++) {
          let requestValue = {}; // 接口请求参数
          if (that.dataSourceList[i].requestValue) requestValue = JSON.parse(that.dataSourceList[i].requestValue);

          // 创建 websocket
          if (that.dataSourceList[i].type === 1) {
            if (typeof WebSocket !== 'undefined') {
              for (let key in requestValue) {
                if(key === "clientId") requestValue[key] = that.clientId;
                let new_vlaue = requestValue[key] ? requestValue[key] : undefined;
                that.dataSourceList[i].url = that.dataSourceList[i].url.replaceAll('{' + key + '}', new_vlaue);
              }
              // console.log(that.dataSourceList[i].url);
              that.webSocketObject['websocket' + i] = new WebSocket(`${that.dataSourceList[i].url}`);
              that.webSocketObject['websocket' + i].websocketNum = 0; // 连接次数
              that.webSocketObject['websocket' + i].websocketName = 'websocket' + i; // websocket名称
              that.webSocketObject['websocket' + i].websocketUrl = that.dataSourceList[i].url; // websocket url
              that.webSocketObject['websocket' + i].requestValue = that.dataSourceList[i].requestValue; // websocket 请求参数
              that.webSocketObject['websocket' + i].onopen = webSocketOpenFun;
              that.webSocketObject['websocket' + i].onerror = webSocketErrorFun;
              that.webSocketObject['websocket' + i].onclose = webSocketCloseFun;
              that.webSocketObject['websocket' + i].onmessage = webSocketMessageFun;
            } else {
              ElMessage({type: "error", showClose: true, message: "您的浏览器不支持websocket"});
            }
          }

          // http
          if (that.dataSourceList[i].type === 2) {
            let dynamicField = {requestHeader: "", requestMethod: "POST", requestInterval: 300};
            if (that.dataSourceList[i].dynamicField) dynamicField = JSON.parse(that.dataSourceList[i].dynamicField);
            that.customRequestList.push({
              interval: dynamicField.requestInterval * 1000, // 请求间隔
              callback: () => {
                meta2dCustomRequest({requestData: requestValue, dynamicField: dynamicField, requestUrl: that.dataSourceList[i].url}).then(res => {
                  // console.log(res);
                  let returnDataInfo = res.data ? res.data : {};
                  let findIndex = that.canvasReturnDataList.findIndex(item => item.requestUrl === that.dataSourceList[i].url && JSON.stringify(item.requestValue) === JSON.stringify(requestValue));
                  if (findIndex === -1) {
                    that.canvasReturnDataList.push({
                      requestUrl: that.dataSourceList[i].url,
                      requestValue: JSON.stringify(requestValue), // 记录请求接口参数
                      requestData: returnDataInfo.dataMap || returnDataInfo, // 接口返回数据
                    });
                  } else {
                    that.canvasReturnDataList[findIndex].requestData = returnDataInfo.dataMap || returnDataInfo; // 接口返回数据
                  }
                })
              }
            })
          }

          // mqtt
          if (that.dataSourceList[i].type === 3) {
          }
        }

        setIntervalImmediate(); // 轮询 http 数据请求
      }
    }

    // 轮询 http 数据请求
    const setIntervalImmediate = () => {
      for (let i = 0; i < that.customRequestList.length; i++) {
        that.customRequestList[i].callback();
        that.httpTimerObject['timer' + i] = setInterval(() => {
          that.customRequestList[i].callback();
        }, that.customRequestList[i].interval);
      }
    }

    // webSocket 打开成功执行
    const webSocketOpenFun = (event) => {
      let {websocketName} = event.target;
      that.webSocketObject[websocketName].websocketNum = 0;
      console.log('连接成功：', websocketName);
    }

    // webSocket 消息回调执行
    const webSocketMessageFun = (event) => {
      try {
        let {data, target} = event;
        let returnDataInfo = JSON.parse(data);
        if (typeof returnDataInfo === "object") {
          // console.log(target);
          // console.log("webSocketMessageFun",returnDataInfo);
          let requestParams_b = {requestUrl: target.websocketUrl, requestValue: target.requestValue};
          let findIndex = that.canvasReturnDataList.findIndex(item => JSON.stringify({
            requestUrl: item.requestUrl, requestValue: item.requestValue
          }) === JSON.stringify(requestParams_b));
          if (findIndex === -1) {
            that.canvasReturnDataList.push({
              ...requestParams_b,
              requestData: returnDataInfo?.data?.dataMap || returnDataInfo
            });
          } else {
            that.canvasReturnDataList[findIndex].requestData = returnDataInfo?.data?.dataMap || returnDataInfo;
          }
        }
      } catch (e) {
        //TODO handle the exception
        // console.log(e)
      }
    }

    // webSocket 连接失败执行
    const webSocketErrorFun = (event) => {
      let {websocketName} = event.target;
      console.log('连接失败：', websocketName);
    }

    // 断开 websocket
    const webSocketCloseFun = (event) => {
      let {websocketName} = event.target;
      console.log('断开连接：', websocketName);
    }

    // 请求返回数据更新 ------》 更新画布数据
    const watchCanvasReturnDataList = watch(() => that.canvasReturnDataList, (newCanvasReturnDataList) => {
      // console.log(newCanvasReturnDataList);
      const {pens} = that.canvasMeta2d.data(); // 获取画布所有的图元
      // console.log(pens);
      pens && pens.forEach(item => {
        // console.log(item);
        try {
          if (item.realTimes && item.realTimes.length) {
            let real_times_data = {id: item.id}; // 图元上所有绑定的 数据
            let active_real_times_data = {id: item.id}; // 当前图元上的实时数据
            for (let rt = 0; rt < item.realTimes.length; rt++) {
              try {
                // 兼容之前老图
                if (!item.realTimes[rt].variableType) {
                  item.realTimes[rt].variableType = 3;
                  item.realTimes[rt].variableId = item.realTimes[rt].variableName;
                }

                if (item.realTimes[rt].variableId) {
                  // console.log("realTimes", item.name, item.realTimes[rt]);
                  let returnDataValue = queryReturnDataValueByVarNameFun(item.realTimes[rt], item.requestParams);
                  // console.log("returnDataValue：",returnDataValue);
                  real_times_data[item.realTimes[rt].key] = JSON.parse(JSON.stringify(returnDataValue));
                  active_real_times_data[item.realTimes[rt].key] = item[item.realTimes[rt].key]; // 当前图元上的数据
                }
              } catch (e) {
                //TODO handle the exception
                console.log("数据获取错误！！！", e);
              }
            }
            // 数据对比，更新图元数据
            // console.log("penData",item);
            // console.log("active_real_times_data：", active_real_times_data);
            if (JSON.stringify(active_real_times_data) !== JSON.stringify(real_times_data)) {
              // console.log("数据发生更新！！！");
              // console.log("real_times_data：", real_times_data);
              that.canvasMeta2d.setValue(real_times_data, {render: false, doEvent: true});
            }
          }
        } catch (e) {
          //TODO handle the exception
          console.log("数据解析错误！！！", e);
        }
      })
      that.canvasMeta2d.render();
    }, {deep: true})

    /*
    * 根据变量名取 对应的数据
    * 1、通过图元参数设定变量值等信息获取对应的接口
    * 2、在通过对应接口把对应的变量数据返回给当前图元
    * */
    const queryReturnDataValueByVarNameFun = (realTimeData = {}, requestParams = {}) => {
      let returnDataValue = null;
      let newRealTimeData = JSON.parse(JSON.stringify(realTimeData));
      // console.log("requestParams", requestParams);
      // console.log("newRealTimeData", newRealTimeData);
      // console.log("dataSourceList",that.dataSourceList);

      let activeVarRequestObj = {}; // 当前变量数据来源接口
      let activeVarDataConfig = {}; // 变量数据绑定信息
      that.dataSourceList.forEach(item => {
        // 获取系统变量 跟功能点接口的数据
        if (newRealTimeData.variableType !== 3 && item.variableType === "systemVarAndFunction") {
          // console.log("requestParams",requestParams);

          // 因为固定的实时数据只有一个接口，不需要多余的判断
          if (requestParams.type === 1 && item.type === 1) {
            activeVarRequestObj = {requestUrl: item.url, requestValue: item.requestValue};
          }

          // http
          if (requestParams.type === 2 && item.type === 2) {
            // 图元上定义的接口参数值
            let penRequestParams = {
              dateType: requestParams.dateType,
              requestInterval: requestParams.requestInterval,
              timeInterval: requestParams.countPeriod + requestParams.countPeriodType
            }
            //数据接口的参数值
            let dynamicField = JSON.parse(item.dynamicField ?? {});
            let requestValue = JSON.parse(item.requestValue ?? {});
            let itemRequestParams = {
              dateType: requestValue.dateType,
              requestInterval: dynamicField.requestInterval,
              timeInterval: requestValue.timeInterval
            };
            if (JSON.stringify(penRequestParams) === JSON.stringify(itemRequestParams)) {
              activeVarRequestObj = {requestUrl: item.url, requestValue: item.requestValue};
            }
          }
        }

        // 获取自定义接口变量的数据
        if (newRealTimeData.variableType === 3 && item.variableList) {
          let findItem = item.variableList.find(el => el.name === newRealTimeData.variableId); // 查看当前自定义变量返回数据是否在当前接口里面
          if (findItem) {
            activeVarDataConfig = JSON.parse(JSON.stringify(findItem));
            activeVarRequestObj = {requestUrl: item.url, requestValue: item.requestValue};
          }
        }
      })

      // 数据对象
      // console.log("activeVarRequestObj：",activeVarRequestObj);
      // console.log("activeVarDataConfig：",activeVarDataConfig);
      // console.log("canvasReturnDataList：",that.canvasReturnDataList);
      // 找到当前图元绑定数据的接口
      let findRequestDataObj = that.canvasReturnDataList.find(item => JSON.stringify({
        requestUrl: item.requestUrl, requestValue: item.requestValue
      }) === JSON.stringify(activeVarRequestObj));
      // console.log("findRequestDataObj：",findRequestDataObj);

      if (findRequestDataObj) {
        // 获取系统变量 跟功能点接口的数据
        if (newRealTimeData.variableType !== 3) {
          // console.log("findRequestDataObj：",findRequestDataObj.requestData); // 变量所在的数据对象
          // if(newRealTimeData.siteOrDeviceId)
          let requestData = JSON.parse(JSON.stringify(findRequestDataObj.requestData));
          let findRequestData = requestData?.deviceDataMap || requestData;
          let findVarData = querySystemVarAndFunctionDataFun(findRequestData, newRealTimeData, requestParams);
          try {
            // console.log("findVarData",findVarData);
            if (typeof findVarData === "object") findVarData.dateList = JSON.parse(JSON.stringify(requestData.dateList ?? []));
            returnDataValue = JSON.parse(JSON.stringify(findVarData));
          } catch (e) {
            //TODO handle the exception
            // console.log("dateList设置失败", e);
          }
        }

        // 自定义变量数据查询
        if (newRealTimeData.variableType === 3) {
          // console.log("findRequestDataObj：",findRequestDataObj.requestData); // 变量所在的数据对象
          if (activeVarDataConfig.dataObject) activeVarDataConfig.dataObject = JSON.parse(activeVarDataConfig.dataObject);
          if (activeVarDataConfig.dataObject && activeVarDataConfig.dataObject.length) {
            let findVarData = queryVarReturnDataFun(activeVarDataConfig.dataObject, findRequestDataObj.requestData);
            // console.log("findVarData：",findVarData);
            try {
              returnDataValue = JSON.parse(JSON.stringify(findVarData?.fieldData ?? null)); // 默认等于自身数据
              // 如果有数据点位的，就取数字点位数据
              if (findVarData?.fieldType === "ArrayList" || findVarData?.fieldType === "ArrayString") {
                // console.log("findVarData：",findVarData);
                findVarData.fieldData = findVarData.fieldData ? JSON.parse(findVarData.fieldData) : [];
                // console.log(findVarData.fieldData);
                if (activeVarDataConfig.dataPointIndex && activeVarDataConfig.dataPointIndex >= 0) {
                  returnDataValue = findVarData.fieldData[activeVarDataConfig.dataPointIndex];
                }
              }
            } catch (e) {
              //TODO handle the exception
              console.log("数据查询错误：", e);
            }
          }
        }
      }

      // console.log("returnDataValue：",returnDataValue);
      return returnDataValue
    }

    // 查询系统变量或者功能点数据
    const querySystemVarAndFunctionDataFun = (findRequestData, newRealTimeData, requestParams) => {
      let returnDataValue = null;  // 先娶到当前站点或者设备的数据
      // console.log("findRequestData：",findRequestData);
      // console.log("newRealTimeData：",newRealTimeData);
      // console.log("requestParams：",requestParams);
      let findSiteOrDeviceInfo = findRequestData[newRealTimeData?.siteOrDeviceId];
      // console.log("findSiteOrDeviceInfo",findSiteOrDeviceInfo);

      if (requestParams.type === 1) {
        if (findSiteOrDeviceInfo) {
          let findItem = {};
          // variableType 设备数据类型 1-系统变量 2-设备功能点
          if (newRealTimeData.variableType === 1) findItem = findSiteOrDeviceInfo.nodeMap;
          if (newRealTimeData.variableType === 2) findItem = findSiteOrDeviceInfo.functionMap;
          // console.log("findItem",findItem);
          returnDataValue = JSON.parse(JSON.stringify(findItem[newRealTimeData.variableId]));
        }
      }

      if (requestParams.type === 2) {
        if (findSiteOrDeviceInfo && findSiteOrDeviceInfo.length) {
          // dataType 设备数据类型 1-系统变量 2-设备功能点
          let findItem = findSiteOrDeviceInfo.find(item => item.dataType === newRealTimeData.variableType && item.dataCode === newRealTimeData.variableId);
          // console.log("findItem",findItem);
          if (findItem) returnDataValue = JSON.parse(JSON.stringify(findItem));
        }
      }

      return returnDataValue
    }

    // 利用递归查询变量数据
    const queryVarReturnDataFun = (dataObject = [], requestData = null, dataObjectIndex = 0) => {
      let activeDataIndexField = dataObject[dataObjectIndex];
      let returnDataValue = JSON.parse(JSON.stringify(requestData)); // 当前查找到的数据
      // console.log("requestData：",requestData);
      // console.log("activeDataIndexField：",activeDataIndexField);

      if (activeDataIndexField && requestData) {
        let activeRequestData = null;
        let typeOFRequestData = requestData.fieldType ? requestData.fieldType : typeof requestData;
        // console.log(typeOFRequestData);

        // 对象格式的数据
        if (typeOFRequestData === "Map" || typeOFRequestData === "object") {
          activeRequestData = requestData[activeDataIndexField];
        }

        // 数组格式的数据
        if (typeOFRequestData === "ArrayList" || typeOFRequestData === "array") {
          // console.log("requestData",requestData);
          if (requestData.fieldData && requestData.fieldData.length) {
            activeRequestData = requestData.fieldData.find(item => item.enName === activeDataIndexField);
            try {
              if (typeof activeRequestData?.fieldData === "object") {
                if (!activeRequestData.fieldData.chName) {
                  activeRequestData.fieldData.chName = activeRequestData.chName;
                }
              }
            } catch (e) {
              //TODO handle the exception
              // console.log("ArrayList中文名称设置失败", e);
            }
          }
        }
        // console.log(activeRequestData);
        returnDataValue = queryVarReturnDataFun(dataObject, activeRequestData, dataObjectIndex + 1);
      }
      // console.log("queryVarReturnDataFun，returnDataValue：",returnDataValue);
      return returnDataValue
    }

    const meta2dResizeFun = () => {
      that.canvasMeta2d && that.canvasMeta2d.resize();
      resizeCanvasMeta2dFun();
    }

    // 销毁全部 数据请求
    const destroyAllDataRequestFun = () => {
      // 关闭全部 webSocket
      for (let key in that.webSocketObject) {
        if (that.webSocketObject[key]) that.webSocketObject[key].close();
      }
      // 清除所有 http 请求 轮询
      for (let key in that.httpTimerObject) {
        clearInterval(that.httpTimerObject[key]);
        console.log("清除定时器：", key);
      }
    }

    // 销毁画布
    const destroyMeta2dFun = () => {
      if (that.canvasMeta2d) {
        that.canvasMeta2d.off('*', bindMeta2dCanvasFun);
        that.canvasMeta2d.destroy(true);
        console.log("画布销毁完成！！！！！");
      }
    }

    // 设置主题颜色配置
    const setLoadingThemeFun = () => {
      let routeQueryData = route.query;
      if (routeQueryData?.theme === "dark") {
        let documentDom = document.documentElement;
        that.themeStyle = {backgroundColor: "#081A30"};
        documentDom.style.setProperty("--el-mask-color", "rgba(0, 0, 0, .8)"); // 设置遮罩颜色
      }

      // 存储用户唯一标识ID
      let clientId = localStorage.getItem("USER_CLIENT_ID");
      if (clientId && clientId !== "undefined") that.clientId = localStorage.getItem("USER_CLIENT_ID");
      localStorage.setItem("USER_CLIENT_ID",that.clientId);
      initMeta2dFun();
    }

    onMounted(() => {
      setLoadingThemeFun();
    })

    onUnmounted(() => {
      destroyAllDataRequestFun();
      destroyMeta2dFun();
      console.log("组件卸载！！！！！");
    });

    return {...toRefs(that), queryGraphDataListByGraphId, queryGraphNamByFileDataFun, canvasMeta2dOpenFun, createDataRequestFun, webSocketMessageFun, webSocketOpenFun,
      webSocketErrorFun, webSocketCloseFun, destroyAllDataRequestFun, watchCanvasReturnDataList, setIntervalImmediate, queryReturnDataValueByVarNameFun,
      queryVarReturnDataFun, meta2dResizeFun, bindMeta2dCanvasFun, setLoadingThemeFun, resizeCanvasMeta2dFun, querySystemVarAndFunctionDataFun}
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: column;

  .meta2dCanvas {
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
}
</style>