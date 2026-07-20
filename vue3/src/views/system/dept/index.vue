<template>
  <div class="system-dept">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="deptName">
          <el-input
            v-model="queryParams.deptName"
            :placeholder="$t('system.dept.780956-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('system.dept.780956-2')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_normal_disable"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Sort" @click="toggleExpandAll">
            {{ $t('role.index.094567-18') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Refresh" @click="handleRefreshCache" v-if="isAdmin">
            {{ $t('system.dept.780956-41') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="deptList"
        :border="false"
        row-key="deptId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptId" :label="$t('role.index.094567-38')" min-width="130" align="left" />
        <el-table-column prop="deptName" :label="$t('role.index.094567-7')" min-width="350" align="left" />
        <el-table-column prop="deptType" :label="$t('system.dept.780956-3')" min-width="150" align="center">
          <template #default="scope">
            <dict-tag :options="dict.type.department_type" :value="scope.row.deptType" />
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('status')" width="100" align="center">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="leader" :label="$t('system.dept.780956-4')" min-width="150" align="left" />
        <el-table-column prop="invitationCode" :label="$t('system.dept.780956-38')" min-width="150" align="left" />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="220"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Plus"
              @click="handleAdd(scope.row)"
              v-hasPermi="['system:dept:add']"
              v-if="userStore.dataScope === '4' || isAdmin"
            >
              {{ $t('add') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:dept:edit']"
            >
              {{ $t('edit') }}
            </el-button>
            <el-dropdown
              size="small"
              @command="(command) => handleCommand(command, scope.row)"
              v-hasPermi="['system:role:edit']"
              style="vertical-align: middle; margin-left: 8px"
            >
              <el-button size="small" link>
                {{ $t('system.dept.780956-7') }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="checkPermi(['system:user:edit'])" command="handleUserManage">
                    {{ $t('system.dept.780956-8') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="handleRoleManage">{{ $t('system.dept.780956-9') }}</el-dropdown-item>
                  <el-dropdown-item v-if="scope.row.parentId != 0 && checkPermi(['system:dept:remove'])" command="handleDelete">
                    {{ $t('del') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加或修改机构对话框 -->
      <el-dialog :title="title" v-model="open" width="620px" append-to-body>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
          <el-row>
            <el-col :span="24" v-if="form.parentId !== 0">
              <el-form-item :label="$t('system.dept.780956-10')" prop="parentId">
                <treeselect
                  v-model="form.parentId"
                  :options="deptTreeOptions"
                  :normalizer="normalizer"
                  disabled
                  :placeholder="$t('system.dept.780956-11')"
                  style="width: 400px"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item :label="$t('system.dept.780956-0')" prop="deptName">
            <el-input v-model="form.deptName" :placeholder="$t('system.dept.780956-1')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-3')" prop="deptType">
            <el-select
              v-model="form.deptType"
              :placeholder="$t('system.dept.780956-12')"
              clearable
              :disabled="form.deptId != null"
              style="width: 400px"
            >
              <el-option
                v-for="option in deptTypeList"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-4')" prop="leader">
            <el-input
              v-model="form.leader"
              :placeholder="$t('system.dept.780956-13')"
              maxlength="20"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-38')" prop="invitationCode">
            <el-input
              v-model="form.invitationCode"
              :placeholder="$t('system.dept.780956-39')"
              maxlength="20"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-2')">
            <el-radio-group v-model="form.status">
              <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-35')" prop="logoName">
            <el-input v-model="form.logoName" :placeholder="$t('system.dept.780956-36')" style="width: 400px" />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-37')" prop="deptLogo">
            <image-upload
              v-model="form.deptLogo"
              :limit="1"
              :fileSize="1"
              @input="getImagePath"
              :fileType="['jpg', 'jpeg', 'png', 'gif']"
            />
          </el-form-item>
          <div class="title-wrap">{{ $t('system.dept.780956-14') }}</div>
          <el-form-item prop="userName">
            <template #label>
              <span class="span-box">
                <span>{{ $t('system.dept.780956-15') }}</span>
                <el-tooltip :content="$t('system.dept.780956-16')" placement="top">
                  <el-icon><Warning /></el-icon>
                </el-tooltip>
              </span>
            </template>
            <el-input
              v-model="form.userName"
              :placeholder="$t('system.dept.780956-17')"
              :disabled="form.deptId != null"
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-18')" prop="password" v-if="form.deptId == null">
            <el-input
              v-model="form.password"
              :placeholder="$t('system.dept.780956-19')"
              type="password"
              show-password
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-20')" prop="confirmPassword" v-if="form.deptId == null">
            <el-input
              v-model="form.confirmPassword"
              :placeholder="$t('system.dept.780956-19')"
              type="password"
              show-password
              style="width: 400px"
            />
          </el-form-item>
          <el-form-item :label="$t('system.dept.780956-21')" prop="phone">
            <el-input
              v-model="form.phone"
              :placeholder="$t('system.dept.780956-22')"
              maxlength="11"
              :disabled="form.deptId != null"
              style="width: 400px"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
            <el-button @click="cancel">{{ $t('cancel') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, Sort, ArrowDown, Warning, QuestionFilled } from '@element-plus/icons-vue';
import Treeselect from 'vue3-treeselect';
import 'vue3-treeselect/dist/vue3-treeselect.css';
import {
  listDept,
  getDept,
  delDept,
  addDept,
  updateDept,
  listDeptExcludeChild,
  getDeptType,
  refreshCache,
} from '@/api/system/dept';
import { parseTime, handleTree } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';
import { useUserStore } from '@/stores/modules/user';
import { checkPermi } from '@/utils/permission';

const { t } = useI18n();
const router = useRouter();
const { proxy } = getCurrentInstance()!;
const userStore = useUserStore();
const { dict } = useDict('sys_normal_disable', 'department_type');

const loading = ref(true);
const showSearch = ref(true);
const deptList = ref<any[]>([]);
const deptTreeOptions = ref<any[]>([]);
const deptType = ref('');
const title = ref('');
const open = ref(false);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const deptTypeList = ref<any[]>([]);
const form = ref<any>({});
const isAdmin = ref(false);

const queryFormRef = ref();
const formRef = ref();

const queryParams = reactive({
  deptName: undefined as string | undefined,
  status: undefined as string | undefined,
});

const equalToPassword = (_rule: any, value: any, callback: any) => {
  if (form.value.password !== value) {
    callback(new Error(t('system.dept.780956-23')));
  } else {
    callback();
  }
};

const rules = reactive({
  parentId: [{ required: true, message: t('system.dept.780956-24'), trigger: 'blur' }],
  deptName: [{ required: true, message: t('system.dept.780956-25'), trigger: 'blur' }],
  deptType: [{ required: true, message: t('system.dept.780956-26'), trigger: 'blur' }],
  leader: [{ required: true, message: t('system.dept.780956-27'), trigger: 'blur' }],
  invitationCode: [{ required: true, message: t('system.dept.780956-40'), trigger: 'blur' }],
  userName: [{ required: true, message: t('system.dept.780956-28'), trigger: 'blur' }],
  password: [
    { required: true, message: t('system.dept.780956-29'), trigger: 'blur' },
    { min: 5, max: 20, message: t('user.index.098976-35'), trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, trigger: 'blur', message: t('system.dept.780956-31') },
    { required: true, validator: equalToPassword, trigger: 'blur' },
  ],
  phone: [
    { required: true, message: t('system.dept.780956-32'), trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: t('user.index.098976-39'), trigger: 'blur' },
  ],
});

onMounted(() => {
  getList();
  if (userStore.roles.includes('admin')) {
    isAdmin.value = true;
  }
});

function getList() {
  loading.value = true;
  listDept(queryParams).then((res: any) => {
    deptList.value = handleTree(res.data, 'deptId');
    loading.value = false;
  });
}

function normalizer(node: any) {
  if (node.children && !node.children.length) {
    delete node.children;
  }
  return { id: node.deptId, label: node.deptName, children: node.children };
}

function cancel() {
  open.value = false;
  reset();
}

function getImagePath(data: any) {
  form.value.imgUrl = data;
}

function reset() {
  form.value = {
    deptId: null,
    parentId: undefined,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    invitationCode: undefined,
    phone: undefined,
    email: undefined,
    status: 0,
    deptType: 0,
    showOwner: false,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleAdd(row: any) {
  reset();
  form.value.deptType = null;
  if (row != undefined) {
    form.value.parentId = row.deptId;
  }
  open.value = true;
  title.value = t('system.dept.780956-33');
  if (row.deptType == null) {
    row.deptType = '';
    deptType.value = row.deptType;
    loadDeptType();
  } else {
    deptType.value = row.deptType;
    loadDeptType();
  }
  listDept().then((res: any) => {
    deptTreeOptions.value = handleTree(res.data, 'deptId');
  });
}

function loadDeptType() {
  getDeptType(deptType.value, form.value.showOwner).then((res: any) => {
    deptTypeList.value = res.data.map((item: any) => ({
      value: item.deptType,
      label: item.deptTypeName,
    }));
  });
}

function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

function handleRefreshCache() {
  refreshCache().then((res: any) => {
    if (res.code == 200) {
      (proxy as any).$modal.msgSuccess(t('system.dept.780956-42'));
    } else {
      (proxy as any).$modal.msgError(res.msg);
    }
  });
}

function handleUpdate(row: any) {
  reset();
  getDept(row.deptId).then((res: any) => {
    form.value = res.data;
    open.value = true;
    title.value = t('system.dept.780956-34');
    form.value.showOwner = true;
    if (row.deptType == null) {
      row.deptType = '';
      deptType.value = row.deptType;
      loadDeptType();
    } else {
      deptType.value = row.deptType;
      loadDeptType();
    }
    listDeptExcludeChild(row.deptId).then((r: any) => {
      deptTreeOptions.value = handleTree(r.data, 'deptId');
      if (deptTreeOptions.value.length == 0) {
        deptTreeOptions.value.push({ deptId: form.value.parentId, deptName: form.value.parentName, children: [] });
      }
    });
  });
}

function handleCommand(command: string, row: any) {
  switch (command) {
    case 'handleUserManage':
      handleUserManage(row);
      break;
    case 'handleRoleManage':
      handleRoleManage(row);
      break;
    case 'handleDelete':
      handleDelete(row);
      break;
  }
}

function handleUserManage(row: any) {
  router.push('/system/userManage/user/' + row.deptId);
}

function handleRoleManage(row: any) {
  router.push('/system/roleManage/role/' + row.deptId);
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.deptId != null) {
        updateDept(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addDept(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  (proxy as any).$modal
    .confirm(t('system.menu.034890-43', [row.deptName]))
    .then(() => delDept(row.deptId))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}
</script>

<style lang="scss" scoped>
.system-dept {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }
  .search-form {
    margin-bottom: -22.5px;
  }
}

.title-wrap {
  position: relative;
  padding-left: 10px;
  font-size: 16px;
  color: #303133;
  font-weight: 700;
  margin-bottom: 25px;

  &:before {
    content: '';
    background-color: #3796ec;
    width: 3px;
    height: 18px;
    position: absolute;
    left: 0;
    top: 50%;
    margin-top: -9px;
    border-radius: 3px;
  }
}
</style>
