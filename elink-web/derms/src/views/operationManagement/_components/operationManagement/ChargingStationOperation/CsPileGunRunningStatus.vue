<template>
  <div class="csPileGunRunningStatus">
    <div class="content_form_top">
      <div class="content_form_top_left">

        <!-- <el-select v-model="formInline.siteIds" clearable filterable multiple collapse-tags max-collapse-tags="1"
          placeholder="全部" @change="querySitePileMonitor">
          <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
        </el-select>
        <div class="refresh_class" style="margin-left: 12px">
          <el-button :icon="RefreshRight" @click="querySitePileMonitor">刷新</el-button>
        </div> -->
        <el-form :model="formInline" inline>
          <el-row :gutter="24">
            <el-col :xs="16" :sm="16" :md="16" :xl="16">
              <el-form-item label="">
                <el-select v-model="formInline.siteIds" clearable filterable multiple collapse-tags
                  max-collapse-tags="1" placeholder="全部" @change="querySitePileMonitor">
                  <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName"
                    :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :xl="6">
              <el-form-item>
                <el-button :icon="RefreshRight" @click="querySitePileMonitor">刷新</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="content_form_top_right">
        <template v-for="(item, index) in gunStatusList" :key="index">
          <template v-if="returnDataInfo[item.fieldName]">
            <div :class="['gun_status_' + item.id, item.id === formInline.status ? 'active_class' : '']"
              class="gun_status_li" @click="clickGunStatusItemFun(item)">
              <div class="gun_status_number">{{ $filters.numberNull(returnDataInfo[item.fieldName]) }}</div>
              <div class="gun_status_text">{{ item.name }}</div>
            </div>
          </template>
        </template>
      </div>
    </div>
    <div class="content_gun_body" v-loading="listLoading">
      <div class="content_gun_list">
        <template v-if="list && list.length">
          <template v-for="(item, index) in list" :key="index">
            <div class="content_list">
              <div class="content_list_top">
                <div class="content_list_top_left">
                  <span class="siteName">{{ $filters.moreData(item.siteName) }}</span>
                  <template v-if="!item.pileGunList || !item.pileGunList.length">
                    <span class="pileGunNoListClass">-</span>
                    <span class="pileGunNoListClass">暂无设备</span>
                  </template>
                </div>
                <div class="content_list_top_right">
                  <div class="content_list_top_right_li">
                    <span class="text">今日充电</span>
                    <span class="number">{{ $filters.moneyTwoNum(item.todayCharge, 3) }}</span>
                    <span class="unit">度</span>
                  </div>
                  <div class="content_list_top_right_li">
                    <span class="text">充电金额</span>
                    <span class="number">{{ $filters.moneyTwoNum(item.todayChargeMoney, 3) }}</span>
                    <span class="unit">元</span>
                  </div>
                </div>
              </div>
              <template v-if="item.pileGunList && item.pileGunList.length">
                <el-row :gutter="12" class="content_list_bottom">
                  <template v-for="(childItem, childIndex) in item.pileGunList" :key="childIndex">
                    <el-col :xl="6" :lg="8" :md="12" :sm="24" :xs="24">
                      <ChargingGunStatusCardCom :dataInfo="childItem" @click="clickChargingGunCard(childItem)">
                      </ChargingGunStatusCardCom>
                    </el-col>
                  </template>
                </el-row>
              </template>
            </div>
          </template>
        </template>
        <template v-else><null-data></null-data></template>
      </div>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';
import { ElMessage, } from "element-plus";
import { queryUserAuthorityIsHaveFun } from "@/utils";
import { RefreshRight } from '@element-plus/icons-vue';
import { defineComponent, onMounted, reactive, toRefs } from "vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { countSitePileMonitor } from "@/api/operationManagement/CsPileGunRunningStatus";
import ChargingGunStatusCardCom from "./CsPileGunRunningStatus/ChargingGunStatusCardCom.vue";

export default defineComponent({
  name: "CsPileGunRunningStatus",
  components: { ChargingGunStatusCardCom },
  setup () {
    const store = useStore();
    const that = reactive({
      RefreshRight,
      siteIdArray: [],
      returnDataInfo: {},
      formInline: { status: -2 },
      gunStatusList: [
        { id: -2, name: "全部", fieldName: 'whole' },
        { id: 3, name: "空闲", fieldName: 'idle', bg_color: "#ECF9EC" },
        { id: 1, name: "充电", fieldName: 'charge', bg_color: "#ECF9EC" },
        { id: 2, name: "放电", fieldName: 'discharge', bg_color: "#ECF9EC" },
        { id: 4, name: "占用", fieldName: 'employ', bg_color: "#ECF9EC" },
        { id: 5, name: "故障", fieldName: 'fault', bg_color: "#ECF9EC" },
        { id: 6, name: "离线", fieldName: 'offline', bg_color: "#EFEFEF" },
        { id: 7, name: "未注册", fieldName: 'unregistered', bg_color: "#EFEFEF" },
        { id: 8, name: "预约", fieldName: 'reservation', bg_color: "#EFEFEF" },
        // {id: -1, name: "未知", fieldName: 'unknown', bg_color: "#EFEFEF"},
      ],

      list: [],
      totalNumber: 0,
      listLoading: false,
    });

    // 统计站点电桩监视数据
    const querySitePileMonitor = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      if (formInline.status === -2) formInline.status = "";
      countSitePileMonitor({ ...formInline, scenarioTypes: "3" }).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        that.list = returnDataInfo.sitePileList;
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        delete that.returnDataInfo.sitePileList;
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };


    const clickGunStatusItemFun = (item) => {
      that.formInline.status = item.id;
      querySitePileMonitor();
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", timer: new Date() }).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    const clickChargingGunCard = (childItem) => {
      const routeName = "/operationManagement/CsChargingPileDetails";
      const isAuthority = queryUserAuthorityIsHaveFun(routeName);
      if (!isAuthority) {
        ElMessage({ type: "warning", showClose: true, message: "请联系管理员打开对应权限！" });
        return;
      }

      store.dispatch("updateSecondaryInfo", {
        subTitle: `设备详情`, id: childItem.pileId, pileCode: childItem.pileCode,
        componentName: "CsChargingPileDetails", backComponentName: "CsPileGunRunningStatus"
      });
      store.dispatch("updateSecondaryVisible", true);
    };

    onMounted(() => {
      querySitePileMonitor();
      querySiteBasicInfoByTenantId();
    });

    return { ...toRefs(that), clickGunStatusItemFun, clickChargingGunCard, querySitePileMonitor, querySiteBasicInfoByTenantId };
  }
});
</script>

<style lang="scss" scoped>
.csPileGunRunningStatus {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 12px;
  border-radius: 6px;
  box-sizing: border-box;
  border: 1px solid #106ec499;

  .content_form_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    .content_form_top_left {
      min-width: 340px;
      display: flex;
      align-items: center;
    }

    .content_form_top_right {
      display: flex;
      align-items: center;

      .gun_status_li {
        min-width: 72px;
        cursor: pointer;
        padding: 7px 14px;
        border-radius: 4px;
        text-align: center;
        margin-right: 12px;
        box-sizing: border-box;
        background: #ffffff08;
        border: 1px solid #106ec499;

        .gun_status_number {
          color: #c7e3f3;
          font-size: 18px;
          font-weight: 700;
        }

        .gun_status_text {
          color: #c7e3f3cc;
          margin-top: 4px;
        }

        &:last-child {
          margin-right: 0;
        }
      }

      // 充电
      .gun_status_1 {
        background: rgba(7, 156, 235, .2);
        border: 1px solid rgba(7, 156, 235, .5);

      }

      // 放电
      .gun_status_2 {
        // background: rgba(255, 248, 134, .2);
        // border: 1px solid rgba(255, 248, 134, .5);
        background-color: rgba(65, 203, 74, .2);
        border: 1px solid rgba(65, 203, 74, .5);
        ;
      }

      // 空闲
      .gun_status_3 {
        background: rgba(19, 82, 120, .2);
        border: 1px solid rgba(19, 82, 120, .5);

      }

      //占用
      .gun_status_4 {
        background: rgba(255, 125, 30, .2);
        border: 1px solid rgba(255, 125, 30, .5);
      }

      // 故障
      .gun_status_5 {
        background: rgba(253, 57, 58, .2);
        border: 1px solid rgba(253, 57, 58, .5);
      }

      // 离线
      .gun_status_6 {
        // background: rgba(109, 125, 145, .2);
        // border: 1px solid rgba(109, 125, 145, .5);
        background: rgba(102, 102, 102, 0.2);
        border: 1px solid rgba(102, 102, 102, .5);
        ;
      }

      .gun_status_8 {
        background: rgba(173, 0, 255, .2);
        border: 1px solid rgba(173, 0, 255, .5);
      }

      .active_class {
        border-color: #30C1FF;
      }
    }
  }

  .content_gun_body {
    flex: 1;
    height: 2px;
    flex-basis: auto;
    display: flex;
    flex-direction: column;

    .content_gun_list {
      flex: 1;
      height: 1px;
      overflow-y: auto;
      padding-bottom: 12px;
      box-sizing: border-box;

      .content_list {
        .content_list_top {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .content_list_top_left {
            color: #ffffff;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 16px;

            .pileGunNoListClass {
              font-size: 12px;
              font-weight: 400;
              color: #ffffff99;
            }
          }

          .content_list_top_right {
            display: flex;
            align-items: center;

            .content_list_top_right_li {
              color: #ffffffcc;
              font-size: 16px;
              margin-right: 16px;

              .number {
                color: #00f0ff;
                font-size: 20px;
                margin: 0 10px;
              }

              &:last-child {
                margin-right: 0;
              }
            }
          }
        }

        .content_list_bottom {
          width: 100%;
          margin-top: 14px;
        }
      }
    }
  }
}
</style>