<template>
  <div class="nodeBasicInfo">
    <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="110px">
      <el-form-item label="节点名称：" prop="nodeName">
        <el-input v-model="formdialog.nodeName" maxlength="50" placeholder="请输入节点名称" show-word-limit type="text"></el-input>
      </el-form-item>

      <el-form-item label="节点编码：" prop="nodeCode">
        <el-input v-model="formdialog.nodeCode" maxlength="50" placeholder="请输入节点编码" show-word-limit type="text"></el-input>
      </el-form-item>

      <el-form-item label="起始时间：" prop="startTime">
        <el-date-picker v-model="formdialog.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm" format="YYYY-MM-DD HH:mm" style="width: 100%"
                        :disabled-date="(time)=> pickerOptions.disabledDate(time,formdialog.startTime)" :disabled-hours="pickerOptions.disabledHours"
                        :disabled-minutes="pickerOptions.disabledMinutes" :disabled-seconds="pickerOptions.disabledSeconds" placeholder="请选择起始时间"/>
      </el-form-item>

      <el-form-item label="保存策略：" prop="strategyType">
        <el-radio-group v-model="formdialog.strategyType">
          <el-radio v-for="item in strategyTypeArray" :key="item.id" :label="item.id">{{ item.name }}</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="统计周期：" prop="countPeriod">
        <el-col :span="12">
          <template v-if="formdialog.countPeriodType === 'm'">
            <el-select v-model="formdialog.countPeriod"  placeholder="请选择" style="width: 100%;">
              <el-option v-for="item in minuteArray" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </template>
          <template v-else>
            <el-input-number v-model="formdialog.countPeriod" :min="countPeriodMin" :max="countPeriodMax" controls-position="right"
                             :disabled="!formdialog.countPeriodType || (formdialog.countPeriodType && countPeriodDis)" style="width: 100%;"/>
          </template>
        </el-col>
        <el-col class="text-center" :span="2"><span>-</span></el-col>
        <el-col :span="10">
          <el-select v-model="formdialog.countPeriodType"  placeholder="请选择" @change="countPeriodChange">
            <el-option v-for="item in countPeriodArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-col>
      </el-form-item>

      <el-form-item label="计算周期：" prop="computePeriod">
        <el-col :span="12">
          <template v-if="formdialog.computePeriodType === 'm'">
            <el-select v-model="formdialog.computePeriod"  placeholder="请选择" style="width: 100%;">
              <el-option v-for="item in minuteArray" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </template>
          <template v-else>
            <el-input-number v-model="formdialog.computePeriod" :min="computePeriodMin" :max="computePeriodMax" controls-position="right"
                             :disabled="!formdialog.computePeriodType || (formdialog.computePeriodType && computePeriodDis)" style="width: 100%;"/>
          </template>
        </el-col>
        <el-col class="text-center" :span="2"><span>-</span></el-col>
        <el-col :span="10">
          <el-select v-model="formdialog.computePeriodType"  placeholder="请选择" @change="computePeriodChange">
            <el-option v-for="item in countPeriodArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-col>
      </el-form-item>

      <el-form-item label="单位：" prop="unit">
        <el-input v-model="formdialog.unit" maxlength="50" placeholder="请输入单位" show-word-limit type="text"></el-input>
      </el-form-item>

      <el-form-item label="备注：">
        <el-input type="textarea" v-model="formdialog.remark" maxlength="120" placeholder="请输入备注" show-word-limit ></el-input>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
import {someCharmap,notCharmap} from "@/utils/validate";
import {reactive, ref, toRefs, defineComponent} from "vue";
import {getNowDateMin,pickerOptionsDHSMTimer} from "@/utils/dateTime";

export default defineComponent({
  name: "NodeBasicInfo",
  setup() {

    const formDialogRef = ref(null);
    const validateNodeName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的节点名称"));
      } else {
        callback();
      }
    };

    const validateNodeCode = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的节点编码"));
      } else {
        callback();
      }
    };

    const validateStrategyType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择保存策略"));
      } else {
        callback();
      }
    };

    const validateStartTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择起始时间"));
      } else {
        callback();
      }
    };

    const validateCountPeriod = (rule, value, callback) => {
      if (!that.formdialog.countPeriodType) {
        callback(new Error("请选择统计周期维度"));
      } else {
        if(!that.formdialog.countPeriod && that.formdialog.countPeriod!==0 ){
          callback(new Error("请填写正确的数值"));
        } else {
          callback();
        }
      }
    };

    const validateComputePeriod = (rule, value, callback) => {
      if (!that.formdialog.computePeriodType) {
        callback(new Error("请选择计算周期维度"));
      } else {
        if(!that.formdialog.computePeriod && that.formdialog.computePeriod !== 0 ){
          callback(new Error("请填写正确的数值"));
        } else {
          callback();
        }
      }
    };

    const validateUnit = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入对应的单位"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formdialog: {
        countPeriod: 1,
        computePeriod: 1,
        startTime: getNowDateMin(5),
      },
      pickerOptions: pickerOptionsDHSMTimer(),

      // 统计周期
      countPeriodMin:0,
      countPeriodMax:999,
      countPeriodDis: true, // 周期是否可填写
      minuteArray:[1,2,5,15,30], // 计算周期，统计周期，分钟只能选择

      // 计算周期
      computePeriodMin:0,
      computePeriodMax:999,
      computePeriodDis: true, // 周期是否可填写

      strategyTypeArray:[{id:1,name:"每次存储"},{id:2,name:"变化存储"},{id:3,name:"不存储"}],
      countPeriodArray:[{id:"y",name:"年",minValue:1,maxValue:1,disabled: true},{id:"n",name:"月",minValue:1,maxValue:1,disabled: true},
         {id:"d",name:"日",minValue:1,maxValue:30}, {id:"h",name:"小时",minValue:1,maxValue:24}, {id:"m",name:"分钟",minValue:1,maxValue:60}],
    // {id:"w",name:"周",minValue:1,maxValue:1,disabled: true},

      rules: {
        unit: [{required: true, trigger: "change", validator: validateUnit}],
        nodeName: [{required: true, trigger: "change", validator: validateNodeName }],
        nodeCode: [{required: true, trigger: "change", validator: validateNodeCode }],
        startTime: [{required: true, trigger: "change", validator: validateStartTime}],
        countPeriod: [{required: true, trigger: "change", validator: validateCountPeriod}],
        strategyType: [{required: true, trigger: "change", validator: validateStrategyType}],
        computePeriod: [{required: true, trigger: "change", validator: validateComputePeriod }],
      }
    });

    const dataFeedbackFun = (formdialog = {})=>{
      delete formdialog.formulaFront;
      delete formdialog.nodeParamInfoList;

      // 处理统计周期
      if(formdialog.countPeriod){
        for(let i = 0;i < that.countPeriodArray.length;i++){
          if(formdialog.countPeriod.indexOf(that.countPeriodArray[i].id) !== -1){
            that.countPeriodDis = that.countPeriodArray[i].disabled;
            that.countPeriodMin = that.countPeriodArray[i].minValue;
            that.countPeriodMax = that.countPeriodArray[i].maxValue;
            let countPeriodArray = formdialog.countPeriod.split(that.countPeriodArray[i].id);
            formdialog.countPeriod = countPeriodArray[0];
            formdialog.countPeriodType = that.countPeriodArray[i].id;
            break
          }
        }
      }

      // 处理计算周期
      if(formdialog.computePeriod){
        for(let i = 0;i < that.countPeriodArray.length;i++){
          if(formdialog.computePeriod.indexOf(that.countPeriodArray[i].id) !== -1){
            that.computePeriodDis = that.countPeriodArray[i].disabled;
            that.computePeriodMin = that.countPeriodArray[i].minValue;
            that.computePeriodMax = that.countPeriodArray[i].maxValue;
            let countPeriodArray = formdialog.computePeriod.split(that.countPeriodArray[i].id);
            formdialog.computePeriod = countPeriodArray[0];
            formdialog.computePeriodType = that.countPeriodArray[i].id;
            break
          }
        }
      }

      that.formdialog = JSON.parse(JSON.stringify(formdialog));
    }

    const countPeriodChange = ()=>{
      let findItem = that.countPeriodArray.find(item=> item.id === that.formdialog.countPeriodType);
      that.countPeriodDis = findItem.disabled;
      that.countPeriodMin = findItem.minValue;
      that.countPeriodMax = findItem.maxValue;

      if(that.formdialog.countPeriodType === "m")that.formdialog.countPeriod = 1;

      // 超过最大设定值，修改为最大值
      if(that.formdialog.countPeriod > that.countPeriodMax){
        that.formdialog.countPeriod = that.countPeriodMax;
      }
    }

    const computePeriodChange = ()=>{
      let findItem = that.countPeriodArray.find(item=> item.id === that.formdialog.computePeriodType);
      that.computePeriodDis = findItem.disabled;
      that.computePeriodMin = findItem.minValue;
      that.computePeriodMax = findItem.maxValue;

      if(that.formdialog.computePeriodType === "m") that.formdialog.computePeriod = 1;

      // 超过最大设定值，修改为最大值
      if(that.formdialog.computePeriod > that.computePeriodMax){
        that.formdialog.computePeriod = that.computePeriodMax;
      }
    }

    return { ...toRefs(that), countPeriodChange, formDialogRef, dataFeedbackFun, computePeriodChange}
  }
})
</script>

<style lang="scss" scoped>
.nodeBasicInfo {
  width: 100%;

  .text-center{
    text-align: center;
  }

  .el-form-item:last-child{
    margin-bottom: 0;
  }
}
</style>
