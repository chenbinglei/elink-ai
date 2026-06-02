<template>
  <div class="eventActionsCom">
    <template v-if="list && list.length">
      <template v-for="(item,index) in list" :key="index">
        <div class="event_class_list">
          <div class="event_class_list_title">
            <div class="title_name flex-ai-center">
              <el-icon size="14"><Right /></el-icon>
              <span style="margin-left: 2px">动作{{ index + 1 }}</span>
            </div>
            <span class="iconfont icon-line_guanbi pointer" @click.stop="clickItemButton(2,index)"></span>
          </div>
          <div class="content_config">

            <div class="content_list_li">
              <div class="content_list_li_left">动作类型</div>
              <div class="content_list_li_right">
                <el-select v-model="item.action" size="small" @change="changeActionFun(item,index)">
                  <el-option v-for="ts in actionTypeArray" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
                </el-select>
              </div>
            </div>

            <template v-if="item.action === 0">
              <div class="content_list_li">
                <div class="content_list_li_left">链接地址</div>
                <div class="content_list_li_right">
                  <el-input v-model="item.value" size="small" placeholder="URL"></el-input>
                </div>
              </div>
              <div class="content_list_li">
                <div class="content_list_li_left">打开方式</div>
                <div class="content_list_li_right">
                  <el-radio-group v-model="item.params">
                    <el-radio v-for="(ts,ti) in openMethodArray" :key="ti" :value="ts.id" size="small">{{ts.name}}</el-radio>
                  </el-radio-group>
                </div>
              </div>
            </template>


            <template v-if="item.action >= 1 && item.action <= 4">
              <div class="content_list_li">
                <div class="content_list_li_left">对象类型</div>
                <div class="content_list_li_right">
                  <el-radio-group v-model="item.targetType">
                    <el-radio v-for="(ts,ti) in targetTypeArray" :key="ti" :value="ts.id" size="small">{{ts.name}}</el-radio>
                  </el-radio-group>
                </div>
              </div>

              <template v-if="item.action === 1">
                <div class="content_list_li">
                  <div class="content_list_li_left">更改对象</div>
                  <div class="content_list_li_right">
                    <template v-if="item.targetType === 'id'">
                      <el-select v-model="item.params" size="small" filterable clearable placeholder="默认自己">
                        <el-option v-for="ts in canvasPenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
                      </el-select>
                    </template>
                    <template v-if="item.targetType === 'tag'">
                      <el-select v-model="item.params" size="small" filterable clearable placeholder="默认自己">
                        <el-option v-for="ts in combinePenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
                      </el-select>
                    </template>
                  </div>
                </div>
                <div class="content_list_li">
                  <div class="content_list_li_left">属性数据</div>
                  <div class="content_list_li_right">
                    <el-table :data="item.valueList" style="width: 100%">
                      <el-table-column align="center" label="属性名" width="60">
                        <template #default="{ row }">
                          <el-tooltip effect="dark" :content="row.key" placement="top-start">
                            <span>{{ row.label }}</span>
                          </el-tooltip>
                        </template>
                      </el-table-column>
                      <el-table-column align="center" label="属性值">
                        <template #default="{ row }">
                          <div class="flex-jc-ai-center" style="width: 100%">
                            <template v-if="row.type">
                              <template v-if="row.type === 'bool'">
                                <el-switch v-model="row.value" size="small" :active-value="true" :inactive-value="false"></el-switch>
                              </template>
                              <template v-else-if="row.type === 'color'">
                                <lx-input-color-picker v-model:color="row.value" pickerType="text" @changEvent="tableValueChangeFun(row,index)" />
                              </template>
                              <template v-else-if="row.type === 'string'">
                                <el-input v-model="row.value" @change="tableValueChangeFun(row,index)"></el-input>
                              </template>
                              <el-input-number v-else v-model="row.value" style="width: 100%" controls-position="right" size="small" @change="tableValueChangeFun(row,index)" />
                            </template>
                            <el-input v-else v-model="row.value" @change="tableValueChangeFun(row,index)"></el-input>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column align="center" width="40">
                        <template #header>
                          <el-popover :width="200" placement="bottom-start" trigger="hover">
                            <template #reference>
                              <div class="content_body">
                                <el-icon size="18" color="#4583ff"><CirclePlusFilled /></el-icon>
                              </div>
                            </template>
                            <div class="content_list scrollbarStyle">
                              <el-input v-model="customFieldName" placeholder="自定义" style="width: 100%;" @keyup.enter="customFieldNameFun(index)"></el-input>
                              <div class="content_list_line"></div>
                              <template v-for="(ts,ti) in dynamic_data_list" :key="ti">
                                <div v-if="ts.key" class="content_popover_list" @click="clickButItemFun(ts,index)">
                                  <div class="content_list_li_left">{{ ts.label }}</div>
                                  <div class="content_list_li_right"></div>
                                </div>
                                <div v-else class="content_list_line"></div>
                              </template>
                            </div>
                          </el-popover>
                        </template>
                        <template #default="{ row,$index }">
                          <div class="pointer" @click="clickRemoveFilledFun(row,$index,index)">
                            <el-icon size="18" color="#FF2626"><RemoveFilled /></el-icon>
                          </div>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
              </template>

              <template v-if="item.action === 2 || item.action === 3 || item.action === 4">
                <div class="content_list_li">
                  <div class="content_list_li_left">播放对象</div>
                  <div class="content_list_li_right">
                    <template v-if="item.targetType === 'id'">
                      <el-select v-model="item.value" size="small" filterable clearable placeholder="默认自己">
                        <el-option v-for="ts in canvasPenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
                      </el-select>
                    </template>
                    <template v-if="item.targetType === 'tag'">
                      <el-select v-model="item.value" size="small" filterable clearable placeholder="默认自己">
                        <el-option v-for="ts in combinePenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
                      </el-select>
                    </template>
                  </div>
                </div>
                <div class="content_list_li">
                  <div class="content_list_li_left">动画名称</div>
                  <div class="content_list_li_right">
                    <el-input v-model="item.params" size="small" placeholder="缺省第一个动画"></el-input>
                  </div>
                </div>
              </template>
            </template>

            <template v-if="item.action === 5">
              <div class="content_list_li fd-column">
                <div class="content_list_title">function javascriptFn(pen) {</div>
                <div content="content_list_code">
                  <CodeEditor v-model:value="item.value"></CodeEditor>
                </div>
                <div class="content_list_title">}</div>
              </div>
            </template>
            <template v-if="item.action === 6 || item.action === 7">
              <div class="content_list_li">
                <div class="content_list_li_left">{{ item.action === 6 ? '函数名称' : '消息名称' }}</div>
                <div class="content_list_li_right">
                  <el-input v-model="item.value" size="small" placeholder="名称"></el-input>
                </div>
              </div>
              <div class="content_list_li">
                <div class="content_list_li_left">{{ item.action === 6 ? '函数参数' : '消息参数' }}</div>
                <div class="content_list_li_right">
                  <el-input v-model="item.params" size="small" placeholder="参数"></el-input>
                </div>
              </div>
            </template>

          </div>
        </div>
      </template>
    </template>

    <div class="link_button">
      <el-link type="primary" @click="clickItemButton(1)">
        <span class="iconfont icon-tianjia"></span>
        <span class="link_text">添加动作</span>
      </el-link>
    </div>
  </div>
</template>

<script>
import {pelAttributeNameList} from "@/utils/publicParam";
import CodeEditor from "@/components/component/CodeEditor.vue";
import {Right,CirclePlusFilled,RemoveFilled} from '@element-plus/icons-vue'
import {reactive, toRefs, defineComponent, onMounted, watch, getCurrentInstance} from "vue";
import {LxInputColorPicker} from "@/components/LxComponents";

export default defineComponent({
  name: "EventActionsCom",
  components:{LxInputColorPicker, Right,CirclePlusFilled,RemoveFilled,CodeEditor},
  props: {
    actions: {
      type: Array,
      default: () => []
    },
    // 画布所有图元
    canvasPenList: {
      type: Array,
      default: () => []
    },
    // 画布所有组图元
    combinePenList: {
      type: Array,
      default: () => []
    },
  },
  emits: ["changeEvent", "update:actions"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      customFieldName: '',
      dynamic_data_list: pelAttributeNameList,
      targetTypeArray: [{id: "id", name: "图元"}, {id: "tag", name: "组"}],
      openMethodArray: [{id: "_blank", name: "新页面"}, {id: "_self", name: "当前页面"}],
      actionTypeArray: [
        {id: 0, name: "打开链接"},
        {id: 1, name: "更改属性"},
        {id: 2, name: "播放动画"},
        {id: 3, name: "暂停动画"},
        {id: 4, name: "停止动画"},
        {id: 5, name: "自定义函数"},
        {id: 6, name: "全局函数"},
        {id: 7, name: "发送消息"}
      ],
    })

    const clickItemButton = (operationType, index) => {
      if (operationType === 1) that.list.push({});
      if (operationType === 2) that.list.splice(index, 1);
    }

    // 动作类型改变
    const changeActionFun = (data,index)=>{
      that.list[index].value = "";
      that.list[index].params = "";
      that.list[index].valueList = [];
      if(data.action === 0) that.list[index].params = "_blank";
    }

    const customFieldNameFun = (index)=>{
      clickButItemFun({ key: that.customFieldName,label: "自定义" },index);
      that.customFieldName = "";
    }

    // 属性数据执行
    const clickButItemFun = (data,index)=>{
      if(!that.list[index].valueList) that.list[index].valueList = [];
      that.list[index].valueList.push({ key: data.key,type: data.type,label: data.label });
    }

    // 绑定属性数据执行
    const tableValueChangeFun = (row,index)=>{
      if(that.list[index].value?.constructor !== Object)that.list[index].value = {};
      that.list[index].value[row.key] = row.value;
    }

    // 删除属性绑定数据
    const clickRemoveFilledFun = (row,tableIndex,index)=>{
      delete that.list[index].value[row.key];
      that.list[index].valueList.splice(tableIndex,1);
    }

    const watchList = watch(() => that.list, (newList) => {
      emit("update:actions", newList);
    }, {deep: true})

    onMounted(() => {
      that.list = JSON.parse(JSON.stringify(props.actions));
    })

    return {...toRefs(that), watchList, clickItemButton, changeActionFun, clickButItemFun, customFieldNameFun, tableValueChangeFun, clickRemoveFilledFun}
  }
})
</script>

<style lang="scss" scoped>
.event_class_list {
  padding-top: 10px;
  margin-bottom: 12px;

  .event_class_list_title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color-title);
    font-size: 14px;
    margin-bottom: 4px;
  }

  .content_list_li {
    display: flex;
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

    .content_list_title{
      font-size: 12px;
      color: var(--color);
      margin-bottom: 5px;
      &:last-child{
        margin-top: 5px;
        margin-bottom: 0;
      }
    }

    :deep(.cm-editor){
      height: 240px;
    }

    .content_list_li_right {
      flex: 1;
      width: 2px;
      display: flex;
      align-items: center;
      //padding-left: 8px;
      //box-sizing: border-box;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  &:last-child {
    margin-bottom: 0;
  }
}

:deep(.el-table){
  .cell{
    padding: 0 2px;
  }
}

.link_button {
  margin-top: 12px;

  .iconfont {
    font-size: 14px;
  }

  .link_text {
    margin-left: 6px;
  }
}

.content_list {
  width: 100%;
  max-height: 380px;
  overflow-y: auto;

  .content_popover_list {
    height: 32px;
    padding: 0 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color);
    font-size: 12px;

    .iconfont {
      font-size: 12px;
    }

    .link_text{
      margin-left: 6px;
    }

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }

  .content_list_line {
    height: 1px;
    margin: 4px 0;
    background-color: #EAEEF1;
  }
}
</style>