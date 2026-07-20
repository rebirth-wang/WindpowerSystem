<template>
  <el-dialog :title="$t('sip.user-list.998538-0')" v-model="open" width="700px" append-to-body>
    <el-form @submit.prevent :model="queryParams" ref="queryRef" :inline="true" style="margin-bottom: -18px">
      <el-form-item :label="$t('sip.user-list.998538-1')" prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          :placeholder="$t('sip.user-list.998538-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="userList"
      @row-click="handleRowClick"
      highlight-current-row
      style="margin-top: 10px"
    >
      <el-table-column width="55" align="center">
        <template #default="scope">
          <el-radio v-model="selectedUserId" :value="scope.row.userId">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sip.user-list.998538-3')" align="center" prop="userName" />
      <el-table-column :label="$t('sip.user-list.998538-4')" align="center" prop="nickName" />
      <el-table-column :label="$t('sip.user-list.998538-1')" align="center" prop="phonenumber" />
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="open = false">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listUser } from '@/api/iot/tool';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['userEvent']);

const open = ref(false);
const loading = ref(false);
const total = ref(0);
const userList = ref<any[]>([]);
const selectedUserId = ref<any>(null);
const selectedUser = ref<any>(null);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  phonenumber: undefined as string | undefined,
});

function getList() {
  loading.value = true;
  listUser(queryParams.value).then((response: any) => {
    userList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.value.phonenumber = undefined;
  handleQuery();
}

function handleRowClick(row: any) {
  selectedUserId.value = row.userId;
  selectedUser.value = row;
}

function handleConfirm() {
  if (selectedUser.value) {
    emit('userEvent', selectedUser.value);
  }
  open.value = false;
}

function openUserList() {
  open.value = true;
  selectedUserId.value = null;
  selectedUser.value = null;
  getList();
}

defineExpose({ openUserList });
</script>
