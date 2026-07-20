<template>
  <div class="iot-firmware">
    <el-card v-show="showSearch" style="margin-bottom: 15px; width: 100%">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="78px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="firmwareName">
          <el-input
            v-model="queryParams.firmwareName"
            :placeholder="$t('firmware.index.222541-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.productId"
            :placeholder="$t('firmware.index.222541-2')"
            clearable
            @change="handleQuery"
            filterable
            style="width: 192px"
          >
            <el-option
              v-for="product in productShortList"
              :key="product.id"
              :label="product.name"
              :value="product.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:firmware:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:firmware:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="firmwareList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('firmware.index.222541-0')" align="left" prop="firmwareName" min-width="210" />
        <el-table-column :label="$t('firmware.index.222541-48')" align="center" prop="firmwareType" width="120">
          <template #default="scope">
            <dict-tag :options="iot_firmware_type" :value="scope.row.firmwareType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('firmware.index.222541-4')" align="center" prop="version" width="120">
          <template #default="scope">
            <span>Version</span>
            {{ scope.row.version }}
          </template>
        </el-table-column>
        <el-table-column :label="$t('firmware.index.222541-5')" align="left" prop="productName" min-width="210">
          <template #default="scope">
            <el-link underline="never" type="primary" @click="handleViewProduct(scope.row.productId)">
              {{ scope.row.productName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('firmware.index.222541-6')" align="left" prop="filePath" min-width="250">
          <template #default="scope">
            <el-link :href="getDownloadUrl(scope.row.filePath)" underline="never" type="primary">
              {{ getDownloadUrl(scope.row.filePath) }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('firmware.index.222541-7')" align="center" prop="isLatest" width="80">
          <template #default="scope">
            <el-tag type="primary" v-if="scope.row.isLatest == 1">{{ $t('firmware.index.222541-8') }}</el-tag>
            <el-tag type="info" v-else>{{ $t('firmware.index.222541-9') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column
          :label="$t('firmware.index.222541-10')"
          align="center"
          prop="createTime"
          width="185"
        ></el-table-column>
        <el-table-column :label="$t('firmware.index.222541-11')" align="left" prop="remark" min-width="190" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="200"
        >
          <template #default="scope">
            <el-button size="small" link :icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['iot:firmware:edit']">
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Document"
              @click="handleInfo(scope.row)"
              v-hasPermi="['iot:firmware:query']"
            >
              {{ $t('detail') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:firmware:remove']"
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

    <!-- 添加或修改产品固件对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item :label="$t('firmware.index.222541-0')" prop="firmwareName">
          <template #label>
            <span>
              <el-tooltip :content="$t('firmware.index.222541-13')" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
            <span>{{ $t('firmware.index.222541-0') }}</span>
          </template>
          <el-input v-model="form.firmwareName" :placeholder="$t('firmware.index.222541-1')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('device.device-edit.148398-4')" prop="productName">
          <el-input
            readonly
            v-model="form.productName"
            :placeholder="$t('device.device-edit.148398-5')"
            style="width: 400px"
          >
            <template #append>
              <el-button @click="selectProduct()">{{ $t('device.device-edit.148398-6') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-50')" prop="firmwareType">
          <el-select
            v-model="form.firmwareType"
            :placeholder="$t('firmware.index.222541-51')"
            style="width: 400px"
            disabled
          >
            <el-option
              v-for="dict in iot_firmware_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-15')" prop="version">
          <el-input v-model="form.version" :placeholder="$t('firmware.index.222541-16')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-17')" prop="isLatest">
          <el-switch v-model="form.isLatest" :active-value="1" :inactive-value="0"></el-switch>
          <el-link type="info" underline="never" style="font-size: 12px; margin-left: 15px">
            {{ $t('firmware.index.222541-18') }}
          </el-link>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-19')" prop="filePath">
          <fileUpload
            ref="fileUploadRef"
            :modelValue="form.filePath"
            :limit="1"
            :fileSize="10"
            :fileType="['bin', 'zip', 'pdf']"
            @update:modelValue="getFilePath($event)"
            style="width: 400px"
          ></fileUpload>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-11')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('firmware.index.222541-21')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('save') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <deviceList ref="deviceListRef" :upGrade="formUpGrade"></deviceList>
    <product-list
      ref="productListRef"
      :productId="form.productId"
      :showSenior="false"
      @productEvent="getProductData($event)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh, Plus, Delete, Edit, Document, QuestionFilled } from '@element-plus/icons-vue';
import fileUpload from '@/components/FileUpload/index.vue';
import deviceList from './device-list.vue';
import { listFirmware, getFirmware, delFirmware, addFirmware, updateFirmware } from '@/api/iot/firmware';
import { listShortProduct } from '@/api/iot/product';
import productList from '../device/product-list.vue';
import { useDict } from '@/utils/dict';
import { useRouter } from 'vue-router';

const { iot_yes_no, oat_update_limit, iot_firmware_type } = useDict(
  'iot_yes_no',
  'oat_update_limit',
  'iot_firmware_type'
);
const proxy = getCurrentInstance()?.proxy as any;
const router = useRouter();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const firmwareList = ref<any[]>([]);
const productShortList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const productListRef = ref<any>(null);
const deviceListRef = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  firmwareName: null,
  productId: null,
});
const form = ref<any>({ version: 1.0 });
const formUpGrade = ref<any>({
  taskName: null,
  firmwareId: 0,
  deviceAmount: 0,
  bookTime: null,
  upgradeType: null,
  upType: null,
  deviceList: [],
  version: null,
  flag: false,
});

const rules = reactive<any>({
  firmwareName: [{ required: true, message: proxy?.$t('firmware.index.222541-36'), trigger: 'blur' }],
  firmwareType: [{ required: true, message: proxy?.$t('firmware.index.222541-51'), trigger: 'blur' }],
  productId: [{ required: true, message: proxy?.$t('firmware.index.222541-37'), trigger: 'blur' }],
  productName: [{ required: true, message: proxy?.$t('firmware.index.222541-38'), trigger: 'blur' }],
  version: [
    { required: true, message: proxy?.$t('firmware.index.222541-39'), trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        const regex = /^[a-zA-Z0-9][a-zA-Z0-9]*(?:\.[a-zA-Z0-9]+)*$/;
        if (!regex.test(value)) {
          callback(new Error(proxy?.$t('device.device-edit.148398-108')));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  filePath: [{ required: true, message: proxy?.$t('firmware.index.222541-40'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
  getProductShortList();
});

function handleViewProduct(productId: any) {
  router.push({ path: '/iot/product/edit', query: { t: Date.now(), productId } });
}

function getProductShortList() {
  listShortProduct().then((response: any) => {
    productShortList.value = response.data;
  });
}

function getProductData(product: any) {
  form.value.productId = product.productId;
  form.value.productName = product.productName;
  form.value.firmwareType = product.firmwareType;
}

function getDownloadUrl(path: string) {
  return window.location.origin + import.meta.env.VITE_APP_BASE_API + path;
}

function getList() {
  loading.value = true;
  listFirmware(queryParams).then((response: any) => {
    firmwareList.value = response.rows;
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
    firmwareId: null,
    firmwareName: null,
    productId: null,
    productName: null,
    tenantId: null,
    tenantName: null,
    isLatest: 0,
    isSys: null,
    version: 1.0,
    filePath: null,
    remark: null,
  };
  formUpGrade.value = {
    taskName: null,
    firmwareId: 0,
    deviceAmount: 0,
    bookTime: null,
    upgradeType: null,
    deviceList: [],
    version: null,
    flag: false,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.productId = null;
  queryFormRef.value?.resetFields();
  handleQuery();
}

function selectProduct() {
  productListRef.value.open = true;
  productListRef.value.getList();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.firmwareId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy?.$t('firmware.index.222541-42');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleEdit(row: any) {
  reset();
  const firmwareId = row.firmwareId || ids.value;
  getFirmware(firmwareId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('firmware.index.222541-43');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

function handleInfo(row: any) {
  if (row) {
    sessionStorage.setItem('firmwareTaskInfo', JSON.stringify(row));
    router.push({ name: 'FirmwareTask' });
  }
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.firmwareId != null) {
        updateFirmware(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
            getList();
          }
          open.value = false;
        });
      } else {
        addFirmware(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
            getList();
          }
          open.value = false;
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const firmwareIds = row?.firmwareId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('firmware.index.222541-44', [firmwareIds]))
    .then(() => {
      return delFirmware(firmwareIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getFilePath(data: any) {
  form.value.filePath = data;
}

function getRowKeys(row: any) {
  return row.firmwareId;
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/firmware/export', { ...queryParams }, `firmware_${new Date().getTime()}.xlsx`);
}

// 移除选中设备
function handleClose(tag: any) {
  formUpGrade.value.deviceList.splice(formUpGrade.value.deviceList.indexOf(tag), 1);
  formUpGrade.value.deviceAmount = formUpGrade.value.deviceList.length;
}

// 文件上传中处理
function handleFileUploadProgress(_event: any, _file: any, _fileList: any) {
  // upload.isUploading = true;
}

// 文件上传成功处理
function handleFileSuccess(response: any, _file: any, _fileList: any) {
  form.value.filePath = response.url;
  proxy?.$modal.msgSuccess(response.msg);
}

// 文件下载处理
function handleDownload(row: any) {
  window.open(import.meta.env.VITE_APP_BASE_API + row.filePath);
}
</script>

<style lang="scss" scoped>
.iot-firmware {
  padding: 20px;
}
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
