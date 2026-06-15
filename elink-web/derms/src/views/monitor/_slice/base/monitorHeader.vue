<template>
    <div class="w-full h-67px position-relative flex items-center justify-center monitor-header">
        <!-- 左侧气象信息展示 -->
        <div></div>
        <!-- 系统子标题 -->
        <div class="w-334px position-relative flex-center">
            <span class="text-32px flex-center select-none whitespace-nowrap sub-system-name family-zihun">{{ systemName
            }}</span>
        </div>
        <!-- 子系统辅助工具： 全屏切换、退出子系统 -->
        <div class="position-absolute flex items-center justify-end h-30px w-auto right-42px top-5px"  @click="switchFullscreen">
            <el-tooltip class="item" effect="dark" :content="fullscreenTip" placement="top" v-if="inFrame">
                <i :class="`${iconClass} ${isFullscreen ? 'icon-quxiaoquanping' : 'icon-quanping'}`"
                   ></i>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="退出子系统" placement="top" v-else>
                <i class="icon-tuichu" :class="iconClass" @click="exitSystem"></i>
            </el-tooltip>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAppStore, useMonitorStore } from '@/stores/index';

import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();
const monitorStore = useMonitorStore();
    const appStore = useAppStore();
const isFullscreen = computed(() => monitorStore.isFullscreen);
const systemName = computed(() => monitorStore.systemName);
const inFrame = computed(() => window.top !== window);
const iconClass = 'header-icon iconfont text-15px text-white cursor-pointer hover:filter-brightness-120 rounded-50% flex items-center justify-center w-30px h-30px ml-18px';
const fullscreenTip = computed(() => {
    return isFullscreen.value ? '退出全屏' : '全屏';
});
const switchFullscreen = () => {
    const payload = !isFullscreen.value;
    monitorStore.updateIsFullscreen(payload);
    window.top.postMessage({
        action: "fullScreenChange",
        payload
    });
};
const exitSystem = () => {
    ElMessageBox.confirm(`确定退出系统吗？`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        showClose: false, closeOnClickModal: false,
    }).then(() => {
        window.top.postMessage({
            action: "exitSystem"
        });
        appStore.exitSystem({ noReload: true });
        setTimeout(() => {
            router.replace(`/login?id=${route.params.id}`);
        });
    });
}

</script>

<style lang="scss" scoped>
.monitor-header {
    background: url("/img/monitor/monitorHeader.png");
    background-repeat: no-repeat;
    background-size: 1920px 87px;
}

.header-icon {
    box-shadow: inset 0 0 8px 3px #34adffb8;
    // background: linear-gradient(180deg, rgba(52, 173, 255, 0.72) 0%, rgba(52, 173, 255, 0) 50%, rgba(52, 173, 255, 0.72) 100%);
}

.sub-system-name {
    background: linear-gradient(180deg, #F9FCFF 0%, #358AC3 100%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}
</style>