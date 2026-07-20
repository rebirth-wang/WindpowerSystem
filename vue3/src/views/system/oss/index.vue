<template>
  <div class="iot-oss">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="88px"
        class="search-form"
      >
        <el-form-item prop="fileSuffix">
          <el-input
            v-model="queryParams.fileSuffix"
            :placeholder="$t('system.oss.index.987541-1')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="fileName">
          <el-input
            v-model="queryParams.fileName"
            :placeholder="$t('system.oss.index.987541-3')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="service">
          <el-input
            v-model="queryParams.service"
            :placeholder="$t('system.oss.index.987541-5')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('system.oss.index.987541-6') }}
          </el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('system.oss.index.987541-7') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['oss:detail:add']">
            {{ $t('system.oss.index.987541-8') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" @click="handleOssConfig" v-hasPermi="['oss:config:edit']">
            {{ $t('system.oss.index.987541-10') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['oss:detail:remove']"
          >
            {{ $t('system.oss.index.987541-9') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="detailList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.oss.index.987541-11')" align="left" prop="fileName" min-width="210" />
        <el-table-column :label="$t('system.oss.index.987541-12')" align="left" prop="originalName" min-width="180" />
        <el-table-column :label="$t('system.oss.index.987541-13')" align="center" prop="fileSuffix" width="150" />
        <el-table-column :label="$t('system.oss.index.987541-14')" align="left" prop="url" min-width="300" />
        <el-table-column :label="$t('system.oss.index.987541-15')" align="center" prop="service" min-width="140" />
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column
          fixed="right"
          :label="$t('system.oss.index.987541-16')"
          align="center"
          class-name="small-padding fixed-width"
          width="130"
        >
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              :icon="Download"
              @click="handleDownload(scope.row)"
              v-hasPermi="['oss:detail:edit']"
            >
              {{ $t('system.oss.index.987541-17') }}
            </el-button>
            <el-button
              size="small"
              type="primary"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['oss:detail:remove']"
            >
              {{ $t('system.oss.index.987541-18') }}
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

      <!-- 添加或修改OSS对象存储对话框 -->
      <el-dialog :title="$t('system.oss.index.987541-22')" v-model="open" width="600px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item :label="$t('system.oss.index.987541-11')">
            <fileUpload v-model="form.file" :fileSize="OssfileSize" :uploadFileUrl="uploadOssUrl" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('system.oss.index.987541-19') }}</el-button>
            <el-button @click="cancel">{{ $t('system.oss.index.987541-20') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { listDetail, delDetail, download } from '@/api/system/ossDetail';
import { blobValidate } from '@/utils/ruoyi';
import FileSaver from 'file-saver';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const router = useRouter();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const detailList = ref<any[]>([]);
const open = ref(false);
const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const uploadOssUrl = import.meta.env.VITE_APP_BASE_API + '/oss/detail/upload';
const OssfileSize = 100;

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  fileName: null as string | null,
  originalName: null as string | null,
  fileSuffix: null as string | null,
  url: null as string | null,
  service: null as string | null,
});
const form = ref<any>({});
const rules = reactive({
  file: [{ required: true, message: t('system.oss.index.987541-21'), trigger: 'blur' }],
});

function getList() {
  loading.value = true;
  listDetail(queryParams).then((response: any) => {
    detailList.value = response.rows;
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
    id: null,
    tenantId: null,
    tenantName: null,
    fileName: null,
    originalName: null,
    fileSuffix: null,
    url: null,
    service: null,
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
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
}

function submitForm() {
  open.value = false;
  getList();
}

function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  (proxy as any).$modal
    .confirm(t('system.oss.index.987541-25', [deleteIds]))
    .then(() => {
      return delDetail(deleteIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('system.oss.index.987541-24'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleDownload(row: any) {
  download(row.id).then((res: any) => {
    const isBlob = blobValidate(res.data);
    if (isBlob) {
      const blob = new Blob([res.data], { type: 'application/octet-stream' });
      FileSaver.saveAs(blob, row.originalName);
    }
  });
}

function handleOssConfig() {
  router.push('/system/ossConfig/index');
}

function getRowKeys(row: any) {
  return row.id;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.iot-oss {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    border-radius: 8px;
    padding-bottom: 22.5px;
    width: 100%;
  }
  .search-form {
    margin-bottom: -18px;
    height: 36px;
    padding: 3px 0 0 0;
  }
}
</style>
