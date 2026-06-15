<template>
  <div class="nullData">
    <svg-icon class="noCartImages" iconName="emptyData"></svg-icon>
    <div v-if="isWords" class="textFont">{{ words }}</div>
    <div class="textFont"><slot name="content"></slot></div>
  </div>
</template>

<script lang="ts">
import {toRefs, reactive, defineComponent} from "vue";

export default defineComponent({
  name: "NullData",
  props: {
    words: {
      type: String,
      default: "暂无数据"
    },
    isWords: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const that = reactive({
      words: props.words,
    });
    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.nullData {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  padding: 20px 0;
  box-sizing: border-box;

  .noCartImages {
    font-size: 90px;
    margin-bottom: 10px;
    color: var(--el-color-primary);
  }

  .textFont {
    color: #989898;
    font-size: 14px;
    line-height: normal;
  }
}
</style>
