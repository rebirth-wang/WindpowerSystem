<template>
  <div class="system-sys-client">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="85px"
        class="search-form"
      >
        <el-form-item prop="clientKey">
          <el-input
            v-model="queryParams.clientKey"
            :placeholder="$t('system.sysclient.652154-1')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('system.sysclient.652154-2') }}
          </el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('system.sysclient.652154-3') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:sysclient:add']">
            {{ $t('system.sysclient.652154-4') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['system:sysclient:edit']">
            {{ $t('system.sysclient.652154-5') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:sysclient:remove']"
          >
            {{ $t('system.sysclient.652154-6') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['system:sysclient:export']">
            {{ $t('system.sysclient.652154-7') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="sysclientList"
        @selection-change="handleSelectionChange"
        :border="false"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.sysclient.652154-8')" align="left" prop="clientKey" min-width="200" />
        <el-table-column :label="$t('system.sysclient.652154-9')" align="left" prop="clientSecret" min-width="200" />
        <el-table-column :label="$t('system.sysclient.652154-10')" align="left" prop="token" min-width="410" />
        <el-table-column :label="$t('system.sysclient.652154-13')" align="center" prop="timeout" width="120" />
        <el-table-column :label="$t('system.sysclient.652154-14')" align="center" prop="enable" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enable"
              active-value="1"
              inactive-value="0"
              @change="handleEnableChange(scope.row)"
              :disabled="isDisabled"
            />
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('system.sysclient.652154-15')"
          align="center"
          class-name="small-padding fixed-width"
          width="140"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:sysclient:edit']"
            >
              {{ $t('system.sysclient.652154-5') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:sysclient:remove']"
            >
              {{ $t('system.sysclient.652154-6') }}
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

    <!-- 添加或修改系统授权对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item :label="$t('system.sysclient.652154-16')" prop="clientKey">
          <el-input v-model="form.clientKey" :placeholder="$t('system.sysclient.652154-17')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.sysclient.652154-18')" prop="clientSecret">
          <el-input v-model="form.clientSecret" :placeholder="$t('system.sysclient.652154-19')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.sysclient.652154-34')">
          <el-switch v-model="neverExpires" :active-value="1" :inactive-value="0" @change="changeSwitch" />
        </el-form-item>
        <el-form-item :label="$t('system.sysclient.652154-20')" prop="timeout">
          <el-input
            v-model="form.timeout"
            :placeholder="$t('system.sysclient.652154-21')"
            type="number"
            style="width: 280px"
            :disabled="neverExpires == 1"
          >
            <template #append>{{ $t('system.sysclient.652154-21') }}</template>
          </el-input>
          <span style="margin-left: 10px">
            <el-tooltip effect="dark" :content="$t('system.sysclient.652154-22')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
        </el-form-item>
        <el-form-item :label="$t('system.sysclient.652154-23')" prop="enable">
          <el-switch v-model="form.enable" active-value="1" inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('system.sysclient.652154-24') }}</el-button>
          <el-button @click="cancel">{{ $t('system.sysclient.652154-25') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus, Edit, Delete, Download, QuestionFilled } from '@element-plus/icons-vue';
import { listSysclient, getSysclient, delSysclient, addSysclient, updateSysclient } from '@/api/system/sysclient';

const { proxy } = getCurrentInstance() as any;

const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const isDisabled = ref(false);
const total = ref(0);
const sysclientList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const neverExpires = ref(0);
const form = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  clientKey: null as any,
  clientSecret: null as any,
  token: null as any,
  grantType: null as any,
  deviceType: null as any,
  timeout: null as any,
  enable: null as any,
});

const rules = reactive<any>({
  clientKey: [{ required: true, message: proxy?.$t('system.sysclient.652154-17'), trigger: 'blur' }],
  clientSecret: [
    { required: true, message: proxy?.$t('system.sysclient.652154-19'), trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (/[a-zA-z]$/.test(value) == false) callback(new Error(proxy?.$t('system.sysclient.652154-33')));
        else callback();
      },
      trigger: 'blur',
    },
  ],
  timeout: [{ required: true, message: proxy?.$t('system.sysclient.652154-32'), trigger: 'blur' }],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listSysclient(queryParams).then((response: any) => {
    sysclientList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function changeSwitch(val: any) {
  neverExpires.value = val;
  form.value.timeout = val === 1 ? -1 : '';
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    clientKey: null,
    clientSecret: null,
    token: null,
    grantType: null,
    deviceType: null,
    timeout: null,
    enable: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
  };
  neverExpires.value = 0;
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

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy?.$t('system.sysclient.652154-26');
}

function handleUpdate(row: any) {
  reset();
  const id = row.id || ids.value;
  neverExpires.value = row.timeout === -1 ? 1 : 0;
  if (neverExpires.value === 1) form.value.timeout = -1;
  getSysclient(id).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy?.$t('system.sysclient.652154-27');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (neverExpires.value === 1) form.value.timeout = -1;
      if (form.value.id != null) {
        updateSysclient(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('system.sysclient.652154-28'));
          open.value = false;
          getList();
        });
      } else {
        addSysclient(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('system.sysclient.652154-29'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('system.sysclient.652154-31', [delIds]))
    .then(() => {
      return delSysclient(delIds);
    })
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('system.sysclient.652154-30'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

async function handleEnableChange(row: any) {
  isDisabled.value = true;
  setTimeout(() => {
    isDisabled.value = false;
  }, 1000);
  updateSysclient(row).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('system.sysclient.652154-28'));
    getList();
  });
}

function handleExport() {
  proxy?.download('system/sysclient/export', { ...queryParams }, `sysclient_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.id;
}
</script>

<style lang="scss" scoped>
.system-sys-client {
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
