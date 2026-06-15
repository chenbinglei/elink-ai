<template>
  <div class="content">
    <div class="content_left">
      <img alt="" class="list_icon" src="@/assets/image/list_icon.png" />
      <span class="text">{{ title }}</span>
    </div>
    <div class="content_right">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "TableHeaderTitle",
  props: {
    title: {
      type: String,
      default: "标题"
    }
  },
  setup() {

    const that = reactive({});

    return {...toRefs(that)};
  }
})
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .content_left{
    display: flex;
    align-items: center;

    .list_icon {
      width: 32px;
      margin-right: 5px;
    }

    .text {
      color: #121C3F;
      font-size: 18px;
      font-weight: bold;
      white-space: nowrap;
    }
  }

  .content_right{
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}

</style>