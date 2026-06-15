<template>
  <el-dialog v-model="is_visible" :close-on-click-modal="closeOnClickModal" :custom-class="customClass" :show-close="showClose" :title="title" :width="width" @close="handleClose" @closed="handleClosed">
    <div v-loading="listLoading && !disabledLoading" class="dialog_content"><slot name="content"></slot></div>
    <template v-if="footerVisible" #footer>
      <div class="dialog-footer">
        <div class="dialog_footer_left"><slot name="bottomContent"></slot></div>
        <div class="dialog_footer_right">
          <template v-if="cancelVisible">
            <el-button :disabled="listLoading && !disabledLoading" class="cancelBut" @click="handleClose('cancelBut')">
              <span>{{ cancelText }}</span>
            </el-button>
          </template>
          <template v-if="confirmVisible">
            <el-button :disabled="listLoading && !disabledLoading" :loading="listLoading && disabledLoading" type="primary" @click="clickConfirmBut('confirmBut')">
              <span>{{ confirmText }}</span>
            </el-button>
          </template>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "CustomDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: "标题"
    },
    width: {
      type: [String, Number],
      default: "1000px"
    },
    //  clearDialogPadding
    customClass: {
      type: String,
      default: ""
    },
    // 是否点击确认关闭弹框
    manualEnterClose: {
      type: Boolean,
      default: true
    },
    // 是否点击取消关闭弹框
    manualCancelClose: {
      type: Boolean,
      default: true
    },
    // 底部确定取消按钮是否显示
    footerVisible: {
      type: Boolean,
      default: true
    },
    // 是否展示右上角关闭按钮
    showClose: {
      type: Boolean,
      default: true
    },
    // 点击空白处是否关闭
    closeOnClickModal: {
      type: Boolean,
      default: false
    },
    cancelText: {
      type: String,
      default: "取消"
    },
    //取消按钮是否显示
    cancelVisible: {
      type: Boolean,
      default: true
    },

    //确定按钮是否显示
    confirmVisible: {
      type: Boolean,
      default: true
    },
    confirmText: {
      type: String,
      default: "确定"
    },
    // 是否现在加载动画，按钮禁止点击
    listLoading: {
      type: Boolean,
      default: false
    },
    // 默认为 禁用 按钮
    disabledLoading: {
      type: Boolean,
      default: false  // true : 按钮旋转状态  // false : 禁用
    }
  },
  emits:["update:isVisible","confirm","cancel"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      is_visible: props.isVisible
    });

    // 确认回调
    const clickConfirmBut = (operateType) => {
      emit("confirm", {type: operateType});
      if (props.manualEnterClose) handleClose();
    };

    // 关闭对话框
    const handleClose = () => {
      emit("cancel");
      if(props.manualCancelClose && !props.listLoading )handleClosed();
    };

    const handleClosed = ()=>{
      emit("update:isVisible", false);
    };

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.is_visible = newVisible;
    }, {deep: true});

    return {...toRefs(that), clickConfirmBut, watchVisible, handleClose, handleClosed};
  }
});
</script>

<style lang="scss">
.el-dialog {
  --el-dialog-padding-primary: 0px;
  --el-bg-color: radial-gradient(50.13% 49.9% at 125.69% 64.1%, #01132df2 0%, #0b1e39f2 100%);

  .el-dialog__header {
    margin-right: 0;
    padding: 16px 24px;
    border-bottom: 1px solid rgba(19,82,120,0.4);

    .el-dialog__title {
      color: #FFFFFF;
      font-size: 18px;
    }

    .el-dialog__close {
      font-size: 21px;
      --el-color-info: #FFFFFF;
    }
  }

  .el-dialog__body {
    --el-dialog-padding-primary: 12px;
    border-bottom: 1px solid rgba(19,82,120,0.4);

    .dialog_content {
      box-sizing: border-box;
      padding: var(--el-dialog-padding-primary);

      .el-form {
        .el-input, .el-select, .el-input-number,.el-textarea, .el-upload{
          width: 100%;
          max-width: 520px;
        }

        //.el-form-item{
        //  &:last-child{
        //    margin-bottom: 0;
        //  }
        //}
      }
    }
  }

  .el-dialog__footer {
    --el-dialog-padding-primary: 16px;
    padding: var(--el-dialog-padding-primary);

    .dialog-footer{
      display: flex;
      align-items: center;

      .dialog_footer_left{
        flex: 1;
        margin-right: 12px;
      }

      //.cancelBut{
      //  --el-button-hover-bg-color: #0847681a;
      //  --el-button-hover-border-color: #084768;
      //  --el-button-active-bg-color: rgba(7,156,235,0.2);
      //  --el-button-active-border-color: rgba(7,156,235,0.2);
      //}
    }
  }
}

.clearDialogPadding{
  .el-dialog__body{
    --el-dialog-padding-primary: 0;
  }
}

.marginDialogClass{
  --el-dialog-margin-top: 5vh;
}
</style>
