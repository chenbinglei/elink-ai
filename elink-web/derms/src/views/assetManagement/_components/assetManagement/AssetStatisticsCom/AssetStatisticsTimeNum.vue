<template>
  <div class="assetStatisticsTimeNum">
    <div class="content_timer">
      <template v-for="(item,index) in timer_list" :key="index">
        <div :class="{active_class: item.id === timeIndex }" class="timer_text" @click="clickTimerItemFun(item)">
          <span>{{ item.name }}</span>
        </div>
      </template>
    </div>

    <div class="content_data">
      <div class="content_data_list">
        <template v-for="(item,index) in list" :key="index">
          <div class="content_data_li" v-if="item.scenarioType === scenarioType">
            <div class="content_data_li_left" :style="{ background: item.bgColor }">
              <div class="rect_class" :style="{ background: item.rectColor }"></div>
              <div class="title">{{ item.name }}</div>
            </div>
            <div class="content_data_li_right">
              <span class="content_data_li_number">{{ $filters.numberValue(timeCountDataInfo[item.fieldName]) }}</span>
              <span class="content_data_li_unit">{{ $filters.numberUnit(timeCountDataInfo[item.fieldName],item.unit) }}</span>
            </div>
          </div>
        </template>
      </div>
      <template v-if="scenarioType === 1 || (scenarioType === 2 && timeIndex !== 1)">
        <AssetStatisticsChart :scenarioType="scenarioType" :timeCountDataInfo="timeCountDataInfo" />
      </template>
    </div>

    <!--          等效发电小时-->
    <EquivalentPowerGenerationHours v-if="scenarioType === 1" :totalHour="totalHour" :timeCountDataInfo="timeCountDataInfo" />
  </div>
</template>

<script>
import {useStore} from 'vuex';
import AssetStatisticsChart from "./AssetStatisticsChart.vue";
import {computed, defineComponent, reactive, toRefs, watch} from "vue";
import EquivalentPowerGenerationHours from "./EquivalentPowerGenerationHours.vue";

export default defineComponent({
  name: "AssetStatisticsCard",
  components: {EquivalentPowerGenerationHours, AssetStatisticsChart},
  props: {
    dataPSCAssetCountInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const store = useStore();
    const scenarioType = computed(() => {
      return store.state.assetManagement.scenarioType;
    });

    const that = reactive({
      timeIndex: 1,
      totalHour: 24,
      returnDataInfo: {},
      timeCountDataInfo: {},
      timer_list: [
        {id: 1, name: "今日",fieldName: "dayCountData", totalHour: 5},
        {id: 2, name: "昨日",fieldName: "lastDayCountData", totalHour: 5},
        {id: 3, name: "本月",fieldName: "monthCountData", totalHour: 5},  //5 * getDaysInMonth()
        {id: 4, name: "累计",fieldName: "sumCountData",totalHour: 5}
      ],
      list: [
        {
          unit: "kWh",
          name: "总发电量",
          scenarioType: 1,
          fieldName: "totalQt",
          rectColor: "#ADFEE5",
          bgColor: "linear-gradient(90deg, #2ab86380 0%, #315e6300 100%)",
        },
        {
          unit: "kWh",
          name: "上网电量",
          scenarioType: 1,
          fieldName: "internetQt",
          rectColor: "#30F3FF",
          bgColor: "linear-gradient(90deg, #30f3ff80 0%, #315e6300 100%)",
        },
        {
          unit: "kWh",
          name: "消纳电量",
          scenarioType: 1,
          fieldName: "absorptiveQt",
          rectColor: "#ffa800",
          bgColor: "linear-gradient(90deg, #4ed4a680 0%, #315e6300 100%)",
        },

        {
          unit: "kWh",
          name: "储能充电量",
          scenarioType: 2,
          fieldName: "storageChargeQt",
          rectColor: "#ADFEE5",
          bgColor: "linear-gradient(90deg, #2ab86380 0%, #315e6300 100%)",
        },
        {
          unit: "kWh",
          name: "储能放电量",
          scenarioType: 2,
          fieldName: "storageDischargeQt",
          rectColor: "#30F3FF",
          bgColor: "linear-gradient(90deg, #30f3ff80 0%, #315e6300 100%)",
        },

        {
          unit: "kWh",
          name: "充电电量",
          scenarioType: 3,
          fieldName: "chargeQt",
          rectColor: "#ADFEE5",
          bgColor: "linear-gradient(90deg, #2ab86380 0%, #315e6300 100%)",
        },
        {
          unit: "度",
          scenarioType: 3,
          name: "枪均充电量",
          rectColor: "#30F3FF",
          fieldName: "gunAvgQt",
          bgColor: "linear-gradient(90deg, #30f3ff80 0%, #315e6300 100%)",
        },
        {
          unit: "%",
          scenarioType: 3,
          name: "一次充电成功率",
          rectColor: "#ADFEE5",
          fieldName: "chargeSuccessRatio",
          bgColor: "linear-gradient(90deg, #2ab86380 0%, #315e6300 100%)",
        },
        {
          unit: "kWh",
          name: " V2G电量",
          scenarioType: 3,
          fieldName: "v2gQt",
          rectColor: "#30F3FF",
          bgColor: "linear-gradient(90deg, #30f3ff80 0%, #315e6300 100%)",
        },
      ]
    });

    const clickTimerItemFun = (item) => {
      that.timeIndex = item.id;
      that.totalHour = item.totalHour * (that.returnDataInfo?.siteNum ?? 1);
      that.timeCountDataInfo = that.returnDataInfo[item.fieldName] ?? {};
    };

    // 监听数据
    const watchDataPSCAssetCountInfo = watch(()=>props.dataPSCAssetCountInfo,(newDataPSCAssetCountInfo)=>{
      // console.log(newDataPSCAssetCountInfo);
      that.returnDataInfo = JSON.parse(JSON.stringify(newDataPSCAssetCountInfo));
      let findItem = that.timer_list.find(item=>item.id === that.timeIndex);
      if(findItem) clickTimerItemFun(findItem);
    },{ deep: true,immediate: true });

    return {...toRefs(that), clickTimerItemFun, scenarioType, watchDataPSCAssetCountInfo};
  }
});
</script>

<style lang="scss" scoped>
.assetStatisticsTimeNum {
  flex: 1;
  height: 2px;
  margin-top: 16px;
  padding: 0 4px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .content_timer {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 0 24px;
    box-sizing: border-box;

    .timer_text {
      cursor: pointer;
      color: #00b2ff80;
    }

    .active_class {
      color: #00b2ff;
    }
  }

  .content_data {
    flex: 1;
    height: 2px;
    display: flex;
    align-items: center;

    .content_data_list {
      flex: 1;
      height: 100%;
      //margin-right: 12px;
      display: flex;
      flex-direction: column;
      justify-content: space-around;

      .content_data_li{
        display: flex;
        align-items: center;
        justify-content: space-between;

        .content_data_li_left{
          height: 30px;
          display: flex;
          align-items: center;
          border-radius: 200px 0 0 200px;
          padding: 0 16px 0 12px;
          box-sizing: border-box;

          .rect_class{
            width: 8px;
            height: 8px;
            margin-right: 11px;
          }
        }

        .content_data_li_number{
          color: #ffffff;
          font-size: 16px;
          font-weight: 800;
        }

        .content_data_li_unit{
          display: inline-block;
          min-width: 35px;
          font-size: 14px;
          color: #a1ddfe80;
          text-align: right;
        }
      }
    }
  }
}
</style>