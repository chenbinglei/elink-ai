<template>
  <div class="currentElementEvents">
    <template v-if="activePelDate.events && activePelDate.events.length">
      <lx-collapse v-for="(item,index) in activePelDate.events" :key="index">
        <template #title>
          <div class="collapse_title flex-ai-center jc-space-between">
            <el-select v-model="item.name" size="small" style="width: 100px;">
              <el-option v-for="ts in events_list" :key="ts.key" :label="ts.label" :value="ts.key"></el-option>
            </el-select>
            <el-popover v-model:visible="visibleObj['visible' + index]" :width="210" placement="left-start" trigger="contextmenu">
              <template #reference>
                <span class="iconfont icon-shanchu" @click.stop="visibleObj['visible' + index] = true"></span>
              </template>
              <div style="margin-bottom: 4px">
                <span class="iconfont icon-tishi" style="margin-right: 4px;color: #007FEB"></span>
                <span style="color: #666666;font-size: 14px">确认删除该交互事件吗？</span>
              </div>
              <div class="flex-ai-center jc-end">
                <el-button size="small" text @click="visibleObj['visible' + index] = false">取消</el-button>
                <el-button size="small" type="primary" @click.stop="clickItemButton(2,index)">确定</el-button>
              </div>
            </el-popover>
          </div>
        </template>
        <template #content>
          <div class="content_list">
            <template v-if="item.conditions && item.conditions.length">
              <div class="content_list_li">
                <div class="content_list_li_left">触发条件</div>
                <div class="content_list_li_right">
                  <el-radio-group v-model="item.conditionType">
                    <el-radio v-for="(ts,ti) in conditionTypeArray" :key="ti" :value="ts.id" size="small">
                      {{ ts.name }}
                    </el-radio>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <EventConditionsCom v-model:conditions="item.conditions" :canvasPenList="canvasPenList"></EventConditionsCom>
            <EventActionsCom v-model:actions="item.actions" :canvasPenList="canvasPenList" :combinePenList="combinePenList"></EventActionsCom>
          </div>
        </template>
      </lx-collapse>
      <AddEventButtonListCom @changeEvent="changeEvent"></AddEventButtonListCom>
    </template>
    <template v-else>
      <null-data words="还没有交互事件">
        <template #content>
          <AddEventButtonListCom @changeEvent="changeEvent"></AddEventButtonListCom>
        </template>
      </null-data>
    </template>
  </div>
</template>

<script>
import {useStore} from "vuex";
import {deepClone} from "@meta2d/core";
import {LxCollapse} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, onMounted, computed, watch} from "vue";
import {AddEventButtonListCom, EventConditionsCom, EventActionsCom} from "./CurrentElementEvents/index";

export default defineComponent({
  name: "CurrentElementEvents",
  components: {LxCollapse, AddEventButtonListCom, EventConditionsCom, EventActionsCom},
  setup() {

    const store = useStore();
    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return store.state.meta2d.current_active_pel_list;
    });

    const delete_field_list = computed(() => {
      return store.state.meta2d.setValueDeleteFieldList;
    });

    const that = reactive({
      visibleObj: {},
      activePelDate: {},
      canvasPenList: [],
      combinePenList: [],
      conditionTypeArray: [{id: "and", name: "满足全部条件"}, {id: "or", name: "满足任意条件"}],
      events_list: [
        {label: "单击", key: 'click'},
        {label: "双击", key: 'dblclick'},
        {label: "鼠标右键", key: 'contextmenu'},
        {label: "鼠标移入", key: 'enter'},
        {label: "鼠标移出", key: 'leave'},
        {label: "获取焦点", key: 'active'},
        {label: "失去焦点", key: 'inactive'},
        {label: "鼠标按下", key: 'mousedown'},
        {label: "鼠标抬起", key: 'mouseup'},
        {label: "值变化", key: 'valueUpdate'},
        {label: "监听全局消息", key: 'message'},
      ],
    })

    const findPenIdConfigFun = () => {
      // console.log(current_active_pel_list);
      let pen_id = current_active_pel_list.value[0];
      let activePelDate = canvasMeta2d.value.findOne(pen_id);
      let clonePelDate = deepClone(activePelDate ? activePelDate : {});
      that.activePelDate = JSON.parse(JSON.stringify(clonePelDate));
      // console.log(that.activePelDate);
    };

    const changeEvent = (data) => {
      // 添加事件
      if (data.type === "AddEventButtonListCom") {
        if (!that.activePelDate.events) that.activePelDate.events = [];
        that.activePelDate.events.push({conditionType: "and", name: data.key, conditions: [], actions: []});
      }

      if (data.type === "eventActionsCom") that.activePelDate.events[data.index].actions = data.list;

      if (data.type === "eventConditionsCom") that.activePelDate.events[data.index].conditions = data.list;
    }

    const clickItemButton = (operateType, index) => {
      // 删除对应事件
      if (operateType === 2) {
        that.visibleObj['visible' + index] = false;
        that.activePelDate.events.splice(index, 1);
      }
    }

    // 查找画布上所有图元
    const queryCanvasMeta2dPens = () => {
      let canvasPenList = [], combinePenList = [];
      let {pens} = canvasMeta2d.value.data();
      // console.log(canvasMeta2dData);
      if (pens && pens.length) {
        for (let i = 0; i < pens.length; i++) {
          canvasPenList.push({id: pens[i].id, chineseName: pens[i].chineseName});
          if (pens[i].name === "combine") combinePenList.push({id: pens[i].id, chineseName: pens[i].chineseName});
        }
      }
      that.canvasPenList = JSON.parse(JSON.stringify(canvasPenList));
      that.combinePenList = JSON.parse(JSON.stringify(combinePenList));
    }

    const watchActiveObjectDate = watch(() => that.activePelDate, (newActiveObjectDate, oldActiveObjectDate) => {
      if (JSON.stringify(oldActiveObjectDate) !== "{}") {
        // console.log(newActiveObjectDate);
        let activePelDate = JSON.parse(JSON.stringify(newActiveObjectDate));

        // 删除对应的视图数据
        for (let i = 0; i < delete_field_list.value.length; i++) delete activePelDate[delete_field_list.value[i]];
        canvasMeta2d.value.setValue(activePelDate);
      }
    }, {deep: true})

    const watchCurrentActivePelList = watch(() => current_active_pel_list, () => {
      findPenIdConfigFun();
    }, {deep: true})

    onMounted(() => {
      findPenIdConfigFun();
      queryCanvasMeta2dPens();
    })

    return {...toRefs(that), canvasMeta2d, current_active_pel_list, watchCurrentActivePelList, watchActiveObjectDate, changeEvent, clickItemButton,
      delete_field_list, queryCanvasMeta2dPens}
  }
})
</script>

<style lang="scss" scoped>
.currentElementEvents {
  height: 100%;

  .content_list {
    padding: 0 8px;
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-collapse-border-color);

    .content_list_li {
      display: flex;
      padding: 0 12px 0 16px;
      box-sizing: border-box;
      margin-bottom: 8px;

      .content_list_li_left {
        width: 80px;
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

        .el-input-number {
          width: 100%;
        }
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    .link_button {
      margin-bottom: 12px;

      .iconfont {
        font-size: 12px;
      }

      .link_text {
        margin-left: 6px;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    &:last-child {
      border-bottom: none;
    }
  }
}

:deep(.nullData) {
  height: 100%;

  .noCartImages {
    max-width: 120px;
  }

  .textFont {
    font-size: 14px;
    color: var(--color-gray);
    margin-bottom: 10px;
  }
}
</style>