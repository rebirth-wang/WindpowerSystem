<template>
  <div class="system-role">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="78px"
        class="search-form"
      >
        <el-form-item prop="deptId">
          <el-tree-select
            v-model="queryParams.deptId"
            :data="deptOptions"
            :props="{ label: 'label', children: 'children', disabled: 'disabled' }"
            node-key="id"
            :value-key="'id'"
            :placeholder="$t('user.index.098976-17')"
            clearable
            filterable
            check-strictly
            :render-after-expand="false"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="roleName">
          <el-input
            v-model="queryParams.roleName"
            :placeholder="$t('role.index.094567-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status" label-width="55px">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('role.index.094567-2')"
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
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:role:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:role:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="roleList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column
          :label="$t('role.index.094567-40')"
          prop="roleId"
          show-overflow-tooltip
          min-width="80"
          align="center"
        />
        <el-table-column
          :label="$t('role.index.094567-0')"
          prop="roleName"
          show-overflow-tooltip
          min-width="200"
          align="left"
        />
        <el-table-column
          :label="$t('role.index.094567-6')"
          prop="roleKey"
          show-overflow-tooltip
          width="160"
          align="left"
        />
        <el-table-column :label="$t('role.index.094567-7')" prop="deptName" align="left" min-width="200" />
        <el-table-column :label="$t('status')" align="center" width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="$t('role.index.094567-8')" prop="roleSort" width="80" align="center" />
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
          width="210"
        >
          <template #default="scope">
            <template v-if="scope.row.roleId !== 1">
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:role:add']"
                :disabled="!scope.row.canEditRole"
              >
                {{ $t('look') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:role:remove']"
                :disabled="!scope.row.canEditRole || scope.row.manager"
              >
                {{ $t('del') }}
              </el-button>
              <el-dropdown
                size="small"
                @command="(command) => handleCommand(command, scope.row)"
                v-hasPermi="['system:role:edit']"
                :disabled="!scope.row.canEditRole"
                style="vertical-align: middle; margin-left: 8px"
              >
                <el-button size="small" link>
                  {{ $t('role.index.094567-11') }}
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="checkPermi(['system:role:edit'])" command="handleDataScope">
                      {{ $t('role.index.094567-23') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="checkPermi(['system:role:edit'])" command="handleAuthUser">
                      {{ $t('role.index.094567-12') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
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

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" v-model="open" width="630px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-form-item :label="$t('user.index.098976-12')" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptOptions"
            :props="{ label: 'label', children: 'children', disabled: 'disabled' }"
            node-key="id"
            :value-key="'id'"
            :placeholder="$t('user.index.098976-17')"
            :disabled="form.roleId != 0"
            check-strictly
            :render-after-expand="false"
            filterable
            clearable
            style="width: 400px"
            @change="getMenuList"
          />
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-0')" prop="roleName">
          <el-input
            v-model="form.roleName"
            :placeholder="$t('role.index.094567-1')"
            :disabled="isEdit === true && form.roleId != 0"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item prop="roleKey">
          <template #label>
            <span>
              <el-tooltip :content="$t('role.index.094567-14')" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
            {{ $t('role.index.094567-6') }}
          </template>
          <el-input
            v-model="form.roleKey"
            :placeholder="$t('role.index.094567-15')"
            :disabled="isEdit === true && form.roleId != 0"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-16')" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('status')">
          <el-radio-group v-model="form.status" :disabled="isEdit === true && form.roleId != 0">
            <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-22')" v-if="form.roleId !== 0">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange" style="width: 400px">
            <el-option
              v-for="item in dict.type.data_scope"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              :disabled="item.value == '5' && form.roleKey == 'manager'"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-22')" v-if="form.roleId === 0">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange" style="width: 400px">
            <el-option
              v-for="item in filteredDataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              :disabled="item.disabled"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('remark')">
          <el-input
            v-model="form.remark"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
            :placeholder="$t('plzInput')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-17')">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">
            {{ $t('role.index.094567-18') }}
          </el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">
            {{ $t('role.index.094567-19') }}
          </el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">
            {{ $t('role.index.094567-20') }}
          </el-checkbox>
          <el-tree
            style="width: 400px"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            ref="menuRef"
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            :empty-text="$t('role.index.094567-21')"
            :props="defaultProps"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['system:role:edit']">
            {{ $t('confirm') }}
          </el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色数据权限对话框 -->
    <el-dialog :title="title" v-model="openDataScope" width="600px" append-to-body>
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('role.index.094567-0')">
          <el-input v-model="form.roleName" :disabled="true" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-6')">
          <el-input v-model="form.roleKey" :disabled="true" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-22')">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange" style="width: 400px">
            <el-option v-for="item in dict.type.data_scope" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('role.index.094567-23')" v-show="form.dataScope == '2'">
          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')">
            {{ $t('role.index.094567-18') }}
          </el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">
            {{ $t('role.index.094567-19') }}
          </el-checkbox>
          <el-checkbox v-model="form.deptCheckStrictly" @change="handleCheckedTreeConnect($event, 'dept')">
            {{ $t('role.index.094567-20') }}
          </el-checkbox>
          <el-tree
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            default-expand-all
            ref="deptRef"
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            :empty-text="$t('role.index.094567-21')"
            :props="defaultProps"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataScope">{{ $t('confirm') }}</el-button>
          <el-button @click="cancelDataScope">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, ArrowDown, QuestionFilled } from '@element-plus/icons-vue';
import {
  listRole,
  getRole,
  delRole,
  addRole,
  updateRole,
  dataScope,
  changeRoleStatus,
  deptTreeSelect,
} from '@/api/system/role';
import { deptsTreeSelect } from '@/api/system/user';
import { treeselect as menuTreeselect, roleMenuTreeselect, partMenuTreeselect } from '@/api/system/menu';
import { parseTime, addDateRange } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';
import { useUserStore } from '@/stores/modules/user';
import { checkPermi } from '@/utils/permission';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance()!;
const userStore = useUserStore();
const { dict } = useDict('sys_normal_disable', 'data_scope');

const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const roleList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const openDataScope = ref(false);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const deptExpand = ref(true);
const deptNodeAll = ref(false);
const isEdit = ref(true);
const dateRange = ref<string[]>([]);
const menuOptions = ref<any[]>([]);
const deptOptions = ref<any[]>([]);
const form = ref<any>({});

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();
const menuRef = ref();
const deptRef = ref();

const defaultProps = { children: 'children', label: 'label' };

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined as string | undefined,
  roleKey: undefined as string | undefined,
  status: undefined as string | undefined,
  deptId: null as number | null,
});

const rules = reactive({
  deptId: [{ required: true, message: t('role.index.094567-29'), trigger: 'blur' }],
  roleName: [{ required: true, message: t('role.index.094567-30'), trigger: 'blur' }],
  roleKey: [{ required: true, message: t('role.index.094567-31'), trigger: 'blur' }],
  roleSort: [{ required: true, message: t('role.index.094567-32'), trigger: 'blur' }],
  dataScope: [{ required: true, message: t('role.index.094567-39'), trigger: 'blur' }],
});

const filteredDataScopeOptions = computed(() => {
  const sourceData = dict.type.data_scope || [];
  const ds = userStore.dataScope;
  if (ds === '3') {
    return sourceData.map((item: any) => ({ ...item, disabled: item.value === '4' }));
  } else if (ds === '4') {
    return sourceData.map((item: any) => ({ ...item, disabled: false }));
  } else if (ds === '5') {
    return sourceData.map((item: any) => ({ ...item, disabled: item.value === '3' || item.value === '4' }));
  }
  return sourceData;
});

onMounted(() => {
  const deptId = route.params?.deptId as string;
  if (deptId) {
    queryParams.deptId = Number(deptId);
  }
  getList();
  getDeptTrees();
});

function getList() {
  loading.value = true;
  listRole(addDateRange(queryParams, dateRange.value)).then((res: any) => {
    roleList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getDeptTrees() {
  deptsTreeSelect().then((res: any) => {
    deptOptions.value = res.data;
  });
}

function getMenuTreeselect() {
  menuTreeselect().then((res: any) => {
    menuOptions.value = res.data;
  });
}

function getMenuList() {
  if (form.value.deptId != undefined && form.value.deptId != null && form.value.roleId == 0) {
    partMenuTreeselect(form.value.deptId).then((res: any) => {
      menuOptions.value = res.data;
    });
  }
}

function getMenuAllCheckedKeys() {
  let checkedKeys = menuRef.value?.getCheckedKeys() || [];
  let halfCheckedKeys = menuRef.value?.getHalfCheckedKeys() || [];
  checkedKeys.unshift(...halfCheckedKeys);
  return checkedKeys;
}

function getDeptAllCheckedKeys() {
  let checkedKeys = deptRef.value?.getCheckedKeys() || [];
  let halfCheckedKeys = deptRef.value?.getHalfCheckedKeys() || [];
  checkedKeys.unshift(...halfCheckedKeys);
  return checkedKeys;
}

function getRoleMenuTreeselect(roleId: number, deptId: number) {
  return roleMenuTreeselect(roleId, deptId).then((res: any) => {
    menuOptions.value = res.menus;
    return res;
  });
}

function getDeptTree(roleId: number) {
  return deptTreeSelect(roleId).then((res: any) => {
    deptOptions.value = res.depts;
    return res;
  });
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.roleId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleStatusChange(row: any) {
  const text = row.status === '0' ? t('simulate.index.111543-54') : t('simulate.index.111543-55');
  (proxy as any).$modal
    .confirm(t('user.index.098976-40') + text + '' + row.roleName + t('role.index.094567-37'))
    .then(() => changeRoleStatus(row.roleId, row.status))
    .then(() => (proxy as any).$modal.msgSuccess(text + t('success')))
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

function cancel() {
  open.value = false;
  reset();
}

function cancelDataScope() {
  openDataScope.value = false;
  reset();
}

function reset() {
  menuRef.value?.setCheckedKeys([]);
  menuExpand.value = false;
  menuNodeAll.value = false;
  deptExpand.value = true;
  deptNodeAll.value = false;
  form.value = {
    roleId: 0,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: 0,
    deptId: null,
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleCommand(command: string, row: any) {
  switch (command) {
    case 'handleDataScope':
      handleDataScope(row);
      break;
    case 'handleAuthUser':
      handleAuthUser(row);
      break;
  }
}

function handleCheckedTreeExpand(value: any, type: string) {
  if (type === 'menu') {
    const treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      if (menuRef.value?.store?.nodesMap[treeList[i].id]) {
        menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
      }
    }
  } else if (type === 'dept') {
    const treeList = deptOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      if (deptRef.value?.store?.nodesMap[treeList[i].id]) {
        deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
      }
    }
  }
}

function handleCheckedTreeNodeAll(value: any, type: string) {
  if (type === 'menu') {
    menuRef.value?.setCheckedNodes(value ? menuOptions.value : []);
  } else if (type === 'dept') {
    deptRef.value?.setCheckedNodes(value ? deptOptions.value : []);
  }
}

function handleCheckedTreeConnect(value: any, type: string) {
  if (type === 'menu') {
    form.value.menuCheckStrictly = !!value;
  } else if (type === 'dept') {
    form.value.deptCheckStrictly = !!value;
  }
}

function handleAdd() {
  reset();
  getMenuTreeselect();
  open.value = true;
  title.value = t('role.index.094567-33');
  isEdit.value = true;
  getMenuList();
}

function handleUpdate(row: any) {
  reset();
  const roleId = row.roleId || ids.value;
  const deptId = row.deptId;
  const roleMenu = getRoleMenuTreeselect(roleId, deptId);
  isEdit.value = row.manager;
  getRole(roleId).then((res: any) => {
    form.value = res.data;
    open.value = true;
    nextTick(() => {
      roleMenu.then((r: any) => {
        const checkedKeys = r.checkedKeys || [];
        checkedKeys.forEach((v: any) => {
          nextTick(() => {
            menuRef.value?.setChecked(v, true, false);
          });
        });
      });
    });
    title.value = t('role.index.094567-34');
  });
}

function dataScopeSelectChange(value: string) {
  if (value !== '2') {
    nextTick(() => {
      deptRef.value?.setCheckedKeys([]);
    });
  }
}

function handleDataScope(row: any) {
  reset();
  const deptTree = getDeptTree(row.roleId);
  getRole(row.roleId).then((res: any) => {
    form.value = res.data;
    openDataScope.value = true;
    nextTick(() => {
      deptTree.then((r: any) => {
        deptRef.value?.setCheckedKeys(r.checkedKeys);
      });
    });
    title.value = t('role.index.094567-35');
  });
}

function handleAuthUser(row: any) {
  router.push('/system/roleAuth/user/' + row.roleId);
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.roleId != 0) {
        form.value.menuIds = getMenuAllCheckedKeys();
        updateRole(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        form.value.menuIds = getMenuAllCheckedKeys();
        addRole(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function submitDataScope() {
  if (form.value.roleId != 0) {
    form.value.deptIds = getDeptAllCheckedKeys();
    dataScope(form.value).then(() => {
      (proxy as any).$modal.msgSuccess(t('updateSuccess'));
      openDataScope.value = false;
      getList();
    });
  }
}

function handleDelete(row?: any) {
  const roleIds = row?.roleId || ids.value;
  (proxy as any).$modal
    .confirm(t('role.index.094567-36', [roleIds]))
    .then(() => delRole(roleIds))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.roleId;
}

/** 导出按钮操作 */
function handleExport() {
  (proxy as any).download('system/role/export', { ...queryParams }, `role_${new Date().getTime()}.xlsx`);
}
</script>

<style lang="scss" scoped>
.system-role {
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
