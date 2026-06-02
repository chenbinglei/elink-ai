import { ref, unref, watch, inject, onMounted, computed } from "vue";
import SystemMonitorController from "@/api/together/systemMonitor";
import {
  INJECT_KEY_DEVICE_INFO,
  commonChartOption,
} from "@/views/monitor/systemMonitoring/constant";
import usePileGunMonitor from "./usePileGunMonitor";
import { CurveColorMap } from "@/common/enum";

const getOptions = ({ timeList, reqPowerList, outPowerList }) => {
  const { legend, xAxis, yAxis, ...rest } = commonChartOption;
  return {
    ...rest,
    grid: {
      top: 10,
      left: 0,
      right: 0,
      bottom: 10,
    },
    legend: {
      show: false,
    },
    xAxis: {
      ...xAxis,
      data: timeList,
      axisLabel: {
        show: false,
      },
      axisTick: {
        show: false,
      },
    },
    yAxis: {
      ...yAxis,
      axisLabel: {
        show: false,
      },
    },
    series: [
      {
        id: "outPower",
        name: "输出功率",
        type: "line",
        data: outPowerList,
        emphasis: {
          focus: "series",
        },
        itemStyle: {
          color: CurveColorMap.outPower,
        },
        lineStyle: {
          color: CurveColorMap.outPower,
        },
      },
      {
        id: "reqPower",
        name: "需求功率",
        type: "line",
        data: reqPowerList,
        emphasis: {
          focus: "series",
        },
        itemStyle: {
          color: CurveColorMap.reqPower,
        },
        lineStyle: {
          color: CurveColorMap.reqPower,
        },
      },
    ],
  };
};

export default function usePileGunList(props) {
  const pileCode = computed(() => props.info.deviceNumber);
  const { deviceId } = inject(INJECT_KEY_DEVICE_INFO) || {};
  const loading = ref(false);
  const gunList = ref([]);
  const getGunList = async () => {
    loading.value = true;
    try {
      const { success, data } =
        await SystemMonitorController.findPileGunPowerList(
          unref(deviceId),
          // 防止触发重复点击  被拦截
          +new Date()
        );
      if (!success) {
        return;
      }
      gunList.value = data.map(
        ({ dayChargeQt, dayV2gQt, gunCode, pileCode, ...rest }) => {
          return {
            dayChargeQt,
            dayV2gQt,
            gunCode,
            pileCode,
            option: getOptions(rest),
          };
        }
      );
    } finally {
      loading.value = false;
    }
  };

  const { gunRealtimeDataMap } = usePileGunMonitor({
    pileCode,
  });

  //初始化和deviceId变化时都触发
  onMounted(() => {
    getGunList();
  });
  watch(
    () => deviceId.value,
    () => {
      getGunList();
    }
  );

  return {
    gunList,
    loading,
    gunRealtimeDataMap,
  };
}
