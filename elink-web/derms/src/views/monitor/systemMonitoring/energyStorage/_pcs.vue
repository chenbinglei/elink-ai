<template>
    <div v-loading="loading"
        class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto">
        <div class="h-220px flex justify-between items-stretch mb-6px">
            <!-- 设备详情 -->
            <DeviceInfo title="储能变流器" icon="photovoltaicInverters" avatar="inverter" class="w-769px flex-shrink-0"
                :info="deviceInfoData" />
            <!-- 遥控遥调 -->
            <TelemetryControl class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-316px flex justify-between items-stretch mb-6px">
            <!-- 遥测数据 -->
            <TelemetryData :data="deviceInfoData" class="w-769px  flex-shrink-0" />
            <!-- 遥信告警 -->
            <TelemetryAlarm :data="deviceInfoData" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-312px flex-grow-1 flex-shrink-0 overflow-hidden flex items-stretch justify-between">
            <BlockChart type-id="10" class="w-769px  flex-shrink-0" />
            <BlockChart type-id="11" class="w-769px  flex-shrink-0" />
        </div>
    </div>
</template>

<script setup>
// 碎片页引入
import { DeviceInfo, TelemetryData, TelemetryAlarm, TelemetryControl, BlockChart } from "../_slice"
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";

const props = defineProps({
    deviceId: {
        type: String,
        required: true
    },
    deviceType: {
        type: String,
        required: true
    }
})

const { loading, deviceInfoData } = useDeviceData({ ...props, apiName: 'findSePcsData' });
</script>
