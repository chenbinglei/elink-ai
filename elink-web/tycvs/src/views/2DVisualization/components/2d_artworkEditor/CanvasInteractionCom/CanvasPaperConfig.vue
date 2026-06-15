<template>
  <el-collapse v-model="activeNames" class="canvasPaperConfig">

    <div class="content_list">
      <div class="content_list_li">
        <div class="content_list_li_left">画布尺寸</div>
        <div class="content_list_li_right">
          <el-input v-model="canvasOptions.width" size="small" @change="setCanvasOptionsFun">
            <template #prefix>
              <div class="innerText">W</div>
            </template>
          </el-input>
          <el-input v-model="canvasOptions.height" size="small" @change="setCanvasOptionsFun">
            <template #prefix>
              <div class="innerText">H</div>
            </template>
          </el-input>
          <el-popover :width="200" placement="bottom-end" trigger="hover">
            <template #reference>
              <span class="iconfont icon-gengduo"></span>
            </template>
            <div class="content_popover">
              <template v-for="(item,index) in screen_list" :key="index">
                <div class="content_popover_list" @click="setCanvasSizeFun(item)">
                  <div class="content_popover_list_left">{{ item.screenName }}</div>
                  <div class="content_popover_list_right">
                    <span class="width">{{ item.width }}</span>
                    <span class="symbol">*</span>
                    <span class="height">{{ item.height }}</span>
                  </div>
                </div>
              </template>
            </div>
          </el-popover>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">默认颜色</div>
        <div class="content_list_li_right">
          <lx-input-color-picker v-model:color="canvasOptions.color" @changEvent="setCanvasOptionsFun"></lx-input-color-picker>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">背景颜色</div>
        <div class="content_list_li_right">
          <lx-input-color-picker v-model:color="canvasOptions.background" @changEvent="setCanvasOptionsFun"></lx-input-color-picker>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">背景图片</div>
        <div class="content_list_li_right">
          <UploadPicturesCom v-model:fileArray="bkImageArray" @changEvent="canvasBkImgFun"></UploadPicturesCom>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">主题</div>
        <div class="content_list_li_right">
          <el-select v-model="canvasOptions.theme" placeholder="请选择主题" size="small" @change="canvasThemeFun">
            <el-option v-for="item in themeArray" :key="item.theme" :label="item.themeName" :value="item.theme"/>
          </el-select>
        </div>
      </div>
    </div>

    <el-collapse-item class="content_list" name="grid" title="网格设置">
      <div class="content_list_li">
        <div class="content_list_li_left">背景网格</div>
        <div class="content_list_li_right">
          <el-checkbox v-model="canvasOptions.grid" @change="setCanvasOptionsFun"></el-checkbox>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">自动对齐</div>
        <div class="content_list_li_right">
          <el-checkbox v-model="canvasOptions.autoAlignGrid" @change="setCanvasOptionsFun"></el-checkbox>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">网格颜色</div>
        <div class="content_list_li_right">
          <lx-input-color-picker v-model:color="canvasOptions.gridColor" @changEvent="setCanvasOptionsFun"></lx-input-color-picker>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">网格大小</div>
        <div class="content_list_li_right">
          <el-input type="number" v-model="canvasOptions.gridSize" size="small" @change="setCanvasOptionsFun"></el-input>
        </div>
      </div>

      <div class="content_list_li">
        <div class="content_list_li_left">网格角度</div>
        <div class="content_list_li_right">
          <el-input type="number" v-model="canvasOptions.gridRotate" size="small" @change="setCanvasOptionsFun"></el-input>
        </div>
      </div>
    </el-collapse-item>

    <el-collapse-item class="content_list" name="2" title="预览设置">
      <div class="content_list_li">
        <div class="content_list_li_left">缩放方式</div>
        <div class="content_list_li_right">
          <el-radio-group v-model="canvasOptions.scaleMode" @change="setCanvasOptionsFun">
            <template v-for="item in scaleModeArray" :key="item.id">
              <el-radio :value="item.id" size="small">{{ item.name }}</el-radio>
            </template>
          </el-radio-group>
        </div>
      </div>
      <div class="content_list_li">
        <div class="content_list_li_left">显示滚动条</div>
        <div class="content_list_li_right">
          <el-checkbox v-model="canvasOptions.isScroll" @change="setCanvasOptionsFun"></el-checkbox>
        </div>
      </div>
      <div class="content_list_li">
        <div class="content_list_li_left">禁止移动</div>
        <div class="content_list_li_right">
          <el-checkbox v-model="canvasOptions.isDisableTranslate" @change="setCanvasOptionsFun"></el-checkbox>
        </div>
      </div>
      <div class="content_list_li">
        <div class="content_list_li_left">禁止缩放</div>
        <div class="content_list_li_right">
          <el-checkbox v-model="canvasOptions.isDisableScale" @change="setCanvasOptionsFun"></el-checkbox>
        </div>
      </div>
    </el-collapse-item>

    <el-collapse-item class="content_list" name="3" title="辅助设置">
      <div class="content_list_li">
        <div class="content_list_li_left">连线相交弯曲</div>
        <div class="content_list_li_right">
          <el-switch v-model="canvasOptions.lineCross" size="small" @change="setCanvasOptionsFun"/>
        </div>
      </div>
    </el-collapse-item>
  </el-collapse>
</template>

<script lang="ts">
import { useMeta2dStore } from '@/stores/index';

import {clearLineCross, lineCross} from "@meta2d/utils";
import {LxInputColorPicker} from "@/components/LxComponents";
import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";

export default defineComponent({
  name: 'CanvasPaperConfig',
  components: {LxInputColorPicker, UploadPicturesCom},
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    // 代表画布初始化成功
    const canvasMeta2dAllLoad = computed(() => {
      return meta2dStore.canvasMeta2dAllLoad;
    });

    const that = reactive({
      bkImageArray: [],
      canvasOptions: {},
      activeNames: ["2", "3", "grid"],
      screen_list: [
        {width: 1920, height: 1080, screenName: "大屏"},
        {width: 1440, height: 1024, screenName: "网页"},
        {width: 1024, height: 1366, screenName: "平板12.9\""},
        {width: 834, height: 1194, screenName: "平板11\""},
        {width: 768, height: 1024, screenName: "平板Mini"},
        {width: 360, height: 640, screenName: "华为P8"},
        {width: 395, height: 856, screenName: "华为P40"},
        {width: 430, height: 932, screenName: "手机1"},
        {width: 375, height: 812, screenName: "手机2"},
      ],
      themeArray: [
        {theme: "dark", themeName: "暗黑", background: "#000000", color: "#bdc7db"},
        {theme: "light", themeName: "明亮", background: "#FFFFFF", color: "#222222"},
      ],
      scaleModeArray:[{id: "1", name: "自动铺满"}, {id: "2", name: "宽度铺满"}, {id: "3", name: "高度铺满"}]
    })

    // 查询配置信息
    const findCanvasOptionsFun = () => {
      if(canvasMeta2d && canvasMeta2d.value){
        // console.log(canvasMeta2d.value.data());
        let { bkImage, autoAlignGrid, gridRotate, grid } = canvasMeta2d.value.data();
        that.canvasOptions = {...canvasMeta2d.value.getOptions(), autoAlignGrid, gridRotate, grid};
        if(bkImage) that.bkImageArray = [{ url: bkImage }];
        // console.log(that.canvasOptions);
        lineCrossChangeFun();
      }
    }

    const setCanvasOptionsFun = ()=>{
      // console.log(that.canvasOptions);
      canvasMeta2d.value.setOptions(that.canvasOptions);
      canvasMeta2d.value.setGrid(that.canvasOptions);
      canvasMeta2d.value.render();
      lineCrossChangeFun();
    }

    // 连线相交弯曲
    const lineCrossChangeFun = ()=>{
      that.canvasOptions.lineCross ? lineCross(that.canvasOptions.lineCross) : clearLineCross();
    }

    // 主题发生改变执行
    const canvasThemeFun = ()=>{
      let findItem = that.themeArray.find(item=> item.theme === that.canvasOptions.theme);
      if(findItem) canvasMeta2d.value.setOptions(findItem);
      canvasMeta2d.value.render();
      findCanvasOptionsFun();
    }

    // 设置画布显示大小
    const setCanvasSizeFun = (data)=>{
      canvasMeta2d.value.setOptions(data);
      canvasMeta2d.value.render();
      findCanvasOptionsFun();
    }

    // 设置背景图片
    const canvasBkImgFun = ()=>{
      if(that.bkImageArray && that.bkImageArray.length){
        pictureConvertBase64Fun (that.bkImageArray[0].raw).then(res=>{
          canvasMeta2d.value.setBackgroundImage(res);
        })
      } else {
        canvasMeta2d.value.setBackgroundImage("");
      }
    }

    // 获取图片转base64
    const pictureConvertBase64Fun = (file)=> {
      return new Promise(function (resolve, reject) {
        const reader = new FileReader()
        let imgResult = '';
        reader.readAsDataURL(file)
        reader.onload = function () {
          imgResult = reader.result
        }
        reader.onerror = function (error) {
          reject(error)
        }
        reader.onloadend = function () {
          resolve(imgResult)
        }
      })
    }

    // 监听画布是否初始化成功
    const watchCanvasMeta2dAllLoad = watch(()=>canvasMeta2dAllLoad,(newCanvasMeta2dAllLoad)=>{
      // console.log(newCanvasMeta2dAllLoad);
      if(newCanvasMeta2dAllLoad.value)findCanvasOptionsFun();
    },{ deep: true,immediate: true })

    return { ...toRefs(that), canvasMeta2d, findCanvasOptionsFun, canvasThemeFun, setCanvasSizeFun, setCanvasOptionsFun, canvasBkImgFun, canvasMeta2dAllLoad,
      pictureConvertBase64Fun, watchCanvasMeta2dAllLoad, lineCrossChangeFun}
  }
})
</script>

<style lang="scss" scoped>
.canvasPaperConfig {
  border: none;
  padding: 12px 0;
  box-sizing: border-box;

  :deep(.content_list) {
    padding: 12px 0;
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-collapse-border-color);

    .el-collapse-item__header {
      font-size: 13px;
      font-weight: 700;
      color: var(--color-title);
      padding-left: 16px;
      box-sizing: border-box;
    }

    .content_list_li {
      display: flex;
      padding: 0 12px 0 16px;
      box-sizing: border-box;
      margin-bottom: 8px;

      .content_list_li_left {
        width: 76px;
        flex-shrink: 0;
        font-size: 12px;
        color: var(--color);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 30px;
      }

      .content_list_li_right {
        flex: 1;
        display: flex;
        align-items: center;

        .el-input {
          margin-right: 8px;

          .innerText {
            padding-right: 4px;
          }

          :last-child {
            margin-right: 0;
          }
        }

        .el-radio-group{
          display: initial;

          .el-radio{
            margin-right: 0;
            display: block;
          }
        }

        .iconfont {
          cursor: pointer;
          font-size: 18px;
          color: #242424;
          font-weight: bold;
        }
      }
    }

    &:last-child {
      border-bottom: none;

      .el-collapse-item__wrap {
        border-bottom: none;
      }
    }
  }

  .el-collapse-item{
    padding: 0;
    border-bottom: none;
  }
}


.content_popover_list {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  box-sizing: border-box;
  border-radius: 2px;
  color: var(--color);
  font-size: 12px;

  .symbol {
    margin: 0 4px;
  }

  &:hover {
    cursor: pointer;
    background: var(--el-menu-hover-bg-color);
  }
}

</style>