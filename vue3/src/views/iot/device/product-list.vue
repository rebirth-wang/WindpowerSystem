<template>
  <el-dialog :title="$t('device.product-list.058448-0')" v-model="open" width="910px">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" @submit.prevent>
      <el-form-item prop="productName">
        <el-input
          v-model="queryParams.productName"
          :placeholder="$t('device.product-list.058448-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.product-list.058448-3') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('device.product-list.058448-4') }}</el-button>
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
      <el-table-column :label="$t('device.device-edit.148398-6')" width="60" align="center">
        <template #default="scope">
          <el-radio :model-value="scope.row.isSelect" :value="true" name="product" :disabled="!scope.row.canSelect" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.allot-record.155854-2')" align="left" prop="productName" min-width="180" />
      <el-table-column :label="$t('device.product-list.058448-6')" align="left" prop="categoryName" min-width="150" />
      <el-table-column :label="$t('device.product-list.058448-7')" align="left" prop="tenantName" min-width="100" />
      <el-table-column :label="$t('device.product-list.058448-8')" align="center" prop="status" width="70">
        <template #default="scope">
          <dict-tag :options="authorization_code_active" :value="scope.row.isAuthorize" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.product-list.058448-11')" align="center" prop="status" min-width="130">
        <template #default="scope">
          <dict-tag :options="iot_vertificate_method" :value="scope.row.vertificateMethod" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.product-list.058448-12')" align="center" prop="networkMethod" min-width="150">
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
        <el-button @click="confirmSelectProduct" type="primary">{{ $t('device.product-list.058448-14') }}</el-button>
        <el-button @click="closeDialog">{{ $t('device.product-list.058448-15') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listProduct } from '@/api/iot/product';
import useDict from '@/utils/dict/useDict';

const { iot_vertificate_method, iot_network_method, sub_gateway_type, authorization_code_active } = useDict(
  'iot_vertificate_method',
  'iot_network_method',
  'sub_gateway_type',
  'authorization_code_active'
);
const { proxy } = getCurrentInstance()!;

const emit = defineEmits(['productEvent']);

const props = defineProps({
  productId: {
    type: Number,
    default: 0,
  },
  showSenior: {
    type: Boolean,
    default: true,
  },
});

const queryFormRef = ref();
const singleTableRef = ref();

const loading = ref(true);
const total = ref(0);
const open = ref(false);
const productList = ref<any[]>([]);
const product = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: null as any,
  categoryId: null as any,
  categoryName: null as any,
  tenantId: null as any,
  tenantName: null as any,
  isSys: null as any,
  status: 2, //已发布
  deviceType: null as any,
  networkMethod: null as any,
  showSenior: true,
});

onMounted(() => {
  queryParams.showSenior = props.showSenior;
});

/** 查询产品列表 */
function getList() {
  loading.value = true;
  queryParams.showSenior = props.showSenior;
  listProduct(queryParams).then((response: any) => {
    //产品列表初始化isSelect值，用于单选
    for (let i = 0; i < response.rows.length; i++) {
      response.rows[i].isSelect = false;
    }
    productList.value = response.rows;
    total.value = response.total;
    if (props.productId != 0) {
      setRadioSelected(props.productId);
    }
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

/** 单选数据 */
function rowClick(productRow: any) {
  if (productRow != null) {
    setRadioSelected(productRow.productId);
    product.value = productRow;
  }
}

/** 设置单选按钮选中 */
function setRadioSelected(productId: number) {
  for (let i = 0; i < productList.value.length; i++) {
    if (productList.value[i].productId == productId) {
      productList.value[i].isSelect = true;
    } else {
      productList.value[i].isSelect = false;
    }
  }
}

/**确定选择产品，产品传递给父组件 */
function confirmSelectProduct() {
  emit('productEvent', product.value);
  open.value = false;
}

/**关闭对话框 */
function closeDialog() {
  open.value = false;
}

// 暴露公共方法给父组件调用
defineExpose({
  getList,
  open: open,
  product: product,
});
</script>
