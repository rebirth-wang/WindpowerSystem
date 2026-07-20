<template>
  <div class="application-configuration">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12">
        <div class="card-box">
          <div class="card-edit">
            <el-icon style="font-size: 4rem; color: #486ff2"><EditPen /></el-icon>
          </div>
          <div class="card-text">
            <div class="card-title">{{ $t('product.drag.854578-1') }}</div>
            <div class="card-content">{{ $t('product.drag.854578-2') }}</div>
          </div>
          <div class="card-button">
            <el-tooltip placement="top">
              <template #content>
                <div>{{ $t('product.drag.854578-3') }}</div>
              </template>
              <el-button :plain="true" :icon="Edit" @click="openApplication" />
            </el-tooltip>
            <el-tooltip
              placement="top"
              :content="enable === 1 ? $t('product.product-edit.473153-74') : $t('product.product-edit.473153-72')"
            >
              <el-button :plain="true" :icon="enable === 1 ? VideoPause : VideoPlay" @click="enableApplication" />
            </el-tooltip>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, getCurrentInstance } from 'vue';
import { Edit, EditPen, VideoPlay, VideoPause } from '@element-plus/icons-vue';
import { updateProduct } from '@/api/iot/product';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  product: { type: Object, default: null },
});

const enable = ref(0);

watch(
  () => props.product,
  (newVal) => {
    if (newVal) console.log(newVal);
  },
  { deep: true }
);

onMounted(() => {
  enable.value = props.product?.panelEnable || 0;
});

function openApplication() {
  window.open('/iot/dragEditor?id=' + props.product.productId + '&deviceType=' + props.product.deviceType);
}

function enableApplication() {
  enable.value = enable.value === 1 ? 0 : 1;
  const text =
    enable.value === 1 ? proxy.$t('product.product-edit.473153-72') : proxy.$t('product.product-edit.473153-74');
  const { tenantId, createBy } = props.product;
  const form = {
    productId: props.product.productId,
    panelEnable: enable.value,
    deviceType: props.product.deviceType,
    tenantId,
    createBy,
  };
  proxy.$modal
    .confirm(proxy.$t('product.drag.854578-5'))
    .then(() => {
      updateProduct(form).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.alertSuccess(proxy.$t('product.drag.854578-6') + text);
        }
      });
    })
    .catch(() => {
      enable.value = enable.value === 1 ? 0 : 1;
    });
}
</script>

<style lang="scss" scoped>
.card-box {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  height: 100px;
  display: grid;
  grid-template-columns: 1fr 3fr 2fr;
  gap: 10px;
  align-items: center;

  .card-edit {
    color: #486ff2;
  }

  .card-text {
    .card-title {
      font-size: 1.125rem;
      font-weight: 500;
    }

    .card-content {
      font-size: 0.875rem;
      color: #909399;
      margin-top: 0.25rem;
    }
  }

  .card-button {
    display: flex;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
