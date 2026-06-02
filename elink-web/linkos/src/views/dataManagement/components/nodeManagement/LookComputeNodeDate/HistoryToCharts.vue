<template>
  <div class="echarts">
    <v-chart ref="chartComponentRef" :option="chartOption" autoresize></v-chart>
  </div>
</template>

<script>
import {reactive, toRefs} from "vue";

export default {
  name: "HistoryToCharts",
  setup() {
    const that = reactive({
      chartOption: {
        tooltip: {
          trigger: 'axis',
          backgroundColor: "rgba(255, 255, 255, 0.9)",
          borderColor: "rgba(255, 255, 255, 0.2)",
          textStyle: {
            color: "#94a1a8"
          },
          confine: true,
          formatter: function (params) {
            // console.log(params);
            var relVal = params[0].name + '<br/>';
            for (var i = 0; i < params.length; i++) {
              relVal +=
                  '<div class="flex jc-space-between">' +
                  '<span>' + params[i].marker + params[i].seriesName + ' : ' + '&nbsp;&nbsp;' + '</span>' +
                  '<span>' +
                  (params[i].value || params[i].value === 0 ? parseFloat(params[i].value).toFixed(2) : "--") +
                  '&nbsp;' + (i > 0 ? 'V' : "kW") +
                  '</span>' +
                  '</div>'
            }
            return relVal
          }
        },
        color: ["#008BFF", "#06DE6F"],
        legend: {
          top: 10,
          right: 25,
          data: ['有功功率', '电压'],
          icon: 'rect',
          itemGap: 15,
          itemWidth: 15,
          itemHeight: 3,
          textStyle: {
            color: '#9EA9B5',
          },
        },
        grid: {
          left: '3%',
          right: '3%',
          bottom: '8%',
          top: '10%',
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
          axisLabel: {
            color: "#9EA9B5",
          },
          axisTick: { //y轴刻度线
            show: false
          },
          data: ["2023-11-26 12:53", "2023-11-26 12:52", "2023-11-26 12:51", "2023-11-26 12:50", "2023-11-26 12:49",
            "2023-11-26 12:48", "2023-11-26 12:47", "2023-11-26 12:46", "2023-11-26 12:45", "2023-11-26 12:44",
            "2023-11-26 12:43", "2023-11-26 12:42", "2023-11-26 12:41", "2023-11-26 12:40", "2023-11-26 12:39",
            "2023-11-26 12:38", "2023-11-26 12:37", "2023-11-26 12:36"],
        },
        yAxis: [{
          type: 'value',
          name: "kW",
          splitLine: {
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.3)",
            }
          },
          axisLabel: {
            color: "#9EA9B5"
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: "#9EA9B5"
            }
          },
        },
          {
            type: 'value',
            name: 'V',
            position: 'right',
            splitLine: {
              lineStyle: {
                type: 'dashed',//y轴分割线类型
                color: "rgba(255,255,255,0.3)",
              }
            },
            axisLabel: {
              color: "#9EA9B5"
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: "#9EA9B5"
              }
            },
          }
        ],
        dataZoom: [{
          type: "slider",
          show: true,
          height: 20,
          textStyle: {
            color: "#FFFFFF"
          },
          fillerColor: "rgba(0, 82, 255, 0.1)",
          borderColor: "rgba(255, 255, 255, 0.3)",
          backgroundColor: 'rgba(0, 82, 255, 0.2)',
          handleStyle: {
            color: "#fff",
            borderColor: '#aab6c6',
          },
          moveHandleStyle: {
            color: "rgba(0, 82, 255, 0.3)",
            borderColor: "rgba(0, 82, 255, 0.3)"
          },
          start: 0,
          end: 100
        }, {
          type: 'inside',
          start: 0,
          end: 100
        }],
        series: [
          {
            name: '有功功率',
            type: 'line',
            smooth: true,
            symbol: "none",
            data: [12.26, 13.76, 4.26, 9.56, 15.75, 14.74, 9.92, 17.45, 13.26, 12.98, 48.45, 15.75, 14.74, 9.92, 17.45,
              13.26, 12.98, 48.45]
          },
          {
            name: '电压',
            type: 'line',
            smooth: true,
            symbol: "none",
            yAxisIndex: 1,
            data: [12.26, 13.76, 4.26, 9.56, 15.75, 4.26, 9.56, 15.75, 4.26, 9.56, 15.75, 4.26, 9.56, 15.75, 4.26, 9.56,
              15.75, 4.26, 9.56, 15.75, 4.26, 9.56, 15.75]
          },
        ]
      },
    })

    const listArray = (operateType)=>{

    }

    return {
      ...toRefs(that), listArray
    }
  }
}
</script>

<style scoped lang="scss">
.echarts {
  height: 400px;
}
</style>
