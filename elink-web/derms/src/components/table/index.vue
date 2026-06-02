<template>
  <div class="w-full h-full flex flex-col items-stretch justify-start" v-loading="loading" v-resize="setTableMaxHeight">
    <el-table :data="tableData" ref="tableRef" :header-cell-style="headerCellStyle" :header-row-style="{}" @selection-change="handleSelectionChange"
      style="height: calc(78vh - 64px);">

      <el-table-column type="selection" width="55" v-if="selection" />
      <template v-for="(column,index) in columns" :key="column.key">
        <el-table-column :label="column.title" width="auto" :column-key="column.key + index" :prop="column.key" v-if="column.key == 'operate'"
          v-bind="column.extraProps">
          <template #default="scope">
            <span v-for="(ope,index) in column.list" :key="'ope' + index" style="cursor: pointer;color: #00CCFF;margin-right: 24px;"
              :style="{cursor: 'pointer',marginRight: '24px',color: ope.disabledProp ?  (scope.row[ope.disabledProp] == ope.disabledLabel ? '#ccc' : '#00CCFF') : '#00CCFF'}"
              @click="operationBtn(scope.row,ope)">{{ope.name}}
            </span>
          </template>
        </el-table-column>
        <el-table-column v-else :label="column.title" width="auto" :column-key="column.key + index" :prop="column.key" v-bind="column.extraProps">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <component v-if="column.render" :is="column.render" v-bind="{ column, row: scope.row }" />
              <el-popover v-else popper-class="w-auto!" placement="bottom" teleported="true">
                <span class="w-full overflow-hidden text-ellipsis whitespace-nowrap">
                  {{ transformTableCellText(scope.row, column) }}
                </span>
                <template #reference>
                  <span class="w-full overflow-hidden text-ellipsis whitespace-nowrap">
                    {{ transformTableCellText(scope.row, column) }}
                  </span>
                </template>
              </el-popover>
            </div>
            <el-table-column v-for="col in column.children" :key="col.key" :label="col.title" width="auto" :column-key="col.key" :prop="col.key"
              v-bind="col.extraProps">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <component v-if="col.render" :is="col.render" v-bind="{ column: col, row: scope.row }" />
                  <el-popover v-else popper-class="w-auto!" placement="bottom" teleported="true">
                    <span class="w-full overflow-hidden text-ellipsis whitespace-nowrap">
                      {{ transformTableCellText(scope.row, col) }}
                    </span>
                    <template #reference>
                      <span class="w-full overflow-hidden text-ellipsis whitespace-nowrap">
                        {{ transformTableCellText(scope.row, col) }}
                      </span>
                    </template>
                  </el-popover>
                </div>

              </template>
            </el-table-column>
          </template>
        </el-table-column>
      </template>
    </el-table>
    <el-pagination v-if="pagination.total > 0" :current-page="pagination.currentPage" class="mt-12px justify-end" :page-size="pagination.pageSize"
      :total="pagination.total" @size-change="handleSizeChange" :page-sizes="pageSizes" @current-change="handleCurrentChange"
      layout="total, sizes, prev, pager, next, jumper" />
  </div>
</template>

<script setup>
import { ref, reactive, defineEmits, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { exportCustomExcel } from '@/common/exportExcel.js';
import { transformWidthToVW, transformTableCellText } from '@/utils/transform.js';

const props = defineProps({
  columns: {
    type: Array,
    required: true,
    default: () => [],
  },
  paginationWay: {
    type: String,
    default: 'front', // 'front' or 'back'
  },
  fetchTableData: {
    type: Function,
    required: true,
  },
  exportConfig: {
    type: Object,
    default: {
      fileName: '导出表格',
      header: [],
      sheetName: 'Sheet1',
    },
  },
  selection: {
    type: Boolean,
    default: false,
  },
});
const defPagination = {
  currentPage: 1,
  pageSize: 10,
  total: 0,
};
const tableRef = ref(null);
const pageSizes = [10, 20, 30, 40, 50, 100, 200, 500];
const loading = ref(false);
const tableData = ref([]);
const rawData = ref([]);
const pagination = reactive({ ...defPagination });

const headerCellStyle = (col) => {
  return {
    width: transformWidthToVW(col.column.width),
  }
}
const clearData = () => {
  tableData.value = [];
  Object.assign(pagination, defPagination);
}
const fetchTableDataWrap = async (params) => {
  loading.value = true;
  try {
    const { success, data, message } = await props.fetchTableData(params);
    return success ? data : { errors: message ?? '获取数据失败' };
  } catch (e) {
    return { errors: e };
  } finally {
    setTimeout(() => {
      loading.value = false;
    }, 300)
  }
};
const emit = defineEmits(['operationBtn', 'selectionChange']);
const operationBtn = (row, ope) => {
  if (ope.disabledProp && row[ope.disabledProp] == ope.disabledLabel) return
  emit('operationBtn', { row, type: ope.type })
};

const handleSelectionChange = (val) => {
  emit('selectionChange', val);
};
const loadTableData = async () => {
  const { items, pageSize, totalSize, index, errors } = await fetchTableDataWrap(pagination);
  await nextTick();
  if (tableRef.value) {
    tableRef.value.setScrollTop(0);
  }
  if (errors) { return; }
  if (props.paginationWay === 'front') {
    rawData.value = items;
    const { currentPage, pageSize } = pagination;
    tableData.value = items.slice((currentPage - 1) * pageSize, currentPage * pageSize);
    pagination.total = items.length;
  } else {
    pagination.currentPage = index;
    pagination.pageSize = pageSize;
    pagination.total = totalSize;
    tableData.value = items;
  }
};

const handleSizeChange = (size) => {
  pagination.pageSize = size;
  loadTableData();
};

const handleCurrentChange = (page) => {
  pagination.currentPage = page;
  loadTableData();
};

const resetTable = () => {
  pagination.currentPage = 1;
  pagination.pageSize = props.initialPagination?.pageSize || 10;
  loadTableData();
};

const exportTable = async () => {
  let _tableData = rawData.value;
  //区分前后端分页
  if (props.paginationWay === 'back') {
    // 后端分页需要用总页数作为每页条数请求数据
    const { items, errors } = await fetchTableDataWrap({
      pageSize: pagination.total,
      currentPage: 1,
    });
    if (errors) { return; }
    _tableData = items;
  }
  // 使用数据导出
  try {
    const { fileName, header, sheetName } = props.exportConfig;
    await exportCustomExcel(header, _tableData, fileName, sheetName);
  } catch (e) {
    console.error('Error exporting table data:', e);
    ElMessage.error('导出失败，请稍后重试');
  }
};
defineExpose({
  loadTableData,
  resetTable,
  exportTable,
  clearData,
});

</script>