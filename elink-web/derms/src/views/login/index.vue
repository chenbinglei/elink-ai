<template>
  <div
    class="w-full h-full flex items-start justify-center relative pt-74px box-border login-container"
  >
    <div class="w-978px flex flex-col justify-start items-center relative">
      <!-- 系统logo -->
      <div class="w-full h-68px flex justify-center items-center">
        <img
          class="h-full w-auto aspect-ratio"
          :class="{
            'opacity-0': !systemLogoDisplay,
          }"
          :src="systemLogo"
          alt="系统logo"
        />
      </div>
      <!-- 系统名称 -->
      <div
        class="w-full h-144px flex justify-center items-start pt-40px box-border system-name"
      >
        <span class="text-42px font-bold text-white">{{
          systemDisplayName
        }}</span>
      </div>
      <!-- 登录表单 -->
      <LoginForm :client-id="clientId" />
    </div>
    <div
      class="w-full h-100px flex justify-center items-center absolute bottom-0 left-0 right-0"
      :class="{
        'opacity-0': !systemLogoDisplay,
      }"
    >
      <LoginFooter />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { LoginForm, LoginFooter } from "./_components";

const route = useRoute();
const { query } = route;
// 通过query上的id判断是否是单站点登录
const clientId = computed(() => {
  const { id } = query;
  return id ? "microgrid-client" : "derms-client";
});
const systemLogoDisplay = computed(() => {
  const { showSysLogo } = window.IEMSConfig || {};
  return showSysLogo;
});
const systemLogo = computed(() => {
  const { logo } = query;
  return logo ? `/img/login/site/${logo}.webp` : "/logo.png";
});
const systemDisplayName = computed(() => {
  const { id, name } = query;
  const { systemName, siteSystemName } = window.IEMSConfig || {};
  return name ?? (id ? siteSystemName : systemName);
});

onMounted(() => {});
</script>

<style lang="scss" scoped>
.login-container {
  background-image: url("/img/login/loginBg1.webp");
  background-size: 100% 100%;

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("/img/login/loginBg2.webp") no-repeat center center fixed;
  }

  .system-name {
    background-image: url("/img/login/loginTitleBg.webp");
    background-repeat: no-repeat;
    background-size: 100% 100%;
  }
}
</style>
