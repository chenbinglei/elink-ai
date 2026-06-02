<template>
  <div class="app-container-right" v-resize="setTableMaxHeight">
    <div class="app-container-right_top" ref="headerFormRef">
      <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddButFun">添加分类</el-button>
    </div>
    <div class="tableContent">
      <el-table v-loading="listLoading" :data="list" :max-height="contentHeight" border stripe>
        <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
        <el-table-column align="center" label="分类名称">
          <template #default="{ row }">{{ $filters.moreData(row.name) }}</template>
        </el-table-column>
        <el-table-column align="center" label="分类标识">
          <template #default="{ row }">{{ $filters.moreData(row.code) }}</template>
        </el-table-column>
        <el-table-column align="center" label="操作">
          <template #default="{ row }">
            <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
            <span class="split_line">|</span>
            <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <AddGraphicClassDialog v-if="addGraphicClassVisible" v-model:isVisible="addGraphicClassVisible" :titleName="titleName" :activeEditInfo="activeEditInfo" @changeEvent="listArray" />
  </div>
</template>

<script>
import {operateButtonIsClick} from "@/utils";
import {CirclePlus} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from "element-plus";
import AddGraphicClassDialog from "./AddGraphicClassDialog.vue";
import {reactive, toRefs, defineComponent, ref, computed, watch} from "vue";
import {deleteGraphTypeById, findGraphTypeListByTypeId} from "@/api/visualization/graphicClassify";

export default defineComponent({
  name: "GraphicAssTableList",
  components:{AddGraphicClassDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    },
    activeTypeId: {
      type: [Number,String],
      default: ""
    },
  },
  setup(props){

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/device/visual/saveGraphType')
    })

    const that = reactive({
      list: [],
      CirclePlus,
      pageNum: 20,
      contentHeight: 380,
      listLoading: false, // 表格加载

      activeEditInfo: {},
      titleName: "添加分类",
      addGraphicClassVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      findGraphTypeListByTypeId({ typeId: props.activeTypeId,timer: new Date() }).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
      })
    }

    const clickAddButFun = ()=>{
      that.titleName = "添加分类";
      that.activeEditInfo = { typeId: props.activeTypeId };
      that.addGraphicClassVisible = true;
    }

    const clickOperateBut = (index,row) => {
      if(index === 1){
        that.titleName = "编辑分类";
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.addGraphicClassVisible = true;
      }

      if(index === 2){
        ElMessageBox.confirm(`您确定要删除（${row.name}）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteGraphTypeById({ id: row.id }).then(()=> {
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
      that.contentHeight = props.contentMaxHeight - headerFormHeight;
    }

    const watchActiveTypeId = watch(()=>props.activeTypeId,(newActiveTypeId)=>{
      if(newActiveTypeId) listArray();
    },{ deep: true,immediate: true })

    return {...toRefs(that), setTableMaxHeight, headerFormRef, isAddButtonClick, clickAddButFun, listArray, watchActiveTypeId, clickOperateBut }
  }
})

</script>

<style scoped lang="scss">
.app-container-right_top{
  display: flex;
  justify-content: flex-end;
  padding-bottom: 10px;
  box-sizing: border-box;
}
</style>