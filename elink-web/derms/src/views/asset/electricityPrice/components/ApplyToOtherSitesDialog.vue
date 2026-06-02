<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <div class="alter_text">
          *将源电站的价格应用到目标电站，系统按源电站的电价配置对目标电站的设备进行下发，下发成功后立即生效。
        </div>
        <div style="display: flex;justify-content: space-between;height: 98%;">
          <div class="content_left" style="width: 50%;">
            <div>电价策略</div>
            <div class="content_list-border">
              <el-tree ref="tree" class="tree" default-expand-all :data="priceList" :props="defaultProps" show-checkbox
                node-key="id">
                <template #default="{ node, data }">
                  <div class="custom-tree-node">
                    <span class="tree-node-label">{{ node.label }}</span>
                  </div>
                </template>
              </el-tree>
            </div>
          </div>
          <div class="content-right" style="width: 50%;height: 100%;">
            <div>选择场站</div>
            <div class="content_list-border">
              <div class="content_header">
                <el-input v-model="siteName" :prefix-icon="Search" placeholder="请输入电站名称"
                  @input="realTimeSearchFun"></el-input>
              </div>
              <div class="content_bottom">
                <template v-if="siteIdArray && siteIdArray.length">
                  <el-checkbox-group v-model="checkSiteList">
                    <template v-for="(item, index) in siteIdArray" :key="item.id">
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
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import { Search } from "@element-plus/icons-vue";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import { getCurrentInstance, ref, reactive, toRefs, watch, defineComponent, onMounted } from "vue";
// import { applyPriceInfoById } from "@/api/operationManagement/CsStationDetails";
import { ElMessage } from "element-plus";
import { applyElectConfigToOtherSites } from "@/api/assetManagement/electricityPrice";

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
    priceList: {
      type: Array,
      default: () => []
    },

  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const tree = ref(null); // ✅ 声明 tree 引用
    const that = reactive({
      Search,
      siteName: "",

      checkSiteList: [],
      listLoading: false,
      titleName: "应用到其他站点",
      dialog_visible: props.isVisible,

      siteIdArray: [],
      allSiteIdArray: [],
      priceList: props.priceList,
      defaultProps: {
        children: 'children',
        label: (node) => {
          return node.label || node.strategyName; // 优先用 label，没有则用 start
        }
      }

    });
    const handleCheckChange = (data, checked) => {
      // 单选逻辑：只允许一个节点被选中
      if (checked) {
        this.priceList.forEach(item => {
          if (item.id !== data.id) {
            item.checked = false;
          }
        });
      }
    };


    // 应用充放电价格到指定电站下
    const clickConfirmBut = () => {
      if (!tree.value) {
        console.error('el-tree 未正确初始化');
        return;
      }
      const checkedNodes = tree.value.getCheckedNodes(false, true);
      // 过滤掉有 children 的节点
      const filteredNodes = checkedNodes.filter(node => !node.children || node.children.length === 0);
      console.log('that.priceList', filteredNodes);
      if (!filteredNodes || !filteredNodes.length) {
        ElMessage({ type: "error", showClose: true, message: "请选择电价策略！" });
        return;
      }
      if (!that.checkSiteList || !that.checkSiteList.length) {
        ElMessage({ type: "error", showClose: true, message: "请先选择应用站点！" });
        return;
      }

      that.listLoading = true;

      // 提取 id 并转为字符串
      const ids = filteredNodes
        .filter(node => node.id !== undefined && node.id !== null)
        .map(node => node.id)


      applyElectConfigToOtherSites({ electConfigIds: ids, siteIds: that.checkSiteList }).then(() => {
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch((err) => {
        console.log('err', err);
        that.listLoading = false;
        ElMessage({
          type: "error",
          showClose: true,
          message: `应用失败，目标站点电价参数冲突：${err.data}`
        });
      })
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
      findSiteListByUserId({}).then(res => {
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

    return { ...toRefs(that), tree, handleCheckChange, watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, realTimeSearchFun };
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

.content_left {
  padding: 12px 12px;
  box-sizing: border-box;
  height: 100%;


}

.content_list-border {
  border-radius: 6px;
  margin-top: 12px;
  border: 1px solid rgb(44, 74, 98);
  height: 98%;
  overflow: auto;

  box-sizing: border-box;
  color: #30f3ffff;
}
.dialog-main {
  height: 60vh;
}

.content-right {
  padding: 12px 12px;
  box-sizing: border-box;
}

.content_bottom {
  padding: 12px 12px;
  height: 40vh;
  overflow: auto;
  box-sizing: border-box;

  // .disabled_class {
  //   padding: 4px 10px;
  //   border-radius: 6px;
  //   background: #4abeff4d;
  //   box-sizing: border-box;
  //   margin-left: 12px;
  //   color: #30f3ffff;
  // }
}

.tree ::v-deep .el-tree-node__content {
  display: flex;
  align-items: center;
  position: relative;
}

.el-tree .el-tree-node .el-tree-node__content .el-tree-node__expand-icon {
  position: absolute;
  left: 0;
}

.tree ::v-deep .el-tree-node__content .el-checkbox {
  position: absolute;
  right: 0;
}

.custom-tree-node {
  position: relative;
}
</style>