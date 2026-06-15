<template>
  <div class="container">
    <div class="content_code" @click="changeStatus">
      <div v-for="(item, index) in unit" :class="{ activeClass: inputArray[index] }" class="verification_code">
        <div class="value">{{ inputArray[index] }}</div>
        <div v-if="inputArray.length === index && inputFocus" class="line"></div>
      </div>
    </div>
    <div class="content_input">
      <input ref="inputRef" v-model="inputValue" :maxlength="unit" type="text" @blur="changeStatus" @input="inputChange"/>
    </div>
  </div>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, ref, defineComponent} from "vue";

export default defineComponent({
  name: "VerificationCode",
  props: {
    unit: {
      type: Number,
      default: 6
    },
    // 是否实时触发
    ontime: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const inputRef = ref(null);
    const {emit} = getCurrentInstance();

    const that = reactive({
      inputValue: "",
      inputArray: [],
      inputFocus: false,
    });

    const changeStatus = () => {
      that.inputFocus = !that.inputFocus;
      if (that.inputFocus) inputRef.value.focus();
      if (!that.inputFocus) inputRef.value.blur();
    };

    const inputChange = () => {
      that.inputArray = that.inputValue.split("");
      if (props.ontime) {
        emit("changeEvent", that.inputValue);
      } else {
        if (that.inputArray.length === props.unit) {
          emit("changeEvent", that.inputValue);
        }
      }
    };

    return {...toRefs(that), inputRef, changeStatus, inputChange};
  }
});
</script>

<style lang="scss" scoped>
.container {
  width: 100%;

  .content_code {
    width: 100%;
    display: flex;
    justify-content: space-between;

    .verification_code {
      width: 48px;
      height: 54px;
      border-radius: 4px;
      background: #081A30;
      border: 1px solid #135278;
      box-sizing: border-box;

      display: flex;
      align-items: center;
      justify-content: center;

      .line {
        width: 2px;
        height: 80%;
        background: #079CEB;
        border-radius: 2px;
        animation: twinkling 1s infinite ease;
      }

      .value {
        //color: $fgGray;
        font-size: 16px;
      }
    }

    .activeClass {
      border: 1px solid #079CEB;
    }
  }

  .content_input {
    margin-top: -25px;

    input {
      border: none;
      color: transparent;
      background: transparent;

      &:focus {
        outline: 0 solid transparent;
      }
    }
  }


  @keyframes twinkling {
    0% {
      opacity: 0.2;
    }
    50% {
      opacity: 0.8;
    }
    100% {
      opacity: 0.2;
    }
  }
}
</style>
