<script setup lang="js">
import { MonitorBlock } from '@/views/monitor/_slice/base';
import { gunWorkStateMap, gunQuotaListLeft, gunQuotaListRight, gunOperationList } from "../constant";
import { computed, ref } from 'vue';
import OpDialog from "./dialog.vue";
import {
    commonVChartProps,
} from "@/views/monitor/systemMonitoring/constant";


const props = defineProps({
    gunInfo: {
        type: Object,
        required: true
    },
    realtimeData: {
        type: Object,
        default: () => ({})
    }
});
const gunWorkState = computed(() => gunWorkStateMap[`${props.realtimeData.gunWorkState}`] ?? gunWorkStateMap['-1']);
const opType = ref('');

const noOperate = (index) => {
    const gunWorkState = props.realtimeData.gunWorkState;
    // 启动充电 or 启动放电
    return (index < 2 && gunWorkState !== 4) ||
        //功率控制
        (index === 2 && !([1, 2].includes(gunWorkState))) ||
        //停止充放
        (index === 3 && !([1, 2, 8].includes(gunWorkState)));
}
const openDialog = (opId, index) => {
    if (noOperate(index)) {
        return;
    }
    opType.value = opId;
}
const onDialogClose = () => {
    opType.value = '';
}
const gunInfoForDialog = computed(() => {
    return {
        ...props.realtimeData,
        pileCode: props.gunInfo.pileCode,
    }
});
const realtimeDataComputed = computed(() => props.realtimeData);
//充电放电中才展示
const showLeftFieldValue = computed(() => props.realtimeData.gunStatus === 2 || props.realtimeData.gunStatus === 5);
</script>
<template>
    <MonitorBlock :title="gunInfo.gunName || realtimeData.gunName" icon="gun">
        <template #sub>
            <div class="ml-11px w-auto h-24px px-13px pt-3px pb-5px box-border flex justify-center items-center " :style="{
                backgroundColor: gunWorkState.color
            }">
                {{ gunWorkState.text }}
            </div>
        </template>
        <template #content>
            <OpDialog v-if="opType" :op-type="opType" @close="onDialogClose" :gun-info="gunInfoForDialog" />
            <div class="w-full h-159px flex justify-start items-center">
                <div class="w-144px h-121px flex justify-center items-center relative">
                    <img src="/img/monitor/pile/pileGun.png" class="h-full w-auto" alt="电枪" />
                    <img :src="`/img/monitor/pile/gunWorkState_${realtimeData.gunWorkState ?? -1}.png`"
                        class="w-86px h-94px absolute bottom-24px" />
                </div>
                <div class="flex-1 flex justify-start items-center">
                    <div class="w-333px  flex flex-wrap justify-between items-center">
                        <div v-for="leftItem in gunQuotaListLeft" :key="leftItem.id"
                            class="relative w-102px h-60px pl-32px box-border flex flex-col justify-center items-start">
                            <img :src="`/img/monitor/common/infoQuotaBg/${leftItem.id}.png`"
                                class="w-full h-full z--1 absolute top-0 left-0" :alt="`${leftItem.name}背景`" />
                            <div class="text-12px text-white text-center mb-5px whitespace-nowrap">
                                {{ leftItem.name }}
                            </div>
                            <div class="w-full h-21px flex justify-start items-center">
                                <div class="text-18px text-white family-fb">{{ showLeftFieldValue ?
                                    (realtimeDataComputed[leftItem.id] ?? '--') : '--'
                                }}
                                </div>
                                <span class="ml-5px text-10px text-[#00CCFF]">{{ leftItem.unit }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="w-413px h-128px relative overflow-hidden ml-34px">
                        <v-chart :option="gunInfo.option" v-bind="commonVChartProps" />
                    </div>
                    <div class="ml-24px flex flex-col w-102px h-full ">
                        <div v-for="rightItem in gunQuotaListRight" :key="rightItem.id"
                            class="relative w-102px h-60px pl-32px box-border flex flex-col justify-center items-start">
                            <img :src="`/img/monitor/common/infoQuotaBg/${rightItem.id}.png`"
                                class="w-full h-full z--1 absolute top-0 left-0" :alt="`${rightItem.name}背景`" />
                            <div class="text-12px text-white text-center mb-5px whitespace-nowrap">
                                {{ rightItem.name }}
                            </div>
                            <div class="w-full h-21px flex justify-start items-center">
                                <div class="text-18px text-white family-fb">{{ gunInfo[rightItem.id] ?? '--' }}
                                </div>
                                <span class="ml-5px text-10px text-[#00CCFF]">{{ rightItem.unit }}</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div
                    class="ml-33px mr-24px w-440px h-144px px-49px py-10px box-border relative grid grid-cols-2 grid-rows-2 gap-x-5px gap-y-8px grid-justify-items-center grid-items-center">
                    <img src="/img/monitor/remoteControlBg.png" class="h-full w-full absolute z--3 top-0 left-0"
                        alt="控制背景" />
                    <div v-for="(op, index) in gunOperationList" :key="op.id" @click="openDialog(op.id, index)"
                        class="w-166px h-54px px-21px box-border flex justify-start items-center relative cursor-pointer hover:filter-brightness-120"
                        :class="{
                            'cursor-not-allowed!': noOperate(index)
                        }">
                        <img src="/img/monitor/controlUnitBg.png" class="h-full w-full absolute z--2 top-0 left-0"
                            alt="控制背景" />
                        <div class="w-42px mr-14px relative flex flex-col justify-end items-center">
                            <img :src="`/img/monitor/quota/${op.icon}.png`" class="w-20px mb--10px" alt="控制背景" />
                            <img src="/img/monitor/common/quotaBase.png" class="w-full w-19px " alt="控制背景" />
                        </div>
                        <div class="text-16px text-white">
                            {{ op.name }}
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </MonitorBlock>
</template>