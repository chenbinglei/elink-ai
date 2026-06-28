<template>
  <div class="content_body">
    <!-- 表格容器 -->
    <div class="table-container" ref="tableContainer">
      <!-- 表头 -->
      <div class="table-header">
        <div class="table-cell table-cells table-position fixed-column">序号</div>
        <div class="table-cell table-cells table-position fixed-column">时间</div>
        <template v-for="(col, colIndex) in data_info_list" :key="colIndex">
          <div class="table-cell table-cells">{{ col.name }}</div>
        </template>
      </div>

      <!-- 表格行 -->
      <div class="table-body" ref="tableBody">
        <div v-for="(item, index) in list" :key="item.id" class="table-row"
          :class="{ 'even-row': index % 2 === 0, 'odd-row': index % 2 === 1 }">
          <div class="table-cell table-position fixed-column">{{ item.index }}</div>
          <div class="table-cell table-position fixed-column">{{ item.time }}</div>
          <template v-for="(col, colIndex) in data_info_list" :key="colIndex">
            <div class="table-cell">{{ item[col.name] }}</div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import pinyin from "js-pinyin";
import { ElMessage } from "element-plus";
import { exportCustomExcel } from "@/common/common/exportExcel";
import { reactive, toRefs, defineComponent, getCurrentInstance, watch, computed, ref, onMounted } from "vue";

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
    const tableContainer = ref(null);
    const tableBody = ref(null);

    const that = reactive({
      list: [],
      data_info_list: []
    })

    // 导出查询数据
    const exportSearchDataFun = (fileName) => {
      // let tableList = [], tableHeader = [];
      // try {
      //   // 获取表格头部数据
      //   for (let item of that.data_info_list) tableHeader.push({ width: 25, key: pinyin.getFullChars(item.name), name: item.name });
      //   for (let i = 0; i < that.list.length; i++) {
      //     let obj_data = {};
      //     for (let tb_index = 0; tb_index < tableHeader.length; tb_index++) {
      //       obj_data[tableHeader[tb_index].key] = that.data_info_list[tb_index].dataList[i];
      //     }
      //     tableList.push({ dateTime: that.list[i], ...obj_data });
      //   }
      //   tableHeader.unshift({ width: 25, key: "dateTime", name: "时间" });
      //   exportCustomExcel(tableHeader, tableList, fileName);
      // } catch (e) {
      //   console.log("导出失败:", e);
      //   ElMessage({ type: "error", message: "导出失败", showClose: true });
      // }
      let tableList = [], tableHeader = [];
      try {
        // 获取表格头部数据 - 修复：使用正确的列名映射
        for (let item of that.data_info_list) {
          tableHeader.push({
            width: 25,
            key: pinyin.getFullChars(item.name),
            name: item.name
          });
        }

        // 修复：正确处理数据映射
        for (let i = 0; i < that.list.length; i++) {
          let obj_data = {};
          // 添加序号和时间
          // obj_data["index"] = that.list[i].index;
          obj_data["dateTime"] = that.list[i].time;


          // 添加其他数据列
          for (let tb_index = 0; tb_index < tableHeader.length; tb_index++) {
            const colKey = tableHeader[tb_index].key;
            const colName = that.data_info_list[tb_index].name;
            obj_data[colKey] = that.list[i][colName];
          }
          tableList.push(obj_data);
        }

        // 修复：在表头前面添加序号和时间列
        tableHeader.unshift(
          // { width: 10, key: "index", name: "序号" },
          { width: 25, key: "dateTime", name: "时间" },
          
        );

        exportCustomExcel(tableHeader, tableList, fileName);
      } catch (e) {
        console.log("导出失败:", e);
        ElMessage({ type: "error", message: "导出失败", showClose: true });
      }
    }

    const rawData = computed(() => {
      return props.xaxisList.map((time, index) => ({
        id: index,
        time,
        index: index + 1,
        ...props.dataInfoList.reduce((acc, col) => {
          acc[col.name] = col.dataList[index];
          return acc;
        }, {})
      }));
    });

    const watchDataInfoList = watch([() => props.dataInfoList, () => props.xaxisList], ([newDataInfoList, newXaXisList]) => {
      that.list = JSON.parse(JSON.stringify(rawData.value));
      that.data_info_list = JSON.parse(JSON.stringify(props.dataInfoList ? props.dataInfoList : []));
      emit("update:listLoading", false);
    }, { deep: true, immediate: true })

    onMounted(() => {
      // 确保表格容器有正确的滚动设置
      if (tableContainer.value) {
        tableContainer.value.style.overflow = 'auto';
      }
    });

    return {
      ...toRefs(that),
      exportSearchDataFun,
      watchDataInfoList,
      rawData,
      tableContainer,
      tableBody
    }
  }
})
</script>

<style scoped>
.content_body {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.table-container {
  height: 100%;
  overflow: auto;
  position: relative;
  /* border: 1px solid #ebeef5; */
  border-radius: 4px;
  background: white;
}

/* 表头样式 */
.table-header {
  display: flex;
  min-width: max-content;
  /* border-bottom: 1px solid #ebeef5; */
  background-color: #f5f7fa;
  position: sticky;
  top: 0;
  z-index: 20;
}

/* 表格行样式 */
.table-body {
  position: relative;
}

.table-body .table-row {
  display: flex;
  min-width: max-content;
  border-bottom: 1px solid #ebeef5;
}

/* 斑马纹效果 - 关键部分 */
.table-body .table-row.even-row {
  background-color: #ffffff;
  /* 偶数行白色 */
}

.table-body .table-row.odd-row {
  background-color: #f8f9fa;
  /* 奇数行浅灰色 */
}

/* 表格单元格基础样式 */
.table-cell {
  padding: 8px 16px;
  min-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
  /* border-right: 1px solid #ebeef5; */
}

/* 表头单元格特殊样式 */
.table-cells {
  background-color: #f5f7fa;
  font-weight: 600;
  position: relative;
}

/* 固定列样式 - 关键部分 */
.table-position {
  min-width: 150px;
}

/* 固定左侧列的样式 */
.fixed-column {
  position: sticky;
  left: 0;
  background-color: inherit;
  /* 继承行的背景色 */
  z-index: 10;
}

/* 表头中的固定列需要更高的z-index */
.table-header .fixed-column {
  z-index: 25;
  background-color: #f5f7fa;
}

/* 第二列固定（时间列） */
.table-header .table-position:nth-child(2),
.table-row .table-position:nth-child(2) {
  left: 150px;
  /* 与第一列的宽度一致 */
  background-color: inherit;
  /* 继承行的背景色 */
}

/* 确保表头中的固定列有正确的背景 */
.table-header .table-cells.fixed-column {
  background-color: #f5f7fa;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .table-cell {
    min-width: 120px;
    padding: 6px 12px;
  }

  .table-position {
    min-width: 80px;
  }

  .table-header .table-position:nth-child(2),
  .table-row .table-position:nth-child(2) {
    left: 80px;
  }
}
</style>