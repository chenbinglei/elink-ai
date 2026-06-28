<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <HandleMenus :isShowHeader="false" :handleMenuArray="handleMenuArray" :handleMenuTopArray="handleMenuTopArray" showTopSelect @handleMenuEvent="handleMenuEvent"/>
    <div class="app-container-right">
      <div class="header-form" ref="headerFormRef">
        <el-form :model="formInline" inline>
          <el-form-item>
            <el-input class="selectAndInput" v-model="formInline.keyword" clearable placeholder="请输入关键词">
              <template #prefix>
                <el-select v-model="formInline.keywordType" clearable placeholder="请选择">
                  <el-option v-for="item in keywordTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="角色：">
            <el-select v-model="formInline.userRole" placeholder="请选择">
              <el-option v-for="item in userRoleArray" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="formInline.userState" placeholder="请选择">
              <el-option v-for="item in userStateArray" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" :icon="Search" @click="listArray('resetPage')">查询</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="paginationFunction({ type: 'AddUser' })">新增用户</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent" v-loading="listLoading">
        <div class="tableCenter" ref="tableCenterRef" >
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="用户名">
              <template #default="{ row }">{{ $filters.moreData(row.fullName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="用户组" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.groupName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="组织" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.organName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="角色">
              <template #default="{ row }">{{ $filters.userRole(row.userRole)}}</template>
            </el-table-column>
            <el-table-column align="center" label="电话">
              <template #default="{ row }">{{ $filters.moreData(row.phone)}}</template>
            </el-table-column>
            <el-table-column align="center" label="状态">
              <template #default="{ row }">
                <template v-if="row.isDefaultAdmin !== 1">
                  <el-switch v-model="row.userState" :active-value="1" :inactive-value="0" @change="userStateFun(row)"/>
                </template>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="300px">
              <template #default="{ row }">
                <template v-if="row.isDefaultAdmin !== 1">
                  <el-link :underline="false" @click="clickOperate(1, row)">修改用户组</el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" @click="clickOperate(2, row)">修改组织</el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" @click="clickOperate(3, row)">编辑</el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" type="danger" @click="clickOperate(4, row)">删除</el-link>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" :paginationButArray="paginationButArray"
                      @pageChange="listArray"  @paginationFunction="paginationFunction" />
        </div>
      </div>
    </div>

    <!--    新增编辑用户-->
    <AddUserDialog v-if="addUserVisible" v-model:isVisible="addUserVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="changeEvent" />

    <!--    添加修改至用户组-->
    <AddToUserGroup v-if="addToUserGroupVisible" v-model:isVisible="addToUserGroupVisible" :titleName="titleName" :userIdsArray="userIdsArray"
                    :formDialog="formDialog" @changeEvent="changeEvent"></AddToUserGroup>

    <!--    添加修改至组织-->
    <AddToOrganDialog v-if="addToOrganVisible" v-model:isVisible="addToOrganVisible" :titleName="titleName" :userIdsArray="userIdsArray" :formDialog="formDialog" @changeEvent="changeEvent"></AddToOrganDialog>
  </div>
</template>
<script lang="ts">

import pinyin from "js-pinyin";
import {ElMessage, ElMessageBox} from "element-plus";
import {computed, onActivated, reactive, ref, toRefs} from "vue";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {getDataListFun, nullToDelete, operateButtonIsClick, setTreeData} from "@/utils";
import {findOrganStructureListByTenantId} from "@/api/systemManagement/userGroupManagement";
import {AddToOrganDialog, AddToUserGroup, AddUserDialog} from "@/views/systemManagement/component";
import {deleteUserInfoById, findUserGroupListById, findUserListByPage, updateUserStateById} from "@/api/systemManagement/userManagement";

export default {
  name: "userManagement",
  components:{AddUserDialog, AddToUserGroup, AddToOrganDialog},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {
    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/systemManage/saveOrUpdateUserInfo')
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

      formDialog: {},
      titleName: "新增用户",
      // firstOrgan: true,
      firstUser: true,
      addUserVisible: false,
      userIdsArray: [], // 用户数组
      isShowSelectUsers: false,
      addToUserGroupVisible: false,
      addToOrganVisible: false,

      handleMenuArray: [],
      allHandleMenuArray: [],
      keywordTypeArray:[{id: 1,name:"用户名"},{id: 2,name:"用户ID"}],

      groupType: "", // 用户组类型 1-未分组 2-已分组
      organId: "", // 组织架构id
      tabIndex: 1,
      handleMenuTopArray: [{name: "用户组", id: 1}, {name: "组织架构", id: 2}],

      userStateArray: [{name: "关闭", id: 0}, {name: "开启", id: 1}],
      userRoleArray: [{name: "管理员", id: 1}, {name: "普通用户", id: 2}],
      paginationButArray: [
        {buttonName: "添加至用户组", buttonType: "AddUserGroups", buttonIcon: "EditPen"},
        {buttonName: "添加至组织", buttonType: "AddOrganization", buttonIcon: "EditPen"},
      ],
    })

    // 查询租户下用户组列表
    const getUserGroupList = () => {
      findUserGroupListById({}).then(res => {
        let resData = res.data ? res.data : [];
        resData.forEach(item => {
          if(!item.id) item.id = pinyin.getFullChars(item.groupName);
          item.name = item.groupName + '(' + item.peopleNumber + ')'
        });
        that.handleMenuArray = JSON.parse(JSON.stringify(resData));
        that.allHandleMenuArray = JSON.parse(JSON.stringify(resData));
      })
    }

    // 根据租户id查询组织架构信息列表
    const getOrganStructureList = () => {
      findOrganStructureListByTenantId({}).then( res=> {
        let resData = res.data ? res.data : [];
        resData.forEach(item => {
          item.name = item.organName;
          item.iconName = !item.parentId || item.parentId === "0" ? "icon-company" : "icon-organization";
        });
        that.handleMenuArray = setTreeData(resData);
        that.allHandleMenuArray = JSON.parse(JSON.stringify(resData));
      }).catch((err)=>{
        console.log(err)
      })
    }

    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findUserListByPage({page: that.currentPage, size: that.pageNum, ...that.formInline, groupId: that.groupId, groupType: that.groupType, organId: that.organId}).then(res => {
        that.list = res.data.items;
        if(that.firstUser){
          that.userIdsArray = that.list && that.list.length ? getDataListFun(that.list,true,false,"fullName") : [];
          that.firstUser = false;
        }
        that.totalNumber = res.data.totalSize;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);

      //左侧菜单打开
      if (menuButDate.menuType === "searchInput") {
        that.tabIndex = menuButDate.type;
        if(that.tabIndex === 1)getUserGroupList();
        if(that.tabIndex === 2)getOrganStructureList();
      }

      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {

        // 用户组
        if(that.tabIndex === 1){
          // 全部人员，置空       1-未分组 2-已分组
          let groupType = 2;
          if(menuButDate.id === pinyin.getFullChars(menuButDate.groupName)){
            groupType = menuButDate.id === "WeiFenZu" ? 1 : "";
          }
          that.organId = "";
          that.groupType = groupType;
          that.groupId = menuButDate.id !== pinyin.getFullChars(menuButDate.groupName) ? menuButDate.id : "";
        }

        // 组织架构
        if(that.tabIndex === 2){
          that.organId = menuButDate.id;
          that.groupType = "";
          that.groupId = "";
        }

        listArray();
      }
    }

    // 操作
    const clickOperate = (index,row) => {
      let formDialog = {};
      for(let key in row)row[key] = nullToDelete(row[key]);
      if(index === 1) {
        that.addToUserGroupVisible = true;
        that.titleName = "修改所属用户组";
        if(row.groupId){
          let groupId = row.groupId.toString().split(",");
          if(!groupId[groupId.length - 1])groupId.splice(groupId.length - 1,1);
          formDialog.groupIds = groupId;
        }
        formDialog.userIds = row.id;
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
      }

      if(index === 2){
        that.addToOrganVisible = true;
        that.titleName = "修改所属组织";
        formDialog.organId = row.organId;
        formDialog.userIds = row.id;
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
      }

      if(index === 3){
        that.titleName = "编辑用户";
        that.addUserVisible = true;
        formDialog = row;
        formDialog.repeatPassword = row.password;
        if(row.groupId){
          let groupId = row.groupId.toString().split(",");
          if(!groupId[groupId.length - 1])groupId.splice(groupId.length - 1,1);
          formDialog.groupId = groupId;
        }
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
      }

      if(index === 4){
        ElMessageBox.confirm(`您确定要删除用户（${row.fullName}）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true,closeOnClickModal: false,
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteUserInfoById({id: row.id}).then(() => {
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

    const paginationFunction = (data) => {
      that.formDialog = {};

      if(data.type === "AddUserGroups"){
        that.addToUserGroupVisible = true;
        that.isShowSelectUsers = false;
        that.titleName = "添加至用户组";
      }

      if(data.type === "AddOrganization"){
        that.addToOrganVisible = true;
        that.titleName = "添加至组织";
      }

      if(data.type === "AddUser"){
        that.titleName = "新增用户";
        that.addUserVisible = true;
        that.formDialog = { userState: 1 };
      }
    }

    // 更新用户状态
    const userStateFun = (row) => {
      ElMessageBox.confirm(`确定更新用户状态吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            updateUserStateById({ id: row.id, userState: row.userState }).then(res => {
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              row.userState = row.userState ? 0 : 1;
              instance.confirmButtonLoading = false;
            })
          } else {
            done();
          }
        }
      }).then(() => {
        listArray();
        ElMessage({ type:"success",showClose: true,message:"更新成功" });
      }).catch(() => {
        console.log("取消！");
      });
    }

    const changeEvent = () => {
      that.tabIndex === 1 ? getUserGroupList() : getOrganStructureList();
      listArray();
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onActivated(() => {
      that.tabIndex === 1 ? getUserGroupList() : getOrganStructureList();
    })

    return {...toRefs(that), paginationFunction, headerFormRef, listArray,tableCenterRef,setTableMaxHeight, clickOperate, handleMenuEvent, userStateFun, getUserGroupList,
      changeEvent, getOrganStructureList,isAddButtonClick}
  }

}
</script>
<style scoped lang="scss">
.header-form {
  :deep(.el-select) {
    min-width: 160px;
    .el-select__wrapper {
      width: 180px;
      min-height: 32px;
    }
    .el-select__selected-item.el-select__placeholder {
      z-index: 1 !important;
      position: absolute !important;
      opacity: 1 !important;
      &.is-transparent { opacity: 1 !important; }
      span { color: #a8abb2 !important; font-size: 14px !important; }
    }
  }
}
</style>
