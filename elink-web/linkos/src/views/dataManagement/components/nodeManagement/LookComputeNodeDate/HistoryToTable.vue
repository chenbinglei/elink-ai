<template>
  <div class="tableContent" v-loading="listLoading">
    <div ref="tableCenterRef" class="tableCenter" v-resize="setTableMaxHeight">
      <el-table ref="multipleTableRef" stripe :data="list" :max-height="tableMaxHeight">
        <el-table-column align="center" label="序号" width="60">
          <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
        </el-table-column>
        <el-table-column align="center" label="存储时间">
          <template #default="{ row }">{{ $filters.moreData(row.ts) }}</template>
        </el-table-column>
        <el-table-column align="center" label="更新时间">
          <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column align="center" label="数据值">
          <template #default="{ row }">{{ $filters.moreData(row.resultValue) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import {reactive, ref, toRefs} from "vue";
import {ElMessage} from "element-plus";

export default {
  name: "HistoryToTable",
  setup() {
    const that = reactive({
      list: [{}],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
    })

    const listArray = (operateType)=>{
      // that.listLoading = true;
      // if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      // findNodeAddRecordListByPage({...that.formInline, recordId: that.recordId, exampleType: that.exampleType, page: that.currentPage,
      //   size: that.pageNum}).then(res => {
      //   that.listLoading = false;
      //   that.list = res.data.items;
      //   that.totalNumber = res.data.totalSize;
      //   if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      // }).catch((error) => {
      //   that.listLoading = false;
      //   if (error && error.code === 88886) return
      //   that.list = [];
      //   that.totalNumber = 0;
      // });
    }

    const tableCenterRef = ref(null);
    // 初始化表格高度
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    };

    return {
      ...toRefs(that), listArray, tableCenterRef, setTableMaxHeight
    }
  }
}
</script>

<style scoped lang="scss">
.tableContent {
  .tableCenter {
    margin: 8px 0;
  }
}
</style>
