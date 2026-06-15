<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" closeOnClickModal disabledLoading width="480" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <template v-for="(item,index) in list" :key="index">
          <div class="card_li_class flex-jc-ai-center pointer" :class="{activeClass: item.id === activeOperateType}" @click="clickItemFun(item.id)">
            <span class="text">{{ item.name }}</span>
          </div>
        </template>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name: "ControlModelDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      titleName: "遥控",
      listLoading: false,
      dialog_visible: props.isVisible,
      
      activeOperateType: 1,
      list: [{id: 1, name: "开机"}, {id: 2, name: "关机"}, {id: 3, name: "重启"}]
    });

    const clickItemFun = (operateType)=>{
      that.activeOperateType = operateType;
    };

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
        }
      });
    };

    const initParamConfigFun = () => {
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun, clickItemFun};

  }
});
</script>

<style lang="scss" scoped>
.dialog-main{
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16px 12px;
  box-sizing: border-box;

  .card_li_class{
    width: 55px;
    height: 55px;
    border-radius: 50%;
    box-sizing: border-box;
    border: 1px solid #ffffff;
    background: radial-gradient(50% 50% at 50% 50%, #d9d9d9 0%, #818c8f 100%);

    .text{
      color: #ffffff;
      font-size: 14px;
      font-weight: 700;
    }
  }

  .activeClass{
    background: radial-gradient(50% 50% at 50% 50%, #46ee78 0%, #00814b 100%);
  }
}
</style>
