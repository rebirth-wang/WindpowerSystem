<template>
  <div class="system-role-authrole">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" class="search-form">
        <el-form-item prop="userName">
          <el-input
            v-model="queryParams.userName"
            :placeholder="$t('role.auth-user.876234-0')"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="phonenumber">
          <el-input
            v-model="queryParams.phonenumber"
            :placeholder="$t('user.index.098976-4')"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            :icon="Plus"
            size="small"
            @click="openSelectUser"
            v-hasPermi="['system:role:add']"
          >
            {{ $t('role.auth-user.876234-1') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="CircleClose"
            size="small"
            :disabled="multiple"
            @click="cancelAuthUserAll"
            v-hasPermi="['system:role:remove']"
          >
            {{ $t('role.auth-user.876234-2') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Close" size="small" @click="handleClose">{{ $t('close') }}</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" :border="false">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('user.profile.index.894502-1')" prop="userName" :show-overflow-tooltip="true" />
        <el-table-column :label="$t('user.index.098976-11')" prop="nickName" :show-overflow-tooltip="true" />
        <el-table-column :label="$t('user.index.098976-19')" prop="email" :show-overflow-tooltip="true" />
        <el-table-column :label="$t('user.index.098976-3')" prop="phonenumber" :show-overflow-tooltip="true" />
        <el-table-column :label="$t('status')" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('opation')" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              :icon="CircleClose"
              @click="cancelAuthUser(scope.row)"
              v-hasPermi="['system:role:remove']"
            >
              {{ $t('role.auth-user.876234-3') }}
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
      <select-user ref="selectRef" :roleId="queryParams.roleId" @ok="handleQuery" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Search, Refresh, Plus, CircleClose, Close } from '@element-plus/icons-vue';
import { allocatedUserList, authUserCancel, authUserCancelAll } from '@/api/system/role';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';
import selectUser from './selectUser.vue';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const route = useRoute();
const { dict } = useDict('sys_normal_disable');

const loading = ref(true);
const userIds = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const userList = ref<any[]>([]);
const queryFormRef = ref();
const selectRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleId: undefined as any,
  userName: undefined as string | undefined,
  phonenumber: undefined as string | undefined,
});

function getList() {
  loading.value = true;
  allocatedUserList(queryParams).then((response: any) => {
    userList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleClose() {
  const obj = { path: '/enterprise/role' };
  (proxy as any).$tab.closeOpenPage(obj);
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
  userIds.value = selection.map((item) => item.userId);
  multiple.value = !selection.length;
}

function openSelectUser() {
  selectRef.value?.show();
}

function cancelAuthUser(row: any) {
  const roleId = queryParams.roleId;
  (proxy as any).$modal
    .confirm(t('role.auth-user.876234-4') + row.userName + t('role.index.094567-37'))
    .then(() => {
      return authUserCancel({ userId: row.userId, roleId: roleId });
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('role.auth-user.876234-5'));
    })
    .catch(() => {});
}

function cancelAuthUserAll() {
  const roleId = queryParams.roleId;
  const userIdStr = userIds.value.join(',');
  (proxy as any).$modal
    .confirm(t('role.auth-user.876234-6'))
    .then(() => {
      return authUserCancelAll({ roleId: roleId, userIds: userIdStr });
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('role.auth-user.876234-5'));
    })
    .catch(() => {});
}

onMounted(() => {
  const roleId = route.params && route.params.roleId;
  if (roleId) {
    queryParams.roleId = roleId;
    getList();
  }
});
</script>

<style lang="scss" scoped>
.system-role-authrole {
  padding: 20px;
}
.search-card {
  margin-bottom: 15px;
  height: 15%;
  width: 100%;
}
.search-form {
  margin-bottom: -18px;
  height: 10%;
}
</style>
