<template>
  <div class="system-user">
    <el-row :gutter="10">
      <!-- 机构数据 -->
      <el-col :span="6" :xs="24">
        <el-card style="margin-right: 10px">
          <el-input
            v-model="deptName"
            :placeholder="$t('user.index.098976-0')"
            clearable
            :prefix-icon="Search"
            style="margin-bottom: 20px; margin-right: 10px"
          />
          <div class="tree-wrap">
            <el-tree
              :data="deptOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="treeRef"
              node-key="id"
              default-expand-all
              highlight-current
              style="overflow-x: auto; overflow-y: auto; width: 500px; font-size: 14px"
              @node-click="handleNodeClick"
            >
              <template #default="{ node }">
                <el-tooltip :content="node.label" placement="top">
                  <span>{{ node.label }}</span>
                </el-tooltip>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 用户数据 -->
      <el-col :span="18" :xs="24">
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
                :placeholder="$t('user.index.098976-4')"
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
                <el-option
                  v-for="item in dict.type.sys_normal_disable"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                  style="width: 150px"
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
            <el-col :span="1.5">
              <el-button plain :icon="Upload" @click="handleImport" v-hasPermi="['system:user:import']">
                {{ $t('import') }}
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['system:user:export']">
                {{ $t('export') }}
              </el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns" />
          </el-row>

          <div class="table-wrap">
            <el-table
              v-loading="loading"
              :data="userList"
              :border="false"
              @selection-change="handleSelectionChange"
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
                show-overflow-tooltip
                min-width="100"
              />
              <el-table-column
                :label="$t('user.index.098976-10')"
                align="left"
                key="userName"
                prop="userName"
                v-if="columns[1].visible"
                show-overflow-tooltip
                min-width="200"
              />
              <el-table-column
                :label="$t('user.index.098976-11')"
                align="left"
                key="nickName"
                prop="nickName"
                v-if="columns[2].visible"
                show-overflow-tooltip
                min-width="200"
              />
              <el-table-column
                :label="$t('user.index.098976-12')"
                align="left"
                key="deptName"
                prop="dept.deptName"
                v-if="columns[3].visible"
                show-overflow-tooltip
                min-width="200"
              />
              <el-table-column
                :label="$t('user.index.098976-13')"
                align="center"
                key="phonenumber"
                prop="phonenumber"
                v-if="columns[4].visible"
                width="120"
              />
              <el-table-column :label="$t('status')" align="center" key="status" v-if="columns[5].visible" width="100">
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
                width="160"
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
                width="250"
              >
                <template #default="scope">
                  <template v-if="scope.row.userId !== 1">
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
                      :disabled="scope.row.manager === true"
                    >
                      {{ $t('del') }}
                    </el-button>
                    <el-button
                      size="small"
                      link
                      :icon="Key"
                      v-hasPermi="['system:user:resetPwd']"
                      @click="handleResetPwd(scope.row)"
                    >
                      {{ $t('user.index.098976-15') }}
                    </el-button>
                  </template>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <pagination
            style="margin-bottom: 20px"
            v-show="total > 0"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>
    </el-row>

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
        <el-form-item :label="$t('user.index.098976-12')" prop="deptId">
          <treeselect
            style="width: 400px"
            v-model="form.deptId"
            :options="deptOptions"
            :show-count="true"
            :placeholder="$t('user.index.098976-17')"
            :disabled="(isEdit === true && form.userId != null) || (idEditDept == null && form.userId != null)"
            @select="(node) => getRoleList(node)"
          />
        </el-form-item>
        <el-form-item :label="$t('user.index.098976-3')" prop="phonenumber">
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
        <el-form-item v-if="form.userId == null" :label="$t('user.index.098976-10')" prop="userName">
          <el-input
            v-model="form.userName"
            :placeholder="$t('user.index.098976-2')"
            maxlength="30"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item v-if="form.userId == null" :label="$t('user.index.098976-21')" prop="password">
          <el-input
            v-model="form.password"
            :placeholder="$t('user.index.098976-22')"
            type="password"
            maxlength="20"
            show-password
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('user.index.098976-5')" prop="status">
          <el-radio-group v-model="form.status" :disabled="isEdit === true && form.userId != null">
            <el-radio v-for="item in dict.type.sys_normal_disable" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('user.index.098976-23')" prop="roleIds">
          <el-select
            v-model="form.roleIds"
            multiple
            :placeholder="$t('user.index.098976-24')"
            :disabled="isEdit === true && form.userId != null"
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
          <el-input
            v-model="form.remark"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
            :placeholder="$t('plzInput')"
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

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="460px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          {{ $t('dragFileTips') }}
          <em>{{ $t('clickFileTips') }}</em>
        </div>
        <template #tip>
          <div style="margin-top: 10px">
            <el-checkbox v-model="upload.updateSupport" />
            {{ $t('user.index.098976-25') }}
          </div>
          <div style="margin-top: 10px">
            <span>{{ $t('user.index.098976-26') }}</span>
            <el-link
              type="primary"
              underline="never"
              style="font-size: 12px; vertical-align: baseline"
              @click="importTemplate"
            >
              {{ $t('user.index.098976-27') }}
            </el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">{{ $t('confirm') }}</el-button>
          <el-button @click="upload.open = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Upload, Download, Edit, Key } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import Treeselect from 'vue3-treeselect';
import 'vue3-treeselect/dist/vue3-treeselect.css';
import {
  listUser,
  getUser,
  delUser,
  addUser,
  updateUser,
  resetUserPwd,
  changeUserStatus,
  deptsTreeSelect,
  getRole,
} from '@/api/system/user';
import { getToken } from '@/utils/auth';
import { parseTime, addDateRange } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';

const { t } = useI18n();
const route = useRoute();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_normal_disable');

const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const userList = ref<any[]>([]);
const title = ref('');
const deptOptions = ref<any[]>([]);
const open = ref(false);
const deptName = ref('');
const isEdit = ref(true);
const idEditDept = ref<any>(null);
const dateRange = ref<string[]>([]);
const roleOptions = ref<any[]>([]);
const form = ref<any>({});

const treeRef = ref();
const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();
const uploadRef = ref();

const defaultProps = { children: 'children', label: 'label' };

const upload = reactive({
  open: false,
  title: '',
  isUploading: false,
  updateSupport: 0,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/system/user/importData',
});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined as string | undefined,
  phonenumber: undefined as string | undefined,
  status: undefined as string | undefined,
  deptId: undefined as number | undefined,
});

const columns = reactive([
  { key: 0, label: t('user.index.098976-30'), visible: true },
  { key: 1, label: t('user.index.098976-10'), visible: true },
  { key: 2, label: t('user.index.098976-11'), visible: true },
  { key: 3, label: t('user.index.098976-29'), visible: true },
  { key: 4, label: t('user.index.098976-3'), visible: true },
  { key: 5, label: t('user.index.098976-5'), visible: true },
  { key: 6, label: t('creatTime'), visible: true },
]);

const rules = reactive({
  userName: [
    { required: true, message: t('user.index.098976-31'), trigger: 'blur' },
    { min: 2, max: 20, message: t('user.index.098976-32'), trigger: 'blur' },
  ],
  nickName: [{ required: true, message: t('user.index.098976-33'), trigger: 'blur' }],
  password: [
    { required: true, message: t('user.index.098976-34'), trigger: 'blur' },
    { min: 5, max: 20, message: t('user.index.098976-35'), trigger: 'blur' },
  ],
  roleIds: [{ required: true, message: t('user.index.098976-36'), trigger: 'change' }],
  status: [{ required: true }],
  email: [{ type: 'email', message: t('user.index.098976-37'), trigger: ['blur', 'change'] }],
  phonenumber: [
    { required: true, message: t('user.index.098976-38'), trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: t('user.index.098976-39'), trigger: 'blur' },
  ],
});

watch(deptName, (val) => {
  treeRef.value?.filter(val);
});

onMounted(() => {
  const deptId = route.params?.deptId as string;
  if (deptId) {
    queryParams.deptId = Number(deptId);
  }
  getList();
  getDeptTree();
});

/** 查询用户列表 */
function getList() {
  loading.value = true;
  form.value.deptId = queryParams.deptId;
  listUser(addDateRange(queryParams, dateRange.value)).then((res: any) => {
    if (res.code === 200) {
      userList.value = res.rows;
      total.value = res.total;
    }
    loading.value = false;
  });
}

/** 查询机构下拉树结构 */
function getDeptTree() {
  deptsTreeSelect().then((res: any) => {
    if (res.code === 200) {
      deptOptions.value = res.data;
    }
  });
}

/** 筛选节点 */
function filterNode(value: string, data: any) {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
}

/** 节点单击事件 */
function handleNodeClick(data: any) {
  queryParams.deptId = data.id;
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 用户状态修改 */
function handleStatusChange(row: any) {
  const text = row.status === '0' ? t('simulate.index.111543-54') : t('simulate.index.111543-55');
  (proxy as any).$modal
    .confirm(t('user.index.098976-40') + text + '""' + row.userName + t('user.index.098976-41'))
    .then(() => changeUserStatus(row.userId, row.status))
    .then(() => (proxy as any).$modal.msgSuccess(text + t('success')))
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    userId: null,
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

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  queryParams.deptId = undefined;
  treeRef.value?.setCurrentKey(null);
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('user.index.098976-42');
  form.value.deptId = queryParams.deptId;
  if (form.value.deptId != undefined) {
    getRole(form.value.deptId).then((res: any) => {
      if (res.code === 200) {
        roleOptions.value = res.roles.filter((item: any) => item.roleId !== 1);
      }
    });
  }
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  reset();
  const userId = row.userId || ids.value;
  getUser(userId).then((res: any) => {
    if (res.code === 200) {
      isEdit.value = row.manager;
      idEditDept.value = row.deptId;
      form.value = res.data;
      roleOptions.value = res.roles.filter((item: any) => item.roleId !== 1);
      form.value.postIds = res.postIds;
      form.value.roleIds = res.roleIds;
      open.value = true;
      title.value = t('user.index.098976-43');
      form.value.password = '';
    }
  });
}

/** 根据机构查询角色列表 */
function getRoleList(node: any) {
  form.value.deptId = node.id;
  if (form.value.deptId != undefined && form.value.deptId != null) {
    if (isEdit.value === false && form.value.userId != 0) {
      form.value.roleIds = [];
    }
    getRole(form.value.deptId).then((res: any) => {
      if (res.code === 200) {
        roleOptions.value = res.roles.filter((item: any) => item.roleId !== 1);
      }
    });
  }
}

/** 重置密码按钮操作 */
function handleResetPwd(row: any) {
  ElMessageBox.prompt(t('user.index.098976-44') + row.userName + t('user.index.098976-45'), t('user.index.098976-46'), {
    confirmButtonText: t('confirm'),
    cancelButtonText: t('cancel'),
    closeOnClickModal: false,
    inputPattern: /^.{5,20}$/,
    inputErrorMessage: t('user.index.098976-35'),
  })
    .then((result: any) => {
      resetUserPwd(row.userId, result.value).then(() => {
        (proxy as any).$modal.msgSuccess(t('user.index.098976-47') + result.value);
      });
    })
    .catch(() => {});
}

/** 提交按钮 */
function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addUser(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: any) {
  const userIds = row?.userId || ids.value;
  (proxy as any).$modal
    .confirm(t('user.index.098976-48', [userIds]))
    .then(() => delUser(userIds))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  (proxy as any).download('system/user/export', { ...queryParams }, `user_${new Date().getTime()}.xlsx`);
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = t('user.index.098976-49');
  upload.open = true;
}

/** 下载模板操作 */
function importTemplate() {
  (proxy as any).download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`);
}

/** 文件上传中处理 */
function handleFileUploadProgress() {
  upload.isUploading = true;
}

/** 文件上传成功处理 */
function handleFileSuccess(response: any) {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value?.clearFiles();
  ElMessageBox.alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + '</div>',
    t('user.index.098976-49'),
    { dangerouslyUseHTMLString: true }
  );
  getList();
}

/** 提交上传文件 */
function submitFileForm() {
  uploadRef.value?.submit();
}

function getRowKeys(row: any) {
  return row.userId;
}
</script>

<style lang="scss" scoped>
.system-user {
  padding: 20px;

  .tree-wrap {
    height: 700px;
    overflow-x: auto;
    overflow-y: auto;
  }

  .table-wrap {
    height: 550px;
    overflow-x: hidden;
    overflow-y: auto;
  }
}

.search-card {
  margin-bottom: 15px;
  height: 10%;
  width: 100%;
}

.search-form {
  margin-bottom: -18px;
  height: 10%;
}
</style>
