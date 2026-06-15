import { ref, onMounted, onActivated,onDeactivated, computed } from "vue";
import { useRoute } from "vue-router";
import { useMonitorStore } from '@/stores/index';

import {searchAlarmEventList } from "@/api/monitoringCenter/monitoringCenter";
import { EventLevelList, AlarmStatusList } from "@/common/enum";
import AlarmStatusView from "../alarmStatusView.vue";
import warningLevel from "../warningLevel.vue";
import { ElMessage } from "element-plus";

export default function useTable({ formData, initData }) {
  const route = useRoute();
  const monitorStore = useMonitorStore();
  const deviceTypeMap = computed(() => monitorStore.deviceTypeMap); // 设备类型映射
  const siteId = route.params.id;
  const tableRef = ref(null);
  const columns = ref([
    {
      title: "告警对象",
      key: "deviceName",
      width: 159,
    },
    {
      title: "SN号",
      key: "deviceNumber",
      width: 232,
    },
    {
      title: "设备类型",
      key: "typeName",
      width: 159
    },
    {
      title: "告警级别",
      key: "eventLevel",
      width: 120,
      formatter: (eventLevel) =>
        EventLevelList.find((item) => item.value === eventLevel)?.label ||
        alarmStatus,
      render: warningLevel,
      filterName: "eventLevel"
    },
    {
      title: "告警描述",
      key: "eventName",
      width: 191,
    },
    {
      title: "所属场站",
      key: "siteName",
      width: 253,
    },
    {
      title: "开始时间",
      key: "createTime",
      width: 253,
    },
    {
      title: "修复时间",
      key: "updateTime",
      width: 253,
    },
    {
      title: "告警状态",
      key: "eventStatus",
      width: 253,
      formatter: (eventLevel) =>
        AlarmStatusList.find((item) => item.value === eventLevel)?.label ||
        alarmStatus,
      render: AlarmStatusView,
      filterName: "alarmStatus"
    },
    {
      title: "操作",
      key: "operate",
      list: [
        {name: "查看",type: 'detail'}
      ],
      width: 253
    }
  ]);
  const exportConfig = ref({
    fileName: "历史告警列表",
    header: columns.value.filter(item => item.key != 'operate'),
    sheetName: "Sheet1",
  });

  const tiggerSearch = () => {
    tableRef.value.loadTableData();
  };
  const tiggerExport = () => {
    tableRef.value.exportTable();
  };
  const onSearch = async ({ currentPage, pageSize }) => {
    const { dateList, ...rest } = formData;
    // 处理时间范围
    const [startDate, endDate] = dateList || [];
    if(!startDate || !endDate) {
      ElMessage({type: "error", showClose: true, message: "请选择时间！"});
      return
    }
    return await searchAlarmEventList({
      siteId,
      startDate,
      endDate,
      page: currentPage || 1,
      size: pageSize || 10,
      ...rest
    },Date.now());
  };
  const onReset = () => {
    Object.assign(formData, {
      ...initData,
    });
    tableRef.value.loadTableData();
  };
  // 初始化查询
  onMounted(() => {
    tiggerSearch();
  });
  onActivated(() => {
    tiggerSearch();
  });
  return {
    tableRef,
    columns,
    exportConfig,
    tiggerSearch,
    tiggerExport,
    onSearch,
    onReset,
  };
}
