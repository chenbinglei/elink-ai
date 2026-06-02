<template>
  <!-- 搜索栏 -->
  <div class="search-bar">
    <el-input class="selectAndInput" v-model="word" placeholder="请输入关键字查询">
      <template #prefix>
        <el-icon class="el-input__icon">
          <search />
        </el-icon> </template></el-input>
    <span class="title">所属区域：</span>
    <el-select v-model="areaType" placeholder="请选择" @change="area = ''" class="selectRegion" style="width: 100px">
      <el-option v-for="(item, index) in areaTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>
    </el-select>
    <el-select v-model="area" filterable clearable placeholder="请选择" class="selectRegion">
      <template v-for="(item, index) in areaType === 1 ? provinceArray : cityArray" :key="index">
        <el-option :label="item.name" :value="item.name"></el-option>
      </template>
    </el-select>
    <el-button class="button queryBtn" @click="queryPage"><el-icon>
        <Search />
      </el-icon> 查询</el-button>
    <el-button class="button resetBtn" @click="reset"><el-icon>
        <RefreshRight />
      </el-icon>重置</el-button>
  </div>
  <div class="main-content" v-loading="loading" style="overflow-x: auto;" ref="tableContentRef" v-resize="setTableMaxHeight">
    <el-table :data="tableData" ref="tableRef" style="table-layout: fixed; width: 100%" stripe :max-height="tableMaxHeight">
      <el-table-column prop="systemName" label="系统名称" width="120" />
      <el-table-column label="场站名称" width="200">
        <template #default="scope">
          <span style="color: #00ccffff">{{ scope.row.siteName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="所属省市" min-width="150">
        <template #default="scope">
          {{ scope.row.location ? scope.row.location : "--" }}
        </template>
      </el-table-column>
      <el-table-column prop="storageType" label="储能类型" min-width="100">
        <template #default="scope">
          {{ scope.row.storageType ? scope.row.storageType : "--" }}
        </template>
      </el-table-column>
      <el-table-column prop="pvcapacity" label="装机容量" min-width="150">
        <template #default="scope">
          {{ scope.row.pcsPower === null ? '--' : `${scope.row.pcsPower}kW` }} /
          {{ scope.row.pcsRatedCap === null ? '--' : `${scope.row.pcsRatedCap}kWh` }}
        </template>

      </el-table-column>
      <el-table-column label="功率曲线" min-width="180">
        <template #default="scope">
          <div v-if="scope.row.devices&&scope.row.devices[0].historys.length">
            <canvas :id="'chartCanvas-' + scope.$index" class="mini-chart" width="160" height="50"></canvas>
          </div>
          <div v-else style="color: #ccc; font-size: 12px">暂无功率曲线</div>
        </template>
      </el-table-column>
      <el-table-column prop="pcsActivepower" label="实时功率" min-width="120">
        <template #default="scope">
          {{ scope.row.pcsActivepower ? scope.row.pcsActivepower : "--" }}
        </template>
      </el-table-column>
      <el-table-column label="SOC" prop="soc" :width="100">
        <template #default="scope">
          {{ scope.row.soc ? `${scope.row.soc}%` : "--" }}
        </template>
      </el-table-column>

      <el-table-column prop="loopTimes" label="今日循环次数" min-width="180" />
      <el-table-column prop="pvcapacity" label="今日充/放电量" min-width="180">
        <template #default="scope">
          {{ scope.row.totalBatteryCharge }}kWh /
          {{ scope.row.totalBatteryDischarge }}kWh
        </template></el-table-column>
      <el-table-column prop="tiedGrade" label="并网等级" min-width="120">
        <template #default="scope">
          {{ scope.row.tiedGrade ? scope.row.tiedGrade : "--" }}
        </template>
      </el-table-column>

      <el-table-column label="操作" min-width="150" fixed="right">
        <template #default="scope">
          <div style="
              display: flex;
              justify-content: center;
              align-items: center;
              gap: 10px;
              color: #00ccffff;
              cursor: pointer;
            ">
            <div size="small" type="primary" @click="lookPanel(scope.row)">
              查看
            </div>
            <div size="small" type="success" @click="togglePanel(true, scope.row)">
              展开
            </div>
          </div>
        
        </template>
      </el-table-column>
    </el-table>
    <!-- 右侧面板 -->
    <div v-if="showRightPanel" class="right-panel">
      <div class="panel-header">
        <div class="left-section">
          <div class="icon"></div>
          <div style="font-size: 15px">
            {{ selectedRowData?.siteName }}
          </div>
        </div>
        <div @click="togglePanel(false)">
          <el-icon style="font-size: 20px">
            <Close />
          </el-icon>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-area">
        <ECharts v-if="selectedRowData && selectedRowData.historys" :key="selectedRowData.siteId"
          :data="selectedRowData.historys" width="100%" :height="250" />
      </div>
      <!-- 状态模块 -->
      <div class="status-module-container">
        <StatusModule v-if="selectedRowData && selectedRowData.devices"
          :normal-count="selectedRowData.devices[0]?.normal ?? 0"
          :unregistered-count="selectedRowData.devices[0]?.unregistered ?? 0"
          :error-count="selectedRowData.devices[0]?.error ?? 0"
          :offline-count="selectedRowData.devices[0]?.offline ?? 0" />
      </div>

      <div class="notification-area" v-if="selectedRowData && selectedRowData.devices">
        <StatusNotification :systemId="selectedRowData.systemId" />
      </div>
    </div>
  </div>

  <!-- 分页 -->
  <div class="pagination-wrapper">
    <Pagination v-model:currentPage="page" v-model:pageSize="size" :layout="layout" :totalNumber="totalNumber"
      @pageChange="queryPage" />
  </div>
</template>
<script>
import {
  defineComponent,
  onMounted,
  ref,
  reactive,
  toRefs,
  nextTick,
} from "vue";
import pinyin from "tiny-pinyin";
import { Search, RefreshRight, Close } from "@element-plus/icons-vue";
import ECharts from "@/components/echarts/echarts.vue";
import StatusModule from "@/components/echarts/StatusModule.vue";
import StatusNotification from "@/components/echarts/StatusNotification.vue";
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";
import { area_type_array } from "@/utils/setVariate";
import {
  energyStoragePage,
  energyStorageDetail,
} from "@/api/monitoringCenter/monitoringCenter.js";
import { useRouter } from "vue-router";
export default defineComponent({
  name: "Station",
  components: {
    ECharts,
    Search,
    Close,
    RefreshRight,
    StatusModule,
    StatusNotification,
  },
  emits: ["updateData", "isVisible"],
  setup (props, { emit }) {
    const vueRouter = useRouter();
    const options = ref([{ value: 0, label: "全部" }]);
    const selectedType = ref([]);
    const tableRef = ref(null);
    const that = reactive({
      page: 1,
      size: 20,
      totalNumber: 10,
      area: "",
      word: "",
      areaType: "",
      loading: true,
      cityArray: [],
      provinceArray: [],
      areaTypeArray: area_type_array,
      tableMaxHeight: 300,
    });
    const tableData = ref([]);
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
    //重置
    const reset = () => {
      that.page = 1;
      that.size = 20;
      that.area = "";
      that.word = "";
      queryPage();
    };
    const showRightPanel = ref(false);
    const selectedRowData = ref(null);
    const togglePanel = (show, rowData = null) => {
      showRightPanel.value = show;
      if (rowData) {
        // 提取 devices 中的所有 historys 数据
        const mergedHistorys =
          rowData.devices?.flatMap((device) => device.historys || []) || [];

        selectedRowData.value = {
          ...rowData,
          historys: mergedHistorys,
        };
      }
    };
    const drawCharts = () => {
      setTimeout(() => {
        tableData.value.forEach((row, index) => {
          const canvas = document.getElementById(`chartCanvas-${index}`);
          if (!canvas || !row.devices || row.devices.length === 0) return;

          const ctx = canvas.getContext("2d");
          const w = canvas.width;
          const h = canvas.height;

          ctx.clearRect(0, 0, w, h);

          // 定义颜色数组
          const colors = ["#00ffcc", "#ffcc00", "#00ccff", "#ff6666"];

          // 收集所有设备的历史数据
          let allDataPoints = [];
          const deviceHistorys = [];

          row.devices.forEach((device) => {
            if (device.historys && device.historys.length > 0) {
              device.historys.forEach((series) => {
                allDataPoints.push(...series.data.map((d) => d.value));
                deviceHistorys.push({ name: series.name, data: series.data });
              });
            }
          });

          if (allDataPoints.length === 0) return;

          const maxValue = Math.max(...allDataPoints);
          const minValue = Math.min(...allDataPoints);
          const range = maxValue - minValue || 1;

          const scale = (val) => h - ((val - minValue) / range) * h;

          deviceHistorys.forEach((series, seriesIndex) => {
            const data = series.data || [];
            if (data.length < 2) return;

            ctx.beginPath();
            ctx.strokeStyle = colors[seriesIndex % colors.length];
            ctx.lineWidth = 2;

            data.forEach((item, i) => {
              const x = (i / (data.length - 1)) * w;
              const y = scale(item.value);
              if (i === 0) {
                ctx.moveTo(x, y);
              } else {
                ctx.lineTo(x, y);
              }
            });

            ctx.stroke();
          });
        });
      }, 100);
    };
    // 默认选中第一行并触发面板展开
    const initFirstRow = () => {
      if (tableData.value.length > 0) {
        const firstRow = tableData.value[0];
        const mergedHistorys =
          firstRow.devices?.flatMap((device) => device.historys || []) || [];

        selectedRowData.value = {
          ...firstRow,
          historys: mergedHistorys,
        };
      }
    };
    // const queryPage = async () => {
    //   that.loading = false;
    //   const { page, size, area, word } = that;
    //   const res = await energyStoragePage({
    //     page,
    //     size,
    //     area,
    //     word,
    //   });
    //   tableData.value = res.data.items;
    //   that.totalNumber = res.data.totalSize;
    //   that.loading = false;
    //   drawCharts(); // 数据更新后重绘
    // };
    const queryPage = async () => {
      try {
        that.loading = true;
        const { page, size, area, word } = that;
        const res = await energyStoragePage({
          page,
          size,
          area,
          word,
        });
        tableData.value = res.data.items;
        that.totalNumber = res.data.totalSize;
        // 滚动到顶部
        if (tableContentRef.value) {
          tableContentRef.value.scrollTop = 0;
        }
        if (tableRef.value) {
          tableRef.value.setScrollTop(0);
        }
        emit("isVisible", res.data.totalSize ? true : false, 2);
        that.loading = false;
        drawCharts(); // 数据更新后重绘
      } catch (error) {
        that.loading = false;
      }
    };
    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = () => {
      let tableContentHeight = tableContentRef.value.offsetHeight;
      that.tableMaxHeight = tableContentHeight;
    };
    onMounted(async () => {
      await queryPage();
      initFirstRow(); // 查询完成后初始化第一行数据
      // await searchTotal();
      querySiteBasicInfoByTenantId();
    });
    const lookPanel = (rowData) => {
      vueRouter.push({
        path: "/stationDetails/stationDetails",
        query: {
          siteName: rowData.siteName,
          scenarioTypes: rowData.scenarioTypes,
          siteId: rowData.siteId,
          type: 2,
        },
      });
    };
    return {
      ...toRefs(that),
      options,
      selectedType,
      queryPage,
      reset,
      lookPanel,
      tableData,
      showRightPanel,
      togglePanel,
      tableRef,
      selectedRowData,
      initFirstRow,
      querySiteBasicInfoByTenantId,
      setTableMaxHeight,
      tableContentRef,
    };
  },
});
</script>
<style lang="scss" scoped>
.search-bar {
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

.mini-chart {
  vertical-align: middle;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background-color: rgba(0, 0, 0, 0.1);
}

.main-content {
  display: flex;
  align-items: stretch; // 让子元素拉伸高度
  gap: 10px;
  width: 100%;
  height: calc(100vh - 300px); // 减去搜索栏和页脚高度
  margin-top: 10px;
  overflow-y: auto;
}

::v-deep .el-table__cell {
  text-align: center;
}

.right-panel {
  min-width: 526px;
  display: flex;
  flex-direction: column;
  background: url("@/assets/images/组 118277.png") no-repeat;
  background-size: 100% 100%;
  overflow: hidden;
  flex: 1;
}

// 图表区域
.chart-area {
  padding-top: 20px;
  height: 300px; // 固定高度
  flex-shrink: 0;
}

.status-module-container {
  padding: 30px 20px 0;
  height: 80px; // 固定高度
  flex-shrink: 0; // 禁止压缩
  overflow: visible;
  position: relative; // 确保绝对定位参照

  // 强制内部组件适应高度
  ::v-deep .status-module {
    height: 100% !important;
    transform: scale(0.9);
    transform-origin: left top;
  }
}

// 通知区域
.notification-area {
  flex: 1; // 通知占1份高度
  margin-top: 15px; // 适当间距
  overflow-y: auto;
  padding-bottom: 20px; // 底部留白
}

.panel-header {
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  width: 100%;
  color: white;
  padding: 10px 20px 10px 10px;
  background: url("@/assets/image/bacbar.png") no-repeat;
  background-size: 100% 100%;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 10px; // 可选：给前两个元素之间加点间距
}

.icon {
  width: 19px;
  height: 24px;
  background: url("@/assets/images/组 118709.png") no-repeat;
  background-size: 100% 100%;
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
