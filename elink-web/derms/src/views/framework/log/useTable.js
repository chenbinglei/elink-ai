import { ref, onMounted, onActivated, computed } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import AlarmStatusView from "./alarmStatusView.vue";
import SystemsetController from "@/api/system/index";

export default function useTable({ formData, initData }) {
  const tableRef = ref(null);
  const columns = ref([
    {
      title: "用户名",
      key: "userAccount",
      width: 159,
    },
    {
      title: "姓名",
      key: "userName",
      width: 232,
    },
    {
      title: "操作结果",
      key: "message",
      width: 120,
      render: AlarmStatusView,
    },
    {
      title: "操作内容",
      key: "content",
      width: 191,
    },
    {
      title: "操作时间",
      key: "createTime",
      width: 253,
    },
    {
      title: "登录ip",
      key: "remoteAddr",
      width: 253,
    }
  ]);
  const exportConfig = ref({
    fileName: "故障告警列表",
    header: columns.value.filter(item => item.key != 'operate'),
    sheetName: "Sheet1",
  });

  const tiggerSearch = () => {
    tableRef.value.loadTableData();
  };
  const tiggerExport = () => {
    tableRef.value.exportTable();
  };
  const onSearch = async ({ currentPage, pageSize,activeTab }) => {
    const { dateList, ...rest } = formData;
    const [startDate, endDate] = dateList || [];
    return await SystemsetController.queryAccessLogList({
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
