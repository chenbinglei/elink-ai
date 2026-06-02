<template>
  <stadetailtemp :type="1" :rightTopList="rightTopList"
   @querySystemVarOrCurveDataTop="searchSystemCurve" @querySystemVarOCurveDataBot="searchPowerGeneration">
    <template v-slot:rightTop>
      <EChartsCategory
        ref="echartTop"
        width="100%"
        unit="kW"
        height="100%"
        :data="chartsDataTop"
      ></EChartsCategory>
    </template>
    <template v-slot:rightBottom>
      <EchartsBar2DBg
        ref="echartBottom"
        width="100%"
        height="100%"
        unit="kWh"
        :data="chartsDataBottom"
      ></EchartsBar2DBg>
    </template>
  </stadetailtemp>
</template>
<script setup>
import { reactive, toRefs, ref, onMounted, watch,defineProps } from "vue";
import * as echarts from "echarts";
import EChartsCategory from "@/components/echart2/echartsCategory.vue";
import EchartsBar2DBg from "@/components/echart2/echartsBar2DBg.vue";
import { stadetailtemp } from "@/views/stationDetails/components";
import SystemMonitorController from "@/api/together/systemMonitor";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDate,
  getNowDateAll, isMonth, isToday, isYear, pickerDateOneMonthDay, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import { useRoute } from "vue-router";
import moment from "moment";
const route = useRoute();
const state = reactive({
  chartsDataTop: { names: [], datas: [] },
  chartsDataBottom: { names: [], datas: [] },
});
const props = defineProps({
  siteId: {
    type: String,
    default: "",
  },
});


const rightTopList = ref([])

const echartTop = ref(null);
const loadChartDataTop = (dataInfoList,dateList) => {
  const lineStyle1 = {
    shadowColor: "#3BAAF5",
    shadowBlur: 10,
    shadowOffsetY: 10,
    width: 2,
  };
  const lineStyle2 = {
    shadowColor: "#34E800",
    shadowBlur: 10,
    shadowOffsetY: 10,
    width: 2,
  };
  let data1 = {
    color: "#00CCFF99",
    name: dataInfoList[0].name,
    needArea: false,
    datas: dataInfoList[0].dataList,
    series: { lineStyle: lineStyle1 },
  };
  let data2 = {
    color: "#34E80099",
    name: dataInfoList[1].name,
    needArea: false,
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
  let data = {
    color: color1,
    name:dataInfoList[0].name,
    needArea: false,
    datas: dataInfoList[0].dataList
  };
  return {
    names: dateList,
    datas: [data],
  };
};

const format = "YYYY-MM-DD";
const date = ref(moment().format(format));
// 查询功率曲线
const searchSystemCurve = (res = {} ) => {

  let endTime = res.endTime ? (moment(res.endTime).isSame(moment(), 'day') ? moment().format('YYYY-MM-DD HH:mm:ss') : res.endTime) : moment().format('YYYY-MM-DD HH:mm:ss')
  SystemMonitorController.findSystemCurve({
    dataId: props.siteId,
    startTime: res?.startTime || moment(date.value).startOf("day").format("YYYY-MM-DD HH:mm:ss"),
    endTime,
    formatInterval: "6",
    timeInterval: "1m",
    type: 1
  }).then(res=>{

    state.chartsDataTop = loadChartDataTop(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询发电量
const searchPowerGeneration = (data = {}) => {``
  let time = pickerDateOneMonthDay(29)
  let res = {
    dataId: props.siteId,
    startTime: data?.startTime  || time[0],
    endTime: data?.endTime || time[1],
    formatInterval: data?.formatInterval || "2",
    timeInterval: data.timeInterval || "1d",
    type: 2
  }
  SystemMonitorController.findSystemCurve(res).then(res=>{

    state.chartsDataBottom = loadChartDataBottom(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询统计数据
const searchTotalData = () => {
  SystemMonitorController.findSystemPvData(props.siteId).then(res=>{
    rightTopList.value =  [
      { value: res.data.totalPower, unit: "kW", text: "当前功率"},
      { value: res.data.realPowerNorm, unit: "%", text: "实时功率归一化" },
      { value: res.data.dayQt, unit: "kWh", text: "今日发电量" },
      { value: res.data.dayHours, unit: "h", text: "今日等效发电时长" },
    ] 
  })
}

watch(props.siteId, (newId) => { 
  if(props.siteId){
    searchSystemCurve()
    searchPowerGeneration()
    searchTotalData()
  }
}, {immediate: true})

const { chartsDataTop, chartsDataBottom } = toRefs(state)
</script>