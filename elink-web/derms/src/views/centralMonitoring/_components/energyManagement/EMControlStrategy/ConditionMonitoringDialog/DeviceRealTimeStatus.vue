<template>
  <title-view title="实时状态">
    <template #headerRight>
      <div class="flex-ai-center">
        <el-button :icon="RefreshRight" @click="queryDeviceFunctionListById">刷新</el-button>
      </div>
    </template>
    <template #content>
      <div class="deviceRealTimeStatus" v-loading="listLoading">
        <div class="content_left">
          <img v-if="activeDeviceType === 'dianZhuangXiTong'" alt="" class="type_image_1" src="@/assets/image/dianZhuangXiTong.png"/>
          <img v-if="activeDeviceType === 'peiDianXiTong'" alt="" class="type_image_2" src="@/assets/image/peiDianXiTong.png"/>
          <img v-if="activeDeviceType === 'guangFuXiTong'" alt="" class="type_image_3" src="@/assets/image/guangFuXiTong.png"/>
          <img v-if="activeDeviceType === 'chuNengXiTong'" alt="" class="type_image_4" src="@/assets/image/chuNengXiTong.png"/>
          <div class="base_class"></div>
        </div>
        <div class="content_right">
          <div class="content_right_card">
            <template v-if="activeDeviceType === 'dianZhuangXiTong'">
              <el-table :data="gunTableList" max-height="320">
                <el-table-column label="序号" type="index" width="60"></el-table-column>
                <el-table-column label="状态">
                  <template #default="{ row }">
                    <span class="gunWorkState" :class="'gunWorkState' + row.gunstatus">{{ $filters.gunOriginalWorkState(row.gunstatus) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="功率(kW)">
                  <template #default="{ row }">{{ $filters.moreData(row.gunpower) }}</template>
                </el-table-column>
              </el-table>
            </template>
            <template v-else>
              <template v-for="(item,index) in list" :key="index">
                <div class="content_card_li" v-if="item.activeDeviceType === activeDeviceType">
                  <div class="content_card_li_left">{{ item.name }}</div>
                  <div class="content_card_li_right">
                    <span class="number">{{ $filters[item.filterName ?? 'moreData'](queryFunctionValueText(returnDataInfo[item.fieldName]),4,'--', false) }}</span>
                    <template v-if="returnDataInfo[item.fieldName] && returnDataInfo[item.fieldName].unit">
                      <span class="unit">{{ returnDataInfo[item.fieldName]?.unit }}</span>
                    </template>
<!--                    <span class="unit" v-if="item.unit">{{ item.unit }}</span>-->
                  </div>
                </div>
              </template>
            </template>
          </div>
        </div>
      </div>
    </template>
  </title-view>
</template>

<script>
import {RefreshRight} from '@element-plus/icons-vue';
import {defineComponent, reactive, toRefs, watch} from "vue";
import {findDeviceFunctionListById} from "@/api/centralMonitoring/energyManagement";

export default defineComponent({
  name: "DeviceRealTimeStatusCom",
  props: {
    activeDeviceId: {
      type: [String, Number],
      default: ""
    },
    activeDeviceType: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {

    const that = reactive({
      RefreshRight,
      gunTableList: [],
      listLoading: false,
      returnDataInfo: {},
      list: [
          // 电桩
        {name: "枪状态", fieldName: "gunstatus", activeDeviceType: "dianZhuangXiTong"},
        {name: "枪功率", fieldName: "gunpower", activeDeviceType: "dianZhuangXiTong", filterName: "moneyTwoNum"},

          // 配电设备
        {name: "总有功功率", fieldName: "total_active_power", activeDeviceType: "peiDianXiTong", filterName: "moneyTwoNum", unit: "kW"},
        {name: "总无功功率", fieldName: "total_reactive_power", activeDeviceType: "peiDianXiTong", filterName: "moneyTwoNum", unit: "kVar"},
        {name: "功率因数", fieldName: "power_factor", activeDeviceType: "peiDianXiTong", filterName: "moneyTwoNum"},

          // 光伏设备
        {name: "限功率实际值", fieldName: "actual_power_limit", activeDeviceType: "guangFuXiTong", unit: "%"},
        {name: "视在功率", fieldName: "apparent_power_value", activeDeviceType: "guangFuXiTong", filterName: "moneyTwoNum", unit: "kW"},
        {name: "有功功率", fieldName: "active_power", activeDeviceType: "guangFuXiTong", filterName: "moneyTwoNum", unit: "kW"},
        {name: "无功功率", fieldName: "reactive_power_value", activeDeviceType: "guangFuXiTong", filterName: "moneyTwoNum", unit: "kW"},
        {name: "当日发电量", fieldName: "daily_power_generation", activeDeviceType: "guangFuXiTong", filterName: "moneyTwoNum", unit: "度"},

          // 储能设备展示
        {name: "并离网状态", fieldName: "grid_status", activeDeviceType: "chuNengXiTong"},
        {name: "PCS工作状态", fieldName: "pcs_operative_mode", activeDeviceType: "chuNengXiTong"},
        {name: "电池SOC", fieldName: "battery_soc", activeDeviceType: "chuNengXiTong", unit: "%"},
        {name: "交流总有功功率", fieldName: "pcs_activepower", activeDeviceType: "chuNengXiTong", filterName: "moneyTwoNum",unit: "kW"},
        {name: "交流总无功功率", fieldName: "pcs_reactivepower", activeDeviceType: "chuNengXiTong", filterName: "moneyTwoNum", unit: "kVar"},
        {name: "交流总视在功率", fieldName: "pcs_apparentpower", activeDeviceType: "chuNengXiTong", filterName: "moneyTwoNum", unit: "kVA"},
      ]
    });

    // 查询设备功能点实时数据
    const queryDeviceFunctionListById = ()=>{
      that.listLoading = true;
      findDeviceFunctionListById({ deviceId: props.activeDeviceId }).then(res=>{
        let returnDataInfo = {},gunTableList = [];

        let list = res.data ? res.data : [];
        for(let i = 0;i < that.list.length;i++){
          if(props.activeDeviceType === that.list[i].activeDeviceType){
            let findItem = list.find(item=> item.functionLogo === that.list[i].fieldName);
            if(findItem){
              findItem.value = JSON.parse(findItem.value);
              if(findItem.dataObject) findItem.dataObject = JSON.parse(findItem.dataObject);
            }
            returnDataInfo[that.list[i].fieldName] = findItem ?? {};
          }
        }

        if(props.activeDeviceType === "dianZhuangXiTong"){
          for (let key in returnDataInfo){
            if(returnDataInfo[key].value && returnDataInfo[key].value.length){
              for(let i = 0;i < returnDataInfo[key].value.length;i++){
                if(!gunTableList[i]) gunTableList[i] = {};
                gunTableList[i][key] = returnDataInfo[key].value[i];
              }
            }
          }
        }

        // console.log(returnDataInfo);
        that.gunTableList = JSON.parse(JSON.stringify(gunTableList));
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const queryFunctionValueText = (data = {})=>{
      let functionValueText = data.value;

      // 枚举项
      if(data.dataType === 5){
        let enumArray = data.dataObject?.enumArray;
        if(enumArray && enumArray.length){
          let findItem = enumArray.find(item => String(item.id) === String(data.value));
          if(findItem) functionValueText = findItem.name;
        }
      }

      // 布尔值
      if(data.dataType === 6){
        let trueValueText = data.dataObject.trueValue;
        let falseValueText = data.dataObject.falseValue;
        functionValueText = data.value ? trueValueText : falseValueText;
      }

      return functionValueText;
    };

    const watchActiveDeviceId = watch(()=>props.activeDeviceId,(newActiveDeviceId)=>{
      if(newActiveDeviceId) queryDeviceFunctionListById();
    },{ deep: true,immediate: true });

    return {...toRefs(that),watchActiveDeviceId, queryDeviceFunctionListById, queryFunctionValueText};
  }
});
</script>

<style lang="scss" scoped>
.deviceRealTimeStatus {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px 24px 32px;
  box-sizing: border-box;

  .content_left {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

    .type_image_1 {
      width: 310px;
      height: 340px;
    }

    .type_image_2 {
      width: 210px;
    }

    .type_image_3{
      width: 210px;
    }

    .type_image_4{
      width: 180px;
      height: 340px;
    }

    .base_class {
      width: 240px;
      height: 35px;
      border-radius: 50%;
      box-sizing: border-box;
      border: 2px solid #3970DAFF;
      box-shadow: 0 0 32px #3970DAFF, 0 0 24px #3970DAFF inset;
      //background: radial-gradient(50.13% 49.9% at 125.69% 64.1%, #01132df2 0%, #0b1e39f2 100%);
      backdrop-filter: blur(8px);
    }
  }

  .content_right{
    padding: 0 24px;
    box-sizing: border-box;

    .content_right_card{
      min-width: 340px;
      padding: 24px 16px;
      border-radius: 6px;
      background: #3249684d;
      box-sizing: border-box;

      .content_card_li{
        font-size: 12px;
        color: #d3ecfbff;
        font-weight: 700;
        margin-bottom: 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .content_card_li_left{
          white-space: nowrap;
        }

        .content_card_li_right{
          font-size: 16px;
          color: #00f7ffff;

          .unit{
            font-size: 14px;
            font-weight: 400;
            margin-left: 2px;
          }
        }

        &:last-child{
          margin-bottom: 0;
        }
      }
    }
  }


  .gunWorkState {
    color: #E66AE3;
  }

  .gunWorkState0 {
    color: #007FEB;
  }

  .gunWorkState1,.gunWorkState2 {
    color: #41CB4A;
  }

  .gunWorkState4,.gunWorkState5 {
    color: #FF9C02;
  }

  .gunWorkState3 {
    color: #EDA300;
  }

  .gunWorkState255 {
    color: #FF1515;
  }

  .gunWorkState88,.gunWorkState8 {
    color: rgba(255,255,255,0.6);
  }

  .gunWorkState7 {
    color: #00B3EB;
  }
}
</style>