<template>
  <el-drawer v-model="is_visible" :direction="direction" :size="size" :close-on-click-modal="closeOnClickModal" @close="handleClose">
    <template #title>
      <span class="title">用户信息</span>
    </template>

    <div class="content_body scrollbarStyle">
    <div class="content_top flex-jc-ai-center">
      <el-avatar :size="80" :src="userInfo?.userProfile">
        <span v-if="userInfo?.fullName">{{ userInfo?.fullName.slice(0, 1) }}</span>
      </el-avatar>
      <div class="userName">{{ $filters.moreData(userInfo?.fullName) }}</div>
    </div>
    <div class="content_bottom">
      <template v-for="(items, index) in dropdownList" :key="index">
        <div class="pointer user_list" @click="clickSkipFun(items.title, items.type)">
          <span :class="['iconfont',items.iconName]"></span>
          <span class="text">{{ items.title }}</span>
        </div>
      </template>
    </div>
  </div>
  </el-drawer>
</template>

<script>
import {useStore} from "vuex";
import {useRouter} from "vue-router";
import {removeToken} from "@/utils/auth";
import {onMounted, reactive, toRefs, defineComponent, computed, getCurrentInstance, watch} from "vue";

export default defineComponent({
  name: "UserOperationDrawer",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
  },
  emits: ["update:isVisible"],
  setup(props) {

    const store = useStore();
    const vueRouter = useRouter();
    const {emit} = getCurrentInstance();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const that = reactive({
      size: 260,
      direction: "ltr",
      closeOnClickModal: false,
      is_visible: props.isVisible,
      dropdownList: [{title: "退出登录", iconName: "icon-tuichu", type: "quit"}]
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
        vueRouter.push({path: "/personalCenter", query: {type: type}});
      }
    };

    const handleClose = ()=>{
      // that.is_visible = false;
      emit("update:isVisible", false);
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.is_visible = newVisible;
    }, {deep: true})

    onMounted(() => {});

    return {...toRefs(that), userInfo, clickSkipFun, watchVisible, handleClose};
  }
})
</script>
<style lang="scss" scoped>
.content_body {
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;

  .content_top {
    flex: 1;
    flex-direction: column;

    .userName {
      font-size: 21px;
      font-weight: bold;
      margin-top: 16px;
    }
  }

  .content_bottom {
    width: 100%;
    margin-top: 24px;

    .user_list {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 8px 0 8px 0;
      border-radius: 6px;
      box-sizing: border-box;
      border: 1px solid var(--el-border-color);

      .iconfont {
        font-size: 21px;
        margin-right: 8px;
      }

      .text {
        font-size: 18px;
      }

      &:hover {
        background: var(--el-bg-color-page);
      }
    }
  }
}
</style>