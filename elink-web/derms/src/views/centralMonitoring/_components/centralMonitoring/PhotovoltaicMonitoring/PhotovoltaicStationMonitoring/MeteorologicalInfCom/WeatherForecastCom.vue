<template>
  <div class="content_body">
    <template v-if="list && list.length">
        <div v-for="(item,index) in list" :key="index" class="weather_list_card flex-ai-center">
          <div class="weather_li_left">
            <div class="weather_li_left_top">{{  $filters.moreData(item.dateWeek) }}</div>
            <div class="weather_li_left_bottom">{{  $filters.moreData(item.dateTime) }}</div>
          </div>
          <div class="weather_li_center flex-all">
            <el-image class="weather_icon" :src="item.weatherObj?.iconImg">
              <template #error>
                <div class="image-slot flex-jc-ai-center">
                  <el-icon size="24" color="#ffffff80"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="weather_text flex-all textTwo">{{ $filters.moreData(item.weatherObj?.description) }}</div>
          </div>
          <div class="weather_li_right">
            <div class="number">
              <span class="min_temp">{{ $filters.roundedNumber(item.temp?.min) }}</span>
              <span class="split_text">~</span>
              <span class="max_temp">{{ $filters.roundedNumber(item.temp?.max) }}</span>
            </div>
            <div class="number_unit">℃</div>
          </div>
        </div>
    </template>
    <null-data v-else words="未获取到未来天气信息！"></null-data>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, watch} from "vue";
import {getNowDate, queryDateWeekdayFun} from "@/utils/dateTime";
import {Picture} from "@element-plus/icons-vue";

export default defineComponent({
  name: "WeatherForecastCom",
  components: {Picture},
  props:{
    returnDataInfo:{
      type: Object,
      default: ()=>{
        return { };
      }
    }
  },
  setup(props) {
    const that = reactive({
      list: []
    });

    const watchReturnDataInfo = watch(()=> props.returnDataInfo,(newReturnDataInfo)=>{
      let return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      if(return_data_info.daily && return_data_info.daily.length){
        for(let i = 0;i < return_data_info.daily.length;i++){
          return_data_info.daily[i].dateTime = getNowDate(return_data_info.daily[i].dt);
          return_data_info.daily[i].dateWeek = queryDateWeekdayFun(return_data_info.daily[i].dt);
          try {
            if(return_data_info.daily[i].weather && return_data_info.daily[i].weather.length){
              return_data_info.daily[i].weatherObj = return_data_info.daily[i].weather[0] ?? {};
              try {
                return_data_info.daily[i].weatherObj.iconImg = `/img/weather/${ return_data_info.daily[i].weatherObj?.icon }@2x.png`;
              } catch (e) {
                // eslint-disable-next-line no-console
                console.log(e);
              }
            }
          } catch (e) {
            // eslint-disable-next-line no-console
            console.log(e);
          }
        }
      }
      that.list = JSON.parse(JSON.stringify(return_data_info.daily ?? []));
    },{ deep: true,immediate: true });

    return { ...toRefs(that), watchReturnDataInfo };
  }
});

</script>


<style lang="scss" scoped>
.weather_list_card{
  padding: 6px 12px;
  border-radius: 6px;
  background: #d9d9d91a;
  box-sizing: border-box;
  margin-bottom: 6px;
  justify-content: space-between;

  .weather_li_left{
    font-size: 14px;
    color: #ffffffcc;

    .weather_li_left_top{
      color: #ffffff;
      margin-bottom: 4px;
    }

    .weather_li_left_bottom{
      white-space: nowrap;
    }
  }

  .weather_li_center{
    display: flex;
    align-items: center;
    padding: 0 10px;
    box-sizing: border-box;

    .weather_icon {
      width: 56px;
      height: 56px;
      display: flex;
      align-items: center;
      justify-content: center;

      .image-slot{
        width: 48px;
        height: 48px;
        border-radius: 4px;
        background: #a7bcce26;
      }
    }

    .weather_text{
      color: #ffffff;
      font-size: 16px;
      font-weight: 700;
      -webkit-line-clamp: 1;
    }
  }

  .weather_li_right{
    display: flex;
    color: #ffffff;
    font-size: 20px;
    font-weight: 700;

    .number_unit{
      font-size: 14px;
      color: #ffffff99;
      margin-left: 2px;
    }
  }

  &:last-child{
    margin-bottom: 0;
  }
}
</style>