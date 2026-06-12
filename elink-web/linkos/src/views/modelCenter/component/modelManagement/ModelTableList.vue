<template>
  <div class="app-container-right" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.keyword" class="input-with-select" clearable placeholder="请输入模型名称或模型ID">
            <template #append>
              <el-button :icon="Search" @click="listArray"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态：">
          <el-select v-model="formInline.modelStatus" clearable placeholder="请选择状态">
            <el-option v-for="item in modelStatusArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Refresh" class="blackFontButtons" @click="listArray('resetPage')">刷新</el-button>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">创建模型</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="tableContent" v-loading="listLoading">
      <div class="tableCenter">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="模型名称/ID" width="380">
            <template #default="{ row }">
              <div class="modelName">{{ $filters.moreData(row.modelName) }}</div>
              <div class="model_id">
                <span class="id">{{ $filters.moreData(row.id) }}</span>
                <el-button type="primary" text @click="clickCopyBut(row)">复制</el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="关联设备">
            <template #default="{ row }">{{ $filters.numberNull(row.deviceNum) }}个</template>
          </el-table-column>
          <el-table-column align="center" label="设备类型">
            <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="状态" width="120">
            <template #default="{ row }">
              <div class="flex-jc-ai-center">
                <div class="modelStatus" :class="'modelStatus' + row.modelStatus">{{ $filters.modelStatus(row.modelStatus) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">模型开发</el-link>
              <span class="split_line">|</span>
              <template v-if="row.modelStatus !== 1">
                <el-link :underline="false" @click="clickOperateBut(2, row)">发布</el-link>
                <span class="split_line">|</span>
              </template>
              <el-link :underline="false" type="danger" @click="clickOperateBut(3, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <CreateModelDialog v-if="createModelVisible" v-model:isVisible="createModelVisible" :formDialog="formDialog" :titleName="titleName" @changeEvent="listArray('resetPage')" />
  </div>
</template>

<script>
import { useTagsViewStore } from '@/stores/index';

import {useRouter,useRoute} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import CreateModelDialog from "./CreateModelDialog.vue";
import {CirclePlus, Refresh, Search} from "@element-plus/icons-vue";
import {computed, reactive, ref, toRefs, defineComponent, watch} from "vue";
import {clickCopyValue, operateButtonIsClick, queryUserAuthorityIsHaveFun} from "@/utils";
import {deleteModelById, queryModelList, updateModelStatus} from "@/api/modelCenter/modelManagement";

export default defineComponent({
  name: "ModelTableList",
  components:{CreateModelDialog},
  props:{
    contentMaxHeight:{
      type: Number,
      default: 520
    },
    // 资产类型id
    activeTypeId:{
      type: [Number,String],
      default: ""
    },
  },
  setup(props){

    const tagsViewStore = useTagsViewStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/model/saveModel')
    })

    const that = reactive({
      formInline: {},
      modelStatusArray: [{id: 0, name: "开发中"}, {id: 1, name: "已发布"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      CirclePlus, Refresh, Search,

      formDialog: {},
      titleName: "创建模型",
      createModelVisible: false,
    })

    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      formInline.typeId = props.activeTypeId;

      queryModelList({ page: that.currentPage, size: that.pageNum,...formInline }).then(res=>{
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = () => {
      that.titleName = "创建模型";
      that.formDialog = { typeId: props.activeTypeId };
      that.createModelVisible = true;
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        const routeName = "/modelCenter/modelDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if(!isAuthority){
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        vueRouter.push({ path: routeName, query: { id: row.id,subTitle: row.modelName } });
        tagsViewStore.addBackButViews({ id: row.id,backRouteName: route.path,showButRoute: routeName });
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定发布（<span class="highlightText">${ row.modelName }</span>）吗？`, "发布模型", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在发布...';
              updateModelStatus({ id: row.id }).then(()=>{
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
          ElMessage({ type: "success", showClose: true, message: "发布成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }

      if(operate === 3){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.modelName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteModelById({ id: row.id }).then(()=>{
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

    // 复制
    const clickCopyBut = (row)=>{
      clickCopyValue(row.id);
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    const watchActiveTypeId = watch(()=>props.activeTypeId,(newActiveTypeId)=>{
      listArray("resetPage");
    },{ deep: true })

    return {...toRefs(that), isAddButtonClick, clickAddBut, listArray,headerFormRef,setTableMaxHeight,clickCopyBut,clickOperateBut,watchActiveTypeId }
  }
})

</script>

<style scoped lang="scss">
.modelStatus{
  padding: 0 8px;
  box-sizing: border-box;
  border-radius: 12px 12px 12px 12px;

  color: #B9B9B9;
  font-size: 14px;
  background: #F5F5F5;
  border: 1px solid #B9B9B9;
}

.modelStatus1{
  color: #049735;
  background: #E5FFEE;
  border: 1px solid #049735;
}
</style>