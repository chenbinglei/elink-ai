<template>
  <div class="content_body">
    <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight - 110" stripe>
      <el-table-column fixed="left" label="序号" type="index" width="100"></el-table-column>
      <el-table-column fixed="left" label="时间" min-width="200">
        <template #default="{ row }">{{ $filters.moreData(row) }}</template>
      </el-table-column>
      <!-- <template v-for="(item, index) in data_info_list" :key="index">
        <el-table-column :label="item.name" min-width="180" show-overflow-tooltip>
          <template #default="{ row, $index }">{{ $filters.moreData(item.dataList[$index]) }}</template>
        </el-table-column>
      </template> -->
      <template v-for="(item, index) in data_info_list" :key="index">
        <el-table-column :label="item.name" min-width="180" show-overflow-tooltip>
          <template #default="{ row, $index }">
            {{ $filters.moreData(item.dataList[$index]) }}
          </template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>

<script>
import pinyin from "js-pinyin";
import { ElMessage } from "element-plus";
import { exportCustomExcel } from "@/common/common/exportExcel";
import { reactive, toRefs, defineComponent, getCurrentInstance, watch } from "vue";

export default defineComponent({
  name: "DataQueryTable",
  props: {
    xaxisList: {
      type: Array,
      default: () => []
    },
    dataInfoList: {
      type: Array,
      default: () => []
    },
    tableMaxHeight: {
      type: Number,
      default: 300
    },
    listLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ["update:listLoading"],
  setup (props) {

    const { emit } = getCurrentInstance();

    const that = reactive({
      list: [],
      data_info_list: []
    })

    // 导出查询数据
    const exportSearchDataFun = (fileName) => {
      let tableList = [], tableHeader = [];
      try {
        // 获取表格头部数据
        for (let item of that.data_info_list) tableHeader.push({ width: 25, key: pinyin.getFullChars(item.name), name: item.name });
        for (let i = 0; i < that.list.length; i++) {
          let obj_data = {};
          for (let tb_index = 0; tb_index < tableHeader.length; tb_index++) {
            obj_data[tableHeader[tb_index].key] = that.data_info_list[tb_index].dataList[i];
          }
          tableList.push({ dateTime: that.list[i], ...obj_data });
        }
        tableHeader.unshift({ width: 25, key: "dateTime", name: "时间" });
        exportCustomExcel(tableHeader, tableList, fileName);
      } catch (e) {
        console.log("导出失败:", e);
        ElMessage({ type: "error", message: "导出失败", showClose: true });
      }
    }

    const watchDataInfoList = watch([() => props.dataInfoList, () => props.xaxisList], ([newDataInfoList, newXaXisList]) => {
      that.list = JSON.parse(JSON.stringify(props.xaxisList ? props.xaxisList : []));
      console.log("应该回显的数据", that.list)
      // that.data_info_list = JSON.parse(JSON.stringify(props.dataInfoList ? props.dataInfoList : []));
      that.data_info_list = newDataInfoList ? [...newDataInfoList] : [];
      console.log("应该回显的数据that.data_info_list", that.data_info_list)

      emit("update:listLoading", false);
    }, { deep: true, immediate: true })


    return { ...toRefs(that), exportSearchDataFun, watchDataInfoList }
  }
})
</script>

<style scoped></style>
