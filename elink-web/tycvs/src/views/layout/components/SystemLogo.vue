<template>
  <div class="systemLogo pointer" @click="clickSystemLogoFun">
    <div class="icon_class flex-jc-ai-center">
      <span class="iconfont icon-zutai"></span>
    </div>
    <span class="logo_text">Topology</span>
    <UserOperationDrawer v-if="userOperationVisible" v-model:isVisible="userOperationVisible"></UserOperationDrawer>
  </div>
</template>

<script>
import UserOperationDrawer from "./UserOperationDrawer.vue";
import {onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "SystemLogo",
  components: {UserOperationDrawer},
  setup() {

    const that = reactive({
      userOperationVisible: false
    });

    const clickSystemLogoFun = ()=>{
      that.userOperationVisible = true;
    }

    onMounted(() => {});

    return {...toRefs(that),clickSystemLogoFun };
  }
})
</script>

<style lang="scss" scoped>
.systemLogo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 0;
  box-sizing: border-box;

  .icon_class{
    width: 28px;
    height: 28px;
    border-radius: 4px;
    background: #1E71EC;

    .iconfont{
      color: #FFFFFF;
      font-size: 21px;
    }
  }

  .logo_text{
    color: #151F41;
    font-size: 18px;
    font-weight: bold;
    margin-left: 10px;
  }
}
</style>
