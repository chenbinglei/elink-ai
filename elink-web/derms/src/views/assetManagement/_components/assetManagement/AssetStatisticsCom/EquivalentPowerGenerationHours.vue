<template>
  <div class="equivalentPowerGenerationHours">
    <title-view title="今日等效发电小时">
      <template #headerRight>
        <span class="number">{{ $filters.moreData(returnDataInfo.waitOutHour) }}</span>
        <span class="unit">h</span>
      </template>
      <template #content>
        <div class="content_chart">
          <v-chart :option="chartOption" autoresize></v-chart>
        </div>
      </template>
    </title-view>
  </div>
</template>

<script lang="ts">
import {calcNumberFun} from "@/utils";
import {reactive, defineComponent, toRefs, watch} from "vue";

export default defineComponent({
  name: "EquivalentPowerGenerationHours",
  props: {
    totalHour: {
      type: Number,
      default: 24
    },
    timeCountDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      returnDataInfo: {},
      chartOption: {
        backgroundColor: '#081a37',
        xAxis: {
          min: 0,
          max: 100,
          show: false,
        },
        yAxis: [
          {
            show: false,
            type: 'category',
            inverse: false,
            // position: 'left',
          },
        ],
        grid: {
          top: 0,
          left: 0,
          right: 0,
          height: 15,
        },
        series: [
          {
            // current data
            name: '1',
            symbol: 'rect',
            type: 'pictorialBar',
            symbolRepeat: 'fixed',
            symbolMargin: '20%',
            symbolClip: true,
            symbolOffset: [0, 0],
            stack: '11',
            symbolSize: [5, 30],
            symbolBoundingData: 100,
            label: {
              normal: {
                show: false,
              },
            },
            data: [
              {
                value: 0,
                itemStyle: {
                  normal: {
                    color: {
                      type: 'linear',
                      x: 0,
                      y: 0,
                      x2: 0,
                      y2: 1,
                      colorStops: [
                        {
                          offset: 0,
                          color: '#46e9f7', // 0% 处的颜色
                        },
                        {
                          offset: 1,
                          color: '#011753', // 100% 处的颜色
                        },
                      ],
                      globalCoord: false, // 缺省为 false
                    },
                  },
                },
              },
            ],
            z: 99999999,
            animationEasing: 'elasticOut',
            animationDelay: function (dataIndex, params) {
              return params.index * 30;
            },
          }
        ],
      }
    });

    // 监听数据
    const watchTimeCountDataInfo = watch(() => props.timeCountDataInfo, (newTimeCountDataInfo) => {
      // console.log(newTimeCountDataInfo);
      // console.log(props.totalHour);
      that.returnDataInfo = JSON.parse(JSON.stringify(newTimeCountDataInfo));
      let waitOutHour = newTimeCountDataInfo.waitOutHour && newTimeCountDataInfo.waitOutHour !== "Infinity" ? newTimeCountDataInfo.waitOutHour : 0;
      let waitOutHourBfb = calcNumberFun(waitOutHour, props.totalHour, '/');
      that.chartOption.series[0].data[0].value = waitOutHourBfb * 100;
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchTimeCountDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.equivalentPowerGenerationHours {
  margin-top: 12px;

  .number {
    font-weight: 800;
    margin-right: 8px;
  }

  .unit {
    font-size: 14px;
    color: #a1ddfe80;
  }

  .content_chart {
    width: 100%;
    height: 15px;
  }
}
</style>