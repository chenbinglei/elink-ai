<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="580" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <AccountInfoFormCom ref="accountInfoFormComRef" componentName="TransactionLedgerCom" :activeEditDataInfo="activeEditDataInfo" :activeSiteId="activeSiteId" />
      </div>
    </template>
  </Dialog>
</template>

<script>
import AccountInfoFormCom from "./AccountInfoFormCom.vue";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";
import {saveSiteAccount} from "@/api/operationManagement/CsSettlementManagement";
import {ElMessage} from "element-plus";

export default defineComponent({
  name:"AddTransactionLedgerDialog",
  components:{AccountInfoFormCom},
  props:{
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
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "分账配置",
      dialog_visible: props.isVisible,
    });

    const accountInfoFormComRef = ref(null);
    const clickConfirmBut = ()=>{
      accountInfoFormComRef.value.formDialogRef.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(accountInfoFormComRef.value.formDialog));
          if(formDialog.ratio !== undefined && !formDialog.ratio) formDialog.ratio = "";
          saveSiteAccount({ ...formDialog,siteId: props.activeSiteId,type: 3 }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{};

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, accountInfoFormComRef, clickConfirmBut};
  }
});
</script>

<style scoped lang="scss">

</style>