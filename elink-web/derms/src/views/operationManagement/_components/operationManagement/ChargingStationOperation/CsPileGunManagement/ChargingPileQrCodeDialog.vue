<template>
  <Dialog v-model:isVisible="dialog_visible" closeOnClickModal cancelText="关闭" :confirmVisible="false" :title="titleName" width="520">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="充电枪：" prop="gunCode">
            <el-select v-model="formDialog.gunCode" placeholder="请选择充电枪" @change="gunCodeChange">
              <el-option v-for="item in gunCodeArray" :key="item.gunCode" :label="item.gunName" :value="item.gunCode"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <div class="content_qr" v-loading="qrCodeLoading">
              <div class="content_qr_top flex-jc-ai-center">
                <el-image :src="qrCodeValue">
                  <template #error>
                    <div class="image-slot flex-jc-ai-center">
                      <el-icon><icon-picture /></el-icon>
                      <span class="text">生成失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="pile_gun_code">
                  <span class="deviceNumber">{{ $filters.moreData(returnDataInfo.deviceNumber) }}</span>
                  <span>-</span>
                  <span class="gunCode">{{ $filters.moreData(formDialog.gunCode) }}</span>
                </div>
              </div>
              <div class="content_qr_bottom" @click="clickDownloadBut">
                <span class="iconfont icon-xiazai"></span>
                <span class="text">下载二维码</span>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import QRCode from 'qrcode';
import { Picture as IconPicture } from '@element-plus/icons-vue';
import { appendObjectToUrl, downloadFiles, getParameterByName } from "@/utils";
import { findPileDetailById } from "@/api/operationManagement/CsPileGunRunningStatus";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";

export default defineComponent({
  name: "ChargingPileQrCodeDialog",
  components: { IconPicture },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activePileId: {
      type: [String, Number],
      default: ""
    }
  },
  setup (props) {
    const qrCodeRef = ref(null);
    const { emit } = getCurrentInstance();

    const validateGunCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择充电枪"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      returnDataInfo: {},
      listLoading: false,
      titleName: "二维码",
      dialog_visible: props.isVisible,

      qrCodeValue: '',
      gunCodeArray: [],
      activeGunInfo: {},
      qrCodeLoading: false,
      appLetLink: "https://sdccs.sunmaxxtech.com/#/applet",
      rules: {
        gunCode: [{ required: true, trigger: "change", validator: validateGunCode }],
      }
    });

    const gunCodeChange = () => {
      that.qrCodeLoading = true;
      let findItem = that.gunCodeArray.find(item => item.gunCode === that.formDialog.gunCode);
      that.activeGunInfo = findItem ? findItem : {};
      let qrCodesArray = [];
      let queryUrlParameObj = {};
      if (that.activeGunInfo.qrCodes) {
        qrCodesArray = that.activeGunInfo.qrCodes.split("?");
        queryUrlParameObj = getParameterByName(that.activeGunInfo.qrCodes);
        if (JSON.stringify(that.activeGunInfo.qrCodes).indexOf("@") !== -1) {
          queryUrlParameObj = getParameterByName(that.activeGunInfo.qrCodes, "@");
        }
        // delete queryUrlParameObj.cid;
        // delete queryUrlParameObj.gid;
      }

      let appLetLink = qrCodesArray[0] ?? that.appLetLink;
      let urlParame = {}
      if (!that.activeGunInfo.qrCodes) {
        urlParame = { cid: that.returnDataInfo.deviceNumber, gid: findItem.gunCode, mc: "nlec" }
      } else if (that.activeGunInfo.qrCodes.indexOf("prot") !== -1) {
        urlParame = { ...queryUrlParameObj }; // 连接默认参数
      } 
      // else if (that.activeGunInfo.qrCodes) {
      //   urlParame = that.activeGunInfo.qrCodes
      // } 
      // else {
      //   urlParame = { cid: that.returnDataInfo.deviceNumber, gid: findItem.gunCode, ...queryUrlParameObj }; // 连接默认参数
      // }
      
      let qrCodeLink = appendObjectToUrl(appLetLink, urlParame);
      console.log(qrCodeLink,'===--');
      QRCode.toDataURL(qrCodeLink, { margin: 0, width: 260, height: 260 }).then(url => {
        that.qrCodeValue = url;
        that.qrCodeLoading = false;
      }).catch(() => {
        that.qrCodeValue = "";
        that.qrCodeLoading = false;
      });
    };

    const formDialogRef = ref(null);
    const clickDownloadBut = () => {
      console.log(that.returnDataInfo,that.activeGunInfo,'===--');
      
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let fileName = `${that.returnDataInfo.siteName}${that.returnDataInfo.deviceNumber}-${that.activeGunInfo.gunCode}.png`;
          downloadFiles(that.qrCodeValue, fileName);
        }
      });
    };

    // 根据id查询电桩详情信息
    const initParamConfigFun = () => {
      that.listLoading = true;
      findPileDetailById({ id: props.activePileId }).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        if (returnDataInfo.gunDetailList && returnDataInfo.gunDetailList.length) {
          that.gunCodeArray = returnDataInfo.gunDetailList;
          if (!that.formDialog.gunCode) {
            that.formDialog.gunCode = that.gunCodeArray[0].gunCode;
            gunCodeChange();
          }
        }
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
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

    return { ...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, qrCodeRef, clickDownloadBut, formDialogRef, gunCodeChange };
  }
});
</script>
<style lang="scss" scoped>
.content_qr {
  border-radius: 8px;
  box-sizing: border-box;
  border: 1px solid #106ec499;

  .content_qr_top {
    padding-top: 12px;
    flex-direction: column;
    box-sizing: border-box;

    .el-image {
      width: 130px;
      height: 130px;

      .image-slot {
        width: 100%;
        height: 100%;
        flex-direction: column;
        font-size: 28px;
        background: var(--el-fill-color-light);
        color: var(--el-text-color-placeholder);

        .text {
          font-size: 12px;
        }
      }
    }

    .pile_gun_code {
      color: #ffffff;
      font-size: 12px;
      text-align: center;
    }
  }

  .content_qr_bottom {
    height: 36px;
    padding: 0 31px;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 0 0 8px 8px;
    font-size: 14px;
    color: #007feb;
    cursor: pointer;

    .text {
      margin-left: 4px;
    }
  }
}
</style>