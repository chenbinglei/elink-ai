<template>
  <div class="historyDataTable">
    <div class="content_top">
      <div class="dateTime">
        <el-date-picker v-model="queryDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts"
                        @calendar-change="pickerOptions.calendarChange" end-placeholder="结束日期" range-separator="-"
                        start-placeholder="开始日期" type="daterange" unlink-panels value-format="YYYY-MM-DD" />
      </div>
      <TabBackground v-model:tabsIndex="componentName" :tabsArray="tabsArray"></TabBackground>
    </div>

    <component ref="componentRef" :is="componentName"></component>

    <!--    清除节点数据-->
    <ClearNodeHistoryData v-if="clearNodeVisible" v-model:isVisible="clearNodeVisible" :computeNodeId="computeNodeId" @changEvent="listArray('refresh')"></ClearNodeHistoryData>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import HistoryToTable from "./HistoryToTable";
import HistoryToCharts from "./HistoryToCharts";
import {onMounted, reactive, toRefs} from "vue";
import ClearNodeHistoryData from "./ClearNodeHistoryData";
import {pickerDateOneMonthDay, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default {
  name: "HistoryDataTable",
  components: { HistoryToCharts, HistoryToTable, ClearNodeHistoryData },
  props: {
    // 当前编辑的节点id
    computeNodeId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 320,
      clearNodeVisible: false,
      queryDate: pickerDateOneMonthDay(),
      pickerOptions: pickerOptionsGthanAcTime(),

      componentName: "HistoryToCharts",
      tabsArray: [{ id: "HistoryToCharts", name: "图表" }, { id: "HistoryToTable", name: "数据" }],
    })

    const listArray = (operateType) => {
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray}
  }
}
</script>

<style scoped lang="scss">
.content_top {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .dateTime {
    width: 300px;
  }
}
</style>
