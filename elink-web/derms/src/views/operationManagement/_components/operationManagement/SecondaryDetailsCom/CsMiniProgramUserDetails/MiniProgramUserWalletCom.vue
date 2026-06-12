<template>
  <div class="miniProgramUserWalletCom" v-loading="listLoading">
    <el-row :gutter="12" v-if="list && list.length">
      <template v-for="(item, index) in list" :key="index">
        <el-col :lg="6" :md="8" :sm="12" :xs="24">
          <div class="account_list_li">
            <div class="account_list_li_top">
              <div class="account_list_li_left">
                <span class="iconfont icon-qianbao"></span>
              </div>
              <div class="account_list_li_center">
                <div class="account_list_li_text">
                  <div class="list_li_text_left">关联账户：</div>
                  <div class="list_li_text_right">{{ $filters.moreData(item.mchId) }}</div>
                </div>
                <div class="account_list_li_text">
                  <div class="list_li_text_left">关联商户：</div>
                  <div class="list_li_text_right">{{ $filters.moreData(item.mchName) }}</div>
                </div>
                <div class="account_list_li_text">
                  <div class="list_li_text_left">冻结金额：</div>
                  <div class="list_li_text_right">
                    <span class="text">{{ $filters.moneyTwoNum(item.freezeMoney) }}</span>
                    <span class="unit">元</span>
                  </div>
                </div>
                <div class="account_list_li_text">
                  <div class="list_li_text_left">余额：</div>
                  <div class="list_li_text_right">
                    <span class="text">{{ $filters.moneyTwoNum(item.balance) }}</span>
                    <span class="unit">元</span>
                  </div>
                </div>
              </div>
              <!--              <div class="account_list_li_right pointer" @click.stop="clickOperateBut(1,item)">-->
              <!--                <span class="iconfont icon-zc-tishi"></span>-->
              <!--              </div>-->
            </div>
            <div class="account_list_li_bottom" @click="clickOperateBut(2, item)">
              <span class="text">交易明细</span>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>
    <template v-else><null-data words="暂无关联的放电钱包"></null-data></template>
    <!--    <CanBeUsedSiteDialog v-if="verificationCodeVisible" v-model:isVisible="verificationCodeVisible" :siteNameList="siteNameList" />-->
  </div>
</template>

<script>
import { useOperationManagementStore } from '@/stores/index';

import { ElMessage } from "element-plus";
import { queryUserAuthorityIsHaveFun } from "@/utils";
import { defineComponent, getCurrentInstance, onMounted, reactive, toRefs } from "vue";
import { findUserDisWalletListById } from "@/api/operationManagement/CsMiniProgramUserDetails";
import { useRouter } from "vue-router";
// import CanBeUsedSiteDialog from "./MiniProgramUserWalletCom/CanBeUsedSiteDialog.vue";

export default defineComponent({
  name: "MiniProgramUserWalletCom",
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup (props) {
    const vueRouter = useRouter();
    const operationManagementStore = useOperationManagementStore();
    const { emit } = getCurrentInstance();

    const that = reactive({
      list: [],
      siteNameList: [],
      listLoading: false,
      verificationCodeVisible: false,
    });

    const queryUserDisWalletListById = () => {
      that.listLoading = true;
      findUserDisWalletListById({ appletUserId: props.routeInfo.id }).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const clickOperateBut = (operateType, item) => {
      // 
      if (operateType === 1) {
        that.siteNameList = item.siteNameList;
        that.verificationCodeVisible = true;
      }

      if (operateType === 2) {
        console.log(operateType, item, '跳转的数据');
        const routeName = "/operationManagement/CsTransactionDetails";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({ type: "warning", showClose: true, message: "请联系管理员打开对应权限！" });
          return;
        }
        emit("changEvent", {
          operateType: "switchComponents",
          componentName: "CsTransactionDetails",
          tradeWay: 1,
          accountId: item.accountId,
          childComponentName: "CsTransactionV2GWalletOrder",
        });
        vueRouter.push({
          path: routeName,
          query: {
            operateType: "switchComponents",
            componentName: "CsTransactionDetails",
            childComponentName: "CsTransactionV2GWalletOrder",
            tradeWay: 1,
            accountId: item.accountId,
          },
        });
        // operationManagementStore.updateSecondaryVisible(false);
      }
    };

    onMounted(() => {
      queryUserDisWalletListById();
    });

    return { ...toRefs(that), clickOperateBut, queryUserDisWalletListById };
  }
});
</script>

<style lang="scss" scoped>
.miniProgramUserWalletCom {
  height: 100%;

  .account_list_li {
    border-radius: 4px;
    margin-bottom: 12px;
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.05);

    .account_list_li_top {
      padding: 12px 16px;
      position: relative;
      display: flex;
      align-items: center;

      .account_list_li_left {
        color: rgba(255, 255, 255, 0.6);

        .iconfont {
          font-size: 32px;
        }
      }

      .account_list_li_center {
        flex: 1;
        margin-left: 12px;
        padding-right: 32px;
        box-sizing: border-box;

        .account_list_li_text {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 10px;

          .list_li_text_left {
            color: #FFFFFF;
            font-size: 14px;
          }

          .list_li_text_right {
            color: #D3ECFB;
            font-size: 14px;

            .unit {
              margin-left: 4px;
            }
          }
        }

      }

      .account_list_li_right {
        color: #FFFFFF;
        font-size: 14px;
        position: absolute;
        right: 12px;
        top: 12px;

        .iconfont {
          font-size: 20px;
        }
      }
    }

    .account_list_li_bottom {
      height: 32px;
      cursor: pointer;
      line-height: 32px;
      text-align: center;
      border-radius: 0 0 4px 4px;
      background-color: #084768;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
</style>