import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import SystemMonitorController from "@/api/together/systemMonitor";
import { ElMessage } from "element-plus";
import moment from "moment";

const columnBase = [
  {
    title: "序号",
    key: "index",
    extraProps: {
      align: "left",
      width: 75,
    },
  },
  {
    title: "时间",
    key: "time",
    extraProps: {
      align: "left",
      width: "auto",
    },
  },
];

export default function useTable ({ formData }) {
  const route = useRoute();
  const siteId = route.params.id;
  const store = useStore();
  const tableRef = ref(null);
  const rawData = ref({});
  const columns = ref([...columnBase]);
  const loading = ref(false);
  const exportConfig = computed(() => {
    return {
      fileName: "历史数据列表",
      header: columns.value,
      sheetName: "Sheet1",
    };
  });
  const isInit = ref(false);

  const checkCascader = () => {
    const { cascaderData } = formData;
    if (cascaderData.fieldCount === 0) {
      ElMessage.info({
        message: "请选择数据字段",
        duration: 1000,
      });
      return;
    }
    if (cascaderData.fieldCount > 20) {
      ElMessage.info({
        message: "数据字段查询个数最大为20个",
        duration: 1000,
      });
      return;
    }
    return true;
  };

  const tiggerSearch = () => {
    if (checkCascader()) {
      tableRef.value.loadTableData();
      isInit.value = true;
    }
  };
  const tiggerExport = () => {
    if (checkCascader()) {
      tableRef.value.exportTable();
    }
  };
  const onReset = () => {
    rawData.value = {};
    columns.value = [];

    if (tableRef.value) {
      tableRef.value.clearData();
    }
  };

  const onSearch = async () => {

    console.log('formData', formData);
    loading.value = true;
    const { dateList, cascaderData } = formData;
    // 处理时间范围
    const [startTime, endTime] = dateList || [];
    try {
      const { data, ...rest } =
        await SystemMonitorController.findAllHistoryDataList({
          startTime: moment(startTime).format("YYYY-MM-DD HH:mm:00"),
          endTime: moment(endTime).format("YYYY-MM-DD HH:mm:59"),
          ...cascaderData,
        });
      //生成tableData
      rawData.value = data;


      const { dateList: dateListFromRes, dataInfoList } = data;
      const columnsAppend = [];
      const hasCol = {};
      dataInfoList.forEach(({ fieldCode, deviceName, fieldName }) => {
        const col = {
          key: fieldCode,
          title: fieldName,
          extraProps: {
            align: "center",
            width: "auto",
          },
        };
        if (hasCol[deviceName]) {
          columnsAppend[hasCol[deviceName] - 1].children.push(col);
        } else {
          hasCol[deviceName] = columnsAppend.push({
            key: deviceName,
            title: deviceName,
            children: [col],
            extraProps: {
              align: "center",
              width: "auto",
            },
          });
        }
      });
      columns.value = columnBase.concat(columnsAppend);


      const tableData = dateListFromRes.map((time, index) => {
        const extra = {};
        dataInfoList.forEach(({ fieldCode, dataList }) => {
          extra[fieldCode] = dataList[index];
        });
        return {
          index: index + 1,
          time,
          ...extra,
        };
      });
     
      return {
        ...rest,
        data: {
          items: tableData,
        },
      };
    } finally {
      loading.value = false;
    }
  };


  onMounted(() => {
    console.log("useTable");
    store.dispatch("getDeviceFieldList", siteId);
  });

  return {
    loading,
    rawData,
    tableRef,
    columns,
    exportConfig,
    tiggerSearch,
    tiggerExport,
    onSearch,
    onReset
  };
}
