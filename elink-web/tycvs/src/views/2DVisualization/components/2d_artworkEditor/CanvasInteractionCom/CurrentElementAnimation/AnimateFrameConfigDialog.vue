<template>
  <div class="animateFrameConfig">
    <el-drawer v-model="dialog_visible" direction="rtl" size="380">
      <template #header>
        <div class="header_class">{{ titleName }}</div>
      </template>
      <div class="drawer_content">
        <div class="content_top scrollbarStyle">
          <el-collapse v-model="activeNames">
            <template v-for="(item,index) in frame_list" :key="index">
              <el-collapse-item :name="index" :title="'帧' + (index + 1)">
                <template #title>
                  <div class="header-title">
                    <div class="title_left">帧{{ index + 1 }}</div>
                    <div class="title_right">
                      <el-tooltip content="在当前帧后面添加动画帧" effect="dark" placement="top">
                        <span class="iconfont icon-wenjianjiatianjia" @click.stop="clickItemButton(1,index)"></span>
                      </el-tooltip>
                      <AddFrameButtonListCom :attributeNameList="attributeNameList" :activeIndex="index" @changeEvent="changeEvent"></AddFrameButtonListCom>
                      <el-popover v-model:visible="visibleObj['visible' + index]" :width="200" placement="left-start" trigger="contextmenu">
                        <template #reference>
                          <span class="iconfont icon-shanchu" @click.stop="visibleObj['visible' + index] = true"></span>
                        </template>
                        <div style="margin-bottom: 4px">
                          <span class="iconfont icon-tishi" style="margin-right: 4px;color: #007FEB"></span>
                          <span style="color: #666666;font-size: 14px">确认删除该动画帧吗？</span>
                        </div>
                        <div class="flex-ai-center jc-end">
                          <el-button size="small" text @click="visibleObj['visible' + index] = false">取消</el-button>
                          <el-button size="small" type="primary" @click.stop="clickItemButton(3,index)">确定</el-button>
                        </div>
                      </el-popover>
                    </div>
                  </div>
                </template>
                <template v-for="(attrValue,attrKey) in item" :key="attrKey">
                  <div class="content_list_li">
                    <div class="content_list_li_left">{{ queryAttributeNameFun(attrKey,'label') }}</div>
                    <div class="content_list_li_right">
                      <div class="flex-all">
                        <template v-if="queryAttributeNameFun(attrKey,'type') === 'bool'">
                          <el-switch v-model="item[attrKey]" size="small" :active-value="true" :inactive-value="false"></el-switch>
                        </template>

                        <template v-else-if="queryAttributeNameFun(attrKey,'type') === 'color'">
                          <lx-input-color-picker v-model:color="item[attrKey]"></lx-input-color-picker>
                        </template>

                        <template v-else-if="queryAttributeNameFun(attrKey,'type') === 'list'">
                          <el-select v-model="item[attrKey]" size="small">
                            <template v-for="(sl_item,sl_index) in queryAttributeNameFun(attrKey,'list')" :key="sl_index">
                              <el-option :label="sl_item.name" :value="sl_item.id"></el-option>
                            </template>
                          </el-select>
                        </template>

                        <template v-else-if="queryAttributeNameFun(attrKey,'type') === 'string'">
                          <el-input v-model="item[attrKey]" :placeholder="queryAttributeNameFun(attrKey,'placeholder')" size="small">
                            <template v-if="queryAttributeNameFun(attrKey,'unit')" #suffix>
                              <span class="unit">{{ queryAttributeNameFun(attrKey,'unit') }}</span>
                            </template>
                          </el-input>
                        </template>

                        <template v-else>
                          <el-input-number v-model="item[attrKey]" controls-position="right" :placeholder="queryAttributeNameFun(attrKey,'placeholder')" size="small">
                            <template v-if="queryAttributeNameFun(attrKey,'unit')" #suffix>
                              <span class="unit">{{ queryAttributeNameFun(attrKey,'unit') }}</span>
                            </template>
                          </el-input-number>
                        </template>
                      </div>
                      <template v-if="attrKey !== 'duration'">
                        <span class="iconfont icon-line_guanbi" @click.stop="clickItemButton(4,index,attrKey)"></span>
                      </template>
                      <span v-else class="iconfont icon-line_guanbi" style="visibility: hidden"></span>
                    </div>
                  </div>
                </template>
              </el-collapse-item>
            </template>
          </el-collapse>
          <el-button style="width: 100%" type="primary" @click="clickAddFrameBut">添加帧</el-button>
        </div>
        <div class="content_bottom">
          <el-button class="cancelBut" @click="clickCancelBut">取消</el-button>
          <el-button class="confirmBut" type="primary" @click="clickConfirmBut">确认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script lang="ts">
import {LxCollapse} from "@/components/LxComponents";
import {LxInputColorPicker} from "@/components/LxComponents";
import AddFrameButtonListCom from "./AddFrameButtonListCom.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "AnimateFrameConfigDialog",
  components: {LxCollapse,AddFrameButtonListCom,LxInputColorPicker},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    titleName: {
      type: String,
      default: "设置动画"
    },
    activeFrame: {
      type: Array,
      default: () => []
    },
  },
  emits: ["update:isVisible", "changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      visibleObj: {},
      frame_list: [],
      activeNames: [],
      dialog_visible: props.isVisible,

      attributeNameList: [
        {label: "时长", key: "duration", type: "integer", placeholder: "毫秒", unit: "ms"},
        {label: "显示", key: "visible", type: "bool"},
        {label: "缩放", key: "scale", type: "integer", placeholder: "缩放比列"},
        {label: "旋转", key: "rotate", type: "integer", unit: "°"},
        {label: "X位移", key: "x", type: "float", unit: "px"},
        {label: "Y位移", key: "y", type: "float", unit: "px"},

        {label: "前景颜色", key: "color", type: "color"},
        {label: "背景颜色", key: "background", type: "color"},
        {label: "背景类型", key: "bkType", type: "list",list: [{id: 0, name: "纯色"}, {id: 1, name: "线性渐变"}, {id: 2, name: "径向渐变"}]},

        {label: "宽", key: "width", type: "float"},
        {label: "高", key: "height", type: "float"},
        {label: "状态", key: "showChild", type: "integer"},
        {label: "文字", key: "text", type: "string"},
        {label: "字体大小", key: "fontSize", type: "integer", unit: "px"},
        {label: "文字颜色", key: "textColor", type: "color"},
        {label: "文字倾斜", key: "fontStyle", type: "list",list:[{id: "normal", name: "正常"}, {id: "italic", name: "倾斜"}]},
        {label: "文字加粗", key: "fontWeight", type: "list",list:[{id: "normal", name: "正常"}, {id: "bold", name: "加粗"}]},

        {label: "水平翻转", key: "flipX", type: "bool"},
        {label: "垂直翻转", key: "flipY", type: "bool"},
        {label: "进度", key: "progress", type: "integer", placeholder: "0-1之间"},
        {label: "进度颜色", key: "progressColor", type: "color"},
        {label: "垂直进度", key: "verticalProgress", type: "bool"},

        {label: "透明度", key: "globalAlpha", type: "integer", placeholder: "0-1之间"},
        {label: "线条宽度", key: "lineWidth", type: "integer", unit: "px"},
      ]
    })

    const clickAddFrameBut = () => {
      that.frame_list.push({duration: ""});
    }

    const clickItemButton = (operateType, index, attrKey) => {
      if (operateType === 1) {
        that.frame_list.splice(index + 1, 0, {duration: ""});
      }

      if (operateType === 3) {
        that.visibleObj['visible' + index] = false;
        that.frame_list.splice(index, 1);
      }

      // 删除某一条属性
      if (operateType === 4) {
        delete that.frame_list[index][attrKey];
      }
    }

    const changeEvent = (data)=>{
      if(data.operateType === "AddFrameButtonListCom"){
        that.frame_list[data.activeIndex][data.key] = null;
      }
    }

    const clickCancelBut = () => {
      that.dialog_visible = false;
    }

    const clickConfirmBut = () => {
      emit("changeEvent", {type: "AnimateFrameConfigDialog", frames: that.frame_list});
      that.dialog_visible = false;
    }

    const queryAttributeNameFun = (fieldName,fieldValue)=>{
      let findItem = that.attributeNameList.find(item => item.key === fieldName);
      return findItem[fieldValue]
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
      try {
        that.frame_list = JSON.parse(JSON.stringify(props.activeFrame));
      } catch (e) {}
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickCancelBut, clickConfirmBut, clickAddFrameBut, clickItemButton, queryAttributeNameFun, changeEvent}
  }
})
</script>

<style lang="scss" scoped>
.animateFrameConfig {

  :deep(.el-drawer) {
    --el-drawer-padding-primary: 12px;

    .el-drawer__header {
      margin-bottom: 0;
    }

    .el-drawer__body {
      padding-left: 0;
      padding-right: 0;
    }
  }

  .drawer_content {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;

    .content_top {
      flex: 1;
      overflow-y: auto;
      padding: 12px 12px;
      box-sizing: border-box;

      .header-title {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .title_right {
          padding-right: 14px;

          .iconfont {
            margin-right: 5px;

            &:last-child {
              margin-right: 0;
            }
          }
        }
      }

      .content_list_li {
        display: flex;
        padding-left: 12px;
        box-sizing: border-box;
        margin-bottom: 8px;

        .content_list_li_left {
          width: 85px;
          font-size: 14px;
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

          .el-input-number {
            width: 100%;
          }

          .iconfont {
            margin-left: 2px;
            cursor: pointer;
            visibility: hidden;
          }

          &:hover {
            .iconfont {
              visibility: initial;
            }
          }
        }

        &:last-child {
          margin-bottom: 0;
        }
      }
    }

    .content_bottom {
      display: flex;
      justify-content: flex-end;
      margin-top: 12px;
      padding: 0 12px;
      box-sizing: border-box;
    }
  }
}
</style>