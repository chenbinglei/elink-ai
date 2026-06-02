<template>
  <div class="w-full h-full" v-loading="loading">
    <div class="h-32px pt-17px pb-20px box-content">
      <SearchBar :config="searchConfig" v-model="formData" :rules="rules" :initData="searchInitData"
        @search="tiggerSearch" @export="tiggerExport" @reset="onReset" />
    </div>
    <!-- 表格 -->
    <div class="flex-1 overflow-hidden" style="flex: 1;height:90%;">
      <SearchTable ref="tableRef" :fetch-table-data="onSearch" :export-config="exportConfig" :columns="columns"
        @operationBtn="operationBtn" pagination-way="back">
      </SearchTable>
    </div>
    <!-- 弹框 忽略-->
    <el-dialog v-model="isVisible" width="20%" align-center>
      <template #header>
        <el-icon color="#fff" size="16">
          <Warning />
        </el-icon><span style="color: #fff;" class="ml-1">提示</span>
      </template>
      <div class="dialog-content">
        <p>确认忽略所选告警?</p>
        <p>忽略的告警将移入【历史告警】</p>
      </div>
      <template #footer>
        <div class="dialog-footer mt-12px justify-center">
          <el-button @click="isVisible = false">取消</el-button>
          <el-button type="primary" @click="ignorableWarning()">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 弹框 转工单-->
    <el-dialog v-model="isVisibleWorker" width="28%" align-center>
      <template #header>
        <span style="color: #fff;">告警转工单</span>
      </template>
      <div class="dialog-content">
        <el-form :model="workerForm" label-width="120px">
          <el-form-item label="工单编号">{{ workerForm.id }}</el-form-item>
          <el-form-item label="关联对象">{{ workerForm.siteName }}/{{ workerForm.deviceName }}</el-form-item>
          <el-form-item label="工单描述">
            <el-input v-model="workerForm.desc" type="textarea" placeholder="请输入工单描述" />

          </el-form-item>
          <el-form-item label="预计闭环日期">
            <el-date-picker v-model="workerForm.date1" type="date" placeholder="请选择日期"
              style="width: 100%;padding: 0 !important;" />
          </el-form-item>
          <el-form-item label="提交人">{{ workerForm.id }}</el-form-item>
          <el-form-item label="备注">
            <el-input v-model="workerForm.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer mt-12px justify-center">
          <el-button @click="isVisibleWorker = false">取消</el-button>
          <el-button type="primary" @click="submitWorker()">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 弹框 查看-->
    <warningHistory :visible="isVisibleWarning" :activeRow="activeRow" @close="isVisibleWarning = false">
    </warningHistory>
  </div>
</template>
<script setup>
import { ref, reactive, nextTick } from "vue";
import SearchBar from "@/components/searchBar/index.vue";
import SearchTable from "@/components/table/index.vue";
import { updateEventIgnoreStatus } from "@/api/monitoringCenter/monitoringCenter";
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";
import warningHistory from "../warningHistory.vue";
import { watch } from "vue";
const siteList = ref([])
const props = defineProps({
  siteList: {
    type: Array,
    default: () => [],
  },
});
watch(
  () => props.siteList,
  (newVal) => {
    siteList.value = newVal;
  },
  { deep: true }
);
if (props.siteList.length > 0) {
  siteList.value=props.siteList
}
const { searchConfig, rules, formData, searchInitData } = useSearchBar(siteList);
const { tableRef, columns, exportConfig, tiggerSearch, tiggerExport, onSearch, onReset } = useTable({ formData });
const isVisible = ref(false);
const isVisibleWorker = ref(false);
const isVisibleWarning = ref(false);

const workerForm = reactive({
  name: "",
  region: "",
  date1: "",
  desc: "",
  remark: "",
});

const activeRow = ref({})
// 操作项
const operationBtn = ({ row, type }) => {
  activeRow.value = row
  if (type == 'ignore') {
    isVisible.value = true;
  } else if (type == 'createWorkOrder') {
    Object.assign(workerForm, row);
    isVisibleWorker.value = true;
  } else isVisibleWarning.value = true
};

// 忽略告警
const ignorableWarning = () => {
  isVisible.value = false;
  updateEventIgnoreStatus({ id: activeRow.value.id, ignoreStatus: 1 }).then(() => {
    nextTick(() => {
      tableRef.value.resetTable();
    });
  });
};
// 转工单
const submitWorker = () => {
  isVisibleWorker.value = false;
};
</script>
<style lang="scss" scoped>
.dialog-content {
  padding: 20px;
  text-align: center;
  line-height: 30px;

  p:last-of-type {
    color: #7DCBFF;
  }
}
</style>