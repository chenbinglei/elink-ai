<template>
  <div class="modelClassCard">
    <div class="content_list_left">
      <el-image :src="cardInfo.sortLogo">
        <template #error>
          <div class="image-slot"><el-icon><Picture/></el-icon></div>
        </template>
      </el-image>
    </div>
    <div class="content_list_right">
      <div class="right_top">
        <div class="right_top_text textTwo">{{ $filters.moreData(cardInfo.sortName) }}</div>
        <template v-if="pageType === 1">
          <span class="iconfont icon-bianji pointer" @click.stop="clickItemButton(2)"></span>
          <span class="iconfont icon-shanchu pointer" @click.stop="clickItemButton(3)"></span>
        </template>
        <span v-else class="iconfont icon-bianji pointer" @click.stop="clickItemButton(2)"></span>
      </div>
      <div class="right_bottom">
        <template v-if="pageType === 1">
          <span class="number">{{ $filters.numberNull(cardInfo.childrenNum) }}</span>
          <span>个子类</span>
        </template>
        <template v-if="pageType === 2">
          <span class="number">ID：</span>
          <span>{{ $filters.moreData(cardInfo.id) }}</span>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import {Box, Picture} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from "element-plus";
import {deleteSortById} from "@/api/modelCenter/modelClassification";
import {getCurrentInstance, onMounted, reactive, toRefs} from "vue";

export default {
  name: "ModelClassCard",
  components: {Box, Picture},
  props: {
    cardInfo: {
      type: Object,
      default: () => {}
    },
    pageType:{
      type: [String,Number],
      default: 1
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({})

    const clickItemButton = (operate) => {
      if(operate === 2){
        emit("changeEvent",{ operate: operate,cardInfo: props.cardInfo });
      }

      if(operate === 3){
        ElMessageBox.confirm(`确定删除（${ props.cardInfo.sortName }）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning', closeOnClickModal: false,
          beforeClose: (action, instance, done)=> {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteSortById({ id: props.cardInfo.id }).then(()=>{
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          emit("changeEvent",{ operate: operate });
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    onMounted(() => {

    })

    return {...toRefs(that), clickItemButton}
  }
}
</script>

<style lang="scss" scoped>
.modelClassCard {
  width: 100%;

  padding: 12px 16px;
  box-sizing: border-box;
  border: 1px solid #DBDBDD;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;

  .content_list_left {
    width: 62px;
    height: 62px;

    .el-image {
      width: 100%;
      height: 100%;
      border-radius: 4px;
      border: 1px solid #DBDBDD;
      box-sizing: border-box;

      .image-slot {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #DBDBDD;
        font-size: 32px;
        color: #F5F5F5;
      }
    }
  }

  .content_list_right {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 4px 0 4px 10px;
    box-sizing: border-box;

    .right_top {
      display: flex;
      align-items: center;

      .right_top_text {
        flex: 1;
        -webkit-line-clamp: 1;
      }

      .icon-shanchu{
        color: #FD393A;
        margin-left: 4px;
      }
    }

    .right_bottom {
      font-size: 12px;

      .number {
        font-size: 16px;
        font-weight: bold;
      }
    }
  }
}
</style>
