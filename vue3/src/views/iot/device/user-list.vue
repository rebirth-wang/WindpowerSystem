<template>
  <el-dialog :title="$t('device.user-list.041943-0')" v-model="openSelectUser" width="800px">
    <el-divider />
    <!--用户数据-->
    <el-form :model="queryParams" ref="queryFormRef" :rules="rules" :inline="true" label-width="80px">
      <el-form-item :label="$t('device.user-list.041943-1')" prop="phonenumber">
        <el-input
          type="text"
          :placeholder="$t('device.user-list.041943-2')"
          v-model="queryParams.phonenumber"
          minlength="10"
          clearable
          show-word-limit
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.user-list.041943-3') }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="userList"
      highlight-current-row
      @current-change="handleCurrentChange"
      :border="false"
    >
      <el-table-column :label="$t('device.device-edit.148398-6')" width="60" align="center">
        <template #default="scope">
          <input type="radio" :checked="scope.row.isSelect" name="user" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('device.user-list.041943-5')" align="center" prop="userId" width="120" />
      <el-table-column :label="$t('device.user-list.041943-6')" align="center" prop="userName" />
      <el-table-column :label="$t('device.user-list.041943-7')" align="center" prop="nickName" />
      <el-table-column :label="$t('device.user-list.041943-1')" align="center" prop="phonenumber" width="120" />
      <el-table-column :label="$t('device.user-list.041943-8')" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button type="primary" @click="addDeviceUser">{{ $t('device.user-list.041943-9') }}</el-button>
      <el-button @click="closeSelectUser">{{ $t('device.user-list.041943-10') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { listUser } from '@/api/iot/tool';
import { addDeviceUser as addUser, addDeviceUsers } from '@/api/iot/deviceuser';
import { parseTime } from '@/utils/ruoyi';
import { useI18n } from 'vue-i18n';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const props = defineProps({
  device: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['success']);

const loading = ref(false);
const userList = ref<any[]>([]);
const user = ref<any>({});
const deviceInfo = ref<any[]>([]);
const openSelectUser = ref(false);

const queryFormRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined as any,
  phonenumber: undefined as any,
  status: 0,
  deptId: undefined as any,
});

const rules = {
  phonenumber: [
    { required: true, message: t('device.user-list.041943-11'), trigger: 'blur' },
    { min: 11, max: 11, message: t('device.user-list.041943-12'), trigger: 'blur' },
  ],
};

/** 监听 device prop */
watch(
  () => props.device,
  (newVal: any) => {
    deviceInfo.value = newVal;
  }
);

/** 查询用户列表 */
const getList = () => {
  loading.value = true;
  listUser(queryParams).then((response: any) => {
    userList.value = response.rows;
    loading.value = false;
  });
};

/** 搜索 */
const handleQuery = () => {
  queryFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      queryParams.pageNum = 1;
      getList();
    }
  });
};

/** 重置查询 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  userList.value = [];
};

/** 设置单选选中 */
const setRadioSelected = (userId: any) => {
  userList.value.forEach((item: any) => {
    item.isSelect = item.userId == userId;
    if (item.isSelect) {
      user.value = item;
    }
  });
};

/** 单选数据 */
const handleCurrentChange = (row: any) => {
  if (row != null) {
    setRadioSelected(row.userId);
    user.value = row;
  }
};

/** 关闭选择用户 */
const closeSelectUser = () => {
  openSelectUser.value = false;
  resetQuery();
};

/** 添加设备用户 */
const addDeviceUser = () => {
  const devices = deviceInfo.value;
  if (devices && devices.length > 0 && user.value) {
    if (devices.length == 1) {
      const device = devices[0] as any;
      const form = {
        deviceId: device.deviceId,
        deviceName: device.deviceName,
        userId: user.value.userId,
        userName: user.value.userName,
        phonenumber: user.value.phonenumber,
      };
      addUser(form).then(() => {
        proxy?.$modal.msgSuccess(t('device.user-list.041943-13'));
        resetQuery();
        openSelectUser.value = false;
        emit('success');
      });
    } else {
      const formList = (devices as any[]).map((device: any) => ({
        deviceId: device.deviceId,
        deviceName: device.deviceName,
        userId: user.value.userId,
        userName: user.value.userName,
        phonenumber: user.value.phonenumber,
      }));
      addDeviceUsers(formList).then(() => {
        proxy?.$modal.msgSuccess(t('device.user-list.041943-13'));
        resetQuery();
        openSelectUser.value = false;
        emit('success');
      });
    }
  } else {
    openSelectUser.value = false;
  }
};

/** 打开对话框（供父组件调用） */
const openDialog = (deviceList?: any[]) => {
  if (deviceList) {
    deviceInfo.value = deviceList;
  }
  openSelectUser.value = true;
  resetQuery();
};

defineExpose({ openDialog });
</script>
