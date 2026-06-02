<template>
  <div class="content_body_chart content_border">
    <TableHeaderTitle :title="titleName"></TableHeaderTitle>
    <div class="chart_class">
      <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
    </div>
  </div>
</template>

<script>
import {defineComponent, reactive, toRefs, watch, ref} from "vue";

export default defineComponent({
  name: "MultiChannelElectricityTrendCom",
  props: {
    titleName: {
      type: String,
      default: ""
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      return_data_info: {},
      chartOption: {
        legend: {
          top: 0,
          type: 'scroll',
          left: 'center',
          orient: 'horizontal',
          textStyle: { color: '#9EA9B5' },
          pageTextStyle:{ color: '#9EA9B5' },
          pageIconInactiveColor: "#ffffff1a",
          pageIconColor: "#ffffffcc",
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? '-' } 度`,
        },
        grid: {
          left: '3%',
          right: '2%',
          bottom: '1%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          splitNumber: 3,
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            showMinLabel: true,
            showMaxLabel: true,
            alignMinLabel: "left",
            alignMaxLabel: "right",
          },
          axisTick: {show: false},
          data: []
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        yAxis: {
          name: "度",
          type: 'value',
          splitLine: {
            // show: false,
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          // position: 'top',
          axisLabel: {color: "#9EA9B5"},
        },
        series: []
      },
      series_obj:{
        type: 'line',
        // smooth: true,
        showSymbol: false,
        areaStyle: { opacity: 0.2 },
        data: []
      }
    });

    const chartComponentRef = ref(null);
    const watchReturnDataInfo = watch([() => props.returnDataInfo], ([newReturnDataInfo]) => {
      // that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      let seriesArray = [];
      if(newReturnDataInfo.platformQtInfoList && newReturnDataInfo.platformQtInfoList.length){
        for(let i = 0;i < newReturnDataInfo.platformQtInfoList.length;i++){
          let series_obj = JSON.parse(JSON.stringify(that.series_obj));
          series_obj.name = newReturnDataInfo.platformQtInfoList[i].platformName;
          series_obj.data = newReturnDataInfo.platformQtInfoList[i].platformQtList;
          seriesArray.push(series_obj);
        }
      }

      that.chartOption.xAxis.data = newReturnDataInfo.xaxisList;
      that.chartOption.series = JSON.parse(JSON.stringify(seriesArray));
      if(!seriesArray.length) chartComponentRef.value?.clear();
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchReturnDataInfo, chartComponentRef};
  }
});
</script>

<style lang="scss" scoped>
.content_body_chart {
  padding: 14px 12px;
  box-sizing: border-box;

  .chart_class {
    width: 100%;
    height: 360px;
  }
}
</style>