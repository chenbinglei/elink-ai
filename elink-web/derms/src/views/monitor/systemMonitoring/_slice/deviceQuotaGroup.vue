<script setup lang="js">
import { unref, inject } from 'vue';
import MonitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import { INJECT_KEY_DEVICE_INFO } from "../constant";

const props = defineProps({
    title: {
        type: String,
        default: '实况气象'
    },
    icon: {
        type: String,
        default: 'liveWeather'
    },
    data: {
        type: Object,
        required: true
    }
});
const { deviceType } = inject(INJECT_KEY_DEVICE_INFO) || {};
const realtimeQuotaList = [{
    id: 'horizontalRadiation',
    name: '水平辐射值',
    unit: 'W/m²',
    auth: ['65']
}, {
    id: 'inclinedRadiation',
    name: '倾斜辐射值',
    unit: 'W/m²',
    auth: ['65']
}, {
    id: 'moduleTemperature',
    name: '组件背板温度',
    unit: '℃',
    auth: ['65']
}, {
    id: 'windSpeedWindDirection',
    name: '风向/风速',
    unit: 'm/s',
    auth: ['65']
}, {
    id: 'cellVoltageConsistency',
    name: '电芯电压一致性',
    tips: '电芯电压极差≤100mV',
    isBoolean: true,
    auth: ['25']
}, {
    id: 'cellVoltageDifference',
    name: '电芯电压极差',
    unit: 'mV',
    auth: ['25']
}, {
    id: 'cellTemperatureConsistency',
    name: '电芯温度一致性',
    tips: '电芯温度极差≤5℃',
    isBoolean: true,
    auth: ['25']
}, {
    id: 'cellTemperatureDifference',
    name: '电芯温度极差',
    unit: '℃',
    auth: ['25']
}, {
    id: 'dayChargeQt',
    name: '今日充电量',
    unit: 'kWh',
    auth: ['28', '29', '30']
}, {
    id: 'dayV2gQt',
    name: '今日放电量',
    unit: 'kWh',
    auth: ['28', '29', '30']
}, {
    id: 'activePower',
    name: '有功功率',
    unit: 'kW',
    auth: ['28', '29', '30']
}, {
    id: 'innerTemperature',
    name: '内部温度',
    unit: '℃',
    auth: ['28', '29', '30']
},
//================================
{
    imgPath: 'outPower',
    id: "activePower",
    name: '有功功率',
    unit: 'kW',
    auth: ['79']
}, {
    id: 'voltage',
    name: '电压',
    unit: 'V',
    auth: ['79']
}, {
    id: 'current',
    name: '电流',
    unit: 'A',
    auth: ['79']
}, {
    id: 'soc',
    name: '当前车辆SOC',
    unit: '%',
    auth: ['79']

}].filter((item) => item.auth.includes(unref(deviceType)));
const consistencyColorArray = ['#ffffff', '#34E800', '#FF0000'];
const consistencyTxtArray = ['未知', '正常', '异常'];
</script>
<template>
    <MonitorBlock :icon="icon" :title="title">
        <template #content>
            <div
                class="w-full h-full box-border grid grid-items-center grid-cols-4 grid-rows-1 gap-47px px-40px box-border">
                <div v-for="(item, index) in realtimeQuotaList" :key="item.id"
                    class="w-138px h-120px relative flex flex-col justify-end items-center pb-10px box-border">
                    <img :src="`/img/monitor/common/deviceQuotaItemBg.png`"
                        class="w-full h-full absolute top-0 left-0 z--2" />
                    <img v-if="item.imgPath" :src="`/img/monitor/quota/${item.imgPath}.png`" class="w-32px h-32px" />
                    <img v-else :src="`/img/monitor/quota/${item.id}.png`" class="w-32px h-32px" />
                    <img :src="`/img/monitor/common/quotaBase.png`" class="w-66px h-26px mt--7px mb-12px" />
                    <template v-if="item.isBoolean">
                        <div class="w-full flex justify-center items-center  h-24px" :style="{
                            '--color': consistencyColorArray[data[item.id]] ?? consistencyColorArray[0]
                        }">
                            <span class="w-8px h-8px rounded-50% mr-8px" style="background-color: var(--color);"></span>
                            <span class="text-14px ml-7px" style="color: var(--color);">{{
                                consistencyTxtArray[data[item.id]] ?? consistencyTxtArray[0] }}</span>
                        </div>
                    </template>
                    <template v-else>
                        <div class="w-full flex justify-center items-center h-24px">
                            <span class="text-24px text-white family-fb">{{ data[item.id] ?? '--' }}</span>
                            <span class="text-14px text-[#00CCFF] ml-7px">{{ item.unit }}</span>
                        </div>
                    </template>
                    <div class="text-14px text-white flex justify-start items-end whitespace-nowrap mt-7px">
                        {{ item.name }}
                        <el-popover v-if="item.tips" popper-class="w-auto!" placement="bottom" teleported="true">
                            <div class="w-auto whitespace-nowrap overflow-hidden text-ellipsis">{{ item.tips }}</div>
                            <template #reference>
                                <img src="/img/monitor/common/tips.svg" class="w-8px h-8px ml-6px cursor-pointer"
                                    alt="tips" />
                            </template>
                        </el-popover>
                    </div>
                    <img v-if="index !== realtimeQuotaList.length - 1" src="/img/monitor/common/quotaConnect.png"
                        class="w-42px h-42px absolute top-39px right--42px z--1" />
                </div>
            </div>
        </template>
    </MonitorBlock>
</template>