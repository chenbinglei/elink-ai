<script setup lang="js">
import MonitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import EmptyView from "@/views/monitor/_slice/base/empty.vue";

const props = defineProps({
    data: {
        type: Object,
        required: true
    }
});
</script>

<template>
    <MonitorBlock icon="telemetryAlarm" title="遥信告警">
        <template #content>
            <div class="w-full h-full box-border px-20px py-17px box-border overflow-auto">
                <EmptyView v-if="!data?.telecommuteAlarmList || data?.telecommuteAlarmList.length === 0" />
                <div v-else class="w-full h-full grid grid-cols-3  gap-22px">
                    <div v-for="item in data?.telecommuteAlarmList" :key="item.id"
                        class="w-229px h-60px  px-20px py-10px box-border flex justify-start items-center relative telemetry-alarm-unit">
                        <img  :src="`/img/monitor/common/remoteAlarm${item.eventLevel}.png`" class="absolute left-0 top-0 w-full h-full z--1"  />
                        <div class="flex-1 h-full flex flex-col justify-center items-start">
                            <div class="w-full text-16px text-white overflow-hidden whitespace-nowrap text-ellipsis">{{ item.eventName }}</div>
                            <div class="w-full text-12px text-white overflow-hidden whitespace-nowrap text-ellipsis">{{ item.alarmTime }}</div>
                        </div>
                        <div class="w-14px h-20px cursor-pointer jump-icon"></div>
                    </div>
                </div>
            </div>
        </template>
    </MonitorBlock>
</template>

<style lang="scss" scoped>
.telemetry-data-unit {
    background-image: url('/img/monitor/common/telemetryDataUnitBg.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
.jump-icon{
    background-image: url('/img/monitor/common/jump.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
</style>