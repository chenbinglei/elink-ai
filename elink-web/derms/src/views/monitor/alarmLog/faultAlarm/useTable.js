import { ref, onMounted, onActivated, onDeactivated, computed } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import SystemMonitorController from "@/api/together/systemMonitor";
import { EventLevelList, AlarmStatusList } from "@/common/enum";
import AlarmStatusView from "./alarmStatusView.vue";
import EventLevelView from "./eventLevelView.vue";

export default function useTable ({ formData, initData }) {
  const route = useRoute();
  const store = useStore();
  const deviceTypeMap = computed(() => store.state.monitor.deviceTypeMap); // 设备类型映射
  const siteId = route.params.id;
  const tableRef = ref(null);
  const columns = ref([
    {
      title: "设备类型",
      key: "typeId",
      width: 159,
      formatter: (typeId) => deviceTypeMap.value[typeId] || typeId,
    },
    {
      title: "告警级别",
      key: "eventLevel",
      width: 159,
      formatter: (eventLevel) =>
        EventLevelList.find((item) => item.value === eventLevel)?.label ||
        alarmStatus,
      render: EventLevelView,
      filterName: "eventLevel"
    },
    {
      title: "告警内容",
      key: "eventName",
      width: 191,
    },
    {
      title: "发生时间",
      key: "createTime",
      width: 253,
    },
    {
      title: "修复时间",
      key: "updateTime",
      width: 253,
    },
    {
      title: "设备名称",
      key: "deviceName",
      width: 159,
    },
    {
      title: "SN号",
      key: "deviceCode",
      width: 232,
      formatter: (deviceCode) => deviceCode || "--",
    },
    {
      title: "告警时长(h)",
      key: "alarmDuration",
      width: 182,

    },
    {
      title: "状态",
      key: "alarmStatus",
      width: 138,
      formatter: (alarmStatus) =>
        AlarmStatusList.find((item) => item.value === alarmStatus)?.label ||
        alarmStatus,
      render: AlarmStatusView,
    },
  ]);
  const exportConfig = ref({
    fileName: "故障告警列表",
    header: columns.value,
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
    return await SystemMonitorController.findFaultAlarmListByPage({
      siteId,
      startDate,
      endDate,
      page: currentPage || 1,
      size: pageSize || 10,
      ...rest,
    }, Date.now());
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
