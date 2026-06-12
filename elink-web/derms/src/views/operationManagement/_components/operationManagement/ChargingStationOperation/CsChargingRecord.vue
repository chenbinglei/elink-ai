<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
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
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
              <el-select v-model="formInline.siteId" clearable filterable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName"
                  :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="订单状态：">
              <el-select v-model="formInline.orderStatus" clearable placeholder="全部">
                <template v-for="item in orderStatusArray" :key="item.id">
                  <el-option v-if="!item.componentName || item.componentName === componentName" :label="item.name"
                    :value="item.id"></el-option>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="启动方式：">
              <el-select v-model="formInline.runMode" clearable placeholder="全部">
                <el-option v-for="item in runModeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate"
                :shortcuts="pickerOptions.shortcuts" clearable value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
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
          <el-col :xs="24" :sm="12" :md="12" :xl="12">
            <el-form-item label="结束时间：">
              <el-date-picker v-model="formInline.endAlsoDate" :disabled-date="pickerOptions.disabledDate"
                :shortcuts="pickerOptions.shortcuts" clearable value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="补单状态：">
              <el-select v-model="formInline.repairStatus" clearable placeholder="全部">
                <template v-for="item in repairStatusArray" :key="item.id">
                  <el-option :label="item.name" :value="item.id"></el-option>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="异常类型：">
              <el-select v-model="formInline.abnormalType" clearable placeholder="全部">
                <template v-for="item in abnormalTypeArray" :key="item.id">
                  <el-option :label="item.name" :value="item.id">
                    <div class="flex-ai-center jc-space-between">
                      <span class="name">{{ item.name }}</span>
                      <span class="alterText">{{ item.alterText }}</span>
                    </div>
                  </el-option>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="电量区间：">
              <IntervalNumInputCom v-model:minValue="formInline.minQt" v-model:maxValue="formInline.maxQt" unit="度" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="时长区间：">
              <IntervalNumInputCom v-model:minValue="formInline.minDuration" v-model:maxValue="formInline.maxDuration"
                unit="时" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              <el-button :icon="Folder" :disabled="exportLoading" :loading="exportLoading"
                @click="clickExportBut">导出</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="订单列表">
        <template #content>
          <el-button :icon="RefreshRight" @click="listArray">刷新</el-button>
        </template>
      </TableHeaderTitle>
      <TotalOrderData :orderTotalData="orderTotalData" :componentName="componentName"></TotalOrderData>
      <div class="content_table_class">
        <el-table v-loading="listLoading" :data="list" :span-method="arraySpanMethod">
          <template #empty><null-data></null-data></template>
          <el-table-column align="center" colspan="9" label="站点名称">
            <template #default="{ row }">
              <DcOrderListCard :componentName="componentName" :orderInfo="row" @changeEvent="listArray('refresh')">
              </DcOrderListCard>
            </template>
          </el-table-column>
          <el-table-column align="center"
            :label="componentName === 'CsChargingRecord' ? '充电时长' : '放电时长'"></el-table-column>
          <el-table-column align="center"
            :label="componentName === 'CsChargingRecord' ? '充电电量' : '放电电量'"></el-table-column>
          <el-table-column align="center" label="订单状态"></el-table-column>
          <el-table-column align="center" label="订单金额"></el-table-column>
          <el-table-column align="center" label="实付金额" v-if="componentName === 'CsChargingRecord'"></el-table-column>
          <el-table-column align="center" label="停止码"></el-table-column>
          <el-table-column align="center" label="订单渠道"></el-table-column>
          <el-table-column align="center" label="操作"></el-table-column>
        </el-table>
      </div>
      <div ref="tablePaginationRef" class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber"
          @pageChange="listArray" />
      </div>
    </div>
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import { ElMessage } from "element-plus";
import { exportCustomExcel } from "@/common/exportExcel";
import { RefreshRight, Search, Folder } from "@element-plus/icons-vue";
import { getNowDateAll, pickerOptionsGthanAcTime } from "@/utils/dateTime";
import { TotalOrderData, DcOrderListCard } from "./CsDisAndChargingRecord/index";
import IntervalNumInputCom from "@/components/component/IntervalNumInputCom.vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { dc_order_status_array, pile_run_mode_array, pile_type_array } from "@/utils/setVariate";
import { watch, onMounted, reactive, ref, toRefs, computed, defineComponent, getCurrentInstance } from "vue";
import { findOrderRecordList, findOrderRecordListByPage, findPlatformInfoListByPage } from "@/api/operationManagement/CsDisAndChargingRecord";
import filterMethodMap from "@/common/filters/filter";
import moment from "moment";

export default defineComponent({
  name: "CsDisAndChargingRecord",
  components: { DcOrderListCard, TotalOrderData, IntervalNumInputCom },
  props: {
    componentName: {
      type: String,
      default: ""
    },
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {

    const appStore = useAppStore();
    const { emit } = getCurrentInstance();

    const userInfo = computed(() => {
      return appStore.userInfo;
    });
    const format = 'YYYY-MM-DD';
    const defAbnormalType = 0;

    const that = reactive({
      Folder,
      Search,
      RefreshRight,
      siteIdArray: [],
      oldFormInline: {},
      orderTotalData: {},
      platformLogoArray: [],
      pileTypeArray: pile_type_array,
      runModeArray: pile_run_mode_array,
      orderStatusArray: dc_order_status_array,
      pickerOptions: pickerOptionsGthanAcTime(),
      formInline: { keywordType: "1", ...props.routeInfo },
      repairStatusArray: [{ id: 0, name: "挂单" }, { id: 1, name: "自动恢复" }, { id: 2, name: "人工恢复" }, { id: 3, name: "正常" }],
      keywordTypeArray: [{ id: "1", name: "订单号" }, { id: "2", name: "手机号" }, { id: "3", name: "VIN码" }, { id: "4", name: "电卡ID" }, { id: "5", name: "桩编码" }],
      abnormalTypeArray: [
        { id: 0, name: "无异常" },
        { id: 1, name: "时间异常", alterText: '订单时长大于24h' },
        { id: 2, name: "大额订单", alterText: '订单总金额大于1000元' },
        { id: 3, name: "电量异常", alterText: '电量大于500度' },
        { id: 4, name: "无效订单", alterText: '电量小于1度' },
        { id: 5, name: "费用异常", alterText: '订单金额为0元' }
      ],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      exportLoading: false,
    });

    const initFormInline = () => {
      const recentOneMonth = [moment().subtract(1, "month").format(format), moment().format(format)];
      Object.assign(that.formInline, {
        abnormalType: defAbnormalType, startAlsoDate: recentOneMonth
      })
    };

    const tableContentRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;

      // 多个订单类型 0-充电订单 1-放电订单 2-离线放电订单
      formInline.orderTypes = props.componentName === "CsChargingRecord" ? [0] : [1, 2];
      formInline.userTenantId = userInfo.value.tenantId;

      if (formInline.startAlsoDate && formInline.startAlsoDate.length) {
        formInline.startAlsoEndDate = formInline.startAlsoDate[1];
        formInline.startAlsoStartDate = formInline.startAlsoDate[0];
        delete formInline.startAlsoDate;
      }

      if (formInline.endAlsoDate && formInline.endAlsoDate.length) {
        formInline.endAlsoEndDate = formInline.endAlsoDate[1];
        formInline.endAlsoStartDate = formInline.endAlsoDate[0];
        delete formInline.endAlsoDate;
      }

      findOrderRecordListByPage({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
        that.listLoading = false;
        let returnDataInfo = res.data ? res.data : {};
        that.list = returnDataInfo.orderRecordDataPage.items;
        that.totalNumber = returnDataInfo.orderRecordDataPage.totalSize;
        let orderTotalData = JSON.parse(JSON.stringify(returnDataInfo));
        orderTotalData.orderNumber = returnDataInfo.orderRecordDataPage.totalSize;
        delete orderTotalData.orderRecordDataPage;
        that.orderTotalData = JSON.parse(JSON.stringify(orderTotalData));
        tableContentRef.value.scrollTop = 0;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.orderTotalData = {};
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray(operateType);
    };

    const arraySpanMethod = () => {
      return [1, 9];
    };

    const clickExportBut = () => {
      that.exportLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      let textName = props.componentName === "CsChargingRecord" ? "充" : "放";
      let disAndCharging = props.componentName === "CsChargingRecord" ? 1 : 2;

      // 0-充电订单 1-放电订单
      formInline.orderTypes = props.componentName === "CsChargingRecord" ? [0] : [1, 2];
      formInline.userTenantId = userInfo.value.tenantId;

      if (formInline.startAlsoDate && formInline.startAlsoDate.length) {
        formInline.startAlsoEndDate = formInline.startAlsoDate[1];
        formInline.startAlsoStartDate = formInline.startAlsoDate[0];
        delete formInline.startAlsoDate;
      }

      if (formInline.endAlsoDate && formInline.endAlsoDate.length) {
        formInline.endAlsoEndDate = formInline.endAlsoDate[1];
        formInline.endAlsoStartDate = formInline.endAlsoDate[0];
        delete formInline.endAlsoDate;
      }

      // 查询订单记录列表信息
      findOrderRecordList({ page: that.currentPage, size: that.pageNum, ...formInline }).then((res) => {
        let orderRecordDataList = res.data ? res.data : [];

        let tableHeader = [
          { width: 40, key: "orderNum", name: "订单号" },
          { width: 20, key: "platformName", name: "平台" },
          { width: 25, key: "siteName", name: "站点名称" },
          { width: 25, key: "operateUnitName", name: "运营商" },
          { width: 20, key: "pileCode", name: "桩编号" },
          { width: 20, key: "gunCode", name: "枪编号" },
          { width: 20, key: "pileType", name: "电桩类型", filterName: 'pileType' },
          { width: 25, key: "startTime", name: "开始时间" },
          { width: 25, key: "endTime", name: "结束时间" },
          { width: 20, key: "duration", name: "订单时长" },
          { width: 20, key: "totalQt", name: `${textName}电电量` },
          { width: 20, key: "totalCost", name: "订单金额" },
          // {width: 20, key: "totalFee", name: "服务费金额"},
          { width: 20, key: "orderStatus", name: "订单状态", filterName: 'orderStatus', disAndCharging: disAndCharging },
          { width: 20, key: "stopReason", name: "停止码" },
          // 2025-04-01新增字段
          { width: 40, key: "stopDetailReason", name: "结束原因" },
          { width: 20, key: "startWay", name: "启动方式" },
          { width: 40, key: "phone", name: "手机号" },
          { width: 20, key: "cardNumber", name: "电卡卡号" },
          { width: 20, key: "busVin", name: "VIN码" },
          { width: 20, key: "plateNumber", name: "车牌号" }
        ];

        that.exportLoading = false;
        // 由于启动方式使用starter字段，后端返回int类型，前端需要转换为中文
        orderRecordDataList.forEach(item => {
          item.startWay = filterMethodMap.pileRunMode(item.starter);
        });
        exportCustomExcel(tableHeader, orderRecordDataList, `${textName}电记录 - ${getNowDateAll()}`);
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", timer: new Date() }).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    // 根据网关id查询关联所有平台信息
    const queryPlatformInfoListByPage = () => {
      findPlatformInfoListByPage({ page: 1, size: 0, tenantId: userInfo.value.tenantId, timer: new Date() }).then(res => {
        that.platformLogoArray = res.data ? res.data : [];
      });
    };

    const watchComponentName = watch(() => props.componentName, () => {
      clickResetForm("refresh");
    }, { deep: true });

    onMounted(() => {
      initFormInline();
      listArray();
      queryPlatformInfoListByPage();
      querySiteBasicInfoByTenantId();
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      emit("changEvent", { operateType: "clearRouteInfo" });
    });

    return {
      ...toRefs(that), watchComponentName, querySiteBasicInfoByTenantId, listArray, clickResetForm, arraySpanMethod, queryPlatformInfoListByPage,
      clickExportBut, tableContentRef
    };
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  :deep(.el-table) {
    --el-table-row-hover-bg-color: none;

    .cell {
      padding: 0;
    }
  }
}

.alterText {
  font-size: 12px;
  color: #ffffffcc;
}
</style>