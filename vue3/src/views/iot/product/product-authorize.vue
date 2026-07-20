<template>
  <div style="padding: 10px">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item prop="serialNumber">
        <el-input
          v-model="queryParams.serialNumber"
          :placeholder="$t('product.product-authorize.314975-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="authorizeCode">
        <el-input
          v-model="queryParams.authorizeCode"
          :placeholder="$t('product.product-authorize.314975-3')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="$t('product.index.091251-5')"
          clearable
          style="width: 180px"
        >
          <el-option v-for="dict in iot_auth_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Plus"
          @click="handleAdd"
          v-if="productInfo.isOwner != 0"
          v-hasPermi="['iot:authorize:add']"
        >
          {{ $t('add') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          plain
          :icon="Delete"
          v-if="productInfo.isOwner != 0"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['iot:authorize:remove']"
        >
          {{ $t('product.product-authorize.314975-9') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain :icon="Download" x @click="handleExport" v-hasPermi="['iot:authorize:export']">
          {{ $t('export') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <span style="font-size: 12px; line-height: 32px; color: #ffb032">
          {{ $t('product.product-authorize.314975-11') }}
        </span>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="authorizeList"
      @selection-change="handleSelectionChange"
      @cell-dblclick="celldblclick"
      size="small"
      :border="false"
    >
      <el-table-column type="selection" :selectable="selectable" width="55" align="center" />
      <el-table-column
        :label="$t('product.product-authorize.314975-2')"
        min-width="300"
        align="left"
        prop="authorizeCode"
      />
      <el-table-column :label="$t('product.product-authorize.314975-4')" align="center" prop="active" width="100">
        <template #default="scope">
          <dict-tag :options="iot_auth_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-authorize.314975-0')"
        min-width="150"
        align="left"
        prop="serialNumber"
      >
        <template #default="scope">
          <el-link type="primary" @click="getDeviceDetail(scope.row.serialNumber)" underline="never">
            {{ scope.row.serialNumber }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-authorize.314975-12')"
        align="center"
        prop="updateTime"
        min-width="180"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{m}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.product-authorize.314975-13')" align="left" prop="remark" min-width="180" />
      <el-table-column
        :label="$t('product.product-authorize.314975-14')"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
        width="220"
      >
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            @click="handleUpdate(scope.row, 'auth')"
            v-hasPermi="['iot:authorize:edit']"
            v-if="scope.row.status == 1 && !scope.row.deviceId && productInfo.isOwner != 0"
          >
            {{ $t('product.index.091251-19') }}
          </el-button>
          <el-button
            size="small"
            type="primary"
            link
            @click="handleUpdate(scope.row, 'remark')"
            v-hasPermi="['iot:authorize:edit']"
            v-if="productInfo.isOwner != 0"
          >
            {{ $t('product.product-authorize.314975-13') }}
          </el-button>
          <el-button
            size="small"
            type="primary"
            link
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:authorize:remove']"
            v-if="!scope.row.deviceId && productInfo.isOwner != 0"
          >
            {{ $t('del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 设备授权和授权备注对话框 -->
    <el-dialog :title="title" v-model="open" :width="editWidth" append-to-body>
      <div v-if="editType == 'auth'">
        <el-form :model="deviceParams" ref="queryDeviceFormRef" :inline="true" label-width="68px">
          <el-form-item prop="deviceName">
            <el-input
              v-model="deviceParams.deviceName"
              :placeholder="$t('product.product-authorize.314975-18')"
              clearable
              size="small"
              @keyup.enter="handleDeviceQuery"
            />
          </el-form-item>
          <el-form-item prop="serialNumber">
            <el-input
              v-model="deviceParams.serialNumber"
              :placeholder="$t('product.product-authorize.314975-1')"
              clearable
              size="small"
              @keyup.enter="handleDeviceQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" size="small" @click="handleDeviceQuery">
              {{ $t('product.product-authorize.314975-6') }}
            </el-button>
            <el-button :icon="Refresh" size="small" @click="resetDeviceQuery">
              {{ $t('product.product-authorize.314975-7') }}
            </el-button>
          </el-form-item>
        </el-form>
        <el-table
          v-loading="deviceLoading"
          :data="deviceList"
          ref="singleTableRef"
          size="small"
          :border="false"
          @row-click="rowClick"
          highlight-current-row
        >
          <el-table-column :label="$t('product.product-authorize.314975-19')" width="60" align="center">
            <template #default="scope">
              <input type="radio" :checked="scope.row.isSelect" name="device" />
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('product.product-authorize.314975-17')"
            align="left"
            prop="deviceName"
            min-width="160"
          />
          <el-table-column
            :label="$t('product.product-authorize.314975-0')"
            align="center"
            prop="serialNumber"
            min-width="150"
          />
          <el-table-column
            :label="$t('product.product-authorize.314975-21')"
            align="center"
            prop="userName"
            min-width="120"
          />
          <el-table-column :label="$t('product.product-authorize.314975-22')" align="center" prop="status" width="100">
            <template #default="scope">
              <dict-tag :options="iot_device_status" :value="scope.row.status" />
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="deviceTotal > 0"
          layout="prev, pager, next"
          :total="deviceTotal"
          v-model:page="deviceParams.pageNum"
          v-model:limit="deviceParams.pageSize"
          @pagination="getDeviceList"
        />
      </div>
      <div v-if="editType == 'remark'">
        <el-input
          v-model="form.remark"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 6 }"
          :placeholder="$t('product.product-authorize.314975-23')"
          style="width: 420px"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 设备详情对话框 -->
    <el-dialog :title="$t('product.product-authorize.314975-26')" v-model="openDevice" width="600px" append-to-body>
      <div v-if="device == null" style="text-align: center">
        <el-icon style="color: #e6a23c"><WarningFilled /></el-icon>
        {{ $t('product.product-authorize.314975-27') }}
      </div>
      <el-descriptions border :column="2" size="default" v-if="device != null">
        <el-descriptions-item :label="$t('product.product-authorize.314975-20')">
          {{ device.deviceId }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-17')">
          {{ device.deviceName }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-0')">
          {{ device.serialNumber }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-22')">
          <dict-tag :options="iot_device_status" :value="device.status" size="small" />
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-32')">
          <el-tag v-if="device.isShadow == 1" type="success">{{ $t('product.product-authorize.314975-33') }}</el-tag>
          <el-tag v-else type="info">{{ $t('product.index.091251-21') }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-35')">
          <dict-tag :options="iot_location_way" :value="device.locationWay" size="small" />
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-39')">
          {{ device.productName }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-40')">
          {{ device.userName }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-41')">
          Version {{ device.firmwareVersion }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-42')">
          {{ device.networkAddress }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-43')">
          {{ device.longitude }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-44')">
          {{ device.latitude }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-45')">
          {{ device.networkIp }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-46')">
          {{ device.rssi }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-47')">
          {{ device.createTime }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-48')">
          {{ device.activeTime }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('product.product-authorize.314975-49')">
          {{ device.remark }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="goToEditDevice(device.deviceId)" type="primary">
            {{ $t('product.product-authorize.314975-50') }}
          </el-button>
          <el-button @click="closeDevice">{{ $t('product.product-authorize.314975-51') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Delete, Download, WarningFilled } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage, ElNotification } from 'element-plus';
import { getDeviceBySerialNumber as getDeviceBySerialNumberApi } from '@/api/iot/device';
import { listUnAuthDevice } from '@/api/iot/device';
import {
  listAuthorize,
  getAuthorize,
  delAuthorize,
  addProductAuthorizeByNum,
  updateAuthorize,
} from '@/api/iot/authorize';
import { parseTime } from '@/utils/ruoyi';
import useDict from '@/utils/dict/useDict';

const { iot_auth_status, iot_device_status, iot_location_way } = useDict(
  'iot_auth_status',
  'iot_device_status',
  'iot_location_way'
);
const { proxy } = getCurrentInstance() as any;
const router = useRouter();

const props = defineProps({
  product: {
    type: Object,
    default: null,
  },
});

const queryFormRef = ref();
const queryDeviceFormRef = ref();
const singleTableRef = ref();

const device = ref<any>({});
const openDevice = ref(false);
const deviceLoading = ref(true);
const deviceTotal = ref(0);
const deviceList = ref<any[]>([]);
const deviceParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: null as any,
  deviceName: null as any,
  productId: 0,
  productName: null as any,
  userName: null as any,
  tenantId: null as any,
  tenantName: null as any,
  serialNumber: null as any,
  status: null as any,
  networkAddress: null as any,
  activeTime: null as any,
});
const editType = ref('');
const editWidth = ref('500px');
const loading = ref(true);
const ids = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const authorizeList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const createNum = ref(10);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  authorizeCode: null as any,
  productId: null as any,
  deviceId: null as any,
  serialNumber: null as any,
  userId: null as any,
  userName: null as any,
  status: null as any,
});
const form = ref<any>({});
const productInfo = ref<any>({});

watch(
  () => props.product,
  (newVal) => {
    productInfo.value = newVal;
    if (productInfo.value && productInfo.value.productId != 0) {
      queryParams.productId = productInfo.value.productId;
      deviceParams.productId = productInfo.value.productId;
      getList();
      getDeviceList();
    }
  }
);

onMounted(() => {
  const productId = props.product?.productId;
  if (productId) {
    queryParams.productId = productId;
    deviceParams.productId = productId;
    getList();
    getDeviceList();
  }
});

/** 获取设备详情 */
function getDeviceDetail(serialNumber: string) {
  openDevice.value = true;
  getDeviceBySerialNumberApi(serialNumber).then((response: any) => {
    device.value = response.data;
  });
}

/** 修改按钮操作 */
function goToEditDevice(deviceId: any) {
  openDevice.value = false;
  router.push({ path: '/iot/device/edit', query: { deviceId } });
}

/** 查询设备列表 */
function getDeviceList() {
  deviceLoading.value = true;
  const params = { ...deviceParams, params: {} };
  listUnAuthDevice(params).then((response: any) => {
    for (let i = 0; i < response.rows.length; i++) {
      response.rows[i].isSelect = false;
    }
    deviceList.value = response.rows;
    deviceTotal.value = response.total;
    deviceLoading.value = false;
  });
}

function handleDeviceQuery() {
  deviceParams.pageNum = 1;
  getDeviceList();
}

function resetDeviceQuery() {
  proxy?.resetForm(queryDeviceFormRef.value);
  handleDeviceQuery();
}

/** 单选数据 */
function rowClick(deviceRow: any) {
  if (deviceRow != null) {
    setRadioSelected(deviceRow.deviceId);
    form.value.userId = deviceRow.userId;
    form.value.userName = deviceRow.userName;
    form.value.deviceId = deviceRow.deviceId;
    form.value.serialNumber = deviceRow.serialNumber;
  }
}

function setRadioSelected(deviceId: any) {
  for (let i = 0; i < deviceList.value.length; i++) {
    deviceList.value[i].isSelect = deviceList.value[i].deviceId == deviceId;
  }
}

/** 查询产品授权码列表 */
function getList() {
  loading.value = true;
  listAuthorize(queryParams).then((response: any) => {
    authorizeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function closeDevice() {
  openDevice.value = false;
}

function reset() {
  form.value = {
    authorizeId: null,
    authorizeCode: null,
    productId: '',
    userId: '',
    deviceId: null,
    serialNumber: null,
    userName: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
  device.value = {};
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.authorizeId);
  multiple.value = !selection.length;
}

/** 批量新增按钮操作 */
function handleAdd() {
  ElMessageBox.prompt('', proxy?.$t('product.product-authorize.314975-52'), {
    customClass: 'createNum',
    confirmButtonText: proxy?.$t('product.product-authorize.314975-53'),
    cancelButtonText: proxy?.$t('product.product-authorize.314975-54'),
    inputPattern: /^(1000|[1-9][0-9]{0,2})$/,
    inputErrorMessage: proxy?.$t('product.product-authorize.314975-55'),
    inputType: 'number',
    inputValue: String(createNum.value),
  })
    .then(({ value }) => {
      createNum.value = Number(value);
      if (queryParams.productId != null) {
        const addData = { productId: queryParams.productId, createNum: createNum.value };
        addProductAuthorizeByNum(addData).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('product.product-authorize.314975-56'));
          getList();
          createNum.value = 10;
        });
      }
    })
    .catch(() => {
      ElMessage({ type: 'info', message: proxy?.$t('product.product-authorize.314975-57') });
    });
}

/** 修改按钮操作 */
function handleUpdate(row: any, type: string) {
  reset();
  editType.value = type;
  const authorizeId = row.authorizeId || ids.value;
  getAuthorize(authorizeId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    if (editType.value == 'auth') {
      title.value = proxy?.$t('product.product-authorize.314975-58');
      editWidth.value = '900px';
    } else {
      title.value = proxy?.$t('product.product-authorize.314975-49');
      editWidth.value = '500px';
    }
    for (let i = 0; i < deviceList.value.length; i++) {
      deviceList.value[i].isSelect = false;
    }
  });
}

/** 提交按钮 */
function submitForm() {
  if (editType.value == 'auth') {
    if (form.value.deviceId != null && form.value.deviceId != 0) {
      updateAuthorize(form.value).then(() => {
        proxy?.$modal.msgSuccess(proxy?.$t('product.product-authorize.314975-59'));
        open.value = false;
        getList();
      });
    } else {
      proxy?.$modal.msg(proxy?.$t('product.product-authorize.314975-60'));
    }
  } else if (form.value.authorizeId != null) {
    updateAuthorize(form.value).then(() => {
      proxy?.$modal.msgSuccess(proxy?.$t('product.product-authorize.314975-61'));
      open.value = false;
      getList();
    });
  }
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const authorizeIds = row?.authorizeId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('product.product-authorize.314975-62', [authorizeIds]))
    .then(() => {
      return delAuthorize(authorizeIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('product.product-authorize.314975-63'));
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/authorize/export', { ...queryParams }, `authorize_${new Date().getTime()}.xlsx`);
}

function selectable(row: any) {
  return row.deviceId != null ? false : true;
}

function celldblclick(row: any, column: any) {
  const text = row[column.property];
  if (text) {
    navigator.clipboard
      .writeText(String(text))
      .then(() => {
        ElNotification({
          title: proxy?.$t('product.product-authorize.314975-64'),
          message: proxy?.$t('product.product-authorize.314975-66'),
          type: 'success',
          offset: 50,
          duration: 2000,
        });
      })
      .catch(() => {
        ElNotification({
          title: proxy?.$t('product.product-authorize.314975-67'),
          message: proxy?.$t('product.product-authorize.314975-68'),
          type: 'error',
          offset: 50,
          duration: 2000,
        });
      });
  }
}
</script>

<style lang="scss">
.createNum {
  width: 360px;
  input {
    width: 300px;
  }
}
</style>
