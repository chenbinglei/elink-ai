<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="关键词：">
              <el-input v-model="formInline.keyword" class="selectAndInput" clearable placeholder="请输入关键词">
                <template #prefix>
                  <el-select v-model="formInline.keywordType" filterable placeholder="请选择">
                    <el-option v-for="item in keywordTypeArray" :key="item.id" :label="item.name"
                      :value="item.id"></el-option>
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="商户平台：">
              <el-select v-model="formInline.tradeWay" clearable placeholder="全部" @change="queryAccountList">
                <el-option v-for="item in payPlatformArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="企业商户：">
              <el-select v-model="formInline.accountId" filterable clearable placeholder="全部"
                :disabled="!formInline.tradeWay">
                <el-option v-for="(item, index) in accountIdArray" :key="index" :label="item.mchName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="交易状态：">
              <el-select v-model="formInline.tradeStatus" clearable placeholder="全部">
                <el-option v-for="item in tradeStatusArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="12" :xl="12" :xs="24">
            <el-form-item label="支付时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate"
                :shortcuts="pickerOptions.shortcuts" clearable end-placeholder="结束时间" format="YYYY-MM-DD"
                range-separator="~" start-placeholder="开始时间" type="daterange" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="交易类型：">
              <el-select v-model="formInline.tradeType" clearable placeholder="全部">
                <el-option v-for="item in tradeTypeArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" class="blackFontButtons" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div ref="tableContentRef" class="tableContent content_border">
      <TableHeaderTitle title="充电订单交易明细">
        <template #content>
          <OrderTotalAndExportCom componentName="CsTransactionChargingOrder" :formInline="formInline"
            :returnDataInfo="returnDataInfo"></OrderTotalAndExportCom>
        </template>
      </TableHeaderTitle>
      <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" stripe ref="tableRef">
          <el-table-column fixed="left" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" fixed="left" label="订单号" show-overflow-tooltip min-width="180">
            <template #default="{ row }">{{ $filters.moreData(row.orderNum) }}</template>
          </el-table-column>
          <el-table-column align="center" label="交易流水号" show-overflow-tooltip min-width="180">
            <template #default="{ row }">{{ $filters.moreData(row.flowNum) }}</template>
          </el-table-column>
          <el-table-column align="center" label="交易类型">
            <template #default="{ row }">{{ $filters.tradeCoType(row.tradeType) }}</template>
          </el-table-column>
          <el-table-column align="center" label="金额（元）">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <div :class="{ burdenMoney: row.detailType === 2 }" class="tradeMoney">
                  <span class="symbol">{{ row.detailType === 2 ? '-' : '+' }}</span>
                  <span class="number">{{ $filters.moneyTwoNum($filters.numberNull(row.tradeMoney), 2) }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="站点名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="商户名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.mchName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="交易方式">
            <template #default="{ row }">{{ $filters.tradeWay(row.tradeWay) }}</template>
          </el-table-column>
          <el-table-column align="center" label="支付时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column align="center" label="完成时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
          </el-table-column>
          <el-table-column align="center" label="交易状态" width="120">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <div :class="'tradeStatus' + row.tradeStatus" class="tradeStatus">
                  {{ $filters.tradeStatus(row.tradeStatus) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" fixed="right" label="操作" width="65">
            <template #default="{ row }">
              <div class="table_operate_class flex-jc-ai-center">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div ref="tablePaginationRef" class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber"
          @pageChange="listArray" />
      </div>
    </div>

    <ChargingOrderDetailsDialog v-if="orderDetailsVisible" v-model:isVisible="orderDetailsVisible"
      :activeOrderId="activeOrderId"></ChargingOrderDetailsDialog>
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import { ElMessage } from "element-plus";
import { pay_plat_form_array } from "@/utils/setVariate";
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import OrderTotalAndExportCom from "./OrderTotalAndExportCom.vue";
import ChargingOrderDetailsDialog from "./ChargingOrderDetailsDialog.vue";
import { findAccountList } from "@/api/operationManagement/CsDataReportManage";
import { RefreshRight, Search, Plus, MoreFilled } from '@element-plus/icons-vue';
import { onMounted, reactive, ref, toRefs, defineComponent, computed, nextTick } from "vue";
import { queryRechargeTradeList } from "@/api/operationManagement/CsTransactionDetails";
import { findAccountListByUserId } from "@/api/operationManagement/CsSettlementManagement";

export default defineComponent({
  name: "CsTransactionChargingOrder",
  components: { OrderTotalAndExportCom, ChargingOrderDetailsDialog },
  setup () {

    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });
    const tableRef = ref(null);

    const that = reactive({
      Plus,
      Search,
      MoreFilled,
      RefreshRight,
      oldFormInline: {},
      formInline: { keywordType: 1 },

      accountIdArray: [],
      payPlatformArray: pay_plat_form_array,
      pickerOptions: pickerOptionsGthanAcTime(),
      tradeTypeArray: [{ id: 1, name: "充电预收" }, { id: 2, name: "充电退款" }],
      tradeStatusArray: [{ id: 1, name: "处理中" }, { id: 2, name: "处理成功" }, { id: 3, name: "处理失败" }],
      keywordTypeArray: [{ id: 1, name: "订单号" }, { id: 2, name: "交易流水号" }, { id: 3, name: "手机号" }, { id: 4, name: "站点名称" }],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      returnDataInfo: {},
      listLoading: false,
      tableMaxHeight: 300,

      activeOrderId: "",
      orderDetailsVisible: false,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.startAlsoDate) {
        formInline.startTime = formInline.startAlsoDate[0];
        formInline.endTime = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryRechargeTradeList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
        that.totalNumber = returnDataInfo.pageDto.totalSize;
        that.list = returnDataInfo.pageDto.items;
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        delete that.returnDataInfo.pageDto;
        that.listLoading = false;
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.returnDataInfo = {};
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) {
        that.activeOrderId = row.id;
        that.orderDetailsVisible = true;
      }
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
      queryAccountList();
    };

    // 根据租户id查询租户账户信息
    const queryAccountList = () => {
      // findAccountList({ queryMode: 1,timer: new Date()}).then(res=>{
      //   that.accountIdArray = res.data ? res.data : [];
      // });
      that.formInline.accountId = null;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      findAccountListByUserId({ platformType: that.formInline.tradeWay, tenantId: userInfo.value.tenantId }).then(res => {
        that.accountIdArray = res.data ? res.data : [];
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 82;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      // queryAccountList();
      listArray();
    });

    return { ...toRefs(that), tableRef, listArray, clickResetForm, tableContentRef, tablePaginationRef, setTableMaxHeight, queryAccountList, userInfo, clickOperateBut };
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .tradeStatus {
    width: fit-content;
    padding: 0 16px;
    height: 28px;
    border-radius: 6px;
    line-height: 28px;
    text-align: center;
    box-sizing: border-box;

    font-size: 14px;
    color: rgba(255, 255, 255, .6);
    background: rgba(7, 156, 235, 0.2);
  }

  .tradeStatus1,
  .burdenMoney {
    color: #FD944A !important;
  }

  .tradeStatus2,
  .tradeMoney {
    color: #56E540;
  }
}
</style>