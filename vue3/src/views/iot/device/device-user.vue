<template>
  <div>
    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" :search="false" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceUserList" @selection-change="handleTableSelectionChange" :border="false">
      <el-table-column :label="$t('device.device-user.037521-1')" align="center" prop="userId" width="100" />
      <el-table-column :label="$t('device.device-user.037521-2')" align="left" prop="userName" min-width="140" />
      <el-table-column :label="$t('device.device-user.037521-3')" align="center" prop="phonenumber" width="150" />
      <el-table-column :label="$t('device.device-user.037521-4')" align="center" prop="isOwner" width="150">
        <template #default="scope">
          <dict-tag :options="device_sharing_user_type" :value="scope.row.isOwner" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.device-user.037521-7')" align="center" prop="createTime" width="150">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('device.device-user.037521-8')"
        align="left"
        prop="remark"
        header-align="center"
        min-width="150"
      />
      <el-table-column
        fixed="right"
        :label="$t('opation')"
        align="center"
        class-name="small-padding fixed-width"
        width="150"
        v-if="deviceInfo.isOwner == 1"
      >
        <template #default="scope">
          <el-button
            size="small"
            link
            :icon="Delete"
            @click="handleCancelShare(scope.row)"
            v-hasPermi="['iot:device:user:remove']"
            v-if="scope.row.isOwner == 1"
          >
            {{ $t('device.device-user.037521-46') }}
          </el-button>
          <el-button
            size="small"
            type="danger"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:share:remove']"
            v-if="scope.row.isOwner == 0"
          >
            {{ $t('device.device-user.037521-11') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--设备分享对话框-->
    <el-dialog :title="$t('device.device-user.037521-12')" v-model="open" width="800px">
      <div style="margin-top: -50px">
        <el-divider></el-divider>
      </div>
      <!--用户查询-->
      <el-form :model="permParams" ref="queryFormRef" :rules="rules" :inline="true" label-width="80px" v-if="type == 1">
        <el-form-item :label="$t('device.device-user.037521-3')" prop="phonenumber">
          <el-input
            type="text"
            :placeholder="$t('device.device-user.037521-13')"
            v-model="permParams.phonenumber"
            minlength="10"
            clearable
            size="small"
            show-word-limit
            style="width: 240px"
            @keyup.enter="handleQuery"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" size="small" @click="userQuery">
            {{ $t('device.device-user.037521-14') }}
          </el-button>
        </el-form-item>
      </el-form>

      <!--用户信息和权限设置-->
      <div v-loading="permsLoading" style="background-color: #f8f8f9; line-height: 28px">
        <div v-if="message" style="padding: 20px">{{ message }}</div>
        <div v-if="form.userId" style="padding: 15px">
          <div style="font-weight: bold; line-height: 28px">{{ $t('device.device-user.037521-15') }}</div>
          <span style="width: 80px; display: inline-block">{{ $t('device.device-user.037521-16') }}</span>
          <span>{{ form.userId }}</span>
          <br />
          <span style="width: 80px; display: inline-block">{{ $t('device.device-user.037521-3') }}：</span>
          <span>{{ form.phonenumber }}</span>
          <br />
          <span style="width: 80px; display: inline-block">{{ $t('device.device-user.037521-2') }}：</span>
          <span>{{ form.userName }}</span>
          <br />
          <!--选择权限-->
          <div style="font-weight: bold; margin: 15px 0 10px">{{ $t('device.device-user.037521-19') }}</div>
          <el-table
            :data="sharePermissionList"
            highlight-current-row
            size="small"
            ref="multipleTableRef"
            @select="handleSelectionChange"
            @select-all="handleSelectionAll"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column
              :label="$t('device.device-user.037521-20')"
              align="center"
              key="modelName"
              prop="modelName"
            />
            <el-table-column
              :label="$t('device.device-user.037521-21')"
              align="center"
              key="identifier"
              prop="identifier"
            />
            <el-table-column
              :label="$t('device.device-edit.148398-17')"
              align="left"
              min-width="100"
              header-align="center"
              key="remark"
              prop="remark"
            />
          </el-table>
          <!--选择权限-->
          <div style="font-weight: bold; margin: 15px 0 10px">{{ $t('device.device-edit.148398-17') }}</div>
          <el-input v-model="form.remark" type="textarea" :placeholder="$t('plzInput')" :rows="2" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="submitForm"
            :disabled="!form.userId || !deviceInfo.deviceId"
            v-hasPermi="['iot:device:user:edit']"
          >
            {{ $t('device.device-user.037521-24') }}
          </el-button>
          <el-button @click="closeSelectUser">{{ $t('device.device-user.037521-25') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { Search, Delete } from '@element-plus/icons-vue';
import { permListModel } from '@/api/iot/model';
import { addShare, delShare, getShare, listShare, shareUser, updateShare, delBind } from '@/api/iot/share';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;
const { parseTime } = proxy;

const { device_sharing_user_type } = useDict('iot_yes_no', 'device_sharing_user_type');

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const type = ref(1);
const message = ref('');
const permsLoading = ref(false);
const showSearch = ref(true);
const sharePermissionList = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const total = ref(0);
const deviceUserList = ref<any[]>([]);
const deviceInfo = ref<any>({});
const form = ref<any>({});
const queryFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const permParams = reactive({
  userName: undefined as any,
  phonenumber: undefined as any,
  deviceId: null as any,
});

const rules = {
  phonenumber: [
    { required: true, message: proxy?.$t('device.device-user.037521-26'), trigger: 'blur' },
    { min: 11, max: 11, message: proxy?.$t('device.device-user.037521-27'), trigger: 'blur' },
  ],
};

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceName: null as any,
  userName: null as any,
  userId: null as any,
  tenantName: null as any,
  isOwner: null as any,
  deviceId: null as any,
});

onMounted(() => {
  deviceInfo.value = props.device;
  queryParams.deviceId = props.device?.deviceId;
  getList();
});

function getList() {
  loading.value = true;
  listShare(queryParams).then((response: any) => {
    deviceUserList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function reset() {
  form.value = {
    deviceId: null,
    userId: null,
    deviceName: null,
    userName: null,
    perms: null,
    phonenumber: null,
    remark: null,
  };
  sharePermissionList.value = [];
  message.value = '';
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleUpdate(row: any) {
  reset();
  type.value = 2;
  getShare(row.deviceId, row.userId).then((response: any) => {
    form.value = response.data;
    getPermissionList();
    open.value = true;
  });
}

function shareDevice() {
  type.value = 1;
  open.value = true;
  form.value = {};
}

function handleCancelShare(row: any) {
  const params = { deviceId: row.deviceId, userId: row.userId };
  proxy?.$modal
    .confirm(proxy?.$t('device.device-user.037521-44'))
    .then(() => {
      return delBind(params);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-user.037521-45'));
    })
    .catch(() => {});
}

function handleDelete(row: any) {
  const params = { deviceId: row.deviceId, userId: row.userId };
  proxy?.$modal
    .confirm(proxy?.$t('device.device-user.037521-28'))
    .then(() => {
      return delShare(params);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-user.037521-29'));
    })
    .catch(() => {});
}

function userQuery() {
  queryFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      reset();
      getShareUser();
    }
  });
}

function getShareUser() {
  permsLoading.value = true;
  if (!deviceInfo.value.deviceId) {
    proxy?.$modal.alert(proxy?.$t('device.device-user.037521-30'));
    return;
  }
  permParams.deviceId = deviceInfo.value.deviceId;
  shareUser(permParams).then((response: any) => {
    if (response.data) {
      form.value = response.data;
      getPermissionList();
    } else {
      permsLoading.value = false;
      message.value = proxy?.$t('device.device-user.037521-31');
    }
  });
}

async function getPermissionList() {
  let perms: string[] = [];
  if (form.value.perms) {
    perms = form.value.perms.split(',');
  }
  permListModel(deviceInfo.value.productId).then((response: any) => {
    sharePermissionList.value = [
      {
        identifier: 'ota',
        modelName: proxy?.$t('device.device-user.037521-32'),
        remark: proxy?.$t('device.device-user.037521-33'),
      },
      {
        identifier: 'timer',
        modelName: proxy?.$t('device.device-user.037521-34'),
        remark: proxy?.$t('device.device-user.037521-35'),
      },
      {
        identifier: 'log',
        modelName: proxy?.$t('device.device-user.037521-36'),
        remark: proxy?.$t('device.device-user.037521-37'),
      },
      {
        identifier: 'monitor',
        modelName: proxy?.$t('device.device-user.037521-38'),
        remark: proxy?.$t('device.device-user.037521-39'),
      },
      {
        identifier: 'statistic',
        modelName: proxy?.$t('device.device-user.037521-40'),
        remark: proxy?.$t('device.device-user.037521-41'),
      },
    ];
    sharePermissionList.value = sharePermissionList.value.concat(response.data);
    if (perms.length > 0) {
      for (let i = 0; i < sharePermissionList.value.length; i++) {
        for (let j = 0; j < perms.length; j++) {
          if (sharePermissionList.value[i].identifier == perms[j]) {
            nextTick(() => {
              multipleTableRef.value?.toggleRowSelection(sharePermissionList.value[i], true);
            });
            break;
          }
        }
      }
    }
    permsLoading.value = false;
  });
}

function resetUserQuery() {
  proxy?.resetForm(queryFormRef.value);
  reset();
}

function closeSelectUser() {
  open.value = false;
  resetUserQuery();
}

function handleSelectionChange(selection: any[]) {
  form.value.perms = selection.map((x: any) => x.identifier).join(',');
}

function handleSelectionAll(selection: any[]) {
  form.value.perms = selection.map((x: any) => x.identifier).join(',');
}

function handleTableSelectionChange(selection: any[]) {
  // table selection for main list (unused but kept for compatibility)
}

function submitForm() {
  if (type.value == 2) {
    updateShare(form.value).then(() => {
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-user.037521-42'));
      resetUserQuery();
      open.value = false;
      getList();
    });
  } else if (type.value == 1) {
    form.value.deviceId = deviceInfo.value.deviceId;
    form.value.deviceName = deviceInfo.value.deviceName;
    addShare(form.value).then(() => {
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-user.037521-43'));
      resetUserQuery();
      open.value = false;
      getList();
    });
  }
}
</script>
