<template>
  <div ref="chart" class="echarts-container"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  IncomeDate: {
    type: Object,
    required: true,
    default: () => ({})
  },
  type: {
    type: String,
    default: 'line',
    validator: (value) => ['line', 'bar'].includes(value)
  }
});

const chart = ref(null);
let myChart = null;

// 初始化图表
const initChart = () => {
  if (!chart.value) return;
  myChart = echarts.init(chart.value);
  updateChart();
};

// 更新图表
const updateChart = () => {
  const option = getOption();
  myChart.setOption(option);
};

// 获取图表配置
const getOption = () => {
  const xAxisData = props.IncomeDate.dateList || [];
  const seriesData = props.IncomeDate.incomeList || [];
  const totalIncomeList = props.IncomeDate.totalIncomeList || [];
  const totalIncomeRateList = props.IncomeDate.totalIncomeRateList || [];
  if (props.type === 'line') {
    return {
      tooltip: {
        trigger: 'axis',
        className: "custom-tooltip-box",
        formatter: (params) => {
          let htmlText = `<div class='custom-tooltip-style'>
              <div class='custom-tooltip-title'>${params[0].name}</div>
             <div class='custom-tooltip-content'>
               <div class='custom-tooltip-title'>${params[0].seriesName}</div>
              <div class="custom-tooltip-value">${params[0].value} 元 </div></div>
              </div>`;
          return htmlText;
        }
      },
      legend: {
        data: ['收益'],
        right: '50',
        textStyle: {
          color: '#fff', // 图例文字颜色
          fontSize: 12,
        }
      },
      xAxis: {
        type: 'category',
        data: xAxisData,
        boundaryGap: false,

        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: "rgba(255,255,255,0.3)"
          }
        }
      },
      grid: {
        top: '15%',
        left: '4%',
        right: '5%',
        bottom: '5%',
        containLabel: true
      },
      yAxis: {
        type: 'value',
        name: "(元)",
        splitLine: {
          show: true,
          lineStyle: {
            type: 'dashed',
            color: "rgba(255,255,255,0.1)"
          }
        },
        nameTextStyle: {
          fontSize: 10,       // 设置字体大小
        },
        axisLabel: {
          color: '#0073FF',
          formatter: function (value) {
            const str = value.toString();
            if (str.includes('.')) {
              const parts = str.split('.');
              if (parts[1].length > 8) {
                return value.toFixed(8); // 超过 8 位小数时保留 8 位
              }
            }
            return value; // 否则保留原样
          }
        }
      },
      series: [
        {
          name: '收益',
          type: 'line',
          data: seriesData,
          smooth: true,
          lineStyle: {
            color: '#13C889', // 折线颜色
            width: 1 // 折线粗细（单位：像素）
          },
          itemStyle: {
            color: '#13C889', // 圆点颜色
          },
          areaStyle: {
            color: 'rgba(109,210,231, 0.3)' // 可选：设置填充颜色
          },
        }
      ]
    };
  } else {
    return {
      tooltip: {
        trigger: 'axis',
        className: "custom-tooltip-box",
        formatter: (params) => {
          let htmlText = `
          <div class='custom-tooltip-style'>
           <div class='custom-tooltip-title'>${params[0].name}</div> `;
          params.forEach((item, index) => {
            let value = item.value;
            let units = '元';
            // 根据单位类型做数值转换
            if (index === 0) {
              // 万元：除以 10000
              value = (item.value).toFixed(2);
              // if (Math.abs(value)  >= 10000) {
              //   value = (value / 10000).toFixed(2);
              //   units = '万元';
              // } else {
              //   value = value.toFixed(2); // 保留两位小数
              // }
            } else {
              // 百分比：除以 100
              value = (item.value).toFixed(2);
            }

            const unit = index === 0 ? units : '%';
            htmlText += `
        <div class='custom-tooltip-content'>
          <div class='custom-tooltip-title'>${item.seriesName}:</div>
          <div class="custom-tooltip-value">${value} ${unit}</div>
        </div>`;
          });

          htmlText += `</div>`;
          return htmlText;
        }
      },
      legend: {
        data: ['累计收益', '累计收益率'],
        right: '80',
        textStyle: {
          color: '#fff',
          fontSize: 12,
        }
      },
      xAxis: {
        type: 'category',
        data: xAxisData,
        boundaryGap: false,
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: "rgba(255,255,255,0.3)"
          }
        }
      },
      yAxis: [
        {
          type: 'value',
          alignTicks: true,
          splitNumber: 5,
          name: '万元',
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed',
              color: "rgba(255,255,255,0.1)",
            }
          },
          nameTextStyle: {
            fontSize: 10,       // 设置字体大小
          },
          axisLabel: {
            color: '#0073FF',
            formatter: (value) => {
              const number = value / 10000;
              // return value / 10000; // 万元：除以 10000
              const str = number.toString();
              if (str.includes('.')) {
                const parts = str.split('.');
                if (parts[1].length > 8) {
                  return number.toFixed(8); // 超过 8 位小数时保留 8 位
                }
              }
              return number; // 否则保留原样
            }
          }
        },
        {
          type: 'value',
          // alignTicks: true,
          splitNumber: 5,
          name: '%',
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed',
              color: "rgba(255,255,255,0.1)",
            }
          },
          nameTextStyle: {
            fontSize: 10,       // 设置字体大小
          },

          axisLabel: {
            color: '#0073FF',
            formatter: (value) => {
              const str = value.toString();
              if (str.includes('.')) {
                const parts = str.split('.');
                if (parts[1].length > 8) {
                  return value.toFixed(8); // 超过 8 位小数时保留 8 位
                }
              }
              // return value; // 否则保留原样
              return value + '%'; // 百分比：除以 100 并加 %
            }
          },
          nameLocation: 'end',
          nameGap: 30
        }
      ],
      grid: {
        top: '18%',
        left: '4%',
        right: '4%',
        bottom: '0%',
        containLabel: true
      },
      series: [
        {
          name: '累计收益',
          type: 'line',
          data: totalIncomeList,
          lineStyle: {
            color: '#13C889',
            width: 1
          },
          itemStyle: {
            color: '#13C889'
          },
          yAxisIndex: 0
        },
        {
          name: '累计收益率',
          type: 'line',
          data: totalIncomeRateList,
          lineStyle: {
            color: '#146AE1',
            width: 1
          },
          itemStyle: {
            color: '#146AE1'
          },
          yAxisIndex: 1
        }
      ]
    }

  }

};

// 响应数据变化
watch(() => props.IncomeDate, () => {
  if (myChart) {
    updateChart();
  }
});

// 挂载时初始化
onMounted(() => {
  initChart();
});

// 卸载时销毁
onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose();
  }
});
</script>

<style scoped>
.echarts-container {
  width: 100%;
  height: calc(100% - 30px);
}
</style>