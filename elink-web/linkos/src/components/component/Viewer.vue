<template>
  <div class="viewer">
    <img class="img_class" :src="activeImage" alt="" @click="clickLookImage">
    <div v-show="false" ref="viewerJsRef" class="viewerjs">
      <img v-for="(item,index) in imageArray" :key="index" :src="item" alt="" />
    </div>
  </div>
</template>
<script>
import Viewer from 'viewerjs';
import 'viewerjs/dist/viewer.css';
import {onMounted, reactive, toRefs, ref} from "vue";

export default {
  name: "Viewer",
  props:{
    imageArray:{
      type: Array,
      default: []
    },
    activeImage:{
      type: String,
      default: ""
    },
    activeIndex:{
      type: Number,
      default: 0
    },
  },
  setup(){

    const viewerJsRef = ref(null);
    const that = reactive({
      viewer: null
    })

    const clickLookImage = ()=>{
      that.viewer.show();
    }

    const initViewerFun = ()=>{
      that.viewer = new Viewer(viewerJsRef.value, {
        // inline: true, //启用 inline 模式
        button: true,  //显示右上角关闭按钮
        navbar: true,//显示缩略图导航
        title: false,//显示当前图片的标题
        toolbar: true,//显示工具栏
        tooltip: true,//	显示缩放百分比
        movable: true, //图片是否可移动
        zoomable: true, //图片是否可缩放
        rotatable: true, //图片是否可旋转
        scalable: true, //图片是否可翻转
        transition: true, //使用 CSS3 过度
        fullscreen: true, //播放时是否全屏
        keyboard: true, //是否支持键盘
        // url: "data-source", //设置大图片的 url
        hide: () => {
          that.viewer.hide()
        }
      });
    }

    onMounted(()=>{
      initViewerFun();
    })

    return {...toRefs(that), clickLookImage, initViewerFun, viewerJsRef }
  }
}
</script>
<style lang="scss" scoped>
.viewer {
  width: 100%;
  height: 100%;
  user-select: none;
  border-radius: 4px;

  .img_class{
    width: 100%;
    height: 100%;
  }
}
</style>
