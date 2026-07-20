<template>
  <div class="system-config">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item prop="configName">
            <el-input
              v-model="queryParams.configName"
              :placeholder="$t('system.config.898564-1')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="configKey">
            <el-input
              v-model="queryParams.configKey"
              :placeholder="$t('system.config.898564-3')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="configType">
            <el-select
              v-model="queryParams.configType"
              :placeholder="$t('system.config.898564-4')"
              clearable
              style="width: 192px"
            >
              <el-option
                v-for="item in dict.type.sys_yes_no"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <template v-if="searchShow">
            <el-form-item>
              <el-date-picker
                v-model="dateRange"
                style="width: 240px"
                value-format="YYYY-MM-DD"
                type="daterange"
                range-separator="-"
                :start-placeholder="$t('system.dict.index.880996-3')"
                :end-placeholder="$t('system.dict.index.880996-4')"
              />
            </el-form-item>
          </template>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:config:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:config:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['system:config:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['system:config:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="configList" :border="false" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.config.898564-5')" align="center" prop="configId" width="80" />
        <el-table-column
          :label="$t('system.config.898564-0')"
          align="left"
          prop="configName"
          show-overflow-tooltip
          min-width="210"
        />
        <el-table-column
          :label="$t('system.config.898564-2')"
          align="left"
          prop="configKey"
          show-overflow-tooltip
          min-width="210"
        />
        <el-table-column
          :label="$t('system.config.898564-6')"
          align="left"
          prop="configValue"
          show-overflow-tooltip
          min-width="180"
        />
        <el-table-column :label="$t('system.config.898564-4')" align="center" prop="configType" width="120">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.configType" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('remark')" align="left" prop="remark" show-overflow-tooltip min-width="250" />
        <el-table-column :label="$t('creatTime')" align="left" prop="createTime" min-width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="150"
        >
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:config:edit']"
            >
              {{ $t('update') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:config:remove']"
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

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('system.config.898564-0')" prop="configName">
          <el-input v-model="form.configName" :placeholder="$t('system.config.898564-1')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.config.898564-2')" prop="configKey">
          <el-input v-model="form.configKey" :placeholder="$t('system.config.898564-3')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.config.898564-6')" prop="configValue">
          <el-input v-model="form.configValue" :placeholder="$t('system.config.898564-7')" style="width: 400px" />
        </el-form-item>
        <el-form-item :label="$t('system.config.898564-4')" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio v-for="item in dict.type.sys_yes_no" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('system.notice.670989-14')" prop="isEncryption">
          <el-radio-group v-model="encryptionDisplay">
            <el-radio v-for="item in dict.type.sys_yes_no" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('remark')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :placeholder="$t('plzInput')"
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
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Plus, Delete, Edit, Download, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { listConfig, getConfig, delConfig, addConfig, updateConfig, refreshCache } from '@/api/system/config';
import { parseTime, addDateRange } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict/useDict';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_yes_no');

const loading = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const configList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const dateRange = ref<string[]>([]);
const form = ref<any>({ isEncryption: false });

const queryFormRef = ref();
const formRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: undefined as string | undefined,
  configKey: undefined as string | undefined,
  configType: undefined as string | undefined,
});

const rules = reactive({
  configName: [{ required: true, message: t('system.config.898564-8'), trigger: 'blur' }],
  configKey: [{ required: true, message: t('system.config.898564-9'), trigger: 'blur' }],
  configValue: [{ required: true, message: t('system.config.898564-10'), trigger: 'blur' }],
});

const encryptionDisplay = computed({
  get() {
    const v = form.value.isEncryption;
    if (v === true || v === 1 || v === '1' || v === 'Y') return 'Y';
    if (v === false || v === 0 || v === '0' || v === 'N') return 'N';
    return v;
  },
  set(value: string) {
    form.value.isEncryption = value === 'Y' ? true : value === 'N' ? false : value;
  },
});

onMounted(() => {
  getList();
});

function getList() {
  loading.value = true;
  listConfig(addDateRange(queryParams, dateRange.value)).then((res: any) => {
    configList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    configId: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: 'Y',
    isEncryption: false,
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

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.config.898564-11');
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.configId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleUpdate(row?: any) {
  reset();
  const configId = row?.configId || ids.value;
  getConfig(configId).then((res: any) => {
    form.value = res.data;
    form.value.isEncryption = [1, '1', true, 'Y'].includes(form.value.isEncryption);
    open.value = true;
    title.value = t('system.config.898564-12');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      const submitData = { ...form.value };
      submitData.isEncryption = [true, 'Y', 1, '1'].includes(submitData.isEncryption) ? 1 : 0;
      if (form.value.configId != undefined) {
        updateConfig(submitData).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addConfig(submitData).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const configIds = row?.configId || ids.value;
  (proxy as any).$modal
    .confirm(t('system.config.898564-13', [configIds]))
    .then(() => delConfig(configIds))
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('system/config/export', { ...queryParams }, `config_${new Date().getTime()}.xlsx`);
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    (proxy as any).$modal.msgSuccess(t('system.dict.index.880996-12'));
  });
}

function searchChange() {
  searchShow.value = !searchShow.value;
}
</script>

<style lang="scss" scoped>
.system-config {
  padding: 20px;
  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
    .form-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: -22.5px;
      .search-btn-group {
        display: flex;
        flex-direction: row;
        margin-bottom: 22px;
      }
    }
  }
}
</style>
