<template>
  <Dialog v-model:isVisible="dialog_visible" :cancelText="cancelText" :confirmText="confirmText" :listLoading="listLoading" :manualCancelClose="false" :manualEnterClose="false"
          :title="titleName" append-to-body disabledLoading width="620" @confirm="saveDialog" @cancel="clickCancelButFun">
    <template v-slot:content>
      <div class="dialog-main">
        <div class="content_body_top">
          <CustomSteps :stepsArray="stepsArray" v-model:active="stepsActiveIndex"></CustomSteps>
        </div>
        <div class="content_body_bottom">
          <OperationPasswordInput v-show="stepsActiveIndex === 1" ref="operationPasswordInputRef"></OperationPasswordInput>
          <RefundOperationCom v-if="stepsActiveIndex === 2" ref="refundOperationComRef" :orderInfo="orderInfo"></RefundOperationCom>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import RefundOperationCom from "./RefundOperationCom.vue";
import CustomSteps from "@/components/component/CustomSteps.vue";
import OperationPasswordInput from "./OperationPasswordInput.vue";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref} from "vue";
import {checkSitePassword, orderRefund} from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "RepairAndRefundOrderDialog",
  components: {CustomSteps,OperationPasswordInput,RefundOperationCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 交易订单类型 1-充放电订单 2-占用订单
    tradeOrderType: {
      type: [String,Number],
      default: 1
    },
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      cancelText: "取消",
      listLoading: false,
      confirmText: "下一步",
      titleName: "补单/退款",
      dialog_visible: props.isVisible,

      stepsActiveIndex: 1, // 当前操作
      stepsArray: [{id: 1, iconTitle: 1, title: "操作鉴权"}, {id: 2, iconTitle: 2, title: "退款"}]
    });

    const refundOperationComRef = ref(null);
    const operationPasswordInputRef = ref(null);
    const saveDialog = () => {

      if(that.stepsActiveIndex === 1){
        let form_dialog = operationPasswordInputRef.value.form_dialog;
        if(!form_dialog.password){
          ElMessage({type: "warning", showClose: true, message: "请输入操作密码！"});
          return;
        }

        if(form_dialog.password.length < form_dialog.unit){
          ElMessage({type: "warning", showClose: true, message: "请输入四位操作密码！"});
          return;
        }
        that.listLoading = true;
        checkSitePassword({ ...form_dialog,siteId: props.orderInfo.siteId }).then((res)=>{
          if(res.data){
            that.stepsActiveIndex = 2;
            that.confirmText = "确定";
            that.cancelText = "上一步";
            that.listLoading = false;
          } else {
            that.listLoading = false;
            ElMessage({type: "error", showClose: true, message: "请输入正确的操作密码！"});
          }
        }).catch(()=>{
          that.listLoading = false;
        });
      }

      // 进行退款操作
      if(that.stepsActiveIndex === 2){
        refundOperationComRef.value.formDialogRef.validate((valid) => {
          if (valid) {
            that.listLoading = true;
            let form_dialog = refundOperationComRef.value.form_dialog;
            orderRefund({...form_dialog,tradeOrderType: props.tradeOrderType,orderId: props.orderInfo.id }).then(()=>{
              emit("changeEvent");
              that.dialog_visible = false;
              ElMessage({type: "success", showClose: true, message: "操作成功！"});
            }).catch(()=>{
              that.listLoading = false;
            });
          }
        });
      }
    };

    // 返回上一步
    const clickCancelButFun = ()=>{
      if(that.stepsActiveIndex === 1){
        that.dialog_visible = false;
      }

      if(that.stepsActiveIndex === 2){
        that.cancelText = "取消";
        that.stepsActiveIndex = 1;
        that.confirmText = "下一步";
      }
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, saveDialog, operationPasswordInputRef, clickCancelButFun, refundOperationComRef};
  }
});
</script>

<style lang="scss" scoped>
.content_body_top{
  padding: 0 32px 16px 32px;
  box-sizing: border-box;
}
</style>