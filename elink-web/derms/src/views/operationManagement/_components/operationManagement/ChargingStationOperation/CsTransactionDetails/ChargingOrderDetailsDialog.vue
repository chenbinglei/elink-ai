<template>
  <Dialog v-model:isVisible="dialog_visible" :cancelVisible="false" :title="titleName" width="65vw">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main">
        <TitleView v-for="(item,index) in list" :key="index" :title="item.name">
          <template #content>
            <el-row :gutter="12" class="content_list">
              <template v-for="(child,i) in item.children" :key="i">
                <template v-if="orderType !== 2 || orderType === 2 && child.fieldName !== 'totalFee'">
                  <el-col :span="8" class="info_li">
                    <div class="flex_li_left">{{ $filters.chargingDisText(child.name, orderType) }}：</div>
                    <div :class="[child.className ? child.className : '', child.className ? child.className + returnDataInfo[child.fieldName] : '' ]" class="flex_li_right">
                      <span v-if="child.filterName">{{$filters[child.filterName](returnDataInfo[child.fieldName])}}</span>
                      <span v-else>{{ $filters.moreData(returnDataInfo[child.fieldName]) }}</span>
                      <span class="unit" v-if="child.unit">{{ child.unit }}</span>
                      <template v-if="child.fieldName === 'phoneNum' && returnDataInfo[child.fieldName]">
                        <div class="yanJingIcon" @click="clickYanJingIconFun(child)">
                          <span class="iconfont icon-yanjing_xianshi_o" v-if="child.isYanJingStatus"></span>
                          <span class="iconfont icon-yanjing_yincang_o" v-else></span>
                        </div>
                      </template>
                    </div>
                  </el-col>
                </template>
              </template>
            </el-row>
          </template>
        </TitleView>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";
import {findRechargeTradeById} from "@/api/operationManagement/CsTransactionDetails";

export default defineComponent({
  name: "OrderDetailsDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 复制时段使用
    activeOrderId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      returnDataInfo: {},
      listLoading: false,
      titleName: "充电订单详情",
      dialog_visible: props.isVisible,

      list: [{
        name: "订单信息",
        children: [
          {name: "订单号", fieldName: "orderNum"},
          {name: "交易流水号", fieldName: "flowNum"},
          {name: "商户平台", fieldName: "tradeWay", filterName: "platformType"},
          {name: "支付时间", fieldName: "createTime"},
          {name: "完成时间", fieldName: "updateTime"},
          {name: "交易金额", fieldName: "tradeMoney", unit: "元"},
          {name: "交易类型", fieldName: "tradeType", filterName: "tradeCoType"},
          {name: "交易状态", fieldName: "tradeStatus", filterName: "tradeStatus", unit: "", className: "tradeStatus"},
          {name: "站点名称", fieldName: "siteName"},
        ]
      }, {
        name: "付款方",
        children: [
          {name: "用户ID", fieldName: "appletUserId"},
          {name: "用户名称", fieldName: "nickName"},
          {name: "手机号", fieldName: "phoneNum", filterName: "phoneFourRep",isYanJingStatus: false},
        ]
      },
        {
          name: "收款方",
          children: [
            {name: "企业名称", fieldName: "tenantName"},
            {name: "商户名称", fieldName: "mchName"},
            {name: "商户ID", fieldName: "mchId"},
          ]
        },
      ]
    });

    // 根据主键id查询充电交易详情
    const initParamConfigFun = () => {
      that.listLoading = true;
      findRechargeTradeById({ id: props.activeOrderId,timer: new Date() }).then(res=>{
        that.returnDataInfo = res.data ? res.data : {};
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
      });
    };

    // 显示手机号
    const clickYanJingIconFun = (child)=>{
      child.isYanJingStatus = !child.isYanJingStatus;
      child.filterName = child.isYanJingStatus ? 'moreData' : 'phoneFourRep';
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible,clickYanJingIconFun};
  }
});
</script>

<style lang="scss" scoped>
.content_list {
  padding: 0 12px;
  box-sizing: border-box;

  .info_li {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .flex_li_left {
      color: #d3ecfb;
      font-size: 14px;
      white-space: nowrap;
    }

    .flex_li_right {
      color: #d3ecfb;
      font-size: 14px;
      display: flex;
      align-items: center;

      span {
        word-wrap: break-word;
        overflow-wrap: anywhere;
      }

      .yanJingIcon{
        margin-left: 4px;
        cursor: pointer;

        .iconfont{
          font-size: 18px;
          color: #007FEB;
        }
      }
    }

    .tradeStatus{
      color: #EDA300;
    }

    .tradeStatus2{
      color: #41CB4A;
    }

    .tradeStatus3{
      color: #FF1515;
    }

    //.orderStatus6{
    //  color: #00B3EB;
    //}
  }
}
</style>