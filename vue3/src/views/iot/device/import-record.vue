<template>
  <el-dialog :title="$t('device.import-record.086254-0')" v-model="open" width="890px">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item>
        <el-date-picker
          v-model="daterangeTime"
          style="width: 340px"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          :start-placeholder="$t('device.import-record.086254-3')"
          :end-placeholder="$t('device.import-record.086254-4')"
        />
      </el-form-item>
      <el-form-item prop="status">
        <el-select
          v-model="queryParams.status"
          style="width: 240px"
          :placeholder="$t('device.import-record.086254-2')"
          clearable
        >
          <el-option v-for="dict in common_status_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.import-record.086254-5') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">
          {{ $t('device.import-record.086254-6') }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="dataList" size="small" :border="false">
      <el-table-column :label="$t('device.import-record.086254-7')" align="center" prop="id" width="80" />
      <el-table-column :label="$t('device.import-record.086254-8')" align="center" prop="total" min-width="100" />
      <el-table-column :label="$t('device.import-record.086254-13')" align="left" prop="productName" min-width="170" />
      <el-table-column
        :label="$t('device.import-record.086254-9')"
        align="center"
        prop="successQuantity"
        min-width="100"
      />
      <el-table-column
        :label="$t('device.import-record.086254-10')"
        align="center"
        prop="failQuantity"
        min-width="100"
      />
      <el-table-column :label="$t('device.import-record.086254-11')" align="center" prop="status" min-width="100">
        <template #default="scope">
          <dict-tag :options="common_status_type" :value="scope.row.status" size="small" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.import-record.086254-12')" align="center" prop="createTime" width="200" />
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import { listProduct } from '@/api/iot/product';
import { listImportRecord } from '@/api/iot/device';

const { proxy } = getCurrentInstance() as any;
const { common_status_type } = useDict('common_status_type');

const queryFormRef = ref();
const loading = ref(true);
const total = ref(0);
const open = ref(false);
const productList = ref<any[]>([]);
const dataList = ref<any[]>([]);
const daterangeTime = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: null as any,
  status: null as any,
  type: 1,
});

/** 查询产品列表 */
function getProductList() {
  loading.value = true;
  const params = { pageSize: 999, showSenior: true };
  listProduct(params).then((response: any) => {
    productList.value = response.rows.map((item: any) => {
      return { value: item.productId, label: item.productName };
    });
    loading.value = false;
  });
}

// 查询导入记录列表
function getList() {
  loading.value = true;
  listImportRecord(proxy?.addDateRange(queryParams, daterangeTime.value)).then((response: any) => {
    dataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  daterangeTime.value = [];
  handleQuery();
}

defineExpose({
  open,
  getList,
  getProductList,
});
</script>
