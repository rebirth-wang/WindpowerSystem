<template>
  <div class="ai-record-page">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item>
          <el-input
            v-model="queryParams.sessionTitle"
            :placeholder="t('ai.chatRecord.index.748794-1')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.userId"
            :placeholder="t('ai.chatRecord.index.748794-3')"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.chatMode"
            :placeholder="t('ai.chatRecord.index.748794-5')"
            clearable
            style="width: 220px"
          >
            <el-option :label="t('ai.chatRecord.index.748794-6')" value="AUTO" />
            <el-option :label="t('ai.chatRecord.index.748794-7')" value="GENERAL" />
            <el-option :label="t('ai.chatRecord.index.748794-8')" value="NL2SQL" />
            <el-option :label="t('ai.chatRecord.index.748794-9')" value="DEVICE_CONTROL" />
            <el-option :label="t('ai.chatRecord.index.748794-10')" value="PROTOCOL_PARSE" />
            <el-option :label="t('ai.chatRecord.index.748794-40')" value="THING_MODEL_GENERATE" />
            <el-option :label="t('ai.chatRecord.index.748794-41')" value="REQUIREMENT_EVALUATION" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="searchShow">
          <el-select
            v-model="queryParams.isArchived"
            :placeholder="t('ai.chatRecord.index.748794-12')"
            clearable
            style="width: 220px"
          >
            <el-option :label="t('ai.chatRecord.index.748794-13')" value="0" />
            <el-option :label="t('ai.chatRecord.index.748794-14')" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery" v-hasPermi="['ai:chatRecord:query']">
            {{ t('ai.chatRecord.index.748794-15') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ t('ai.chatRecord.index.748794-16') }}</el-button>
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
      <el-row :gutter="10" class="toolbar-row toolbar-row--right" align="middle">
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleBatchDelete"
            v-hasPermi="['ai:chatRecord:remove']"
          >
            {{ t('ai.chatRecord.index.748794-37') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="recordList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="t('ai.chatRecord.index.748794-0')" prop="sessionTitle" min-width="220" />
        <el-table-column :label="t('ai.chatRecord.index.748794-17')" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            {{ getSessionUserName(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.chatRecord.index.748794-18')" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.tenantName || '-' }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.chatRecord.index.748794-4')" prop="chatMode" width="160">
          <template #default="scope">
            {{ getModeLabel(scope.row.chatMode) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.chatRecord.index.748794-19')" prop="providerCode" min-width="120" />
        <el-table-column :label="t('ai.chatRecord.index.748794-20')" prop="modelCode" min-width="180" />
        <el-table-column :label="t('ai.chatRecord.index.748794-21')" prop="messageCount" width="100" />
        <el-table-column :label="t('ai.chatRecord.index.748794-11')" prop="isArchived" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.isArchived === '1' ? 'warning' : 'success'" effect="light">
              {{ getArchivedLabel(scope.row.isArchived) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.chatRecord.index.748794-22')" prop="lastMessageTime" min-width="180" />
        <el-table-column :label="t('ai.chatRecord.index.748794-23')" width="230" fixed="right" align="center">
          <template #default="scope">
            <el-button
              size="small"
              link
              :icon="View"
              @click="handleView(scope.row)"
              v-hasPermi="['ai:chatRecord:query']"
            >
              {{ t('ai.chatRecord.index.748794-24') }}
            </el-button>
            <el-button
              v-if="scope.row.isArchived === '1'"
              link
              size="small"
              :icon="Unlock"
              @click="handleUnarchive(scope.row)"
              v-hasPermi="['ai:chatRecord:remove']"
            >
              {{ t('ai.chatRecord.index.748794-25') }}
            </el-button>
            <el-button
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['ai:chatRecord:remove']"
            >
              {{ t('ai.chatRecord.index.748794-26') }}
            </el-button>
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
    </el-card>

    <el-dialog
      v-model="detailOpen"
      :title="t('ai.chatRecord.index.748794-27')"
      width="860px"
      append-to-body
      @opened="refreshDetailScrollButtonState"
      @closed="handleDetailDialogClosed"
    >
      <template v-if="detailForm">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-0')">
            {{ detailForm.session?.sessionTitle || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-28')">
            {{ detailForm.session?.sessionCode || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-17')">
            {{ getSessionUserName(detailForm.session) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-18')">
            {{ detailForm.session?.tenantName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-29')">
            {{ getModeLabel(detailForm.session?.chatMode) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-11')">
            {{ getArchivedLabel(detailForm.session?.isArchived) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.chatRecord.index.748794-30')">
            {{ detailForm.session?.modelCode || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-message-panel">
          <div ref="detailMessageListRef" class="detail-message-list" @scroll="handleDetailMessageListScroll">
            <div v-for="item in detailForm.messages || []" :key="item.messageId" class="detail-message-item">
              <div class="detail-message-head">
                <div class="detail-message-meta">
                  <span>{{ item.roleType === 'user' ? t('ai.chatRecord.index.748794-31') : 'AI' }}</span>
                  <el-tag v-if="item.abilityType" size="small" type="info" effect="plain">
                    {{ getModeLabel(item.abilityType) }}
                  </el-tag>
                </div>
                <span>{{ item.createTime || '' }}</span>
              </div>
              <div class="detail-message-content">{{ item.messageContent }}</div>
            </div>
          </div>
          <el-tooltip
            v-if="(detailForm.messages || []).length > 0 && showDetailScrollBottomButton"
            :content="t('ai.chatRecord.index.748794-42')"
            placement="top"
          >
            <el-button
              class="detail-scroll-bottom-button"
              circle
              :icon="Bottom"
              :aria-label="t('ai.chatRecord.index.748794-42')"
              @click="handleDetailScrollToBottom"
            />
          </el-tooltip>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Bottom, Delete, Refresh, Search, Unlock, View, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { getChatRecord, listChatRecord, removeChatRecord, unarchiveChatRecord } from '@/api/ai/chatRecord';
import { listUser } from '@/api/system/user';

const { t } = useI18n();

const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const recordList = ref([]);
const ids = ref([]);
const multiple = ref(true);
const multipleTableRef = ref(null);
const detailOpen = ref(false);
const detailForm = ref(null);
const detailMessageListRef = ref(null);
const showDetailScrollBottomButton = ref(false);
const userOptions = ref([]);
const searchShow = ref(false);

const DETAIL_BOTTOM_THRESHOLD = 48;

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  sessionTitle: '',
  userId: undefined,
  chatMode: '',
  isArchived: '',
});

function getModeLabel(mode) {
  const modeLabelMap = {
    AUTO: t('ai.chatRecord.index.748794-6'),
    PLATFORM_ASSISTANT: t('ai.chatRecord.index.748794-32'),
    GENERAL: t('ai.chatRecord.index.748794-7'),
    GENERAL_CHAT: t('ai.chatRecord.index.748794-7'),
    NL2SQL: t('ai.chatRecord.index.748794-8'),
    DEVICE_CONTROL: t('ai.chatRecord.index.748794-9'),
    PROTOCOL_PARSE: t('ai.chatRecord.index.748794-10'),
    THING_MODEL_GENERATE: t('ai.chatRecord.index.748794-40'),
    REQUIREMENT_EVALUATION: t('ai.chatRecord.index.748794-41'),
  };
  return modeLabelMap[mode] || mode || '-';
}

function getSessionUserName(session) {
  if (!session) {
    return '-';
  }
  if (session.createBy) {
    return session.createBy;
  }
  const matchedUser = userOptions.value.find((item) => item.value === session.userId);
  return matchedUser?.rawUserName || '-';
}

function getArchivedLabel(isArchived) {
  return isArchived === '1' ? t('ai.chatRecord.index.748794-14') : t('ai.chatRecord.index.748794-13');
}

function getRowKeys(row) {
  return row.sessionId;
}

async function getList() {
  loading.value = true;
  try {
    const response = await listChatRecord(queryParams.value);
    recordList.value = response.rows || [];
    total.value = response.total || 0;
  } finally {
    loading.value = false;
  }
}

async function getUserOptions() {
  try {
    const response = await listUser({
      pageNum: 1,
      pageSize: 999,
    });
    userOptions.value = (response.rows || []).map((item) => ({
      label: item.nickName ? `${item.userName}（${item.nickName}）` : item.userName,
      value: item.userId,
      rawUserName: item.userName,
    }));
  } catch (error) {
    userOptions.value = [];
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
    sessionTitle: '',
    userId: undefined,
    chatMode: '',
    isArchived: '',
  };
  getList();
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.sessionId);
  multiple.value = !selection.length;
}

async function handleView(row) {
  showDetailScrollBottomButton.value = false;
  const response = await getChatRecord(row.sessionId);
  detailForm.value = response.data || null;
  detailOpen.value = true;
  await refreshDetailScrollButtonState();
}

async function refreshDetailScrollButtonState() {
  await nextTick();
  handleDetailMessageListScroll();
}

function isDetailMessageListScrollable() {
  const container = detailMessageListRef.value;
  if (!container) {
    return false;
  }
  return container.scrollHeight > container.clientHeight + 8;
}

function isDetailMessageListNearBottom() {
  const container = detailMessageListRef.value;
  if (!container) {
    return true;
  }
  const distanceToBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
  return distanceToBottom <= DETAIL_BOTTOM_THRESHOLD;
}

function handleDetailMessageListScroll() {
  showDetailScrollBottomButton.value = isDetailMessageListScrollable() && !isDetailMessageListNearBottom();
}

async function handleDetailScrollToBottom() {
  await nextTick();
  const container = detailMessageListRef.value;
  if (!container || typeof container.scrollTo !== 'function') {
    return;
  }
  showDetailScrollBottomButton.value = false;
  container.scrollTo({
    top: container.scrollHeight,
    behavior: 'smooth',
  });
}

function resetDetailState() {
  detailForm.value = null;
  detailMessageListRef.value = null;
  showDetailScrollBottomButton.value = false;
}

function closeDetailDialog() {
  detailOpen.value = false;
  resetDetailState();
}

function handleDetailDialogClosed() {
  resetDetailState();
}

function clearSelection() {
  ids.value = [];
  multiple.value = true;
  multipleTableRef.value?.clearSelection();
}

async function deleteChatRecords(deleteIds, confirmMessage) {
  const targetIds = Array.isArray(deleteIds) ? deleteIds : [deleteIds];
  if (!targetIds.length) {
    ElMessage.warning(t('ai.chatRecord.index.748794-39'));
    return;
  }

  try {
    await ElMessageBox.confirm(confirmMessage, t('ai.chatRecord.index.748794-33'), {
      type: 'warning',
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancel'),
    });
  } catch {
    return;
  }
  await removeChatRecord(targetIds);
  ElMessage.success(t('ai.chatRecord.index.748794-36'));

  const currentSessionId = detailForm.value?.session?.sessionId;
  if (currentSessionId != null && targetIds.map((item) => String(item)).includes(String(currentSessionId))) {
    closeDetailDialog();
  }

  clearSelection();
  await getList();
}

async function handleUnarchive(row) {
  try {
    await ElMessageBox.confirm(
      t('ai.chatRecord.index.748794-43', [row.sessionTitle || row.sessionId]),
      t('ai.chatRecord.index.748794-33'),
      {
        type: 'warning',
        confirmButtonText: t('ai.chatRecord.index.748794-25'),
        cancelButtonText: t('cancel'),
      }
    );
  } catch {
    return;
  }
  await unarchiveChatRecord(row.sessionId);
  ElMessage.success(t('ai.chatRecord.index.748794-35'));
  if (detailForm.value?.session?.sessionId === row.sessionId) {
    detailForm.value.session.isArchived = '0';
  }
  await getList();
}

async function handleDelete(row) {
  await deleteChatRecords([row.sessionId], t('ai.chatRecord.index.748794-44', [row.sessionTitle || row.sessionId]));
}

async function handleBatchDelete() {
  if (!ids.value.length) {
    ElMessage.warning(t('ai.chatRecord.index.748794-39'));
    return;
  }
  await deleteChatRecords([...ids.value], t('ai.chatRecord.index.748794-38', [ids.value.length]));
}
function searchChange() {
  searchShow.value = !searchShow.value;
}

onMounted(() => {
  getList();
  getUserOptions();
});
</script>

<style scoped>
.ai-record-page {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
}

.toolbar-row {
  margin-bottom: 16px;
}

.detail-message-panel {
  position: relative;
  margin-top: 16px;
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

:deep(.el-card__body) {
  overflow: hidden;
}

.detail-message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: min(680px, 70vh);
  overflow-y: auto;
  padding-right: 8px;
  scroll-behavior: smooth;
}

.detail-message-item {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fc;
}

.detail-message-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #7f8a99;
  font-size: 12px;
  margin-bottom: 8px;
}

.detail-message-meta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.detail-message-content {
  line-height: 1.6;
  color: #243042;
  white-space: pre-wrap;
}

.detail-scroll-bottom-button {
  position: absolute;
  right: 18px;
  bottom: 18px;
  z-index: 2;
  width: 38px;
  height: 38px;
  border: 1px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 12px 26px rgba(37, 99, 235, 0.14);
}

.detail-scroll-bottom-button:hover,
.detail-scroll-bottom-button:focus {
  border-color: #c7d2fe;
  color: #2563eb;
  background: #ffffff;
}

.detail-scroll-bottom-button :deep(.el-icon) {
  font-size: 18px;
}
</style>
