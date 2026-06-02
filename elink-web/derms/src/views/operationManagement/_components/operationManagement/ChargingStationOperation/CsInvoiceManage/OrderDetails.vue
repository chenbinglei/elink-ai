<template>
  <el-dialog v-model="dialog_visible" width='60%' :manual-enter-close="false" disabledLoading class="operationLogDialog">
    <template #header>
      <TableHeaderTitle title="订单详情"></TableHeaderTitle>
    </template>
    <div class="header-form content_border">
      <div class="orderSum">
        <div class="invoiceOrder">
          <div class="invoiceOrderTitle">
            <div>订单总金额</div>
            <div><span style="color: #FFC439;">{{ InvoiceList.totalMoney }}</span> 元</div>
          </div>
          <div class="line"></div>
          <div class="powerService">
            <div class="powerServiceItem">
              <div>电费</div>
              <div class="money"><span>{{ InvoiceList.totalElect }}</span> 元</div>
            </div>
            <div class="powerServiceItem">
              <div>服务费</div>
              <div class="money"><span>{{ InvoiceList.totalFee }}</span> 元</div>

            </div>
          </div>
        </div>
        <div class="invoiceOrder">
          <div class="invoiceOrderTitle">
            <div>订单总电量</div>
            <div><span style="color: #4ABEFF;">{{ InvoiceList.totalQt }}</span> 度</div>

          </div>
        </div>
      </div>
      <div style="float: right;margin:10px 0">
        <el-button :icon="RefreshRight" @click="ExportInvoice"><i class="iconfont icon-daochu w-16px h-16px text-16px mr-8px" />导出</el-button>
      </div>
      <el-table v-loading="listlogLoading" stripe :data="InvoiceList.orderList" style="width: 100%;margin-top: 10px;height: 40vh; overflow-y: auto;">
        <el-table-column prop="orderNum" label="订单编号" align="center" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <span style="white-space: nowrap">{{ row.orderNum }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="siteName" label="充电站" align="center"></el-table-column>
        <el-table-column prop="endTime" label="订单推送时间" align="center" width="200"></el-table-column>
        <el-table-column prop="totalQt" label="电量（度）" align="center" width="120"></el-table-column>
        <el-table-column prop="actualTotalElect" label="实收电费（元）" align="center" width="140"></el-table-column>
        <el-table-column prop="actualTotalFee" label="实收服务费（元）" align="center" width="180"></el-table-column>
      </el-table>

    </div>

  </el-dialog>
</template>

<script setup>
import { exportMultiSheetExcel } from "@/common/exportExcel";
import { findInvoiceOrderById, findInvoiceDetailById } from '@/api/operationManagement/CsInvoiceManage';
import { ref, watch, onMounted } from 'vue'
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  invoiceOrderId: {
    type: String,
    default: ''
  },
  Merchant: {
    type: Object,
    default: () => ({})
  }
})
const InvoiceList = ref({})

// 发射事件，用于更新父组件的值
const emit = defineEmits(['update:isVisible'])
// 内部控制 dialog 显示/隐藏
const dialog_visible = ref(props.isVisible)

// 监听父组件传递的 isVisible 变化
watch(() => props.isVisible, (newVal) => {
  dialog_visible.value = newVal
  if (props.invoiceOrderId) {
    getInvoiceOrderDetail()
  }
})

const ExportInvoice = async () => {
  const res = await findInvoiceDetailById({
    id: props.invoiceOrderId
  })
  // 定义表头（注意属性名不要有空格）
  const orderHeader = [
    { prop: 'orderNum', label: '订单编号', width: 50 },
    { prop: 'siteName', label: '充电站', width: 30 },
    { prop: "mchName", label: "交易商户", width: 30 },
    { prop: 'mchId', label: '商户ID', width: 30 },
    { prop: 'endTime', label: '订单推送时间', width: 24 },
    { prop: 'totalQt', label: '电量（度）', width: 24, width: 24 },
    { prop: 'actualTotalElect', label: '实收电费（元）', width: 24 },
    { prop: 'actualTotalFee', label: '实收服务费（元）', width: 24 },
  ];

  const invoiceHeader = [
    { prop: 'invoiceAmount', label: '开票金额' },
    { prop: 'titleType', label: '抬头类型' },
    { prop: 'invoiceTitle', label: '发票抬头' },
    { prop: 'taxNumber', label: '纳税人识别号', width: 30 },  // 已修正空格
    { prop: 'bankName', label: '开户银行' },
    { prop: 'bankAccount', label: '银行账号' },
    { prop: 'registeredAddress', label: '企业地址' },
  ];

  const orderList = InvoiceList.value.orderList.map(item => ({
    ...item,
    mchName: props.Merchant.mchName || '',
    mchId: props.Merchant.mchId || '',
  }));

  const invoiceData = res.data ? [res.data] : [];
  const processedData = invoiceData.map(item => {
    return {
      ...item,

      titleType: item.titleType === 1 ? '个人' : '单位',
    }
  });
  exportMultiSheetExcel(
    [
      { tableHeader: orderHeader, json: orderList, sheetName: '订单明细' },
      { tableHeader: invoiceHeader, json: processedData, sheetName: '发票信息' },
    ],
    "【" + processedData[0].invoiceTitle + "】_开票金额 " + processedData[0].invoiceAmount + "元"
  );
}
const getInvoiceOrderDetail = () => {
  findInvoiceOrderById({
    id: props.invoiceOrderId
  }).then(res => {
    InvoiceList.value = res.data

  })

}

onMounted(() => {
})
const listlogLoading = ref(false)


// 当内部 dialog_visible 改变时，通知父组件
watch(dialog_visible, (val) => {
  emit('update:isVisible', val)
})

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

  .orderSum {
    font-size: 14px;
    font-weight: 400;
    display: flex;
    justify-content: space-between;

    .invoiceOrder {
      width: 48%;
      border: 1px solid #036da2;
      background-color: #0b2b52;
      border-radius: 8px;
      padding: 10px 15px;
      box-sizing: border-box;

      .invoiceOrderTitle {
        display: flex;
        justify-content: space-between;
      }

      .line {
        width: 100%;
        height: 1px;
        background-color: #036da2;
        margin: 10px 0;
      }

      .powerService {
        display: flex;
        color: #cfd5dd;

        .powerServiceItem {
          width: 50%;
          text-align: center;
        }

        .money {
          margin-top: 10px;
        }
      }
    }
  }
}
</style>