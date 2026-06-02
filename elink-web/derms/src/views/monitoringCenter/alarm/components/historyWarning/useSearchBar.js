import { useStore } from "vuex";
import moment from "moment";
import { ref, reactive, computed, onMounted } from "vue";
import { EventLevelList, AlarmStatusList } from "@/common/enum";
import { disabledDate } from "@/views/monitor/constant";
const format = "YYYY-MM-DD";
// import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";

export default function useSearchBar(props) {
  const startTime = moment().subtract(29, "days").format(format);
  const endTime = moment().format(format);
  const store = useStore();
  const assetTypeList = computed(() => store.state.monitor.assetTypeList.filter(item => item.id == '3'));
  // 响应式站点列表
  // const siteList = ref([]);
  // 获取站点列表
  // const fetchSiteList = async () => {
  //   try {
  //     const res = await findSiteListByUserId({});
  //     siteList.value = res.data.map(item => ({
  //       value: item.id,        // 假设每个站点有 id 字段
  //       label: item.siteName,  // 假设每个站点有 siteName 字段
  //     }));
  //   } catch (error) {
  //     console.error("获取站点列表失败:", error);
  //   }
  // };

  // 在组件挂载时获取数据
  onMounted(() => {
    // fetchSiteList();
  });
  const searchConfig = ref([
    {
      key: "siteId",
      title: "场站",
      type: "select",
      props: {
        placeholder: "全部",
        clearable: true,
        filterable: true,
        multiple: false,
        options: props.siteList,
        style: { width: "180px" }, // 设置宽度
      },
    },
    {
      key: "typeId",
      title: "设备类型",
      type: "treeSelect",
      props: {
        placeholder: "全部",
        clearable: true,
        filterable: true,
        multiple: false,
        data: assetTypeList,
        nodeKey: "id",
        defaultExpandAll: true,
        // style: { width: "200px" }, // 设置宽度
        props: {
          label: "typeName",
          disabled: (item) => {
            return item.type === 1;
          },
        },
      },
    },
    {
      key: "eventLevel",
      title: "告警级别",
      type: "select",
      props: {
        placeholder: "全部",
        clearable: true,
        filterable: true,
        multiple: false,
        options: EventLevelList,
         style: { width: "140px" }
      },
    },
    {
      key: "dateList",
      title: "发生时间",
      type: "date",
      props: {
        type: "daterange",
        startPlaceholder: "开始时间",
        endPlaceholder: "结束时间",
        format,
        valueFormat: format,
        rangeSeparator: "~",
        disabledDate: disabledDate.disableAfterToday,
        shortcuts: [
          {
            text: "最近三天",
            value: () => [moment().subtract(2, "day").format(format), endTime],
          },
          {
            text: "最近一周",
            value: () => [moment().subtract(6, "day").format(format), endTime],
          },
          {
            text: "最近一月",
            value: () => [
              moment().subtract(1, "month").format(format),
              endTime,
            ],
          },
          {
            text: "最近半年",
            value: () => [
              moment().subtract(6, "month").format(format),
              endTime,
            ],
          },
        ],
      },
    },
    {
      key: "eventStatus",
      title: "状态",
      type: "select",

      props: {
        placeholder: "全部",
        clearable: true,
        filterable: true,
        multiple: false,
        options: AlarmStatusList,
       style: { width: "140px" }
      },
    },
  ]);
  const rules = ref({
    typeId: [
      {
        required: true,
        message: "必须输入",
        trigger: "change",
      },
    ],
  });
  const searchInitData = {
    typeId: null,
    eventLevel: null,
    dateList: [startTime, endTime]
  };
  const formData = reactive({
    ...searchInitData,
  });
  return {
    searchConfig,
    rules,
    formData,
    searchInitData,
  };
}
