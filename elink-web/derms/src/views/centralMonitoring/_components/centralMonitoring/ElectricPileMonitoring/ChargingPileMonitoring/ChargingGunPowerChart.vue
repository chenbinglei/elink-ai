<template>
  <div class="chargingGunPowerChart" v-loading="listLoading">
    <v-chart :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script lang="ts">
import {getNowDate, getNowDateAll} from "@/utils/dateTime";
import {reactive, defineComponent, toRefs, watch} from "vue";
import {findSystemVarOrFunctionCurveData} from "@/api/centralMonitoring/centralMonitoring";

export default defineComponent({
  name: "ChargingGunPowerChart",
  props:{
    activeDeviceInfo:{
      type: Object,
      default: ()=>{
        return { };
      }
    },
    gunWorkState:{
      type: Number,
      default: 0
    },
    gunIndex:{
      type: Number,
      default: 0
    }
  },
  setup(props) {

    const that = reactive({
      listLoading: false,
      chartOption: {
        color: ["#3CFFA7", "#3CC4FF"],
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? "-" } kW`,
        },
        grid: {
          top: "28",
          left: '-14',
          right: '118',
          bottom: '10%',
          containLabel: true
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#ffffff26"
            }
          },
          axisLabel: {
            show: false,
            color: "#9EA9B5",
          },
          axisTick: {show: false},
          data: []
        },
        yAxis: {
          type: 'value',
          position: 'right',
          axisTick: {
            show: false,
            length: 15,
            lineStyle: {
              color: "#ffffff",
            }
          },
          axisLabel: {show: false},
          splitLine: {show: false},
          axisLine: {
            show: true,
            lineStyle: {
              width: 1,
              color: '#ffffff4d',
            }
          }
        },
        series: [
          {
            type: 'line',
            smooth: true,
            name: '输出功率',
            showSymbol: false,
            endLabel: {
              show: true,
              fontSize: 11,
              offset: [0,-8],
              color: "#3CFFA7",
              formatter: `输出功率：{c}kW`
            },
            areaStyle: {color: "rgba(60, 255, 167, 0.2)"},
            data: [],
          },
          {
            type: 'line',
            smooth: true,
            name: '需求功率',
            showSymbol: false,
            endLabel: {
              show: true,
              fontSize: 11,
              offset: [0,8],
              color: "#3CC4FF",
              formatter: `需求功率：{c}kW`
            },
            areaStyle: {color: "rgba(60, 196, 255, 0.2)"},
            data: []
          }
        ]
      }
    });

    // 查询系统变量曲线数据
    const querySystemVarCurveData = ()=>{
      that.listLoading = true;
      findSystemVarOrFunctionCurveData({
        timeInterval: "1m",
        endTime: getNowDateAll(),
        index: props.gunIndex + 1,
        startTime: getNowDate() + " 00:00:00",
        deviceIdList: props.activeDeviceInfo.id,
        functionLogos: "gunrepower,gunpower"
      },2).then(res=>{
        let returnDataInfo = res.data ? res.data : {};
        let chartDataInfo = returnDataInfo[props.activeDeviceInfo.id] ?? returnDataInfo;
        let chartOption = JSON.parse(JSON.stringify(that.chartOption));
        chartOption.xAxis.data = chartDataInfo.xAxisList;
        chartOption.series[0].data = chartDataInfo.gunpower;
        chartOption.series[1].data = chartDataInfo.gunrepower;
        that.chartOption = JSON.parse(JSON.stringify(chartOption));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const watchActiveDeviceInfo = watch([()=>props.activeDeviceInfo,()=>props.gunIndex,()=>props.gunWorkState],([newActiveDeviceInfo])=>{
      if(newActiveDeviceInfo.id) querySystemVarCurveData();
    },{ deep: true,immediate: true});

    return {...toRefs(that), querySystemVarCurveData, watchActiveDeviceInfo };
  }
});
</script>

<style lang="scss" scoped>
.chargingGunPowerChart {
  width: 100%;
  height: 145px;
}
</style>