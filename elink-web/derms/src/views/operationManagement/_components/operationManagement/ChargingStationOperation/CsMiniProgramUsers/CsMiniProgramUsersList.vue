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
            <el-form-item label="用户分组：">
              <el-select v-model="formInline.groupId" filterable clearable placeholder="全部">
                <el-option v-for="item in groupIdArray" :key="item.id" :label="item.groupName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6">
            <el-form-item label="用户状态：">
              <el-select v-model="formInline.userState" filterable clearable placeholder="全部">
                <el-option v-for="item in userStateArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <TableHeaderTitle title="小程序用户">
        <template #content>
          <el-button :icon="Plus" type="primary" @click="clickOperateBut(1)">添加用户</el-button>
        </template>
      </TableHeaderTitle>
      <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
        <el-table v-loading="listLoading" :data="list" stripe :max-height="tableMaxHeight" ref="tableRef">
          <el-table-column label="序号" type="index" width="80" fixed="left">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="用户昵称" fixed="left">
            <template #default="{ row }">{{ $filters.moreData(row.nickName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="手机号码">
            <template #default="{ row }">{{ $filters.moreData(row.phoneNum) }}</template>
          </el-table-column>
          <el-table-column align="center" label="分组名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.groupName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="状态">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <div class="userState" :class="'userState' + row.userState">{{ $filters.userState(row.userState) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="描述" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.refer) }}</template>
          </el-table-column>
          <el-table-column align="center" label="注册时间">
            <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="table_operate_class flex-jc-ai-center">
                <el-link :underline="false" @click="clickOperateBut(2, row)">详情</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(3, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(4, row)">订单</el-link>
                <span class="split_line">|</span>
                <el-dropdown>
                  <el-button size="small" plain :icon="MoreFilled"></el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="clickOperateBut(5, row)">启用</el-dropdown-item>
                      <el-dropdown-item @click="clickOperateBut(6, row)">冻结</el-dropdown-item>
                      <el-dropdown-item @click="clickOperateBut(7, row)">注销</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <AddUserInfoDialog v-if="addUserInfoVisible" v-model:isVisible="addUserInfoVisible" :titleName="titleName" :activeEditDataInfo="activeEditDataInfo" @changeEvent="listArray('refresh')" />
  </div>
</template>

<script lang="ts">
import { useOperationManagementStore } from '@/stores/index';

import {queryUserAuthorityIsHaveFun} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import AddUserInfoDialog from "./AddUserInfoDialog.vue";
import {RefreshRight, Search, Plus, MoreFilled} from '@element-plus/icons-vue';
import {onMounted, reactive, ref, toRefs, defineComponent, getCurrentInstance, nextTick} from "vue";
import {queryAllUserGroupList, queryAppletUserList, updateAppletUserState} from "@/api/operationManagement/CsMiniProgramUsers";

export default defineComponent({
  name: "CsMiniProgramUsersList",
  components: {AddUserInfoDialog},
  setup() {

    const operationManagementStore = useOperationManagementStore();
    const {emit} = getCurrentInstance();

    const that = reactive({
      Plus,
      Search,
      MoreFilled,
      RefreshRight,
      formInline: {},
      oldFormInline: {},
      groupIdArray: [],
      userStateArray: [{id: 1, name: "正常"}, {id: 2, name: "冻结"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      activeEditId: "",
      titleName: "添加用户",
      activeEditDataInfo: {},
      addUserInfoVisible: false,
    });
    const tableRef = ref(null);
    const listArray = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryAppletUserList({page: that.currentPage, size: that.pageNum, ...formInline}).then(async res => {
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
        that.list = [];
        that.totalNumber = 0;
      });
    };

    const clickOperateBut = (operateType, row) => {
    
      if (operateType === 1) {
        that.titleName = "添加用户";
        that.activeEditDataInfo = {};
        that.addUserInfoVisible = true;
      }

      if (operateType === 2) {
          
        const routeName = "/operationManagement/CsMiniProgramUserDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);

        if (!isAuthority) {
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }

        operationManagementStore.updateSecondaryInfo({
          subTitle: `用户详情-${row.nickName}`,
          id: row.id,
          componentName: "CsMiniProgramUserDetails"
        });
        console.log(operateType, row);
        operationManagementStore.updateSecondaryVisible(true);
        //  router.push(routeName);
      }

      if (operateType === 3) {
        that.activeEditDataInfo = JSON.parse(JSON.stringify(row));
        that.titleName = "编辑用户";
        that.addUserInfoVisible = true;
      }

      if (operateType === 4) {
        const routeName = "/operationManagement/CsChargingRecord";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }

        emit("changEvent", {
          operateType: "switchComponents",
          componentName: "CsChargingRecord",
          keywordType: "2",
          keyword: row.phoneNum
        });
      }

      if (operateType === 5 || operateType === 6 || operateType === 7) {
        let highlightText = "启用";
        if(operateType === 6) highlightText = "冻结";
        if(operateType === 7) highlightText = "注销";
        ElMessageBox.confirm(`确定${ highlightText }用户（<span class="highlightText">${ row.phoneNum }</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', showClose: false,
          closeOnClickModal: false, beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在操作...';
              updateAppletUserState({id: row.id,userState: operateType - 4}).then(() => {
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
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    };

    // 查询全部用户分组列表
    const findAllUserGroupList = () => {
      queryAllUserGroupList({timer: new Date()}).then(res => {
        that.groupIdArray = res.data;
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
      findAllUserGroupList();
      listArray();
    });

    return {...toRefs(that), tableRef, setTableMaxHeight, tableContentRef, tablePaginationRef, clickOperateBut, listArray, findAllUserGroupList, clickResetForm};
  }
});
</script>

<style scoped lang="scss">
.tableContent {
  padding: 12px;
  box-sizing: border-box;

  .userState{
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

  .userState1{
    color: #56E540;
    background: rgba(7, 156, 235,0.2);
  }

  .userState2{
    color: #FAAD14;
    background: rgba(7, 156, 235,0.2);
  }
}
</style>