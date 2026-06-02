<template>
  <div>
    <div class="photovoltaic-overview">
      <div class="left">
        <template v-if="list.length">
          <div class="block-box" v-for="(data, id) in list" :key="'box' + id">
            <div class="block-box-title">{{ data.typeName }}</div>
            <div class="block-list">
              <div class="block-list-content" v-for="(item, index) in data.deviceList" :key="'gfblock' + index">
                <!-- <div class="block" :class="transFormStatusToText(data.typeId,item.deviceStatus).bac"> -->
                <div class="block" :class="transFormStatusToText(data.typeId, item.deviceStatusName).bac">
                  <div class="block-title">{{ item.deviceName }}</div>
                  <div class="block-content">
                    <div class="block-left">
                      <div class="block-icon">
                        <!-- <i :class="data.class"></i> -->
                        <!-- <span>{{ transFormStatusToText(data.typeId,item.deviceStatus).name }}</span> -->
                        <span>{{ item.deviceStatusName ?? '未知' }}</span>

                      </div>
                    </div>
                    <div class="block-right">
                      <div class="block-text" v-for="(child, idx) in item.dataList" :key="'blocktext' + idx">
                        <p>{{ child.name }}</p>
                        <div class="flex_e_c">
                          <span>{{ child.value ? child.value?.toFixed(3) : '--' }}</span>
                          <!-- <p>{{  child.value }}</p> -->
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <div v-else class="flex ai-center w-full h-full jc-center">
          <el-empty :image="emptyImg" image-size="260" />
        </div>
      </div>
      <div class="right">
        <div class="right-top">
          <div class="info-block" v-for="(item, index) in rightTopList" :key="'rightinfo' + index">
            <div class="info-value">
              <span>{{ item.value || 0 }}</span>{{ item.unit }}
            </div>
            <div class="info-text">{{ item.text }}</div>
          </div>
        </div>
        <div class="right-chart-bg first">
          <div class="right-chart-top">
            <div class="right-chart-title">
              <img src="@/assets/image/station-details/photovoltaic-icon-1.png" alt="" width="20" height="20" />
              <span>{{ titleObj[type].rTopName }}</span>
            </div>
          </div>
          <div class="right-chart-content">
            <slot name="rightTop"></slot>
            <div class="right-chart-title-right-date">
              <el-date-picker v-model="monthTimeDate1" :disabled-date="pickerOptions.disabledDate" :clearable="false"
                format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 240px" type="date"
                @change="querySystemVarOrCurveDataTop" />
            </div>
          </div>
        </div>
        <div class="right-chart-bg">
          <div class="right-chart-top">
            <div class="right-chart-title">
              <img src="@/assets/image/station-details/photovoltaic-icon-2.png" alt="" width="20" height="20" />
              <span>{{ titleObj[type].rBotName }}</span>
            </div>
            <div class="right-chart-title-right">
              <StationTabGroup style="padding: 0" :tab-list="tabList" :tab-active="0"
                @tabChangeEvent="tabChangeHandler">
              </StationTabGroup>
            </div>
          </div>
          <div class="right-chart-content">
            <slot name="rightBottom"></slot>
            <div class="right-chart-title-right-date">
              <template v-if="tabActive === 0">
                <el-date-picker v-model="dayTimeDate" :disabled-date="pickerOptions.disabledDate"
                  :shortcuts="pickerOptions.shortcuts" end-placeholder="结束时间" format="YYYY-MM-DD" range-separator="~"
                  start-placeholder="开始时间" style="width: 360px" type="daterange" :clearable="false"
                  value-format="YYYY-MM-DD" @change="querySystemVarOCurveDataBot" />
              </template>
              <template v-if="tabActive === 1">
                <el-date-picker v-model="monthTimeDate" :disabled-date="pickerOptions.disabledDate"
                  end-placeholder="结束时间" format="YYYY-MM" range-separator="~" :clearable="false"
                  start-placeholder="开始时间" style="width: 360px" type="monthrange" value-format="YYYY-MM"
                  @change="querySystemVarOCurveDataBot" />
              </template>
              <template v-if="tabActive === 2">
                <el-date-picker v-model="yearTimeDate" :disabled-date="pickerOptions.disabledDate"
                  end-placeholder="结束时间" format="YYYY" range-separator="~" :clearable="false" start-placeholder="开始时间"
                  style="width: 360px" type="yearrange" value-format="YYYY" @change="querySystemVarOCurveDataBot" />
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import emptyImg1 from '@/assets/image/empty.png';
import { reactive, defineComponent, toRefs, watch, inject, ref } from "vue";
import EChartsCategory from "@/components/echart2/echartsCategory.vue";
import EchartsBar2D from "@/components/echart2/echartsBar2D.vue";
import EchartsBar2DBg from "@/components/echart2/echartsBar2DBg.vue";
import { StationTabGroup, tablist } from "@/views/stationDetails/components";
import { deviceDCCStatusText, deviceDQStatusText, deviceStatusText } from "@/common/enum.js";
import {
  getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getNowDate,
  getNowDateAll, isMonth, isToday, isYear, pickerDateOneMonthDay, pickerOptionsGthanAcTime
} from "@/utils/dateTime";
import { getDeviceList } from "@/api/monitoringCenter/monitoringCenter.js";
import { useRoute } from "vue-router";

//电站总览
export default defineComponent({
  name: "PhotovoltaicOverview",
  components: {
    EChartsCategory,
    StationTabGroup,
    EchartsBar2D,
    EchartsBar2DBg,
    tablist,
  },
  emits: ["querySystemVarOCurveDataBot", "querySystemVarOrCurveDataTop"],
  props: {
    rightTopList: {
      type: Array,
      default: () => []
    },
    type: {
      type: Number,
      default: 1
    }
  },
  setup (props, { emit }) {
    const siteId = inject('siteId')
    const state = reactive({
      list: [],
      tabActive: 0,
      tabList: ["日", "月", "年"],
      monthTimeDate1: getNowDate(),
      yearTimeDate: [getDaysFromCurrentTime(-722, 2), getDaysFromCurrentTime(0, 2)],
      monthTimeDate: [getDaysFromCurrentTime(-180, 1), getDaysFromCurrentTime(0, 1)],
      dayTimeDate: pickerDateOneMonthDay(29),
      timeDate: getNowDate(),
      pickerOptions: pickerOptionsGthanAcTime(),
    });
    const emptyImg = ref(emptyImg1);
    const titleObj = ref({
      1: {
        rTopName: '光伏功率曲线',
        rBotName: '光伏发电量',
      },
      2: {
        rTopName: '储能功率曲线',
        rBotName: '储能充放电量',
      },
      3: {
        rTopName: '电桩功率曲线',
        rBotName: '电桩充放电量',
      },
      6: {
        rTopName: '换电站功率曲线',
        rBotName: '换电站充电量',
      },
    })
    // 获取设备列表
    const searchDeviceList = (data) => {
      getDeviceList(data).then((res) => {
        state.list = res.data
      })
        .catch((err) => {
          console.log(err)
        });
    };
    const route = useRoute();
    watch(() => props.type,
      (newId) => {
        if (newId) searchDeviceList({ type: props.type, siteId: siteId.value });
      }, { immediate: true }
    );
    // const transFormStatusToText = (type,status) => { 
    //   if(type == 25){
    //     return {
    //       name: deviceDCCStatus[status]?.name || '未知',
    //       bac: deviceDCCStatus[status]?.bac || 'gray',
    //       icon: deviceDCCStatus[status]?.icon || '',
    //     }
    //   }else if(type == 29 || type == 30 || type == 28){
    //     return {
    //       name: deviceDQStatus[status]?.name || '未知',
    //       bac: deviceDQStatus[status]?.bac || 'gray',
    //       icon: deviceDQStatus[status]?.icon || '',
    //     }
    //   }else{
    //     return {
    //       name: deviceStatus[status]?.name || '未知',
    //       bac: deviceStatus[status]?.bac || 'gray',
    //       icon: deviceStatus[status]?.icon || '',
    //     }
    //   }
    // };
    const transFormStatusToText = (type, status) => {
      if (type == 25) {
        return {
          name: status,
          bac: deviceDCCStatusText[status]?.bac || 'gray',
          icon: deviceDCCStatusText[status]?.icon || '',
        }
      } else if (type == 29 || type == 30 || type == 28 || type == 79) { 
        return {
          name: status,
          bac: deviceDQStatusText[status]?.bac || 'gray',
          icon: deviceDQStatusText[status]?.icon || '',
        }
      } else {
        return {
          name: status,
          bac: deviceStatusText[status]?.bac || 'gray',
          icon: deviceStatusText[status]?.icon || '',
        }
      }
    };

    const querySystemVarOrCurveDataTop = () => {
      let requestData = {};
      requestData['startTime'] = state.monthTimeDate1 + " 00:00:00";
      requestData['endTime'] = state.monthTimeDate1 + " 23:59:59";
      emit("querySystemVarOrCurveDataTop", requestData);
    };
    const querySystemVarOCurveDataBot = () => {
      let requestData = {};
      if (state.tabActive === 0) {
        requestData['timeInterval'] = '1d';
        requestData['formatInterval'] = '2';
        requestData["isCurrent"] = isToday(state.dayTimeDate[1]) ? 1 : 0;
        requestData['startTime'] = getNowDate(state.dayTimeDate[0]) + " 00:00:00";
        requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : state.dayTimeDate[1] + " 23:59:59";
      }

      if (state.tabActive === 1) {
        requestData['formatInterval'] = '3';
        requestData['timeInterval'] = '1n';
        requestData['startTime'] = getCurrentMonthFirstDay(state.monthTimeDate[0]) + " 00:00:00";
        let endTime = getCurrentMonthLastDay(state.monthTimeDate[1]);
        requestData["isCurrent"] = isMonth(endTime) ? 1 : 0;
        requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : endTime + " 23:59:59";
      }

      if (state.tabActive === 2) {
        requestData['formatInterval'] = '4';
        requestData['timeInterval'] = '1y';
        requestData['startTime'] = getCurrentYearFirstDay(state.yearTimeDate[0]) + " 00:00:00";
        let endTime = getCurrentYearLastDay(state.yearTimeDate[1]);
        requestData["isCurrent"] = isYear(endTime) ? 1 : 0;
        requestData['endTime'] = requestData["isCurrent"] ? getNowDateAll() : endTime + " 23:59:59";
      }
      emit("querySystemVarOCurveDataBot", requestData);
    };
    // 切换年月日
    const tabChangeHandler = (index) => {
      state.tabActive = index;
      querySystemVarOCurveDataBot()
    };
    return {
      ...toRefs(state),
      tabChangeHandler,
      transFormStatusToText,
      querySystemVarOCurveDataBot,
      querySystemVarOrCurveDataTop,
      searchDeviceList,
      titleObj,
      siteId,
      emptyImg
    };
  }
});
</script>
<style scoped lang="scss">
.flex_e_c {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.photovoltaic-overview {
  color: white;
  font-size: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 1460px;
  height: calc(100vh - 330px);

  .left {
    width: 50%;
    flex-shrink: 1;
    background: rgba(0, 23, 39, 0.5);
    border: 1px solid #1c4a87;
    margin-right: 16px;
    height: 100%;
    overflow-y: scroll;

    .block-box {
      padding: 24px 14px 0 24px;

      .block-box-title {
        font-size: 18px;
        margin-bottom: 20px;

        &::before {
          display: inline-block;
          margin-right: 22px;
          content: "";
          width: 0px;
          height: 10px;
          box-shadow: 0px 0px 6px 1px #fff;
          border: 2px solid #fff;
        }
      }

      .block-list {
        display: flex;
        flex-wrap: wrap;
        font-size: 14px;

        .block-list-content {
          width: 25%;
          height: 100px;
          padding-right: 10px;
          margin-bottom: 6px;
          box-sizing: border-box;

          .block {
            margin-bottom: 6px;

            .block-content {
              display: flex;
              align-items: center;
            }

            .block-title {
              font-size: 14px;
              color: #fff;
              line-height: 20px;
              text-align: center;
            }

            .block-icon {
              width: 44px;
              height: 80px;
              display: flex;
              align-items: center;
              justify-content: center;
              flex-direction: column;
              font-size: 12px;

              i {
                display: block;
              }

              .nbq {
                width: 20px;
                height: 18px;
                margin-bottom: 7px;
              }

              .qxz {
                width: 23px;
                height: 26px;
                margin-bottom: 1px;
              }

              .hdc {
                width: 20px;
                height: 14px;
                margin-bottom: 9px;
              }

              .wdjc {
                width: 11px;
                height: 21px;
                margin-bottom: 7px;
              }

              .cdq {
                width: 20px;
                height: 18px;
                margin-bottom: 7px;
              }

              .dcc {
                width: 18px;
                height: 22px;
                margin-bottom: 4px;
              }

              .pcs {
                width: 19px;
                height: 20px;
                margin-bottom: 6px;
              }
            }

            .block-right {
              padding: 0 8px;
              width: 165px;
              height: 60px;
              font-size: 14px;
              display: flex;
              flex-direction: column;
              justify-content: space-around;
              align-items: center;

              .block-text {
                width: 100%;
                display: flex;
                align-items: center;
                justify-content: space-between;
                line-height: 21px;
                font-size: 12px;

                span {
                  font-size: 18px;
                  color: #00ccff;
                  font-family: AgencyFB, fantasy, sans-serif;
                }

                .flex_e_c {
                  p {
                    margin-left: 4px;
                    width: 35px;
                    font-size: 14px;
                  }
                }
              }
            }

            &.green {
              background: url("@/assets/image/station-details/item-bg-green.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #34e800;

                .nbq {
                  background: url("@/assets/image/station-details/inverter-icon1.png") top left no-repeat;
                }

                .qxz {
                  background: url("@/assets/image/station-details/run-icon1.png") top left no-repeat;
                }

                .hdc {
                  background: url("@/assets/image/station-details/battery-icon1.png") top left no-repeat;
                }

                .wdjc {
                  background: url("@/assets/image/station-details/temperature-icon1.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon1.png") top left no-repeat;
                }

                .dcc {
                  background: url("@/assets/image/station-details/power-icon1.png") top left no-repeat;
                }

                .pcs {
                  background: url("@/assets/image/station-details/pcs-icon1.png") top left no-repeat;
                }
              }
            }

            &.blue {
              background: url("@/assets/image/station-details/item-bg-blue.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #61b2ff;

                .nbq {
                  background: url("@/assets/image/station-details/inverter-icon2.png") top left no-repeat;
                }

                .hdc {
                  background: url("@/assets/image/station-details/battery-icon2.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon6.png") top left no-repeat;
                }

                .dcc {
                  background: url("@/assets/image/station-details/power-icon2.png") top left no-repeat;
                }

                .pcs {
                  background: url("@/assets/image/station-details/pcs-icon2.png") top left no-repeat;
                }
              }
            }

            &.red {
              background: url("@/assets/image/station-details/item-bg-red.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #ff0000;

                .nbq {
                  background: url("@/assets/image/station-details/inverter-icon3.png") top left no-repeat;
                }

                .hdc {
                  background: url("@/assets/image/station-details/battery-icon3.png") top left no-repeat;
                }

                .wdjc {
                  background: url("@/assets/image/station-details/temperature-icon3.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon3.png") top left no-repeat;
                }

                .dcc {
                  background: url("@/assets/image/station-details/power-icon3.png") top left no-repeat;
                }

                .pcs {
                  background: url("@/assets/image/station-details/pcs-icon3.png") top left no-repeat;
                }
              }
            }

            &.cyan {
              background: url("@/assets/image/station-details/item-bg-cyan.png") top left no-repeat;
            }

            &.yellow {
              background: url("@/assets/image/station-details/item-bg-yellow.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #ffbb00;

                .wdjc {
                  background: url("@/assets/image/station-details/temperature-icon2.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon7.png") top left no-repeat;
                }
              }
            }

            &.skyblue {
              background: url("@/assets/image/station-details/item-bg-skyblue.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #9fdeff;

                .hdc {
                  background: url("@/assets/image/station-details/battery-icon4.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon2.png") top left no-repeat;
                }
              }
            }

            &.purple {
              background: url("@/assets/image/station-details/item-bg-purple.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #fa64ff;

                .nbq {
                  background: url("@/assets/image/station-details/inverter-icon4.png") top left no-repeat;
                }

                .dcc {
                  background: url("@/assets/image/station-details/power-icon4.png") top left no-repeat;
                }

                .pcs {
                  background: url("@/assets/image/station-details/pcs-icon4.png") top left no-repeat;
                }
              }
            }

            &.gray {
              background: url("@/assets/image/station-details/item-bg-gray.png") no-repeat;
              background-size: 100% 100%;

              .block-icon {
                color: #aaaaaa;

                .hdc {
                  background: url("@/assets/image/station-details/battery-icon5.png") top left no-repeat;
                }

                .wdjc {
                  background: url("@/assets/image/station-details/temperature-icon4.png") top left no-repeat;
                }

                .cdq {
                  background: url("@/assets/image/station-details/charginggun-icon4.png") top left no-repeat;
                }

                .dcc {
                  background: url("@/assets/image/station-details/power-icon5.png") top left no-repeat;
                }
              }
            }
          }
        }
      }
    }
  }

  .right {
    width: 50%;
    flex-shrink: 1;
    height: 100%;
    display: flex;
    flex-direction: column;

    .right-top {
      display: flex;
      flex-wrap: no-wrap;
      height: 70px;
      margin-bottom: 20px;

      .info-block {
        margin-right: 17px;
        width: 25%;
        height: 70px;
        background: url("@/assets/image/station-details/right-box-bg.png") no-repeat;
        background-size: 100% 100%;
        text-align: center;

        &:last-of-type {
          margin-right: 0;
        }

        .info-value {
          font-size: 14px;
          color: #00ccff;
          height: 38px;
          line-height: 42px;
          font-family: Agency FB, Agency FB;

          span {
            margin-right: 4px;
            color: #fff;
            font-size: 24px;
          }
        }

        .info-text {
          height: 25px;
          line-height: 25px;
          font-size: 14px;
          color: #fff;
        }
      }
    }

    .right-chart-bg {
      position: relative;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: url("@/assets/image/station-details/right-echart-bg.png") no-repeat;
      background-size: 100% 100%;

      .right-chart-top {
        width: 100%;
        flex: 40;
        padding: 0 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        box-sizing: border-box;

        .right-chart-title {
          font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
          font-weight: 400;
          font-size: 18px;
          color: #ffffff;
          text-align: left;
          display: flex;
          align-items: center;

          span {
            margin-left: 5px;
          }
        }
      }
    }
  }

  .right-chart-content {
    flex: 300;
    width: 100%;
    height: auto;
    position: relative;

    .right-chart-title-right-date {
      position: absolute;
      z-index: 2;
      top: -10px;
      right: 3%;
    }
  }
}
</style>