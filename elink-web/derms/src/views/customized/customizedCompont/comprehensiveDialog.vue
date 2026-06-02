<template>
  <el-dialog title="综合分析" v-model="visible" width="64%" @close="closeDialog" style="margin-top: 1%; overflow: hidden;">
    <div class="dialog-body">
      <div class="dialog-title">
        <div class="dataType">
          <div v-for="(item, index) in tabsArray" :key="index" class="dataType-item"
            :class="{ 'active': tabsIndex === item.id }" @click="tabsIndex = item.id">
            {{ item.name }}
          </div>
        </div>

        <!-- 🔥 动态绑定格式 -->
        <el-date-picker v-if="tabsIndex == '1'" v-model="dateTime" type="daterange" style="max-width: 325px"
          range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间" :format="dateFormat"
          :value-format="dateFormat" :disabled-date="disabledDate" />
        <el-date-picker v-if="tabsIndex == '2'" v-model="dateTime" type="monthrange" style="max-width: 325px"
          range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间" :format="dateFormat"
          :value-format="dateFormat" :disabled-date="disabledDate" />
        <el-date-picker v-if="tabsIndex == '3'" v-model="dateTime" type="yearrange" style="max-width: 325px"
          range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间" :format="dateFormat"
          :value-format="dateFormat" :disabled-date="disabledDate" />

        <div class="dialog-title-right">
          <div class="dataType-item active" @click="saveDialog()">查询</div>
          <div class="dataType-item" @click="resetDialog">重置</div>
        </div>
      </div>
      <div class="cntent-box">
        <div class="table-box">
          <img src="@/assets/customized/arrow.png" class="power-statistics-icon" alt="">
          负载用电构成分析
        </div>
        <div class="power-content">
          <div class="pie-power">
            <PieChart ref="pieChart" :chartData="chartData" style="width: 100%; height: 100%;"
              :SystemNearbyArray="SystemNearbyArray" />

          </div>
          <!-- 储能 -->
          <div class="power-content-item">
            <div class="power-content-item-left icon-left">
              <img src="@/assets/customized/cbzh.png" class="power-content-item-icon" alt="">

            </div>
            <img src="@/assets/customized/zhleft.png" class="power-content-icon" alt="">
            <span>负载</span>
            <span class="power-value">
              {{ (Number(SystemNearbyArray.pvSeQt) + Number(SystemNearbyArray.gridQt)).toFixed(2) }}
            </span>


            <span>kWh</span>
            <!-- <span class="power-value">{{ SystemNearbyArray.storagePercent ?? '--' }}</span> -->
            <!-- <span>%</span> -->
          </div>

          <div class="power-content-right">
            <div class="power-content-item">
              <div class="power-content-item-left">
                <img src="@/assets/customized/gfzh.png" class="power-content-item-icons" alt="">
              </div>
              <div class="power-value-name">新能源</div>
              <div class="power-value-gf">
                {{ (SystemNearbyArray.pvSeQt ?? 0).toFixed(2) }}
              </div>
              <div>kWh</div>
              <div class="power-value-gf">{{ (SystemNearbyArray.pvSePercent?? 0 ).toFixed(2) }}</div>
              <div>%</div>
            </div>
            <div class="power-content-item">
              <div class="power-content-item-left">
                <img src="@/assets/customized/dwzh.png" class="power-content-item-icons" alt="">
              </div>
              <div class="power-value-name">电网</div>
              <div class="power-value-dw">{{ (SystemNearbyArray.gridQt?? 0).toFixed(2) }}</div>
              <div>kWh</div>
              <div class="power-value-dw">{{ (SystemNearbyArray.gridPercent?? 0 ).toFixed(2) }}</div>
              <div>%</div>
            </div>
          </div>
          <div class="connect-line line-1"></div>
          <div class="connect-line line-2"></div>
          <div class="connect-line line-3"></div>
          <div class="connect-line line-4"></div>
        </div>
      </div>
      <div class="profit-box">
        <div class="table-box">
          <img src="@/assets/customized/arrow.png" class="power-statistics-icon" alt="">
          储能收益分析
        </div>
        <div class="profit-table">
          <div class="profit-table-item">
            <div class="profit-table-item-echart" ref="changeProfit"></div>
            <div class="profit-table-item-content">
              <div class="profit-table-item-title">
                <div class="profit-table-item-title-text">
                  充电成本
                </div>
                <div class="profit-table-item-title-value">{{ SystemNearbyArray.chargeMoney ?? '--' }} <span>元</span>
                </div>

              </div>
              <div class="profit-table-item-type">
                <div v-for="(item, index) in changeProfitlist" :key="index" class="profit-table-item-type-item">
                  <img :src=item.staus alt="" class="alarm-item-icon" />
                  <div class="list-items">
                    <div class="list-item-top">
                      <div class="name">{{ item.name }}</div>
                      <div class="value">{{ item.value ?? '--' }} <span>元</span></div>
                    </div>
                    <div class="list-item-bottom"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="profit-table-item">
            <div class="profit-table-item-echart" ref="changeProfits"></div>
            <div class="profit-table-item-content">
              <div class="profit-table-item-title">
                <div class="profit-table-item-title-text">
                  放电收入
                </div>
                <div class="profit-table-item-title-value">{{ SystemNearbyArray.dischargeMoney ?? '--' }} <span>元</span>
                </div>

              </div>
              <div class="profit-table-item-type">
                <div v-for="(item, index) in dischangeProfitlist" :key="index" class="profit-table-item-type-item">
                  <img :src=item.staus alt="" class="alarm-item-icon" />
                  <div class="list-items">
                    <div class="list-item-top">
                      <div class="name">{{ item.name }}</div>
                      <div class="value">{{ item.value ?? '--' }} <span>元</span></div>
                    </div>
                    <div class="list-item-bottom"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import PieChart from './pieChart.vue'
import * as echarts from 'echarts'
import { changeoptionsProfit, disChangeoptionsProfit } from './options.js'
import jImg from '@/assets/customized/zh-j.png'
import FImg from '@/assets/customized/zh-f.png'
import PImg from '@/assets/customized/zh-p.png'
import GImg from '@/assets/customized/zh-g.png'
import SImg from '@/assets/customized/zh-s.png'
import { ref, watch, onMounted, computed, nextTick } from 'vue'
import { getSiteSystemNearby } from '@/api/customized/api'

const props = defineProps({
  isVisible: { type: Boolean, default: false },
  siteId: {
    type: String,
    default: ''
  }
})
const SystemNearbyArray = ref({})
const disabledDate = (time) => {
  // 结束日期不能大于今天
  return time.getTime() > Date.now()
}
const changeProfit = ref(null)
const changeProfits = ref(null)

const emit = defineEmits(['close'])

const visible = ref(false)
const tabsIndex = ref(1)
const tabsArray = ref([
  { name: '日', id: 1 },
  { name: '月', id: 2 },
  { name: '年', id: 3 },
])
const changeProfitlist = ref([])
const dischangeProfitlist = ref([])

const changeProfitData = ref([
  [
    { name: '充电桩1', value: 100 },
    { name: '充电桩2', value: 200 },
    { name: '充电桩3', value: 300 },
    { name: '充电桩4', value: 400 },
    { name: '充电桩5', value: 500 },
    { name: '充电桩6', value: 600 },
    { name: '充电桩7', value: 700 },
  ],
  [
    { name: '充电桩1', value: 100 },
    { name: '充电桩2', value: 500 },
    { name: '充电桩3', value: 300 },
    { name: '充电桩4', value: 400 },
    { name: '充电桩5', value: 500 },
    { name: '充电桩6', value: 600 },
    { name: '充电桩7', value: 700 },
  ],
  [
    { name: '充电桩1', value: 100 },
    { name: '充电桩2', value: 200 },
    { name: '充电桩3', value: 300 },
    { name: '充电桩4', value: 400 },
    { name: '充电桩5', value: 500 },
    { name: '充电桩6', value: 600 },
    { name: '充电桩7', value: 700 },
  ]
])
const changeProfitDataSum = ref([
  { name: '充电桩1', value: 100 },
  { name: '充电桩2', value: 200 },
  { name: '充电桩3', value: 300 },
  { name: '充电桩4', value: 400 },
  { name: '充电桩5', value: 500 },
  { name: '充电桩6', value: 600 },
  { name: '充电桩7', value: 700 },
])


// 🔥 动态格式
const dateFormat = computed(() => {
  if (tabsIndex.value === 1) return 'YYYY-MM-DD'
  if (tabsIndex.value === 2) return 'YYYY-MM'
  if (tabsIndex.value === 3) return 'YYYY'
})

const dateTime = ref([])

// 初始化
onMounted(() => {
  setDefaultDate()

})


// 切换 tab 自动更新日期
watch(tabsIndex, (val) => {
  setDefaultDate()
})



// 核心：根据类型设置正确日期
// 核心：根据类型设置正确日期
function setDefaultDate () {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');

  // 今天日期
  const endDate = `${year}-${month}-${day}`;

  if (tabsIndex.value === 1) {
    // 🔥 近30天
    const start = new Date();
    start.setDate(today.getDate() - 29);
    const startDate = `${start.getFullYear()}-${String(start.getMonth() + 1).padStart(2, '0')}-${String(start.getDate()).padStart(2, '0')}`;
    dateTime.value = [startDate, endDate];
  }

  if (tabsIndex.value === 2) {
    // 🔥 近6个月
    const startMonthNum = today.getMonth() - 5; // 减5个月 = 近6个月区间
    const startYear = startMonthNum < 0 ? year - 1 : year;
    const realStartMonth = startMonthNum < 0 ? startMonthNum + 12 : startMonthNum;
    const startMonthStr = String(realStartMonth + 1).padStart(2, '0');

    dateTime.value = [
      `${startYear}-${startMonthStr}`,
      `${year}-${month}`
    ];
  }

  if (tabsIndex.value === 3) {
    // 🔥 近3年
    dateTime.value = [`${year - 2}`, `${year}`];
  }
}
const saveDialog = () => {
  querySiteSystemNearby()
}// 工具函数：获取某月最后一天
function getLastDay (year, month) {
  return new Date(year, month, 0).getDate();
}

const querySiteSystemNearby = () => {
  const today = new Date();
  const currentYear = today.getFullYear();
  const currentMonth = today.getMonth() + 1;
  const currentDay = today.getDate();

  let startTime = '';
  let endTime = '';

  if (tabsIndex.value === 1) {
    startTime = dateTime.value[0];
    endTime = dateTime.value[1];
  }
  else if (tabsIndex.value === 2) {
    const [startMonthStr, endMonthStr] = dateTime.value;
    const [endY, endM] = endMonthStr.split('-').map(Number);

    startTime = `${startMonthStr}-01`;

    // 判断结束月是不是本月
    if (endY === currentYear && endM === currentMonth) {
      endTime = `${endY}-${String(endM).padStart(2, '0')}-${String(currentDay).padStart(2, '0')}`;
    } else {
      const lastDay = getLastDay(endY, endM);
      endTime = `${endY}-${String(endM).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`;
    }
  }
  else if (tabsIndex.value === 3) {
    const [startYearStr, endYearStr] = dateTime.value;
    const endY = Number(endYearStr);

    startTime = `${startYearStr}-01-01`;

    // 判断结束年是不是今年
    if (endY === currentYear) {
      endTime = `${currentYear}-${String(currentMonth).padStart(2, '0')}-${String(currentDay).padStart(2, '0')}`;
    } else {
      endTime = `${endY}-12-31`;
    }
  }

  let obj = {
    dateType: tabsIndex.value,
    startDate: startTime,
    endDate: endTime,
    queryType: 1,
    siteId: props.siteId,
    dataId: '1'
  };

  getSiteSystemNearby(obj).then(res => {
    const arr = res.data;
    SystemNearbyArray.value = arr;
    changeProfitlist.value = [
      { name: "尖", value: arr.topChargeMoney, staus: jImg },
      { name: "峰", value: arr.peakChargeMoney, staus: FImg },
      { name: "平", value: arr.plainChargeMoney, staus: PImg },
      { name: "谷", value: arr.valleyChargeMoney, staus: GImg },
      { name: "深", value: arr.deepChargeMoney, staus: SImg }
    ];
    dischangeProfitlist.value = [
      { name: "尖", value: arr.topDischargeMoney, staus: jImg },
      { name: "峰", value: arr.peakDischargeMoney, staus: FImg },
      { name: "平", value: arr.plainDischargeMoney, staus: PImg },
      { name: "谷", value: arr.valleyDischargeMoney, staus: GImg },
      { name: "深", value: arr.deepDischargeMoney, staus: SImg }
    ];
    getChangeProfit();
  });
};
const getChangeProfit = () => {
  if (!changeProfit.value) return
  const chart = echarts.init(changeProfit.value)
  const chartSum = echarts.init(changeProfits.value)


  const options = changeoptionsProfit(SystemNearbyArray.value)
  const optionsSum = disChangeoptionsProfit(SystemNearbyArray.value)

  chart.setOption(options)
  chartSum.setOption(optionsSum)

}

// 打开关闭同步
watch(
  () => props.isVisible,
  (val) => {
    visible.value = val
    if (val) {
      nextTick(() => {
        querySiteSystemNearby()

      })
    }
  }
)
watch(visible, (val) => {
  if (!val) emit('close')
})

const closeDialog = () => {
  emit('close')
}

const resetDialog = () => {
  tabsIndex.value = 1
  setDefaultDate()
  querySiteSystemNearby()
}
</script>

<style scoped lang="scss">
.dialog-body {
  padding: 20px;
  color: #fff;

  .dialog-title {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 14px;
  }

  .dataType {
    display: flex;
    align-items: center;
    border: 1px solid #0071cc;
    border-radius: 4px;
    overflow: hidden;

    .dataType-item {
      padding: 7px 23px;
      cursor: pointer;
    }

    .active {
      background: linear-gradient(180deg, #54a1df 0%, #005599 82.42%, #40a5fe 100%);
    }
  }

  .dialog-title-right {
    display: flex;
    align-items: center;
    gap: 8px;

    .dataType-item {
      padding: 7px 23px;
      border-radius: 4px;
      border: 1px solid #0071cc;
      cursor: pointer;
    }

    .active {
      background: linear-gradient(180deg, #54a1df 0%, #005599 82.42%, #40a5fe 100%);
    }
  }

  .cntent-box {
    margin-top: 30px;

    .table-box {
      display: flex;
      align-items: center;
      gap: 12px;
      font-family: YouSheBiaoTiHei, sans-serif;
      font-size: 20px;
      color: #fff;

      .power-statistics-icon {
        width: 38px;
        height: 38px;
      }
    }

    .power-content {
      display: flex;
      align-items: center;
      /* 垂直居中 */
      justify-content: space-around;
      margin-top: 20px;
      position: relative;

      /* 必须加，让连线绝对定位 */
      // 连线样式
      .connect-line {
        position: absolute;
        z-index: 2;
        pointer-events: none;
        transform-origin: left center;

        /* 箭头公共样式 */
        &::after {
          content: "";
          position: absolute;
          width: 4px;
          height: 4px;
          border-top: 2px solid #fff;
          border-right: 2px solid #fff;
          top: -4px;
          right: 0;
          transform: rotate(45deg);
        }

        &.line-3 {
          top: 26%;
          left: 60%;
          width: 6.5%;
          height: 0;
          border-top: 1px solid rgb(255, 255, 255, .8)
        }

        &.line-4 {
          top: 71%;
          left: 60%;
          width: 6.5%;
          height: 0;
          border-top: 1px solid rgb(255, 255, 255, .8)
        }



        &.line-1 {
          top: 50%;
          left: 55%;
          width: 5%;
          height: 0;
          border-top: 1px solid rgb(255, 255, 255, .8);

          &::after {
            display: none;
          }
        }

        &.line-2 {
          // 负载 → 电网
          top: 25.5%;
          left: 60%;
          width: 0;
          height: 47%;
          border-right: 1px solid rgb(255, 255, 255, .8);

          &::after {
            display: none;
          }
        }
      }

      .pie-power {
        width: 20%;
        height: 120px;
        background: url(/src/assets/customized/pie-bottom.png) no-repeat;
        background-size: 100% 100%;
      }

      // span {
      //   display: inline-block;
      //   vertical-align: baseline;
      //   line-height: 1;
      //   padding: 0;
      //   margin: 0;
      // }
      .power-value-name {
        width: 30%;
      }

      /* 普通文字 */
      div:not(.power-value, .power-value-gf, .power-value-dw) {

        font-family: MicrosoftYaHeiRegular, MicrosoftYaHeiRegular;
        font-size: 14px;
        color: #FFFFFF;
        margin-left: 7px;
      }

      /* 数值文字 */
      .power-value {
        font-family: DIN, DIN;
        font-weight: 500;
        font-size: 20px;
        color: #00FFFF;
        margin-left: 30px;
      }

      .power-value-gf {
        font-family: DIN, DIN;
        font-weight: 500;
        font-size: 20px;
        color: #FF6E00;
        margin-left: 30px;

      }

      .power-value-dw {
        font-family: DIN, DIN;
        font-weight: 500;
        font-size: 20px;
        color: #009BFF;
        margin-left: 30px;

      }


      .power-content-item {
        display: flex;
        align-items: baseline;
        /* 🔥 关键：所有文字底部对齐 */
        position: relative;
        white-space: nowrap;

        .icon-left {
          background: url(/src/assets/customized/border-zh.png) no-repeat;
          background-size: 100% 100%;
        }

        .power-content-item-left {

          width: 19px;
          height: 19px;
          display: flex;
          align-items: center;
          justify-content: center;

          margin-right: 12px;

          .power-content-item-icon {
            width: 9px;
            height: 9px;
          }

          .power-content-item-iconss {
            width: 24px;
            height: 20px;
          }
        }

        .power-content-icon {
          position: absolute;
          left: 6px;
          top: calc(50% - 9.5px);
          width: 19px;
          height: 19px;
        }
      }
    }

    .power-content-right {
      display: flex;
      flex-direction: column;
      gap: 30px;
    }
  }

  .profit-box {
    .table-box {
      display: flex;
      align-items: center;
      gap: 12px;
      font-family: YouSheBiaoTiHei, sans-serif;
      font-size: 20px;
      color: #fff;

      .power-statistics-icon {
        width: 38px;
        height: 38px;
      }
    }

    .profit-table {
      height: 520px;
      width: 100%;

      .profit-table-item {
        height: 48%;
        width: 100%;
        display: flex;
        align-items: center;

        .profit-table-item-echart {
          height: 100%;
          width: 65%;
        }

        .profit-table-item-content {
          width: 33%;
          height: 100%;

          .profit-table-item-title {
            display: flex;
            text-align: center;
            justify-content: space-between;

            .profit-table-item-title-text {
              font-family: YouSheBiaoTiHei, YouSheBiaoTiHei;
              font-weight: 400;
              font-size: 20px;
              color: #FFFFFF;
              background: url(/src/assets/customized/titlezh.png) no-repeat;
              background-size: 100% 8px;
              /* 固定高度为 8px，宽度自适应 */
              // height: 8px;
              width: 72%;
              text-align: left;
              background-position: center bottom;

            }

            .profit-table-item-title-value {

              font-family: DIN, DIN;
              font-weight: 500;
              font-size: 22px;
              color: #00E883;

              span {

                font-family: Microsoft YaHei, Microsoft YaHei;
                font-weight: 400;
                font-size: 14px;
                color: #FFFFFF;

              }
            }
          }

          .profit-table-item-type {
            height: 72%;
            margin-top: 16px;
            width: 100%;
            padding-top: 5px;
            background: url(/src/assets/customized/zh-r-border.png) no-repeat;
            background-size: 100% 100%;

            .profit-table-item-type-item {
              height: 20%;
              margin: 0 auto;
              padding: 0 18px 0 20px;
              display: flex;
              align-items: center;

              .list-items {

                padding: 0 18px 0 20px;
                width: 80%;

                .list-item-top {

                  display: flex;
                  padding: 0 19px;
                  align-items: center;
                  justify-content: space-between;

                  .name {
                    font-family: Microsoft YaHei, Microsoft YaHei;
                    font-weight: 400;
                    font-size: 14px;
                    color: #FFFFFF;
                  }

                  .value {
                    font-family: DIN, DIN;
                    font-weight: 500;
                    font-size: 20px;
                    color: #00FFFF;

                    span {

                      font-family: Microsoft YaHei, Microsoft YaHei;
                      font-weight: 400;
                      font-size: 14px;
                      color: #FFFFFF;
                    }
                  }
                }

                .list-item-bottom {
                  background: url(/src/assets/customized/list-zh.png) no-repeat;
                  background-size: 100% 100%;
                  height: 3px;
                }

              }

              .img {
                width: 55px;
                height: 23px;
              }

              // background: url(/src/assets/customized/list-zh.png) no-repeat;
              // background-size: 100% 100%;
            }
          }

        }

      }
    }

  }
}

::v-deep .custom-tooltip-box {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;
  color: #ffffff;


  .custom-tooltip-style {

    background: rgba(4, 57, 90, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 10px 15px;


    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }


    .custom-radio {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
      margin-right: 5px;
      box-sizing: border-box;

    }



    .custom-tooltip-value {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      margin-left: 20px;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
  }
}
</style>