<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <div class="app-container-right">

      <div class="header-form" ref="headerFormRef">
        <el-form :model="formInline" inline>
          <el-form-item label="关键字：">
            <el-input v-model="formInline.groupName" class="input-with-select" clearable placeholder="请输入用户组名称">
              <template #append>
                <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">新增用户组</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent" v-loading="listLoading">
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="用户组名称">
              <template #default="{ row }">{{ $filters.moreData(row.groupName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="描述">
              <template #default="{ row }">{{ $filters.moreData(row.refer)}}</template>
            </el-table-column>
            <el-table-column align="center" label="人员数">
              <template #default="{ row }"><span style="color: #1F74E2">{{ $filters.moreData(row.peopleNumber)}}</span></template>
            </el-table-column>
            <el-table-column align="center" label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperate(1, row)">管理人员</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperate(2, row)">权限配置</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperate(3, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperate(4, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <!--    添加编辑用户组-->
    <AddUserGroup v-if="addUserGroupVisible" v-model:isVisible="addUserGroupVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="listArray('resetPage')"></AddUserGroup>

    <!--    管理人员-->
    <ManagerDialog v-if="managerVisible" v-model:isVisible="managerVisible" :groupId="groupId" :userDtoList="userDtoList" @changeEvent="listArray('resetPage')"></ManagerDialog>

    <!--    权限配置-->
    <PermissionConfig v-if="permissionConfigVisible" v-model:isVisible="permissionConfigVisible" :groupId="groupId" @changeEvent="listArray('resetPage')"></PermissionConfig>

  </div>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {computed, onActivated, reactive, ref, toRefs} from "vue"
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {AddUserGroup, ManagerDialog, PermissionConfig} from "@/views/systemManagement/component";
import {deleteUserGroupById, findUserGroupListByPage} from "@/api/systemManagement/userGroupManagement";
import {operateButtonIsClick} from "@/utils";

export default {
  name: "userGroupManagement",
  components:{AddUserGroup, ManagerDialog, PermissionConfig},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {
    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/systemManage/saveOrUpdateUserGroup')
    })

    const that = reactive({
      formInline: {},

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      CirclePlus, Delete, Refresh, Search,

      groupId: "", // 用户组id
      userDtoList: [], // 用户数组

      formDialog: {},
      titleName: "新增用户组",
      addUserGroupVisible: false,

      managerVisible: false,
      permissionConfigVisible: false,
    })

    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findUserGroupListByPage({ page: that.currentPage, size: that.pageNum, ...that.formInline }).then(res => {
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        that.listLoading = false;
      }).catch(err => {
        that.listLoading = false;
      })
    }

    // 操作
    const clickOperate = (index,row) => {
      if(index === 1){
        that.managerVisible = true;
        that.groupId = row.id;
        that.userDtoList = row.userDtoList && row.userDtoList.length ? row.userDtoList : [];
      }

      if(index === 2){
        that.permissionConfigVisible = true;
        that.groupId = row.id;
      }

      if(index === 3){
        that.titleName = "编辑用户组";
        that.addUserGroupVisible = true;
        that.formDialog = row;
      }

      if(index === 4){
        ElMessageBox.confirm(`您确定要删除用户组（${row.groupName}）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true,closeOnClickModal: false,
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteUserGroupById({groupId: row.id}).then(() => {
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
          listArray("resetPage");
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    const clickAddBut = () => {
      that.formDialog = {};
      that.titleName = "新增用户组";
      that.addUserGroupVisible = true;
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onActivated(() => {
      listArray();
    })

    return {...toRefs(that), clickAddBut, listArray,tableCenterRef,setTableMaxHeight, clickOperate, headerFormRef, isAddButtonClick }
  }
}
</script>

<style scoped>

</style>
