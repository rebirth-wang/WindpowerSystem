<template>
  <el-dialog :title="upload.title" v-model="upload.importDeviceDialog" width="400px" append-to-body>
    <div v-loading="loading">
      <el-form label-position="top" :model="importForm" ref="importFormRef" :rules="importRules">
      <el-form-item :label="$t('uploadFile')" prop="fileList" style="margin-left: 20px">
        <el-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :headers="upload.headers"
          :action="`${upload.url}?productId=${productId}`"
          :disabled="upload.isUploading"
          :before-upload="beforeUpload"
          :on-progress="handleFileUploadProgress"
          :on-error="handleError"
          :on-success="handleFileSuccess"
          :auto-upload="false"
          :on-change="handleChange"
          :on-remove="handleRemove"
          drag
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            {{ $t('dragFileTips') }}
            <em>{{ $t('clickFileTips') }}</em>
          </div>
          <template #tip>
            <div style="margin-top: 10px">
              <span>{{ $t('device.batch-import-dialog.850870-5') }}</span>
            </div>
          </template>
        </el-upload>
      </el-form-item>
      <el-link
        type="primary"
        underline="never"
        style="font-size: 14px; vertical-align: baseline; margin-left: 20px"
        @click="importTemplate"
      >
        <el-icon><Download /></el-icon>
        {{ $t('product.product-things-model.142341-138') }}
      </el-link>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">{{ $t('confirm') }}</el-button>
        <el-button @click="upload.importDeviceDialog = false">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { UploadFilled, Download } from '@element-plus/icons-vue';
import { getToken } from '@/utils/auth';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['refreshList']);

const props = defineProps({
  productId: { type: Number, default: 0 },
});

const loading = ref(false);
const importFormRef = ref<any>(null);
const uploadRef = ref<any>(null);
const importForm = reactive({ productId: null as number | null, fileList: [] as any[] });

const upload = reactive({
  importDeviceDialog: false,
  title: '',
  isUploading: false,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/iot/model/importData',
});

const importRules = reactive<any>({
  fileList: [{ required: true, message: '', trigger: 'change' }],
});

function importTemplate() {
  proxy.download('/iot/model/temp', {}, `${new Date().getTime()}.xlsx`);
}

function beforeUpload(file: any) {
  const ext = (file.name || '').split('.').pop()?.toLowerCase();
  if (!['xls', 'xlsx'].includes(ext || '')) {
    proxy.$message.warning(proxy.$t('device.batch-import-dialog.850870-5'));
    return false;
  }
  return true;
}

function handleChange(_file: any, fileList: any[]) {
  importForm.fileList = fileList;
  if (importForm.fileList.length) importFormRef.value?.clearValidate('fileList');
}

function handleRemove(_file: any, fileList: any[]) {
  importForm.fileList = fileList;
  importFormRef.value?.validateField('fileList');
}

function handleFileUploadProgress() {
  upload.isUploading = true;
}

function handleError(err: any) {
  upload.importDeviceDialog = false;
  proxy.$alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
      (err.msg || err) +
      '</div>',
    proxy.$t('device.allot-import-dialog.060657-17'),
    {
      dangerouslyUseHTMLString: true,
    }
  );
}

function handleFileSuccess(response: any) {
  upload.importDeviceDialog = false;
  upload.isUploading = false;
  loading.value = false;
  uploadRef.value?.clearFiles();
  proxy.$alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + '</div>',
    proxy.$t('device.allot-import-dialog.060657-17'),
    { dangerouslyUseHTMLString: true }
  );
  emit('refreshList');
}

function submitFileForm() {
  importFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      upload.isUploading = true;
      uploadRef.value?.submit();
    }
  });
}

defineExpose({ upload });
</script>
