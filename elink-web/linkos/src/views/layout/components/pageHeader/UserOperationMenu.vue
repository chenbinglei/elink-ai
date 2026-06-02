<template>
  <div class="userOperationMenu">
    <el-popover popper-class="headPortrait" :teleported="false" :show-arrow="false" placement="bottom" :width="180" trigger="click">
      <template #reference>
        <el-avatar :size="36" :src="userInfo?.userProfile">
          <span v-if="userInfo?.userName">{{ userInfo.userName.slice(0, 1) }}</span>
        </el-avatar>
      </template>

      <div class="user-dropdown white">
        <div class="userName" v-if="userInfo?.userName">{{ userInfo.userName }}</div>
        <div v-for="(items, index) in dropdownList" class="pointer user_list" @click="clickSkipFun(items.title, items.type)" :key="index">
          <span :class="['iconfont',items.iconName]"></span>
          <span>{{ items.title }}</span>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { removeToken } from "@/utils/auth";
import { toRefs, reactive, computed, defineComponent } from "vue";

export default defineComponent({
  name: "UserOperationMenu",
  setup() {
    const store = useStore();
    const vueRouter = useRouter();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const that = reactive({
      dropdownList: [
        { title: "账号信息", iconName: "icon-yonghuxinxi", type: "system" },
        { title: "退出登录", iconName: "icon-tuichu", type: "quit" }
      ]
    });

    const clickSkipFun = (title, type) => {
      that.userDropdown = !that.userDropdown;

      // 退出登录
      if (type === "quit") {
        removeToken(); //清除用户token
        localStorage.removeItem("USER_INFO"); //清除
        localStorage.removeItem("SIDEBAR");
        localStorage.removeItem("AUTH_ROUTER");
        location.reload(); // 为了重新实例化vue-router对象 避免bug
      }

      //跳转用户详情页
      if(type === "system"){
        vueRouter.push({ path: "/personalCenter", query: { type: type } });
      }
    };

    return { ...toRefs(that), userInfo,clickSkipFun };
  }
});
</script>

<style lang="scss" scoped>
.user-dropdown {
  border-radius: 4px;

  .user_list {
    display: flex;
    align-items: center;
    line-height: 32px;
    padding: 8px 0 8px 32px;
    box-sizing: border-box;

    .iconfont{
      font-size: 16px;
      margin-right: 8px;
    }

    &:hover {
      color: #1F74E2;
      background: #EDF0F5;
    }
  }

  .userName {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    box-sizing: border-box;
    padding: 8px 8px 8px 32px;
  }
}
</style>
