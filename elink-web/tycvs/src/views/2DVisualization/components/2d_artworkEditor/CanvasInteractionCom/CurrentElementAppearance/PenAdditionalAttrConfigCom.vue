<template>
  <el-collapse-item name="5" title="属性配置">
    <el-form :model="active_pel_date" label-width="auto" class="content_item_body">
      <el-form-item label="别名">
        <el-input v-model="active_pel_date.chineseName" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
      </el-form-item>
      <el-form-item v-if="active_pel_date.name !== 'gif'" label="画布层">
        <el-select v-model="active_pel_date.canvasLayer" size="small" @change="setValueCanvasMeta2dPenFun">
          <template v-for="ts in canvasLayerArray" :key="ts.id">
            <el-option :disabled="active_pel_date.name !== 'image' && (ts.id === 2 || ts.id === 4) " :label="ts.name" :value="ts.id"></el-option>
          </template>
        </el-select>
      </el-form-item>
      <el-form-item v-if="active_pel_date.name === 'gif'" label="z-index">
        <el-input type="number" v-model="active_pel_date.zIndex" min="0" placeholder="默认5" size="small" @change="setValueCanvasMeta2dPenFun"/>
      </el-form-item>
      <el-form-item>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.x" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">X</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.y" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">Y</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-form-item>
      <el-form-item>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.width" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">W</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.height" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">H</div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-form-item>
      <el-form-item>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.rotate" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">
                  <span class="iconfont icon-rotate-right"></span>
                </div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="11">
          <el-form-item>
            <el-input v-model="active_pel_date.borderRadius" size="small" type="number" @change="setValueCanvasMeta2dPenFun">
              <template #prefix>
                <div class="innerText">
                  <span class="iconfont icon-yuanjiao"></span>
                </div>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-form-item>
      <el-form-item label="透明度">
        <div class="flex-ai-center" style="width: 100%;">
          <el-slider v-model="active_pel_date.globalAlpha" :max="1" :min="0" :step="0.1" class="flex-all" size="small" @change="setValueCanvasMeta2dPenFun"></el-slider>
          <div class="globalAlpha">{{ active_pel_date.globalAlpha }}</div>
        </div>
      </el-form-item>
      <el-form-item v-if="active_pel_date.name === 'combine'" label="状态">
        <el-select v-model="active_pel_date.showChild" size="small" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="(childItem,childIndex) in active_pel_date.children" :key="childItem" :label="childIndex" :value="childIndex">
            <template #default>状态{{ childIndex }}</template>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </el-collapse-item>
</template>

<script>
import {useStore} from "vuex";
import {deepClone} from "@meta2d/core";
import {reactive, toRefs, defineComponent, computed, getCurrentInstance, watch} from "vue";

export default defineComponent({
  name: "PenAdditionalAttrConfigCom",
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changeEvent"],
  setup(props) {

    const store = useStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      active_pel_date: {},
      canvasLayerArray: [{id: 4, name: "上层图片层"}, {id: 3, name: " 主画布层"}, {id: 2, name: "下层图片层"}, {id: 1, name: "模板层"}],
      updateFieldList: ["globalAlpha", "showChild", "x", "y", "width", "height", "rotate", "borderRadius", "chineseName", "canvasLayer", "zIndex"],
    })

    const setValueCanvasMeta2dPenFun = () => {
      let updateFieldObj = {};
      emit("update:activePelDate", that.active_pel_date);
      for (let key of that.updateFieldList) updateFieldObj[key] = that.active_pel_date[key];
      canvasMeta2d.value.setValue({id: that.active_pel_date.id, ...updateFieldObj});
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true, immediate: true})

    return {...toRefs(that), canvasMeta2d, watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.globalAlpha {
  width: 12px;
  font-size: 12px;
  text-align: center;
  margin-left: 12px;
}
</style>