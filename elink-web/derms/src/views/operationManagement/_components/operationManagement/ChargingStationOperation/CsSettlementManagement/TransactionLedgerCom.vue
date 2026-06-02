<template>
  <div class="transactionLedgerCom">
    <div class="content_top">
      <div class="content_top_left">分账上限：{{ sliderMaxValue }}%</div>
      <div class="content_top_right">
        <el-button :icon="Plus" type="primary" @click="clickAddButtonFun">添加分账账户</el-button>
      </div>
    </div>
    <div class="content_bottom">
      <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" stripe>
        <el-table-column fixed="left" label="序号" type="index" width="80"></el-table-column>
        <el-table-column label="分账商户" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="platformName">{{ $filters.platformType(row.payPlatform) }}：</span>
            <span class="mchName">{{ $filters.moreData(row.mchName) }}</span>
            <span class="mchId">（{{ $filters.moreData(row.mchId) }}）</span>
          </template>
        </el-table-column>
        <el-table-column label="分账比例" align="center">
          <template #default="{ row }">{{ $filters.moreData(row.ratio) }}%</template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="{ row }">
            <div class="table_operate_class flex-jc-ai-center">
              <el-link :underline="false" @click="clickOperateBut(1, row)">编辑</el-link>
              <span class="split_line">|</span>
              <el-link :underline="false" @click="clickOperateBut(2, row)">删除</el-link>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AddTransactionLedgerDialog v-if="settlementSettingVisible" v-model:isVisible="settlementSettingVisible" :activeSiteId="activeSiteId"
                                :activeEditDataInfo="activeEditDataInfo" @changeEvent="initParamConfigFun" />
  </div>
</template>

<script>
import {Plus} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from "element-plus";
import {reactive, toRefs, defineComponent, onMounted} from "vue";
import AddTransactionLedgerDialog from "./AddTransactionLedgerDialog.vue";
import {deleteSiteAccountById, findSiteAccountListBySiteIdAndType} from "@/api/operationManagement/CsSettlementManagement";

export default defineComponent({
  name: "TransactionLedgerCom",
  components:{AddTransactionLedgerDialog},
  props: {
    activeSiteId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {
    const that = reactive({
      Plus,
      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      residueMaxValue: 0, // 剩余分账比例
      sliderMaxValue: 30, // 最大分账比例
      activeEditDataInfo: {},
      settlementSettingVisible: false,
    });

    const initParamConfigFun = () => {
      that.listLoading = true;
      findSiteAccountListBySiteIdAndType({siteId:props.activeSiteId,type: 3,timer: new Date()}).then(res=>{
        let list = res.data ? res.data : [];
        let residueMaxValue = that.sliderMaxValue;
        for(let i = 0;i < list.length;i++) residueMaxValue = residueMaxValue - list[i].ratio;
        that.list = JSON.parse(JSON.stringify(list));
        that.residueMaxValue = residueMaxValue;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const clickAddButtonFun = ()=>{
      that.activeEditDataInfo = { residueMaxValue: that.residueMaxValue };
      that.settlementSettingVisible = true;
    };

    const clickOperateBut = (operateType, row) => {

      if(operateType === 1){
        let activeEditDataInfo = JSON.parse(JSON.stringify(row));
        activeEditDataInfo.residueMaxValue = that.residueMaxValue + row.ratio;
        that.activeEditDataInfo = JSON.parse(JSON.stringify(activeEditDataInfo));
        that.settlementSettingVisible = true;
      }

      if(operateType === 2){
        ElMessageBox.confirm(`确定删除账户（<span class="deleteName">${ row.mchName }</span>）分账吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSiteAccountById({ id: row.id }).then(()=>{
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
          initParamConfigFun();
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), initParamConfigFun, clickOperateBut, clickAddButtonFun};
  }
});
</script>

<style lang="scss" scoped>
.transactionLedgerCom {
  padding-top: 12px;
  box-sizing: border-box;

  .content_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }
}
</style>