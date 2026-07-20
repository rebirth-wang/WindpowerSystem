<template>
  <div>
    <el-row :gutter="10" style="margin-bottom: 16px">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Plus"
          @click="handleAlertUser"
          v-hasPermi="['iot:device:alert:user:add']"
        >
          {{ $t('add') }}
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :search="false"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceUserList" :border="false">
      <el-table-column :label="$t('user.index.098976-30')" align="left" prop="userId" min-width="160" />
      <el-table-column :label="$t('user.profile.index.894502-1')" align="center" prop="userName" min-width="150" />
      <el-table-column :label="$t('user.index.098976-3')" align="center" prop="phoneNumber" min-width="120" />
      <el-table-column fixed="right" :label="$t('opation')" align="center" width="100">
        <template #default="scope">
          <el-button
            link
            :icon="Delete"
            size="small"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:device:alert:user:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="userTotal > 0"
      :total="userTotal"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
      style="height: 40px"
    />

    <!--选择告警用户对话框-->
    <el-dialog :title="$t('alert-user.837395-0')" v-model="open" width="800px">
      <el-form :model="permParams" ref="permFormRef" :inline="true" label-width="68px">
        <el-form-item prop="userName">
          <el-input
            v-model="permParams.userName"
            :placeholder="$t('online.093480-2')"
            clearable
            @keyup.enter="handleUserQuery"
          />
        </el-form-item>
        <el-form-item prop="phonenumber">
          <el-input
            v-model="permParams.phonenumber"
            :placeholder="$t('user.index.098976-4')"
            clearable
            @keyup.enter="handleUserQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleUserQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        ref="singleTableRef"
        :data="userList"
        highlight-current-row
        size="small"
        @selection-change="changeCheckBoxValue"
        :row-key="getRowKeys"
        :border="false"
      >
        <el-table-column type="selection" width="55" align="center" :reserve-selection="true" />
        <el-table-column :label="$t('user.index.098976-30')" align="left" prop="userId" min-width="100" />
        <el-table-column :label="$t('user.profile.index.894502-1')" align="left" prop="userName" min-width="160" />
        <el-table-column :label="$t('user.index.098976-3')" align="left" prop="phonenumber" min-width="120" />
      </el-table>

      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        v-model:page="permParams.pageNum"
        v-model:limit="permParams.pageSize"
        @pagination="getUserList"
        style="margin-bottom: 30px"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:device:alert:user:add']">
            {{ $t('confirm') }}
          </el-button>
          <el-button @click="closeSelectUser">{{ $t('close') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { Plus, Delete, Search, Refresh } from '@element-plus/icons-vue';
import { alertUserList, listUser, addAlertUser, delAlertUser } from '@/api/iot/alertUser';

const proxy = getCurrentInstance()?.proxy as any;

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const total = ref(0);
// 是否显示选择用户弹出层
const open = ref(false);
const userList = ref<any[]>([]);
// 查询参数
const permParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined as string | undefined,
  phonenumber: undefined as string | undefined,
  deviceId: null as number | null,
});
// 遮罩层
const loading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 设备用户表格数据
const deviceUserList = ref<any[]>([]);
// 设备信息
const deviceInfo = ref<any>({});
//用户id
const userIds = ref<number[]>([]);
//选中的数据
const tableData = ref<any[]>([]);
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceId: null as number | null,
});
const userTotal = ref(0);
// 表单参数
const form = ref<any>({});
const permFormRef = ref();
const singleTableRef = ref();

// 获取到父组件传递的device后，刷新列表
watch(
  () => props.device,
  (newVal) => {
    deviceInfo.value = newVal;
    if (deviceInfo.value && deviceInfo.value.deviceId != 0) {
      queryParams.deviceId = deviceInfo.value.deviceId;
      getList();
    }
  }
);

onMounted(() => {
  queryParams.deviceId = props.device.deviceId;
  getList();
});

/** 查询设备用户列表 */
function getList() {
  loading.value = true;
  listUser(queryParams).then((response: any) => {
    deviceUserList.value = response.rows;
    userTotal.value = response.total;
    loading.value = false;
  });
}

function getRowKeys(row: any) {
  return row.userId;
}

// 表单重置
function reset() {
  form.value = {
    deviceId: null,
    userId: null,
    userName: null,
    phoneNumber: null,
  };
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 告警用户弹框搜索按钮操作 */
function handleUserQuery() {
  permParams.pageNum = 1;
  getUserList();
}

//告警用户查询重置
function resetQuery() {
  proxy?.resetForm(permFormRef.value);
  handleUserQuery();
}

function changeCheckBoxValue(selection: any[]) {
  tableData.value = selection;
}

// 选择告警用户弹框
function handleAlertUser() {
  open.value = true;
  getUserList();
}

/** 删除按钮操作 */
function handleDelete(row: any) {
  proxy?.$modal
    .confirm(proxy?.$t('alert-user.837395-1'))
    .then(function () {
      return delAlertUser(row.deviceId, row.userId);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
    })
    .catch(() => {});
}

/** 查询用户 */
function getUserList() {
  alertUserList(permParams).then((response: any) => {
    userList.value = response.rows;
    total.value = response.total;
  });
}

// 重置查询
function resetUserQuery() {
  reset();
}

// 关闭选择用户
function closeSelectUser() {
  open.value = false;
  resetUserQuery();
}

/** 确定按钮 */
function submitForm() {
  userIds.value = tableData.value.map((item) => item.userId);
  const useridStr = JSON.parse(JSON.stringify(userIds.value));
  const params = {
    userIdList: useridStr,
    deviceId: props.device.deviceId,
  };
  addAlertUser(params).then((response: any) => {
    if (response.code == 200) {
      proxy?.$modal.msgSuccess(response.msg);
      resetUserQuery();
      open.value = false;
      getList();
      singleTableRef.value?.clearSelection();
    } else {
      proxy?.$modal.msgError(response.msg);
    }
  });
}
</script>
