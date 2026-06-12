<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="关键词：">
              <el-input class="selectAndInput" v-model="formInline.keyword" clearable placeholder="请输入关键词">
                <template #prefix>
                  <el-select v-model="formInline.keywordType" filterable placeholder="请选择">
                    <el-option v-for="item in keywordTypeArray" :key="item.id" :label="item.name"
                      :value="item.id"></el-option>
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
              <el-select v-model="formInline.siteId" filterable clearable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="订单状态：">
              <el-select v-model="formInline.orderStatus" clearable placeholder="全部">
                <el-option v-for="item in orderStatusArray" :key="item.id" :label="item.name"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="电桩编码：">
              <el-input v-model="formInline.pileCode" clearable placeholder="请输入电桩编码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :xl="12">
            <el-form-item label="结束时间：">
              <el-date-picker v-model="formInline.endAlsoDate" type="daterange" :shortcuts="pickerOptions.shortcuts"
                clearable :disabled-date="pickerOptions.disabledDate" range-separator="~" start-placeholder="开始时间"
                end-placeholder="结束时间" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button class="blackFontButtons" @click="clickResetForm('resetPage')">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef" v-resize="setTableMaxHeight">
      <TableHeaderTitle title="订单列表"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" ref="tableRef" :span-method="arraySpanMethod">
          <el-table-column label="站点名称">
            <template #default="{ row }">
              <OcOrderListCard :orderInfo="row"></OcOrderListCard>
            </template>
          </el-table-column>
          <el-table-column label="占用时长"></el-table-column>
          <el-table-column label="订单状态"></el-table-column>
          <el-table-column label="订单金额"></el-table-column>
          <el-table-column label="实付金额"></el-table-column>
          <el-table-column label="车牌号"></el-table-column>
          <el-table-column label="操作"></el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
          @pageChange="listArray" />
      </div>
    </div>

  </div>
</template>

<script>
import moment from 'moment';
import { useAppStore } from '@/stores/index';

import { ElMessage } from "element-plus";
import { oc_order_status_array } from "@/utils/setVariate";
import { pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { OcOrderListCard } from "./CsDisAndChargingRecord/index";
import { onMounted, reactive, ref, toRefs, computed, defineComponent, nextTick } from "vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { findOccupyPileRecordListByPage } from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "CsPileOccupationRecord",
  components: { OcOrderListCard },
  setup() {

    const appStore = useAppStore();
    const tableRef = ref(null);

    const userInfo = computed(() => {
      return appStore.userInfo;
    });
    const format = 'YYYY-MM-DD';
    const defAbnormalType = 0;

    const that = reactive({
      siteIdArray: [],
      oldFormInline: {},
      formInline: { keywordType: 1 },
      orderStatusArray: oc_order_status_array,
      pickerOptions: pickerOptionsGthanAcTime(),
      keywordTypeArray: [{ id: 1, name: "占桩单号" }, { id: 2, name: "充放电单号" }, { id: 3, name: "手机号" }],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });

    const initFormInline = () => {
      const recentOneMonth = [moment().subtract(1, "month").format(format), moment().format(format)];
      Object.assign(that.formInline, {
        abnormalType: defAbnormalType, startAlsoDate: recentOneMonth
      })
    };


    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      formInline.userTenantId = userInfo.value.tenantId;

      if (formInline.endAlsoDate && formInline.endAlsoDate.length) {
        formInline.endAlsoEndDate = formInline.endAlsoDate[1];
        formInline.endAlsoStartDate = formInline.endAlsoDate[0];
        delete formInline.endAlsoDate;
      }

      findOccupyPileRecordListByPage({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray(operateType);
    };

    const arraySpanMethod = ({ row, column, rowIndex, columnIndex }) => {
      return [1, 8];
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", pageName: 'PileOccupationRecord' }).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 32;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      initFormInline();
      querySiteBasicInfoByTenantId();
      listArray();
    });

    return { ...toRefs(that),tableRef, tableContentRef, tablePaginationRef, setTableMaxHeight, clickResetForm, arraySpanMethod, listArray, querySiteBasicInfoByTenantId };
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}
</style>