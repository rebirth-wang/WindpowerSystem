<template>
  <div class="system-terminal-user">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="userName">
          <el-input
            v-model="queryParams.userName"
            :placeholder="$t('user.index.098976-2')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="phonenumber">
          <el-input
            v-model="queryParams.phonenumber"
            :placeholder="$t('user.index.098976-18')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('user.index.098976-6')"
            clearable
            style="width: 192px"
          >
            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:user:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:user:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column
          :label="$t('user.index.098976-30')"
          align="left"
          key="userId"
          prop="userId"
          v-if="columns[0].visible"
          :show-overflow-tooltip="true"
          min-width="100"
        />
        <el-table-column
          :label="$t('user.index.098976-10')"
          align="left"
          key="userName"
          prop="userName"
          v-if="columns[1].visible"
          :show-overflow-tooltip="true"
          min-width="220"
        />
        <el-table-column
          :label="$t('user.index.098976-11')"
          align="left"
          key="nickName"
          prop="nickName"
          v-if="columns[2].visible"
          :show-overflow-tooltip="true"
          min-width="220"
        />
        <el-table-column
          :label="$t('user.index.098976-13')"
          align="center"
          key="phonenumber"
          prop="phonenumber"
          v-if="columns[3].visible"
          width="120"
        />
        <el-table-column :label="$t('status')" align="center" key="status" v-if="columns[4].visible" width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('creatTime')"
          align="center"
          prop="createTime"
          v-if="columns[6].visible"
          width="170"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="215"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:user:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:user:remove']"
            >
              {{ $t('del') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Refresh"
              v-hasPermi="['system:user:resetPwd']"
              @click="handleResetPwd(scope.row)"
            >
              {{ $t('user.index.098976-15') }}
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

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('user.index.098976-11')" prop="nickName">
          <el-input
            v-model="form.nickName"
            :placeholder="$t('user.index.098976-16')"
            maxlength="30"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('user.index.098976-13')" prop="phonenumber">
          <el-input
            v-model="form.phonenumber"
            :placeholder="$t('user.index.098976-18')"
            maxlength="11"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('user.index.098976-19')" prop="email">
          <el-input
            v-model="form.email"
            :placeholder="$t('user.index.098976-20')"
            maxlength="50"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item v-if="form.userId == undefined" :label="$t('user.index.098976-10')" prop="userName">
          <el-input
            v-model="form.userName"
            :placeholder="$t('user.index.098976-2')"
            maxlength="30"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item v-if="form.userId == undefined" :label="$t('role.selectUser.093468-2')" prop="password">
          <el-input
            v-model="form.password"
            :placeholder="$t('user.index.098976-22')"
            type="password"
            maxlength="20"
            show-password
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('status')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('role.selectUser.093468-3')" prop="roleIds">
          <el-select
            v-model="form.roleIds"
            multiple
            :placeholder="$t('role.selectUser.093468-4')"
            :disabled="form.userId != undefined"
            style="width: 400px"
          >
            <el-option
              v-for="item in roleOptions"
              :key="item.roleId"
              :label="item.roleName"
              :value="item.roleId"
              :disabled="item.status == 1"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('remark')">
          <el-input v-model="form.remark" type="textarea" :placeholder="$t('plzInput')" style="width: 400px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Delete, Plus, Edit } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';
import {
  terminalUserList,
  getUser,
  delUser,
  addUser,
  updateUser,
  resetUserPwd,
  changeUserStatus,
  getRole,
} from '@/api/system/user';
import { getConfigKey } from '@/api/system/config';

const { proxy } = getCurrentInstance() as any;
const { sys_normal_disable } = useDict('sys_normal_disable');

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const loading = ref(true);
const ids = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const userList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const initPassword = ref<string | undefined>(undefined);
const roleOptions = ref<any[]>([]);
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined as any,
  phonenumber: undefined as any,
  status: undefined as any,
  deptId: undefined as any,
});

const columns = reactive([
  { key: 0, label: proxy?.$t('user.index.098976-30'), visible: true },
  { key: 1, label: proxy?.$t('user.index.098976-10'), visible: true },
  { key: 2, label: proxy?.$t('user.index.098976-11'), visible: true },
  { key: 3, label: proxy?.$t('user.index.098976-29'), visible: true },
  { key: 4, label: proxy?.$t('user.index.098976-13'), visible: true },
  { key: 5, label: proxy?.$t('status'), visible: true },
  { key: 6, label: proxy?.$t('creatTime'), visible: true },
]);

const rules = reactive<any>({
  userName: [
    { required: true, message: proxy?.$t('user.index.098976-31'), trigger: 'blur' },
    { min: 2, max: 20, message: proxy?.$t('user.index.098976-32'), trigger: 'blur' },
  ],
  nickName: [{ required: true, message: proxy?.$t('user.index.098976-33'), trigger: 'blur' }],
  password: [
    { required: true, message: proxy?.$t('user.index.098976-34'), trigger: 'blur' },
    { min: 5, max: 20, message: proxy?.$t('user.index.098976-35'), trigger: 'blur' },
  ],
  roleIds: [{ required: true, message: proxy?.$t('user.index.098976-36'), trigger: 'change' }],
  status: [{ required: true }],
  email: [{ type: 'email', message: proxy?.$t('user.index.098976-37'), trigger: ['blur', 'change'] }],
  phonenumber: [
    { required: true, message: proxy?.$t('user.index.098976-38'), trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: proxy?.$t('user.index.098976-39'), trigger: 'blur' },
  ],
});

onMounted(() => {
  const deptId = proxy?.$route?.params?.deptId;
  if (deptId) queryParams.deptId = deptId;
  getList();
  getConfigKey('sys.user.initPassword').then((response: any) => {
    initPassword.value = response.msg;
  });
});

function getList() {
  loading.value = true;
  form.value.deptId = queryParams.deptId;
  terminalUserList(proxy?.addDateRange(queryParams, [])).then((response: any) => {
    userList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleStatusChange(row: any) {
  const text = row.status === '0' ? proxy?.$t('simulate.index.111543-54') : proxy?.$t('simulate.index.111543-55');
  proxy?.$modal
    .confirm(proxy?.$t('user.index.098976-40') + text + '""' + row.userName + proxy?.$t('user.index.098976-41'))
    .then(() => {
      return changeUserStatus(row.userId, row.status);
    })
    .then(() => {
      proxy?.$modal.msgSuccess(text + proxy?.$t('success'));
    })
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.userId);
  multiple.value = !selection.length;
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: 0,
    remark: undefined,
    postIds: [],
    roleIds: [],
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

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy?.$t('user.index.098976-42');
  form.value.deptId = queryParams.deptId;
  form.value.password = initPassword.value;
  // 获取角色列表
  if (form.value.deptId != undefined) {
    getRole(form.value.deptId).then((response: any) => {
      roleOptions.value = response.roles;
    });
  }
}

function getRoleList(node: any) {
  form.value.deptId = node.id;
  if (form.value.deptId != undefined && form.value.deptId != null) {
    getRole(form.value.deptId).then((response: any) => {
      roleOptions.value = response.roles;
    });
  }
}

function handleUpdate(row: any) {
  reset();
  const userId = row.userId || ids.value;
  getUser(userId).then((response: any) => {
    form.value = response.data;
    roleOptions.value = response.roles;
    form.value.postIds = response.postIds;
    form.value.roleIds = response.roleIds;
    open.value = true;
    title.value = proxy?.$t('user.index.098976-43');
    form.value.password = '';
  });
}

function handleResetPwd(row: any) {
  ElMessageBox.prompt(
    proxy?.$t('user.index.098976-44') + row.userName + proxy?.$t('user.index.098976-45'),
    proxy?.$t('user.index.098976-46'),
    {
      confirmButtonText: proxy?.$t('confirm'),
      cancelButtonText: proxy?.$t('cancel'),
      closeOnClickModal: false,
      inputPattern: /^.{5,20}$/,
      inputErrorMessage: proxy?.$t('user.index.098976-35'),
    }
  )
    .then(({ value }: any) => {
      resetUserPwd(row.userId, value).then(() => {
        proxy?.$modal.msgSuccess(proxy?.$t('user.index.098976-47') + value);
      });
    })
    .catch(() => {});
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addUser(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  const userIds = row.userId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('user.index.098976-48', [userIds]))
    .then(() => {
      return delUser(userIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.userId;
}
</script>

<style lang="scss" scoped>
.system-terminal-user {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}
</style>
