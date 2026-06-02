<template>
  <div class="echarts-container" ref="chartRef" :style="{
    width: typeof width === 'number' ? width + 'px' : width,
    height: typeof height === 'number' ? height + 'px' : height,
  }"></div>
</template>


<script setup>
import { ref, onMounted, onUnmounted, defineProps, watch, defineExpose, nextTick, reactive, defineOptions } from "vue";
import * as echarts from "echarts";
import { hexToRgba } from "./utils";

/**
 * 通过两个X轴 实现的背景
 */
defineOptions({
  name: "component_chart_bar_2d_bg"
});
// 图表DOM引用
const chartRef = ref(null);
const viewData = reactive({
  data: null
});
const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => {
      let color1 = "#FFFF00";
      let names = ["1", "2", "3"];
      let data1 = { color: color1, name: "发电能力", datas: [], barWidth: "15%" };
      //渐变色定义
      data1.areaColor = {
        linear: [0, 0, 0, 1],
        colors: [
          { color: data1.color, offset: 0, alpha: 0.4 }
          , { color: data1.color, offset: 0.5, alpha: 0.1 }
          , { color: data1.color, offset: 1, alpha: 0 }
        ]
      };
      data1.datas = [52.8, 56.92, 99.79]
      let datalist = [data1];
      return {
        hideLegend: true,
        names: names,
        datas: datalist
      };
    }
  },
  name: {
    type: String,
    default: "功率数据",
  },
  theme: {
    type: String,
    default: "default",
  },
  chartOptions: {
    type: Object,
    default: () => ({}),
  },
  unit: {
    type: [String, Number],
    default: "kW",
  },
  unitx: {
    type: [String, Number],
    default: "",
  },
  width: {
    type: [String, Number],
    default: "400px",
  },
  height: {
    type: [String, Number],
    default: "180px",
  },
});

// ECharts实例
let chartInstance = null;


const getBarSeries = (datas, name, color, barWidth) => {
  return {
    name: name,
    type: 'bar',
    // barWidth: barWidth ?? 10,
    barWidth: barWidth ?? "10%",
    showBackground: false,
    itemStyle: {
      color: color
    },
    xAxisIndex: 1,
    data: datas
  }
}

const getLegend = (name, color, originData) => {
  let o = {
    name: name,
    icon: "rect",
    textStyle: {
      color: color,
      fontSize: 14
    }
  };
  if (originData.lineType != null) {
    o.icon = "path://M10,10L30,10L30,20L10,20zM40,10L60,10L60,20L40,20zM70,10L90,10L90,20L70,20z";
  }
  return o;

}

const getAxisPointer = (val, margin) => {
  let axisPointer = {
    value: val,
    snap: false,
    triggerTooltip: false,
    lineStyle: {
      color: '#00CCFF',
      type: "dotted",
      width: 2
    },
    label: {
      show: true,
      color: '#00CCFF',
      fontSize: 14,
      // formatter: function (params) {
      //   return "";
      // },
      margin: margin,
      backgroundColor: "rgba(0,0,0,0)"
    },
    handle: {
      size: 0,
      show: true,
      color: '#7581BD'
    }
  };
  return axisPointer;
}
// 根据 name 返回对应颜色
const getGradientStyle = (config) => {
  const direction = config.y2 === 1 ? 'to bottom' : 'to right';
  const stops = config.colorStops
    .map(stop => `${stop.color} ${stop.offset * 100}%`)
    .join(', ');
  return `linear-gradient(${direction}, ${stops})`;
};


const getOption2 = (datas) => {
  //计算最大数值
  let max = 10;
  let count = 0;
  let dataCount = datas.datas.length;
  for (let i = 0; i < dataCount; i++) {
    const dataList = datas.datas[i].datas;
    max = Math.max(Math.max(...dataList), max);
    count = Math.max(count, dataList.length);
  }
  max = Math.ceil(max / 10) * 10
  let maxList = [];
  for (let i = 0; i < count; i++) {
    maxList.push(max)
  }


  let option = {
    xAxis: [
      {
        data: datas.names,
        axisLine: {
          show: false,
        },
        axisLabel: {
          show: false,
        },
        axisTick: {
          show: false,
        },
        position: 'bottom',
        z: 9,
      },
      {
        data: datas.names,
        axisLine: {
          lineStyle: {
            color: '#00D2FF',
          },
        },
        axisLabel: {
          color: '#fff',
        },
        axisTick: {
          show: false,
        },
        position: 'bottom',
        z: 10,
      }

    ],
    yAxis: {
      type: "value",
      name: props.unit,
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: "#005B8B",
          type: 'dashed'    //设置网格线类型 dotted：虚线   solid:实线
        },
      },
      nameTextStyle: {
        fontSize: 12,
        color: "#00CCFF"
      },
      axisLabel: {
        fontSize: 12,
        color: "#00CCFF"
      },
      axisLine: {
        show: false,
        // lineStyle: {
        //   color: '#063670'
        // }
      },
    },
    // tooltip: {
    //   trigger: 'axis',
    //   // triggerOn: 'item',
    //   textStyle: {
    //     fontSize: 14,
    //   },
    //   axisPointer: null,
    //   // axisPointer: {
    //   //   type: 'cross',
    //   //   label: {
    //   //     backgroundColor: '#6a7985'
    //   //   }
    //   // }
    // },
    tooltip: {
      trigger: "axis",
      className: "custom-tooltip-box",

      formatter: (params) => {
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;
        console.log(params);
        params.forEach((p) => {
          const color = getGradientStyle(p.color)
          html += `
       <div style="display:flex;align-items:center;"> 
        <div style="
          display:inline-block;
          margin:4px 4px 2px 0;
          border-radius:50%;
          width:10px;
          height:10px;
         background:${color};
        "></div>
        <div style="color:#fff;font-size:14px;">${p.seriesName}： ${p.value}</div>
       </div>
      `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;
      },
    },
    dataZoom: [
      {
        type: 'inside'
      }
    ],
    legend: {
      show: false,
      itemHeight: 14,
      top: "6%",
      left: "10%",
      data: []
    },
    grid: datas.grid ?? {
      left: "6%",
      right: "3%",
      top: "22%",
      bottom: "10%",
    },
    series: []
  };
  // console.log(datas);
  //背景
  // option.series.push({
  //   // 该系列为背景深蓝色半胶囊
  //   data: maxList,
  //   showBackground:false,
  //   type: 'bar',
  //   xAxisIndex: 0,
  //   silent: true,
  //   itemStyle: {
  //     color: '#08476B99',
  //     // borderColor: '#0488A9',
  //     // barBorderRadius: [20, 20, 0, 0],
  //     borderWidth: 0,
  //   },
  //   barWidth: dataCount * 10 + 20,
  //   tooltip: {
  //     show: false,
  //   },
  // });
  for (let i = 0; i < datas.datas.length; i++) {
    let o = datas.datas[i];
    if (!datas.hideLegend) {
      option.legend.show = true;
      option.legend.data.push(getLegend(o.name, "#ffffff", o));
    }

    option.series.push(getBarSeries(o.datas, o.name, o.color, 10));
  }
  // if(!datas.hideTooltip){
  //   option["tooltip"] = null;
  // }
  if (datas.pointerInfo) {
    //pointerInfo value 保存x轴的值，marin 坐标
    option.xAxis.axisPointer = getAxisPointer(datas.pointerInfo.value, datas.pointerInfo.margin);
    //显示竖线，默认不显示tooltips
    option.tooltip.triggerOn = "none";
  }
  // console.log("getOption2")
  // console.log(option)
  return option;
}

// // 从父组件数据中提取x轴和y轴数据
// // 提取多系列数据
// const extractChartData = () => {
//   const seriesData = props.data.map((series) => ({
//     name: series.name,
//     type: "line",
//     data: series.data.map((item) => item.value),
//     smooth: true,
//     symbol: "circle",
//     symbolSize: 8,
//   }));
//
//   const xAxisData = props.data[0]?.data.map((item) => item.time) || [];
//   return { seriesData, xAxisData };
// };

const initChart = (data) => {
  if (!chartRef.value) return;
  // 销毁旧实例避免内存泄漏
  if (chartInstance) {
    chartInstance.dispose();
  }
  // 创建新实例
  chartInstance = echarts.init(chartRef.value, props.theme);
  viewData.data = data;
  // 应用配置
  chartInstance.setOption(getOption2(data));

  // 监听窗口大小变化
  const resizeHandler = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  };

  window.removeEventListener("resize", resizeHandler);

  window.addEventListener("resize", resizeHandler);

  // 组件卸载时移除监听
  onUnmounted(() => {
    if (chartInstance) {
      chartInstance.dispose();
      chartInstance = null;
    }
    window.removeEventListener("resize", resizeHandler);
  });
}
const loadData = (data) => {
  // console.log(data);
  if (chartInstance == null) {
    initChart(data)
  } else {
    viewData.data = data;
    chartInstance.setOption(getOption2(data));
  }
}
defineExpose({ loadData })

// 监听props变化，更新图表
watch(
  [() => props.data, () => props.name],
  () => {
    initChart(props.data); // 完全重新初始化图表
  },
  { deep: true }
);

// 组件挂载时初始化图表
onMounted(() => {
  initChart(props.data);
});
</script>


<style scoped lang="scss">
::v-deep .custom-tooltip-box {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;
  color: #ffffff;

  .custom-tooltip-style {

    background: rgba(0, 47, 78, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 5px 15px;

    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }


    .custom-radio {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
      margin-right: 5px;
      box-sizing: border-box;

    }



    .custom-tooltip-value {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      margin-left: 20px;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
  }
}
</style>