import moment from "moment";
import { ref, reactive, computed } from "vue";
import { useMonitorStore } from '@/stores/index';

import { disabledDate } from "@/views/monitor/constant";

const dateFormat = "YYYY-MM-DD";
const timeFormat = "HH:mm";
const format = `${dateFormat} ${timeFormat}`;
export default function useSearchBar() {
  const startTime = moment().startOf("day").format(format);
  const endTime = moment().format(format);
  const monitorStore = useMonitorStore();
  const deviceFieldList = computed(() => monitorStore.deviceFieldList);
  const searchConfig = ref([
    {
      key: "dateList",
      title: "选择时间",
      type: "date",
      props: {
        type: "datetimerange",
        startPlaceholder: "开始时间",
        endPlaceholder: "结束时间",
        dateFormat,
        timeFormat,
        format,
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
      key: "cascaderData",
      title: "选择数据",
      type: "cascader",
      props: {
        placeholder: "全部",
        data: deviceFieldList,
      },
    },
  ]);
  const rules = ref({});
  const searchInitData = {
    dateList: [startTime, endTime],
    cascaderData: {
      timeInterval: "1m",
      functions: [],
      nodes: [],
      fieldCount: 0,
    },
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
