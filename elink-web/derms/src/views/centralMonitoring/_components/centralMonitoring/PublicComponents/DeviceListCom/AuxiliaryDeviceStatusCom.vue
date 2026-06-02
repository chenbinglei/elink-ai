<template>
  <div class="content_body">
    <el-tooltip content="制冷" placement="top-start">
      <span :class="{ open_class: deviceInfo.coolingState }" class="iconfont icon-zhileng"></span>
    </el-tooltip>
    <el-tooltip content="制热" placement="top-start">
      <span :class="{ open_class: deviceInfo.heatingState }" class="iconfont icon-zhire"></span>
    </el-tooltip>
    <el-tooltip content="外风机" placement="top-start">
      <span :class="{ open_class: deviceInfo.externalfanState }" class="iconfont icon-fengshan"></span>
    </el-tooltip>
    <el-tooltip content="内风机" placement="top-start">
      <span :class="{ open_class: deviceInfo.internalfanState }" class="iconfont icon-chushi"></span>
    </el-tooltip>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "AuxiliaryDeviceStatusCom",
  props: {
    deviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup() {
    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.iconfont {
  font-size: 20px;
  color: #ffffff80;
  margin-right: 12px;

  &:last-child {
    margin-right: 0;
  }
}

.open_class {
  color: #00F7FF;
}
</style>