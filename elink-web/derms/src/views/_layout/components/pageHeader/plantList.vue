<template>
    <div class="flex flex-col items-stretch justify-start overflow-hidden pt-16px pb-0 plant-list">
        <!-- 标题和场站数量 -->
        <div class="flex items-center justify-start w-full p-x-20px p-y-0 text-white box-border">
            <img src="@/assets/svg/plant.svg" class="w-21px h-21px mr-9px" />
            <span class="flex-1 text-18px font-bold">场站列表</span>
            <span class="text-14px ml-9px">共 <span class="text-24px gradient-text">{{ plantNum }}</span> 座</span>
        </div>
        <!-- 模糊搜索框 -->
        <div class="flex items-center justify-center w-full p-x-20px p-y-12px box-border">
            <!-- 复合输入框：查询icon + 无边框输入域 + 筛选icon -->
            <div
                class="flex items-center justify-start w-full p-x-12px p-y-6px bg-transparent border-second border-width-1 border-style-solid rounded-4px">
                <i class="iconfont icon-search text-white text-size-20px cursor-pointer" @click="debunceQuery"></i>
                <input type="text" placeholder="请输入场站名称" v-model="siteName" @keyup.enter="debunceQuery"
                    class="w-full text-14px px-8px py-6px bg-transparent border-none text-main placeholder-main focus:outline-none" />
                <i class="iconfont icon-shaixuan text-white text-size-18px cursor-pointer"></i>
            </div>
        </div>
        <!-- 场站列表(可滚动) -->
        <div class="flex-1 flex flex-col w-full overflow-y-auto box-border beutify-scrollbar" v-loading="loading">
            <plantItem v-for="station in stationList" :key="station.id" :station="station"
                :is-active="station.id === active_site_id" @click="selectStation(station)" />
        </div>

        <!-- 场站状态图例：正常 故障 异常通信 -->
        <div class="flex items-center justify-start w-full px-20px py-14px text-14px text-white box-border">
            <div class="flex items-center mr-20px" v-for="item in SiteStateList" :key="item.id">
                <span :style="{ backgroundColor: item.color, '--bs-color': item.color }"
                    class="w-8px h-8px mr-10px rounded-full legend-bs"></span>
                {{ item.name }}
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import { SiteStateList } from "@/common/enum.js";
import plantItem from './plantItem.vue';
import debounce from 'lodash/debounce';

const emits = defineEmits(['stationChange']);

const route = useRoute();
const stationList = ref([]);
const plantNum = computed(() => stationList.value.length); // 场站数量
const stationMenuFilterComRef = ref(null);
const loading = ref(false);
const active_site_id = ref(null); // 当前选中场站ID
const siteName = ref(''); // 场站名称搜索

const siteStateMap = computed(() => {
    const map = new Map();
    SiteStateList.forEach(item => {
        map.set(item.id, item);
    });
    return map;
});

// 选中站点
const selectStation = (station) => {
    active_site_id.value = station.id;
    // 触发事件，传递选中的场站ID subTitle
    emits('stationChange', { ...station });
};

// 查询场站列表
const querySiteListByUserId = () => {
   
    loading.value = true;
    let formInline = {};
    if (stationMenuFilterComRef.value) {
        formInline = JSON.parse(JSON.stringify(stationMenuFilterComRef.value.formInline));
        if (formInline.scenarioTypes) formInline.scenarioTypes = formInline.scenarioTypes.join(',');
        if (formInline.areaValue) formInline.areaValue = formInline.areaValue[formInline.areaValue.length - 1];
    }
    findSiteListByUserId({ ...formInline, siteName: siteName.value, timer: new Date() }).then(res => {
        stationList.value = res.data ? res.data : [];
        if (stationList.value.length > 0) {
            stationList.value.forEach(item => {
                item.stateList = item.siteStates.split(',').filter((a) => a).map(state => siteStateMap.value.get(state));
            });
            // 如果未选中场站ID，则默认选中第一个场站
            let findItem;
            if (!active_site_id.value) {
                findItem = stationList.value[0];
                active_site_id.value = findItem?.id;
            } else {
                // 如果选中的场站ID不在列表中，则默认选中第一个场站
                findItem = stationList.value.find(item => item.id === active_site_id.value);
                if (!findItem) {
                    findItem = stationList.value[0];
                    active_site_id.value = findItem?.id;
                };
            }
            //如果是当前默认第一个进集中监控
            if (route.name === 'centralMonitoring' && route.query.siteId !== findItem.id) {
                selectStation(findItem);
            }
        }
    }).finally(() => {
        loading.value = false;
    });
};
const debunceQuery = debounce(querySiteListByUserId, 250);
// 初始化
onMounted(() => {
    querySiteListByUserId()
});

</script>

<style scoped>
.plant-list {
    width: 320px;
    height: 630px;
    background: rgba(0, 84, 128, 0.9);
    box-shadow: inset 0px 0px 6px 1px #3BAAF5;
    border-radius: 3px 3px 3px 3px;
    border: 1px solid rgba(3, 165, 255, 0.25);
}

.gradient-text {
    background-image: -webkit-linear-gradient(top, #07CDFF, #F4FDFF);
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.legend-bs {
    box-shadow: 0px 0px 4px 1px var(--bs-color);
}
</style>