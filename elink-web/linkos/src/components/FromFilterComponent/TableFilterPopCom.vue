<template>
  <div class="flex ai-center">
    <span class="liable">{{ tableName }}</span>
    <el-dropdown ref="dropdownRef" v-model:visible="dropdownVisible" trigger="click">
      <span class="iconfont icon-shaixuan"></span>
      <template #dropdown>
        <div class="filter_content">
          <div class="content_top">
            <template v-if="filterType === 'input'">
              <el-input v-model="fieldValue" :placeholder="placeholder" clearable></el-input>
            </template>
            <template v-if="filterType === 'select'">
              <el-radio-group v-model="fieldValue">
                <template v-for="item in selectDataList" :key="item.id">
                  <div class="radio_class">
                    <el-radio :label="item.id">{{ item.name }}</el-radio>
                  </div>
                </template>
              </el-radio-group>
            </template>
            <template v-if="filterType === 'checkbox'">
<!--              <el-input v-model="keyWordText" placeholder="请输入" clearable size="small" @change></el-input>-->
              <el-checkbox-group v-model="fieldValue">
                <template v-for="item in selectDataList" :key="item.id">
                  <div class="radio_class">
                    <el-checkbox :label="item.id">{{ item.name }}</el-checkbox>
                  </div>
                </template>
              </el-checkbox-group>
            </template>
          </div>
          <div class="content_bottom">
            <el-button size="small" @click="clickResetButFun">重置</el-button>
            <el-button size="small" type="primary" @click="clickConfirmButFun">确定</el-button>
          </div>
        </div>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "TableFilterPopCom",
  props: {
    value: {
      type: [String, Array, Object, Number],
      default: ""
    },
    tableName: {
      type: String,
      default: ""
    },
    placeholder: {
      type: String,
      default: "请输入"
    },
    // 筛选展示类型  input: 输入框  select： 单选   checkbox： 多选
    filterType: {
      type: String,
      default: "input"
    },
    // 字段名称
    fieldName: {
      type: String,
      default: "fieldName"
    },
    // 拉下选择数据列表
    dataList: {
      type: Array,
      default: () => []
    }
  },
  emits: ["changEvent", "update:value"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      keyWordText: "",
      fieldValue: null,
      selectDataList: [],
      dropdownVisible: false,
      oldFieldValue: props.value,
    })

    const clickConfirmButFun = () => {
      that.dropdownVisible = false;
      emit("update:value", that.fieldValue);
      emit("changEvent", {type: "confirmBut", fieldName: props.fieldName, fieldValue: that.fieldValue});
    }

    const clickResetButFun = () => {
      that.fieldValue = JSON.parse(JSON.stringify(that.oldFieldValue));

      that.dropdownVisible = false;
      emit("update:value", that.fieldValue);
      emit("changEvent", {type: "resetBut", fieldName: props.fieldName, fieldValue: that.fieldValue});
    }

    const watchValue = watch(() => props.value, (newValue) => {
      let fieldValue = JSON.parse(JSON.stringify(newValue));
      if(props.filterType === 'checkbox' && !fieldValue) fieldValue = [];
      that.fieldValue = JSON.parse(JSON.stringify(fieldValue));
    }, {deep: true, immediate: true})

    const watchDataList = watch(()=> props.dataList,(newDataList)=>{
      that.selectDataList = JSON.parse(JSON.stringify(newDataList ?? []));
    },{ deep: true, immediate: true});

    return {...toRefs(that), clickResetButFun, clickConfirmButFun, watchValue, watchDataList}
  }
})
</script>

<style lang="scss" scoped>
.liable {
  margin-right: 8px;
}

.filter_content {
  padding: 12px 8px;
  box-sizing: border-box;

  .content_top {
    max-height: 320px;
    margin-bottom: 12px;
    overflow-y: auto;

    .radio_class{
      width: 100%;
    }
  }

  .content_bottom {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>