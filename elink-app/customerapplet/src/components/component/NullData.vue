<template>
  <div class="noCart">
    <van-image class="nullDataImage" :src="require(`@/assets/images/${imageName}.png`)" alt=""></van-image>
    <div class="textFont" v-if="isShowText">{{ words }}</div>
    <div class="content_solt" v-if="isShowSlot"><slot name="content"></slot></div>
  </div>
</template>

<script>
import { toRefs, reactive } from "vue";

export default {
  name: "nullData",
  props: {
    words: {
      type: String,
      default: "暂无数据"
    },
    imageName:{
      type: String,
      default: "nullDataImage"
    },
    isShowText:{
      type: Boolean,
      default: true
    },
    isShowSlot:{
      type: Boolean,
      default: false
    },
  },
  setup(props) {
    const that = reactive({
      words: props.words,
    });
    return { ...toRefs(that) };
  }
};
</script>

<style scoped lang="scss">
.noCart{
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .nullDataImage {
    width: 2.7rem;
    height: 2rem;
    margin-bottom: .1rem;
  }

  .textFont{
    color: #979797;
    font-size: .28rem;
    line-height: normal;
  }

  .content_solt{
    margin-top: .1rem;
  }
}
</style>
