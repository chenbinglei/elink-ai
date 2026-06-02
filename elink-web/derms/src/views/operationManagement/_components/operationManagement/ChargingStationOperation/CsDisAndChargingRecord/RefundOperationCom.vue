<template>
  <div class="refundOperationCom" v-loading="listLoading">
    <el-form :model="form_dialog" :rules="rules" ref="formDialogRef" label-width="120px">
      <el-form-item label="预付金额：">
        <span class="number">{{ $filters.moneyTwoNum(returnDataInfo.prepayMoney) }}</span>
        <span class="unit">元</span>
      </el-form-item>
      <el-form-item label="已退款金额：">
        <span class="number">{{ $filters.moneyTwoNum(returnDataInfo.refundMoney) }}</span>
        <span class="unit">元</span>
      </el-form-item>
<!--      <el-form-item label="退款方式：" prop="refundMethod">-->
<!--        <el-radio-group v-model="form_dialog.refundMethod">-->
<!--          <el-radio v-for="item in refundMethodArray" :key="item.id" :label="item.id">{{ item.name }}</el-radio>-->
<!--        </el-radio-group>-->
<!--      </el-form-item>-->
      <el-form-item label="退款金额：" prop="refundMoney">
        <div class="flex-ai-center">
          <div class="flex-ai-center" style="width: 220px">
            <el-input-number v-model="form_dialog.refundMoney" :min="0" :max="returnDataInfo.maxRefundMoney" controls-position="right" />
            <span class="unit">元</span>
          </div>
          <div class="allRefundMoney" @click="clickAllRefundMoney">
            <span class="fullRefund">退全款</span>
            <span class="refundableMoney">
              <span class="number">{{ $filters.moneyTwoNum(returnDataInfo.maxRefundMoney) }}</span>
              <span class="unit">元</span>
            </span>
          </div>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {defineComponent, reactive, toRefs, ref, onMounted} from "vue";
import {findOrderTradeMoneyById} from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "RefundOperationCom",
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const validateRefundMethod = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择退款方式"));
      } else {
        callback();
      }
    };

    const validateRefundAmount = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入退款金额"));
      } else {
        callback();
      }
    };

    const that = reactive({
      form_dialog: {},
      returnDataInfo: {},
      listLoading: false,
      refundMethodArray: [{id: 1, name: "原路退回"}],
      rules:{
        refundMethod: [{required: true, trigger: "change", validator: validateRefundMethod }],
        refundMoney: [{required: true, trigger: "change", validator: validateRefundAmount }],
      }
    });

    const formDialogRef = ref(null);
    const clickAllRefundMoney = ()=>{
      that.form_dialog.refundMoney = that.returnDataInfo.maxRefundMoney;
    };

    const queryOrderTradeMoneyById = ()=>{
      that.listLoading = true;
      findOrderTradeMoneyById({ orderId: props.orderInfo.id }).then(res=>{
        that.returnDataInfo = res.data ?? {};
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    onMounted(()=>{
      queryOrderTradeMoneyById();
    });

    return {...toRefs(that),clickAllRefundMoney,formDialogRef,queryOrderTradeMoneyById};
  }
});

</script>

<style lang="scss" scoped>
.refundOperationCom{
  padding: 24px 0 12px 0;
  box-sizing: border-box;

  .unit{
    margin-left: 4px;
  }

  .allRefundMoney{
    cursor: pointer;
    color: #079CEB;
    margin-left: 12px;

    .refundableMoney{
      font-size: 12px;
      //margin-left: 4px;

      .number{
        color: #079CEB;
        margin-left: 4px;
      }
    }
  }
}
</style>