<template>
  <div class="lineChartVSAndPC">
    <v-chart :option="chartOption" autoresize></v-chart>
  </div>
</template>
<script>
import cloneDeep from "lodash/cloneDeep";
import {reactive, toRefs, watch} from "vue";

export default {
  name: "LineChartVSAndPC",
  props:{
    chartInfo:{
      type: Object,
      default:()=>{
        return{};
      }
    },
    xAxisList:{
      type: Array,
      default:()=>[]
    },
    yAxisName:{
      type: String,
      default: ""
    },
  },
  setup(props) {
    const that = reactive({
      chartOption: {
        color: ["#13CBE3"],
        legend:{
          icon: 'rect',
          itemHeight: 3,
          itemWidth: 16,
          left: "right",
          textStyle: {color: '#ffffffcc'},
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? '-' } ${ props.yAxisName }`,
        },
        grid: {
          top: "12%",
          left: '3%',
          right: '2%',
          bottom: '4%',
          containLabel: true
        },
        dataZoom: [{type: 'inside', start: 0, end: 100}],
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#FFFFFF1A"
            }
          },
          axisTick:{show: false},
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            showMinLabel: true,
            showMaxLabel: true,
            alignMinLabel: "left",
            alignMaxLabel: "right",
          },
          data: props.xAxisList
        },
        yAxis: {
          name: "kW",
          type: 'value',
          splitLine: {
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
          nameTextStyle: {color: "#9EA9B5"},
        },
        series: []
      },
    });

    const watchChartInfo = watch([() => props.chartInfo,()=>props.xAxisList], ([newChartInfo,newXAxisList]) => {
      // console.log(newChartInfo);
      let chartOption = cloneDeep(that.chartOption);
      chartOption.xAxis.data = newXAxisList;
      chartOption.color = newChartInfo.color;
      chartOption.series = newChartInfo.series;
      chartOption.yAxis.name = newChartInfo.yAxisName;
      that.chartOption = cloneDeep(chartOption);
    }, { deep: true,immediate:true });

    return {...toRefs(that), watchChartInfo};
  }
};
</script>
<style lang="scss" scoped>
.lineChartVSAndPC {
  height: 100%;
}
</style>