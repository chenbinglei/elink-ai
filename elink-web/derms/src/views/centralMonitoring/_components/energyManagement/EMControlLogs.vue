<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="站点名称：">
              <el-select v-model="formInline.siteId" clearable filterable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="时间范围：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts"
                              value-format="YYYY-MM-DD" format="YYYY-MM-DD" end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange"/>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div ref="tableContentRef" class="tableContent content_border">
      <TableHeaderTitle title="控制日志"></TableHeaderTitle>
      <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" stripe>
          <el-table-column fixed="left" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column fixed="left" label="站点名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.strategyName) }}</template>
          </el-table-column>
          <el-table-column label="时间">
            <template #default="{ row }">{{ $filters.moreData(row.strategyType) }}</template>
          </el-table-column>
          <el-table-column label="策略" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.explainName) }}</template>
          </el-table-column>
          <el-table-column label="事件" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.strategyType) }}</template>
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
import {pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {RefreshRight, Search} from '@element-plus/icons-vue';
import {reactive, defineComponent, toRefs, onMounted, ref, computed} from "vue";
import {findSiteBasicInfoByTenantId} from "@/api/operationManagement/CsStationManagement";

export default defineComponent({
  name: "EMControlLogs",
  props:{
    routeInfo:{
      type: Object,
      default:()=>{
         return { };
      }
    }
  },
  setup(props) {
    const store = useStore();
    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const that = reactive({
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},

      siteIdArray: [],
      pickerOptions: pickerOptionsGthanAcTime(),

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
    });

    const listArray = (operateType) => {
      // that.listLoading = true;
      // let formInline = JSON.parse(JSON.stringify(that.formInline));
      // if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      // queryAppletUserList({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
      //   that.listLoading = false;
      //   that.list = res.data.items;
      //   that.totalNumber = res.data.totalSize;
      //   if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      // }).catch((error) => {
      //   that.listLoading = false;
      //   if (error && error.code === 88886) return
      //   that.list = [];
      //   that.totalNumber = 0;
      // })
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteBasicInfoByTenantId({tenantId: userInfo.value.tenantId,timer: new Date()}).then(res => {
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
      that.formInline = Object.assign({},props.routeInfo,that.formInline);
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), tableContentRef, tablePaginationRef, userInfo, setTableMaxHeight, clickResetForm, listArray, querySiteBasicInfoByTenantId};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .textClass{
    color: #079CEB;
    font-size: 14px;
    cursor: pointer;
    text-decoration: underline;
  }
}
</style>