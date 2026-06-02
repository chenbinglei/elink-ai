<template>
  <MonitorBlock :title="title" :icon="icon">
    <template #extra>
      <FancyTab
        v-if="tabList?.length > 1"
        :tabs="tabList"
        v-model:activeTab="activeTabId"
        @update:activeTab="onTabChange"
      />
      <FancyTab
        v-if="typeId == 'meterPower'"
        :tabs="tabListPower"
        v-model:activeTab="activePowerId"
        @update:activeTab="onPowerChange"
      />
    </template>
    <template #content>
      <div class="w-full h-full overflow-hidden relative" v-loading="loading">
        <div class="absolute right-25px top-14px z-2">
          <el-date-picker
            v-model="date"
            v-bind="pickerProps"
            v-on="pickerEvents"
            v-if="!hidePicker"
          />
          <el-button class="text-white text-16px ml-8px" @click.stop="onExport"
            ><i
              class="iconfont icon-daochu w-16px h-16px text-16px mr-8px"
            />导出</el-button
          >
        </div>
        <v-chart
          ref="myChart"
          :option="chartOptions"
          v-bind="commonVChartProps"
        />
      </div>
    </template>
  </MonitorBlock>
</template>

<script setup>
import { MonitorBlock } from "@/views/monitor/_slice/base";
import FancyTab from "@/components/Tabs/FancyTab.vue";
// 引入chart
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart, BarChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
} from "echarts/components";
import VChart from "vue-echarts";
// hooks
import useBlockChart from "../_hooks/useMergeChart";
//
import { commonVChartProps } from "../constant";
import { onMounted, ref } from "vue";

use([
  CanvasRenderer,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  LineChart,
  BarChart,
]);
const tabListPower = ref([
  {
    id: 25,
    name: "有功功率",
  },
  {
    id: 26,
    name: "无功功率",
  },
]);
const props = defineProps({
  typeId: {
    type: String,
    default: "1",
  },
  fileNameFun: {
    type: Function,
    required: false,
  },
});
onMounted(() => {});
const {
  title,
  icon,
  //tab
  tabList,

  activeTabId,
  onTabChange,
  onPowerChange,
  //日期
  hidePicker,
  date,
  pickerProps,
  pickerEvents,
  //chart
  loading,
  myChart,
  chartOptions,
  activePowerId,
  onExport,
} = useBlockChart({
  ...props,
});
</script>