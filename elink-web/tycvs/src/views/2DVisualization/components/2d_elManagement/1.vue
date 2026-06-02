<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    append-to-body disabledLoading width="520" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="上传文件：" prop="fileList">
            <uploadExcelCustom ref="uploadExcelCustomRef" v-model:fileArray="formDialog.fileList"
              :acceptType="acceptType" :fileName="'上传图模'">
              <template #tip_content>
                <div class="el-upload__tip">
                  <span style="margin-right: 4px">格式为 .json 或 .svg</span>
                </div>
              </template>
            </uploadExcelCustom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import { ElMessage } from "element-plus";
import { importGraph } from "@/api/2DVisualization/2d_elManagement";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";
import uploadExcelCustom from "@/components/component/uploadExcelCustom.vue";

export default defineComponent({
  name: "AddFileAndFolderDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "导入图模"
    },
  },
  components: { uploadExcelCustom },
  setup(props) {
    const { emit } = getCurrentInstance();

    const validateFileList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传变量文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      formDialog: {},
      acceptType: ".json,.svg",
      rules: {
        fileList: [{ required: true, trigger: "change", validator: validateFileList }],
      },
    });

    const formDialogRef = ref(null);

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    // 核心：SVG转成和第一张一样的 Meta2d v1.x 完整格式
    const svgToMeta2dV1Json = async (svgText, fileName) => {
      // 1. 把SVG转成base64图片（和第一张的bkImage一致）
      const base64 = await svgToBase64(svgText);
      
      // 2. 构建和第一张完全一致的完整数据结构
      return {
        version: "1.0.64", // 必须和你项目的版本一致
        name: fileName.replace(/\.(svg|json)$/, ""),
        x: 0,
        y: 0,
        scale: 1,
        origin: { x: 0, y: 0 },
        center: { x: 0, y: 0 },
        gridColor: "#e2e2e2",
        gridSize: 20,
        isDisableScale: false,
        isDisableTranslate: false,
        isRequestWebsocket: true,
        isScroll: false,
        lineCross: false,
        bkImage: base64, // 把SVG转成背景图
        pens: [], // 暂时留空，如果你要保留图形，可以在这里加
        dataPoints: [],
        paths: {},
        requestParamsList: [],
        siteId: "",
        siteName: "",
        template: ""
      };
    };

    // 辅助：SVG转base64
    const svgToBase64 = (svgText) => {
      return new Promise((resolve) => {
        const blob = new Blob([svgText], { type: "image/svg+xml;charset=utf-8" });
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.readAsDataURL(blob);
      });
    };

    const clickConfirmBut = () => {
      formDialogRef.value.validate(async (valid) => {
        if (!valid) return;

        const rawFile = that.formDialog.fileList[0]?.raw;
        if (!rawFile) {
          ElMessage.error("请先选择文件");
          return;
        }

        try {
          that.listLoading = true;
          let finalFile = rawFile;

          // 如果是 SVG，自动转成 Meta2d v1.x 标准JSON
          if (rawFile.name.endsWith(".svg")) {
            const svgText = await rawFile.text();
            const jsonData = await svgToMeta2dV1Json(svgText, rawFile.name);
            const jsonStr = JSON.stringify(jsonData);

            // 构建新的 JSON 文件
            finalFile = new File([jsonStr], rawFile.name.replace(".svg", ".json"), {
              type: "application/json"
            });
          }

          const formData = new FormData();
          formData.append("file", finalFile);

          const res = await importGraph(formData);
          if (res.success) {
            ElMessage.success("上传成功");
            emit("changeEvent", that.formDialog);
            that.dialog_visible = false;
          } else {
            ElMessage.error(res.message || "上传失败");
          }
        } catch (error) {
          ElMessage.error("上传失败：" + error.message);
          console.error(error);
        } finally {
          that.listLoading = false;
        }
      });
    };

    return {
      ...toRefs(that),
      watchDialogVisible,
      watchVisible,
      clickConfirmBut,
      formDialogRef,
    };
  },
});
</script>

<style lang="scss" scoped></style>