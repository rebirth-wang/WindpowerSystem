<template>
  <div class="product-sub">
    <el-row :gutter="10" style="margin-bottom: 8px">
      <el-col :span="1.5">
        <el-button plain type="primary" :icon="Plus" @click="handleAdd" v-hasPermi="['productModbus:gateway:add']">
          {{ $t('product.product-sub.3843945-0') }}
        </el-button>
      </el-col>
      <el-col :span="1.5" v-if="!isSet">
        <el-button plain :icon="Edit" @click="setSubDeviceAddress">
          {{ $t('product.product-sub.3843945-3') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          plain
          :icon="Delete"
          @click="handleDelete()"
          v-hasPermi="['productModbus:gateway:remove']"
          :disabled="multiple"
        >
          {{ $t('product.product-sub.3843945-1') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <span style="font-size: 13px; line-height: 32px; color: #ffb032">
          {{ $t('product.product-sub.3843945-2') }}
        </span>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" :search="false" @queryTable="getList">
        <template #default>
          <div style="margin-right: 10px" v-if="isSet">
            <el-button plain type="primary" @click="saveSetting" v-hasPermi="['productModbus:gateway:edit']">
              {{ $t('save') }}
            </el-button>
            <el-button plain type="info" @click="cancelSetting">{{ $t('cancel') }}</el-button>
          </div>
        </template>
      </right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="subProductData" @selection-change="handleSelectionChange" :border="false">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        :label="$t('product.product-sub.3843945-4')"
        align="left"
        prop="subProductName"
        min-width="160"
      />
      <el-table-column :label="$t('product.product-sub.3843945-5')" align="center" prop="address" width="200px">
        <template #default="scope">
          <el-input
            style="width: 100%; text-align: center"
            :disabled="!isSet"
            v-model="scope.row.address"
            :placeholder="$t('product.product-sub.3843945-6')"
          />
        </template>
      </el-table-column>
      <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="120">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" :label="$t('product.product-sub.3843945-7')" align="center" width="80">
        <template #default="scope">
          <el-button
            type="danger"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['productModbus:gateway:remove']"
          >
            {{ $t('product.product-sub.3843945-8') }}
          </el-button>
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

    <subProductList ref="subProductListRef" :gateway="gateway" @addSuccess="addSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import { gatewayProductList, delProductGateway, editProductGatewayBatch } from '@/api/iot/gateway';
import { parseTime } from '@/utils/ruoyi';
import subProductList from './sub-product-list.vue';

const { proxy } = getCurrentInstance() as any;

const props = defineProps({
  product: { type: Object, default: null },
});

const loading = ref(true);
const ids = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const subProductData = ref<any[]>([]);
const queryParams = ref({ pageNum: 1, pageSize: 10, gwProductId: null as number | null });
const gateway = reactive<any>({});
const isSet = ref(false);
const subProductListRef = ref<any>(null);

watch(
  () => props.product,
  (newVal) => {
    if (newVal && newVal.productId != 0) {
      gateway.gwProductId = newVal.productId;
      queryParams.value.gwProductId = newVal.productId;
      getList();
    }
  }
);

onMounted(() => {
  const productId = props.product?.productId;
  if (productId) {
    queryParams.value.gwProductId = productId;
    gateway.gwProductId = productId;
    getList();
  }
});

function getList() {
  gatewayProductList(queryParams.value).then((response: any) => {
    subProductData.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

function handleAdd() {
  subProductListRef.value?.openDialog();
}

function handleDelete(row?: any) {
  const productIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('product.product-sub.3843945-9', [productIds]))
    .then(() => {
      return delProductGateway(productIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('product.product-sub.3843945-10'));
    })
    .catch(() => {});
}

function setSubDeviceAddress() {
  ElMessageBox.confirm(proxy.$t('product.product-sub.3843945-11'), proxy.$t('product.product-sub.3843945-12'), {
    confirmButtonText: proxy.$t('product.product-sub.3843945-13'),
    cancelButtonText: proxy.$t('product.product-sub.3843945-14'),
    type: 'warning',
  })
    .then(() => {
      isSet.value = !isSet.value;
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: proxy.$t('product.product-sub.3843945-15'),
      });
    });
}

function saveSetting() {
  isSet.value = !isSet.value;
  editProductGatewayBatch(subProductData.value).then(() => {
    getList();
    proxy.$modal.msgSuccess(proxy.$t('saveSuccess'));
  });
}

function cancelSetting() {
  isSet.value = !isSet.value;
}

function addSuccess() {
  getList();
}
</script>

<style lang="scss" scoped>
.product-sub {
  margin-bottom: 20px;
}
</style>
