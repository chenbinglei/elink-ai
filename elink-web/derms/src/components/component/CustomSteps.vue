<template>
  <div class="customSteps">
    <template v-for="(item,index) in steps_array" :key="index">
      <div :class="{activeClass:active === item.id,preAllClass: active > item.id }" class="steps_list">
        <div class="steps_li">
          <div class="iconTitle">
            <span v-if="active > item.id">
              <span class="iconfont icon-duihao"></span>
            </span>
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

<script>
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
      position: relative;

      .iconTitle {
        width: 40px;
        height: 40px;
        font-size: 12px;
        text-align: center;
        line-height: 40px;
        border-radius: 50%;
        background: rgba(7, 156, 235, 0.1);

        .icon-duihao {
          color: #079CEB;
        }
      }

      .title {
        width: max-content;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.4);
        position: absolute;
        bottom: -20px;
      }
    }

    .steps_line {
      flex: 1;
      height: 40px;
      display: flex;
      align-items: center;
      box-sizing: border-box;

      .line {
        width: 100%;
        border-top: 1px dashed rgba(7, 156, 235, 0.4);
      }
    }
  }

  .activeClass {
    .steps_li {
      .iconTitle {
        background: rgba(7, 156, 235, 0.8);
      }

      .title {
        color: rgba(7, 156, 235, 0.8);
      }
    }
  }

  .preAllClass {
    .steps_li {
      .title {
        color: #079CEB;
      }
    }

    .steps_line {
      .line {
        border-color: rgba(7, 156, 235, 0.8);
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
