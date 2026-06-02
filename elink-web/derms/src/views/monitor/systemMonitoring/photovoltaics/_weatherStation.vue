<template>
    <div v-loading="loading"
        class="w-full h-full flex flex-col items-stretch justify-start overflow-x-hidden overflow-y-auto">
        <div class="h-220px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <!-- 设备详情 -->
            <DeviceInfo title="气象站" icon="weatherStation" avatar="weatherStation" class="w-769px flex-shrink-0"
                :info="deviceInfoData" />
            <!-- 遥控遥调 -->
            <DeviceQuotaGroup :data="deviceInfoData" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-316px flex-shrink-0 flex justify-between items-stretch mb-6px">
            <BlockChart type-id="5" class="w-769px  flex-shrink-0" />
            <BlockChart type-id="6" class="w-769px  flex-shrink-0" />
        </div>
        <div class="h-312px flex-grow-1 overflow-hidden flex-shrink-0  flex items-stretch justify-start">
            <BlockChart type-id="7" class="w-full" />
        </div>
    </div>
</template>

<script setup>
// 碎片页引入
import { BlockChart, DeviceQuotaGroup, DeviceInfo } from "../_slice"
// hooks
import useDeviceData from "@/views/monitor/systemMonitoring/_hooks/useDeviceData";

const preset = [{
    id: 'recentSevenDays',
    name: '近7天',
    dateRange: [
        [-6, 'day'],
        [1, 'day']
    ],
    activeTabValue: 'month',
    formatInterval: 1,
    timeInterval: '1h',
    interval: 23
}, {
    id: 'recentHalfYear',
    name: '近半年',
    dateRange: [
        [-5, 'month'],
        [1, 'month']
    ],
    activeTabValue: 'year',
    formatInterval: 2,
    timeInterval: '1d',
    interval: 29
}];

const fileNameFun = ({
    siteName,
    curveName,
    presetSelect
}) => {
    const dateName = preset.find((item) => item.id === presetSelect.value)?.name;
    return `${siteName}_${dateName}_${curveName}`;
}

const { loading, deviceInfoData } = useDeviceData({ apiName: 'findPvWeatherData' });
</script>
