<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" disabledLoading title="创建数据补录任务" width="580px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">

          <el-form-item label="选择节点：" prop="nodeId">
            <el-select v-model="formDialog.nodeId" placeholder="请选择节点" style="width: 100%;" @change="changeNodeId">
              <el-option v-for="item in nodeArray" :key="item.id" :label="item.nodeName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <template v-if="formatType === 'y'">
            <el-form-item label="补录时间：" required>
              <el-col :span="11">
                <el-form-item prop="startTime">
                  <el-date-picker v-model="formDialog.startTime" type="year" :disabled-date="pickerOptions.disabledDate"
                                  placeholder="开始时间" :disabled="!formDialog.nodeId" @change="changeTimer"/>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <div class="text">~</div>
              </el-col>
              <el-col :span="11">
                <el-form-item prop="endTime">
                  <el-date-picker v-model="formDialog.endTime" type="year" :disabled-date="pickerOptions.disabledDate"
                                  placeholder="结束时间" :disabled="!formDialog.nodeId" @change="changeTimer" />
                </el-form-item>
              </el-col>
            </el-form-item>
          </template>

          <template v-if="formatType === 'n'">
            <el-form-item label="补录时间：" prop="pickerDate">
              <el-date-picker
                  v-model="formDialog.pickerDate" type="monthrange" :disabled-date="pickerOptions.disabledDate" range-separator="~"
                  start-placeholder="开始时间" end-placeholder="结束时间" :disabled="!formDialog.nodeId"/>
            </el-form-item>
          </template>

          <template v-if="formatType === 'w'">
            <el-form-item label="补录时间：" required>
              <el-col :span="11">
                <el-form-item prop="startTime">
                  <el-date-picker v-model="formDialog.startTime" type="week" :disabled-date="(time)=>pickerOptions.disabledDate(time,upperWeekLastDay)"
                                  format="[第] ww [周]" placeholder="开始时间" :disabled="!formDialog.nodeId" @change="changeTimer"/>
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <div class="text">~</div>
              </el-col>
              <el-col :span="11">
                <el-form-item prop="endTime">
                  <el-date-picker v-model="formDialog.endTime" type="week" :disabled-date="(time)=>pickerOptions.disabledDate(time,upperWeekLastDay)"
                                  format="[第] ww [周]" placeholder="结束时间" :disabled="!formDialog.nodeId" @change="changeTimer"/>
                </el-form-item>
              </el-col>
            </el-form-item>
          </template>

          <template v-if="formatType === 'd'">
            <el-form-item label="补录时间：" prop="pickerDate">
              <el-date-picker v-model="formDialog.pickerDate" type="daterange" :format="valueFormat" value-format="YYYY-MM-DD HH:mm"
                              :shortcuts="pickerOptions.shortcuts" :disabled-date="pickerOptions.disabledDate" range-separator="~"
                              start-placeholder="开始时间" end-placeholder="结束时间" :disabled="!formDialog.nodeId"/>
            </el-form-item>
          </template>

          <template v-if="formatType === 'h' || formatType === 'm'">
            <el-form-item label="补录时间：" prop="pickerDate">
                <el-date-picker v-model="formDialog.pickerDate" type="datetimerange" :format="valueFormat" value-format="YYYY-MM-DD HH:mm"
                                :shortcuts="pickerOptions.shortcuts" :disabled="!formDialog.nodeId" range-separator="~"
                                @calendar-change="pickerOptions.calendarChange" :disabled-date="pickerOptions.disabledDate"
                                :disabled-hours="pickerOptions.disabledHours" :disabled-minutes="pickerOptions.disabledMinutes"
                                start-placeholder="开始时间" end-placeholder="结束时间"/>
            </el-form-item>
          </template>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";
import {addRecordNodeDataById, findNodeListByDeviceId, saveNodeAddRecord} from "@/api/dataManagement/nodeAddRecording";
import {compareTimer, getCurrentMonthLastDay, getCurrentWeekLastDay, getCurrentYearLastDay, getNowDateAll, getUpperWeekLastDay, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default {
  name: "CreateNodeTask",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    recordId: {
      type: [Number,String],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateNodeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择节点"));
      } else {
        callback();
      }
    };

    const validateStartTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择开始时间"));
      } else {
        callback();
      }
    };

    const validateEndTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择结束时间"));
      } else {
        callback();
      }
    };

    const validatePickerDate = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择补录时间"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      activeNodeInfo: {},
      listLoading: false,
      dialog_visible: props.isVisible,
      upperWeekLastDay: getUpperWeekLastDay(), // 上一周的周日
      pickerOptions: pickerOptionsGthanAcTime(),

      nodeArray:[],
      formatType: "y", // y n w d h  m
      valueFormat: "YYYY-MM-DD",

      dFormat: "YYYY-MM-DD",
      hFormat: "YYYY-MM-DD HH",
      mFormat: "YYYY-MM-DD HH:mm",

      countPeriodArray:[{id:"y",name:"年",minValue:1,maxValue:1,disabled: true},{id:"n",name:"月",minValue:1,maxValue:1,disabled: true},
        {id:"w",name:"周",minValue:1,maxValue:1,disabled: true}, {id:"d",name:"日",minValue:1,maxValue:30}, {id:"h",name:"小时",minValue:1,maxValue:24},
        {id:"m",name:"分钟",minValue:1,maxValue:60}],
      rules:{
        nodeId: [{required: true, trigger: "change", validator: validateNodeId}],
        pickerDate: [{required: true, trigger: "change", validator: validatePickerDate}],
        endTime: [{required: true, trigger: "change", validator: validateEndTime}],
        startTime: [{required: true, trigger: "change", validator: validateStartTime}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          let findItem = that.nodeArray.find(item => item.id === formDialog.nodeId);
          // formDialog.storageId = findItem.storageId;  //节点存储id

          if(that.formatType === 'n' || that.formatType === 'd' || that.formatType === 'h' || that.formatType === 'm'){
            formDialog.endTime = formDialog.pickerDate[1];
            formDialog.startTime = formDialog.pickerDate[0];
          }

          if(that.formatType === 'y'){
            formDialog.endTime = getCurrentYearLastDay(new Date(formDialog.endTime));
          }

          if(that.formatType === 'n'){
            formDialog.endTime = getCurrentMonthLastDay(new Date(formDialog.endTime));
          }

          // 周时间处理 (结束时间获取最后一天的）
          if(that.formatType === 'w'){
            formDialog.startTime = new Date(formDialog.startTime).getTime() + 8.64e7;
            formDialog.endTime = getCurrentWeekLastDay(formDialog.endTime);
          }

          // 除去 时分秒的选择器
          if(that.formatType !== 'h' && that.formatType !== 'm'){
            // 设置结束时间都为 23:59:59
            let newEndTime = new Date(formDialog.endTime).getTime() + 8.64e7 - 1;

            // 比较时间默认为当前
            let compareTime = getNowDateAll('',{ isSs: false });
            // 周只能选择到上一周的周末
            if(that.formatType === 'w')compareTime = new Date(that.upperWeekLastDay).getTime() + 8.64e7 - 1;
            // 跟当前时间做比较，不能大于当前时间
            let objTimer = compareTimer(compareTime,newEndTime);
            formDialog.endTime = objTimer.startTime;
          }

          // 处理开始时间、结束时间带上时分秒
          formDialog.endTime = getNowDateAll(formDialog.endTime,{ isSs: false });
          formDialog.startTime = getNowDateAll(formDialog.startTime,{ isSs: false });
          // console.log(formDialog);
          saveNodeAddRecord({ ...formDialog,deviceId: props.recordId,addRecordState:1 }).then((res)=>{

            // 根据节点id和时间补录taos数据
            addRecordNodeDataById({ ...formDialog,deviceId:props.recordId,addRecordId: res.data }).then(()=>{
              ElMessage({type: "success", showClose: true, message: "创建成功！"});
              emit("changEvent", { type: "listArray" });
              that.dialog_visible = false;
              that.listLoading = false;
            }).catch(()=>{
              that.listLoading = false;
              emit("changEvent", { type: "listArray" });
            })
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const changeNodeId = ()=>{
      let formatType = "";
      that.activeNodeInfo = that.nodeArray.find(item => item.id === that.formDialog.nodeId);

      if(that.activeNodeInfo){
        let countPeriod = that.activeNodeInfo.countPeriod;
        if(that.activeNodeInfo.computeType === 1)countPeriod = that.activeNodeInfo.period;

        // 处理计算周期
        if(countPeriod){
          for(let i = 0;i < that.countPeriodArray.length;i++){
            if(countPeriod.indexOf(that.countPeriodArray[i].id) !== -1){
              formatType = that.countPeriodArray[i].id;
              that.valueFormat = that[formatType + 'Format'];
              break
            }
          }
        }

        that.formatType = formatType;
        that.formDialog.endTime = "";
        that.formDialog.startTime = "";
        that.formDialog.pickerDate = [];
      }
    }

    // 年 或者 月时间发生改变执行
    const changeTimer = ()=>{
      let formDialog = JSON.parse(JSON.stringify(that.formDialog));
      let objTimer = compareTimer(formDialog.startTime,formDialog.endTime);
      formDialog.endTime = objTimer.endTime;
      formDialog.startTime = objTimer.startTime;
      that.formDialog = JSON.parse(JSON.stringify(formDialog));
    }

    // 根据记录id查询计算节点列表
    const queryNodeListByRecordId = ()=>{
      findNodeListByDeviceId({ deviceId: props.recordId }).then((res)=>{
        that.nodeArray = res.data;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryNodeListByRecordId();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog,queryNodeListByRecordId,changeNodeId,changeTimer}
  }
}
</script>

<style scoped lang="scss">
.text{
  width: 100%;
  text-align: center;
}
</style>
