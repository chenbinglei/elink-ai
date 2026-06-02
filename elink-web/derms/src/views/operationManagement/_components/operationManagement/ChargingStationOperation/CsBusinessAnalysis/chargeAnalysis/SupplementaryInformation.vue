<template>
  <el-dialog v-model="dialog_visible" :manualEnterClose="false" width="30%" @close="closeLog"
    class="operationLogDialog">
    <template #header>
      <TableHeaderTitle :title="'补充信息'"></TableHeaderTitle>
    </template>
    <div class="Information">

      <el-form ref="formDialogRef" :rules="rules" :model="formDialog" label-width="40%">

        <el-form-item label="场站：" prop="priceType">
          <span>{{ stationSelect.siteName }}</span>
        </el-form-item>

        <el-form-item label="收益模型：" prop="incomeModelId">
          <el-select v-model="formDialog.incomeModelId">
            <el-option v-for="item in InformationArray" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="设备总成本（元）：" prop="deviceCost">
          <el-input v-model="formDialog.deviceCost" type="number" placeholder="请输入设备总成本" />
        </el-form-item>

        <el-form-item label="施工总成本（元）：" prop="constructionCost">
          <el-input v-model="formDialog.constructionCost" type="number" placeholder="请输入施工总成本" />
        </el-form-item>

        <el-form-item label="场地租金（元/月）：" prop="siteRent">
          <el-input v-model="formDialog.siteRent" type="number" placeholder="请输入场地租金" />
        </el-form-item>
        <el-form-item label="场地租金补贴时间：" prop="rentDateTime">
          <el-date-picker v-model="formDialog.rentDateTime" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
            :clearable="false" end-placeholder="结束时间" range-separator="至" start-placeholder="开始时间" type="daterange" />
        </el-form-item>
        <el-form-item label="充电运营补贴（元/度）：" prop="operationSubsidy">
          <el-input v-model="formDialog.operationSubsidy" type="number" placeholder="请输入充电运营补贴" />
        </el-form-item>
        <el-form-item label="充电运营补贴时间：" prop="customDateTime">
          <el-date-picker v-model="formDialog.customDateTime" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
            :clearable="false" end-placeholder="结束时间" range-separator="至" start-placeholder="开始时间" type="daterange" />
        </el-form-item>
        <el-form-item label="建站补贴（元）：" prop="constructionSubsidy">
          <el-input v-model="formDialog.constructionSubsidy" type="number" placeholder="请输入建站补贴" />
        </el-form-item>

        <el-form-item label="运营成本（元/月）：" prop="operationCost">
          <el-input v-model="formDialog.operationCost" type="number" placeholder="请输入运营成本" />
        </el-form-item>
        <el-form-item label="运维成本（元/月）：" prop="maintainCost">
          <el-input v-model="formDialog.maintainCost" type="number" placeholder="请输入运维成本" />
        </el-form-item>


      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer mt-12px justify-end">
        <el-button type="primary" @click="closeLog">取消</el-button>
        <el-button type="primary" @click="clickConfirmBut()">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { ElMessage } from "element-plus";
import { ref, watch, onMounted } from "vue";
import { saveSiteIncome } from "@/api/operationManagement/CsBusinessAnalysis";


const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  stationSelect: {
    type: Object,
    default: () => ({})
  },
  chargeAnalysis: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:isVisible']);

const dialog_visible = ref(props.isVisible);
const InformationArray = [
  { value: "工商业V2G", label: "工商业V2G" },
]
const formDialog = ref({
  incomeModelId: "工商业V2G",
  deviceCost: '',
  constructionCost: '',
  siteRent: '',
  operationSubsidy: '',
  constructionSubsidy: '',
  operationCost: '',
  maintainCost: ''
});
// 监听 isVisible 变化
watch(() => props.isVisible, (newVal) => {
  dialog_visible.value = newVal;
});
const rules = {
  incomeModelId: [{ required: true, message: '请选择收益模型', trigger: 'change' }],
  deviceCost: [{ required: true, message: '请输入设备总成本', trigger: 'blur' }],
  constructionCost: [{ required: true, message: '请输入施工总成本', trigger: 'blur' }],
}
const formDialogRef = ref(null);
const clickConfirmBut = () => {
  formDialogRef.value.validate((valid) => {
    if (valid) {
      let obj = {
        ...formDialog.value,
        subsidyStartDate: formDialog.value.customDateTime?.[0] || '',
        subsidyEndDate: formDialog.value.customDateTime?.[1] || '',
        rentStartDate: formDialog.value.rentDateTime?.[0] || '',
        rentEndDate: formDialog.value.rentDateTime?.[1] || '',
        siteId: props.stationSelect.id,
      }
      saveSiteIncome(obj).then(res => {
        if (res.success) {
          ElMessage.success('保存成功');
          emit('updata');

          closeLog();
        } else {
          ElMessage.error(res.msg || '保存失败');
        }
      });
    }
  })

}

// 初始化时加载 chargeAnalysis
onMounted(() => {
  if (props.chargeAnalysis) {
    formDialog.value = {
      ...props.chargeAnalysis,
      customDateTime: [props.chargeAnalysis.subsidyStartDate, props.chargeAnalysis.subsidyEndDate],
      rentDateTime: [props.chargeAnalysis.rentStartDate, props.chargeAnalysis.rentEndDate]
    }

    console.log(formDialog.value, 'props.IncomeDate')
  }
});

// 关闭弹窗
const closeLog = () => {
  dialog_visible.value = false;
  emit('update:isVisible', false);
};
</script>

<style lang="scss" scoped>
.operationLogDialog {

  .content {
    margin-bottom: 0 !important;
  }

  .text {
    font-weight: 400 !important;
  }
}

.Information {
  width: 100%;
  border-radius: 4px;
  box-sizing: border-box;
  padding: 20px 10%;
  max-height: 60vh;
  overflow-y: auto;

}
::v-deep .el-form-item{
  margin-bottom: 12px !important;
}
</style>