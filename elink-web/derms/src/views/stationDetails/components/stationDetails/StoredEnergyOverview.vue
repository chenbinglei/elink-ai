<template>
  <stadetailtemp :type="2" :rightTopList="rightTopList" @querySystemVarOCurveDataBot="searchPowerGeneration" @querySystemVarOrCurveDataTop="searchSystemCurve">
    <template v-slot:rightTop>
      <EChartsCategory
        ref="echartTop"
        width="100%"
        height="100%"
        :data="chartsDataTop"
      ></EChartsCategory>
    </template>
    <template v-slot:rightBottom>
      <EchartsBar2D
        ref="echartBottom"
        width="100%"
        height="100%"
        unit="kWh"
        :data="chartsDataBottom"
      ></EchartsBar2D>
    </template>
  </stadetailtemp>
</template>
<script setup>
import moment from "moment";
import { reactive, toRefs, ref, watch, onMounted,defineProps } from "vue";
import EChartsCategory from "@/components/echart2/echartsCategory.vue";
import EchartsBar2D from "@/components/echart2/echartsBar2DBg.vue";
import * as echarts from "echarts";
import { stadetailtemp } from "@/views/stationDetails/components";
import SystemMonitorController from "@/api/together/systemMonitor";
import { useRoute } from "vue-router";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDate,
  getNowDateAll, isMonth, isToday, isYear, pickerDateOneMonthDay, pickerOptionsGthanAcTime} from "@/utils/dateTime";
const route = useRoute();
const state = reactive({
  chartsDataTop: { names: [], datas: [] },
  chartsDataBottom: { names: [], datas: [] },
});
const rightTopList = ref([])
const echartTop = ref(null);
const props = defineProps({
  siteId: {
    type: String,
    default: "",
  },
});
const loadChartDataTop = (dataInfoList,dateList) => {
  const lineStyle1 = {
    // 设置阴影颜色
    shadowColor: "#3BAAF5",
    shadowBlur: 10,
    // 设置阴影沿y轴偏移量为10
    shadowOffsetY: 10,
    width: 2,
  };
  const lineStyle2 = {
    // 设置阴影颜色
    shadowColor: "#34E800",
    // 设置阴影沿x轴偏移量为10
    // shadowOffsetX: 10,
    shadowBlur: 10,
    // 设置阴影沿y轴偏移量为10
    shadowOffsetY: 10,
    width: 2,
  };
  let data1 = {
    color: "#00CCFF99",
    name: dataInfoList[0].name,
    needArea: false,
    step: true,
    datas: dataInfoList[0].dataList,
    series: { lineStyle: lineStyle1 },
  };
  console.log(dataInfoList,'dataInfoList')
  let data2 = {
    color: "#34E80099",
    name: dataInfoList[1].name,
    needArea: false,
    step: true,
    datas: dataInfoList[1].dataList,
    series: { lineStyle: lineStyle2 },
  };
  //渐变色定义
  data1.areaColor = {
    linear: [0, 0, 0, 1],
    colors: [
      { color: data1.color, offset: 0, alpha: 0.4 },
      { color: data1.color, offset: 0.2, alpha: 0.1 },
      { color: data1.color, offset: 0.3, alpha: 0 },
      { color: data1.color, offset: 1, alpha: 0 },
    ],
  };
  return {
    names: dateList,
    datas: [data1,data2],
  };
};

const echartBottom = ref(null);
const loadChartDataBottom = (dataInfoList,dateList) => {
  let color1 = new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: "#66FFCC" },
    { offset: 1, color: "#FFFFFF" },
  ]);
  let color2 = new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: "#FFA366" },
    { offset: 1, color: "#FFFFFF" },
  ]);
  let data1 = {
    color: color1,
    name: dataInfoList[0].name,
    needArea: false,
    datas: dataInfoList[0].dataList,
  };
  let data2 = {
    color: color2,
    name: dataInfoList[1].name,
    needArea: false,
    datas: dataInfoList[1].dataList,
  };
  return {
    names: dateList,
    datas: [data1,data2],
  };
};
const format = "YYYY-MM-DD";
const date = ref(moment().format(format));
// 查询功率曲线
const searchSystemCurve = (res = {} ) => {
  let endTime = res.endTime ? (moment(res.endTime).isSame(moment(), 'day') ? moment().format('YYYY-MM-DD HH:mm:ss') : res.endTime) : moment().format('YYYY-MM-DD HH:mm:ss')
  SystemMonitorController.findSystemCurve({
    dataId: route.query.siteId,
    startTime: res?.startTime || moment(date.value).startOf("day").format("YYYY-MM-DD HH:mm:ss"),
    endTime,
    formatInterval: "6",
    timeInterval: "1m",
    type: 8
  }).then(res=>{
    state.chartsDataTop = loadChartDataTop(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询发电量
const searchPowerGeneration = (data = {}) => {
  let time = pickerDateOneMonthDay(29)
  let res = {
    dataId: route.query.siteId,
    startTime: time[0],
    endTime: time[1],
    formatInterval: "2",
    timeInterval: "1d",
    type: 9
  }
  SystemMonitorController.findSystemCurve({...res,...data}).then(res=>{
    state.chartsDataBottom = loadChartDataBottom(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询统计数据
const searchTotalData = () => {
  SystemMonitorController.findSystemSeData(route.query.siteId).then(res=>{
    rightTopList.value =  [
      { value:  res.data.totalPower, unit: "kW", text: "当前功率" },
      { value:  res.data.dayChargeQt + '/' + res.data.dayDischargeQt, unit: "kWh", text: "今日充/放电量" },
      { value:  res.data.soc, unit: "%", text: "SOC", class: "green" },
      { value:  res.data.sumChargeCycleNum, unit: "次", text: "累计循环次数" },
      { value:  res.data.lastDay30Eff, unit: "%", text: "近30日综合效率", class: "yellow" },
    ] 
  })
}
watch(props.siteId, (newId) => { 
  if(props.siteId){
    searchSystemCurve()
    searchPowerGeneration()
    searchTotalData()
  }
},{immediate: true})
const { chartsDataTop, chartsDataBottom } = toRefs(state)
</script>