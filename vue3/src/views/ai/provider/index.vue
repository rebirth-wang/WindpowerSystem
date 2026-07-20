<template>
  <div class="ai-provider-page">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item>
          <el-select
            v-model="queryParams.providerCode"
            :placeholder="t('ai.provider.index.748794-1')"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in dict.type.ai_provider_code"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="queryParams.providerName"
            :placeholder="t('ai.provider.index.748794-3')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.regionProfile"
            :placeholder="t('ai.provider.index.748794-5')"
            clearable
            style="width: 220px"
          >
            <el-option
              v-for="item in dict.type.ai_provider_region_profile"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="searchShow">
          <el-select
            v-model="queryParams.authType"
            :placeholder="t('ai.provider.index.748794-7')"
            clearable
            style="width: 220px"
          >
            <el-option
              v-for="item in dict.type.ai_provider_auth_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="searchShow">
          <el-select
            v-model="queryParams.status"
            :placeholder="t('ai.provider.index.748794-9')"
            clearable
            style="width: 220px"
          >
            <el-option
              v-for="item in dict.type.ai_provider_status"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery" v-hasPermi="['ai:provider:query']">
            {{ t('ai.provider.index.748794-10') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ t('ai.provider.index.748794-11') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowDown v-if="!searchShow" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar-row">
        <div class="toolbar-row__left">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['ai:provider:add']">
            {{ t('ai.provider.index.748794-12') }}
          </el-button>
        </div>
        <div class="toolbar-row__right">
          <div class="button-group">
            <el-tooltip effect="dark" :content="t('ai.provider.index.748794-13')" placement="top">
              <el-button
                size="small"
                :icon="Menu"
                @click="handleViewModeChange('card')"
                :class="['toggle-button card-button', { active: viewMode === 'card' }]"
              />
            </el-tooltip>
            <div class="separator"></div>
            <el-tooltip effect="dark" :content="t('ai.provider.index.748794-14')" placement="top">
              <el-button
                size="small"
                :icon="Grid"
                @click="handleViewModeChange('table')"
                :class="['toggle-button list-button', { active: viewMode === 'table' }]"
              />
            </el-tooltip>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </div>

      <el-table v-if="viewMode === 'table'" v-loading="loading" :data="providerList" :border="false">
        <el-table-column :label="t('ai.provider.index.748794-0')" min-width="180">
          <template #default="scope">
            <div class="provider-code-cell">
              <dict-tag :options="dict.type.ai_provider_code" :value="scope.row.providerCode" />
              <span class="provider-code-value">{{ scope.row.providerCode }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.provider.index.748794-2')" prop="providerName" min-width="160" />
        <el-table-column :label="t('ai.provider.index.748794-4')" width="120">
          <template #default="scope">
            <dict-tag :options="dict.type.ai_provider_region_profile" :value="scope.row.regionProfile" />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.provider.index.748794-6')" width="120">
          <template #default="scope">
            <dict-tag :options="dict.type.ai_provider_auth_type" :value="scope.row.authType" />
          </template>
        </el-table-column>
        <el-table-column
          :label="t('ai.provider.index.748794-15')"
          prop="apiBaseUrl"
          min-width="260"
          show-overflow-tooltip
        />
        <el-table-column :label="t('ai.provider.index.748794-16')" min-width="160">
          <template #default="scope">
            {{
              scope.row.hasApiKey
                ? scope.row.apiKeyPreview || t('ai.provider.index.748794-17')
                : scope.row.authType === 'NONE'
                  ? t('ai.provider.index.748794-18')
                  : t('ai.provider.index.748794-19')
            }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.provider.index.748794-8')" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="'0'"
              :inactive-value="'1'"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.provider.index.748794-20')" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              @click="handleViewModels(scope.row)"
              v-hasPermi="['ai:model:query']"
            >
              {{ t('ai.provider.index.748794-21') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['ai:provider:edit']"
            >
              {{ t('ai.provider.index.748794-22') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['ai:provider:remove']"
            >
              {{ t('ai.provider.index.748794-23') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-row v-else v-loading="loading" :gutter="16" class="provider-card-row">
        <el-col
          v-for="item in providerList"
          :key="item.providerId"
          style="margin-bottom: 15px"
          :xs="24"
          :sm="12"
          :lg="8"
          :xl="6"
        >
          <el-card shadow="hover" class="provider-card">
            <div class="provider-card__header">
              <div>
                <div class="provider-card__title">{{ item.providerName || '-' }}</div>
                <div class="provider-card__subtitle">
                  <dict-tag :options="dict.type.ai_provider_code" :value="item.providerCode" />
                  <span class="provider-card__code">{{ item.providerCode || '-' }}</span>
                </div>
              </div>
              <el-switch
                v-model="item.status"
                :active-value="'0'"
                :inactive-value="'1'"
                @change="handleStatusChange(item)"
              />
            </div>
            <div class="provider-card__meta">
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.provider.index.748794-4') }}</span>
                <dict-tag :options="dict.type.ai_provider_region_profile" :value="item.regionProfile" />
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.provider.index.748794-6') }}</span>
                <dict-tag :options="dict.type.ai_provider_auth_type" :value="item.authType" />
              </div>
              <div class="meta-item meta-item--full">
                <span class="meta-item__label">{{ t('ai.provider.index.748794-15') }}</span>
                <span class="meta-item__value">{{ item.apiBaseUrl || '-' }}</span>
              </div>
              <div class="meta-item meta-item--full">
                <span class="meta-item__label">{{ t('ai.provider.index.748794-16') }}</span>
                <span class="meta-item__value">
                  {{
                    item.hasApiKey
                      ? item.apiKeyPreview || t('ai.provider.index.748794-17')
                      : item.authType === 'NONE'
                        ? t('ai.provider.index.748794-18')
                        : t('ai.provider.index.748794-19')
                  }}
                </span>
              </div>
            </div>
            <div class="provider-card__footer card-action-bar">
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleViewModels(item)"
                v-hasPermi="['ai:model:query']"
              >
                {{ t('ai.provider.index.748794-21') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleUpdate(item)"
                v-hasPermi="['ai:provider:edit']"
              >
                {{ t('ai.provider.index.748794-22') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleDelete(item)"
                v-hasPermi="['ai:provider:remove']"
              >
                {{ t('ai.provider.index.748794-23') }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog v-model="open" :title="title" width="600px" append-to-body>
      <el-form ref="formRef" class="ai-dialog-form" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="t('ai.provider.index.748794-0')" prop="providerCode">
          <el-select
            v-model="form.providerCode"
            :placeholder="t('ai.provider.index.748794-1')"
            filterable
            style="width: 100%"
            @change="handleProviderCodeChange"
          >
            <el-option v-for="item in providerCodeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-2')" prop="providerName">
          <el-input v-model="form.providerName" :placeholder="t('ai.provider.index.748794-3')" />
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-4')" prop="regionProfile">
          <el-select v-model="form.regionProfile" :placeholder="t('ai.provider.index.748794-5')" style="width: 100%">
            <el-option
              v-for="item in dict.type.ai_provider_region_profile"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-15')" prop="apiBaseUrl">
          <el-input v-model="form.apiBaseUrl" :placeholder="t('ai.provider.index.748794-24')" />
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-6')" prop="authType">
          <el-select v-model="form.authType" :placeholder="t('ai.provider.index.748794-7')" style="width: 100%">
            <el-option
              v-for="item in dict.type.ai_provider_auth_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="API Key">
          <el-input
            v-model="form.apiKeyCipher"
            type="textarea"
            :rows="3"
            :placeholder="
              form.authType === 'NONE' ? t('ai.provider.index.748794-25') : t('ai.provider.index.748794-26')
            "
          />
          <div v-if="form.hasApiKey" class="form-tip">
            {{ t('ai.provider.index.748794-43', { key: form.apiKeyPreview || t('ai.provider.index.748794-27') }) }}
          </div>
          <div v-else-if="form.authType === 'NONE'" class="form-tip">{{ t('ai.provider.index.748794-28') }}</div>
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-29')" prop="sortNum">
          <el-input-number v-model="form.sortNum" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-8')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in dict.type.ai_provider_status" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-30')">
          <el-input
            v-model="form.extraConfig"
            type="textarea"
            :rows="3"
            :placeholder="t('ai.provider.index.748794-31')"
          />
        </el-form-item>
        <el-form-item :label="t('ai.provider.index.748794-32')">
          <el-input v-model="form.remark" type="textarea" :rows="3" :placeholder="t('ai.provider.index.748794-33')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm" v-if="form.providerId" v-hasPermi="['ai:provider:edit']">
          {{ t('ai.provider.index.748794-34') }}
        </el-button>
        <el-button type="primary" @click="submitForm" v-else v-hasPermi="['ai:provider:add']">
          {{ t('ai.provider.index.748794-34') }}
        </el-button>
        <el-button @click="open = false">{{ t('ai.provider.index.748794-35') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Edit, Grid, Menu, Plus, Refresh, Search, View, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { useDict } from '@/utils/dict';

const { t } = useI18n();
import {
  addAiProvider,
  changeAiProviderStatus,
  getAiProvider,
  listAiProvider,
  removeAiProvider,
  updateAiProvider,
} from '@/api/ai/provider';

const { dict } = useDict(
  'ai_provider_code',
  'ai_provider_region_profile',
  'ai_provider_auth_type',
  'ai_provider_status'
);
const PROVIDER_VIEW_MODE_KEY = 'fastbee.ai.provider.viewMode';

const loading = ref(false);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const providerList = ref([]);
const viewMode = ref('card');
const open = ref(false);
const title = ref('');
const formRef = ref();
const router = useRouter();

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  providerCode: '',
  providerName: '',
  regionProfile: '',
  authType: '',
  status: '',
});

const form = ref(createDefaultForm());
const rules = {
  providerCode: [{ required: true, message: t('ai.provider.index.748794-1'), trigger: 'change' }],
  providerName: [{ required: true, message: t('ai.provider.index.748794-3'), trigger: 'blur' }],
  regionProfile: [{ required: true, message: t('ai.provider.index.748794-5'), trigger: 'change' }],
  authType: [{ required: true, message: t('ai.provider.index.748794-7'), trigger: 'change' }],
};

const providerCodeOptions = computed(() => {
  const currentOptions = dict.type.ai_provider_code || [];
  const currentCode = form.value.providerCode;
  if (!currentCode || currentOptions.some((item) => item.value === currentCode)) {
    return currentOptions;
  }
  return [
    ...currentOptions,
    {
      label: currentCode,
      value: currentCode,
      remark: '',
    },
  ];
});

function createDefaultForm() {
  return {
    providerId: undefined,
    providerCode: '',
    providerName: '',
    regionProfile: 'CN',
    apiBaseUrl: '',
    authType: 'API_KEY',
    apiKeyCipher: '',
    apiKeyPreview: '',
    hasApiKey: false,
    extraConfig: '',
    sortNum: 0,
    status: '0',
    remark: '',
  };
}

function toNumberOrDefault(value, defaultValue = 0) {
  if (value === '' || value === null || value === undefined) return defaultValue;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? defaultValue : numberValue;
}

function normalizeFormNumbers(data) {
  return {
    ...data,
    sortNum: toNumberOrDefault(data?.sortNum, 0),
  };
}

function parseProviderPreset(option) {
  if (!option || !option.remark) {
    return {};
  }
  try {
    return JSON.parse(option.remark);
  } catch (error) {
    return {};
  }
}

function handleProviderCodeChange(value) {
  const option = providerCodeOptions.value.find((item) => item.value === value);
  if (!option) {
    return;
  }
  const preset = parseProviderPreset(option);
  form.value.providerName = option.label || form.value.providerName;
  form.value.regionProfile = preset.regionProfile || form.value.regionProfile || 'CN';
  form.value.authType = preset.authType || form.value.authType || 'API_KEY';
  if (Object.prototype.hasOwnProperty.call(preset, 'apiBaseUrl')) {
    form.value.apiBaseUrl = preset.apiBaseUrl || '';
  }
}

async function getList() {
  loading.value = true;
  try {
    const response = await listAiProvider(queryParams.value);
    providerList.value = response.rows || [];
    total.value = response.total || 0;
  } finally {
    loading.value = false;
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function handleReset() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    providerCode: '',
    providerName: '',
    regionProfile: '',
    authType: '',
    status: '',
  };
  getList();
}

function handleAdd() {
  form.value = createDefaultForm();
  open.value = true;
  title.value = t('ai.provider.index.748794-12');
}

function handleViewModeChange(mode) {
  viewMode.value = mode || 'card';
  window.localStorage.setItem(PROVIDER_VIEW_MODE_KEY, viewMode.value);
}

function handleViewModels(row) {
  router
    .push({
      path: '/ai/aiModel',
      query: {
        providerId: String(row.providerId || ''),
        providerName: row.providerName || '',
        t: Date.now().toString(),
      },
    })
    .catch(() => {});
}

async function handleUpdate(row) {
  const response = await getAiProvider(row.providerId);
  form.value = {
    ...createDefaultForm(),
    ...(response.data || {}),
    apiKeyCipher: '',
  };
  form.value = normalizeFormNumbers(form.value);
  open.value = true;
  title.value = t('ai.provider.index.748794-36');
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    form.value = normalizeFormNumbers(form.value);
    if (form.value.providerId) {
      await updateAiProvider(form.value);
      ElMessage.success(t('ai.provider.index.748794-37'));
    } else {
      await addAiProvider(form.value);
      ElMessage.success(t('ai.provider.index.748794-38'));
    }
    open.value = false;
    await getList();
  });
}

async function handleStatusChange(row) {
  const nextStatus = row.status;
  const text = nextStatus === '0' ? t('ai.provider.index.748794-39') : t('ai.provider.index.748794-40');
  try {
    await ElMessageBox.confirm(
      t('ai.provider.index.748794-44', { action: text, name: row.providerName }),
      t('ai.provider.index.748794-41'),
      {
        type: 'warning',
      }
    );
    await changeAiProviderStatus(row.providerId, nextStatus);
    ElMessage.success(t('ai.provider.index.748794-45', { action: text }));
  } catch (error) {
    row.status = nextStatus === '0' ? '1' : '0';
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(
    t('ai.provider.index.748794-46', { name: row.providerName }),
    t('ai.provider.index.748794-41'),
    {
      type: 'warning',
    }
  );
  await removeAiProvider(row.providerId);
  ElMessage.success(t('ai.provider.index.748794-42'));
  await getList();
}

function searchChange() {
  searchShow.value = !searchShow.value;
}

onMounted(() => {
  const storedViewMode = window.localStorage.getItem(PROVIDER_VIEW_MODE_KEY);
  if (storedViewMode === 'card' || storedViewMode === 'table') {
    viewMode.value = storedViewMode;
  }
  getList();
});
</script>

<style scoped lang="scss">
.ai-provider-page {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
}

.toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.toolbar-row__left,
.toolbar-row__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.button-group {
  display: flex;
  align-items: center;
}

.toggle-button {
  width: 32.5px;
  height: 32.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  background: #fff;
}

.active {
  background-color: #e0e0e0;
  border-color: #dcdfe6;
}

.card-button {
  border-radius: 4px 0 0 4px !important;
}

.list-button {
  border-radius: 0 4px 4px 0 !important;
}

.separator {
  width: 1px;
  height: 32px;
  background: #dcdfe6;
}

.provider-code-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.provider-code-value {
  color: #7f8a99;
  font-size: 12px;
  line-height: 1;
}

.form-tip {
  margin-top: 8px;
  color: #7f8a99;
  font-size: 12px;
}

.provider-card-row {
  margin-bottom: 16px;
}

.provider-card {
  height: 100%;
}

.provider-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.provider-card__title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.3;
}

.provider-card__subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.provider-card__code {
  color: #7f8a99;
  font-size: 12px;
}

.provider-card__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.meta-item--full {
  grid-column: 1 / -1;
}

.meta-item__label {
  color: #7f8a99;
  font-size: 12px;
}

.meta-item__value {
  color: #1f2d3d;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-all;
}

.provider-card__footer {
  margin-top: 16px;
}

.card-action-bar {
  display: flex;
  align-items: center;
  min-height: 39px;
  margin: 16px -20px -20px;
  padding: 0 10px;
  border-top: 1px solid #dcdfe6;
  overflow: hidden;
  box-sizing: border-box;
}

.card-action-item {
  flex: 1 1 0;
  min-width: 0;
  height: 39px;
  margin: 0 !important;
  padding: 0 8px !important;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #606266;
  font-size: 12px;
  white-space: nowrap;
  position: relative;
}

.card-action-item + .card-action-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 11px;
  width: 1px;
  height: 16px;
  background: #dcdfe6;
}

@media (max-width: 768px) {
  .provider-card__meta {
    grid-template-columns: 1fr;
  }

  .meta-item--full {
    grid-column: auto;
  }
}

.search-card {
  padding: 3px 0;
}

.search-card :deep(.el-form) {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: -22.5px;
}

.search-card :deep(.el-form-item:last-child) {
  margin-left: auto;
}

.toolbar-row {
  margin-bottom: 15px;
}

.toolbar-row--right {
  justify-content: flex-end;
}

:deep(.el-card) {
  border-radius: 4px;
}

:deep(.el-table .el-button + .el-button) {
  margin-left: 0;
}

:deep(.el-table .el-button.is-link) {
  min-height: 24px;
  padding: 0 4px;
}

:deep(.el-card__body) {
  overflow: hidden;
}

.ai-dialog-form :deep(.el-input),
.ai-dialog-form :deep(.el-select),
.ai-dialog-form :deep(.el-input-number),
.ai-dialog-form :deep(.el-textarea) {
  width: 400px !important;
  max-width: 100%;
}

.ai-dialog-form :deep(.el-upload),
.ai-dialog-form :deep(.el-upload-dragger) {
  width: 400px;
  max-width: 100%;
}

</style>
