<template>
  <el-container class="layout" direction="vertical">
    <el-header style="padding: 0">
      <tool-bar :productId="productId" @preview="handlePreview" @save="handleSave"></tool-bar>
    </el-header>
    <el-container class="content">
      <el-aside class="aside" width="305px">
        <property-bar :pointer="pointer"></property-bar>
      </el-aside>
      <el-main class="main">
        <phone-panel ref="phonePanelRef" :preview="preview" :pointer="pointer"></phone-panel>
      </el-main>
      <el-aside class="aside" width="380px">
        <settings-bar></settings-bar>
      </el-aside>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { ElLoading } from 'element-plus';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import ToolBar from './components/toolBar.vue';
import PropertyBar from './components/propertyBar/index.vue';
import PhonePanel from './components/phonePanel/index.vue';
import SettingsBar from './components/settingsBar.vue';
import { getProduct, updateProduct } from '@/api/iot/product';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const dragStore = useDragEditorStore();

const preview = ref({ show: false });
const pointer = ref({ show: false });
const productId = ref('');
const deviceType = ref('');
const panelModelsJson = ref('');
const productDetails = ref<any>(null);
const phonePanelRef = ref<any>(null);

/** 获取产品信息 */
function getProductData() {
  getProduct(productId.value).then((res: any) => {
    const { code, data } = res;
    if (code === 200) {
      productDetails.value = data;
      const { panelModelsJson: json } = data;
      if (json) {
        const parsed = JSON.parse(json);
        panelModelsJson.value = parsed;
        const { templateJson, component } = parsed;
        dragStore.setPageSetup(JSON.parse(templateJson));
        dragStore.setPageComponents(JSON.parse(component));
        dragStore.setCurrentproperties(JSON.parse(templateJson));
        dragStore.setRightcom('decorate');
      }
    }
  });
}

/** 预览 */
function handlePreview() {
  preview.value.show = true;
}

/** 保存 */
async function handleSave() {
  const loading = ElLoading.service({
    lock: true,
    text: proxy.$t('saving'),
  });

  try {
    const data = JSON.stringify({
      productId: dragStore.id,
      name: dragStore.pageSetup.name,
      templateJson: JSON.stringify(dragStore.pageSetup),
      component: JSON.stringify(dragStore.pageComponents),
    });
    const { tenantId, createBy } = productDetails.value || {};
    const form = {
      productId: productId.value,
      panelModelsJson: data,
      deviceType: deviceType.value,
      tenantId,
      createBy,
    };
    const res: any = await updateProduct(form);
    if (res.code === 200) {
      proxy.$modal.alertSuccess(proxy.$t('saveSuccess'));
    }
  } finally {
    loading.close();
  }
}

onMounted(() => {
  const { id, deviceType: dt } = route.query;
  productId.value = id ? String(id) : '';
  deviceType.value = dt ? String(dt) : '';
  getProductData();
});
</script>

<style lang="scss" scoped>
.layout {
  width: 100%;
  height: 100%;

  .content {
    width: 100%;
    height: 100%;
    overflow: hidden;

    .aside {
      padding: 0;
    }

    .main {
      padding: 0;
    }
  }
}
</style>
