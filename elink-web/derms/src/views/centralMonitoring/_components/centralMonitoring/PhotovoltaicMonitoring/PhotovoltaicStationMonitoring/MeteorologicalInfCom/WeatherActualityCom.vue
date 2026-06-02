<template>
  <div class="content_body">
    <div class="content_body_info">
      <el-image class="weather_icon" :src="return_data_info.weatherObj?.iconImg">
        <template #error>
          <div class="image-slot flex-jc-ai-center">
            <el-icon size="24" color="#ffffff80">
              <Picture />
            </el-icon>
          </div>
        </template>
      </el-image>
      <div class="weather_info">
        <div class="info_right_top flex-ai-center">
          <div class="flex">
            <div class="number">{{ $filters.roundedNumber(return_data_info.temp) }}</div>
            <div class="number_unit">℃</div>
          </div>
          <div class="weather_text flex-all">{{ $filters.moreData(return_data_info.weatherObj?.description) }}</div>
        </div>
        <div class="info_right_bottom">
          <div class="info_right_weiZhi">
            <span class="iconfont icon-dingwei"></span>
            <span class="city_name">
              <span>{{ $filters.moreData(return_data_info.province) }}</span>
              <span v-if="return_data_info.city">{{ $filters.moreData(return_data_info.city) }}</span>
              <span v-if="return_data_info.county">{{ $filters.moreData(return_data_info.county) }}</span>
            </span>
          </div>
          <div class="info_right_time" v-if="return_data_info.dt">{{ $filters.moreData(return_data_info.dt) }}更新</div>
        </div>
      </div>
    </div>
    <div class="content_body_list flex-all">
      <div v-for="(item, index) in list" :key="index" class="content_body_list_li">
        <div class="list_li_left">
          <el-image :src="item.iconName" class="list_li_icon"></el-image>
          <div class="list_li_name">{{ item.name }}</div>
        </div>
        <div class="list_li_right">{{ $filters.moreData(return_data_info[item.fieldName]) }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { Picture } from '@element-plus/icons-vue';
import { reactive, defineComponent, toRefs, watch } from "vue";
import qx_xfzd_icon from "@/assets/image/qx_xfzd_icon.png";
import qx_qiya_icon from "@/assets/image/qx_qiya_icon.png";
import qx_spfzd_icon from "@/assets/image/qx_spfzd_icon.png";
import qx_shidu_icon from "@/assets/image/qx_shidu_icon.png";
import qx_fengsu_icon from "@/assets/image/qx_fengsu_icon.png";
import qx_fengxiang_icon from "@/assets/image/qx_fengxiang_icon.png";

export default defineComponent({
  name: "WeatherActualityCom",
  components: { Picture },
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      return_data_info: {},
      list: [
        { name: "湿度（%)", fieldName: "humidity", iconName: qx_shidu_icon },
        { name: "气压（hPa)", fieldName: "pressure", iconName: qx_qiya_icon },
        { name: "风速（m/s)", fieldName: "wind_speed", iconName: qx_fengsu_icon },
        { name: "风向", fieldName: "windDirection", iconName: qx_fengxiang_icon },
        { name: "水平辐照度（W/㎡)", fieldName: "", iconName: qx_spfzd_icon },
        { name: "斜辐照度 (W/㎡)", fieldName: "", iconName: qx_xfzd_icon },
      ]
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      let return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      if (return_data_info.weather && return_data_info.weather.length) {
        return_data_info.weatherObj = return_data_info.weather[0] ?? {};
        try {
          return_data_info.weatherObj.iconImg = `/img/weather/${return_data_info.weatherObj?.icon}@2x.png`;
        } catch (e) {
          // Handle missing image error
          console.error("Image not found:", e);
        }
      }
      that.return_data_info = JSON.parse(JSON.stringify(return_data_info));
    }, { deep: true, immediate: true });

    return { ...toRefs(that), watchReturnDataInfo };
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  height: 100%;
  display: flex;
  flex-direction: column;

  .content_body_info {
    display: flex;
    align-items: center;
    margin-bottom: 32px;

    .weather_icon {
      width: 98px;
      height: 98px;
      display: flex;
      align-items: center;
      justify-content: center;

      .image-slot {
        width: 68px;
        height: 68px;
        border-radius: 4px;
        background: #a7bcce26;
      }
    }

    .weather_info {
      flex: 1;
      padding-right: 12px;
      box-sizing: border-box;

      .info_right_top {
        font-size: 16px;
        color: #ffffffcc;
        font-weight: 700;

        .number {
          color: #ffffff;
          font-size: 32px;
        }

        .number_unit {
          color: #ffffff99;
          margin-left: 6px;
          margin-right: 12px;
        }
      }

      .info_right_bottom {
        font-size: 12px;
        color: #ffffff99;

        .info_right_weiZhi {
          display: flex;
          align-items: center;
          margin-bottom: 4px;
        }
      }
    }
  }

  .content_body_list {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 0 16px;
    box-sizing: border-box;

    .content_body_list_li {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .list_li_left {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #ffffffcc;

        .list_li_icon {
          width: 20px;
          margin-right: 16px;
        }
      }

      .list_li_right {
        color: #ffffff;
        font-size: 14px;
      }
    }
  }
}
</style>