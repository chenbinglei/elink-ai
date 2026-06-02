<template>
  <el-dialog v-model="dialog_visible" width='60%' :manual-enter-close="false" disabledLoading
    class="operationLogDialog">
    <template #header>
      <TableHeaderTitle title="操作记录"></TableHeaderTitle>
    </template>
    <div class="header-form content_border">
      <el-form ref="formRef" :model="formLog" label-width="150px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发票申请单号：">
              <el-input v-model="formLog.invoiceId" clearable placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商户名称：">
              <!-- <el-input v-model="formLog.mchId" clearable placeholder="请输入"></el-input> -->
               <el-select v-model="formLog.mchId" filterable clearable placeholder="全部">
                <el-option v-for="(item, index) in accountIdArray" :key="index" :label="item.mchName"
                  :value="item.mchId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码：">
              <el-input v-model="formLog.phoneNum" clearable placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArraylog('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-table v-loading="listlogLoading" stripe :data="logList"
        style="width: 100%;margin-top: 10px;height: 40vh; overflow-y: auto;">
        <el-table-column prop="invoiceId" label="发票申请单号" align="center" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <span style="white-space: nowrap">{{ row.invoiceId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="phoneNum" label="手机号" align="center"></el-table-column>
        <el-table-column prop="mchName" label="商户名称" align="center"></el-table-column>
        <el-table-column prop="operationType" label="操作内容" align="center" width="100">

          <template #default="{ row }">
            {{ row.operationType === 1 ? '受理' : '开票' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" align="center"></el-table-column>
        <el-table-column prop="userName" label="操作人" align="center" width="120"></el-table-column>
      </el-table>

    </div>
    <template #footer>
      <div style="display: flex; justify-content: space-between;">
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
            @pageChange="listArraylog" />
        </div>
        <el-button type="primary" @click="closeLog">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { queryInvoiceRecordList } from '@/api/operationManagement/CsInvoiceManage';
import { ref, watch, onMounted } from 'vue'
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  accountIdArray: {
    type: Array,
    default: () => []
  }
})
const formLog = ref({
  invoiceId: '',
  mchId: '',
  phoneNum: '',
})
const accountIdArray = ref([]);
const logList = ref([])
const totalNumber = ref(0)
const currentPage = ref(1)
const pageNum = ref(10)
// 发射事件，用于更新父组件的值
const emit = defineEmits(['update:isVisible'])
// 内部控制 dialog 显示/隐藏
const dialog_visible = ref(props.isVisible)

// 监听父组件传递的 isVisible 变化
watch(() => props.isVisible, (newVal) => {
  dialog_visible.value = newVal
  if (newVal) {
    accountIdArray.value = props.accountIdArray;

    listArraylog()
  }
})
onMounted(() => {

})
const listlogLoading = ref(false)


// 当内部 dialog_visible 改变时，通知父组件
watch(dialog_visible, (val) => {
  emit('update:isVisible', val)
})
const clickResetForm = () => {
  formLog.value = {
    invoiceId: '',
    mchId: '',
    phoneNum: '',
  }
  listArraylog()
  // formRef.value.resetFields(); // 调用 resetFields 方法重置表单 没有生效

}
const listArraylog = () => {
  let obj = {
    ...formLog.value,
    page: currentPage.value,
    size: pageNum.value,
  }
  queryInvoiceRecordList(obj).then(res => {
    listlogLoading.value = false
    logList.value = res.data.items;
    totalNumber.value = res.data.totalSize;
  })
}
// 关闭弹窗
const closeLog = () => {
  dialog_visible.value = false;
}

</script>
<style scoped lang="scss">
.operationLogDialog {
  .content {
    margin-bottom: 0 !important;
  }

  .text {
    font-weight: 400 !important;
  }
}
</style>