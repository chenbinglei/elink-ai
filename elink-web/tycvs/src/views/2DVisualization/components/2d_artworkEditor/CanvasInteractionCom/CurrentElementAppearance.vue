<template>
  <el-collapse v-model="activeNames" class="currentElementAppearance">
    <div style="padding: 0 12px;box-sizing: border-box">
      <el-form :model="activePelDate" label-width="auto" disabled>
        <el-form-item label="ID">
          <el-input v-model="activePelDate.id" size="small"></el-input>
        </el-form-item>
        <el-form-item label="Name">
          <el-input v-model="activePelDate.name" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <PenAdditionalAttrConfigCom v-model:activePelDate="activePelDate" />
    <!--    图元大屏对齐-->
    <PenLargeScreenAlignmentCom :activePelDate="activePelDate" @changeEvent="findPenIdConfigFun" />
    <template v-if="!loading">
      <!--      echarts图元属性控制-->
      <template v-if="activePelDate.name === 'echartsComponent' || activePelDate.name === 'echarts'">
        <PenEchartsConfigCom v-model:echarts="activePelDate.echarts" :chartName="activePelDate.chartName" :penId="activePelDate.id" />
      </template>
      <!--      时间图元属性控制-->
      <template v-if="activePelDate.name === 'time' || activePelDate.name === 'countdown'">
        <PenTimeConfigCom v-if="activePelDate.name === 'time' || activePelDate.name === 'countdown'" v-model:activePelDate="activePelDate" />
      </template>
      <template v-if="activePelDate.name === 'video' || activePelDate.name ==='flvPlayerDom' || activePelDate.name ==='rtspPlayerDom'">
        <PenAudioAndVideoConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />
      </template>
      <!--      网页图元属性控制-->
      <PenIframeConfigCom v-if="activePelDate.name ==='iframe'" v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />
       <!--      switch图元属性控制-->
      <PenSwitchConfigCom v-if="activePelDate.name ==='switch'" v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />
      <!--      radio图元属性控制-->
      <PenRadioConfigCom v-if="activePelDate.name ==='radio'" v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />

      <template v-if="activePelDate.name !== 'echartsComponent' && activePelDate.name !== 'echarts'">
        <!--        外观属性-->
        <PenAppearanceConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" @changeRetrieve="findPenIdConfigFun" />
        <template v-if="activePelDate.name !== 'image' && activePelDate.name !== 'gif' && activePelDate.type !== 1">
          <!--          文字属性-->
          <PenTextConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />
          <!--          进度属性设置-->
          <PenProgressConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun" />
        </template>
        <!--        图片属性配置-->
        <template v-if="activePelDate.name === 'image' || activePelDate.name === 'gif'">
          <PenImageGifConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun"/>
        </template>
      </template>
      <!--      字体图标配置-->
      <PenIconConfigCom v-if="activePelDate.iconFamily" v-model:activePelDate="activePelDate"></PenIconConfigCom>
    </template>
    <PenAdditionalConfigCom v-model:activePelDate="activePelDate" @changeEvent="setValueCanvasMeta2dPenFun"></PenAdditionalConfigCom>
  </el-collapse>
</template>

<script>
import {useStore} from "vuex";
import {deepClone} from "@meta2d/core";
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import {PenEchartsConfigCom, PenTimeConfigCom, PenLargeScreenAlignmentCom, PenAdditionalAttrConfigCom, PenAudioAndVideoConfigCom, PenIframeConfigCom,
  PenIconConfigCom, PenAdditionalConfigCom, PenImageGifConfigCom, PenProgressConfigCom, PenTextConfigCom, PenAppearanceConfigCom, PenSwitchConfigCom,
  PenRadioConfigCom,
} from "./CurrentElementAppearance/index";

export default defineComponent({
  name: "currentElementAppearance",
  components: {LxInputColorPicker, PenEchartsConfigCom, PenTimeConfigCom, PenLargeScreenAlignmentCom, PenAdditionalAttrConfigCom, PenAudioAndVideoConfigCom,
    PenIframeConfigCom, PenIconConfigCom, PenAdditionalConfigCom, PenImageGifConfigCom, PenProgressConfigCom, PenTextConfigCom, PenAppearanceConfigCom,
    PenSwitchConfigCom,PenRadioConfigCom},
  setup() {

    const store = useStore();
    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return store.state.meta2d.current_active_pel_list;
    });

    const current_active_pel_num = computed(() => {
      return store.state.meta2d.current_active_pel_num;
    });

    const delete_field_list = computed(() => {
      return store.state.meta2d.setValueDeleteFieldList;
    });

    const that = reactive({
      loading: true,
      activePelDate: {},
      activeNames: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"],
    })

    const findPenIdConfigFun = ()=>{
      let pen_id = current_active_pel_list.value[0];
      let activePelDate = canvasMeta2d.value.findOne(pen_id);
      const penRect = canvasMeta2d.value.getPenRect(activePelDate);
      let clonePelDate = deepClone(activePelDate ? activePelDate : {});
      that.activePelDate = JSON.parse(JSON.stringify({...deepClone(clonePelDate), ...penRect}));
      // console.log(that.activePelDate);
      // 额外字段需单独处理
      that.loading = false;
    };

    const setValueCanvasMeta2dPenFun = ()=>{
      // 删除对应的视图数据
      let active_pel_date = JSON.parse(JSON.stringify(that.activePelDate));
      for(let i = 0;i < delete_field_list.value.length;i++) delete active_pel_date[delete_field_list.value[i]];
      canvasMeta2d.value.setValue(active_pel_date);
    }

    const watchCurrentActivePelList = watch([() => current_active_pel_list, () => current_active_pel_num], ([newCurrentActivePelList, newCurrentActivePelNum]) => {
      if(newCurrentActivePelList.value && newCurrentActivePelList.value.length) findPenIdConfigFun();
    }, {deep: true, immediate: true})

    return {...toRefs(that), canvasMeta2d, current_active_pel_list, watchCurrentActivePelList, current_active_pel_num, delete_field_list, setValueCanvasMeta2dPenFun, findPenIdConfigFun}
  }
})
</script>

<style lang="scss" scoped>
.currentElementAppearance {
  border: none;
  padding: 12px 0;
  box-sizing: border-box;

  :deep(.el-collapse-item){
    padding: 0;

    .el-collapse-item__header {
      font-size: 14px;
      font-weight: bold;
      color: var(--color-title);
      padding-left: 14px;
      box-sizing: border-box;
    }

    .el-collapse-item__content{
      padding-left: 16px;
      padding-right: 12px;
      padding-bottom: 12px;
      box-sizing: border-box;

      .el-form-item {
        margin-bottom: 4px;
      }

      .innerText {
        padding-right: 4px;
      }

      .el-checkbox {
        --el-checkbox-font-size: 12px;
        --el-checkbox-text-color: var(--color);
      }
    }
  }
}
</style>