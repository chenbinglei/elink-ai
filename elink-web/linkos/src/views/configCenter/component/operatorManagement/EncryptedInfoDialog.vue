<template>
  <Dialog v-model:isVisible="dialog_visible" :footerVisible=false :title="titleName" closeOnClickModal width="640px">
    <template v-slot:content>
      <div class="content_body">
        <template v-for="(item,index) in list" :key="index">
          <div class="content_list">
            <div class="content_list_left">{{ item.name }}</div>
            <div class="content_list_right">{{ $filters.moreData(returnDataInfo[item.fieldName]) }}</div>
          </div>
        </template>
      </div>
    </template>
  </Dialog>
</template>
<script>
import {defineComponent, getCurrentInstance, onMounted, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "AddOperatorDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增运营商"
    },
    operatorId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "加密设置",
      returnDataInfo: {},
      dialog_visible: props.isVisible,
      list: [
        {name: "运营商密钥", fieldName: ''},
        {name: "消息密钥", fieldName: ''},
        {name: "消息密钥初始化向量", fieldName: ''},
        {name: "签名密钥", fieldName: ''},
      ]
    })

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {

    })

    return {...toRefs(that), watchVisible, watchDialogVisible}
  }
})
</script>
<style lang="scss" scoped>
.content_body {
  padding-left: 32px;
  box-sizing: border-box;

  .content_list {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .content_list_left {
      color: #666666;
      font-size: 14px;
      min-width: 160px;
    }

    .content_list_right {
      color: #121C3F;
      font-size: 14px;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}

</style>