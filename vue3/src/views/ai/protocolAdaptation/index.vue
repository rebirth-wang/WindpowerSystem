<template>
  <div class="ai-protocol-adaptation-page">
    <el-card v-show="showSearch" class="search-card">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item>
          <el-input
            v-model="queryParams.taskName"
            :placeholder="t('ai.protocolAdaptation.index.748794-1')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="queryParams.protocolCode"
            :placeholder="t('ai.protocolAdaptation.index.748794-3')"
            clearable
            @keyup.enter="getList"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="queryParams.flowStatus"
            :placeholder="t('ai.protocolAdaptation.index.748794-5')"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in adaptationFlowStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery" v-hasPermi="['ai:protocol:adaptation:list']">
            {{ t('ai.protocolAdaptation.index.748794-6') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            {{ t('ai.protocolAdaptation.index.748794-7') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar-row">
        <div class="toolbar-row__left">
          <el-button plain :icon="Refresh" @click="getList" v-hasPermi="['ai:protocol:adaptation:list']">
            {{ t('ai.protocolAdaptation.index.748794-8') }}
          </el-button>
        </div>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </div>

      <el-table v-loading="loading" :data="taskList" :empty-text="t('ai.protocolAdaptation.index.748794-9')">
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-10')" width="110">
          <template #default="{ row }">#{{ row.taskId || '-' }}</template>
        </el-table-column>
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-0')" min-width="220">
          <template #default="{ row }">
            <div class="task-name-cell">
              <el-button type="primary" link class="task-name-link" @click="handleDetail(row)">
                {{ row.taskName || '-' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-2')" prop="protocolCode" min-width="150" />
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-11')" min-width="200">
          <template #default="{ row }">
            <div class="protocol-cell">
              <span>{{ row.protocolName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-4')" width="150">
          <template #default="{ row }">
            <el-tag :type="resolveAdaptationStatus(row).type">
              {{ resolveAdaptationStatus(row).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-12')" prop="artifactCount" width="100" />
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-13')" prop="generationRecordCount" width="130" />
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-14')" prop="updateTime" min-width="170" />
        <el-table-column :label="t('ai.protocolAdaptation.index.748794-15')" align="center" width="180" fixed="right">
          <template #default="{ row }">
            <div class="task-operation-cell">
              <el-button
                size="small"
                link
                :icon="View"
                @click="handleContinueTask(row)"
                v-hasPermi="['ai:protocol:adaptation:query']"
              >
                {{ t('ai.protocolAdaptation.index.748794-16') }}
              </el-button>
              <el-dropdown
                v-if="
                  checkPermi([
                    'ai:protocol:adaptation:edit',
                    'ai:protocol:adaptation:workbook:export',
                    'ai:protocol:adaptation:workbook:import',
                    'ai:protocol:adaptation:remove',
                  ])
                "
                @command="(command) => handleTaskCommand(command, row)"
              >
                <el-button size="small" link :icon="MoreFilled">
                  {{ t('ai.protocolAdaptation.index.748794-17') }}
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="checkPermi(['ai:protocol:adaptation:edit'])" command="edit" :icon="Edit">
                      {{ t('ai.protocolAdaptation.index.748794-18') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:workbook:export'])"
                      command="exportWorkbook"
                      :icon="Download"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-19') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:workbook:import'])"
                      command="importWorkbook"
                      :icon="Upload"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-20') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:remove'])"
                      command="delete"
                      divided
                      :icon="Delete"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-21') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
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
    </el-card>

    <el-dialog v-model="formOpen" :title="formTitle" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" class="ai-dialog-form" :model="form" :rules="formRules" label-width="100px">
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-0')" prop="taskName">
          <el-input v-model="form.taskName" :placeholder="t('ai.protocolAdaptation.index.748794-1')" />
        </el-form-item>
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-2')" prop="protocolCode">
          <el-input v-model="form.protocolCode" :placeholder="t('ai.protocolAdaptation.index.748794-3')" />
        </el-form-item>
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-11')" prop="protocolName">
          <el-input v-model="form.protocolName" :placeholder="t('ai.protocolAdaptation.index.748794-22')" />
        </el-form-item>
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-23')" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            :placeholder="t('ai.protocolAdaptation.index.748794-24')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">
          {{ t('ai.protocolAdaptation.index.748794-25') }}
        </el-button>
        <el-button @click="formOpen = false">
          {{ t('ai.protocolAdaptation.index.748794-26') }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="workbookImportOpen"
      :title="t('ai.protocolAdaptation.index.748794-20')"
      width="600px"
      append-to-body
    >
      <el-form class="ai-dialog-form" label-width="100px">
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-0')">
          <el-input :model-value="currentWorkbookTask?.taskName || '-'" disabled />
        </el-form-item>
        <el-form-item :label="t('ai.protocolAdaptation.index.748794-27')" required>
          <el-upload
            ref="workbookImportRef"
            action="#"
            drag
            :auto-upload="false"
            :limit="1"
            accept=".xls,.xlsx"
            :file-list="workbookImportFileList"
            :on-change="handleWorkbookFileChange"
            :on-remove="handleWorkbookFileRemove"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">{{ t('ai.protocolAdaptation.index.748794-28') }}</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="workbookImporting" @click="submitWorkbookImport">
          {{ t('ai.protocolAdaptation.index.748794-25') }}
        </el-button>
        <el-button @click="workbookImportOpen = false">
          {{ t('ai.protocolAdaptation.index.748794-26') }}
        </el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="detailOpen"
      :title="t('ai.protocolAdaptation.index.748794-29')"
      size="60%"
      direction="rtl"
      append-to-body
      destroy-on-close
    >
      <div v-loading="detailLoading" class="detail-drawer">
        <section v-if="detail.taskId" class="detail-workbench">
          <div class="detail-workbench__header">
            <div class="detail-workbench__main">
              <div class="detail-workbench__eyebrow">{{ t('ai.protocolAdaptation.index.748794-30') }}</div>
              <div class="detail-workbench__name">{{ detail.taskName || '-' }}</div>
              <div class="detail-workbench__summary">{{ detailStatusSummary }}</div>
            </div>
            <div class="detail-workbench__actions">
              <el-button
                :type="detailPrimaryAction.type"
                :icon="detailPrimaryAction.icon"
                :loading="detailPrimaryAction.loading"
                :disabled="detailPrimaryAction.disabled"
                @click="handleDetailPrimaryAction"
                v-hasPermi="[
                  'ai:protocol:adaptation:auto-run',
                  'ai:protocol:adaptation:workbook:export',
                  'ai:protocol:adaptation:verify',
                  'ai:protocol:adaptation:confirm',
                  'ai:protocol:adaptation:query',
                ]"
              >
                {{ detailPrimaryAction.label }}
              </el-button>
              <el-dropdown
                v-if="
                  checkPermi([
                    'ai:protocol:adaptation:edit',
                    'ai:protocol:adaptation:workbook:export',
                    'ai:protocol:adaptation:workbook:import',
                    'ai:protocol:adaptation:remove',
                  ])
                "
                @command="(command) => handleTaskCommand(command, detail)"
              >
                <el-button plain :icon="MoreFilled">
                  {{ t('ai.protocolAdaptation.index.748794-17') }}
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="checkPermi(['ai:protocol:adaptation:edit'])" command="edit" :icon="Edit">
                      {{ t('ai.protocolAdaptation.index.748794-18') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:workbook:export'])"
                      command="exportWorkbook"
                      :icon="Download"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-19') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:workbook:import'])"
                      command="importWorkbook"
                      :icon="Upload"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-20') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="checkPermi(['ai:protocol:adaptation:remove'])"
                      command="delete"
                      divided
                      :icon="Delete"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-21') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <el-steps :active="activeWorkflowStep" finish-status="success" process-status="process" simple>
            <el-step v-for="step in workflowSteps" :key="step.title" :title="step.title" />
          </el-steps>

          <div class="detail-workbench__status-grid">
            <div class="status-tile">
              <span>{{ t('ai.protocolAdaptation.index.748794-31') }}</span>
              <el-tag :type="resolveAdaptationStatus(detail, latestGenerationRecord).type">
                {{ resolveAdaptationStatus(detail, latestGenerationRecord).label }}
              </el-tag>
            </div>
            <div class="status-tile">
              <span>{{ t('ai.protocolAdaptation.index.748794-32') }}</span>
              <el-tag :type="resolveQualityGateStatus(detail).type">
                {{ resolveQualityGateStatus(detail).label }}
              </el-tag>
            </div>
            <div class="status-tile">
              <span>{{ t('ai.protocolAdaptation.index.748794-33') }}</span>
              <el-tag :type="resolveCodePackageStatus(detail, latestGenerationRecord).type">
                {{ resolveCodePackageStatus(detail, latestGenerationRecord).label }}
              </el-tag>
            </div>
            <div class="status-tile">
              <span>{{ t('ai.protocolAdaptation.index.748794-34') }}</span>
              <el-tag :type="resolveCompileStatusTag(latestGenerationRecord?.compileStatus)">
                {{ latestGenerationRecord ? formatCompileStatus(latestGenerationRecord.compileStatus) : '-' }}
              </el-tag>
            </div>
          </div>

          <el-alert
            v-if="qualityGateAlertVisible"
            :type="qualityGateAlertType"
            show-icon
            :closable="false"
            class="quality-gate-alert"
          >
            <template #title>
              {{ qualityGateAlertTitle }}
            </template>
            <div class="quality-gate-alert__body">
              <span>{{ qualityGateAlertSummary }}</span>
              <div class="quality-gate-alert__actions">
                <el-button
                  v-if="latestValidationReportArtifact"
                  type="primary"
                  link
                  :icon="View"
                  @click="handlePreviewArtifact(latestValidationReportArtifact)"
                  v-hasPermi="['ai:protocol:adaptation:query']"
                >
                  {{ t('ai.protocolAdaptation.index.748794-35') }}
                </el-button>
                <el-button
                  v-if="latestEnterpriseWorkbookArtifact"
                  type="primary"
                  link
                  :icon="Download"
                  @click="handleDownloadArtifact(latestEnterpriseWorkbookArtifact)"
                  v-hasPermi="['ai:protocol:adaptation:query']"
                >
                  {{ t('ai.protocolAdaptation.index.748794-19') }}
                </el-button>
              </div>
            </div>
          </el-alert>
        </section>

        <section class="detail-section">
          <div class="detail-section__title">{{ t('ai.protocolAdaptation.index.748794-36') }}</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-0')">
              {{ detail.taskName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-10')">
              #{{ detail.taskId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-11')">
              {{ detail.protocolName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-2')">
              {{ detail.protocolCode || '-' }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-37')" :span="2">
              {{ detail.dslSnapshotPath || t('ai.protocolAdaptation.index.748794-38') }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-39')" :span="2">
              {{ detail.workbookPath || t('ai.protocolAdaptation.index.748794-38') }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-23')" :span="2">
              {{ detail.remark || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </section>

        <section class="detail-section">
          <div class="detail-section__title">{{ t('ai.protocolAdaptation.index.748794-40') }}</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-4')">
              <el-tag :type="resolveAdaptationStatus(detail, latestGenerationRecord).type">
                {{ resolveAdaptationStatus(detail, latestGenerationRecord).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-32')">
              <el-tag :type="resolveQualityGateStatus(detail).type">
                {{ resolveQualityGateStatus(detail).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-33')">
              <el-tag :type="resolveCodePackageStatus(detail, latestGenerationRecord).type">
                {{ resolveCodePackageStatus(detail, latestGenerationRecord).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-34')">
              <el-tag :type="resolveCompileStatusTag(latestGenerationRecord?.compileStatus)">
                {{ latestGenerationRecord ? formatCompileStatus(latestGenerationRecord.compileStatus) : '-' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-14')">
              {{ detail.updateTime || '-' }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('ai.protocolAdaptation.index.748794-41')" :span="2">
              {{ detail.errorSummary || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </section>

        <el-tabs>
          <el-tab-pane :label="t('ai.protocolAdaptation.index.748794-42')">
            <el-table :data="artifactList" :empty-text="t('ai.protocolAdaptation.index.748794-9')">
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-43')"
                prop="artifactName"
                min-width="180"
              />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-44')" min-width="150">
                <template #default="{ row }">
                  <dict-tag :options="dict.type.ai_protocol_adaptation_artifact_type" :value="row.artifactType" />
                </template>
              </el-table-column>
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-45')" min-width="130">
                <template #default="{ row }">
                  <dict-tag :options="dict.type.ai_protocol_adaptation_artifact_status" :value="row.artifactStatus" />
                </template>
              </el-table-column>
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-46')" width="110">
                <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
              </el-table-column>
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-47')"
                prop="summary"
                min-width="220"
                show-overflow-tooltip
              />
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-48')"
                prop="filePath"
                min-width="260"
                show-overflow-tooltip
              />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-49')" prop="createTime" width="170" />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-15')" width="170" fixed="right">
                <template #default="{ row }">
                  <div class="artifact-operation-cell">
                    <el-button
                      link
                      :icon="Download"
                      :disabled="!row.artifactId"
                      @click="handleDownloadArtifact(row)"
                      v-hasPermi="['ai:protocol:adaptation:query']"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-50') }}
                    </el-button>
                    <el-button
                      v-if="isPreviewableArtifact(row)"
                      link
                      :icon="View"
                      :disabled="!row.artifactId"
                      @click="handlePreviewArtifact(row)"
                      v-hasPermi="['ai:protocol:adaptation:query']"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-51') }}
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane :label="t('ai.protocolAdaptation.index.748794-52')">
            <el-table :data="generationRecordList" :empty-text="t('ai.protocolAdaptation.index.748794-9')">
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-53')" prop="recordId" width="110" />
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-54')"
                prop="generationStrategy"
                min-width="150"
              />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-55')" min-width="150">
                <template #default="{ row }">
                  <el-tag :type="resolveCompileStatusTag(row.compileStatus)">
                    {{ formatCompileStatus(row.compileStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-56')" min-width="130">
                <template #default="{ row }">
                  <dict-tag
                    :options="dict.type.ai_protocol_adaptation_generation_status"
                    :value="row.generationStatus"
                  />
                </template>
              </el-table-column>
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-57')"
                prop="validationErrorCount"
                width="120"
              />
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-58')"
                prop="validationWarningCount"
                width="120"
              />
              <el-table-column
                :label="t('ai.protocolAdaptation.index.748794-59')"
                prop="codePackagePath"
                min-width="260"
                show-overflow-tooltip
              />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-60')" prop="confirmBy" width="130" />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-61')" prop="confirmTime" width="170" />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-49')" prop="createTime" width="170" />
              <el-table-column :label="t('ai.protocolAdaptation.index.748794-15')" width="230" fixed="right">
                <template #default="{ row }">
                  <div class="generation-operation-cell">
                    <el-button
                      type="primary"
                      link
                      :icon="Download"
                      :disabled="!row.codePackagePath"
                      @click="handleDownloadGenerationFile(row, 'package')"
                      v-hasPermi="['ai:protocol:adaptation:query']"
                    >
                      {{ t('ai.protocolAdaptation.index.748794-62') }}
                    </el-button>
                    <el-dropdown
                      v-if="
                        checkPermi([
                          'ai:protocol:adaptation:verify',
                          'ai:protocol:adaptation:query',
                          'ai:protocol:adaptation:confirm',
                        ])
                      "
                      @command="(command) => handleGenerationRecordCommand(command, row)"
                    >
                      <el-button link :icon="MoreFilled">
                        {{ t('ai.protocolAdaptation.index.748794-17') }}
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item
                            v-if="checkPermi(['ai:protocol:adaptation:verify'])"
                            command="verify"
                            :icon="DocumentChecked"
                            :disabled="row.generationStatus !== 'SUCCESS'"
                          >
                            {{ t('ai.protocolAdaptation.index.748794-63') }}
                          </el-dropdown-item>
                          <el-dropdown-item
                            v-if="checkPermi(['ai:protocol:adaptation:query'])"
                            command="manifest"
                            :icon="Download"
                            :disabled="!row.fileManifestPath"
                          >
                            {{ t('ai.protocolAdaptation.index.748794-64') }}
                          </el-dropdown-item>
                          <el-dropdown-item
                            v-if="checkPermi(['ai:protocol:adaptation:query'])"
                            command="testReport"
                            :icon="Download"
                            :disabled="!row.testReportPath"
                          >
                            {{ t('ai.protocolAdaptation.index.748794-65') }}
                          </el-dropdown-item>
                          <el-dropdown-item
                            v-if="checkPermi(['ai:protocol:adaptation:confirm'])"
                            command="confirm"
                            :icon="CircleCheck"
                            :disabled="row.generationStatus !== 'SUCCESS' || !!row.confirmTime"
                          >
                            {{
                              row.confirmTime
                                ? t('ai.protocolAdaptation.index.748794-66')
                                : t('ai.protocolAdaptation.index.748794-67')
                            }}
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <el-dialog v-model="artifactPreviewOpen" :title="artifactPreviewTitle" width="860px" append-to-body>
      <div v-loading="artifactPreviewLoading" class="artifact-preview">
        <pre>{{ artifactPreviewContent || '-' }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  CircleCheck,
  Delete,
  DocumentChecked,
  Download,
  Edit,
  FolderChecked,
  MoreFilled,
  Refresh,
  Search,
  Upload,
  UploadFilled,
  View,
} from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { useDict } from '@/utils/dict';
import {
  autoRunProtocolAdaptationTask,
  confirmProtocolGenerationRecord,
  downloadProtocolAdaptationArtifact,
  downloadProtocolGenerationFile,
  exportProtocolAdaptationWorkbook,
  getProtocolAdaptationArtifactContent,
  getProtocolAdaptationTask,
  importProtocolAdaptationWorkbook,
  listProtocolAdaptationArtifacts,
  listProtocolAdaptationGenerationRecords,
  listProtocolAdaptationTask,
  removeProtocolAdaptationTask,
  updateProtocolAdaptationTask,
  verifyProtocolGenerationRecord,
} from '@/api/ai/protocolAdaptation';
import { checkPermi } from '@/utils/permission';

const { t } = useI18n();
const { dict } = useDict(
  'ai_protocol_adaptation_generation_status',
  'ai_protocol_adaptation_artifact_type',
  'ai_protocol_adaptation_artifact_status'
);

const loading = ref(false);
const detailLoading = ref(false);
const autoRunningTaskId = ref();
const exportingWorkbookTaskId = ref();
const importingWorkbookTaskId = ref();
const verifyingGenerationRecordId = ref();
const confirmingGenerationRecordId = ref();
const showSearch = ref(true);
const taskList = ref([]);
const artifactList = ref([]);
const generationRecordList = ref([]);
const total = ref(0);
const formOpen = ref(false);
const detailOpen = ref(false);
const workbookImportOpen = ref(false);
const artifactPreviewOpen = ref(false);
const workbookImporting = ref(false);
const artifactPreviewLoading = ref(false);
const formRef = ref();
const workbookImportRef = ref();
const form = ref(createDefaultForm());
const detail = ref({});
const currentWorkbookTask = ref(null);
const currentDetailTaskId = ref();
const workbookImportFile = ref(null);
const workbookImportFileList = ref([]);
const artifactPreviewTitle = ref('');
const artifactPreviewContent = ref('');

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  taskName: '',
  protocolCode: '',
  flowStatus: '',
});

const formTitle = computed(() => t('ai.protocolAdaptation.index.748794-68'));
const formRules = computed(() => ({
  taskName: [{ required: true, message: t('ai.protocolAdaptation.index.748794-69'), trigger: 'blur' }],
}));
const workflowSteps = computed(() => [
  { title: t('ai.protocolAdaptation.index.748794-70') },
  { title: t('ai.protocolAdaptation.index.748794-71') },
  { title: t('ai.protocolAdaptation.index.748794-72') },
  { title: t('ai.protocolAdaptation.index.748794-73') },
  { title: t('ai.protocolAdaptation.index.748794-74') },
  { title: t('ai.protocolAdaptation.index.748794-75') },
]);
const latestGenerationRecord = computed(() => generationRecordList.value[0] || null);
const latestValidationReportArtifact = computed(() => findLatestArtifactByType('VALIDATION_REPORT'));
const latestEnterpriseWorkbookArtifact = computed(() => findLatestArtifactByType('ENTERPRISE_WORKBOOK'));
const activeWorkflowStep = computed(() => resolveActiveWorkflowStep(detail.value, latestGenerationRecord.value));
const detailStatusSummary = computed(() => resolveTaskStatusSummary(detail.value, latestGenerationRecord.value));
const detailPrimaryAction = computed(() => resolveSuggestedTaskAction(detail.value, latestGenerationRecord.value));
const adaptationFlowStatusOptions = computed(() => [
  { label: t('ai.protocolAdaptation.index.748794-76'), value: 'PROCESSING' },
  { label: t('ai.protocolAdaptation.index.748794-77'), value: 'NEED_REVIEW' },
  { label: t('ai.protocolAdaptation.index.748794-78'), value: 'PACKAGE_READY' },
  { label: t('ai.protocolAdaptation.index.748794-79'), value: 'CONFIRMED' },
  { label: t('ai.protocolAdaptation.index.748794-80'), value: 'FAILED' },
]);
const qualityGateAlertVisible = computed(
  () =>
    detail.value?.validationStatus === 'BLOCKED' ||
    detail.value?.validationStatus === 'WARNING' ||
    Boolean(detail.value?.errorSummary && latestValidationReportArtifact.value)
);
const qualityGateAlertType = computed(() => {
  if (detail.value?.validationStatus === 'BLOCKED') {
    return 'error';
  }
  if (detail.value?.validationStatus === 'WARNING') {
    return 'warning';
  }
  return 'info';
});
const qualityGateAlertTitle = computed(() => {
  if (detail.value?.validationStatus === 'BLOCKED') {
    return t('ai.protocolAdaptation.index.748794-81');
  }
  if (detail.value?.validationStatus === 'WARNING') {
    return t('ai.protocolAdaptation.index.748794-82');
  }
  return t('ai.protocolAdaptation.index.748794-83');
});
const qualityGateAlertSummary = computed(
  () =>
    detail.value?.errorSummary ||
    latestValidationReportArtifact.value?.summary ||
    t('ai.protocolAdaptation.index.748794-84')
);

function createDefaultForm() {
  return {
    taskId: undefined,
    taskName: '',
    protocolCode: '',
    protocolName: '',
    remark: '',
  };
}

function resetQueryParams() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    taskName: '',
    protocolCode: '',
    flowStatus: '',
  };
}

function buildListQueryParams() {
  const params = { ...queryParams.value };
  const flowStatus = params.flowStatus;
  delete params.flowStatus;
  if (flowStatus) {
    params.params = { flowStatus };
  }
  return params;
}

function buildSubmitData() {
  return { ...form.value };
}

function formatFileSize(value) {
  const size = Number(value || 0);
  if (!size) {
    return '-';
  }
  if (size < 1024) {
    return t('ai.protocolAdaptation.index.748794-85', { size });
  }
  if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`;
  }
  return `${(size / 1024 / 1024).toFixed(1)} MB`;
}

function findLatestArtifactByType(artifactType) {
  return artifactList.value.find((item) => item.artifactType === artifactType) || null;
}

function resolveArtifactFileName(row) {
  const fallback = `protocol_artifact_${row?.artifactId || Date.now()}`;
  if (row?.artifactName) {
    return row.artifactName;
  }
  const filePath = row?.filePath || '';
  const fileName = String(filePath).split(/[\\/]/).pop();
  return fileName || fallback;
}

function getArtifactFileSuffix(row) {
  const fileName = resolveArtifactFileName(row);
  const index = fileName.lastIndexOf('.');
  return index >= 0 ? fileName.slice(index).toLowerCase() : '';
}

function isPreviewableArtifact(row) {
  if (!row?.artifactId) {
    return false;
  }
  const previewTypes = ['VALIDATION_REPORT', 'AI_DSL_DRAFT', 'EXTRACTED_TEXT'];
  if (previewTypes.includes(row.artifactType)) {
    return true;
  }
  return ['.json', '.txt', '.md', '.csv', '.log'].includes(getArtifactFileSuffix(row));
}

function formatArtifactPreviewContent(data) {
  const content = data?.content || '';
  if (!content) {
    return '';
  }
  const trimmed = String(content).trim();
  if (!trimmed) {
    return '';
  }
  if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
    try {
      return JSON.stringify(JSON.parse(trimmed), null, 2);
    } catch (_error) {
      return content;
    }
  }
  return content;
}

function formatCompileStatus(status) {
  const statusMap = {
    PENDING: t('ai.protocolAdaptation.index.748794-86'),
    STATIC_PASSED: t('ai.protocolAdaptation.index.748794-87'),
    STATIC_WARNING: t('ai.protocolAdaptation.index.748794-88'),
    STATIC_FAILED: t('ai.protocolAdaptation.index.748794-89'),
  };
  return statusMap[status] || status || '-';
}

function resolveCompileStatusTag(status) {
  if (status === 'STATIC_PASSED') {
    return 'success';
  }
  if (status === 'STATIC_WARNING') {
    return 'warning';
  }
  if (status === 'STATIC_FAILED') {
    return 'danger';
  }
  return 'info';
}

function hasUploadedArtifact(task) {
  return (
    Number(task?.artifactCount || 0) > 0 ||
    (task?.taskId && task.taskId === currentDetailTaskId.value && artifactList.value.length > 0)
  );
}

function isAcceptedValidationStatus(status) {
  return status === 'PASSED' || status === 'WARNING';
}

function isStaticVerifiedStatus(status) {
  return status === 'STATIC_PASSED' || status === 'STATIC_WARNING';
}

function makeBusinessStatus(label, type = 'info') {
  return { label, type };
}

function resolveAdaptationStatus(task, generationRecord = null) {
  if (!task?.taskId) {
    return makeBusinessStatus('-', 'info');
  }
  if (
    task.taskStatus === 'FAILED' ||
    task.parseStatus === 'FAILED' ||
    task.generationStatus === 'FAILED' ||
    generationRecord?.compileStatus === 'STATIC_FAILED'
  ) {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-80'), 'danger');
  }
  if (task.validationStatus === 'BLOCKED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-77'), 'danger');
  }
  if (generationRecord?.confirmTime || task.taskStatus === 'CONFIRMED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-79'), 'success');
  }
  if (isStaticVerifiedStatus(generationRecord?.compileStatus)) {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-90'), 'success');
  }
  if (task.generationStatus === 'SUCCESS' || task.taskStatus === 'GENERATED' || generationRecord?.recordId) {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-78'), 'success');
  }
  if (task.generationStatus === 'GENERATING') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-91'), 'warning');
  }
  if (task.validationStatus === 'WARNING') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-92'), 'warning');
  }
  if (task.validationStatus === 'PASSED' || task.taskStatus === 'VALIDATED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-93'), 'success');
  }
  if (task.parseStatus === 'PARSING') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-94'), 'warning');
  }
  if (task.parseStatus === 'SUCCESS' || task.taskStatus === 'AI_PARSED' || task.taskStatus === 'REVIEW_IMPORTED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-95'), 'primary');
  }
  if (hasUploadedArtifact(task) || task.taskStatus === 'UPLOADED' || task.taskStatus === 'WORKBOOK_EXPORTED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-76'), 'primary');
  }
  return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-96'), 'info');
}

function resolveQualityGateStatus(task) {
  if (!task?.taskId) {
    return makeBusinessStatus('-', 'info');
  }
  if (task.validationStatus === 'BLOCKED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-77'), 'danger');
  }
  if (task.validationStatus === 'WARNING') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-92'), 'warning');
  }
  if (task.validationStatus === 'PASSED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-93'), 'success');
  }
  return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-97'), 'info');
}

function resolveCodePackageStatus(task, generationRecord = null) {
  if (!task?.taskId) {
    return makeBusinessStatus('-', 'info');
  }
  if (task.generationStatus === 'FAILED' || generationRecord?.compileStatus === 'STATIC_FAILED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-80'), 'danger');
  }
  if (task.generationStatus === 'GENERATING') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-91'), 'warning');
  }
  if (generationRecord?.confirmTime || task.taskStatus === 'CONFIRMED') {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-79'), 'success');
  }
  if (task.generationStatus === 'SUCCESS' || generationRecord?.recordId) {
    return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-78'), 'success');
  }
  return makeBusinessStatus(t('ai.protocolAdaptation.index.748794-98'), 'info');
}

function resolveActiveWorkflowStep(task, generationRecord) {
  if (!task?.taskId) {
    return 0;
  }
  if (!hasUploadedArtifact(task)) {
    return 0;
  }
  if (generationRecord?.confirmTime) {
    return 6;
  }
  if (isStaticVerifiedStatus(generationRecord?.compileStatus) || generationRecord?.compileStatus === 'STATIC_FAILED') {
    return 5;
  }
  if (generationRecord?.recordId || task.generationStatus === 'SUCCESS') {
    return 4;
  }
  if (isAcceptedValidationStatus(task.validationStatus)) {
    return 3;
  }
  if (task.parseStatus === 'SUCCESS' || task.validationStatus === 'BLOCKED') {
    return 2;
  }
  return 1;
}

function resolveTaskStatusSummary(task, generationRecord) {
  if (!task?.taskId) {
    return '-';
  }
  if (!hasUploadedArtifact(task)) {
    return t('ai.protocolAdaptation.index.748794-99');
  }
  if (task.validationStatus === 'BLOCKED') {
    return t('ai.protocolAdaptation.index.748794-100');
  }
  if (generationRecord?.confirmTime) {
    return t('ai.protocolAdaptation.index.748794-101');
  }
  if (generationRecord?.compileStatus === 'STATIC_FAILED') {
    return t('ai.protocolAdaptation.index.748794-102');
  }
  if (isStaticVerifiedStatus(generationRecord?.compileStatus)) {
    return t('ai.protocolAdaptation.index.748794-103');
  }
  if (generationRecord?.recordId && generationRecord.generationStatus === 'SUCCESS') {
    return t('ai.protocolAdaptation.index.748794-104');
  }
  if (isAcceptedValidationStatus(task.validationStatus)) {
    return t('ai.protocolAdaptation.index.748794-105');
  }
  if (task.parseStatus === 'SUCCESS') {
    return t('ai.protocolAdaptation.index.748794-106');
  }
  return t('ai.protocolAdaptation.index.748794-107');
}

function resolveSuggestedTaskAction(task, generationRecord) {
  const taskId = task?.taskId;
  const continueAction = {
    command: 'autoRun',
    scope: 'task',
    label: t('ai.protocolAdaptation.index.748794-16'),
    icon: FolderChecked,
    type: 'primary',
    loading: autoRunningTaskId.value === taskId,
    disabled: !taskId || !hasUploadedArtifact(task),
  };
  if (!taskId) {
    return continueAction;
  }
  if (!hasUploadedArtifact(task)) {
    return continueAction;
  }
  if (generationRecord?.confirmTime && generationRecord.codePackagePath) {
    return {
      command: 'package',
      scope: 'generation',
      label: t('ai.protocolAdaptation.index.748794-62'),
      icon: Download,
      type: 'primary',
      loading: false,
      disabled: false,
    };
  }
  if (generationRecord?.confirmTime) {
    return {
      command: 'detail',
      scope: 'task',
      label: t('ai.protocolAdaptation.index.748794-108'),
      icon: View,
      type: 'primary',
      loading: false,
      disabled: false,
    };
  }
  if (isStaticVerifiedStatus(generationRecord?.compileStatus)) {
    return {
      command: 'confirm',
      scope: 'generation',
      label: t('ai.protocolAdaptation.index.748794-67'),
      icon: CircleCheck,
      type: 'success',
      loading: confirmingGenerationRecordId.value === generationRecord?.recordId,
      disabled: !generationRecord?.recordId,
    };
  }
  if (generationRecord?.compileStatus === 'STATIC_FAILED' && generationRecord.testReportPath) {
    return {
      command: 'testReport',
      scope: 'generation',
      label: t('ai.protocolAdaptation.index.748794-65'),
      icon: Download,
      type: 'warning',
      loading: false,
      disabled: false,
    };
  }
  if (generationRecord?.recordId && generationRecord.generationStatus === 'SUCCESS') {
    return {
      command: 'verify',
      scope: 'generation',
      label: t('ai.protocolAdaptation.index.748794-63'),
      icon: DocumentChecked,
      type: 'primary',
      loading: verifyingGenerationRecordId.value === generationRecord.recordId,
      disabled: false,
    };
  }
  if (task.validationStatus === 'BLOCKED') {
    return {
      command: 'exportWorkbook',
      scope: 'task',
      label: t('ai.protocolAdaptation.index.748794-19'),
      icon: Download,
      type: 'warning',
      loading: exportingWorkbookTaskId.value === task.taskId,
      disabled: false,
    };
  }
  return continueAction;
}

async function getList() {
  loading.value = true;
  try {
    const response = await listProtocolAdaptationTask(buildListQueryParams());
    taskList.value = response.rows || [];
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
  resetQueryParams();
  getList();
}

function handleContinueTask(row) {
  handleDetail(row);
}

async function handleTaskCommand(command, row) {
  if (!row?.taskId) {
    return;
  }
  if (command === 'detail') {
    await handleDetail(row);
    return;
  }
  if (command === 'edit') {
    await handleUpdate(row);
    return;
  }
  if (command === 'autoRun') {
    await handleAutoRun(row);
    return;
  }
  if (command === 'exportWorkbook') {
    await handleExportWorkbook(row);
    return;
  }
  if (command === 'importWorkbook') {
    await handleImportWorkbook(row);
    return;
  }
  if (command === 'delete') {
    await handleDelete(row);
  }
}

async function handleGenerationRecordCommand(command, row) {
  if (!row?.recordId) {
    return;
  }
  if (command === 'verify') {
    await handleVerifyGenerationRecord(row);
    return;
  }
  if (command === 'package') {
    handleDownloadGenerationFile(row, 'package');
    return;
  }
  if (command === 'manifest') {
    handleDownloadGenerationFile(row, 'manifest');
    return;
  }
  if (command === 'testReport') {
    handleDownloadGenerationFile(row, 'testReport');
    return;
  }
  if (command === 'confirm') {
    await handleConfirmGenerationRecord(row);
  }
}

async function handleDetailPrimaryAction() {
  const action = detailPrimaryAction.value;
  if (!action || action.disabled) {
    return;
  }
  if (action.scope === 'generation') {
    await handleGenerationRecordCommand(action.command, latestGenerationRecord.value);
    return;
  }
  await handleTaskCommand(action.command, detail.value);
}

async function handleUpdate(row) {
  const response = await getProtocolAdaptationTask(row.taskId);
  form.value = {
    ...createDefaultForm(),
    ...(response.data || {}),
  };
  formOpen.value = true;
  await nextTick();
  formRef.value?.clearValidate();
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    const submitData = buildSubmitData();
    await updateProtocolAdaptationTask(submitData);
    ElMessage.success(t('ai.protocolAdaptation.index.748794-109'));
    formOpen.value = false;
    await getList();
  });
}

async function handleDelete(row) {
  await ElMessageBox.confirm(
    t('ai.protocolAdaptation.index.748794-110', { name: row.taskName || (row.taskId ? `#${row.taskId}` : '-') }),
    t('ai.protocolAdaptation.index.748794-111'),
    {
      type: 'warning',
    }
  );
  await removeProtocolAdaptationTask(row.taskId);
  ElMessage.success(t('ai.protocolAdaptation.index.748794-112'));
  await getList();
}

async function handleAutoRun(row) {
  autoRunningTaskId.value = row.taskId;
  try {
    const response = await autoRunProtocolAdaptationTask(row.taskId);
    const result = response?.data || {};
    const summary = result.summary || t('ai.protocolAdaptation.index.748794-113');
    if (result.runStatus === 'COMPLETED') {
      ElMessage.success(summary);
    } else if (result.runStatus === 'COMPLETED_WITH_WARNINGS' || result.runStatus === 'NEED_REVIEW') {
      ElMessage.warning(summary);
    } else {
      ElMessage.error(summary);
    }
    await getList();
    if (detailOpen.value && currentDetailTaskId.value === row.taskId) {
      await loadTaskDetail(row.taskId);
    } else if (!detailOpen.value) {
      await handleDetail(row);
    }
  } finally {
    autoRunningTaskId.value = undefined;
  }
}

async function handleExportWorkbook(row) {
  exportingWorkbookTaskId.value = row.taskId;
  try {
    const response = await exportProtocolAdaptationWorkbook(row.taskId);
    const exportedArtifact = response?.data || null;
    await getList();
    if (detailOpen.value && currentDetailTaskId.value === row.taskId) {
      await loadTaskDetail(row.taskId);
    } else if (!detailOpen.value) {
      detailOpen.value = true;
      currentDetailTaskId.value = row.taskId;
      await loadTaskDetail(row.taskId);
    }
    const workbookArtifact = exportedArtifact?.artifactId ? exportedArtifact : latestEnterpriseWorkbookArtifact.value;
    if (workbookArtifact?.artifactId) {
      ElMessage.success(t('ai.protocolAdaptation.index.748794-114'));
      await handleDownloadArtifact(workbookArtifact);
    } else {
      ElMessage.success(t('ai.protocolAdaptation.index.748794-115'));
    }
  } finally {
    exportingWorkbookTaskId.value = undefined;
  }
}

async function handleImportWorkbook(row) {
  currentWorkbookTask.value = row;
  workbookImportFile.value = null;
  workbookImportFileList.value = [];
  workbookImportRef.value?.clearFiles?.();
  workbookImportOpen.value = true;
}

function handleWorkbookFileChange(file, fileList) {
  workbookImportFile.value = file.raw || null;
  workbookImportFileList.value = fileList.slice(-1);
}

function handleWorkbookFileRemove(_file, fileList) {
  workbookImportFile.value = null;
  workbookImportFileList.value = fileList;
}

async function submitWorkbookImport() {
  if (!currentWorkbookTask.value?.taskId) {
    return;
  }
  if (!workbookImportFile.value) {
    ElMessage.warning(t('ai.protocolAdaptation.index.748794-116'));
    return;
  }
  const formData = new FormData();
  formData.append('file', workbookImportFile.value);
  const taskId = currentWorkbookTask.value.taskId;
  workbookImporting.value = true;
  importingWorkbookTaskId.value = taskId;
  try {
    await importProtocolAdaptationWorkbook(taskId, formData);
    ElMessage.success(t('ai.protocolAdaptation.index.748794-117'));
    workbookImportOpen.value = false;
    await getList();
    if (detailOpen.value && currentDetailTaskId.value === taskId) {
      await loadTaskDetail(taskId);
    } else if (!detailOpen.value) {
      await handleDetail({ taskId });
    }
  } finally {
    workbookImporting.value = false;
    importingWorkbookTaskId.value = undefined;
  }
}

function resolveGenerationFileName(row, fileType) {
  const filePathMap = {
    package: row?.codePackagePath,
    manifest: row?.fileManifestPath,
    testReport: row?.testReportPath,
  };
  const fallbackMap = {
    package: `protocol_package_${row?.recordId || Date.now()}.zip`,
    manifest: `protocol_manifest_${row?.recordId || Date.now()}.json`,
    testReport: `protocol_test_report_${row?.recordId || Date.now()}.json`,
  };
  const filePath = filePathMap[fileType] || '';
  const fileName = String(filePath).split(/[\\/]/).pop();
  return fileName || fallbackMap[fileType] || `protocol_generation_${row?.recordId || Date.now()}`;
}

function handleDownloadGenerationFile(row, fileType) {
  if (!row?.recordId) {
    return;
  }
  downloadProtocolGenerationFile(row.recordId, fileType, resolveGenerationFileName(row, fileType));
}

function handleDownloadArtifact(row) {
  if (!row?.artifactId) {
    return Promise.resolve();
  }
  return downloadProtocolAdaptationArtifact(row.artifactId, resolveArtifactFileName(row));
}

async function handlePreviewArtifact(row) {
  if (!row?.artifactId) {
    return;
  }
  artifactPreviewOpen.value = true;
  artifactPreviewLoading.value = true;
  artifactPreviewTitle.value = row.artifactName || t('ai.protocolAdaptation.index.748794-51');
  artifactPreviewContent.value = '';
  try {
    const response = await getProtocolAdaptationArtifactContent(row.artifactId);
    artifactPreviewContent.value = formatArtifactPreviewContent(response?.data);
  } finally {
    artifactPreviewLoading.value = false;
  }
}

async function handleVerifyGenerationRecord(row) {
  if (!row?.recordId) {
    return;
  }
  verifyingGenerationRecordId.value = row.recordId;
  try {
    await verifyProtocolGenerationRecord(row.recordId);
    ElMessage.success(t('ai.protocolAdaptation.index.748794-118'));
    await getList();
    if (detailOpen.value && row.taskId && currentDetailTaskId.value === row.taskId) {
      await loadTaskDetail(row.taskId);
    }
  } finally {
    verifyingGenerationRecordId.value = undefined;
  }
}

async function handleConfirmGenerationRecord(row) {
  if (!row?.recordId) {
    return;
  }
  await ElMessageBox.confirm(
    t('ai.protocolAdaptation.index.748794-119', { recordId: row.recordId }),
    t('ai.protocolAdaptation.index.748794-111'),
    {
      type: 'warning',
    }
  );
  confirmingGenerationRecordId.value = row.recordId;
  try {
    await confirmProtocolGenerationRecord(row.recordId);
    ElMessage.success(t('ai.protocolAdaptation.index.748794-120'));
    await getList();
    if (detailOpen.value && row.taskId && currentDetailTaskId.value === row.taskId) {
      await loadTaskDetail(row.taskId);
    }
  } finally {
    confirmingGenerationRecordId.value = undefined;
  }
}

async function handleDetail(row) {
  detailOpen.value = true;
  currentDetailTaskId.value = row.taskId;
  await loadTaskDetail(row.taskId);
}

async function loadTaskDetail(taskId) {
  detailLoading.value = true;
  detail.value = {};
  artifactList.value = [];
  generationRecordList.value = [];
  try {
    const [detailResponse, artifactResponse, generationResponse] = await Promise.all([
      getProtocolAdaptationTask(taskId),
      listProtocolAdaptationArtifacts(taskId),
      listProtocolAdaptationGenerationRecords(taskId),
    ]);
    detail.value = detailResponse.data || {};
    artifactList.value = artifactResponse.data || [];
    generationRecordList.value = generationResponse.data || [];
  } finally {
    detailLoading.value = false;
  }
}

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
.ai-protocol-adaptation-page {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
}

.search-card :deep(.el-form-item) {
  margin-bottom: 12px;
}

.toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.toolbar-row__left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.task-operation-cell,
.artifact-operation-cell,
.generation-operation-cell {
  display: flex;
  align-items: center;
  gap: 4px 8px;
  min-width: 0;
}

.task-operation-cell :deep(.el-button),
.artifact-operation-cell :deep(.el-button),
.generation-operation-cell :deep(.el-button) {
  margin-left: 0;
  min-height: 24px;
}

.task-name-cell,
.protocol-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.task-name-link {
  justify-content: flex-start;
  padding-left: 0;
  min-height: 22px;
}

.detail-drawer {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 0 4px 24px;
}

.detail-workbench {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  background: var(--el-fill-color-blank);
}

.detail-workbench__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.detail-workbench__main {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.detail-workbench__eyebrow {
  color: var(--el-color-primary);
  font-size: 12px;
  line-height: 18px;
}

.detail-workbench__name {
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
  word-break: break-word;
}

.detail-workbench__summary {
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 20px;
  word-break: break-word;
}

.detail-workbench__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-workbench__status-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.quality-gate-alert__body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  line-height: 20px;
}

.quality-gate-alert__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.status-tile {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-width: 0;
  padding: 10px 12px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
}

.status-tile > span {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 18px;
  white-space: nowrap;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-section__title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
  line-height: 22px;
}

.artifact-preview {
  min-height: 220px;
}

.artifact-preview pre {
  max-height: 60vh;
  margin: 0;
  padding: 14px;
  overflow: auto;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  font-size: 12px;
  line-height: 20px;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 768px) {
  .toolbar-row {
    align-items: stretch;
  }

  .toolbar-row__left {
    width: 100%;
  }

  .detail-workbench__header {
    flex-direction: column;
  }

  .detail-workbench__actions {
    width: 100%;
    justify-content: flex-start;
  }

  .detail-workbench__status-grid {
    grid-template-columns: 1fr;
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

.ai-dialog-form :deep(.el-row) {
  width: 400px;
  max-width: 100%;
}

.ai-dialog-form :deep(.el-col) {
  flex: 0 0 100%;
  max-width: 100%;
}
</style>
