<script setup lang="js">
import { unref, inject } from 'vue';
import monitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import { TxStatusEnum, RunStateEnum, WorkStatusEnum } from "@/common/enum";
import { INJECT_KEY_DEVICE_INFO, airStateList } from "../constant";
import { key } from '@/utils/localStorageUtil';

const props = defineProps({
    title: {
        type: String,
        default: ''
    },
    icon: {
        type: String,
        default: ''
    },
    avatar: {
        type: String,
        default: ''
    },
    info: {
        type: Object,
        default: () => ({})
    }
});
const { deviceType } = inject(INJECT_KEY_DEVICE_INFO);
const infoItemList = [{
    id: 'txStatus',
    name: '通信状态',
    //0-未注册 1-在线 2-维护 3-故障 88-离线
    formatter: (val) => {
        return TxStatusEnum[val] || TxStatusEnum.other;
    },
    auth: ['20', '23', '25', '28', '29', '30', '60', '65', '66', '79']
}, {
    id: 'workStatus',
    name: '工作状态',
    //0-待机 1、2、3运行 其它的表示故障
    formatter: (val) => {
        return WorkStatusEnum[val] || WorkStatusEnum.other;
    },
    auth: ['28', '29', '30']

}, {
    id: 'runState',
    key: 'runStateName',
    name: '工作状态',
    //0-待机 1、2、3运行 其它的表示故障
    // formatter: (val) => {
    //     console.log(val,'runStateName',RunStateEnum);
    //     return RunStateEnum[val] || RunStateEnum.other;
    // },
    auth: ['20', '65', '66']
},
{
    id: 'runState',
    key: 'runStateName',
    name: '工作状态',
    title: "superStatus",
    //0-待机 1、2、3运行 其它的表示故障
    // formatter: (val) => {
    //     console.log(val,'runStateName',RunStateEnum);
    //     return RunStateEnum[val] || RunStateEnum.other;
    // },
    auth: ['79']
},
{
    id: 'runState',
    key: 'runStateName',
    name: '电池簇状态',

    auth: ['25']
}, {
    id: 'runState',
    key: 'runStateName',
    name: 'PCS状态',

    auth: ['23']
},
{
    id: 'airRunState',
    name: '工作状态',
    auth: ['60']
}, {
    id: 'ratedPower',
    name: '额定功率',
    unit: 'kW',
    auth: ['20', '23', '29', '30', '79']

}, {
    id: 'ratedCap',
    name: '额定容量',
    unit: 'kWh',
    auth: ['25', '28']
}, {
    id: 'model',
    name: '设备型号',
    auth: ['20', '23', '25', '28', '29', '30', '60', '79']
}, {
    id: 'longitude',
    name: '经度',
    auth: ['65', '66']
}, {
    id: 'latitude',
    name: '纬度',
    auth: ['65', '66']
}, {
    id: 'manufacturerName',
    name: '设备厂家',
    auth: ['20', '23', '25', '28', '29', '30', '60', '65', '66', '79']

}].filter((item) => item.auth.includes(unref(deviceType)));
const statusColor = (value) => {
    return statusMap[value] || '#999';
};

const statusMap = {
    '未知': '#999',
    '停机': '#FF4D4D',
    '待机': '#2196f3',
    '运行': '#4caf50',
    '故障': '#f44336',
    '充电': '#4caf50',
    '放电': '#4caf50',
    '禁充': '#ff9800',
    '禁放': '#ff9800',
    '告警': '#f44336',
    '休眠': '#9E9E9E',
    // 超充
    '空闲':'#2196F3',
    '预约':'#03A9F4',
    '充电':"#4CAF50",
    '放电':'#FF9800',
    '占用':'#FF9800',
    



};


</script>
<template>
    <monitorBlock :icon="icon" :title="title">
        <template #content>
            <div class="w-full h-full pl-15px pr-19px flex justify-start items-center box-border">
                <!-- 左侧设备模型图 -->
                <div class="w-125px overflow-hidden mr-17px flex justify-center items-center relative">
                    <img :src="`/img/monitor/device/${avatar}.png`" loading="lazy" decoding="async" class="w-full"
                        alt="设备模型图" />
                </div>
                <!-- 右侧设备信息 -->
                <div class="flex-1 flex flex-col justify-start items-stretch">
                    <div class="h-30px flex justify-center items-start device-name">
                        <span class="text-white text-16px family-fb">{{ info?.deviceName || '--' }}</span>
                    </div>
                    <div class="h-16px mb-16px text-center">
                        <span class="text-[#C7E8FF] text-12px">SN:{{ info?.deviceNumber || '--' }}</span>
                    </div>
                    <div class="h-60px flex justify-between items-start">
                        <div class="w-102px h-60px pl-32px pt-9px bg-no-repeat bg-cover box-border flex flex-col justify-start items-stretch relative"
                            v-for="item in infoItemList">
                            <img :src="`/img/monitor/common/infoQuotaBg/${item.id}.png`"
                                class="absolute w-full h-full z--1 top-0 left-0" />
                            <div class="text-14px text-white">
                                {{ item.name }}
                            </div>
                            <div class="flex justify-start items-center">
                                <!-- 通信状态和工作状态 -->
                                <div v-if="['txStatus', 'workStatus'].includes(item.id)"
                                    class="w-full text-12px text-white flex justify-start items-center mt-7px">
                                    <i class="flex w-8px h-8px mr-8px rounded-50% status-item"
                                        :class="`${item.id}-${info[item.id] ?? 'other'}`" />
                                    {{ item.formatter(info[item.id]) }}
                                </div>
                                <div v-if="['runStateName'].includes(item.key)"
                                    class="w-full text-12px text-white flex justify-start items-center mt-7px">
                                    <i class="flex w-8px h-8px mr-8px rounded-50% status-item" v-if="item.title"
                                        :style="{ backgroundColor: statusColor (info[item.key]), boxShadow: `0px 0px 4px 1px ${statusColor (info[item.key])}` }" />
                                    <i class="flex w-8px h-8px mr-8px rounded-50% status-item" v-else
                                        :style="{ backgroundColor: statusColor(info[item.key]), boxShadow: `0px 0px 4px 1px ${statusColor(info[item.key])}` }" />
                                   {{ info[item.key] ?? '未知' }}

                                </div>
                                <!-- 空调设备的工作状态 -->
                                <div v-if="['airRunState'].includes(item.id)"
                                    class="w-full text-12px text-white flex flex-shrink-0 justify-start items-center mt-10px transform-translate-x--20px">
                                    <span v-for="(state, index) in airStateList" :key="state.id"
                                        class="flex-shrink-0 flex w-16px h-16px iconfont" :title="state.name" :class="{
                                            'ml-8px': index > 0,
                                            [`icon-${state.id}`]: true,
                                            'text-[#34E800]': info[item.id]?.[state.id] === 1,
                                            'text-[#AAAAAA]': info[item.id]?.[state.id] === 0
                                        }"></span>
                                </div>
                                <!-- 额定功率 -->
                                <div v-if="['ratedPower', 'ratedCap'].includes(item.id)"
                                    class="flex justify-start items-center mt-4px">
                                    <span
                                        class="family-fb text-18px text-white max-w-60px overflow-hidden text-ellipsis whitespace-nowrap">{{
                                            info[item.id] ?? '--' }}</span>
                                    <span class="text-14px text-[#00CCFF] ml-4px">{{ item.unit }}</span>
                                </div>
                                <!-- 设备型号和设备厂家 -->
                                <div v-if="['model', 'manufacturerName', 'longitude', 'latitude'].includes(item.id)"
                                    class="w-full text-18px text-white mt-4px">
                                    <el-popover placement="bottom" teleported="true">
                                        <div>{{ info[item.id] ?? '--' }}</div>
                                        <template #reference>
                                            <div class="w-80px whitespace-nowrap overflow-hidden text-ellipsis">{{
                                                info[item.id] ?? '--' }}</div>
                                        </template>
                                    </el-popover>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </monitorBlock>
</template>
<style lang="scss" scoped>
.device-name {
    background-image: url('/img/monitor/deviceNameBg.png');
    background-repeat: no-repeat;
    background-size: 100% 18px;
    background-position: left bottom;
}

.status-item {
    --color: #fff;
    background-color: var(--color);
    box-shadow: 0px 0px 4px 1px var(--color);
}

.txStatus-0 {
    --color: #d3d3d3;
    /* 未注册 */
}

.txStatus-1 {
    --color: #4caf50;
    /* 在线 */
}

.txStatus-2 {
    --color: #ff9800;
    /* 维护 */
}

.txStatus-3 {
    --color: #f44336;
    /* 故障 */
}

.txStatus-88 {
    --color: #9e9e9e;
    /* 离线 */
}

.txStatus-other {
    --color: #607d8b;
    /* 其他 */
}


.runState-0 {
    --color: #2196f3;
    /* 待机 */
}

.runState-1,
.runState-2,
.runState-3 {
    --color: #4caf50;
    /* 运行 */
}

.runState-other {
    --color: #f44336;
    /* 故障 */
}

//电桩工作状态 1-在线 2-维护 3-故障 88-离线
.workStatus-0 {
    --color: #2196f3;
    /* 待机 */
}

.workStatus-1,
.workStatus-2,
.workStatus-3 {
    --color: #4caf50;
    /* 运行 */
}

.workStatus-other {
    --color: #f44336;
    /* 故障 */
}
</style>