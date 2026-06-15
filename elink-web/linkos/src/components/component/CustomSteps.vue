<template>
  <div class="customSteps">
    <template v-for="(item,index) in steps_array" :key="index">
      <div :class="{ steps_list: true,activeClass:active === item.id,preAllClass: active > item.id }">
        <div class="steps_li">
          <div class="iconTitle">
            <span v-if="active > item.id" class="iconfont icon-duihao"></span>
            <span v-else>{{ item.iconTitle }}</span>
          </div>
          <div class="title">{{ item.title }}</div>
        </div>
        <div class="steps_line">
          <div class="line"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "CustomSteps",
  props: {
    //	显示方向
    direction: {
      type: String,
      default: "horizontal"
    },
    // 设置当前激活步骤
    active: {
      type: [String, Number],
      default: 0
    },
    stepsArray: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const that = reactive({
      active_index: props.active,
      steps_array: props.stepsArray,
    })

    const watchSteps = watch([() => props.stepsArray, () => props.active], ([newStepsArray, newActive]) => {
      that.active_index = newActive;
      that.steps_array = newStepsArray;
    }, {deep: true})

    return {...toRefs(that), watchSteps}
  }
});
</script>

<style lang="scss" scoped>
.customSteps {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .steps_list {
    flex: 1;
    display: flex;
    box-sizing: border-box;

    .steps_li {
      display: flex;
      align-items: center;
      flex-direction: column;

      .iconTitle {
        width: 40px;
        height: 40px;
        border: 1px solid #BBBBBB;
        color: #242424;
        font-size: 16px;
        text-align: center;
        line-height: 40px;
        border-radius: 50%;
      }

      .title {
        width: max-content;
        font-size: 14px;
        color: #242424;
        margin-top: 4px;
      }
    }

    .steps_line {
      flex: 1;
      height: 40px;
      display: flex;
      align-items: center;
      //padding: 0 22px;
      box-sizing: border-box;

      .line {
        width: 100%;
        height: 1px;
        background: #D8D8D8;
      }
    }
  }

  .activeClass {
    .steps_li {
      .iconTitle {
        color: #ffffff;
        background: #1F74E2;
      }

      .title {
        color: #1F74E2;
      }
    }
  }

  .preAllClass {
    .steps_li {

      .iconTitle {
        color: #079CEB;
        background: #ffffff;
        border: 1px solid #079CEB;
      }

      .title {
        color: #242424;
      }
    }

    .steps_line {
      .line {
        background: #1F74E2;
      }
    }
  }

  .steps_list:last-child {
    flex: initial;

    .steps_line {
      display: none;
    }
  }
}
</style>
