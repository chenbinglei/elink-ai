<template>
  <div class="content_body scrollbarStyle">
    <el-row :gutter="16">
      <template v-for="(item,index) in list" :key="index">
        <el-col :lg="12" :md="24">
          <div class="content_body_li">
            <div class="content_body_li_top flex-ai-center">
              <TitleView :title="item.name"></TitleView>
            </div>
            <div class="content_body_li_bottom" v-loading="listLoading">
              <LineChartVSAndPC :chartInfo="item" :xAxisList="xAxisList" :yAxisName="item.yAxisName"></LineChartVSAndPC>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>
  </div>
</template>
<script lang="ts">
import {colorHexTurnRgba} from "@/utils";
import LineChartVSAndPC from "./LineChartVSAndPC.vue";
import {defineComponent, reactive, toRefs, onMounted, watch} from "vue";
import {findProcessAnalysisByOrderId} from "@/api/operationManagement/CsDisAndChargingRecord";

export default defineComponent({
  name: "DcOrderAnalysis",
  components: {LineChartVSAndPC},
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      xAxisList: [],
      returnDataInfo: {},
      listLoading: false,
      list: [
        {
          name: "电流",
          yAxisName: "A",
          color: ["#06DA93", "#FDAA03"],
          series: [
            {
              data: [],
              type: 'line',
              // smooth: true,
              name: '输出电流',
              showSymbol: false,
              fieldName: 'guncurrent',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#06DA93",0)},
                    {offset: 1, color: colorHexTurnRgba("#06DA93",0.5)}
                  ],
                }
              }
            },
            {
              data: [],
              type: 'line',
              // smooth: true,
              name: '需求电流',
              showSymbol: false,
              fieldName: 'gunrecurrent',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#FDAA03",0)},
                    {offset: 1, color: colorHexTurnRgba("#FDAA03",0.5)}
                  ],
                }
              }
            }
          ]
        },
        {
          name: "电压",
          color: ["#007FEB", "#E950E6"],
          yAxisName: "V",
          series: [
            {
              data: [],
              type: 'line',
              // smooth: true,
              name: '输出电压',
              showSymbol: false,
              fieldName: 'gunvoltage',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#007FEB",0)},
                    {offset: 1, color: colorHexTurnRgba("#007FEB",0.5)}
                  ],
                }
              }
            },
            {
              data: [],
              type: 'line',
              name: '需求电压',
              // smooth: true,
              showSymbol: false,
              fieldName: 'gunrevoltage',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#E950E6",0)},
                    {offset: 1, color: colorHexTurnRgba("#E950E6",0.5)}
                  ],
                }
              }
            }
          ]
        },
        {
          name: "功率",
          yAxisName: "kW",
          color: ["#13CBE3"],
          series: [
            {
              data: [],
              type: 'line',
              smooth: true,
              name: '输出功率',
              showSymbol: false,
              fieldName: 'gunpower',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#13CBE3",0)},
                    {offset: 1, color: colorHexTurnRgba("#13CBE3",0.5)}
                  ],
                }
              }
            }
          ]
        },
        {
          data: [],
          name: "SOC",
          yAxisName: "%",
          color: ["#03B7FF"],
          series: [
            {
              name: 'SOC',
              type: 'line',
              smooth: true,
              showSymbol: false,
              fieldName: 'gunsoc',

              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 1, x2: 0, y2: 0, global: false,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#03B7FF",0)},
                    {offset: 1, color: colorHexTurnRgba("#03B7FF",0.5)}
                  ],
                }
              }
            }
          ]
        },
      ],
      functionLogos: "guncurrent,gunrecurrent,gunvoltage,gunrevoltage,gunpower,gunsoc",
      // varCodes: "gunoutputcurrent,gundemandcurrent,gunoutputvoltage,gundemandvoltage,gunpower,vehiclesoc",
    });

    // 根据充放电订单id查询过程分析曲线数据
    const queryProcessAnalysisByOrderId = ()=>{
      that.listLoading = true;
      // varCodes 参数废弃，未兼容之前后端版本暂时保留，后端发布一次之后可删除
      findProcessAnalysisByOrderId({ orderId: props.orderInfo.id,functionLogos: that.functionLogos }).then(res=>{
        that.returnDataInfo = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const watchOrderInfo = watch(()=>that.returnDataInfo,(newOrderInfo)=>{
      let list = JSON.parse(JSON.stringify(that.list));

      for(let i = 0;i < list.length;i++){
        for(let j = 0;j < list[i].series.length;j++){
          list[i].series[j].data = newOrderInfo[list[i].series[j].fieldName];
        }
      }

      that.xAxisList = newOrderInfo.xAxisList;
      that.list = JSON.parse(JSON.stringify(list));
    },{ deep: true,immediate: true });

    onMounted(()=>{
      queryProcessAnalysisByOrderId();
    });

    return { ...toRefs(that),queryProcessAnalysisByOrderId,watchOrderInfo };
  }
});
</script>
<style lang="scss" scoped>
.content_body {
  height: 100%;
  overflow-y: auto;

  .content_body_li {
    border-radius: 8px;
    box-sizing: border-box;
    border: 1px solid #4ab3ff4d;
    box-shadow: 0 2px 8px 0 rgba(40, 77, 155, 0.1);
    margin-bottom: 16px;

    .content_body_li_top {
      height: 50px;
      padding-left: 12px;
      box-sizing: border-box;
      border-radius: 8px 8px 0 0;
      background: #0094ff1a;

      :deep(.header_title) {
        padding-bottom: 0;
      }
    }

    .content_body_li_bottom {
      height: 290px;
      box-sizing: border-box;
      padding: 16px 0 0 0;
    }
  }
}
</style>