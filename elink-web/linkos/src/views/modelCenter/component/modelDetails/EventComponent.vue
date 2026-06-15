<template>
  <div class="content_table" v-resize="setTableMaxHeight">

    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.functionName" clearable placeholder="请输入事件名称进行搜索">
            <template #append>
              <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="事件级别：">
          <el-select v-model="formInline.eventLevel" placeholder="请选择" @change="listArray('resetPage')">
            <el-option v-for="item in eventLevelArray" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加事件</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="tableContent" v-loading="listLoading">
      <div class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight" border stripe @selection-change="handleSelectionChange">
          <el-table-column align="center" type="selection" width="55"></el-table-column>
          <el-table-column align="center" label="序号" type="index" width="80">
            <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
          </el-table-column>
          <el-table-column align="center" label="事件名称">
            <template #default="{ row }">{{ $filters.moreData(row.eventName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="功能点">
            <template #default="{ row }">{{ $filters.moreData(row.functionNames) }}</template>
          </el-table-column>
          <el-table-column align="center" label="事件级别">
            <template #default="{ row }">{{ $filters.eventLevel(row.eventLevel)}}</template>
          </el-table-column>
          <el-table-column align="center" label="描述" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.eventDesc)}}</template>
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
      <div class="tablePagination">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" :paginationButArray="paginationButArray"
                    @pageChange="listArray" @paginationFunction="paginationFunction" />
      </div>
    </div>

    <add-model-event v-if="addModelEventVisible" v-model:isVisible="addModelEventVisible" :titleName="titleName" :activeEventId="activeEventId"
                     :activeModelId="activeModelId" @changeEvent="listArray('resetPage')"></add-model-event>

  </div>
</template>

<script lang="ts">
import {AddModelEvent} from "./component";
import {event_level_array} from "@/utils/setVariate";
import {ElMessage, ElMessageBox} from "element-plus";
import {onMounted, reactive, ref, toRefs} from "vue";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {batchDeleteModelEventByIds, findModelEventList} from "@/api/modelCenter/modelManagement";

export default {
  name: "EventComponent",
  components:{AddModelEvent},
  props: {
    activeModelId: {
      type: [String, Number],
      default: ''
    },
    componentMaxHeight: {
      type: [String, Number],
      default: 320
    }
  },
  setup(props) {

    const that = reactive({
      list: [],
      pageNum: 20,
      formInline: {},
      currentPage: 1,
      totalNumber: 0,
      listLoading: false, // 表格加载
      tableMaxHeight: 300,
      selectableArrayIds: [],

      activeEventId: "",
      titleName: "添加事件",
      addModelEventVisible: false,
      CirclePlus, Delete, Refresh, Search,
      eventLevelArray: event_level_array,
      paginationButArray: [
        {buttonName: "批量删除", buttonType: "batchDelete", buttonIcon: "Delete"},
      ],
    })

    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findModelEventList({ modelId: props.activeModelId, page: that.currentPage, size: that.pageNum,...that.formInline }).then(res=>{
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = ()=>{
      that.activeEventId = "";
      that.titleName = "添加事件";
      that.addModelEventVisible = true;
    }

    const clickOperateBut = (operate,row)=>{
      if(operate === 1){
        that.titleName = "编辑事件";
        that.activeEventId = row.id;
        that.addModelEventVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.eventName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              batchDeleteModelEventByIds({ ids: [row.id],modelId: props.activeModelId }).then(() => {
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
          ElMessage({ type: "success", message: "删除成功!", showClose: true });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const handleSelectionChange = (val)=> {
      that.selectableArrayIds = val;
    }

    const paginationFunction = (data)=>{
      if (data.type === "batchDelete") {
        if (!that.selectableArrayIds.length) {
          ElMessage({ type: "error", message: "请选择需要操作的数据!", showClose: true });
          return
        }

        let data = [];
        for (let i = 0; i < that.selectableArrayIds.length; i++) {
          data.push(that.selectableArrayIds[i].id);
        }

        ElMessageBox.confirm(`您确定要删除事件数据吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
        }).then(() => {
          batchDeleteModelEventByIds({ ids: data,modelId: props.activeModelId }).then(() => {
            listArray("resetPage");
            ElMessage({ type: "success", message: "删除成功!", showClose: true });
          });
        }).catch(() => {
          console.log("取消操作！");
        });
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.componentMaxHeight - headerFormHeight - 115;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), headerFormRef, setTableMaxHeight, listArray, paginationFunction, handleSelectionChange, clickOperateBut, clickAddBut }
  }
}
</script>

<style scoped lang="scss">
.content_table{
  height: 100%;
  display: flex;
  flex-direction: column;

  .header-form,.tableContent{
    padding: 0;
    border-top: none;
  }
}
</style>
