<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline @submit.native.prevent>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="分组名称：">
              <el-input v-model="formInline.groupName" clearable placeholder="请输入关键字"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="listArray('refresh')">查询</el-button>
              <!--              <el-button class="blackFontButtons" :icon="RefreshRight" @click="clickResetForm">重置</el-button>-->
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="tableContent content_border" ref="tableContentRef">
      <TableHeaderTitle title="分组列表">
        <template #content>
          <el-button :icon="Plus" type="primary" @click="clickOperateBut(1)">添加用户组</el-button>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="分组名称" fixed="left" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.groupName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="用户数量">
            <template #default="{ row }">{{ $filters.moreData(row.userNum) }}</template>
          </el-table-column>
          <el-table-column align="center" label="应用站点">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <el-popover trigger="click" placement="right-end" :width="400">
                  <template #reference>
                    <div class="applySiteNum">{{ $filters.moreData(row.applySiteNum) }}</div>
                  </template>
                  <el-table :data="row.siteDetailList" show-overflow-tooltip :max-height="480">
                    <el-table-column align="center" label="站点ID">
                      <template #default="{ row }">{{ $filters.moreData(row.id) }}</template>
                    </el-table-column>
                    <el-table-column align="center" label="站点名称" show-overflow-tooltip>
                      <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
                    </el-table-column>
                  </el-table>
                </el-popover>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="充电优惠策略" align="center" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="flex-jc-ai-center fd-column">
                <div class="content_list flex">
                  <div class="title">电费折扣：</div>
                  <span class="number">{{ $filters.moreData(row.elecDiscount) }}</span>
                  <span class="unit">%</span>
                </div>
                <div class="content_list flex">
                  <div class="title">服务费折扣：</div>
                  <span class="number">{{ $filters.moreData(row.serviceDiscount) }}</span>
                  <span class="unit">%</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="创建信息" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ $filters.moreData(row.createUserName) }}</span>
              <span>，</span>
              <span>{{ $filters.moreData(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="210" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class flex-jc-ai-center">
                <el-link :underline="false" @click="clickOperateBut(2, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(3, row)">管理用户</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(4, row)">删除</el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage"
          @pageChange="listArray" />
      </div>
    </div>

    <ManagingUsersDialog v-if="managingUsersVisible" v-model:isVisible="managingUsersVisible"
      :activeEditId="activeEditId" @changeEvent="listArray('refresh')" />
    <AddUserGroupingDialog v-if="addUserGroupingVisible" v-model:isVisible="addUserGroupingVisible"
      :titleName="titleName" :activeEditDataInfo="activeEditDataInfo" @changeEvent="listArray('refresh')" />
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from "element-plus";
import { RefreshRight, Search, Plus } from '@element-plus/icons-vue';
import { onMounted, reactive, ref, toRefs, defineComponent, nextTick } from "vue";
import ManagingUsersDialog from "./CsUserGrouping/ManagingUsersDialog.vue";
import AddUserGroupingDialog from "./CsUserGrouping/AddUserGroupingDialog.vue";
import { deleteUserGroupById, queryUserGroupList } from "@/api/operationManagement/CsUserGrouping";

export default defineComponent({
  name: "CsUserGrouping",
  components: { AddUserGroupingDialog, ManagingUsersDialog },
  setup () {
    const tableRef = ref(null);

    const that = reactive({
      Plus,
      Search,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activeEditId: "",
      titleName: "添加用户组",
      activeEditDataInfo: {},
      managingUsersVisible: false,
      addUserGroupingVisible: false,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryUserGroupList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(async res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({ type: "success", message: "重置成功", showClose: true });
        await nextTick();
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.list = [];
        that.totalNumber = 0;
      });
    };

    const clickOperateBut = (operateType, row) => {
      if (operateType === 1) {
        that.titleName = "添加用户组";
        that.activeEditDataInfo = {};
        that.addUserGroupingVisible = true;
      }

      if (operateType === 2) {
        that.activeEditDataInfo = JSON.parse(JSON.stringify(row));
        that.titleName = "编辑用户组";
        that.addUserGroupingVisible = true;
      }

      if (operateType === 3) {
        that.activeEditId = row.id;
        that.managingUsersVisible = true;
      }

      if (operateType === 4) {
        ElMessageBox.confirm(`确定删除用户组（<span class="deleteName">${row.groupName}</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteUserGroupById({ id: row.id }).then(() => {
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
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const tablePaginationRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight - 78;
      that.tableMaxHeight = tableContentHeight - tablePaginationRef.value.offsetHeight;
    };

    onMounted(() => {
      listArray();
    });

    return { ...toRefs(that), tableRef, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, listArray };
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .applySiteNum {
    width: 120px;
    padding: 0 12px;
    height: 32px;
    color: #FFFFFF;
    font-size: 14px;
    border-radius: 8px;
    background: #053047;
    line-height: 32px;
    text-align: center;
    box-sizing: border-box;
    cursor: pointer;
  }

  .content_list {
    .title {
      min-width: 90px;
      text-align: right;
    }

    .number {
      color: var(--el-color-primary);
      margin-right: 2px;
    }
  }
}
</style>