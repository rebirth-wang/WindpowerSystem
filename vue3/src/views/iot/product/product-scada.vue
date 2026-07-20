<template>
  <div class="product-scada-wrap">
    <div v-if="isScada" class="scada" :style="{ height: contentHeight + 'px' }">
      <component :is="scadaComp" :fullScreemTip="false" :isContextmenu="false" />
    </div>
    <div v-else>
      <el-empty :description="$t('product.product-scada.638785-0')" />
      <div style="text-align: center">
        <el-button size="small" type="primary" @click="handleGoToScada()" v-hasPermi="['iot:product:scada']">
          {{ $t('product.product-scada.034908-0') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, shallowRef } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const props = defineProps({
  product: { type: Object, default: null },
});

const isScada = ref(false);
const contentHeight = ref(window.innerHeight);
const scadaComp = shallowRef<any>(null);

onMounted(() => {
  const { guid } = props.product || {};
  if (guid) {
    getScadaComp();
    isScada.value = true;
  } else {
    isScada.value = false;
  }
});

function handleGoToScada() {
  router.push({ path: '/scada/center/temp', query: { productId: String(props.product.productId) } });
}

function getScadaComp() {
  // 以下组态特有
  import('../../scada/topo/components/topoRender.vue').then((module) => {
    scadaComp.value = module.default;
  });
  // 以上组态特有
  router.push({ query: { ...route.query, guid: props.product.guid, type: '1' } });
}
</script>

<style lang="scss" scoped>
.product-scada-wrap {
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
