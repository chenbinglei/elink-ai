<script setup lang="js">
import { unref, inject } from 'vue';
import MonitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import { INJECT_KEY_DEVICE_INFO } from "../constant";

const { deviceType } = inject(INJECT_KEY_DEVICE_INFO);
const controlUnitList = [{
    id: 'controlMode',
    name: '控制模式',
    auth: ['20', '23', '60']
}, {
    id: 'activePowerControl',
    name: '有功调节',
    auth: ['20', '23']
}, {
    id: 'reactivePowerControl',
    name: '无功调节',
    auth: ['20', '23']
}, {
    id: 'alarmReset',
    name: '告警复位',
    auth: ['20', '23']
}, {
    id: 'fixedValueSetting',
    name: '定值设置',
    auth: ['60']
}].filter((item) => item.auth.includes(unref(deviceType)));
</script>
<template>
    <MonitorBlock icon="telemetryControl" title="遥控遥调">
        <template #content>
            <div class="w-full h-full box-border px-20px pt-15px pb-12px">
                <div class="w-full h-full px-46px box-border flex justify-start items-center remote-control">
                    <div v-for="(controlUnit, index) in controlUnitList" :key="controlUnit.id"
                        class="control-unit w-148px h-126px flex flex-col justify-center items-center cursor-pointer"
                        :class="{
                            'ml-16px': index > 0
                        }">
                        <img class="w-auto h-25px mb--9px" :src="`/img/monitor/quota/${controlUnit.id}.png`"
                            :alt="controlUnit.name" />
                        <img class="w-56px h-25px mb-9px" :src="`/img/monitor/common/quotaBase.png`" alt="指标基座" />
                        <div class="h-21px text-16px text-white">
                            {{ controlUnit.name }}
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </MonitorBlock>
</template>
<style lang="scss" scoped>
.remote-control {
    background-image: url('/img/monitor/remoteControlBg.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}

.control-unit {
    background-image: url('/img/monitor/controlUnitBg.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;

    &:hover {
        background-image: url('/img/monitor/controlUnitBgActive.png');
    }
}
</style>