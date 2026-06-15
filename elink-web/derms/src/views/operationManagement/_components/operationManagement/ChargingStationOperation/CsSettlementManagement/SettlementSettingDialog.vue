<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="720" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <Tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray"></Tabs>
        <component ref="componentRef" :is="componentName" :componentName="componentName" :activeSiteId="activeSiteId"></component>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import PaymentAccountCom from "./AccountInfoFormCom.vue";
import ReceivingAccountCom from "./AccountInfoFormCom.vue";
import TransactionLedgerCom from "./TransactionLedgerCom.vue";
import {saveSiteAccount} from "@/api/operationManagement/CsSettlementManagement";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name: "SettlementSettingDialog",
  components:{ReceivingAccountCom,PaymentAccountCom,TransactionLedgerCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeSiteId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "结算设置",
      dialog_visible: props.isVisible,
      componentName: "ReceivingAccountCom",
      tabsArray: [{id: "ReceivingAccountCom", name: "收款账户"}, {id: "PaymentAccountCom", name: "付款账户"}], // , {id: "TransactionLedgerCom", name: "交易分账"}
    });

    const componentRef = ref(null);
    const clickConfirmBut = () => {
      if(that.componentName === "ReceivingAccountCom" || that.componentName === "PaymentAccountCom"){
        componentRef.value.formDialogRef.validate((valid) => {
          if (valid) {
            that.listLoading = true;
            let formDialog = JSON.parse(JSON.stringify(componentRef.value.formDialog));
            if(formDialog.ratio !== undefined && !formDialog.ratio) formDialog.ratio = "";
            formDialog.type = that.componentName === "ReceivingAccountCom" ? 1 : 2;
            saveSiteAccount({ ...formDialog,siteId: props.activeSiteId }).then(()=>{
              emit("changeEvent");
              that.dialog_visible = false;
              ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            }).catch(()=>{
              that.listLoading = false;
            });
          }
        });
      }

      // 交易分账
      if(that.componentName === "TransactionLedgerCom"){
        that.dialog_visible = false;
      }
    };

    const initParamConfigFun = ()=>{};

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, componentRef};
  }
});
</script>

<style scoped lang="scss">

</style>