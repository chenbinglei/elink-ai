<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <HandleMenus :handleMenuArray="handleMenuArray" :isShowHeader="false" @handleMenuEvent="handleMenuEvent"></HandleMenus>
    <div class="app-container-right">
      <div class="header-form" ref="headerFormRef">
        <el-form inline :model="formInline">
          <el-form-item>
            <el-input v-model="formInline.keyword" :suffix-icon="Search" clearable placeholder="请输入关键字" @keyup.enter="listArray('refresh')">
              <template #prepend>
                <el-select v-model="formInline.keywordType" placeholder="请选择" style="width: 100px">
                  <el-option v-for="item in keyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="存储策略：">
            <el-select v-model="formInline.strategyType" clearable placeholder="请选择存储策略">
              <el-option v-for="item in strategyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" @click="listArray('refresh')">查询</el-button>
            <el-button class="blackFontButtons" @click="resetForm('resetPage')">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="addOpenDialog">新增计算节点</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="序号" width="60">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column align="center" label="节点名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.nodeName) }}</template>
            </el-table-column>
            <el-table-column align="center" label="节点编码" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.nodeCode) }}</template>
            </el-table-column>
            <el-table-column align="center" label="存储ID">
              <template #default="{ row }">{{ $filters.moreData(row.storageId) }}</template>
            </el-table-column>
            <el-table-column align="center" label="存储策略">
              <template #default="{ row }">{{ $filters.strategyType(row.strategyType) }}</template>
            </el-table-column>
            <el-table-column align="center" label="统计周期">
              <template #default="{ row }">{{ $filters.moreData(row.countPeriod) }}</template>
            </el-table-column>
            <el-table-column align="center" label="计算周期">
              <template #default="{ row }">{{ $filters.moreData(row.computePeriod) }}</template>
            </el-table-column>
            <el-table-column align="center" label="单位">
              <template #default="{ row }">{{ $filters.moreData(row.unit) }}</template>
            </el-table-column>
            <el-table-column align="center" label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.remark) }}</template>
            </el-table-column>
            <el-table-column align="center" label="创建时间" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="200">
              <template #default="{ row }">
                <el-link :underline="false" @click="infoClick(1, row)">编辑</el-link>
                <span style="margin:0 8px">|</span>
                <el-link :underline="false" type="danger" @click="infoClick(2,row)">删除</el-link>
                <span style="margin:0 8px">|</span>
                <el-link :underline="false" @click="infoClick(3, row)">数据</el-link>
                <span style="margin:0 8px">|</span>
                <el-link :underline="false" @click="infoClick(4,row)">日志</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <!--    节点日志-->
    <ComputeNodeLog v-if="computeNodeLogVisible" v-model:isVisible="computeNodeLogVisible" :computeNodeId="computeNodeId" />

    <!--    新增编辑计算节点-->
    <AddComputeNode v-if="computeNodeVisible" v-model:isVisible="computeNodeVisible" :titleName="titleName" :recordId="activeRecordId" :activeSiteId="activeSiteId"
                    :computeNodeId="computeNodeId" :exampleType="exampleType" @changEvent="listArray('refresh')" />

    <!--    数据-->
    <LookComputeNodeDate v-if="lookComputeNodeVisible" v-model:isVisible="lookComputeNodeVisible" :computeNodeId="computeNodeId" :titleName="computeNodeName" />

  </div>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Search} from '@element-plus/icons-vue';
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {reactive, toRefs, ref, onActivated, defineComponent, computed} from "vue";
import {operateButtonIsClick, selectTreeData, setTreeData} from "@/utils";
import {findComputeNodeByPage,deleteAllComputeNodeById} from "@/api/dataManagement/nodeManagement";
import {AddComputeNode, ComputeNodeLog, LookComputeNodeDate} from "@/views/dataManagement/components";

export default defineComponent({
  name: "nodeManagement",
  components: {AddComputeNode, LookComputeNodeDate, ComputeNodeLog},
  props: {
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/crontab/computeNode/saveOrUpdateComputeNodeInfo')
    })

    const that = reactive({
      Search, CirclePlus,
      oldFormInline: {},
      formInline: { keywordType: 1 },

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      handleMenuArray: [],
      allHandleMenuArray: [],

      exampleType: 1, //实例类型 1-设备类型 2-站点类型
      activeRecordId: "",  //记录id
      activeSiteId: "", // 当前站点id

      computeNodeId: "",
      titleName: "新增节点",
      computeNodeVisible: false,

      computeNodeName: "",
      lookComputeNodeVisible: false,
      computeNodeLogVisible: false,

      keyTypeArray: [{id: 1, name: "名称"}, {id: 2, name: "编码"}],
      strategyTypeArray: [{id: 1, name: "每次存储"}, {id: 2, name: "变化存储"}, {id: 3, name: "不存储"}],
    })

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 0,timer: new Date() }).then(res=>{

        let handleMenuArray = res.data ? res.data : [];

        handleMenuArray.forEach(element => {
          if (element.type === 1) {
            element.iconName = "icon-zhandian";
          }

          if (element.type !== 1) {
            let typeDetailText = "--";
            if(element.typeDetail === 1) typeDetailText = "直连";
            if(element.typeDetail === 2) typeDetailText = "网关";
            if(element.typeDetail === 3) typeDetailText = "子设备";
            element.name = `<span class="textTwo flex-all">${ element.name }</span><span class='typeDetailClass'>${ typeDetailText }</span>`;
          }
        });

        that.allHandleMenuArray = handleMenuArray;
        that.handleMenuArray = setTreeData(handleMenuArray);
      })
    }

    const handleMenuEvent = (data) => {
      // console.log(data);
      if(data.menuType === "clickTreeNode"){
        that.activeRecordId = data.id;
        that.exampleType = data.type !== 1 ? 1 : 2; // 实例类型 1-设备类型 2-站点类型
        let activeSiteId = "";
        let selArray = selectTreeData(data.id,'id',that.handleMenuArray);
        if(selArray && selArray.length)activeSiteId = selArray[0].id;
        that.activeSiteId = activeSiteId;
        listArray("refresh");
      }
    }

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType ===  "refresh") that.currentPage = 1;
      findComputeNodeByPage({ ...that.formInline,deviceId:that.activeRecordId,page:that.currentPage,size:that.pageNum }).then(res=>{
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if(operateType === "resetPage")ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const resetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    const addOpenDialog = () => {
      that.computeNodeId = "";
      that.titleName = "新增节点";
      that.computeNodeVisible = true;
    }

    const infoClick = (operateType, row) => {

      if (operateType === 1) {
        that.titleName = "编辑节点";
        that.computeNodeId = row.id;
        that.computeNodeVisible = true;
      }

      if (operateType === 2) {
        ElMessageBox.confirm(`确定删除（${row.nodeName}）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose: false, type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteAllComputeNodeById({ id: row.id }).then(()=>{
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
          console.log("取消操作！");
        });
      }

      if (operateType === 3) {
        that.computeNodeId = row.id;
        that.computeNodeName = row.nodeName;
        that.lookComputeNodeVisible = true;
      }

      if (operateType === 4) {
        that.computeNodeId = row.id;
        that.computeNodeLogVisible = true;
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onActivated(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), isAddButtonClick, headerFormRef, setTableMaxHeight, querySiteDeviceTreeList, listArray, resetForm, addOpenDialog, handleMenuEvent, infoClick}
  }
})
</script>

<style lang="scss" scoped>

</style>
