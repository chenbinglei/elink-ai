<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <div class="alter_text">
          *将源电站的价格应用到目标电站，系统按源电站的电价配置对目标电站的设备进行下发，下发成功后立即生效。
        </div>
        <div class="content_body content_border">
          <div class="content_header">
            <el-input v-model="siteName" :prefix-icon="Search" placeholder="请输入电站名称" @input="realTimeSearchFun"></el-input>
          </div>
          <div class="content_bottom">
            <template v-if="siteIdArray && siteIdArray.length">
              <el-checkbox-group v-model="checkSiteList">
                <template v-for="(item,index) in siteIdArray" :key="item.id">
                  <div class="content_list">
                    <el-checkbox :disabled="item.id === siteId" :value="item.id">
                      <div class="content_li flex ai-center">
                        <span class="siteName">{{ item.siteName }}</span>
                        <template v-if="item.id === siteId">
                          <div class="disabled_class">源电站</div>
                        </template>
                      </div>
                    </el-checkbox>
                  </div>
                </template>
              </el-checkbox-group>
            </template>
            <null-data v-else words="暂无站点配置。"></null-data>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {Search} from "@element-plus/icons-vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";
import {applyPriceInfoById} from "@/api/operationManagement/CsStationDetails";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: "ApplyToOtherSitesDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    siteId: {
      type: [Number, String],
      default: ""
    },
    activePricingId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      siteName: "",

      checkSiteList: [],
      listLoading: false,
      titleName: "应用到其他站点",
      dialog_visible: props.isVisible,

      siteIdArray: [],
      allSiteIdArray: [],
    });

    // 应用充放电价格到指定电站下
    const clickConfirmBut = () => {
      if(!that.checkSiteList || !that.checkSiteList.length){
        ElMessage({type: "error", showClose: true, message: "请先选择应用站点！"});
        return;
      }

      that.listLoading = true;
      applyPriceInfoById({pirceId: props.activePricingId,siteIds: that.checkSiteList}).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({type: "success", showClose: true, message: "操作成功！"});
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const realTimeSearchFun = () => {
      let siteIdArray = that.allSiteIdArray.filter(item => {
        let siteName = item.siteName ?? "";
        return siteName.indexOf(that.siteName) !== -1;
      });
      that.siteIdArray = JSON.parse(JSON.stringify(siteIdArray));
    };

    // 查询站点下拉列表
    const initParamConfigFun = () => {
      findSiteInfoByUserId({scenarioTypes: "3", timer: new Date()}).then(res => {
        let list = res.data ? res.data : [];
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.allSiteIdArray = JSON.parse(JSON.stringify(list));
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, realTimeSearchFun};
  }
});
</script>

<style lang="scss" scoped>
.alter_text {
  color: #C7C7C7;
  font-size: 12px;
  margin-bottom: 12px;
}

.content_header {
  padding: 12px 12px;
  box-sizing: border-box;
  border-bottom: 1px solid rgba(16, 110, 196, 0.6);
}

.content_bottom {
  padding: 12px 12px;
  height: 40vh;
  overflow: auto;
  box-sizing: border-box;

  .disabled_class {
    padding: 4px 10px;
    border-radius: 6px;
    background: #4abeff4d;
    box-sizing: border-box;
    margin-left: 12px;
    color: #30f3ffff;
  }
}
</style>