<template>
  <div class="app-container">

    <div class="app-container-right">
      <div class="app-container-right_top">
        <div class="c_right_t_left">
          <span class="text" style="margin-right: 12px">应用场景</span>
          <el-button :icon="Refresh" class="blackFontButtons" @click="listArray('resetPage')">刷新</el-button>
        </div>
        <div class="c_right_t_right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">添加场景</el-button>
        </div>
      </div>
      <div class="tableContent" v-loading="listLoading">
        <div class="tableCenter" ref="tableCenterRef">
          <template v-if="list && list.length">
            <el-row :gutter="12" style="width: 100%;">
              <template v-for="(item,index) in list" :key="index">
                <el-col :lg="6" :md="12" :sm="24">
                  <model-class-card @click="clickItemButton(item)" :cardInfo="item" @changeEvent="changeEvent" class="pointer"></model-class-card>
                </el-col>
              </template>
            </el-row>
          </template>
          <template v-else><null-data></null-data></template>
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
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {useRoute, useRouter} from "vue-router";
import {computed, onMounted, reactive, toRefs} from "vue"
import {CirclePlus, Delete, Refresh} from '@element-plus/icons-vue'
import { querySortList } from "@/api/modelCenter/modelClassification"
import {operateButtonIsClick, queryUserAuthorityIsHaveFun} from "@/utils";
import {ModelClassCard,AddModelClassDialog} from "@/views/modelCenter/component"

export default {
  name: "modelClassification",
  components:{ModelClassCard,AddModelClassDialog},
  setup() {
    const store = useStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/sort/saveSort')
    })

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      CirclePlus, Delete,Refresh,

      fileLength: 1,
      fileListArray: [],

      formDialog: {},
      titleName: "创建场景",
      addModelClassVisible: false,
    })

    // 查询模型分类树结构列表
    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      querySortList({ page: that.currentPage, size: that.pageNum }).then(res=>{
        that.listLoading = false;
        that.list = res.data.childPage.items;
        that.totalNumber = res.data.childPage.totalSize;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickItemButton = (cardInfo) => {
      const routeName = "/modelCenter/modelClassDetails";
      const isAuthority = queryUserAuthorityIsHaveFun(routeName);
      if(!isAuthority){
        ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
        return
      }

      vueRouter.push({ path: routeName, query: { id: cardInfo.id,subTitle: cardInfo.sortName } });
      store.dispatch("addBackButViews",{ id: cardInfo.id,backRouteName: route.name, showButRoute: routeName });
    }

    const clickAddBut = () => {
      that.formDialog = {};
      that.titleName = "创建场景";
      that.addModelClassVisible = true;
    }

    const changeEvent = (data)=>{

      if(data.operate === 2){
        that.titleName = "编辑场景";
        that.formDialog = data.cardInfo;
        that.addModelClassVisible = true;
      }
      if(data.operate === 3)listArray("resetPage");
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), isAddButtonClick, clickAddBut, listArray,changeEvent, clickItemButton}
  }
}
</script>

<style lang="scss" scoped>
.app-container-right_top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px 12px 24px;
  box-sizing: border-box;
}
.tableContent{
  padding: 16px 0 0 0 !important;
}
</style>
