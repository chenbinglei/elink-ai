<script setup lang="js">
import MonitorBlock from '@/views/monitor/_slice/base/monitorBlock.vue';
import FancyTab from '@/components/Tabs/FancyTab.vue';
import useSingleBattery from "../_hooks/useSingleBattery";
import EmptyView from "@/views/monitor/_slice/base/empty.vue";

const {
    tipList, tabs, activeTab, loading, cellIndex,
    pagination, cellList, currentUnit, curveLoading,
    currentChartTitle, date, dateProps, option, chartProps,
    handleSizeChange, handleCurrentChange, onExport, selectCell
} = useSingleBattery();
</script>
<template>
    <MonitorBlock title="单体电池信息" icon="singleCellInformation">
        <template #sub>
            <el-popover popper-class="w-auto!" placement="top" teleported="true">
                <div class="w-auto flex flex-col justify-start items-stretch">
                    <div class="h-20px flex-shrink-0 flex justify-start items-center" v-for="tip in tipList"
                        :key="tip.name">
                        <div class="w-72px text-14px text-[#00CCFF] mr-15px">{{ tip.name }}</div>
                        <div class="text-14px flex-shrink-0 text-white mr-15px flex justify-start items-center"
                            v-for="child in tip.children">
                            <div class="w-full flex justify-center items-center" :style="{
                                '--color': child.color
                            }">
                                <span class="w-8px h-8px rounded-50% mr-8px"
                                    style="background-color: var(--color);"></span>
                                <span class="text-14px ml-7px">{{ child.name }}</span>
                            </div>
                        </div>
                    </div>
                </div>
                <template #reference>
                    <img src="/img/monitor/common/tips.svg" class="w-8px h-8px ml-6px cursor-pointer" alt="tips" />
                </template>
            </el-popover>
        </template>
        <template #extra>
            <FancyTab :tabs="tabs" v-model:activeTab="activeTab" />
        </template>
        <template #content>
            <div class="w-full h-full flex  justify-start items-stretch">
                <!--  -->
                <div class="w-745px flex flex-col justify-start items-stretch pl-27px box-border" v-loading="loading">
                    <!-- <el-pagination :current-page="pagination.currentPage" class="py-4px justify-end"
                        :page-size="pagination.pageSize" :total="pagination.total" @size-change="handleSizeChange"
                        @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" /> -->
                    <div class="flex-1 relative overflow-x-hidden overflow-y-auto">

                        <div class="flex flex-wrap justify-start items-start h-56px" v-if="cellList.length > 0">

                            <div v-for="cell in cellList" :key="cell.id" @click="selectCell(cell.id)"
                                class="battery-cell w-135px h-56px relative flex justify-start items-center pl-32px box-border mr-6px mb-4px cursor-pointer hover:filter-brightness-120"
                                :class="`state-${cell.state} ${cell.id === cellIndex ? 'filter-brightness-150' : ''}`">
                                <span
                                    class="text-14px text-white font-bold family-arial absolute top-6px left-4px cell-name">{{
                                        cell.name
                                    }}</span>
                                <span class="text-24px text-white family-fb">{{ cell.value }}</span>
                                <span class="text-14px text-[#00CCFF] ml-4px">{{ currentUnit }}</span>
                                <i v-if="cell.id === cellIndex"
                                    class="iconfont icon-duihao text-16px text-[#00CCFF] absolute top-4px right-4px" />
                            </div>
                        </div>
                        <EmptyView v-else />
                        
                    </div>
                    <el-pagination :current-page="pagination.currentPage" class="py-4px justify-end"
                        :page-size="pagination.pageSize" :total="pagination.total" @size-change="handleSizeChange"
                        @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" />
                </div>
                <!-- 电芯电压曲线 -->
                <div class="ml-72px flex-1 relative pt-24px pr-24px" v-loading="curveLoading">
                    <span class="absolute top-33px left-16px text-white text-18px family-fb">{{ currentChartTitle
                    }}</span>
                    <div class="w-full h-full overflow-hidden relative" v-loading="loading">
                        <div class="absolute right-0 top-0 z-2">
                            <el-date-picker v-model="date" v-bind="dateProps" />
                            <el-button class="text-white text-16px ml-8px" @click.stop="onExport"><i
                                    class="iconfont icon-daochu w-16px h-16px text-16px mr-8px" />导出</el-button>
                        </div>
                        <v-chart :option="option" v-bind="chartProps" class="overflow-hidden battery-chart" />
                    </div>
                </div>
            </div>
        </template>
    </MonitorBlock>
</template>
<style lang="scss" scoped>
.battery-chart {
    :deep(.vue-echarts-inner) {
        height: 100% !important;
    }
}

.battery-cell {
    --cell-color: #ffffff;
    border: 1px solid var(--cell-color);
    box-shadow: inset 0 0 10px var(--cell-color);

    &.state-none {
        --cell-color: #595959;
    }

    &.state-0 {
        --cell-color: #00619A;
    }

    &.state-1 {
        --cell-color: #B58229;
    }

    &.state-2 {
        --cell-color: #780707;
    }

    .cell-name {
        --width: 24px;

        &::after {
            content: '';
            display: inline-block;
            width: 0;
            height: 0;
            border-width: var(--width);
            border-style: solid;
            border-color: var(--cell-color) transparent transparent var(--cell-color);
            position: absolute;
            top: -6px;
            left: -4px;
            z-index: -1;
        }
    }
}
</style>