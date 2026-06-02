<template>
    <div class="w-full h-full flex flex-col items-stretch justify-start overflow-hidden" v-loading="isSwitching">
        <MonitorHeader />
        <div
            class="flex-1 flex items-center justify-center px-10px box-border position-relative overflow-hidden monitor-body">
            <MonitorSideTab :tab-list="sideTabList" />
            <router-view class="z-11" />
        </div>
        <MonitorTab class="z-100" :auth-searching="loading" />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from "vue-router";
import { MonitorHeader, MonitorTab, MonitorSideTab } from "./_slice/base";
import SystemMonitorController from "@/api/together/systemMonitor";
import { computed } from 'vue';

const store = useStore();
const route = useRoute();
const loading = ref(false);
const isSwitching = computed(() => store.state.app.isSwitching);

const getSiteInfo = async () => {
    loading.value = true;
    const siteId = route.params.id;
    try {
        const { success, data } = await SystemMonitorController.findSiteDetailById(siteId);
        if (success) {

            localStorage.setItem("StationRoulist", data.readwriteObject);
            console.log(JSON.stringify(data.readwriteObject));
            store.dispatch("updateSiteInfo", {
                ...data,
            });
        }
    } finally {
        loading.value = false;
    }
}

onMounted(() => {
    getSiteInfo();
    store.dispatch('getAssetTypeList');
});
</script>

<style lang="scss" scoped>
.monitor-body {
    background: url("/img/monitor/routeViewBg.png");
    background-repeat: no-repeat;
    background-size: 100% 100%;

    &::before {
        content: '';
        position: absolute;
        width: calc(100% - 20px);
        height: 100%;
        background: url("/img/monitor/monitorBody.png");
        background-repeat: no-repeat;
        background-size: 100% 100%;
        z-index: 100;
        pointer-events: none;
    }
}
</style>