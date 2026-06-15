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
            <el-button :disabled="isAddButtonClick" :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">新建通道</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div class="tableCenter" ref="tableCenterRef">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column label="通道名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.channelName) }}</template>
            </el-table-column>
            <el-table-column label="协议名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.protocolName) }}</template>
            </el-table-column>
            <el-table-column label="协议地址">
              <template #default="{ row }">{{ $filters.moreData(row.address) }}</template>
            </el-table-column>
            <el-table-column label="启用">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="2" @change="clickOperateBut(4, row)" />
              </template>
            </el-table-column>
            <el-table-column label="状态">
              <template #default="{ row }">
                <span :class="'status' + row.status">{{ $filters.moreData(row.status === 1 ? '启用' : '断开') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" @click="clickOperateBut(2, row)">配置</el-link>
                <span class="split_line">|</span>
                <el-link :underline="false" type="danger" @click="clickOperateBut(3, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <PassagewayConfigDialog v-if="passagewayConfigVisible" v-model:isVisible="passagewayConfigVisible" :dataForwardId="dataForwardId" :protocolType="protocolType" :platformName="titleName"/>
    <AddPassagewayDialog v-if="addPassagewayVisible" v-model:isVisible="addPassagewayVisible" :titleName="titleName" :dataForwardId="dataForwardId" @changeEvent="listArray('resetPage')"/>
  </div>
</template>

<script lang="ts">
import {operateButtonIsClick} from "@/utils";
import {ElMessage,ElMessageBox} from "element-plus";
import {CirclePlus,Search} from "@element-plus/icons-vue";
import {computed, onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {AddPassagewayDialog,PassagewayConfigDialog} from "@/views/configCenter/component";
import {deleteDataForwardById, queryDataForwardList, updateForwardStatus} from "@/api/configCenter/dataForwarding";

export default defineComponent({
  name: "dataForwarding",
  components: {AddPassagewayDialog,PassagewayConfigDialog},
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

      protocolType: "", //接入协议类型
      dataForwardId: "",
      titleName: "新建通道",
      addPassagewayVisible: false,
      passagewayConfigVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage") that.currentPage = 1;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      queryDataForwardList({ page: that.currentPage, size: that.pageNum, ...formInline }).then(res => {
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
      that.dataForwardId = "";
      that.titleName = "新建通道";
      that.addPassagewayVisible = true;
    }

    const clickOperateBut = (operateType, row) => {
      if(operateType === 1){
        that.titleName = "编辑通道";
        that.dataForwardId = row.id;
        that.addPassagewayVisible = true;
      }

      if(operateType === 2){
        that.dataForwardId = row.id;
        that.titleName = row.channelName;
        that.protocolType = row.protocolType;
        that.passagewayConfigVisible = true;
      }

      if(operateType === 3){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.channelName }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteDataForwardById({ id: row.id }).then(()=>{
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

      if(operateType === 4){
        let statusText = row.status === 1 ? '开启' : '关闭';
        ElMessageBox.confirm(`确定${ statusText }（<span class="deleteName">${ row.channelName }</span>）吗？`, "操作提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              updateForwardStatus({ id: row.id,status: row.status }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonLoading = false;
                row.status = row.status === 1 ? 2 : 1;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          listArray();
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          row.status = row.status === 1 ? 2 : 1;
          console.log("取消！");
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
      return operateButtonIsClick('/system/configureCenter/saveDataForward')
    })

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that),headerFormRef,setTableMaxHeight,isAddButtonClick,listArray,clickAddBut,clickOperateBut}
  }
})
</script>
<style lang="scss" scoped>
.status1{
  color: #41CB4A;
}
.status2{
  color: #FD393A;
}
</style>