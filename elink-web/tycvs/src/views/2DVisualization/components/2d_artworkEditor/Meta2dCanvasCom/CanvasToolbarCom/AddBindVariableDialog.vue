<template>
  <Dialog v-model:isVisible="dialog_visible" :cancelVisible="false" :title="titleName" width="65vw" append-to-body>
    <template v-slot:content>
      <div class="dialog-main app-container">

        <div class="header-form">
          <el-form :model="formInline" inline @submit.prevent>
            <el-form-item label="关键词：">
              <el-input v-model="formInline.keyword" placeholder="请输入关键词">
                <template #suffix>
                  <div class="pointer" @click="queryGraphVariableByGraphId">
                    <el-icon> <Search/></el-icon>
                  </div>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item style="float: right">
              <el-button :icon="Plus" class="whiteFontButtons" @click="clickVariableBut">新建变量</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="tableContent">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80"></el-table-column>
            <el-table-column label="变量名">
              <template #default="{ row }">{{ $filters.moreData(row.name) }}</template>
            </el-table-column>
            <el-table-column label="类型">
              <template #default="{ row }">{{ $filters.moreData(row.type) }}</template>
            </el-table-column>
            <el-table-column label="关联数据源" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.graphSourceName) }}</template>
            </el-table-column>
            <el-table-column label="数据对象" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.dataObject) }}</template>
            </el-table-column>
            <el-table-column label="数据点" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.dataPoint) }}</template>
            </el-table-column>
            <el-table-column label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.description) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <div class="table_operate_class">
                  <el-link :underline="false" @click="clickOperateBut(1, row)">
                    <span class="text">编辑</span>
                  </el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">
                    <span class="text">删除</span>
                  </el-link>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <AddVariableDialog v-if="addVariableVisible" v-model:isVisible="addVariableVisible" :titleName="titleNameText" :activeEditInfo="activeEditInfo" @changeEvent="queryGraphVariableByGraphId"></AddVariableDialog>
        <ImportVariablesDialog v-if="importVariablesVisible" v-model:isVisible="importVariablesVisible" @changeEvent="queryGraphVariableByGraphId"></ImportVariablesDialog>
      </div>
    </template>
    <template #bottomContent>
      <div class="flex-ai-center">
        <el-button class="whiteFontButtons" @click="clickExportButFun(1)">导出变量</el-button>
        <el-button class="whiteFontButtons" @click="clickExportButFun(2)">导入变量</el-button>
        <span style="font-size: 12px;margin-left: 12px">支持excel格式导入/导出</span>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {useRoute} from "vue-router";
import {getNowDateMin} from "@/utils/dateTime";
import {ElMessage,ElMessageBox} from "element-plus";
import {Search, Plus} from '@element-plus/icons-vue';
import {variableTypeArray} from "@/utils/publicParam";
import {exportCustomExcel} from "@/common/exportExcel";
import AddVariableDialog from "./AddBindVariableDialog/AddVariableDialog.vue";
import ImportVariablesDialog from "./AddBindVariableDialog/ImportVariablesDialog.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted, computed} from 'vue';
import {findGraphVariableByGraphId,deleteGraphVariableByIds} from "@/api/2DVisualization/2d_artworkEditor";

export default defineComponent({
  name: "AddBindVariableDialog",
  components: {AddVariableDialog, Search, ImportVariablesDialog},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const route = useRoute();
    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();
    const canvasMeta2dData = computed(() => {
      return meta2dStore.canvasMeta2dData;
    });

    const that = reactive({
      Plus,
      listLoading: false,
      titleName: "变量管理",
      activeGraphId: route.query.id, // 当前图模id
      dialog_visible: props.isVisible,

      list: [],
      formInline: {},
      tableMaxHeight: 320,

      activeEditInfo: {},
      titleNameText: "新建变量",
      addVariableVisible: false,
      importVariablesVisible: false,
    })

    // 根据图模id查询关联变量数据
    const queryGraphVariableByGraphId = (operateType) => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      findGraphVariableByGraphId({ ...formInline,graphId: that.activeGraphId }).then(res => {
        that.list = res.data;
        that.listLoading = false;
        if(operateType === "resetPage")ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
      })
    }

    const clickVariableBut = ()=>{
      that.activeEditInfo = { graphId: that.activeGraphId };
      that.titleNameText = "新建变量";
      that.addVariableVisible = true;
    }

    const clickOperateBut = (operateType,row)=>{

      if(operateType === 1){
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.titleNameText = "编辑变量";
        that.addVariableVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除数据源（<span class="deleteName">${ row.name }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteGraphVariableByIds({ ids: [row.id] }).then(()=>{
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
          queryGraphVariableByGraphId();
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 导入导出
    const clickExportButFun = (operateType)=>{
      if(operateType === 1){

        let tableHeader = [
          {width: 20, key: "name", name: "变量名"},
          {width: 20, key: "type", name: "类型",reaType: 3,options: variableTypeArray},
          // {width: 35, key: "graphSourceId", name: "关联数据源Id"},
          // {width: 35, key: "graphSourceName", name: "关联数据源"},
          // {width: 35, key: "dataObject", name: "数据对象"},
          // {width: 35, key: "dataPoint", name: "数据点"},
          // {width: 15, key: "dataPointIndex", name: "数据点下标",reaType: 1,options:{ minValue: 0,maxValue: 9999999 } },
          {width: 20, key: "description", name: "描述"},
        ];

        let dateList = JSON.parse(JSON.stringify(that.list));
        exportCustomExcel(tableHeader, dateList,`${canvasMeta2dData.value.name}，绑定变量 - ${ getNowDateMin() }`,"Sheet1",{
          isInsertEmptyData: true,  // 是否生成空数据
          totalNumber: 500, // 生成空数据的总条数
          isLock: false, // 是否锁定该文件
        });
      }

      if(operateType === 2){
        that.importVariablesVisible = true;
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
      queryGraphVariableByGraphId();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickOperateBut, clickVariableBut, queryGraphVariableByGraphId, clickExportButFun, canvasMeta2dData}
  }
})

</script>

<style lang="scss" scoped>
.app-container{
  flex-direction: column;
}
</style>