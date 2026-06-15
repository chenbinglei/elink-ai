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
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="电桩类型：">
              <el-select v-model="formInline.typeId" clearable placeholder="全部">
                <el-option v-for="item in typeIdArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <RunTableHeaderCom :formInline="formInline" :siteAllIds="siteAllIds" />
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table :data="list" stripe border v-loading="listLoading" :max-height="tableMaxHeight" ref="tableRef">
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
          <el-table-column min-width="150">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">充电时长（时）</span>
                <div class="sort_class" @click="clickTableSortFun('chargeDurationSort')">
                  <span v-if="formInline.chargeDurationSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.chargeDuration) }}</template>
          </el-table-column>
          <el-table-column min-width="150">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">V2G时长（时）</span>
                <div class="sort_class" @click="clickTableSortFun('dischargeDurationSort')">
                  <span v-if="formInline.dischargeDurationSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.dischargeDuration) }}</template>
          </el-table-column>
          <el-table-column min-width="160">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">时间利用率（%）</span>
                <div class="sort_class" @click="clickTableSortFun('timeUtilizeSort')">
                  <span v-if="formInline.timeUtilizeSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.timeUtilize) }}</template>
          </el-table-column>
          <el-table-column min-width="160">
            <template #header>
              <div class="flex-ai-center">
                <span class="header_text">日均充电量（度）</span>
                <div class="sort_class" @click="clickTableSortFun('dayAvgQtSort')">
                  <span v-if="formInline.dayAvgQtSort" class="iconfont icon-paixu-daoxu"></span>
                  <span v-else class="iconfont icon-paixu-zhengxu"></span>
                </div>
              </div>
            </template>
            <template #default="{ row }">{{ $filters.moreData(row.dayAvgQt) }}</template>
          </el-table-column>
          <el-table-column label="在线时长" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.onlineDuration) }}</template>
          </el-table-column>
          <el-table-column label="设备在线率" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.onlineRate) }}</template>
          </el-table-column>
          <el-table-column label="告警次数" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.alarmNum) }}</template>
          </el-table-column>
          <el-table-column label="启动失败次数" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.startFailNum) }}</template>
          </el-table-column>
          <el-table-column label="异常订单数量" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.abnormalOrderNum) }}</template>
          </el-table-column>
          <el-table-column label="订单异常率" min-width="150">
            <template #default="{ row }">{{ $filters.moreData(row.abnormalOrderRate) }}</template>
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
import {ElMessage} from "element-plus";
import {pile_type_array} from "@/utils/setVariate";
import RunTableHeaderCom from "./RunTableHeaderCom.vue";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {reactive, toRefs, defineComponent, onMounted, ref, nextTick} from "vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {getDaysFromCurrentTime, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {findPileRunReport} from "@/api/operationManagement/CsDataReportManage";

export default defineComponent({
  name: "CsPileRunReport",
  components:{RunTableHeaderCom},
  setup() {
    const tableRef = ref(null);

    const that = reactive({
      Search,
      RefreshRight,
      siteAllIds: [],
      oldFormInline: {},
      formInline: {
        areaType: 1,
        dayAvgQtSort: 0, // 日均充电量排序 0-升序 1-降序
        timeUtilizeSort: 0, // 时间利用率排序 0-升序 1-降序
        chargeDurationSort: 0, // 充电时长排序 0-升序 1-降序
        dischargeDurationSort: 0, // V2G时长排序 0-升序 1-降序
        startAlsoDate: [getDaysFromCurrentTime(-31),getDaysFromCurrentTime(-1)]
      },

      siteIdArray: [],
      typeIdArray: pile_type_array,
      pickerOptions: pickerOptionsGthanAcTime(1),

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
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
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        formInline.siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findPileRunReport({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        that.totalNumber = returnDataInfo.totalSize;
        tableContentRef.value.scrollTop = 0;
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
        that.list = returnDataInfo.items;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    // 点击表格头部筛选
    const clickTableSortFun = (fieldName)=>{
      let activeValue = that.formInline[fieldName];
      that.formInline[fieldName] = activeValue ? 0 : 1;
      listArray('refresh');
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

    // 计算出表格最大高度
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(()=>{
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), tableRef, querySiteBasicInfoByTenantId, clickResetForm, listArray, tableContentRef, clickTableSortFun, setTableMaxHeight, tablePaginationRef};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .sort_class{
    cursor: pointer;
    display: flex;
    align-items: center;

    .iconfont{
      color: #007FEB;
    }
  }
}
</style>