<template>
  <el-collapse-item name="8" title="音视频属性">
    <el-form :model="active_pel_date" class="content_item_body">
      <template v-if="active_pel_date.name === 'video'">
        <el-form-item label="音频地址" v-if="active_pel_date.videoType === 'audio'">
          <el-input type="textarea" v-model="active_pel_date.audio" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
        <el-form-item label="视频地址" v-if="active_pel_date.videoType === 'video'">
          <el-input type="textarea" v-model="active_pel_date.video" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
      </template>
      <template v-if="active_pel_date.name === 'flvPlayerDom'">
        <el-form-item label="视频地址">
          <el-input type="textarea" v-model="active_pel_date.video" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
        <el-form-item label="媒体数据源"></el-form-item>
        <el-form-item label="配置"></el-form-item>
      </template>
      <template v-if="active_pel_date.name === 'rtspPlayerDom'">
        <el-form-item label="类型">
          <el-select v-model="active_pel_date.mse" size="small" @change="setValueCanvasMeta2dPenFun">
            <el-option v-for="(ts,ti) in mse_list" :key="ti" :label="ts.name" :value="ts.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="流服务">
          <el-input v-model="active_pel_date.url" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
        <el-form-item label="RTSP URL">
          <el-input v-model="active_pel_date.rtspUrl" size="small" @change="setValueCanvasMeta2dPenFun"></el-input>
        </el-form-item>
      </template>
    </el-form>
  </el-collapse-item>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PenAudioAndVideoConfigCom",
  props: {
    activePelDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:activePelDate","changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      active_pel_date: {},
      mse_list: [{id: true,name:"音视频流处理方式"},{id: false,name:"Webrtc"}],
    })

    const setValueCanvasMeta2dPenFun = () => {
      emit("update:activePelDate", that.active_pel_date);
      emit("changeEvent");
    }

    const watchActivePelDate = watch(() => props.activePelDate, (newActivePelDate) => {
      if(JSON.stringify(that.active_pel_date) !== JSON.stringify(newActivePelDate)){
        that.active_pel_date = JSON.parse(JSON.stringify(newActivePelDate));
      }
    }, {deep: true,immediate: true})

    return {...toRefs(that), watchActivePelDate, setValueCanvasMeta2dPenFun}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 4px;
}
</style>