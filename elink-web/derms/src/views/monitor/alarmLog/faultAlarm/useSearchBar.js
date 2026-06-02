import { useStore } from "vuex";
import moment from "moment";
import { ref, reactive, computed } from "vue";
import { EventLevelList, AlarmStatusList } from "@/common/enum";
import { disabledDate } from "@/views/monitor/constant";

const format = "YYYY-MM-DD";

export default function useSearchBar() {
  const startTime = moment().subtract(29, "days").format(format);
  const endTime = moment().format(format);
  const store = useStore();
  const assetTypeList = computed(() => store.state.monitor.assetTypeList);
  const searchConfig = ref([
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
      key: "alarmStatus",
      title: "状态",
      type: "select",
      props: {
        placeholder: "全部",
        clearable: true,
        filterable: true,
        multiple: false,
        options: AlarmStatusList,
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
    dateList: [startTime, endTime],
    alarmStatus: null,
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
