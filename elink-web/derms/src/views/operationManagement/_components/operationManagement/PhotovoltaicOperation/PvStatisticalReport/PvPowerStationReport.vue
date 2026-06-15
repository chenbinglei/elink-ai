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
            <el-form-item label="时间维度：">
              <el-select v-model="formInline.dateType" placeholder="全部">
                <el-option v-for="item in dateTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="formInline.dateType === 1">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.dayTimeDate" :disabled-date="pickerOptions.disabledDate" :clearable="false" value-format="YYYY-MM-DD"
                              format="YYYY-MM-DD" type="date" placeholder="请选择时间" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="formInline.dateType === 2">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.monthTimeDate" :disabled-date="pickerOptions.disabledDate" format="YYYY-MM" :clearable="false"
                              placeholder="请选择时间" type="month" value-format="YYYY-MM"/>
            </el-form-item>
          </el-col>
          <el-col v-if="formInline.dateType === 3" :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.yearTimeDate" :disabled-date="pickerOptions.disabledDate" format="YYYY" :clearable="false" type="year"
                              value-format="YYYY" placeholder="请选择时间"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm('resetPage')">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="电站列表">
        <template #content>
          <div class="table_top_content">
            <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="listArray('exportTable')">导出</el-button>
            <el-button :icon="Setting" @click="clickOperateBut(1)">列表显示项</el-button>
          </div>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" min-width="210" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <template v-for="item in tableAllFieldList" :key="item.key">
            <template v-if="tableShowFieldList.indexOf(item.key) !== -1">
              <el-table-column :min-width="item.minWidth" show-overflow-tooltip>
                <template #header>
                  <div class="header_label">
                    <span class="header_name">{{ item.name }}</span>
                    <span class="header_unit" v-if="item.unit">（{{ item.unit }}）</span>
                  </div>
                </template>
                <template #default="{ row }">
                  <div class="table_content_class" :class="[item.className,item.className + row[item.key]]">
                    <template v-if="item.componentName">
                      <component :is="item.componentName" :configInfo="item" :returnDataInfo="row"></component>
                    </template>
                    <template v-else>{{ $filters[item.filterName ? item.filterName : 'moreData'](row[item.key]) }}</template>
                  </div>
                </template>
              </el-table-column>
            </template>
          </template>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <TableFieldControlDialog v-if="tableFieldControlVisible" v-model:isVisible="tableFieldControlVisible" :tableAllFieldList="tableAllFieldList"
                             :tableFieldStorageName="tableFieldStorageName" @changeEvent="queryTableFieldListFun" />
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import SiteCityNameCom from "./SiteCityNameCom.vue";
import {exportCustomExcel} from "@/common/exportExcel";
import {onMounted, reactive, ref, toRefs, defineComponent, nextTick} from "vue";
import {RefreshRight, Search, Folder, Setting} from "@element-plus/icons-vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {findPvSiteReportList} from "@/api/operationManagement/PvOperationsAnalysis";
import TableFieldControlDialog from "@/views/operationManagement/_components/operationManagement/PublicComponents/TableFieldControlDialog.vue";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDateAll, isMonth,
  isToday, isYear, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default defineComponent({
  name: "PvPowerStationReport",
  components:{TableFieldControlDialog, SiteCityNameCom},
  setup(){
    const tableRef = ref(null);
    const that = reactive({
      Search,
      Folder,
      Setting,
      RefreshRight,
      siteAllIds: [],
      siteIdArray: [],
      oldFormInline: {},
      formInline: {
        dateType: 1,  // 时间类型 1-逐日 2-逐月
        dayTimeDate: getDaysFromCurrentTime(),
        yearTimeDate: getDaysFromCurrentTime(0, 2),
        monthTimeDate: getDaysFromCurrentTime(0, 1),
      },
      pickerOptions: pickerOptionsGthanAcTime(),
      dateTypeArray: [{id: 1,name: "逐日"},{id: 2,name: "逐月"},{id: 3,name: "逐年"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      exportLoading: false,

      tableShowFieldList: [], // 表格展示字段
      tableFieldControlVisible: false,
      tableFieldStorageName: 'PvPowerStationReport_TableFieldList',
      tableAllFieldList: [
        { name: "所在城市", key: 'siteCityName', componentName: "SiteCityNameCom", minWidth: 150, exportHandleFun: 'siteCityNameFun'},
        { name: "装机容量", key: "pvCapacity", minWidth: 150, unit: "kWp", width: 30 },
        { name: "总辐照量", key: "totalIrradiation", minWidth: 170, unit: "kWh/㎡", width: 30 },
        { name: "平均温度", key: "avgTemp", minWidth: 150, unit: "°C", width: 30 },
        { name: "理论发电量", key: "theoryQt", minWidth: 150, unit: "度", width: 30 },
        { name: "逆变器发电量", key: "inverterQt", minWidth: 150, unit: "度", width: 30 },
        { name: "并网点发电量", key: "parallelQt", minWidth: 150, unit: "度", width: 30 },
        { name: "上网电量", key: "internetQt", minWidth: 150, unit: "度", width: 30 },
        { name: "自用电量", key: "occupiedQt", minWidth: 150, unit: "度", width: 30 },
        { name: "自发自用比例", key: "occupiedRatio", minWidth: 150, unit: "%", width: 30 },
        { name: "损失电量", key: "lossQt", minWidth: 150, unit: "度", width: 30 },
        { name: "损失收益", key: "lossMoney", minWidth: 150, unit: "元", width: 30 },
        { name: "峰值发电功率", key: "fvaluePower", minWidth: 170, unit: "kW", width: 30 },
        { name: "负荷率", key: "loadRatio", minWidth: 150, unit: "%", width: 30 },
        { name: "光伏收益", key: "pvMoney", minWidth: 150, unit: "元", width: 30 },
        { name: "上网收益", key: "internetMoney", minWidth: 150, unit: "元", width: 30 },
        { name: "消纳收益", key: "consumMoney", minWidth: 150, unit: "元", width: 30 },
        { name: "补贴收益", key: "subsidyMoney", minWidth: 150, unit: "元", width: 30 },
        { name: "二氧化碳减排量", key: "dioxideReduce", minWidth: 180, unit: "千克", width: 30 },
        { name: "节约标煤量", key: "thriftTce", minWidth: 150, unit: "千克", width: 30 },
        { name: "等效植树", key: "equivalentTree", minWidth: 150, unit: "棵", width: 30 },
      ]
    });

    const listArray = (operateType) => {
      // console.log(operateType);
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      operateType === "exportTable" ? that.exportLoading = true : that.listLoading = true;

      if(formInline.dateType === 1){
        formInline.startTime = formInline.dayTimeDate + ' 00:00:00';
        formInline.endTime = formInline.dayTimeDate + ' 23:59:59';
        if(isToday(formInline.dayTimeDate)) formInline['endTime'] = getNowDateAll();
        delete formInline.dayTimeDate;
      }

      if(formInline.dateType === 2){
        formInline.startTime = getCurrentMonthFirstDay(formInline.monthTimeDate) + ' 00:00:00';
        formInline['endTime'] = getCurrentMonthLastDay(formInline.monthTimeDate) + ' 23:59:59';
        if(isMonth(formInline.monthTimeDate)) formInline['endTime'] = getNowDateAll();
        delete formInline.monthTimeDate;
      }

      if(formInline.dateType === 3){
        formInline['startTime'] = getCurrentYearFirstDay(formInline.yearTimeDate) + ' 00:00:00';
        formInline['endTime'] = getCurrentYearLastDay(formInline.yearTimeDate) + ' 23:59:59';
        if(isYear(formInline.yearTimeDate)) formInline['endTime'] = getNowDateAll();
        delete formInline.yearTimeDate;
      }

      if(!formInline.siteIds || !formInline.siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.list = [];
          that.totalNumber = 0;
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      let size = operateType === "exportTable" ? 0 : that.pageNum;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPvSiteReportList({page: that.currentPage, size , ...formInline}).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        // 导出列表数据
        if(operateType === "exportTable"){
          let tableExportList = [{width: 25, key: "siteName", name: "站点名称"}];
          for (let i = 0;i < that.tableAllFieldList.length;i++){
            let table_name = `${ that.tableAllFieldList[i].name }${ that.tableAllFieldList[i].unit ? '(' + that.tableAllFieldList[i].unit + ')' : '' }`;
            tableExportList.push({ ...that.tableAllFieldList[i],name: table_name });
          }

          exportCustomExcel(tableExportList, returnDataInfo, `光伏_电站报表_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${getNowDateAll()}`);
          setTimeout(()=> {that.exportLoading = false;},500);
          await nextTick();
           if(tableRef.value){
          tableRef.value.setScrollTop(0);
        }
        } else {
          if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
          that.totalNumber = returnDataInfo.totalSize;
          that.list = returnDataInfo.items;
          that.listLoading = false;
        }
      }).catch((error) => {
        that.listLoading = false;
        that.exportLoading = false;
        if (error && error.code === 88886) return;
        if(operateType !== "exportTable"){
          that.totalNumber = 0;
          that.list = [];
        }
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if(!that.siteAllIds.length) querySiteBasicInfoByTenantId();
      if(that.siteAllIds.length) listArray(operateType);
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) that.tableFieldControlVisible = true;
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: 1,timer: new Date()}).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        for (let i = 0; i < list.length; i++) siteAllIds.push(list[i].id); // 站点id
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        if(that.siteAllIds.length) listArray(); // 有站点id 就获取报表数据
      });
    };

    // 查询表格展示字段列表
    const queryTableFieldListFun = ()=>{
      let tableShowFieldList = [];
      let storageFieldList = localStorage.getItem(that.tableFieldStorageName);
      // console.log(storageFieldList);
      if(!storageFieldList){
        for(let i = 0;i < that.tableAllFieldList.length;i++){
          tableShowFieldList.push(that.tableAllFieldList[i].key);
        }
      }
      that.tableShowFieldList = storageFieldList ? JSON.parse(storageFieldList) : tableShowFieldList;
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
      queryTableFieldListFun();
    });

    return {...toRefs(that), tableRef, clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, querySiteBasicInfoByTenantId, listArray, queryTableFieldListFun,
      clickOperateBut};
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;
}
</style>