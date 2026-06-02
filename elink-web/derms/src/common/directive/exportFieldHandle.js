export default {
    // 所在城市
    siteCityNameFun(data) {
        console.log(data);
        return `${data.province ?? '--'}/${data.city ?? '--'}`;
    }
};