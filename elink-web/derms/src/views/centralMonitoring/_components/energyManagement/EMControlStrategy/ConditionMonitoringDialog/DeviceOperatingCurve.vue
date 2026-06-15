<template>
  <title-view title="运行曲线">
    <template #headerRight>
      <div class="flex-ai-center">
        <el-select v-model="functionLogo" placeholder="请选择" style="width: 210px;" @change="changeFunctionLogoFun">
          <el-option v-for="item in functionLogoArray" :key="item.functionLogo" :label="item.functionName" :value="item.functionLogo"></el-option>
        </el-select>
        <div style="width: 12px"></div>
        <el-button :icon="RefreshRight" @click="clickResetForm">刷新</el-button>
      </div>
    </template>
    <template #content>
      <div class="deviceOperatingCurve" v-loading="listLoading">
        <v-chart v-if="functionLogoArray && functionLogoArray.length" :option="chartOption" autoresize></v-chart>
        <null-data v-else words="该设备无功能点"></null-data>
      </div>
    </template>
  </title-view>
</template>

<script lang="ts">
import {RefreshRight} from '@element-plus/icons-vue';
import {defineComponent, reactive, toRefs, watch} from "vue";
import {queryDeviceFunCurveData, queryFunctionList} from "@/api/centralMonitoring/energyManagement";

export default defineComponent({
  name: "DeviceOperatingCurve",
  props: {
    activeDeviceId: {
      type: [String, Number],
      default: ""
    },
    activeDeviceType: {
      type: String,
      default: ""
    },
    activeDeviceTypeId: {
      type: String,
      default: ""
    },
  },
  setup(props) {

    const that = reactive({
      RefreshRight,
      functionLogo: "",
      listLoading: false,
      functionLogoArray: [],
      active_device_type_id: "",

      chartOption: {
        color: ["#008BFF", "#06DE6F"],
        legend: {
          top: 0,
          left: 'center',
          textStyle: {color: '#9EA9B5'},
        },
        tooltip: {
          confine: true,
          trigger: 'axis',
          textStyle: {color: "#94a1a8"},
          borderColor: "rgba(255, 255, 255, 0.2)",
          backgroundColor: "rgba(255, 255, 255, 0.9)",
        },
        dataZoom: [{type: 'inside', start: 0, end: 100}],
        grid: {
          top: '15%',
          left: '2%',
          right: '2%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#9EA9B5"
            }
          },
          axisTick: {show: false},
          axisLabel: {color: "#9EA9B5"},
          data: [],
        },
        yAxis: {
          type: 'value',
          splitLine: {
            lineStyle: {
              type: 'dashed',//y轴分割线类型
              color: "rgba(255,255,255,0.3)",
            }
          },
          axisLabel: {color: "#9EA9B5"},
        },
        series: []
      },
      cdz_functionLogo: ["piledischargepower","gunsoc","pilechargepower","pilepower","gunpower","gunvoltage","guncurrent"],
      gf_functionLogo: ["apparent_power_value","reactive_power_value","c_phase_current","b_phase_current","a_phase_current","c_phase_voltage","b_phase_voltage", "a_phase_voltage","dc_bus_voltage"],
      cn_functionLogo: ["igbt_temp","battery_current","battery_voltage","total_dc_current","pcs_dc_power","total_bus_voltage","pcs_activepower","pcs_reactivepower","pcs_cos", "pcs_apparentpower","hz","ic","ib","ia","uc","ub","ua","uca","ubc","uab"],
    });

    // 查询模型标准功能列表
    const findFunctionList = ()=>{
      queryFunctionList({page: 1,size: 999999,typeId: that.active_device_type_id}).then(res=>{
        let functionLogoArray = [];
        let returnDataInfo = res.data.items;
        let functionLogo = [...that.cn_functionLogo,...that.gf_functionLogo,...that.cdz_functionLogo];
        if(returnDataInfo && returnDataInfo.length){
          if(props.activeDeviceType !== "peiDianXiTong"){
            for(let i = 0;i < returnDataInfo.length;i++){
              let findIndex = functionLogo.findIndex(item=> item === returnDataInfo[i].functionLogo);
              if(findIndex !== -1) functionLogoArray.push(returnDataInfo[i]);
            }
          } else {
            functionLogoArray = JSON.parse(JSON.stringify(returnDataInfo));
          }

          let findItem = functionLogoArray.find(item=> item.functionLogo === that.functionLogo);
          if(!findItem) that.functionLogo = functionLogoArray[0].functionLogo;
          clickResetForm();
        }
        that.functionLogoArray = JSON.parse(JSON.stringify(functionLogoArray));
      }).catch((error)=>{
        if (error && error.code === 88886) return;
        that.functionLogoArray = [];
        that.functionLogo = "";
      });
    };

    const clickResetForm = ()=>{
      if(that.functionLogo) changeFunctionLogoFun();
    };

    // 查询电桩、储能、光伏、配电设备功能点曲线数据数据
    const changeFunctionLogoFun = ()=>{
      that.listLoading = true;
      queryDeviceFunCurveData({ deviceId: props.activeDeviceId,functionLogo: that.functionLogo },props.activeDeviceType).then(res=>{
        let chartSeriesArray = [];
        let returnDataInfo = res.data ? res.data : {};
        let chartOption = JSON.parse(JSON.stringify(that.chartOption));

        if(props.activeDeviceType !== "dianZhuangXiTong"){
          let findItem = that.functionLogoArray.find(item => item.functionLogo === that.functionLogo);
          chartSeriesArray.push({
            symbol: "emptyCircle",
            type: 'line', smooth: true,
            name: findItem?.functionName,
            data: returnDataInfo.dataList,
          });
        }

        if(props.activeDeviceType === "dianZhuangXiTong"){
          if(returnDataInfo.dataInfoList && returnDataInfo.dataInfoList.length){
            for (let i = 0;i < returnDataInfo.dataInfoList.length;i++){
              chartSeriesArray.push({
                symbol: "emptyCircle",
                type: 'line', smooth: true,
                name: returnDataInfo.dataInfoList[i].name,
                data: returnDataInfo.dataInfoList[i]?.dataList,
              });
            }
          }
        }

        chartOption.series = chartSeriesArray;
        chartOption.xAxis.data = returnDataInfo.xAXisList || returnDataInfo.xaxisList;
        that.chartOption = JSON.parse(JSON.stringify(chartOption));
        that.listLoading = false;
      }).catch((err)=>{
        console.log(err);
        that.listLoading = false;
      });
    };

    const watchActiveDeviceIdAndType = watch([()=>props.activeDeviceTypeId,()=>props.activeDeviceId],([newActiveDeviceTypeId,newActiveDeviceId])=>{
      if(that.active_device_type_id !== newActiveDeviceTypeId){
        that.active_device_type_id = newActiveDeviceTypeId;
        that.functionLogo = "";
        findFunctionList();
      } else {
        changeFunctionLogoFun();
      }
    },{ deep: true,immediate: true });

    return {...toRefs(that), findFunctionList, watchActiveDeviceIdAndType, changeFunctionLogoFun, clickResetForm};
  }
});
</script>

<style lang="scss" scoped>
.deviceOperatingCurve{
  width: 100%;
  height: 280px;
}
</style>