<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="选择站点：">
              <el-select v-model="formInline.siteIds" clearable filterable multiple collapse-tags max-collapse-tags="1"
                placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="所属区域：">
              <el-col :span="8" style="padding: 0">
                <el-select v-model="formInline.areaType" placeholder="请选择" @change="formInline.area = ''">
                  <el-option v-for="(item, index) in areaTypeArray" :key="index" :label="item.name"
                    :value="item.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="16" style="padding: 0">
                <el-select v-model="formInline.area" filterable clearable placeholder="请选择">
                  <template v-for="(item, index) in (formInline.areaType === 1 ? provinceArray : cityArray)"
                    :key="index">
                    <el-option :label="item.name" :value="item.name"></el-option>
                  </template>
                </el-select>
              </el-col>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="商户：">
              <el-select v-model="formInline.accountId" filterable clearable placeholder="全部">
                <el-option v-for="item in accountArray" :key="item.id" :label="item.mchName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="订单渠道：">
              <el-select v-model="formInline.platformLogo" filterable clearable placeholder="全部">
                <template v-for="item in platformLogoArray" :key="item.id">
                  <el-option :label="item.platformName" :value="item.platformLogo"></el-option>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="16" :lg="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate"
                :shortcuts="pickerOptions.shortcuts" :clearable="false" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" />
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

    <div class="tableContent content_border" ref="tableContentRef" style="">
      <TableHeaderCom :componentName="componentName" :siteAllIds="siteAllIds" :returnDataInfo="returnDataInfo"
        :formInline="formInline" />
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table :data="list" stripe border v-loading="listLoading" :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" show-overflow-tooltip min-width="200">
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column label="所在城市" min-width="160">
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.province) }}</span>
              <span>/</span>
              <span>{{ $filters.moreData(row.city) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="商户" show-overflow-tooltip min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.accountName) }}</template>
          </el-table-column>

          <template v-if="componentName === 'CsStationChargingReport'">
            <el-table-column label="直流桩/枪数" min-width="150">
              <template #default="{ row }">
                <span class="number">{{ $filters.moreData(calculationNumFun(row.dcPileNum, row.v2gPileNum)) }}</span>
                <span class="split_symbol">/</span>
                <span class="number">{{ $filters.moreData(calculationNumFun(row.dcGunNum, row.v2gGunNum)) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="交流桩/枪数" min-width="150">
              <template #default="{ row }">
                <span class="number">{{ $filters.moreData(row.acPileNum) }}</span>
                <span class="split_symbol">/</span>
                <span class="number">{{ $filters.moreData(row.acGunNum) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="直流额定功率（kW）" min-width="180">
              <template #default="{ row }">{{ $filters.moreData(calculationNumFun(row.dcRatedPower, row.v2gRatedPower))
                }}</template>
            </el-table-column>
            <el-table-column label="交流额定功率（kW）" min-width="180">
              <template #default="{ row }">{{ $filters.moreData(row.acRatedPower) }}</template>
            </el-table-column>
            <el-table-column label="直流充电量（度）" min-width="160">
              <template #default="{ row }">{{ $filters.moreData(calculationNumFun(row.dcTotalQt, row.v2gTotalQt))
                }}</template>
            </el-table-column>
            <el-table-column label="交流充电量（度）" min-width="160">
              <template #default="{ row }">{{ $filters.moreData(row.acTotalQt) }}</template>
            </el-table-column>
            <el-table-column label="总充电量（度）" min-width="160">
              <template #default="{ row }">{{ $filters.moreData(row.totalQt) }}</template>
            </el-table-column>
          </template>

          <template v-if="componentName === 'CsStationV2GReport'">
            <el-table-column label="V2G桩/枪数" min-width="150">
              <template #default="{ row }">
                <span class="number">{{ $filters.moreData(row.v2gPileNum) }}</span>
                <span class="split_symbol">/</span>
                <span class="number">{{ $filters.moreData(row.v2gGunNum) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="V2G额定功率（kW）" min-width="170">
              <template #default="{ row }">{{ $filters.moreData(row.v2gRatedPower) }}</template>
            </el-table-column>
            <el-table-column label="总放电量（度）" min-width="160">
              <template #default="{ row }">{{ $filters.moreData(row.v2gTotalQt) }}</template>
            </el-table-column>
          </template>

          <el-table-column :label="componentName === 'CsStationV2GReport' ? '分时放电量（度）' : '分时充电量（度）'" align="center">
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
          <el-table-column :label="componentName === 'CsStationV2GReport' ? '总放电时长（时）' : '总充电时长（时）'" label="总放电时长（时）"
            min-width="160">
            <template #default="{ row }">{{ $filters.moreData(row.totalDuration) }}</template>
          </el-table-column>
          <el-table-column label="订单数量（笔）" min-width="140">
            <template #default="{ row }">{{ $filters.moreData(row.orderCount) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
          @pageChange="listArray" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import pinyin from "tiny-pinyin";
import { ElMessage } from "element-plus";
import { calcNumberFun, isNumber } from "@/utils";
import { area_type_array } from "@/utils/setVariate";
import { RefreshRight, Search } from '@element-plus/icons-vue';
import TableHeaderCom from "./CsStationChargingReport/TableHeaderCom.vue";
import { getDaysFromCurrentTime, pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { reactive, toRefs, defineComponent, onMounted, ref, watch, computed, nextTick } from "vue";
import { findPlatformInfoListByPage } from "@/api/operationManagement/CsDisAndChargingRecord";
import { findAccountList, findSiteChargeReport } from "@/api/operationManagement/CsDataReportManage";

export default defineComponent({
  name: "CsStationChargingAndDisReport",
  components: { TableHeaderCom },
  props: {
    componentName: {
      type: String,
      default: "CsStationChargingReport"
    }
  },
  setup (props) {

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
      formInline: { areaType: 1, startAlsoDate: [getDaysFromCurrentTime(-31), getDaysFromCurrentTime(-1)] },

      cityArray: [],
      siteIdArray: [],
      accountArray: [],
      provinceArray: [],
      platformLogoArray: [],
      areaTypeArray: area_type_array,
      pickerOptions: pickerOptionsGthanAcTime(1),

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });
    const tableRef = ref(null);
    const tableContentRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.startAlsoDate) {
        formInline.startTime = formInline.startAlsoDate[0] + ' 00:00:00';
        formInline.endTime = formInline.startAlsoDate[1] + ' 23:59:59';
        delete formInline.startAlsoDate;
      }

      if (!formInline.siteIds || !formInline.siteIds.length) {
        if (!that.siteAllIds || !that.siteAllIds.length) {
          that.list = [];
          that.totalNumber = 0;
          that.returnDataInfo = {};
          that.listLoading = false;
          ElMessage({ type: "error", showClose: true, message: "请先选择查询的站点！" });
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      formInline.runMode = props.componentName === 'CsStationV2GReport' ? 1 : 0;  // 报表模式 0-充电 1-放电
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findSiteChargeReport({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
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

    const calculationNumFun = (num1, num2) => {
      if (isNumber(num1) || isNumber(num2)) {
        return calcNumberFun(num1 ?? 0, num2 ?? 0, '+');
      }
      return '';
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if (!that.siteAllIds.length) querySiteBasicInfoByTenantId();
      if (that.siteAllIds.length) listArray(operateType);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", timer: new Date() }).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        let provinceArray = [], cityArray = [];
        for (let i = 0; i < list.length; i++) {
          siteAllIds.push(list[i].id); // 站点id

          // 读写类型字段
          if (list[i].siteReadwriteObject) list[i].siteReadwriteObject = JSON.parse(list[i].siteReadwriteObject);
          let location_info = list[i]?.siteReadwriteObject?.location ?? {};

          //省份处理
          if (location_info.province) {
            let province_id = pinyin.convertToPinyin(location_info.province);

            let findProvince = provinceArray.find(item => item.id === province_id);
            if (!findProvince) provinceArray.push({ name: location_info.province, id: province_id });

            // 市区处理
            let findCity = cityArray.find(item => item.name === location_info.city);
            if (!findCity) cityArray.push({ name: location_info.city, parentId: province_id });
          }
        }
        // console.log(provinceArray);
        // console.log(cityArray);
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.cityArray = JSON.parse(JSON.stringify(cityArray));
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
        if (that.siteAllIds.length) listArray(); // 有站点id 就获取报表数据
      });
    };

    // 查询商户列表
    const queryAccountList = () => {
      let queryMode = props.componentName === 'CsStationV2GReport' ? 2 : 1;
      findAccountList({ queryMode: queryMode, timer: new Date() }).then(res => {
        that.accountArray = res.data ? res.data : [];
      });
    };

    // 根据网关id查询关联所有平台信息
    const queryPlatformInfoListByPage = () => {
      findPlatformInfoListByPage({ page: 1, size: 0, tenantId: userInfo.value.tenantId, timer: new Date() }).then(res => {
        that.platformLogoArray = res.data ? res.data : [];
      });
    };

    const watchComponentName = watch(() => props.componentName, (newComponentName) => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      clickResetForm("refresh");
      queryAccountList();
    }, { deep: true });
    // 计算出表格最大高度
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 168;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };
    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      queryPlatformInfoListByPage();
      queryAccountList();
    });

    return {
      ...toRefs(that), querySiteBasicInfoByTenantId, clickResetForm, listArray, tableContentRef, watchComponentName, queryAccountList, calculationNumFun,
      queryPlatformInfoListByPage, setTableMaxHeight, tablePaginationRef, tableRef
    };
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  box-sizing: border-box;
  padding: 12px;

  z-index: 1;

  .tableCenter {
    // overflow: auto;
  }
}
</style>