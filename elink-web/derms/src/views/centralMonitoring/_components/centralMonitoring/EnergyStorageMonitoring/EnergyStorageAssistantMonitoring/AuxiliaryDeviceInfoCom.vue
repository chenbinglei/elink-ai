<template>
  <div class="inverterDeviceInfo">
    <el-image :src="kongtTiaoIcon" class="card_left_icon"></el-image>
    <div class="card_right_info">
      <el-row :gutter="12" class="card_right_info_top">
        <el-col :md="19" :sm="18" :xl="22">
          <div class="flex-ai-center flex-warp left_info">
            <div class="deviceNumber textTwo">
              <span class="deviceName">{{ $filters.moreData(returnDataInfo.deviceName) }}</span>
              <template v-if="returnDataInfo.deviceNumber">
                <span>-</span>
                <span class="deviceNumber">{{ $filters.moreData(returnDataInfo.deviceNumber) }}</span>
              </template>
            </div>
            <div class="device_status flex-ai-center">
              <DeviceTxStatusCom :deviceInfo="returnDataInfo" style="margin: 0 12px"></DeviceTxStatusCom>
            </div>
            <!--            <div class="refresh_time">上次更新时间：{{ $filters.moreData(returnDataInfo.chargeStateTime) }}</div>-->
          </div>
        </el-col>
        <el-col :md="5" :sm="6" :xl="2">
          <el-button :icon="Refresh" size="small" @click="clickRefreshButFun">刷新</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="12" class="card_right_info_bottom">
        <el-col :md="12" :sm="24" :xl="8">
          <div class="card_right_info_bt_li">
            <div class="title">设备型号</div>
            <div class="number">{{ $filters.moreData(returnDataInfo.model) }}</div>
          </div>
        </el-col>
        <el-col :md="12" :sm="24" :xl="8">
          <div class="card_right_info_bt_li">
            <div class="title">生产厂家</div>
            <div class="number">{{ $filters.moreData(returnDataInfo.manufacturerName) }}</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts">
import {Refresh} from '@element-plus/icons-vue';
import kongtTiaoIcon from "@/assets/image/kongtTiao_icon.png";
import {reactive, defineComponent, toRefs, watch, getCurrentInstance} from "vue";
import {DeviceTxStatusCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "AuxiliaryDeviceInfoCom",
  props: {
    activeDeviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  emits: ["changeEvent"],
  components:{DeviceTxStatusCom},
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      Refresh,
      kongtTiaoIcon,
      returnDataInfo: {}
    });

    const clickRefreshButFun = ()=>{
      emit("changeEvent",{operateType: "PCSDeviceInfoCom"});
    };

    const watchActiveDeviceInfo = watch(() => props.activeDeviceInfo, (newActiveDeviceInfo) => {
      that.returnDataInfo = JSON.parse(JSON.stringify(newActiveDeviceInfo ?? {}));
      // console.log(that.returnDataInfo);
    }, {deep: true,immediate: true});

    return {...toRefs(that), watchActiveDeviceInfo, clickRefreshButFun};
  }
});
</script>

<style lang="scss" scoped>
.inverterDeviceInfo {
  width: 100%;
  display: flex;
  align-items: center;
  border-radius: 6px;
  background: #ffffff08;
  box-sizing: border-box;
  padding: 21px 12px 21px 24px;

  .card_left_icon {
    width: 68px;
    margin-right: 28px;
  }

  .card_right_info {
    flex: 1;
    flex-basis: auto;

    .card_right_info_top {
      width: 100%;
      display: flex;
      flex-wrap: wrap;

      .deviceNumber {
        color: #ffffff;
        font-size: 16px;
      }

      .refresh_time {
        font-size: 10px;
        color: #ffffff80;
        margin: 0 8px;
      }
    }

    .card_right_info_bottom {
      margin-top: 24px;

      .card_right_info_bt_li {
        font-size: 16px;
        color: #ffffffcc;
        text-align: center;
        margin-bottom: 4px;

        .number {
          color: #00f7ff;
          margin-top: 16px;
        }
      }
    }
  }
}
</style>