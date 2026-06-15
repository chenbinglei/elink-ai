<template>
  <el-collapse-item name="1" title="外观属性">
    <el-form :model="active_pel_date" label-width="auto" class="content_item_body">
      <el-form-item>
        <div class="content_list_li flex-ai-center jc-space-between">
          <lx-input-color-picker v-model:color="active_pel_date.color" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>前景颜色</template>
          </lx-input-color-picker>

          <lx-input-color-picker v-model:color="active_pel_date.hoverColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>悬停颜色</template>
          </lx-input-color-picker>

          <lx-input-color-picker v-model:color="active_pel_date.activeColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>选中颜色</template>
          </lx-input-color-picker>
        </div>
      </el-form-item>
      <el-form-item label="线条">
        <div class="content_list_li flex-ai-center">
          <div class="flex-all">
            <el-select v-model="active_pel_date.dash" placeholder="线条样式" size="small" @change="setValueCanvasMeta2dPenFun">
              <el-option v-for="ts in dash_list" :key="ts.action" :label="ts.name" :value="ts.action">
                <template #default>
                  <span class="iconfont" :class="ts.iconName"></span>
                </template>
              </el-option>
            </el-select>
          </div>
          <el-input type="number" v-model="active_pel_date.lineWidth" placeholder="宽度" style="width: 80px;margin: 0 10px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          <el-checkbox v-model="active_pel_date.strokeType" @change="setValueCanvasMeta2dPenFun"></el-checkbox>
        </div>
      </el-form-item>
      <el-form-item v-if="active_pel_date.strokeType">
        <div class="content_list_li flex-ai-center jc-space-between">
          <lx-input-color-picker v-model:color="active_pel_date.lineGradientFromColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>起始颜色</template>
          </lx-input-color-picker>
          <lx-input-color-picker v-model:color="active_pel_date.lineGradientToColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
            <template #content>结束颜色</template>
          </lx-input-color-picker>
          <el-input v-model="active_pel_date.lineGradientAngle" placeholder="角度" size="small" style="width: 50px;" @change="setValueCanvasMeta2dPenFun"></el-input>
        </div>
      </el-form-item>
      <el-form-item label="起点箭头">
        <div class="content_list_li flex-ai-center">
          <div class="flex-all">
            <el-select v-model="active_pel_date.fromArrow" size="small" @change="setValueCanvasMeta2dPenFun">
              <el-option v-for="ts in from_arrow_list" :key="ts.fieldName" :label="ts.name" :value="ts.fieldName">
                <span class="iconfont" :class="ts.iconName"></span>
              </el-option>
            </el-select>
          </div>
          <el-input type="number" v-model="active_pel_date.fromArrowSize" placeholder="大小" style="width: 55px;margin: 0 10px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          <lx-input-color-picker v-model:color="active_pel_date.fromArrowColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
        </div>
      </el-form-item>
      <el-form-item label="终点箭头">
        <div class="content_list_li flex-ai-center">
          <div class="flex-all">
            <el-select v-model="active_pel_date.toArrow" size="small" @change="setValueCanvasMeta2dPenFun">
              <el-option v-for="ts in to_arrow_list" :key="ts.fieldName" :label="ts.name" :value="ts.fieldName">
                <span class="iconfont" :class="ts.iconName"></span>
              </el-option>
            </el-select>
          </div>
          <el-input type="number" v-model="active_pel_date.toArrowSize" placeholder="大小" style="width: 55px;margin: 0 10px" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
          <lx-input-color-picker v-model:color="active_pel_date.toArrowColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun"></lx-input-color-picker>
        </div>
      </el-form-item>
      <el-form-item label="连线类型" v-if="active_pel_date.type === 1">
        <el-select v-model="active_pel_date.lineName" size="small" placeholder="连线类型" @change="setValueCanvasMeta2dPenFun('lineName')">
          <el-option v-for="ts in line_name_array" :key="ts.fieldName" :label="ts.name" :value="ts.fieldName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="末端样式">
        <el-select v-model="active_pel_date.lineCap" size="small" placeholder="末端样式" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="ts in line_cap_array" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="连接样式">
        <el-select v-model="active_pel_date.lineJoin" size="small" placeholder="连接样式" @change="setValueCanvasMeta2dPenFun">
          <el-option v-for="ts in line_join_array" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
        </el-select>
      </el-form-item>
      <template v-if="active_pel_date.type !== 1">
        <el-form-item label="背景">
          <div class="content_list_li">
            <TabBackground v-model:tabsIndex="active_pel_date.bkType" :tabsArray="bkTypeArray" customClass="connectClass" @changEvent="setValueCanvasMeta2dPenFun"/>
          </div>
        </el-form-item>
        <template v-if="active_pel_date.bkType >= 0">
          <el-form-item label="背景颜色" v-if="active_pel_date.bkType === 0">
            <div class="content_list_li">
              <lx-input-color-picker v-model:color="active_pel_date.background" placeholder="背景色" @changEvent="setValueCanvasMeta2dPenFun" />
            </div>
          </el-form-item>
          <template v-if="active_pel_date.bkType >= 1">
            <el-form-item label="背景颜色" >
              <lx-input-color-picker v-model:color="active_pel_date.gradientFromColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
                <template #content>起始</template>
              </lx-input-color-picker>
              <div style="width: 4px"></div>
              <lx-input-color-picker v-model:color="active_pel_date.gradientToColor" pickerType="text" @changEvent="setValueCanvasMeta2dPenFun">
                <template #content>结束</template>
              </lx-input-color-picker>
            </el-form-item>
            <el-form-item label="背景角度">
              <el-input v-model="active_pel_date.gradientAngle" placeholder="角度" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
            </el-form-item>
            <template v-if="active_pel_date.bkType === 2">
              <el-form-item label="背景半径">
                <el-input v-model="active_pel_date.gradientRadius" placeholder="半径" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
              </el-form-item>
            </template>
          </template>
        </template>
      </template>
      <el-form-item label="阴影">
        <div class="content_list_li flex-ai-center">
          <el-checkbox v-model="active_pel_date.shadow" @change="setValueCanvasMeta2dPenFun"></el-checkbox>
          <template v-if="active_pel_date.shadow">
            <div class="flex-all" style="margin-left: 10px">
              <lx-input-color-picker v-model:color="active_pel_date.shadowColor" @changEvent="setValueCanvasMeta2dPenFun" />
            </div>
          </template>
        </div>
      </el-form-item>
      <template v-if="active_pel_date.shadow">
        <el-form-item label="文字阴影">
          <el-checkbox v-model="active_pel_date.textHasShadow" @change="setValueCanvasMeta2dPenFun"></el-checkbox>
         </el-form-item>
        <el-form-item>
          <div class="content_list_li flex-ai-center">
            <div class="flex-all">
              <el-tooltip effect="dark" content="X偏移" placement="top-start">
                <el-input type="number" v-model="active_pel_date.shadowOffsetX" size="small" @change="setValueCanvasMeta2dPenFun">
                  <template #prefix>
                    <div class="innerText">X</div>
                  </template>
                </el-input>
              </el-tooltip>
            </div>
            <div style="width: 4px"></div>
            <div class="flex-all">
              <el-tooltip effect="dark" content="Y偏移" placement="top-start">
                <el-input type="number" v-model="active_pel_date.shadowOffsetY" size="small" @change="setValueCanvasMeta2dPenFun">
                  <template #prefix>
                    <div class="innerText">Y</div>
                  </template>
                </el-input>
              </el-tooltip>
            </div>
            <div style="width: 4px"></div>
            <div class="flex-all">
              <el-tooltip effect="dark" content="模糊度" placement="top-start">
                <el-input type="number" v-model="active_pel_date.shadowBlur" placeholder="模糊度" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
              </el-tooltip>
            </div>
          </div>
        </el-form-item>
      </template>
    </el-form>
  </el-collapse-item>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance, computed} from "vue";
import {dashArray, fromArrowArray, lineCapArray, lineJoinArray, lineNameArray, toArrowArray} from "@/utils/publicParam";

export default defineComponent({
  name: "PenAppearanceConfigCom",
  components: {LxInputColorPicker},
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate","changeEvent"],
  setup(props) {

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      active_pel_date: {},
      dash_list: dashArray,
      to_arrow_list: toArrowArray,
      line_cap_array: lineCapArray,
      line_name_array: lineNameArray,
      line_join_array: lineJoinArray,
      from_arrow_list: fromArrowArray,
      bkTypeArray: [{id: 0, name: "纯色"}, {id: 1, name: "线性渐变"}, {id: 2, name: "径向渐变"}],
    })

    const setValueCanvasMeta2dPenFun = (fieldName) => {
      let findItem = that.dash_list.find(item=> item.action === that.active_pel_date.dash);
      if(findItem) that.active_pel_date.lineDash = findItem.lineDash;

      // 更新连线类型
      if(fieldName === "lineName"){
        let pen = canvasMeta2d.value.findOne(that.active_pel_date.id);
        canvasMeta2d.value.updateLineType(pen, that.active_pel_date.lineName);
        emit("changeRetrieve");
        return
      }

      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), watchActivePelDate, setValueCanvasMeta2dPenFun, canvasMeta2d}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;

  .content_list_li{
    width: 100%;

    .el-select,.el-input-number{
      width: 100%;
    }
  }

  :deep(.tabBackground) {
    .content_left{
      width: 100%;

      .but_list_li{
        flex: 1;
        max-height: 28px;
        padding: 4px 0 !important;

        .name_text{
          font-size: 12px;
        }
      }
    }
  }
}
</style>