<template>
    <div v-loading="loading"
        class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto">
        <div class="h-220px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <!-- 设备详情 -->
            <DeviceInfo title="电池簇" icon="batteryCluster" avatar="batteryCluster" class="w-769px flex-shrink-0"
                :info="deviceInfoData" />
            <!-- 遥控遥调 -->
            <DeviceQuotaGroup :data="deviceInfoData" title="电池一致性分析" icon="batteryConsistencyAnalysis" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-316px flex-shrink-0 flex justify-between items-stretch  mb-6px">
            <!-- 遥测数据 -->
            <TelemetryData :data="deviceInfoData" class="w-769px  flex-shrink-0" />
            <!-- 遥信告警 -->
            <TelemetryAlarm :data="deviceInfoData" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-312px flex-shrink-0 flex  justify-between items-stretch mb-6px">
            <BlockChart type-id="12" class="w-769px  flex-shrink-0" />
            <BlockChart type-id="13" class="w-769px  flex-shrink-0" />
        </div>
        <div class="w-full h-312px flex-grow-1 overflow-hidden flex-shrink-0 relative ">
            <SingleBattery />
        </div>
    </div>
</template>

<script setup>
// 碎片页引入
import { DeviceInfo, TelemetryData, TelemetryAlarm, BlockChart, DeviceQuotaGroup } from "../_slice";
import SingleBattery from "./_slice/singleBattery.vue";
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";

const { loading, deviceInfoData } = useDeviceData({ apiName: 'findSeBatteryData' });

</script>
