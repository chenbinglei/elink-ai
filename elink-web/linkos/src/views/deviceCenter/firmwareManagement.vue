<template>
  <div class="app-container">
    <div v-resize="setTableMaxHeight" class="app-container-right">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item label="关键词：">
            <el-input v-model="formInline.keyword" clearable placeholder="请输入固件名称、版本号查询"></el-input>
          </el-form-item>
          <el-form-item label="设备类型：">
            <el-tree-select v-model="formInline.typeId" :data="deviceAssetTypeList" :indent="0" :props="treeProps" :render-after-expand="false"
                            placeholder="请选择设备类型" class="leftArrowClass fw-type-select" default-expand-all filterable clearable/>
          </el-form-item>
          <el-form-item>
            <el-button :icon="Search" class="whiteFontButtons" @click="listArray('resetPage')">查询</el-button>
            <el-button class="blackFontButtons" :icon="Refresh" @click="clickResetForm">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :disabled="isAddButtonClick" :icon="Plus" class="whiteFontButtons" @click="clickAddButFun">上传固件</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tableContent">
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80" fixed="left">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="固件名称" show-overflow-tooltip fixed="left">
              <template #default="{ row }">{{ $filters.moreData(row.firmwareName) }}</template>
            </el-table-column>
            <el-table-column label="版本号">
              <template #default="{ row }">{{ $filters.moreData(row.firmwareVersion) }}</template>
            </el-table-column>
            <el-table-column label="设备类型">
              <template #default="{ row }">{{ $filters.moreData(row.typeName) }}</template>
            </el-table-column>
            <el-table-column label="设备型号" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.equipmentModels) }}</template>
            </el-table-column>
            <el-table-column label="文件大小">
              <template #default="{ row }">{{ $filters.formatBytes(row.firmwareSize) }}</template>
            </el-table-column>
            <el-table-column label="固件类型" show-overflow-tooltip>
              <template #default="{ row }">
                <template v-if="row.typeId >= 28 && row.typeId <= 30">{{ $filters.pileFirmwareType(row.firmwareType) }}</template>
                <template v-else>{{ $filters.moreData(row.firmwareType) }}</template>
              </template>
            </el-table-column>
            <el-table-column label="描述" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.firmwareDesc) }}</template>
            </el-table-column>
            <el-table-column label="创建信息" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.createName) }}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="最后更新" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.updateName) }}</span>
                <span>，</span>
                <span>{{ $filters.moreData(row.updateTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateButFun(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperateButFun(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <AddFirmwareDialog v-if="addFirmwareVisible" v-model:isVisible="addFirmwareVisible" :titleName="titleName" :activeEditDataInfo="activeEditDataInfo"
                       @changeEvent="listArray('resetPage')"></AddFirmwareDialog>
  </div>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {operateButtonIsClick,setTreeData} from "@/utils";
import {Plus, Search, Refresh} from "@element-plus/icons-vue";
import {AddFirmwareDialog} from "@/views/deviceCenter/component";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {computed, reactive, ref, toRefs, defineComponent, onMounted} from "vue";
import {deleteFirmwareById, queryFirmwareList} from "@/api/deviceCenter/firmwareManagement";

export default defineComponent({
  name: "firmwareManagement",
  components: {AddFirmwareDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(props) {

    const isAddButtonClick = computed(() => {
      return operateButtonIsClick('/device/firmware/uploadOrEditFirmware')
    })

    const that = reactive({
      Plus,
      Search,
      Refresh,
      formInline: {},
      oldFormInline: {},
      deviceAssetTypeList: [],
      treeProps: {value: 'id', label: 'typeName', children: 'children'},

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      titleName: "上传固件",
      activeEditDataInfo: {},
      addFirmwareVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      queryFirmwareList({ page: that.currentPage, size: that.pageNum, ...that.formInline}).then(res => {
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.totalNumber = 0;
        that.list = [];
      })
    }

    const clickResetForm = ()=>{
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    const clickAddButFun = () => {
      that.titleName = "上传固件";
      that.activeEditDataInfo = {};
      that.addFirmwareVisible = true;
    }

    const clickOperateButFun = (index, row) => {
      if(index === 1){
        that.titleName = "编辑固件包信息";
        that.activeEditDataInfo = JSON.parse(JSON.stringify(row));
        that.addFirmwareVisible = true;
      }

      if(index === 2){
        ElMessageBox.confirm(`您确定要删除固件包（<span class="deleteName">${row.firmwareName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteFirmwareById({ id: row.id }).then(()=> {
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

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({pageName:"AddFirmwareDialog",timer: new Date()}).then(res=>{
        let returnDataList = setTreeData(res.data ?? []);
        let findDataItem= returnDataList.find(item => item.id === "3");
        that.deviceAssetTypeList = findDataItem?.children;
      })
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      queryAssetTypeList();
      listArray();
    })

    return {...toRefs(that), listArray, headerFormRef, setTableMaxHeight, clickAddButFun, clickOperateButFun, isAddButtonClick, queryAssetTypeList, clickResetForm}
  }
})
</script>

<style lang="scss" scoped>
.iconfont {
  margin-right: 8px;

  &:last-child {
    margin-right: 0;
  }
}
</style>

<!-- 固件管理页面设备类型下拉框宽度 - 非 scoped 样式，确保穿透所有组件边界 -->
<style lang="scss">
.app-container-right .header-form .fw-type-select,
.app-container-right .header-form .fw-type-select .el-select__wrapper {
  width: 200px !important;
}
</style>