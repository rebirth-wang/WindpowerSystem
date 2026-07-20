<template>
  <el-drawer
    v-model="dialogVisible"
    class="knowledge-document-manager-drawer"
    :title="t('ai.knowledge.components.KnowledgeDocumentManager.748794-0')"
    size="88%"
    direction="rtl"
    append-to-body
    destroy-on-close
    @opened="handleOpened"
  >
    <div
      v-loading="operationLocked"
      class="manager-body"
      :element-loading-text="operationLoadingText"
      element-loading-background="rgba(255, 255, 255, 0.7)"
    >
      <el-alert type="info" :closable="false" show-icon class="manager-alert" :title="alertTitle" />

      <el-card class="search-card">
        <el-form :model="queryParams" inline @submit.prevent>
          <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-1')">
            <el-input
              v-model="queryParams.fileName"
              :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-2')"
              clearable
              :disabled="operationLocked"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-3')">
            <el-select
              v-model="queryParams.sourceOrigin"
              :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-4')"
              clearable
              style="width: 160px"
              :disabled="operationLocked"
            >
              <el-option
                v-for="item in sourceOriginOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-5')">
            <el-select
              v-model="queryParams.parseStatus"
              :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-6')"
              clearable
              style="width: 160px"
              :disabled="operationLocked"
            >
              <el-option v-for="item in parseStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-7')">
            <el-select
              v-model="queryParams.status"
              :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-8')"
              clearable
              style="width: 160px"
              :disabled="operationLocked"
            >
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              v-if="canQueryKnowledge"
              type="primary"
              :icon="Search"
              :disabled="operationLocked"
              @click="handleQuery"
            >
              {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-9') }}
            </el-button>
            <el-button :icon="Refresh" :disabled="operationLocked" @click="handleReset">
              {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-10') }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="toolbar-row">
        <el-button
          v-if="canEditKnowledge"
          type="primary"
          plain
          :icon="Upload"
          :disabled="operationLocked"
          @click="handleUpload"
        >
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-11') }}
        </el-button>
        <el-button v-if="canQueryKnowledge" plain :icon="Refresh" :disabled="operationLocked" @click="getList">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-12') }}
        </el-button>
      </div>

      <el-table v-loading="loading" :data="documentList">
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-1')"
          prop="fileName"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-3')" min-width="110">
          <template #default="{ row }">
            <dict-tag :options="sourceOriginOptions" :value="row.sourceOrigin" />
          </template>
        </el-table-column>
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-13')"
          prop="sortNum"
          width="80"
        />
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-14')"
          prop="appVersion"
          min-width="120"
        />
        <el-table-column :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-5')" min-width="120">
          <template #default="{ row }">
            <dict-tag :options="parseStatusOptions" :value="row.parseStatus" />
          </template>
        </el-table-column>
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-15')"
          prop="chunkCount"
          width="90"
        />
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-16')"
          prop="parsedSummary"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-17')"
          prop="updateTime"
          min-width="160"
        />
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-7')"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="'0'"
              :inactive-value="'1'"
              :disabled="operationLocked || !canEditKnowledge"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-18')"
          min-width="340"
          fixed="right"
        >
          <template #default="{ row }">
            <div class="document-operation">
              <el-button
                v-if="canEditKnowledge"
                size="small"
                link
                :icon="Edit"
                :disabled="operationLocked"
                @click="handleEdit(row)"
              >
                {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-19') }}
              </el-button>
              <el-button
                v-if="canEditKnowledge"
                size="small"
                link
                :icon="RefreshRight"
                :loading="isReparsing(row)"
                :disabled="operationLocked && !isReparsing(row)"
                @click="handleReparse(row)"
              >
                {{
                  isReparsing(row)
                    ? t('ai.knowledge.components.KnowledgeDocumentManager.748794-20')
                    : t('ai.knowledge.components.KnowledgeDocumentManager.748794-21')
                }}
              </el-button>
              <el-button
                v-if="canQueryKnowledge && row.parsedSnapshotPath"
                size="small"
                link
                :icon="View"
                :disabled="operationLocked || !canViewDocumentSnapshot"
                @click="handlePreviewSnapshot(row)"
              >
                {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-22') }}
              </el-button>
              <el-button
                v-if="canQueryKnowledge"
                size="small"
                link
                :icon="Download"
                :disabled="operationLocked || !canDownloadDocument"
                @click="handleDownload(row)"
              >
                {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-23') }}
              </el-button>
              <el-button
                v-if="canQueryKnowledge && supportsSegmentPreview(row)"
                size="small"
                link
                :icon="Tickets"
                :disabled="operationLocked"
                @click="handlePreviewSegments(row)"
              >
                {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-24') }}
              </el-button>
              <el-button
                v-if="canRemoveKnowledge"
                size="small"
                link
                :icon="Delete"
                :disabled="operationLocked"
                @click="handleDelete(row)"
              >
                {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-25') }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <el-dialog
      v-model="editOpen"
      :title="t('ai.knowledge.components.KnowledgeDocumentManager.748794-26')"
      width="600px"
      append-to-body
    >
      <el-form ref="formRef" class="ai-dialog-form" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-1')">
          <el-input :model-value="form.fileName" disabled />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-3')" prop="sourceOrigin">
          <el-select
            v-model="form.sourceOrigin"
            :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-4')"
            style="width: 100%"
          >
            <el-option v-for="item in sourceOriginOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-27')" prop="sortNum">
          <el-input-number v-model="form.sortNum" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-14')">
          <el-input
            v-model="form.appVersion"
            :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-28')"
          />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-5')">
          <el-input :model-value="formatDictLabel(parseStatusOptions, form.parseStatus)" disabled />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-16')">
          <el-input :model-value="form.parsedSummary || '-'" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-7')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-29')">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            :placeholder="t('ai.knowledge.components.KnowledgeDocumentManager.748794-30')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="canEditKnowledge" type="primary" :disabled="operationLocked" @click="submitForm">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-31') }}
        </el-button>
        <el-button @click="editOpen = false">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-32') }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="segmentsOpen"
      :title="t('ai.knowledge.components.KnowledgeDocumentManager.748794-33')"
      width="860px"
      append-to-body
    >
      <el-alert
        type="info"
        :closable="false"
        show-icon
        class="manager-alert"
        :title="
          t('ai.knowledge.components.KnowledgeDocumentManager.748794-87', [segmentPreviewDocument?.fileName || '-'])
        "
      />
      <el-empty
        v-if="!segmentList.length"
        :description="t('ai.knowledge.components.KnowledgeDocumentManager.748794-34')"
      />
      <el-timeline v-else>
        <el-timeline-item
          v-for="item in segmentList"
          :key="item.segmentCode || item.chunkIndex"
          :timestamp="t('ai.knowledge.components.KnowledgeDocumentManager.748794-88', [item.chunkIndex ?? '-'])"
        >
          <div class="segment-card">
            <div class="segment-title">
              {{
                item.segmentTitle ||
                t('ai.knowledge.components.KnowledgeDocumentManager.748794-88', [item.chunkIndex ?? '-'])
              }}
            </div>
            <div class="segment-content">{{ item.content }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <template #footer>
        <el-button @click="segmentsOpen = false">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-35') }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="snapshotOpen"
      :title="t('ai.knowledge.components.KnowledgeDocumentManager.748794-36')"
      width="1180px"
      append-to-body
    >
      <el-alert type="info" :closable="false" show-icon class="manager-alert" :title="snapshotAlertTitle" />
      <el-descriptions v-if="snapshotData" :column="4" border class="snapshot-descriptions">
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-37')">
          {{ snapshotData.kbType || '-' }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-5')">
          {{ snapshotData.parseStatus || '-' }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-38')">
          {{ snapshotData.totalItemCount ?? snapshotData.rowCount ?? 0 }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-39')">
          {{ snapshotData.previewItemCount ?? 0 }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-40')">
          {{ snapshotData.validationErrorCount ?? 0 }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-41')">
          {{ snapshotData.validationWarningCount ?? 0 }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-42')">
          {{ snapshotData.generatedAt || '-' }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-43')">
          {{ snapshotData.parsedSnapshotPath || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="snapshotData?.parsedSummary" class="snapshot-summary">
        <span class="snapshot-summary__label">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-16') }}
        </span>
        <div class="snapshot-summary__content">{{ snapshotData.parsedSummary }}</div>
      </div>
      <div v-if="snapshotValidationIssues.length" class="snapshot-section">
        <div class="snapshot-section__title">{{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-44') }}</div>
        <el-table :data="snapshotValidationIssues" max-height="280">
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-45')"
            prop="rowNum"
            width="80"
          />
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-46')"
            prop="level"
            width="90"
          />
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-47')"
            prop="fieldName"
            width="140"
          />
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-48')"
            prop="issueCode"
            min-width="160"
          />
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-49')"
            prop="currentValue"
            min-width="180"
            show-overflow-tooltip
          />
          <el-table-column
            :label="t('ai.knowledge.components.KnowledgeDocumentManager.748794-50')"
            prop="message"
            min-width="220"
            show-overflow-tooltip
          />
        </el-table>
      </div>
      <div class="snapshot-section">
        <div class="snapshot-section__title">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-51') }}
          <span v-if="snapshotData?.hasMoreItems" class="snapshot-section__tip">
            {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-89', [snapshotData.previewItemCount]) }}
          </span>
        </div>
        <el-empty
          v-if="!snapshotItems.length"
          :description="t('ai.knowledge.components.KnowledgeDocumentManager.748794-52')"
        />
        <el-table v-else :data="snapshotItems" max-height="360">
          <el-table-column
            v-for="column in snapshotColumns"
            :key="column"
            :label="formatSnapshotColumnLabel(column)"
            :prop="column"
            min-width="160"
            show-overflow-tooltip
          />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="snapshotOpen = false">
          {{ t('ai.knowledge.components.KnowledgeDocumentManager.748794-35') }}
        </el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup>
import { computed, getCurrentInstance, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Download, Edit, Refresh, RefreshRight, Search, Tickets, Upload, View } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
import { checkPermi, isAdminAccount } from '@/utils/permission';

const { t } = useI18n();
import {
  changeKnowledgeDocumentStatus,
  getKnowledgeDocumentSnapshot,
  getKnowledgeDocument,
  listKnowledgeDocument,
  listKnowledgeSegments,
  reparseKnowledgeDocument,
  removeKnowledgeDocument,
  updateKnowledgeDocument,
} from '@/api/ai/knowledgeDocument';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  knowledgeBase: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(['update:modelValue', 'upload', 'updated']);

const { proxy } = getCurrentInstance();
const { dict } = useDict('ai_knowledge_parse_status', 'ai_knowledge_source_origin', 'ai_knowledge_status');
const fallbackParseStatusOptions = [
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-53'), value: 'PENDING' },
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-54'), value: 'PARSING' },
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-55'), value: 'SUCCESS' },
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-56'), value: 'FAILED' },
];
const fallbackSourceOriginOptions = [
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-57'), value: 'OFFICIAL' },
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-58'), value: 'CUSTOM' },
];
const fallbackStatusOptions = [
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-59'), value: '0' },
  { label: t('ai.knowledge.components.KnowledgeDocumentManager.748794-60'), value: '1' },
];

const loading = ref(false);
const total = ref(0);
const documentList = ref([]);
const editOpen = ref(false);
const segmentsOpen = ref(false);
const snapshotOpen = ref(false);
const segmentList = ref([]);
const segmentPreviewDocument = ref(null);
const snapshotData = ref(null);
const formRef = ref();
const reparsingDocumentId = ref(undefined);

const queryParams = ref(createQueryParams());
const form = ref(createDefaultForm());
const rules = {
  sourceOrigin: [
    { required: true, message: t('ai.knowledge.components.KnowledgeDocumentManager.748794-4'), trigger: 'change' },
  ],
  sortNum: [
    { required: true, message: t('ai.knowledge.components.KnowledgeDocumentManager.748794-61'), trigger: 'blur' },
  ],
};

const parseStatusOptions = computed(() => {
  return dict.type.ai_knowledge_parse_status?.length ? dict.type.ai_knowledge_parse_status : fallbackParseStatusOptions;
});
const sourceOriginOptions = computed(() => {
  return dict.type.ai_knowledge_source_origin?.length
    ? dict.type.ai_knowledge_source_origin
    : fallbackSourceOriginOptions;
});
const statusOptions = computed(() => {
  return dict.type.ai_knowledge_status?.length ? dict.type.ai_knowledge_status : fallbackStatusOptions;
});
const canQueryKnowledge = computed(() => checkPermi(['ai:knowledge:query']));
const canEditKnowledge = computed(() => checkPermi(['ai:knowledge:edit']));
const canRemoveKnowledge = computed(() => checkPermi(['ai:knowledge:remove']));
const canDownloadDocument = computed(() => isAdminAccount());
const canViewDocumentSnapshot = computed(() => isAdminAccount());
const operationLocked = computed(() => reparsingDocumentId.value !== undefined);
const operationLoadingText = computed(() => {
  return operationLocked.value ? t('ai.knowledge.components.KnowledgeDocumentManager.748794-62') : '';
});
const snapshotItems = computed(() => {
  return Array.isArray(snapshotData.value?.items) ? snapshotData.value.items : [];
});
const snapshotColumns = computed(() => {
  if (!snapshotItems.value.length) {
    return [];
  }
  const columnSet = new Set();
  snapshotItems.value.forEach((item) => {
    Object.keys(item || {}).forEach((key) => {
      columnSet.add(key);
    });
  });
  return Array.from(columnSet);
});
const snapshotValidationIssues = computed(() => {
  return Array.isArray(snapshotData.value?.validationIssues) ? snapshotData.value.validationIssues : [];
});
const snapshotAlertTitle = computed(() => {
  const fileName = snapshotData.value?.fileName || '-';
  return t('ai.knowledge.components.KnowledgeDocumentManager.748794-90', [fileName]);
});

const dialogVisible = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);
  },
});

const alertTitle = computed(() => {
  const knowledgeBaseName = props.knowledgeBase?.kbName || '-';
  const activeVersion = props.knowledgeBase?.activeVersionNo || '-';
  return t('ai.knowledge.components.KnowledgeDocumentManager.748794-91', [knowledgeBaseName, activeVersion]);
});

watch(
  () => props.knowledgeBase?.knowledgeBaseId,
  () => {
    if (dialogVisible.value) {
      resetQuery();
      getList();
    }
  }
);

function createQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    knowledgeBaseId: undefined,
    fileName: '',
    sourceOrigin: '',
    parseStatus: '',
    status: '',
  };
}

function createDefaultForm() {
  return {
    documentId: undefined,
    fileName: '',
    sourceOrigin: 'CUSTOM',
    sortNum: 100,
    appVersion: '',
    parseStatus: '',
    parsedSummary: '',
    status: '0',
    remark: '',
  };
}

function toNumberOrDefault(value, defaultValue = 100) {
  if (value === '' || value === null || value === undefined) return defaultValue;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? defaultValue : numberValue;
}

function handleOpened() {
  resetQuery();
  getList();
}

function resetQuery() {
  queryParams.value = {
    ...createQueryParams(),
    knowledgeBaseId: props.knowledgeBase?.knowledgeBaseId,
  };
}

async function getList() {
  if (!props.knowledgeBase?.knowledgeBaseId) {
    documentList.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  try {
    const response = await listKnowledgeDocument({
      ...queryParams.value,
      knowledgeBaseId: props.knowledgeBase.knowledgeBaseId,
    });
    documentList.value = response.rows || [];
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
  resetQuery();
  getList();
}

function handleUpload() {
  if (operationLocked.value) {
    return;
  }
  emit('upload', props.knowledgeBase);
}

async function handleEdit(row) {
  if (operationLocked.value) {
    return;
  }
  try {
    const response = await getKnowledgeDocument(row.documentId);
    form.value = {
      ...createDefaultForm(),
      ...(response.data || {}),
      sortNum: toNumberOrDefault(response.data?.sortNum),
      status: response.data?.status || '0',
      sourceOrigin: response.data?.sourceOrigin || 'CUSTOM',
    };
    editOpen.value = true;
  } catch (_error) {
    // 接口层统一兜底提示，这里避免组件事件产生未处理 Promise。
  }
}

async function submitForm() {
  if (operationLocked.value) {
    return;
  }
  try {
    await formRef.value.validate();
  } catch (_error) {
    return;
  }
  form.value.sortNum = toNumberOrDefault(form.value.sortNum);
  await updateKnowledgeDocument(form.value);
  ElMessage.success(t('ai.knowledge.components.KnowledgeDocumentManager.748794-63'));
  editOpen.value = false;
  await getList();
  emit('updated');
}

async function handleReparse(row) {
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-92', [row.fileName]),
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-64'),
      {
        type: 'warning',
      }
    );
    reparsingDocumentId.value = row.documentId;
    const response = await reparseKnowledgeDocument(row.documentId);
    const result = response.data || {};
    ElMessage.success(response.msg || t('ai.knowledge.components.KnowledgeDocumentManager.748794-65'));
    await getList();
    emit('updated');
    await ElMessageBox.alert(
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-93', [
        result.parseStatus || '-' + t('ai.knowledge.components.KnowledgeDocumentManager.748794-66') + '-',
      ]),
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-67'),
      { confirmButtonText: t('ai.knowledge.components.KnowledgeDocumentManager.748794-31') }
    );
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
    await getList();
    emit('updated');
  } finally {
    reparsingDocumentId.value = undefined;
  }
}

async function handleStatusChange(row) {
  if (operationLocked.value) {
    return;
  }
  const nextStatus = row.status;
  const text =
    nextStatus === '0'
      ? t('ai.knowledge.components.KnowledgeDocumentManager.748794-59')
      : t('ai.knowledge.components.KnowledgeDocumentManager.748794-60');
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-94', [text, row.fileName]),
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-64'),
      {
        type: 'warning',
      }
    );
    await changeKnowledgeDocumentStatus(row.documentId, nextStatus);
    ElMessage.success(t('ai.knowledge.components.KnowledgeDocumentManager.748794-95', [text]));
    emit('updated');
  } catch (_error) {
    row.status = nextStatus === '0' ? '1' : '0';
  }
}

function handleDownload(row) {
  if (operationLocked.value) {
    return;
  }
  if (!canDownloadDocument.value) {
    ElMessage.warning(t('ai.knowledge.components.KnowledgeDocumentManager.748794-68'));
    return;
  }
  proxy?.download(
    '/ai/knowledge/document/download',
    { documentId: row.documentId },
    row.fileName || `document_${row.documentId}`
  );
}

async function handleDelete(row) {
  if (operationLocked.value) {
    return;
  }
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-96', [row.fileName]),
      t('ai.knowledge.components.KnowledgeDocumentManager.748794-64'),
      {
        type: 'warning',
      }
    );
    await removeKnowledgeDocument(row.documentId);
    ElMessage.success(t('ai.knowledge.components.KnowledgeDocumentManager.748794-69'));
    await getList();
    emit('updated');
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
  }
}

async function handlePreviewSegments(row) {
  if (operationLocked.value) {
    return;
  }
  try {
    segmentPreviewDocument.value = row;
    const response = await listKnowledgeSegments(row.documentId);
    segmentList.value = response.data || [];
    segmentsOpen.value = true;
  } catch (_error) {
    // 接口层统一兜底提示，这里避免组件事件产生未处理 Promise。
  }
}

async function handlePreviewSnapshot(row) {
  if (operationLocked.value) {
    return;
  }
  if (!canViewDocumentSnapshot.value) {
    ElMessage.warning(t('ai.knowledge.components.KnowledgeDocumentManager.748794-70'));
    return;
  }
  try {
    const response = await getKnowledgeDocumentSnapshot(row.documentId, 30);
    snapshotData.value = response.data || {};
    snapshotOpen.value = true;
  } catch (_error) {
    // 接口层统一兜底提示，这里避免组件事件产生未处理 Promise。
  }
}

function supportsSegmentPreview(row) {
  const fileName = String(row?.fileName || '').toLowerCase();
  return ['.txt', '.md', '.json', '.csv', '.log', '.yaml', '.yml', '.xml', '.sql'].some((suffix) =>
    fileName.endsWith(suffix)
  );
}

function formatDictLabel(options, value) {
  const optionList = Array.isArray(options) ? options : options?.value || [];
  return optionList.find((item) => item.value === value)?.label || value || '-';
}

function formatSnapshotColumnLabel(column) {
  const labelMap = {
    tableName: t('ai.knowledge.components.KnowledgeDocumentManager.748794-71'),
    tableComment: t('ai.knowledge.components.KnowledgeDocumentManager.748794-72'),
    columnName: t('ai.knowledge.components.KnowledgeDocumentManager.748794-73'),
    columnComment: t('ai.knowledge.components.KnowledgeDocumentManager.748794-74'),
    semanticName: t('ai.knowledge.components.KnowledgeDocumentManager.748794-75'),
    semanticType: t('ai.knowledge.components.KnowledgeDocumentManager.748794-76'),
    sourceType: t('ai.knowledge.components.KnowledgeDocumentManager.748794-3'),
    sourceCode: t('ai.knowledge.components.KnowledgeDocumentManager.748794-77'),
    relationHints: t('ai.knowledge.components.KnowledgeDocumentManager.748794-78'),
    valueMappings: t('ai.knowledge.components.KnowledgeDocumentManager.748794-79'),
    aliases: t('ai.knowledge.components.KnowledgeDocumentManager.748794-80'),
    queryHints: t('ai.knowledge.components.KnowledgeDocumentManager.748794-81'),
    moduleName: t('ai.knowledge.components.KnowledgeDocumentManager.748794-82'),
    moduleCode: t('ai.knowledge.components.KnowledgeDocumentManager.748794-83'),
    itemTitle: t('ai.knowledge.components.KnowledgeDocumentManager.748794-84'),
    itemType: t('ai.knowledge.components.KnowledgeDocumentManager.748794-85'),
    itemContent: t('ai.knowledge.components.KnowledgeDocumentManager.748794-86'),
    remark: t('ai.knowledge.components.KnowledgeDocumentManager.748794-29'),
  };
  return labelMap[column] || column;
}

function isReparsing(row) {
  return reparsingDocumentId.value === row.documentId;
}

defineExpose({
  reload: getList,
});
</script>

<style scoped>
.manager-body {
  min-height: 100%;
}

:deep(.knowledge-document-manager-drawer .el-drawer__body) {
  overflow-y: auto;
  scrollbar-width: none;
}

:deep(.knowledge-document-manager-drawer .el-drawer__body::-webkit-scrollbar) {
  display: none;
}

.manager-alert {
  margin-bottom: 16px;
}

.search-card {
  margin-bottom: 16px;
  border: none;
  box-shadow: none;
  height: 50px;
}

.toolbar-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.snapshot-descriptions {
  margin-bottom: 16px;
}

.snapshot-summary {
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
}

.snapshot-summary__label {
  display: inline-block;
  margin-bottom: 8px;
  color: #1f2d3d;
  font-weight: 600;
}

.snapshot-summary__content {
  color: #4f5b6b;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.snapshot-section {
  margin-bottom: 16px;
}

.snapshot-section__title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #1f2d3d;
  font-weight: 600;
}

.snapshot-section__tip {
  color: #909399;
  font-size: 12px;
  font-weight: 400;
}

.segment-card {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
}

.segment-title {
  margin-bottom: 8px;
  color: #1f2d3d;
  font-weight: 600;
}

.segment-content {
  color: #4f5b6b;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.search-card {
  margin-bottom: 15px;
  padding: 3px 0;
  border: none;
  box-shadow: none;
}

.search-card :deep(.el-card__body) {
  padding: 0;
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

.document-operation {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0 4px;
}

.document-operation :deep(.el-button.is-link) {
  color: var(--el-text-color-regular);
}

.document-operation :deep(.el-button.is-link:hover),
.document-operation :deep(.el-button.is-link:focus) {
  color: var(--el-text-color-primary);
}

.document-operation :deep(.el-button.is-disabled) {
  color: var(--el-text-color-placeholder);
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
.el-card__body {
  overflow: hidden;
}
</style>
