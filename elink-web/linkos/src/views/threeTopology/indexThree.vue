<template>
  <div class="app-container">
    <div class="three_class" id="three" ref="threeRef">{{ threeElement }}</div>
  </div>
</template>

<script>
import * as THREE from "three"; // 导入所有Three.js模块
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import {OBJLoader} from "three/examples/jsm/loaders/OBJLoader.js"
import {MTLLoader} from "three/examples/jsm/loaders/MTLLoader.js"

import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import {onMounted, reactive, toRefs, defineComponent, ref} from "vue";

export default defineComponent({
  name: "indexThree",
  setup(){

    const that = reactive({
      mesh: null, //网格模型
      camera: null, //相机对象
      renderer: null, //渲染器对象

      canvasWidth: 0, // 画布宽度
      canvasHeight: 0, // 画布高度
    });

    const scene = new THREE.Scene(); //创建一个三维场景

    const threeRef = ref(null);
    const initThreeFun = ()=>{
      that.canvasWidth = threeRef.value.offsetWidth; //宽度
      that.canvasHeight = threeRef.value.offsetHeight; //高度
      // console.log(THREE)

      // 模型对象
      // const geometry = new THREE.BoxGeometry(50, 50, 50);
      // const material = new THREE.MeshBasicMaterial({ color: 0x0000ff });
      // const mesh = new THREE.Mesh(geometry, material);
      // that.scene.add(mesh);

      //系统坐标系绘制-----------------------------------------
      const axesHelper = new THREE.AxesHelper(50);
      scene.add(axesHelper);


      // 添加一个辅助网格地面
      const gridHelper = new THREE.GridHelper(240, 50, 0x888888, 0x888888);
      gridHelper.material.opacity = 0.4;
      gridHelper.material.transparent = true;
      // gridHelper.rotation.x = Math.PI / 2.0;
      scene.add(gridHelper);

      // 相机
      that.camera = new THREE.PerspectiveCamera(30, that.canvasWidth / that.canvasHeight, 1, 3000);
      that.camera.position.set(292, 223, 185); //设置相机位置
      that.camera.lookAt(0, 0, 0); //设置相机方向

      // 创建一个WebGL渲染器
      that.renderer = new THREE.WebGLRenderer({
        // alpha: true, // 背景透明
        antialias: true, //抗锯齿
      });  // 创建渲染器对象
      that.renderer.setSize(that.canvasWidth, that.canvasHeight); //设置three.js渲染区域的尺寸(像素px)
      // that.renderer.setClearColor(new THREE.Color(0x888888)); //设置背景颜色和透明度
      // that.renderer.setPixelRatio(window.devicePixelRatio); //框锯齿设置



      // // 创建一个长方体几何对象Geometry
      // const geometry = new THREE.CylinderGeometry(100, 100, 100);
      // // 创建一个材质对象Material
      // const material = new THREE.MeshBasicMaterial({
      //   color: 0xff0000,//0xff0000设置材质颜色为红色
      // });
      //
      // // 两个参数分别为几何体geometry、材质material
      // const mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
      // // 设置网格模型在三维空间中的位置坐标，默认是坐标原点
      // mesh.position.set(0,0,0);
      // that.scene.add(mesh);

      //添加光源 这里添加了两个光源
      // AmbientLight：环境光源，均匀照亮所有物体，防止有些光源照射不到呈现不出来
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
      //PointLight：点光源，类似灯泡发出的光，可以投射阴影，使模型更加立体
      const pointLight = new THREE.PointLight(0xffffff, 0.4);
      pointLight.position.set(200,300,400); //设置点光源所在位置
      scene.add(ambientLight);
      scene.add(pointLight);


      // const ObjectLoader = new OBJLoader();//obj加载器
      // const MaterialLoader = new MTLLoader();
      // MaterialLoader.load('three/three_mtl.mtl',(mtl)=>{
      //   mtl.preload();
      //   ObjectLoader.setMaterials(mtl);
      //   ObjectLoader.load('three/three_obj.obj',(obj)=>{
      //     scene.add(obj);
      //   })
      // })



      // const geometry = new THREE.BoxGeometry(100, 100, 100); //创建一个长方体几何对象Geometry
      // const material = new THREE.MeshBasicMaterial({ color: 0xff0000 }); //创建一个材质对象Material
      // // 两个参数分别为几何体geometry、材质material
      // const mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
      // // 设置网格模型在三维空间中的位置坐标，默认是坐标原点
      // mesh.position.set(0,10,0);
      // that.scene.add(mesh);

      that.renderer.render(scene,that.camera); //执行渲染操作
      document.getElementById('three')?.appendChild(that.renderer.domElement);
      initOrbitControlsFun(); // 设置画布可旋转平移缩放
    }

    // 设置相机控件轨道控制器
    const initOrbitControlsFun = ()=>{
      const controls = new OrbitControls(that.camera, that.renderer.domElement);
      // // 如果OrbitControls改变了相机参数，重新调用渲染器渲染三维场景
      controls.addEventListener('change', function () {
        that.renderer.render(scene, that.camera); //执行渲染操作
      });//监听鼠标、键盘事件
    }

    onMounted(()=>{
      initThreeFun()
    })

    return { ...toRefs(that), initThreeFun, initOrbitControlsFun, threeRef }
  }
})
</script>

<style scoped lang="scss">
.app-container{
  height: 100%;
  padding: 0;

  .three_class{
    width: 100%;
    height: 100%;
  }
}
</style>
