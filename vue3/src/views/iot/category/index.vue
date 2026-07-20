<template>
  <div class="iot-category">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        @submit.prevent
        :inline="true"
        label-width="76px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="categoryName">
          <el-input
            v-model="queryParams.categoryName"
            :placeholder="$t('product.index.091251-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="isSys">
          <el-select
            v-model="queryParams.isSys"
            :placeholder="$t('template.index.891112-124')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in system_type_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:category:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:category:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="categoryList"
        @selection-change="handleSelectionChange"
        :border="false"
        :row-key="getRowKeys"
        ref="multipleTableRef"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('product.category.142342-11')" align="left" prop="categoryId" width="160" />
        <el-table-column :label="$t('product.category.142342-0')" align="left" prop="categoryName" min-width="160" />
        <el-table-column :label="$t('template.index.891112-117')" align="left" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-124')" align="center" prop="isSys" width="90">
          <template #default="scope">
            <dict-tag :options="system_type_status" :value="scope.row.isSys" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-118')" align="left" prop="createBy" width="150" />
        <el-table-column :label="$t('product.category.142342-1')" align="center" prop="orderNum" width="100" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" min-width="170" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="130"
        >
          <template #default="scope">
            <el-tooltip
              class="item"
              effect="dark"
              :content="$t('template.index.891112-21')"
              placement="top"
              v-if="scope.row.isSys == 1 && !isAdmin"
            >
              <el-button
                size="small"
                link
                :disabled="true"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['iot:category:query']"
              >
                <el-icon><Edit /></el-icon>
                {{ $t('edit') }}
              </el-button>
            </el-tooltip>
            <el-button size="small" link v-else @click="handleUpdate(scope.row)" v-hasPermi="['iot:category:query']">
              <el-icon><Edit /></el-icon>
              {{ $t('edit') }}
            </el-button>

            <el-tooltip
              class="item"
              effect="dark"
              :content="$t('template.index.891112-21')"
              placement="top"
              v-if="scope.row.isSys == 1 && !isAdmin"
            >
              <el-button
                size="small"
                link
                :disabled="true"
                @click="handleDelete(scope.row)"
                v-hasPermi="['iot:category:remove']"
              >
                <el-icon><Delete /></el-icon>
                {{ $t('del') }}
              </el-button>
            </el-tooltip>
            <el-button size="small" link v-else @click="handleDelete(scope.row)" v-hasPermi="['iot:category:remove']">
              <el-icon><Delete /></el-icon>
              {{ $t('del') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改产品分类对话框 -->
    <el-dialog :title="$t('product.index.091251-2')" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('product.index.091251-2')" prop="categoryName">
          <el-input v-model="form.categoryName" :placeholder="$t('product.index.091251-3')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('product.category.142342-1')" prop="orderNum">
          <el-input-number
            controls-position="right"
            v-model="form.orderNum"
            type="number"
            :placeholder="$t('product.category.142342-2')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('remark')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('product.category.142342-3')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSubmitForm"
            v-hasPermi="['iot:category:edit']"
            v-show="form.categoryId"
          >
            {{ $t('update') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmitForm"
            v-hasPermi="['iot:category:add']"
            v-show="!form.categoryId"
          >
            {{ $t('add') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from '@/api/iot/category';
import { mergeObjects } from '@/utils/index';
import { useUserStore } from '@/stores/modules/user';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { system_type_status } = useDict('iot_yes_no', 'system_type_status');
const userStore = useUserStore();

const isAdmin = ref(false);
const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const categoryList = ref<any[]>([]);
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  showSenior: false,
  pageNum: 1,
  pageSize: 10,
  categoryName: null as any,
  isSys: null as any,
});

const formParams = {
  categoryName: '',
  orderNum: null as any,
  remark: '',
};

const form = ref<any>({ ...formParams });

const rules = reactive({
  categoryName: [{ required: true, message: proxy.$t('product.category.142342-4'), trigger: 'blur' }],
  isSys: [{ required: true, message: proxy.$t('product.category.142342-5'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  if (userStore.roles.includes('admin')) {
    isAdmin.value = true;
  }
});

/** 查询产品分类列表 */
function getList() {
  loading.value = true;
  listCategory(queryParams).then((response: any) => {
    categoryList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = JSON.parse(JSON.stringify(formParams));
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.categoryId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row: any) {
  reset();
  const id = row.categoryId || ids.value;
  getCategory(id).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      open.value = true;
      nextTick(() => {
        formRef.value?.clearValidate();
      });
    }
  });
}

function handleSubmitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const { categoryId, isSys, tenantId, createBy, ...fo } = form.value;
      let data = mergeObjects(formParams, fo);
      if (categoryId === undefined || categoryId === null || categoryId === '') {
        addCategory(data).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
            getList();
          }
          open.value = false;
        });
      } else {
        data = { ...data, categoryId, isSys, tenantId, createBy };
        updateCategory(data).then((res: any) => {
          if (res.code) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            getList();
          }
          open.value = false;
        });
      }
    }
  });
}

function handleDelete(row: any) {
  const categoryIds = row.categoryId || ids.value;
  let msg = '';
  proxy.$modal
    .confirm(proxy.$t('product.category.142342-8', [categoryIds]))
    .then(() => {
      return delCategory(categoryIds).then((response: any) => {
        msg = response.msg;
      });
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(msg);
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.categoryId;
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/category/export', { ...queryParams }, `category_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.iot-category {
  padding: 20px;
}

:deep(.el-dialog__body) {
  box-sizing: border-box;
  padding: 20px;
}
</style>
