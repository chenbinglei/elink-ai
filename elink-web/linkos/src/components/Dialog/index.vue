<template>
  <el-dialog v-model="is_visible" :close-on-click-modal="closeOnClickModal" :custom-class="customClass" :show-close="showClose" :title="title" :width="width"
             @close="handleClose" @closed="handleClosed">
    <div v-loading="listLoading && !disabledLoading" class="dialog_content"><slot name="content"></slot></div>
    <template v-if="footerVisible" #footer>
      <div class="dialog-footer">
        <div class="dialog_footer_left">
          <slot name="bottomContent"></slot>
        </div>
        <div class="dialog_footer_right">
          <el-button v-if="cancelVisible" :disabled="listLoading && !disabledLoading" class="cancelBut" @click="handleClose('cancelBut')">
            <span>{{ cancelText }}</span>
          </el-button>
          <el-button v-if="confirmVisible" :disabled="listLoading && !disabledLoading" :loading="listLoading && disabledLoading"
                     class="confirmBut" type="primary" @click="clickConfirmBut('confirmBut')">
            <span>{{ confirmText }}</span>
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import {getCurrentInstance, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "Dialog",
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
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.is_visible = newVisible;
    }, {deep: true})

    return {...toRefs(that), clickConfirmBut, watchVisible, handleClose, handleClosed};
  }
});
</script>

<style lang="scss">
.el-dialog {
  //--el-dialog-border-radius: 6px;
  --el-dialog-padding-primary: 0px;

  .el-dialog__header {
    margin-right: 0;
    padding: 16px 24px;
    background: #EEEEEE;
    border-bottom: 1px solid rgba(224, 224, 224, 0.4);

    .el-dialog__title {
      color: #121C3F;
      font-size: 18px;
      font-weight: bold;
    }

    .el-dialog__close {
      font-size: 18px;
      --el-color-info: #242424;
    }
  }

  .el-dialog__body {
    --el-dialog-padding-primary: 12px;
    border-bottom: 1px solid rgba(224, 224, 224, 0.4);

    .dialog_content {
      box-sizing: border-box;
      padding: 0 var(--el-dialog-padding-primary);

      .el-form {
        .el-input, .el-select, .el-input-number {
          width: 100%;
          max-width: 520px;
        }

        .el-form-item{
          &:last-child{
            margin-bottom: 0;
          }
        }
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

      .cancelBut {
        --el-border: none;
        --el-button-bg-color: #F1F3FA;
      }
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
