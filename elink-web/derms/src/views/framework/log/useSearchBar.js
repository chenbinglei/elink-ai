import { useMonitorStore } from '@/stores/index';

import moment from "moment";
import { ref, reactive, computed } from "vue";
import { EventLevelList, AlarmStatusList } from "@/common/enum";
import { disabledDate } from "@/views/monitor/constant";

const format = "YYYY-MM-DD";

export default function useSearchBar() {
  const startTime = moment().subtract(29, "days").format(format);
  const endTime = moment().format(format);
  const monitorStore = useMonitorStore();
  const assetTypeList = computed(() => monitorStore.assetTypeList.filter(item => item.id == '3'));
  const searchConfig = ref([
    {
      key: "userAccount",
      title: "操作用户",
      type: "input"
    },
    {
      key: "dateList",
      title: "操作时间",
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
    }
  ]);
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
    formData,
    searchInitData,
  };
}
