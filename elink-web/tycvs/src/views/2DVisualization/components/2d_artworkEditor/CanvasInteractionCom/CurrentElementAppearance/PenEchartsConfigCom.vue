<template>
  <el-collapse-item name="7" title="图表属性">
    <el-form :model="pel_echarts_data" @submit.native.prevent>
      <el-form-item label="echarts配置">
        <el-button :icon="DArrowLeft" size="small" @click="clickCodeOptionFun"></el-button>
      </el-form-item>
      <template v-if="chartName === 'lineChart' || chartName === 'barChart' || chartName === 'basicBarChart'">
        <el-form-item>
          <el-checkbox v-model="pel_echarts_data.seriesReplaceDataName" @change="setValueCanvasMeta2dPenFun">
            series名称自动更换
          </el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="pel_echarts_data.multipleDataAxesHandle" @change="setValueCanvasMeta2dPenFun">
            单数据多轴自动处理
          </el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="pel_echarts_data.differentDimensionDataHandle" @change="setValueCanvasMeta2dPenFun">
            不同维度数据处理
          </el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="pel_echarts_data.systemVarOrFunctionHandle" @change="setValueCanvasMeta2dPenFun">
            系统变量&功能点处理
          </el-checkbox>
        </el-form-item>
      </template>

      <template v-if="chartName === 'circularProgressBar' || chartName === 'pgCircularProgressBar' || chartName === 'batteryChart' || chartName === 'progressDashboard'">
        <el-form-item label="进度值">
          <el-input type="number" v-model="pel_echarts_data.value" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
        <template v-if="chartName === 'pgCircularProgressBar'">
          <el-form-item label="刻度背景色">
            <lx-input-color-picker v-model:color="pel_echarts_data.scaleColor" @changEvent="setValueCanvasMeta2dPenFun" />
          </el-form-item>
          <el-form-item label="刻度初始色">
            <lx-input-color-picker v-model:color="pel_echarts_data.color0" @changEvent="setValueCanvasMeta2dPenFun" />
          </el-form-item>
          <el-form-item label="刻度结束色">
            <lx-input-color-picker v-model:color="pel_echarts_data.color1" @changEvent="setValueCanvasMeta2dPenFun" />
          </el-form-item>
        </template>
      </template>

    </el-form>
    <PenCodeOptionDialog v-if="penCodeOptionVisible" v-model:isVisible="penCodeOptionVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent"/>
  </el-collapse-item>
</template>

<script>
import {useStore} from "vuex";
import {DArrowLeft} from '@element-plus/icons-vue';
import PenCodeOptionDialog from "./PenCodeOptionDialog.vue";
import {reactive, toRefs, defineComponent, computed, watch, getCurrentInstance} from "vue";
import {LxInputColorPicker} from "@/components/LxComponents";

export default defineComponent({
  name: "PenEchartsConfigCom",
  components: {LxInputColorPicker, PenCodeOptionDialog},
  props: {
    echarts: {
      type: Object,
      default: () => {
        return {}
      }
    },
    chartName: {
      type: String,
      default: ""
    },
    penId: {
      type: String,
      default: ""
    },
  },
  emits: ["update:echarts"],
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      DArrowLeft,
      activeEditInfo: {},
      pel_echarts_data: {},
      penCodeOptionVisible: false,
    })

    const clickCodeOptionFun = () => {
      let echartsOption = that.pel_echarts_data?.option;
      that.activeEditInfo = JSON.parse(JSON.stringify(echartsOption ?? {}));
      that.penCodeOptionVisible = true;
    }

    const changeEvent = (data) => {
      that.pel_echarts_data.option = data.codeValue ?? {};
      setValueCanvasMeta2dPenFun();
    }

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:echarts", that.pel_echarts_data);
      canvasMeta2d.value.setValue({id: props.penId, echarts: that.pel_echarts_data});
    }

    const watchEcharts = watch(() => props.echarts, (newEcharts) => {
      if(JSON.stringify(that.pel_echarts_data) !== JSON.stringify(newEcharts)){
        that.pel_echarts_data = JSON.parse(JSON.stringify(newEcharts));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), canvasMeta2d, watchEcharts, clickCodeOptionFun, changeEvent, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped></style>