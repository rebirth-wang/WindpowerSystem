<template>
  <div class="scene-list-scada">
    <div v-if="isScada" class="scada" :style="{ height: contentHeight + 'px' }">
      <component ref="sceneScadaRef" :is="scadaComp" :fullScreemTip="false" :isContextmenu="false"></component>
    </div>
    <div v-else>
      <el-empty :description="$t('scene.scada.433893-0')">
        <el-button size="small" type="primary" @click="handleGoToScada()" v-hasPermi="['scene:model:scada:design']">
          {{ $t('scene.scada.433893-2') }}
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const props = defineProps<{ sceneModels: any }>();

const isScada = ref(true);
const scadaComp = shallowRef<any>(null);
const sceneScadaRef = ref<any>(null);
const contentHeight = ref(window.innerHeight);

onMounted(() => {
  const { guid } = props.sceneModels;
  if (guid) {
    getScadaComp();
    isScada.value = true;
  } else {
    isScada.value = false;
  }
});

/** 获取组态 */
function getScadaComp() {
  // 以下组态特有
  import('../../../scada/topo/components/topoRender.vue').then((module) => {
      scadaComp.value = module.default;
  });
  // 以上组态特有
  router.push({ query: { ...route.query, guid: props.sceneModels.guid, type: '2' } });
}

/** 组态设计 */
function handleGoToScada() {
  router.push({
    path: '/scada/center/scene',
    query: {
      sceneModelId: route.query.sceneModelId as string,
    },
  });
}

defineExpose({ sceneScadaRef });
</script>

<style lang="scss" scoped>
.scene-list-scada {
  position: relative;
  width: 100%;
  height: 100%;

  .scada {
    position: relative;
    width: 100%;
    height: 550px;
    margin-top: 5px;
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
