<template>
  <div class="app-content">
    <el-card v-show="showSearch" class="search-card">
      <el-form class="search-form" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="name">
          <el-input
            v-model="queryParams.name"
            :placeholder="$t('iot.maintenance.index.839454-0')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="deviceName">
          <el-input
            v-model="queryParams.deviceName"
            :placeholder="$t('iot.maintenance.index.839454-1')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('iot.maintenance.index.839454-2')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="dict in iot_device_maintenance_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="maintenanceType">
          <el-select
            v-model="queryParams.maintenanceType"
            :placeholder="$t('iot.maintenance.index.839454-3')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="dict in iot_device_maintenance_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:maintenance:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:maintenance:edit']">
            {{ $t('edit') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:maintenance:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:maintenance:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="maintenanceList" :border="false" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('iot.maintenance.index.839454-4')" align="left" prop="name" min-width="180" />
        <el-table-column :label="$t('iot.maintenance.index.839454-5')" align="left" prop="deviceName" min-width="180" />
        <el-table-column
          :label="$t('iot.maintenance.index.839454-6')"
          align="center"
          prop="startMaintenanceTime"
          min-width="125"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.startMaintenanceTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('iot.maintenance.index.839454-7')"
          align="center"
          prop="nextMaintenanceTime"
          min-width="125"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.nextMaintenanceTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('iot.maintenance.index.839454-8')" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="iot_device_maintenance_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('iot.maintenance.index.839454-9')" align="center" prop="maintenanceType">
          <template #default="scope">
            <dict-tag :options="iot_device_maintenance_type" :value="scope.row.maintenanceType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('iot.maintenance.index.839454-10')" align="center" prop="createBy" />
        <el-table-column :label="$t('iot.maintenance.index.839454-11')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="150"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:maintenance:query']"
            >
              {{ $t('look') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:maintenance:remove']"
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
    </el-card>

    <!-- 添加或修改设备维保对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item :label="$t('iot.maintenance.index.839454-4')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('iot.maintenance.index.839454-0')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('iot.maintenance.index.839454-5')" prop="deviceId">
          <el-select
            v-model="form.deviceId"
            :placeholder="$t('iot.maintenance.index.839454-12')"
            style="width: 400px"
            @change="changeDevice"
          >
            <el-option
              v-for="(item, index) in deviceList"
              :key="index"
              :label="item.deviceName"
              :value="item.deviceId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="maintenanceTime">
          <template #label>
            <span class="span-box">
              <span>{{ $t('iot.maintenance.index.839454-13') }}</span>
              <el-tooltip :content="$t('iot.maintenance.index.839454-14')" placement="top">
                <el-icon style="margin-left: 3px; color: #909399"><InfoFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input
            v-model="form.maintenanceTime"
            :placeholder="$t('iot.maintenance.index.839454-15')"
            style="width: 400px"
          >
            <template #prepend>
              <el-select
                v-model="form.maintenanceTimeUnit"
                :placeholder="$t('iot.maintenance.index.839454-16')"
                class="prepend-select"
              >
                <el-option
                  v-for="dict in maintenance_unit"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="maintenancePeriod">
          <template #label>
            <span class="span-box">
              <span>{{ $t('iot.maintenance.index.839454-17') }}</span>
              <el-tooltip :content="$t('iot.maintenance.index.839454-18')" placement="top">
                <el-icon style="margin-left: 3px; color: #909399"><InfoFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-input
            v-model="form.maintenancePeriod"
            :placeholder="$t('iot.maintenance.index.839454-19')"
            style="width: 400px"
          >
            <template #prepend>
              <el-select
                v-model="form.maintenancePeriodUnit"
                :placeholder="$t('iot.maintenance.index.839454-16')"
                class="prepend-select"
              >
                <el-option
                  v-for="dict in maintenance_unit"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="startMaintenanceTime">
          <template #label>
            <span class="span-box">
              <span>{{ $t('iot.maintenance.index.839454-6') }}</span>
              <el-tooltip :content="$t('iot.maintenance.index.839454-20')" placement="top">
                <el-icon style="margin-left: 3px; color: #909399"><InfoFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-date-picker
            v-model="form.startMaintenanceTime"
            type="date"
            value-format="YYYY-MM-DD"
            :placeholder="$t('iot.maintenance.index.839454-21')"
            style="width: 400px"
            :disabled-date="(time: Date) => time.getTime() < Date.now() - 8.64e7"
          />
        </el-form-item>
        <el-form-item prop="preWorkTimeType">
          <template #label>
            <span class="span-box">
              <span>{{ $t('iot.maintenance.index.839454-22') }}</span>
              <el-tooltip :content="$t('iot.maintenance.index.839454-23')" placement="top">
                <el-icon style="margin-left: 3px; color: #909399"><InfoFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-select
            v-model="form.preWorkTimeType"
            :placeholder="$t('iot.maintenance.index.839454-24')"
            style="width: 400px"
          >
            <el-option
              v-for="dict in maintenance_pretime_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="expireStopFlag">
          <el-checkbox v-model="form.expireStopFlag" :true-value="1" :false-value="0">
            {{ $t('iot.maintenance.index.839454-25') }}
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit" v-hasPermi="['iot:maintenance:add']" v-show="!form.id">
            {{ $t('add') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            v-hasPermi="['iot:maintenance:edit']"
            v-show="form.id && form.status === 0"
          >
            {{ $t('update') }}
          </el-button>
          <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download, View, InfoFilled } from '@element-plus/icons-vue';
import {
  listMaintenance,
  getMaintenance,
  delMaintenance,
  addMaintenance,
  updateMaintenance,
} from '@/api/iot/maintenance';
import { listDeviceShort } from '@/api/iot/device';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { maintenance_unit, maintenance_pretime_type, iot_device_maintenance_type, iot_device_maintenance_status } =
  useDict(
    'maintenance_unit',
    'maintenance_pretime_type',
    'iot_device_maintenance_type',
    'iot_device_maintenance_status'
  );

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const maintenanceList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const deviceList = ref<any[]>([]);
const formRef = ref<any>(null);
const form = ref<any>({});
const queryFormRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null as any,
  deviceName: null as any,
  status: null as any,
  maintenanceType: null as any,
});

const rules = reactive({
  name: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-26'), trigger: 'blur' }],
  deviceId: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-27'), trigger: 'change' }],
  maintenanceTime: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-28'), trigger: 'blur' }],
  maintenanceTimeUnit: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-29'), trigger: 'blur' }],
  maintenancePeriod: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-30'), trigger: 'blur' }],
  maintenancePeriodUnit: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-31'), trigger: 'blur' }],
  startMaintenanceTime: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-32'), trigger: 'blur' }],
  preWorkTimeType: [{ required: true, message: proxy.$t('iot.maintenance.index.839454-33'), trigger: 'change' }],
});

onMounted(() => {
  getList();
  getDeviceList();
});

function getList() {
  loading.value = true;
  listMaintenance(queryParams)
    .then((res: any) => {
      if (res.code === 200) {
        maintenanceList.value = res.rows;
        total.value = res.total;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

function getDeviceList() {
  listDeviceShort({ showChild: true, pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) deviceList.value = res.rows;
  });
}

function reset() {
  form.value = {
    id: null,
    name: null,
    deviceId: null,
    deviceName: null,
    maintenanceTime: null,
    maintenanceTimeUnit: 1,
    maintenancePeriod: null,
    maintenancePeriodUnit: 1,
    startMaintenanceTime: null,
    nextMaintenanceTime: null,
    expireStopFlag: false,
    preWorkTimeType: null,
    status: null,
    maintenanceType: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
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

function changeDevice(value: any) {
  const selectedDevice = deviceList.value.find((item) => item.deviceId === value);
  form.value.deviceName = selectedDevice ? selectedDevice.deviceName : null;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('iot.maintenance.index.839454-34');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row?: any) {
  reset();
  getMaintenance(row?.id || ids.value).then((res: any) => {
    if (res.code === 200) {
      form.value = res.data;
      open.value = true;
      title.value = proxy.$t('iot.maintenance.index.839454-35');
      nextTick(() => {
        formRef.value?.clearValidate();
      });
    }
  });
}

function handleSubmit() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateMaintenance(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            open.value = false;
            getList();
          }
        });
      } else {
        addMaintenance(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
            open.value = false;
            getList();
          }
        });
      }
    }
  });
}

function handleCancel() {
  open.value = false;
  reset();
}

function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('iot.maintenance.index.839454-36', [deleteIds]))
    .then(() => delMaintenance(deleteIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/maintenance/export', { ...queryParams }, `maintenance_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.app-content {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}
:deep(.prepend-select) {
  width: 70px !important;
  background-color: #ffffff !important;
}
</style>
