<template>
  <div class="iot-order">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="76px"
        style="margin-bottom: -18px; padding-top: 3px"
        @submit.prevent
      >
        <el-form-item prop="deviceId">
          <el-select
            v-model="queryParams.deviceId"
            :placeholder="$t('scene.edit.202832-15')"
            filterable
            @change="getCommandList()"
            @keyup.enter="handleQuery"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="(item, index) in deviceList"
              :key="index"
              :label="item.deviceName"
              :value="item.deviceId"
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['order:control:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['order:control:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="controlList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('order.index.045965-4')" align="left" prop="modelNames" min-width="250" />
        <el-table-column :label="$t('order.index.045965-5')" align="left" prop="deviceName" width="150" />
        <el-table-column :label="$t('order.index.045965-6')" align="center" prop="userName" width="110" />
        <el-table-column :label="$t('order.index.045965-7')" align="center" prop="count" width="100" />
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
        <el-table-column :label="$t('order.index.045965-8')" align="center" prop="startTime" width="162">
          <template #default="scope">
            <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('order.index.045965-9')" align="center" prop="endTime" width="162">
          <template #default="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('iot.group.index.637432-9')" align="left" prop="remark" min-width="180" />
        <el-table-column fixed="right" :label="$t('iot.group.index.637432-10')" align="center" width="270">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              :disabled="!scope.row.filePath"
              @click="handleDownLoad(scope.row.filePath)"
            >
              {{ $t('order.index.045965-31') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Picture"
              :disabled="!scope.row.imgUrl"
              @click="handleDownLoad(scope.row.imgUrl)"
            >
              {{ $t('order.index.045965-32') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="EditPen"
              @click="handleUpdate(scope.row)"
              v-if="canEdit(scope.row)"
              v-hasPermi="['order:control:edit']"
            >
              {{ $t('edit') }}
            </el-button>
            <el-button
              v-if="scope.row.status == 0"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['order:control:remove']"
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

    <!-- 添加或修改指令权限控制对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('order.index.045965-10')" prop="deviceId">
          <el-select
            style="width: 400px"
            v-model="form.deviceId"
            :placeholder="$t('scene.edit.202832-15')"
            filterable
            @change="getCommandList()"
          >
            <el-option
              v-for="(item, index) in deviceList"
              :key="index"
              :label="item.deviceName"
              :value="item.deviceId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-11')" prop="userId">
          <el-select style="width: 400px" v-model="form.userId" :placeholder="$t('scene.edit.202832-15')" filterable>
            <el-option
              v-for="(item, index) in userList"
              :key="index"
              :label="item.nickName"
              :value="item.userId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-12')" prop="selectOrder" v-loading="loadingSelect">
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">
            {{ $t('order.index.045965-13') }}
          </el-checkbox>
          <div class="select-border">
            <el-checkbox-group v-model="checkedEqFunc" @change="handleCheckedEqFuncChange">
              <el-row>
                <el-col :span="12" v-for="command in commandsList" :key="command.modelId">
                  <el-checkbox :value="command.modelId">{{ command.modelName }}</el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </div>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-14')" prop="count">
          <el-input-number v-model="form.count" :min="1" :max="100000" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-15')" prop="startTime">
          <el-date-picker
            style="width: 400px"
            clearable
            v-model="form.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            :placeholder="$t('order.index.045965-16')"
          ></el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-17')" prop="endTime">
          <el-date-picker
            clearable
            v-model="form.endTime"
            style="width: 400px"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            :placeholder="$t('order.index.045965-18')"
          ></el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-19')" prop="file">
          <file-upload
            :model-value="form.filePath"
            :limit="1"
            :fileSize="10"
            @update:model-value="getFilePath"
          ></file-upload>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-20')">
          <image-upload :model-value="form.imgUrl" :fileSize="1" @update:model-value="getImagePath"></image-upload>
        </el-form-item>
        <el-form-item :label="$t('order.index.045965-21')" prop="remark">
          <el-input
            v-model="form.remark"
            style="width: 400px"
            :autosize="{ minRows: 3, maxRows: 5 }"
            type="textarea"
            :placeholder="$t('order.index.045965-22')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('order.index.045965-23') }}</el-button>
          <el-button @click="cancel">{{ $t('order.index.045965-24') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Refresh, Plus, Delete, View, Picture, EditPen } from '@element-plus/icons-vue';
import { listControl, getControl, delControl, addControl, updateControl } from '@/api/iot/control';
import { listDeviceShort } from '@/api/iot/device';
import { getWriteList } from '@/api/iot/model';
import { getByDeptId } from '@/api/system/user';
import { useUserStore } from '@/stores/modules/user';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const controlList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const loadingSelect = ref(false);
const isIndeterminate = ref(false);
const checkedEqFunc = ref<any[]>([]);
const chooseAllwrList = ref<any[]>([]);
const checkAll = ref(false);
const deviceList = ref<any[]>([]);
const userList = ref<any[]>([]);
const commandsList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  modelNames: '',
  pageNum: 1,
  pageSize: 10,
  deviceId: null as any,
});

const form = ref<any>({ status: 1 });

function validateEndDate(_rule: any, value: any, callback: any) {
  const { startTime } = form.value;
  if (!startTime || !value) return callback();
  if (startTime > value) {
    callback(new Error('结束时间不能早于开始时间'));
  } else {
    callback();
  }
}

const rules = reactive<any>({
  deviceId: [{ required: true, message: proxy.$t('order.index.045965-25'), trigger: 'change' }],
  userId: [{ required: true, message: proxy.$t('order.index.045965-26'), trigger: 'change' }],
  count: [{ required: true, message: proxy.$t('order.index.045965-27'), trigger: 'blur' }],
  startTime: [{ required: true, message: proxy.$t('order.index.045965-28'), trigger: 'change' }],
  endTime: [
    { required: true, message: proxy.$t('order.index.045965-29'), trigger: 'change' },
    { validator: validateEndDate, trigger: 'change' },
  ],
});

onMounted(() => {
  getList();
  getDeviceList();
});

function getList() {
  loading.value = true;
  listControl(queryParams).then((response: any) => {
    controlList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function getDeviceList() {
  listDeviceShort({ showChild: true, pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
    }
  });
}

function getCommandList() {
  loadingSelect.value = true;
  isIndeterminate.value = false;
  try {
    const params = { deviceId: form.value.deviceId, pageNum: 1, pageSize: 9999 };
    chooseAllwrList.value = [];
    getWriteList(params).then((res: any) => {
      if (res.code === 200) {
        commandsList.value = res.rows;
        commandsList.value.forEach((item: any) => {
          chooseAllwrList.value.push(item.modelId);
        });
      }
    });
  } finally {
    loadingSelect.value = false;
  }
}

function getUserList() {
  getByDeptId({ pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) {
      userList.value = res.rows;
    }
  });
}

function handleCheckAllChange(val: boolean) {
  checkedEqFunc.value = val ? [...chooseAllwrList.value] : [];
  isIndeterminate.value = false;
}

function handleCheckedEqFuncChange(value: any[]) {
  const checkedCount = value.length;
  checkAll.value = checkedCount === commandsList.value.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < commandsList.value.length;
}

function reset() {
  form.value = {
    id: null,
    tenantId: null,
    selectOrder: null,
    status: 1,
    userId: null,
    deviceId: null,
    count: null,
    file: null,
    startTime: null,
    endTime: null,
    img: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    imgUrl: '',
    filePath: '',
  };
  formRef.value?.resetFields();
}

function toNumberOrNull(value: any) {
  if (value === null || value === undefined || value === '') return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
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
  checkedEqFunc.value = [];
  commandsList.value = [];
  title.value = proxy.$t('order.index.045965-1');
  getDeviceList();
  getUserList();
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleDownLoad(path: string) {
  const url = window.location.origin + import.meta.env.VITE_APP_BASE_API + path;
  window.open(url);
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  getControl(id).then(async (response: any) => {
    form.value = {
      ...(response.data || {}),
      count: toNumberOrNull(response.data?.count),
    };
    checkedEqFunc.value = response.data.selectOrder.split(',').map(Number);
    open.value = true;
    title.value = proxy.$t('order.index.045965-2');
    getDeviceList();
    getUserList();
    await getCommandList();
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

function getFilePath(data: any) {
  form.value.filePath = data;
}

function getImagePath(data: any) {
  form.value.imgUrl = data;
}

function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (checkedEqFunc.value.length == 0) {
        proxy.$modal.msgError(proxy.$t('order.index.045965-30'));
        return;
      }
      form.value.selectOrder = checkedEqFunc.value.join(',');
      if (form.value.id != null) {
        updateControl(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(res.msg);
            getList();
          }
          open.value = false;
        });
      } else {
        addControl(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess('success');
            getList();
          }
          open.value = false;
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const deleteIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('order.index.045965-3', [deleteIds]))
    .then(() => {
      return delControl(deleteIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess('success');
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function canEdit(row: any) {
  const currentUserName = userStore.name;
  const currentRoles = userStore.roles;
  if (currentUserName === 'admin') return true;
  if (currentRoles && currentRoles.includes('admin')) return true;
  if (row.createBy === currentUserName) return true;
  return false;
}

function getRowKeys(row: any) {
  return row.id;
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('order/control/export', { ...queryParams }, `control_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.iot-order {
  padding: 20px;

  .select-border {
    margin-top: 5px;
    border: 1px solid #e5e6e7;
    background: #ffffff none;
    border-radius: 4px;
    padding: 16px;
    height: 300px;
    overflow: auto;
  }
}
</style>
