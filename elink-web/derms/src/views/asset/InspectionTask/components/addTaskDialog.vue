<template>
  <el-dialog v-model="dialog_visible" width='996' :manual-enter-close="false" disabledLoading class="operationLogDialog"
    @close="saveDialog()">
    <template #header>
      <TableHeaderTitle title="新增"></TableHeaderTitle>
    </template>
    <div class="dialog_main">
      <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px"
        style="width: 90%; margin:0 auto;">
        <el-form-item label="任务名称:" prop="taskName">
          <el-input v-model="formDialog.taskName" placeholder="请输入任务名称" type="text" max="32" />
        </el-form-item>
        <el-form-item label="任务描述:">
          <el-input v-model="formDialog.taskDesc" type="textarea" :rows="3" placeholder="请输入描述" maxlength="100"
            show-word-limit />
        </el-form-item>
      </el-form>
      <div class="line"></div>
      <div class="dialog_main">
        <el-form :model="searchDialog" inline style="width: 90%; margin:0 auto;">
          <el-row :gutter="16">
            <el-col :xs="14" :sm="14" :md="14" :xl="14">
              <el-form-item label="场站名称：" prop="taskTime">
                <el-input v-model="searchDialog.siteName" placeholder="请输入场站名称" type="text" max="32" />
              </el-form-item>
            </el-col>
            <el-col :xs="10" :sm="10" :md="10" :xl="10">
              <el-form-item>
                <el-button :icon="Search" type="primary" @click="queryList">查询</el-button>
                <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-table v-loading="listLoading" :data="siteList" :max-height="tableMaxHeight"
          @selection-change="handleSelectionChange" class="custom-expand-table"
          style="height: 32vh;width: 95%; margin:0 auto; " row-key="id">
          <el-table-column type="selection" width="55" :selectable="(row) => row.isInspection !== 1"></el-table-column>
          <el-table-column prop="siteName" label="场站名称" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="location" label="位置" show-overflow-tooltip align="center">
            <template #default="scope">
              {{ scope.row.location ? scope.row.location : "--" }}
            </template>
          </el-table-column>
          <el-table-column prop="tenantName" label="所属租户" show-overflow-tooltip align="center">
            <template #default="scope">
              {{ scope.row.tenantName ? scope.row.tenantName : "--" }}
            </template>
          </el-table-column>
          <el-table-column prop="lastInspectionTime" label="上次巡检时间" show-overflow-tooltip align="center">
            <template #default="scope">{{ scope.row.lastInspectionTime ? scope.row.lastInspectionTime : '--'
            }}</template>
          </el-table-column>
        </el-table>
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
            :layout="'total, prev, pager, next, sizes'" @pageChange="queryList" />
        </div>
      </div>


    </div>
    <div class="btn-group">
      <el-button type="primary" @click="submitForm">确认</el-button>
      <el-button @click="saveDialog()">取消</el-button>
    </div>
  </el-dialog>
</template>

<script setup>
import { RefreshRight, Search, Plus } from '@element-plus/icons-vue';
import { ElMessage } from "element-plus";
import { saveInspectionTask, getSiteSaveTaskList } from "@/api/assetManagement/inspection";
import { ref, onMounted , computed} from 'vue'
import { useAppStore } from '@/stores/index';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },

})
const rules = {
  taskName: [{ required: true, message: "请输入账号", trigger: "blur" }],

};
const listLoading = ref(false)
const dialog_visible = ref(props.isVisible)
const selectValue = ref([])
const emit = defineEmits(['saveDialog'])
const appStore = useAppStore();
const userInfo = computed(() => {
  return appStore.userInfo;
});
const saveDialog = () => {
  emit('saveDialog')
}
const formDialog = ref({
  taskName: ""
})
const searchDialog = ref({
  siteName: ""
})
// 场站数据
const siteList = ref([
])
const formDialogRef = ref(null)
// 重置
const clickResetForm = () => {
  currentPage.value = 1
  pageNum.value = 10
  searchDialog.value = {
    siteName: ""
  }
  queryList()
}
// 查询
const queryList = () => {
  let obj = {
    size: pageNum.value,
    page: currentPage.value,
    siteName: searchDialog.value.siteName
  }
  getSiteSaveTaskList(obj).then(res => {
    siteList.value = res.data.items
    totalNumber.value = res.data.totalSize
  })
}
// 多选
const handleSelectionChange = (val) => {

  selectValue.value = val.map(item => item.id)
}
// 保存
const submitForm = () => {
  if (selectValue.value.length === 0) {
    ElMessage.error('请选择要保存的站点！')
    return
  }
  formDialogRef.value.validate((valid) => {
    if (valid) {
      let obj = {
        siteIds: selectValue.value,
        tenantId: userInfo.value.tenantId,
        ...formDialog.value
      }
      saveInspectionTask(obj).then(res => {
        emit("queryList");
        emit('saveDialog')
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      })

    }
  });

}


// 分页
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
// 初始化
onMounted(async () => {
  queryList()
})
</script>

<style lang="scss" scoped>
.operationLogDialog {
  .content {
    margin-bottom: 0 !important;
  }



  .line {
    width: 90%;
    height: 1px;
    margin: 0 auto;
    background-color: rgba(255, 255, 255, 0.15);
  }



}

.dialog_main {
  padding: 20px;
  box-sizing: border-box;
}

.btn-group {
  text-align: center;
  padding: 0 0 20px;
}
</style>