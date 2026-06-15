<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="发票申请单号：">
              <el-input v-model="formInline.id" clearable placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="商户名称：">
              <!-- <el-input v-model="formInline.mchId" clearable placeholder="请输入"></el-input> -->
              <el-select v-model="formInline.mchId" filterable clearable placeholder="全部">
                <el-option v-for="(item, index) in accountIdArray" :key="index" :label="item.mchName"
                  :value="item.mchId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="发票抬头：">
              <el-input v-model="formInline.invoiceTitle" clearable placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="手机号码：">
              <el-input v-model="formInline.phoneNum" clearable placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="申请时间：">
              <!-- :shortcuts="pickerOptions.shortcuts" -->
              <el-date-picker v-model="formInline.ApplicationTime" :disabled-date="pickerOptions.disabledDate" clearable
                value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~"
                start-placeholder="开始时间" type="daterange" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="状态：">
              <el-select v-model="formInline.invoiceStatus" clearable placeholder="全部">
                <el-option v-for="item in siteStatusArray" :key="item.value" :label="item.label"
                  :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="发票列表">
        <template #content>
          <el-button :icon="RefreshRight" @click="OperationLog">操作记录</el-button>
          <el-button :icon="RefreshRight" @click="ExportInvoice"><i
              class="iconfont icon-daochu w-16px h-16px text-16px mr-8px" />导出</el-button>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="InvoiceList" :max-height="tableMaxHeight"
          @selection-change="handleSelectionChange" ref="tableRef">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="发票申请单号" align="center" show-overflow-tooltip />
          <el-table-column prop="mchName" label="交易商户" align="center" show-overflow-tooltip />
          <el-table-column prop="mchId" label="商户ID" align="center" show-overflow-tooltip />
          <el-table-column prop="createTime" label="申请时间" align="center" show-overflow-tooltip />
          <el-table-column prop="phoneNum" label="申请人" align="center" show-overflow-tooltip />
          <el-table-column prop="invoiceTitle" label="发票抬头" align="center" show-overflow-tooltip />
          <el-table-column prop="invoiceAmount" label="开票金额" align="center" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.invoiceAmount ? $filters.moneyTwoNum($filters.numberNull(row.invoiceAmount), 2) + ' 元 ' : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="invoiceStatus" label="状态" align="center" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="button_class"
                :class="{ 'text-success': row.invoiceStatus === 1, 'text-warning': row.invoiceStatus === 2, 'text-danger': row.invoiceStatus === 3, 'text-primary': row.invoiceStatus === 4 }">
                {{ row.invoiceStatus == 1 ? '待开票' : row.invoiceStatus == 2 ? '开票中' : row.invoiceStatus == 3 ? '已开票' :
                  row.invoiceStatus ==
                    4 ? '已撤销' : '未知状态' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="210" fixed="right" align="center">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" v-if="row.invoiceStatus === 1" @click="clickAccept(row)">受理</el-link>
                <el-link :underline="false" v-if="row.invoiceStatus === 2" @click="clickInvoice(row, '开票')">开票</el-link>
                <el-link :underline="false" v-if="row.invoiceStatus === 3"
                  @click="clickInvoice(row, '查看发票')">查看开票</el-link>

                <el-link :underline="false" @click="clickOrderDetails(row)">查看订单</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
          @pageChange="listArray" />
      </div>
    </div>
    <operationLog v-model:isVisible="operationLogVisible" :accountIdArray="accountIdArray" />

    <InvoiceInformationDialog v-model:isVisible="InvoiceInformationVisible" :title="title"
      @update:isVisible="closeInvoiceInformation" :invoiceOrderId="invoiceOrderId" @updataVisible="updataVisible" />

    <OrderDetailsDialog v-model:isVisible="OrderDetailsVisible" :title="title" :Merchant='Merchant' :invoiceOrderId="invoiceOrderId" />

  </div>
</template>

<script setup>
import operationLog from './CsInvoiceManage/operationLog.vue'
import InvoiceInformationDialog from './CsInvoiceManage/InvoiceInformation.vue'
import OrderDetailsDialog from './CsInvoiceManage/OrderDetails.vue'
import { exportCustomExcel } from "@/common/exportExcel";
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus';
import { queryInvoiceList, updateInvoiceStatus } from '@/api/operationManagement/CsInvoiceManage';
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { useAppStore } from '@/stores/index';

import { findAccountListByUserId } from "@/api/operationManagement/CsSettlementManagement";
const formInline = ref({
  id: '',
  mchId: "",
  invoiceTitle: "",
  phoneNum: "",
  ApplicationTime: "",
  invoiceStatus: ""
})
// 分页
const tableRef = ref(null);
const currentPage = ref(1)
const pageNum = ref(10)
const totalNumber = ref(0)
const operationLogVisible = ref(false) // 操作记录弹窗是否显示
const InvoiceInformationVisible = ref(false) // 发票详情弹窗是否显示
const OrderDetailsVisible = ref(false) // 订单详情弹窗是否显示
const title = ref(null) // 发票详情弹窗标题
const invoiceOrderId = ref(null) // 订单详情弹窗订单id
const Merchant = ref({})

const updataVisible = (val) => {
  listArray();
}


// 状态
const siteStatusArray = ref([
  { value: 1, label: "待开票" },
  { value: 2, label: "开票中" },
  { value: 3, label: "已开票" },
  { value: 4, label: "已撤销" },
]);
const pickerOptions = ref(pickerOptionsGthanAcTime());
const InvoiceList = ref([])
// 点击订单详情
const clickOrderDetails = (row) => {
  Merchant.value=row
  invoiceOrderId.value = row.id;
  OrderDetailsVisible.value = true;
}
// 点击受理
const clickAccept = (row) => {
  ElMessageBox.confirm(`请确认是否受理开票？受理后请线下开票后点击开票上传发票文件？`, '', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let obj = {
      ...row,
      invoiceStatus: 2
    }
    updateInvoiceStatus(obj).then(() => {
      ElMessage.success('受理成功');
      listArray('refresh');
    });
  }).catch(() => {
    ElMessage.info('取消操作');
  });
}

// 搜索
const listArray = () => {
  let obj = {
    ...formInline.value,
    page: currentPage.value,
    size: pageNum.value,
    startTime: formInline.value.ApplicationTime ? formInline.value.ApplicationTime[0] : '',
    endTime: formInline.value.ApplicationTime ? formInline.value.ApplicationTime[1] : '',

  };

  queryInvoiceList(obj).then(async res => {
    InvoiceList.value = res.data.items;
    totalNumber.value = res.data.totalSize;
    console.log("listArray", res.data);
    await nextTick();
    if(tableRef.value){
      tableRef.value.setScrollTop(0);
    }
  })
}
// 计算出表格最大高度
const tableContentRef = ref(null);
const tablePaginationRef = ref(null);
const tableMaxHeight = ref(300)
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight - 78;
  tableMaxHeight.value = tableContentHeight - tablePaginationRef.value.offsetHeight;
};
// 重置
const clickResetForm = () => {
  formInline.value = {
    id: '',
    mchId: "",
    invoiceTitle: "",
    phoneNum: "",
    ApplicationTime: "",
    invoiceStatus: ""
  }
  listArray()
}
const selectList = ref([])
// 多选选中的数据
const handleSelectionChange = (val) => {
  selectList.value = val
  // console.log(val);
}
// 导出
const ExportInvoice = () => {
  if (selectList.value.length == 0) {
    selectList.value = InvoiceList.value

  }
  const tableHeader = [
    { key: 'id', name: '发票申请单号' },
    { key: 'mchName', name: '交易商户' },
    { key: 'mchId', name: '商户ID' },
    { key: 'createTime', name: '申请时间' },
    { key: 'phoneNum', name: '申请人' },
    { key: 'invoiceTitle', name: '发票抬头' },
    { key: 'invoiceAmount', name: '开票金额' },
    { key: 'invoiceStatus', name: '状态' }
  ];
  const processedData = selectList.value.map(item => {
    const status = siteStatusArray.value.find(s => s.value === item.invoiceStatus);
    return {
      ...item,
      invoiceStatus: status ? status.label : '未知状态'
    };
  });
  exportCustomExcel(tableHeader, processedData, '发票列表', '发票列表');
}
// 操作记录
const OperationLog = () => {
  console.log("OperationLog", operationLogVisible.value);
  operationLogVisible.value = true;
  // console.log("OperationLog");
}

// 开票
const clickInvoice = (row, type) => {
  InvoiceInformationVisible.value = true
  invoiceOrderId.value = row.id;
  title.value = type // 设置标题为 '发票' 或其他内容
}
const appStore = useAppStore();
const userInfo = computed(() => {
  return appStore.userInfo;
})
const accountIdArray = ref([])
const getfindAccountList = () => {
  findAccountListByUserId({ platformType: 1, tenantId: userInfo.value.tenantId }).then(res => {
    accountIdArray.value = res.data ? res.data : [];
  });
}



onMounted(() => {
  listArray();
  getfindAccountList()
})

</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .button_class {
    padding: 1px 10px;
    display: inline-block;
    border-radius: 5px;

  }

  .text-success {
    color: #F3BB0E;
    background-color: #9D826E;

  }

  .text-warning {
    color: #3CB0F3;
    background-color: #517A9D;
  }

  .text-danger {
    color: #14BD96;
    background-color: #518A88;
  }

  .text-primary {
    color: #96989B;
    background-color: #5B626E;
  }

  .table_operate_class {
    display: flex;
    justify-content: flex-end;

    a {
      margin-right: 20px;
    }



  }
}
</style>