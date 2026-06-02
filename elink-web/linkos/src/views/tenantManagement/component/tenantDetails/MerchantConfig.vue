<template>
  <div class="tableContent" v-resize="setTableMaxHeight">
    <el-table :data="list" v-loading="listLoading" :max-height="tableMaxHeight">
      <el-table-column align="center" label="商户名称">
        <template #default="{ row }">{{ $filters.moreData(row.mchName) }}</template>
      </el-table-column>
      <el-table-column align="center" label="商户号">
        <template #default="{ row }">{{ $filters.moreData(row.mchId) }}</template>
      </el-table-column>
      <el-table-column align="center" label="平台类型">
        <template #default="{ row }">{{ $filters.platformType(row.platformType) }}</template>
      </el-table-column>
      <template v-if="isEditCompany">
        <el-table-column align="center" label="操作" width="150px">
          <template #default="{ row }">
            <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
            <span class="split_line">|</span>
            <el-link type="danger" :underline="false" @click="clickOperateBut(2,row)">删除</el-link>
            <span></span>
          </template>
        </el-table-column>
      </template>
    </el-table>

    <AddMerchantDialog v-if="addMerchantVisible" v-model:isVisible="addMerchantVisible" :titleName="titleName" :activeEditInfo="activeEditInfo" @changeEvent="queryTenantAccountList" />
  </div>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import AddMerchantDialog from "./MerchantConfig/AddMerchantDialog.vue";
import {computed, onMounted, reactive, toRefs, watch, defineComponent} from "vue";
import {deleteTenantAccountById, findTenantAccountList} from "@/api/tenantManagement/merchantConfig";

export default defineComponent({
  name: "MerchantConfig",
  components:{AddMerchantDialog},
  props: {
    tenantId: {
      type: [String, Number],
      default: ""
    },
    isEditCompany:{
      type: Boolean,
      default: false
    }
  },
  setup(props){

    const store = useStore();
    const contentMainMaxHeight = computed(() => {
      return store.state.app.contentMainMaxHeight;
    });

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      activeEditInfo: {},
      titleName: "新增商户",
      addMerchantVisible: false,
    })


    // 根据租户id查询租户账户信息
    const queryTenantAccountList = ()=>{
      that.listLoading = true;
      findTenantAccountList({ tenantId: props.tenantId,timer: new Date() }).then(res=>{
        that.list = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const clickButtonFun = ()=>{
      that.titleName = "新增商户";
      that.activeEditInfo = {tenantId: props.tenantId};
      that.addMerchantVisible = true;
    }

    const clickOperateBut = (operateType, row)=>{

      if(operateType === 1){
        that.titleName = "编辑商户";
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.addMerchantVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除该商户（<span class="deleteName">${ row.mchName }</span>）配置吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteTenantAccountById({ id: row.id }).then(()=>{
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
          queryTenantAccountList();
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    // 初始化表格高度
    const setTableMaxHeight = () => {
      that.tableMaxHeight = contentMainMaxHeight.value - 100;
    }

    const watchTenantId = watch(()=>props.tenantId,(newTenantId)=>{
      queryTenantAccountList();
    },{ deep: true })

    onMounted(()=>{
      queryTenantAccountList();
    })

    return { ...toRefs(that), clickButtonFun, setTableMaxHeight, queryTenantAccountList, watchTenantId, clickOperateBut }
  }
})
</script>

<style scoped lang="scss">
.content_body{
  border: none;
  padding: 12px 0 0 0;
  box-sizing: border-box;
}
</style>