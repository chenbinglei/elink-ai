<template>
  <div class="content_body" style="height: 100vh;">
    <vxe-table :data="list" v-loading="listLoading" :max-height="tableMaxHeight - 80" stripe show-overflow height="100%"
      :column-config="{ resizable: true }" :row-config="{ isHover: true }" :virtual-x-config="{ enabled: true, gt: 0 }"
      :virtual-y-config="{ enabled: true, gt: 0 }">
      <vxe-column type="seq" title="序号" min-width="54" align="center" fixed="left"></vxe-column>
      <vxe-column field="value" title="时间" min-width="200" align="center" fixed="left">
        <template #default="scope">
          {{ scope.row.value }}
        </template>
      </vxe-column>
      <template v-for="(item, idx) in data_info_list">
        <vxe-column :field="item.name" :title="item.name" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            {{ item.dataList[idx] }}
          </template>
        </vxe-column>
      </template>
    </vxe-table>
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
        // 创建仅含 value 的 list 用于导出
        const exportList = that.list.map(item => item.value);
        for (let i = 0; i < exportList.length; i++) {
          let obj_data = {};
          for (let tb_index = 0; tb_index < tableHeader.length; tb_index++) {
            obj_data[tableHeader[tb_index].key] = that.data_info_list[tb_index].dataList[i];
          }
          tableList.push({ dateTime: exportList[i], ...obj_data });
        }
        tableHeader.unshift({ width: 25, key: "dateTime", name: "时间" });
        exportCustomExcel(tableHeader, tableList, fileName);
      } catch (e) {
        console.log("导出失败:", e);
        ElMessage({ type: "error", message: "导出失败", showClose: true });
      }
    }

    const watchDataInfoList = watch([() => props.dataInfoList, () => props.xaxisList], ([newDataInfoList, newXaXisList]) => {
      let xaxisList = JSON.parse(JSON.stringify(props.xaxisList ? props.xaxisList : []));
      that.list = xaxisList.map((time, index) => ({
        _X_ROW_KEY: index,
        value: time
      }));

      that.data_info_list = newDataInfoList ? [...newDataInfoList] : [];
      emit("update:listLoading", false);
    }, { deep: true, immediate: true })


    return { ...toRefs(that), exportSearchDataFun, watchDataInfoList }
  }
})
</script>

<style scoped></style>
