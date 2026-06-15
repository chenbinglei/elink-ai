<template>
  <el-form class="pelBindRequestParamsCom" label-width="85px">
    <el-col :span="24">
      <el-form-item label="属性操作">
        <div class="flex-ai-center jc-end" style="width: 100%">
          <el-button type="warning" size="small" @click="clickOperateBut(1)">清除</el-button>
          <el-button type="primary" size="small" @click="clickOperateBut(2)">重置</el-button>
        </div>
      </el-form-item>
    </el-col>
    <el-col :span="24">
      <el-form-item label="通信方式">
        <el-radio-group v-model="request_params.type" @change="changeRequestParams">
          <el-radio v-for="item in socketTypeArray" :key="item.id" :value="item.id">{{ item.name }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-col>
    <template v-if="request_params.type === 2">
      <el-col :span="24">
        <el-form-item label="请求间隔(s)">
          <el-input-number v-model="request_params.requestInterval" controls-position="right" min="30" placeholder="请输入请求间隔" @change="changeRequestParams"/>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="时间范围">
          <el-select v-model="request_params.dateType" placeholder="请选择时间范围" @change="changeRequestParams">
            <el-option v-for="item in dateTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="数据间隔">
          <el-col :span="12">
            <template v-if="request_params.countPeriodType === 'm'">
              <el-select v-model="request_params.countPeriod" @change="changeRequestParams">
                <el-option v-for="item in minuteArray" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </template>
            <template v-else>
              <el-input-number v-model="request_params.countPeriod" :disabled="!request_params.countPeriodType || (request_params.countPeriodType && countPeriodDis)"
                               clearable :max="countPeriodMax" :min="countPeriodMin" controls-position="right" @change="changeRequestParams"/>
            </template>
          </el-col>
          <el-col :span="2">
            <span class="flex-jc-ai-center">-</span>
          </el-col>
          <el-col :span="10">
            <el-select v-model="request_params.countPeriodType" @change="countPeriodChange">
              <el-option v-for="item in countPeriodArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-col>
        </el-form-item>
      </el-col>
    </template>
  </el-form>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent, onMounted, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PelBindRequestParamsCom",
  props: {
    requestParams: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:requestParams"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      countPeriodMin: 0,
      countPeriodMax: 999,
      countPeriodDis: true, // 周期是否可填写
      minuteArray: [1, 2, 5, 15, 30], // 计算周期，统计周期，分钟只能选择
      request_params: props.requestParams,
      old_request_params: props.requestParams,
      socketTypeArray: [{id: 2, name: "http"}, {id: 1, name: "websocket"}],
      countPeriodArray: [
        {id: "y", name: "年", minValue: 1, maxValue: 1, disabled: true},
        {id: "n", name: "月", minValue: 1, maxValue: 1, disabled: true},
        {id: "d", name: "天", minValue: 1, maxValue: 30},
        {id: "h", name: "小时", minValue: 1, maxValue: 24},
        {id: "m", name: "分钟", minValue: 1, maxValue: 60}],
      dateTypeArray: [
        {id: 0, name: "当日"},
        {id: 1, name: "昨日"},
        {id: 2, name: "近七天"},
        {id: 3, name: "近30天"},
        {id: 4, name: "当月"},
        {id: 5, name: "上月"},
        {id: 6, name: "本年"}
      ],
    })

    const countPeriodChange = (isChange = true) => {
      let findItem = that.countPeriodArray.find(item => item.id === that.request_params.countPeriodType);
      if(findItem){
        that.countPeriodDis = findItem.disabled;
        that.countPeriodMin = findItem.minValue;
        that.countPeriodMax = findItem.maxValue;
      }

      if (that.request_params.countPeriodType === "m") that.request_params.countPeriod = 1;

      // 超过最大设定值，修改为最大值
      if (that.request_params.countPeriod > that.countPeriodMax) {
        that.request_params.countPeriod = that.countPeriodMax;
      }

      if(isChange) changeRequestParams();
    }

    const changeRequestParams = () => {
      // console.log(that.request_params);
      let request_params = JSON.parse(JSON.stringify(that.request_params));
      emit("update:requestParams", request_params);
    }

    const clickOperateBut = (operateType)=>{
      if(operateType === 1) that.request_params = {};
      if(operateType === 2) that.request_params = JSON.parse(JSON.stringify(that.old_request_params));
      changeRequestParams();
    }

    const watchRequestParams = watch(() => props.requestParams, (mewRequestParams) => {
      if(JSON.stringify(mewRequestParams) !== JSON.stringify(that.request_params)){
        that.request_params = JSON.parse(JSON.stringify(mewRequestParams));
        countPeriodChange(false);
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), watchRequestParams, countPeriodChange, changeRequestParams, clickOperateBut}
  }
})

</script>

<style lang="scss" scoped>
.pelBindRequestParamsCom {
  padding: 0 12px;
  box-sizing: border-box;

  .el-input-number {
    width: 100%;
  }
}
</style>