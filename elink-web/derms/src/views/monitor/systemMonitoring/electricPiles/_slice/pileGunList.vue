<script setup lang="js">
import PileGunItem from "./pileGunItem.vue";
import EmptyView from "@/views/monitor/_slice/base/empty.vue";
import usePileGunList from "../_hooks/usePileGunList";

const props = defineProps({
    info: {
        type: Object,
    }
});
const { gunList, gunRealtimeDataMap, loading } = usePileGunList(props);
</script>
<template>
    <div class="w-full flex flex-col justify-start items-stretch min-h-200px" v-loading="loading">
        <template v-if="gunList?.length > 0">
            <PileGunItem v-for="(gun, index) in gunList" :key="gun.gunCode" :gun-info="gun"
                :realtime-data="gunRealtimeDataMap[gun.gunCode]" :class="{
                    'mt-4px': index > 0
                }" />
        </template>
        <template v-else>
            <EmptyView />
        </template>
    </div>
</template>