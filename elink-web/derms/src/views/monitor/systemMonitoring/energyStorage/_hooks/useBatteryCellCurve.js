import { ref, unref, onMounted, onUnmounted } from "vue";
import moment from "moment";
import SystemMonitorController from "@/api/together/systemMonitor";
import {
  commonChartOption,
  tooltipFormatter,
  commonVChartProps,
} from "@/views/monitor/systemMonitoring/constant";
import { CurveColorMap } from "@/common/enum";
import { exportCustomExcel } from "@/common/exportExcel";

const getOptions = ({ dateList, dataInfoList }, unit) => {
  const { grid, tooltip, legend, xAxis, yAxis, ...rest } = commonChartOption;
  const scale = window.innerWidth / 1920;

  return {
    ...rest,
    grid: {
      ...grid,
      top: scale * (grid.top+10),
    },
    tooltip: {
      ...tooltip,
      formatter: tooltipFormatter({
        unit,
      }),
      textStyle: {
        ...tooltip.textStyle,
        fontSize: 14 * scale,
      },
    },
    legend: {
      ...legend,
      data: dataInfoList.map((item) => item.name),
      top: (legend.top-10) * scale,
      left: "20%",
      height: legend.height * scale,
      itemHeight: legend.itemHeight * scale,
      itemWidth: legend.itemHeight * scale,
      textStyle: {
        ...legend.textStyle,
        fontSize: legend.textStyle.fontSize * scale,
        height: legend.textStyle.height * scale,
        lineHeight: legend.textStyle.lineHeight * scale,
      },
    },
    xAxis: {
      ...xAxis,
      data: dateList,
      axisLabel: {
        ...xAxis.axisLabel,
        fontSize: legend.textStyle.fontSize * scale,
      },
    },
    yAxis: {
      ...yAxis,
      name: unit,
      nameTextStyle: {
        ...yAxis.nameTextStyle,
        padding: yAxis.nameTextStyle.padding.map((item) => item * scale),
        fontSize: legend.textStyle.fontSize * scale,
      },
    },
    series: dataInfoList.map(({ name, code, dataList }) => ({
      id: code,
      name,
      type: "line",
      data: dataList,
      barMinWidth: 4 * scale,
      barMaxWidth: 32 * scale,
      emphasis: {
        focus: "series",
      },
      itemStyle: {
        color: CurveColorMap[code],
      },
      lineStyle: {
        color: CurveColorMap[code],
      },
      areaStyle: {
        shadowBlur: 10 * scale,
        shadowOffsetX: 1,
        shadowOffsetY: 1,
        opacity: 0.1,
      },
    })),
  };
};

export default function useBatteryCellCurve({
  deviceId,
  date,
  cellIndex,
  activeTab,
  currentUnit,
  currentChartTitle,
}) {
  const curveLoading = ref(false);
  const option = ref({});
  const lastData = ref();

  const findCellCurve = async () => {
    if (curveLoading.value) {
      return;
    }
    curveLoading.value = true;
    try {
      const { success, data } = await SystemMonitorController.findSystemCurve({
        dataId: unref(deviceId),
        startTime: moment(date.value)
          .startOf("day")
          .format("YYYY-MM-DD HH:mm:ss"),
        endTime: moment(date.value).endOf("day").format("YYYY-MM-DD HH:mm:ss"),
        formatInterval: "6",
        timeInterval: "1m",
        type: activeTab.value === "voltage" ? 20 : 21,
        dataIndex: cellIndex?.value ?? 0,
      });
      if (!success) {
        return;
      }
      lastData.value = data;
      option.value = getOptions(data, currentUnit.value);
    } finally {
      curveLoading.value = false;
    }
  };

  const onExport = async () => {
    if (curveLoading.value) {
      return;
    }
    curveLoading.value = true;
    const { series, xAxis } = option.value;
    const tableHeader = [
      {
        key: "index",
        name: "序号",
      },
      {
        key: "time",
        name: "时间",
      },
    ].concat(
      series.map(({ id, name }) => ({
        key: id,
        name: `${name}(${currentUnit.value})`,
      }))
    );
    const jsonData = xAxis.data?.map((time, index) => {
      const extra = {};
      series.forEach(({ id, data }) => {
        extra[id] = data[index];
      });
      return {
        ...extra,
        index: index + 1,
        time,
      };
    });
    const fileName = `${currentChartTitle.value}_${moment(date.value).format(
      "YYYY-MM-DD"
    )}`;
    try {
      await exportCustomExcel(tableHeader, jsonData, fileName);
    } finally {
      curveLoading.value = false;
    }
  };

  const onResize = () => {
    requestAnimationFrame(() => {
      option.value = getOptions(lastData.value, currentUnit.value);
    });
  };
  // 监听触发: 设备id变化 日期变化
  onMounted(() => {
    window.addEventListener("resize", onResize);
  });
  onUnmounted(() => {
    window.removeEventListener("resize", onResize);
  });

  return {
    date,
    curveLoading,
    option,
    chartProps: commonVChartProps,
    findCellCurve,
    onExport,
  };
}
