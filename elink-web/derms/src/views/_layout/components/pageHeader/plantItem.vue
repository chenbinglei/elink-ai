<template>
    <div class="flex flex-col justify-start items-stretch px-20px py-8px plant-item"
        :class="{ 'active': props.isActive }">
        <!-- 站点名称 -->
        <div class="flex items-center justify-start w-full h-20px mb-6px">
            <span class="text-14px flex-1">{{ props.station.siteName }}</span>
            <span class="flex items-center justify-center w-16px h-16px">
                <span v-for="state in props.station.stateList" :key="state"
                    :style="{ backgroundColor: state?.color, '--bs-color': state?.color }"
                    :title="state?.name"
                    class="w-8px h-8px mr-4px rounded-full legend-bs">
                </span>
            </span>
        </div>
        <!-- 租户类型 -->
        <div class="flex items-center justify-start w-full h-18px mb-11px" v-if="isShown">
            <scenarioTypeList :scenario-types="props.station.scenarioTypes" />
        </div>

    </div>
</template>

<script setup>
import { computed } from 'vue';
import scenarioTypeList from './scenarioTypeList.vue';

// 读取外部传入字符串数组参数scenarioTypes
const props = defineProps({
    station: {
        type: Object,
        default: {
            siteName: '',
            stateList: [],
            scenarioTypes: ''
        }
    },
    isActive: {
        type: Boolean,
        default: false
    }
});
const isShown = computed(() => {
    const scenarioTypes = props.station.scenarioTypes;
    return (!['null', 'undefined', 'false', ','].includes(scenarioTypes)) && scenarioTypes.length > 0;
});
</script>

<style lang="scss" scoped>
.plant-item {
    position: relative;
    cursor: pointer;

    &::after {
        content: '';
        width: 100%;
        height: 1px;
        background: #0078C6;
        border-radius: 0px 0px 0px 0px;
    }

    &:not(.active):hover {
        background: #0032544d;
        box-shadow: inset 0px 0px 10px 1px #00C2FF4d;
        border-radius: 0px 0px 0px 0px;

        &::after {
            display: none;
        }
    }
}

.active {
    position: relative;
    background: #003254;
    box-shadow: inset 0px 0px 10px 1px #00C2FF;
    border-radius: 0px 0px 0px 0px;

    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 3px;
        height: 100%;
        background: #ffffff;
        border-radius: 0px 0px 0px 0px;
    }

    &::after {
        display: none;
    }
}
</style>