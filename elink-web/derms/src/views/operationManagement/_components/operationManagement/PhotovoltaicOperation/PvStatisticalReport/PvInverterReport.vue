<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="选择设备：">
              <el-select v-model="formInline.deviceIds" :data="deviceIdArray" clearable filterable multiple collapse-tags max-collapse-tags="1" placeholder="全部">
                <el-option-group v-for="item in deviceIdArray" :key="item.id" :label="item.name">
                  <el-option v-for="deviceItem in item.children" :key="deviceItem.id" :label="deviceItem.name" :value="deviceItem.id"></el-option>
                </el-option-group>
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
      <TableHeaderTitle title="逆变器报表">
        <template #content>
          <div class="table_top_content">
            <el-button :disabled="exportLoading" :icon="Folder" :loading="exportLoading" @click="listArray('exportTable')">导出</el-button>
            <el-button :icon="Setting" @click="clickOperateBut(1)">列表显示项</el-button>
          </div>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table ref="multipleTableRef" v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" @sort-change="changeTableSort">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称" fixed="left" min-width="210" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column label="设备名称" fixed="left" min-width="210" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
          </el-table-column>
          <template v-for="item in tableAllFieldList" :key="item.key">
            <template v-if="tableShowFieldList.indexOf(item.key) !== -1">
              <el-table-column :prop="item.key" :min-width="item.minWidth" sortable show-overflow-tooltip>
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
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {exportCustomExcel} from "@/common/exportExcel";
import {onMounted, reactive, ref, toRefs, defineComponent, nextTick} from "vue";
import {RefreshRight, Search, Folder, Setting} from "@element-plus/icons-vue";
import {findPvInverterReportList,findInverterListByUserId} from "@/api/operationManagement/PvOperationsAnalysis";
import TableFieldControlDialog from "@/views/operationManagement/_components/operationManagement/PublicComponents/TableFieldControlDialog.vue";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDateAll, isMonth,
  isToday, isYear, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default defineComponent({
  name: "PvInverterReport",
  components:{TableFieldControlDialog},
  setup(){
    const that = reactive({
      Search,
      Folder,
      Setting,
      RefreshRight,
      formInline: {
        dateType: 1,  // 时间类型 1-逐日 2-逐月
        dayTimeDate: getDaysFromCurrentTime(),
        yearTimeDate: getDaysFromCurrentTime(0, 2),
        monthTimeDate: getDaysFromCurrentTime(0, 1),
      },
      deviceAllIds: [],
      deviceIdArray: [],
      oldFormInline: {},
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
      tableFieldStorageName: 'PvInverterReport_TableFieldList',
      tableAllFieldList: [
        { name: "组串容量", key: "seriesCapacity", minWidth: 180, unit: "kWp", width: 30 },
        { name: "发电量", key: "generateQt", minWidth: 150, unit: "度", width: 30 },
        { name: "累计发电量", key: "sumGenerateQt", minWidth: 180, unit: "度", width: 30 },
        { name: "等价发电小时", key: "equivGeneHour", minWidth: 230, unit: "kWh/kWp", width: 30 },
        { name: "峰值交流功率", key: "peakAcPower", minWidth: 210, unit: "kW", width: 30 },
        { name: "并网时长", key: "gridHour", minWidth: 150, unit: "h", width: 30 },
        { name: "限电损失电量", key: "rationLossQt", minWidth: 180, unit: "度", width: 30 },
        { name: "离散率", key: "discRate", minWidth: 150, unit: "%", width: 30 }
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

      if(!formInline.deviceIds || !formInline.deviceIds.length){
        if(!that.deviceAllIds || !that.deviceAllIds.length){
          that.list = [];
          that.totalNumber = 0;
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的设备！"});
          return;
        }
        formInline.deviceIds = JSON.parse(JSON.stringify(that.deviceAllIds));
      }

      let size = operateType === "exportTable" ? 0 : that.pageNum;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPvInverterReportList({page: that.currentPage, size , ...formInline}).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        // 导出列表数据
        if(operateType === "exportTable"){
          let tableExportList = [{width: 25, key: "siteName", name: "站点名称"}, {width: 25, key: "deviceName", name: "设备名称"}];
          for (let i = 0;i < that.tableAllFieldList.length;i++){
            let table_name = `${ that.tableAllFieldList[i].name }${ that.tableAllFieldList[i].unit ? '(' + that.tableAllFieldList[i].unit + ')' : '' }`;
            tableExportList.push({ ...that.tableAllFieldList[i],name: table_name });
          }

          exportCustomExcel(tableExportList, returnDataInfo, `光伏_逆变器报表_${ formInline.startTime }~${ formInline.endTime }区间数据 - ${getNowDateAll()}`);
          setTimeout(()=> {that.exportLoading = false;},500);
        } else {
          if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
          that.totalNumber = returnDataInfo.totalSize;
          that.list = returnDataInfo.items;
          that.listLoading = false;
        }
        await nextTick();
        if (multipleTableRef.value) {
          multipleTableRef.value.setScrollTop(0);
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

    const multipleTableRef = ref(null);
    const clickResetForm = (operateType = "resetPage") => {
      if(operateType === "resetPage") multipleTableRef.value.clearSort(); // 清空排序条件
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if(!that.deviceAllIds.length) queryInverterListByUserId();
      if(that.deviceAllIds.length) listArray(operateType);
    };

    // 点击排序获取属性判断    ascending:升序   descending:降序
    const changeTableSort = (row) => {
      // console.log(row);
      that.formInline[row.prop + 'Sort'] = row.order === 'ascending' ? 0 : 1;
      clickResetForm('refresh');
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) that.tableFieldControlVisible = true;
    };

    // 根据登录用户id查询逆变器设备列表
    const queryInverterListByUserId = () => {
      findInverterListByUserId({ timer: new Date() }).then(res => {
        let deviceAllIds = [];
        let list = res.data ? res.data : [];
        for (let i = 0; i < list.length; i++) if(list[i].typeId) deviceAllIds.push(list[i].id); // 设备id

        that.deviceIdArray = setTreeData(list);
        that.deviceAllIds = JSON.parse(JSON.stringify(deviceAllIds));
        if(that.deviceAllIds.length) listArray(); // 有站点id 就获取报表数据
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
      queryInverterListByUserId();
      queryTableFieldListFun();
    });

    return {...toRefs(that), clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, queryInverterListByUserId, listArray, queryTableFieldListFun,
      clickOperateBut, changeTableSort, multipleTableRef};
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .header_label{
    display: inline-block;
  }
}
</style>