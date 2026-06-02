<template>
  <div class="app-container-right" v-resize="setTableMaxHeight">
    <div class="app-container-right_top" ref="headerFormRef">
      <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddButFun">添加关联</el-button>
    </div>
    <div v-loading="listLoading" class="tableContent">
      <div class="tableCenter">
        <el-table :data="list" :max-height="contentHeight" border stripe>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="{$index}">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="图形名称">
            <template #default="{ row }">{{ $filters.moreData(row.graphName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="图形分类">
            <template #default="{ row }">{{ $filters.moreData(row.graphTypeName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="默认图形">
            <template #default="{ row }">{{ row.isDefault === 1 ? '默认' : '否' }}</template>
          </el-table-column>
          <el-table-column align="center" label="图形URL" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.graphUrl) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" @click="clickOperateBut(2, row)">预览</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(3, row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="tablePagination">
        <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
      </div>
    </div>
    <AddGraphicAssDialog v-if="addGraphicAssVisible" v-model:isVisible="addGraphicAssVisible" :titleName="titleName" :activeEditInfo="activeEditInfo"
                         @changeEvent="listArray('resetPage')"></AddGraphicAssDialog>
  </div>
</template>

<script>
import {operateButtonIsClick} from "@/utils";
import {CirclePlus} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from "element-plus";
import AddGraphicAssDialog from "./AddGraphicAssDialog.vue";
import {reactive, toRefs, defineComponent, ref, computed, watch} from "vue";
import {deleteGraphById, findGraphListByDeviceId} from "@/api/visualization/graphicAssociation";

export default defineComponent({
  name: "GraphicAssTableList",
  components:{AddGraphicAssDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    },
    activeSelectId: {
      type: [Number,String],
      default: ""
    },
    activeTypeId: {
      type: [Number,String],
      default: ""
    },
  },
  setup(props){

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/visual/saveGraph')
    })

    const that = reactive({
      list: [],
      CirclePlus,
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      contentHeight: 380,
      listLoading: false, // 表格加载

      activeEditInfo: {},
      titleName: "添加关联",
      addGraphicAssVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      findGraphListByDeviceId({ page: that.currentPage, size: that.pageNum,deviceId: props.activeSelectId }).then(res => {
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

    const clickAddButFun = ()=>{
      that.titleName = "添加关联";
      that.activeEditInfo = { deviceId: props.activeSelectId, typeId: props.activeTypeId };
      that.addGraphicAssVisible = true;
    }

    const clickOperateBut = (index,row) => {
      if(index === 1){
        that.titleName = "编辑关联";
        that.activeEditInfo = JSON.parse(JSON.stringify({...row,typeId: props.activeTypeId}));
        that.addGraphicAssVisible = true;
      }

      if(index === 2){
        window.open(row.graphUrl,"_blank");
      }

      if(index === 3){
        ElMessageBox.confirm(`您确定要删除（${ row.graphName }）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteGraphById({ id: row.id }).then(()=> {
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

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value?.offsetHeight ?? 0;
      that.contentHeight = props.contentMaxHeight - headerFormHeight - 120;
    }

    const watchActiveSelectId = watch(()=>props.activeSelectId,(newActiveSelectId)=>{
      if(newActiveSelectId) listArray('resetPage');
    },{ deep: true,immediate: true})

    return {...toRefs(that), setTableMaxHeight, headerFormRef, isAddButtonClick, clickAddButFun, listArray, watchActiveSelectId, clickOperateBut }
  }
})

</script>

<style scoped lang="scss">
.app-container-right_top{
  display: flex;
  justify-content: flex-end;
  padding-bottom: 10px;
}
</style>