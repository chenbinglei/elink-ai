import { ref, computed, unref, watch, onMounted } from "vue";
import moment from "moment";

const pickerTypeFormatMap = {
  year: "YYYY",
  years: "YYYY",
  month: "YYYY-MM",
  months: "YYYY-MM",
  date: "YYYY-MM-DD",
  dates: "YYYY-MM-DD",
  datetime: "YYYY-MM-DD HH:mm:ss",
  week: "YYYY-WW",
  datetimerange: "YYYY-MM-DD HH:mm:ss",
  daterange: "YYYY-MM-DD",
  monthrange: "YYYY-MM",
  yearrange: "YYYY",
};
const disableAfterToday = (date) => {
  return moment(date).isAfter(moment(), "day");
};
const disableAfterThisMonth = (date) => {
  return moment(date).isAfter(moment(), "month");
};

const disableAfterThisYear = (date) => {
  return moment(date).isAfter(moment(), "year");
};

export const disabledDateMap = {
  disableAfterToday,
  disableAfterThisMonth,
  disableAfterThisYear,
};

const getDate = (val, format) => {
  const opType = val[0] > 0 ? "add" : "subtract";
  // 配置化生成初始日期
  return moment()[opType]?.(Math.abs(val[0]), val[1]).format(unref(format));
};
const getDefVal = (val, format) => {
  if (val instanceof Array) {
    if (typeof val[0] === "number") {
      // 配置化生成初始日期
      return getDate(val, format);
    } else if (val[0] instanceof Array) {
      return val.map((item) => getDate(item, format));
    }
  }
  return val;
};

const useDiabledDate = (disabledConfig, format) => {
  // 选择的日期
  const choosenDay = ref(null);
  const preset = computed(()=> unref(disabledConfig)?.preset);
  const range = computed(()=> unref(disabledConfig)?.range);
  //预设的禁止方法
  const disableFunFromPreset = computed(() =>
    unref(preset) ? disabledDateMap[unref(preset)] : null
  );
  //时间比较的维度
  const granularity = computed(() => format.value[format.value.length - 1]);
  //
  const minAndMax = computed(() => {
    //对设置的range范围进行判断 不在范围内的禁止
    let minTime, maxTime;
    //如果设置了限制范围
    if (range && range instanceof Array && range.length === 2) {
      minTime = range[0] ? getDate(range[0], format) : null;
      maxTime = range[1] ? getDate(range[1], format) : null;
    }
    return {
      minTime,
      maxTime,
    };
  });

  /**
   * 判断日期是否被禁止
   * @param {Date} date
   * @returns {Boolean} 日期是否禁止
   */
  const disabledDate = computed(() => (date) => {
    //预设的快捷判断
    const disableFromPreset = disableFunFromPreset.value?.(date);
    // 面板隐藏的,或者未选中
    if (!choosenDay.value) {
      return disableFromPreset;
    }
    const { minTime, maxTime } = minAndMax.value;
    const beforeMinTime =
      minTime && moment(date).isBefore(minTime, granularity.value);
    const afterMaxTime =
      maxTime && moment(date).isAfter(maxTime, granularity.value);
    //如果设置了duration 限制日期的前后跨度最大
    const { duration } = disabledConfig.value || {};
    let outOfDuration;
    if (duration && duration instanceof Array) {
      const [num, unit] = duration || [];
      const diffDuration = moment(date).diff(choosenDay.value, unit);
      outOfDuration = Math.abs(diffDuration) > num;
    }
    return disableFromPreset || beforeMinTime || afterMaxTime || outOfDuration;
  });
  return {
    disabledDate,
    //面板选中时
    calendarChange: (e) => {
      choosenDay.value = e[0];
    },
    //面板关闭时
    visibleChange: (visibility) => {
      if (visibility) {
        return;
      }
      //关闭时 清空选中
      choosenDay.value = null;
    },
  };
};

export const usePickerProps = ({
  type = "date",
  defaultValue,
  disabledConfig,
  ...rest
}) => {
  const format = computed(
    () => pickerTypeFormatMap[unref(type)] ?? pickerTypeFormatMap.date
  );
  const date = ref([]);
  const { disabledDate, ...useDiabledDateRest } = useDiabledDate(
    disabledConfig,
    format
  );

  const pickerProps = computed(() => ({
    ...rest,
    disabledDate: disabledDate.value,
    type: unref(type),
    teleported: true,
    rangeSeparator: "~",
    startPlaceholder: "开始日期",
    endPlaceholder: "截止日期",
  }));

  const pickerEvents = {
    ...useDiabledDateRest,
  };

  onMounted(() => {
    date.value = getDefVal(unref(defaultValue), format);
  });
  watch(
    defaultValue,
    () => {
      date.value = getDefVal(unref(defaultValue), format);
    }
  );

  return {
    date,
    format,
    pickerProps,
    pickerEvents,
  };
};
