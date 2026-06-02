<template>
    <div class="w-full h-full flex items-stretch justify-start pl-40px pr-14px pt-7px pb-32px box-border">
        <!-- 左侧设备树 -->
        <div class="w-280px py-12px mr-14px device-tree" v-loading="treeLoading">
            <el-tree v-bind="treeProps" :current-node-key="currentNodeKey" />
        </div>
        <!-- 右侧指标展示 -->
        <div class="flex-1 h-full flex flex-col items-stretch justify-start pb-90px box-border overflow-hidden">
            <component :is="currentView" v-bind="viewProps" />
        </div>
    </div>
</template>

<script setup>
import { provide,watch } from "vue";
// 引入hooks
import useTreeSwitchView from "@/views/monitor/systemMonitoring/_hooks/useTreeSwitchView";
import { INJECT_KEY_DEVICE_INFO } from "./constant";

const props = defineProps({
    viewMap: {
        type: Object,
        required: true,
    },
    tabType: {
        type: Number,
        default: 1
    }
});
const { currentNodeKey,treeProps, treeLoading, currentView, viewProps } = useTreeSwitchView(props);
// 监听选中节点变化
watch(currentNodeKey, (newKey) => {
    // console.log("选中节点 ID：", newKey,props);
});

watch(viewProps, (newData) => {
    // console.log("选中节点数据：", newData);
});
// 向下方的碎片页提供设备信息，方便查询
provide(INJECT_KEY_DEVICE_INFO, viewProps);
</script>

<style lang="scss">
.tree-node-no-click {
    &>.el-tree-node__content {
        pointer-events: none;

        &>.el-tree-node__label {
            cursor: not-allowed;
            pointer-events: none;
        }
    }
}
</style>

<style lang="scss" scoped>
.device-tree {
    border: 1px solid #1C4A87;
}
</style>