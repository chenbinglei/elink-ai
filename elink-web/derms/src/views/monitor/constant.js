import moment from "moment";

const disableAfterToday = (date) => {
  return moment(date).isAfter(moment(), "day");
};
const disableAfterThisMonth = (date) => {
  return moment(date).isAfter(moment(), "month");
};

const disableAfterThisYear = (date) => {
  return moment(date).isAfter(moment(), "year");
};

export const disabledDate = {
  disableAfterToday,
  disableAfterThisMonth,
  disableAfterThisYear,
};
