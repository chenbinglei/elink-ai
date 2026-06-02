<template>
  <div class="login">
    <div class="content_body">
      <div class="login_left"></div>
      <AccountPasswordLogin></AccountPasswordLogin>
    </div>

<!--    <div class="recordNumber">-->
<!--      <a href="https://beian.miit.gov.cn" target="_blank">{{ systemConfigInfo.recordNumber }}</a>-->
<!--    </div>-->
  </div>
</template>

<script setup>
import {onMounted, reactive} from "vue";
import {AccountPasswordLogin} from "./components";

const that = reactive({});

onMounted(() => {});
</script>

<style lang="scss" scoped>
.login {
  width: 100%;
  height: 100%;
  position: relative;
  //background-size: 100% 100%;
  //background-repeat: no-repeat;

  .content_body {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    //padding-right: 180px;
    box-sizing: border-box;
  }

  .recordNumber {
    width: 100%;
    position: absolute;
    bottom: 24px;
    text-align: center;
    color: #666666;
    font-size: 14px;
  }
}

@media (max-width: 768px) {

  .login {
    width: 100vw;

    .content_body {
      padding-right: 0;
      justify-content: center;

      .login_left{
        display: none;
      }
    }
  }
}
</style>
