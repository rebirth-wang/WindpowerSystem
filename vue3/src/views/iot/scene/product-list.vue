<template>
  <el-dialog :title="$t('device.product-list.058448-0')" v-model="open" width="920px" :close-on-click-modal="false">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" @submit.prevent>
      <el-form-item prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('product.index.091251-1')"
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
      ref="singleTableRef"
      :data="productList"
      @row-click="rowClick"
      highlight-current-row
      size="small"
      :border="false"
    >
      <el-table-column :label="$t('select')" width="60" align="center">
        <template #default="scope">
          <input type="radio" :checked="scope.row.isSelect" name="product" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.index.091251-0')" align="left" prop="productName" min-width="160px" />
      <el-table-column :label="$t('device.product-list.058448-6')" align="left" prop="categoryName" min-width="120px" />
      <el-table-column :label="$t('device.product-list.058448-7')" align="center" prop="tenantName" min-width="120px" />
      <el-table-column :label="$t('device.product-list.058448-8')" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.isAuthorize == 1">{{ $t('device.product-list.058448-9') }}</el-tag>
          <el-tag type="info" v-if="scope.row.isAuthorize == 0">{{ $t('device.product-list.058448-10') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.product-list.058448-11')" align="center" prop="status" min-width="110px">
        <template #default="scope">
          <dict-tag :options="iot_vertificate_method" :value="scope.row.vertificateMethod" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('device.product-list.058448-12')"
        align="center"
        prop="networkMethod"
        min-width="130px"
      >
        <template #default="scope">
          <dict-tag
            :options="scope.row.deviceType == 4 ? sub_gateway_type : iot_network_method"
            :value="scope.row.networkMethod"
          />
        </template>
      </el-table-column>
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
        <el-button @click="confirmSelectProduct" type="primary">{{ $t('confirm') }}</el-button>
        <el-button @click="closeDialog" type="info">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listProduct } from '@/api/iot/product';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_vertificate_method, iot_network_method, sub_gateway_type } = useDict(
  'iot_vertificate_method',
  'iot_network_method',
  'sub_gateway_type'
);

const emit = defineEmits(['productEvent']);

const loading = ref(true);
const total = ref(0);
const open = ref(false);
const productList = ref<any[]>([]);
const selectProductId = ref(0);
const product = ref<any>({});
const queryFormRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  productName: null,
  categoryId: null,
  categoryName: null,
  tenantId: null,
  tenantName: null,
  isSys: null,
  status: 2,
  deviceType: null,
  networkMethod: null,
  showSenior: false,
});

function getList() {
  loading.value = true;
  listProduct(queryParams).then((response: any) => {
    for (let i = 0; i < response.rows.length; i++) {
      response.rows[i].isSelect = false;
    }
    productList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    setRadioSelected(selectProductId.value);
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function rowClick(row: any) {
  if (row != null) {
    setRadioSelected(row.productId);
    product.value = row;
  }
}

function setRadioSelected(productIdVal: number) {
  for (let i = 0; i < productList.value.length; i++) {
    productList.value[i].isSelect = productList.value[i].productId == productIdVal;
  }
}

function confirmSelectProduct() {
  emit('productEvent', product.value);
  open.value = false;
  product.value = null;
}

function closeDialog() {
  open.value = false;
}

defineExpose({ open, selectProductId, queryParams, getList });
</script>
