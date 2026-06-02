<template>
  <el-dialog v-model="dialog_visible" width='50%' :manual-enter-close="false" disabledLoading
    class="operationLogDialog" @close="closeLog">
    <template #header>
      <TableHeaderTitle :title="title"></TableHeaderTitle>
    </template>
    <div class="header-form content_border" style="">
      <el-form :model="formDialog" label-width="110px" :rules="rules" ref="formRef">
        <el-form-item label="发票" v-if="title !== '开票'">
          <div class="table_operate_class">


            <el-link :underline="false" v-if="!resubmitUpload" @click="Resubmit">重新上传发票</el-link>
            <el-link :underline="false" @click="downloadInvoice" style="margin-left: 30px;">下载发票</el-link>
            <el-link :underline="false" v-if="resubmitUpload" style="margin-left: 30px;"
              @click="CancelResubmit">取消重新上传发票</el-link>
          </div>
        </el-form-item>
        <el-form-item label="发票文件" prop="explainFile_List" v-if="resubmitUpload || title === '开票'">
          <UploadFileCustom v-model:fileArray="formDialog.explainFile_List" :fileSize="20" acceptType=".pdf"
            :classFileType="3">
            <template #tip_content>
              <span style="color: #788392;font-size: 12px;">上传PDF格式，请确保发票真实且正确，后续存在发票问题投诉，商户自行承担。</span>
            </template>
          </UploadFileCustom>
        </el-form-item>

        <el-form-item label="接收邮箱">
          <span>{{ InvoiceMessage.receiptEmail }}</span>
        </el-form-item>
        <div v-if="InvoiceMessage && Object.keys(InvoiceMessage).length > 0" style="margin-bottom: 20px;">

          <el-form-item label="发票信息">
          </el-form-item>
          <div class="invoice-message-container">

            <div class="invoice-message">
              <div class="invoice-message-item">
                <p>开票金额</p>{{ InvoiceMessage.invoiceAmount }}
              </div>
              <div class="invoice-message-item">
                <p>抬头类型</p>{{ InvoiceMessage.titleType == 1 ? '个人' : '单位' }}
              </div>
            </div>
            <div class="invoice-message">
              <div class="invoice-message-item">
                <p>发票抬头</p>{{ InvoiceMessage.invoiceTitle }}
              </div>
              <div class="invoice-message-item">
                <p>纳税人识别号</p>{{ InvoiceMessage.taxNumber }}
              </div>
            </div>
            <div class="invoice-message">
              <div class="invoice-message-item">
                <p>开户银行</p>{{ InvoiceMessage.bankName }}
              </div>
              <div class="invoice-message-item">
                <p>银行账号</p>{{ InvoiceMessage.bankAccount }}
              </div>
            </div>
            <div class="invoice-message">
              <div class="invoice-message-items">
                <p>企业地址</p>{{ InvoiceMessage.registeredAddress }}
              </div>

            </div>
          </div>

        </div>
      </el-form>
    </div>
    <template #footer>
      <el-button type="primary" @click="closeLog">取消</el-button>
      <el-button type="primary" @click="InvoiceSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import axios from "axios";
import { ref, watch, onMounted, reactive } from 'vue'
import { findInvoiceDetailById, updateInvoiceStatus } from "@/api/operationManagement/CsInvoiceManage";
import UploadFileCustom from "@/components/uploadFileCom/UploadFileCustom.vue";
import { ElMessage } from 'element-plus';
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: "发票"
  },
  invoiceOrderId: {
    type: String,
    default: ''
  }
})
const rules = {
  explainFile_List: [
    { required: true, message: '请上传发票文件', trigger: 'change' }
  ]
}
const formDialog = reactive({
  explainFile_List: []
})
const InvoiceMessage = ref({})
watch(() => formDialog.explainFile_List, (newValue, oldValue) => {
  if (newValue) {
  }
})
const resubmitUpload = ref(false)
// 重新上传发票
const Resubmit = () => {
  resubmitUpload.value = true
}
// 取消重新上传发票
const CancelResubmit = () => {
  resubmitUpload.value = false
}
const fileUrl = ref('')
// 下载发票
// const downloadInvoice = async () => {
//   window.open(fileUrl.value, '_blank');
// };
const downloadInvoice = async () => {
  try {
    const response = await axios.get(fileUrl.value, { responseType: 'blob' });
    // 忽略 content-type 判断，直接下载
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', '发票.pdf');
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    ElMessage({ type: "error", showClose: true, message: "下载失败，请重试" });
  }
};
const getfindInvoiceDetailById = async () => {
  const res = await findInvoiceDetailById({ id: props.invoiceOrderId })
  if (res.success) {

    InvoiceMessage.value = res.data
    console.log(res.data, 'resres.data')
    fileUrl.value = res.data.invoiceFilePath
  }
}



// 发射事件，用于更新父组件的值
const emit = defineEmits(['update:isVisible'])
// 内部控制 dialog 显示/隐藏
const dialog_visible = ref(props.isVisible)

// 监听父组件传递的 isVisible 变化
watch(() => props.isVisible, (newVal) => {
  dialog_visible.value = newVal
  if (props.invoiceOrderId) {
    getfindInvoiceDetailById()
  }
})
onMounted(() => {
})
const listlogLoading = ref(true)
const formRef = ref(null)
const InvoiceSubmit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const formData = new FormData();
      formData.append('invoiceFile', formDialog.explainFile_List[0].raw);
      formData.append('id', props.invoiceOrderId);
      formData.append('invoiceStatus', 3);
      console.log(formDialog.explainFile_List[0].raw, '99999', formDialog)
      // 发送请求
      updateInvoiceStatus(formData).then(res => {
        ElMessage({ type: "success", showClose: true, message: "添加成功" });
        dialog_visible.value = false
        emit('updataVisible')


      });
    } else {
      return false;
    }
  });
};


// 当内部 dialog_visible 改变时，通知父组件
watch(dialog_visible, (val) => {
  emit('update:isVisible', val)
})
// 关闭弹窗
const closeLog = () => {
  dialog_visible.value = false;
 
  formDialog.explainFile_List = []

 console.log("关闭");
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

.invoice-message-container {
  border-top: 1px solid #064275;
  border-bottom: 1px solid #064275;
  border-left: 1px solid #064275;
  margin-bottom: 10px;
  width: 90%;
  margin: 0 auto;
  color: #D0D3D9;

  .invoice-message {
    display: flex;
    font-size: 14px;

    .invoice-message-item {
      flex: 0 0 50%;
      display: flex;
      height: 40px;
      line-height: 40px;
      border-right: 1px solid #064275;
      border-bottom: 1px solid #064275;

      p {
        width: 30%;
        text-align: center;
        border-right: 1px solid #064275;
        margin-right: 10px;
        background-color: #082249;
        color: #8D9AAB;
      }
    }

    .invoice-message-items {
      flex: 0 0 100%;
      display: flex;
      height: 30px;
      line-height: 30px;
      border-right: 1px solid #064275;

      p {
        width: 15%;
        text-align: center;
        border-right: 1px solid #064275;
        margin-right: 10px;
        background-color: #082249;
        color: #8D9AAB;
      }
    }

  }
}
</style>