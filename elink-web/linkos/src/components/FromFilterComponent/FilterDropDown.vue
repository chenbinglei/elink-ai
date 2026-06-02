<template>
  <div class="filter_content">
    <el-dropdown ref="dropdownRef" trigger="click">
      <span class="pointer iconfont icon-shaixuan icon_class" @click.stop="clickIconFun"></span>
      <template #dropdown>
        <div class="filterDropDown">
          <div class="card-top scrollbarStyle">
            <HandleMenus ref="handleMenuRef" :handleMenuArray="objectData.list" :isSearchInput="false" :isShowHeader="false" :showCheckbox="objectData.isCheckbox"
                         defaultExpandAll rightArrowClass @handleMenuEvent="handleMenuEvent"></HandleMenus>
          </div>
          <div class="card-btm">
            <el-button size="small" @click="clickReset">重置</el-button>
            <el-button size="small" type="primary" @click="clickConfirm">确定</el-button>
          </div>
        </div>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import {getCurrentInstance, reactive, toRefs, onMounted, ref, defineComponent, watch} from "vue";

export default defineComponent({
  name: "FilterDropDown",
  props: {
    formInline: {
      type: Object,
      default: () => {
        return {}
      }
    },
    //展示数据
    objectData: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changEvent"],
  setup(props) {
    const dropdownRef = ref(null);
    const handleMenuRef = ref(null);
    const {emit} = getCurrentInstance();

    const that = reactive({
      firstEmit: true, // 是否第一次进入
      fieldSelectList: [], // 选择的列表数据
      oldFieldSelectList: [], // 选择的列表数据
      object_data: props.objectData,
      oldFormInline: JSON.parse(JSON.stringify(props.formInline))
    });

    const initHandleMenuFun = () => {
      let fieldSelectList = [];
      if (props.formInline[that.object_data.fieldName]) {
        fieldSelectList = props.formInline[that.object_data.fieldName];
      }
      that.fieldSelectList = JSON.parse(JSON.stringify(fieldSelectList));
      setFieldSelectList();
    };

    const setFieldSelectList = (operateType) => {
      handleMenuRef.value.setCheckedKeysFun(that.fieldSelectList);
      that.oldFieldSelectList = JSON.parse(JSON.stringify(that.fieldSelectList));
      if (operateType === "clickReset") clickConfirm();
    }

    const handleMenuEvent = (data) => {
      // console.log(data)
      // console.log(that.object_data)
      if (that.firstEmit) {
        if (props.formInline[that.object_data.fieldName]) {
          handleMenuRef.value.setCheckedKeysFun(props.formInline[that.object_data.fieldName]);
        }
        that.firstEmit = false;
        return
      }
      that.fieldSelectList = data.allSelectIdArray || [data.id];
    }

    // 重置  判断多选单选   isCheckbox（true） 代表多选，无则单选
    const clickReset = () => {
      let fieldSelectList = [];
      if (that.oldFormInline[that.object_data.fieldName]) {
        fieldSelectList = that.oldFormInline[that.object_data.fieldName];
      }
      that.fieldSelectList = fieldSelectList;
      setFieldSelectList("clickReset");
    };

    // 确定
    const clickConfirm = () => {
      let nameList = [];
      let fieldSelectList = JSON.parse(JSON.stringify(that.fieldSelectList));
      for (let i = 0; i < fieldSelectList.length; i++) {
        let findItem = that.object_data.list.find(item => item.id === fieldSelectList[i]);
        if(findItem) nameList.push(findItem.name);
      }

      // console.log(dataEmit);
      emit("changEvent", {
        type: "FilterDropDown",
        value: fieldSelectList,
        nameList: nameList.join("，"),
        title: that.object_data.title,
        fieldName: that.object_data.fieldName,
      });

      that.oldFieldSelectList = JSON.parse(JSON.stringify(fieldSelectList));
      // console.log(that.oldFieldSelectList);
    };

    //下拉框显示
    const clickIconFun = () => {
      handleMenuRef.value.setCheckedKeysFun(that.oldFieldSelectList);
      dropdownRef.value.handleOpen();
    }

    const watchFormInline = watch(()=>props.formInline,(newFormInline)=>{
      initHandleMenuFun();
    },{ deep: true });

    onMounted(() => {
      initHandleMenuFun();
    });

    return {...toRefs(that), initHandleMenuFun, handleMenuRef, handleMenuEvent, clickReset, clickConfirm, setFieldSelectList, clickIconFun, dropdownRef, watchFormInline};
  }
});
</script>

<style lang="scss" scoped>
.filter_content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .icon_class {
    margin-left: 12px;
  }
}

.filterDropDown {
  display: flex;
  flex-direction: column;

  .card-top {
    display: flex;
    flex-direction: column;
    max-height: 220px;
    overflow-y: auto;
    box-sizing: border-box;
    border-bottom: 1px solid #E3E3E3;

    :deep(.handleMenu) {
      width: 100%;
      border: none;
      min-width: 180px;
    }
  }

  .card-btm {
    padding: 8px;
    display: flex;
    justify-content: space-around;
    box-sizing: border-box;
  }
}
</style>
