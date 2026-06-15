<template>
  <el-form :model="formInline">
    <el-form-item label="系统标题：">
      <el-input v-model="formInline.systemName" style="width: 320px;" maxlength="12" placeholder="Please input"
        show-word-limit type="text" @change="selectionChangeFun" />
    </el-form-item>
    <el-form-item label="URL：">
      <a :href="siteUrl" target="_blank">{{ siteUrl }}</a>
    </el-form-item>
    <el-form-item label="功能配置：">

      <el-tree :data="list" ref="configTree" style="max-height: 360px;" :show-checkbox="true" default-expand-all
        node-key="value" @check="selectionChangeFun" />
    </el-form-item>
    <el-form-item v-if="isClickEditBut">
      <el-button @click="clickCancelButFun">取消</el-button>
      <el-button type="primary" @click="clickSaveButFun">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts">
import { computed, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { updateSiteSetUp } from "@/api/siteCenter/stationDetails";
import { defineComponent, getCurrentInstance, onMounted, reactive, toRefs, watch } from "vue";

export default defineComponent({
  name: "SiteAppFunConfig",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changEvent"],
  setup (props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      list: [],
      formInline: {},
      oldFormInline: '{}',
      isClickEditBut: false,
    });

    const configTree = ref();
    const siteUrl = computed(() => {
      return `${window.siteConfig.iemsUrl}/monitor/${that.formInline.siteId}?id=${that.formInline.siteId}`;
    });


    // 根据资产分类id查询图形分类列表
    const queryGraphTypeListByTypeId = () => {
      that.listLoading = true;
      that.list = [{
        label: '主页',
        value: 'monitorHome',
        // disabled: true,
        checkAble: false
      }, {
        label: '主接线图',
        value: 'mainWiringDiagram',
        // disabled: true
      }, {
        label: '系统监控',
        value: 'systemMonitoring',
        // disabled: true,
        children: [{
          label: '光伏监控',
          value: 'photovoltaics',
        }, {
          label: '储能监控',
          value: 'energyStorage',
        }, {
          label: '电桩监控',
          value: 'electricPiles',
        }]
      }, {
        label: '告警日志',
        value: 'alarmLog',
        // disabled: true,
        children: [{
          label: '故障告警',
          value: 'faultAlarm',
        }]
      }, {
        label: '数据分析',
        value: 'dataAnalysis',
        // disabled: true,
        children: [{
          label: '历史数据',
          value: 'historyData',
        }]
      }];
      that.listLoading = false;
    }

    // 取消保存
    const clickCancelButFun = () => {
      that.isClickEditBut = false;
      that.formInline = JSON.parse(that.oldFormInline);
      configTree.value.setCheckedKeys(that.formInline.readwriteObject);
    }

    const clickSaveButFun = () => {
      that.formInline.readwriteObject = configTree.value.getCheckedKeys(true);
      console.log(that.formInline, 'that.formInline')
      ElMessageBox.confirm(`确定保存当前站点功能应用配置吗？`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在保存...';
            updateSiteSetUp({ ...that.formInline }).then(() => {
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
        emit("changEvent");
        that.isClickEditBut = false;
        ElMessage({ type: "success", showClose: true, message: "更改成功！" });
      }).catch(() => {
        console.log("取消删除！");
      });
    }

    const selectionChangeFun = () => {
      that.isClickEditBut = true;
    }

    // const setFormData = () => {
    //   let returnDataInfo = JSON.parse(JSON.stringify(props.returnDataInfo ?? {}));

    //   if (!returnDataInfo.siteId) { return; }
    //   try {
    //     if (returnDataInfo.readwriteObject) {
    //       returnDataInfo.readwriteObject = JSON.parse(returnDataInfo.readwriteObject)
    //     }
    //   } catch (e) {
    //     returnDataInfo.readwriteObject = [];
    //   }
    //   console.log(returnDataInfo, '传递的数据')
    //   that.formInline = returnDataInfo;
    //   that.oldFormInline = JSON.stringify(returnDataInfo);
    //   configTree.value?.setCheckedKeys(returnDataInfo.readwriteObject);
    // }
    const setFormData = () => {
      let returnDataInfo = JSON.parse(JSON.stringify(props.returnDataInfo ?? {}));

      if (!returnDataInfo.siteId) { return; }

      try {
        if (returnDataInfo.readwriteObject) {
          returnDataInfo.readwriteObject = JSON.parse(returnDataInfo.readwriteObject);
        } else {
          returnDataInfo.readwriteObject = [];
        }
      } catch (e) {
        returnDataInfo.readwriteObject = [];
      }

      that.formInline = returnDataInfo;
      that.oldFormInline = JSON.stringify(returnDataInfo);

      if (configTree.value) {
        configTree.value.setCheckedKeys(returnDataInfo.readwriteObject);
      }
    };

    onMounted(() => {
      setFormData();
      queryGraphTypeListByTypeId();
    });

    watch(() => props.returnDataInfo, () => {
      setFormData();
    }, { deep: true, immediate: false })

    return { ...toRefs(that), configTree, siteUrl, queryGraphTypeListByTypeId, selectionChangeFun, clickCancelButFun, clickSaveButFun }
  }
})

</script>

<style lang="scss" scoped></style>