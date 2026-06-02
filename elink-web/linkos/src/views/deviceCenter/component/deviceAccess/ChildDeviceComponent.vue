<template>
  <div class="childDeviceComponent">
    <null-data :isWords="isWords">
      <template #content>
        <div class="alterText">
          <span class="text">网关子设备请在【</span>
          <span class="text_color">边缘管理-边缘接入</span>
          <span class="text">】进行配置</span>
        </div>
      </template>
    </null-data>
  </div>
</template>

<script>
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "ChildDeviceComponent",
  setup() {
    const that = reactive({
      isWords: false
    })

    return {...toRefs(that)}
  }
})
</script>

<style lang="scss" scoped>
.childDeviceComponent {
  height: 100%;

  .text_color{
    color: #1F74E2;
    font-weight: bold;
  }
}
</style>
