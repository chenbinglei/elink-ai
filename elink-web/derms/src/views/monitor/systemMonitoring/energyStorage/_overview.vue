<template>
    <div class="h-312px flex-shrink-0 mb-6px">
        <RunningQuota v-bind="runningQuotaProps" />
    </div>
    <div class="flex-grow-1 overflow-hidden flex-shrink-0 flex items-stretch justify-between">
        <BlockChart type-id="8" class="w-769px  flex-shrink-0" />
        <BlockChart type-id="9" class="w-769px  flex-shrink-0" />
    </div>
</template>

<script setup>
import { RunningQuota } from "../_slice";
import { BlockChart } from "@/views/monitor/systemMonitoring/_slice";
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
    api: SystemMonitorController.findSystemSeData
};
</script>