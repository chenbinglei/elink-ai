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
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="电桩编号：">
              <el-input v-model="formInline.pileCode" clearable placeholder="请输入电桩编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" :clearable="false"
                              value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="componentName !== 'CsPileV2GReport'">
            <el-form-item label="电桩类型：">
              <el-select v-model="formInline.pileType" clearable placeholder="全部">
                <el-option v-for="item in pileTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="订单渠道：">
              <el-select v-model="formInline.platformLogo" clearable placeholder="全部">
                <template v-for="item in platformLogoArray" :key="item.id">
                  <el-option :label="item.platformName" :value="item.platformLogo"></el-option>
                </template>
              </el-select>
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

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderCom :componentName="componentName" :siteAllIds="siteAllIds" :returnDataInfo="returnDataInfo" :formInline="formInline" />
      <div class="content_table_class flex-all" v-loading="listLoading">
        <el-table :data="list" stripe style="height: 100%;" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" show-overflow-tooltip min-width="200">
            <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
          </el-table-column>
          <el-table-column label="电桩编号" fixed="left" min-width="160" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.pileCode) }}</template>
          </el-table-column>
          <el-table-column label="枪编号" fixed="left">
            <template #default="{ row }">{{ $filters.moreData(row.gunCode) }}</template>
          </el-table-column>
          <el-table-column label="桩类型" min-width="90">
            <template #default="{ row }">
              <div class="pileType" :class="'pileType' + row.typeTd">
                <span class="typeTd">{{ $filters.pileType(row.typeTd) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="额定功率（kW）" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.ratedPower) }}</template>
          </el-table-column>
          <el-table-column :label="componentName === 'CsPileV2GReport' ? '总放电量（度）' : '总充电量（度）'" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.totalQt) }}</template>
          </el-table-column>
          <el-table-column :label="componentName === 'CsPileV2GReport' ? '分时放电量（度）' : '分时充电量（度）'" align="center">
            <el-table-column label="尖" min-width="90">
              <template #default="{ row }">{{ $filters.moreData(row.jqt) }}</template>
            </el-table-column>
            <el-table-column label="峰" min-width="90">
              <template #default="{ row }">{{ $filters.moreData(row.fqt) }}</template>
            </el-table-column>
            <el-table-column label="平" min-width="90">
              <template #default="{ row }">{{ $filters.moreData(row.pqt) }}</template>
            </el-table-column>
            <el-table-column label="谷" min-width="90">
              <template #default="{ row }">{{ $filters.moreData(row.gqt) }}</template>
            </el-table-column>
            <el-table-column label="深谷" min-width="90">
              <template #default="{ row }">{{ $filters.moreData(row.fukayaQt) }}</template>
            </el-table-column>
          </el-table-column>
          <el-table-column :label="componentName === 'CsPileV2GReport' ? '放电时长（时）' : '充电时长（时）'" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.totalDuration) }}</template>
          </el-table-column>
          <el-table-column label="订单数（笔）" min-width="120">
            <template #default="{ row }">{{ $filters.moreData(row.orderCount) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import {ElMessage} from "element-plus";
import {pile_type_array} from "@/utils/setVariate";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import TableHeaderCom from "./CsPileChargingAndDisReport/TableHeaderCom.vue";
import {findPileChargeReport} from "@/api/operationManagement/CsDataReportManage";
import {getDaysFromCurrentTime, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {reactive, toRefs, defineComponent, onMounted, ref, watch, computed, nextTick} from "vue";
import {findPlatformInfoListByPage} from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "CsPileChargingAndDisReport",
  components:{TableHeaderCom},
  props:{
    componentName:{
      type: String,
      default: "CsPileChargingReport"
    }
  },
  setup(props) {
    const tableRef = ref(null);

    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({
      Search,
      RefreshRight,
      siteAllIds: [],
      oldFormInline: {},
      returnDataInfo: {},
      formInline: {startAlsoDate: [getDaysFromCurrentTime(-31),getDaysFromCurrentTime(-1)]},

      siteIdArray: [],
      platformLogoArray: [],
      pileTypeArray: pile_type_array,
      pickerOptions: pickerOptionsGthanAcTime(1),

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

      // V2G报表直接传V2G类型
      if(props.componentName === 'CsPileV2GReport') formInline.pileType = 30;
      formInline.runMode = props.componentName === 'CsPileV2GReport' ? 1 : 0;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPileChargeReport({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        that.totalNumber = returnDataInfo.dataReportInfoPage.totalSize;
        that.list = returnDataInfo.dataReportInfoPage.items;
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        delete that.returnDataInfo.dataReportInfoPage;
        tableContentRef.value.scrollTop = 0;
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
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

        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        if(that.siteAllIds.length) listArray(); // 有站点id 就获取报表数据
      });
    };

    // 根据网关id查询关联所有平台信息
    const queryPlatformInfoListByPage = ()=>{
      findPlatformInfoListByPage({ page: 1,size: 0,tenantId: userInfo.value.tenantId,timer: new Date()}).then(res=>{
        that.platformLogoArray = res.data ? res.data : [];
      });
    };

    const watchComponentName = watch(()=> props.componentName,(newComponentName)=>{
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      clickResetForm("refresh");
    },{ deep: true });

    onMounted(()=>{
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      queryPlatformInfoListByPage();
    });

    return {...toRefs(that), tableRef, querySiteBasicInfoByTenantId, clickResetForm, listArray, tableContentRef, watchComponentName, queryPlatformInfoListByPage};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}
.content_table_class{
  height: 62%;
}
</style>