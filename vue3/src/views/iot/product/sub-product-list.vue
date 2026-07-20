<template>
  <el-dialog :title="$t('scene.index.670805-36')" v-model="openSubProductList" width="800px" append-to-body>
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" @submit.prevent label-width="48px">
      <el-form-item prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('product.product-sub.3843945-16')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="gatewayList"
      :border="false"
      @selection-change="handleSelectionChange"
      :row-key="getRowKeys"
      ref="multipleTableRef"
    >
      <el-table-column type="selection" :reserve-selection="true" width="55" align="center" />
      <el-table-column label="ID" align="left" prop="productId" width="120" />
      <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="productName" />
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleDeviceSelected">{{ $t('confirm') }}</el-button>
        <el-button @click="closeSelectDeviceList">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { addProductGatewayBatch } from '@/api/iot/gateway';
import { listProduct } from '@/api/iot/product';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['addSuccess']);

const props = defineProps({
  gateway: { type: Object, default: null },
});

const loading = ref(true);
const ids = ref<any[]>([]);
const showSearch = ref(true);
const total = ref(0);
const gatewayList = ref<any[]>([]);
const openSubProductList = ref(false);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  productName: null as string | null,
  deviceType: 4,
  protocolCode: null as string | null,
});
const queryRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

watch(
  () => props.gateway,
  () => {
    queryParams.value.pageNum = 1;
    getList();
  },
  { immediate: true }
);

function getList() {
  loading.value = true;
  queryParams.value.protocolCode = props.gateway?.protocolCode;
  listProduct(queryParams.value).then((response: any) => {
    gatewayList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.productId);
}

function closeSelectDeviceList() {
  openSubProductList.value = false;
}

function handleDeviceSelected() {
  const data = { ...props.gateway, subProductIds: ids.value };
  addProductGatewayBatch(data).then(() => {
    proxy.$modal.msgSuccess(proxy.$t('device.sub-device-list.323213-4'));
    openSubProductList.value = false;
    emit('addSuccess');
  });
}

function getRowKeys(row: any) {
  return row.productId;
}

function openDialog() {
  openSubProductList.value = true;
  queryParams.value.pageNum = 1;
  getList();
  nextTick(() => {
    multipleTableRef.value?.clearSelection();
  });
}

defineExpose({ openDialog });
</script>
