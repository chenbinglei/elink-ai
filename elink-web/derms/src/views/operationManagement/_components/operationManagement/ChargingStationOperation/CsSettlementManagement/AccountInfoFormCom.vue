<template>
  <div class="accountInfoFormCom">
    <el-form ref="formDialogRef" :disabled="listLoading" :model="formDialog" :rules="rules" label-width="130px">
      <el-form-item label="选择企业：" prop="tenantId">
        <el-select v-model="formDialog.tenantId" placeholder="请选择企业" @change="clearAccountInfoFun">
          <el-option v-for="(item,index) in tenantIdArray" :key="index" :label="item.tenantName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商户平台：" prop="payPlatform">
        <el-select v-model="formDialog.payPlatform" placeholder="请选择商户平台" @change="clearAccountInfoFun">
          <el-option v-for="(item,index) in payPlatformArray" :key="index" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择账户：" prop="accountId">
        <el-select v-model="formDialog.accountId" placeholder="请选择账户" :disabled="!formDialog.tenantId || !formDialog.payPlatform" @change="accountIdChaneFun">
          <el-option v-for="(item,index) in accountIdArray" :key="index" :label="item.mchName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item v-if="formDialog.accountId">
        <div class="account_info_class">
          <div class="account_info_list">
            <span>商户全称：</span>
            <span>{{ $filters.moreData(activeAccountInfo.mchName) }}</span>
          </div>
          <div class="account_info_list">
            <span>商户号：</span>
            <span>{{ $filters.moreData(activeAccountInfo.mchId) }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item v-if="componentName === 'TransactionLedgerCom'" label="分账比例：" prop="ratio" >
        <el-input-number v-model="formDialog.ratio" :max="formDialog.residueMaxValue" precision="2" controls-position="right" placeholder="请输入分账比例"/>
        <div class="alter_text">当前剩余最大分账比例：{{ formDialog.residueMaxValue }}%</div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
import {pay_plat_form_array} from "@/utils/setVariate";
import {reactive, toRefs, defineComponent, ref, onMounted, watch} from "vue";
import {findSiteAccountListBySiteIdAndType, findTenantAccountList, findTenantListByUserId} from "@/api/operationManagement/CsSettlementManagement";

export default defineComponent({
  name: "AccountInfoFormCom",
  props: {
    componentName: {
      type: String,
      default: "ReceivingAccountCom"
    },
    activeSiteId: {
      type: [Number, String],
      default: ""
    },
    activeEditDataInfo: {
      type: Object,
      default: ()=>{
        return {};
      }
    },
  },
  setup(props) {

    const validateTenantId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择企业"));
      } else {
        callback();
      }
    };

    const validatePayPlatform = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择商户平台"));
      } else {
        callback();
      }
    };

    const validateAccountId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择账户"));
      } else {
        callback();
      }
    };

    const validateRatio = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入分账比例"));
      } else {
        callback();
      }
    };

    const formDialogRef = ref(null);
    const that = reactive({
      formDialog: {},
      tenantIdArray: [],
      accountIdArray: [],
      listLoading: false,
      activeAccountInfo: {},
      payPlatformArray: pay_plat_form_array,

      rules: {
        ratio: [{required: true, trigger: "change", validator: validateRatio }],
        tenantId: [{required: true, trigger: "change", validator: validateTenantId }],
        accountId: [{required: true, trigger: "change", validator: validateAccountId }],
        payPlatform: [{required: true, trigger: "change", validator: validatePayPlatform }],
      }
    });

    const clearAccountInfoFun = ()=>{
      that.formDialog.accountId = "";
      that.accountIdArray = [];
      queryTenantAccountList();
    };

    // 根据租户id查询租户账户信息
    const queryTenantAccountList = ()=>{
      if(!that.formDialog.tenantId || !that.formDialog.payPlatform) return;
      let formDialog = JSON.parse(JSON.stringify(that.formDialog));
      formDialog.platformType = formDialog.payPlatform;
      findTenantAccountList({ ...formDialog }).then(res=>{
        that.accountIdArray = res.data ? res.data : [];
        accountIdChaneFun();
      });
    };

    // 商户发生改变执行
    const accountIdChaneFun = ()=>{
      let findItem = that.accountIdArray.find(item=> item.id === that.formDialog.accountId);
      if(findItem){
        // that.formDialog.payPlatform = findItem.platformType;
        that.activeAccountInfo = JSON.parse(JSON.stringify(findItem));
      }
    };

    // 根据站点id和类型查询站点账户数据
    const querySiteAccountListBySiteIdAndType = ()=>{

      // 收款账户  付款账户
      if(props.componentName === "ReceivingAccountCom" || props.componentName === "PaymentAccountCom"){
        that.listLoading = true;
        let type = props.componentName === "ReceivingAccountCom" ? 1 : 2;
        findSiteAccountListBySiteIdAndType({siteId: props.activeSiteId,type: type,timer: new Date() }).then(res=>{
          let returnDataInfo = res.data && res.data.length ? res.data[0] : {};
          that.formDialog = JSON.parse(JSON.stringify(returnDataInfo));
          queryTenantAccountList();
          that.listLoading = false;
        }).catch(()=>{
          that.listLoading = false;
        });
      }

      // 交易分账
      if(props.componentName === "TransactionLedgerCom"){
        // console.log(props.activeEditDataInfo);
        that.formDialog = Object.assign({},props.activeEditDataInfo,that.formDialog);
        queryTenantAccountList();
      }
    };

    // 根据用户id查询所有租户列表数据
    const initParamConfigFun = ()=>{
      findTenantListByUserId({ timer: new Date() }).then(res=>{
        that.tenantIdArray = res.data;
      });
    };

    const watchComponentName = watch(()=>props.componentName,(newComponentName)=>{
      querySiteAccountListBySiteIdAndType();
    },{ deep: true });

    onMounted(()=>{
      initParamConfigFun();
      querySiteAccountListBySiteIdAndType();
    });

    return {...toRefs(that), initParamConfigFun, queryTenantAccountList, formDialogRef, clearAccountInfoFun, watchComponentName, accountIdChaneFun};

  }
});

</script>

<style lang="scss" scoped>
.accountInfoFormCom{
  padding-top: 12px;
  box-sizing: border-box;

  .account_info_class{
    width: 100%;
    max-width: 520px;
    padding: 2px 12px;
    border-radius: 6px;
    background: #ffffff26;
    box-sizing: border-box;
  }

  .alter_text{
    color: rgba(255,255,255,.6);
  }
}
</style>