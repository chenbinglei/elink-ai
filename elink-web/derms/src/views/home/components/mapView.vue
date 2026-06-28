<template>
  <div style="position: relative;">
    <!-- v-if="isMapLoaded" -->
    <div id="echartsMap" style="width: 100%; height: calc(100vh - 125px);"></div>
    <div class="lenged">
      <div class="lenged-item" v-for="(item, index) in stationTypeMap[selectedDevice]" :key="index">
        <div :style="{
          width: '10px',
          height: '10px',
          borderRadius: '50%',
          backgroundColor: color[index]
        }"></div>
        {{ item }}
      </div>

    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import locationIcon from '@/assets/image/location.png';
import mapboxgl from 'mapbox-gl';

const props = defineProps({
  selectedDevice: { type: String, default: 'pileData' },
  location: { type: Array, default: () => [] },
  siteId: { type: String, default: '' },
  searchStaition: { type: String, default: '' }
});

const emit = defineEmits(['iscreen', 'iSearch', 'searchValue']);

const myChart = ref(null);
const geographicalCoordinates = ref([]);
const distributionOptions = ref({});
const isMapLoaded = ref(false);
const color = ['#00EAFF', '#00FF4C', '#FFD000'];
const zoomChina = ref(1);
const oldselectedDevice = ref('');
const markers = ref([]);
const map = ref(null);
const point = ref({ number: 3.5 });

const stationTypeMap = {
  'pileData': ['充电场站', 'V2G场站', '超充场站'],
  'pvData': ['户用', '工商业分布式', '集中式'],
  'storageData': ['户用', '用户侧工商业', '集中式'],
  'changeData': ['换电站']
};

onMounted(() => {
  setTimeout(() => { isMapLoaded.value = true; }, 500);
  init();
});

watch(() => props.searchStaition, (newVal) => {
  if (!newVal) {
    map.value?.setCenter([106.54, 33.19]);
    map.value?.setZoom(3.5);
    markers.value.forEach(marker => marker.remove());
    markers.value = [];
    geographicalCoordinates.value.forEach(item => {
      createMarkerWithEvent(item, false);
    });
    nextTick(() => startSequentialBlinking());
  }
});

watch(() => props.selectedDevice, (newVal, oldVal) => {
  oldselectedDevice.value = oldVal;
});

function arraysEqual(a, b) {
  if (!Array.isArray(a) || !Array.isArray(b)) return false;
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i++) {
    if (!deepEqual(a[i], b[i])) return false;
  }
  return true;
}

function deepEqual(obj1, obj2) {
  if (obj1 === obj2) return true;
  if (typeof obj1 !== 'object' || typeof obj2 !== 'object' || obj1 == null || obj2 == null) return false;
  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);
  if (keys1.length !== keys2.length) return false;
  for (const key of keys1) {
    if (!keys2.includes(key) || !deepEqual(obj1[key], obj2[key])) return false;
  }
  return true;
}

watch(() => props.location, (newVal, oldVal) => {
  if (!Array.isArray(newVal.siteList) || newVal.siteList.length === 0) {
    init();
    return;
  }

  const hasChanged = newVal.totalName !== oldVal.totalName || !arraysEqual(newVal.siteList, oldVal.siteList);
  if (!hasChanged) return;

  const updatedData = newVal.siteList.map(item => {
    const { siteName, siteType, coordinate, id } = item;
    const selectedTypes = stationTypeMap[props.selectedDevice] || [];
    const type = selectedTypes?.[siteType] || 'default';

    let value = [0, 0];
    if (coordinate && coordinate.includes(',')) {
      const [lng, lat] = coordinate.split(',').map(Number);
      if (!isNaN(lng) && !isNaN(lat)) value = [lng, lat];
    }

    return { name: siteName, type, value, id };
  }).filter(item => item.value[0] !== 0 || item.value[1] !== 0);

  geographicalCoordinates.value = updatedData;
  init();
});

watch(() => props.siteId, (newVal) => {
  if (!newVal) {
    markers.value.forEach(m => m.remove());
    markers.value = [];
    geographicalCoordinates.value.forEach(item => createMarkerWithEvent(item, false));
    return;
  }

  const target = geographicalCoordinates.value.find(x => x.id === newVal);
  if (!target || !map.value) return;

  markers.value.forEach(m => m.remove());
  markers.value = [];
  geographicalCoordinates.value.forEach(item => {
    if (item.id !== newVal) createMarkerWithEvent(item, false);
  });
  createMarkerWithEvent(target, true);
  map.value.setCenter(target.value);
  map.value.setZoom(15);
});

const init = () => {
  if (map.value) map.value.remove();

  mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN;
  map.value = new mapboxgl.Map({
    container: 'echartsMap',
    style: "mapbox://styles/sunmax/cl9xyl0im000s14o481h6jaee",
    center: [106.54, 33.19],
    zoom: 3.5,
    minZoom: 3.5,
  });

  map.value.on('load', () => {
    markers.value.forEach(m => m.remove());
    markers.value = [];

    geographicalCoordinates.value.forEach(item => {
      const [lng, lat] = item.value;
      if (!isNaN(lng) && !isNaN(lat)) createMarkerWithEvent(item, false);
    });

    nextTick(() => startSequentialBlinking());

    map.value.on('zoom', () => {
      point.value.number = Math.floor(map.value.getZoom());
    });
  });
};

function createMarkerWithEvent(item, isIcon = false) {
  if (!item || !item.value) return;
  const [lng, lat] = item.value;
  if (isNaN(lng) || isNaN(lat)) return;

  const el = isIcon ? createIconElement() : createSmallCircle(item);
  const marker = new mapboxgl.Marker({ element: el })
    .setLngLat(item.value)
    .addTo(map.value);

  marker.isBlinking = false;
  markers.value.push(marker);

  marker.getElement().addEventListener('mouseenter', () => {
    const html = `<div style="font-size:12px;padding:6px 12px;color:#fff;border:1px solid #00e6fe;background:#071019;border-radius:4px;white-space:nowrap">${item.name}</div>`;
    const popup = new mapboxgl.Popup({ offset: 10, closeButton: false })
      .setHTML(html)
      .setLngLat(item.value)
      .addTo(map.value);
    marker.popup = popup;
  });

  marker.getElement().addEventListener('mouseleave', () => {
    marker.popup?.remove();
    marker.popup = null;
  });

  marker.getElement().addEventListener('click', () => {
    emit('iSearch', true);
    emit('searchValue', item);
    map.value.setCenter(item.value);
    map.value.setZoom(15);
    marker.remove();
    createMarkerWithEvent(item, !isIcon);
  });
}

let currentBlinkIndex = 0;
function startSequentialBlinking() {
  if (window.blinkInterval) clearInterval(window.blinkInterval);
  window.blinkInterval = setInterval(() => {
    markers.value.forEach(m => m.getElement().classList.remove('blink-animation'));
    const cnt = Math.floor(Math.random() * 3) + 1;
    const set = new Set();
    while (set.size < cnt && set.size < markers.value.length) {
      set.add(Math.floor(Math.random() * markers.value.length));
    }
    set.forEach(i => markers.value[i].getElement().classList.add('blink-animation'));
  }, 1500);
}

function createSmallCircle(value) {
  const idx = stationTypeMap[props.selectedDevice]?.indexOf(value.type) || 0;
  const d = document.createElement('div');
  d.style.width = '10px';
  d.style.height = '10px';
  d.style.borderRadius = '50%';
  d.style.backgroundColor = color[idx];
  d.style.setProperty('--color', color[idx]);
  d.classList.add('circle-with-shadow');
  return d;
}

function createIconElement() {
  const img = document.createElement('img');
  img.src = locationIcon;
  img.width = 30;
  img.height = 30;
  return img;
}
</script>
<style>
@keyframes blink {

  0%,
  100% {
    opacity: 1;
    width: 10px;
    height: 10px;
    box-shadow: 0 0 20px var(--color);
  }

  50% {
    opacity: 0.3;
    width: 50px;
    height: 50px;

    box-shadow: 0 0 50px var(--color);
    /* 更强的阴影 */
  }

}

.blink-animation {
  animation: blink 1.5s infinite;
}
</style>
<style scoped lang="scss">
::v-deep .mapboxgl-popup-content {
  padding: 0 !important;
  background-color: transparent !important;
}

.lenged {
  position: absolute;
  bottom: 60px;
  // left: 30px;
  z-index: 2;
  right: 5.5%;
  color: rgba(255, 255, 255, 0.6)
}

.lenged-item {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  /* 圆点和文字之间的间距 */
}
::v-deep .mapboxgl-ctrl-bottom-right{
  display: none;
}
</style>