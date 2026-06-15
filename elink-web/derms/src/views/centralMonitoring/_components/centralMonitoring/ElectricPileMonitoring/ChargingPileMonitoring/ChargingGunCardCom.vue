<template>
  <div class="chargingGunCard">
    <div class="charging_gun_top">
      <div class="gun_top_left">
        <div class="textTwo gunName">{{ $filters.moreData(gunDataInfo.gunName) }}</div>
        <div :class="'gun_status_' + gunDataInfo.gunWorkState" class="gun_status_class">
          <span class="gunWorkState">{{ $filters.gunWorkState(gunDataInfo.gunWorkState) }}</span>
        </div>
      </div>
      <div class="gun_top_right">
        <template v-for="(item,index) in operateButList" :key="index">
          <el-tooltip effect="dark" placement="top-start" :content="item.name">
            <el-image :src="item.iconName" class="button_icon" :class="{
              notAllowed: ((item.id === 1 || item.id === 2) && gunDataInfo.gunWorkState !== 4) ||
                      (item.id === 3 && gunDataInfo.gunWorkState !== 1 && gunDataInfo.gunWorkState !== 2) ||
                      (item.id === 4 && gunDataInfo.gunWorkState !== 1 && gunDataInfo.gunWorkState !== 2 && gunDataInfo.gunWorkState !== 8)
            }" @click="clickOperateButFun(item.id)"></el-image>
          </el-tooltip>
        </template>
      </div>
    </div>
    <el-row :gutter="12" class="charging_gun_bottom">
      <el-col :lg="17" :sm="24">
        <div class="charging_gun_left">
          <el-image :src="charging_gun_icon" class="charging_gun_icon"></el-image>
          <div class="charging_gun_info flex-all">
            <el-row :gutter="14">
              <el-col v-for="(item,index) in info_list" :key="index" :span="8">
                <AttrFieldCardCom :attrFieldInfo="{...item, value: gunDataInfo.gunStatus === 2 || gunDataInfo.gunStatus === 5 ? gunDataInfo[item.fieldName] : null}" />
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
      <el-col :lg="7" :sm="24">
        <div class="content_chart">
          <ChargingGunPowerChart :activeDeviceInfo="activeDeviceInfo" :gunWorkState="gunDataInfo.gunWorkState" :gunIndex="gunIndex"></ChargingGunPowerChart>
        </div>
      </el-col>
    </el-row>

    <EndChargingDialog v-if="endChargingVisible" v-model:isVisible="endChargingVisible" :returnDataInfo="returnDataInfo" />
    <PowerControlDialog v-if="powerControlVisible" v-model:isVisible="powerControlVisible" :returnDataInfo="returnDataInfo" />
    <StartDisAndChargingDialog v-if="startDisAndChargingVisible" v-model:isVisible="startDisAndChargingVisible" :returnDataInfo="returnDataInfo" :operateType="operateType" />
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {reactive, defineComponent, toRefs} from "vue";
import tingzhi_icon from "@/assets/image/tingzhi_icon.png";
import fangdian_icon from "@/assets/image/fangdian_icon.png";
import gonglvkz_icon from "@/assets/image/gonglvkz_icon.png";
import chongdian_icon from "@/assets/image/chongdian_icon.png";
import ChargingGunPowerChart from "./ChargingGunPowerChart.vue";
import charging_gun_icon from "@/assets/image/charging_gun_icon.png";
import {AttrFieldCardCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";
import {StartDisAndChargingDialog,EndChargingDialog,PowerControlDialog} from "@/views/operationManagement/_components";

export default defineComponent({
  name: "ChargingGunCardCom",
  components: {PowerControlDialog, StartDisAndChargingDialog, EndChargingDialog, ChargingGunPowerChart,AttrFieldCardCom},
  props:{
    gunDataInfo:{
      type: Object,
      default: ()=>{
        return { };
      }
    },
    activeDeviceInfo:{
      type: Object,
      default: ()=>{
        return { };
      }
    },
    gunIndex:{
      type: Number,
      default: 0
    }
  },
  setup(props) {
    const that = reactive({
      charging_gun_icon,
      info_list: [
        {varName: "有功功率", fieldName: "outPower", unit: "kW"},
        {varName: "电压", fieldName: "outVolt", unit: "V"},
        {varName: "电流", fieldName: "outCurrent", unit: "A"},
        {varName: "当前车辆SOC", fieldName: "batterySoc", unit: "%"},
        {varName: "当前充放电时长", fieldName: "runTime", unit: "分"},
        {varName: "当前充/放电量", fieldName: "totalQt", unit: "kWh"},
      ],
      operateButList:[
        {name:"启动充电", id: 1,iconName: chongdian_icon},
        {name:"启动放电", id: 2,iconName: fangdian_icon},
        {name:"功率控制", id: 3,iconName: gonglvkz_icon},
        {name:"结束订单", id: 4,iconName: tingzhi_icon}
      ],

      operateType: 1,
      returnDataInfo: {},
      endChargingVisible: false,
      powerControlVisible: false,
      startDisAndChargingVisible: false,
    });

    const clickOperateButFun = (butType)=>{

      // 启动充电 or 启动放电
      if(butType === 1 || butType === 2){
        if(props.gunDataInfo.gunWorkState !== 4){
          // ElMessage({ type: "warning", message: "请检查充电枪是否已连接！", showClose: true });
          return;
        }
      }

      // 功率控制
      if(butType === 3){
        if(props.gunDataInfo.gunWorkState !== 1 && props.gunDataInfo.gunWorkState !== 2){
          // ElMessage({ type: "warning", message: "当前充电枪未进行工作！", showClose: true });
          return;
        }
      }

      if(butType === 4){
        if(props.gunDataInfo.gunWorkState !== 1 && props.gunDataInfo.gunWorkState !== 2 && props.gunDataInfo.gunWorkState !== 8){
          // ElMessage({ type: "warning", message: "当前充电枪未进行工作！", showClose: true });
          return;
        }
      }

      that.operateType = butType;
      that.returnDataInfo = JSON.parse(JSON.stringify(props.gunDataInfo));
      that.returnDataInfo.pileCode = props.activeDeviceInfo.deviceNumber;

      if(butType === 4) that.endChargingVisible = true;
      if(butType === 3) that.powerControlVisible = true;
      if(butType === 1 || butType === 2) that.startDisAndChargingVisible = true;
    };

    return { ...toRefs(that), clickOperateButFun };
  }
});
</script>

<style lang="scss" scoped>
.chargingGunCard {
  border-radius: 6px;
  background: #ffffff08;
  box-sizing: border-box;
  padding: 21px 16px 2px 16px;
  margin-bottom: 12px;

  .charging_gun_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 21px;

    .gun_top_left {
      display: flex;
      align-items: center;

      .gunName {
        color: #ffffff;
        font-size: 16px;
        margin-right: 12px;
        -webkit-line-clamp: 1;
      }

      .gun_status_class {
        font-size: 12px;
        padding: 2px 12px;
        border-radius: 4px;
        box-sizing: border-box;

        color: #ffffff66;
        background: #ffffff1A;
        border: 1px solid #ffffff66;
      }

      .gun_status_1 {
        color: #00ff90;
        background: #00ff9047;
        border: 1px solid #00ff90;
      }

      .gun_status_2 {
        color: #00f0ff;
        background: #00f0ff47;
        border: 1px solid #00f0ff;
      }

      .gun_status_3 {
        color: #00D1FFFF;
        background: #00D1FF1A;
        border: 1px solid #00D1FFFF;
      }

      .gun_status_7 {
        background: #00D1FF0B;
        color: rgba(255, 255, 255, .25);
        border: 1px solid rgba(255, 255, 255, .25);
      }

      .gun_status_8{
        color: #ad00ff;
        background: #ad00ff1a;
        border: 1px solid #ad00ff;
      }

      .gun_status_4 {
        color: #ffb800;
        background: #ffb8001a;
        border: 1px solid #ffb800;
      }

      .gun_status_5 {
        color: #ff1818;
        background: #ff18181a;
        border: 1px solid #ff1818;
      }
    }

    .gun_top_right{
      display: flex;
      align-items: center;

      .button_icon{
        width: 30px;
        cursor: pointer;
        margin-right: 16px;

        &:last-child{
          margin-right: 0;
        }
      }
    }
  }

  .charging_gun_bottom {

    .charging_gun_left {
      height: 100%;
      display: flex;
      align-items: center;
      box-sizing: border-box;

      .charging_gun_icon {
        width: 100px;
        margin-right: 16px;
      }
    }

    .content_chart {
      width: 99%;
    }
  }
}
</style>