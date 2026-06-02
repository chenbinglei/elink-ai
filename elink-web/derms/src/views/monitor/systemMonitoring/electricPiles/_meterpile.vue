<template>
  <div
    v-loading="loading"
    class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto"
  >
    <div class="h-220px flex-shrink-0 flex justify-between">
      <!-- 设备详情 -->
      <meterDevice
        avatar="dianzhaung"
        class="w-769px flex-shrink-0"
        :info="deviceInfoData"
        @infoList="infoList"
      />
      <merteDeviceList :info="meterList" class="w-769px flex-shrink-0"></merteDeviceList>
    </div>
    <div
      class="h-316px flex-shrink-0 flex justify-between items-stretch mb-6px"
    >
      <TelemetryData :data="meterList" class="w-769px flex-shrink-0" />
      <blockChartMeter type-id="24" class="w-769px flex-shrink-0" />
    </div>
    <div
      class="h-316px flex-shrink-0 flex justify-between items-stretch mb-6px"
    >
      <!-- meterPower -->
      <blockChartMeter type-id="meterPower" class="w-769px flex-shrink-0" />
      <blockChartMeter type-id="27" class="w-769px flex-shrink-0" />
    </div>
  </div>
</template>

<script setup>
// 碎片页引入
import {
  meterDevice,
  DeviceQuotaGroup,
  blockChartMeter,
  TelemetryData,
  merteDeviceList
} from "../_slice";
import PileGunList from "./_slice/pileGunList.vue";
import { ref } from "vue";
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";
const { loading, deviceInfoData } = useDeviceData({ apiName: "findPileData" });
const meterList = ref([]);
const infoList = (data) => {
  meterList.value = data;
};
</script>