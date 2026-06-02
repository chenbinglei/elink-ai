<template>
  <div style="width:100%; height:100%;">
    <div ref="container" style="width:100%; height:100%;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, defineProps, watch } from 'vue'
import * as THREE from 'three'
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js'

const props = defineProps({
  SystemNearbyArray: {
    type: Object,
    default: () => ({})
  },
})

const dataList = ref([])

watch(() => props.SystemNearbyArray, (newVal) => {
  if (newVal) {
    dataList.value = [
      { name: '新能源', value: newVal.pvSePercent ?? 0, color: '#FF6E00' },
      { name: '电网', value: newVal.gridPercent ?? 0, color: '#009BFF' },
    ]
    disposeAll()
    init()
    createPie()
    animate()
  }
})

const container = ref(null)
let scene = null
let camera = null
let renderer = null
let css2DRenderer = null
let pieGroup = null
let animationId = null
let leaderLinesGroup = null

const radius = 8.5
const baseThickness = 0.8
const gap = 0.05

// 清理所有资源
function disposeAll () {
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
  if (pieGroup) {
    pieGroup.traverse((obj) => {
      if (obj.geometry) obj.geometry.dispose()
      if (obj.material) {
        if (Array.isArray(obj.material)) obj.material.forEach(m => m.dispose())
        else obj.material.dispose()
      }
      if (obj.isCSS2DObject && obj.element) obj.element.remove()
    })
  }
  if (renderer) {
    renderer.dispose()
    renderer.forceContextLoss()
  }
  if (css2DRenderer) {
    css2DRenderer.domElement.remove()
    css2DRenderer = null
  }
  if (container.value) {
    try { if (renderer?.domElement) container.value.removeChild(renderer.domElement) } catch {}
    try { if (css2DRenderer?.domElement) container.value.removeChild(css2DRenderer.domElement) } catch {}
  }
  scene = camera = renderer = css2DRenderer = pieGroup = leaderLinesGroup = null
}

onMounted(() => { })
onUnmounted(() => {
  disposeAll()
  window.removeEventListener('resize', onResize)
})

function init () {
  scene = new THREE.Scene()
  const width = container.value.clientWidth || 400
  const height = container.value.clientHeight || 300

  if (container.value) {
    container.value.style.overflow = 'visible'
    let parent = container.value.parentElement
    while (parent) {
      const style = getComputedStyle(parent)
      if (style.overflow === 'hidden') parent.style.overflow = 'visible'
      parent = parent.parentElement
    }
  }

  camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 1000)
  camera.position.set(0, -8, 4)
  camera.lookAt(0, 0, 0)

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
  renderer.setSize(width, height)
  renderer.setClearColor(0x000000, 0)
  container.value.appendChild(renderer.domElement)

  css2DRenderer = new CSS2DRenderer()
  css2DRenderer.setSize(width, height)
  css2DRenderer.domElement.style.position = 'absolute'
  css2DRenderer.domElement.style.top = '0'
  css2DRenderer.domElement.style.left = '0'
  css2DRenderer.domElement.style.pointerEvents = 'none'
  css2DRenderer.domElement.style.overflow = 'visible'
  container.value.appendChild(css2DRenderer.domElement)

  const dirLight = new THREE.DirectionalLight(0xffffff, 1.3)
  dirLight.position.set(0, -5, 10)
  scene.add(dirLight)
  scene.add(new THREE.AmbientLight(0xffffff, 0.6))

  pieGroup = new THREE.Group()
  pieGroup.rotation.x = Math.PI / 9.5
  pieGroup.position.y = 8.5
  pieGroup.rotation.z = 0.1
  scene.add(pieGroup)

  leaderLinesGroup = new THREE.Group()
  pieGroup.add(leaderLinesGroup)
}

// 创建饼图
function createPie () {
  const total = dataList.value.reduce((sum, d) => sum + d.value, 0) || 1
  let startAngle = -Math.PI / 2
  let thicknessProgress = 0
  const sectorsInfo = []

  dataList.value.forEach(item => {
    const angle = (item.value / total) * Math.PI * 2 || Math.PI / 1.5
    const depth = baseThickness + thicknessProgress * 1.2
    thicknessProgress += 0.35

    const endAngle = startAngle + angle
    createPieSegment(startAngle + gap, endAngle - gap, item.color, depth)

    const midAngle = (startAngle + endAngle) / 2
    sectorsInfo.push({
      midAngle,
      thickness: depth,
      color: item.color,
      value: item.value
    })

    startAngle = endAngle
  })

  createLeaderLines(sectorsInfo)
}

// 扇形
function createPieSegment (startAngle, endAngle, color, depth) {
  const points = [new THREE.Vector3(0, 0, 0)]
  const segments = 32

  for (let i = 0; i <= segments; i++) {
    const a = startAngle + (endAngle - startAngle) * (i / segments)
    const x = Math.cos(a) * radius
    const y = Math.sin(a) * radius
    points.push(new THREE.Vector3(x, y, 0))
  }

  const shape = new THREE.Shape(points)
  const geometry = new THREE.ExtrudeGeometry(shape, {
    depth: depth,
    bevelEnabled: false,
  })

  const material = new THREE.MeshStandardMaterial({
    color: new THREE.Color(color),
    metalness: 0.1,
    roughness: 0.8,
  })

  const mesh = new THREE.Mesh(geometry, material)
  mesh.rotation.x = Math.PI
  mesh.position.z = depth / 2
  pieGroup.add(mesh)
}

// 创建指示线（起点 = 扇形顶面正中间）
function createLeaderLines (sectors) {
  if (!leaderLinesGroup) return
  while (leaderLinesGroup.children.length > 0) {
    const child = leaderLinesGroup.children[0]
    if (child.isCSS2DObject) child.element.remove()
    leaderLinesGroup.remove(child)
  }

  const lineOffset = 3
  const sphereRadius = 0.22
  const textOffset = 0.5

  sectors.forEach(sector => {
    const { midAngle, thickness, color, value } = sector
    const cosA = Math.cos(midAngle)
    const sinA = Math.sin(midAngle)
    const topZ = thickness / 2

    // ==============================================
    // ✅ 【核心修改】指示线起点 = 扇区顶面正中间
    // ==============================================
    const centerRadius = radius * 0.5  // 扇形顶面正中心位置
    const startPoint = new THREE.Vector3(
      centerRadius * cosA,
      centerRadius * sinA,
      topZ
    )

    const endPoint = new THREE.Vector3(
      (radius + lineOffset) * cosA,
      (radius + lineOffset) * sinA,
      topZ
    )

    // 指示线
    const lineGeometry = new THREE.BufferGeometry().setFromPoints([startPoint, endPoint])
    const lineMaterial = new THREE.LineBasicMaterial({ color: new THREE.Color(color), linewidth: 2 })
    const line = new THREE.Line(lineGeometry, lineMaterial)
    leaderLinesGroup.add(line)

    // 端点小球
    const sphereGeometry = new THREE.SphereGeometry(sphereRadius, 16, 16)
    const sphereMaterial = new THREE.MeshStandardMaterial({ color: new THREE.Color(color) })
    const sphere = new THREE.Mesh(sphereGeometry, sphereMaterial)
    sphere.position.copy(endPoint)
    leaderLinesGroup.add(sphere)

    // 文字（只显示百分比）
    const labelPos = new THREE.Vector3(
      (radius + lineOffset + textOffset) * cosA,
      (radius + lineOffset + textOffset) * sinA,
      topZ
    )

    const div = document.createElement('div')
    div.textContent = `${value.toFixed(1)}%`
    div.style.color = '#fff'
    div.style.fontSize = '14px'
    div.style.fontFamily = 'Arial'
    div.style.pointerEvents = 'none'

    const label = new CSS2DObject(div)
    label.position.copy(labelPos)
    leaderLinesGroup.add(label)
  })
}

function animate () {
  animationId = requestAnimationFrame(animate)
  if (renderer && camera && scene) renderer.render(scene, camera)
  if (css2DRenderer && camera && scene) css2DRenderer.render(scene, camera)
}

function onResize () {
  if (!container.value || !camera || !renderer) return
  const w = container.value.clientWidth
  const h = container.value.clientHeight
  renderer.setSize(w, h)
  if (css2DRenderer) css2DRenderer.setSize(w, h)
  camera.aspect = w / h
  camera.updateProjectionMatrix()
}
</script>

<style scoped>
div {
  position: relative;
}
</style>