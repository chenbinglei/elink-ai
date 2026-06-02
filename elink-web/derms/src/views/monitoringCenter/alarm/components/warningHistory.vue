<template>
  <div class="w-full h-full">
    <!-- 弹框 查看-->
    <el-dialog v-model="isVisibleWarning" width="25%" align-center @close="closeDialog">
      <template #header>
        <div class="dialog-title">
          <span class="pointer" v-for="(item, index) in dialogTabList" :key="index" @click="activeIndex = index" :class="activeIndex == index ? 'active' : ''">{{item}}</span>
        </div>
      </template>
      <div class="dialog-warning-content">
        <div style="padding-bottom: 24px;" v-if="activeIndex == 1">
          <el-date-picker v-model="dateT" type="daterange" start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD" :disabled-date="disabledDate"
            @change="searchDeviceAlarmEventList" :shortcuts="pickerOptionsGthanAcTime().shortcuts"/>
        </div>
        <div class="flex ac-content" v-if="activeIndex == 0" style="height: 20vw;">
          <div class="acc-left">{{activeRow?.createTime?.substring(0,11)}}</div>
          <div class="flex ai-center fd-column">
            <img src="@/assets/image/icon-progress.png" alt="" width="28" height="28">
            <div class="dot-line flex-1"></div>
          </div>
          <div class="acc-right">
            <div class="flex-ai-center jc-space-between acc-r-warning pr-1">
              <h3>{{activeRow.eventName}}</h3>
              <img :src="getImg(activeRow.eventLevel)" alt="" width="50">
            </div>
            <div class="acc-r-event">
              <div class="acc-r-event-list"><span>发生时间</span>{{activeRow.createTime}}</div>
              <div class="acc-r-event-list"><span>结束时间</span>{{activeRow.updateTime}}</div>
              <div class="acc-r-event-list"><span>持续时间</span>{{activeRow.alarmDuration}}</div>
            </div>
          </div>
        </div>
        <div v-else style="height: 20vw;overflow-y: scroll;">
          <div class="flex ac-content" v-for="(val,key,index) in historyWarningList" :key="index + 'historyWarningList'" >
            <div class="acc-left">{{key}}</div>
            <div class="flex ai-center fd-column">
              <img src="@/assets/image/icon-progress.png" alt="" width="28" height="28">
              <div class="dot-line flex-1"></div>
            </div>
            <div class="acc-right">
              <div  v-for="(item, index) in val" :key="index + 'val'">
                <div class="flex-ai-center jc-space-between acc-r-warning pr-1">
                  <h3>{{item.eventName}}</h3>
                  <img :src="getImg(item.eventLevel)" alt="" width="50">
                </div>
                <div class="acc-r-event">
                  <div class="acc-r-event-list"><span>发生时间</span>{{item.createTime}}</div>
                  <div class="acc-r-event-list"><span>结束时间</span>{{item.updateTime}}</div>
                  <div class="acc-r-event-list"><span>持续时间</span>{{item.alarmDuration}}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref,watch,defineEmits,defineProps } from "vue";
import { EventLevelList } from "@/common/enum";
import {pickerDateOneMonthDayNOMin, pickerOptionsGthanAcTime} from "@/utils/dateTime";
import { findDeviceAlarmEventList } from "@/api/monitoringCenter/monitoringCenter";
const isVisibleWarning = ref(false);
const dialogTabList = ref(['当前警告','历史告警'])
const activeIndex = ref(0)
const historyWarningList = ref({})
const dateT = ref(pickerDateOneMonthDayNOMin(29))
const tempStartDate = ref(null)
const today = new Date();
today.setHours(0, 0, 0, 0);
// 禁用日期逻辑
const disabledDate = (time) => {
  const timeDate = new Date(time);
  timeDate.setHours(0, 0, 0, 0);
  if (timeDate > today) return true;  // 1. 永远禁用今天之后的日期
  if (tempStartDate.value) {  // 2. 如果已选择开始日期，则禁用超出90天范围的日期
    const startDate = new Date(tempStartDate.value);
    startDate.setHours(0, 0, 0, 0);
    const minDate = new Date(startDate);
    minDate.setDate(startDate.getDate() - 90);
    const maxDate = new Date(startDate);
    maxDate.setDate(startDate.getDate() + 90);
    return timeDate < minDate || timeDate > maxDate;
  }
  return false;
}
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  activeRow: {
    type: Object,
    default: () => {}
  },
})
const emit = defineEmits(['close'])

watch(() => props.visible, (newVisible) => { 
  isVisibleWarning.value = newVisible;
  activeIndex.value = 0
  dateT.value = pickerDateOneMonthDayNOMin(29)
  searchDeviceAlarmEventList()
})
watch(() => props.activeRow, (newVisible) => { 
  searchDeviceAlarmEventList()
})
// 报警类型
const getImg = (level) => { 
  return EventLevelList.find(item => item.value == level)?.icon
}
const closeDialog = () => { 
  isVisibleWarning.value = false;
  emit('close')
}
// 查询历史告警
const searchDeviceAlarmEventList = () => { 
  findDeviceAlarmEventList({
    startDate: dateT.value[0],
    endDate: dateT.value[1],
    deviceId: props.activeRow.deviceId,
  }).then(res => { 
    historyWarningList.value = res.data
  })
}
</script>
<style lang="scss" scoped>
.dot-line{
  width: 1px;
  flex: 1;
  border-left: 1px dotted #0070BB;
}
.dialog-title{
  color: #7DCBFF;
  span{ padding: 10px 20px; }
  .active{
    color: #fff;
    background: linear-gradient( 180deg, rgba(0,168,255,0) 0%, rgba(79,176,251,0.3) 100%);
  }
}
.dialog-warning-content{
  padding: 32px;
  .acc-left{box-sizing: border-box; padding: 10px; background: linear-gradient( 90deg, rgba(0,204,255,0) 0%, rgba(0,204,255,0.2) 52%, rgba(0,204,255,0) 100%);height: 55px;font-weight: 400;font-size: 14px;}
  .acc-right{
    box-sizing: border-box;
    flex: 1;
    h3{line-height: 28px;font-size: 14px;font-family: Microsoft YaHei, Microsoft YaHei;font-weight: bold;}
    .acc-r-event{
      padding: 12px 32px 5px 0;
    }
    .acc-r-event-list{
      width: 100%;
      background: rgba(0,204,255,0.1);
      line-height: 34px;
      padding: 0 12px;
      font-size: 14px;
      font-family: Microsoft YaHei, Microsoft YaHei;
      span{
        color: #7DCBFF;
        margin-right: 44px;
      }
    }
  }
}
</style>