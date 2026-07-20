<template>
  <div class="ai-model-page">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item>
          <el-input
            v-model="queryParams.modelCode"
            :placeholder="t('ai.model.index.748794-1')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="queryParams.modelName"
            :placeholder="t('ai.model.index.748794-3')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.providerId"
            :placeholder="t('ai.model.index.748794-5')"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in providerFilterOptions"
              :key="item.providerId"
              :label="buildProviderLabel(item)"
              :value="item.providerId"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="searchShow">
          <el-select
            v-model="queryParams.modelType"
            :placeholder="t('ai.model.index.748794-7')"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in dict.type.ai_model_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="searchShow">
          <el-select
            v-model="queryParams.status"
            :placeholder="t('ai.model.index.748794-9')"
            clearable
            style="width: 160px"
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
          <el-button type="primary" :icon="Search" @click="handleQuery" v-hasPermi="['ai:model:query']">
            {{ t('ai.model.index.748794-10') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ t('ai.model.index.748794-11') }}</el-button>
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
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['ai:model:add']">
            {{ t('ai.model.index.748794-12') }}
          </el-button>
        </div>
        <div class="toolbar-row__right">
          <el-button v-if="hasCacheStatsPermission" plain @click="handleOpenCacheStats">
            {{ t('ai.model.index.748794-13') }}
          </el-button>
          <div class="button-group">
            <el-tooltip effect="dark" :content="t('ai.model.index.748794-14')" placement="top">
              <el-button
                size="small"
                :icon="Menu"
                @click="handleViewModeChange('card')"
                :class="['toggle-button card-button', { active: viewMode === 'card' }]"
              />
            </el-tooltip>
            <div class="separator"></div>
            <el-tooltip effect="dark" :content="t('ai.model.index.748794-15')" placement="top">
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

      <el-table v-if="viewMode === 'table'" v-loading="loading" :data="modelList" :border="false">
        <el-table-column :label="t('ai.model.index.748794-0')" prop="modelCode" min-width="180" />
        <el-table-column :label="t('ai.model.index.748794-2')" prop="modelName" min-width="160" />
        <el-table-column :label="t('ai.model.index.748794-4')" min-width="180">
          <template #default="scope">
            <div class="provider-cell">
              <span>{{ scope.row.providerName || '-' }}</span>
              <span class="provider-code">{{ scope.row.providerCode || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.model.index.748794-6')" min-width="140">
          <template #default="scope">
            <dict-tag :options="dict.type.ai_model_type" :value="scope.row.modelType" />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.model.index.748794-16')" prop="contextLength" width="120" />
        <el-table-column :label="t('ai.model.index.748794-17')" prop="isDefault" width="100" align="center">
          <template #default="scope">
            {{ scope.row.isDefault === '1' ? t('ai.model.index.748794-18') : t('ai.model.index.748794-19') }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.model.index.748794-8')" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="'0'"
              :inactive-value="'1'"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.model.index.748794-20')" width="160" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" link :icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ai:model:edit']">
              {{ t('ai.model.index.748794-21') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['ai:model:remove']"
            >
              {{ t('ai.model.index.748794-22') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-row v-else v-loading="loading" :gutter="16" class="model-card-row">
        <el-col
          v-for="item in modelList"
          :key="item.modelId"
          style="margin-bottom: 15px"
          :xs="24"
          :sm="12"
          :lg="8"
          :xl="6"
        >
          <el-card shadow="hover" class="model-card">
            <div class="model-card__header">
              <div>
                <div class="model-card__title">{{ item.modelName || '-' }}</div>
                <div class="model-card__subtitle">
                  <span class="model-card__code">{{ item.modelCode || '-' }}</span>
                </div>
              </div>
              <el-switch
                v-model="item.status"
                :active-value="'0'"
                :inactive-value="'1'"
                @change="handleStatusChange(item)"
              />
            </div>
            <div class="model-card__meta">
              <div class="meta-item meta-item--full">
                <span class="meta-item__label">{{ t('ai.model.index.748794-4') }}</span>
                <div class="provider-cell">
                  <span>{{ item.providerName || '-' }}</span>
                  <span class="provider-code">{{ item.providerCode || '-' }}</span>
                </div>
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.model.index.748794-6') }}</span>
                <dict-tag :options="dict.type.ai_model_type" :value="item.modelType" />
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.model.index.748794-16') }}</span>
                <span class="meta-item__value">{{ item.contextLength ?? 0 }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.model.index.748794-17') }}</span>
                <span class="meta-item__value">
                  {{ item.isDefault === '1' ? t('ai.model.index.748794-18') : t('ai.model.index.748794-19') }}
                </span>
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.model.index.748794-8') }}</span>
                <dict-tag :options="dict.type.ai_provider_status" :value="item.status" />
              </div>
            </div>
            <div class="model-card__footer card-action-bar">
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleUpdate(item)"
                v-hasPermi="['ai:model:edit']"
              >
                {{ t('ai.model.index.748794-21') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleDelete(item)"
                v-hasPermi="['ai:model:remove']"
              >
                {{ t('ai.model.index.748794-22') }}
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

    <el-drawer
      v-model="cacheStatsOpen"
      :title="t('ai.model.index.748794-23')"
      size="520px"
      direction="rtl"
      append-to-body
      destroy-on-close
    >
      <div class="cache-stats-drawer" v-loading="cacheStatsLoading">
        <div class="cache-stats-drawer__intro">
          <div class="cache-stats-drawer__header">
            <div class="cache-stats-drawer__title-group">
              <div class="cache-stats-drawer__title">{{ t('ai.model.index.748794-24') }}</div>
              <div class="cache-stats-drawer__description">{{ t('ai.model.index.748794-25') }}</div>
            </div>
            <div class="cache-stats-drawer__actions">
              <el-tag :type="cacheStats.cacheEnabled ? 'success' : 'info'">
                {{ cacheStats.cacheEnabled ? t('ai.model.index.748794-26') : t('ai.model.index.748794-27') }}
              </el-tag>
              <el-tag type="info">TTL {{ cacheTtlText }}</el-tag>
              <el-button
                type="primary"
                link
                :icon="RefreshRight"
                :loading="cacheStatsLoading"
                @click="handleRefreshCacheStats"
              >
                {{ t('ai.model.index.748794-28') }}
              </el-button>
            </div>
          </div>
        </div>

        <el-empty
          v-if="!cacheStatsLoaded && !cacheStatsLoading"
          :description="cacheStatsLoadError || t('ai.model.index.748794-29')"
        />

        <template v-else>
          <div class="cache-summary-grid">
            <div v-for="item in cacheSummaryCards" :key="item.label" class="cache-summary-card">
              <div class="cache-summary-card__label">{{ item.label }}</div>
              <div class="cache-summary-card__value">{{ item.value }}</div>
              <div class="cache-summary-card__tip">{{ item.tip }}</div>
            </div>
          </div>

          <div class="cache-section">
            <div class="cache-section__title">{{ t('ai.model.index.748794-30') }}</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item v-for="item in cacheDetailItems" :key="item.label" :label="item.label">
                {{ item.value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="cache-section">
            <div class="cache-section__title">{{ t('ai.model.index.748794-31') }}</div>
            <ul class="cache-note-list">
              <li>{{ t('ai.model.index.748794-32') }}</li>
              <li>{{ t('ai.model.index.748794-33') }}</li>
              <li>{{ t('ai.model.index.748794-34') }}</li>
            </ul>
          </div>
        </template>
      </div>
    </el-drawer>

    <el-dialog v-model="open" :title="title" width="600px" append-to-body>
      <el-form ref="formRef" class="ai-dialog-form" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="t('ai.model.index.748794-4')" prop="providerId">
          <el-select
            v-model="form.providerId"
            :placeholder="t('ai.model.index.748794-5')"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in providerOptions"
              :key="item.providerId"
              :label="buildProviderLabel(item)"
              :value="item.providerId"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-0')" prop="modelCode">
          <el-input v-model="form.modelCode" :placeholder="t('ai.model.index.748794-1')" />
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-2')" prop="modelName">
          <el-input v-model="form.modelName" :placeholder="t('ai.model.index.748794-3')" />
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-6')" prop="modelType">
          <el-select v-model="form.modelType" :placeholder="t('ai.model.index.748794-7')" style="width: 100%">
            <el-option
              v-for="item in dict.type.ai_model_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-16')">
          <el-input-number v-model="form.contextLength" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-17')">
          <el-radio-group v-model="form.isDefault">
            <el-radio :value="'1'">{{ t('ai.model.index.748794-18') }}</el-radio>
            <el-radio :value="'0'">{{ t('ai.model.index.748794-19') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-8')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in dict.type.ai_provider_status" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-35')">
          <div class="request-options-editor">
            <div class="request-options-toolbar">
              <span class="request-options-tip">{{ t('ai.model.index.748794-36') }}</span>
              <el-button type="primary" link :icon="Plus" @click="addRequestOptionRow">
                {{ t('ai.model.index.748794-37') }}
              </el-button>
            </div>
            <div v-for="(row, index) in requestOptionRows" :key="`option-${index}`" class="request-option-row">
              <el-select
                v-model="row.key"
                filterable
                allow-create
                default-first-option
                clearable
                :placeholder="t('ai.model.index.748794-38')"
                style="width: 260px"
              >
                <el-option
                  v-for="item in requestParamKeyOptions"
                  :key="item.value"
                  :label="item.displayLabel"
                  :value="item.value"
                />
              </el-select>
              <el-input v-model="row.value" :placeholder="t('ai.model.index.748794-39')" class="request-option-value" />
              <el-button link :icon="Delete" @click="removeRequestOptionRow(index)">
                {{ t('ai.model.index.748794-22') }}
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item :label="t('ai.model.index.748794-40')">
          <el-input v-model="form.remark" type="textarea" :rows="3" :placeholder="t('ai.model.index.748794-41')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm" v-if="form.modelId" v-hasPermi="['ai:model:edit']">
          {{ t('ai.model.index.748794-42') }}
        </el-button>
        <el-button type="primary" @click="submitForm" v-else v-hasPermi="['ai:model:add']">
          {{ t('ai.model.index.748794-42') }}
        </el-button>
        <el-button @click="open = false">{{ t('ai.model.index.748794-43') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Delete,
  Edit,
  Grid,
  Menu,
  Plus,
  Refresh,
  RefreshRight,
  Search,
  ArrowDown,
  ArrowUp,
} from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { useDict } from '@/utils/dict';
import { checkPermi } from '@/utils/permission';
import {
  addAiModel,
  changeAiModelStatus,
  getAiModel,
  getAiModelCacheStats,
  listAiModel,
  removeAiModel,
  updateAiModel,
} from '@/api/ai/model';
import { listAiProvider, listEnabledAiProvider } from '@/api/ai/provider';
const { t } = useI18n();

const { dict } = useDict('ai_provider_status', 'ai_model_type', 'ai_model_param_key');
const MODEL_VIEW_MODE_KEY = 'fastbee.ai.model.viewMode';
const hasCacheStatsPermission = checkPermi(['ai:model:query']);

const loading = ref(false);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const modelList = ref([]);
const providerOptions = ref([]);
const providerFilterOptions = ref([]);
const requestOptionRows = ref([createRequestOptionRow()]);
const viewMode = ref('card');
const open = ref(false);
const title = ref('');
const formRef = ref();
const cacheStatsOpen = ref(false);
const cacheStatsLoading = ref(false);
const cacheStatsLoaded = ref(false);
const cacheStatsLoadError = ref('');
const cacheStats = ref(createDefaultCacheStats());
const route = useRoute();
const router = useRouter();

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  providerId: undefined,
  modelCode: '',
  modelName: '',
  modelType: '',
  status: '',
});

const form = ref(createDefaultForm());
const rules = {
  providerId: [{ required: true, message: t('ai.model.index.748794-44'), trigger: 'change' }],
  modelCode: [{ required: true, message: t('ai.model.index.748794-1'), trigger: 'blur' }],
  modelName: [{ required: true, message: t('ai.model.index.748794-3'), trigger: 'blur' }],
  modelType: [{ required: true, message: t('ai.model.index.748794-7'), trigger: 'change' }],
};

const requestParamKeyOptions = computed(() =>
  (dict.type.ai_model_param_key || []).map((item) => ({
    ...item,
    displayLabel: `${item.label}（${item.value}）`,
  }))
);
const cacheHitRateText = computed(() => formatHitRate(cacheStats.value.hitRate));
const cacheTtlText = computed(() => formatDuration(cacheStats.value.cacheTtlMs));
const cacheSummaryCards = computed(() => [
  {
    label: t('ai.model.index.748794-45'),
    value: cacheHitRateText.value,
    tip: t('ai.model.index.748794-77', [formatCounter(cacheStats.value.hitCount), formatCounter(cacheStats.value.cacheLookupCount)]),
  },
  {
    label: t('ai.model.index.748794-46'),
    value: cacheTtlText.value,
    tip: cacheStats.value.cacheEnabled ? t('ai.model.index.748794-47') : t('ai.model.index.748794-48'),
  },
  {
    label: t('ai.model.index.748794-49'),
    value: formatCounter(cacheStats.value.cacheEntryCount),
    tip: t('ai.model.index.748794-50'),
  },
  {
    label: t('ai.model.index.748794-51'),
    value: `${formatCounter(cacheStats.value.coldMissCount)} / ${formatCounter(cacheStats.value.expiredMissCount)}`,
    tip: t('ai.model.index.748794-52'),
  },
]);
const cacheDetailItems = computed(() => [
  {
    label: t('ai.model.index.748794-53'),
    value: cacheStats.value.cacheEnabled ? t('ai.model.index.748794-54') : t('ai.model.index.748794-55'),
  },
  { label: t('ai.model.index.748794-46'), value: cacheTtlText.value },
  { label: t('ai.model.index.748794-56'), value: formatCounter(cacheStats.value.requestCount) },
  { label: t('ai.model.index.748794-57'), value: formatCounter(cacheStats.value.cacheLookupCount) },
  { label: t('ai.model.index.748794-58'), value: formatCounter(cacheStats.value.hitCount) },
  { label: t('ai.model.index.748794-59'), value: formatCounter(cacheStats.value.coldMissCount) },
  { label: t('ai.model.index.748794-60'), value: formatCounter(cacheStats.value.expiredMissCount) },
  { label: t('ai.model.index.748794-61'), value: formatCounter(cacheStats.value.bypassCount) },
  { label: t('ai.model.index.748794-62'), value: formatCounter(cacheStats.value.providerEvictCount) },
  { label: t('ai.model.index.748794-63'), value: formatCounter(cacheStats.value.modelEvictCount) },
  { label: t('ai.model.index.748794-64'), value: formatCounter(cacheStats.value.clearCount) },
  { label: t('ai.model.index.748794-65'), value: formatCounter(cacheStats.value.cacheEntryCount) },
]);

function createDefaultForm() {
  return {
    modelId: undefined,
    providerId: undefined,
    modelCode: '',
    modelName: '',
    modelType: 'CHAT',
    contextLength: 0,
    requestOptions: '',
    isDefault: '0',
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
    contextLength: toNumberOrDefault(data?.contextLength, 0),
  };
}

function createDefaultCacheStats() {
  return {
    cacheEnabled: false,
    cacheTtlMs: 0,
    requestCount: 0,
    cacheLookupCount: 0,
    hitCount: 0,
    coldMissCount: 0,
    expiredMissCount: 0,
    bypassCount: 0,
    providerEvictCount: 0,
    modelEvictCount: 0,
    clearCount: 0,
    cacheEntryCount: 0,
    hitRate: 0,
  };
}

function createRequestOptionRow() {
  return {
    key: '',
    value: '',
  };
}

function buildProviderLabel(provider) {
  if (!provider) {
    return '';
  }
  if (provider.providerCode) {
    return `${provider.providerName}（${provider.providerCode}）`;
  }
  return provider.providerName || '';
}

function formatCounter(value) {
  const numberValue = Number(value ?? 0);
  if (Number.isNaN(numberValue)) {
    return '0';
  }
  return String(numberValue);
}

function formatHitRate(value) {
  const numberValue = Number(value ?? 0);
  if (Number.isNaN(numberValue)) {
    return '--';
  }
  return `${(numberValue * 100).toFixed(1)}%`;
}

function formatDuration(ms) {
  const duration = Number(ms ?? 0);
  if (Number.isNaN(duration) || duration <= 0) {
    return t('ai.model.index.748794-66');
  }
  if (duration % 60000 === 0) {
    return t('ai.model.index.748794-78', [duration / 60000]);
  }
  if (duration % 1000 === 0) {
    return t('ai.model.index.748794-79', [duration / 1000]);
  }
  return `${duration} ms`;
}

function formatRequestOptionValue(value) {
  if (value === null || value === undefined) {
    return '';
  }
  if (Array.isArray(value) || Object.prototype.toString.call(value) === '[object Object]') {
    return JSON.stringify(value);
  }
  return String(value);
}

function parseRequestOptions(text) {
  if (!text) {
    return [createRequestOptionRow()];
  }
  try {
    const json = JSON.parse(text);
    if (!json || Array.isArray(json) || typeof json !== 'object') {
      return [createRequestOptionRow()];
    }
    const rows = Object.entries(json).map(([key, value]) => ({
      key,
      value: formatRequestOptionValue(value),
    }));
    return rows.length ? rows : [createRequestOptionRow()];
  } catch (error) {
    return [createRequestOptionRow()];
  }
}

function resetRequestOptionRows(text = '') {
  requestOptionRows.value = parseRequestOptions(text);
}

function addRequestOptionRow() {
  requestOptionRows.value.push(createRequestOptionRow());
}

function removeRequestOptionRow(index) {
  if (requestOptionRows.value.length === 1) {
    requestOptionRows.value = [createRequestOptionRow()];
    return;
  }
  requestOptionRows.value.splice(index, 1);
}

function normalizeRequestOptionValue(rawValue) {
  const text = String(rawValue ?? '').trim();
  if (!text) {
    return '';
  }
  if ((text.startsWith('{') && text.endsWith('}')) || (text.startsWith('[') && text.endsWith(']'))) {
    try {
      return JSON.parse(text);
    } catch (error) {
      return rawValue;
    }
  }
  if (/^(true|false)$/i.test(text)) {
    return text.toLowerCase() === 'true';
  }
  if (/^-?(0|[1-9]\d*)(\.\d+)?$/.test(text)) {
    return Number(text);
  }
  return rawValue;
}

function buildRequestOptionsText() {
  const requestOptions = {};
  for (const row of requestOptionRows.value) {
    const key = String(row.key || '').trim();
    if (!key) {
      continue;
    }
    if (Object.prototype.hasOwnProperty.call(requestOptions, key)) {
      ElMessage.warning(t('ai.model.index.748794-80', [key]));
      return null;
    }
    requestOptions[key] = normalizeRequestOptionValue(row.value);
  }
  return Object.keys(requestOptions).length ? JSON.stringify(requestOptions) : '';
}

async function loadProviderOptions(currentModel) {
  const response = await listEnabledAiProvider();
  const options = response.data || [];
  if (currentModel?.providerId && !options.some((item) => item.providerId === currentModel.providerId)) {
    options.push({
      providerId: currentModel.providerId,
      providerName: currentModel.providerName || t('ai.model.index.748794-67'),
      providerCode: currentModel.providerCode || '',
    });
  }
  providerOptions.value = options;
}

async function loadProviderFilterOptions() {
  const response = await listAiProvider({
    pageNum: 1,
    pageSize: 500,
  });
  const options = response.rows || [];
  const routeProviderId = normalizeProviderId(route.query.providerId);
  if (routeProviderId && !options.some((item) => item.providerId === routeProviderId)) {
    options.push({
      providerId: routeProviderId,
      providerName: String(route.query.providerName || t('ai.model.index.748794-67')),
      providerCode: '',
    });
  }
  providerFilterOptions.value = options;
}

async function loadCacheStats(options = {}) {
  if (!hasCacheStatsPermission) {
    return;
  }
  const { showError = false } = options;
  cacheStatsLoading.value = true;
  cacheStatsLoadError.value = '';
  try {
    const response = await getAiModelCacheStats();
    cacheStats.value = {
      ...createDefaultCacheStats(),
      ...(response.data || {}),
    };
    cacheStatsLoaded.value = true;
  } catch (error) {
    cacheStatsLoaded.value = false;
    cacheStatsLoadError.value = t('ai.model.index.748794-68');
    if (showError) {
      ElMessage.error(cacheStatsLoadError.value);
    }
  } finally {
    cacheStatsLoading.value = false;
  }
}

function normalizeProviderId(value) {
  if (value === undefined || value === null || value === '') {
    return undefined;
  }
  const providerId = Number(value);
  return Number.isNaN(providerId) ? undefined : providerId;
}

function applyRouteProviderFilter() {
  queryParams.value.providerId = normalizeProviderId(route.query.providerId);
}

async function handleRefreshCacheStats() {
  await loadCacheStats({ showError: true });
  if (cacheStatsLoaded.value) {
    ElMessage.success(t('ai.model.index.748794-69'));
  }
}

async function handleOpenCacheStats() {
  cacheStatsOpen.value = true;
  await loadCacheStats({ showError: true });
}

function shouldRefreshCacheStatsAfterMutation() {
  return hasCacheStatsPermission && (cacheStatsOpen.value || cacheStatsLoaded.value);
}

function handleViewModeChange(mode) {
  viewMode.value = mode || 'card';
  window.localStorage.setItem(MODEL_VIEW_MODE_KEY, viewMode.value);
}

async function getList() {
  loading.value = true;
  try {
    const response = await listAiModel(queryParams.value);
    modelList.value = response.rows || [];
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
    providerId: undefined,
    modelCode: '',
    modelName: '',
    modelType: '',
    status: '',
  };
  if (route.query.providerId || route.query.providerName || route.query.t) {
    const nextQuery = { ...route.query };
    delete nextQuery.providerId;
    delete nextQuery.providerName;
    delete nextQuery.t;
    router.replace({ path: route.path, query: nextQuery }).catch(() => {});
    return;
  }
  getList();
}

async function handleAdd() {
  await loadProviderOptions();
  form.value = createDefaultForm();
  resetRequestOptionRows();
  open.value = true;
  title.value = t('ai.model.index.748794-12');
}

async function handleUpdate(row) {
  const response = await getAiModel(row.modelId);
  const modelData = {
    ...createDefaultForm(),
    ...(response.data || {}),
  };
  await loadProviderOptions(modelData);
  form.value = normalizeFormNumbers(modelData);
  resetRequestOptionRows(modelData.requestOptions);
  open.value = true;
  title.value = t('ai.model.index.748794-70');
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    const requestOptionsText = buildRequestOptionsText();
    if (requestOptionsText === null) {
      return;
    }
    form.value = normalizeFormNumbers(form.value);
    form.value.requestOptions = requestOptionsText;
    if (form.value.modelId) {
      await updateAiModel(form.value);
      ElMessage.success(t('ai.model.index.748794-71'));
    } else {
      await addAiModel(form.value);
      ElMessage.success(t('ai.model.index.748794-72'));
    }
    open.value = false;
    await getList();
    if (shouldRefreshCacheStatsAfterMutation()) {
      await loadCacheStats();
    }
  });
}

async function handleStatusChange(row) {
  const nextStatus = row.status;
  const text = nextStatus === '0' ? t('ai.model.index.748794-73') : t('ai.model.index.748794-74');
  try {
    await ElMessageBox.confirm(t('ai.model.index.748794-81', [text, row.modelName]), t('ai.model.index.748794-75'), {
      type: 'warning',
    });
    await changeAiModelStatus(row.modelId, nextStatus);
    ElMessage.success(t('ai.model.index.748794-82', [text]));
    if (shouldRefreshCacheStatsAfterMutation()) {
      await loadCacheStats();
    }
  } catch (error) {
    row.status = nextStatus === '0' ? '1' : '0';
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(t('ai.model.index.748794-83', [row.modelName]), t('ai.model.index.748794-75'), {
    type: 'warning',
  });
  await removeAiModel(row.modelId);
  ElMessage.success(t('ai.model.index.748794-76'));
  await getList();
  if (shouldRefreshCacheStatsAfterMutation()) {
    await loadCacheStats();
  }
}
function searchChange() {
  searchShow.value = !searchShow.value;
}

watch(
  () => [route.query.providerId, route.query.t],
  async () => {
    applyRouteProviderFilter();
    await loadProviderFilterOptions();
    await getList();
  }
);

onMounted(async () => {
  const storedViewMode = window.localStorage.getItem(MODEL_VIEW_MODE_KEY);
  if (storedViewMode === 'card' || storedViewMode === 'table') {
    viewMode.value = storedViewMode;
  }
  applyRouteProviderFilter();
  await loadProviderFilterOptions();
  await loadProviderOptions();
  await getList();
});
</script>

<style scoped lang="scss">
.ai-model-page {
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

.provider-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.provider-code {
  color: #7f8a99;
  font-size: 12px;
  line-height: 1;
}

.request-options-editor {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.request-options-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.request-options-tip {
  color: #7f8a99;
  font-size: 12px;
  line-height: 1.5;
}

.request-option-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.request-option-value {
  flex: 1;
}

.model-card-row {
  margin-bottom: 16px;
}

.model-card {
  height: 100%;
}

.model-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.model-card__title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.3;
}

.model-card__subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.model-card__code {
  color: #7f8a99;
  font-size: 12px;
}

.model-card__meta {
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
}

.model-card__footer {
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

.cache-stats-drawer {
  min-height: 100%;
}

.cache-stats-drawer__intro {
  margin-bottom: 16px;
}

.cache-stats-drawer__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.cache-stats-drawer__title-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cache-stats-drawer__title {
  color: #1f2d3d;
  font-size: 16px;
  font-weight: 600;
}

.cache-stats-drawer__description {
  color: #5f6b7a;
  font-size: 13px;
  line-height: 1.7;
}

.cache-stats-drawer__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.cache-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.cache-summary-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #f8fafc;
}

.cache-summary-card__label {
  color: #7f8a99;
  font-size: 12px;
}

.cache-summary-card__value {
  color: #1f2d3d;
  font-size: 22px;
  font-weight: 600;
  line-height: 1.2;
}

.cache-summary-card__tip {
  color: #5f6b7a;
  font-size: 12px;
  line-height: 1.7;
}

.cache-section {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
}

.cache-section__title {
  margin-bottom: 12px;
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 600;
}

.cache-note-list {
  margin: 0;
  padding-left: 18px;
  color: #5f6b7a;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .cache-stats-drawer__header {
    width: 100%;
  }

  .cache-stats-drawer__header {
    flex-direction: column;
  }

  .cache-summary-grid,
  .model-card__meta {
    grid-template-columns: 1fr;
  }

  .meta-item--full {
    grid-column: auto;
  }

  .request-option-row {
    flex-direction: column;
    align-items: stretch;
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

.request-options-editor {
  width: 400px;
  max-width: 100%;
}

.request-option-row {
  flex-wrap: wrap;
}

.request-option-row :deep(.el-select),
.request-option-row :deep(.el-input) {
  width: 400px !important;
  max-width: 100%;
  flex: none;
}

</style>
