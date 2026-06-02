<template>
  <div class="w-full h-full flex flex-col justify-start items-stretch box-border" v-loading="loading">
    <tab-list :list="[{ name: '实时告警', type: 1 }, { name: '历史告警', type: 2 }, { name: '告警统计', type: 3 }]"
      v-model="activeTab" />
    <!-- 告警统计 -->
    <template v-if="activeTab == 3">
      <div class="flex dateTab items-center jusitify-between">
        <div class="mr-20"><span :class="activeIndex == index ? 'active' : ''" v-for="(item, index) in dateList"
            :key="item" @click="activeIndex = index">{{ item }}</span></div>

        <el-form ref="formRef" :inline="true" :status-icon="true" class="h-32px flex items-stretch  search-bar">
          <el-form-item label="选择场站">
            <el-select class="selectRegion" v-model="siteId" placeholder="请选择场站" :filter-method="filterMethod"
              filterable multiple collapse-tags max-collapse-tags="1">
              <!--  @change="handleSelectChange" -->
              <el-option v-for="item in siteList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="发生时间" v-if="activeIndex == 2">
            <el-date-picker v-model="dateT" type="daterange" start-placeholder="开始时间" end-placeholder="结束时间"
              value-format="YYYY-MM-DD" :disabled-date="disabledDate" @calendar-change="handleCalendarChange" />
          </el-form-item>
          <el-form-item class="h-full flex items-center justify-center">
            <el-button type="primary" @click="onSearch">
              <i class="iconfont icon-search text-18px w-16px text-white mr-6px" />查询
            </el-button>
            <el-button @click="onReset"> <i
                class="iconfont icon-reset text-16px w-16px text-white mr-6px" />重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="wrap flex">
        <div class="wrap-left">
          <div class="flex flex-col jusitify-between h-full" v-loading="loading">
            <div v-for="(item, index) in table" :key="'item' + index"
              style="overflow-y: scroll;flex-shrink: 0;flex: 1;">
              <el-table :data="item.tableData" table-layout="fixed" @row-click="(row) => handleRowClick(row, index)"
                height="35vh" highlight-current-row :current-row-key="item.currentDefaultKey" :row-key="item.rowKey"
                :ref="(el) => setTableRef(el, index)">
                <el-table-column :prop="vo.prop" :label="vo.name" v-for="(vo, ind) in item.columns"
                  :column-key="vo.prop + index" :key="'vo' + ind" align="center" :sortable="vo.prop == 'alarmNum'"
                  :width="vo.prop == 'dataName' ? 200 : auto">
                  <!-- 自定义渲染占比 -->
                  <template #default="scope">
                    <span v-if="vo.prop === 'proportion'">
                      {{ scope.row[vo.prop].includes('NaN') ? '--' : scope.row[vo.prop] }}
                    </span>
                    <!-- 其他列正常显示 -->
                    <span v-else>{{ scope.row[vo.prop] }}</span>
                  </template></el-table-column>
                <template #empty>
                  <el-empty :image="emptyImg" description="请选择场站" />
                </template>
              </el-table>
            </div>
          </div>
        </div>
        <div class="wrap-right">
          <div class="wrap-right-echart" style="flex: 1;width: 100%;height: 100%;">
            <el-row :gutter="20" style="width: 100%;height: 100%;">
              <el-col :span="8">
                <alarmTrendMapEchart :echartsData="state.chartsDataTop" key="chartsDataTop"></alarmTrendMapEchart>
              </el-col>
              <el-col :span="8">
                <div class="alarmwrap">
                  <div class="alLeBg flex justify-center">
                    <div>
                      <p>{{ alarmCountTop }}</p>
                      <p style="font-size: 14px;">共</p>
                    </div>
                  </div>
                  <alarmLevelEchart :pieData="echartTopDatas.pieData"></alarmLevelEchart>
                </div>
              </el-col>
              <el-col :span="8" style="width: 100%;height: 100%;">
                <durationDistributionEchart :echartsData="echartTopDatas.durationMap"></durationDistributionEchart>
              </el-col>
            </el-row>
          </div>
          <div class="wrap-right-echart wrap-right-echartBottom" style="flex: 1;width: 100%;height: 100%;">
            <el-row :gutter="20" v-if="activeRow.dataId" style="width: 100%;height: 100%;">
              <el-col :span="8">
                <alarmTrendMapEchart :echartsData="state.chartsDataBottom" key="chartsDataBottom"></alarmTrendMapEchart>
              </el-col>
              <el-col :span="8">
                <div style="width: 100%;height: 100%;">
                  <scatterPlotEchart :echartsData="echartTopDatas.scatterMap"></scatterPlotEchart>
                </div>
              </el-col>
              <el-col :span="8">
                <div style="width: 100%;height: 100%;" class="flex fd-column">
                  <div class="warning-text">{{ dateT[0] || '' }}至{{ dateT[1] || '' }}期间，
                    <span class="special-text" style="font-size: 14px;">{{ activeRow.dataName || '' }}</span>累计发生
                    <span class="ml-1 mr-1 special-text" style="color: #FF0000;">{{ alarmCount }}</span>次告警，其中：
                  </div>
                  <div class="flex pt-3 pb-3 jc-space-between">
                    <div class="flex ai-center fd-column" v-for="(item, index) in levelListDynamic" :key="index">
                      <img :src="item.icon" alt="" width="60" height="21">
                      <div class="number special-text mt-1" :style="{ color: item.color }">{{ item.number }}</div>
                    </div>
                  </div>
                  <station-tab-group :tab-active="tabActiveIndex" :tab-list="['高频告警', '长时告警']"
                    @tabChangeEvent="tabChangeHandler" style="padding: 0;"></station-tab-group>
                  <ul class="infinite-list" style="overflow: auto">
                    <template v-if="tabActiveIndex == 0">
                      <li v-for="(item, cindex) in highFrequencyAlarmList" :key="cindex" class="infinite-list-item">
                        <span>{{ cindex < 10 ? '0' + (cindex + 1) : cindex }}</span>
                            <span class="ml-2">{{ item.eventName }}故障告警，发生</span>
                            <span class="ml-2 mr-2 special-text">{{ item.alarmCount }}</span>次，占比
                            <span class="ml-2 mr-2 special-text">{{ item.proportion }}</span>%
                      </li>
                    </template>
                    <template v-else>
                      <li v-for="(item, cindex) in accDurationAlarmList" :key="cindex" class="infinite-list-item">
                        <span>{{ cindex < 10 ? '0' + (cindex + 1) : cindex }}</span>
                            <span class="ml-2">{{ item.eventName }}故障告警</span>,累计持续
                            <span class="ml-2 mr-2 special-text">{{ item.totalDuration }}</span>小时
                      </li>
                    </template>
                  </ul>
                </div>
              </el-col>
            </el-row>
            <el-empty :image="emptyImg" v-else description="请选择设备" />
          </div>
        </div>
      </div>
    </template>
    <!-- 实时告警 -->
    <realTimeWarning v-if="activeTab == 1" :siteList="siteList"></realTimeWarning>
    <!-- 历史告警 -->
    <historyWarning v-if="activeTab == 2" :siteList="siteList"></historyWarning>
  </div>
</template>
<script setup>
import emptyImg from '@/assets/image/empty.png';
import { useStore } from 'vuex';
import moment from "moment";
import { ElMessage } from "element-plus";
import { ref, reactive, watch, onMounted, computed } from "vue";
import StationTabGroup from "@/components/Tabs/StationTabGroup.vue";
import alarmLevelEchart from "./components/alarmLevelEchart.vue";
import realTimeWarning from "./components/realTimeWarning/index.vue";
import historyWarning from "./components/historyWarning/index.vue";
import scatterPlotEchart from "./components/scatterPlotEchart.vue";
import durationDistributionEchart from "./components/durationDistributionEchart.vue";
import alarmTrendMapEchart from "./components/alarmTrendMapEchart.vue";
import TabList from "@/components/Tabs/TabList.vue";
import { searchAlarmSiteCount, searchAlarmDeviceCount } from "@/api/monitoringCenter/monitoringCenter";
import { EventLevelList } from "@/common/enum";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";


import { color } from 'echarts';
const store = useStore();
const activeTab = ref(1);
const activeRow = ref({});
const activeTopRow = ref({});
const siteList = ref([]);
// 表格
const table = reactive([
  {
    tableData: [],
    rowKey: 'dataName',
    currentDefaultKey: '全部',
    ref: 'tableRefFir',
    columns: [{ name: '场站', prop: 'dataName' }, { name: '告警数量', prop: 'alarmNum' }, { name: '占比', prop: 'proportion' }],
  },
  {
    tableData: [],
    rowKey: 'dataId',
    ref: 'tableRefSec',
    currentDefaultKey: null,
    columns: [{ name: '设备', prop: 'dataName' }, { name: '告警数量', prop: 'alarmNum' }, { name: '占比', prop: 'proportion' }],
  }
])
const tableRefs = ref([]);
const siteId = ref(null)
const setTableRef = (el, index) => {
  if (el) {
    tableRefs.value[index] = el;
  }
};
// const handleSelectChange = () => {
//   searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], siteIds: siteId.value });

// };
// 获取站点列表
const fetchSiteList = async () => {
  try {
    const res = await findSiteListByUserId({});
    siteList.value = res.data.map(item => ({
      value: item.id,        // 假设每个站点有 id 字段
      label: item.siteName,  // 假设每个站点有 siteName 字段
    }));
  } catch (error) {
    console.error("获取站点列表失败:", error);
  }
};
table[1].currentDefaultKey = computed(() => table[1].tableData[0]?.dataId || null)
// 高频长时间报警
const tabActiveIndex = ref(0);
const tabChangeHandler = (index) => {
  tabActiveIndex.value = index;
}
const highFrequencyAlarmList = ref([]);
const accDurationAlarmList = ref([]);

// 报警等级 加上 总数
const levelListDynamic = ref(EventLevelList)
const alarmCount = computed(() => {
  return levelListDynamic.value.reduce((pre, cur) => {
    return pre + cur?.number
  }, 0)
})

// 告警登记统计总数
const alarmCountTop = computed(() => {
  return echartTopDatas.value.pieData.reduce((pre, cur) => {
    return pre + cur.value
  }, 0)
})
// 趋势图
const state = reactive({
  chartsDataTop: [],
  chartsDataBottom: [],
});
// 其他echarts图
const echartTopDatas = ref({
  pieData: [],
  durationMap: [],
  scatterMap: []
})
// const loading = ref(true);
// 表格点击
const handleRowClick = (row, index) => {
  console.log(row, index, '表格点击')
  if (index == 0) {
    if (row.dataName == '全部') table[1].tableData = []
    if (dateT.value?.length) {
      activeTopRow.value = row
      activeRow.value = {}
      // siteId.value && 
      if (row.dataName == '全部') return searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], siteIds: siteId.value });
      searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], siteId: row.dataId });
    } else {
      ElMessage({ type: "error", showClose: true, message: "请选择时间！" });
    }
  }
  else {
    activeRow.value = row
    searchAlarmDeviceCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], deviceId: row.dataId });
  }
};
// 获取站点
const searchAlarmSiteCountFun = (data) => {
  console.log("searchAlarmSiteCountFun", data);
  searchAlarmSiteCount(data).then((res) => {
    let alarmNumList = res.data.alarmNumList;
    let totalNum = data.siteId ? alarmNumList.reduce((a, b) => a + b.alarmNum, 0) : res.data.alarmNumList[0]?.alarmNum
    alarmNumList.map(item => {
      item.proportion = ((item.alarmNum / totalNum) * 100).toFixed(2) + '%';
    })

    table[data.siteId ? 1 : 0].tableData = alarmNumList
    if (data.siteId) {
      activeRow.value = alarmNumList[0]
      searchAlarmDeviceCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], deviceId: activeRow.value.dataId });
    }
    let arr = []
    for (const key in res.data.alarmLevelMap) {
      if (key != 0) {
        let obj = EventLevelList.find(item => item.value == key)
        arr.push({ name: obj.label.substring(0, 2), value: res.data.alarmLevelMap[key], itemStyle: { color: obj.color } })
      }
    }
    echartTopDatas.value.pieData = arr
    let keys = Object.keys(res.data.durationMap)
    let obj = { 1: '<1h', 2: '1-3h', 3: '3-12h', 4: '12-24h', 5: '24-72h', 6: '>72h' }
    let ac = []
    keys.forEach(key => ac.push(obj[key]))
    echartTopDatas.value.durationMap = [ac, Object.values(res.data.durationMap)]
    state.chartsDataTop = [Object.keys(res.data.alarmTrendMap), Object.values(res.data.alarmTrendMap)];
    console.log(state.chartsDataTop)
  });
};
// 获取设备统计
const searchAlarmDeviceCountFun = (data) => {
  searchAlarmDeviceCount(data).then((res) => {
    let arr = []
    for (const key in res.data.scatterMap) {
      res.data.scatterMap[key].forEach(item => {
        arr.push({ value: [key, item.totalDuration], name: item.eventName })
      })
    }
    echartTopDatas.value.scatterMap = arr
    levelListDynamic.value = levelListDynamic.value.map(item => {
      item.number = 0
      let obj = res.data.alarmLevelList?.find(levelItem => levelItem.alarmLevel == item.value)
      if (obj) item.number = obj.alarmCount
      return item
    })
    highFrequencyAlarmList.value = res.data.highFrequencyAlarmList;
    accDurationAlarmList.value = res.data.accDurationAlarmList;
    state.chartsDataBottom = [Object.keys(res.data.alarmTrendMap), Object.values(res.data.alarmTrendMap)];
  });
};
onMounted(() => {
  fetchSiteList()
  store.dispatch('getAssetTypeList');
  searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1] });
});
const onSearch = () => {
  if (activeIndex !== 2 && dateT.value?.length) {
    activeTopRow.value = {}
    activeRow.value = {}
    table[1].tableData = []
    tableRefs.value[0]?.setCurrentRow(null)
    tableRefs.value[0]?.setCurrentRow(table[0].tableData.find(item => item.dataName === '全部'))
    console.log('jinru');
    if (siteId.value && siteId.value.length > 0) {
      searchAlarmSiteCountFun({
        startDate: dateT.value[0],
        endDate: dateT.value[1],
        siteIds: siteId.value
      });
    } else {
     searchAlarmSiteCountFun({
        startDate: dateT.value[0],
        endDate: dateT.value[1],

      });
    }
    // searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1], siteIds: siteId.value });
  } else {
    ElMessage({ type: "error", showClose: true, message: "请选择时间！" });
  }
};
const onReset=()=>{
  activeIndex.value = 0
  siteId.value = null
}
// 日期
const dateList = reactive(['近7日', '近30日', '自定义'])
const dateT = ref([]);
const activeIndex = ref(0);
watch(activeIndex, (newIndex) => {
  if (0 == newIndex) {
    const end = new Date()
    const start = new Date()
    siteId.value = null
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 6)
    dateT.value = [moment(start).format('YYYY-MM-DD'), moment(end).format('YYYY-MM-DD')]
    // onSearch()
    searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1] });
  } else if (1 == newIndex) {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 29)
    dateT.value = [moment(start).format('YYYY-MM-DD'), moment(end).format('YYYY-MM-DD')]
    // onSearch()
    searchAlarmSiteCountFun({ startDate: dateT.value[0], endDate: dateT.value[1] });
    siteId.value = null
  } else {
    dateT.value = []
    siteId.value = null
  }
}, { immediate: true });
const tempStartDate = ref(null)
const today = new Date();
today.setHours(0, 0, 0, 0);
// 禁用日期逻辑
const disabledDate = (time) => {
  const timeDate = new Date(time);
  timeDate.setHours(0, 0, 0, 0);
  if (timeDate > today) return true;  // 1. 永远禁用今天之后的日期
  if (tempStartDate.value) {  // 2. 如果已选择开始日期，则禁用超出90天范围的日期
    const startDate = new Date(tempStartDate.value);
    startDate.setHours(0, 0, 0, 0);
    const minDate = new Date(startDate);
    minDate.setDate(startDate.getDate() - 90);
    const maxDate = new Date(startDate);
    maxDate.setDate(startDate.getDate() + 90);
    return timeDate < minDate || timeDate > maxDate;
  }
  return false;
}
// 当用户选择日期时触发
const handleCalendarChange = (dates) => {
  tempStartDate.value = dates[0]; // 记录开始日期
};

</script>
<style lang="scss" scoped>
@import "./index.scss";

.selectRegion {
  width: 320px;
  height: 32px;
}
</style>