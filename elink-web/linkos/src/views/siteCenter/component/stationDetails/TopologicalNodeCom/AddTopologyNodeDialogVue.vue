<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="640"
    @confirm="saveDialog" @cancel="cancelDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="120px" :validate-on-rule-change="false">
          <el-form-item label="节点类型：">
            <div class="node-type-container">
              <div v-for="item in nodeType" :class="{ active: formDialog.nodeType === item.id }" :key="item.id" class="node-item" @click="handleType(item)">
                <div class="nodeTypeImg" :class="{ 'active-img': formDialog.nodeType === item.id }">
                  <img :src="formDialog.nodeType === item.id ? item.activeImage : item.image" alt="" class="carousel_image" />
                </div>
                <div>{{ item.name }}</div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="节点名称" prop="nodeName">
            <el-input v-model="formDialog.nodeName" maxlength="32" placeholder="请输入名称" show-word-limit />
          </el-form-item>
          <el-form-item label="父级节点">
            <el-input v-model="newNode.parentName" disabled />
          </el-form-item>
          <template v-if="formDialog.nodeType === 3">
            <el-form-item label="高压电压等级">
              <el-input placeholder="请输入高压电压等级" v-model="formDialog.reaObject.maxVoltage">
                <template #append>kV</template>
              </el-input>
            </el-form-item>
            <el-form-item label="低压电压等级">
              <el-input placeholder="请输入低压电压等级" v-model="formDialog.reaObject.minVoltage">
                <template #append>kV</template>
              </el-input>
            </el-form-item>
          </template>
          <template v-if="formDialog.nodeType === 4 || formDialog.nodeType === 5 || formDialog.nodeType === 6 || formDialog.nodeType === 10 || formDialog.nodeType === 11 || formDialog.nodeType === 13
          ">
            <el-form-item label="关联设备">
              <el-select v-model="formDialog.deviceIds" placeholder="请选择关联设备" multiple filterable>
                <el-option v-for="item in deviceIdsArray" :key="item.id" :label="item.deviceName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="变压器安全容量" v-if="formDialog.nodeType === 4">
              <el-input placeholder="请输入内容变压器安全容量" v-model="formDialog.reaObject.safeByq">
                <template #append>kVa</template>
              </el-input>
            </el-form-item>

          </template>
          <template v-if="formDialog.nodeType === 7">

            <el-form-item label="光伏容量：">
              <el-input placeholder="光伏容量" v-model="formDialog.reaObject.photoRl">
                <template #append>kWp</template>
              </el-input>
            </el-form-item>

          </template>
          <template v-if="formDialog.nodeType === 8">
            <el-form-item label="多柜并机运行">
              <el-radio-group v-model="formDialog.reaObject.operation">
                <el-radio label="1">是</el-radio>
                <el-radio label="2">否</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="关联设备">
              <el-button type="text" :icon="Plus" @click="storageAdd">添加</el-button>
            </el-form-item>
            <el-table :data="formDialog.deviceIds" :max-height="tableMaxHeight" style="margin-bottom: 10px; width: 90%;margin-left: auto">
              <el-table-column fixed label="序号" type="index" width="60"></el-table-column>
              <el-table-column label="pcs">
                <template #default="{ row }">
                  <el-select v-model="row.pcsId" placeholder="请选择">
                    <el-option v-for="item in pcsOptions" :key="item.id" :label="item.deviceName" :value="item.id" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="电池簇">
                <template #default="{ row }">
                  <el-select v-model="row.batteryId" placeholder="请选择">
                    <el-option v-for="item in batteryOptions" :key="item.id" :label="item.deviceName" :value="item.id" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="">
                <template #default="{ $index }">
                  <span style="color: #FF1414;" @click="storageDelete($index)">删除</span>
                </template>
              </el-table-column>
            </el-table>
          </template>
          <el-form-item label="数据项" v-if="formDialog.nodeType === 4 ||
            formDialog.nodeType === 5 ||
            formDialog.nodeType === 6 ||
            formDialog.nodeType === 8 ||
            formDialog.nodeType === 10 || formDialog.nodeType === 13 ||
            formDialog.nodeType === 11">
            <div style="color: #4083FF;cursor: pointer; " @click="dataConfig(formDialog.nodeType)">配置</div>
          </el-form-item>
        </el-form>
      </div>

    </template>
  </Dialog>
  <DataItemDialog v-if="dataItenVisible" v-model:isVisible="dataItenVisible" @changedataItem="changedataItem" :titleName="dataName"
    :nodeType="formDialog.nodeType" :siteTopItemList='siteTopItemList'></DataItemDialog>
</template>

<script lang="ts">
import { ElMessage, ElMessageBox, } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { commonCharName, validateURL } from "@/utils/validate";
import { getCurrentInstance, onMounted, reactive, ref, defineExpose, toRefs, watch, defineComponent } from "vue";
import { findDeviceListBySiteId, saveOrUpdateTopoNodeInfo, saveSiteTopNode, findTopNodeInfoById } from "@/api/siteCenter/stationDetails";
import DataItemDialog from "./DataItemDialog.vue";
import { useAppStore } from '@/stores/index';


export default defineComponent({
  name: "AddTopologyNodeDialog",
  components: { DataItemDialog },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加节点"
    },
    siteRecordsId: {
      type: [String, Number],
      default: ""
    },
    // 当前选择的父级节点信息
    activeParentNodeInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
    newNode: {
      type: Object,
      default: () => ({})
    },
    topNodeId: {
      type: [String, Number],
      default: ""
    }
  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const appStore = useAppStore();
    onMounted(() => {
    });
    const validateNodeName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入正确的名称'));
      } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error('名称只能包含汉字、字母和数字'));
      } else {
        callback();
      }
    };

    const validateDeviceType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择类型"));
      } else {
        callback();
      }
    };

    const validateDeviceIds = (rule, value, callback) => {
      if (!value || (that.formDialog.nodeType === 1 && !value.length)) {
        callback(new Error("请选择关联设备"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      Plus,
      deviceIdsArray: [],
      dialog_visible: props.isVisible,
      dataItenVisible: false,// 添加数据项配置的弹窗
      formDialog: {
        nodeType: 1,//默认节点类型为拓扑图
        nodeName: null,
        reaObject: {
          operation: "1",// 默认多柜冰机运行为”是“
        },
        pageExtend: {},
        deviceIds: [],
        siteId: null
      },
      newNode: props.newNode,
      pcsOptions: [],
      batteryOptions: [],
      siteTopItemList: [],
      dataName: "数据项配置",
      nodeTypeArray: [{ id: 0, name: '计量节点' }, { id: 1, name: '设备节点' }],
      deviceTypeArray: [{ id: 1, name: '光伏' }, { id: 2, name: '储能' }, { id: 3, name: '电桩' }, { id: 4, name: '其他' }],
      typeArray: [{ id: 1, name: '光伏并网点' }, { id: 2, name: '储能并网点' }, { id: 3, name: '电桩并网点' }, { id: 4, name: '其他' }],
      // 节点类型
      nodeType: [
        { id: 1, name: '拓扑点', image: require("@/assets/image/TPJD.png"), activeImage: require("@/assets/image/TPJD-white.png") },
        { id: 2, name: '电网', image: require("@/assets/image/powerGrid.png"), activeImage: require("@/assets/image/DWJD-white.png") },
        { id: 3, name: '变压器', image: require("@/assets/image/BYQ.png"), activeImage: require("@/assets/image/BYQ-white.png") },
        { id: 4, name: '关口点', image: require("@/assets/image/BYQ.png"), activeImage: require("@/assets/image/BYQ-white.png") },
        { id: 5, name: '计量点', image: require("@/assets/image/JLJD.png"), activeImage: require("@/assets/image/JLJD-white.png") },
        { id: 6, name: '逆变器', image: require("@/assets/image/NBQ.png"), activeImage: require("@/assets/image/NBQ-white.png") },
        { id: 7, name: '光伏组件', image: require("@/assets/image/GF.png"), activeImage: require("@/assets/image/GF-white.png") },
        { id: 8, name: '储能柜', image: require("@/assets/image/CNG.png"), activeImage: require("@/assets/image/CNG-white.png") },
        { id: 9, name: '负荷', image: require("@/assets/image/FHJD.png"), activeImage: require("@/assets/image/FHJD-white.png") },
        { id: 10, name: '充电桩', image: require("@/assets/image/CDZ.png"), activeImage: require("@/assets/image/CDZ-white.png") },
        { id: 11, name: '开关', image: require("@/assets/image/KGJD.png"), activeImage: require("@/assets/image/KGJD-white.png") },
        { id: 12, name: '车辆', image: require("@/assets/image/CL.png"), activeImage: require("@/assets/image/CL-white.png") },
        { id: 13, name: '换电站', image: require("@/assets/image/HDZJD.png"), activeImage: require("@/assets/image/HDZJD-white.png") }
      ],
      rules: {
        nodeName: [{ required: true, message: '请输入正确的名称', trigger: 'blur' },
        { validator: validateNodeName, trigger: 'blur' }],
        deviceType: [{ required: true, trigger: "change", validator: validateDeviceType }],
        deviceIds: [{ required: true, trigger: "change", validator: validateDeviceIds }],
      }


    })


    // 配置项接收到的值
    const changedataItem = (e) => {
      that.formDialog.siteTopItems = e
    }
    const formDialogRef = ref(null);
    // 把 [对象, 字符串, ...] 转换成 [对象的id, 原字符串, ...]
    function normalizeIdList (arr) {
      if (!Array.isArray(arr)) return arr;
      return arr.map(item => {
        if (typeof item === 'object' && item !== null && item.id) {
          return item.id;
        }
        return item; // 已经是字符串或其他可直接使用的值
      });
    }
    const saveDialog = (node) => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          if (props.topNodeId) {

            let formDialog = {
              ...that.formDialog,
              deviceIds: normalizeIdList(that.formDialog.deviceIds),
              // siteTopItems: { ...that.formDialog.siteTopItems ?? that.formDialog.siteTopItemList }
              siteTopItems: that.formDialog.siteTopItems || that.formDialog.siteTopItemList
            }
            saveDialogDialog(formDialog)
          } else {

            that.formDialog.pageExtend = props.newNode
            that.formDialog.siteId = props.siteRecordsId;
            that.formDialog.parentId = props.newNode.parentId
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            console.log("新增节点", formDialog)
            // const receivedNewNode = reaObject;
            // if (formDialog.nodeType === 0) formDialog.deviceIds = [formDialog.deviceIds];
            saveDialogDialog(formDialog)


            if (appStore.TopoNodeLocationValue) {
              appStore.TopoNodeLocationValue.forEach(node => saveDialogDialog(node, 'Updata'));
            }
          }
        }
      })
    }
    const cancelDialog = (val) => {
      emit("changeEvent");
      appStore.updateNewTopoNode(null);
      that.dialog_visible = false;
    }
    const saveDialogDialog = (val, val1) => {
      saveSiteTopNode(val).then(res => {
        emit("changeEvent");
        that.dialog_visible = false;
        appStore.updateNewTopoNode(null);
        if (!val1) return ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      })
    }
    const changeDeviceTypeFun = (val, siteId) => {

      that.deviceIdsArray = [];
      delete that.formDialog.deviceIds;
      queryDeviceListBySiteId(val, siteId);
    }
    const handleType = (value, siteId) => {
      const nodeTypeId = value.nodeType ? value.nodeType : value.id
      that.deviceIdsArray = [];
      that.formDialog.deviceIds = null;
      that.formDialog.nodeType = nodeTypeId;
      that.formDialog.nodeName = value.name;
      // if (value.nodeType) {
      //   that.formDialog.nodeName = value.name;
      // } else if (!siteId) {
      //   that.formDialog.nodeName = null;
      // }
      const typeMap = {
        4: '1',
        5: '2',
        6: '3',
        10: '6',
        11: '7',
        13: '8'
      };

      if (typeMap[nodeTypeId]) {
        changeDeviceTypeFun(typeMap[nodeTypeId], siteId);
      }

      // 处理设备类型 8 的逻辑
      if (nodeTypeId === 8) {
        Promise.all([
          findDeviceListBySiteId({ siteId: props.siteRecordsId ? props.siteRecordsId : siteId, deviceType: '4' }),
          findDeviceListBySiteId({ siteId: props.siteRecordsId ? props.siteRecordsId : siteId, deviceType: '5' })
        ]).then(([pcsRes, batteryRes]) => {
          that.pcsOptions = pcsRes.data;
          that.batteryOptions = batteryRes.data;
        });
      }
    };
    // 储能柜关联设备添加
    const storageAdd = () => {
      that.formDialog.deviceIds = that.formDialog.deviceIds || [];
      that.formDialog.deviceIds.push({ pcsId: "", batteryId: "" });


    }
    // 储能柜删除
    const storageDelete = (index) => {
      ElMessageBox.confirm(`确定要删除这一行吗？`, "删除提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose: false, type: 'warning',
      }).then(() => {
        that.formDialog.deviceIds.splice(index, 1);
      }).catch(() => {
        console.log("取消删除！");
      });

    }
    // 点击打开配置项
    const dataConfig = (e) => {
      that.dataItenVisible = true;
      that.dataName = '数据项配置'
      that.formDialog.nodeType = e
    }

    // 根据站点id查询数据项的配置
    const queryDeviceListBySiteId = (val, siteId) => {
      findDeviceListBySiteId({ siteId: props.siteRecordsId ? props.siteRecordsId : siteId, deviceType: val }).then(res => {
        that.deviceIdsArray = res.data
        console.log("that.deviceIdsArray ", that.deviceIdsArray, that.formDialog.deviceIds)
      })
    }
    // 根据拓扑节点查询拓扑消息
    // const initParamConfigFun = () => {
    //   findTopNodeInfoById({ topNodeId: props.topNodeId }).then(res => {
    //     that.formDialog = { ...res.data }; // 保持响应性
    //     handleType(that.formDialog, that.formDialog.siteId)
    //     that.formDialog.reaObject = JSON.parse(that.formDialog.reaObject)

    //     const idToDeviceName = {};
    //     that.newNode.parentName = JSON.parse(that.formDialog.pageExtend).parentName
    //     that.formDialog.nodeName = res.data.nodeName;
    //     const deviceIds = JSON.parse(res.data.deviceIds || '[]');

    //     that.formDialog.deviceList?.forEach(item => {
    //       idToDeviceName[item.id] = item.deviceName;
    //     });

    //     console.log("deviceIds", deviceIds,that.formDialog.deviceList)
    //     if (that.formDialog.nodeType == '8') {
    //       const result = deviceIds.map(item => ({
    //         pcsId: item.pcsId,
    //         batteryId: item.batteryId,
    //         pcsName: idToDeviceName[item.pcsId],
    //         batteryName: idToDeviceName[item.batteryId]
    //       }))
    //       that.formDialog.deviceIds = result
    //     } else {
    //       const result = deviceIds.map(item => ({
    //         id: item,
    //         deviceName: idToDeviceName[item],
    //       }))
    //       that.formDialog.deviceIds = result
    //     }
    //     that.siteTopItemList = that.formDialog.siteTopItemList || [];

    //   })
    // }
    const initParamConfigFun = () => {
      findTopNodeInfoById({ topNodeId: props.topNodeId }).then(res => {
        that.formDialog = { ...res.data };
        handleType(that.formDialog, that.formDialog.siteId)
        that.formDialog.reaObject = JSON.parse(that.formDialog.reaObject)

        const idToDeviceName = {};
        that.newNode.parentName = JSON.parse(that.formDialog.pageExtend).parentName
        that.formDialog.nodeName = res.data.nodeName;
        const deviceIds = JSON.parse(res.data.deviceIds || '[]');

        that.formDialog.deviceList?.forEach(item => {
          idToDeviceName[item.id] = item.deviceName;
        });

        if (that.formDialog.nodeType == '8') {
          // 储能柜：deviceIds 是 [{ pcsId, batteryId }] 结构
          const result = deviceIds.map(item => ({
            pcsId: item.pcsId,
            batteryId: item.batteryId,
            pcsName: idToDeviceName[item.pcsId],
            batteryName: idToDeviceName[item.batteryId]
          }))
          that.formDialog.deviceIds = result
        } else {
          // 其他节点：deviceIds 是字符串数组，直接赋值
          that.formDialog.deviceIds = Array.isArray(deviceIds) ? deviceIds : [];
        }
        that.siteTopItemList = that.formDialog.siteTopItemList || [];
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      that.formDialog.nodeName = '拓扑点'
      if (props.topNodeId) initParamConfigFun();
    })

    return { ...toRefs(that), watchVisible, cancelDialog, saveDialogDialog, watchDialogVisible, changedataItem, formDialogRef, dataConfig, storageDelete, handleType, storageAdd, saveDialog, changeDeviceTypeFun, initParamConfigFun }
  }
})

</script>

<style lang="scss" scoped>
.node-type-container {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  /* 7 列 */
  gap: 10px;

  /* 项目之间的间距 */
  .node-item {
    text-align: center;
  }

  .nodeTypeImg {
    width: 20px;
    height: 20px;
    border: 1px solid #d8d8d8;
    margin: 1px auto;
    border-radius: 6px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    /* 添加阴影 */
    // background-image: url('@/assets/image/bgc-border.png');
    background-size: 100% 100%;
    margin-top: 5px;
    padding: 7px;
  }

  .active-img {
    border: 1px solid #fff;
  }

  .active {
    background-color: #01a1ff;
    color: #fff;
    border-radius: 5px;
  }
}

:deep(.el-form-item__label) {
  width: 140px !important;
}

:deep(.el-form-item__content) {
  line-height: 26px !important;
}

.carousel_image {
  width: 100%;
  height: 100%;
}
</style>