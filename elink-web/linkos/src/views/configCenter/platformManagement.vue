<template>
  <div class="app-container">
    <div class="app-container-right" v-resize="setTableMaxHeight">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline>
          <el-form-item label="关键字：">
            <el-input v-model="formInline.keyword" clearable placeholder="请输入关键词搜索">
              <template #append>
                <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :disabled="isAddButtonClick" :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">添加平台</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="平台名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.platformName)}}</template>
            </el-table-column>
            <el-table-column label="平台标识">
              <template #default="{ row }">{{ $filters.moreData(row.platformLogo)}}</template>
            </el-table-column>
            <el-table-column label="ip地址">
              <template #default="{ row }">{{ $filters.moreData(row.ipAddress)}}</template>
            </el-table-column>
            <el-table-column label="端口号">
              <template #default="{ row }">{{ $filters.moreData(row.portNumber)}}</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <AddPlatformDialog v-if="addPlatformVisible" v-model:isVisible="addPlatformVisible" :titleName="titleName" :activeEditInfo="formDialog" @changeEvent="listArray('resetPage')" />
  </div>
</template>
<script lang="ts">

import {operateButtonIsClick} from "@/utils";
import {ElMessage,ElMessageBox} from "element-plus";
import {CirclePlus,Search} from "@element-plus/icons-vue";
import {AddPlatformDialog} from "@/views/configCenter/component";
import {computed, onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {deletePlatformInfoById, findPlatformInfoListByPage} from "@/api/configCenter/platformManagement";

export default defineComponent({
  name: "platformManagement",
  components: {AddPlatformDialog},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(props) {

    const that = reactive({
      Search,
      CirclePlus,
      formInline: {},

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      formDialog:{},
      titleName: "添加平台",
      addPlatformVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      findPlatformInfoListByPage({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
        that.totalNumber = res.data.totalSize;
        that.list = res.data.items;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
        that.totalNumber = 0;
      })
    }

    const clickAddBut = ()=>{
      that.formDialog = {};
      that.titleName = "添加平台";
      that.addPlatformVisible = true;
    }

    const clickOperateBut = (operateType, row) => {
      if(operateType === 1){
        that.titleName = "编辑平台";
        that.formDialog = JSON.parse(JSON.stringify(row));
        that.addPlatformVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.platformName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deletePlatformInfoById({ id: row.id }).then(()=>{
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
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/configureCenter/saveOrUpdatePlatformInfo')
    })

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that),headerFormRef,setTableMaxHeight,isAddButtonClick,listArray,clickAddBut,clickOperateBut}
  }
})
</script>
<style lang="scss" scoped>

</style>