<template>
  <div class="content_table" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <el-form :model="formInline" inline>
        <el-form-item label="关键字：">
          <el-input v-model="formInline.reaName" clearable placeholder="请输入属性名称进行搜索">
            <template #append>
              <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加扩展属性</el-button>
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
          <el-table-column align="center" label="属性名称">
            <template #default="{ row }">{{ $filters.moreData(row.reaName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="英文名称">
            <template #default="{ row }">{{ $filters.moreData(row.fieldName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="类型">
            <template #default="{ row }">{{ $filters.reaType(row.reaType) }}</template>
          </el-table-column>
          <el-table-column align="center" label="默认值">
            <template #default="{ row }">{{ $filters.moreData(row.defaultValue) }}</template>
          </el-table-column>
          <el-table-column align="center" label="是否必填">
            <template #default="{ row }">{{ $filters.required(row.required) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-link :underline="false" @click="clickOperateBut(3, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" @click="clickOperateBut(1, row)">查看</el-link>
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

    <AddModelExtendedAttr v-if="addModelExtendedAttrVisible" v-model:isVisible="addModelExtendedAttrVisible" :titleName="titleName"
                             :activeAttrId="activeAttrId" :activeModelId="activeModelId" @changeEvent="listArray('resetPage')" />

    <AddExtendedAttrDialog v-if="addExtendedAttrVisible" v-model:isVisible="addExtendedAttrVisible" :titleName="titleName"
                           :activeExtendedAttrId="activeAttrId" isDetails></AddExtendedAttrDialog>

    <EditModelDefaultValueDialog v-if="editModelDefaultValueVisible" v-model:isVisible="editModelDefaultValueVisible" :titleName="titleName"
                                 :activeEditInfo="activeEditInfo" @changeEvent="listArray('resetPage')" />
  </div>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {AddModelExtendedAttr,EditModelDefaultValueDialog} from "./component";
import {CirclePlus, Delete, Refresh, Search} from "@element-plus/icons-vue";
import {modelBindReaData, findModelReaListByModelId} from "@/api/modelCenter/modelManagement";
import {AddExtendedAttrDialog} from "@/views/modelCenter/component/masterDataManagement/component";

export default defineComponent({
  name: "ExtendedAttr",
  components:{AddModelExtendedAttr,AddExtendedAttrDialog,EditModelDefaultValueDialog},
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

      activeAttrId: "",
      activeEditInfo: {},
      titleName: "添加扩展属性",
      addExtendedAttrVisible: false,
      addModelExtendedAttrVisible: false,
      editModelDefaultValueVisible: false,
      CirclePlus, Delete, Refresh, Search,

      paginationButArray: [
        {buttonName: "批量删除", buttonType: "batchDelete", buttonIcon: "Delete"},
      ],
    })

    const listArray = (reset)=>{
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findModelReaListByModelId({ modelId: props.activeModelId, page: that.currentPage, size: that.pageNum,...that.formInline }).then(res=>{
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
      that.activeAttrId = "";
      that.titleName = "添加扩展属性";
      that.addModelExtendedAttrVisible = true;
    }

    const clickOperateBut = (operate,row)=>{

      if(operate === 1){
        that.activeAttrId = row.id;
        that.titleName = "查看扩展属性";
        that.addExtendedAttrVisible = true;
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.reaName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose:false,type: 'warning', closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              modelBindReaData({ reaIds: [row.id],type: 2,modelId: props.activeModelId }).then(() => {
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

      if(operate === 3){
        that.activeEditInfo = {
          id: row.modelReaId,
          reaName: row.reaName,
          reaType: row.reaType,
          defaultValue: row.defaultValue,
        };
        that.titleName = "编辑默认值";
        that.editModelDefaultValueVisible = true;
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
          modelBindReaData({ reaIds: data,type: 2,modelId: props.activeModelId }).then(() => {
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
})
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
