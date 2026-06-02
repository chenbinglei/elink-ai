<template>
    <div class="h-312px flex-shrink-0">
        <RunningQuota v-bind="runningQuotaProps" />
    </div>
    <div class="flex-1 flex-shrink-0 flex items-stretch justify-between overflow-hidden">
        <BlockChart type-id="1" class="w-769px  flex-shrink-0" />
        <BlockChart type-id="2" class="w-769px  flex-shrink-0" />
    </div>
</template>

<script setup>
import { RunningQuota } from "../_slice";
import BlockChart from "@/views/monitor/systemMonitoring/_slice/blockChart.vue";
import SystemMonitorController from "@/api/together/systemMonitor";
import { runningQuotaList, keyReflectMap } from "./constant";

const props = defineProps({
    deviceId: {
        type: String,
        required: true,
    },
    deviceType: {
        type: String,
        required: true,
    }
});
const runningQuotaProps = {
    runningQuotaList, keyReflectMap,
    api: SystemMonitorController.findSystemPvData
};
</script>