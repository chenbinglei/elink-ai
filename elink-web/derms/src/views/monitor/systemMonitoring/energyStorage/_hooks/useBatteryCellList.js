import { ref, reactive, unref, watchEffect } from "vue";
import SystemMonitorController from "@/api/together/systemMonitor";

const voltageJudge = (_val) => {
  if (typeof _val !== "number") {
    return "none";
  }
  const val = _val / 1000;
  if (val < 2.6) {
    return 1;
  } else if (val <= 3.6) {
    return 0;
  }
  return 2;
};
const temperatureJudge = (val) => {
  if (typeof val !== "number") {
    return "none";
  }
  if (val < 0) {
    return 1;
  } else if (val <= 50) {
    return 0;
  }
  return 2;
};

export default function useBatteryCellList({ tabs, deviceId, activeTab }) {
  const cellList = ref([]);
  const cellIndex = ref(-1);
  const loading = ref(false);
  const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 1,
  });
  const selectCell = (index) => {
    cellIndex.value = index;
  };
  const findAllCellList = async () => {
    loading.value = true;
    try {
      const { success, data } = await SystemMonitorController.findAllCellList({
        dataId: unref(deviceId),
        queryType: tabs.findIndex((item) => item.id === activeTab.value) + 1,
        page: pagination.currentPage,
        size: pagination.pageSize,
      });
      if (!success) {
        return;
      }
      const { items, index, pageSize, totalSize } = data;
      //id  name state value
      const judgeFun =
        activeTab.value === "voltage" ? voltageJudge : temperatureJudge;
      const base = (index - 1) * pageSize;
      cellList.value = items.map((value, ind) => ({
        id: base + ind,
        value: typeof value === "number" ? Number(value).toFixed(2) : "--",
        name: `${base + ind + 1}#`,
        state: judgeFun(value),
      }));
      Object.assign(pagination, {
        total: totalSize,
      });
      cellIndex.value = 0;
    } finally {
      loading.value = false;
    }
  };
  const handleSizeChange = (val) => {
    pagination.pageSize = val;
    findAllCellList();
  };
  const handleCurrentChange = (val) => {
    pagination.currentPage = val;
    findAllCellList();
  };
  watchEffect(() => {
    findAllCellList();
  });

  return {
    cellList,
    cellIndex,
    loading,
    pagination,
    selectCell,
    findAllCellList,
    handleSizeChange,
    handleCurrentChange,
  };
}
