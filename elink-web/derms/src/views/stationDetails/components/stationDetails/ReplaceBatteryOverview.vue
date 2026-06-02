<template>
  <stadetailtemp :type="6" :rightTopList="rightTopList" @querySystemVarOCurveDataBot="searchPowerGeneration" @querySystemVarOrCurveDataTop="searchSystemCurve">
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
  let data2 = {
    color: "#34E80099",
    name:dataInfoList[0].name,
    needArea: false,
    step: true,
    datas: dataInfoList[0].dataList,
    series: { lineStyle: lineStyle2 },
  };
  return {
    names: dateList,
    datas: [data2],
  };
};

const echartBottom = ref(null);
const loadChartDataBottom = (dataInfoList,dateList) => {
  let color1 = new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: "#66FFCC" },
    { offset: 1, color: "#FFFFFF" },
  ]);
  let data1 = {
    color: color1,
    name: dataInfoList[0].name,
    needArea: false,
    datas: dataInfoList[0].dataList,
  };
  return {
    names: dateList,
    datas: [data1],
  };
};
const format = "YYYY-MM-DD";
const date = ref(moment().format(format));
// 查询功率曲线
const searchSystemCurve = (res = {} ) => {
  let endTime = res.endTime ? (moment(res.endTime).isSame(moment(), 'day') ? moment().format('YYYY-MM-DD HH:mm:ss') : res.endTime) : moment().format('YYYY-MM-DD HH:mm:ss')
  SystemMonitorController.findSystemCurve({
    dataId:props.siteId,
    startTime: res?.startTime || moment(date.value).startOf("day").format("YYYY-MM-DD HH:mm:ss"),
    endTime,
    formatInterval: "6",
    timeInterval: "1m",
    type: 22
  }).then(res=>{
    state.chartsDataTop = loadChartDataTop(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询发电量
const searchPowerGeneration = (data = {}) => {
  let time = pickerDateOneMonthDay(29)
  let res = {
    dataId:props.siteId,
    startTime: data?.startTime  || time[0],
    endTime: data?.endTime || time[1],
    formatInterval: data?.formatInterval || "2",
    timeInterval: data.timeInterval || "1d",
    type: 23
  }
  SystemMonitorController.findSystemCurve(res).then(res=>{
    state.chartsDataBottom = loadChartDataBottom(res.data.dataInfoList,res.data.dateList);
  })
}
// 查询统计数据
const searchTotalData = () => {
  SystemMonitorController.findSystemChangeData(props.siteId).then(res=>{
    rightTopList.value =  [
      { value:  res.data.totalPower, unit: "kW", text: "当前功率" },
      { value: res.data.totalChargeQt, unit: "kWh", text: "总充电量" },
      { value: res.data.totalDischargeQt, unit: "万kWh", text: "总耗电量" },
      { value: res.data.soc,unit: "%", text: "可换SOC" },
      { value:res.data.dayChangeCount, unit: "次", text: "今日换电次数" },
      { value:res.data.chargeEnergyRatio, unit: "%", text: "充电能耗占比" },
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