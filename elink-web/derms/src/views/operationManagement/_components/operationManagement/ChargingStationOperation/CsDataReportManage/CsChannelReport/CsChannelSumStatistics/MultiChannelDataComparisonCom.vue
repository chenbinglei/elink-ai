<template>
  <div class="content_body_chart content_border">
    <TableHeaderTitle :title="titleName">
      <template #content>
        <ButtonsTabs v-model:tabs-index="tabsIndex" :tabs-array="tabsArray" @changeEvent="findChartDataFun"></ButtonsTabs>
      </template>
    </TableHeaderTitle>
    <div class="chart_class">
      <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
    </div>
  </div>
</template>

<script lang="ts">
import cloneDeep from "lodash/cloneDeep";
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "MultiChannelDataComparisonCom",
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
      tabsIndex: "eachPlatformChargeQtList",
      tabsArray: [
        {id: "eachPlatformChargeQtList", name: "充电量", unit: "度"},
        {id: "eachPlatformChargeMoneyList", name: "订单金额", unit: "元"},
        {id: "eachPlatformChargeNumList", name: "订单数", unit: "笔"}
      ],

      chartOption: {
        tooltip: {
          confine: true,
          trigger: 'axis',
          borderColor: "#072948",
          backgroundColor: "#072948",
          textStyle: {color: "#FFFFFFCC"},
        },
        grid: {
          top: "5%",
          left: '3%',
          right: '5%',
          bottom: '1%',
          containLabel: true
        },
        dataZoom: [
          {
            type: 'inside',
            yAxisIndex: [0],
            xAxisIndex: false,
          }
        ],
        xAxis: {
          name: "度",
          type: 'value',
          splitLine: {
            show: false,
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.1)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
        },
        yAxis: {
          type: 'category',
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisLabel: {
            color: "#9EA9B5",
            hideOverlap: true,
          },
          axisTick: {show: false},
          data: []
        },
        series: {
          type: 'bar',
          name: '充电量',
          barMaxWidth: 18,
          itemStyle: {
            borderRadius: [0, 6, 6, 0],
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [
                {
                  offset: 0,
                  color: '#00F7FF89', // 0% 处的颜色
                },
                {
                  offset: 1,
                  color: '#46e9f7', // 100% 处的颜色
                },
              ]
            },
          },
          label: {
            show: true,
            precision: 2,
            color: "#9EA9B5",
            position: 'right',
            valueAnimation: true
          },
          data: []
        }
      }
    });

    const findChartDataFun = ()=>{
      let findItem = that.tabsArray.find(item=> item.id === that.tabsIndex);
      let chartOption = cloneDeep(that.chartOption);
      chartOption.xAxis.name = findItem.unit;
      chartOption.series.name = findItem.name;
      chartOption.series.data = that.return_data_info[that.tabsIndex];
      chartOption.yAxis.data = that.return_data_info.platformXaxisList;
      chartOption.tooltip.valueFormatter = (value) => `${ value ?? '-' } ${ findItem.unit }`;
      that.chartOption = cloneDeep(chartOption);
    };

    const watchReturnDataInfo = watch([() => props.returnDataInfo], ([newReturnDataInfo]) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo));
      findChartDataFun();
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchReturnDataInfo, findChartDataFun};
  }
});
</script>

<style lang="scss" scoped>
.content_body_chart {
  padding: 14px 12px;
  box-sizing: border-box;

  :deep(.buttonsTabs) {
    width: fit-content;

    .tabs_li {
      padding: 4px 16px;
    }
  }

  .chart_class {
    width: 100%;
    height: 360px;
  }
}
</style>