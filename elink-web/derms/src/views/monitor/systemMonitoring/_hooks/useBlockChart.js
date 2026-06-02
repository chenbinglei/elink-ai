import {
  ref,
  unref,
  computed,
  watch,
  inject,
  onMounted,
  onUnmounted,
} from "vue";
import { useStore } from "vuex";
import moment from "moment";
//
import SystemMonitorController from "@/api/together/systemMonitor";
import { exportCustomExcel } from "@/common/exportExcel";
import { CurveColorMap, CurveDataTypeMap } from "@/common/enum";
import { TypeBlockChartConfig } from "@/common/chartSearchConfig";
import { INJECT_KEY_DEVICE_INFO } from "../constant";
import { commonChartOption } from "../constant";
//
import { usePickerProps } from "@/hooks/usePicker";
import useApiWrap from "@/hooks/useApiWrap";

//依据参数生成chart的option
const getCommonOption = ({ data, seriesType, unit, interval }) => {
  const { dataInfoList = [], dateList = [] } = data || {};
  const { grid, tooltip, legend, xAxis, yAxis, ...rest } = commonChartOption;
  const scale = window.innerWidth / 1920;
  console.log(commonChartOption)
  return {
    ...rest,
    tooltip: {
      ...tooltip,
      textStyle: {
        ...tooltip.textStyle,
        fontSize: 14 * scale,
      },
    },
    grid: {
      ...grid,
      top: scale * grid.top,
    },
    legend: {
      ...legend,
      height: legend.height * scale,
      itemHeight: legend.itemHeight * scale,
      data: dataInfoList?.map((item) => item.name),
      icon: seriesType === "line" ? legend.icon : "rect",
      itemWidth: seriesType === "line" ? 30 * scale : 16 * scale,
      textStyle: {
        ...legend.textStyle,
        fontSize: legend.textStyle.fontSize * scale,
        height: legend.textStyle.height * scale,
        lineHeight: legend.textStyle.lineHeight * scale,
      },
    },
    xAxis: {
      ...xAxis,
      data: dateList ?? [],
      axisLabel: {
        ...xAxis.axisLabel,
        interval: unref(interval),
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
    series: dataInfoList?.map(({ name, dataList, code }) => ({
      id: code,
      name,
      type: seriesType,
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
const valueFormat = "YYYY-MM-DD HH:mm:ss";

export default function useBlockChart({
  // 曲线类型id
  typeId,
  //
  fileNameFun,
}) {
  const { deviceId } = inject(INJECT_KEY_DEVICE_INFO);
  const store = useStore();
  // 获取当前图表块的配置
  const blockChartConfig = computed(
    () => TypeBlockChartConfig[unref(typeId)] ?? TypeBlockChartConfig["1"]
  );
  //分解为渲染需要的各种参数
  //
  const title = computed(() => blockChartConfig.value.title);
  const icon = computed(() => blockChartConfig.value.icon);
  // tab
  const tabList = computed(() => blockChartConfig.value?.presetList ?? []);
  const activeTabId = ref();
  const activeTab = computed(() => {
    const find = tabList.value.find((item) => item.id === activeTabId.value);
    return (
      find ?? {
        pickerType: "date",
        defaultValue: [0, "day"],
        disabledConfig: {},
      }
    );
  });
  const onTabChange = (val) => {};
  // 日期
  const pickerType = computed(() => activeTab.value.pickerType);
  const hidePicker = computed(() => blockChartConfig.value?.hidePicker);
  const { date, format, pickerEvents, pickerProps } = usePickerProps({
    type: pickerType,
    defaultValue: computed(() => activeTab.value.defaultValue),
    disabledConfig: computed(() => activeTab.value.disabledConfig),
  });
  //chart 数据请求和导出
  const myChart = ref("");
  const lastData = ref();
  const chartOptions = ref({});
  const { run, loading } = useApiWrap({
    api: SystemMonitorController.findSystemCurve,
    guarante: () => ({
      dataInfoList: [],
      dateList: [],
    }),
  });
  const getData = async () => {
    console.log(pickerType.value,'99999***',date.value )
    // if(pickerType.value?.includes('range')&&!(date.value instanceof Array)){
    //   return;
    // }
    if (
  pickerType.value &&
  typeof pickerType.value === 'string' &&
  pickerType.value.includes('range') &&
  !(date.value instanceof Array)
) {
  return;
}
    //拦截
    if (!unref(deviceId) || !activeTab.value?.id || !date.value) {
      return;
    }
    let startTime, endTime;
    const timeUnit = activeTab.value?.disabledConfig?.duration?.[1] ?? "day";
    if (date.value instanceof Array) {
      startTime = moment(date.value[0]).startOf(timeUnit).format(valueFormat);
      endTime = moment(date.value[1]).endOf(timeUnit).format(valueFormat);
    } else {
      //判断是否当天,当天的结束时刻到当前时刻
      startTime = moment(date.value).startOf(timeUnit).format(valueFormat);
      if (moment(date.value).isSame(moment(), "day")) {
        endTime = moment().format(valueFormat);
      } else {
        endTime = moment(date.value).endOf(timeUnit).format(valueFormat);
      }
    }
    const data = await run({
      type: unref(typeId),
      dataId: unref(deviceId),
      startTime,
      endTime,
      timeInterval: activeTab.value?.timeInterval,
      formatInterval: activeTab.value?.formatInterval,
      interval: activeTab.value?.interval,
    });
    //生成新的chartOption
    lastData.value = data;
    chartOptions.value = getCommonOption({
      data,
      seriesType: blockChartConfig.value?.seriesType,
      unit: blockChartConfig.value?.unit,
      interval: activeTab.value?.chartAxisInterval,
    });
  };
  const onExport = async () => {
    if (loading.value) {
      return;
    }
    loading.value = true;
    const siteName = store.state.monitor.siteName;
    const { series, xAxis } = chartOptions.value;
    if (!(xAxis.data.length > 0) || series.length === 0) {
      return;
    }
    const tableHeader = [
      {
        key: "index",
        title: "序号",
      },
      {
        key: "date",
        title: "日期",
      },
    ].concat(
      series.map((item, index) => ({
        key: `series_${index}`,
        title: item.name,
      }))
    );
    const jsonData = new Array(xAxis.data?.length);
    xAxis.data.forEach((item, index) => {
      const rowData = {
        index: index + 1,
        date: item,
      };
      series.forEach((sery, seryIndex) => {
        rowData[`series_${seryIndex}`] = sery.data[index] ?? "";
      });
      jsonData[index] = rowData;
    });
    const fileName = fileNameFun
      ? fileNameFun({
          siteName,
          curveName: CurveDataTypeMap[unref(typeId)] ?? CurveDataTypeMap[1],
          date,
        })
      : `${siteName}_${
          CurveDataTypeMap[unref(typeId)] ?? CurveDataTypeMap[1]
        }_${moment(date.value).format(format.value)}`;
    try {
      await exportCustomExcel(tableHeader, jsonData, fileName);
    } finally {
      loading.value = false;
    }
  };
  const onResize = () => {
    requestAnimationFrame(() => {
      chartOptions.value = getCommonOption({
        data: lastData.value,
        seriesType: blockChartConfig.value?.seriesType,
        unit: blockChartConfig.value?.unit,
        interval: activeTab.value?.chartAxisInterval,
      });
    });
  };
  // 监听触发: 设备id变化 日期变化
  watch([deviceId, date], () => getData());
  onMounted(() => {
    activeTab.value = tabList.value[0];
    activeTabId.value = tabList.value[0].id;
    window.addEventListener("resize", onResize);
  });
  onUnmounted(() => {
    window.removeEventListener("resize", onResize);
  });
  return {
    title,
    icon,
    // tab
    tabList,
    activeTabId,
    onTabChange,
    // 日期
    hidePicker,
    date,
    format,
    pickerEvents,
    pickerProps,
    //
    loading,
    myChart,
    chartOptions,
    onExport,
  };
}
