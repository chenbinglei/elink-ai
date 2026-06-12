<template>
  <div class="app-container">

    <div class="app-container-right">

      <div class="header-form" ref="headerFormRef">
        <el-form inline :model="formInline">
          <el-form-item>
            <el-input class="selectAndInput" v-model="formInline.keyword" clearable placeholder="请输入关键词">
              <template #prefix>
                <el-select v-model="formInline.keywordType" clearable placeholder="请选择">
                  <el-option v-for="item in keywordTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" @click="listArray('resetPage')">查询</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">新增租户</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent">
        <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="序号" type="index" width="80">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column align="center" label="租户名称">
              <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="租户ID" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.id)}}</template>
            </el-table-column>
            <el-table-column align="center" label="管理员账号">
              <template #default="{ row }">{{ $filters.moreData(row.superAccount)}}</template>
            </el-table-column>
            <el-table-column align="center" label="创建时间">
              <template #default="{ row }">{{ $filters.moreData(row.createTime)}}</template>
            </el-table-column>
            <el-table-column align="center" label="状态">
              <template #default="{ row }">
                <el-switch v-model="row.tenantState" :active-value="1" :inactive-value="0" @change="tenantStateFun(row)"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperate(1, row)">查看</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperate(2, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperate(3, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <!--    新增租户-->
    <AddTenantDialog v-if="addTenantVisible" v-model:isVisible="addTenantVisible" :titleName="titleName" :formDialog="formDialog" @changeEvent="listArray('resetPage')"></AddTenantDialog>
  </div>
</template>

<script>
import { useTagsViewStore } from '@/stores/index';

import {useRoute, useRouter} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Delete} from "@element-plus/icons-vue";
import {computed, onActivated, reactive, ref, toRefs} from "vue";
import {AddTenantDialog} from "@/views/tenantManagement/component";
import {operateButtonIsClick, queryUserAuthorityIsHaveFun} from "@/utils";
import {deleteTenantInfoById, findTenantInfoByPage, updateTenantStateById} from "@/api/tenantManagement/tenantTabulation";

export default {
  name: "tenantTabulation",
  components: { AddTenantDialog },
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {
    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/tenantManage/saveOrUpdateTenantInfo')
    })

    const that = reactive({
      CirclePlus, Delete,
      formInline: { keywordType: 1 },
      keywordTypeArray:[{id: 1,name:"租户名称"},{id: 2,name:"租户ID"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      formDialog: {},
      titleName: "新增租户",
      addTenantVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      findTenantInfoByPage({ page: that.currentPage, size: that.pageNum, ...that.formInline}).then(res => {
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
        that.totalNumber = 0;
      })
    }

    const clickAddBut = ()=>{
      that.titleName = "新增租户";
      that.addTenantVisible = true;
      that.formDialog = {};
    }

    const clickOperate = (index,row) => {
      if(index === 1){
        const routeName = "/tenantManagement/tenantDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        vueRouter.push({ path: routeName, query: { id: row.id, subTitle: row.tenantName } });
        tagsViewStore.addBackButViews({ id: row.id,backRouteName: route.path,showButRoute: routeName });
      }

      if(index === 2){
        that.titleName = "编辑租户";
        that.addTenantVisible = true;
        that.formDialog = JSON.parse(JSON.stringify(row));
        that.formDialog.repeatPassword = row.password;
      }

      if(index === 3){
        ElMessageBox.confirm(`您确定要删除（${row.tenantName}）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteTenantInfoById({ id: row.id }).then(()=> {
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
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });

      }
    }

    // 更新租户状态
    const tenantStateFun = (row) => {
      ElMessageBox.confirm(`确定更新租户状态吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            updateTenantStateById({ id: row.id, tenantState: row.tenantState }).then(() => {
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              instance.confirmButtonLoading = false;
              row.tenantState = row.tenantState ? 0 : 1;
            });
        } else {
          done();
        }
      }
      }).then(() => {
        listArray();
        ElMessage({ type:"success",showClose: true,message:"更新成功" });
      }).catch(() => {
        row.tenantState = row.tenantState ? 0 : 1
        // console.log(error);
      });
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

    return {...toRefs(that), isAddButtonClick, listArray, headerFormRef, tableCenterRef, setTableMaxHeight,clickAddBut, clickOperate, tenantStateFun,
    }
  }
}
</script>

<style scoped>

</style>
