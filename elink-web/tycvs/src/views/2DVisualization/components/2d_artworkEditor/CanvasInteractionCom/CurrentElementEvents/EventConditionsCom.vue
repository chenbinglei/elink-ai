<template>
  <div class="eventActionsCom">
    <template v-if="list && list.length">
      <template v-for="(item,index) in list" :key="index">
        <div class="event_class_list">
          <div class="event_class_list_title">
            <div class="title_name flex-ai-center">
              <el-icon size="14"><Right /></el-icon>
              <span style="margin-left: 2px">条件{{ index + 1 }}</span>
            </div>
            <span class="iconfont icon-line_guanbi pointer" @click.stop="clickItemButton(2,index)"></span>
          </div>
          <div class="content_config">

            <div class="content_list_li">
              <div class="content_list_li_left">条件类型</div>
              <div class="content_list_li_right">
                <el-radio-group v-model="item.type">
                  <el-radio v-for="(ts,ti) in typeArray" :key="ti" :value="ts.id" size="small">{{ts.name}}</el-radio>
                </el-radio-group>
              </div>
            </div>

            <template v-if="item.type ==='fn'">
              <div class="content_list_li fd-column">
                <div class="content_list_title">function condition(pen) {</div>
                <div content="content_list_code">
                  <CodeEditor v-model:value="item.fnJs"></CodeEditor>
                </div>
                <div class="content_list_title">}</div>
              </div>
            </template>
            <template v-else>
              <div class="content_list_li">
                <div class="content_list_li_left">属性名</div>
                <div class="content_list_li_right">
                  <el-select-v2 v-model="item.key" :props="props" :options="attributeNameArray" placeholder="属性名" allow-create filterable clearable/>
                </div>
              </div>
              <div class="content_list_li">
                <div class="content_list_li_left">关系运算</div>
                <div class="content_list_li_right">
                  <el-select v-model="item.operator" size="small">
                    <el-option v-for="ts in operatorArray" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
                  </el-select>
                </div>
              </div>
              <div class="content_list_li">
                <div class="content_list_li_left">运算对象</div>
                <div class="content_list_li_right">
                  <el-select v-model="item.valueType" size="small" placeholder="固定值" @change="valueTypeChangeFun(index)">
                    <el-option v-for="ts in valueTypeArray" :key="ts.id" :label="ts.name" :value="ts.id"></el-option>
                  </el-select>
                </div>
              </div>
              <template v-if="item.valueType === 'prop'">
                <div class="content_list_li">
                  <div class="content_list_li_left">对象</div>
                  <div class="content_list_li_right">
                    <el-select v-model="item.target" size="small" filterable clearable placeholder="图元id">
                      <el-option v-for="ts in canvasPenList" :key="ts.id" :label="ts.chineseName" :value="ts.id"></el-option>
                    </el-select>
                  </div>
                </div>
              </template>
              <div class="content_list_li">
                <div class="content_list_li_left">{{ item.valueType === 'prop' ? '属性' : '值'  }}</div>
                <div class="content_list_li_right">
                  <el-input v-model="item.value" size="small"></el-input>
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
        <span class="link_text">添加触发条件</span>
      </el-link>
    </div>
  </div>
</template>

<script lang="ts">
import {Right} from "@element-plus/icons-vue";
import {pelAttributeNameList} from "@/utils/publicParam";
import CodeEditor from "@/components/component/CodeEditor.vue";
import {reactive, toRefs, defineComponent, onMounted, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "EventActionsCom",
  components: {CodeEditor, Right},
  props: {
    conditions: {
      type: Array,
      default: () => []
    },
    canvasPenList: {
      type: Array,
      default: () => []
    },
  },
  emits: ["update:conditions"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      typeArray:[{id: "",name: "关系条件"},{id: "fn",name: "高级条件"}],
      valueTypeArray:[{id: "",name: "固定值"},{id: "prop",name: "对象属性值"}],
      operatorArray:[
        {id: "=",name: "="},
        {id: "!=",name: "!="},
        {id: ">",name: ">"},
        {id: "<",name: "<"},
        {id: ">=",name: ">="},
        {id: "<=",name: "<="},
        {id: "[)",name: "包含"},
        {id: "![)",name: "不包含"}
      ],
      props:{ label: 'label', value: 'key'},
      attributeNameArray: pelAttributeNameList,
    })

    const clickItemButton = (operationType, index) => {
      if (operationType === 1) that.list.push({ type: "",valueType: "",operator: "=" });
      if (operationType === 2) that.list.splice(index, 1);
    }

    const valueTypeChangeFun = (index)=>{
      if(that.list[index].valueType !== "prop"){
        delete that.list[index].target
      }
    }

    const watchList = watch(() => that.list, (newList) => {
      emit("update:conditions", newList);
    }, {deep: true})

    onMounted(() => {
      that.list = JSON.parse(JSON.stringify(props.conditions));
    })

    return {...toRefs(that), watchList, clickItemButton, valueTypeChangeFun}
  }
})
</script>

<style lang="scss" scoped>
.event_class_list {
  padding-top: 10px;

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

    .content_list_li_right {
      flex: 1;
      width: 2px;
      display: flex;
      align-items: center;
      padding-left: 8px;
      box-sizing: border-box;
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

    &:last-child {
      margin-bottom: 0;
    }
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
</style>