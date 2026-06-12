<template>
  <div class="userOperationMenu">
    <el-popover v-model:visible="popoverVisible" :teleported="false" placement="bottom-end" popper-class="user-popover"
      trigger="click">
      <template #reference>
        <div class="content_body pointer">
          <el-avatar class="avatar" :src="userInfo?.userProfile">
            <span v-if="userProfile?.fullName">{{ userProfile?.fullName.slice(0, 1) }}</span>
          </el-avatar>
          <div class="content_center">
            <div class="userName">{{ $filters.moreData(userInfo?.fullName) }}</div>
          </div>
          <div class="detail_icon">
            <span v-if="popoverVisible" class="iconfont icon-shangjiantou"></span>
            <span v-else class=" iconfont icon-xiajiantou"></span>
          </div>
        </div>
      </template>
      <div class="user-dropdown">
        <template v-for="(items, index) in dropdownList" :key="index">
          <div class="pointer user_list" @click="clickSkipFun(items.title, items.type)">
            <span :class="['iconfont', items.iconName]"></span>
            <span>{{ items.title }}</span>
          </div>
        </template>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import { useRouter } from "vue-router";
import { removeToken } from "@/utils/auth";
import { computed, defineComponent, reactive, toRefs } from "vue";

export default defineComponent({
  name: "UserOperationMenu",
  setup() {
    const appStore = useAppStore();
    const vueRouter = useRouter();

    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({
      popoverVisible: false,
      dropdownList: [
        // {title: "账号信息", iconName: "icon-yonghuxinxi", type: "system"},
        { title: "退出登录", iconName: "icon-tuichu", type: "quit" }
      ]
    });

    const clickSkipFun = (title, type) => {
      that.userDropdown = !that.userDropdown;

      // 退出登录
      if (type === "quit") {
        removeToken(); //清除用户token
        localStorage.clear();
        location.reload(); // 为了重新实例化vue-router对象 避免bug
      }

      //跳转用户详情页
      if (type === "system") {
        vueRouter.push({ path: "/personalCenter", query: { type: type } });
      }
    };

    return { ...toRefs(that), userInfo, clickSkipFun };
  }
});
</script>

<style lang="scss">
.user-popover {
  padding: 12px !important;
  width: 120px !important;
  min-width: 120px !important;
  --el-font-size-base: 14px;
}
</style>
<style lang="scss" scoped>
.userOperationMenu {
  z-index: 1000;
  transform: translateY(9px);
}

.content_body {
  display: flex;
  align-items: center;

  .avatar {
    width: 40px;
    height: 40px;

    :deep(img){
      background-color: #FFFFFF;
    }
  }

  .content_center {
    margin: 0 13px 0 8px;

    .userName {
      font-size: 14px;
      color: #FFFFFF;
      height: 19px;
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
  }

  .detail_icon .iconfont {
    color: #FFFFFF;
    font-size: 12px;
    transition: all .28s;
  }
}


.user-dropdown {
  border-radius: 4px;

  .user_list {
    display: flex;
    align-items: center;
    padding: 8px 0 8px 0;
    box-sizing: border-box;

    .iconfont {
      font-size: 16px;
      margin-right: 8px;
    }

    &:hover {
      color: #FFFFFF;
      background: #135278;
    }
  }
}
</style>
