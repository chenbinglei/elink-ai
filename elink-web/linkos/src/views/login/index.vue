<template>
  <div class="login">
    <div class="content_body">
      <div class="login_left"></div>
      <AccountPasswordLogin></AccountPasswordLogin>
    </div>
  </div>
</template>

<script>
import {onMounted, toRefs, reactive} from "vue";
import {AccountPasswordLogin} from "./components";

export default {
  name: "logOn",
  components:{AccountPasswordLogin},
  setup() {

    const that = reactive({});

    onMounted(() => {});

    return { ...toRefs(that) };
  }
}
</script>

<style scoped lang="scss">
.login {
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-image: url("@/assets/image/login_bg_image.png");

  .content_body{
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-right: 120px;
    box-sizing: border-box;
    background-color: rgba(255,255,255,.35);
  }
}
</style>
