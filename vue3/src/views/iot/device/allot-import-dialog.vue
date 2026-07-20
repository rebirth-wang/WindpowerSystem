<template>
  <!-- 导入分配设备弹窗 -->
  <div>
    <el-dialog :title="upload.title" v-model="upload.importAllotDialog" width="550px" append-to-body>
      <el-form label-width="100px" :model="allotForm" ref="allotFormRef" :rules="allotRules">
        <el-form-item label="" prop="productName">
          <template #label>
            {{ $t('device.device-edit.148398-4') }}
          </template>
          <el-input
            readonly
            v-model="allotForm.productName"
            :placeholder="$t('device.device-edit.148398-5')"
            style="width: 360px"
          >
            <template #append>
              <el-button @click="selectProduct()">{{ $t('device.device-edit.148398-6') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('device.allot-import-dialog.060657-2')" prop="deptId">
          <el-tree-select
            v-model="allotForm.deptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' } as any"
            :placeholder="$t('device.allot-import-dialog.060657-3')"
            check-strictly
            filterable
            :render-after-expand="false"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item :label="$t('uploadFile')" prop="fileList">
          <el-upload
            ref="uploadRef"
            :limit="1"
            accept=".xlsx, .xls"
            :headers="upload.headers"
            :action="upload.url + '?productId=' + allotForm.productId + '&deptId=' + allotForm.deptId"
            :disabled="upload.isUploading"
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
              <div style="line-height: 26px; width: 360px">
                <div>{{ $t('device.allot-import-dialog.060657-7') }}</div>
                <div>{{ $t('device.allot-import-dialog.060657-8') }}</div>
                <div>{{ $t('device.allot-import-dialog.060657-9') }}</div>
              </div>
            </template>
          </el-upload>
          <el-link
            type="primary"
        underline="never"
            style="font-size: 14px; vertical-align: baseline"
            @click="importAllotTemplate"
          >
            <el-icon><Download /></el-icon>
            {{ $t('device.allot-import-dialog.060657-10') }}
          </el-link>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitImportDevice">
            {{ $t('device.allot-import-dialog.060657-12') }}
          </el-button>
          <el-button @click="upload.importAllotDialog = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <product-list ref="productListRef" :productId="allotForm.productId" @productEvent="getProductData($event)" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { UploadFilled, Download } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { getToken } from '@/utils/auth';
import { deptsTreeSelect } from '@/api/system/user';
import productList from './product-list.vue';

const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['save']);

const allotFormRef = ref();
const uploadRef = ref();
const productListRef = ref();

const type = ref(1);
const deptOptions = ref<any[]>([]);

const allotForm = reactive({
  productId: 0 as any,
  deptId: 0 as any,
  fileList: [] as any[],
  productName: '',
});

const upload = reactive({
  title: proxy?.$t('device.allot-import-dialog.060657-13') || '导入分配设备',
  importAllotDialog: false,
  isUploading: false,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/iot/device/importAssignmentData',
});

const allotRules = reactive({
  productName: [{ required: true, message: proxy?.$t('device.allot-import-dialog.060657-14'), trigger: 'change' }],
  deptId: [{ required: true, message: proxy?.$t('device.allot-import-dialog.060657-15'), trigger: 'change' }],
  fileList: [{ required: true, message: proxy?.$t('plzUploadFile'), trigger: 'change' }],
});

onMounted(() => {
  getDeptTree();
});

/** 查询机构下拉树结构 */
function getDeptTree() {
  deptsTreeSelect().then((response: any) => {
    deptOptions.value = response.data;
  });
}

/** 下载分配导入模板操作 */
function importAllotTemplate() {
  type.value = 2;
  proxy?.download('/iot/device/uploadTemplate?type=' + type.value, {}, `allot_device_${new Date().getTime()}.xlsx`);
}

// 选择文件后给表单验证的prop字段赋值，并且清除该字段的校验
function handleChange(file: any, fileList: any[]) {
  allotForm.fileList = fileList;
  if (allotForm.fileList) {
    allotFormRef.value?.clearValidate('fileList');
  }
}

// 删除文件后重新校验该字段
function handleRemove(file: any, fileList: any[]) {
  allotForm.fileList = fileList;
  allotFormRef.value?.validateField('fileList');
}

// 文件上传中处理
function handleFileUploadProgress() {
  upload.isUploading = true;
}

// 文件上传成功处理
function handleFileSuccess(response: any) {
  upload.importAllotDialog = false;
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
  allotForm.productId = product.productId;
  allotForm.productName = product.productName;
}

// 分配设备导入设备提交按钮
function submitImportDevice() {
  allotFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      uploadRef.value?.submit();
      upload.importAllotDialog = false;
    }
  });
}

defineExpose({
  upload,
  allotForm,
});
</script>
