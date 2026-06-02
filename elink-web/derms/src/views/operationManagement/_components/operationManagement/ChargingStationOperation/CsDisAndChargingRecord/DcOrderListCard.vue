<template>
  <div class="content_list">
    <div class="content_li_top">
      <div class="orderNum">
        <span class="title">订单号：</span>
        <span class="number">{{ $filters.moreData(orderInfo.orderNum) }}</span>
      </div>
      <template v-if="orderInfo.abnormalCode && orderInfo.abnormalCode.length">
        <template v-for="(item,index) in orderInfo.abnormalCode" :key="index">
          <template v-if="item && item !== '[' && item !== ',' && item !== ']'">
            <el-tooltip effect="dark" placement="top-start">
              <template #content>{{ $filters.abnormalTypeAlterText(item) }}</template>
              <div class="abnormal_liable">
                <span class="abnormal_text">{{ $filters.abnormalType(item) }}</span>
              </div>
            </el-tooltip>
          </template>
        </template>
      </template>
    </div>
    <div class="content_li_bottom">
      <div class="table_li">
        <span class="text">{{ $filters.moreData(orderInfo.siteName) }}</span>
      </div>
      <div class="table_li">
        <div class="table_li_top" style="text-align: left">{{ $filters.moreData(orderInfo.duration) }}</div>
        <el-timeline>
          <template v-for="(activity, index) in activities" :key="index">
            <el-timeline-item :color="activity.color" :hollow="activity.hollow" :type="activity.type">
              <span class="timestamp">{{ $filters.moreData(orderInfo[activity.timestamp]) }}</span>
            </el-timeline-item>
          </template>
        </el-timeline>
      </div>
      <div class="table_li">
        <span>{{ $filters.moreData(orderInfo.totalQt) }}</span>
        <span class="unit">度</span>
      </div>
      <div class="table_li">
        <div class="flex jc-center">
          <div :class="'order_status' + orderInfo.orderStatus" class="text order_status">
            <span>{{ $filters.chargingDisText($filters.orderStatus(orderInfo.orderStatus), orderType) }}</span>
          </div>
        </div>
      </div>
      <div class="table_li">
        <span class="money">¥</span>
        <span class="text">{{ $filters.moneyTwoNum(orderInfo.totalCost) }}</span>
      </div>
      <div class="table_li" v-if="componentName === 'CsChargingRecord'">
        <span class="money">¥</span>
        <span class="text">{{ $filters.moneyTwoNum(orderInfo.actualTotalCost) }}</span>
      </div>
      <div class="table_li">
        <span class="text">{{ $filters.moreData(orderInfo.stopReason) }}</span>
      </div>
      <div class="table_li">
        <span class="text">{{ $filters.moreData(orderInfo.platformName) }}</span>
      </div>
      <div class="table_operate_class table_li">
        <el-link :underline="false" type="primary" @click="clickOperateBut(1)">
          <span class="iconfont icon-dingdanguanli"></span>
          <span>详情</span>
        </el-link>
        <span class="split_line">|</span>
        <el-dropdown>
          <el-button :icon="MoreFilled" plain size="small"></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <template v-if="componentName === 'CsChargingRecord'">
                <template v-if="orderInfo.prepayMoney > 0">
                  <el-dropdown-item :disabled="orderInfo.orderStatus === 1 || orderInfo.orderStatus === 6" @click="clickOperateBut(2)">
                    <span>补单/退款</span>
                  </el-dropdown-item>
                </template>
                <template v-else>
                  <el-dropdown-item :disabled="orderInfo.orderStatus === 1 || orderInfo.orderStatus === 6" @click="clickOperateBut(3)">
                    <span>补单/退款</span>
                  </el-dropdown-item>
                </template>
              </template>
              <template v-if="componentName === 'CsDisChargingRecord'">
                <el-dropdown-item :disabled="orderInfo.orderStatus === 1 || orderInfo.orderStatus === 6" @click="clickOperateBut(3)">
                  <span>补单/退款</span>
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <RepairAndRefundOrderDialog v-if="repairAndRefundOrderVisible" v-model:isVisible="repairAndRefundOrderVisible" :orderInfo="orderInfo" @changeEvent="changeEvent" />
  </div>
</template>

<script>
import {useStore} from "vuex";
import {MoreFilled} from '@element-plus/icons-vue';
import {queryUserAuthorityIsHaveFun} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import RepairAndRefundOrderDialog from "./RepairAndRefundOrderDialog.vue";
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";
import {updateOrderStatus} from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "DcOrderListCard",
  components:{RepairAndRefundOrderDialog},
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    componentName: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const store = useStore();
    const {emit} = getCurrentInstance();

    const that = reactive({
      MoreFilled,
      orderType: 1, // 1：充 2：放
      abnormalType: [],
      repairAndRefundOrderVisible: false,
      activities: [{timestamp: 'startTime', color: '#41CB4A'}, {timestamp: 'endTime', type: 'primary', hollow: true}]
    });

    const clickOperateBut = (operateType) => {

      if (operateType === 1) {
        const routeName = "/operationManagement/CsOrderRecordDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return;
        }

        store.dispatch("updateSecondaryInfo", {
          type: that.orderType,
          id: props.orderInfo.id,
          componentName: "CsOrderRecordDetails",
          backComponentName: "CsDisAndChargingRecord",
          subTitle: `${that.orderType === 2 ? '放' : '充'}电记录详情`,
        });
        store.dispatch("updateSecondaryVisible", true);
      }

      if (operateType === 2) {
        that.repairAndRefundOrderVisible = true;
      }

      if (operateType === 3) {
        let confirmTitle = `该订单为免支付订单，确定结算该订单吗？`;
        if(props.componentName === "CsDisChargingRecord") confirmTitle = `该订单已挂起，确定结算该订单吗？`;
        ElMessageBox.confirm(confirmTitle, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', showClose: false,
          closeOnClickModal: false, beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在操作...';
              updateOrderStatus({orderId: props.orderInfo.id}).then(() => {
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
          changeEvent();
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    const changeEvent = (data)=>{
      emit("changeEvent",data);
    };

    const watchComponentName = watch(() => props.componentName, (newComponentName) => {
      that.orderType = newComponentName === "CsDisChargingRecord" ? 2 : 1;
    }, {deep: true, immediate: true});

    return {...toRefs(that), clickOperateBut, watchComponentName, changeEvent};
  }
});
</script>

<style lang="scss" scoped>
.content_list {
  width: 100%;
  border-radius: 8px;
  background: #003d6033;
  box-sizing: border-box;

  .content_li_top {
    color: #d3ecfb;
    background: #00a3ff4d;
    box-sizing: border-box;
    border-radius: 8px 8px 0 0;
    padding: 10px 16px 9px 16px;

    display: flex;
    align-items: center;
    justify-content: flex-start;

    .abnormal_liable{
      padding: 0 8px;
      margin-left: 6px;
      border-radius: 4px;
      box-sizing: border-box;
      background: rgba(254, 57, 57, .9);

      .abnormal_text{
        color: #FFFFFF;
        font-size: 12px;
      }
    }
  }

  .content_li_bottom {
    padding-top: 21px;
    box-sizing: border-box;
    display: flex;

    .table_operate_class {
      display: initial;
    }

    .table_li {
      flex: 1;
      color: #d3ecfb;
      padding: 0 12px;
      box-sizing: border-box;

      .order_status {
        width: fit-content;
        font-size: 14px;
        padding: 5px 20px;
        border-radius: 8px;
        background: #0094ff1a;
        box-sizing: border-box;
      }

      .order_status1 {
        color: #FF9C02;
        background: rgba(255, 156, 2, .1);
      }

      .order_status2 {
        color: #41CB4A;
        background: rgba(65, 203, 74, .1);
      }

      .order_status4 {
        color: #FF1515;
        background: rgba(255, 21, 21, .1);
      }

      .order_status6 {
        color: #00B3EB;
        background: rgba(0, 179, 235, .1);
      }

      .unit {
        font-size: 14px;
      }

      .iconfont {
        color: #007FEB;
        margin-right: 6px;
      }

      :deep(.el-timeline) {
        margin-top: 10px;
        --el-color-white: transparent;

        .timestamp {
          color: #d3ecfb;
          white-space: nowrap;
        }

        .el-timeline-item{
          &:last-child{
            padding-bottom: 8px;
          }
        }
      }
    }
  }
}
</style>