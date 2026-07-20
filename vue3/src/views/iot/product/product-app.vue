<template>
  <div style="padding-left: 20px">
    <el-row :gutter="10">
      <el-col :span="14">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="warning" plain :icon="Refresh" size="small" @click="getList">
              {{ $t('product.product-app.045891-0') }}
            </el-button>
          </el-col>
          <el-tag type="danger" style="margin-left: 15px">{{ $t('product.product-app.045891-1') }}</el-tag>
        </el-row>
        <el-table
          v-loading="loading"
          :data="modelList"
          border
          style="margin-bottom: 60px; margin-top: 20px"
          size="small"
        >
          <el-table-column :label="$t('product.product-app.045891-2')" align="center" prop="modelName" />
          <el-table-column :label="$t('product.product-app.045891-3')" align="center" prop="identifier" />
          <el-table-column :label="$t('product.product-app.045891-4')" align="center" prop="type">
            <template #default="scope">
              <dict-tag :options="iot_things_type" :value="scope.row.type" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('product.product-app.045891-5')" align="center" prop="datatype">
            <template #default="scope">
              <dict-tag :options="iot_data_type" :value="scope.row.datatype" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('product.product-app.045891-6')" align="center" prop="part">
            <template #default="scope">{{ scope.row.part }} {{ $t('product.product-app.045891-7') }}</template>
          </el-table-column>
        </el-table>
        <el-divider>{{ $t('product.product-app.045891-8') }}</el-divider>
        <el-form ref="formRef" :model="form" label-width="100px">
          <el-form-item :label="$t('product.product-app.045891-9')" prop="page">
            <el-input v-model="form.page" :placeholder="$t('product.product-app.045891-10')" />
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="8" :offset="2">
        <div class="phone">
          <div class="phone-container"></div>
        </div>
        <div style="text-align: center; margin-top: 15px; width: 370px">{{ $t('product.product-app.045891-11') }}</div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Refresh } from '@element-plus/icons-vue';
import { listModel } from '@/api/iot/model';
import { useDict } from '@/utils/dict';

const { iot_things_type, iot_data_type } = useDict('iot_things_type', 'iot_data_type');

const props = defineProps({
  product: { type: Object, default: null },
});

const loading = ref(false);
const modelList = ref<any[]>([]);
const form = ref<any>({});
const queryParams = ref({ productId: 0, type: 4 });

watch(
  () => props.product,
  (newVal) => {
    if (newVal && newVal.productId != 0) {
      queryParams.value.productId = newVal.productId;
      getList();
    }
  }
);

function getList() {
  loading.value = true;
  listModel(queryParams.value).then((response: any) => {
    modelList.value = response.rows;
    loading.value = false;
  });
}
</script>

<style scoped>
.phone {
  height: 700px;
  width: 370px;
  background-image: url('@/assets/images/phone.png');
  background-size: cover;
  top: 0px;
}
.phone-container {
  height: 620px;
  width: 345px;
  border-radius: 20px;
  position: relative;
  top: 45px;
  left: 12px;
  border: 1px solid #888;
  background: linear-gradient(303deg, #b2e9fc 50%, #b5c4f8 50%);
}
</style>
