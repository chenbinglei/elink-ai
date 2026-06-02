<template>
  <el-dialog v-model="dialog_visible" width='48%' :manual-enter-close="false" disabledLoading class="operationLogDialog"
    @close="saveDialog()">
    <template #header>
      <TableHeaderTitle title="巡检"></TableHeaderTitle>
    </template>
    <div class="dialog-main">
      <Tabs v-model:tabsIndex="activeTab" :tabsArray="handleMenuArray"></Tabs>
      <div class="tableContent">
        <detailTask v-if="activeTab == 1 && detailsList.length > 0" :type="type" @submitForm="submitForm"
          :taskStatus="inspectionType" :detailsList="detailsList" @cancel="saveDialog" :titleValue="titleValue" />
        <!-- 处理流水 -->
        <div v-if="activeTab == 2" style="height: 550px;">
          <div class="map-table" ref="tableContentRef" v-resize="setTableMaxHeight">
            <el-table :data="dataList" border stripe :max-height="tableMaxHeight">
              <el-table-column label="节点" prop="nodeName" align="center">
                <template #default="{ row }">
                  <div class="node-name">

                    <img :src="nodeName" alt="" style="width: 20px; height: 20px;margin-right: 5px;" />
                    {{ row.nodeName }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="处理人" prop="createName" align="center"></el-table-column>
              <el-table-column label="处理结果" prop="result" align="center">
                <template #default="{ row }">
                  <span class="handle-result" :class="{ 'success': row.result == '1', 'danger': row.result == '2','handover': row.result == '3' }">
                    {{ row.result == '1' ? '已提交' : row.result == '2' ? '已退回' : '已交接' }}
                  </span>
                </template>

              </el-table-column>
              <el-table-column label="流转意见" prop="flowOpinion" align="center">
                <template #default="{ row }">{{ $filters.moreData(row.flowOpinion) }}</template>
              </el-table-column>
              <el-table-column label="处理时间" prop="createTime" align="center">
                <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <div class="dialog-main" v-if="activeTab == 3">
          <nodeTask :selectedNode="selectedNode" :selectType="selectType"></nodeTask>
        </div>

      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import nodeName from '@/assets/image/nodeName.png'
import nodeTask from './nodeTask.vue'
import detailTask from './detailTask.vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { findInspectionTaskDetailById, updateInspectionTask } from '@/api/assetManagement/inspection'
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: '1'
  },
  inspectionType: {
    type: String,
    default: ''
  },
  inspectionArray: {
    type: Object,
    default: () => { }
  },
  titleValue: {
    type: String,
    default: ''
  }
})
const selectedNode = ref(false)
const selectType = ref('1')
const detailsList = ref([])
const titleValue = ref(props.titleValue)
const dataList = ref([
  { nodeName: "试剂库", result: "1", handleTime: "2023-05-05 16:21:27", errNum: '1' },
  { nodeName: "试剂库", result: "1", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "2", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "1", handleTime: "", errNum: '1' },
  { nodeName: "试剂库", result: "2", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "1", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "1", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "2", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "2", handleTime: "2023-05-05", errNum: '1' },
  { nodeName: "试剂库", result: "1", handleTime: "", errNum: '1' },
])
const tableMaxHeight = ref(500);
// 计算出表格最大高度
const tableContentRef = ref(null);
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight;
  tableMaxHeight.value = tableContentHeight;
};
const getfindInspectionTaskDetail = () => {
  findInspectionTaskDetailById({ id: props.inspectionArray.id }).then((res) => {
    console.log(res, 'res')
    dataList.value = res.data?.inspectionRecordList
    detailsList.value = res.data?.inspectionSiteList
    // inspectionTaskDetail.value = res.data
  })
}

const dialog_visible = ref(props.isVisible)
const activeTab = ref(1)
const emit = defineEmits(['saveDialog'])
const handleMenuArray = ref(
  [{ name: '任务详情', id: 1 }, { name: '处理流水', id: 2 }, { name: '流程图', id: 3 }])

const saveDialog = () => {
  emit('saveDialog')
}

// 任务详情的提交
const submitForm = (val) => {
  let obj = {
    ...val,
    id: props.inspectionArray.id
  }
  updateInspectionTask(obj).then((res) => {
    ElMessage({ type: "success", showClose: true, message: "修改成功" });
    saveDialog()
  });
}
onMounted(() => {
  if (props.inspectionArray) {
    getfindInspectionTaskDetail()

  }
})
</script>

<style lang="scss" scoped>
.dialog-main {
  padding-top: 10px;
}

.map-table {
  padding: 20px;
  height: 80%;

}

.node-name {
  display: flex;
  align-items: center;
  justify-content: center;

}

.success {
  color: rgba(35, 162, 31, 1);
  padding: 15px;
  background: rgba(188, 255, 207, 0.79);
  border-radius: 18px;
  overflow: hidden;

}
.handover{
  color: rgb(38, 101, 238);
  padding: 15px;
  background: rgba(183, 209, 248, 0.79);
  border-radius: 18px;
  overflow: hidden;
}

.danger {
  color: rgba(223, 115, 20, 1);
  padding: 15px;
  background: rgba(255, 224, 188, 0.79);
  border-radius: 18px;
  overflow: hidden;

}
</style>