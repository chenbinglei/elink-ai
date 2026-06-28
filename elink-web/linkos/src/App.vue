<template>
  <router-view/>
</template>
<script lang="ts">
import { useAppStore, useTagsViewStore } from '@/stores/index';
import {onMounted, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "App",
  setup() {

    const appStore = useAppStore();
    const tagsViewStore = useTagsViewStore();
    const that = reactive({
      userInfo: JSON.parse(localStorage.getItem('USER_INFO'))
    });

    const updateUserInfo = () => {
      appStore.updateUserInfo(that.userInfo);
      let userId = that.userInfo && that.userInfo.userId;
      appStore.updateOldUserId(userId);
    }

    // 重置带有返回按钮的页面
    const updateTagsViewBackArray = () => {
      let backButArray = localStorage.getItem("TAGS_VIEW_BACK_ARRAY");
      if (backButArray) tagsViewStore.updateBackButViews(JSON.parse(backButArray));
    }

    const debounce = (fn, delay) => {
      let timer = null;
      return () => {
        let context = this;
        let args = arguments;
        clearTimeout(timer);
        timer = setTimeout(() => {
          fn.apply(context, args);
        }, delay);
      };
    };

    const _ResizeObserver = window.ResizeObserver;
    window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
      constructor(callback) {
        callback = debounce(callback, 16);
        super(callback);
      }
    };

    /**
     * 修复 Element Plus el-select 占位符不可见问题
     * 根因：Element Plus 设置 .el-select__placeholder { z-index: -1 }
     * 导致占位符被 wrapper 背景色覆盖
     * 方案：直接设置内联样式（优先级高于所有 CSS 规则）
     */
    const fixSelectPlaceholder = () => {
      const placeholders = document.querySelectorAll(
        '.el-select__selected-item.el-select__placeholder, ' +
        '.el-tree-select .el-select__selected-item.el-select__placeholder'
      );
      placeholders.forEach((el: HTMLElement) => {
        el.style.zIndex = '1';
        el.style.position = 'absolute';
        el.style.opacity = '1';
        if (el.classList.contains('is-transparent')) {
          el.style.opacity = '1';
        }
        const span = el.querySelector('span');
        if (span) {
          (span as HTMLElement).style.color = '#a8abb2';
          (span as HTMLElement).style.fontSize = '14px';
        }
      });
    };

    // 初始修复 + MutationObserver 监听动态组件
    onMounted(() => {
      updateUserInfo();
      updateTagsViewBackArray();

      // 立即修复一次
      setTimeout(fixSelectPlaceholder, 100);
      setTimeout(fixSelectPlaceholder, 500);
      setTimeout(fixSelectPlaceholder, 1000);

      // 监听 DOM 变化，自动修复动态添加的 el-select
      const observer = new MutationObserver(() => {
        fixSelectPlaceholder();
      });
      observer.observe(document.body, { childList: true, subtree: true });
    });

    return {...toRefs(that), updateUserInfo, updateTagsViewBackArray, debounce, _ResizeObserver};
  }
})
</script>
<style lang="scss">
/* ==================== el-select 占位符可见性终极修复 ====================
 * 根因：Element Plus 设置 .el-select__placeholder { z-index: -1 }
 * 导致占位符被 wrapper 背景色覆盖，用户看不到 placeholder
 * 此样式在 App.vue 中定义，确保在所有组件之后加载，优先级最高 */
.el-select__selected-item.el-select__placeholder,
.el-tree-select .el-select__selected-item.el-select__placeholder {
  z-index: 1 !important;
  position: absolute !important;
  opacity: 1 !important;

  &.is-transparent {
    opacity: 1 !important;
  }

  span {
    color: #a8abb2 !important;
    font-size: 14px !important;
  }
}
/* ==================== el-select 占位符可见性终极修复结束 ==================== */
</style>
