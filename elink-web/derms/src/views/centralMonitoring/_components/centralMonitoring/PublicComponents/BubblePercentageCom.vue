<template>
  <div class="content_body">
    <v-chart :option="chartOption" autoresize></v-chart>
    <div class="bubble_class flex-jc-ai-center">
      <img :src="require(`@/assets/image/${ centerImg }.png`)" alt="" class="station_icon"/>
    </div>
  </div>
</template>

<script lang="ts">
import {calcNumberFun} from "@/utils";
import {reactive, defineComponent, toRefs, watch} from "vue";

export default defineComponent({
  name: "BubblePercentageCom",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 中间气泡图片名称
    centerImg: {
      type: String,
      default: "photovoltaic"
    },
    // 除数字段
    divisorField: {
      type: String,
      default: "realPower"
    },
    // 被除数字段
    dividendField: {
      type: String,
      default: "capacity"
    }
  },
  setup(props) {

    const that = reactive({
      chartOption: {
        tooltip: {
          trigger: 'item',
          position: "right",
          formatter: `当前占比：0%`
        },
        series: [
          {
            name: "内部进度条",
            type: "pie",
            startAngle: 270,
            radius: ['82%', '92%'],
            label: {
              normal: {
                show: false
              },
              emphasis: {
                show: false
              }
            },
            hoverAnimation: false,
            data: [
              {
                value: 0,
                itemStyle: {
                  color: {
                    x: 0,
                    y: 0,
                    x2: 1,
                    y2: 0,
                    global: false, // 缺省为 false
                    type: "linear",
                    colorStops: [{
                      offset: 0,
                      color: "#ACD3FF" // 0% 处的颜色
                    }, {
                      offset: 1,
                      color: "#55D1EF" // 100% 处的颜色
                    }],
                  }
                },
              },
              {value: 100, itemStyle: {color: 'transparent'}}
            ]
          },
          {
            name: '外圈',
            type: 'pie',
            startAngle: 270,
            radius: ['96%', '99%'],
            hoverAnimation: false,
            label: {
              normal: {
                show: false
              },
              emphasis: {
                show: false
              }
            },
            data: [
              {value: 0, itemStyle: {color: '#455972'}},
              {value: 100, itemStyle: {color: '#FFFFFF10'}}
            ]
          }
        ]
      }
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      let return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      let divisorField = return_data_info[props.divisorField] ?? 0;
      let dividendField = return_data_info[props.dividendField] ?? 0;

      // console.log("divisorField",divisorField);
      // console.log("dividendField",dividendField);
      let accountFor = calcNumberFun(divisorField, dividendField, '/');
      let proportion = calcNumberFun(accountFor, 100, '*');
      proportion = isNaN(proportion) ? 0 : proportion.toFixed(2);
      let bfb1 = proportion <= 100 ? proportion : 100;
      let chartOption = JSON.parse(JSON.stringify(that.chartOption));

      chartOption.series[0].data[0].value = bfb1;
      chartOption.series[0].data[1].value = 100 - bfb1;
      chartOption.series[1].data[0].value = bfb1;
      chartOption.series[1].data[1].value = 100 - bfb1;
      chartOption.tooltip.formatter =  `当前占比：${ bfb1 }%`;
      that.chartOption = JSON.parse(JSON.stringify(chartOption));
    }, {deep: true,immediate: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  width: 90px;
  height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  .bubble_class {
    z-index: 10;
    width: 64px;
    height: 64px;
    position: absolute;
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-image: url("@/assets/image/bubble_img.png");

    .station_icon {
      width: 48px;
    }
  }
}
</style>