export const getHourValue = (hourInt) => {
    let hour = Math.floor(hourInt);
    return hour * 100 + (hourInt - hour) * 60;
}
export const getHourStr = (hourInt) => {
    let hour = Math.floor(hourInt);
    if ((hourInt - hour) * 60 < 10) {
        return hour + ":0" + (hourInt - hour) * 60;
    } else {
        return hour + ":" + (hourInt - hour) * 60;
    }
}