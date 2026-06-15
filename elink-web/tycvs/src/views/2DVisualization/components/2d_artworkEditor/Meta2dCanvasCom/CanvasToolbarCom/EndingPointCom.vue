<template>
  <div class="endingPointCom">
    <template v-for="(item,index) in list" :key="index">
      <div class="content_list_li" @click="clickItemFun(item)">
        <div class="content_list_li_left">{{ item.name }}</div>
        <div class="content_list_li_right">
          <span :class="item.iconName" class="iconfont"></span>
        </div>
      </div>
    </template>
  </div>
</template>
<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {toArrowArray} from "@/utils/publicParam";
import {reactive, toRefs, defineComponent, getCurrentInstance, computed} from "vue";

export default defineComponent({
  name: 'EndingPointCom',
  setup() {

    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      list: toArrowArray
    })

    const clickItemFun = (data) => {
      canvasMeta2d.value.setOptions({ toArrow: data.fieldName });
      emit("changEvent",{ fieldName: "ending-point",iconName: data.iconName });
    }

    return {...toRefs(that), clickItemFun}
  }
})
</script>
<style lang="scss" scoped>
.endingPointCom {
  width: 100%;

  .content_list_li {
    height: 35px;
    padding: 0 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color);

    .content_list_li_left {
      font-size: 12px;
    }

    .iconfont {
      font-size: 28px;
    }

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }
}
</style>