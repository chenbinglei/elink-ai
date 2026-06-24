<template>
  <view class="containers">
    <view v-for="(item, index) in chartList" :key="index" class="chart-box">
      <view class="chart-title">{{ item.name }}</view>
      <qiun-data-charts type="line" :opts="chartOpts" :chartData="item.chartData" style="width: 100%; height: 300rpx;"
        :canvas2d="true" :ontouch="true" :canvasId="'chart-area-' + index" :inScrollView="true"></qiun-data-charts>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { findProcessAnalysisByOrderId } from '../../api/chargingOrder.js'

const props = defineProps({
  orderInfo: {
    type: Object,
    default: () => ({})
  }
})

// 后端返回数据
const dataEcharts = ref({})

// 图表配置列表
const list = ref([
  {
    name: "电流",
    yAxisName: "A",
    color: ["#06DA93", "#FDAA03"],
    series: [
      { name: '输出电流', fieldName: 'guncurrent' },
      { name: '需求电流', fieldName: 'gunrecurrent' }
    ]
  },
  {
    name: "电压",
    yAxisName: "V",
    color: ["#007FEB", "#E950E6"],
    series: [
      { name: '输出电压', fieldName: 'gunvoltage' },
      { name: '需求电压', fieldName: 'gunrevoltage' }
    ]
  },
  {
    name: "功率",
    yAxisName: "kW",
    color: ["#13CBE3"],
    series: [{ name: '输出功率', fieldName: 'gunpower' }]
  },
  {
    name: "SOC",
    yAxisName: "%",
    color: ["#03B7FF"],
    series: [{ name: 'SOC', fieldName: 'gunsoc' }]
  }
])

// 最终图表数据（不用 computed！）
const chartList = ref([])

// 图表通用配置
const chartOpts = {
  padding: [10, 15, 20, 0],
  dataLabel: false,
  dataPointShape: false,
  legend: {
    show: true, position: 'top', float: 'right',
    margin: 5, padding: 5, itemGap: 10, fontSize: 12, fontColor: '#333'
  },
  extra: {
    area: { type: "curve", opacity: 0.6, addLine: true, width: 2, gradient: true }
  },
  xAxis: {
    disableGrid: true, fontColor: '#666666', fontSize: 10,
    labelCount: 5, // 固定5个，不闪烁
    rotate: false
  },
  yAxis: {
    show: true, disableGrid: false, gridType: "dash", splitNumber: 4, gridColor: "#CCCCCC",
    padding: 10, showTitle: true, data: [{ position: "left", title: "" }]
  }
}

// 颜色转换
const colorHexTurnRgba = (hex, opacity) => {
  if (!hex) return 'rgba(153,153,153,0)'
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return `rgba(${r}, ${g}, ${b}, ${opacity})`
}

// ==============================================
// 处理图表数据（手动调用，不用 computed）
// ==============================================
const handleChartData = () => {
  const originX = dataEcharts.value.xAxisList || []
  const total = originX.length

  // X轴格式化
  const categories = originX.map(time => {
    if (!time) return ''
    return time.split(' ')[1]?.substring(0, 5) || ''
  })

  // 组装图表数据
  chartList.value = list.value.map(item => {
    const series = item.series.map((s, i) => ({
      name: s.name,
      data: dataEcharts.value[s.fieldName] || [],
      type: 'line',
      showSymbol: false,
      smooth: true,
      lineStyle: { color: item.color[i] || '#999' },
      areaStyle: {
        color: {
          type: "linear", x: 0, y: 1, x2: 0, y2: 0,
          colorStops: [
            { offset: 0, color: colorHexTurnRgba(item.color[i] || '#999', 0) },
            { offset: 1, color: colorHexTurnRgba(item.color[i] || '#999', 0.5) }
          ]
        }
      }
    }))

    return {
      ...item,
      chartData: { categories, series }
    }
  })
}

// ==============================================
// watch 获取接口数据 → 直接处理图表
// ==============================================
watch(() => props.orderInfo, async (newVal) => {
  if (newVal?.id) {
    const res = await findProcessAnalysisByOrderId({
      orderId: newVal.id,
      functionLogos: 'guncurrent,gunrecurrent,gunvoltage,gunrevoltage,gunpower,gunsoc'
    })
    if (res.success) {
      dataEcharts.value = res.data || {}
      
      // 数据回来后 → 直接处理图表
      handleChartData()
    }
  }
}, { deep: true, immediate: true })

</script>

<style lang="scss" scoped>
.containers {
  width: 100%;
  padding: 20rpx;
  box-sizing: border-box;
}

.chart-box {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 30rpx;
}

.chart-title {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 20rpx;
}
</style>