<template>
    <div v-loading="loading"
        class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto">
        <div class="h-220px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <!-- 设备详情 -->
            <DeviceInfo title="储能辅助设备" icon="energyStorageAuxiliaryEquipment" avatar="energyStorageAuxiliaryEquipment"
                class="w-769px flex-shrink-0" :info="deviceInfoDataFormat" />
            <!-- 遥控遥调 -->
            <TelemetryControl class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-316px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <!-- 遥测数据 -->
            <TelemetryData :data="deviceInfoData" class="w-769px  flex-shrink-0" />
            <!-- 遥信告警 -->
            <TelemetryAlarm :data="deviceInfoData" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-316px flex-grow-1 overflow-hidden flex-shrink-0 flex justify-between items-stretch mb-6px">
            <BlockChart type-id="14" class="w-769px  flex-shrink-0" />
            <BlockChart type-id="15" class="w-769px  flex-shrink-0" />
        </div>
    </div>
</template>

<script setup>
// 碎片页引入
import { computed } from "vue";
import { DeviceInfo, TelemetryData, TelemetryAlarm, TelemetryControl, BlockChart } from "../_slice"
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";

const { loading, deviceInfoData } = useDeviceData({ apiName: 'findSeAuxEquipmentData' });
const deviceInfoDataFormat = computed(() => {
    const { coolingState, externalState, heatingState, internalState, ...rest } = deviceInfoData.value;
    return {
        ...rest,
        airRunState: {
            coolingState, externalState, heatingState, internalState
        }
    };
})
</script>
