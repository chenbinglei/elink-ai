<template>
  <div class="app-container" v-resize="setTableMaxHeight">

    <div class="app-container-right">

      <div class="header-form" ref="headerFormRef">
        <div class="app-container-top-card">
          <model-class-card :cardInfo="cardInfo" :pageType="pageType" @changeEvent="changeEvent"></model-class-card>
        </div>

        <div class="app-container-right_top">
          <div class="c_right_t_left">
            <span class="text" style="margin-right: 12px">子类列表</span>
            <el-button :icon="Refresh" class="blackFontButtons" @click="listArray('resetPage')">刷新</el-button>
          </div>
          <div class="c_right_t_right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加分类</el-button>
          </div>
        </div>
      </div>

      <div class="tableContent" v-loading="listLoading">
        <div class="tableCenter" ref="tableCenterRef" v-resize="setTableMaxHeight">

          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
            <el-table-column align="center" label="序号" type="index" width="80">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column align="center" label="分类名称">
              <template #default="{ row }">{{ $filters.moreData(row.sortName)}}</template>
            </el-table-column>
            <el-table-column align="center" label="分类ID">
              <template #default="{ row }">{{ $filters.moreData(row.id)}}</template>
            </el-table-column>
            <el-table-column align="center" label="关联模型数量">
              <template #default="{ row }">{{ $filters.numberNull(row.modelNum) }}个</template>
            </el-table-column>
            <el-table-column align="center" label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateBut(1, row)">查看</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(2, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperateBut(3, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>

        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>

    </div>

    <AddModelClassDialog v-if="addModelClassVisible" v-model:isVisible="addModelClassVisible" :titleName="titleName" :formDialog="formDialog"
                         @changeEvent="listArray('resetPage')"></AddModelClassDialog>
  </div>
</template>

<script>
import { useTagsViewStore } from '@/stores/index';

import {useRoute,useRouter} from "vue-router";
import {queryUserAuthorityIsHaveFun} from "@/utils";
import {onMounted, reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Delete, Refresh} from "@element-plus/icons-vue";
import {ModelClassCard,AddModelClassDialog} from "@/views/modelCenter/component";
import {deleteSortById, querySortList} from "@/api/modelCenter/modelClassification";

export default {
  name: "modelClassDetails",
  components:{ModelClassCard,AddModelClassDialog},
  setup(props){

    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      CirclePlus, Delete,Refresh,

      pageType: 2,
      cardInfo: {},  // 当前父级模型信息
      activeModelId: route.query.id, // 当前父级模型id

      formDialog: {},
      titleName: "创建场景",
      addModelClassVisible: false,
    })

    // 查询模型分类树结构列表
    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      querySortList({ id: that.activeModelId, page: that.currentPage, size: that.pageNum }).then(res=>{
        that.listLoading = false;
        that.list = res.data.childPage.items;
        that.totalNumber = res.data.childPage.totalSize;

        const { id,modelNum,parentId,sortLogo,sortName } = res.data;
        that.cardInfo = { id,modelNum,parentId,sortLogo,sortName };
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = () => {
      that.titleName = "创建分类";
      that.formDialog = { parentId: that.activeModelId };
      that.addModelClassVisible = true;
    }

    const changeEvent = (data)=>{

      if(data.operate === 2){
        that.titleName = "编辑分类";
        that.formDialog = data.cardInfo;
        that.addModelClassVisible = true;
      }

      if(data.operate === 3)listArray("resetPage");
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        const routeName = "/modelCenter/modelClassDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        vueRouter.push({ path: routeName, query: { id: row.id,subTitle: row.sortName} });
        tagsViewStore.addBackButViews({ id: row.id,backRouteName: route.path,showButRoute: routeName });
      }

      if(operate === 2){
        that.formDialog = row;
        that.titleName = "编辑分类";
        that.addModelClassVisible = true;
      }

      if(operate === 3){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.sortName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSortById({ id: row.id }).then(()=>{
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
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight;
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray, clickAddBut, changeEvent,tableCenterRef,setTableMaxHeight,clickOperateBut, headerFormRef }
  }
}
</script>

<style scoped lang="scss">
.app-container{
  flex-direction: column;

  :deep(.modelClassCard){
    border: none;
    margin-bottom: 0;

    .content_list_left {
      width: 98px;
      height: 98px;
    }

    .content_list_right {
      padding: 4px 0 4px 24px;
    }
  }

  .app-container-right_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px 12px 24px;
    box-sizing: border-box;
  }
}
</style>
