<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="选择站点：">
              <el-select v-model="formInline.siteIds" clearable filterable multiple collapse-tags max-collapse-tags="1" placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" :clearable="false"
                              value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button class="blackFontButtons" :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent" ref="tableContentRef">
      <el-row class="content_center_chart" :gutter="12">
        <template v-for="(item,index) in chartList" :key="index">
          <el-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
            <component :is="item.componentName" :titleName="item.name" :returnDataInfo="returnDataInfo"/>
          </el-col>
        </template>
      </el-row>
      <div class="content_border table_bottom_class">
        <TableHeaderCom :siteAllIds="siteAllIds" :formInline="formInline" />
        <div class="content_table_class flex-all" v-loading="listLoading">
          <el-table :data="list" stripe border :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80" fixed="left">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="订单来源" fixed="left" min-width="160" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.platformName) }}</template>
            </el-table-column>
            <el-table-column label="站点名称" fixed="left" show-overflow-tooltip min-width="200">
              <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
            </el-table-column>
            <el-table-column label="充电订单数（笔）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.chargeOrderNum) }}</template>
            </el-table-column>
            <el-table-column label="充电电量（度）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.chargeQt) }}</template>
            </el-table-column>
            <el-table-column label="充电时长（时）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.chargeDuration) }}</template>
            </el-table-column>
            <el-table-column label="订单量占比" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.orderNumRatio) }}</template>
            </el-table-column>
            <el-table-column label="订单总金额（元）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.orderAmount) }}</template>
            </el-table-column>
            <el-table-column label="充电电费（元）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.chargeElecMony) }}</template>
            </el-table-column>
            <el-table-column label="充电服务费（元）" min-width="150">
              <template #default="{ row }">{{ $filters.moreData(row.chargeServiceMony) }}</template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination" ref="tablePaginationRef">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {reactive, toRefs, defineComponent, onMounted, ref} from "vue";
import TableHeaderCom from "./CsChannelSumStatistics/TableHeaderCom.vue";
import {getDaysFromCurrentTime, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {findSummaryCountData} from "@/api/operationManagement/CsDataReportManage";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import MultiChannelDataComparisonCom from "./CsChannelSumStatistics/MultiChannelDataComparisonCom.vue";
import MultiChannelElectricityTrendCom from "./CsChannelSumStatistics/MultiChannelElectricityTrendCom.vue";

export default defineComponent({
  name: "CsChannelSumStatistics",
  components: {TableHeaderCom,MultiChannelDataComparisonCom,MultiChannelElectricityTrendCom},
  setup(){

    const that = reactive({
      Search,
      RefreshRight,
      siteAllIds: [],
      oldFormInline: {},
      returnDataInfo: {},
      formInline: {startAlsoDate: [getDaysFromCurrentTime(-31),getDaysFromCurrentTime(-1)]},

      siteIdArray: [],
      pickerOptions: pickerOptionsGthanAcTime(1),
      chartList: [
        {id: 1,name: "多渠道数据对比",componentName: "MultiChannelDataComparisonCom"},
        {id: 2,name: "多渠道电量趋势",componentName: "MultiChannelElectricityTrendCom"}
      ],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 520,
    });

    const tableContentRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if(formInline.startAlsoDate){
        formInline.startTime = formInline.startAlsoDate[0] + ' 00:00:00';
        formInline.endTime = formInline.startAlsoDate[1] + ' 23:59:59';
        delete formInline.startAlsoDate;
      }

      if(!formInline.siteIds || !formInline.siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.list = [];
          that.totalNumber = 0;
          that.returnDataInfo = {};
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findSummaryCountData({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        that.totalNumber = returnDataInfo.dataReportInfoPage.totalSize;
        that.list = returnDataInfo.dataReportInfoPage.items;
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        delete that.returnDataInfo.dataReportInfoPage;
        tableContentRef.value.scrollTop = 0;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.returnDataInfo = {};
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if(!that.siteAllIds.length) querySiteBasicInfoByTenantId();
      if(that.siteAllIds.length) listArray(operateType);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        for (let i = 0; i < list.length; i++) siteAllIds.push(list[i].id); // 站点id
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        if(that.siteAllIds.length) listArray(); // 有站点id 就获取报表数据
      });
    };

    onMounted(()=>{
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), querySiteBasicInfoByTenantId, clickResetForm, listArray, tableContentRef};
  }
});
</script>

<style scoped lang="scss">
.content_center_chart{
  margin: 12px 0;
}

.table_bottom_class {
  padding: 12px;
  box-sizing: border-box;
}
</style>