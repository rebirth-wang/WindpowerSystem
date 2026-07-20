<template>
  <div class="device-scada-wrap">
    <div v-if="isScada" class="scada" :style="{ height: contentHeight + 'px' }">
      <component ref="deviceScadaRef" :is="scadaComp" :fullScreemTip="false" :isContextmenu="false"></component>
    </div>
    <div v-else>
      <el-empty :description="$t('device.scada.789543-0')"></el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const { proxy } = getCurrentInstance()!;
const route = useRoute();
const router = useRouter();

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const isScada = ref(false);
const contentHeight = ref(window.innerHeight);
const scadaComp = ref<any>(null);
const deviceScadaRef = ref();

// 设备联动需要用到
watch(
  () => props.device,
  (newVal, oldVal) => {
    if (newVal.deviceId !== oldVal?.deviceId) {
      getScadaComp();
    }
  },
  { deep: true }
);

onMounted(() => {
  const { guid } = props.device;
  if (guid) {
    getScadaComp();
    isScada.value = true;
  } else {
    isScada.value = false;
  }
});

// 获取窗体高度
function calculateContentHeight() {
  let originalHeight = document.getElementById('deviceDetailTab')?.offsetHeight;
  contentHeight.value = parseFloat(String(originalHeight));
}

// 获取组态
function getScadaComp() {
  const { guid, serialNumber } = props.device;
  // 以下组态特有
  scadaComp.value = null;
  import('../../scada/topo/components/topoRender.vue').then((module) => {
    scadaComp.value = module.default;
  });
  // 以上组态特有
  router.push({ query: { ...route.query, guid: guid, serialNumber: serialNumber, type: '1' } });
}

defineExpose({ deviceScadaRef });
</script>

<style lang="scss" scoped>
.device-scada-wrap {
  position: relative;
  width: 100%;
  height: 100%;

  .scada {
    position: relative;
    width: 100%;
    overflow: auto;
  }

  ::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }

  ::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }

  ::-webkit-scrollbar {
    width: 5px;
    height: 5px;
    position: absolute;
  }

  ::-webkit-scrollbar-track {
    background-color: #fff;
  }
}
</style>
