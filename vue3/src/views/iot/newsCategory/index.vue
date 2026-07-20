<template>
  <div class="iot-news-category">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="categoryName">
          <el-input
            v-model="queryParams.categoryName"
            :placeholder="$t('system.news.893410-3')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:newsCategory:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:newsCategory:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:newsCategory:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:newsCategory:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="categoryList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.newsCategory.874509-1')" align="center" prop="categoryId" width="90" />
        <el-table-column :label="$t('system.newsCategory.874509-0')" align="left" prop="categoryName" min-width="200" />
        <el-table-column :label="$t('system.newsCategory.874509-2')" align="center" prop="orderNum" width="90" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" min-width="200" />
        <el-table-column :label="$t('opation')" align="center" class-name="small-padding fixed-width" width="150">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:newsCategory:query']"
            >
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:newsCategory:remove']"
            >
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

    <!-- 添加或修改新闻分类对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('system.newsCategory.874509-0')" prop="categoryName">
          <el-input v-model="form.categoryName" :placeholder="$t('system.news.893410-3')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.newsCategory.874509-2')" prop="orderNum">
          <el-input v-model="form.orderNum" :placeholder="$t('system.newsCategory.874509-3')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('remark')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('plzInput')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:newsCategory:edit']" v-show="form.categoryId">
            {{ $t('update') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:newsCategory:add']" v-show="!form.categoryId">
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
import { Search, Refresh, Plus, Edit, Delete, Download, View } from '@element-plus/icons-vue';
import {
  listNewsCategory,
  getNewsCategory,
  delNewsCategory,
  addNewsCategory,
  updateNewsCategory,
} from '@/api/iot/newsCategory';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const categoryList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryName: null as any,
});

const form = ref<any>({});

const rules = reactive({
  categoryName: [{ required: true, message: proxy.$t('system.newsCategory.874509-4'), trigger: 'blur' }],
  orderNum: [{ required: true, message: proxy.$t('system.newsCategory.874509-5'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listNewsCategory(queryParams).then((response: any) => {
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
  form.value = {
    categoryId: null,
    categoryName: null,
    orderNum: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
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
  title.value = proxy.$t('system.newsCategory.874509-6');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row?: any) {
  reset();
  const categoryId = row?.categoryId || ids.value;
  getNewsCategory(categoryId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy.$t('system.newsCategory.874509-7');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.categoryId != null) {
        updateNewsCategory(form.value).then(() => {
          proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addNewsCategory(form.value).then(() => {
          proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const categoryIds = row?.categoryId || ids.value;
  let msg = '';
  proxy.$modal
    .confirm(proxy.$t('system.newsCategory.874509-8', [categoryIds]))
    .then(() => {
      return delNewsCategory(categoryIds).then((response: any) => {
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

function handleExport() {
  proxy.download('iot/newsCategory/export', { ...queryParams }, `category_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.categoryId;
}
</script>

<style lang="scss" scoped>
.iot-news-category {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
