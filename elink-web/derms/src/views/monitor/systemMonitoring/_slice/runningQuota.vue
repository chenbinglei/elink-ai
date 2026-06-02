<template>
    <MonitorBlock title="运行指标" icon="runningQuota">
        <template #content>
            <div class="px-24px pt-45px pb-50px grid grid-cols-7 grid-rows-2 gap-38px box-border" v-loading="loading">
                <MonitorQuotaItem v-for="runningQuota in runningQuotaList" :key="runningQuota.id" :id="runningQuota.id"
                    :name="runningQuota.name" :unit="runningQuota.unit" :val="runningQuotaData[runningQuota.id]" />
            </div>
        </template>
    </MonitorBlock>
</template>

<script setup>
import { MonitorBlock, MonitorQuotaItem } from "@/views/monitor/_slice/base";
// hooks
import useRunningQuota from "../_hooks/useRunningQuota";

const props = defineProps({
    keyReflectMap: {
        type: Object,
        required: true,
    },
    runningQuotaList: {
        type: Array,
        required: true,
    },
    api: {
        type: Function,
        required: true,
    },
});

const { loading, runningQuotaList, runningQuotaData } = useRunningQuota(props);
</script>