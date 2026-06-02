<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="站点名称：">
                <el-select v-model="formInline.siteId" filterable clearable placeholder="全部">
                  <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
                </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="桩编号：">
              <el-input v-model="formInline.pileCode" clearable placeholder="请输入桩编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="告警状态：">
              <el-select v-model="formInline.alarmStatus" filterable clearable placeholder="全部">
                <el-option v-for="item in alarmStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="故障码：">
              <el-input v-model="formInline.faultCode" clearable placeholder="请输入故障码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :xl="12">
            <el-form-item label="告警时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" clearable
                              value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="告警等级：">
              <el-select v-model="formInline.eventLevel" filterable clearable placeholder="全部">
                <el-option v-for="item in eventLevelArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
              <el-button :icon="Folder" :disabled="exportLoading" :loading="exportLoading" @click="clickExportBut">导出</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="告警记录"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
          </el-table-column>
          <el-table-column label="桩编号" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceCode)}}</template>
          </el-table-column>
          <el-table-column label="枪编号">
            <template #default="{ row }">{{ $filters.moreData(row.gunCode)}}</template>
          </el-table-column>
          <el-table-column label="告警等级">
            <template #default="{ row }">
              <div class="eventLevel" :class="'eventLevel' + row.eventLevel">
                <div class="round_dot"></div>
                <div class="eventName">{{ $filters.eventLevel(row.eventLevel) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="告警信息" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.eventName) }}</template>
          </el-table-column>
          <el-table-column label="故障码">
            <template #default="{ row }">{{ $filters.moreData(row.faultCode) }}</template>
          </el-table-column>
          <el-table-column label="告警时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="告警状态">
            <template #default="{ row }">
              <div class="alarmStatus" :class="'alarmStatus' + row.alarmStatus">
                <span class="alarmStatusText">{{ $filters.alarmStatus(row.alarmStatus) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="恢复时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
          </el-table-column>
          <el-table-column label="故障时长">
            <template #default="{ row }">{{ $filters.moreData(row.alarmDuration) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import {exportCustomExcel} from "@/common/exportExcel";
import {RefreshRight,Search,Folder} from '@element-plus/icons-vue';
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {findAlarmListByPage} from "@/api/operationManagement/CsElectricPileLog";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {getNowDateAll, pickerOptionsGthanAcTime, getDaysFromCurrentTime} from "@/utils/dateTime";

export default defineComponent({
  name: "CsElectricPileLog",
  setup(){
    const that = reactive({
      Search,
      Folder,
      RefreshRight,
      oldFormInline: {},
      formInline: {startAlsoDate: [getDaysFromCurrentTime(-30),getDaysFromCurrentTime()]},

      siteIdArray: [],
      pickerOptions: pickerOptionsGthanAcTime(),
      alarmStatusArray: [{id: 0,name: "未修复"},{id: 1,name: "已修复"}],
      eventLevelArray: [{id: 1,name: "普通告警"},{id: 2,name: "重要告警"},{id: 3,name: "紧急告警"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      exportLoading: false,

      exportTableHeader:[
        {width: 30, key: "siteName", name: "站点名称"},
        {width: 20, key: "deviceCode", name: "桩编号"},
        {width: 15, key: "gunCode", name: "枪编号"},
        {width: 20, key: "eventLevel", name: "告警等级",filterName:'eventLevel'},
        {width: 25, key: "eventName", name: "告警信息"},
        {width: 15, key: "faultCode", name: "故障码"},
        {width: 25, key: "createTime", name: "告警时间"},
        {width: 15, key: "alarmStatus", name: "告警状态",filterName:'alarmStatus'},
        {width: 25, key: "updateTime", name: "恢复时间"},
        {width: 20, key: "alarmDuration", name: "故障时长"},
      ]
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.startAlsoDate && formInline.startAlsoDate.length) {
        formInline.alarmStartDate = formInline.startAlsoDate[0];
        formInline.alarmEndDate = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findAlarmListByPage({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    const clickExportBut = ()=> {
      that.exportLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.startAlsoDate && formInline.startAlsoDate.length) {
        formInline.alarmStartDate = formInline.startAlsoDate[0];
        formInline.alarmEndDate = formInline.startAlsoDate[1];
        delete formInline.startAlsoDate;
      }
      findAlarmListByPage({page: 1, size: 0, ...formInline}).then(res => {
        let recordDataList = res.data ? res.data : [];
        exportCustomExcel(that.exportTableHeader, recordDataList, `告警记录 - ${ getNowDateAll() }`);
        that.exportLoading = false;
      }).catch(() => {
        that.exportLoading = false;
      });
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      listArray();
    });

    return {...toRefs(that), clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, listArray, querySiteBasicInfoByTenantId, clickExportBut};
  }
});
</script>

<style scoped lang="scss">
.tableContent{
  padding: 12px;
  box-sizing: border-box;

  .eventLevel{
    display: flex;
    align-items: center;

    .round_dot{
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #007FEB;
    }

    .eventName{
      color: #007FEB;
      font-size: 14px;
      margin-left: 8px;
    }
  }

  .eventLevel2{

    .round_dot{
      background: #FF9C02;
    }

    .eventName{
      color: #FF9C02;
    }
  }

  .eventLevel3{

    .round_dot{
      background: #FF1515;
    }

    .eventName{
      color: #FF1515;
    }
  }

  .alarmStatus{
    width: fit-content;
    height: 32px;
    border-radius: 8px;
    background: rgba(255,21,21,0.2);
    padding: 0 16px;
    box-sizing: border-box;
    display: flex;
    align-items: center;

    .alarmStatusText{
      color: #FF1515;
      font-size: 14px;
    }
  }

  .alarmStatus1{
    background: rgba(65, 203, 74, .2);
    .alarmStatusText{
      color: #41CB4A;
    }
  }
}
</style>