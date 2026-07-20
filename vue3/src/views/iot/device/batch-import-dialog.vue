<template>
  <!-- 批量导入设备 -->
  <div>
    <el-dialog :title="upload.title" v-model="upload.importDeviceDialog" width="550px" append-to-body>
      <el-form label-width="100px" :model="importForm" ref="importFormRef" :rules="importRules">
        <el-form-item label="" prop="productName">
          <template #label>
            {{ $t('device.device-edit.148398-4') }}
          </template>
          <el-input
            readonly
            v-model="importForm.productName"
            :placeholder="$t('device.device-edit.148398-5')"
            style="width: 360px"
          >
            <template #append>
              <el-button @click="selectProduct()">{{ $t('device.device-edit.148398-6') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('uploadFile')" prop="fileList">
          <el-upload
            ref="uploadRef"
            :limit="1"
            accept=".xlsx, .xls"
            :headers="upload.headers"
            :action="upload.url + '?productId=' + importForm.productId"
            :disabled="upload.isUploading"
            :before-upload="beforeUpload"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :auto-upload="false"
            :on-change="handleChange"
            :on-remove="handleRemove"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
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
          style="font-size: 14px; vertical-align: baseline"
          @click="importTemplate"
        >
          <el-icon><Download /></el-icon>
          {{ $t('device.batch-import-dialog.850870-6') }}
        </el-link>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">{{ $t('confirm') }}</el-button>
          <el-button @click="upload.importDeviceDialog = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <product-list ref="productListRef" :productId="importForm.productId" @productEvent="getProductData($event)" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { UploadFilled, Download } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { getToken } from '@/utils/auth';
import productList from './product-list.vue';

const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['save']);

const importFormRef = ref();
const uploadRef = ref();
const productListRef = ref();

const type = ref(1);

const importForm = reactive({
  productId: null as any,
  fileList: [] as any[],
  productName: '',
});

const upload = reactive({
  importDeviceDialog: false,
  title: proxy?.$t('batchImport') || '批量导入',
  isUploading: false,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/iot/device/importData',
});

const importRules = reactive({
  productName: [{ required: true, message: proxy?.$t('device.allot-import-dialog.060657-14'), trigger: 'blur' }],
  fileList: [{ required: true, message: proxy?.$t('plzUploadFile'), trigger: 'change' }],
});

/** 下载模板操作 */
function importTemplate() {
  proxy?.download('/iot/device/uploadTemplate?type=' + type.value, {}, `device_template_${new Date().getTime()}.xlsx`);
}

function beforeUpload(file: any) {
  const name = file.name || '';
  const idx = name.lastIndexOf('.');
  const ext = idx !== -1 ? name.slice(idx + 1).toLowerCase() : '';
  const allowed = ['xls', 'xlsx'];
  if (!allowed.includes(ext)) {
    proxy?.$modal.msgWarning(proxy?.$t('device.batch-import-dialog.850870-5'));
    return false;
  }
  return true;
}

// 选择文件后给表单验证的prop字段赋值，并且清除该字段的校验
function handleChange(file: any, fileList: any[]) {
  importForm.fileList = fileList;
  if (importForm.fileList) {
    importFormRef.value?.clearValidate('fileList');
  }
}

// 删除文件后重新校验该字段
function handleRemove(file: any, fileList: any[]) {
  importForm.fileList = fileList;
  importFormRef.value?.validateField('fileList');
}

// 文件上传中处理
function handleFileUploadProgress() {
  upload.isUploading = true;
}

// 文件上传成功处理
function handleFileSuccess(response: any) {
  upload.importDeviceDialog = false;
  upload.isUploading = false;
  uploadRef.value?.clearFiles();
  ElMessageBox.alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + '</div>',
    proxy?.$t('device.allot-import-dialog.060657-17'),
    {
      dangerouslyUseHTMLString: true,
    }
  );
  emit('save');
}

/** 选择产品 */
function selectProduct() {
  productListRef.value.open = true;
  productListRef.value.getList();
}

/** 获取选中的产品 */
function getProductData(product: any) {
  importForm.productId = product.productId;
  importForm.productName = product.productName;
}

// 提交上传文件
function submitFileForm() {
  importFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      uploadRef.value?.submit();
      upload.importDeviceDialog = false;
    }
  });
}

defineExpose({
  upload,
  importForm,
});
</script>
