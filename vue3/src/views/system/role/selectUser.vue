<template>
  <!-- 授权用户 -->
  <el-dialog :title="$t('role.selectUser.093468-0')" v-model="visible" width="930px" top="5vh" append-to-body>
    <el-form :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item prop="userName">
        <el-input
          v-model="queryParams.userName"
          :placeholder="$t('role.auth-user.876234-0')"
          size="small"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          :placeholder="$t('user.index.098976-18')"
          size="small"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" size="small" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" size="small" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-table
        @row-click="clickRow"
        ref="tableRef"
        :data="userList"
        @selection-change="handleSelectionChange"
        size="small"
        :border="false"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column
          :label="$t('user.profile.index.894502-1')"
          prop="userName"
          align="left"
          min-width="160"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          :label="$t('user.index.098976-11')"
          prop="nickName"
          align="left"
          min-width="160"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          :label="$t('user.index.098976-19')"
          prop="email"
          align="left"
          min-width="130"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          :label="$t('user.index.098976-13')"
          prop="phonenumber"
          align="center"
          min-width="100"
          :show-overflow-tooltip="true"
        />
        <el-table-column :label="$t('status')" align="center" prop="status" min-width="80">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-row>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSelectUser">{{ $t('confirm') }}</el-button>
        <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh } from '@element-plus/icons-vue';
import { unallocatedUserList, authUserSelectAll } from '@/api/system/role';
import { useDict } from '@/utils/dict/useDict';
import { parseTime } from '@/utils/ruoyi';

const props = defineProps({
  roleId: {
    type: [Number, String],
  },
});

const emit = defineEmits(['ok']);

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_normal_disable');

const visible = ref(false);
const userIds = ref<any[]>([]);
const total = ref(0);
const userList = ref<any[]>([]);
const queryFormRef = ref();
const tableRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleId: undefined as any,
  userName: undefined as string | undefined,
  phonenumber: undefined as string | undefined,
});

/** 显示弹框 */
function show() {
  queryParams.roleId = props.roleId;
  getList();
  visible.value = true;
}

function clickRow(row: any) {
  tableRef.value?.toggleRowSelection(row);
}

function handleSelectionChange(selection: any[]) {
  userIds.value = selection.map((item) => item.userId);
}

function getList() {
  unallocatedUserList(queryParams).then((res: any) => {
    userList.value = res.rows;
    total.value = res.total;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleSelectUser() {
  const roleId = queryParams.roleId;
  const userIdStr = userIds.value.join(',');
  if (userIdStr === '') {
    (proxy as any).$modal.msgError(t('role.selectUser.093468-'));
    return;
  }
  authUserSelectAll({ roleId: roleId, userIds: userIdStr }).then((res: any) => {
    (proxy as any).$modal.msgSuccess(res.msg);
    if (res.code === 200) {
      visible.value = false;
      emit('ok');
    }
  });
}

defineExpose({ show });
</script>
