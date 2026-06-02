<template>
  <div class="content_body" :style="{ height: tableMaxHeight + 'px' }" v-loading="listLoading">
    <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script>
import {exportCustomEcharts} from "@/common/common/exportExcel";
import {reactive, toRefs, watch, ref, defineComponent, getCurrentInstance} from "vue";

export default defineComponent({
  name: "DataQueryCharts",
  props:{
    tableMaxHeight:{
      type: Number,
      default: 300
    },
    xaxisList: {
      type: Array,
      default: () => []
    },
    dataInfoList: {
      type: Array,
      default: () => []
    },
    listLoading:{
      type: Boolean,
      default: false
    }
  },
  emits:["update:listLoading"],
  setup(props) {
    const {emit} = getCurrentInstance();
    const chartComponentRef = ref(null);

    const that = reactive({
      chartOption: {
        tooltip: { trigger: 'axis' },
        legend: {
          top: 10,
          right: 25,
          icon: 'rect',
          itemGap: 15,
          itemWidth: 15,
          itemHeight: 3,
          data: []
        },
        grid: {
          left: '3%',
          right: '3%',
          bottom: '8%',
          top: '10%',
          containLabel: true
        },
        dataZoom: [
          { type: 'inside', start: 0, end: 100 },
          {end: 100, start: 0, show: true, height: 20, type: "slider"}
        ],
        xAxis: {
          data: [],
          type: 'category',
          boundaryGap: false,
          axisTick: { show: false },
        },
        yAxis: { type: 'value' },
        series: []
      },
    })

    // 导出查询数据
    const exportSearchDataFun = (fileName)=>{
      // console.log(chartComponentRef.value);
      exportCustomEcharts(chartComponentRef,fileName);
    }

    // 监听数据发生改变执行
    const watchDataInfoList = watch(()=> props.dataInfoList,(newDataInfoList)=>{
      let seriesArray = [],legendData = [];
      for(let i = 0;i < newDataInfoList.length;i++){
        seriesArray.push({
          type: 'line',
          smooth: true,
          symbol: "none",
          name: newDataInfoList[i].name,
          data: newDataInfoList[i].dataList
        });
        legendData.push(newDataInfoList[i].name);
      }

      that.chartOption.series = JSON.parse(JSON.stringify(seriesArray));
      that.chartOption.legend.data = JSON.parse(JSON.stringify(legendData));
      that.chartOption.xAxis.data = JSON.parse(JSON.stringify(props.xaxisList));

      if(chartComponentRef && chartComponentRef.value){
        chartComponentRef.value.setOption(that.chartOption,true);
      }

      emit("update:listLoading",false);
    },{ deep: true,immediate: true })

    return {...toRefs(that), watchDataInfoList, chartComponentRef, exportSearchDataFun}
  }
})
</script>

<style scoped lang="scss">

</style>
