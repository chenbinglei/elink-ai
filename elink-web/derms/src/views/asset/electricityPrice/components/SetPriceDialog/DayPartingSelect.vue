<template>
  <div class="dayPartingSelect">
    <div class="content_top">
      <template v-if="!isResetTime">
        <el-button :icon="RefreshRight" type="primary" @click="clickResetTimeBut">重置时段</el-button>
      </template>
      <template v-else>
        <el-button :icon="CloseBold" @click="clickCancelBut">取消</el-button>
        <el-button :icon="Select" type="primary" @click="clickEnterBut">确定</el-button>
      </template>
    </div>
    <div v-if="isResetTime" class="content_bottom">

      <div class="sl_timer_top">
        <template v-for="(item, index) in timeListArray" :key="index">
          <div v-if="!item.isHalfTimer" class="time_li">{{ item.number }}</div>
        </template>
      </div>
      <div class="sl_timer_bottom">
        <div class="sl_timer_bottom_left">
          <template v-for="(item, index) in periodTypeArray" :key="index">
            <div :class="'period_type' + item.id" class="period_type flex-jc-ai-center">{{ item.name }}</div>
          </template>
        </div>
        <div class="sl_timer_bottom_right">
          <template v-for="(item, index) in periodTypeArray" :key="index">
            <div ref="timerListRef" class="timer_content_list" @mouseup.stop="mouseupCheckTimerFun(item.id)"
              @mouseleave="mouseupCheckTimerFun(item.id)"
              @mousemove.prevent.stop="mousemoveCheckTimerFun(item.id, $event)"
              @mousedown.stop="mousedownCheckTimerFun(item.id, $event)">
              <template v-for="(time, ti) in timeListArray" :key="ti">
                <div :time-id="time.id" class="timer_list_li isClick"
                  @click.stop="clickCheckTimerFun(item.id, time.id)">
                </div>
              </template>

              <!--              选中的时段数据列表-->
              <template v-if="formCheckTime[item.id] && formCheckTime[item.id].length">
                <template v-for="(li, k) in formCheckTime[item.id]">
                  <div :class="'timer_sl_list_li' + item.id" :style="{ width: li.width, left: li.left }"
                    class="timer_sl_list_li flex-jc-ai-center">
                    <span class="start_end_time noSelect">{{ li.startTime }}～{{ li.endTime }}</span>
                  </div>
                </template>
              </template>

              <!--              滑动选中的数据列表-->
              <template v-if="mousedownFormCheckTime[item.id] && mousedownFormCheckTime[item.id].length">
                <template v-for="(li, k) in mousedownFormCheckTime[item.id]">
                  <div :style="{ width: li.width, left: li.left }"
                    class="timer_sl_list_li flex-jc-ai-center mouse_timer_sl_list_li">
                    <span class="start_end_time noSelect">{{ li.startTime }}～{{ li.endTime }}</span>
                  </div>
                </template>
              </template>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { ElMessage } from "element-plus";
import { Select, RefreshRight, CloseBold } from '@element-plus/icons-vue';
import { getCurrentInstance, reactive, toRefs, defineComponent, ref, onMounted, nextTick } from "vue";

export default defineComponent({
  name: "DayPartingSelect",
  props: {
    periodTimeList: {
      type: Array,
      default: () => []
    }
  },
  emits: ["update:periodTimeList"],
  setup () {
    const { emit } = getCurrentInstance();

    const timerListRef = ref([]);
    const that = reactive({
      Select,
      CloseBold,
      RefreshRight,
      formCheckTime: {},
      periodTimeList: [], // 选择的时段数据
      isResetTime: false,
      oldFormCheckTime: {},
      oldPeriodTimeList: [], // 选择的时段数据

      timeListArray: [
        { id: 1, startTime: "00:00", endTime: "00:30", actualEndTime: "00:29", number: "00" },
        { id: 2, startTime: "00:30", endTime: "01:00", actualEndTime: "00:59", isHalfTimer: true },
        { id: 3, startTime: "01:00", endTime: "01:30", actualEndTime: "01:29", number: "01" },
        { id: 4, startTime: "01:30", endTime: "02:00", actualEndTime: "01:59", isHalfTimer: true },
        { id: 5, startTime: "02:00", endTime: "02:30", actualEndTime: "02:29", number: "02" },
        { id: 6, startTime: "02:30", endTime: "03:00", actualEndTime: "02:59", isHalfTimer: true },
        { id: 7, startTime: "03:00", endTime: "03:30", actualEndTime: "03:29", number: "03" },
        { id: 8, startTime: "03:30", endTime: "04:00", actualEndTime: "03:59", isHalfTimer: true },
        { id: 9, startTime: "04:00", endTime: "04:30", actualEndTime: "04:29", number: "04" },
        { id: 10, startTime: "04:30", endTime: "05:00", actualEndTime: "04:59", isHalfTimer: true },
        { id: 11, startTime: "05:00", endTime: "05:30", actualEndTime: "05:29", number: "05" },
        { id: 12, startTime: "05:30", endTime: "06:00", actualEndTime: "05:59", isHalfTimer: true },
        { id: 13, startTime: "06:00", endTime: "06:30", actualEndTime: "06:29", number: "06" },
        { id: 14, startTime: "06:30", endTime: "07:00", actualEndTime: "06:59", isHalfTimer: true },
        { id: 15, startTime: "07:00", endTime: "07:30", actualEndTime: "07:29", number: "07" },
        { id: 16, startTime: "07:30", endTime: "08:00", actualEndTime: "07:59", isHalfTimer: true },
        { id: 17, startTime: "08:00", endTime: "08:30", actualEndTime: "08:29", number: "08" },
        { id: 18, startTime: "08:30", endTime: "09:00", actualEndTime: "08:59", isHalfTimer: true },
        { id: 19, startTime: "09:00", endTime: "09:30", actualEndTime: "09:29", number: "09" },
        { id: 20, startTime: "09:30", endTime: "10:00", actualEndTime: "09:59", isHalfTimer: true },
        { id: 21, startTime: "10:00", endTime: "10:30", actualEndTime: "10:29", number: "10" },
        { id: 22, startTime: "10:30", endTime: "11:00", actualEndTime: "10:59", isHalfTimer: true },
        { id: 23, startTime: "11:00", endTime: "11:30", actualEndTime: "11:29", number: "11" },
        { id: 24, startTime: "11:30", endTime: "12:00", actualEndTime: "11:59", isHalfTimer: true },
        { id: 25, startTime: "12:00", endTime: "12:30", actualEndTime: "12:29", number: "12" },
        { id: 26, startTime: "12:30", endTime: "13:00", actualEndTime: "12:59", isHalfTimer: true },
        { id: 27, startTime: "13:00", endTime: "13:30", actualEndTime: "13:29", number: "13" },
        { id: 28, startTime: "13:30", endTime: "14:00", actualEndTime: "13:59", isHalfTimer: true },
        { id: 29, startTime: "14:00", endTime: "14:30", actualEndTime: "14:29", number: "14" },
        { id: 30, startTime: "14:30", endTime: "15:00", actualEndTime: "14:59", isHalfTimer: true },
        { id: 31, startTime: "15:00", endTime: "15:30", actualEndTime: "15:29", number: "15" },
        { id: 32, startTime: "15:30", endTime: "16:00", actualEndTime: "15:59", isHalfTimer: true },
        { id: 33, startTime: "16:00", endTime: "16:30", actualEndTime: "16:29", number: "16" },
        { id: 34, startTime: "16:30", endTime: "17:00", actualEndTime: "16:59", isHalfTimer: true },
        { id: 35, startTime: "17:00", endTime: "17:30", actualEndTime: "17:29", number: "17" },
        { id: 36, startTime: "17:30", endTime: "18:00", actualEndTime: "17:59", isHalfTimer: true },
        { id: 37, startTime: "18:00", endTime: "18:30", actualEndTime: "18:29", number: "18" },
        { id: 38, startTime: "18:30", endTime: "19:00", actualEndTime: "18:59", isHalfTimer: true },
        { id: 39, startTime: "19:00", endTime: "19:30", actualEndTime: "19:29", number: "19" },
        { id: 40, startTime: "19:30", endTime: "20:00", actualEndTime: "19:59", isHalfTimer: true },
        { id: 41, startTime: "20:00", endTime: "20:30", actualEndTime: "20:29", number: "20" },
        { id: 42, startTime: "20:30", endTime: "21:00", actualEndTime: "20:59", isHalfTimer: true },
        { id: 43, startTime: "21:00", endTime: "21:30", actualEndTime: "21:29", number: "21" },
        { id: 44, startTime: "21:30", endTime: "22:00", actualEndTime: "21:59", isHalfTimer: true },
        { id: 45, startTime: "22:00", endTime: "22:30", actualEndTime: "22:29", number: "22" },
        { id: 46, startTime: "22:30", endTime: "23:00", actualEndTime: "22:59", isHalfTimer: true },
        { id: 47, startTime: "23:00", endTime: "23:30", actualEndTime: "23:29", number: "23" },
        { id: 48, startTime: "23:30", endTime: "23:59", actualEndTime: "23:59", isHalfTimer: true }
      ],
      // , {id: 5, name: "深谷"}
      periodTypeArray: [{ id: 1, name: "尖" }, { id: 2, name: "峰" }, { id: 3, name: "平" }, { id: 4, name: "谷" }, { id: 5, name: "深谷" }],

      mouseTimerId: [], // 移动中出发的数组
      isMousedown: false,
      mousedownPeriodType: "",
      mousedownCheckItem: null,
      mousedownFormCheckTime: {},
      mousedownPeriodTimeList: [],
    });

    const clickEnterBut = (insertionMoney = false) => {
      let slTimeNumber = 0;
      let periodTimeList = [];



      for (let key in that.formCheckTime) {
        if (that.formCheckTime[key] && that.formCheckTime[key].length) {
          let data = { periodType: key, children: that.formCheckTime[key] };
          // 复制时段使用
          if (insertionMoney) {
            let findItem = that.periodTimeList.find(item => item.periodType === Number(key));
            if (findItem) {
              data.electMoney = findItem.electMoney;
              data.serviceMoney = findItem.serviceMoney;
            }
          }
          periodTimeList.push(data);
        }
      }

      for (let i = 0; i < that.periodTimeList.length; i++) {
        slTimeNumber += that.periodTimeList[i].children.length;
      }
      if (slTimeNumber !== that.timeListArray.length) {
        ElMessage({ type: "error", showClose: true, message: "请确认时段完整性！" });
        return;
      }

      that.isResetTime = false;
      emit("update:periodTimeList", periodTimeList);
    };

    // 点击选中某个时间段
    const clickCheckTimerFun = (periodType, timerId) => {
      // 添加默认数据
      let findPeriodIndex = that.periodTimeList.findIndex(item => item.periodType === periodType);
      if (findPeriodIndex === -1) that.periodTimeList.push({ periodType: periodType, children: [] });
      let activeClickTime = that.timeListArray.find(item => item.id === timerId); // 当前点击的时段信息
      // console.log(activeClickTime);

      for (let i = 0; i < that.periodTimeList.length; i++) {
        let findIndex = that.periodTimeList[i].children.findIndex(item => item.id === timerId);
        // 操作的当前项
        if (that.periodTimeList[i].periodType === periodType) {
          if (findIndex === -1) {
            that.periodTimeList[i].children.push(activeClickTime);
          } else {
            that.periodTimeList[i].children.splice(findIndex, 1);
          }
        } else {
          if (findIndex !== -1) that.periodTimeList[i].children.splice(findIndex, 1);
        }
        that.periodTimeList[i].children.sort((a, b) => a.id - b.id);
      }

      createDomElementFun();
      // console.log(that.periodTimeList);
    };



    const mousedownCheckTimerFun = (periodType) => {
      that.mouseTimerId = [];
      that.isMousedown = true;
      that.mousedownPeriodType = periodType;
      let findPeriodIndex = that.mousedownPeriodTimeList.findIndex(item => item.periodType === periodType);
      if (findPeriodIndex === -1) that.mousedownPeriodTimeList.push({ periodType: periodType, children: [] });
      // that.mousedownCheckItem = JSON.parse(JSON.stringify(activeClickTime));
    };

    // 移动中执行的
    const mousemoveCheckTimerFun = (periodType, event) => {
      
      if (!that.isMousedown || that.mousedownPeriodType !== periodType) {
        console.log("mousedownCheckTimerFun****", periodType,that.isMousedown);
        that.isMousedown = false;
        return;
      }
console.log("mousedownCheckTimerFun", periodType,that.isMousedown);
      let timerId = event.target.getAttribute('time-id');
      // console.log(timerId);
      // console.log('periodType' + periodType);
      let findTimerIndex = that.mouseTimerId.findIndex(item => item === timerId);
      if (findTimerIndex === -1) that.mouseTimerId.push(timerId);

      let activeClickTime = that.timeListArray.find(item => item.id === Number(timerId)); // 当前点击的时段信息
      if (activeClickTime) {
        for (let i = 0; i < that.mousedownPeriodTimeList.length; i++) {
          let findIndex = that.mousedownPeriodTimeList[i].children.findIndex(item => item.id === Number(timerId));
          if (findIndex === -1) that.mousedownPeriodTimeList[i].children.push(activeClickTime);
          that.mousedownPeriodTimeList[i].children.sort((a, b) => a.id - b.id);
        }
      }
      createDomElementFun("mousedownPeriodTimeList", "mousedownFormCheckTime");
    };

    // // 框选结束执行
    // const mouseupCheckTimerFun = () => {
    //   // 说明只操作了一个，可执行点击事件里的方法
    //   if (that.mouseTimerId.length <= 1) {
    //     that.isMousedown = false;
    //     that.mousedownFormCheckTime = {};
    //     that.mousedownPeriodTimeList = [];
    //     return;
    //   }

    //   that.mousedownFormCheckTime = {};
    //   let findIndex = that.periodTimeList.findIndex(item => item.periodType === that.mousedownPeriodType);
    //   let active_sl_time_list = that.mousedownPeriodTimeList.find(item => item.periodType === that.mousedownPeriodType);

    //   if (findIndex === -1) that.periodTimeList.push(active_sl_time_list);


    //   for (let i = 0; i < that.periodTimeList.length; i++) {
    //     for (let j = 0; j < active_sl_time_list.children.length; j++) {
    //       let find_dl_index = that.periodTimeList[i].children.findIndex(item => item.id === active_sl_time_list.children[j].id);

    //       // 如果操作当前的查看是否存在该时段，存在则不添加、不存在则添加
    //       if (that.periodTimeList[i].periodType === that.mousedownPeriodType) {
    //         if (find_dl_index === -1) that.periodTimeList[i].children.push(active_sl_time_list.children[j]);
    //       } else {
    //         // 其他时段 数据，存在即删除
    //         if (find_dl_index !== -1) that.periodTimeList[i].children.splice(find_dl_index, 1);
    //       }
    //     }
    //     that.periodTimeList[i].children.sort((a, b) => a.id - b.id);
    //   }

    //   createDomElementFun();
    //   that.isMousedown = false;
    //   that.mousedownPeriodTimeList = [];
    //   console.log(that.periodTimeList,'periodTimeList');
    // };
    const mouseupCheckTimerFun = () => {
      console.log('正在被调用******');
      if (that.mouseTimerId.length <= 1) {
        that.isMousedown = false;
        that.mousedownFormCheckTime = {};
        that.mousedownPeriodTimeList = [];
        return;
      }

      that.mousedownFormCheckTime = {};
      console.log('mouseupCheckTimerFun',that.mousedownPeriodTimeList,that.mousedownPeriodType);
      let active_sl_time_list = that.mousedownPeriodTimeList.find(item => item.periodType === that.mousedownPeriodType);
      if (!active_sl_time_list) {
        that.isMousedown = false;
        that.mousedownPeriodTimeList = [];
        return;
      }

      let findIndex = that.periodTimeList.findIndex(item => item.periodType === that.mousedownPeriodType);
      if (findIndex === -1) {
        that.periodTimeList.push({
          periodType: that.mousedownPeriodType,
          children: []
        });
        findIndex = that.periodTimeList.length - 1;
      }
      console.log('active_sl_time_list:', active_sl_time_list);
      console.log('periodTimeList before merge:', that.periodTimeList);
      for (let i = 0; i < that.periodTimeList.length; i++) {
        for (let j = 0; j < active_sl_time_list.children.length; j++) {
          let child = active_sl_time_list.children[j];
          let find_dl_index = that.periodTimeList[i].children.findIndex(item => item.id === child.id);

          if (that.periodTimeList[i].periodType === that.mousedownPeriodType) {
            if (find_dl_index === -1) {
              that.periodTimeList[i].children.push(child);
            }
          } else {
            if (find_dl_index !== -1) {
              that.periodTimeList[i].children.splice(find_dl_index, 1);
            }
          }
        }
        that.periodTimeList[i].children.sort((a, b) => a.id - b.id);
      }

      createDomElementFun();
      that.isMousedown = false;
      that.mousedownPeriodTimeList = [];
      console.log("periodTimeList after merge:", that.periodTimeList);
    };
    // 根据选中的数据进行 时段合并 并计算don元素的数据
    const createDomElementFun = (fieldName = "periodTimeList", formFieldName = "formCheckTime", isClickEnterBut = false) => {
      for (let i = 0; i < that[fieldName].length; i++) {
        let periodDom = timerListRef.value[that[fieldName][i].periodType - 1];
        let domChildrenList = periodDom.querySelectorAll('.timer_list_li');
        // 时段合并处理
        let periodTimeList = [];
        if (that[fieldName][i].children && that[fieldName][i].children.length) {
          let startTime = "", actualEndTime = "", recordEndTime = ""; // 开始结束时间
          for (let j = 0; j < that[fieldName][i].children.length; j++) {

            recordEndTime = that[fieldName][i].children[j].endTime; // 记录当前的结束时间
            actualEndTime = that[fieldName][i].children[j].actualEndTime;
            if (!startTime) startTime = that[fieldName][i].children[j].startTime;
            let isExistEndTime = that[fieldName][i].children.find(item => item.startTime === recordEndTime);
            // console.log(isExistEndTime);

            if (!isExistEndTime) {
              // 设置对应时段的 宽度 以及 left
              let startTimeIndex = that.timeListArray.findIndex(item => item.startTime === startTime);
              let endTimeIndex = that.timeListArray.findIndex(item => item.startTime === that[fieldName][i].children[j].endTime);

              // 处理点击最后一项报错问题
              let offsetWidth = 0;
              if (endTimeIndex === -1) {
                endTimeIndex = that.timeListArray.length - 1;
                offsetWidth = domChildrenList[endTimeIndex].offsetWidth;
              }
              let leftNum = domChildrenList[startTimeIndex].offsetLeft;
              let widthNum = domChildrenList[endTimeIndex].offsetLeft - leftNum + offsetWidth;
              periodTimeList.push({
                startTime: startTime,
                endTime: recordEndTime,
                actualEndTime: actualEndTime, // 实际结束时间
                width: widthNum + 'px',
                left: leftNum + 'px'
              });
              startTime = "";
            }

          }
        }

        that[formFieldName][that[fieldName][i].periodType] = periodTimeList;
        // that.periodTimeList[i].periodTimeList = JSON.parse(JSON.stringify(periodTimeList));
      }

      if (isClickEnterBut) clickEnterBut(true);
    };

    const echoPeriodTimeConfigFun = (priceConfigList) => {
      let periodTimeList = [];
      for (let i = 0; i < priceConfigList.length; i++) {
        // console.log(priceConfigList[i]);
        let findIndex = periodTimeList.findIndex(item => item.periodType === priceConfigList[i].periodType);
        // 获取对应的时段信息
        let endTimeIndex = that.timeListArray.findIndex(item => (item.actualEndTime === priceConfigList[i].endTime) || (item.endTime === priceConfigList[i].endTime));
        let startTimeIndex = that.timeListArray.findIndex(item => item.startTime === priceConfigList[i].startTime);
        let timer_list_array = that.timeListArray.slice(startTimeIndex, endTimeIndex + 1);

        if (findIndex === -1) {
          periodTimeList.push({
            children: timer_list_array,
            periodType: priceConfigList[i].periodType,
            electMoney: priceConfigList[i].electMoney,
            serviceMoney: priceConfigList[i].serviceMoney,
          });
        } else {
          periodTimeList[findIndex].children = [...timer_list_array, ...periodTimeList[findIndex].children];
        }
      }

      that.isResetTime = true;
      that.periodTimeList = JSON.parse(JSON.stringify(periodTimeList));
      nextTick(() => {
        // console.log(periodTimeList);
        createDomElementFun("periodTimeList", "formCheckTime", true);
      });
    };

    const clickResetTimeBut = () => {
      that.oldFormCheckTime = JSON.parse(JSON.stringify(that.formCheckTime));
      that.oldPeriodTimeList = JSON.parse(JSON.stringify(that.periodTimeList));
      that.isResetTime = true;
    };

    const clickCancelBut = () => {
      that.formCheckTime = JSON.parse(JSON.stringify(that.oldFormCheckTime));
      that.periodTimeList = JSON.parse(JSON.stringify(that.oldPeriodTimeList));
      that.isResetTime = false;
    };

    onMounted(() => {
    });

    return {
      ...toRefs(that), clickResetTimeBut, clickCancelBut, clickCheckTimerFun, timerListRef, clickEnterBut, mousedownCheckTimerFun,
      mouseupCheckTimerFun, mousemoveCheckTimerFun, echoPeriodTimeConfigFun
    };
  }
});
</script>
<style lang="scss" scoped>
.dayPartingSelect {
  margin-bottom: 16px;

  .content_top {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .content_bottom {
    padding: 12px;
    border-radius: 8px;
    transition: all .28s;
    background: #00437d26;
    box-sizing: border-box;

    .sl_timer_top {
      display: flex;
      align-items: center;
      padding-left: 46px;
      box-sizing: border-box;

      .time_li {
        flex: 1;
        font-size: 12px;
        color: #ffffff99;
      }
    }

    .sl_timer_bottom {
      display: flex;
      align-items: center;

      .sl_timer_bottom_left {

        .period_type {
          width: 44px;
          height: 32px;
          margin-bottom: 6px;
          border-radius: 8px 0 0 8px;
          color: #FFFFFF;
          font-size: 14px;

          &:last-child {
            margin-bottom: 0;
          }
        }

        .period_type1 {
          background: #FB6868;
        }

        .period_type2 {
          background: #FD9449;
        }

        .period_type3 {
          background: #56ADF7;
        }

        .period_type4 {
          background: #6DCF36;
        }

        .period_type5 {
          background: #36CFC2;
        }
      }

      .sl_timer_bottom_right {
        flex: 1;
        //overflow: hidden;
        border-radius: 8px;
        box-sizing: border-box;
        background: #ffffff0d;
        border: 1px solid #ffffff33;

        .timer_content_list {
          width: 100%;
          height: 38px;
          display: flex;
          align-items: center;
          position: relative;
          box-sizing: border-box;
          border-bottom: 1px solid #ffffff33;

          .timer_list_li {
            flex: 1;
            z-index: 10;
            height: 100%;
            cursor: pointer;
            box-sizing: border-box;
            border-right: 1px solid #ffffff33;

            &:last-child {
              border-right: none;
            }

            &:nth-of-type(odd) {
              border-right: none;
            }
          }

          .timer_sl_list_li {
            position: absolute;
            left: 0;
            //top: 0;
            //width: 0;
            height: 100%;
            border-radius: 8px;

            .start_end_time {
              font-size: 10px;
              white-space: nowrap;
              position: absolute;
            }
          }

          .timer_sl_list_li1 {
            color: #FB6868;
            box-sizing: border-box;
            border: 1px solid #FB6868;
            background: rgba(251, 104, 104, .1);
          }

          .timer_sl_list_li2 {
            color: #FD9449;
            border: 1px solid #FD9449;
            background: rgba(253, 148, 73, 0.1);
          }

          .timer_sl_list_li3 {
            color: #56ADF7;
            border: 1px solid #56ADF7;
            background: rgba(86, 173, 247, 0.1);
          }

          .timer_sl_list_li4 {
            color: #6DCF36;
            border: 1px solid #6DCF36;
            background: rgba(109, 207, 54, .1)
          }

          .timer_sl_list_li5 {
            color: #36CFC2;
            border: 1px solid #36CFC2;
            background: rgba(54, 207, 194, .1)
          }

          .mouse_timer_sl_list_li {
            color: #18FBC9;
            border: 1px solid #18FBC9;
            background: rgba(24, 251, 201, .1);
          }


          &:last-child {
            border-bottom: none;
          }
        }
      }
    }
  }
}
</style>