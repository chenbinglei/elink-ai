<template>
  <div class="station-top-info">
    <div class="station-info">
      <div class="station-left-icon"></div>
      <div class="station-name-change flex-ai-center">
        <div>{{ stationName }}</div>
        <el-dropdown @command="changeSiteStation" class="custom-trigger">
          <div class="ml-5 pt-2" style="width: 50px; height: 50px">
            <img src="@/assets/image/station-details/change-station.png" width="50" alt="" />
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown-menu">
              <el-input style="padding:10px" v-model="searchText" placeholder="请输入电站名称" clearable @keyup.enter="querySiteListByUserId"></el-input>
              <el-dropdown-item v-for="(item, index) in stationList" :key="index + 'stationList'" :command="index">{{
                item.siteName }}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="station-day-info">
        <div class="station-day-info-bg"></div>
        电站已安全运行
        <div class="station-day-num-bg" v-for="(value, index) in safe_day" :key="index">
          {{ value }}
        </div>
        天
      </div>
      <div class="station-weather-info-list">
        <StationWeatherInfo v-for="(value, index) in day_list" :key="index" :index="index" :info="value">
        </StationWeatherInfo>
      </div>
    </div>
    <div class="station-top-tab-list">
      <div class="station-top-tab" v-for="(value, index) in tab_list" :key="index"
        :class="tab_active === value.id ? 'active' : ''" @click="clickStationChangeFun(value.id)">
        {{ value.name + (value.id === 0 ? "总览" : "概览") }}
      </div>
    </div>
  </div>
</template>
<script>
import {
  reactive,
  defineComponent,
  toRefs,
  getCurrentInstance,
  watch,
  onMounted,
} from "vue";
import { findSiteListByUserId, getWeatherDayListBySiteId } from "@/api/centralMonitoring/centralMonitoring";

import { StationWeatherInfo } from "@/views/stationDetails/components";
import { Switch } from "@element-plus/icons-vue";
import { useRoute } from "vue-router";
import { TpjdTypeList } from "@/common/enum";
import { useRouter } from "vue-router";
import { computed } from "vue";
export default defineComponent({
  name: "StationTopInfo",
  components: { Switch, StationWeatherInfo },
  props: {
    stationInfo: {
      type: Object,
      default: () => {
        return {};
      },
    },
    siteStateArray: {
      type: Array,
      default: () => [],
    },
    isActiveCard: {
      type: Boolean,
      default: false,
    },
    comeFromName: {
      type: String,
      default: "default",
    },
  },
  emits: ["tabChangeEvent"],
  setup (props) {
    const vueRouter = useRouter();
    const { emit } = getCurrentInstance();
    const route = useRoute();
    // 基础数据
    const that = reactive({
      station_info: {},
      stationList: [],
      scenarioTypes: "",
      tab_active: 0,
      safe_day: "00",
      stationName: "",
      searchText: "",

      day_list: [
        {
          icon: "weather-icon1",
          day: "今天",
          temp: "24~34°C",
        },
        {
          icon: "weather-icon2",
          day: "明天",
          temp: "22~33°C",
        },
        {
          icon: "weather-icon3",
          day: "后天",
          temp: "24~30°C",
        },
      ],
    });
    // 场站类型
    const tab_list = computed(() => {
      let list = TpjdTypeList.filter((item) =>
        that.scenarioTypes.includes(item.id)
      );

      return [{ id: 0, name: "电站" }, ...list,
      //  { id: 9, name: "停车场" }
      ];
    });
    // 查询场站列表
    const querySiteListByUserId = () => {
      console.log("querySiteListByUserId",'searchText');
     
      findSiteListByUserId({siteName: that.searchText,})
        .then((res) => {
          that.stationList = res.data ? res.data : [];
          let item = res.data.find((item) => item.id == route.query.siteId);
          if (item) {
            that.stationName = item.siteName;
            that.scenarioTypes = item.scenarioTypes;
            that.safe_day = item.runDays.toString();
          }
        })
        .catch((e) => {
          console.log(e);
        });
    };

    const queryWeatherDayListBySiteId = (siteId) => {

      getWeatherDayListBySiteId({ siteId })
        .then((res) => {

          const day_list = res.data ? res.data : [];
      

          // 获取今天
          const today = new Date();
          today.setHours(0, 0, 0, 0);

          // 处理数据：添加 day 字段
          that.day_list = day_list.map(item => {
            const fxDate = new Date(item.fxDate);
            fxDate.setHours(0, 0, 0, 0);

            const diffDays = Math.floor((fxDate - today) / (24 * 60 * 60 * 1000));

            let dayLabel = '';
            if (diffDays === 0) dayLabel = '今天';
            else if (diffDays === 1) dayLabel = '明天';
            else if (diffDays === 2) dayLabel = '后天';

            return { ...item, day: dayLabel };
          });
          // console.log(that.day_list);
        })
        .catch((e) => {
          console.log(e);
        });
    };

    if (route.query.siteId) {
      that.siteId = route.query.siteId;
      querySiteListByUserId();
      // 查询天气
      queryWeatherDayListBySiteId(route.query.siteId);

    }
    if (route.query.type) that.tab_active = route.query.type;
    // 切换场站
    const changeSiteStation = (index) => {
      let item = that.stationList[index];

      // that.stationName = item.siteName;
      // that.tab_active = 0
      // that.scenarioTypes = item.scenarioTypes;
      // that.safe_day = item.runDays.toString();
      // emit("tabChangeEvent", { type: 0, siteId: item.id });
      vueRouter.push({
        path: "/stationDetails/stationDetails",
        query: {
          siteName: item.siteName,
          scenarioTypes: item.scenarioTypes,
          siteId: item.id,
        },
      });
      // 查询天气
      // queryWeatherDayListBySiteId(item.id);

    };
    // 切换场站类型
    const clickStationChangeFun = (type) => {
      if (that.tab_active === type) {
        return;
      }
      that.tab_active = type;

      emit("tabChangeEvent", { type });
    };
    return {
      ...toRefs(that),
      tab_list,
      clickStationChangeFun,
      querySiteListByUserId,
      changeSiteStation,
      queryWeatherDayListBySiteId,
    };
  },
});
</script>
<style>
.custom-trigger {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}

.custom-dropdown-menu {
  max-height: 300px;
  /* 设置你想要的高度 */
  overflow-y: auto;
  /* 启用垂直滚动 */
}

.custom-trigger:focus {
  outline: none;
}

.el-dropdown,
.el-dropdown * {
  outline: none;
}

/* 强制移除 el-dropdown 悬停和聚焦时的边框 */
.el-dropdown__inner:hover,
.el-dropdown__inner:focus {
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
}
</style>
<style lang="scss" scoped>
.station-top-info {
  z-index: 1000;

  .station-info {
    height: 90px;
    background: url("@/assets/image/station-details/top_info_bg.png") no-repeat;
    background-size: 100% 100%;
    margin-left: 24px;
    margin-right: 20px;
    position: relative;

    .station-left-icon {
      margin-left: 16px;
      width: 88px;
      height: 88px;
      margin-top: 1px;
      background: url("@/assets/image/station-details/top_left_icon.png") no-repeat;
      background-size: 100% 100%;
    }

    .station-name-change {
      position: absolute;
      top: 0;
      left: 140px;
      width: 450px;
      height: 90px;
      font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
      font-weight: 400;
      font-size: 18px;
      line-height: 90px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
      display: flex;
      align-items: center;

      .station-name-change-btn {
        //position: absolute;
        width: 32px;
        height: 32px;
        //background: url("@/assets/image/station-details/station-name-change-btn.png") top left no-repeat;
        //background-size: 100% 100%;
        top: 29px;
        right: 29px;
        margin-left: 20px;
        background: rgba(0, 45, 57, 0.7);
        box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16),
          inset 0px 0px 10px 1px #03a5ff;
        border-radius: 4px 4px 4px 4px;
        border: 1px solid #00ccff;

        //&.resetBtn {
        //  background: rgba(0, 45, 57, 0.7);
        //  box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16),
        //  inset 0px 0px 10px 1px #03a5ff;
        //  border-radius: 4px 4px 4px 4px;
        //  border: 1px solid #00ccff;
        //}
      }
    }

    .station-weather-info-list {
      position: absolute;
      right: 2px;
      top: 0;
      width: 600px;
      height: 90px;
      align-items: center;
      display: flex;
      justify-content: flex-end;
      flex: 2;
    }

    .station-day-info {
      width: 416px;
      height: 90px;
      position: absolute;
      top: 0;
      left: 40%;
      transform: translateX(-50%);
      display: flex;
      justify-content: center;
      align-items: center;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      line-height: 14px;
      text-align: center;
      font-style: normal;

      .station-day-num-bg {
        width: 32px;
        height: 32px;
        background: url("@/assets/image/station-details/top_day_num_bg.png") bottom center no-repeat;
        background-size: 100% 100%;
        font-family: Agency FB, Agency FB;
        font-weight: bold;
        font-size: 18px;
        line-height: 32px;
        color: #34e800;
        text-shadow: 0px 0px 4px rgba(52, 232, 0, 0.7);
        text-align: center;
        font-style: normal;
        margin: 0 6px;
      }

      .station-day-info-bg {
        width: 416px;
        height: 37px;
        position: absolute;
        bottom: 13px;
        //background: url("@/assets/image/station-details/top_day_bg.png") bottom center no-repeat;
        //background-size: 100% 100%;
      }
    }
  }

  .station-top-tab-list {
    display: flex;
    flex-direction: row;
    //margin-left: 20px;
    margin: 20px 24px;

    .station-top-tab {
      align-items: center;
      width: 148px;
      height: 32px;
      background: url("@/assets/image/station-details/tab_normal.png");
      background-size: 100% 100%;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 16px;
      line-height: 32px;
      color: #7dcbff;
      text-align: center;
      font-style: normal;
      text-transform: none;
      margin-right: -20px;
      cursor: pointer;

      &.active,
      &:hover {
        background: url("@/assets/image/station-details/tab_over.png");
        background-size: 100% 100%;
        font-weight: bold;
        font-size: 16px;
        color: #ffffff;
        text-stroke: 1px rgba(0, 0, 0, 0);
        text-align: center;
        font-style: normal;
        text-transform: none;
        -webkit-text-stroke: 1px rgba(0, 0, 0, 0);
      }
    }
  }
}
</style>