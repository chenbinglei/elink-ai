<template>
  <div class="app-container flex jc-center align-center">
      <!-- 空白页 解决在没有配置首页权限时关闭最后一个页卡会跳至404页面的问题-->
    <null-data words="空白页"></null-data>
  </div>
</template>

<script lang="ts">
export default {
  name: "blankPage"
}
</script>

<style lang="scss" scoped></style>
