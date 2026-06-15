<template>
  <div class="historyDataChart">
    <template v-if="returnDataInfo.dateList && returnDataInfo.dateList.length">
      <v-chart ref="chartComponentRef" :option="chart_option" autoresize></v-chart>
    </template>
    <null-data v-else></null-data>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "HistoryDataChart",
  props:{
    returnDataInfo:{
      type:Object,
      default:()=>{
        return { }
      }
    },
    cardInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props){

    const that = reactive({
      chart_option:{
        legend: {
          top: 10,
          right: 25,
          icon: 'rect',
          itemGap: 15,
          itemWidth: 15,
          itemHeight: 3,
          textStyle: { color: '#9EA9B5' },
        },
        dataZoom:[{type: 'inside', minValueSpan: 5}],
        grid: {
          left: '1%',
          right: '1%',
          bottom: '1%',
          top: '14%',
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          fontSize: 9,
          textStyle: {
            color: "#242424",
          },
          confine: true,
        },
        xAxis: {
          type: "category",
          axisTick: { show: false },
          axisLine: {
            lineStyle: {
              color: "rgba(255,255,255,0.2)"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
          },
          splitLine: {
            lineStyle: {
              color: "rgba(255,255,255,0.2)",
            },
          },
          data: []
        },
        yAxis: {
          show: true,
          type: "value",
          nameTextStyle: {
            color: "#9EA9B5",
          },
          splitLine:{
            lineStyle:{
              type:'dashed',//y轴分割线类型
              color:'rgba(255,255,255,0.1)',
            }
          },
          axisLabel: {
            color: "#9EA9B5",
          },
        },
        series: [{
          type: "line",
          name: "value",
          smooth: true,
          symbol: "emptyCircle",
          data: []
        }]
      },
      series_obj:{ type: "line", name: "value", symbol: "emptyCircle", smooth: true, data: [] }
    })

    const watchReturnDataInfo = watch(()=>props.returnDataInfo,(newReturnDataInfo)=>{
      let series_array = [];
      let chart_option = JSON.parse(JSON.stringify(that.chart_option));
      chart_option.xAxis.data = newReturnDataInfo.dateList;
      chart_option.yAxis.show = props.cardInfo.dataType !== 6;

      for(let i = 0;i < newReturnDataInfo.valueListNum;i++){
        let series_obj = JSON.parse(JSON.stringify(that.series_obj));
        series_obj.name = `第${ i + 1 }个值`;
        series_obj.data = newReturnDataInfo['seriesList' + i];
        series_array.push(series_obj);
      }

      chart_option.series = series_array;

      that.chart_option = JSON.parse(JSON.stringify(chart_option));
    },{ deep: true,immediate: true })

    return {  ...toRefs(that),watchReturnDataInfo }
  }
})
</script>

<style lang="scss" scoped>
.historyDataChart {
  width: 100%;
  height: 100%;
}
</style>
