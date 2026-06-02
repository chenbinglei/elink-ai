<template>
  <div class="content_body bg_color_class">
    <TableHeaderTitle title="白名单列表">
      <template #content>
        <el-button v-if="authority === 2" :icon="Plus" type="primary" @click="clickAddButFun">添加白名单</el-button>
      </template>
    </TableHeaderTitle>
    <SiteWhitelistModCom ref="siteWhitelistModComRef" :siteId="siteId" :authority="authority"></SiteWhitelistModCom>
    <div ref="tableContentRef" v-resize="setTableMaxHeight" class="tableContent">
      <el-table :data="list" v-loading="listLoading" :max-height="tableMaxHeight">
        <el-table-column align="center" label="序号" type="index" width="80"></el-table-column>
        <el-table-column align="center" label="类型">
          <template #default="{ row }">{{ $filters.authorityType(row.authorityType) }}</template>
        </el-table-column>
        <el-table-column align="center" label="手机号/VIN码">
          <template #default="{ row }">{{ $filters.moreData(row.authorityAccount) }}</template>
        </el-table-column>
        <el-table-column align="center" label="备注" show-overflow-tooltip>
          <template #default="{ row }">{{ $filters.moreData(row.notes) }}</template>
        </el-table-column>
        <el-table-column align="center" label="操作" v-if="authority === 2">
          <template #default="{ row }">
            <div class="table_operate_class flex-jc-ai-center">
              <el-link :underline="false" type="primary" @click="clickOperateBut(1, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">删除</el-link>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AddWhiteListDialog v-if="addWhiteListVisible" v-model:isVisible="addWhiteListVisible" :titleName="titleName" :activeEditInfo="activeEditInfo" @changeEvent="listArray('refresh')" />
  </div>
</template>

<script>
import {Plus} from "@element-plus/icons-vue";
import {ElMessage,ElMessageBox} from "element-plus";
import AddWhiteListDialog from "./CsWhiteList/AddWhiteListDialog.vue";
import SiteWhitelistModCom from "./CsWhiteList/SiteWhitelistModCom.vue";
import {reactive, toRefs, ref, watch, onMounted, defineComponent} from "vue";
import {deleteSiteWhiteRosterById, findSiteWhiteRosterList} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "CsWhiteList",
  components: {AddWhiteListDialog,SiteWhitelistModCom},
  props:{
    siteId:{
      type: [Number,String],
      default:""
    },
    //权限 1-只读 2-读写
    authority:{
      type: Number,
      default: 1
    },
  },
  setup(props) {

    const that = reactive({
      Plus,
      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      activeEditInfo: {},
      titleName: "添加白名单",
      addWhiteListVisible: false,
    });

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType ===  "refresh") that.currentPage = 1;
      findSiteWhiteRosterList({ siteId: props.siteId }).then(res => {
        that.list = res.data;
        that.listLoading = false;
        if(operateType === "resetPage")ElMessage({ type: "success", message: "重置成功", showClose: true });
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return;
        that.list = [];
      });
    };

    const clickAddButFun = ()=>{
      that.titleName = "添加白名单";
      that.activeEditInfo = { siteId: props.siteId };
      that.addWhiteListVisible = true;
    };

    const clickOperateBut = (operateType,row)=>{

      if(operateType === 1){
        that.titleName = "编辑白名单";
        that.activeEditInfo = JSON.parse(JSON.stringify({ siteId: props.siteId,...row }));
        that.addWhiteListVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${ row.authorityAccount }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSiteWhiteRosterById({ whiteId: row.id }).then(()=>{
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
          listArray("refresh");
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableContentRef.value.offsetHeight;
    };

    const watchSiteId = watch(()=>props.siteId,(newSiteId)=>{
      listArray("refresh");
    },{ deep:true });

    onMounted(()=>{
      listArray("refresh");
    });

    return {...toRefs(that), tableContentRef, setTableMaxHeight,listArray,clickAddButFun,watchSiteId,clickOperateBut};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 12px;
  box-sizing: border-box;

  .header-form{
    display: flex;
    align-items: center;
    color: #FFFFFF;
  }
}
</style>