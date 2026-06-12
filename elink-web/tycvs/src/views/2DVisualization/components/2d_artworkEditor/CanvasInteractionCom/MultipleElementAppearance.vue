<template>
  <el-collapse v-model="activeNames" class="multipleElementAppearance">

    <div class="content_header content_list">
      <div class="content_list_li_left">选中了{{ current_active_pel_list.length }}个图元</div>
      <div class="content_list_li_right">
        <el-tooltip effect="dark" placement="top-start">
          <div class="iconfont_list" @click="clickPenItemFun('locked')">
            <span v-if="activePelDate.locked === 0" class="iconfont icon-kebianji"></span>
            <span v-if="activePelDate.locked === 1" class="iconfont icon-suoding"></span>
            <span v-if="activePelDate.locked === 2" class="iconfont icon-jiesuo"></span>
            <span v-if="activePelDate.locked === 10" class="iconfont icon-jinyong"></span>
          </div>
          <template #content>
            <span v-if="activePelDate.locked === 0">可编辑</span>
            <span v-if="activePelDate.locked === 1">禁止编辑</span>
            <span v-if="activePelDate.locked === 2">禁止编辑和移动</span>
            <span v-if="activePelDate.locked === 10">禁止所有事件</span>
          </template>
        </el-tooltip>

        <div class="iconfont_list" @click="clickPenItemFun('visible')">
          <span :class="activePelDate.visible ? 'iconfont icon-yanjing_xianshi' : 'icon-yanjing_yincang'" class="iconfont iconfont_visible"></span>
        </div>
      </div>
    </div>

    <el-collapse-item class="content_list" name="1" title="对齐">
      <template v-for="(item,index) in align_list" :key="index">
        <div class="content_list_li fd-column">
          <div class="content_list_li_left">{{ item.name }}</div>
          <div class="content_list_li_right">
            <template v-for="icon in item.children" :key="icon.fieldName">
              <el-tooltip effect="dark" :content="icon.name" placement="top-start">
                <div class="icon_li" @click="clickCanvasAlignIcon(item.fieldName,icon.fieldName)">
                  <span class="iconfont" :class="icon.iconName" style="font-weight: 400"></span>
                </div>
              </el-tooltip>
            </template>
          </div>
        </div>
      </template>
    </el-collapse-item>

    <el-collapse-item class="content_list" name="2" title="外观">

      <div class="content_list_li jc-space-between">
        <lx-input-color-picker v-model:color="activePelDate.color" pickerType="text">
          <template #content>前景颜色</template>
        </lx-input-color-picker>

        <lx-input-color-picker v-model:color="activePelDate.hoverColor" pickerType="text">
          <template #content>悬停颜色</template>
        </lx-input-color-picker>

        <lx-input-color-picker v-model:color="activePelDate.activeColor" pickerType="text">
          <template #content>选中颜色</template>
        </lx-input-color-picker>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">线条</div>
        <div class="content_list_li_right fd-column">
          <div class="content_right_top flex-ai-center" style="width: 100%">
            <el-select v-model="activePelDate.dash" placeholder="线条样式" size="small" style="width: 100px">
              <el-option v-for="ts in dash_list" :key="ts.action" :label="ts.name" :value="ts.action">
                <template #default>
                  <span :class="ts.iconName" class="iconfont"></span>
                </template>
              </el-option>
            </el-select>
            <el-input v-model="activePelDate.lineWidth" placeholder="数值" size="small" style="width: 80px;margin: 0 12px"></el-input>
            <el-checkbox v-model="activePelDate.strokeType"></el-checkbox>
          </div>
          <template v-if="activePelDate.strokeType">
            <div class="content_right_bottom flex-ai-center jc-space-between" style="width: 100%;margin-top: 10px">
              <lx-input-color-picker v-model:color="activePelDate.lineGradientFromColor" pickerType="text">
                <template #content>起始颜色</template>
              </lx-input-color-picker>
              <lx-input-color-picker v-model:color="activePelDate.lineGradientToColor" pickerType="text">
                <template #content>结束颜色</template>
              </lx-input-color-picker>
              <el-input v-model="activePelDate.lineGradientAngle" placeholder="角度" size="small" style="width: 50px;"></el-input>
            </div>
          </template>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">起点箭头</div>
        <div class="content_list_li_right">
          <el-select v-model="activePelDate.fromArrow" size="small" style="width: 90px">
            <el-option v-for="ts in from_arrow_list" :key="ts.fieldName" :label="ts.name" :value="ts.fieldName">
              <span :class="ts.iconName" class="iconfont"></span>
            </el-option>
          </el-select>
          <el-input v-model="activePelDate.fromArrowSize" placeholder="大小" size="small" style="width: 80px;margin: 0 12px"></el-input>
          <lx-input-color-picker v-model:color="activePelDate.fromArrowColor" pickerType="text"></lx-input-color-picker>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">终点箭头</div>
        <div class="content_list_li_right">
          <el-select v-model="activePelDate.toArrow" size="small" style="width: 90px">
            <el-option v-for="ts in to_arrow_list" :key="ts.fieldName" :label="ts.name" :value="ts.fieldName">
              <span :class="ts.iconName" class="iconfont"></span>
            </el-option>
          </el-select>
          <el-input v-model="activePelDate.toArrowSize" placeholder="大小" size="small" style="width: 80px;margin: 0 12px"></el-input>
          <lx-input-color-picker v-model:color="activePelDate.toArrowColor" pickerType="text"></lx-input-color-picker>
        </div>
      </div>

      <div class="content_list_li">
        <el-tooltip effect="dark" content="末端样式" placement="top-start">
          <el-select v-model="activePelDate.lineCap" size="small" placeholder="末端样式">
            <el-option v-for="ts in line_cap_array" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
          </el-select>
        </el-tooltip>
        <div style="width: 12px"></div>
        <el-tooltip effect="dark" content="连接样式" placement="top-start">
          <el-select v-model="activePelDate.lineJoin" size="small" placeholder="连接样式">
            <el-option v-for="ts in line_join_array" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
          </el-select>
        </el-tooltip>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">背景</div>
        <div class="content_list_li_right fd-column" style="padding-left: 12px">
          <TabBackground v-model:tabsIndex="activePelDate.bkType" :tabsArray="bkTypeArray" customClass="connectClass"></TabBackground>
          <div v-if="activePelDate.bkType >= 0" class="content_right_bottom" style="width: 100%;margin-top: 10px">
            <template v-if="activePelDate.bkType === 0">
              <lx-input-color-picker v-model:color="activePelDate.background" placeholder="背景色"></lx-input-color-picker>
            </template>
            <template v-if="activePelDate.bkType >= 1">
              <div class="flex ai-center jc-space-between">
                <lx-input-color-picker v-model:color="activePelDate.gradientFromColor" pickerType="text">
                  <template #content>起始</template>
                </lx-input-color-picker>
                <div class="" style="width: 4px"></div>
                <lx-input-color-picker v-model:color="activePelDate.gradientToColor" pickerType="text">
                  <template #content>结束</template>
                </lx-input-color-picker>
                <el-input v-model="activePelDate.gradientAngle" class="flex-all" placeholder="角度" size="small" style="margin: 0 5px"></el-input>
                <template v-if="activePelDate.bkType === 2">
                  <el-input v-model="activePelDate.gradientRadius" class="flex-all" placeholder="半径" size="small"></el-input>
                </template>
              </div>
            </template>
          </div>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">阴影</div>
        <div class="content_list_li_right fd-column" style="padding-left: 12px">
          <div class="content_right_top flex ai-center" style="width: 100%;margin-bottom: 8px">
            <div class="flex ai-center" style="margin-right: 16px">
              <el-checkbox v-model="activePelDate.shadow"></el-checkbox>
              <template v-if="activePelDate.shadow">
                <div style="width: 12px"></div>
                <lx-input-color-picker v-model:color="activePelDate.shadowColor" pickerType="text"></lx-input-color-picker>
              </template>
            </div>
            <template v-if="activePelDate.shadow">
              <span class="" style="margin-right: 10px">文字阴影</span>
              <el-checkbox v-model="activePelDate.textHasShadow"></el-checkbox>
            </template>
          </div>
          <div v-if="activePelDate.shadow" class="content_right_bottom flex-ai-center">
            <el-tooltip content="X偏移" effect="dark" placement="top-start">
              <el-input v-model="activePelDate.shadowOffsetX" class="row_input_class" size="small">
                <template #prefix>
                  <div class="innerText">X</div>
                </template>
              </el-input>
            </el-tooltip>
            <el-tooltip content="Y偏移" effect="dark" placement="top-start">
              <el-input v-model="activePelDate.shadowOffsetY" class="row_input_class" size="small">
                <template #prefix>
                  <div class="innerText">Y</div>
                </template>
              </el-input>
            </el-tooltip>
            <el-tooltip content="模糊度" effect="dark" placement="top-start">
              <el-input v-model="activePelDate.shadowBlur" class="row_input_class" placeholder="模糊度" size="small"></el-input>
            </el-tooltip>
          </div>
        </div>
      </div>
    </el-collapse-item>

    <el-collapse-item class="content_list" name="3" title="文字">
      <div class="content_list_li">
        <div class="content_list_li_left">文字样式</div>
        <div class="content_list_li_right">
          <el-select v-model="activePelDate.fontFamily" clearable size="small" class="flex-all">
            <el-option v-for="(item,index) in font_family_list" :key="index" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">文字大小</div>
        <div class="content_list_li_right">
          <el-input-number v-model="activePelDate.fontSize" controls-position="right" class="flex-all" min="0" size="small"></el-input-number>
        </div>
      </div>

      <div class="content_list_li jc-space-between">
        <el-select v-model="activePelDate.textAlign" class="flex-all" size="small">
          <el-option v-for="(item,index) in text_align_list" :key="index" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <div style="width: 5px"></div>
        <el-select v-model="activePelDate.textBaseline" class="flex-all" size="small">
          <el-option v-for="(item,index) in text_base_line_list" :key="index" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <div :class="{ active_border_icon: activePelDate.fontWeight === 'bold'}" class="border_icon" @click="clickPenItemFun('B')">B
        </div>
        <div :class="{ active_border_icon: activePelDate.fontStyle === 'italic'}" class="border_icon" @click="clickPenItemFun('I')">I
        </div>
      </div>

      <div class="content_list_li jc-space-between">
        <lx-input-color-picker v-model:color="activePelDate.textColor" pickerType="text">
          <template #content>前景</template>
        </lx-input-color-picker>
        <lx-input-color-picker v-model:color="activePelDate.textBackground" pickerType="text">
          <template #content>背景</template>
        </lx-input-color-picker>
        <lx-input-color-picker v-model:color="activePelDate.hoverTextColor" pickerType="text">
          <template #content>悬停</template>
        </lx-input-color-picker>
        <lx-input-color-picker v-model:color="activePelDate.activeTextColor" pickerType="text">
          <template #content>选中</template>
        </lx-input-color-picker>
      </div>

      <div class="content_list_li ai-center">
        <el-checkbox v-model="activePelDate.whiteSpace" true-value="break-all">换行</el-checkbox>
        <el-checkbox v-model="activePelDate.ellipsis">省略号</el-checkbox>
        <el-tooltip content="行高" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.lineHeight" class="flex-all" placeholder="行高" size="small" style="margin-left: 5px"></el-input>
        </el-tooltip>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">保留小数位数</div>
        <div class="content_list_li_right">
          <el-input-number v-model="activePelDate.keepDecimal" class="flex-all" min="0" placeholder="显示时保留小数位数" controls-position="right" size="small"/>
        </div>
      </div>

      <div class="content_list_li ai-center">
        <el-tooltip content="水平偏移" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.textLeft" class="flex-all" size="small" style="margin-left: 5px"></el-input>
        </el-tooltip>
        <el-tooltip content="垂直偏移" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.textTop" class="flex-all" size="small" style="margin-left: 5px"></el-input>
        </el-tooltip>
        <el-tooltip content="宽" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.textWidth" class="flex-all" size="small" style="margin-left: 5px"></el-input>
        </el-tooltip>
        <el-tooltip content="高" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.textHeight" class="flex-all" size="small" style="margin-left: 5px"></el-input>
        </el-tooltip>
      </div>
      <div class="content_list_li ai-center jc-space-between">
        <el-checkbox v-model="activePelDate.disableInput">只读</el-checkbox>
        <el-checkbox v-model="activePelDate.hiddenText">隐藏文字</el-checkbox>
        <el-checkbox v-model="activePelDate.textAutoAdjust">自动调整</el-checkbox>
      </div>
    </el-collapse-item>

    <div class="content_list">
      <div class="content_list_li ai-center">
        <el-checkbox v-model="activePelDate.flipX">水平翻转</el-checkbox>
        <el-checkbox v-model="activePelDate.flipY">垂直翻转</el-checkbox>
        <div style="width: 10px"></div>
        <el-tooltip content="锚点半径" effect="dark" placement="top-start">
          <el-input v-model="activePelDate.anchorRadius" class="flex-all" size="small">
            <template #prefix>
              <div class="innerText">半径</div>
            </template>
          </el-input>
        </el-tooltip>
      </div>
    </div>

    <div class="content_list">
      <div class="content_list_li ai-center jc-space-between">
        <el-checkbox v-model="activePelDate.disableRotate">禁止旋转</el-checkbox>
        <el-checkbox v-model="activePelDate.disableSize">禁止缩放</el-checkbox>
        <el-checkbox v-model="activePelDate.disableAnchor">禁用锚点</el-checkbox>
      </div>
    </div>

  </el-collapse>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {MoreFilled} from "@element-plus/icons-vue";
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import {dashArray, fontFamilyArray, fromArrowArray, lineCapArray, lineJoinArray, textAlignArray, toArrowArray, verticalAlignArray} from "@/utils/publicParam";

export default defineComponent({
  name: "MultipleElementAppearance",
  components: {LxInputColorPicker},
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const that = reactive({
      MoreFilled,
      activePelDate: {
        locked: 0, // 可编辑
        visible: true, // 是否可见
      },
      penIconFamilyVisible: false,
      activeNames: ["1", "2", "3", "4"],

      dash_list: dashArray,
      to_arrow_list: toArrowArray,
      line_cap_array: lineCapArray,
      line_join_array: lineJoinArray,
      text_align_list: textAlignArray,
      from_arrow_list: fromArrowArray,
      font_family_list: fontFamilyArray,
      text_base_line_list: verticalAlignArray,
      bkTypeArray: [{id: 0, name: "纯色"}, {id: 1, name: "线性渐变"}, {id: 2, name: "径向渐变"}],
      align_list: [
        {
          name: "大屏对齐",
          fieldName: 'screen_align',
          children: [
            {name: "左对齐", fieldName: "left", iconName: "icon-align-left"},
            {name: "水平居中对齐", fieldName: "center", iconName: "icon-align-center"},
            {name: "右对齐", fieldName: "right", iconName: "icon-align-right"},
            {name: "顶部对齐", fieldName: "top", iconName: "icon-align-top"},
            {name: "垂直居中对齐", fieldName: "middle", iconName: "icon-align-middle"},
            {name: "底部对齐", fieldName: "bottom", iconName: "icon-align-bottom"},
          ]
        },
        {
          name: "区域对齐",
          fieldName: 'area_align',
          children: [
            {name: "左对齐", fieldName: "left", iconName: "icon-align-left"},
            {name: "垂直居中对齐", fieldName: "center", iconName: "icon-align-center"},
            {name: "右对齐", fieldName: "right", iconName: "icon-align-right"},
            {name: "顶部对齐", fieldName: "top", iconName: "icon-align-top"},
            {name: "水平居中对齐", fieldName: "middle", iconName: "icon-align-middle"},
            {name: "底部对齐", fieldName: "bottom", iconName: "icon-align-bottom"},
            {name: "水平等距", fieldName: "spaceBetween", iconName: "icon-horizontal-between"},
            {name: "垂直等距", fieldName: "spaceBetweenColumn", iconName: "icon-vertical-between"},
          ]
        },
        {
          name: "以最后选中图元对齐",
          fieldName: 'last_align',
          children: [
            {name: "左对齐", fieldName: "left", iconName: "icon-align-left"},
            {name: "垂直居中对齐", fieldName: "center", iconName: "icon-align-center"},
            {name: "右对齐", fieldName: "right", iconName: "icon-align-right"},
            {name: "顶部对齐", fieldName: "top", iconName: "icon-align-top"},
            {name: "水平居中对齐", fieldName: "middle", iconName: "icon-align-middle"},
            {name: "底部对齐", fieldName: "bottom", iconName: "icon-align-bottom"},
            {name: "相同", fieldName: "equal", iconName: "icon-xiangtongliangbiao"},
          ]
        }
      ]
    })

    const clickPenItemFun = (operateType) => {
      if (operateType === "B") {
        let fontWeight = "bold";
        if (that.activePelDate.fontWeight === 'bold') fontWeight = "normal";
        that.activePelDate.fontWeight = fontWeight;
      }

      if (operateType === "I") {
        let fontStyle = "italic";
        if (that.activePelDate.fontStyle === 'italic') fontStyle = "normal";
        that.activePelDate.fontStyle = fontStyle;
      }

      if (operateType === "visible") {
        that.activePelDate.visible = !that.activePelDate.visible;
      }

      if (operateType === "locked") {
        let locked = 0;
        switch (that.activePelDate.locked) {
          case 0:
            locked = 1;
            break
          case 1:
            locked = 2;
            break
          case 2:
            locked = 10;
            break
          case 10:
            locked = 0;
            break
          default:
            locked = 0;
        }
        that.activePelDate.locked = locked;
      }
    }

    const clickCanvasAlignIcon = (fieldName,align)=>{

      let pens = [];
      if (current_active_pel_list.value && current_active_pel_list.value.length) {
        for (let i = 0; i < current_active_pel_list.value.length; i++) {
          let activePelDate = canvasMeta2d.value.findOne(current_active_pel_list.value[i]);
          if(activePelDate) pens.push(activePelDate)
        }
      }

      // 大屏对齐
      if(fieldName === "screen_align") canvasMeta2d.value.alignNodesV(align,pens);

      // 区域对齐
      if(fieldName === "area_align"){
        if(align === "spaceBetween"){
          canvasMeta2d.value.spaceBetween(pens);
        } else if (align === "spaceBetweenColumn"){
          canvasMeta2d.value.spaceBetweenColumn(pens);
        } else {
          canvasMeta2d.value.alignNodes(align,pens);
        }
      }

      // 以最后选中图元对齐
      if(fieldName === "last_align"){
        if(align === "equal"){
          canvasMeta2d.value.beSameByLast(pens);
        } else {
          canvasMeta2d.value.alignNodesByLast(align,pens);
        }
      }
    }

    const watchActiveObjectDate = watch(() => that.activePelDate, (newActiveObjectDate) => {
      let activePelDate = JSON.parse(JSON.stringify(newActiveObjectDate));
      let findItem = that.dash_list.find(item => item.action === activePelDate.dash);
      if (findItem) activePelDate.lineDash = findItem.lineDash;
      if (current_active_pel_list.value && current_active_pel_list.value.length) {
        for (let i = 0; i < current_active_pel_list.value.length; i++) {
          canvasMeta2d.value.setValue({ id: current_active_pel_list.value[i],...activePelDate }, { render: false });
        }
        canvasMeta2d.value.render();
      }
    }, {deep: true})

    const watchCurrentActivePelList = watch(() => current_active_pel_list, () => {
      that.activePelDate = {locked: 0, visible: true};
    }, { deep: true })

    return {...toRefs(that), canvasMeta2d, current_active_pel_list, watchCurrentActivePelList, clickPenItemFun, watchActiveObjectDate,clickCanvasAlignIcon}
  }
})
</script>

<style lang="scss" scoped>
.multipleElementAppearance {
  border: none;
  padding: 12px 0;
  box-sizing: border-box;

  .content_header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 12px 12px 16px !important;

    .content_list_li_left {
      color: var(--color-title);
    }

    .content_list_li_right {
      display: flex;
      align-items: center;

      .iconfont_list {
        cursor: pointer;
        color: var(--color);

        .iconfont {
          font-size: 18px;
          margin-right: 8px;
        }

        &:last-child {
          padding-top: 4px;
          box-sizing: border-box;

          .iconfont {
            margin-right: 0;
            font-size: 21px;
          }
        }
      }
    }
  }

  :deep(.content_list) {
    padding: 12px 0;
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-collapse-border-color);

    .el-collapse-item__header {
      font-size: 13px;
      font-weight: 700;
      color: var(--color-title);
      padding-left: 16px;
      box-sizing: border-box;
    }

    .el-collapse-item__content {
      padding-bottom: 12px;
    }

    .content_list_li {
      display: flex;
      padding: 0 12px 0 16px;
      box-sizing: border-box;
      margin-bottom: 8px;

      .border_icon {
        width: 21px;
        height: 21px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-left: 5px;
        border-radius: 4px;
        box-sizing: border-box;

        &:hover {
          cursor: pointer;
          color: var(--color-primary);
          border: 1px solid var(--color-primary);
        }
      }

      .active_border_icon {
        color: var(--color-title);
        border: 1px solid var(--color-border-input);
        background-color: var(--color-border-input);
      }

      .row_input_class {
        flex: 1;
        margin-right: 10px;

        &:last-child {
          margin-right: 0;
        }
      }

      .min_width_class {
        width: 60px;
      }

      .el-checkbox {
        --el-checkbox-font-size: 12px;
        --el-checkbox-text-color: var(--color);
      }

      .content_list_li_left {
        flex-shrink: 0;
        font-size: 12px;
        color: var(--color);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 30px;
      }

      .content_list_li_right {
        flex: 1;
        display: flex;
        align-items: center;
        padding-left: 8px;
        box-sizing: border-box;

        .icon_li{
          cursor: pointer;
          margin-right: 10px;
          &:last-child{
            margin-right: 0;
          }
        }

        .tabBackground {
          //margin-left: 16px;

          .content_left {
            width: 100%;

            .but_list_li {
              flex: 1;
              padding: 4px 0;

              .name_text {
                font-size: 12px;
              }
            }
          }
        }

        .innerText {
          padding-right: 4px;
        }

        .el-radio-group {
          display: initial;

          .el-radio {
            margin-right: 0;
            display: block;
          }
        }

        .iconfont {
          cursor: pointer;
          font-size: 18px;
          color: #242424;
          font-weight: bold;
        }

        .globalAlpha {
          width: 48px;
          font-size: 12px;
          text-align: center;
        }

        .marginLeft {
          margin-left: 4px;
        }
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    .content_list_image {
      width: 100%;
      padding: 12px 10px;
      box-sizing: border-box;
      display: flex;
      align-items: center;
      justify-content: center;

      .el-image {
        width: 98px;
        height: 98px;
      }
    }

    &:last-child {
      border-bottom: none;

      .el-collapse-item__wrap {
        border-bottom: none;
      }
    }
  }

  .el-collapse-item {
    padding: 0;
    border-bottom: none;
  }
}
</style>