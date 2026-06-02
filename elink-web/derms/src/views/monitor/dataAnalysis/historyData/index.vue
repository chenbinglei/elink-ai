<template>
    <div class="w-full h-full flex flex-col justify-start items-stretch box-border">
        <!-- 查询栏目 -->
        <div class="h-32px pt-17px pb-20px box-content">
            <SearchBar :config="searchConfig" v-model="formData" :rules="rules" :initData="searchInitData"
                @search="tiggerSearch" @export="tiggerExport" @reset="onReset" />
        </div>
        <!-- 表格 -->
        <div class="flex-1 overflow-hidden" v-loading="loading">
            <MonitorBlock title="数据" icon="powerCurve">
                <template #extra>
                    <FancyTab :tabs="tabs" v-model:activeTab="activeTab" />
                </template>
                <template #content>
                    <div v-if="activeTab === 'chart'" class="h-full">
                        <el-empty v-if="!options?.xAxis?.data?.length" :image="emptyImg" description="暂无数据" />

                        <VChart v-else :option="options" />
                    </div>
    
                    <SearchTable v-show="activeTab === 'table'" ref="tableRef" :fetch-table-data="onSearch"
                        :export-config="exportConfig" :columns="columns" pagination-way="front">
                    </SearchTable>
                </template>
            </MonitorBlock>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart } from 'echarts/charts';
import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
} from 'echarts/components';
import emptyImg from "@/assets/image/empty.png";
import VChart from 'vue-echarts';
import SearchBar from "@/components/searchBar/index.vue";
import SearchTable from "@/components/table/index.vue";
import MonitorBlock from "@/views/monitor/_slice/base/monitorBlock.vue";
import FancyTab from "@/components/Tabs/FancyTab.vue";
// hooks
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";
import useChart from "./useChart";


use([
    CanvasRenderer,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
]);

const { searchConfig, rules, formData, searchInitData } = useSearchBar();
const { loading, rawData, tableRef, columns, exportConfig, tiggerSearch, tiggerExport, onSearch ,onReset} = useTable({ formData });
const { options } = useChart({ rawData });
const tabs = ref([{
    id: 'chart',
    name: '曲线'
}, {
    id: 'table',
    name: '表格'
}]);
const activeTab = ref('chart');
console.log(options, 'xuanzh');

</script>

<style lang="scss" scoped></style>