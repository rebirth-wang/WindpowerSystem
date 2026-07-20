<template>
  <div class="system-post">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="postCode">
          <el-input
            v-model="queryParams.postCode"
            :placeholder="$t('system.post.236590-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="postName">
          <el-input
            v-model="queryParams.postName"
            :placeholder="$t('system.post.236590-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('system.post.236590-4')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_normal_disable"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:post:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:post:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['system:post:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['system:post:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="postList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.post.236590-5')" align="center" prop="postId" min-width="90" />
        <el-table-column :label="$t('system.post.236590-0')" align="left" prop="postCode" min-width="200" />
        <el-table-column :label="$t('system.post.236590-2')" align="left" prop="postName" min-width="200" />
        <el-table-column :label="$t('system.post.236590-6')" align="center" prop="postSort" width="80" />
        <el-table-column :label="$t('status')" align="center" prop="status" width="90">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="150">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:post:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:post:remove']"
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

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('system.post.236590-2')" prop="postName">
          <el-input v-model="form.postName" :placeholder="$t('system.post.236590-3')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.post.236590-0')" prop="postCode">
          <el-input v-model="form.postCode" :placeholder="$t('system.post.236590-1')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.post.236590-6')" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.post.236590-4')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
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
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { listPost, getPost, delPost, addPost, updatePost } from '@/api/system/post';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_normal_disable');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const postList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  postCode: undefined as string | undefined,
  postName: undefined as string | undefined,
  status: undefined as string | undefined,
});
const form = ref<any>({});
const rules = reactive({
  postName: [{ required: true, message: t('system.post.236590-7'), trigger: 'blur' }],
  postCode: [{ required: true, message: t('system.post.236590-8'), trigger: 'blur' }],
  postSort: [{ required: true, message: t('system.post.236590-9'), trigger: 'blur' }],
});

function getList() {
  loading.value = true;
  listPost(queryParams).then((response: any) => {
    postList.value = response.rows;
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
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: 0,
    remark: undefined,
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
  ids.value = selection.map((item) => item.postId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.post.236590-10');
}

function handleUpdate(row?: any) {
  reset();
  const postId = row?.postId || ids.value;
  getPost(postId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = t('system.post.236590-11');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.postId != undefined) {
        updatePost(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addPost(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const postIds = row?.postId || ids.value;
  (proxy as any).$modal
    .confirm(t('system.post.236590-12', [postIds]))
    .then(() => {
      return delPost(postIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('system/post/export', { ...queryParams }, `post_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.postId;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.system-post {
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
