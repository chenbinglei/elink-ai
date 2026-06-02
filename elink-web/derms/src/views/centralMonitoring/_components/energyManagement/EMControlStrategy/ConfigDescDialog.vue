<template>
  <Dialog v-model:isVisible="dialog_visible" closeOnClickModal :footerVisible="false" :title="titleName" width="580">
    <template v-slot:content>
      <div class="dialog-main">{{ describeContent }}</div>
    </template>
  </Dialog>
</template>

<script>
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name:"ConfigDescDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "说明"
    },
    describeContent: {
      type: String,
      default: ""
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      dialog_visible: props.isVisible,
    });

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return {...toRefs(that), watchDialogVisible, watchVisible};
  }
});
</script>

<style scoped lang="scss">
.dialog-main{
  color: #FFFFFF80;
}
</style>