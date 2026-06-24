<template>
  <div class="app-container">

    <div class="content">

      <div class="btns">

        <template v-if="browserType === 'weChat'">
          <!-- 公众号： gh_2f54590230d4  掌上电动： gh_844478a7dfcb   晟曼e充：gh_4f519b6574ef 浙电中新：gh_4db9c953a7a5 -->
          <wx-open-launch-weapp id="launch-btn" :appid="appletKey" :path="wxPath" :username="username" @error="errorHandle" @launch="launchHandle">
            <div v-is="'script'" type="text/wxtag-template">
              <div v-is="'style'">
                .button {
                width: 100%;
                height: 48px;
                font-weight: 600;
                color: #083329;
                font-size: 18px;
                background: #15E8AF;
                border-radius: 6px;
                margin-bottom: 16px;
                border: none;
                }
              </div>
              <button class="button" type="button">微信小程序</button>
            </div>
          </wx-open-launch-weapp>
        </template>
        <template v-if="browserType === 'alipay'">
          <button class="button" type="button" @click="toZfb">支付宝小程序</button>
        </template>
      </div>

    </div>

    <van-overlay :show="overlayShow">
      <van-loading vertical>加载中...</van-loading>
    </van-overlay>

  </div>
</template>
<script>
import { Toast } from 'vant';
import { isBrowserTypeFun, getParameterByName } from "@/utils";
import { defineComponent, reactive, toRefs, onMounted } from "vue";
import { getWechatSignature, getAlipayAppletId } from "@/api/applet";

export default defineComponent({
  name: "applet",
  setup () {

    const that = reactive({
      gunCode: "",
      pileCode: "",
      startMode: 0, // 0: 代表要进入充电  1： 代码要进入放电
      overlayShow: true,
      browserType: isBrowserTypeFun(), //当前浏览器类型
      isSanCodeAndDownload: true, // true 下载App 或者 进入 小程序 首页，  false ：打开进入充放电选择页面。

      username: "gh_4f519b6574ef", // 默认跳转 晟曼e充
      appletKey: "wx6c2157bc4d08d531", // 默认跳转 晟曼e充
      wxPath: "/pages/welcome/index.html",

      usernameArray: [
        { id: "jshqjk", name: "浙新充电", username: "gh_31f50e73074b", appletKey: "wx1d3c3560b98568c5" },
        { id: "sunmax", name: "晟曼e充", username: "gh_4f519b6574ef", appletKey: "wx6c2157bc4d08d531" },
        { id: "chxc", name: "红船享充", username: "gh_cd38ee96ad3b", appletKey: "wxe02dfd7da2097c23" },
        { id: "nlec", name: "能链E充", username: "gh_67d79223c277", appletKey: "wxcbbd98f921327cfc" },
      ]
    })

    // 获取 小程序信息
    const initMiniApplet = () => {
      that.overlayShow = true;
      let urlParam = getParameterByName(window.location.href);
      // 根据@符号解析
      if (JSON.stringify(urlParam).indexOf("@") !== -1) urlParam = getParameterByName(res.result, "@");
      if (!urlParam.No) {
        that.pileCode = urlParam.cid;
        that.gunCode = urlParam.gid || (isNaN(urlParam.data * 1) ? urlParam.data : urlParam.data * 1)
      }
      if (urlParam.No) {
        that.pileCode = urlParam.No.slice(0, urlParam.No.length - 2);
        that.gunCode = urlParam.No?.slice(urlParam.No.length - 2, urlParam.No.length - 1) == 0 ? urlParam.No?.slice(urlParam.No.length - 1, urlParam.No.length) : urlParam.No?.slice(urlParam.No.length - 2) || urlParam.No.slice(urlParam.No.length - 2);
      }

      if (urlParam.dc) that.startMode = urlParam.dc; // 启动方式 0/1 充  2： 放
      //获取对应的小程序原始id (连接后面跟参数 mc)
      let wxPath = that.wxPath;
      let findItem = that.usernameArray.find(item => item.id === urlParam.mc);
      if (findItem) {
        that.username = findItem.username;
        that.appletKey = findItem.appletKey;
      }

      if (that.pileCode !== undefined && that.gunCode !== undefined) {
        // 小程序内部  startMode： 1: 充  2：放
        wxPath = `/firstPackage/pages/startDisAndcharging.html?pileCode=${that.pileCode}&gunCode=${that.gunCode}&startMode=${that.startMode + 1}`;
      }

      that.wxPath = wxPath;

      getWechatSignature({ url: window.location.href.split("#")[0], appletKey: that.appletKey }).then(res => {
        wx.config({
          appId: res.data.appId,
          timestamp: res.data.timestamp,
          nonceStr: res.data.nonceStr,
          signature: res.data.signature,
          // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
          jsApiList: ["checkJsApi", "scanQRCode", "chooseWXPay", "openLocation", 'chooseImage', 'previewImage'],
          openTagList: ["wx-open-launch-weapp", "wx-open-launch-app"] // 这里要配置一下
        });
        that.overlayShow = false;
      }).catch(() => {
        that.overlayShow = false;
      })
    }

    const launchHandle = (e) => {
      console.log('success', e.detail);
    }

    const errorHandle = (e) => {
      console.log('fail', e.detail);
      Toast({ message: '跳转失败！', position: 'bottom' });
    }

    const toZfb = () => {
      getAlipayAppletId({ pileCode: that.pileCode }).then(res => {
        if (res.data) {
          window.location.href = `https://ds.alipay.com/?scheme=` + encodeURIComponent(`alipays://platformapi/startapp?appId=${res.data}&page=firstPackage/pages/startDisAndcharging&pileCode=${that.pileCode}&gunCode=${that.gunCode}&startMode=${that.startMode + 1}`);
        } else {
          Toast({ message: '请前往配置支付宝小程序id！', position: 'bottom' });
        }
      })
    }

    onMounted(() => {
      initMiniApplet();
    })

    return { ...toRefs(that), initMiniApplet, launchHandle, errorHandle, toZfb }
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  position: relative;
  backdrop-filter: blur(20 rpx);
  background: linear-gradient(180deg, #141823 0%, #0a1f25 100%);

  .content {
    width: 100%;
    height: 100%;
    position: absolute;
    left: 0;
    top: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-end;

    .btns {
      width: 100%;
      display: flex;
      flex-direction: column;
      box-sizing: border-box;
      padding: 0 0.32rem 0.32rem 0.32rem;

      .button {
        height: 48px;
        font-weight: 600;
        color: #083329;
        font-size: 18px;
        background: #15e8af;
        border-radius: 6px;
        margin-bottom: 16px;
        border: none;
      }
    }
  }

  .isNoWeiXin {
    text-align: right;
    padding: 5px 5px;
    box-sizing: border-box;
  }
}
</style>
