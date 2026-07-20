<template>
  <el-dialog :title="$t('sip.product-list.998534-0')" v-model="open" width="900px" append-to-body @close="handleClose">
    <el-form @submit.prevent :model="queryParams" ref="queryRef" :inline="true" style="margin-bottom: -18px">
      <el-form-item prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('sip.product-list.998534-2')"
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
      :data="productList"
      @row-click="handleRowClick"
      highlight-current-row
      style="margin-top: 10px"
    >
      <el-table-column width="55" align="center">
        <template #default="scope">
          <el-radio v-model="selectedProductId" :value="scope.row.productId" @change="handleRowClick(scope.row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.product-list.998534-1')" align="center" prop="productName" />
      <el-table-column :label="$t('sip.product-list.998534-4')" align="center" prop="categoryName" />
      <el-table-column :label="$t('sip.product-list.998534-5')" align="center" prop="tenantName" />
      <el-table-column :label="$t('sip.product-list.998536-2')" align="center" prop="networkMethod" min-width="130">
        <template #default="scope">
          <dict-tag
            :options="scope.row.deviceType == 4 ? sub_gateway_type : iot_network_method"
            :value="scope.row.networkMethod"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.mediaServer.998535-4')" align="center" prop="createTime" width="100">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <template #footer>
      <div>
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listProduct } from '@/api/iot/product';
import { useDict } from '@/utils/dict';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['productEvent']);

const { iot_vertificate_method, iot_network_method, sub_gateway_type } = useDict(
  'iot_vertificate_method',
  'iot_network_method',
  'sub_gateway_type'
);
const open = ref(false);
const loading = ref(false);
const total = ref(0);
const productList = ref<any[]>([]);
const selectedProductId = ref<any>(null);
const tempSelectedProduct = ref<any>(null); // 临时保存选中的产品
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  productName: undefined as string | undefined,
  deviceType: 3, // 监控设备
});

function getList() {
  loading.value = true;
  listProduct(queryParams.value).then((response: any) => {
    productList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.value.productName = undefined;
  handleQuery();
}

function handleRowClick(row: any) {
  if (row && row.productId) {
    selectedProductId.value = row.productId;
    tempSelectedProduct.value = row; // 保存临时选中项
  }
}

function handleConfirm() {
  if (tempSelectedProduct.value) {
    emit('productEvent', tempSelectedProduct.value);
    open.value = false;
  }
}

function handleCancel() {
  open.value = false;
}

function handleClose() {
  // 关闭弹窗时重置选择项
  selectedProductId.value = null;
  tempSelectedProduct.value = null;
}

defineExpose({ open, getList });
</script>
