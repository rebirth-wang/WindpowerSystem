<template>
  <div class="ai-knowledge-page">
    <el-card class="page-tool-card">
      <el-alert
        type="info"
        :closable="false"
        show-icon
        class="page-alert"
        style="margin-bottom: 10px"
        :title="t('ai.knowledge.index.748794-0')"
      />
      <div class="page-tool-row">
        <div class="page-tool-row__left">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['ai:knowledge:add']">
            {{ t('ai.knowledge.index.748794-1') }}
          </el-button>
          <el-button plain :icon="Refresh" @click="getList" v-hasPermi="['ai:knowledge:list']">
            {{ t('ai.knowledge.index.748794-2') }}
          </el-button>
        </div>
        <div class="page-tool-row__right">
          <div class="button-group">
            <el-tooltip effect="dark" :content="t('ai.knowledge.index.748794-3')" placement="top">
              <el-button
                size="small"
                :icon="Menu"
                @click="handleViewModeChange('card')"
                :class="['toggle-button card-button', { active: viewMode === 'card' }]"
              />
            </el-tooltip>
            <div class="separator"></div>
            <el-tooltip effect="dark" :content="t('ai.knowledge.index.748794-4')" placement="top">
              <el-button
                size="small"
                :icon="Grid"
                @click="handleViewModeChange('table')"
                :class="['toggle-button list-button', { active: viewMode === 'table' }]"
              />
            </el-tooltip>
          </div>
        </div>
      </div>

      <el-row v-show="viewMode === 'card'" :gutter="16" class="knowledge-card-row">
        <el-col v-for="item in knowledgeList" :key="item.knowledgeBaseId || item.kbType" :xs="24" :sm="24" :md="8">
          <el-card shadow="hover" class="knowledge-card">
            <div class="knowledge-card__header">
              <div>
                <div class="knowledge-card__title">{{ item.kbName || formatKbType(item.kbType) }}</div>
                <div class="knowledge-card__subtitle">
                  <dict-tag :options="knowledgeTypeOptions" :value="normalizeKbType(item.kbType)" />
                  <span class="knowledge-card__code">{{ item.kbCode || '-' }}</span>
                </div>
              </div>
              <el-switch
                v-model="item.status"
                :active-value="'0'"
                :inactive-value="'1'"
                :disabled="!canEditKnowledge"
                @change="handleStatusChange(item)"
              />
            </div>
            <div class="knowledge-card__description">
              {{ getKbDescription(item.kbType) }}
            </div>
            <div class="knowledge-card__meta">
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.knowledge.index.748794-5') }}</span>
                <dict-tag :options="vectorStoreOptions" :value="item.vectorStoreType" />
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.knowledge.index.748794-6') }}</span>
                <dict-tag :options="publishStatusOptions" :value="item.publishStatus || 'DRAFT'" />
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.knowledge.index.748794-7') }}</span>
                <span>{{ formatActiveVersion(item) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ resolveCurrentRuntimeLabel(item) }}</span>
                <template v-if="isNl2SqlKnowledge(item)">
                  <dict-tag :options="vectorStoreOptions" :value="item.activeVectorStoreType || ''" />
                </template>
                <span v-else>{{ resolveCurrentRuntimeText(item) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-item__label">{{ t('ai.knowledge.index.748794-8') }}</span>
                <span>{{ item.documentCount || 0 }}</span>
              </div>
            </div>
            <div class="knowledge-card__footer card-action-bar">
              <el-dropdown
                v-if="!isCodebaseKnowledge(item) && checkPermi(['ai:knowledge:query'])"
                class="card-action-item"
                trigger="click"
                @command="(command) => handleDownloadTemplateCommand(item, command)"
              >
                <el-button size="small" link>
                  {{ resolveDownloadTemplateLabel(item) }}
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="option in templateModeOptions"
                      :key="option.value"
                      :command="option.value"
                      :disabled="isEnterpriseTemplateMode(option.value) && !canDownloadEnterpriseTemplate"
                    >
                      {{ option.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button
                v-if="isCodebaseKnowledge(item)"
                class="card-action-item"
                size="small"
                link
                :loading="isRebuildingCodebase(item)"
                :disabled="!isAdminAccount()"
                @click="handleRebuildCodebase(item)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ t('ai.knowledge.index.748794-178') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                :disabled="isCodebaseKnowledge(item) && !isAdminAccount()"
                @click="handleUpload(item)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ resolveUploadActionLabel(item) }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleDocumentManager(item)"
                v-hasPermi="['ai:knowledge:query']"
              >
                {{ t('ai.knowledge.index.748794-10') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleVersion(item)"
                v-hasPermi="['ai:knowledge:query']"
              >
                {{ t('ai.knowledge.index.748794-11') }}
              </el-button>
              <el-button
                v-if="supportsRuntimeStatus(item)"
                class="card-action-item"
                size="small"
                link
                @click="handleRuntimeStatus(item)"
                v-hasPermi="['ai:knowledge:query']"
              >
                {{ t('ai.knowledge.index.748794-12') }}
              </el-button>
              <el-button
                class="card-action-item"
                size="small"
                link
                @click="handleUpdate(item)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ t('ai.knowledge.index.748794-13') }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- <el-card > -->
      <!-- <template #header>
          <div class="governance-card__header">
            <span>{{ t('ai.knowledge.index.748794-14') }}</span>
            <el-button link @click="getList" v-hasPermi="['ai:knowledge:list']">
              {{ t('ai.knowledge.index.748794-2') }}
            </el-button>
          </div>
        </template> -->
      <el-table
        v-show="viewMode === 'table'"
        class="governance-card"
        v-loading="loading"
        :data="knowledgeList"
        :border="false"
      >
        <el-table-column :label="t('ai.knowledge.index.748794-15')" prop="kbName" min-width="160" />
        <el-table-column :label="t('ai.knowledge.index.748794-16')" min-width="140">
          <template #default="{ row }">
            <dict-tag :options="knowledgeTypeOptions" :value="normalizeKbType(row.kbType)" />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-17')" min-width="140">
          <template #default="{ row }">
            <dict-tag :options="vectorStoreOptions" :value="row.vectorStoreType" />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-7')" min-width="150">
          <template #default="{ row }">
            {{ formatActiveVersion(row) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-18')" min-width="140">
          <template #default="{ row }">
            <template v-if="isNl2SqlKnowledge(row)">
              <dict-tag :options="vectorStoreOptions" :value="row.activeVectorStoreType || ''" />
            </template>
            <span v-else>{{ resolveCurrentRuntimeText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-6')" min-width="120">
          <template #default="{ row }">
            <dict-tag :options="publishStatusOptions" :value="row.publishStatus || 'DRAFT'" />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-19')" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="'0'"
              :inactive-value="'1'"
              :disabled="!canEditKnowledge"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.knowledge.index.748794-8')" prop="documentCount" width="90" />
        <el-table-column :label="t('ai.knowledge.index.748794-20')" prop="updateTime" min-width="160" />
        <el-table-column
          :label="t('ai.knowledge.index.748794-21')"
          prop="remark"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column :label="t('ai.knowledge.index.748794-22')" align="center" min-width="520" fixed="right">
          <template #default="{ row }">
            <div class="knowledge-operation-cell">
              <el-dropdown
                v-if="!isCodebaseKnowledge(row) && checkPermi(['ai:knowledge:query'])"
                trigger="click"
                @command="(command) => handleDownloadTemplateCommand(row, command)"
              >
                <el-button link size="small">
                  {{ resolveDownloadTemplateLabel(row) }}
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="option in templateModeOptions"
                      :key="option.value"
                      :command="option.value"
                      :disabled="isEnterpriseTemplateMode(option.value) && !canDownloadEnterpriseTemplate"
                    >
                      {{ option.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button
                v-if="isCodebaseKnowledge(row)"
                link
                size="small"
                :loading="isRebuildingCodebase(row)"
                :disabled="!isAdminAccount()"
                @click="handleRebuildCodebase(row)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ t('ai.knowledge.index.748794-178') }}
              </el-button>
              <el-button
                link
                size="small"
                :disabled="isCodebaseKnowledge(row) && !isAdminAccount()"
                @click="handleUpload(row)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ resolveUploadActionLabel(row) }}
              </el-button>
              <el-button link size="small" @click="handleDocumentManager(row)" v-hasPermi="['ai:knowledge:query']">
                {{ t('ai.knowledge.index.748794-10') }}
              </el-button>
              <el-button link size="small" @click="handleVersion(row)" v-hasPermi="['ai:knowledge:query']">
                {{ t('ai.knowledge.index.748794-11') }}
              </el-button>
              <el-button
                v-if="supportsRuntimeStatus(row)"
                link
                size="small"
                @click="handleRuntimeStatus(row)"
                v-hasPermi="['ai:knowledge:query']"
              >
                {{ t('ai.knowledge.index.748794-12') }}
              </el-button>
              <el-button link size="small" @click="handleUpdate(row)" v-hasPermi="['ai:knowledge:edit']">
                {{ t('ai.knowledge.index.748794-13') }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="open" :title="dialogTitle" width="600px" append-to-body>
      <el-form ref="formRef" class="ai-dialog-form" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="t('ai.knowledge.index.748794-23')" prop="kbCode">
          <el-input v-model="form.kbCode" :disabled="isEditMode" :placeholder="t('ai.knowledge.index.748794-24')" />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-16')" prop="kbType">
          <el-select
            v-model="form.kbType"
            :disabled="isEditMode"
            :placeholder="t('ai.knowledge.index.748794-25')"
            style="width: 100%"
          >
            <el-option v-for="item in knowledgeTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-15')" prop="kbName">
          <el-input v-model="form.kbName" :placeholder="t('ai.knowledge.index.748794-26')" />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-17')" prop="vectorStoreType">
          <el-select
            v-model="form.vectorStoreType"
            :placeholder="t('ai.knowledge.index.748794-27')"
            style="width: 100%"
          >
            <el-option v-for="item in vectorStoreOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-19')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in knowledgeStatusOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-28')">
          <div class="extra-config-editor">
            <div v-for="(item, index) in extraConfigItems" :key="`extra-${index}`" class="extra-config-row">
              <el-input v-model="item.configKey" :placeholder="t('ai.knowledge.index.748794-29')" />
              <el-input v-model="item.configValue" :placeholder="t('ai.knowledge.index.748794-30')" />
              <el-button
                type="danger"
                link
                :disabled="extraConfigItems.length === 1"
                @click="removeExtraConfigItem(index)"
              >
                <el-icon><Delete /></el-icon>
                <span>{{ t('ai.knowledge.index.748794-31') }}</span>
              </el-button>
            </div>
            <el-button link class="extra-config-add" @click="addExtraConfigItem">
              <el-icon><Plus /></el-icon>
              <span>{{ t('ai.knowledge.index.748794-32') }}</span>
            </el-button>
          </div>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-21')">
          <el-input v-model="form.remark" type="textarea" :rows="3" :placeholder="t('ai.knowledge.index.748794-33')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm" v-if="isEditMode" v-hasPermi="['ai:knowledge:edit']">
          {{ t('ai.knowledge.index.748794-34') }}
        </el-button>
        <el-button type="primary" @click="submitForm" v-else v-hasPermi="['ai:knowledge:add']">
          {{ t('ai.knowledge.index.748794-34') }}
        </el-button>
        <el-button @click="open = false">{{ t('ai.knowledge.index.748794-35') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="uploadOpen" :title="t('ai.knowledge.index.748794-36')" width="600px" append-to-body>
      <el-alert type="info" :closable="false" show-icon class="upload-alert" :title="uploadDialogTip" />
      <el-form ref="uploadFormRef" class="ai-dialog-form" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item :label="t('ai.knowledge.index.748794-16')">
          <el-input :model-value="formatKbType(currentKnowledge?.kbType)" disabled />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-37')" prop="sourceOrigin">
          <el-select
            v-model="uploadForm.sourceOrigin"
            :placeholder="t('ai.knowledge.index.748794-38')"
            style="width: 100%"
          >
            <el-option v-for="item in sourceOriginOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <div class="upload-form-tip">{{ t('ai.knowledge.index.748794-39') }}</div>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-40')">
          <el-input v-model="uploadForm.appVersion" :placeholder="t('ai.knowledge.index.748794-41')" clearable />
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-42')">
          <el-input-number v-model="uploadForm.sortNum" :min="0" controls-position="right" style="width: 100%" />
          <div class="upload-form-tip">{{ uploadSortTip }}</div>
        </el-form-item>
        <el-form-item :label="t('ai.knowledge.index.748794-43')" required>
          <el-upload
            ref="uploadRef"
            action="#"
            drag
            :auto-upload="false"
            :limit="1"
            :accept="uploadAccept"
            :file-list="uploadFileList"
            :on-change="handleUploadFileChange"
            :on-remove="handleUploadFileRemove"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              {{ t('ai.knowledge.index.748794-44') }}
              <em>{{ t('ai.knowledge.index.748794-45') }}</em>
            </div>
            <template #tip>
              <div class="upload-tip">{{ uploadFileTip }}</div>
            </template>
          </el-upload>
          <el-dropdown
            v-if="!isCodebaseKnowledge(currentKnowledge)"
            trigger="click"
            class="download-link"
            @command="(command) => handleDownloadTemplateCommand(currentKnowledge, command)"
          >
            <el-link type="primary" underline="never">
              <el-icon><Download /></el-icon>
              {{ resolveDownloadTemplateLabel(currentKnowledge, true) }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-link>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="option in templateModeOptions" :key="option.value" :command="option.value">
                  {{ option.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="uploading" @click="submitUpload" v-hasPermi="['ai:knowledge:edit']">
          {{ t('ai.knowledge.index.748794-47') }}
        </el-button>
        <el-button @click="uploadOpen = false">{{ t('ai.knowledge.index.748794-35') }}</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="versionOpen"
      :title="t('ai.knowledge.index.748794-48')"
      size="86%"
      direction="rtl"
      append-to-body
      destroy-on-close
    >
      <div
        v-loading="versionOperationLocked"
        class="version-manager-body"
        :element-loading-text="versionOperationLoadingText"
        element-loading-background="rgba(255, 255, 255, 0.7)"
      >
        <el-alert
          type="warning"
          :closable="false"
          show-icon
          class="upload-alert"
          :title="t('ai.knowledge.index.748794-179', [currentKnowledge?.kbName || '-'])"
        />
        <div class="version-toolbar">
          <el-button
            type="primary"
            :disabled="versionOperationLocked || !canOperateKnowledgeVersion"
            @click="handleBuildVersion"
            v-hasPermi="['ai:knowledge:edit']"
          >
            {{ t('ai.knowledge.index.748794-49') }}
          </el-button>
          <el-button
            type="primary"
            plain
            :disabled="versionOperationLocked || !canOperateKnowledgeVersion"
            @click="getVersionList"
            v-hasPermi="['ai:knowledge:query']"
          >
            {{ t('ai.knowledge.index.748794-50') }}
          </el-button>
        </div>
        <el-table v-loading="versionLoading" :data="versionList">
          <el-table-column :label="t('ai.knowledge.index.748794-51')" prop="versionNo" min-width="170" />
          <el-table-column :label="t('ai.knowledge.index.748794-5')" min-width="120">
            <template #default="{ row }">
              <dict-tag :options="vectorStoreOptions" :value="row.vectorStoreType" />
            </template>
          </el-table-column>
          <el-table-column :label="t('ai.knowledge.index.748794-6')" min-width="120">
            <template #default="{ row }">
              <dict-tag :options="publishStatusOptions" :value="row.publishStatus" />
            </template>
          </el-table-column>
          <el-table-column :label="t('ai.knowledge.index.748794-52')" width="90">
            <template #default="{ row }">
              <el-tag :type="row.isActive === '1' ? 'success' : 'info'" size="small">
                {{ row.isActive === '1' ? t('ai.knowledge.index.748794-53') : t('ai.knowledge.index.748794-54') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="t('ai.knowledge.index.748794-55')" prop="sourceFileCount" width="90" />
          <el-table-column :label="t('ai.knowledge.index.748794-56')" prop="mergedItemCount" width="90" />
          <el-table-column :label="t('ai.knowledge.index.748794-57')" prop="overrideCount" width="90" />
          <el-table-column :label="t('ai.knowledge.index.748794-58')" prop="conflictCount" width="90" />
          <el-table-column
            :label="t('ai.knowledge.index.748794-59')"
            prop="rollbackFromVersion"
            min-width="140"
            show-overflow-tooltip
          />
          <el-table-column :label="t('ai.knowledge.index.748794-60')" prop="publishTime" min-width="160" />
          <el-table-column
            :label="t('ai.knowledge.index.748794-61')"
            prop="buildSummary"
            min-width="220"
            show-overflow-tooltip
          />
          <el-table-column :label="t('ai.knowledge.index.748794-22')" width="300" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="supportsQualityCheck(row)"
                type="primary"
                link
                :loading="isCheckingVersion(row)"
                :disabled="!canOperateKnowledgeVersion || (versionOperationLocked && !isCheckingVersion(row))"
                @click="handleQualityCheck(row)"
                v-hasPermi="['ai:knowledge:query']"
              >
                {{ isCheckingVersion(row) ? t('ai.knowledge.index.748794-62') : t('ai.knowledge.index.748794-63') }}
              </el-button>
              <el-button
                v-if="showPublishButton(row)"
                type="primary"
                link
                :loading="isPublishingVersion(row)"
                :disabled="!canOperateKnowledgeVersion || (versionOperationLocked && !isPublishingVersion(row))"
                @click="handlePublishVersion(row)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ isPublishingVersion(row) ? t('ai.knowledge.index.748794-64') : t('ai.knowledge.index.748794-65') }}
              </el-button>
              <el-button
                v-if="showRollbackButton(row)"
                type="warning"
                link
                :loading="isRollingBackVersion(row)"
                :disabled="!canOperateKnowledgeVersion || (versionOperationLocked && !isRollingBackVersion(row))"
                @click="handleRollbackVersion(row)"
                v-hasPermi="['ai:knowledge:edit']"
              >
                {{ isRollingBackVersion(row) ? t('ai.knowledge.index.748794-66') : t('ai.knowledge.index.748794-67') }}
              </el-button>
              <el-button
                v-if="row.isActive !== '1'"
                type="danger"
                link
                :loading="isDeletingVersion(row)"
                :disabled="!canOperateKnowledgeVersion || (versionOperationLocked && !isDeletingVersion(row))"
                @click="handleDeleteVersion(row)"
                v-hasPermi="['ai:knowledge:remove']"
              >
                {{ isDeletingVersion(row) ? t('ai.knowledge.index.748794-68') : t('ai.knowledge.index.748794-31') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>

    <el-dialog
      v-model="qualityCheckOpen"
      :title="t('ai.knowledge.index.748794-69')"
      width="960px"
      append-to-body
      destroy-on-close
    >
      <el-skeleton v-if="qualityCheckLoading" :rows="10" animated />
      <template v-else-if="qualityCheckResult">
        <el-alert
          :type="qualityCheckAlertType"
          :closable="false"
          show-icon
          class="upload-alert"
          :title="qualityCheckResult.summary || t('ai.knowledge.index.748794-70')"
        />
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-71')">
            {{ currentKnowledge?.kbName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-51')">
            {{ qualityCheckResult.versionNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-72')">
            {{ qualityCheckResult.checkedQuestionCount ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-73')">
            {{ qualityCheckResult.p0QuestionCount ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-74')">
            <el-tag :type="(qualityCheckResult.blockingIssueCount || 0) > 0 ? 'danger' : 'success'">
              {{ qualityCheckResult.blockingIssueCount ?? 0 }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-75')">
            <el-tag :type="(qualityCheckResult.warningIssueCount || 0) > 0 ? 'warning' : 'success'">
              {{ qualityCheckResult.warningIssueCount ?? 0 }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-76')">
            {{ qualityCheckResult.checkedTime || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="quality-check-section">
          <div class="runtime-section__title">{{ t('ai.knowledge.index.748794-77') }}</div>
          <el-empty v-if="!qualityCheckIssues.length" :description="t('ai.knowledge.index.748794-78')" />
          <el-table v-else :data="qualityCheckIssues" border size="small">
            <el-table-column :label="t('ai.knowledge.index.748794-79')" width="90">
              <template #default="{ row }">
                {{ row.rowNum || '-' }}
              </template>
            </el-table-column>
            <el-table-column :label="t('ai.knowledge.index.748794-80')" width="100">
              <template #default="{ row }">
                <el-tag :type="row.level === 'WARNING' ? 'warning' : 'danger'" size="small">
                  {{ row.level === 'WARNING' ? t('ai.knowledge.index.748794-81') : t('ai.knowledge.index.748794-82') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              :label="t('ai.knowledge.index.748794-83')"
              prop="question"
              min-width="260"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                {{ row.question || '-' }}
              </template>
            </el-table-column>
            <el-table-column
              :label="t('ai.knowledge.index.748794-84')"
              prop="message"
              min-width="420"
              show-overflow-tooltip
            />
          </el-table>
        </div>
      </template>
      <template #footer>
        <el-button @click="qualityCheckOpen = false">{{ t('ai.knowledge.index.748794-85') }}</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="runtimeOpen"
      :title="t('ai.knowledge.index.748794-86')"
      size="720px"
      direction="rtl"
      append-to-body
      destroy-on-close
    >
      <el-alert
        v-if="runtimeStatus"
        :type="runtimeStatus.consistent ? 'success' : 'warning'"
        :closable="false"
        show-icon
        class="upload-alert"
        :title="runtimeStatus.summary || t('ai.knowledge.index.748794-87')"
      />
      <div class="runtime-toolbar">
        <el-button type="primary" plain @click="loadRuntimeStatus" v-hasPermi="['ai:knowledge:query']">
          {{ t('ai.knowledge.index.748794-88') }}
        </el-button>
        <el-button
          v-if="showRuntimeRebuildButton"
          type="primary"
          :loading="runtimeSyncing"
          @click="handleRebuildRuntime"
          v-hasPermi="['ai:knowledge:edit']"
        >
          {{ t('ai.knowledge.index.748794-89') }}
        </el-button>
      </div>
      <el-skeleton v-if="runtimeLoading" :rows="8" animated />
      <template v-else-if="runtimeStatus">
        <el-descriptions :column="2" border class="runtime-descriptions">
          <el-descriptions-item :label="t('ai.knowledge.index.748794-15')">
            {{ runtimeStatus.kbName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-23')">
            {{ runtimeStatus.kbCode || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-90')">
            <dict-tag :options="knowledgeStatusOptions" :value="runtimeStatus.knowledgeStatus" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-6')">
            <dict-tag :options="publishStatusOptions" :value="runtimeStatus.publishStatus || 'DRAFT'" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-7')">
            {{ runtimeStatus.activeVersionNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-91')">
            {{ runtimeStatus.runtimeTargetName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimeVersionLabel">
            {{ runtimeStatus.runtimeVersionNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimePrimaryModeLabel">
            {{ runtimeStatus.expectedStoreType || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimeSecondaryModeLabel">
            {{ runtimeStatus.readerStoreType || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-92')">
            {{ runtimeStatus.sourceFileCount ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimeExpectedCountLabel">
            {{ runtimeStatus.expectedItemCount ?? runtimeStatus.expectedFieldCount ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimeActualCountLabel">
            {{ runtimeStatus.runtimeItemCount ?? runtimeStatus.runtimeFieldCount ?? 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimePublishTimeLabel">
            {{ runtimeStatus.runtimePublishTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="runtimePublishedByLabel">
            {{ runtimeStatus.runtimePublishedBy || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('ai.knowledge.index.748794-61')" :span="2">
            {{ runtimeStatus.buildSummary || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="runtime-summary-grid">
          <div class="runtime-summary-card">
            <div class="runtime-summary-card__label">{{ runtimeLoadedCardLabel }}</div>
            <el-tag :type="runtimeStatus.runtimeLoaded ? 'success' : 'info'">
              {{ runtimeStatus.runtimeLoaded ? runtimeLoadedSuccessText : runtimeLoadedFailedText }}
            </el-tag>
          </div>
          <div v-if="showRuntimeStoreCheckCard" class="runtime-summary-card">
            <div class="runtime-summary-card__label">{{ t('ai.knowledge.index.748794-93') }}</div>
            <el-tag :type="runtimeStatus.readerStoreMatched ? 'success' : 'danger'">
              {{
                runtimeStatus.readerStoreMatched ? t('ai.knowledge.index.748794-94') : t('ai.knowledge.index.748794-95')
              }}
            </el-tag>
          </div>
          <div class="runtime-summary-card">
            <div class="runtime-summary-card__label">{{ runtimeImplementationCardLabel }}</div>
            <el-tag :type="runtimeStatus.runtimeStoreImplemented ? 'success' : 'danger'">
              {{
                runtimeStatus.runtimeStoreImplemented
                  ? t('ai.knowledge.index.748794-96')
                  : t('ai.knowledge.index.748794-97')
              }}
            </el-tag>
          </div>
          <div class="runtime-summary-card">
            <div class="runtime-summary-card__label">{{ t('ai.knowledge.index.748794-98') }}</div>
            <el-tag :type="runtimeStatus.consistent ? 'success' : 'warning'">
              {{ runtimeStatus.consistent ? t('ai.knowledge.index.748794-94') : t('ai.knowledge.index.748794-99') }}
            </el-tag>
          </div>
        </div>

        <div class="runtime-section">
          <div class="runtime-section__title">{{ t('ai.knowledge.index.748794-100') }}</div>
          <el-empty v-if="!runtimeIssues.length" :description="t('ai.knowledge.index.748794-101')" />
          <ul v-else class="runtime-issue-list">
            <li v-for="(item, index) in runtimeIssues" :key="`runtime-issue-${index}`">
              {{ item }}
            </li>
          </ul>
        </div>
      </template>
    </el-drawer>

    <KnowledgeDocumentManager
      ref="documentManagerRef"
      v-model="documentManagerOpen"
      :knowledge-base="currentKnowledge"
      @upload="handleUpload"
      @updated="handleDocumentUpdated"
    />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowDown, Download, Grid, Menu, Plus, Refresh, Upload, Delete } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
import { checkPermi, isAdminAccount } from '@/utils/permission';
import KnowledgeDocumentManager from './components/KnowledgeDocumentManager.vue';
import {
  addKnowledge,
  changeKnowledgeStatus,
  getKnowledge,
  listKnowledge,
  rebuildCodebaseGuide,
  updateKnowledge,
  uploadCodebaseGuideSnapshot,
  uploadKnowledgeTemplate,
} from '@/api/ai/knowledge';
import {
  buildKnowledgeVersion,
  listKnowledgeVersion,
  publishKnowledgeVersion,
  previewKnowledgeVersionQuality,
  removeKnowledgeVersion,
  rollbackKnowledgeVersion,
} from '@/api/ai/knowledgeVersion';
import { getKnowledgeRuntimeStatus, rebuildKnowledgeRuntime } from '@/api/ai/knowledgeRuntime';

const { t } = useI18n();

const { proxy } = getCurrentInstance();
const { dict } = useDict(
  'ai_knowledge_type',
  'ai_knowledge_vector_store_type',
  'ai_knowledge_publish_status',
  'ai_knowledge_status',
  'ai_knowledge_source_origin'
);
const KNOWLEDGE_VIEW_MODE_KEY = 'fastbee.ai.knowledge.viewMode';
const TEMPLATE_MODE_EMPTY = 'EMPTY';
const TEMPLATE_MODE_ENTERPRISE_EXPORT = 'ENTERPRISE_EXPORT';
const SOURCE_STRATEGY_AUTO = 'AUTO';
const templateModeOptions = [
  { label: t('ai.knowledge.index.748794-102'), value: TEMPLATE_MODE_EMPTY },
  { label: t('ai.knowledge.index.748794-103'), value: TEMPLATE_MODE_ENTERPRISE_EXPORT },
];

const fallbackKnowledgeTypeOptions = [
  { label: t('ai.knowledge.index.748794-104'), value: 'NL2SQL_SEMANTIC' },
  { label: t('ai.knowledge.index.748794-105'), value: 'PROTOCOL_SPEC' },
  { label: t('ai.knowledge.index.748794-106'), value: 'PLATFORM_DOC' },
  { label: t('ai.knowledge.index.748794-180'), value: 'CODEBASE_GUIDE' },
];
const fallbackVectorStoreOptions = [
  { label: 'Redis', value: 'REDIS' },
  { label: 'Redis Stack', value: 'REDIS_STACK' },
  { label: 'Milvus', value: 'MILVUS' },
  { label: 'PGVector', value: 'PGVECTOR' },
  { label: 'Elasticsearch', value: 'ELASTICSEARCH' },
  { label: 'Qdrant', value: 'QDRANT' },
  { label: 'Memory', value: 'MEMORY' },
];
const fallbackPublishStatusOptions = [
  { label: t('ai.knowledge.index.748794-107'), value: 'DRAFT' },
  { label: t('ai.knowledge.index.748794-108'), value: 'PUBLISHED' },
  { label: t('ai.knowledge.index.748794-109'), value: 'ARCHIVED' },
];
const fallbackKnowledgeStatusOptions = [
  { label: t('ai.knowledge.index.748794-110'), value: '0' },
  { label: t('ai.knowledge.index.748794-111'), value: '1' },
];
const fallbackSourceOriginOptions = [
  { label: t('ai.knowledge.index.748794-112'), value: 'OFFICIAL' },
  { label: t('ai.knowledge.index.748794-113'), value: 'CUSTOM' },
];

const loading = ref(false);
const versionLoading = ref(false);
const open = ref(false);
const uploadOpen = ref(false);
const versionOpen = ref(false);
const runtimeOpen = ref(false);
const qualityCheckOpen = ref(false);
const documentManagerOpen = ref(false);
const uploading = ref(false);
const runtimeLoading = ref(false);
const runtimeSyncing = ref(false);
const qualityCheckLoading = ref(false);
const rebuildingCodebaseId = ref(undefined);
const checkingVersionId = ref(undefined);
const publishingVersionId = ref(undefined);
const rollbackingVersionId = ref(undefined);
const deletingVersionId = ref(undefined);
const knowledgeList = ref([]);
const versionList = ref([]);
const formRef = ref();
const uploadFormRef = ref();
const uploadRef = ref();
const documentManagerRef = ref();
const currentKnowledge = ref(null);
const runtimeStatus = ref(null);
const qualityCheckResult = ref(null);
const uploadFile = ref(null);
const uploadFileList = ref([]);
const viewMode = ref('card');
const extraConfigItems = ref([createExtraConfigItem()]);

const form = ref(createDefaultForm());
const uploadForm = ref(createDefaultUploadForm());
const rules = {
  kbCode: [{ required: true, message: t('ai.knowledge.index.748794-24'), trigger: 'blur' }],
  kbType: [{ required: true, message: t('ai.knowledge.index.748794-25'), trigger: 'change' }],
  kbName: [{ required: true, message: t('ai.knowledge.index.748794-26'), trigger: 'blur' }],
  vectorStoreType: [{ required: true, message: t('ai.knowledge.index.748794-27'), trigger: 'change' }],
};
const uploadRules = {
  sourceOrigin: [{ required: true, message: t('ai.knowledge.index.748794-38'), trigger: 'change' }],
};

const versionQueryParams = ref({
  pageNum: 1,
  pageSize: 100,
  knowledgeBaseId: undefined,
});

const knowledgeTypeOptions = computed(() => {
  return dict.type.ai_knowledge_type?.length ? dict.type.ai_knowledge_type : fallbackKnowledgeTypeOptions;
});
const vectorStoreOptions = computed(() => {
  return dict.type.ai_knowledge_vector_store_type?.length
    ? dict.type.ai_knowledge_vector_store_type
    : fallbackVectorStoreOptions;
});
const publishStatusOptions = computed(() => {
  return dict.type.ai_knowledge_publish_status?.length
    ? dict.type.ai_knowledge_publish_status
    : fallbackPublishStatusOptions;
});
const knowledgeStatusOptions = computed(() => {
  return dict.type.ai_knowledge_status?.length ? dict.type.ai_knowledge_status : fallbackKnowledgeStatusOptions;
});
const sourceOriginOptions = computed(() => {
  return dict.type.ai_knowledge_source_origin?.length
    ? dict.type.ai_knowledge_source_origin
    : fallbackSourceOriginOptions;
});
const canEditKnowledge = computed(() => checkPermi(['ai:knowledge:edit']));
const canDownloadEnterpriseTemplate = computed(() => isAdminAccount());
const canOperateKnowledgeVersion = computed(() => isAdminAccount());
const isEditMode = computed(() => !!form.value.knowledgeBaseId);
const dialogTitle = computed(() =>
  isEditMode.value ? t('ai.knowledge.index.748794-114') : t('ai.knowledge.index.748794-1')
);
const runtimeIssues = computed(() => runtimeStatus.value?.issues || []);
const qualityCheckIssues = computed(() => qualityCheckResult.value?.issues || []);
const qualityCheckAlertType = computed(() => {
  if ((qualityCheckResult.value?.blockingIssueCount || 0) > 0) {
    return 'warning';
  }
  if ((qualityCheckResult.value?.warningIssueCount || 0) > 0) {
    return 'warning';
  }
  return 'success';
});
const currentRuntimeScene = computed(() => runtimeStatus.value?.runtimeScene || '');
const showRuntimeStoreCheckCard = computed(() => runtimeStatus.value?.storeCheckRequired === true);
const showRuntimeRebuildButton = computed(() => runtimeStatus.value?.rebuildSupported === true);
const runtimeVersionLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-115')
    : t('ai.knowledge.index.748794-116');
});
const runtimePrimaryModeLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-117')
    : t('ai.knowledge.index.748794-118');
});
const runtimeSecondaryModeLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-119')
    : t('ai.knowledge.index.748794-120');
});
const runtimeExpectedCountLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-121')
    : t('ai.knowledge.index.748794-122');
});
const runtimeActualCountLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-123')
    : t('ai.knowledge.index.748794-124');
});
const runtimePublishTimeLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-125')
    : t('ai.knowledge.index.748794-126');
});
const runtimePublishedByLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-127')
    : t('ai.knowledge.index.748794-128');
});
const runtimeLoadedCardLabel = computed(() => {
  if (currentRuntimeScene.value === 'NL2SQL_RUNTIME') {
    return 'active bundle';
  }
  return t('ai.knowledge.index.748794-129');
});
const runtimeLoadedSuccessText = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-130')
    : t('ai.knowledge.index.748794-131');
});
const runtimeLoadedFailedText = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-132')
    : t('ai.knowledge.index.748794-133');
});
const runtimeImplementationCardLabel = computed(() => {
  return currentRuntimeScene.value === 'NL2SQL_RUNTIME'
    ? t('ai.knowledge.index.748794-134')
    : t('ai.knowledge.index.748794-135');
});
const versionOperationLocked = computed(() => {
  return (
    checkingVersionId.value !== undefined ||
    publishingVersionId.value !== undefined ||
    rollbackingVersionId.value !== undefined ||
    deletingVersionId.value !== undefined
  );
});
const versionOperationLoadingText = computed(() => {
  if (checkingVersionId.value !== undefined) {
    return t('ai.knowledge.index.748794-136');
  }
  if (publishingVersionId.value !== undefined) {
    return t('ai.knowledge.index.748794-137');
  }
  if (rollbackingVersionId.value !== undefined) {
    return t('ai.knowledge.index.748794-138');
  }
  if (deletingVersionId.value !== undefined) {
    return t('ai.knowledge.index.748794-139');
  }
  return '';
});
const uploadSortTip = computed(() => {
  return uploadForm.value.sourceOrigin === 'OFFICIAL'
    ? t('ai.knowledge.index.748794-140')
    : t('ai.knowledge.index.748794-141');
});
const uploadDialogTip = computed(() => {
  if (isCodebaseKnowledge(currentKnowledge.value)) {
    return t('ai.knowledge.index.748794-181', [currentKnowledge.value?.kbName || '-']);
  }
  return t('ai.knowledge.index.748794-182', [currentKnowledge.value?.kbName || '-']);
});
const uploadAccept = computed(() => (isCodebaseKnowledge(currentKnowledge.value) ? '.json' : '.xls,.xlsx'));
const uploadFileTip = computed(() =>
  isCodebaseKnowledge(currentKnowledge.value) ? t('ai.knowledge.index.748794-183') : t('ai.knowledge.index.748794-46')
);

function createDefaultForm() {
  return {
    knowledgeBaseId: undefined,
    kbCode: '',
    kbName: '',
    kbType: '',
    vectorStoreType: 'REDIS',
    status: '1',
    extraConfig: '',
    remark: '',
  };
}

function createDefaultUploadForm() {
  return {
    sourceOrigin: 'CUSTOM',
    appVersion: '',
    sortNum: undefined,
  };
}

function toNumberOrUndefined(value) {
  if (value === '' || value === null || value === undefined) return undefined;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? undefined : numberValue;
}

function createExtraConfigItem(configKey = '', configValue = '') {
  return {
    configKey,
    configValue,
  };
}

function normalizeKbType(kbType) {
  const actualType = String(kbType || '')
    .trim()
    .toUpperCase();
  if (actualType === 'NL2SQL') {
    return 'NL2SQL_SEMANTIC';
  }
  if (actualType === 'PROTOCOL') {
    return 'PROTOCOL_SPEC';
  }
  if (['GENERAL', 'PLATFORM', 'PLATFORM_KNOWLEDGE'].includes(actualType)) {
    return 'PLATFORM_DOC';
  }
  if (['CODEBASE', 'CODEBASE_NAV', 'CODEBASE_GUIDE'].includes(actualType)) {
    return 'CODEBASE_GUIDE';
  }
  return actualType || 'PLATFORM_DOC';
}

function formatKbType(kbType) {
  return (
    knowledgeTypeOptions.value.find((item) => item.value === normalizeKbType(kbType))?.label || normalizeKbType(kbType)
  );
}

function formatDictLabel(options, value) {
  return options?.find((item) => item.value === value)?.label || value || '-';
}

function formatActiveVersion(row) {
  if (!row) {
    return '-';
  }
  return row.activeVersionNo || (row.activeVersionId ? `ID-${row.activeVersionId}` : '-');
}

function getKbDescription(kbType) {
  const actualKbType = normalizeKbType(kbType);
  if (actualKbType === 'NL2SQL_SEMANTIC') {
    return t('ai.knowledge.index.748794-142');
  }
  if (actualKbType === 'PROTOCOL_SPEC') {
    return t('ai.knowledge.index.748794-143');
  }
  if (actualKbType === 'CODEBASE_GUIDE') {
    return t('ai.knowledge.index.748794-184');
  }
  return t('ai.knowledge.index.748794-144');
}

function isNl2SqlKnowledge(row) {
  return normalizeKbType(row?.kbType) === 'NL2SQL_SEMANTIC';
}

function isProtocolKnowledge(row) {
  return normalizeKbType(row?.kbType) === 'PROTOCOL_SPEC';
}

function isPlatformKnowledge(row) {
  return normalizeKbType(row?.kbType) === 'PLATFORM_DOC';
}

function isCodebaseKnowledge(row) {
  return normalizeKbType(row?.kbType) === 'CODEBASE_GUIDE';
}

function supportsRuntimeStatus(row) {
  const actualKbType = normalizeKbType(row?.kbType);
  return ['NL2SQL_SEMANTIC', 'PROTOCOL_SPEC', 'PLATFORM_DOC', 'CODEBASE_GUIDE'].includes(actualKbType);
}

function resolveCurrentRuntimeLabel(row) {
  if (isNl2SqlKnowledge(row)) {
    return t('ai.knowledge.index.748794-145');
  }
  return t('ai.knowledge.index.748794-146');
}

function resolveCurrentRuntimeText(row) {
  if (isProtocolKnowledge(row)) {
    return row?.activeVersionId ? t('ai.knowledge.index.748794-147') : '-';
  }
  if (isPlatformKnowledge(row)) {
    return row?.activeVersionId ? t('ai.knowledge.index.748794-148') : '-';
  }
  if (isCodebaseKnowledge(row)) {
    return row?.activeVersionId ? t('ai.knowledge.index.748794-185') : '-';
  }
  return '-';
}

function resolveDownloadTemplateLabel(row, withPrefix = false) {
  return withPrefix ? t('ai.knowledge.index.748794-149') : t('ai.knowledge.index.748794-150');
}

function resolveUploadActionLabel(row) {
  return isCodebaseKnowledge(row) ? t('ai.knowledge.index.748794-186') : t('ai.knowledge.index.748794-9');
}

function isEnterpriseTemplateMode(templateMode) {
  return templateMode === TEMPLATE_MODE_ENTERPRISE_EXPORT;
}

function showPublishButton(row) {
  return row?.isActive !== '1' && ['DRAFT', 'READY'].includes(row?.publishStatus);
}

function showRollbackButton(row) {
  return row?.isActive !== '1' && row?.publishStatus === 'ARCHIVED';
}

function supportsQualityCheck(_row) {
  return isNl2SqlKnowledge(currentKnowledge.value);
}

async function getList() {
  loading.value = true;
  try {
    const response = await listKnowledge({ pageNum: 1, pageSize: 100 });
    knowledgeList.value = response.rows || [];
    syncCurrentKnowledge();
  } finally {
    loading.value = false;
  }
}

async function getVersionList() {
  if (!versionQueryParams.value.knowledgeBaseId) {
    versionList.value = [];
    return;
  }
  versionLoading.value = true;
  try {
    const response = await listKnowledgeVersion(versionQueryParams.value);
    versionList.value = response.rows || [];
  } finally {
    versionLoading.value = false;
  }
}

function handleAdd() {
  form.value = createDefaultForm();
  extraConfigItems.value = [createExtraConfigItem()];
  open.value = true;
}

async function handleUpdate(row) {
  const response = await getKnowledge(row.knowledgeBaseId);
  form.value = {
    ...createDefaultForm(),
    ...(response.data || {}),
    kbType: normalizeKbType(response.data?.kbType),
    vectorStoreType: response.data?.vectorStoreType || 'REDIS',
    status: response.data?.status || '0',
  };
  extraConfigItems.value = parseExtraConfigItems(response.data?.extraConfig);
  open.value = true;
}

async function submitForm() {
  try {
    await formRef.value.validate();
  } catch (_error) {
    return;
  }
  let extraConfig = '';
  try {
    extraConfig = buildExtraConfigValue();
  } catch (error) {
    ElMessage.warning(error.message || t('ai.knowledge.index.748794-151'));
    return;
  }
  const payload = {
    ...form.value,
    kbType: normalizeKbType(form.value.kbType),
    extraConfig,
  };
  if (isEditMode.value) {
    await updateKnowledge(payload);
    ElMessage.success(payload.status === '0' ? t('ai.knowledge.index.748794-152') : t('ai.knowledge.index.748794-153'));
  } else {
    await addKnowledge(payload);
    ElMessage.success(payload.status === '0' ? t('ai.knowledge.index.748794-154') : t('ai.knowledge.index.748794-155'));
  }
  open.value = false;
  await getList();
}

async function handleStatusChange(row) {
  const nextStatus = row.status;
  const text = nextStatus === '0' ? t('ai.knowledge.index.748794-110') : t('ai.knowledge.index.748794-111');
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.index.748794-187', [text, row.kbName]),
      t('ai.knowledge.index.748794-156'),
      {
        type: 'warning',
      }
    );
    await changeKnowledgeStatus(row.knowledgeBaseId, nextStatus);
    ElMessage.success(
      nextStatus === '0' ? t('ai.knowledge.index.748794-188', [text]) : t('ai.knowledge.index.748794-189', [text])
    );
    await getList();
  } catch (_error) {
    row.status = nextStatus === '0' ? '1' : '0';
  }
}

function handleDownloadTemplate(row, options = {}) {
  if (!row?.knowledgeBaseId) {
    ElMessage.warning(t('ai.knowledge.index.748794-157'));
    return;
  }
  const actualKbType = normalizeKbType(row.kbType);
  const actualTemplateMode = options.templateMode || TEMPLATE_MODE_EMPTY;
  if (isEnterpriseTemplateMode(actualTemplateMode) && !canDownloadEnterpriseTemplate.value) {
    ElMessage.warning(t('ai.knowledge.index.748794-158'));
    return;
  }
  const actualSourceStrategy = options.sourceStrategy || SOURCE_STRATEGY_AUTO;
  const fileName = `${row.kbCode || 'knowledge'}_${actualKbType.toLowerCase()}_template_${Date.now()}.xlsx`;
  const params = {
    knowledgeBaseId: row.knowledgeBaseId,
    kbType: actualKbType,
    templateMode: actualTemplateMode,
    sourceStrategy: actualSourceStrategy,
  };
  proxy?.download('/ai/knowledge/template/download', params, fileName);
}

function handleDownloadTemplateCommand(row, command) {
  handleDownloadTemplate(row, {
    templateMode: command || TEMPLATE_MODE_EMPTY,
    sourceStrategy: SOURCE_STRATEGY_AUTO,
  });
}

function handleUpload(row) {
  if (isCodebaseKnowledge(row) && !isAdminAccount()) {
    ElMessage.warning(t('ai.knowledge.index.748794-190'));
    return;
  }
  currentKnowledge.value = row;
  uploadForm.value = {
    ...createDefaultUploadForm(),
    sourceOrigin: (row?.documentCount || 0) > 0 ? 'CUSTOM' : 'OFFICIAL',
    sortNum: toNumberOrUndefined(row?.nextSortNum),
  };
  uploadFile.value = null;
  uploadFileList.value = [];
  uploadRef.value?.clearFiles?.();
  uploadFormRef.value?.clearValidate?.();
  uploadOpen.value = true;
}

function handleDocumentManager(row) {
  currentKnowledge.value = row;
  documentManagerOpen.value = true;
}

function handleUploadFileChange(file, fileList) {
  uploadFile.value = file.raw || null;
  uploadFileList.value = fileList.slice(-1);
}

function handleUploadFileRemove(_file, fileList) {
  uploadFile.value = null;
  uploadFileList.value = fileList;
}

async function submitUpload() {
  if (!currentKnowledge.value?.knowledgeBaseId) {
    ElMessage.warning(t('ai.knowledge.index.748794-159'));
    return;
  }
  if (isCodebaseKnowledge(currentKnowledge.value) && !isAdminAccount()) {
    ElMessage.warning(t('ai.knowledge.index.748794-190'));
    return;
  }
  try {
    await uploadFormRef.value?.validate?.();
  } catch (_error) {
    return;
  }
  if (!uploadFile.value) {
    ElMessage.warning(t('ai.knowledge.index.748794-160'));
    return;
  }
  const formData = new FormData();
  uploadForm.value.sortNum = toNumberOrUndefined(uploadForm.value.sortNum);
  formData.append('knowledgeBaseId', currentKnowledge.value.knowledgeBaseId);
  formData.append('sourceOrigin', uploadForm.value.sourceOrigin || 'CUSTOM');
  if (uploadForm.value.appVersion?.trim()) {
    formData.append('appVersion', uploadForm.value.appVersion.trim());
  }
  if (uploadForm.value.sortNum !== undefined && uploadForm.value.sortNum !== null && uploadForm.value.sortNum !== '') {
    formData.append('sortNum', String(uploadForm.value.sortNum));
  }
  formData.append('file', uploadFile.value);
  uploading.value = true;
  try {
    const response = isCodebaseKnowledge(currentKnowledge.value)
      ? await uploadCodebaseGuideSnapshot(formData)
      : await uploadKnowledgeTemplate(formData);
    const result = response.data || {};
    ElMessage.success(response.msg || t('ai.knowledge.index.748794-161'));
    uploadOpen.value = false;
    await getList();
    await documentManagerRef.value?.reload?.();
    await ElMessageBox.alert(
      t('ai.knowledge.index.748794-200', [
        result.documentId ||
          '-' +
            t('ai.knowledge.index.748794-162') +
            '-' +
            t('ai.knowledge.index.748794-163') +
            '-' +
            t('ai.knowledge.index.748794-164') +
            '-' +
            t('ai.knowledge.index.748794-165') +
            '-',
      ]),
      t('ai.knowledge.index.748794-166'),
      { confirmButtonText: t('ai.knowledge.index.748794-34') }
    );
  } finally {
    uploading.value = false;
  }
}

async function handleRebuildCodebase(row) {
  if (!row?.knowledgeBaseId) {
    ElMessage.warning(t('ai.knowledge.index.748794-157'));
    return;
  }
  if (!isAdminAccount()) {
    ElMessage.warning(t('ai.knowledge.index.748794-191'));
    return;
  }
  try {
    await ElMessageBox.confirm(t('ai.knowledge.index.748794-192', [row.kbName]), t('ai.knowledge.index.748794-156'), {
      type: 'warning',
    });
    rebuildingCodebaseId.value = row.knowledgeBaseId;
    const response = await rebuildCodebaseGuide(row.knowledgeBaseId);
    const result = response.data || {};
    ElMessage.success(response.msg || result.message || t('ai.knowledge.index.748794-193'));
    await getList();
    await documentManagerRef.value?.reload?.();
    await ElMessageBox.alert(
      t('ai.knowledge.index.748794-201', [result.documentId || '-', result.rowCount ?? 0]),
      t('ai.knowledge.index.748794-194'),
      { confirmButtonText: t('ai.knowledge.index.748794-34') }
    );
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
    throw error;
  } finally {
    rebuildingCodebaseId.value = undefined;
  }
}

function isRebuildingCodebase(row) {
  return rebuildingCodebaseId.value === row?.knowledgeBaseId;
}

async function handleVersion(row) {
  currentKnowledge.value = row;
  versionQueryParams.value.knowledgeBaseId = row.knowledgeBaseId;
  qualityCheckResult.value = null;
  qualityCheckOpen.value = false;
  versionOpen.value = true;
  await getVersionList();
}

async function handleRuntimeStatus(row) {
  currentKnowledge.value = row;
  runtimeOpen.value = true;
  await loadRuntimeStatus();
}

function warnAdminVersionOperation() {
  ElMessage.warning(t('ai.knowledge.index.748794-167'));
}

async function handleBuildVersion() {
  if (versionOperationLocked.value) {
    return;
  }
  if (!canOperateKnowledgeVersion.value) {
    warnAdminVersionOperation();
    return;
  }
  if (!currentKnowledge.value?.knowledgeBaseId) {
    ElMessage.warning(t('ai.knowledge.index.748794-157'));
    return;
  }
  await ElMessageBox.confirm(
    t('ai.knowledge.index.748794-195', [currentKnowledge.value.kbName]),
    t('ai.knowledge.index.748794-156'),
    { type: 'warning' }
  );
  const response = await buildKnowledgeVersion(currentKnowledge.value.knowledgeBaseId);
  const result = response.data || {};
  ElMessage.success(response.msg || t('ai.knowledge.index.748794-168'));
  await getVersionList();
  await getList();
  await ElMessageBox.alert(
    t('ai.knowledge.index.748794-202', [result.versionNo || '-' + t('ai.knowledge.index.748794-169') + '-']),
    t('ai.knowledge.index.748794-170'),
    { confirmButtonText: t('ai.knowledge.index.748794-34') }
  );
}

async function handlePublishVersion(row) {
  if (versionOperationLocked.value) {
    return;
  }
  if (!canOperateKnowledgeVersion.value) {
    warnAdminVersionOperation();
    return;
  }
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.index.748794-196', [row.versionNo]),
      t('ai.knowledge.index.748794-156'),
      {
        type: 'warning',
      }
    );
    publishingVersionId.value = row.versionId;
    await publishKnowledgeVersion(row.versionId);
    ElMessage.success(t('ai.knowledge.index.748794-171'));
    await getVersionList();
    await getList();
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
  } finally {
    publishingVersionId.value = undefined;
  }
}

async function handleQualityCheck(row) {
  if (versionOperationLocked.value) {
    return;
  }
  if (!canOperateKnowledgeVersion.value) {
    warnAdminVersionOperation();
    return;
  }
  qualityCheckLoading.value = true;
  qualityCheckResult.value = null;
  qualityCheckOpen.value = true;
  checkingVersionId.value = row.versionId;
  try {
    const response = await previewKnowledgeVersionQuality(row.versionId);
    qualityCheckResult.value = response.data || null;
    ElMessage.success(response.msg || t('ai.knowledge.index.748794-172'));
  } catch (error) {
    qualityCheckOpen.value = false;
    throw error;
  } finally {
    checkingVersionId.value = undefined;
    qualityCheckLoading.value = false;
  }
}

async function handleRollbackVersion(row) {
  if (versionOperationLocked.value) {
    return;
  }
  if (!canOperateKnowledgeVersion.value) {
    warnAdminVersionOperation();
    return;
  }
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.index.748794-197', [row.versionNo]),
      t('ai.knowledge.index.748794-156'),
      {
        type: 'warning',
      }
    );
    rollbackingVersionId.value = row.versionId;
    await rollbackKnowledgeVersion(row.versionId);
    ElMessage.success(t('ai.knowledge.index.748794-173'));
    await getVersionList();
    await getList();
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
    throw error;
  } finally {
    rollbackingVersionId.value = undefined;
  }
}

async function handleDeleteVersion(row) {
  if (versionOperationLocked.value) {
    return;
  }
  if (!canOperateKnowledgeVersion.value) {
    warnAdminVersionOperation();
    return;
  }
  try {
    await ElMessageBox.confirm(
      t('ai.knowledge.index.748794-198', [row.versionNo]),
      t('ai.knowledge.index.748794-156'),
      {
        type: 'warning',
      }
    );
    deletingVersionId.value = row.versionId;
    await removeKnowledgeVersion(row.versionId);
    ElMessage.success(t('ai.knowledge.index.748794-174'));
    await getVersionList();
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
    throw error;
  } finally {
    deletingVersionId.value = undefined;
  }
}

function isPublishingVersion(row) {
  return publishingVersionId.value === row?.versionId;
}

function isDeletingVersion(row) {
  return deletingVersionId.value === row?.versionId;
}

function isCheckingVersion(row) {
  return checkingVersionId.value === row?.versionId;
}

function isRollingBackVersion(row) {
  return rollbackingVersionId.value === row?.versionId;
}

async function handleDocumentUpdated() {
  await getList();
}

async function loadRuntimeStatus() {
  if (!currentKnowledge.value?.knowledgeBaseId) {
    runtimeStatus.value = null;
    return;
  }
  runtimeLoading.value = true;
  try {
    const response = await getKnowledgeRuntimeStatus(currentKnowledge.value.knowledgeBaseId);
    runtimeStatus.value = response.data || null;
  } finally {
    runtimeLoading.value = false;
  }
}

async function handleRebuildRuntime() {
  if (!currentKnowledge.value?.knowledgeBaseId) {
    ElMessage.warning(t('ai.knowledge.index.748794-157'));
    return;
  }
  if (!showRuntimeRebuildButton.value) {
    ElMessage.warning(t('ai.knowledge.index.748794-175'));
    return;
  }
  await ElMessageBox.confirm(
    t('ai.knowledge.index.748794-199', [currentKnowledge.value.kbName]),
    t('ai.knowledge.index.748794-156'),
    {
      type: 'warning',
    }
  );
  runtimeSyncing.value = true;
  try {
    const response = await rebuildKnowledgeRuntime(currentKnowledge.value.knowledgeBaseId);
    const result = response.data || {};
    ElMessage.success(response.msg || result.message || t('ai.knowledge.index.748794-176'));
    await loadRuntimeStatus();
    await getList();
  } finally {
    runtimeSyncing.value = false;
  }
}

function handleViewModeChange(value) {
  viewMode.value = value || 'card';
  window.localStorage.setItem(KNOWLEDGE_VIEW_MODE_KEY, viewMode.value);
}

function syncCurrentKnowledge() {
  if (!currentKnowledge.value?.knowledgeBaseId) {
    return;
  }
  const latest = knowledgeList.value.find((item) => item.knowledgeBaseId === currentKnowledge.value.knowledgeBaseId);
  if (latest) {
    currentKnowledge.value = latest;
  }
}

function parseExtraConfigItems(rawValue) {
  if (!rawValue) {
    return [createExtraConfigItem()];
  }
  try {
    const parsed = JSON.parse(rawValue);
    if (Array.isArray(parsed)) {
      const items = parsed
        .map((item) => {
          if (item && typeof item === 'object') {
            return createExtraConfigItem(item.key ?? item.configKey ?? '', item.value ?? item.configValue ?? '');
          }
          return null;
        })
        .filter(Boolean);
      return items.length ? items : [createExtraConfigItem()];
    }
    if (parsed && typeof parsed === 'object') {
      const items = Object.entries(parsed).map(([configKey, configValue]) =>
        createExtraConfigItem(configKey, configValue == null ? '' : String(configValue))
      );
      return items.length ? items : [createExtraConfigItem()];
    }
  } catch (_error) {
    return [createExtraConfigItem('raw', rawValue)];
  }
  return [createExtraConfigItem()];
}

function buildExtraConfigValue() {
  const items = extraConfigItems.value
    .map((item) => ({
      configKey: String(item.configKey || '').trim(),
      configValue: item.configValue == null ? '' : String(item.configValue).trim(),
    }))
    .filter((item) => item.configKey || item.configValue);
  if (!items.length) {
    return '';
  }
  const invalidItem = items.find((item) => !item.configKey);
  if (invalidItem) {
    throw new Error(t('ai.knowledge.index.748794-177'));
  }
  const configMap = {};
  items.forEach((item) => {
    configMap[item.configKey] = item.configValue;
  });
  return JSON.stringify(configMap);
}

function addExtraConfigItem() {
  extraConfigItems.value.push(createExtraConfigItem());
}

function removeExtraConfigItem(index) {
  if (extraConfigItems.value.length <= 1) {
    return;
  }
  extraConfigItems.value.splice(index, 1);
}

onMounted(() => {
  const storedViewMode = window.localStorage.getItem(KNOWLEDGE_VIEW_MODE_KEY);
  if (storedViewMode === 'card' || storedViewMode === 'table') {
    viewMode.value = storedViewMode;
  }
  getList();
});
</script>

<style scoped>
.ai-knowledge-page {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
}

.page-alert {
  margin-bottom: 0;
}

.page-tool-card {
  border-radius: 4px;
}

.page-tool-card :deep(.el-card__body) {
  padding: 16px;
}

.page-tool-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.page-tool-row__left,
.page-tool-row__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 10px;
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

.knowledge-card-row {
  margin-bottom: 0;
}

.knowledge-card {
  height: 100%;
}

.knowledge-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.knowledge-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.knowledge-card__title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.3;
}

.knowledge-card__subtitle {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.knowledge-card__code {
  color: #7f8a99;
  font-size: 12px;
}

.knowledge-card__description {
  min-height: 52px;
  color: #5f6b7a;
  font-size: 13px;
  line-height: 1.7;
}

.knowledge-card__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  grid-auto-rows: minmax(58px, auto);
  gap: 12px;
  padding: 10px;
  min-height: 146px;
  align-content: start;
  border-radius: 12px;
  background: #f8fafc;
}

.meta-item {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 6px;
  min-height: 58px;
  color: #1f2d3d;
  font-size: 13px;
}

.meta-item__label {
  color: #7f8a99;
  font-size: 12px;
}

.knowledge-card__footer {
  align-items: center;
  margin-top: auto;
}

.knowledge-card__footer > * {
  margin: 0 !important;
}

.knowledge-card__footer :deep(.el-button),
.knowledge-card__footer :deep(.el-link) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.knowledge-card__footer :deep(.el-dropdown) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.card-action-bar {
  display: flex;
  align-items: center;
  min-height: 39px;
  margin: 18px -20px -20px;
  padding: 10px;
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
  overflow: hidden;
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

.knowledge-card__footer .card-action-item :deep(.el-button) {
  width: 100%;
  height: 100%;
  margin: 0 !important;
  padding: 0 !important;
  color: inherit;
  font-size: inherit;
  overflow: hidden;
}

.knowledge-operation-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0;
}

.knowledge-operation-cell :deep(.el-button),
.knowledge-operation-cell :deep(.el-dropdown) {
  display: inline-flex;
  align-items: center;
  height: 28px;
  line-height: 28px;
  margin: 0;
}

.knowledge-operation-cell :deep(.el-button + .el-button),
.knowledge-operation-cell :deep(.el-dropdown + .el-button),
.knowledge-operation-cell :deep(.el-button + .el-dropdown) {
  margin-left: 12px;
}

.governance-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.upload-alert {
  margin-bottom: 16px;
}

.upload-tip {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
}

.upload-form-tip {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.6;
}

.download-link {
  margin-top: 10px;
}

.extra-config-editor {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.extra-config-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
}

.extra-config-add {
  align-self: flex-start;
}

.version-toolbar {
  margin-bottom: 16px;
}

.version-manager-body {
  min-height: 100%;
}

.runtime-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.runtime-descriptions {
  margin-bottom: 16px;
}

.quality-check-section {
  margin-top: 16px;
}

.runtime-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.runtime-summary-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #f8fafc;
}

.runtime-summary-card__label {
  color: #1f2d3d;
  font-size: 13px;
  font-weight: 500;
}

.runtime-section {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
}

.runtime-section__title {
  margin-bottom: 12px;
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 600;
}

.runtime-issue-list {
  margin: 0;
  padding-left: 18px;
  color: #5f6b7a;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .page-tool-row {
    align-items: flex-start;
  }

  .extra-config-row {
    grid-template-columns: 1fr;
  }

  .knowledge-card__meta {
    grid-template-columns: 1fr;
  }

  .runtime-summary-grid {
    grid-template-columns: 1fr;
  }
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

.extra-config-editor {
  width: 400px;
  max-width: 100%;
}

.extra-config-row :deep(.el-input) {
  width: 100% !important;
}

.upload-form-tip,
.upload-tip,
.download-link {
  max-width: 400px;
}
</style>
