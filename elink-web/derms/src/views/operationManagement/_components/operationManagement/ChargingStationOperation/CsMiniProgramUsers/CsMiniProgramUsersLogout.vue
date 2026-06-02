<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="手机号码：">
              <el-input v-model="formInline.phoneNum" clearable placeholder="请输入手机号码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="用户状态：">
              <el-select v-model="formInline.applyState" filterable placeholder="全部">
                <el-option v-for="item in applyStateArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <TableHeaderTitle title="小程序用户"></TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="用户ID" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.appletUserId) }}</template>
          </el-table-column>
          <el-table-column align="center" label="手机号码">
            <template #default="{ row }">{{ $filters.moreData(row.phoneNum) }}</template>
          </el-table-column>
          <el-table-column align="center" label="小程序名称">
            <template #default="{ row }">{{ $filters.moreData(row.appletName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="分组名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.groupName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="状态">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <div class="applyState" :class="'applyState' + row.applyState">{{ $filters.applyState(row.applyState) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="申请时间">
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class flex-jc-ai-center">
                <el-link :underline="false" @click="clickOperateBut(1, row)">注销</el-link>
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

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {RefreshRight, Search, Plus} from '@element-plus/icons-vue';
import {onMounted, reactive, ref, toRefs, defineComponent, nextTick} from "vue";
import {
  cancelAppletUser,
  findAppletCancelListByPage,
  updateAppletUserState
} from "@/api/operationManagement/CsMiniProgramUsers";

export default defineComponent({
  name: "CsMiniProgramUsersLogout",
  setup() {
    const tableRef = ref(null);

    const that = reactive({
      Plus,
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      applyStateArray: [{id: 1, name: "申请注销"}, {id: 2, name: "已注销"}],

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
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findAppletCancelListByPage({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.list = [];
      });
    };

    const clickOperateBut = (operateType, row) => {

      if (operateType === 1) {
        ElMessageBox.confirm(`确定注销用户（<span class="highlightText">${ row.phoneNum }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', showClose: false,
          closeOnClickModal: false, beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在操作...';
              cancelAppletUser({id: row.id }).then(() => {
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          listArray("refresh");
          ElMessage({type: "success", showClose: true, message: "注销成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
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

    return {...toRefs(that), tableRef, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, listArray, clickResetForm};
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .applyState{
    width: fit-content;
    padding: 0 16px;
    height: 28px;
    border-radius: 6px;
    line-height: 28px;
    text-align: center;
    box-sizing: border-box;

    font-size: 14px;
    color: rgba(255,255,255,.6);
    background: rgba(7, 156, 235,0.2);
  }

  .applyState1{
    color: #FAAD14;
    background: rgba(7, 156, 235,0.2);
  }
}
</style>