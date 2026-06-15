<template>
  <div ref="echartsComponentRef" class="echarts-component">
    <v-chart ref="meta2dEchartsComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script lang="ts">
import vChart from "vue-echarts";
import {deepClone} from "@meta2d/core";
import {reactive, toRefs, onMounted, defineComponent, ref} from "vue";

export default defineComponent({
  name: "EchartsComponent",
  components: {vChart},
  props: {
    pen_id: {
      type: [String, Number],
      default: ""
    }
  },
  setup() {
    const echartsComponentRef = ref(null);
    const meta2dEchartsComponentRef = ref(null);

    const that = reactive({
      chartOption: {},
      cloneChartOption: {},
    })

    const setChartOptionFun = (activePelData = {}) => {
      try {
        let chartOption = JSON.parse(JSON.stringify(deepClone(activePelData)));
        if(JSON.stringify(chartOption) !== JSON.stringify(that.cloneChartOption)) {

          // seriesReplaceDataName  series名称自动更换
          // differentDimensionDataHandle  不同维度数据处理
          // multipleDataAxesHandle  单数据多轴自动处理
          // systemVarOrFunctionHandle  系统变量&功能点处理
          // 图标配置数据
          let {chartName} = activePelData;
          let {
            option,
            seriesReplaceDataName,
            multipleDataAxesHandle,
            systemVarOrFunctionHandle,
            differentDimensionDataHandle
          } = JSON.parse(JSON.stringify(activePelData.echarts));

          // 折线图、柱状图、饼图
          if (chartName === "lineChart" || chartName === "barChart" || chartName === "basicBarChart") {
            // console.log(option);
            // console.log("seriesReplaceDataName",seriesReplaceDataName);
            // console.log("multipleDataAxesHandle",multipleDataAxesHandle);
            // console.log("differentDimensionDataHandle",differentDimensionDataHandle);
            if (option.series && option.series.length) {
              for (let i = 0; i < option.series.length; i++) {
                // series data 数据为对象格式
                if (Object.prototype.toString.call(option.series[i].data) === '[object Object]') {
                  // 返回数据是数据格式的  系统变量&功能点处理
                  if (systemVarOrFunctionHandle && Array.isArray(option.series[i].data.dataList)) {
                    let dataList = {};
                    // console.log(option.series[i].data);
                    for (let len = 0; len < option.series[i].data.dataList.length; len++) {
                      try {
                        if (option.series[i].data.dataList[len]) {
                          let activeData = JSON.parse(option.series[i].data.dataList[len]);
                          // console.log(activeData);
                          if (Array.isArray(activeData)) {
                            for (let arr = 0; arr < activeData.length; arr++) {
                              if (!dataList[arr]) dataList[arr] = [];
                              dataList[arr][len] = activeData[arr];
                            }
                          } else {
                            if (!dataList[0]) dataList[0] = [];
                            dataList[0][len] = option.series[i].data.dataList[len];
                          }
                        } else {
                          if (!dataList[0]) dataList[0] = [];
                          dataList[0][len] = option.series[i].data.dataList[len];
                        }
                      } catch (e) {
                        console.log(e);
                      }
                    }
                    option.series[i].data.dataList = JSON.parse(JSON.stringify(dataList));
                    // console.log("option.series[i].data.dataList",option.series[i].data);
                  }

                  let seriesDataListArray = [];
                  let series_obj = JSON.parse(JSON.stringify(option.series[i])); // series 配置信息
                  delete series_obj.data
                  // console.log("series_obj",series_obj);

                  // 单数据多轴自动处理
                  if (multipleDataAxesHandle) {
                    if (option.series[i].data.dataList) {
                      // console.log("dataList",option.series[i].data);
                      let seriesDataList = JSON.parse(JSON.stringify(option.series[i].data.dataList));
                      let seriesDataListLen = Object.keys(seriesDataList);
                      for (let key in seriesDataList) {
                        seriesDataListArray.push({
                          ...series_obj,
                          dataList: seriesDataList[key],
                          dateList: option.series[i].data.datelist || option.series[i].data.dateList,
                          chName: `${option.series[i].data.chName}${seriesDataListLen.length > 1 ? ("-" + key) : ""}`,
                        });
                      }
                    }
                  }

                  try {
                    // console.log("seriesDataListArray",seriesDataListArray);
                    for (let k = 0; k < seriesDataListArray.length; k++) {
                      if (seriesReplaceDataName) seriesDataListArray[k].name = seriesDataListArray[k].chName;
                      // 不同维度数据处理
                      if (differentDimensionDataHandle) {
                        let dataList = [];
                        if (seriesDataListArray[k].dateList && seriesDataListArray[k].dateList.length) {
                          for (let time = 0; time < seriesDataListArray[k].dateList.length; time++) {
                            dataList[time] = [new Date(seriesDataListArray[k].dateList[time]).getTime(), seriesDataListArray[k].dataList[time]];
                          }
                        }
                        seriesDataListArray[k].data = dataList;
                      } else {
                        seriesDataListArray[k].data = seriesDataListArray[k].dataList;
                        if (chartName === 'basicBarChart') {
                          option.yAxis.data = JSON.parse(JSON.stringify(seriesDataListArray[k].dateList));
                        } else {
                          option.xAxis.data = JSON.parse(JSON.stringify(seriesDataListArray[k].dateList));
                        }
                        delete seriesDataListArray[k].dataList;
                      }
                      option.series.splice(i, k === 0 ? 1 : 0, seriesDataListArray[k]);
                      if (k !== 0) i++
                    }
                  } catch (e) {
                    console.log(e);
                  }
                }
              }
            }
          }

          // 圆环进度条
          if (chartName === "circularProgressBar" || chartName === "pgCircularProgressBar") {
            //     "color0": "#0ff", // 内环刻度 值起始色
            //     "color1": "#6648FF", // 内环刻度 值结束色
            //     "scaleColor": "#9EA9B5", // 内环刻度默认色
            let {value, color0, color1, scaleColor} = JSON.parse(JSON.stringify(activePelData.echarts));

            // console.log(activePelData);
            // title 为数组类型
            if (Array.isArray(option.title)) {
              for (let i = 0; i < option.title.length; i++) {
                if (option.title[i].isTitleNum) {
                  option.title[i].text = `${value ? value : 0}${option.title[i].unit ?? ''}`;
                }
              }
            }

            if (typeof option.title === 'object') {
              if (option.title.isTitleNum) {
                option.title.text = `${value ? value : 0}${option.title.unit ?? ''}`;
              }
            }

            // 环形进度条 内环刻度值处理
            if (chartName === "pgCircularProgressBar") {
              if (option.series && option.series.length) {
                option.series[1].data = generateProgressScaleFun({value, color0, color1, scaleColor});
              }
            }
          }

          // 电池
          if (chartName === "batteryChart") {
            let {value} = JSON.parse(JSON.stringify(activePelData.echarts));
            if (option.tooltip.formatter) option.tooltip.formatter = `{a}: ${value + "%"}`;
            option.series.data[1] = value / 100;
          }

          // 进度仪表盘
          if (chartName === "progressDashboard") {
            try {
              let {value} = JSON.parse(JSON.stringify(activePelData.echarts));
              option.series[0].axisLine.lineStyle.color[0][0] = value / 100;
            } catch (e) {
            }
          }

          // 跟新配置信息
          // console.log(option);
          if (JSON.stringify(option) !== JSON.stringify(that.chartOption)) {
            that.cloneChartOption = JSON.parse(JSON.stringify(chartOption)); // 克隆画笔对象
            that.chartOption = JSON.parse(JSON.stringify(option));
          }
        }
      } catch (e) {
        //TODO handle the exception
        console.log(e);
      }
    }

    const generateProgressScaleFun = ({value = 0, color0, color1, scaleColor}) => {
      let labelData = [];
      for (let i = 0; i < 100; ++i) {
        let itemStyle = {normal: {color: scaleColor}};
        if (i <= value) itemStyle = {
          "normal": {
            "color": {
              "x": 0,
              "y": 1,
              "x2": 0,
              "y2": 0,
              "global": false,
              "type": "linear",
              "colorStops": [{"offset": 0, "color": color0}, {"offset": 1, "color": color1}],
            }
          }
        };
        labelData.push({name: i, value: 1, itemStyle: itemStyle});
      }
      return labelData
    }

    onMounted(() => {
      echartsComponentRef.value.setChartOptionFun = setChartOptionFun;
    })

    return {
      ...toRefs(that),
      meta2dEchartsComponentRef,
      echartsComponentRef,
      generateProgressScaleFun,
      setChartOptionFun
    }
  }
})
</script>

<style lang="scss" scoped>
.echarts-component {
  width: 100%;
  height: 100%;

  :deep(.vue-echarts-inner) {
    width: 100%;
    height: 100%;
  }
}
</style>