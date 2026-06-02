<template>
  <div class="stationInfoCard" :class="{ active_class: isActiveCard }" @click="clickStationCardFun">
    <div class="content_left">
      <div class="content_top textTwo">{{ station_info.siteName }}</div>
      <template v-if="comeFromName === 'default'">
        <div class="content_bottom" :class="{marginNoBottom: !station_info.scenarioTypes || station_info.scenarioTypes === 'null' }">
          <el-tooltip v-if="station_info.scenarioTypes.indexOf('1') !== -1" content="光伏" effect="dark" placement="top-start">
            <span class="iconfont icon-guangfu"></span>
          </el-tooltip>
          <el-tooltip v-if="station_info.scenarioTypes.indexOf('2') !== -1" content="储能" effect="dark" placement="top-start">
            <span class="iconfont icon-chuneng"></span>
          </el-tooltip>
          <el-tooltip v-if="station_info.scenarioTypes.indexOf('3') !== -1" content="电桩" effect="dark" placement="top-start">
            <span class="iconfont icon-dianzhuang"></span>
          </el-tooltip>
          <el-tooltip v-if="station_info.scenarioTypes.indexOf('4') !== -1" content="用能" effect="dark" placement="top-start">
            <span class="iconfont icon-dianwang"></span>
          </el-tooltip>
          <el-tooltip v-if="station_info.scenarioTypes.indexOf('5') !== -1" content="变配电" effect="dark" placement="top-start">
            <span class="iconfont icon-bianyaqi"></span>
          </el-tooltip>
        </div>
      </template>
    </div>
    <div class="content_right" v-if="comeFromName === 'default'">
      <template v-for="(item,index) in siteStateArray" :key="index">
        <template v-if="station_info.siteStates.indexOf(item.id) !== -1">
          <div :style="{ backgroundColor: item.color }" class="round_dot"></div>
        </template>
      </template>
    </div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, getCurrentInstance, watch} from "vue";

export default defineComponent({
  name: "StationInfoCard",
  props: {
    stationInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    siteStateArray: {
      type: Array,
      default: () => []
    },
    isActiveCard:{
      type: Boolean,
      default: false
    },
    comeFromName:{
      type: String,
      default: "default"
    }
  },
  emits: ["changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      station_info: {},
    });

    const clickStationCardFun = () => {
      emit("changeEvent", that.station_info);
    };

    const watchStationInfo = watch(() => props.stationInfo, (newStationInfo) => {
      // if (newStationInfo.siteStates) newStationInfo.siteStates = newStationInfo.siteStates.split(",");
      if(!newStationInfo.scenarioTypes) newStationInfo.scenarioTypes = "";
      that.station_info = JSON.parse(JSON.stringify(newStationInfo));
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchStationInfo, clickStationCardFun};
  }
});

</script>

<style lang="scss" scoped>
.stationInfoCard {
  background: #071527;
  padding: 14px 16px;
  box-sizing: border-box;
  margin-bottom: 14px;
  border-radius: 6px;
  display: flex;
  cursor: pointer;

  .content_left {
    flex: 1;

    .content_top {
      color: #FFFFFF;
      font-size: 16px;
      font-weight: 500;
    }

    .content_bottom {
      margin-top: 16px;

      .iconfont {
        font-size: 18px;
        margin-right: 12px;
        background: linear-gradient(180deg, #00f7ff 0%, #00f7ff66 100%);
        -webkit-text-fill-color: transparent;
        -webkit-background-clip: text;

        &:last-child {
          margin-right: 0;
        }
      }
    }

    .marginNoBottom{
      margin-top: 2px;
    }
  }

  .content_right {
    padding-left: 2px;
    box-sizing: border-box;

    .round_dot {
      width: 8px;
      height: 8px;
      border-radius: 1px;
      margin-bottom: 5px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.active_class{
  background: linear-gradient(90deg, #2b69e3a3 0%, #0d3178a3 83%);
}
</style>