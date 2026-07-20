<template>
  <el-dialog :title="$t('device.allot-record.155854-0')" v-model="open" width="1000px">
    <div class="allot-recor-dialog">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="distributeType">
          <el-select
            v-model="queryParams.distributeType"
            :placeholder="$t('device.allot-record.155854-16')"
            style="width: 205px"
            filterable
            clearable
          >
            <el-option :label="$t('device.index.105953-14')" :value="1" />
            <el-option :label="$t('device.index.105953-15')" :value="2" />
            <el-option :label="$t('device.index.105953-63')" :value="3" />
          </el-select>
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
        <el-form-item prop="operateDeptId" style="width: 205px">
          <el-tree-select
            v-model="queryParams.operateDeptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' } as any"
            :placeholder="$t('device.allot-record.155854-1')"
            check-strictly
            filterable
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" " @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh"  @click="resetQuery">{{ $t('reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" size="small" :border="false">
        <el-table-column align="left" prop="operateDeptName" min-width="160">
          <template #header>
            <span>{{ $t('device.allot-record.155854-1') }}</span>
            <el-tooltip
              effect="dark"
              :content="$t('device.allot-record.155854-5')"
              placement="top"
              style="margin-left: 10px"
            >
              <el-icon><WarningFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column align="left" prop="targetDeptName" min-width="170">
          <template #header>
            <span>{{ $t('device.allot-import-dialog.060657-2') }}</span>
            <el-tooltip
              effect="dark"
              :content="$t('device.allot-record.155854-7')"
              placement="top"
              style="margin-left: 10px"
            >
              <el-icon><WarningFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column :label="$t('device.allot-record.155854-8')" align="center" prop="total" min-width="85" />
        <el-table-column
          :label="$t('device.allot-record.155854-9')"
          align="center"
          prop="successQuantity"
          min-width="85"
        />
        <el-table-column
          :label="$t('device.allot-record.155854-10')"
          align="center"
          prop="failQuantity"
          min-width="85"
        />
        <el-table-column :label="$t('device.allot-record.155854-11')" align="center" prop="status" min-width="90">
          <template #default="scope">
            <dict-tag :options="common_status_type" :value="scope.row.status" size="small" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('device.allot-record.155854-12')"
          align="center"
          prop="distributeTypeDesc"
          min-width="90"
        />
        <el-table-column :label="$t('device.allot-record.155854-2')" align="left" prop="productName" min-width="190" />
        <el-table-column :label="$t('device.allot-record.155854-13')" align="center" prop="createTime" width="150" />
        <el-table-column :label="$t('opation')" align="center" width="100">
          <template #default="scope">
            <el-button
              type="primary"
              link
              size="small"
              :icon="Download"
              @click="handleDownLoad(scope.row)"
              v-hasPermi="['iot:device:record:export']"
            >
              {{ $t('device.allot-record.155854-15') }}
            </el-button>
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
        style="margin-bottom: 30px;"
      />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { Search, Refresh, Download, WarningFilled } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict/useDict';
import { listProduct } from '@/api/iot/product';
import { listRecycleRecord } from '@/api/iot/device';
import { deptsTreeSelect } from '@/api/system/user';

const { proxy } = getCurrentInstance() as any;
const { common_status_type } = useDict('common_status_type');

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
  type: 3,
  distributeType: null as any,
  productId: null as any,
  operateDeptId: null as any,
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

// 查询分配记录列表
function getList() {
  loading.value = true;
  listRecycleRecord(queryParams).then((response: any) => {
    dataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 下载明细
function handleDownLoad(row: any) {
  const params = { parentId: row.id, type: 4 };
  proxy?.download('iot/record/export', { ...params }, `allot_${new Date().getTime()}.xlsx`);
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
