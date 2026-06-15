<template>
  <div class="station-container" v-loading="loading">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input class="selectAndInput" v-model="word" placeholder="请输入关键字查询">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
      <span class="title">所属区域：</span>
      <el-select v-model="areaType" placeholder="请选择" @change="area = ''" class="selectRegion" style="width: 100px">
        <el-option v-for="(item, index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-select v-model="area" filterable clearable placeholder="请选择" class="selectRegion">
        <el-option v-for="(item, index) in (areaType === 1 ? provinceArray : cityArray)" :key="index" :label="item.name" :value="item.name"></el-option>
      </el-select>
      <el-button class="button queryBtn" @click="queryPage">
        <el-icon>
          <Search />
        </el-icon> 查询
      </el-button>
      <el-button class="button resetBtn" @click="reset">
        <el-icon>
          <RefreshRight />
        </el-icon> 重置
      </el-button>
    </div>

    <!-- 图表 -->
    <div class="charts" ref="chartListRef">
      <div class="chart" v-for="(item, chartIndex) in handleChartArray" :key="item.siteId" style="position: relative">
        <!-- 站点名称 -->
        <div style="display: flex; align-items: center;width: 52%;margin: 0 auto;">
          <el-tooltip effect="dark" placement="bottom" :content="item.siteName">
            <div class="chartTitle">{{ item.siteName }}</div>
          </el-tooltip>
        </div>

        <!-- 状态 -->
        <div v-if="item.siteStatus == 1" class="normal">正常投运</div>
        <div v-else class="overhaul">检修中</div>
        <img src="../../../assets/image/station-details/sta-detail.png" class="searchBtn" @click="goStationDetailsHandler(item)" />

        <!-- tab 切换 -->
        <div style="display: flex; margin-left: 20px">
          <div class="chart-type" v-for="(dataType, typeIndex) in item.devices" :key="dataType.functionLogos"
            @click="selectChartType(chartIndex, dataType.functionLogos)" :class="{ active: selectedType[chartIndex] === dataType.functionLogos }">
            {{ dataType.functionLogos }}
          </div>
        </div>

        <!-- 图表 -->
        <div style="margin-top: 20px" v-if="selectedType[chartIndex] && item && item.devices">
          <div style="min-height:50%">
            <ECharts :data="(() => {
              const device = item.devices?.find(
                (d) => d.functionLogos === selectedType[chartIndex]
              );
              return device ? device.historys : [];
            })()
              " :name="selectedType[chartIndex]" width="100%" height="160" />
          </div>
          <div class="warning-wrap">
            <StatusModule :normal-count="getDeviceStatus('normal', chartIndex)" :unregistered-count="getDeviceStatus('unregistered', chartIndex)"
              :error-count="getDeviceStatus('error', chartIndex)" :offline-count="getDeviceStatus('offline', chartIndex)" />
          </div>
        </div>
        <div v-else>
          <el-empty :image="emptyImg" description="暂无数据" />
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <Pagination v-model:currentPage="page" v-model:pageSize="size" :layout="layout" :totalNumber="totalNumber" @pageChange="queryPage" />
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  onMounted,
  ref,
  reactive,
  toRefs,
  nextTick,
} from "vue";
import pinyin from "tiny-pinyin";
import { useRouter } from "vue-router";
import { Search, RefreshRight } from "@element-plus/icons-vue";
import ECharts from "@/components/echarts/echarts.vue";
import StatusModule from "@/components/echarts/StatusModule.vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { area_type_array } from "@/utils/setVariate";
import {
  queryStation,
  queryStatusTotal,
} from "@/api/monitoringCenter/monitoringCenter.js";
import emptyImg from "@/assets/image/empty.png";
export default defineComponent({
  name: "Station",
  components: { ECharts, Search, RefreshRight, StatusModule },
  emits: ["updateData", "isVisible"],
  setup (props, { emit }) {
    const vueRouter = useRouter();
    const options = ref([{ value: 0, label: "全部" }]);
    const selectedType = ref([]);
    const chartListRef = ref(null);
    const that = reactive({
      page: 1,
      size: 20,
      totalNumber: 0,
      area: "",
      word: "",
      loading: true,
      areaType: "",
      cityArray: [],
      provinceArray: [],
      areaTypeArray: area_type_array,
    });

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: "3", timer: new Date() }).then(
        (res) => {
          let list = res.data ? res.data : [];
          let provinceArray = [],
            cityArray = [];
          for (let i = 0; i < list.length; i++) {
            // 读写类型字段
            if (list[i].siteReadwriteObject)
              list[i].siteReadwriteObject = JSON.parse(
                list[i].siteReadwriteObject
              );
            let location_info = list[i]?.siteReadwriteObject?.location ?? {};

            //省份处理
            if (location_info.province) {
              let province_id = pinyin.convertToPinyin(location_info.province);

              let findProvince = provinceArray.find(
                (item) => item.id === province_id
              );
              if (!findProvince)
                provinceArray.push({
                  name: location_info.province,
                  id: province_id,
                });

              // 市区处理
              let findCity = cityArray.find(
                (item) => item.name === location_info.city
              );
              if (!findCity)
                cityArray.push({
                  name: location_info.city,
                  parentId: province_id,
                });
            }
          }
          that.siteIdArray = JSON.parse(JSON.stringify(list));
          that.cityArray = JSON.parse(JSON.stringify(cityArray));
          that.provinceArray = JSON.parse(JSON.stringify(provinceArray));
        }
      );
    };
    const handleChartArray = ref();
    //查询站点数据
    // const queryPage = async () => {
    //   that.loading = true;
    //   try {
    //     const { page, size, area, word } = that;
    //     let res = await queryStation({ page, size, area, word });
    //     handleChartArray.value = res.data.items;
    //     console.log(handleChartArray.value, 'handleChartArray.value');
    //     that.totalNumber = res.data.totalSize;
    //     that.loading = false;
    //     selectedType.value = handleChartArray.value.map((item) => {
    //       const firstDevice = item.devices?.[0];
    //       return firstDevice?.functionLogos || "";
    //     });
    //     emit("isVisible", res.data.totalSize ? true : false, 0);
    //   } catch (error) {
    //     console.error("查询站点数据失败:", error);
    //     that.loading = false;
    //     ElMessage.error("查询站点数据失败，请重试！");
    //   }
    // };


    const queryPage = async () => {
      that.loading = true;
      try {
        const { page, size, area, word } = that;
        let res = await queryStation({ page, size, area, word });
        handleChartArray.value = res.data.items;
        that.totalNumber = res.data.totalSize;

        // 等待 DOM 更新完成
        await nextTick();
        // 滚动到顶部
        if (chartListRef.value) {
          chartListRef.value.scrollTop = 0;
        }

        selectedType.value = handleChartArray.value.map((item) => {
          const firstDevice = item.devices?.[0];
          return firstDevice?.functionLogos || "";
        });
        emit("isVisible", res.data.totalSize ? true : false, 0);
      } catch (error) {
        console.error("查询站点数据失败:", error);
        ElMessage.error("查询站点数据失败，请重试！");
      } finally {
        that.loading = false;
      }
    };
    //查询统计数据
    const searchTotal = async () => {
      let res = await queryStatusTotal({});
      emit("updateData", res.data);
    };
    const selectChartType = (chartIndex, logo) => {
      selectedType.value[chartIndex] = logo;
    };
    // 获取设备状态
    const getDeviceStatus = (key, chartIndex) => {
      const item = handleChartArray.value[chartIndex];
      if (!item || !item.devices) return 0;

      const device = item.devices.find(
        (d) => d.functionLogos === selectedType.value[chartIndex]
      );
      return device ? device[key] : 0;
    };
    //重置
    const reset = () => {
      that.page = 1;
      that.size = 20;
      that.area = "";
      that.word = "";
      queryPage();
    };
    const goStationDetailsHandler = (item) => {
      vueRouter.push({
        path: "/stationDetails/stationDetails",
        query: {
          siteName: item.siteName,
          scenarioTypes: item.scenarioTypes,
          siteId: item.siteId,
        },
      });
    };
    onMounted(async () => {

      await queryPage();
      await searchTotal();

      querySiteBasicInfoByTenantId();
    });

    return {
      ...toRefs(that),
      options,
      handleChartArray,
      selectedType,
      chartListRef,
      goStationDetailsHandler,
      queryPage,
      searchTotal,
      reset,
      querySiteBasicInfoByTenantId,
      emptyImg,
      getDeviceStatus,
      selectChartType
    };
  },
});
</script>
<style lang="scss" scoped>
.station-container {
  height: calc(100vh - 190px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.warning-wrap {
  background: url("@/assets/image/station-details/warning-bac.png") no-repeat;
  background-size: 100% 100%;
  width: 90%;
  margin: 5px auto 0;
}

.search-bar {
  flex-shrink: 0;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.selectAndInput {
  width: 240px;
  height: 32px;
  box-shadow: inset 0px 0px 8px 1px #03baff;
  border: 1px solid #0071a4;
}

.title {
  margin-left: 32px;
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 14px;
  color: #ffffff;
  line-height: 14px;
  text-align: right;
  font-style: normal;
  text-transform: none;
}

.selectAndInput ::v-deep .el-input__inner::-webkit-input-placeholder {
  padding-left: 5px;
}

.selectRegion {
  margin-left: 10px;
  width: 240px;
  height: 32px;
  box-shadow: inset 0px 0px 8px 1px #03baff;
  border: 1px solid #0071a4;
}

.button {
  margin-left: 10px;
  width: 80px;
  height: 28px;
  border-radius: 0px 0px 0px 0px;
  display: flex;
  align-items: center;
  /* 垂直居中 */
  justify-content: center;

  /* 水平居中 */
  .el-icon {
    font-size: 16px;
    /* 控制图标大小 */
    width: 16px;
    height: 16px;
    margin-right: 10px;
  }
}

.queryBtn {
  background: rgba(0, 132, 167, 0.7);
  box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16),
    inset 0px 0px 10px 1px #03a5ff;
  border-radius: 4px 4px 4px 4px;
  border: 1px solid #00ccff;
}

.resetBtn {
  background: rgba(0, 45, 57, 0.7);
  box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16),
    inset 0px 0px 10px 1px #03a5ff;
  border-radius: 4px 4px 4px 4px;
  border: 1px solid #00ccff;
}

.charts {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  max-height: calc(100vh - 180px);
  overflow-y: auto;
  padding-right: 10px;
  width: 100%;
  height: 87%;

  .chart {
    flex: 0 0 auto;
    height: 353px;
    min-width: 24.3%;
    /* 最小宽度为 23% */
    background: url("../../../assets/images/组 118170.png") no-repeat;
    background-size: 100% 100%;
    box-sizing: border-box;

    .chartTitle {
      font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
      font-weight: 400;
      font-size: 16px;
      color: #ffffff;
      text-align: center;
      font-style: normal;
      text-transform: none;
      margin: 0 auto;
      /* 自动左右 margin 居中 */
      width: max-content;
      /* 根据内容自适应宽度 */
      line-height: 50px;

      max-width: 100%;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .normal {
      width: 55px;
      height: 24px;
      background: rgba(52, 232, 0, 0.3);
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #34e800;
      opacity: 0.8;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 12px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
      text-transform: none;

      display: flex;
      justify-content: center;
      align-items: center;
      position: absolute;
      top: 10px;
      right: 50px;
    }

    .overhaul {
      width: 55px;
      height: 24px;
      background: rgba(255, 136, 0, 0.3);
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #e99d45;
      opacity: 0.8;

      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 12px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
      text-transform: none;

      display: flex;
      justify-content: center;
      align-items: center;
      position: absolute;
      top: 8px;
      right: 60px;
    }
  }
}

.searchBtn {
  position: absolute;
  top: 4px;
  right: 10px;
  width: 44px;
  height: 44px;
  cursor: pointer;
}

//  图表样式
.chart-type {
  font-family: Microsoft YaHei, Microsoft YaHei;
  font-weight: 400;
  font-size: 14px;
  color: #7dcbff;
  text-align: left;
  font-style: normal;
  text-transform: none;
  cursor: pointer;
  width: 99px;
  height: 32px;
  display: flex;
  margin-top: 3%;
  justify-content: center;
  align-items: center;

  &.active {
    background: url("../../../assets/images/组 118143.png") no-repeat center
      center;
    background-size: cover;
  }
}

.pagination-wrapper {
  position: fixed;
  /* 固定定位 */
  bottom: 40px;
  /* 底部对齐 */
  left: 50%;
  /* 水平居中 */
  transform: translateX(-50%);
  /* 精确水平居中 */
  width: 100%;
  background-color: #001121;
  z-index: 1000;
  padding: 10px 24px;
  box-sizing: border-box;
}
</style>
