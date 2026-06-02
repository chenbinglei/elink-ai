<template>
  <div v-resize="setTableMaxHeight" class="content_chart">
    <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script>
import cloneDeep from "lodash/cloneDeep";
import {colorHexTurnRgba} from "@/utils";
import {defineComponent, reactive, toRefs, watch, ref} from "vue";

export default defineComponent({
  name: "RevenueStAndOperatingEfChart",
  props: {
    unit: {
      type: String,
      default: ""
    },
    legendShow: {
      type: Boolean,
      default: true
    },
    boundaryGap: {
      type: Boolean,
      default: false
    },
    dateList: {
      type: Array,
      default: () => []
    },
    seriesListArray: {
      type: Array,
      default: () => []
    },
  },
  setup(props) {
    const that = reactive({
      chartOption: {
        color: ["#8979FF", "#FF928A", "#3CC3DF"],
        legend: {
          top: 0,
          left: 'center',
          show: props.legendShow,
          textStyle: {color: '#9EA9B5'},
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
          valueFormatter: (value) => `${ value ?? '-' } ${ props.unit }`,
        },
        grid: {
          left: '3%',
          right: '2%',
          bottom: '1%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          splitNumber: 2,
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
            // showMinLabel: true,
            // showMaxLabel: true,
            // alignMinLabel: "left",
            // alignMaxLabel: "right"
          },
          axisTick: {show: false},
          data: []
        },
        dataZoom: [{type: "inside", start: 0, end: 100}],
        yAxis: {
          // name: "",
          type: 'value',
          splitLine: {
            show: true,
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
    });

    const chartComponentRef = ref(null);
    const setTableMaxHeight = () => {
      // console.log(chartComponentRef);
      chartComponentRef.value?.resize();
    };

    const watchChartOptFun = watch([() => props.seriesListArray, () => props.dateList], ([newSeriesListArray, newDateList]) => {
      let chartOption = cloneDeep(that.chartOption);
      delete chartOption.tooltip.axisPointer;

      chartOption.yAxis.name = props.unit;
      chartOption.legend.show = props.legendShow;
      chartOption.xAxis.boundaryGap = props.boundaryGap;
      chartOption.xAxis.data = JSON.parse(JSON.stringify(newDateList));

      let chart_series = JSON.parse(JSON.stringify(newSeriesListArray));
      if(chart_series && chart_series.length){
        for(let i = 0;i < chart_series.length;i++){
          if(chart_series[i].type === "line"){
            let line_color = chart_series[i].color ? chart_series[i].color : chartOption.color[i];
            chart_series[i].areaStyle = {
              color: {
                type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                colorStops: [
                  {offset: 0, color: colorHexTurnRgba(line_color,0)},
                  {offset: 1, color: colorHexTurnRgba(line_color,0.5)}
                ],
              }
            };
          }
          if(chart_series[i].type === "bar") chartOption.tooltip.axisPointer = { type: 'shadow' };
        }
      }
      chartOption.series = JSON.parse(JSON.stringify(chart_series));
      that.chartOption = cloneDeep(chartOption);
    }, {deep: true});

    return {...toRefs(that), watchChartOptFun, chartComponentRef, setTableMaxHeight};
  }
});
</script>

<style lang="scss" scoped>
.content_chart {
  width: 100%;
  height: 100%;
}
</style>