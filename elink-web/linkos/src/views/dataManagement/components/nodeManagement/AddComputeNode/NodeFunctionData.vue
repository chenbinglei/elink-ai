<template>
  <div class="nodeFunctionData">
    <div class="alter_title">函数（直接拖动插入函数）：</div>
    <div class="content_body">
      <div class="content_left scrollbarStyle">
        <el-tree :data="formulaArray" :indent="0" :node-key="nodeKey" :props="props" highlight-current auto-expand-parent check-on-click-node
                 :default-expanded-keys="defaultExpandedKeys" :current-node-key="currentLivingId" class="leftArrowClass" @node-click="clickTreeNode">
          <template #default="{ node, data }">
            <div :class="{ dragClass: data.draggable }" :draggable="data.draggable" class="custom-tree-node noSelect"
                 @dragend="handleDragEnd($event, data)" @dragstart="handleDragStart($event, data)">
              <div class="left_content textTwo"><span class="label">{{ node.label }}</span></div>
              <span v-if="data.draggable" class="iconfont icon-tuodong"></span>
            </div>
          </template>
        </el-tree>
      </div>
      <div class="content_right scrollbarStyle">
        <div class="illustrate">{{ activeIllustrateInfo.illustrate }}</div>

        <template v-if="activeIllustrateInfo.pourArray && activeIllustrateInfo.pourArray.length">
          <div class="pour_class">
            <div class="pour_title">注：</div>
            <div class="pour_content">
              <template v-for="(item,index) in activeIllustrateInfo.pourArray" :key="index">
                <div class="pour_content_li">{{ item }}</div>
              </template>
            </div>
          </div>
        </template>

        <div class="pour_class" v-if="activeIllustrateInfo.example">
          <div class="pour_title">例：</div>
          <div class="pour_content">
            <div class="pour_content_li">{{ activeIllustrateInfo.example }}</div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import {getBracketPreStr} from "@/utils";
import {getCurrentInstance, onMounted, reactive, defineComponent, toRefs} from "vue";

export default defineComponent({
  name: "NodeFunctionData",
  setup() {

    const {emit} = getCurrentInstance();
    const that = reactive({
      timestamp: "",
      nodeKey: "fieldLogo",
      currentLivingId: "SUM()",
      activeIllustrateInfo: {},
      defaultExpandedKeys: ["function"],
      props: { label: 'fieldName', value: 'fieldLogo', children: 'children' },
      formulaArray:[
        {
          fieldLogo: "function", fieldName: "统计函数",
          children:[
            { fieldLogo: "SUM()", fieldName: "SUM()", type: "function", draggable: true },
            { fieldLogo: "MAX()", fieldName: "MAX()", type: "function", draggable: true },
            { fieldLogo: "MIN()", fieldName: "MIN()", type: "function", draggable: true },
            { fieldLogo: "AVG()", fieldName: "AVG()", type: "function", draggable: true },
            { fieldLogo: "DIF()", fieldName: "DIF()", type: "function", draggable: true },
            { fieldLogo: "DIFF()", fieldName: "DIFF()", type: "function", draggable: true },
          ]
        },
        {
          fieldLogo: "mathematics", fieldName: "数学运算",
          children:[
            { fieldLogo: "SQRT()", fieldName: "SQRT()", type: "mathematics", draggable: true },
            { fieldLogo: "SQUARE()", fieldName: "SQUARE()", type: "mathematics", draggable: true },
            { fieldLogo: "ROUND()", fieldName: "ROUND()", type: "mathematics", draggable: true },
            { fieldLogo: "REVERSE()", fieldName: "REVERSE()", type: "mathematics", draggable: true },
            { fieldLogo: "POW()", fieldName: "POW()", type: "mathematics", draggable: true },
            { fieldLogo: "INTPART()", fieldName: "INTPART()", type: "mathematics", draggable: true },
            { fieldLogo: "ABS()", fieldName: "ABS()", type: "mathematics", draggable: true },
          ]
        },
        {
          fieldLogo: "arithmetic", fieldName: "运算符",
          children:[
            { fieldLogo: "+", fieldName: "加", type: "arithmetic", draggable: true },
            { fieldLogo: "-", fieldName: "减", type: "arithmetic", draggable: true },
            { fieldLogo: "*", fieldName: "乘", type: "arithmetic", draggable: true },
            { fieldLogo: "/", fieldName: "除", type: "arithmetic", draggable: true },
            { fieldLogo: "(", fieldName: "括号", type: "arithmetic", draggable: true },
            { fieldLogo: ")", fieldName: "翻括号", type: "arithmetic", draggable: true }
          ]
        }
      ],
      illustrateArray:[
        {fieldName: "SUM()",illustrate:"求X在周期内的总和。"},
        {fieldName: "MAX()",illustrate:"求X在周期内的最⼤值。"},
        {fieldName: "MIN()",illustrate:"求X在周期内的最⼩值。"},
        {fieldName: "AVG()",illustrate:"求X在周期内的平均值。"},
        {fieldName: "DIF()",illustrate:"求X的最新值与周期内第⼀个值的差值。", pourArray:["1、若第⼀个值为空，返回空值；"]},
        {fieldName: "DIFF()",illustrate:"求X的最新值与周期内第⼀个值的差值。", pourArray:["1、若第⼀个值为空，在周期内向后寻找临近点位的数据代替；"]},

        {fieldName: "SQRT()",illustrate:"求X的平⽅根。", pourArray:["1、X的取值为正数，X为负数时返回空值；"]},
        {fieldName: "SQUARE()",illustrate:"求X的平⽅根。", pourArray:["1、SQUARE(X)，X=3时，返回结果9；"]},
        {fieldName: "ROUND()",illustrate:"对X进⾏⼩数点后，位数为M的四舍五⼊。",example:"INTPART(X)，X=3.1415926，M=2时，返回结果3.14。"},
        {fieldName: "REVERSE()",illustrate:"取相反值，返回－X。", example:"REVERSE(X)，X=100时，返回结果-100。"},
        {fieldName: "POW()",illustrate:"求X的Y次幂。", pourArray:["1、当X为负数时，Y必须为整数，因为底数为负时，不能进⾏开⽅运算，返回值为空值；","2、X,Y可以为数值，也可以为变量；"]},
        {fieldName: "INTPART()",illustrate:"取X的整数部分。", example:"INTPART(X)，X=3.21时，返回结果3"},
        {fieldName: "ABS()",illustrate:"取的X的绝对值。", pourArray:["1、正数的绝对值是它本身；","2、负数的绝对值是它的相反数；"]},

        {fieldName: "+",illustrate:"加法+"},
        {fieldName: "-",illustrate:"减法-"},
        {fieldName: "*",illustrate:"乘法*"},
        {fieldName: "/",illustrate:" 除法/"},
        {fieldName: "(",illustrate:"括号 ()"},
        {fieldName: ")",illustrate:"括号 ()"},
      ]
    })

    const handleDragStart = (e, data)=>{
      const target = document.createElement('span');
      target.className = "dragDomClass";
      target.innerHTML = data.fieldName;
      that.timestamp = Math.round(new Date()); // 用了绑定当前字段的额外属性使用

      // 设置数据类型为纯文本
      let dataHtml = data.fieldLogo;
      if(data.type !== "arithmetic"){
        let functionName = getBracketPreStr(data.fieldLogo);
        dataHtml = `<span class="mceNonEditable startHtml">${ functionName + '(' }</span><span class="endHtml">)</span>`;
      }
      e.dataTransfer.setData('text/html',`<span class="clickable-text ${ data.type }" data-id="${ that.timestamp }">${ dataHtml }</span>`);
      e.target.classList.add('draggingClass'); // 添加拖拽时的样式
      e.dataTransfer.setDragImage(target, 0, 0); // 设置拖拽时的图片
    }

    const handleDragEnd = (e, data)=>{
      // console.log(data);
      e.target.classList.remove('draggingClass'); // 移除拖拽时的样式
      // emit("changEvent",{ type:"insertVarAndFormula",timestamp: that.timestamp, ...data });
    }

    // 点击树形结构数据
    const clickTreeNode = (item) => {
      if(item.draggable){
        that.currentLivingId = item.fieldLogo;
        setIllustrateFun();
      }
    }

    const setIllustrateFun = ()=>{
      that.activeIllustrateInfo = that.illustrateArray.find(item=> item.fieldName === that.currentLivingId);
      // console.log(that.activeIllustrateInfo);
    }

    onMounted(()=>{
      setIllustrateFun();
    })

    return { ...toRefs(that), handleDragStart, handleDragEnd, clickTreeNode, setIllustrateFun }
  }
})
</script>

<style scoped lang="scss">
.nodeFunctionData{
  height: 100%;
  display: flex;
  flex-direction: column;

  .content_body{
    flex: 1;
    height: 2px;
    display: flex;
    align-items: center;
    border: 1px solid #E3E3E3;
    border-radius: 12px;
    box-sizing: border-box;
    margin-top: 12px;

    .content_left{
      width: 220px;
      height: 100%;
      overflow-y: auto;
      border-right: 1px solid #E3E3E3;

      .custom-tree-node {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 2px;
        box-sizing: border-box;

        .left_content {
          flex: 1;
          -webkit-line-clamp: 1;
        }
      }

      .draggingClass {
        color: #1F74E2;
      }
    }

    .content_right{
      flex: 1;
      height: 100%;
      padding: 10px 6px;
      box-sizing: border-box;
      overflow-y: auto;

      .illustrate{
        font-size: 14px;
        font-weight: bold;
        margin-bottom: 12px;
      }

      .pour_class{
        margin-bottom: 12px;

        .pour_title{
          font-size: 14px;
          margin-bottom: 4px;
        }

        .pour_content{
          padding-left: 14px;
          box-sizing: border-box;

          .pour_content_li{
            font-size: 12px;
            margin-bottom: 6px;
          }
        }

        &:last-child{
          margin-bottom: 0;
        }
      }
    }
  }
}
</style>
