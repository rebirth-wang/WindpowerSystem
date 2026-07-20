<template>
  <div class="scada-model">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px" class="search-form">
        <el-form-item prop="modelName">
          <el-input
            v-model="queryParams.modelName"
            :placeholder="$t('scada.model.649850-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="success">
          <el-select
            v-model="queryParams.success"
            :placeholder="$t('scada.model.649850-6')"
            clearable
            style="width: 192px"
          >
            <el-option :label="$t('scada.model.649850-3')" :value="0"></el-option>
            <el-option :label="$t('scada.model.649850-4')" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="sysFlag">
          <el-select
            v-model="queryParams.sysFlag"
            :placeholder="$t('template.index.891112-124')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          >
            <el-option
              v-for="item in dict.type.system_type_status"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['scada:model:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['scada:model:query']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['scada:model:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['scada:model:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="modelList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('scada.model.649850-0')" align="left" prop="modelName" min-width="230" />
        <el-table-column :label="$t('scada.component.302923-5')" align="center" prop="imageUrl" width="100">
          <template #default="scope">
            <image-preview :src="scope.row.imageUrl" :width="48" :height="48" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scada.model.649850-2')" align="center" prop="modelType" width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.scada_model_type" :value="scope.row.modelType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-124')" align="left" prop="sysFlag" min-width="130">
          <template #default="scope">
            <dict-tag :options="dict.type.system_type_status" :value="scope.row.sysFlag" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('scada.model.649850-5')" align="left" prop="modelUrl" min-width="260">
          <template #default="scope">
            <el-link :href="getModelDownloadUrl(scope.row.modelUrl)" underline="never" type="primary">
              {{ getModelDownloadUrl(scope.row.modelUrl) }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />

        <el-table-column fixed="right" :label="$t('opation')" align="center" width="190">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['scada:model:query']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="View"
              @click="handlePreview(scope.row)"
              v-hasPermi="['scada:model:preview']"
            >
              {{ $t('preview') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['scada:model:remove']"
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
        :page="queryParams.pageNum"
        @update:page="queryParams.pageNum = $event"
        :limit="queryParams.pageSize"
        @update:limit="queryParams.pageSize = $event"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改模型管理对话框 -->
    <el-dialog :title="modelDialog.title" v-model="modelDialog.open" width="600px" append-to-body>
      <el-form ref="dialogFormRef" :model="modelDialog.form" :rules="modelDialog.rules" label-width="100px">
        <el-form-item :label="$t('scada.component.302923-5')" prop="imageUrl">
          <imageUpload ref="imageUploadRef" :value="modelDialog.form.imageUrl" :limit="1" :fileSize="1" />
        </el-form-item>
        <el-form-item :label="$t('scada.model.649850-0')" prop="modelName">
          <el-input
            v-model="modelDialog.form.modelName"
            :placeholder="$t('scada.model.649850-1')"
            clearable
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('scada.model.649850-2')" prop="modelType">
          <el-select
            v-model="modelDialog.form.modelType"
            :placeholder="$t('scada.model.649850-6')"
            clearable
            @change="handleModelTypeChange"
            style="width: 400px"
          >
            <el-option
              v-for="item in dict.type.scada_model_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('scada.model.649850-5')" prop="modelUrl">
          <fileUpload
            ref="file-upload"
            :value="modelDialog.form.modelUrl"
            :limit="1"
            :fileSize="20"
            :fileType="modelDialog.fileType"
            @input="getModelFilePath($event)"
            style="width: 400px"
          ></fileUpload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleModelDialogSubmit">{{ $t('confirm') }}</el-button>
        <el-button @click="handleModelDialogCancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
    <!-- 预览 -->
    <el-dialog :title="previewDialog.title" v-model="previewDialog.open" width="1000px">
      <div style="position: relative" v-loading="previewDialog.loading">
        <three-model ref="threeModelRef" :fileType="previewDialog.modelType" :width="960" :height="500"></three-model>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download, View } from '@element-plus/icons-vue';
import { listModel, getModel, delModel, addModel, updateModel, downloadModel } from '@/api/scada/model';
import ThreeModel from '@/components/ThreeModel/index.vue';
import { blobToArrayBuffer } from '@/utils/topo/index';
import { useDict } from '@/utils/dict/useDict';
import imageUpload from '@/components/ImageUpload/index.vue';

const { proxy } = getCurrentInstance() as any;
const { dict } = useDict('scada_model_type', 'system_type_status');
const baseApi = import.meta.env.VITE_APP_BASE_API;

const loading = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const modelList = ref<any[]>([]);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  modelName: null as string | null,
  modelType: 'glb',
  success: null as number | null,
  sysFlag: null as string | null,
});

const modelDialog = reactive({
  open: false,
  title: '',
  form: {
    imageUrl: '',
    modelName: '',
    status: 0,
    modelUrl: null as string | null,
    modelType: 'glb',
  } as any,
  rules: {
    modelName: [{ required: true, message: proxy.$t('scada.model.649850-1'), trigger: 'change' }],
    modelType: [{ required: true, message: proxy.$t('scada.model.649850-6'), trigger: 'change' }],
    modelUrl: [{ required: true, message: proxy.$t('scada.model.649850-7'), trigger: 'change' }],
  },
  fileType: ['glb'] as string[],
});

const previewDialog = reactive({
  open: false,
  title: proxy.$t('preview'),
  modelType: 'glb',
  loading: false,
});

const uploadDisabled = computed(() => modelDialog.form.imageUrl !== '');

const dialogFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const threeModelRef = ref<any>(null);

function getList() {
  loading.value = true;
  listModel(queryParams).then((res: any) => {
    if (res.code === 200) {
      modelList.value = res.rows;
      total.value = res.total;
    }
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  (proxy.$refs.queryForm as any)?.resetFields();
  handleQuery();
}

function handleAdd() {
  reset();
  modelDialog.open = true;
  modelDialog.title = proxy.$t('scada.model.649850-8');
}

function reset() {
  modelDialog.form = {
    imageUrl: '',
    modelName: '',
    modelType: 'glb',
    modelUrl: '',
  };
  modelDialog.fileType = ['glb'];
  dialogFormRef.value?.resetFields();
}

function handleModelTypeChange(val: string) {
  modelDialog.fileType = [val];
}

function getModelFilePath(data: string) {
  modelDialog.form.modelUrl = data;
}

function getModelDownloadUrl(path: string) {
  if (path) {
    return window.location.origin + baseApi + path;
  }
}

function handleModelDialogCancel() {
  modelDialog.open = false;
}

function handleModelDialogSubmit() {
  dialogFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (modelDialog.form.id != null) {
        updateModel(modelDialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            modelDialog.open = false;
            getList();
          }
        });
      } else {
        addModel(modelDialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
            modelDialog.open = false;
            getList();
          }
        });
      }
    }
  });
}

function handleUpdate(row: any) {
  modelDialog.title = proxy.$t('scada.model.649850-9');
  const id = row.id || ids.value;
  getModel(id).then((res: any) => {
    if (res.code === 200) {
      modelDialog.form = res.data;
      modelDialog.open = true;
    }
  });
}

function handlePreview(row: any) {
  const modelUrl = row.modelUrl;
  getModelFile(modelUrl);
  previewDialog.open = true;
}

function handleDelete(row: any) {
  const deleteIds = row.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('scada.model.649850-11', [deleteIds]))
    .then(() => {
      return delModel(deleteIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleExport() {
  (proxy as any).download('scada/model/export', { ...queryParams }, `模型${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.id;
}

function getModelFile(modelUrl: string) {
  previewDialog.loading = true;
  downloadModel(modelUrl)
    .then((blob: Blob) => {
      blobToArrayBuffer(blob)
        .then((arrayBuffer: ArrayBuffer) => {
          threeModelRef.value?.loadThree(arrayBuffer);
          previewDialog.loading = false;
        })
        .catch((err: any) => {
          previewDialog.loading = false;
          console.log(err);
        });
    })
    .catch((err: any) => {
      previewDialog.loading = false;
      console.log(err);
    });
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.scada-model {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
}

.disable {
  :deep(.el-upload--picture-card) {
    display: none !important;
  }
}
</style>
