<template>
  <view class="line-area">
    <!-- 图表容器 -->
    <view v-if="showChart">
      <qiun-data-charts v-if="selectType == '1'" type="mix" :chartData="chartData" :opts="opts"
        style="width: 100%; height: 200rpx;" :canvas2d="true" :ontouch="true" canvas-id="column" inScrollView="true" />
      <qiun-data-charts v-else type="mix" :chartData="chartDatas" :opts="barOpts" style="width: 100%; height: 200rpx;"
        :canvas2d="true" :ontouch="true" canvas-id="column" inScrollView="true" />
    </view>

    <!-- 加载状态 -->
    <view v-if="isLoading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 空状态 -->
    <empty v-if="!isLoading && (!props.lendList?.timeList || props.lendList.timeList.length === 0)"
      style="width: 100%; height: 300rpx;" />
  </view>
</template>

<script setup>
import empty from './empty.vue'
import { ref, watch, onUnmounted } from 'vue';

const props = defineProps({
  lendList: {
    type: Object,
    default: () => ({})
  },
  selectType: {
    type: String,
    default: '1'
  },
  selectDeviceType: {
    type: String,
    default: 'guangkou'
  }
});

// 响应式数据
const chartData = ref(null)
const chartDatas = ref(null)
const showChart = ref(false)
const isLoading = ref(false)

// 图表配置
const opts = ref({
  padding: [10, 15, 20, 0],
  enableScroll: false,
  dataLabel: false,
  animate: false, // 保持动画
  enableZoom: false,
  enablePan: false,
  legend: {
    show: true,
    position: 'top',
    float: 'right',
    margin: 2,
    padding: 5,
    itemGap: 10,
    fontSize: 12,
    color: '#333'
  },
  xAxis: {
    formatter: (value, index) => {
      if (props.selectType === '1') {
        if (/^\d{2}:00$/.test(value)) {
          const hour = parseInt(value.split(':')[0]);
          if (hour % 2 === 0) {
            return hour.toString();
          } else {
            return '';
          }
        } else {
          return '';
        }
      } else if (props.selectType === '2') {
        if (value % 2 === 1) {
          return value.toString();
        }
        return "";
      } else {
        return value;
      }
    }
  },
  extra: {
    line: {
      width: 1,
    },
  },
  yAxis: {
    gridType: "dash",
    dashLength: 2,
    splitNumber: 4, // 7个刻度线需要6个间隔
    grid: {
      show: false
    },
    data: [
      {
        position: "left",
        title: 'kW',
        min: 0,
        max: 100,
        tofix: "0",

      }
    ]
  }
})
const barOpts = ref({
  padding: [10, 15, 20, 0],
  enableScroll: false,
  dataLabel: false,
  animate: false, // 保持动画
  enableZoom: false,
  enablePan: false,
  legend: {
    show: true,
    position: 'top',
    float: 'right',
    margin: 2,
    padding: 5,
    itemGap: 10,
    fontSize: 12,
    color: '#333'
  },
  xAxis: {
    formatter: (value, index) => {
      if (props.selectType === '1') {
        if (/^\d{2}:00$/.test(value)) {
          const hour = parseInt(value.split(':')[0]);
          if (hour % 2 === 0) {
            return hour.toString();
          } else {
            return '';
          }
        } else {
          return '';
        }
      } else if (props.selectType === '2') {
        if (value % 2 === 1) {
          return value.toString();
        }
        return "";
      } else {
        return value;
      }
    }
  },
  extra: {
    line: {
      width: 1,
    },
    column: {
      // type: "group",
      barBorderCircle: true
    },

  },
  yAxis: {
    show: true,
    disableGrid: false,
    gridType: "dash",
    forceZero: true,  // 强制从0开始
    dashLength: 4,
    gridColor: "#CCCCCC",
    padding: 10,
    showTitle: true,
    grid: {
      show: false
    },
    data: [
      {
        position: "left",
        title: 'kW',

      }
    ]
  }
})

// 数据降采样（解决性能问题但保留数据）
const downSampleData = (dataList, targetCount = 200) => {
  if (!Array.isArray(dataList) || dataList.length <= targetCount) {
    return dataList ? [...dataList] : [];
  }

  const step = Math.ceil(dataList.length / targetCount);
  const result = [];

  for (let i = 0; i < dataList.length; i += step) {
    const value = dataList[i];

    // 如果是数字且小数位数 > 4，则保留四位小数
    if (typeof value === 'number') {
      const str = value.toString();
      const decimalIndex = str.indexOf('.');
      if (decimalIndex !== -1 && str.length - decimalIndex - 1 > 4) {
        result.push(Number(value.toFixed(4)));
      } else {
        result.push(value);
      }
    } else {
      result.push(value);
    }
  }

  // 确保最后一个元素是原始数据的最后一个
  if (result.length && result[result.length - 1] !== dataList[dataList.length - 1]) {
    result[result.length - 1] = dataList[dataList.length - 1];
  }

  return result;
};
const calculateDataExtremes = (curve1List, curve2List) => {
  // const max = Math.max(...curve1List, ...curve2List);
  // const min = Math.min(...curve1List, ...curve2List);
  const dataA = curve1List
  const dataB = curve2List
  // 检查是否有负数
  const hasNegative = dataA.some(v => v < 0) || dataB.some(v => v < 0)
  // 转换为绝对值
  const dataAObs = dataA.map(Math.abs)
  const dataBObs = dataB.map(Math.abs)

  // 获取最大值
  const maxLine = Math.max(...dataAObs, ...dataBObs)

  // 设置 min 和 max
  const yAxisMin = hasNegative ? -maxLine : 0
  const yAxisMax = maxLine
  console.log('opts.value.yAxis.data[0].min', yAxisMin, 'opts.value.yAxis.data[0].max', yAxisMax)
  if (yAxisMax == 0 && yAxisMin == 0) {
    opts.value.yAxis.data[0].min = 0;
    opts.value.yAxis.data[0].max = 4;
  } else {
    opts.value.yAxis.data[0].min = yAxisMin;
    opts.value.yAxis.data[0].max = yAxisMax;
  }


}
// 日模式图表
const dateLineCharts = () => {
  const { timeList = [], curve1List = [], curve2List = [] } = props.lendList;
  if (!timeList.length) {
    chartData.value = null;
    showChart.value = false;
    return;
  }

  calculateDataExtremes(curve1List, curve2List);
  const sampledTimeList = downSampleData(timeList, 200);
  const sampledCurve1List = downSampleData(curve1List, 200);
  const sampledCurve2List = downSampleData(curve2List, 200);

  const res = {
    categories: sampledTimeList,
    series: [
      {
        name: "前一日",
        type: "line",
        style: "curve",
        color: "#e99d45",
        smooth: true,
        showPoint: false,
        data: sampledCurve1List,
        legendShape: 'rect',
        // lineType: "dash",
        opacity: 0.2,
        fillColor: "#25D500",
        gradient: true,
        lineWidth: 4
      },
      {
        name: '当日',
        type: "line",
        smooth: true,
        color: "#00BFFF",
        data: sampledCurve2List,
        legendShape: 'rect'
      },
    ]
  };

  chartData.value = res;
  showChart.value = true;
  isLoading.value = false;

};

// 周/月模式图表
const weekLineCharts = () => {
  const { timeList = [], curve1List = [], curve2List = [] } = props.lendList;
  if (!timeList.length) {
    chartData.value = null;
    showChart.value = false;
    return;
  }

  const sampledTimeList = downSampleData(timeList, 150);
  const sampledCurve1List = downSampleData(curve1List, 150);
  const sampledCurve2List = downSampleData(curve2List, 150);

  const seriesName1 = props.selectDeviceType == 'guangkou'
    ? '关口下网电量'
    : props.selectDeviceType == 'chuneng'
      ? '储能充电电量' : props.selectDeviceType == 'dianzhuang'?
  '电桩充电电量': '光伏实际发电量';

  const seriesName2 = props.selectDeviceType == 'guangkou'
    ? '关口上网电量'
    : props.selectDeviceType == 'chuneng'
      ? '储能放电电量': props.selectDeviceType == 'dianzhuang'?
  '电桩放电电量'
      : '光伏理论发电量';

  const res = {
    categories: sampledTimeList,
    series: [
      {
        name: seriesName1,
        type: "column",
        style: "curve",
        color: "#19CE89",
        smooth: true,
        showPoint: false,
        data: sampledCurve1List,
        legendShape: 'circle'
      },
      {
        name: seriesName2,
        type: "column",
        smooth: true,
        color: "#FAB758",
        data: sampledCurve2List,
        showPoint: false,
        legendShape: 'circle'
      },
    ]
  };

  chartDatas.value = res;
  showChart.value = true;
};

// 查询图表数据
const queryDataCharts = async () => {
  chartData.value = null;
  showChart.value = false;
  if (isLoading.value) return;

  try {
    isLoading.value = true;

    dateLineCharts();
  } catch (error) {
  } finally {
    isLoading.value = false;
  }
};
// 查询图表数据
const queryDataChart = async () => {
  chartDatas.value = null;
  showChart.value = false;
  if (isLoading.value) return;

  try {
    isLoading.value = true;
    weekLineCharts();

  } catch (error) {
  } finally {
    isLoading.value = false;
  }
};

// 监听数据变化
watch(
  () => [props.lendList?.timeList?.length],
  () => {
    chartDatas.value = null;
    chartData.value = null;

    if (props.selectType === '1') {
      queryDataCharts();

    } else {
      queryDataChart()
    }

  },
  { immediate: true }
);
// 更新 Y 轴标题
watch(() => props.selectType, (newVal) => {
  console.log('newValshuju ', newVal);
  if (newVal === '1') {
    opts.value.yAxis.data[0].title = 'kW';
  } else {
    barOpts.value.yAxis.data[0].title = 'kWh';
  }
}, { immediate: true });

// 组件卸载清理
onUnmounted(() => {
  // 可选：清理定时器等资源
});
</script>

<style lang="scss" scoped>
.line-area {
  height: 100%;
  width: 100%;

  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    z-index: 10;

    .loading-spinner {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #f3f3f3;
      border-top: 3rpx solid #388bff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 16rpx;
    }

    .loading-text {
      font-size: 24rpx;
      color: #666;
    }

    @keyframes spin {
      0% {
        transform: rotate(0deg);
      }

      100% {
        transform: rotate(360deg);
      }
    }
  }
}
</style>