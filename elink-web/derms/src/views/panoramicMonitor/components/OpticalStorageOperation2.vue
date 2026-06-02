<template>
  <div class="optical-storage-operation">
    <div class="optical-storage-operation-title w-full flex jc-space-between">
      <div class="real-time-power-title flex-ai-center">
        <img
          src="@/assets/image/panoramic-monitor/p-m-header-icon5.png"
          alt=""
          width="20"
        />
        <span class="ml-2">光储运营</span>
      </div>
      <TabGroup
        class="right-tab"
        :tab-active="activeTab"
        :tab-list="tabList"
        @tabChangeEvent="tabChangeHandler"
      ></TabGroup>
    </div>
    <div class="gfzfdl flex">
      <div class="gfzfdl-icon">
        <img
          src="@/assets/image/panoramic-monitor/icon-gfcn.png"
          alt=""
          width="95"
          v-if="activeTab == 0"
        />
        <img
          src="@/assets/image/panoramic-monitor/icon-gfcn2.png"
          alt=""
          width="95"
          v-else
        />
      </div>
      <div class="gfzfdl-text flex jc-center" v-if="activeTab == 0">
        <p>光伏总发电量</p>
        <div class="gfzfdl-value">
          <span class="special-text ml-5">{{ pvOperation.totalQt }}</span
          >kWh
        </div>
      </div>
      <div class="gfzfdl-text flex jc-center" v-if="activeTab == 1">
        <p>储能总充/放电量</p>
        <div class="gfzfdl-value">
          <span class="special-text ml-5"
            >{{ storageOperation.chargeTotalQt }}/{{
              storageOperation.dischargeTotalQt
            }}</span
          >kWh
        </div>
      </div>
    </div>
    <!-- <div class="details">
      <template v-for="(citem, index) in topTotal[activeTab]">
        <div
          class="details-item flex"
          style="width: calc((100% - 20px) / 2)"
          :class="index == 0 ? '' : 'pl-5'"
        >
          <div><img :src="citem.img" alt="" width="30" /></div>
          <div class="ml-2 pt-2">
            <div class="details-text">{{ citem.title }}</div>
            <div class="details-value flex">
              <span>{{
                activeTab == 0
                  ? pvOperation[citem.numberProp]
                  : storageOperation[citem.numberProp]
              }}</span
              >{{ citem.unit }}
            </div>
            <div
              class="details-state flex mt-3"
              :style="
                backgroundStyle(
                  activeTab == 0
                    ? pvOperation[citem.jiaosyProp]
                    : storageOperation[citem.jiaosyProp]
                )
              "
            >
              较上月<span
                :class="
                  getClass(
                    activeTab == 0
                      ? pvOperation[citem.jiaosyProp]
                      : storageOperation[citem.jiaosyProp]
                  )
                "
                >{{
                  activeTab == 0
                    ? pvOperation[citem.jiaosyProp]
                    : storageOperation[citem.jiaosyProp]
                }}</span
              >
            </div>
          </div>
        </div>
        <div class="details-item-line" v-if="index == 0"></div>
      </template>
    </div> -->
    <!-- <div class="segmentation-line"></div> -->
    <div class="gcyy" v-if="activeTab == 0">
      <div class="flex">
        <div class="gcyy-icon">
          <img
            src="@/assets/image/panoramic-monitor/icon-gcyy-3.png"
            alt=""
            width="29"
          />
        </div>
        <div class="w-full ml-2">
          <div class="gcyy-top flex jc-space-between">
            <div
              class="gcyy-top-item"
              :class="item.className"
              v-for="(item, index) in bottomTotal[0]"
              :key="'topTotal' + index"
            >
              <div class="gcyy-top-text">{{ item.label }}</div>
              <div class="gcyy-top-value">
                <span class="special-text">{{ pvOperation[item.numProp] }}</span
                >{{ item.unit }}
              </div>
            </div>
          </div>
          <div class="gcyy-progress-container flex_s_c">
            <div class="gcyy-progress" style="width: 60%"></div>
            <div class="gcyy-progress1" style="width: 40%"></div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="activeTab == 1" class="flex jc-space-between pl-5 pr-5">
      <template v-for="(item, index) in bottomTotal[1]">
        <div class="flex ai-center fd-column">
          <div style="padding: 13px 0">
            <img :src="item.img" alt="" width="103" height="88" />
          </div>
          <div class="text-white mb-2">
            {{ item.label }}
            <span class="special-text ml-2 mr-2" :class="item.className">{{
              storageOperation[item.numProp]
            }}</span>
            {{ item.unit }}
          </div>
        </div>
        <div class="details-item-line" v-if="index == 0"></div>
      </template>
    </div>
    <div class="chart-content">
      <EchartsTwo
        ref="echart1"
        width="100%"
        height="100%"
        font-size="12"
        :data="chartData"
      ></EchartsTwo>
    </div>
    <div class="gcyy-bottom flex jc-space-between" v-if="activeTab == 0">
      <div class="gcyy-bottom-item flex">
        <div>
          <img
            src="@/assets/image/panoramic-monitor/icon-gcyy-4.png"
            alt=""
            width="29"
          />
        </div>
        <div class="ml-2">
          <div class="gcyy-bottom-value special-text">
            <span>{{ pvOperation.standardCoalReduction }}</span
            >t
          </div>
          <div class="gcyy-bottom-text">节约标煤</div>
        </div>
      </div>
      <div class="gcyy-bottom-line"></div>
      <div class="gcyy-bottom-item flex">
        <div>
          <img
            src="@/assets/image/panoramic-monitor/icon-gcyy-4.png"
            alt=""
            width="29"
          />
        </div>
        <div class="ml-2">
          <div class="gcyy-bottom-value special-text">
            <span>{{ pvOperation.co2Reduction }}</span
            >t
          </div>
          <div class="gcyy-bottom-text">CO2减排</div>
        </div>
      </div>
      <div class="gcyy-bottom-line"></div>
      <div class="gcyy-bottom-item flex">
        <div>
          <img
            src="@/assets/image/panoramic-monitor/icon-gcyy-4.png"
            alt=""
            width="29"
          />
        </div>
        <div class="ml-2">
          <div class="gcyy-bottom-value special-text">
            <span>{{ pvOperation.treeReduction }}</span
            >棵
          </div>
          <div class="gcyy-bottom-text">等效植树</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, reactive, toRefs, watchEffect } from "vue";
import { TabGroup } from "@/views/panoramicMonitor/components/index";
import EchartsTwo from "@/components/echart2/echartsTwo.vue";
import arrowLine1 from "@/assets/image/panoramic-monitor/arrow-line-1.png";
import arrowLine2 from "@/assets/image/panoramic-monitor/arrow-line-2.png";
export default defineComponent({
  name: "opticalStorageOperation",
  components: { EchartsTwo, TabGroup },
  props: {
    // 储能
    storageOperation: {
      type: Object,
      required: true,
      default: () => {},
    },
    // 光伏
    pvOperation: {
      type: Object,
      required: true,
      default: () => {},
    },
    // 日期项
    operationDateList: {
      type: Array,
      required: true,
      default: () => [],
    },
  },
  setup(props) {
    // 获取图片路径
    function getImageUrl(name) {
      return new URL(
        `/src/assets/image/panoramic-monitor/${name}.png`,
        import.meta.url
      ).href;
    }
    // 自定义样式
    const backgroundStyle = (num) => {
      let obj = {};
      if (Number(num) > 0) {
        obj = {
          background: `url(${arrowLine1}) no-repeat`,
          backgroundSize: "100% auto",
          backgroundPosition: "bottom",
        };
      } else if (Number(num) < 0) {
        obj = {
          background: `url(${arrowLine2}) no-repeat`,
          backgroundSize: "100% auto",
          backgroundPosition: "top",
        };
      }
      return obj;
    };
    // 自定义类
    const getClass = (num) => {
      return Number(num) > 0 ? "green-num" : "yellow-num";
    };
    // 基础数据
    const state = reactive({
      tabList: ["光伏", "储能"],
      activeTab: 0,
      topTotal: {
        0: [
          {
            img: getImageUrl("icon-gfcn-1"),
            title: "日均等效发电时长",
            unit: "小时/天",
            numberProp: "dayEffectiveTime",
            jiaosyProp: "dayEffectiveCompare",
          },
          {
            img: getImageUrl("icon-gfcn-2"),
            title: "效率PR",
            unit: "%",
            numberProp: "systemEfficiency",
            jiaosyProp: "systemEfficiencyCompare",
          },
        ],
        1: [
          {
            img: getImageUrl("icon-gfcn-3"),
            title: "本月充放循环次数",
            unit: "次",
            numberProp: "monthTimes",
            jiaosyProp: "monthTimesCompare",
          },
          {
            img: getImageUrl("icon-gfcn-4"),
            title: "本月综合效率",
            unit: "%",
            numberProp: "monthEfficiency",
            jiaosyProp: "monthEfficiencyCompare",
          },
        ],
      },
      bottomTotal: {
        0: [
          {
            label: "本月发电量",
            numProp: "monthQt",
            unit: "kWh",
            className: "green",
          },
          {
            label: "本月消纳电量",
            numProp: "monthConsumeQt",
            unit: "kWh",
            className: "yellow",
          },
          {
            label: "消纳率",
            numProp: "montConsumeRate",
            unit: "%",
            className: "blue",
          },
        ],
        1: [
          {
            img: getImageUrl("icon-gfcn-5"),
            label: "当前可充电量",
            numProp: "chargeCurrentQt",
            unit: "kWh",
            className: "green-num",
          },
          {
            img: getImageUrl("icon-gfcn-6"),
            label: "当前可放电量",
            numProp: "dischargeCurrentQt",
            unit: "kWh",
            className: "yellow-num",
          },
        ],
      },
      chartData: { names: [], datas: [] },
    });
    // tab切换
    const tabChangeHandler = (index) => {
      state.activeTab = index;
    };
    // echarts数据加载
    const loadChartData = (serverData) => {
      let legendData =
        state.activeTab == 0
          ? [
              {
                name: "月实际发电量",
                icon: "rect",
                textStyle: { color: "#ffffff", fontSize: "12" },
              },
              {
                name: "去年同期",
                icon: "rect",
                textStyle: { color: "#ffffff", fontSize: "12" },
              },
            ]
          : [
              {
                name: "充电量",
                icon: "rect",
                textStyle: { color: "#ffffff", fontSize: "12" },
              },
              {
                name: "放电量",
                icon: "rect",
                textStyle: { color: "#ffffff", fontSize: "12" },
              },
            ];
      let series =
        state.activeTab == 0
          ? [
              {
                name: "月实际发电量",
                type: "bar",
                barWidth: "15%",
                showBackground: false,
                itemStyle: { color: "#00FFB1" },
                // xAxisIndex: 1,
                data: serverData[1],
                // z: 11,
              },
              {
                name: "去年同期",
                type: "bar",
                barWidth: "15%",
                showBackground: false,
                itemStyle: { color: "#FFA700" },
                data: serverData[2],
                // xAxisIndex: 0,
              },
            ]
          : [
              {
                name: "充电量",
                type: "bar",
                barWidth: "15%",
                showBackground: false,
                itemStyle: { color: "#00FFB1" },
                // xAxisIndex: 1,
                data: serverData[1],
                // z: 11,
              },
              {
                name: "放电量",
                type: "bar",
                barWidth: "15%",
                showBackground: false,
                itemStyle: { color: "#FFA700" },
                data: serverData[2],
                // xAxisIndex: 0,
              },
            ];
      const tooltipFormatter = (params) => {
        let res = // 字符串形式的html标签会被echarts转换渲染成数据，这个res主要是画的tooltip里的上部分的标题部分
          "<div style='margin-bottom:5px;padding:0 6px;width:100%;height:18px;line-height:18px;border-radius:3px;'><p>" +
          params[0].name +
          " </p></div>";
        for (var i = 0; i < params.length; i++) {
          //因为是个数组，所以要遍历拿到里面的数据，并加入到tooltip的数据内容部分里面去
          res += `<div style="font-size: 14px; padding:0 6px;line-height: 18px">
                  <span style="display:inline-block;margin-right:5px;border-radius:2px;width:10px;height:10px;background-color:${[
                    params[i].color, // 默认是小圆点，我们将其修改成有圆角的正方形，这里用的是模板字符串。并拿到对应颜色、名字、数据
                  ]};"></span>
                  ${params[i].seriesName}
                  ${params[i].data}
                </div>`;
        }
        return res; // 经过这么一加工，最终返回出去并渲染，最终就出现了我们所看的效果
      };
      let chartData2 = {
        xAxis: {
          type: "category",
          name: "",
          data: serverData[0],
          axisTick: { show: false },
          axisLabel: { fontSize: "12", color: "#FFFFFF" },
          axisLine: { lineStyle: { color: "#D4DBE266" } },
          // z: 10,
        },
        yAxis: {
          type: "value",
          name: "kWh",
          axisTick: { show: false },
          splitLine: { lineStyle: { color: "#005B8B", type: "dashed" } },
          nameTextStyle: { fontSize: "12", color: "#FFFFFF" },
          axisLine: { show: true, lineStyle: { width: 1, color: "#D4DBE266" } },
          axisLabel: { fontSize: "12", color: "#FFFFFF" },
        },
        tooltip: {
          trigger: "axis",
          textStyle: { fontSize: 14 },
          axisPointer: null,
          formatter: tooltipFormatter,
        },
        dataZoom: [{ type: "inside" }],
        legend: {
          show: true,
          itemHeight: 10,
          itemWidth: 10,
          top: "1%",
          right: "0%",
          data: legendData,
        },
        grid: { left: "10%", right: "0%", top: "20%", bottom: "20%" },
        series,
      };

      return chartData2;
    };
    // 监听数据变化 echarts
    watchEffect(() => {
      let data = [
        props.operationDateList,
        state.activeTab == 0
          ? props.pvOperation?.qtList
          : props.storageOperation?.chargeQtList,
        state.activeTab == 0
          ? props.pvOperation?.qtLastYearList
          : props.storageOperation?.dischargeQtList,
      ];
      state.chartData = loadChartData(data);
    });
    return { ...toRefs(state), tabChangeHandler, backgroundStyle, getClass };
  },
});
</script>
<style scoped lang="scss">
.optical-storage-operation {
  .details-item-line {
    width: 20px;
    height: 118px;
    background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-1.png")
      no-repeat;
    background-size: 100% 100%;
  }
  box-sizing: border-box;
  padding: 0 20px;
  font-family: Agency FB, Agency FB;
  width: 100%;
  height: 100%;
  background: url("@/assets/image/panoramic-monitor/p-m-header-bg.png")
    no-repeat;
  background-size: 100% 100%;
  .optical-storage-operation-title {
    font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
    font-size: 18px;
    color: #fff;
  }
  .gfzfdl {
    padding: 18px 0 8px 0;
    color: #fff;
    .gfzfdl-icon {
      position: relative;
      top: -10px;
    }
    .gfzfdl-text {
      width: calc(100% - 95px);
      line-height: 44px;
      height: 44px;
      background: url("@/assets/image/panoramic-monitor/info-bg.png") no-repeat;
      background-size: 100% 100%;
      font-size: 14px;
      .gfzfdl-value {
        font-size: 14px;
        font-family: Microsoft YaHei, Microsoft YaHei;
        span {
          margin-right: 15px;
          font-size: 24px;
        }
      }
    }
  }
  .details {
    display: flex;
    color: #fff;
    font-size: 14px;
    .details-value {
      font-size: 12px;
      line-height: 30px;
      span {
        font-size: 18px;
        color: #00ffb1;
        font-family: Agency FB, Agency FB;
        font-weight: 400;
        margin-right: 30px;
      }
    }
    .details-state {
      width: 95px;
      padding-top: 10px;
      padding-bottom: 5px;
      font-size: 12px;
      color: #00ccff;
      flex: 1;
      span {
        font-size: 18px;
        font-family: Agency FB, Agency FB;
        font-weight: 400;
        color: #00ffb1;
        margin-left: 20px;
      }
    }
    .details-item-line {
      width: 20px;
      height: 118px;
      background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-1.png")
        no-repeat;
      background-size: 100% 100%;
    }
  }
  .gcyy {
    width: 100%;
    color: #fff;
    margin-bottom: 34px;
    .gcyy-top {
      span {
        display: inline-block;
        width: 38px;
        font-size: 18px;
      }
      .gcyy-top-item {
        .gcyy-top-text {
          margin-bottom: 5px;
        }
        .gcyy-top-value {
          font-size: 14px;
          font-family: Microsoft YaHei, Microsoft YaHei;
        }
        &.green {
          margin-right: 53px;
          span {
            color: #00ffb1;
          }
        }
        &.yellow {
          margin-right: 53px;
          span {
            color: #ffa700;
          }
        }
        &.blue {
          span {
            color: #00ccff;
          }
        }
      }
    }
    .gcyy-progress {
      margin-top: 6px;
      width: 322px;
      height: 6px;
      background: linear-gradient(
        to right,
        #00ffb1 0%,
        #00ffb1 75%,
        transparent 76%,
        transparent 100%
      );
      background-size: 12px 6px;
    }

    .gcyy-progress1 {
      margin-top: 6px;
      width: 322px;
      height: 6px;
      background: linear-gradient(
        to right,
        #ffa700 0%,
        #ffa700 75%,
        transparent 76%,
        transparent 100%
      );
      background-size: 12px 6px;
    }
  }
  .gcyy-bottom {
    .gcyy-bottom-item {
      color: #fff;
      .gcyy-bottom-value {
        font-size: 12px;
        margin-bottom: 2px;
        span {
          display: inline-block;
          width: 42px;
          font-size: 18px;
        }
      }
      .gcyy-bottom-text {
        font-size: 14px;
      }
    }
    .gcyy-bottom-line {
      width: 16px;
      height: 54px;
      background: url("@/assets/image/panoramic-monitor/vertica-ldivider-line-3.png")
        top left no-repeat;
    }
  }
  .segmentation-line {
    width: 90%;
    height: 7px;
    margin: 0 auto 16px;
    background: linear-gradient(
      180deg,
      rgba(78, 186, 255, 0) 0%,
      rgba(78, 186, 255, 0.83) 100%
    );
    opacity: 0.81;
  }
  .chart-content {
    width: 100%;
    height: 30%;
  }
}
</style>