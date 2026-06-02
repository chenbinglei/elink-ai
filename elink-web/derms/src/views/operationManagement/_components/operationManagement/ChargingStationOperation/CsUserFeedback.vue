<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="站点名称：">
              <el-select v-model="formInline.siteIds" filterable multiple collapse-tags max-collapse-tags="1" clearable placeholder="请选择站点">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="反馈类型：">
              <el-select v-model="formInline.feedbackType" placeholder="请选择反馈类型">
                <el-option v-for="item in feedbackTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="反馈状态：">
              <el-select v-model="formInline.status" placeholder="请选择反馈状态">
                <el-option v-for="item in statusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="12" :xl="12" :xs="24">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts"
                              end-placeholder="结束时间" format="YYYY-MM-DD" range-separator="~" start-placeholder="开始时间" type="daterange" value-format="YYYY-MM-DD"/>
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
      <TableHeaderTitle title="反馈列表"></TableHeaderTitle>
      <div v-resize="setTableMaxHeight" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="站点名称">
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column label="反馈类型">
            <template #default="{ row }">{{ $filters.feedbackType(row.feedbackType) }}</template>
          </el-table-column>
          <el-table-column label="反馈时间">
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="状态">
            <template #default="{ row }">
              <div class="feedbackStatus" :class="'feedbackStatus' + row.status">
                <span>{{ $filters.feedbackStatus(row.status) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="描述" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.description) }}</template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(2, row)">受理</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div ref="tablePaginationRef" class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>

  </div>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {queryUserAuthorityIsHaveFun} from "@/utils";
import {pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {RefreshRight, Search, Folder} from "@element-plus/icons-vue";
import {queryUserFeedbackList} from "@/api/operationManagement/CsUserFeedback";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {computed, onMounted, reactive, ref, toRefs, defineComponent, getCurrentInstance} from "vue";

export default defineComponent({
  name: "CsUserFeedback",
  setup() {
    const store = useStore();
    const that = reactive({
      Folder,
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},

      siteIdArray: [],
      pickerOptions: pickerOptionsGthanAcTime(),
      statusArray: [{id: 1, name: "待处理"}, {id: 2, name: "处理中"}, {id: 3, name: "已处理"}],
      feedbackTypeArray: [{id: 1, name: "充电过程"}, {id: 2, name: "发票开具"}, {id: 3, name: "占位费"}, {id: 4, name: "信息不符"}, {id: 5, name: "事故"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if(formInline.startAlsoDate){
        formInline.endDate = formInline.startAlsoDate[1];
        formInline.startDate = formInline.startAlsoDate[0];
      }
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryUserFeedbackList({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
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

    const clickOperateBut = (operateType, row) => {

      if(operateType === 1){
        const routeName = "/operationManagement/CsFeedbackDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }

        store.dispatch("updateSecondaryInfo",{ subTitle: "反馈详情", id: row.id, componentName: "CsFeedbackDetails"});
        store.dispatch("updateSecondaryVisible",true);
      }

      if(operateType === 2){

      }
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3", timer: new Date()}).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 74;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
      // listArray();
    });

    return {...toRefs(that), tableContentRef, tablePaginationRef, setTableMaxHeight, querySiteBasicInfoByTenantId, clickResetForm, listArray, clickOperateBut};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .feedbackStatus{
    width: fit-content;
    padding: 0 16px;
    height: 32px;
    box-sizing: border-box;
    color: rgba(255,255,255,0.6);
    font-size: 14px;
    border-radius: 8px;
    background: rgba(7,156,235,0.2);
    line-height: 32px;
  }
  
  .feedbackStatus1{
    color: #FF9C02;
    background: rgba(255, 156, 2, .2);
  }
  .feedbackStatus2{
    color: #41CB4A;
    background: rgba(65, 203, 74, .2);
  }
}
</style>