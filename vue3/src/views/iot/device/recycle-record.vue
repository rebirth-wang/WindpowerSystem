<template>
  <el-dialog :title="$t('device.recycle-record.845969-0')" v-model="open" width="910px">
    <div class="recycle-recor-dialog">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="serialNumber">
          <el-input
            v-model="queryParams.serialNumber"
            :placeholder="$t('device.device-edit.148398-7')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="operateDeptId">
          <el-tree-select
            v-model="queryParams.operateDeptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' } as any"
            :placeholder="$t('device.recycle-record.845969-1')"
            check-strictly
            filterable
            :render-after-expand="false"
            style="width: 205px"
          />
        </el-form-item>
        <el-form-item prop="productId">
          <el-select
            v-model="queryParams.productId"
            :placeholder="$t('device.allot-record.155854-2')"
            filterable
            clearable
            style="width: 205px"
          >
            <el-option v-for="item in productList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('device.recycle-record.845969-4') }}
          </el-button>
          <el-button :icon="Refresh" @click="resetQuery">
            {{ $t('device.recycle-record.845969-5') }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" size="small" :border="false">
        <el-table-column :label="$t('device.device-edit.148398-7')" align="left" prop="serialNumber" min-width="160" />
        <el-table-column :label="$t('device.device-edit.148398-1')" align="left" prop="deviceName" min-width="150" />
        <el-table-column
          :label="$t('device.recycle-record.845969-1')"
          align="left"
          prop="operateDeptName"
          min-width="150"
        />
        <el-table-column :label="$t('device.allot-record.155854-2')" align="left" prop="productName" min-width="180" />
        <el-table-column
          :label="$t('device.recycle-record.845969-6')"
          align="left"
          prop="targetDeptName"
          min-width="220"
        />
        <el-table-column :label="$t('device.recycle-record.845969-8')" align="center" prop="createTime" width="140" />
      </el-table>
      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listProduct } from '@/api/iot/product';
import { listRecycleRecord } from '@/api/iot/device';
import { deptsTreeSelect } from '@/api/system/user';

const { proxy } = getCurrentInstance() as any;

const queryFormRef = ref();
const loading = ref(true);
const total = ref(0);
const open = ref(false);
const productList = ref<any[]>([]);
const dataList = ref<any[]>([]);
const deptOptions = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operateDeptId: null as any,
  productId: null as any,
  serialNumber: '',
  type: 2,
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

// 查询记录列表
function getList() {
  loading.value = true;
  listRecycleRecord(queryParams).then((response: any) => {
    dataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 查询机构下拉树结构 */
function getDeptTree() {
  deptsTreeSelect().then((response: any) => {
    deptOptions.value = response.data;
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
  handleQuery();
}

defineExpose({
  open,
  getList,
  getProductList,
  getDeptTree,
});
</script>
