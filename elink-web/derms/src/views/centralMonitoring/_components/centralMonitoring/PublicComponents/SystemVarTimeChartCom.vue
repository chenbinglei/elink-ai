<template>
  <TitleView :isTitleIcon="false" :title="titleName" is-content-height>
    <template #headerRight>
      <div class="header_form flex-warp">
        <template v-if="timerType === 1">
          <el-date-picker v-model="timeDate" :disabled-date="pickerOptions.disabledDate" format="YYYY-MM-DD" placeholder="请选择时间" style="width: 160px;"
                          type="date" value-format="YYYY-MM-DD" :clearable="false" @change="querySystemVarOrFunctionCurveData"/>
        </template>
        <template v-if="timerType === 2">
          <ButtonsTabs v-model:tabsIndex="tabsIndex" :tabsArray="tabsArray" @changeEvent="findVarCodeListFun"/>
          <template v-if="tabsIndex === 1">
            <el-date-picker v-model="dayTimeDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" end-placeholder="结束时间"
                            format="YYYY-MM-DD" range-separator="~" start-placeholder="开始时间" style="width: 220px" type="daterange" :clearable="false"
                            value-format="YYYY-MM-DD" @change="querySystemVarOrFunctionCurveData"/>
          </template>
          <template v-if="tabsIndex === 2">
            <el-date-picker v-model="monthTimeDate" :disabled-date="pickerOptions.disabledDate" end-placeholder="结束时间" format="YYYY-MM" range-separator="~"
                            :clearable="false" start-placeholder="开始时间" style="width: 220px" type="monthrange" value-format="YYYY-MM"
                            @change="querySystemVarOrFunctionCurveData"/>
          </template>
          <template v-if="tabsIndex === 3">
            <el-date-picker v-model="yearTimeDate" :disabled-date="pickerOptions.disabledDate" end-placeholder="结束时间" format="YYYY" range-separator="~"
                            :clearable="false" start-placeholder="开始时间" style="width: 220px" type="yearrange" value-format="YYYY"
                            @change="querySystemVarOrFunctionCurveData"/>
          </template>
        </template>

        <div v-if="isDownload" class="download">
          <el-dropdown :disabled="exportLoading" placement="bottom-start">
            <span class="iconfont icon-xiazai"></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="clickDownloadButFun(1)">导出图片</el-dropdown-item>
                <el-dropdown-item @click="clickDownloadButFun(2)">导出Excel</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </template>
    <template #content>
      <div v-loading="listLoading" class="content_body">
        <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
      </div>
    </template>
  </TitleView>
</template>

<script>
import pinyin from "tiny-pinyin";
import cloneDeep from "lodash/cloneDeep";
import {ElMessage} from 'element-plus';
import {exportCustomExcel} from "@/common/exportExcel";
import {colorHexTurnRgba, downloadFiles} from "@/utils";
import {reactive, defineComponent, toRefs, ref, watch} from "vue";
import {findSystemVarOrFunctionCurveData} from "@/api/centralMonitoring/centralMonitoring";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDate,
  getNowDateAll, isMonth, isToday, isYear, pickerDateOneMonthDay, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default defineComponent({
  name: "SystemVarTimeChartCom",
  props: {
    // 站点id  或者 设备id
    siteId: {
      type: [Number, String],
      default: ""
    },
    titleName: {
      type: String,
      default: ""
    },
    // 下载文件名称
    fileName: {
      type: String,
      default: ""
    },
    // 表头 右侧时间筛选 类型  1； 单个时间 （年月日）  2： 年月日 三种区 间 时间选择
    timerType: {
      type: Number,
      default: 1
    },
    // 请求接口  1： 根据系统变量查询数据  2： 根据功能点标识查询数据  3: 充电站检测 电量统计  4:光伏站点功率曲线数据
    isRequestType:{
      type: Number,
      default: 1
    },
    // 数据查询点位
    dataIndex:{
      type: Number,
      default: null
    },
    // 是否可下载
    isDownload: {
      type: Boolean,
      default: true
    },
    // legend 配置
    legend: {
      type: Object,
      default: () => {
        return null;
      }
    },
    // yAxis 配置
    yAxis: {
      type: Object,
      default: () => {
        return null;
      }
    },
    // 单y轴数据名称
    yAxisName:{
      type: String,
      default: ""
    },
    // 是否根据日月年获取对应的变量or功能点名称
    isHandleVarName:{
      type: Boolean,
      default: false
    },
    seriesListArray: {
      type: Array,
      default: () => []
    },
  },
  setup(props) {

    const that = reactive({
      tabsIndex: 1,
      varCodeList: "", // 查询系统变量数据
      listLoading: false,
      exportLoading: false,
      timeDate: getNowDate(),
      dayTimeDate: pickerDateOneMonthDay(),
      pickerOptions: pickerOptionsGthanAcTime(),

      formInline: {},
      tableHeader: [],
      returnDataInfo: {},

      tabsArray: [{name: "日", id: 1}, {name: "月", id: 2}, {name: "年", id: 3}],
      yearTimeDate: [getDaysFromCurrentTime(-722, 2), getDaysFromCurrentTime(0, 2)],
      monthTimeDate: [getDaysFromCurrentTime(-180, 1), getDaysFromCurrentTime(0, 1)],
      chartOption: {
        color: ["#8979FF", "#FF928A", "#3CC3DF"],
        legend: {
          top: 0,
          left: 'center',
          textStyle: {color: '#9EA9B5'},
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          className: 'echarts-tooltip',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? "-" } ${ props.yAxisName }`,
          // formatter: (params) => {
          //   // console.log(params);
          //   let tooltipHtml = `<div class="axisValue">${ params[0].name }</div>`;
          //   if(Array.isArray(params[0].value)) tooltipHtml = `<div class="axisValue">${ params[0].value[0] }</div>`;
          //   for (let i = 0; i < params.length; i++) {
          //     tooltipHtml += `<div class="flex-ai-center jc-space-between">
          //                       <div class="tooltip_left">
          //                         <span class="marker">${ params[i].marker }</span>
          //                         <span class="seriesName">${ params[i].seriesName }：</span>
          //                       </div>
          //                       <div class="tooltip_right">
          //                         <span class="value" style="color: ${ params[i].color }">${ Array.isArray(params[i].value) ? (params[i].value[1] ?? '--') : params[i].value }</span>
          //                         <span class="unit">${ props.yAxisName }</span>
          //                       </div>
          //                      </div>`;
          //   }
          //   return tooltipHtml
          // }
        },
        grid: {
          top: '14%',
          left: '3%',
          right: '2%',
          bottom: '1%',
          containLabel: true
        },
        xAxis: {
          type: props.isRequestType >= 4 ? 'category' : 'time',
          splitNumber: 3,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            showMinLabel: true,
            // showMaxLabel: true,
            // alignMinLabel: "left",
            // alignMaxLabel: "right"
          },
          axisTick: {show: false},
          data: []
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        yAxis: {
          type: 'value',
          name: props.yAxisName ?? "",
          splitLine: {
            // show: false,
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
        },
        series: []
      }
    });

    //查看数据绑定的系统变量
    const findVarCodeListFun = () => {
      let varCodeList = "",tableHeader = [];
      let chartOption = cloneDeep(that.chartOption);
      if (props.legend) chartOption.legend = JSON.parse(JSON.stringify(props.legend ?? {}));
      if (props.yAxis) chartOption.yAxis = JSON.parse(JSON.stringify(props.yAxis));

      let formatter = "{HH}:{mm}";
      if(props.timerType === 2){
        if(that.tabsIndex === 3) formatter = "{yyyy}";
        if(that.tabsIndex === 2) formatter = "{yyyy}-{MM}";
        if(that.tabsIndex === 1) formatter = "{yyyy}-{MM}-{dd}";
      }

      if (props.seriesListArray && props.seriesListArray.length) {
        let seriesListArray = JSON.parse(JSON.stringify(props.seriesListArray));
        for (let i = 0; i < seriesListArray.length; i++) {
          let active_field_name = seriesListArray[i].fieldName;
          // 处理 调用的系统变量或者功能点
          if(props.isHandleVarName) active_field_name = seriesListArray[i]['fieldName_' + that.tabsIndex];
          seriesListArray[i].fieldName = active_field_name; // 设置当前数据的来源
          if(active_field_name) varCodeList += active_field_name + ','; // 所有变量or功能点拼接
          tableHeader.push({
            width: 25,
            fieldName: active_field_name,
            name: seriesListArray[i].name,
            key: pinyin.convertToPinyin(seriesListArray[i].name),
          });

          // 设置 曲线渐变样式
          if(seriesListArray[i].type === "bar") chartOption.tooltip.axisPointer = { type: 'shadow' };
          if(seriesListArray[i].type === "line" && !seriesListArray[i].isNoAreaStyle){
            chartOption.xAxis.boundaryGap = true;
            let line_color = seriesListArray[i].color ? seriesListArray[i].color : chartOption.color[i];
            seriesListArray[i].areaStyle = {
              color: {
                type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                colorStops: [
                  {offset: 0, color: colorHexTurnRgba(line_color,0.0)},
                  {offset: 1, color: colorHexTurnRgba(line_color,0.5)}
                ],
              }
            };
          }
        }

        if(props.isRequestType < 4) chartOption.xAxis.axisLabel.formatter = formatter;
        chartOption.series = JSON.parse(JSON.stringify(seriesListArray));
      }

      // console.log(chartOption);
      that.chartOption = cloneDeep(chartOption);
      that.varCodeList = varCodeList.slice(0, -1);
      that.tableHeader = JSON.parse(JSON.stringify(tableHeader));
      if (props.siteId && that.varCodeList) querySystemVarOrFunctionCurveData();
    };

    // 查询系统变量曲线数据
    const querySystemVarOrFunctionCurveData = () => {
      let requestData = {};
      that.listLoading = true;

      if (props.timerType === 1) {
        requestData["timeInterval"] = "1m";
        requestData["startTime"] = that.timeDate + " 00:00:00";

        requestData["isCurrent"] = isToday(that.timeDate) ? 1 : 0;
        requestData["endTime"] = requestData["isCurrent"] ? getNowDateAll() : that.timeDate + " 23:59:59";
      }

      if (props.timerType === 2) {
        if (that.tabsIndex === 1) {
          requestData['timeInterval'] = '1d';
          requestData["isCurrent"] = isToday(that.dayTimeDate[1]) ? 1 : 0;
          requestData['startTime'] = getNowDate(that.dayTimeDate[0]) + " 00:00:00";
          requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : that.dayTimeDate[1] + " 23:59:59";
        }

        if (that.tabsIndex === 2) {
          requestData['timeInterval'] = '1n';
          requestData['startTime'] = getCurrentMonthFirstDay(that.monthTimeDate[0]) + " 00:00:00";

          let endTime = getCurrentMonthLastDay(that.monthTimeDate[1]);
          requestData["isCurrent"] = isMonth(endTime) ? 1 : 0;
          requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : endTime + " 23:59:59";
        }

        if (that.tabsIndex === 3) {
          requestData['timeInterval'] = '1y';
          requestData['startTime'] = getCurrentYearFirstDay(that.yearTimeDate[0]) + " 00:00:00";

          let endTime = getCurrentYearLastDay(that.yearTimeDate[1]);
          requestData["isCurrent"] = isYear(endTime) ? 1 : 0;
          requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : endTime + " 23:59:59";
        }
      }
      if(props.isRequestType === 1) requestData['varCodeList'] = that.varCodeList;
      if(props.isRequestType === 2) requestData['functionLogos'] = that.varCodeList;
      if(props.isRequestType === 3) requestData['dateType'] = that.tabsIndex;
      if(props.isRequestType === 8) requestData['pcsDeviceId'] = props.siteId; //PCS设备唯一id

      requestData['siteId'] = props.siteId;
      requestData['siteIds'] = [props.siteId];
      requestData['queryType'] = that.tabsIndex; // 查询类型(1-日 2-月 3-年)
      if(props.dataIndex || props.dataIndex === 0) requestData['index'] = props.dataIndex + 1; //索引号
      findSystemVarOrFunctionCurveData({...requestData, deviceIdList: props.siteId},props.isRequestType).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ?? {}));

        // 数据处理-->  处理成统一格式
        if(props.isRequestType >= 4){
          if(returnDataInfo.dataInfoList && returnDataInfo.dataInfoList.length){
            for (let i = 0; i < returnDataInfo.dataInfoList.length; i++) {
              if(returnDataInfo.dataInfoList[i].ename){
                returnDataInfo[returnDataInfo.dataInfoList[i].ename] = JSON.parse(JSON.stringify(returnDataInfo.dataInfoList[i].dataList ?? []));
              }
            }
            delete returnDataInfo.dataInfoList;
          }
        }

        let chartOption = cloneDeep(that.chartOption);
        let chartDataInfo = returnDataInfo[props.siteId] ?? returnDataInfo;

        if(props.isRequestType < 4){
          if (chartOption.series && chartOption.series.length) {
            if(chartDataInfo.xAxisList && chartDataInfo.xAxisList.length){
              for (let i = 0; i < chartOption.series.length; i++) {
                chartOption.series[i].data = [];
                for (let j = 0; j < chartDataInfo.xAxisList.length; j++) {
                  chartOption.series[i].data[j] = [chartDataInfo.xAxisList[j],chartDataInfo[chartOption.series[i].fieldName][j]];
                }
              }
            }
          }
        }

        // 数据处理
        if(props.isRequestType >= 4){
          chartOption.xAxis.data = chartDataInfo.xAxisList;
          if (chartOption.series && chartOption.series.length) {
            for (let i = 0; i < chartOption.series.length; i++) {
              chartOption.series[i].data = JSON.parse(JSON.stringify(chartDataInfo[chartOption.series[i].fieldName] ?? []));
            }
          }
        }

        // console.log(chartOption);
        that.chartOption = cloneDeep(chartOption);
        that.formInline = JSON.parse(JSON.stringify(requestData));
        that.returnDataInfo = JSON.parse(JSON.stringify(chartDataInfo));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const chartComponentRef = ref(null);
    const clickDownloadButFun = (operateType) => {
      that.exportLoading = true;
      let fileName = `${props.fileName}_${props.titleName}_${that.formInline.startTime}~${that.formInline.endTime}`;

      if (operateType === 1) {
        const fileUrl = chartComponentRef.value.getDataURL({pixelRatio: 2, backgroundColor: '#FFFFFF'});
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
        downloadFiles(fileUrl, fileName);
        that.exportLoading = false;
      }

      if (operateType === 2) {
        let exportExcelListData = [];
        if (that.returnDataInfo.xAxisList && that.returnDataInfo.xAxisList.length) {
          for (let i = 0; i < that.returnDataInfo.xAxisList.length; i++) {
            exportExcelListData.push({dateTime: that.returnDataInfo.xAxisList[i]});
            for (let j = 0; j < that.tableHeader.length; j++) {
              if (that.returnDataInfo[that.tableHeader[j].fieldName] && that.returnDataInfo[that.tableHeader[j].fieldName].length) {
                exportExcelListData[i][that.tableHeader[j].key] = that.returnDataInfo[that.tableHeader[j].fieldName][i];
              }
            }
          }
        }

        let newTableHeader = [{width: 25, key: "dateTime", name: "时间"}, ...that.tableHeader];
        exportCustomExcel(newTableHeader, exportExcelListData, fileName);
        that.exportLoading = false;
      }
    };

    const watchSiteId = watch([() => props.siteId,()=> props.dataIndex], ([newSiteId]) => {
      if(newSiteId) findVarCodeListFun();
    }, {deep: true,immediate: true});

    return {...toRefs(that), clickDownloadButFun, chartComponentRef, watchSiteId, querySystemVarOrFunctionCurveData, findVarCodeListFun};
  }
});
</script>

<style lang="scss" scoped>
.header_form {
  display: flex;
  align-items: center;
  justify-content: flex-end;

  :deep(.buttonsTabs) {
    width: fit-content;
    margin-right: 8px;

    .tabs_li {
      max-height: 32px;
      margin-right: 4px;
      padding: 6px 10px;
    }
  }

  .download {
    margin-left: 12px;

    .iconfont {
      font-size: 24px;
      color: #ffffffcc;
    }
  }
}

.content_body {
  height: 100%;
  overflow: hidden;
}
</style>