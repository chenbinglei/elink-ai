<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="桩编号：">
              <el-input v-model="formInline.pileCode" clearable placeholder="请输入桩编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="枪编号：">
              <el-input v-model="formInline.gunCode" clearable placeholder="请输入枪编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="类型：">
              <el-select v-model="formInline.siteId" filterable clearable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="协议：">
              <el-select v-model="formInline.area" clearable placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :xl="12">
            <el-form-item label="开始时间：">
              <el-date-picker v-model="formInline.startAlsoDate" :disabled-date="pickerOptions.disabledDate" :shortcuts="pickerOptions.shortcuts" clearable
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

    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="日志记录"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column label="桩编号" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.siteName)}}</template>
          </el-table-column>
          <el-table-column label="枪编号">
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="类型">
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="命令">
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="时间" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="客户端地址" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="服务端地址" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="协议">
            <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class">
                <el-link :underline="false" @click="clickOperateBut(1, row)">详情</el-link>
              </div>
            </template>
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
import {ElMessage, ElMessageBox} from "element-plus";
import {pickerOptionsGthanAcTime} from "@/utils/dateTime";
import {RefreshRight,Search} from '@element-plus/icons-vue';
import {computed, onMounted, reactive, ref, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "CsElectricPileLog",
  setup(){

    const that = reactive({
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
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
      // querySiteAccountList({page: that.currentPage, size: that.pageNum, ...formInline}).then(res => {
      //   that.listLoading = false;
      //   that.list = res.data.items;
      //   that.totalNumber = res.data.totalSize;
      //   if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      // }).catch((error) => {
      //   that.listLoading = false;
      //   if (error && error.code === 88886) return
      //   that.totalNumber = 0;
      //   that.list = [];
      // })
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    const clickOperateBut = (operateType, row) => {
      if(operateType === 1){
        that.activeSiteId = row.siteId;
        that.messageParsingVisible = true;
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 32;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      listArray();
    });

    return {...toRefs(that), clickResetForm, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, listArray};
  }
});
</script>

<style scoped lang="scss">
.tableContent{
  padding: 12px;
  box-sizing: border-box;
}
</style>