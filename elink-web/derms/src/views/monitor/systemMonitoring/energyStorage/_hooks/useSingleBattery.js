import { ref, watch, computed, inject, watchEffect } from "vue";
import moment from "moment";
import { commonVChartProps } from "@/views/monitor/systemMonitoring/constant";
import { INJECT_KEY_DEVICE_INFO } from "@/views/monitor/systemMonitoring/constant";
import useBatteryCellList from "./useBatteryCellList";
import useBatteryCellCurve from "./useBatteryCellCurve";

const format = "YYYY-MM-DD";
const tipList = [
  {
    name: "电压（V）",
    children: [
      {
        name: "欠压[u< 2.60]",
        color: "#FF8900",
      },
      {
        name: "正常[2.60≤u≤3.60]",
        color: "#3BAAF5",
      },
      {
        name: "过压[u＞3.60]",
        color: "#FF0000",
      },
    ],
  },
  {
    name: "温度（°C）",
    children: [
      {
        name: "低温[t< 0.00]",
        color: "#FF8900",
      },
      {
        name: "正常[0.00≤t≤50.00]]",
        color: "#3BAAF5",
      },
      {
        name: "超温[u＞50.00]",
        color: "#FF0000",
      },
    ],
  },
];
const tabs = [
  {
    id: "voltage",
    name: "电压",
  },
  {
    id: "temperature",
    name: "温度",
  },
];

const dateProps = {
  format,
  type: "date",
};

export default function useSingleBattery() {
  const { deviceId } = inject(INJECT_KEY_DEVICE_INFO);
  const activeTab = ref(tabs[0].id);
  const date = ref(moment().format(format));
  const currentUnit = computed(() =>
    activeTab.value === tabs[0].id ? "mV" : "℃"
  );
  const currentChartTitle = computed(() =>
    activeTab.value === tabs[0].id ? "电芯电压曲线" : "电芯温度曲线"
  );
  //
  const { findAllCellList, cellIndex, ...listRest } = useBatteryCellList({
    tabs,
    deviceId,
    activeTab,
  });
  const { findCellCurve, ...curveRest } = useBatteryCellCurve({
    deviceId,
    activeTab,
    cellIndex,
    date,
    currentUnit,
    currentChartTitle,
  });

  watch([deviceId, activeTab, cellIndex, date], () => {
    if (cellIndex.value < 0) {
      return;
    }
    findCellCurve();
  });

  return {
    ...listRest,
    ...curveRest,
    cellIndex,
    tabs,
    tipList,
    activeTab,
    dateProps,
    chartProps: commonVChartProps,
    currentUnit,
    currentChartTitle,
    findCellCurve,
  };
}
