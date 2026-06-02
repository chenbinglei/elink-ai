<template>
  <div class="powerCurveChart">
    <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script>
import {colorHexTurnRgba} from "@/utils";
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "PowerCurveChartCom",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      chartOption: {
        color: ["#49E9FF", "#FFE700"],
        grid: {
          top: '1%',
          left: '1%',
          right: '1%',
          bottom: '1%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisTick: {show: false},
          axisLabel: {show: false},
          data: [],
        },
        yAxis: {
          type: 'value',
          splitLine: {show: false},
          axisLabel: {show: false, color: "#9EA9B5"},
        },
        series: [
          {
            data: [],
            type: 'line',
            name: '实时功率',
            showSymbol: false,
            areaStyle: {
              color: {
                type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                colorStops: [
                  {offset: 0, color: colorHexTurnRgba("#49E9FF", 0)},
                  {offset: 1, color: colorHexTurnRgba("#49E9FF", 0.5)}
                ],
              }
            }
          }
        ]
      }
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.chartOption.xAxis.data = newReturnDataInfo?.xaxisList;
      that.chartOption.series[0].data = newReturnDataInfo?.realPowerList;
    }, {deep: true,immediate: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.powerCurveChart {
  width: 100%;
  height: 80px;
}
</style>