<template>
  <div class="iot-platform">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="85px"
        class="search-form"
      >
        <el-form-item prop="platform">
          <el-select
            v-model="queryParams.platform"
            clearable
            :placeholder="$t('system.platform.675309-1')"
            style="width: 192px"
          >
            <el-option v-for="dict in iot_social_platform" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            clearable
            :placeholder="$t('system.platform.675309-2')"
            style="width: 192px"
          >
            <el-option
              v-for="dict in iot_social_platform_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['iot:platform:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:platform:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['iot:platform:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['iot:platform:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="platformList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column align="left" :label="$t('system.platform.675309-3')" prop="platform" min-width="200">
          <template #default="scope">
            <dict-tag :options="iot_social_platform" :value="scope.row.platform" />
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('status')" prop="status" width="80">
          <template #default="scope">
            <dict-tag :options="iot_social_platform_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.platform.675309-4')" align="center" prop="clientId" min-width="180" />
        <el-table-column
          :label="$t('system.platform.675309-5')"
          align="left"
          prop="redirectUri"
          min-width="250"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="left"
          :label="$t('system.platform.675309-6')"
          prop="bindUri"
          :show-overflow-tooltip="true"
          min-width="250"
        >
          <template #header>
            <span>{{ $t('system.platform.675309-6') }}</span>
            <el-tooltip :content="$t('system.platform.675309-24')" placement="top">
              <el-icon style="color: #486ff2; margin-left: 5px"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          align="left"
          :label="$t('system.platform.675309-7')"
          prop="redirectLoginUri"
          :show-overflow-tooltip="true"
          min-width="250"
        >
          <template #header>
            <span>{{ $t('system.platform.675309-7') }}</span>
            <el-tooltip :content="$t('system.platform.675309-25')" placement="top">
              <el-icon style="color: #486ff2; margin-left: 5px"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          align="left"
          :label="$t('system.platform.675309-8')"
          prop="errorMsgUri"
          :show-overflow-tooltip="true"
          min-width="250"
        >
          <template #header>
            <span>{{ $t('system.platform.675309-8') }}</span>
            <el-tooltip :content="$t('system.platform.675309-26')" placement="top">
              <el-icon style="color: #486ff2; margin-left: 5px"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('creatTime')" prop="createTime" width="100">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="150">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:platform:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:platform:remove']"
            >
              {{ $t('del') }}
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

    <!-- 添加或修改第三方登录平台控制对话框 -->
    <el-dialog :title="title" v-model="open" width="630px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-form-item :label="$t('system.platform.675309-9')" prop="platform">
          <el-select
            v-model="form.platform"
            :placeholder="$t('system.platform.675309-10')"
            :validate-event="false"
            style="width: 400px"
          >
            <el-option
              v-for="dict in iot_social_platform"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-11')" prop="status">
          <el-select
            v-model="form.status"
            :placeholder="$t('system.platform.675309-2')"
            :validate-event="false"
            style="width: 400px"
          >
            <el-option
              v-for="dict in iot_social_platform_status"
              :key="dict.value"
              :label="dict.label"
              :value="Number(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-12')" prop="clientId">
          <el-input v-model="form.clientId" :placeholder="$t('system.platform.675309-13')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-14')" prop="secretKey">
          <el-input v-model="form.secretKey" :placeholder="$t('system.platform.675309-15')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-16')" prop="redirectUri">
          <el-input v-model="form.redirectUri" :placeholder="$t('system.platform.675309-17')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-18')" prop="bindUri">
          <el-input v-model="form.bindUri" :placeholder="$t('system.platform.675309-19')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-20')" prop="redirectLoginUri">
          <el-input
            v-model="form.redirectLoginUri"
            :placeholder="$t('system.platform.675309-21')"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('system.platform.675309-22')" prop="errorMsgUri">
          <el-input v-model="form.errorMsgUri" :placeholder="$t('system.platform.675309-23')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('remark')" prop="remark">
          <el-input
            v-model="form.remark"
            :placeholder="$t('plzInput')"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Edit, Delete, Download, QuestionFilled } from '@element-plus/icons-vue';
import { addPlatform, delPlatform, getPlatform, listPlatform, updatePlatform } from '@/api/iot/platform';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const { iot_social_platform, iot_social_platform_status } = useDict(
  'iot_social_platform',
  'iot_social_platform_status'
);

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const platformList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  platform: null as any,
  status: null as any,
});

const form = ref<any>({});

function requiredRule(message: string, trigger: 'blur' | 'change') {
  return {
    trigger,
    validator: (_rule: any, value: any, callback: (error?: Error) => void) => {
      if (value === null || value === undefined || value === '') {
        callback(new Error(message));
        return;
      }
      callback();
    },
  };
}

const rules = reactive({
  platform: [requiredRule(t('system.platform.675309-27'), 'change')],
  status: [requiredRule(t('system.platform.675309-28'), 'change')],
  clientId: [requiredRule(t('system.platform.675309-29'), 'blur')],
  secretKey: [requiredRule(t('system.platform.675309-30'), 'blur')],
  redirectUri: [requiredRule(t('system.platform.675309-31'), 'blur')],
  bindUri: [requiredRule(t('system.platform.675309-32'), 'blur')],
  redirectLoginUri: [requiredRule(t('system.platform.675309-33'), 'blur')],
  errorMsgUri: [requiredRule(t('system.platform.675309-34'), 'blur')],
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listPlatform(queryParams).then((response: any) => {
    platformList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    socialPlatformId: null,
    platform: null,
    status: null,
    clientId: null,
    secretKey: null,
    redirectUri: null,
    createBy: null,
    createTime: null,
    updateTime: null,
    updateBy: null,
    remark: null,
    bindUri: null,
    redirectLoginUri: null,
    errorMsgUri: null,
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

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.socialPlatformId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('system.platform.675309-35');
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

function handleUpdate(row?: any) {
  reset();
  const socialPlatformId = row?.socialPlatformId || ids.value;
  getPlatform(socialPlatformId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = proxy.$t('system.platform.675309-36');
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  });
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  if (form.value.socialPlatformId != null) {
    updatePlatform(form.value).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
      open.value = false;
      getList();
    });
  } else {
    addPlatform(form.value).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
      open.value = false;
      getList();
    });
  }
}

function handleDelete(row?: any) {
  const socialPlatformIds = row?.socialPlatformId || ids.value;
  proxy.$modal
    .confirm(proxy.$t('system.platform.675309-37', [socialPlatformIds]))
    .then(() => {
      return delPlatform(socialPlatformIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download('iot/platform/export', { ...queryParams }, `platform_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.socialPlatformId;
}
</script>

<style lang="scss" scoped>
.iot-platform {
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
