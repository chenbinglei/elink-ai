<template>
  <div class="strategyControlListCom content_border">
    <div class="content_body_top">
      <div class="content_body_top_left">
        <img alt="" src="@/assets/image/kongzhicelve.png">
        <span class="content_body_top_title">控制策略</span>
      </div>
      <div class="content_body_top_right">
        <el-button circle size="small" type="primary" @click="clickAddStrategyBut" icon="Plus" style="width: 26px;height: 26px;">
          <template #icon>
            <el-icon color="#fff" size="16"><Plus/></el-icon>
          </template>
        </el-button>
      </div>
    </div>
    <div class="content_body_bottom" v-loading="listLoading">
      <template v-if="strategyList && strategyList.length">
        <template v-for="(item,index) in strategyList" :key="index">
          <div class="strategy_li_content" :class="{active_strategy_li: item.id === activeStrategyId}" @click.stop="clickStrategyListCardFun(item)">
            <el-image :src="strategy_icon[`strategy_icon_${ item.strategyType }`]" alt="" class="strategy_li_content_icon">
              <template #error>
                <div class="image-error">
                  <el-icon color="#2AB3FF" size="32"><Picture/></el-icon>
                </div>
              </template>
            </el-image>
            <div class="strategy_li_content_right">
              <div class="strategy_li_cr_li">
                <div class="strategy_name textTwo">{{ $filters.moreData(item.strategyName) }}</div>
                <span class="iconfont icon-zc-tishi" @click.stop="clickLookDescribeFun(item)"></span>
              </div>
              <div class="strategy_li_cr_li">
                <div class="strategy_type">{{ $filters.strategyType(item.strategyType) }}</div>
                <span class="iconfont " :class="[item.executeStatus ? 'icon-yunduanxiafa' : 'icon-yunduanweixiafa']"></span>
              </div>
            </div>
          </div>
        </template>
      </template>
      <null-data v-else words="请先添加策略"></null-data>
    </div>

    <AddControlStrategyDialog v-if="addControlStrategyVisible" v-model:isVisible="addControlStrategyVisible" :titleName="titleName" :activeSiteId="activeSiteId"
                              :activeDeviceInfo="activeDeviceInfo" @changeEvent="findStrategyList" />

    <StrategyFileView v-if="strategyFileViewVisible" v-model:isVisible="strategyFileViewVisible" :titleName="titleName" :templateId="templateId" :templateType="templateType" />
  </div>
</template>

<script>
import {useStore} from "vuex";
import {Plus, Picture} from '@element-plus/icons-vue';
import strategy_icon_1 from "@/assets/image/strategy_icon_1.png";
import strategy_icon_2 from "@/assets/image/strategy_icon_2.png";
import strategy_icon_3 from "@/assets/image/strategy_icon_3.png";
import strategy_icon_4 from "@/assets/image/strategy_icon_4.png";
import strategy_icon_5 from "@/assets/image/strategy_icon_5.png";
import strategy_icon_6 from "@/assets/image/strategy_icon_6.png";
import strategy_icon_7 from "@/assets/image/strategy_icon_7.png";
import strategy_icon_8 from "@/assets/image/strategy_icon_8.png";
import AddControlStrategyDialog from "./AddControlStrategyDialog.vue";
import StrategyFileView from "../EMStrategyTemplate/StrategyFileView.vue";
import {queryStrategyList} from "@/api/centralMonitoring/energyManagement";
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "StrategyControlListCom",
  emits: ["update:activeStrategyId"],
  components: {StrategyFileView, AddControlStrategyDialog, Plus, Picture},
  props: {
    // 当前站点id
    activeSiteId: {
      type: [String, Number],
      default: ""
    },
    // 当前选中的策略id
    activeStrategyId: {
      type: [String, Number],
      default: ""
    },
    // 当前设备信息
    activeDeviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {
    const store = useStore();
    const {emit} = getCurrentInstance();

    const that = reactive({
      strategyList: [],
      listLoading: false,

      strategy_icon: {
        strategy_icon_1: strategy_icon_1,
        strategy_icon_2: strategy_icon_2,
        strategy_icon_3: strategy_icon_3,
        strategy_icon_4: strategy_icon_4,
        strategy_icon_5: strategy_icon_5,
        strategy_icon_6: strategy_icon_6,
        strategy_icon_7: strategy_icon_7,
        strategy_icon_8: strategy_icon_8,
      },

      templateId: "",
      templateType: 1,
      titleName: "添加控制策略",
      strategyFileViewVisible: false,
      addControlStrategyVisible: false,
    });

    // 查询策略管理列表
    const findStrategyList = () => {
      that.listLoading = true;
      queryStrategyList({siteId: props.activeSiteId,timer: new Date(), ...props.activeDeviceInfo}).then(res => {
        that.strategyList = res.data ? res.data : [];
        clickStrategyListCardFun();
        that.listLoading = false;
      }).catch((error)=>{
        that.listLoading = false;
        if (error && error.code === 88886) return;
        emit("update:activeStrategyId","");
        that.strategyList = [];
      });
    };

    // 点击获取当前策略
    const clickStrategyListCardFun = (data = null)=>{
      let findItem = that.strategyList.find(item => item.id === props.activeStrategyId); // 查询当前策略是否还存在
      if(!data) data = findItem ? findItem : that.strategyList[0]; // 获取第一条数据
      emit("update:activeStrategyId",data?.id);
      store.dispatch("updateStrategyName",data?.strategyName);
    };

    const clickAddStrategyBut = ()=>{
      that.titleName = "添加控制策略";
      that.addControlStrategyVisible = true;
    };

    const clickLookDescribeFun = (data)=>{
      that.titleName = "策略说明";
      that.templateId = data.templateId;
      that.strategyFileViewVisible = true;
    };

    const watchSiteAndDeviceInfo = watch(() => props.activeDeviceInfo, (newActiveDeviceInfo) => {
      if(newActiveDeviceInfo.deviceNumber) findStrategyList();
    }, {deep: true,immediate:true});

    return {...toRefs(that), findStrategyList, watchSiteAndDeviceInfo, clickAddStrategyBut, clickStrategyListCardFun, clickLookDescribeFun};
  }
});
</script>

<style lang="scss" scoped>
.strategyControlListCom {
  width: 310px;
  height: 100%;
  padding: 12px 14px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .content_body_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;

    .content_body_top_left {
      display: flex;
      align-items: center;
      font-size: 16px;
      color: rgba(255, 255, 255, 1);

      img {
        width: 48px;
        height: 48px;
        margin-right: 10px;
      }
    }
  }

  .content_body_bottom {
    flex: 1;
    height: 2px;
    flex-basis: auto;
    overflow-y: auto;

    .strategy_li_content {
      padding: 12px 14px 12px 16px;
      border-radius: 12px;
      box-sizing: border-box;
      background: rgba(0, 53, 89, 1);
      margin-bottom: 24px;
      display: flex;
      align-items: center;

      .strategy_li_content_icon {
        width: 32px;
        height: 32px;
        margin-right: 16px;
      }

      .strategy_li_content_right {
        flex: 1;

        .strategy_li_cr_li {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 28px;

          .strategy_name {
            flex: 1;
            color: #ffffff;
            font-size: 14px;
            margin-right: 4px;
            -webkit-line-clamp: 1;
          }

          .iconfont {
            font-size: 18px;
            cursor: pointer;
            color: rgba(255, 255, 255, 0.5);
          }

          .icon-yunduanxiafa {
            color: #00F7FF;
          }

          .strategy_type {
            font-size: 14px;
            color: rgba(255, 255, 255, 0.5);
          }

          &:last-child {
            margin-bottom: 0;
          }
        }
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    .active_strategy_li {
      border: 1px solid rgba(75, 201, 255, 1);
    }
  }
}
</style>