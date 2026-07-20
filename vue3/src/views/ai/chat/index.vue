<template>
  <div :class="['ai-chat-page', { 'is-sidebar-collapsed': sidebarCollapsed, 'has-right-panel': rightPanelVisible }]">
    <div class="chat-layout">
      <el-card class="session-card" v-loading="sessionLoading">
        <template #header>
          <div class="session-shell-header">
            <div class="session-brand">
              <img src="/favicon.ico" alt="FastBee" class="session-brand-logo" />
              <span v-if="!sidebarCollapsed" class="session-brand-name">{{ currentSkillBrandName }}</span>
            </div>
            <div class="session-header-actions">
              <el-tooltip v-if="!sidebarCollapsed" :content="t('ai.chat.index.748794-358')" placement="bottom">
                <el-button text circle :icon="QuestionFilled" @click.stop="handleOpenUsageGuidePanel" />
              </el-tooltip>
              <el-tooltip v-if="!sidebarCollapsed" :content="t('ai.chat.index.748794-0')" placement="bottom">
                <el-button text circle :icon="View" @click.stop="handleOpenObservabilityPanel" />
              </el-tooltip>
              <el-tooltip v-if="!sidebarCollapsed" :content="t('ai.chat.index.748794-1')" placement="bottom">
                <el-button text circle :icon="Setting" @click.stop="openModeStrategyDialog" />
              </el-tooltip>
              <el-tooltip
                :content="sidebarCollapsed ? t('ai.chat.index.748794-2') : t('ai.chat.index.748794-3')"
                placement="bottom"
              >
                <el-button
                  text
                  circle
                  :icon="sidebarCollapsed ? Expand : Fold"
                  @click.stop="sidebarCollapsed = !sidebarCollapsed"
                />
              </el-tooltip>
              <el-tooltip v-if="sidebarCollapsed" :content="t('ai.chat.index.748794-4')" placement="bottom">
                <el-button text circle :icon="Plus" @click.stop="handleNewSession" />
              </el-tooltip>
            </div>
          </div>
        </template>
        <template v-if="!sidebarCollapsed">
          <div class="session-new-row">
            <el-button
              class="session-new-button"
              round
              :icon="Plus"
              @click="handleNewSession"
              v-hasPermi="['ai:chat:add']"
            >
              {{ t('ai.chat.index.748794-4') }}
            </el-button>
          </div>
          <div class="session-scroll">
            <div v-if="sessionList.length === 0" class="session-empty">
              <el-empty :description="t('ai.chat.index.748794-5')" />
            </div>
            <div v-else class="session-list">
              <div v-for="section in groupedSessionSections" :key="section.label" class="session-section">
                <div class="session-section-title">{{ section.label }}</div>
                <div
                  v-for="item in section.items"
                  :key="item.sessionId"
                  :class="['session-item', { active: item.sessionId === activeSessionId }]"
                  @click="handleSelectSession(item)"
                >
                  <div class="session-main">
                    <div class="session-title">{{ item.sessionTitle || t('ai.chat.index.748794-6') }}</div>
                  </div>
                  <el-dropdown
                    trigger="click"
                    class="session-more-dropdown"
                    @command="(command) => handleSessionMenuCommand(command, item)"
                  >
                    <el-button text circle :icon="MoreFilled" class="session-more-button" @click.stop />
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="rename">{{ t('ai.chat.index.748794-7') }}</el-dropdown-item>
                        <el-dropdown-item v-if="checkPermi(['ai:chatRecord:remove'])" command="archive">
                          {{ t('ai.chat.index.748794-8') }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-card>

      <el-card :class="['message-card', { 'is-empty-chat': messageList.length === 0 }]" v-loading="messageLoading">
        <template v-if="messageList.length > 0" #header>
          <div class="message-header">
            <div class="conversation-title-block">
              <div class="conversation-title">{{ currentSessionTitle }}</div>
              <div class="conversation-subtitle">{{ conversationStrategyLabel }}</div>
            </div>
          </div>
        </template>
        <div v-if="messageList.length === 0" class="welcome-panel">
          <div class="welcome-center">
            <div class="welcome-title-row">
              <img src="/favicon.ico" alt="FastBee" class="welcome-logo" />
              <h3>{{ t('ai.chat.index.748794-9') }}</h3>
            </div>
            <div class="welcome-mode-actions">
              <el-button
                :type="welcomeAdvancedModeActive ? '' : 'primary'"
                plain
                round
                :icon="View"
                @click="handleSelectAutoMode"
              >
                {{ t('ai.chat.index.748794-10') }}
              </el-button>
              <el-button
                :type="welcomeAdvancedModeActive ? 'primary' : ''"
                plain
                round
                :icon="Setting"
                @click="openModeStrategyDialog"
              >
                {{ t('ai.chat.index.748794-1') }}
              </el-button>
            </div>
            <p>{{ t('ai.chat.index.748794-11') }}</p>
          </div>
        </div>
        <div v-else ref="messageListRef" class="message-list" @scroll="handleMessageListScroll">
          <div
            v-for="group in messageGroups"
            :key="group.groupKey"
            :class="['message-group', { active: isMessageGroupActive(group) }]"
          >
            <div class="message-group-content">
              <div class="message-group-body">
                <div
                  v-for="item in group.items"
                  :key="item.messageId"
                  :class="['message-item', item.roleType, { active: isMessageResultSelected(item) }]"
                  @click="handlePreviewMessageResult(item)"
                >
                  <div class="message-role">{{ item.roleType === 'user' ? t('ai.chat.index.748794-12') : 'AI' }}</div>
                  <div class="message-bubble">
                    <div class="message-meta">
                      <span>{{ item.createTime || '' }}</span>
                      <el-tag
                        v-if="item.roleType === 'assistant' && resolveDisplayMessageAbility(item)"
                        size="small"
                        effect="plain"
                      >
                        {{ getModeLabel(resolveDisplayMessageAbility(item)) }}
                      </el-tag>
                      <el-tag v-if="item.roleType === 'user' && item.modelCode" size="small" type="info" effect="plain">
                        {{ item.modelCode }}
                      </el-tag>
                    </div>
                    <div
                      :class="[
                        'message-content',
                        { 'is-thinking': shouldShowThinking(item), 'is-markdown': shouldRenderMarkdown(item) },
                      ]"
                      @click="handleMessageContentClick"
                    >
                      <template v-if="shouldShowThinking(item)">
                        <span class="message-thinking-dot"></span>
                        <span>{{ t('ai.chat.index.748794-393', [formatThinkingDuration(item)]) }}</span>
                      </template>
                      <div v-else-if="shouldRenderMarkdown(item)" v-html="renderMarkdown(item.messageContent)"></div>
                      <template v-else>{{ item.messageContent }}</template>
                    </div>
                    <div v-if="clarifyPayloadMap[item.messageId]?.clarifyOptions?.length" class="clarify-box">
                      <div class="clarify-title">
                        {{ clarifyPayloadMap[item.messageId].clarifyTitle || t('ai.chat.index.748794-13') }}
                      </div>
                      <div class="clarify-options">
                        <el-button
                          v-for="option in clarifyPayloadMap[item.messageId].clarifyOptions"
                          :key="`${item.messageId}-${option.value}`"
                          size="small"
                          plain
                          @click.stop="handleClarifyOptionSelect(item, clarifyPayloadMap[item.messageId], option)"
                        >
                          {{ option.label || option.value }}
                        </el-button>
                      </div>
                      <div class="clarify-tip">
                        <div
                          v-for="option in clarifyPayloadMap[item.messageId].clarifyOptions"
                          :key="`${item.messageId}-${option.value}-tip`"
                          class="clarify-tip-item"
                        >
                          <span class="clarify-tip-label">{{ option.label || option.value }}</span>
                          <span class="clarify-tip-value">{{ option.description || option.value }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="message-actions">
                      <el-tooltip :content="t('ai.chat.index.748794-14')" placement="top">
                        <el-button
                          class="message-copy-button"
                          text
                          circle
                          :icon="CopyDocument"
                          @click.stop="handleCopyMessage(item)"
                        />
                      </el-tooltip>
                      <el-tooltip
                        v-if="item.roleType === 'assistant'"
                        :content="t('ai.chat.index.748794-15')"
                        placement="top"
                      >
                        <el-button
                          class="message-result-button"
                          text
                          circle
                          :icon="View"
                          @click.stop="handleOpenResultPanel(item)"
                        />
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-tooltip
          v-if="messageList.length > 0 && showScrollToBottomButton"
          :content="t('ai.chat.index.748794-16')"
          placement="top"
        >
          <el-button class="scroll-bottom-button" circle :icon="Bottom" @click="handleScrollToLatest" />
        </el-tooltip>

        <div class="composer">
          <div class="composer-card">
            <div class="composer-input-wrap">
              <el-input
                ref="composerInputRef"
                v-model="draftMessage"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 5 }"
                resize="none"
                :placeholder="getComposerPlaceholder(composerMode)"
                @input="handleComposerInput"
                @focus="handleComposerFocus"
                @blur="handleComposerBlur"
                @keydown="handleComposerKeydown"
                @keyup.ctrl.enter="handleSend"
              />
              <div v-if="quickPromptVisible" class="composer-quick-prompt" @mousedown.prevent>
                <div class="composer-quick-prompt__header">
                  <span>{{ t('ai.chat.index.748794-17') }}</span>
                  <span>{{ t('ai.chat.index.748794-18') }}</span>
                </div>
                <button
                  v-for="(item, index) in quickPromptExamples"
                  :key="item.code"
                  type="button"
                  :class="['composer-quick-prompt__item', { active: quickPromptActiveIndex === index }]"
                  @mouseenter="quickPromptActiveIndex = index"
                  @mousedown.prevent="handleQuickPromptSelect(item)"
                >
                  <span class="composer-quick-prompt__text">{{ item.content }}</span>
                  <span class="composer-quick-prompt__tag">{{ item.modeLabel }}</span>
                </button>
              </div>
            </div>
            <div v-if="selectedUploadFiles.length > 0" class="composer-file-list">
              <el-tag
                v-for="file in selectedUploadFiles"
                :key="file.uid"
                closable
                type="info"
                effect="plain"
                @close="handleRemoveComposerFile(file.uid)"
              >
                {{ file.name }}
              </el-tag>
            </div>
            <div class="composer-bottom">
              <div class="composer-left-actions">
                <el-tooltip :content="t('ai.chat.index.748794-19')" placement="top">
                  <el-upload
                    ref="composerUploadRef"
                    action="#"
                    accept=".pdf,.doc,.docx,.txt,.md,.xls,.xlsx,.csv,.json"
                    :auto-upload="false"
                    :limit="1"
                    :show-file-list="false"
                    :on-change="handleComposerFileChange"
                    :on-exceed="handleComposerFileExceed"
                  >
                    <el-button class="composer-upload-button" text circle :icon="Plus" />
                  </el-upload>
                </el-tooltip>
              </div>
              <div class="composer-right-actions">
                <el-select
                  v-model="selectedModel"
                  class="composer-model-select"
                  :style="{ width: composerModelSelectWidth }"
                  :placeholder="t('ai.chat.index.748794-20')"
                  filterable
                  @visible-change="handleModelDropdownVisibleChange"
                >
                  <el-option-group v-for="group in groupedModels" :key="group.providerCode" :label="group.label">
                    <el-option
                      v-for="item in group.options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-option-group>
                </el-select>
                <el-tooltip :content="t('ai.chat.index.748794-21')" placement="top">
                  <el-button circle :icon="Microphone" disabled />
                </el-tooltip>
                <el-tooltip
                  :content="sending ? t('ai.chat.index.748794-22') : t('ai.chat.index.748794-23')"
                  placement="top"
                >
                  <el-button
                    class="composer-send-button"
                    type="primary"
                    circle
                    @click="handleComposerSendAction"
                    v-hasPermi="['ai:chat:add']"
                  >
                    <span v-if="sending" class="composer-stop-icon"></span>
                    <el-icon v-else><Top /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card v-if="rightPanelVisible" class="result-card">
        <template #header>
          <div
            :class="[
              'result-header',
              {
                'is-observability': rightPanelType === 'observability',
                'is-usage-guide': rightPanelType === 'usageGuide',
              },
            ]"
          >
            <template v-if="rightPanelType === 'result'">
              <div class="result-header-row result-header-main">
                <div class="card-title">{{ t('ai.chat.index.748794-24') }}</div>
                <div v-hasPermi="['ai:chat:query']" class="result-debug-switch">
                  <span class="result-debug-switch-label">{{ t('ai.chat.index.748794-25') }}</span>
                  <el-switch
                    v-model="localRouteDebugEnabled"
                    inline-prompt
                    :active-text="t('ai.chat.index.748794-26')"
                    :inactive-text="t('ai.chat.index.748794-27')"
                    @change="handleRouteDebugPreferenceChange"
                  />
                </div>
                <el-button text circle :icon="Fold" :title="t('ai.chat.index.748794-28')" @click="closeRightPanel" />
              </div>
              <div class="result-header-row result-header-secondary">
                <el-button v-if="isViewingHistoryResult" @click="handleResetResultSelection">
                  {{ t('ai.chat.index.748794-29') }}
                </el-button>
                <el-button
                  :disabled="!latestUserQuestion"
                  @click="handleRetryLastQuestion"
                  v-hasPermi="['ai:chat:add']"
                >
                  {{ t('ai.chat.index.748794-30') }}
                </el-button>
                <el-dropdown
                  v-if="quickRetryModeOptions.length > 0 && checkPermi(['ai:chat:add'])"
                  :disabled="!latestUserQuestion || sending"
                  trigger="click"
                  @command="handleRetryQuestionByMode"
                >
                  <el-button :disabled="!latestUserQuestion || sending">{{ t('ai.chat.index.748794-31') }}</el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-for="item in quickRetryModeOptions" :key="item.value" :command="item.value">
                        {{ t('ai.chat.index.748794-394', [item.label]) }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                <el-button v-if="latestSqlText" type="primary" plain @click="handleCopySql">
                  {{ t('ai.chat.index.748794-32') }}
                </el-button>
              </div>
            </template>
            <template v-else-if="rightPanelType === 'observability'">
              <div class="result-header-row result-header-main">
                <div class="card-title">{{ t('ai.chat.index.748794-0') }}</div>
                <el-tag type="info">{{ observabilityScopeLabel }}</el-tag>
                <el-tag type="success">{{ observabilityWindowLabel }}</el-tag>
                <el-button
                  type="primary"
                  link
                  :icon="Refresh"
                  :loading="observabilityLoading"
                  @click="handleRefreshObservabilityStats"
                >
                  {{ t('ai.chat.index.748794-33') }}
                </el-button>
                <el-button text circle :icon="Fold" :title="t('ai.chat.index.748794-28')" @click="closeRightPanel" />
              </div>
            </template>
            <template v-else-if="rightPanelType === 'usageGuide'">
              <div class="result-header-row result-header-main">
                <div class="card-title">{{ t('ai.chat.index.748794-358') }}</div>
                <el-button text circle :icon="Fold" :title="t('ai.chat.index.748794-28')" @click="closeRightPanel" />
              </div>
            </template>
          </div>
        </template>

        <div v-if="rightPanelType === 'result'" class="result-scroll">
          <div class="section-header">
            <div class="section-title no-margin">{{ t('ai.chat.index.748794-34') }}</div>
            <el-button text @click="toggleMetaExpanded">
              {{ metaExpanded ? t('ai.chat.index.748794-35') : t('ai.chat.index.748794-36') }}
            </el-button>
          </div>
          <div class="result-meta result-meta-compact">
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-37') }}</span>
              <span class="result-value">{{ getModeLabel(latestEffectiveMode) }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-38') }}</span>
              <span class="result-value">{{ latestToolName || t('ai.chat.index.748794-39') }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-40') }}</span>
              <span class="result-value result-text">{{ latestUserQuestion || t('ai.chat.index.748794-41') }}</span>
            </div>
          </div>
          <div v-if="quickRetryModeOptions.length > 0 && latestUserQuestion" class="summary-box">
            {{ t('ai.chat.index.748794-42') }}
          </div>
          <div v-if="metaExpanded" class="result-meta result-meta-detail">
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-43') }}</span>
              <span class="result-value">{{ currentResultModelLabel || t('ai.chat.index.748794-41') }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-44') }}</span>
              <span class="result-value">{{ getModeLabel(latestEffectiveMode) || t('ai.chat.index.748794-41') }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-45') }}</span>
              <span class="result-value">{{ currentSessionCode || t('ai.chat.index.748794-46') }}</span>
            </div>
            <div class="result-item">
              <span class="result-label">{{ t('ai.chat.index.748794-47') }}</span>
              <span class="result-value">{{ currentResultMessageTime || t('ai.chat.index.748794-41') }}</span>
            </div>
          </div>

          <div v-if="routeDebugEnabled" v-hasPermi="['ai:chat:query']" class="result-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-48') }}</div>
              <el-button v-if="latestRouteAuditPretty" text @click="toggleRouteAuditRawExpanded">
                {{ routeAuditRawExpanded ? t('ai.chat.index.748794-49') : t('ai.chat.index.748794-50') }}
              </el-button>
            </div>
            <div v-if="latestRouteAudit">
              <div class="stats-row">
                <el-tag type="info" effect="plain">{{ t('ai.chat.index.748794-395') }}{{ getModeLabel(latestRouteAudit.requestedMode) }}</el-tag>
                <el-tag type="success" effect="plain">{{ t('ai.chat.index.748794-396') }}{{ getModeLabel(latestRouteAudit.aiMode) }}</el-tag>
                <el-tag v-if="latestRouteAudit.finalMode" type="warning" effect="plain">
                  {{ t('ai.chat.index.748794-397') }}{{ getModeLabel(latestRouteAudit.finalMode) }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.ruleMode" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-398') }}{{ getModeLabel(latestRouteAudit.ruleMode) }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.confidenceLabel" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-399') }}{{ latestRouteAudit.confidenceLabel }}
                </el-tag>
              </div>
              <div class="stats-row">
                <el-tag v-if="latestRouteAudit.businessTypeLabel" type="success" effect="plain">
                  {{ t('ai.chat.index.748794-400') }}{{ latestRouteAudit.businessTypeLabel }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.subjectTypeLabel" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-401') }}{{ latestRouteAudit.subjectTypeLabel }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.thingModelTypeLabel" type="warning" effect="plain">
                  {{ t('ai.chat.index.748794-402') }}{{ latestRouteAudit.thingModelTypeLabel }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.timeIntentLabel" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-403') }}{{ latestRouteAudit.timeIntentLabel }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.aggregateTypeLabel" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-404') }}{{ latestRouteAudit.aggregateTypeLabel }}
                </el-tag>
              </div>
              <div class="stats-row">
                <el-tag :type="latestRouteAudit.adoptedBySystem ? 'success' : 'warning'" effect="plain">
                  {{ t('ai.chat.index.748794-405') }}{{
                    latestRouteAudit.adoptedBySystem ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </el-tag>
                <el-tag :type="latestRouteAudit.matchedRuleMode ? 'success' : 'warning'" effect="plain">
                  {{ t('ai.chat.index.748794-406') }}{{
                    latestRouteAudit.matchedRuleMode ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </el-tag>
                <el-tag :type="latestRouteAudit.needClarify ? 'warning' : 'success'" effect="plain">
                  {{ t('ai.chat.index.748794-407') }}{{
                    latestRouteAudit.needClarify ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </el-tag>
                <el-tag v-if="latestRouteAudit.parseStatus" type="info" effect="plain">
                  {{ t('ai.chat.index.748794-408') }}{{ latestRouteAudit.parseStatus }}
                </el-tag>
                <el-tag
                  v-if="latestRouteAudit.structuredOutput !== null"
                  :type="latestRouteAudit.structuredOutput ? 'success' : 'warning'"
                  effect="plain"
                >
                  {{ t('ai.chat.index.748794-409') }}{{
                    latestRouteAudit.structuredOutput ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </el-tag>
              </div>
              <div v-if="latestRouteAudit.performanceItems.length" class="stats-row route-performance-row">
                <el-tag v-for="item in latestRouteAudit.performanceItems" :key="item.key" type="info" effect="plain">
                  {{ item.label }}：{{ item.value }}
                </el-tag>
              </div>
              <div v-if="latestRouteAudit.reason" class="summary-box">{{ t('ai.chat.index.748794-410') }}{{ latestRouteAudit.reason }}</div>
              <div v-if="latestRouteAudit.fallbackReason" class="summary-box route-debug-fallback">
                {{ t('ai.chat.index.748794-411') }}{{ latestRouteAudit.fallbackReason }}
              </div>
              <div v-if="latestRouteAudit.parseErrorText" class="summary-box route-debug-error">
                {{ t('ai.chat.index.748794-412') }}{{ latestRouteAudit.parseErrorText }}
              </div>
              <div v-if="latestRouteAudit.slotItems.length" class="table-section">
                <div class="section-subtitle">{{ t('ai.chat.index.748794-53') }}</div>
                <div class="info-list">
                  <div v-for="slot in latestRouteAudit.slotItems" :key="slot.label" class="info-item">
                    {{ slot.label }}：{{ slot.value }}
                  </div>
                </div>
              </div>
              <div v-if="routeAuditRawExpanded && latestRouteAuditPretty" class="table-section">
                <div class="section-subtitle">{{ t('ai.chat.index.748794-54') }}</div>
                <pre class="code-block json-block">{{ latestRouteAuditPretty }}</pre>
              </div>
            </div>
            <div v-else class="summary-box">{{ t('ai.chat.index.748794-55') }}</div>
          </div>

          <div v-if="isViewingHistoryResult" class="answer-section">
            <div class="summary-box">{{ t('ai.chat.index.748794-56') }}</div>
          </div>

          <div v-if="latestClarifyPayload" class="result-section">
            <div class="section-title">{{ t('ai.chat.index.748794-57') }}</div>
            <div class="summary-box">
              {{ latestClarifyPayload.clarifyTitle || t('ai.chat.index.748794-58') }}
            </div>
            <div class="clarify-tip">
              <div
                v-for="option in latestClarifyPayload.clarifyOptions"
                :key="`result-${option.value}`"
                class="clarify-tip-item"
              >
                <span class="clarify-tip-label">{{ option.label || option.value }}</span>
                <span class="clarify-tip-value">{{ option.description || option.value }}</span>
              </div>
            </div>
          </div>

          <div v-if="latestAssistantAnswer" class="answer-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-59') }}</div>
              <el-button v-if="canCollapseAnswer" text @click="toggleAnswerExpanded">
                {{ answerExpanded ? t('ai.chat.index.748794-60') : t('ai.chat.index.748794-61') }}
              </el-button>
            </div>
            <div
              :class="['answer-content', 'is-markdown', { collapsed: canCollapseAnswer && !answerExpanded }]"
              @click="handleMessageContentClick"
              v-html="renderMarkdown(latestAssistantAnswer)"
            ></div>
          </div>

          <div v-if="latestNl2SqlResult" class="result-section">
            <div class="section-title">{{ t('ai.chat.index.748794-62') }}</div>
            <div
              v-if="latestNl2SqlSummary"
              class="summary-box is-markdown"
              @click="handleMessageContentClick"
              v-html="renderMarkdown(latestNl2SqlSummary)"
            ></div>
            <div v-if="latestNl2SqlHighlightItems.length" class="nl2sql-highlight-grid">
              <div
                v-for="item in latestNl2SqlHighlightItems"
                :key="item.key"
                :class="['nl2sql-highlight-card', `is-${item.tone}`]"
              >
                <div class="nl2sql-highlight-label">{{ item.label }}</div>
                <div class="nl2sql-highlight-value">
                  {{ item.value }}
                  <span v-if="item.unit" class="nl2sql-highlight-unit">{{ item.unit }}</span>
                </div>
                <div v-if="item.tip" class="nl2sql-highlight-tip">{{ item.tip }}</div>
              </div>
            </div>
            <div class="stats-row">
              <el-tag type="info" effect="plain">{{ t('ai.chat.index.748794-413') }}{{ getQueryModeLabel(latestNl2SqlMode) }}</el-tag>
              <el-tag v-if="latestNl2SqlPrimarySemantic" type="success" effect="plain">
                {{ t('ai.chat.index.748794-414') }}{{ latestNl2SqlPrimarySemantic }}
              </el-tag>
              <el-tag v-if="latestNl2SqlRuntimeSource" type="warning" effect="plain">
                {{ t('ai.chat.index.748794-415') }}{{ latestNl2SqlRuntimeSource }}
              </el-tag>
              <el-tag
                v-if="latestNl2SqlRdbResult"
                :type="latestNl2SqlStructuredOutput ? 'success' : 'warning'"
                effect="plain"
              >
                {{ t('ai.chat.index.748794-416') }}{{
                  latestNl2SqlStructuredOutput ? t('ai.chat.index.748794-63') : t('ai.chat.index.748794-64')
                }}
              </el-tag>
              <el-tag v-if="latestNl2SqlParseStatus" type="info" effect="plain">
                {{ t('ai.chat.index.748794-408') }}{{ latestNl2SqlParseStatus }}
              </el-tag>
              <el-tag v-if="latestNl2SqlConfidenceLabel" type="info" effect="plain">
                {{ t('ai.chat.index.748794-399') }}{{ latestNl2SqlConfidenceLabel }}
              </el-tag>
            </div>
            <div v-if="latestNl2SqlTables.length && latestNl2SqlRdbResult" class="stats-row">
              <el-tag v-for="table in latestNl2SqlTables" :key="table" type="warning" effect="plain">
                {{ t('ai.chat.index.748794-417') }}{{ table }}
              </el-tag>
            </div>

            <div v-if="latestNl2SqlRdbResult" class="sql-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-65') }}</div>
              <pre class="code-block">{{ latestSqlText }}</pre>
            </div>

            <div v-if="latestNl2SqlRdbResult" class="stats-row">
              <el-tag type="success" effect="plain">{{ t('ai.chat.index.748794-418') }}{{ latestRowCount }}</el-tag>
              <el-tag type="info" effect="plain">{{ t('ai.chat.index.748794-419') }}{{ latestMaxRows }}</el-tag>
              <el-tag :type="latestDataScopeApplied ? 'warning' : 'info'" effect="plain">
                {{ t('ai.chat.index.748794-420') }}{{ latestDataScopeApplied ? t('ai.chat.index.748794-66') : t('ai.chat.index.748794-67') }}
              </el-tag>
            </div>

            <div v-if="latestNl2SqlRdbResult" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-68') }}</div>
              <el-table v-if="resultTableRows.length > 0" :data="resultTableRows" border size="small" height="280">
                <el-table-column
                  v-for="column in resultTableColumns"
                  :key="column"
                  :prop="column"
                  :label="column"
                  min-width="140"
                  show-overflow-tooltip
                >
                  <template #default="scope">
                    <span :class="getNl2SqlCellClass(column, scope.row[column])">
                      {{ formatCellValue(scope.row[column]) }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else :description="t('ai.chat.index.748794-69')" />
            </div>

            <div v-else-if="latestNl2SqlRealtimeResult" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-70') }}</div>
              <div class="info-list">
                <div class="info-item">{{ t('ai.chat.index.748794-421') }}{{ latestNl2SqlRealtimeResult.serialNumber || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-422') }}{{ latestNl2SqlRealtimeResult.deviceName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-423') }}{{ latestNl2SqlRealtimeResult.semanticName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-424') }}{{ latestNl2SqlRealtimeResult.identifier || '-' }}</div>
                <div class="info-item is-key">
                  {{ t('ai.chat.index.748794-425') }}{{ latestNl2SqlRealtimeResult.currentValue || '-' }}
                  <span v-if="latestNl2SqlRealtimeResult.unit">{{ latestNl2SqlRealtimeResult.unit }}</span>
                </div>
                <div class="info-item is-key">{{ t('ai.chat.index.748794-426') }}{{ latestNl2SqlRealtimeResult.reportTime || '-' }}</div>
                <div class="info-item is-key">
                  {{ t('ai.chat.index.748794-427') }}{{
                    latestNl2SqlRealtimeResult.cacheHit ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </div>
              </div>
            </div>

            <div v-else-if="latestNl2SqlTsdbResult" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-71') }}</div>
              <div class="info-list">
                <div class="info-item">{{ t('ai.chat.index.748794-428') }}{{ latestNl2SqlTsdbResult.queryType || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-421') }}{{ latestNl2SqlTsdbResult.serialNumber || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-422') }}{{ latestNl2SqlTsdbResult.deviceName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-423') }}{{ latestNl2SqlTsdbResult.semanticName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-424') }}{{ latestNl2SqlTsdbResult.identifier || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-429') }}{{ latestNl2SqlTsdbResult.timeWindowLabel || '-' }}</div>
                <div v-if="hasDisplayValue(latestNl2SqlTsdbResult.latestValue)" class="info-item is-key">
                  {{ t('ai.chat.index.748794-430') }}{{ latestNl2SqlTsdbResult.latestValue }}
                  <span v-if="latestNl2SqlTsdbResult.unit">{{ latestNl2SqlTsdbResult.unit }}</span>
                </div>
                <div v-if="latestNl2SqlTsdbResult.latestTime" class="info-item is-key">
                  {{ t('ai.chat.index.748794-431') }}{{ latestNl2SqlTsdbResult.latestTime }}
                </div>
                <div v-if="latestNl2SqlTsdbResult.statisticOperation" class="info-item">
                  {{ t('ai.chat.index.748794-432') }}{{ latestNl2SqlTsdbResult.statisticOperation }}
                </div>
                <div v-if="hasDisplayValue(latestNl2SqlTsdbResult.statisticValue)" class="info-item is-key">
                  {{ t('ai.chat.index.748794-433') }}{{ latestNl2SqlTsdbResult.statisticValue }}
                  <span v-if="latestNl2SqlTsdbResult.unit">{{ latestNl2SqlTsdbResult.unit }}</span>
                </div>
                <div class="info-item is-key">{{ t('ai.chat.index.748794-434') }}{{ latestNl2SqlTsdbResult.historyPoints?.length || 0 }}</div>
                <div class="info-item is-key">{{ t('ai.chat.index.748794-435') }}{{ latestNl2SqlTsdbResult.statisticSamples ?? '-' }}</div>
              </div>
            </div>

            <div v-else-if="latestNl2SqlHybridResult" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-72') }}</div>
              <div class="info-list">
                <div class="info-item">{{ t('ai.chat.index.748794-436') }}{{ latestNl2SqlHybridResult.queryMode || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-437') }}{{ latestNl2SqlHybridResult.finalQueryMode || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-428') }}{{ latestNl2SqlHybridResult.queryType || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-421') }}{{ latestNl2SqlHybridResult.serialNumber || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-422') }}{{ latestNl2SqlHybridResult.deviceName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-423') }}{{ latestNl2SqlHybridResult.semanticName || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-424') }}{{ latestNl2SqlHybridResult.identifier || '-' }}</div>
                <div class="info-item">{{ t('ai.chat.index.748794-429') }}{{ latestNl2SqlHybridResult.timeWindowLabel || '-' }}</div>
                <div v-if="hasDisplayValue(latestNl2SqlHybridResult.currentValue)" class="info-item is-key">
                  {{ t('ai.chat.index.748794-425') }}{{ latestNl2SqlHybridResult.currentValue }}
                  <span v-if="latestNl2SqlHybridResult.unit">{{ latestNl2SqlHybridResult.unit }}</span>
                </div>
                <div v-if="latestNl2SqlHybridResult.currentTime" class="info-item is-key">
                  {{ t('ai.chat.index.748794-438') }}{{ latestNl2SqlHybridResult.currentTime }}
                </div>
                <div v-if="latestNl2SqlHybridResult.statisticOperation" class="info-item">
                  {{ t('ai.chat.index.748794-432') }}{{ latestNl2SqlHybridResult.statisticOperation }}
                </div>
                <div v-if="hasDisplayValue(latestNl2SqlHybridResult.statisticValue)" class="info-item is-key">
                  {{ t('ai.chat.index.748794-433') }}{{ latestNl2SqlHybridResult.statisticValue }}
                  <span v-if="latestNl2SqlHybridResult.unit">{{ latestNl2SqlHybridResult.unit }}</span>
                </div>
                <div class="info-item is-key">{{ t('ai.chat.index.748794-434') }}{{ latestNl2SqlHybridResult.historyPoints?.length || 0 }}</div>
                <div class="info-item is-key">{{ t('ai.chat.index.748794-435') }}{{ latestNl2SqlHybridResult.statisticSamples ?? '-' }}</div>
                <div class="info-item is-key">
                  {{ t('ai.chat.index.748794-439') }}{{
                    latestNl2SqlHybridResult.fallbackUsed ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52')
                  }}
                </div>
                <div v-if="latestNl2SqlHybridResult.routeReason" class="info-item">
                  {{ t('ai.chat.index.748794-440') }}{{ latestNl2SqlHybridResult.routeReason }}
                </div>
              </div>
            </div>
          </div>

          <div v-else-if="latestDeviceControlResult" class="result-section">
            <div class="section-title">{{ t('ai.chat.index.748794-73') }}</div>
            <div class="stats-row">
              <el-tag type="warning" effect="plain">{{ t('ai.chat.index.748794-441') }}：{{ latestDeviceControlResult.actionLabel }}</el-tag>
              <el-tag :type="latestDeviceControlResult.statusType" effect="plain">
                {{ t('ai.chat.index.748794-442') }}：{{ latestDeviceControlResult.statusLabel }}
              </el-tag>
              <el-tag v-if="latestToolName" type="info" effect="plain">{{ t('ai.chat.index.748794-443') }}{{ latestToolName }}</el-tag>
              <el-tag v-if="latestDeviceControlResult.sceneId" type="success" effect="plain">
                {{ t('ai.chat.index.748794-444') }}：{{ latestDeviceControlResult.sceneId }}
              </el-tag>
              <el-tag v-if="latestDeviceControlResult.responseCode !== ''" type="info" effect="plain">
                {{ t('ai.chat.index.748794-445') }}：{{ latestDeviceControlResult.responseCode }}
              </el-tag>
            </div>
            <div v-if="latestDeviceControlHighlightItems.length" class="result-highlight-grid">
              <div v-for="item in latestDeviceControlHighlightItems" :key="item.key" class="result-highlight-card">
                <div class="result-highlight-label">{{ item.label }}</div>
                <div class="result-highlight-value">
                  {{ item.value }}
                  <span v-if="item.unit" class="result-highlight-unit">{{ item.unit }}</span>
                </div>
                <div v-if="item.tip" class="result-highlight-tip">{{ item.tip }}</div>
              </div>
            </div>
            <div
              v-if="latestDeviceControlResult.responseMessage"
              class="summary-box is-markdown result-summary-emphasis"
              @click="handleMessageContentClick"
              v-html="renderMarkdown(latestDeviceControlResult.responseMessage)"
            ></div>
            <div v-if="latestDeviceControlResult.commandText" class="sql-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-74') }}</div>
              <pre class="code-block">{{ latestDeviceControlResult.commandText }}</pre>
            </div>
            <div v-if="latestDeviceControlResult.responseDataPretty" class="table-section">
              <div class="section-header">
                <div class="section-subtitle no-margin">{{ t('ai.chat.index.748794-75') }}</div>
                <el-button text @click="toggleDeviceReceiptExpanded">
                  {{ deviceReceiptExpanded ? t('ai.chat.index.748794-76') : t('ai.chat.index.748794-77') }}
                </el-button>
              </div>
              <div v-if="deviceReceiptExpanded">
                <pre class="code-block json-block">{{ latestDeviceControlResult.responseDataPretty }}</pre>
              </div>
              <div v-else class="summary-box">{{ t('ai.chat.index.748794-78') }}</div>
            </div>
            <div v-if="latestDeviceControlResult.rawResultPretty" class="table-section">
              <div class="section-header">
                <div class="section-subtitle no-margin">{{ t('ai.chat.index.748794-79') }}</div>
                <el-button text @click="toggleDeviceRawExpanded">
                  {{ deviceRawExpanded ? t('ai.chat.index.748794-80') : t('ai.chat.index.748794-81') }}
                </el-button>
              </div>
              <div v-if="deviceRawExpanded">
                <pre class="code-block json-block">{{ latestDeviceControlResult.rawResultPretty }}</pre>
              </div>
              <div v-else class="summary-box">{{ t('ai.chat.index.748794-82') }}</div>
            </div>
          </div>

          <div v-else-if="latestProtocolParseResult" class="result-section">
            <div class="section-title">{{ t('ai.chat.index.748794-83') }}</div>
            <div class="stats-row">
              <el-tag :type="latestProtocolParseResult.statusType" effect="plain">
                {{ t('ai.chat.index.748794-408') }}{{ latestProtocolParseResult.statusLabel }}
              </el-tag>
              <el-tag v-if="latestToolName" type="info" effect="plain">{{ t('ai.chat.index.748794-443') }}{{ latestToolName }}</el-tag>
            </div>
            <div class="summary-box">{{ latestProtocolParseResult.summary }}</div>
            <div v-if="latestProtocolParseResult.keyPoints.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-84') }}</div>
              <div class="info-list">
                <div v-for="item in latestProtocolParseResult.keyPoints" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestProtocolParseResult.missingInformation.length > 0" class="table-section">
              <div class="section-header">
                <div class="section-subtitle no-margin">{{ t('ai.chat.index.748794-85') }}</div>
                <el-button text @click="toggleProtocolMissingExpanded">
                  {{ protocolMissingExpanded ? t('ai.chat.index.748794-86') : t('ai.chat.index.748794-87') }}
                </el-button>
              </div>
              <div v-if="protocolMissingExpanded" class="info-list">
                <div v-for="item in latestProtocolParseResult.missingInformation" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
              <div v-else class="summary-box">
                {{ t('ai.chat.index.748794-446', [latestProtocolParseResult.missingInformation.length]) }}
              </div>
            </div>
          </div>

          <div v-else-if="latestThingModelGenerateResult" class="result-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-447') }}</div>
              <el-button
                v-if="latestThingModelGenerateResult.artifactCode"
                type="primary"
                plain
                size="small"
                @click="handleDownloadThingModelWorkbook"
              >
                {{ t('ai.chat.index.748794-448') }}
              </el-button>
            </div>
            <div class="stats-row">
              <el-tag :type="latestThingModelGenerateResult.statusType" effect="plain">
                {{ t('ai.chat.index.748794-449') }}{{ latestThingModelGenerateResult.statusLabel }}
              </el-tag>
              <el-tag type="success" effect="plain">{{ t('ai.chat.index.748794-450') }}{{ latestThingModelGenerateResult.rowCount }}</el-tag>
              <el-tag v-if="latestToolName" type="info" effect="plain">{{ t('ai.chat.index.748794-443') }}{{ latestToolName }}</el-tag>
            </div>
            <div class="summary-box">{{ latestThingModelGenerateResult.summary }}</div>
            <div v-if="latestThingModelGenerateResult.keyPoints.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-451') }}</div>
              <div class="info-list">
                <div v-for="item in latestThingModelGenerateResult.keyPoints" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestThingModelGenerateResult.previewRows.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-452') }}</div>
              <el-table :data="latestThingModelGenerateResult.previewRows" size="small" border>
                <el-table-column
                  v-for="column in Object.keys(latestThingModelGenerateResult.previewRows[0] || {})"
                  :key="column"
                  :prop="column"
                  :label="column"
                  min-width="120"
                  show-overflow-tooltip
                />
              </el-table>
            </div>
            <div v-if="latestThingModelReviewItems.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-453') }}</div>
              <div class="info-list">
                <div v-for="item in latestThingModelReviewItems" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
          </div>

          <div v-else-if="latestRequirementEvaluationResult" class="result-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-454') }}</div>
              <el-button
                v-if="latestRequirementEvaluationResult.artifactCode"
                type="primary"
                plain
                size="small"
                @click="handleDownloadRequirementEvaluationReport"
              >
                {{ t('ai.chat.index.748794-455') }}
              </el-button>
            </div>
            <div class="stats-row">
              <el-tag :type="latestRequirementEvaluationResult.statusType" effect="plain">
                {{ t('ai.chat.index.748794-456') }}{{ latestRequirementEvaluationResult.statusLabel }}
              </el-tag>
              <el-tag :type="latestRequirementEvaluationResult.matchLevelType" effect="plain">
                {{ t('ai.chat.index.748794-457') }}{{ latestRequirementEvaluationResult.matchLevelLabel }}
              </el-tag>
              <el-tag type="success" effect="plain">
                {{ t('ai.chat.index.748794-458') }}{{ latestRequirementEvaluationResult.requirementItems.length }}
              </el-tag>
              <el-tag v-if="latestToolName" type="info" effect="plain">{{ t('ai.chat.index.748794-443') }}{{ latestToolName }}</el-tag>
            </div>
            <div v-if="latestRequirementEvaluationResult.overallConclusion" class="summary-box">
              {{ latestRequirementEvaluationResult.overallConclusion }}
            </div>
            <div
              v-if="latestRequirementEvaluationResult.summary"
              class="summary-box is-markdown"
              @click="handleMessageContentClick"
              v-html="renderMarkdown(latestRequirementEvaluationResult.summary)"
            ></div>
            <div v-if="latestRequirementEvaluationResult.keyPoints.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-451') }}</div>
              <div class="info-list">
                <div v-for="item in latestRequirementEvaluationResult.keyPoints" :key="item" class="info-item is-key">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestRequirementEvaluationResult.requirementItems.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-459') }}</div>
              <el-table :data="latestRequirementEvaluationResult.requirementItems" size="small" border>
                <el-table-column prop="需求点" :label="t('ai.chat.index.748794-460')" min-width="170" show-overflow-tooltip />
                <el-table-column prop="匹配结论" :label="t('ai.chat.index.748794-461')" min-width="120">
                  <template #default="scope">
                    <span :class="getRequirementMatchHighlightClass(scope.row['匹配结论'])">
                      {{ scope.row['匹配结论'] || '-' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="平台能力/依据" :label="t('ai.chat.index.748794-462')" min-width="180" show-overflow-tooltip />
                <el-table-column prop="建议动作" :label="t('ai.chat.index.748794-463')" min-width="180" show-overflow-tooltip />
                <el-table-column prop="复杂度" :label="t('ai.chat.index.748794-464')" min-width="90" show-overflow-tooltip />
                <el-table-column prop="涉及模块" :label="t('ai.chat.index.748794-465')" min-width="140" show-overflow-tooltip />
              </el-table>
            </div>
            <div v-if="latestRequirementEvaluationResult.moduleImpacts.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-466') }}</div>
              <el-table :data="latestRequirementEvaluationResult.moduleImpacts" size="small" border>
                <el-table-column prop="模块" :label="t('ai.chat.index.748794-467')" min-width="120" show-overflow-tooltip />
                <el-table-column prop="影响范围" :label="t('ai.chat.index.748794-468')" min-width="180" show-overflow-tooltip />
                <el-table-column prop="二开提示" :label="t('ai.chat.index.748794-469')" min-width="180" show-overflow-tooltip />
              </el-table>
            </div>
            <div v-if="latestRequirementEvaluationResult.risks.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-470') }}</div>
              <div class="info-list">
                <div v-for="item in latestRequirementEvaluationResult.risks" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestRequirementEvaluationResult.pendingQuestions.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-471') }}</div>
              <div class="info-list">
                <div v-for="item in latestRequirementEvaluationResult.pendingQuestions" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestRequirementEvaluationResult.nextSteps.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-472') }}</div>
              <div class="info-list">
                <div v-for="item in latestRequirementEvaluationResult.nextSteps" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div v-if="latestRequirementEvaluationResult.references.length > 0" class="table-section">
              <div class="section-subtitle">{{ t('ai.chat.index.748794-473') }}</div>
              <div class="info-list">
                <div v-for="item in latestRequirementEvaluationResult.references" :key="item" class="info-item">
                  {{ item }}
                </div>
              </div>
            </div>
            <div class="summary-box result-summary-emphasis">
              {{ latestRequirementEvaluationResult.disclaimer }}
            </div>
          </div>

          <div v-else-if="latestFallbackResultPretty" class="result-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-88') }}</div>
              <el-button text @click="toggleFallbackRawExpanded">
                {{ fallbackRawExpanded ? t('ai.chat.index.748794-89') : t('ai.chat.index.748794-90') }}
              </el-button>
            </div>
            <div v-if="fallbackRawExpanded">
              <pre class="code-block json-block">{{ latestFallbackResultPretty }}</pre>
            </div>
            <div v-else class="summary-box">{{ t('ai.chat.index.748794-91') }}</div>
          </div>

          <div v-else class="result-empty">
            <el-alert :title="t('ai.chat.index.748794-92')" type="info" :closable="false" />
          </div>
        </div>
        <div
          v-else-if="rightPanelType === 'observability'"
          class="observability-panel"
          v-loading="observabilityLoading"
        >
          <div class="observability-window-switch">
            <span class="observability-window-switch__label">{{ t('ai.chat.index.748794-101') }}</span>
            <el-radio-group v-model="observabilityDays" size="small" @change="handleObservabilityDaysChange">
              <el-radio-button :value="7">{{ t('ai.chat.index.748794-102') }}</el-radio-button>
              <el-radio-button :value="14">{{ t('ai.chat.index.748794-103') }}</el-radio-button>
              <el-radio-button :value="30">{{ t('ai.chat.index.748794-104') }}</el-radio-button>
            </el-radio-group>
          </div>

          <el-empty
            v-if="!observabilityLoaded && !observabilityLoading"
            :description="observabilityLoadError || t('ai.chat.index.748794-105')"
          />

          <template v-else>
            <div class="observability-summary-grid">
              <div v-for="item in observabilitySummaryCards" :key="item.label" class="observability-summary-card">
                <div class="observability-summary-card__label">{{ item.label }}</div>
                <div class="observability-summary-card__value">{{ item.value }}</div>
                <div class="observability-summary-card__tip">{{ item.tip }}</div>
              </div>
            </div>

            <div class="observability-section">
              <div class="observability-section__title">{{ t('ai.chat.index.748794-106') }}</div>
              <el-descriptions :column="1" border>
                <el-descriptions-item v-for="item in observabilityDetailItems" :key="item.label" :label="item.label">
                  {{ item.value }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="observability-section">
              <div class="observability-section__title">{{ t('ai.chat.index.748794-107') }}</div>
              <ul class="observability-note-list">
                <li>{{ t('ai.chat.index.748794-108') }}</li>
                <li>{{ t('ai.chat.index.748794-109') }}</li>
                <li>{{ t('ai.chat.index.748794-110') }}</li>
              </ul>
            </div>
          </template>
        </div>
        <div v-else-if="rightPanelType === 'usageGuide'" class="usage-guide-panel">
          <div class="usage-guide-hero">
            <div class="usage-guide-hero__title">{{ t('ai.chat.index.748794-359') }}</div>
            <div class="usage-guide-hero__description">{{ t('ai.chat.index.748794-360') }}</div>
          </div>

          <div class="usage-guide-section">
            <div class="usage-guide-section__title">{{ t('ai.chat.index.748794-361') }}</div>
            <div class="usage-guide-feature-list">
              <div v-for="item in usageGuideFeatures" :key="item.title" class="usage-guide-feature">
                <div class="usage-guide-feature__title">{{ item.title }}</div>
                <div class="usage-guide-feature__desc">{{ item.description }}</div>
              </div>
            </div>
          </div>

          <div class="usage-guide-section">
            <div class="usage-guide-section__title">{{ t('ai.chat.index.748794-362') }}</div>
            <ul class="usage-guide-note-list">
              <li v-for="item in usageGuideTips" :key="item.title" class="usage-guide-note-item">
                <strong class="usage-guide-emphasis">{{ item.title }}</strong>
                <span class="usage-guide-note-text">{{ item.description }}</span>
              </li>
            </ul>
          </div>

          <div class="usage-guide-section">
            <div class="usage-guide-section__title">{{ t('ai.chat.index.748794-376') }}</div>
            <div class="usage-guide-shortcut-table">
              <div v-for="item in usageGuideShortcuts" :key="item.title" class="usage-guide-shortcut-row">
                <div class="usage-guide-shortcut-keys">
                  <span v-for="key in item.keys" :key="key" class="keyboard-key">{{ key }}</span>
                </div>
                <div class="usage-guide-shortcut-content">
                  <strong class="usage-guide-emphasis">{{ item.title }}</strong>
                  <span>{{ item.description }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="skill-section usage-guide-skill-section">
            <div class="section-header">
              <div class="section-title no-margin">{{ t('ai.chat.index.748794-93') }}</div>
              <div class="section-header-actions">
                <span class="section-summary-text">
                  {{ t('ai.chat.index.748794-366', { total: skillList.length, enabled: enabledSkillCount }) }}
                </span>
                <el-button text @click="toggleSkillExpanded">
                  {{ skillExpanded ? t('ai.chat.index.748794-94') : t('ai.chat.index.748794-95') }}
                </el-button>
              </div>
            </div>
            <div v-if="!skillExpanded" class="summary-box">{{ t('ai.chat.index.748794-96') }}</div>
            <div v-else-if="skillList.length === 0" class="skill-empty">{{ t('ai.chat.index.748794-97') }}</div>
            <div v-else class="skill-list">
              <div v-for="item in skillList" :key="item.skillCode" :class="['skill-item', { disabled: !item.enabled }]">
                <div class="skill-header">
                  <span class="skill-name">{{ item.skillName }}</span>
                  <div class="skill-badges">
                    <el-tag v-if="item.skillCategory" size="small" effect="plain">
                      {{ getSkillCategoryLabel(item.skillCategory) }}
                    </el-tag>
                    <el-tag size="small" :type="item.enabled ? 'success' : 'info'" effect="plain">
                      {{ item.enabled ? t('ai.chat.index.748794-98') : t('ai.chat.index.748794-99') }}
                    </el-tag>
                  </div>
                </div>
                <div class="skill-mode">{{ getModeLabel(item.chatMode) }}</div>
                <div class="skill-desc">{{ item.description }}</div>
                <div v-if="resolveSkillTestCases(item).length" class="skill-case-list">
                  <div class="skill-case-title">{{ t('ai.chat.index.748794-100') }}</div>
                  <button
                    v-for="testCase in resolveSkillTestCases(item)"
                    :key="`${item.skillCode || item.chatMode}-${testCase.code}`"
                    type="button"
                    class="skill-case-item"
                    @click="handleUseSkillCase(testCase.question)"
                  >
                    <span class="skill-case-code">{{ testCase.code }}</span>
                    <span class="skill-case-question">{{ testCase.question }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="modeStrategyDialogVisible" :title="t('ai.chat.index.748794-1')" width="600px">
      <div class="mode-strategy-dialog">
        <div class="mode-strategy-dialog-tip">{{ t('ai.chat.index.748794-111') }}</div>
        <el-form class="ai-dialog-form" label-width="100px">
          <el-form-item :label="t('ai.chat.index.748794-112')">
            <el-radio-group v-model="modePolicyDraft">
              <el-radio value="AUTO">{{ t('ai.chat.index.748794-113') }}</el-radio>
              <el-radio value="PINNED">{{ t('ai.chat.index.748794-114') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="modePolicyDraft === 'PINNED'" :label="t('ai.chat.index.748794-115')">
            <el-select
              v-model="pinnedModeDraft"
              :placeholder="t('ai.chat.index.748794-116')"
              class="mode-strategy-dialog-select"
            >
              <el-option v-for="item in manualModeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('ai.chat.index.748794-117')">
            <el-select
              v-model="modeOverrideDraft"
              clearable
              :placeholder="t('ai.chat.index.748794-118')"
              class="mode-strategy-dialog-select"
            >
              <el-option v-for="item in manualModeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-form>
        <div class="mode-strategy-dialog-summary">
          <div>{{ t('ai.chat.index.748794-474') }}{{ conversationStrategyLabel }}</div>
          <div>{{ t('ai.chat.index.748794-475') }}{{ getModeLabel(modeOverrideDraft || resolvedDraftSessionMode) }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="modeStrategyDialogVisible = false">{{ t('ai.chat.index.748794-119') }}</el-button>
        <el-button type="primary" @click="applyModeStrategy">{{ t('ai.chat.index.748794-120') }}</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="observabilityDrawerVisible"
      :title="t('ai.chat.index.748794-0')"
      size="520px"
      direction="rtl"
      append-to-body
      destroy-on-close
    >
      <div class="observability-drawer" v-loading="observabilityLoading">
        <div class="observability-drawer__intro">
          <div class="observability-drawer__header">
            <div class="observability-drawer__title-group">
              <div class="observability-drawer__title">{{ t('ai.chat.index.748794-121') }}</div>
              <div class="observability-drawer__description">{{ t('ai.chat.index.748794-122') }}</div>
            </div>
            <div class="observability-drawer__actions">
              <el-tag type="info">{{ observabilityScopeLabel }}</el-tag>
              <el-tag type="success">{{ observabilityWindowLabel }}</el-tag>
              <el-button
                type="primary"
                link
                :icon="Refresh"
                :loading="observabilityLoading"
                @click="handleRefreshObservabilityStats"
              >
                {{ t('ai.chat.index.748794-123') }}
              </el-button>
            </div>
          </div>
          <div class="observability-window-switch">
            <span class="observability-window-switch__label">{{ t('ai.chat.index.748794-101') }}</span>
            <el-radio-group v-model="observabilityDays" size="small" @change="handleObservabilityDaysChange">
              <el-radio-button :value="7">{{ t('ai.chat.index.748794-102') }}</el-radio-button>
              <el-radio-button :value="14">{{ t('ai.chat.index.748794-103') }}</el-radio-button>
              <el-radio-button :value="30">{{ t('ai.chat.index.748794-104') }}</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <el-empty
          v-if="!observabilityLoaded && !observabilityLoading"
          :description="observabilityLoadError || t('ai.chat.index.748794-105')"
        />

        <template v-else>
          <div class="observability-summary-grid">
            <div v-for="item in observabilitySummaryCards" :key="item.label" class="observability-summary-card">
              <div class="observability-summary-card__label">{{ item.label }}</div>
              <div class="observability-summary-card__value">{{ item.value }}</div>
              <div class="observability-summary-card__tip">{{ item.tip }}</div>
            </div>
          </div>

          <div class="observability-section">
            <div class="observability-section__title">{{ t('ai.chat.index.748794-106') }}</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item v-for="item in observabilityDetailItems" :key="item.label" :label="item.label">
                {{ item.value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="observability-section">
            <div class="observability-section__title">{{ t('ai.chat.index.748794-107') }}</div>
            <ul class="observability-note-list">
              <li>{{ t('ai.chat.index.748794-124') }}</li>
              <li>{{ t('ai.chat.index.748794-125') }}</li>
              <li>{{ t('ai.chat.index.748794-126') }}</li>
            </ul>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, nextTick, onActivated, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Bottom,
  CopyDocument,
  Expand,
  Fold,
  Microphone,
  MoreFilled,
  Plus,
  QuestionFilled,
  Refresh,
  Setting,
  Top,
  View,
} from '@element-plus/icons-vue';
import { copyText } from '@/utils/common';
import { checkPermi } from '@/utils/permission';
import { useDict } from '@/utils/dict';
import { useSettingsStore } from '@/stores/modules/settings';
import { useTagsViewStore } from '@/stores/modules/tagsView';
import {
  getDefaultChatRoute,
  getChatObservabilityStats,
  downloadRequirementEvaluationReport,
  downloadThingModelWorkbook,
  listChatMessage,
  listChatSession,
  resumeChatMessageStream,
  sendChatMessage,
  sendChatMessageStream,
  uploadChatFileStream,
} from '@/api/ai/chat';
import { archiveChatRecord, renameChatRecord } from '@/api/ai/chatRecord';
import { listAiModelGrouped } from '@/api/ai/model';
import { getProtocolAdaptationTask, listProtocolAdaptationTask } from '@/api/ai/protocolAdaptation';
import { listAiSkill } from '@/api/ai/skill';

const { t } = useI18n();

defineOptions({
  name: 'Chat',
});

const ROUTE_DEBUG_PREFERENCE_KEY = 'fastbee.ai.chat.route.debug.enabled';
const MODEL_PREFERENCE_KEY = 'fastbee.ai.chat.selected.model';
const PAGE_STATE_KEY_PREFIX = 'fastbee.ai.chat.page.state';
const RUNTIME_VISITED_ROUTE_KEY = '__fastbeeAiChatVisitedRouteKeys';
const STREAM_IDLE_COMPLETE_MS = 9000;
const STREAM_CHUNK_RENDER_INTERVAL_MS = 80;
const route = useRoute();
const settingsStore = useSettingsStore();
const tagsViewStore = useTagsViewStore();
const defaultRoute = ref({});
const selectedModel = ref(loadSelectedModelPreference());
const selectedMode = ref('AUTO');
const sidebarCollapsed = ref(false);
const rightPanelVisible = ref(false);
const rightPanelType = ref('result');
const selectedUploadFiles = ref([]);
const composerInputRef = ref(null);
const composerUploadRef = ref(null);
const pendingModeOverride = ref('');
const modeStrategyDialogVisible = ref(false);
const modePolicyDraft = ref('AUTO');
const pinnedModeDraft = ref('');
const modeOverrideDraft = ref('');
const groupedModels = ref([]);
const skillList = ref([]);
const sessionList = ref([]);
const currentSessionId = ref(null);
const currentSessionCode = ref('');
const messageList = ref([]);
const messageListRef = ref(null);
const showScrollToBottomButton = ref(false);
const selectedResultMessageId = ref(null);
const newSessionDraft = ref(true);
const draftMessage = ref('');
const quickPromptVisible = ref(false);
const quickPromptActiveIndex = ref(0);
const lastEffectiveMode = ref('');
const lastExecutedSkill = ref('');
const metaExpanded = ref(false);
const answerExpanded = ref(false);
const skillExpanded = ref(false);
const deviceReceiptExpanded = ref(false);
const deviceRawExpanded = ref(false);
const protocolMissingExpanded = ref(false);
const fallbackRawExpanded = ref(false);
const routeAuditRawExpanded = ref(false);
const observabilityDrawerVisible = ref(false);
const observabilityLoading = ref(false);
const observabilityLoaded = ref(false);
const observabilityLoadError = ref('');
const observabilityDays = ref(7);
const observabilityStats = ref(createDefaultObservabilityStats(7));
const sending = ref(false);
const sessionLoading = ref(false);
const messageLoading = ref(false);
const activeChatAbortController = ref(null);
const activeThinkingMessageId = ref(null);
const thinkingTick = ref(Date.now());
const { dict } = useDict('ai_chat_skill');
const localRouteDebugEnabled = ref(loadRouteDebugPreference());
let localStreamMessageId = -1;
let thinkingTimerId = null;
let quickPromptBlurTimerId = null;

const MESSAGE_BOTTOM_THRESHOLD = 120;
const QUICK_PROMPT_TRIGGER = '/';
const NL2SQL_HIGHLIGHT_LIMIT = 4;
const CODEBASE_PATH_PATTERN =
  /\b(?:(?:springboot|vue3|src|fastbee-[\w-]+)\/)[A-Za-z0-9_./@{}-]+\.(?:java|vue|js|ts|xml|yml|yaml|sql|md|json)\b/g;
const CODEBASE_PACKAGE_PATTERN = /\bcom\.fastbee(?:\.[A-Za-z0-9_]+)+\b/g;
const CODEBASE_MODULE_PATTERN = /\b(?:springboot|vue3|fastbee-[a-z0-9-]+)\b/g;
const CODEBASE_ENDPOINT_PATTERN = /\b(?:GET|POST|PUT|DELETE|PATCH)\s+\/[A-Za-z0-9_./{}:-]+/g;
const CODEBASE_CLASS_PATTERN =
  /\b[A-Z][A-Za-z0-9]*(?:Controller|ServiceImpl|Service|Mapper|DTO|Dto|VO|BO|ReqDto|RespDto|Request|Response|Publish|Invoke|Producer|Consumer|Handler|Factory|Constant|Config|Entity|Model|Task|Job|Client|Impl)\b/g;
const CODEBASE_METHOD_NAMES = [
  'commandGenerate',
  'getThingModels',
  'handleSend',
  'invokeNoReply',
  'invokeReply',
  'publishFunction',
  'selectById',
  'selectByKey',
  'sendCommand',
  'sendCommands',
  'invoke',
];
const CODEBASE_METHOD_PATTERN = new RegExp(
  `\\b(?:${CODEBASE_METHOD_NAMES.map((name) => escapeRegExp(name)).join('|')})\\b`,
  'g'
);
const CODEBASE_LAYER_TERMS = [
  '基础 IoT 设备控制链路',
  '设备服务调用链路',
  '接口入口层',
  '服务接口层',
  '服务实现层',
  '指令/报文生成层',
  '消息发布链路',
  'Controller层',
  'Service层',
  'Mapper XML',
  '前端 API',
  '前端页面',
];
const AI_RESULT_LABEL_NAMES = [
  '当前值',
  '最近值',
  '实时值',
  '统计结果',
  '统计方式',
  '返回行数',
  '返回条数',
  '最大行数',
  '历史点数',
  '统计样本数',
  '执行动作',
  '执行状态',
  '响应码',
  '场景ID',
  '命中技能',
  '设备编号',
  '设备名称',
  '产品名称',
  '物模型',
  '指标名称',
  '标识符',
  '上报时间',
  '最近上报时间',
  '当前值时间',
  'Redis命中',
  '缓存命中',
  '查询类型',
  '最终执行源',
  '时间窗口',
  '主语义',
  '语义来源',
  '置信度',
  '菜单路径',
  '操作步骤',
  '结果说明',
  '查询结果',
  '来源定位',
  '前置条件',
  '注意事项',
  '参考来源',
  '路由说明',
];
const AI_RESULT_LABEL_VALUE_PATTERN = new RegExp(
  `(${AI_RESULT_LABEL_NAMES.map((name) => escapeRegExp(name)).join('|')})([:：]\\s*)([^，。；;\\n|<]{1,80})`,
  'g'
);
const AI_RESULT_STATUS_PATTERN =
  /(执行成功|执行失败|控制成功|控制失败|已下发|已触发执行|已生成指令|待确认执行|等待补充参数|已完成|已执行|已发送|已发布|未命中|缓存命中|Redis命中|在线|离线|待确认|失败|异常)/g;
const AI_RESULT_TARGET_VALUE_PATTERN = /(调整为|设置为|当前为|当前值为|值为)([^\s，。；;]{1,30})/g;
const REQUIREMENT_MATCH_COLUMN_NAMES = ['匹配结论', '匹配类型', '匹配结果'];
const REQUIREMENT_DISCLAIMER_TEXT =
  '本评估结果仅供参考，不代表正式报价、交付承诺或最终实施方案，实际范围、周期、费用和技术路线仍需由我方产品、技术或项目团队结合完整资料进一步评估确认。';
const REQUIREMENT_DISCLAIMER_PATTERN = new RegExp(escapeRegExp(REQUIREMENT_DISCLAIMER_TEXT), 'g');
const AI_DOWNLOAD_ARTIFACT_CODE_PATTERN = /^\d{8}_[A-Za-z0-9]{32}$/;
const NL2SQL_KEY_COLUMN_PATTERNS = [
  t('ai.chat.index.748794-127'),
  t('ai.chat.index.748794-128'),
  t('ai.chat.index.748794-129'),
  t('ai.chat.index.748794-130'),
  t('ai.chat.index.748794-131'),
  t('ai.chat.index.748794-132'),
  t('ai.chat.index.748794-133'),
  t('ai.chat.index.748794-134'),
  t('ai.chat.index.748794-135'),
  t('ai.chat.index.748794-136'),
  t('ai.chat.index.748794-137'),
  t('ai.chat.index.748794-138'),
  t('ai.chat.index.748794-139'),
  t('ai.chat.index.748794-140'),
  t('ai.chat.index.748794-141'),
  'count',
  'total',
  'sum',
  'avg',
  'average',
  'max',
  'min',
  'value',
  'amount',
  'rate',
  'ratio',
];
const NL2SQL_DIMENSION_COLUMN_PATTERNS = [
  'id',
  t('ai.chat.index.748794-142'),
  t('ai.chat.index.748794-143'),
  t('ai.chat.index.748794-144'),
  'name',
  'code',
  'serial',
  'identifier',
  t('ai.chat.index.748794-145'),
  t('ai.chat.index.748794-146'),
  'time',
  'date',
];
const quickPromptExamples = [
  { code: 'SQL_PRODUCT_COUNT', content: t('ai.chat.index.748794-147'), modeLabel: t('ai.chat.index.748794-148') },
  { code: 'DEVICE_PROPERTY_CURRENT', content: t('ai.chat.index.748794-149'), modeLabel: t('ai.chat.index.748794-150') },
  { code: 'DEVICE_CONTROL_OPEN', content: t('ai.chat.index.748794-151'), modeLabel: t('ai.chat.index.748794-152') },
  { code: 'DEVICE_CONTROL_SET', content: t('ai.chat.index.748794-153'), modeLabel: t('ai.chat.index.748794-152') },
  { code: 'PROTOCOL_PARSE_FILE', content: t('ai.chat.index.748794-188'), modeLabel: t('ai.chat.index.748794-178') },
  { code: 'THING_MODEL_GENERATE_FILE', content: t('ai.chat.index.748794-367'), modeLabel: t('ai.chat.index.748794-368') },
  { code: 'REQUIREMENT_EVALUATION_FILE', content: t('ai.chat.index.748794-388'), modeLabel: t('ai.chat.index.748794-389') },
];
const PROTOCOL_TASK_STATUS_LABELS = {
  DRAFT: t('ai.chat.index.748794-154'),
  UPLOADED: t('ai.chat.index.748794-155'),
  AI_PARSED: t('ai.chat.index.748794-156'),
  WORKBOOK_EXPORTED: t('ai.chat.index.748794-157'),
  REVIEW_IMPORTED: t('ai.chat.index.748794-158'),
  VALIDATED: t('ai.chat.index.748794-159'),
  GENERATED: t('ai.chat.index.748794-160'),
  CONFIRMED: t('ai.chat.index.748794-161'),
  FAILED: t('ai.chat.index.748794-162'),
};
const PROTOCOL_PARSE_STATUS_LABELS = {
  PENDING: t('ai.chat.index.748794-163'),
  PARSING: t('ai.chat.index.748794-164'),
  SUCCESS: t('ai.chat.index.748794-165'),
  FAILED: t('ai.chat.index.748794-166'),
};
const PROTOCOL_VALIDATION_STATUS_LABELS = {
  PENDING: t('ai.chat.index.748794-167'),
  PASSED: t('ai.chat.index.748794-168'),
  WARNING: t('ai.chat.index.748794-169'),
  BLOCKED: t('ai.chat.index.748794-170'),
  FAILED: t('ai.chat.index.748794-171'),
};
const PROTOCOL_GENERATION_STATUS_LABELS = {
  PENDING: t('ai.chat.index.748794-172'),
  GENERATING: t('ai.chat.index.748794-173'),
  SUCCESS: t('ai.chat.index.748794-174'),
  FAILED: t('ai.chat.index.748794-175'),
};

const fallbackModeOptions = [
  { value: 'AUTO', label: t('ai.chat.index.748794-113'), listClass: 'primary' },
  { value: 'PLATFORM_ASSISTANT', label: t('ai.chat.index.748794-176'), listClass: 'success' },
  { value: 'GENERAL', label: t('ai.chat.index.748794-177'), listClass: 'info' },
  { value: 'NL2SQL', label: t('ai.chat.index.748794-148'), listClass: 'warning' },
  { value: 'DEVICE_CONTROL', label: t('ai.chat.index.748794-152'), listClass: 'danger' },
  { value: 'PROTOCOL_PARSE', label: t('ai.chat.index.748794-178'), listClass: 'primary' },
  { value: 'THING_MODEL_GENERATE', label: t('ai.chat.index.748794-368'), listClass: 'success' },
  { value: 'REQUIREMENT_EVALUATION', label: t('ai.chat.index.748794-389'), listClass: 'warning' },
];

const skillTestCaseMap = {
  GENERAL: [
    { code: 'GEN-01', question: t('ai.chat.index.748794-179') },
    { code: 'GEN-02', question: t('ai.chat.index.748794-180') },
    { code: 'GEN-03', question: t('ai.chat.index.748794-181') },
  ],
  PLATFORM_ASSISTANT: [
    { code: 'PLAT-01', question: t('ai.chat.index.748794-182') },
    { code: 'PLAT-02', question: t('ai.chat.index.748794-183') },
    { code: 'PLAT-03', question: t('ai.chat.index.748794-184') },
  ],
  NL2SQL: [
    { code: 'SQL-01', question: t('ai.chat.index.748794-147') },
    { code: 'SQL-02', question: t('ai.chat.index.748794-185') },
    { code: 'SQL-03', question: t('ai.chat.index.748794-186') },
  ],
  DEVICE_CONTROL: [
    { code: 'CTRL-01', question: t('ai.chat.index.748794-151') },
    { code: 'CTRL-02', question: t('ai.chat.index.748794-187') },
    { code: 'CTRL-03', question: t('ai.chat.index.748794-153') },
  ],
  PROTOCOL_PARSE: [
    { code: 'PROTO-01', question: t('ai.chat.index.748794-188') },
    { code: 'PROTO-02', question: t('ai.chat.index.748794-189') },
    { code: 'PROTO-03', question: t('ai.chat.index.748794-190') },
  ],
  THING_MODEL_GENERATE: [
    { code: 'TM-01', question: t('ai.chat.index.748794-367') },
    { code: 'TM-02', question: t('ai.chat.index.748794-369') },
    { code: 'TM-03', question: t('ai.chat.index.748794-370') },
  ],
  REQUIREMENT_EVALUATION: [
    { code: 'REQ-01', question: t('ai.chat.index.748794-388') },
    { code: 'REQ-02', question: t('ai.chat.index.748794-390') },
    { code: 'REQ-03', question: t('ai.chat.index.748794-391') },
  ],
};

const usageGuideFeatures = computed(() => [
  {
    title: t('ai.chat.index.748794-176'),
    description: t('ai.chat.index.748794-371'),
  },
  {
    title: t('ai.chat.index.748794-148'),
    description: t('ai.chat.index.748794-372'),
  },
  {
    title: t('ai.chat.index.748794-152'),
    description: t('ai.chat.index.748794-373'),
  },
  {
    title: t('ai.chat.index.748794-178'),
    description: t('ai.chat.index.748794-374'),
  },
  {
    title: t('ai.chat.index.748794-368'),
    description: t('ai.chat.index.748794-375'),
  },
  {
    title: t('ai.chat.index.748794-389'),
    description: t('ai.chat.index.748794-392'),
  },
]);

const usageGuideTips = computed(() => [
  {
    title: t('ai.chat.index.748794-363'),
    description: t('ai.chat.index.748794-381'),
  },
  {
    title: t('ai.chat.index.748794-364'),
    description: t('ai.chat.index.748794-382'),
  },
  {
    title: t('ai.chat.index.748794-365'),
    description: t('ai.chat.index.748794-383'),
  },
]);

const usageGuideShortcuts = computed(() => [
  {
    keys: ['/'],
    title: t('ai.chat.index.748794-377'),
    description: t('ai.chat.index.748794-384'),
  },
  {
    keys: ['↑ / ↓', 'Enter'],
    title: t('ai.chat.index.748794-378'),
    description: t('ai.chat.index.748794-385'),
  },
  {
    keys: ['Ctrl', 'Enter'],
    title: t('ai.chat.index.748794-379'),
    description: t('ai.chat.index.748794-386'),
  },
  {
    keys: ['+'],
    title: t('ai.chat.index.748794-380'),
    description: t('ai.chat.index.748794-387'),
  },
]);

const modeOptions = computed(() => {
  const dictOptions = dict.type.ai_chat_skill || [];
  const source = dictOptions.length ? dictOptions : fallbackModeOptions;
  return source
    .filter((item) => item && item.value)
    .map((item) => ({
      ...item,
      value: item.value,
      label: item.label || item.value,
    }));
});
const manualModeOptions = computed(() => modeOptions.value.filter((item) => item.value && item.value !== 'AUTO'));
const conversationModePolicy = computed(() => (selectedMode.value === 'AUTO' ? 'AUTO' : 'PINNED'));
const conversationPinnedMode = computed(() => (conversationModePolicy.value === 'PINNED' ? selectedMode.value : ''));
const composerMode = computed(() => pendingModeOverride.value || selectedMode.value || 'AUTO');
const conversationStrategyLabel = computed(() =>
  conversationModePolicy.value === 'PINNED' && conversationPinnedMode.value
    ? t('ai.chat.index.748794-506', [getModeLabel(conversationPinnedMode.value)])
    : t('ai.chat.index.748794-113')
);
const welcomeAdvancedModeActive = computed(() =>
  Boolean(pendingModeOverride.value || conversationModePolicy.value === 'PINNED')
);
const currentSkillBrandName = computed(
  () => getModeLabel(composerMode.value || 'AUTO') || t('ai.chat.index.748794-191')
);
const currentSessionTitle = computed(() => {
  const currentSession = sessionList.value.find((item) => item.sessionId === currentSessionId.value);
  return currentSession?.sessionTitle || t('ai.chat.index.748794-4');
});
const groupedSessionSections = computed(() => {
  const groups = [
    { label: t('ai.chat.index.748794-192'), items: [] },
    { label: t('ai.chat.index.748794-193'), items: [] },
    { label: t('ai.chat.index.748794-194'), items: [] },
    { label: t('ai.chat.index.748794-195'), items: [] },
  ];
  sessionList.value.forEach((item) => {
    const days = getSessionDistanceDays(item);
    if (days <= 0) {
      groups[0].items.push(item);
    } else if (days <= 7) {
      groups[1].items.push(item);
    } else if (days <= 30) {
      groups[2].items.push(item);
    } else {
      groups[3].items.push(item);
    }
  });
  return groups.filter((group) => group.items.length > 0);
});
const resolvedDraftSessionMode = computed(() =>
  modePolicyDraft.value === 'PINNED' && pinnedModeDraft.value ? pinnedModeDraft.value : 'AUTO'
);

const chatStreamEnabled = computed(() => true);
const routeDebugEnabled = computed(() => localRouteDebugEnabled.value);
const activeSessionId = computed(() => (newSessionDraft.value ? null : currentSessionId.value));
const composerModelSelectWidth = computed(() => {
  const label = selectedModel.value || resolveModelLabel(selectedModel.value) || t('ai.chat.index.748794-20');
  const textWidth = Array.from(label).reduce((total, char) => total + (char.charCodeAt(0) > 255 ? 14 : 8), 0);
  const nextWidth = Math.min(Math.max(textWidth + 18, 64), 240);
  return `${nextWidth}px`;
});

const assistantMessages = computed(() => messageList.value.filter((item) => item.roleType === 'assistant'));
const latestAssistantMessage = computed(() => assistantMessages.value[assistantMessages.value.length - 1] || null);
const currentResultMessage = computed(() => {
  if (!selectedResultMessageId.value) {
    return latestAssistantMessage.value;
  }
  return (
    assistantMessages.value.find((item) => item.messageId === selectedResultMessageId.value) ||
    latestAssistantMessage.value
  );
});
const latestClarifyPayload = computed(() => normalizeClarifyPayload(currentResultMessage.value));
const clarifyPayloadMap = computed(() => {
  const result = {};
  messageList.value.forEach((item) => {
    const clarifyPayload = normalizeClarifyPayload(item);
    if (clarifyPayload) {
      result[item.messageId] = clarifyPayload;
    }
  });
  return result;
});
const currentResultUserMessage = computed(() => {
  const resultMessage = currentResultMessage.value;
  if (!resultMessage) {
    return [...messageList.value].reverse().find((item) => item.roleType === 'user') || null;
  }
  return resolveUserMessageForAssistant(resultMessage);
});
const isViewingHistoryResult = computed(
  () =>
    Boolean(currentResultMessage.value?.messageId) &&
    Boolean(latestAssistantMessage.value?.messageId) &&
    currentResultMessage.value?.messageId !== latestAssistantMessage.value?.messageId
);
const currentResultSnapshot = computed(() =>
  buildResultSnapshot(currentResultMessage.value, currentResultUserMessage.value)
);
const currentResultMessageTime = computed(() => currentResultSnapshot.value.createTime || '');
const latestUserQuestion = computed(() => currentResultSnapshot.value.question || '');
const latestAssistantAnswer = computed(() => currentResultSnapshot.value.answer || '');
const latestRouteAudit = computed(() => currentResultSnapshot.value.routeAudit || null);
const latestRouteAuditPretty = computed(() => {
  if (!latestRouteAudit.value?.rawPayload) {
    return '';
  }
  return JSON.stringify(latestRouteAudit.value.rawPayload, null, 2);
});
const observabilityScopeLabel = computed(() => observabilityStats.value.scopeLabel || t('ai.chat.index.748794-196'));
const observabilityWindowLabel = computed(
  () => observabilityStats.value.windowLabel || t('ai.chat.index.748794-482', [observabilityDays.value])
);
const observabilitySummaryCards = computed(() => [
  {
    label: t('ai.chat.index.748794-197'),
    value: formatPercentValue(observabilityStats.value.autoHitRate) || '0%',
    tip: t('ai.chat.index.748794-483', [observabilityStats.value.autoRequestCount || 0, observabilityStats.value.autoHitCount || 0]),
  },
  {
    label: t('ai.chat.index.748794-198'),
    value: formatPercentValue(observabilityStats.value.manualModeRate) || '0%',
    tip: t('ai.chat.index.748794-484', [observabilityStats.value.manualModeRequestCount || 0, observabilityStats.value.requestCount || 0]),
  },
  {
    label: t('ai.chat.index.748794-199'),
    value: formatPercentValue(observabilityStats.value.correctionSuccessRate) || '0%',
    tip: t('ai.chat.index.748794-485', [observabilityStats.value.correctionRetryCount || 0, observabilityStats.value.correctionSuccessCount || 0]),
  },
  {
    label: t('ai.chat.index.748794-200'),
    value: String(observabilityStats.value.autoCorrectedCount || 0),
    tip: t('ai.chat.index.748794-201'),
  },
]);
const observabilityDetailItems = computed(() => [
  { label: t('ai.chat.index.748794-202'), value: String(observabilityStats.value.requestCount || 0) },
  { label: t('ai.chat.index.748794-203'), value: String(observabilityStats.value.autoRequestCount || 0) },
  { label: t('ai.chat.index.748794-204'), value: String(observabilityStats.value.autoHitCount || 0) },
  { label: t('ai.chat.index.748794-205'), value: String(observabilityStats.value.autoCorrectedCount || 0) },
  { label: t('ai.chat.index.748794-206'), value: String(observabilityStats.value.manualModeRequestCount || 0) },
  { label: t('ai.chat.index.748794-207'), value: String(observabilityStats.value.pinnedRequestCount || 0) },
  { label: t('ai.chat.index.748794-208'), value: String(observabilityStats.value.overrideRequestCount || 0) },
  { label: t('ai.chat.index.748794-209'), value: String(observabilityStats.value.directManualRequestCount || 0) },
  { label: t('ai.chat.index.748794-210'), value: String(observabilityStats.value.correctionRetryCount || 0) },
  { label: t('ai.chat.index.748794-211'), value: String(observabilityStats.value.correctionSuccessCount || 0) },
]);
const canCollapseAnswer = computed(() => {
  const answer = latestAssistantAnswer.value || '';
  return answer.length > 220 || answer.split('\n').filter(Boolean).length > 5;
});

const latestEffectiveMode = computed(() => currentResultSnapshot.value.abilityType || '');
const latestToolName = computed(() => currentResultSnapshot.value.toolName || '');
const currentResultModelLabel = computed(() => currentResultSnapshot.value.modelLabel || '');
const latestToolResultRaw = computed(() => currentResultSnapshot.value.toolResult || '');
const latestToolResult = computed(() => parseJsonSafe(latestToolResultRaw.value));
const latestToolResultPretty = computed(() => {
  if (!latestToolResult.value) {
    return '';
  }
  return JSON.stringify(latestToolResult.value, null, 2);
});
const quickRetryModeOptions = computed(() =>
  manualModeOptions.value.filter((item) => item.value && item.value !== latestEffectiveMode.value)
);

const latestNl2SqlResult = computed(() => normalizeNl2SqlResult(latestToolResult.value));
const latestNl2SqlPlan = computed(() => latestNl2SqlResult.value?.queryPlan || null);
const latestNl2SqlMode = computed(() => latestNl2SqlResult.value?.queryMode || latestNl2SqlPlan.value?.queryMode || '');
const latestNl2SqlPrimarySemantic = computed(() => latestNl2SqlPlan.value?.primarySemantic || '');
const latestNl2SqlRuntimeSource = computed(() => latestNl2SqlPlan.value?.runtimeSource || '');
const latestNl2SqlRdbResult = computed(() => latestNl2SqlResult.value?.queryResult || null);
const latestNl2SqlRealtimeResult = computed(() => latestNl2SqlResult.value?.realtimeResult || null);
const latestNl2SqlTsdbResult = computed(() => latestNl2SqlResult.value?.tsdbResult || null);
const latestNl2SqlHybridResult = computed(() => latestNl2SqlResult.value?.hybridResult || null);
const latestDeviceControlResult = computed(() =>
  normalizeDeviceControlResult(
    latestEffectiveMode.value,
    latestToolName.value,
    latestAssistantAnswer.value,
    latestToolResultRaw.value,
    latestToolResult.value
  )
);
const latestDeviceControlHighlightItems = computed(() =>
  buildDeviceControlHighlightItems(latestDeviceControlResult.value)
);
const latestProtocolParseResult = computed(() =>
  normalizeProtocolParseResult(
    latestEffectiveMode.value,
    latestToolName.value,
    latestAssistantAnswer.value,
    latestToolResult.value
  )
);
const latestThingModelGenerateResult = computed(() =>
  normalizeThingModelGenerateResult(
    latestEffectiveMode.value,
    latestToolName.value,
    latestAssistantAnswer.value,
    latestToolResult.value
  )
);
const latestRequirementEvaluationResult = computed(() =>
  normalizeRequirementEvaluationResult(
    latestEffectiveMode.value,
    latestToolName.value,
    latestAssistantAnswer.value,
    latestToolResult.value
  )
);
const latestThingModelReviewItems = computed(() => {
  const result = latestThingModelGenerateResult.value;
  if (!result) {
    return [];
  }
  return [...result.qualityIssues, ...result.missingInformation].filter(Boolean);
});
const latestFallbackResultPretty = computed(() => {
  if (
    latestClarifyPayload.value ||
    latestNl2SqlResult.value ||
    latestDeviceControlResult.value ||
    latestProtocolParseResult.value ||
    latestThingModelGenerateResult.value ||
    latestRequirementEvaluationResult.value
  ) {
    return '';
  }
  return latestToolResultPretty.value;
});
const latestSqlText = computed(() => latestNl2SqlResult.value?.sqlText || '');
const latestNl2SqlSummary = computed(() => latestNl2SqlResult.value?.summary || '');
const latestNl2SqlHighlightItems = computed(() => buildNl2SqlHighlightItems(latestNl2SqlResult.value));
const latestNl2SqlConfidence = computed(() => latestNl2SqlResult.value?.confidence ?? null);
const latestNl2SqlConfidenceLabel = computed(() =>
  latestNl2SqlConfidence.value === null || latestNl2SqlConfidence.value === undefined
    ? ''
    : `${Math.round(Number(latestNl2SqlConfidence.value) * 100)}%`
);
const latestNl2SqlTables = computed(() =>
  Array.isArray(latestNl2SqlResult.value?.tables) ? latestNl2SqlResult.value.tables.filter(Boolean) : []
);
const latestNl2SqlParseStatus = computed(() => latestNl2SqlResult.value?.parseStatus || '');
const latestNl2SqlStructuredOutput = computed(() => Boolean(latestNl2SqlResult.value?.structuredOutput));
const latestQueryResult = computed(() => latestNl2SqlRdbResult.value);
const latestRowCount = computed(() => latestQueryResult.value?.rowCount ?? 0);
const latestMaxRows = computed(() => latestQueryResult.value?.maxRows ?? 0);
const latestDataScopeApplied = computed(() => Boolean(latestQueryResult.value?.dataScopeApplied));
const resultTableRows = computed(() => latestQueryResult.value?.rows || []);
const resultTableColumns = computed(() => {
  const columns = latestQueryResult.value?.columns || [];
  if (columns.length > 0) {
    return columns;
  }
  if (resultTableRows.value.length > 0) {
    return Object.keys(resultTableRows.value[0] || {});
  }
  return [];
});
const enabledSkillCount = computed(() => skillList.value.filter((item) => item.enabled).length);
const messageGroups = computed(() => buildMessageGroups(messageList.value));
const hasPendingThinkingMessage = computed(() => messageList.value.some((item) => shouldShowThinking(item)));

watch(
  () => currentResultMessage.value?.messageId,
  () => {
    resetResultBlockExpandedState();
  }
);

watch(
  hasPendingThinkingMessage,
  (active) => {
    if (active) {
      startThinkingTimer();
    } else {
      stopThinkingTimer();
    }
  },
  { immediate: true }
);

watch(selectedModel, (modelCode) => {
  persistSelectedModelPreference(modelCode);
});

watch(
  () => settingsStore.tagsView,
  (enabled) => {
    if (enabled) {
      markCurrentRouteVisitedInRuntime();
      if (messageList.value.length > 0) {
        void scrollMessageListToBottomStable();
      }
    } else {
      clearCurrentRouteVisitedInRuntime();
    }
  }
);

watch(
  [
    newSessionDraft,
    currentSessionId,
    currentSessionCode,
    selectedMode,
    pendingModeOverride,
    selectedModel,
    sidebarCollapsed,
    rightPanelVisible,
    rightPanelType,
    selectedResultMessageId,
    draftMessage,
    lastEffectiveMode,
    lastExecutedSkill,
  ],
  () => {
    persistPageStateSnapshot();
  },
  { flush: 'post' }
);

function isAssistantThinkingMessage(item) {
  const thinkingStartedAt = Number(item?.thinkingStartedAt || 0);
  return (
    sending.value &&
    item?.messageId === activeThinkingMessageId.value &&
    item?.roleType === 'assistant' &&
    item.messageStatus === 'RUNNING' &&
    thinkingStartedAt > 0 &&
    !String(item.messageContent || '').trim()
  );
}

function shouldShowThinking(item) {
  return isAssistantThinkingMessage(item);
}

function shouldRenderMarkdown(item) {
  return item?.roleType === 'assistant' && Boolean(String(item.messageContent || '').trim());
}

function handleMessageContentClick(event) {
  const target = event?.target;
  const link = target?.closest?.('a');
  if (link) {
    event.preventDefault();
    event.stopPropagation();
    openSafeLink(link.getAttribute('href'));
  }
}

function startThinkingTimer() {
  thinkingTick.value = Date.now();
  if (thinkingTimerId) {
    return;
  }
  thinkingTimerId = window.setInterval(() => {
    thinkingTick.value = Date.now();
  }, 1000);
}

function stopThinkingTimer() {
  if (!thinkingTimerId) {
    return;
  }
  window.clearInterval(thinkingTimerId);
  thinkingTimerId = null;
}

function formatThinkingDuration(item) {
  const startTime = Number(item?.thinkingStartedAt || Date.now());
  const safeStartTime = startTime > 0 && startTime <= Date.now() ? startTime : Date.now();
  const elapsedSeconds = Math.max(0, Math.floor((thinkingTick.value - safeStartTime) / 1000));
  if (elapsedSeconds < 60) {
    return t('ai.chat.index.748794-486', [elapsedSeconds]);
  }
  const minutes = Math.floor(elapsedSeconds / 60);
  const seconds = elapsedSeconds % 60;
  return t('ai.chat.index.748794-487', [minutes, seconds]);
}

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function escapeAttribute(value) {
  return escapeHtml(value).replace(/`/g, '&#96;');
}

function sanitizeLinkUrl(url) {
  const raw = String(url || '')
    .trim()
    .replace(/&amp;/g, '&');
  if (parseThingModelWorkbookDownloadUrl(raw) || parseRequirementEvaluationReportDownloadUrl(raw)) {
    return raw;
  }
  if (/^(https?:\/\/|mailto:)/i.test(raw)) {
    return raw;
  }
  return '';
}

function openSafeLink(url) {
  const workbookDownload = parseThingModelWorkbookDownloadUrl(url);
  if (workbookDownload) {
    handleDownloadThingModelWorkbookByCode(workbookDownload.artifactCode, workbookDownload.filename);
    return;
  }
  const requirementReportDownload = parseRequirementEvaluationReportDownloadUrl(url);
  if (requirementReportDownload) {
    handleDownloadRequirementEvaluationReportByCode(
      requirementReportDownload.artifactCode,
      requirementReportDownload.filename
    );
    return;
  }
  const safeUrl = sanitizeLinkUrl(url);
  if (!safeUrl) {
    return;
  }
  window.open(safeUrl, '_blank', 'noopener,noreferrer');
}

function parseThingModelWorkbookDownloadUrl(url) {
  const raw = String(url || '')
    .trim()
    .replace(/&amp;/g, '&');
  if (!raw.toLowerCase().startsWith('fastbee://thing-model-workbook')) {
    return null;
  }
  try {
    const parsed = new URL(raw);
    const artifactCode = parsed.searchParams.get('artifactCode') || '';
    if (parsed.protocol !== 'fastbee:' || parsed.hostname !== 'thing-model-workbook') {
      return null;
    }
    if (!AI_DOWNLOAD_ARTIFACT_CODE_PATTERN.test(artifactCode)) {
      return null;
    }
    return {
      artifactCode,
      filename: normalizeDownloadFilename(parsed.searchParams.get('filename') || t('ai.chat.index.748794-488')),
    };
  } catch (_error) {
    return null;
  }
}

function parseRequirementEvaluationReportDownloadUrl(url) {
  const raw = String(url || '')
    .trim()
    .replace(/&amp;/g, '&');
  if (!raw.toLowerCase().startsWith('fastbee://requirement-evaluation-report')) {
    return null;
  }
  try {
    const parsed = new URL(raw);
    const artifactCode = parsed.searchParams.get('artifactCode') || '';
    if (parsed.protocol !== 'fastbee:' || parsed.hostname !== 'requirement-evaluation-report') {
      return null;
    }
    if (!AI_DOWNLOAD_ARTIFACT_CODE_PATTERN.test(artifactCode)) {
      return null;
    }
    return {
      artifactCode,
      filename: normalizeDownloadFilename(parsed.searchParams.get('filename') || t('ai.chat.index.748794-489')),
    };
  } catch (_error) {
    return null;
  }
}

function normalizeDownloadFilename(filename) {
  const normalized = String(filename || '')
    .replace(/[\\/:*?"<>|]+/g, '_')
    .trim();
  return normalized || t('ai.chat.index.748794-488');
}

function splitTrailingUrlPunctuation(url) {
  let normalizedUrl = String(url || '');
  let trailing = '';
  while (/[.,;:!?，。；：！？、）)\]】》]/.test(normalizedUrl.slice(-1))) {
    trailing = normalizedUrl.slice(-1) + trailing;
    normalizedUrl = normalizedUrl.slice(0, -1);
  }
  return {
    url: normalizedUrl,
    trailing,
  };
}

function normalizeAutoLinkUrl(url) {
  return splitTrailingUrlPunctuation(url);
}

function createInlineToken(type, index) {
  return `\uE000AI${type}${index}\uE001`;
}

function restoreInlineToken(html, token, value) {
  return String(html || '')
    .split(token)
    .join(value);
}

function escapeRegExp(value) {
  return String(value || '').replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function restoreSafeInlineBreaks(html) {
  return String(html || '').replace(/&lt;br\s*\/?&gt;/gi, '<br>');
}

function wrapCodebaseHighlight(value, type) {
  return `<span class="codebase-highlight codebase-highlight--${type}">${value}</span>`;
}

function applyCodebaseHighlightRule(html, pattern, type) {
  const highlightTokens = [];
  const tokenizedHtml = String(html || '').replace(
    /<span class="(?:codebase-highlight|ai-result-highlight)[\s\S]*?<\/span>/g,
    (value) => {
      const token = createInlineToken('HIGHLIGHT', highlightTokens.length);
      highlightTokens.push(value);
      return token;
    }
  );
  pattern.lastIndex = 0;
  let result = tokenizedHtml.replace(pattern, (value) => wrapCodebaseHighlight(value, type));
  highlightTokens.forEach((highlightHtml, index) => {
    result = restoreInlineToken(result, createInlineToken('HIGHLIGHT', index), highlightHtml);
  });
  return result;
}

function applyCodebaseLiteralHighlight(html, value, type) {
  return applyCodebaseHighlightRule(html, new RegExp(escapeRegExp(value), 'g'), type);
}

function highlightCodebaseImportantText(html) {
  let result = String(html || '');
  result = applyCodebaseHighlightRule(result, CODEBASE_PATH_PATTERN, 'path');
  result = applyCodebaseHighlightRule(result, CODEBASE_PACKAGE_PATTERN, 'package');
  result = applyCodebaseHighlightRule(result, CODEBASE_MODULE_PATTERN, 'module');
  result = applyCodebaseHighlightRule(result, CODEBASE_ENDPOINT_PATTERN, 'endpoint');
  CODEBASE_LAYER_TERMS.forEach((term) => {
    result = applyCodebaseLiteralHighlight(result, term, 'layer');
  });
  result = applyCodebaseHighlightRule(result, CODEBASE_CLASS_PATTERN, 'class');
  result = applyCodebaseHighlightRule(result, CODEBASE_METHOD_PATTERN, 'method');
  return result;
}

function wrapResultHighlight(value, type) {
  return `<span class="ai-result-highlight ai-result-highlight--${type}">${value}</span>`;
}

function applyResultHighlightRule(html, pattern, replacer) {
  const highlightTokens = [];
  const tokenizedHtml = String(html || '').replace(
    /<span class="(?:codebase-highlight|ai-result-highlight)[\s\S]*?<\/span>/g,
    (value) => {
      const token = createInlineToken('HIGHLIGHT', highlightTokens.length);
      highlightTokens.push(value);
      return token;
    }
  );
  pattern.lastIndex = 0;
  let result = tokenizedHtml.replace(pattern, replacer);
  highlightTokens.forEach((highlightHtml, index) => {
    result = restoreInlineToken(result, createInlineToken('HIGHLIGHT', index), highlightHtml);
  });
  return result;
}

function highlightAiResultImportantText(html) {
  let result = String(html || '');
  result = applyResultHighlightRule(
    result,
    AI_RESULT_LABEL_VALUE_PATTERN,
    (_, label, separator, value) =>
      `${wrapResultHighlight(`${label}${separator}`, 'label')}${wrapResultHighlight(value.trim(), 'value')}`
  );
  result = applyResultHighlightRule(result, AI_RESULT_TARGET_VALUE_PATTERN, (_, prefix, value) => {
    return `${prefix}${wrapResultHighlight(value, 'value')}`;
  });
  result = applyResultHighlightRule(result, AI_RESULT_STATUS_PATTERN, (value) => wrapResultHighlight(value, 'status'));
  return result;
}

function stripInlineHtmlTags(value) {
  return String(value || '').replace(/<[^>]*>/g, '');
}

function normalizeRequirementMatchText(value) {
  return stripInlineHtmlTags(value).replace(/\s+/g, '').trim();
}

function isRequirementMatchColumn(columnName) {
  const normalized = normalizeRequirementMatchText(columnName);
  return REQUIREMENT_MATCH_COLUMN_NAMES.some((name) => normalized === name || normalized.includes(name));
}

function resolveRequirementMatchHighlightType(value) {
  const text = normalizeRequirementMatchText(value);
  if (/平台已有|已有能力|标准/.test(text)) {
    return 'builtin';
  }
  if (/配置可实现|配置/.test(text)) {
    return 'config';
  }
  if (/需要二开|二开|定制|开发/.test(text)) {
    return 'develop';
  }
  if (/暂无法判断|无法判断|待确认|未知/.test(text)) {
    return 'unknown';
  }
  return '';
}

function wrapRequirementMatchHighlight(html, type) {
  const normalizedType = type || 'default';
  return `<span class="requirement-match-highlight requirement-match-highlight--${normalizedType}">${html}</span>`;
}

function getRequirementMatchHighlightClass(value) {
  const type = resolveRequirementMatchHighlightType(value) || 'default';
  return ['requirement-match-highlight', `requirement-match-highlight--${type}`];
}

function highlightRequirementDisclaimerText(html) {
  return String(html || '').replace(
    REQUIREMENT_DISCLAIMER_PATTERN,
    (value) => `<span class="requirement-disclaimer-highlight">${value}</span>`
  );
}

function applyInlineMarkdown(text, options = {}) {
  const { enableAiResultHighlight = true, enableRequirementDisclaimerHighlight = true } = options;
  const codeTokens = [];
  const linkTokens = [];
  let html = escapeHtml(text);
  html = restoreSafeInlineBreaks(html);
  html = html.replace(/`([^`]+)`/g, (_, code) => {
    const token = createInlineToken('CODE', codeTokens.length);
    codeTokens.push(`<code>${code}</code>`);
    return token;
  });
  html = html.replace(/\[([^\]]+)]\(([^)\s]+)(?:\s+&quot;[^&]*&quot;)?\)/g, (_, label, url) => {
    const safeUrl = sanitizeLinkUrl(url);
    if (!safeUrl) {
      return label;
    }
    const token = createInlineToken('LINK', linkTokens.length);
    linkTokens.push(`<a href="${escapeAttribute(safeUrl)}" target="_blank" rel="noopener noreferrer">${label}</a>`);
    return token;
  });
  html = html.replace(
    /(^|[\s([<{>"'“‘：:，,；;、])((?:https?:\/\/)[A-Za-z0-9\-._~:/?#[\]@!$&'()*+,;=%]+)/g,
    (match, prefix, url) => {
      const normalized = normalizeAutoLinkUrl(url);
      const safeUrl = sanitizeLinkUrl(normalized.url);
      if (!safeUrl) {
        return match;
      }
      const token = createInlineToken('LINK', linkTokens.length);
      const displayUrl = normalized.url.replace(/&amp;/g, '&');
      linkTokens.push(
        `<a href="${escapeAttribute(safeUrl)}" target="_blank" rel="noopener noreferrer">${escapeHtml(displayUrl)}</a>`
      );
      return `${prefix}${token}${normalized.trailing}`;
    }
  );
  html = html.replace(
    /(^|[\s([<{>"'“‘：:，,；;、])(www\.[A-Za-z0-9\-._~:/?#[\]@!$&'()*+,;=%]+)/g,
    (match, prefix, url) => {
      const normalized = normalizeAutoLinkUrl(`https://${url}`);
      const safeUrl = sanitizeLinkUrl(normalized.url);
      if (!safeUrl) {
        return match;
      }
      const token = createInlineToken('LINK', linkTokens.length);
      const displayUrl = normalized.url.replace(/^https:\/\//, '').replace(/&amp;/g, '&');
      linkTokens.push(
        `<a href="${escapeAttribute(safeUrl)}" target="_blank" rel="noopener noreferrer">${escapeHtml(displayUrl)}</a>`
      );
      return `${prefix}${token}${normalized.trailing}`;
    }
  );
  html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/__([^_]+)__/g, '<strong>$1</strong>');
  html = html.replace(/(^|[^*])\*([^*\n]+)\*(?!\*)/g, '$1<em>$2</em>');
  html = html.replace(/(^|[^_])_([^_\n]+)_(?!_)/g, '$1<em>$2</em>');
  html = highlightCodebaseImportantText(html);
  if (enableAiResultHighlight) {
    html = highlightAiResultImportantText(html);
  }
  if (enableRequirementDisclaimerHighlight) {
    html = highlightRequirementDisclaimerText(html);
  }
  codeTokens.forEach((codeHtml, index) => {
    html = restoreInlineToken(html, createInlineToken('CODE', index), codeHtml);
  });
  linkTokens.forEach((linkHtml, index) => {
    html = restoreInlineToken(html, createInlineToken('LINK', index), linkHtml);
  });
  return html;
}

function closeOpenList(rendered, listTypeRef) {
  if (!listTypeRef.value) {
    return;
  }
  rendered.push(`</${listTypeRef.value}>`);
  listTypeRef.value = '';
}

function isMarkdownTableRow(line) {
  return /^\|.+\|$/.test(line) && line.split('|').length >= 3;
}

function isMarkdownTableSeparator(line) {
  return /^\|?[\s:-]+(?:\|[\s:-]+)+\|?$/.test(line);
}

function splitMarkdownTableRow(line) {
  return line
    .replace(/^\|/, '')
    .replace(/\|$/, '')
    .split('|')
    .map((cell) => cell.trim());
}

function renderMarkdownTableCell(cell, columnName) {
  const isMatchColumn = isRequirementMatchColumn(columnName);
  const html = applyInlineMarkdown(cell, {
    enableAiResultHighlight: !isMatchColumn,
    enableRequirementDisclaimerHighlight: false,
  });
  if (!isMatchColumn) {
    return html;
  }
  const type = resolveRequirementMatchHighlightType(cell);
  return wrapRequirementMatchHighlight(html || '-', type);
}

function renderMarkdownTable(headerLine, bodyLines = []) {
  const headers = splitMarkdownTableRow(headerLine);
  const rows = bodyLines.map(splitMarkdownTableRow);
  const thead = `<thead><tr>${headers.map((cell) => `<th>${applyInlineMarkdown(cell)}</th>`).join('')}</tr></thead>`;
  const tbody = rows.length
    ? `<tbody>${rows
        .map(
          (row) =>
            `<tr>${row
              .map((cell, index) => `<td>${renderMarkdownTableCell(cell, headers[index] || '')}</td>`)
              .join('')}</tr>`
        )
        .join('')}</tbody>`
    : '';
  return `<table>${thead}${tbody}</table>`;
}

function renderMarkdown(content) {
  const lines = String(content || '')
    .replace(/\r\n/g, '\n')
    .split('\n');
  const rendered = [];
  const listTypeRef = { value: '' };
  let inCodeBlock = false;
  let codeLanguage = '';
  let codeBuffer = [];
  let skipUntilIndex = -1;

  const flushCodeBlock = () => {
    const languageClass = codeLanguage ? ` class="language-${escapeAttribute(codeLanguage)}"` : '';
    rendered.push(`<pre><code${languageClass}>${escapeHtml(codeBuffer.join('\n'))}</code></pre>`);
    inCodeBlock = false;
    codeLanguage = '';
    codeBuffer = [];
  };

  lines.forEach((line, index) => {
    if (index <= skipUntilIndex) {
      return;
    }
    const rawLine = line || '';
    const trimmed = rawLine.trim();
    const fenceMatch = trimmed.match(/^```([\w-]*)\s*$/);
    if (fenceMatch) {
      if (inCodeBlock) {
        flushCodeBlock();
      } else {
        closeOpenList(rendered, listTypeRef);
        inCodeBlock = true;
        codeLanguage = fenceMatch[1] || '';
        codeBuffer = [];
      }
      return;
    }
    if (inCodeBlock) {
      codeBuffer.push(rawLine);
      return;
    }
    if (!trimmed) {
      closeOpenList(rendered, listTypeRef);
      return;
    }
    if (/^(-{3,}|\*{3,}|_{3,})$/.test(trimmed)) {
      closeOpenList(rendered, listTypeRef);
      rendered.push('<hr />');
      return;
    }

    const nextLine = String(lines[index + 1] || '').trim();
    if (isMarkdownTableRow(trimmed) && isMarkdownTableSeparator(nextLine)) {
      const bodyLines = [];
      let cursor = index + 2;
      while (cursor < lines.length && isMarkdownTableRow(String(lines[cursor] || '').trim())) {
        bodyLines.push(String(lines[cursor] || '').trim());
        cursor += 1;
      }
      closeOpenList(rendered, listTypeRef);
      rendered.push(renderMarkdownTable(trimmed, bodyLines));
      skipUntilIndex = cursor - 1;
      return;
    }

    const headingMatch = trimmed.match(/^(#{1,6})(?:\s+|(?=[^\s#]))(.+)$/);
    if (headingMatch) {
      closeOpenList(rendered, listTypeRef);
      const level = Math.min(headingMatch[1].length, 4);
      rendered.push(`<h${level}>${applyInlineMarkdown(headingMatch[2])}</h${level}>`);
      return;
    }

    const blockquoteMatch = trimmed.match(/^>\s?(.+)$/);
    if (blockquoteMatch) {
      closeOpenList(rendered, listTypeRef);
      rendered.push(`<blockquote>${applyInlineMarkdown(blockquoteMatch[1])}</blockquote>`);
      return;
    }

    const orderedMatch = trimmed.match(/^\d+[.)]\s+(.+)$/);
    if (orderedMatch) {
      if (listTypeRef.value !== 'ol') {
        closeOpenList(rendered, listTypeRef);
        rendered.push('<ol>');
        listTypeRef.value = 'ol';
      }
      rendered.push(`<li>${applyInlineMarkdown(orderedMatch[1])}</li>`);
      return;
    }

    const unorderedMatch = trimmed.match(/^[-*+]\s+(.+)$/);
    if (unorderedMatch) {
      if (listTypeRef.value !== 'ul') {
        closeOpenList(rendered, listTypeRef);
        rendered.push('<ul>');
        listTypeRef.value = 'ul';
      }
      rendered.push(`<li>${applyInlineMarkdown(unorderedMatch[1])}</li>`);
      return;
    }

    closeOpenList(rendered, listTypeRef);
    rendered.push(`<p>${applyInlineMarkdown(trimmed)}</p>`);
  });

  if (inCodeBlock) {
    flushCodeBlock();
  }
  closeOpenList(rendered, listTypeRef);
  return rendered.join('');
}

function getModeLabel(mode) {
  const dictItem = modeOptions.value.find((item) => item.value === mode);
  if (dictItem?.label) {
    return dictItem.label;
  }
  const modeLabelMap = {
    AUTO: t('ai.chat.index.748794-113'),
    PLATFORM_ASSISTANT: t('ai.chat.index.748794-176'),
    GENERAL: t('ai.chat.index.748794-177'),
    NL2SQL: t('ai.chat.index.748794-148'),
    DEVICE_CONTROL: t('ai.chat.index.748794-152'),
    PROTOCOL_PARSE: t('ai.chat.index.748794-178'),
    THING_MODEL_GENERATE: t('ai.chat.index.748794-447'),
    REQUIREMENT_EVALUATION: t('ai.chat.index.748794-454'),
  };
  return modeLabelMap[mode] || mode || t('ai.chat.index.748794-212');
}

function resolveSkillCaseMode(skill = {}) {
  if (skill.chatMode && skillTestCaseMap[skill.chatMode]) {
    return skill.chatMode;
  }
  const skillCode = String(skill.skillCode || '').toLowerCase();
  if (skillCode.includes('platform')) {
    return 'PLATFORM_ASSISTANT';
  }
  if (skillCode.includes('nl2sql') || skillCode.includes('data_query')) {
    return 'NL2SQL';
  }
  if (skillCode.includes('device')) {
    return 'DEVICE_CONTROL';
  }
  if (skillCode.includes('protocol')) {
    return 'PROTOCOL_PARSE';
  }
  if (skillCode.includes('thing_model') || skillCode.includes('thingmodel')) {
    return 'THING_MODEL_GENERATE';
  }
  if (skillCode.includes('requirement') || skillCode.includes('evaluation')) {
    return 'REQUIREMENT_EVALUATION';
  }
  if (skillCode.includes('general')) {
    return 'GENERAL';
  }
  return '';
}

function resolveSkillTestCases(skill) {
  return skillTestCaseMap[resolveSkillCaseMode(skill)] || [];
}

async function handleUseSkillCase(question) {
  draftMessage.value = question || '';
  await nextTick();
  void scrollMessageListToBottomStable('smooth');
}

function resolveSessionModePolicySnapshot(session) {
  if (!session) {
    return 'AUTO';
  }
  if (session.modePolicy === 'PINNED') {
    return 'PINNED';
  }
  if (session.modePolicy === 'AUTO') {
    return 'AUTO';
  }
  const compatibilityMode = session.chatMode || 'AUTO';
  return compatibilityMode && compatibilityMode !== 'AUTO' ? 'PINNED' : 'AUTO';
}

function resolveSessionPinnedModeSnapshot(session) {
  if (!session) {
    return '';
  }
  if (resolveSessionModePolicySnapshot(session) !== 'PINNED') {
    return '';
  }
  return session.pinnedMode || (session.chatMode && session.chatMode !== 'AUTO' ? session.chatMode : '') || '';
}

function resolveSessionModeValue(session, fallbackMode = 'AUTO') {
  const modePolicy = resolveSessionModePolicySnapshot(session);
  const pinnedMode = resolveSessionPinnedModeSnapshot(session);
  if (modePolicy === 'PINNED' && pinnedMode) {
    return pinnedMode;
  }
  if (modePolicy === 'AUTO') {
    return 'AUTO';
  }
  return fallbackMode || 'AUTO';
}

function getSessionStrategyLabel(session) {
  const modePolicy = resolveSessionModePolicySnapshot(session);
  const pinnedMode = resolveSessionPinnedModeSnapshot(session);
  if (modePolicy === 'PINNED' && pinnedMode) {
    return t('ai.chat.index.748794-481', [getModeLabel(pinnedMode)]);
  }
  return t('ai.chat.index.748794-113');
}

function resolveModelLabel(modelCode) {
  if (!modelCode) {
    return '';
  }
  for (const group of groupedModels.value) {
    const target = (group.options || []).find((item) => item.value === modelCode);
    if (target) {
      return target.label;
    }
  }
  return modelCode;
}

function resolveModeByToolName(toolName) {
  if (!toolName) {
    return '';
  }
  const normalizedToolName = String(toolName).toLowerCase();
  if (normalizedToolName === 'platform_assistant_chat' || normalizedToolName.includes('platform_assistant')) {
    return 'PLATFORM_ASSISTANT';
  }
  if (normalizedToolName === 'general_chat' || normalizedToolName.includes('general')) {
    return 'GENERAL';
  }
  if (normalizedToolName.includes('nl2sql') || normalizedToolName.includes('data_query')) {
    return 'NL2SQL';
  }
  if (normalizedToolName.includes('device') || normalizedToolName.includes('scene')) {
    return 'DEVICE_CONTROL';
  }
  if (normalizedToolName.includes('protocol')) {
    return 'PROTOCOL_PARSE';
  }
  if (normalizedToolName.includes('thing_model')) {
    return 'THING_MODEL_GENERATE';
  }
  if (normalizedToolName.includes('requirement')) {
    return 'REQUIREMENT_EVALUATION';
  }
  return '';
}

function resolveToolNameByMode(mode) {
  const toolNameMap = {
    PLATFORM_ASSISTANT: 'platform_assistant_chat',
    GENERAL: 'general_chat',
    NL2SQL: 'nl2sql_query',
    DEVICE_CONTROL: 'device_control',
    PROTOCOL_PARSE: 'protocol_parse_chat',
    THING_MODEL_GENERATE: 'thing_model_generate',
    REQUIREMENT_EVALUATION: 'requirement_evaluation',
  };
  return toolNameMap[mode] || '';
}

function resolveMessageAbility(message, userMessage = null) {
  if (!message) {
    return '';
  }
  const directMode = message.abilityType || message.chatMode || '';
  if (directMode) {
    return directMode;
  }
  const routeAudit = normalizeRouteAudit(userMessage);
  return routeAudit?.finalMode || resolveModeByToolName(message.toolName);
}

function resolveDisplayMessageAbility(item) {
  if (!item) {
    return '';
  }
  if (item.roleType === 'assistant') {
    return resolveMessageAbility(item, resolveUserMessageForAssistant(item));
  }
  if (item.roleType === 'user') {
    const routeAudit = normalizeRouteAudit(item);
    return item.abilityType || item.chatMode || routeAudit?.finalMode || '';
  }
  return item.abilityType || item.chatMode || '';
}

function buildResultSnapshot(resultMessage, userMessage = null) {
  if (!resultMessage) {
    return {};
  }
  const abilityType = resolveMessageAbility(resultMessage, userMessage);
  const toolName = resultMessage.toolName || resolveToolNameByMode(abilityType);
  const modelCode = resultMessage.modelCode || userMessage?.modelCode || '';
  return {
    messageId: resultMessage.messageId,
    userMessageId: userMessage?.messageId || null,
    abilityType,
    toolName,
    modelCode,
    modelLabel: resolveModelLabel(modelCode) || modelCode,
    createTime: resultMessage.createTime || '',
    question: userMessage?.messageContent || '',
    answer: resultMessage.messageContent || '',
    toolResult: resultMessage.toolResult || '',
    routeAudit: normalizeRouteAudit(userMessage),
  };
}

function createDefaultObservabilityStats(windowDays = 7) {
  return {
    scopeLabel: t('ai.chat.index.748794-196'),
    windowDays,
    windowLabel: t('ai.chat.index.748794-482', [windowDays]),
    requestCount: 0,
    autoRequestCount: 0,
    autoHitCount: 0,
    autoCorrectedCount: 0,
    autoHitRate: 0,
    manualModeRequestCount: 0,
    manualModeRate: 0,
    pinnedRequestCount: 0,
    overrideRequestCount: 0,
    directManualRequestCount: 0,
    correctionRetryCount: 0,
    correctionSuccessCount: 0,
    correctionSuccessRate: 0,
  };
}

function getQueryModeLabel(mode) {
  const queryModeLabelMap = {
    RDB_SQL: t('ai.chat.index.748794-213'),
    REDIS_VALUE: t('ai.chat.index.748794-214'),
    TSDB_QUERY: t('ai.chat.index.748794-215'),
    HYBRID_PIPELINE: t('ai.chat.index.748794-216'),
  };
  return queryModeLabelMap[mode] || mode || t('ai.chat.index.748794-217');
}

function getIntentBusinessTypeLabel(type) {
  const typeLabelMap = {
    PLATFORM_HELP: t('ai.chat.index.748794-176'),
    GENERAL_CHAT: t('ai.chat.index.748794-177'),
    DEVICE_RUNTIME_QUERY: t('ai.chat.index.748794-218'),
    RDB_QUERY: t('ai.chat.index.748794-213'),
    HYBRID_QUERY: t('ai.chat.index.748794-216'),
    DEVICE_PROPERTY_CONTROL: t('ai.chat.index.748794-219'),
    DEVICE_SERVICE_INVOKE: t('ai.chat.index.748794-220'),
    DEVICE_COMMAND_GENERATE: t('ai.chat.index.748794-221'),
    DEVICE_SCENE_EXECUTE: t('ai.chat.index.748794-222'),
    PROTOCOL_PARSE: t('ai.chat.index.748794-178'),
    PROTOCOL_GENERATE: t('ai.chat.index.748794-223'),
    THING_MODEL_GENERATE: t('ai.chat.index.748794-447'),
    REQUIREMENT_EVALUATION: t('ai.chat.index.748794-454'),
    UNKNOWN: t('ai.chat.index.748794-217'),
  };
  return typeLabelMap[type] || type || '';
}

function getIntentSubjectTypeLabel(type) {
  const typeLabelMap = {
    DEVICE: t('ai.chat.index.748794-224'),
    PRODUCT: t('ai.chat.index.748794-225'),
    BUSINESS: t('ai.chat.index.748794-226'),
    UNKNOWN: t('ai.chat.index.748794-217'),
  };
  return typeLabelMap[type] || type || '';
}

function getIntentThingModelTypeLabel(type) {
  const typeLabelMap = {
    PROPERTY: t('ai.chat.index.748794-227'),
    SERVICE: t('ai.chat.index.748794-228'),
    EVENT: t('ai.chat.index.748794-229'),
    UNKNOWN: t('ai.chat.index.748794-217'),
  };
  return typeLabelMap[type] || type || '';
}

function getIntentTimeLabel(type) {
  const typeLabelMap = {
    CURRENT: t('ai.chat.index.748794-230'),
    HISTORY: t('ai.chat.index.748794-231'),
    TREND: t('ai.chat.index.748794-232'),
    STAT: t('ai.chat.index.748794-129'),
    UNKNOWN: t('ai.chat.index.748794-217'),
  };
  return typeLabelMap[type] || type || '';
}

function getIntentAggregateTypeLabel(type) {
  const typeLabelMap = {
    COUNT: t('ai.chat.index.748794-233'),
    SUM: t('ai.chat.index.748794-234'),
    AVG: t('ai.chat.index.748794-235'),
    MAX: t('ai.chat.index.748794-236'),
    MIN: t('ai.chat.index.748794-237'),
    NONE: t('ai.chat.index.748794-238'),
  };
  return typeLabelMap[type] || type || '';
}

function getSkillCategoryLabel(category) {
  const categoryLabelMap = {
    SYSTEM: t('ai.chat.index.748794-239'),
    DATA: t('ai.chat.index.748794-240'),
    DEVICE: t('ai.chat.index.748794-241'),
    PROTOCOL: t('ai.chat.index.748794-242'),
    THING_MODEL: t('ai.chat.index.748794-476'),
    REQUIREMENT: t('ai.chat.index.748794-454'),
    CUSTOM: t('ai.chat.index.748794-243'),
  };
  return categoryLabelMap[category] || category || t('ai.chat.index.748794-244');
}

const DEVICE_CONTROL_PLACEHOLDER_EXAMPLE =
  '{"serialNumber":"SN001","identifier":"switch","remoteCommand":{"switch":1}}';

function getComposerPlaceholder(mode) {
  const placeholderMap = {
    AUTO: t('ai.chat.index.748794-245'),
    PLATFORM_ASSISTANT: t('ai.chat.index.748794-246'),
    GENERAL: t('ai.chat.index.748794-247'),
    NL2SQL: t('ai.chat.index.748794-248'),
    DEVICE_CONTROL: t('ai.chat.index.748794-477', [DEVICE_CONTROL_PLACEHOLDER_EXAMPLE]),
    PROTOCOL_PARSE: t('ai.chat.index.748794-249'),
    THING_MODEL_GENERATE: t('ai.chat.index.748794-478'),
    REQUIREMENT_EVALUATION: t('ai.chat.index.748794-479'),
  };
  const placeholderText = placeholderMap[mode] || '';
  return placeholderText ? t('ai.chat.index.748794-480', [placeholderText]) : t('ai.chat.index.748794-250');
}

function isQuickPromptTrigger(value) {
  return String(value || '').trim() === QUICK_PROMPT_TRIGGER;
}

function clearQuickPromptBlurTimer() {
  if (quickPromptBlurTimerId) {
    window.clearTimeout(quickPromptBlurTimerId);
    quickPromptBlurTimerId = null;
  }
}

function closeQuickPrompt() {
  clearQuickPromptBlurTimer();
  quickPromptVisible.value = false;
}

function refreshQuickPromptVisible() {
  const visible = !sending.value && isQuickPromptTrigger(draftMessage.value);
  quickPromptVisible.value = visible;
  if (visible) {
    quickPromptActiveIndex.value = 0;
  }
}

function handleComposerInput() {
  refreshQuickPromptVisible();
}

function handleComposerFocus() {
  clearQuickPromptBlurTimer();
  refreshQuickPromptVisible();
}

function handleComposerBlur() {
  clearQuickPromptBlurTimer();
  quickPromptBlurTimerId = window.setTimeout(() => {
    quickPromptVisible.value = false;
    quickPromptBlurTimerId = null;
  }, 120);
}

async function handleQuickPromptSelect(item) {
  if (!item?.content) {
    closeQuickPrompt();
    return;
  }
  draftMessage.value = item.content;
  closeQuickPrompt();
  await nextTick();
  composerInputRef.value?.focus?.();
}

function handleComposerKeydown(event) {
  if (!quickPromptVisible.value || quickPromptExamples.length === 0) {
    return;
  }
  if (event.key === 'ArrowDown') {
    event.preventDefault();
    quickPromptActiveIndex.value = (quickPromptActiveIndex.value + 1) % quickPromptExamples.length;
    return;
  }
  if (event.key === 'ArrowUp') {
    event.preventDefault();
    quickPromptActiveIndex.value =
      (quickPromptActiveIndex.value - 1 + quickPromptExamples.length) % quickPromptExamples.length;
    return;
  }
  if (event.key === 'Enter' && !event.ctrlKey && !event.metaKey && !event.shiftKey && !event.altKey) {
    event.preventDefault();
    void handleQuickPromptSelect(quickPromptExamples[quickPromptActiveIndex.value]);
    return;
  }
  if (event.key === 'Escape') {
    event.preventDefault();
    closeQuickPrompt();
  }
}

function resolveCurrentModePolicy() {
  return selectedMode.value === 'AUTO' ? 'AUTO' : 'PINNED';
}

function resolveCurrentPinnedMode() {
  return resolveCurrentModePolicy() === 'PINNED' ? selectedMode.value : '';
}

function buildChatRequestPayload(question, overrides = {}) {
  return {
    sessionId: overrides.sessionId ?? currentSessionId.value,
    message: question,
    modelCode: overrides.modelCode ?? selectedModel.value,
    chatMode: overrides.chatMode || composerMode.value,
    modePolicy: overrides.modePolicy || resolveCurrentModePolicy(),
    pinnedMode: overrides.pinnedMode !== undefined ? overrides.pinnedMode : resolveCurrentPinnedMode(),
    modeOverride: overrides.modeOverride !== undefined ? overrides.modeOverride : pendingModeOverride.value,
    interactionSource: overrides.interactionSource || '',
    retrySourceMessageId: overrides.retrySourceMessageId ?? null,
    resumeToken: overrides.resumeToken || '',
    clarifyKey: overrides.clarifyKey || '',
    selectedValue: overrides.selectedValue || '',
    selectedLabel: overrides.selectedLabel || '',
    resumeQuestion: overrides.resumeQuestion || '',
  };
}

function syncModeStrategyDraft() {
  modePolicyDraft.value = resolveCurrentModePolicy();
  pinnedModeDraft.value = resolveCurrentPinnedMode();
  modeOverrideDraft.value = pendingModeOverride.value || '';
}

function openModeStrategyDialog() {
  syncModeStrategyDraft();
  modeStrategyDialogVisible.value = true;
}

function applyModeStrategy() {
  if (modePolicyDraft.value === 'PINNED' && !pinnedModeDraft.value) {
    ElMessage.warning(t('ai.chat.index.748794-251'));
    return;
  }
  selectedMode.value = modePolicyDraft.value === 'PINNED' ? pinnedModeDraft.value : 'AUTO';
  pendingModeOverride.value = modeOverrideDraft.value || '';
  modeStrategyDialogVisible.value = false;
  ElMessage.success(pendingModeOverride.value ? t('ai.chat.index.748794-252') : t('ai.chat.index.748794-253'));
}

function clearOneTurnModeOverride() {
  pendingModeOverride.value = '';
  modeOverrideDraft.value = '';
}

function normalizeGroupedModels(groups = []) {
  return groups.map((group) => ({
    label: group.providerName || group.providerCode || t('ai.chat.index.748794-254'),
    providerCode: group.providerCode,
    options: (group.models || []).map((item) => ({
      ...item,
      value: item.modelCode,
      label: item.modelCode,
    })),
  }));
}

function hasModelOption(modelCode) {
  if (!modelCode) {
    return false;
  }
  return groupedModels.value.some((group) => (group.options || []).some((item) => item.value === modelCode));
}

function buildDefaultRouteModelOption() {
  if (!defaultRoute.value?.modelCode) {
    return null;
  }
  return {
    modelCode: defaultRoute.value.modelCode,
    modelName: defaultRoute.value.modelName,
    providerCode: defaultRoute.value.providerCode,
    providerName: defaultRoute.value.providerName,
    value: defaultRoute.value.modelCode,
    label: defaultRoute.value.modelCode,
  };
}

function ensureDefaultRouteModelOption() {
  const defaultOption = buildDefaultRouteModelOption();
  if (!defaultOption || hasModelOption(defaultOption.value)) {
    return;
  }
  const providerCode = defaultOption.providerCode || 'DEFAULT';
  const providerLabel = defaultOption.providerName || defaultOption.providerCode || t('ai.chat.index.748794-255');
  const targetGroup = groupedModels.value.find((group) => group.providerCode === providerCode);
  if (targetGroup) {
    groupedModels.value = groupedModels.value.map((group) =>
      group.providerCode === providerCode ? { ...group, options: [defaultOption, ...(group.options || [])] } : group
    );
    return;
  }
  groupedModels.value = [
    {
      label: providerLabel,
      providerCode,
      options: [defaultOption],
    },
    ...groupedModels.value,
  ];
}

function resolveDefaultModelCode() {
  return defaultRoute.value?.modelCode || groupedModels.value[0]?.options?.[0]?.value || '';
}

function parseJsonSafe(value) {
  if (!value) {
    return null;
  }
  try {
    return JSON.parse(value);
  } catch (error) {
    return null;
  }
}

function resolveProtocolTaskIdFromToolResult(toolResult) {
  const payload = typeof toolResult === 'string' ? parseJsonSafe(toolResult) : toolResult;
  const taskId = payload?.taskId;
  if (taskId === null || taskId === undefined || taskId === '') {
    return null;
  }
  const numericTaskId = Number(taskId);
  return Number.isFinite(numericTaskId) ? numericTaskId : taskId;
}

function formatProtocolStatusLabel(status, labelMap) {
  if (status === null || status === undefined || status === '') {
    return '-';
  }
  return labelMap[status] || status;
}

function isProtocolTaskTerminal(task = {}) {
  return (
    ['GENERATED', 'CONFIRMED', 'FAILED'].includes(task.taskStatus) ||
    ['SUCCESS', 'FAILED'].includes(task.generationStatus) ||
    ['FAILED'].includes(task.parseStatus) ||
    ['BLOCKED'].includes(task.validationStatus)
  );
}

function buildProtocolTaskProgressToolResult(task = {}) {
  return JSON.stringify({
    summary: buildProtocolTaskProgressMessage(task),
    keyPoints: [
      task.taskId ? t('ai.chat.index.748794-507', [task.taskId]) : '',
      t('ai.chat.index.748794-508', [formatProtocolStatusLabel(task.taskStatus, PROTOCOL_TASK_STATUS_LABELS)]),
      t('ai.chat.index.748794-408') + formatProtocolStatusLabel(task.parseStatus, PROTOCOL_PARSE_STATUS_LABELS),
      t('ai.chat.index.748794-509', [formatProtocolStatusLabel(task.validationStatus, PROTOCOL_VALIDATION_STATUS_LABELS)]),
      t('ai.chat.index.748794-510', [formatProtocolStatusLabel(task.generationStatus, PROTOCOL_GENERATION_STATUS_LABELS)]),
    ].filter(Boolean),
    missingInformation: task.errorSummary ? [task.errorSummary] : [],
    status: isProtocolTaskTerminal(task) ? 'ANALYZED' : 'PROCESSING',
    rawAnswer: buildProtocolTaskProgressMessage(task),
    taskId: task.taskId,
    taskStatus: task.taskStatus,
    parseStatus: task.parseStatus,
    validationStatus: task.validationStatus,
    generationStatus: task.generationStatus,
  });
}

function buildProtocolTaskProgressMessage(task = {}) {
  const lines = [
    t('ai.chat.index.748794-256'),
    '',
    task.taskId ? t('ai.chat.index.748794-507', [task.taskId]) : '',
    t('ai.chat.index.748794-508', [formatProtocolStatusLabel(task.taskStatus, PROTOCOL_TASK_STATUS_LABELS)]),
    t('ai.chat.index.748794-408') + formatProtocolStatusLabel(task.parseStatus, PROTOCOL_PARSE_STATUS_LABELS),
    t('ai.chat.index.748794-509', [formatProtocolStatusLabel(task.validationStatus, PROTOCOL_VALIDATION_STATUS_LABELS)]),
    t('ai.chat.index.748794-510', [formatProtocolStatusLabel(task.generationStatus, PROTOCOL_GENERATION_STATUS_LABELS)]),
  ].filter((item) => item !== '');
  if (task.errorSummary) {
    lines.push('', t('ai.chat.index.748794-511', [task.errorSummary]));
  }
  if (!isProtocolTaskTerminal(task)) {
    lines.push('', t('ai.chat.index.748794-257'));
  }
  return lines.join('\n');
}

function formatPercentValue(value) {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue)) {
    return '';
  }
  return `${Math.round(numericValue * 100)}%`;
}

function isRouteAuditPayload(payload, toolName) {
  if (!payload || typeof payload !== 'object') {
    return false;
  }
  if (toolName === 'auto_router_analyze') {
    return true;
  }
  return [
    'requestedMode',
    'mode',
    'ruleMode',
    'finalMode',
    'businessType',
    'subjectType',
    'thingModelText',
    'timeIntent',
  ].some((key) => Object.prototype.hasOwnProperty.call(payload, key));
}

function buildRouteAuditSlotItems(payload) {
  const slotList = [
    { label: t('ai.chat.index.748794-258'), value: payload.deviceNameText },
    { label: t('ai.chat.index.748794-259'), value: payload.serialNumberText },
    { label: t('ai.chat.index.748794-260'), value: payload.productNameText },
    { label: t('ai.chat.index.748794-261'), value: payload.thingModelText },
    { label: t('ai.chat.index.748794-262'), value: payload.actionText },
    { label: t('ai.chat.index.748794-263'), value: payload.actionValue },
  ];
  return slotList.filter((item) => item.value !== null && item.value !== undefined && item.value !== '');
}

const ROUTE_PERFORMANCE_LABELS = {
  runtimeValidateMs: t('ai.chat.index.748794-264'),
  modelRouteMs: t('ai.chat.index.748794-265'),
  historyLoadMs: t('ai.chat.index.748794-266'),
  routeAnalyzeMs: t('ai.chat.index.748794-267'),
  messagePersistMs: t('ai.chat.index.748794-268'),
  prepareMs: t('ai.chat.index.748794-269'),
  contextBuildMs: t('ai.chat.index.748794-270'),
  firstTokenMs: t('ai.chat.index.748794-271'),
  modelStreamMs: t('ai.chat.index.748794-272'),
  executeMs: t('ai.chat.index.748794-273'),
  totalMs: t('ai.chat.index.748794-274'),
  chunkCount: t('ai.chat.index.748794-275'),
  answerChars: t('ai.chat.index.748794-276'),
  promptChars: t('ai.chat.index.748794-277'),
  historyMessageCount: t('ai.chat.index.748794-278'),
  questionChars: t('ai.chat.index.748794-279'),
  executionType: t('ai.chat.index.748794-280'),
  failed: t('ai.chat.index.748794-281'),
};

const ROUTE_PERFORMANCE_ORDER = [
  'executionType',
  'prepareMs',
  'runtimeValidateMs',
  'modelRouteMs',
  'historyLoadMs',
  'routeAnalyzeMs',
  'messagePersistMs',
  'contextBuildMs',
  'firstTokenMs',
  'modelStreamMs',
  'executeMs',
  'totalMs',
  'chunkCount',
  'answerChars',
  'promptChars',
  'historyMessageCount',
  'questionChars',
  'failed',
];

function formatRoutePerformanceValue(key, value) {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  if (key.endsWith('Ms')) {
    const duration = Number(value);
    if (!Number.isFinite(duration)) {
      return String(value);
    }
    if (duration >= 1000) {
      return `${(duration / 1000).toFixed(duration >= 10000 ? 1 : 2)}s`;
    }
    return `${Math.round(duration)}ms`;
  }
  if (typeof value === 'boolean') {
    return value ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52');
  }
  return String(value);
}

function buildRoutePerformanceItems(performanceTrace) {
  if (!performanceTrace || typeof performanceTrace !== 'object') {
    return [];
  }
  const orderedKeys = [
    ...ROUTE_PERFORMANCE_ORDER,
    ...Object.keys(performanceTrace).filter((key) => !ROUTE_PERFORMANCE_ORDER.includes(key)),
  ];
  return orderedKeys
    .filter((key, index) => orderedKeys.indexOf(key) === index)
    .map((key) => ({
      key,
      label: ROUTE_PERFORMANCE_LABELS[key] || key,
      value: formatRoutePerformanceValue(key, performanceTrace[key]),
    }))
    .filter((item) => item.value !== '');
}

function normalizeRouteAudit(item) {
  if (!item || item.roleType !== 'user') {
    return null;
  }
  const rawPayload = parseJsonSafe(item.toolResult);
  if (!isRouteAuditPayload(rawPayload, item.toolName)) {
    return null;
  }
  return {
    rawPayload,
    requestedMode: rawPayload.requestedMode || '',
    aiMode: rawPayload.mode || '',
    finalMode: rawPayload.finalMode || '',
    ruleMode: rawPayload.ruleMode || '',
    confidenceLabel: formatPercentValue(rawPayload.modeConfidence),
    businessTypeLabel: getIntentBusinessTypeLabel(rawPayload.businessType),
    subjectTypeLabel: getIntentSubjectTypeLabel(rawPayload.subjectType),
    thingModelTypeLabel: getIntentThingModelTypeLabel(rawPayload.thingModelTypeHint),
    timeIntentLabel: getIntentTimeLabel(rawPayload.timeIntent),
    aggregateTypeLabel: getIntentAggregateTypeLabel(rawPayload.aggregateType),
    needClarify: rawPayload.needClarify === true,
    matchedRuleMode: rawPayload.matchedRuleMode === true,
    adoptedBySystem: rawPayload.adoptedBySystem === true,
    structuredOutput:
      rawPayload.structuredOutput === null || rawPayload.structuredOutput === undefined
        ? null
        : rawPayload.structuredOutput === true,
    parseStatus: rawPayload.parseStatus || '',
    parseErrorText: [rawPayload.parseErrorCode, rawPayload.parseErrorMessage].filter(Boolean).join(' / '),
    reason: rawPayload.reason || '',
    fallbackReason: rawPayload.fallbackReason || '',
    slotItems: buildRouteAuditSlotItems(rawPayload),
    performanceItems: buildRoutePerformanceItems(rawPayload.performanceTrace),
  };
}

function normalizeClarifyPayload(item) {
  if (!item) {
    return null;
  }
  const rawPayload =
    item.streamClarifyPayload && typeof item.streamClarifyPayload === 'object'
      ? item.streamClarifyPayload
      : parseJsonSafe(item.toolResult);
  if (!rawPayload || typeof rawPayload !== 'object') {
    return null;
  }
  const clarifyOptions = Array.isArray(rawPayload.clarifyOptions)
    ? rawPayload.clarifyOptions.filter((option) => option && (option.value || option.label))
    : [];
  if (!rawPayload.clarifyType || !clarifyOptions.length) {
    return null;
  }
  return {
    clarifyType: rawPayload.clarifyType,
    clarifyKey: rawPayload.clarifyKey || '',
    clarifyTitle: rawPayload.clarifyTitle || '',
    clarifyOptions,
    resumeToken: rawPayload.resumeToken || '',
    resumeQuestion: rawPayload.resumeQuestion || '',
  };
}

function normalizeNl2SqlResult(payload) {
  if (!payload || typeof payload !== 'object') {
    return null;
  }
  if (payload.realtimeResult && typeof payload.realtimeResult === 'object') {
    return {
      queryMode: payload.queryMode || 'REDIS_VALUE',
      queryPlan: payload.queryPlan || null,
      summary: payload.summary || payload.realtimeResult.summary || '',
      realtimeResult: payload.realtimeResult,
    };
  }
  if (payload.tsdbResult && typeof payload.tsdbResult === 'object') {
    return {
      queryMode: payload.queryMode || 'TSDB_QUERY',
      queryPlan: payload.queryPlan || null,
      summary: payload.summary || payload.tsdbResult.summary || '',
      tsdbResult: payload.tsdbResult,
    };
  }
  if (payload.hybridResult && typeof payload.hybridResult === 'object') {
    return {
      queryMode: payload.queryMode || 'HYBRID_PIPELINE',
      queryPlan: payload.queryPlan || null,
      summary: payload.summary || payload.hybridResult.summary || '',
      hybridResult: payload.hybridResult,
    };
  }
  if (payload.queryResult && typeof payload.queryResult === 'object') {
    return {
      queryMode: payload.queryMode || 'RDB_SQL',
      queryPlan: payload.queryPlan || null,
      summary: payload.summary || '',
      confidence: payload.confidence ?? payload.generationResult?.confidence ?? null,
      tables: Array.isArray(payload.tables)
        ? payload.tables
        : Array.isArray(payload.generationResult?.tables)
          ? payload.generationResult.tables
          : Array.isArray(payload.queryResult?.tables)
            ? payload.queryResult.tables
            : [],
      parseStatus: payload.parseStatus || payload.generationResult?.parseStatus || '',
      structuredOutput: payload.structuredOutput ?? payload.generationResult?.structuredOutput ?? false,
      sqlText:
        payload.generatedSql ||
        payload.generationResult?.sql ||
        payload.queryResult.executedSql ||
        payload.queryResult.normalizedSql ||
        payload.queryResult.originalSql ||
        '',
      queryResult: payload.queryResult,
    };
  }
  if (payload.executedSql || payload.normalizedSql || payload.originalSql || Array.isArray(payload.rows)) {
    return {
      queryMode: 'RDB_SQL',
      queryPlan: null,
      summary: '',
      sqlText: payload.executedSql || payload.normalizedSql || payload.originalSql || '',
      queryResult: payload,
    };
  }
  return null;
}

function normalizeDeviceControlResult(mode, toolName, answer, rawToolResult, parsedToolResult) {
  if (mode !== 'DEVICE_CONTROL') {
    return null;
  }

  const actionLabelMap = {
    device_invoke: t('ai.chat.index.748794-282'),
    device_invoke_reply: t('ai.chat.index.748794-283'),
    device_command_generate: t('ai.chat.index.748794-284'),
    device_run_scene: t('ai.chat.index.748794-285'),
    device_control_confirm: t('ai.chat.index.748794-286'),
    device_control_guide: t('ai.chat.index.748794-287'),
  };
  const actionLabel = actionLabelMap[toolName] || t('ai.chat.index.748794-152');

  if (toolName === 'device_command_generate') {
    return {
      actionLabel,
      statusLabel: t('ai.chat.index.748794-288'),
      statusType: 'success',
      responseCode: '',
      responseMessage: t('ai.chat.index.748794-289'),
      commandText: rawToolResult || answer || '',
      responseDataPretty: '',
      rawResultPretty: '',
      sceneId: '',
    };
  }

  if (toolName === 'device_run_scene') {
    return {
      actionLabel,
      statusLabel: t('ai.chat.index.748794-290'),
      statusType: 'warning',
      responseCode: '',
      responseMessage: answer || t('ai.chat.index.748794-291'),
      commandText: '',
      responseDataPretty: '',
      rawResultPretty: parsedToolResult ? JSON.stringify(parsedToolResult, null, 2) : '',
      sceneId: parsedToolResult?.sceneId ?? '',
    };
  }

  if (toolName === 'device_control_confirm') {
    return {
      actionLabel,
      statusLabel: t('ai.chat.index.748794-292'),
      statusType: 'warning',
      responseCode: '',
      responseMessage: answer || t('ai.chat.index.748794-293'),
      commandText: '',
      responseDataPretty: '',
      rawResultPretty: parsedToolResult ? JSON.stringify(parsedToolResult, null, 2) : '',
      sceneId: '',
    };
  }

  if (toolName === 'device_invoke' || toolName === 'device_invoke_reply') {
    const responseCode = parsedToolResult?.code ?? '';
    const success = ['0', '200', 0, 200].includes(responseCode);
    return {
      actionLabel,
      statusLabel: success ? t('ai.chat.index.748794-294') : t('ai.chat.index.748794-295'),
      statusType: success ? 'success' : 'danger',
      responseCode,
      responseMessage: parsedToolResult?.msg || answer || '',
      commandText: '',
      responseDataPretty: formatStructuredValue(parsedToolResult?.data),
      rawResultPretty: parsedToolResult ? JSON.stringify(parsedToolResult, null, 2) : '',
      sceneId: '',
    };
  }

  return {
    actionLabel,
    statusLabel: t('ai.chat.index.748794-296'),
    statusType: 'info',
    responseCode: '',
    responseMessage: answer || t('ai.chat.index.748794-297'),
    commandText: '',
    responseDataPretty: '',
    rawResultPretty: '',
    sceneId: '',
  };
}

function normalizeProtocolParseResult(mode, toolName, answer, parsedToolResult) {
  if (mode !== 'PROTOCOL_PARSE' && toolName !== 'protocol_parse_chat') {
    return null;
  }
  if (parsedToolResult && typeof parsedToolResult === 'object') {
    const missingInformation = Array.isArray(parsedToolResult.missingInformation)
      ? parsedToolResult.missingInformation.filter(Boolean)
      : [];
    return {
      statusLabel:
        parsedToolResult.status === 'NEED_MORE_INFO' || missingInformation.length > 0
          ? t('ai.chat.index.748794-85')
          : t('ai.chat.index.748794-298'),
      statusType: parsedToolResult.status === 'NEED_MORE_INFO' || missingInformation.length > 0 ? 'warning' : 'success',
      summary: parsedToolResult.summary || t('ai.chat.index.748794-299'),
      keyPoints: Array.isArray(parsedToolResult.keyPoints) ? parsedToolResult.keyPoints.filter(Boolean) : [],
      missingInformation,
      rawAnswer: parsedToolResult.rawAnswer || answer || '',
    };
  }
  const detailText = (answer || '').trim();
  const lines = detailText
    .split('\n')
    .map((item) => item.trim())
    .filter(Boolean);
  const statusPending = /缺失|不足|补充|待确认|无法判断|未提供/.test(detailText);
  const summary = lines[0] || t('ai.chat.index.748794-299');
  const keyPoints = lines.slice(1, 6);
  return {
    statusLabel: statusPending ? t('ai.chat.index.748794-85') : t('ai.chat.index.748794-298'),
    statusType: statusPending ? 'warning' : 'success',
    summary,
    keyPoints,
    missingInformation: [],
    rawAnswer: detailText,
  };
}

function normalizeThingModelGenerateResult(mode, toolName, answer, parsedToolResult) {
  if (mode !== 'THING_MODEL_GENERATE' && toolName !== 'thing_model_generate') {
    return null;
  }
  const payload = parsedToolResult && typeof parsedToolResult === 'object' ? parsedToolResult : {};
  const missingInformation = Array.isArray(payload.missingInformation)
    ? payload.missingInformation.filter(Boolean)
    : [];
  const qualityIssues = Array.isArray(payload.qualityIssues) ? payload.qualityIssues.filter(Boolean) : [];
  const previewRows = Array.isArray(payload.previewRows) ? payload.previewRows.filter(Boolean) : [];
  const rowCount = payload.rowCount ?? previewRows.length;
  const status = payload.status || (qualityIssues.length || missingInformation.length ? 'NEED_REVIEW' : 'GENERATED');
  return {
    status,
    statusLabel: status === 'PROCESSING' ? t('ai.chat.index.748794-490') : status === 'NEED_MORE_INFO' ? t('ai.chat.index.748794-491') : t('ai.chat.index.748794-492'),
    statusType: status === 'NEED_MORE_INFO' ? 'warning' : status === 'PROCESSING' ? 'info' : 'success',
    summary: payload.summary || answer || t('ai.chat.index.748794-493'),
    keyPoints: Array.isArray(payload.keyPoints) ? payload.keyPoints.filter(Boolean) : [],
    missingInformation,
    qualityIssues,
    previewRows,
    rowCount,
    artifactCode: payload.artifactCode || '',
    artifactName: payload.artifactName || t('ai.chat.index.748794-488'),
    confidence: payload.confidence ?? null,
    rawAnswer: answer || '',
  };
}

function normalizeRequirementEvaluationResult(mode, toolName, answer, parsedToolResult) {
  if (mode !== 'REQUIREMENT_EVALUATION' && toolName !== 'requirement_evaluation') {
    return null;
  }
  const payload = parsedToolResult && typeof parsedToolResult === 'object' ? parsedToolResult : {};
  const status = payload.status || 'COMPLETED';
  const matchLevel = payload.matchLevel || 'UNKNOWN';
  return {
    status,
    statusLabel: getRequirementStatusLabel(status),
    statusType: getRequirementStatusType(status),
    matchLevel,
    matchLevelLabel: getRequirementMatchLevelLabel(matchLevel),
    matchLevelType: getRequirementMatchLevelType(matchLevel),
    summary: payload.summary || '',
    overallConclusion: payload.overallConclusion || answer || '',
    artifactCode: payload.artifactCode || '',
    artifactName: payload.artifactName || '',
    artifactSize: payload.artifactSize ?? null,
    artifactRelativePath: payload.artifactRelativePath || '',
    artifactType: payload.artifactType || '',
    keyPoints: toDisplayList(payload.keyPoints),
    requirementItems: toDisplayRows(payload.requirementItems),
    moduleImpacts: toDisplayRows(payload.moduleImpacts),
    risks: toDisplayList(payload.risks),
    pendingQuestions: toDisplayList(payload.pendingQuestions),
    nextSteps: toDisplayList(payload.nextSteps),
    references: toDisplayList(payload.references),
    disclaimer: payload.disclaimer || REQUIREMENT_DISCLAIMER_TEXT,
    rawAnswer: answer || '',
  };
}

function toDisplayList(value) {
  return Array.isArray(value) ? value.filter(Boolean).map((item) => String(item)) : [];
}

function toDisplayRows(value) {
  return Array.isArray(value) ? value.filter((item) => item && typeof item === 'object') : [];
}

function getRequirementStatusLabel(status) {
  const normalized = String(status || '').toUpperCase();
  if (normalized === 'PROCESSING') {
    return t('ai.chat.index.748794-494');
  }
  if (normalized === 'NEED_MORE_INFO' || normalized === 'NEED_REVIEW') {
    return t('ai.chat.index.748794-495');
  }
  if (normalized === 'FAILED') {
    return t('ai.chat.index.748794-496');
  }
  return t('ai.chat.index.748794-497');
}

function getRequirementStatusType(status) {
  const normalized = String(status || '').toUpperCase();
  if (normalized === 'PROCESSING') {
    return 'info';
  }
  if (normalized === 'NEED_MORE_INFO' || normalized === 'NEED_REVIEW') {
    return 'warning';
  }
  if (normalized === 'FAILED') {
    return 'danger';
  }
  return 'success';
}

function getRequirementMatchLevelLabel(matchLevel) {
  const normalized = String(matchLevel || '').toUpperCase();
  if (normalized === 'HIGH') {
    return t('ai.chat.index.748794-498');
  }
  if (normalized === 'MEDIUM') {
    return t('ai.chat.index.748794-499');
  }
  if (normalized === 'LOW') {
    return t('ai.chat.index.748794-500');
  }
  return t('ai.chat.index.748794-501');
}

function getRequirementMatchLevelType(matchLevel) {
  const normalized = String(matchLevel || '').toUpperCase();
  if (normalized === 'HIGH') {
    return 'success';
  }
  if (normalized === 'MEDIUM') {
    return 'warning';
  }
  if (normalized === 'LOW') {
    return 'danger';
  }
  return 'info';
}

function formatStructuredValue(value) {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  if (typeof value === 'string') {
    return value;
  }
  try {
    return JSON.stringify(value, null, 2);
  } catch (error) {
    return String(value);
  }
}

function formatCellValue(value) {
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  if (typeof value === 'object') {
    return JSON.stringify(value);
  }
  return String(value);
}

function hasDisplayValue(value) {
  return value !== null && value !== undefined && value !== '';
}

function normalizeHighlightLabel(label) {
  return String(label || '').trim();
}

function isNumericLikeValue(value) {
  if (!hasDisplayValue(value)) {
    return false;
  }
  if (typeof value === 'number') {
    return Number.isFinite(value);
  }
  if (typeof value !== 'string') {
    return false;
  }
  const normalizedValue = value.replace(/[,，%\s]/g, '');
  if (!normalizedValue || /^[-+]?\d{4}[-/]\d{1,2}[-/]\d{1,2}/.test(value)) {
    return false;
  }
  return Number.isFinite(Number(normalizedValue));
}

function includesAnyPattern(text, patterns) {
  const normalizedText = String(text || '').toLowerCase();
  return patterns.some((pattern) => normalizedText.includes(String(pattern).toLowerCase()));
}

function isLikelyDimensionColumn(column) {
  return includesAnyPattern(column, NL2SQL_DIMENSION_COLUMN_PATTERNS);
}

function isKeyMetricColumn(column) {
  const label = normalizeHighlightLabel(column);
  if (!label) {
    return false;
  }
  return includesAnyPattern(label, NL2SQL_KEY_COLUMN_PATTERNS) && !/^id$/i.test(label);
}

function resolveNl2SqlHighlightTone(label, fallbackIndex = 0) {
  const text = String(label || '').toLowerCase();
  if (/失败|异常|错误|告警|离线|下线|fault|error|fail|offline|alarm/.test(text)) {
    return 'danger';
  }
  if (/在线|成功|正常|当前|实时|最近|命中|缓存|success|online|current|latest|hit/.test(text)) {
    return 'success';
  }
  if (/统计|平均|最大|最小|趋势|历史|样本|窗口|avg|max|min|stat|sample|history/.test(text)) {
    return 'warning';
  }
  return ['primary', 'success', 'warning', 'info'][fallbackIndex % 4];
}

function buildHighlightItem(key, label, value, options = {}) {
  if (!hasDisplayValue(value)) {
    return null;
  }
  return {
    key,
    label,
    value: formatCellValue(value),
    unit: options.unit || '',
    tip: options.tip || '',
    tone: options.tone || resolveNl2SqlHighlightTone(label, options.index || 0),
  };
}

function resolveNl2SqlMetricColumns(rows = [], columns = []) {
  const normalizedColumns = columns.length ? columns : Object.keys(rows[0] || {});
  return normalizedColumns.filter((column) => {
    const sampleValue = rows.find((row) => hasDisplayValue(row?.[column]))?.[column];
    if (!hasDisplayValue(sampleValue)) {
      return false;
    }
    if (isKeyMetricColumn(column)) {
      return true;
    }
    return isNumericLikeValue(sampleValue) && !isLikelyDimensionColumn(column);
  });
}

function buildRdbHighlightItems(queryResult = {}) {
  const rows = Array.isArray(queryResult.rows) ? queryResult.rows : [];
  const columns = Array.isArray(queryResult.columns) ? queryResult.columns : [];
  const metricColumns = resolveNl2SqlMetricColumns(rows, columns);
  const items = [];
  if (rows.length === 1 && metricColumns.length > 0) {
    metricColumns.slice(0, NL2SQL_HIGHLIGHT_LIMIT).forEach((column, index) => {
      const item = buildHighlightItem(`rdb-${column}`, column, rows[0][column], {
        index,
        tip: t('ai.chat.index.748794-300'),
      });
      if (item) {
        items.push(item);
      }
    });
    return items;
  }
  const rowCountItem = buildHighlightItem(
    'rdb-row-count',
    t('ai.chat.index.748794-301'),
    queryResult.rowCount ?? rows.length,
    {
      tone: 'primary',
      tip: t('ai.chat.index.748794-302'),
    }
  );
  if (rowCountItem) {
    items.push(rowCountItem);
  }
  if (rows.length > 0 && metricColumns.length > 0) {
    metricColumns.slice(0, Math.max(NL2SQL_HIGHLIGHT_LIMIT - items.length, 0)).forEach((column, index) => {
      const item = buildHighlightItem(`rdb-first-${column}`, column, rows[0][column], {
        index: index + items.length,
        tip: t('ai.chat.index.748794-303'),
      });
      if (item) {
        items.push(item);
      }
    });
  }
  return items.slice(0, NL2SQL_HIGHLIGHT_LIMIT);
}

function buildRealtimeHighlightItems(result = {}) {
  return [
    buildHighlightItem('realtime-current-value', t('ai.chat.index.748794-132'), result.currentValue, {
      unit: result.unit || '',
      tone: 'success',
      tip: [result.deviceName, result.semanticName].filter(Boolean).join(' / '),
    }),
    buildHighlightItem('realtime-report-time', t('ai.chat.index.748794-304'), result.reportTime, {
      tone: 'info',
    }),
    buildHighlightItem(
      'realtime-cache-hit',
      t('ai.chat.index.748794-305'),
      result.cacheHit ? t('ai.chat.index.748794-51') : t('ai.chat.index.748794-52'),
      {
        tone: result.cacheHit ? 'success' : 'warning',
      }
    ),
  ].filter(Boolean);
}

function buildTsdbHighlightItems(result = {}) {
  return [
    buildHighlightItem('tsdb-latest-value', t('ai.chat.index.748794-134'), result.latestValue, {
      unit: result.unit || '',
      tone: 'success',
      tip: result.latestTime ? t('ai.chat.index.748794-426') + result.latestTime : '',
    }),
    buildHighlightItem('tsdb-statistic-value', t('ai.chat.index.748794-135'), result.statisticValue, {
      unit: result.unit || '',
      tone: 'warning',
      tip: result.statisticOperation || '',
    }),
    buildHighlightItem('tsdb-history-points', t('ai.chat.index.748794-306'), result.historyPoints?.length || 0, {
      tone: 'primary',
      tip: result.timeWindowLabel || '',
    }),
    buildHighlightItem('tsdb-samples', t('ai.chat.index.748794-307'), result.statisticSamples, {
      tone: 'info',
    }),
  ].filter(Boolean);
}

function buildHybridHighlightItems(result = {}) {
  return [
    buildHighlightItem('hybrid-current-value', t('ai.chat.index.748794-132'), result.currentValue, {
      unit: result.unit || '',
      tone: 'success',
      tip: result.currentTime ? t('ai.chat.index.748794-438') + result.currentTime : '',
    }),
    buildHighlightItem('hybrid-statistic-value', t('ai.chat.index.748794-135'), result.statisticValue, {
      unit: result.unit || '',
      tone: 'warning',
      tip: result.statisticOperation || '',
    }),
    buildHighlightItem('hybrid-history-points', t('ai.chat.index.748794-306'), result.historyPoints?.length || 0, {
      tone: 'primary',
      tip: result.timeWindowLabel || '',
    }),
    buildHighlightItem('hybrid-final-source', t('ai.chat.index.748794-308'), result.finalQueryMode, {
      tone: result.fallbackUsed ? 'warning' : 'info',
      tip: result.fallbackUsed ? t('ai.chat.index.748794-309') : '',
    }),
  ].filter(Boolean);
}

function buildNl2SqlHighlightItems(result = {}) {
  if (!result || typeof result !== 'object') {
    return [];
  }
  if (result.queryResult) {
    return buildRdbHighlightItems(result.queryResult);
  }
  if (result.realtimeResult) {
    return buildRealtimeHighlightItems(result.realtimeResult);
  }
  if (result.tsdbResult) {
    return buildTsdbHighlightItems(result.tsdbResult);
  }
  if (result.hybridResult) {
    return buildHybridHighlightItems(result.hybridResult);
  }
  return [];
}

function buildDeviceControlHighlightItems(result = {}) {
  if (!result || typeof result !== 'object') {
    return [];
  }
  return [
    buildHighlightItem('device-action', t('ai.chat.index.748794-441'), result.actionLabel, {
      tone: 'primary',
    }),
    buildHighlightItem('device-status', t('ai.chat.index.748794-442'), result.statusLabel, {
      tone: result.statusType || 'info',
    }),
    buildHighlightItem('device-scene', t('ai.chat.index.748794-444'), result.sceneId, {
      tone: 'info',
    }),
    buildHighlightItem('device-code', t('ai.chat.index.748794-445'), result.responseCode, {
      tone: result.statusType || 'info',
    }),
  ]
    .filter(Boolean)
    .slice(0, NL2SQL_HIGHLIGHT_LIMIT);
}

function shouldHighlightNl2SqlCell(column, value) {
  if (!hasDisplayValue(value)) {
    return false;
  }
  if (isKeyMetricColumn(column)) {
    return true;
  }
  return isNumericLikeValue(value) && !isLikelyDimensionColumn(column);
}

function getNl2SqlCellClass(column, value) {
  if (!shouldHighlightNl2SqlCell(column, value)) {
    return '';
  }
  return ['nl2sql-key-cell', `is-${resolveNl2SqlHighlightTone(column)}`];
}

function buildMessageGroups(messages = []) {
  const groupMap = new Map();
  messages.forEach((item, index) => {
    const messageNo = Number(item.messageNo);
    const roundIndex = Number.isFinite(messageNo) && messageNo > 0 ? Math.ceil(messageNo / 2) : index + 1;
    if (!groupMap.has(roundIndex)) {
      groupMap.set(roundIndex, {
        groupKey: `round-${roundIndex}`,
        roundIndex,
        items: [],
      });
    }
    groupMap.get(roundIndex).items.push(item);
  });
  return Array.from(groupMap.values()).map((group) => {
    const assistantMessage = [...group.items].reverse().find((item) => item.roleType === 'assistant') || null;
    const userMessage = [...group.items].reverse().find((item) => item.roleType === 'user') || null;
    return {
      ...group,
      assistantMessage,
      timeLabel: group.items[group.items.length - 1]?.createTime || '',
      abilityType: resolveMessageAbility(assistantMessage, userMessage),
      modelCode: assistantMessage?.modelCode || '',
    };
  });
}

function resolvePreviewAssistantMessage(item) {
  if (!item) {
    return null;
  }
  if (item.roleType === 'assistant') {
    return item;
  }
  const currentIndex = messageList.value.findIndex((candidate) => candidate.messageId === item.messageId);
  if (currentIndex > -1) {
    for (let index = currentIndex + 1; index < messageList.value.length; index += 1) {
      const candidate = messageList.value[index];
      if (candidate.roleType === 'assistant') {
        return candidate;
      }
    }
  }
  const currentMessageNo = Number(item.messageNo) || 0;
  return (
    messageList.value.find(
      (candidate) => candidate.roleType === 'assistant' && (Number(candidate.messageNo) || 0) > currentMessageNo
    ) || null
  );
}

function resolveUserMessageForAssistant(item) {
  if (!item) {
    return null;
  }
  const currentIndex = messageList.value.findIndex((candidate) => candidate.messageId === item.messageId);
  if (currentIndex > -1) {
    for (let index = currentIndex - 1; index >= 0; index -= 1) {
      const candidate = messageList.value[index];
      if (candidate.roleType === 'user') {
        return candidate;
      }
    }
  }
  const currentMessageNo = Number(item.messageNo) || 0;
  const candidates = messageList.value.filter(
    (candidate) => candidate.roleType === 'user' && (Number(candidate.messageNo) || 0) < currentMessageNo
  );
  return candidates[candidates.length - 1] || null;
}

function isMessageResultSelected(item) {
  const target = resolvePreviewAssistantMessage(item);
  if (!target || !currentResultMessage.value?.messageId) {
    return false;
  }
  return target.messageId === currentResultMessage.value.messageId;
}

function isMessageGroupActive(group) {
  if (!group || !currentResultMessage.value?.messageId) {
    return false;
  }
  return group.items.some((item) => item.messageId === currentResultMessage.value.messageId);
}

function handlePreviewMessageResult(item) {
  const target = resolvePreviewAssistantMessage(item);
  if (!target) {
    ElMessage.info(t('ai.chat.index.748794-310'));
    return;
  }
  selectedResultMessageId.value = target.messageId;
}

function handleResetResultSelection() {
  selectedResultMessageId.value = null;
}

function handleCopyMessage(item) {
  if (!item?.messageContent) {
    ElMessage.warning(t('ai.chat.index.748794-311'));
    return;
  }
  const result = copyText(item.messageContent);
  ElMessage[result.type](result.message);
}

function toggleMetaExpanded() {
  metaExpanded.value = !metaExpanded.value;
}

function toggleAnswerExpanded() {
  answerExpanded.value = !answerExpanded.value;
}

function toggleSkillExpanded() {
  skillExpanded.value = !skillExpanded.value;
}

function toggleDeviceReceiptExpanded() {
  deviceReceiptExpanded.value = !deviceReceiptExpanded.value;
}

function toggleDeviceRawExpanded() {
  deviceRawExpanded.value = !deviceRawExpanded.value;
}

function toggleProtocolMissingExpanded() {
  protocolMissingExpanded.value = !protocolMissingExpanded.value;
}

function toggleFallbackRawExpanded() {
  fallbackRawExpanded.value = !fallbackRawExpanded.value;
}

function toggleRouteAuditRawExpanded() {
  routeAuditRawExpanded.value = !routeAuditRawExpanded.value;
}

function resetResultBlockExpandedState() {
  deviceReceiptExpanded.value = false;
  deviceRawExpanded.value = false;
  protocolMissingExpanded.value = false;
  fallbackRawExpanded.value = false;
  routeAuditRawExpanded.value = false;
}

async function scrollMessageListToBottom(behavior = 'auto') {
  await nextTick();
  const container = messageListRef.value;
  if (!container || typeof container.scrollTo !== 'function') {
    return;
  }
  showScrollToBottomButton.value = false;
  container.scrollTo({
    top: container.scrollHeight,
    behavior,
  });
}

async function scrollMessageListToBottomStable(behavior = 'auto') {
  await scrollMessageListToBottom(behavior);
  window.setTimeout(() => {
    void scrollMessageListToBottom(behavior);
  }, 80);
  window.setTimeout(() => {
    void scrollMessageListToBottom(behavior);
  }, 240);
  window.setTimeout(() => {
    void scrollMessageListToBottom(behavior);
  }, 500);
}

function isMessageListNearBottom() {
  const container = messageListRef.value;
  if (!container) {
    return true;
  }
  const distanceToBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
  return distanceToBottom <= MESSAGE_BOTTOM_THRESHOLD;
}

function handleMessageListScroll() {
  showScrollToBottomButton.value = !isMessageListNearBottom();
}

async function handleScrollToLatest() {
  showScrollToBottomButton.value = false;
  await scrollMessageListToBottomStable('smooth');
}

function handleCopySql() {
  if (!latestSqlText.value) {
    ElMessage.warning(t('ai.chat.index.748794-312'));
    return;
  }
  const result = copyText(latestSqlText.value);
  ElMessage[result.type](result.message);
}

async function handleDownloadThingModelWorkbook() {
  const result = latestThingModelGenerateResult.value;
  if (!result?.artifactCode) {
    ElMessage.warning(t('ai.chat.index.748794-502'));
    return;
  }
  await handleDownloadThingModelWorkbookByCode(result.artifactCode, result.artifactName || t('ai.chat.index.748794-488'));
}

async function handleDownloadThingModelWorkbookByCode(artifactCode, filename) {
  if (!artifactCode) {
    ElMessage.warning(t('ai.chat.index.748794-502'));
    return;
  }
  await downloadThingModelWorkbook(artifactCode, normalizeDownloadFilename(filename || t('ai.chat.index.748794-488')));
}

async function handleDownloadRequirementEvaluationReport() {
  const result = latestRequirementEvaluationResult.value;
  if (!result?.artifactCode) {
    ElMessage.warning(t('ai.chat.index.748794-503'));
    return;
  }
  await handleDownloadRequirementEvaluationReportByCode(
    result.artifactCode,
    result.artifactName || t('ai.chat.index.748794-489')
  );
}

async function handleDownloadRequirementEvaluationReportByCode(artifactCode, filename) {
  if (!artifactCode) {
    ElMessage.warning(t('ai.chat.index.748794-503'));
    return;
  }
  await downloadRequirementEvaluationReport(
    artifactCode,
    normalizeDownloadFilename(filename || t('ai.chat.index.748794-489'))
  );
}

function nextLocalMessageId() {
  localStreamMessageId -= 1;
  return localStreamMessageId;
}

function buildLocalMessage(roleType, content, overrides = {}) {
  return {
    messageId: nextLocalMessageId(),
    messageNo: overrides.messageNo ?? Date.now(),
    roleType,
    messageContent: content || '',
    messageSummary: content || '',
    toolName: overrides.toolName || '',
    toolResult: overrides.toolResult || '',
    providerCode: overrides.providerCode || defaultRoute.value.providerCode || '',
    modelCode: overrides.modelCode || selectedModel.value || defaultRoute.value.modelCode || '',
    abilityType: overrides.abilityType || composerMode.value,
    routeMode: overrides.routeMode || 'DEFAULT',
    messageStatus: overrides.messageStatus || 'SUCCESS',
    createTime: overrides.createTime || new Date().toLocaleString(),
    thinkingStartedAt: overrides.thinkingStartedAt || null,
  };
}

function appendLocalStreamMessages(question, abilityType = composerMode.value) {
  const baseMessageNo = Date.now();
  const userMessage = buildLocalMessage('user', question, {
    abilityType,
    messageNo: baseMessageNo,
  });
  const assistantMessage = buildLocalMessage('assistant', '', {
    abilityType,
    messageNo: baseMessageNo + 1,
    messageStatus: 'RUNNING',
    thinkingStartedAt: Date.now(),
  });
  messageList.value = [...messageList.value, userMessage, assistantMessage];
  activeThinkingMessageId.value = assistantMessage.messageId;
  selectedResultMessageId.value = assistantMessage.messageId;
  return {
    userMessage,
    assistantMessage,
  };
}

function appendLocalConversationNotice(content, overrides = {}) {
  const message = buildLocalMessage('assistant', content, {
    abilityType: overrides.abilityType || composerMode.value,
    toolName: overrides.toolName || '',
    messageStatus: overrides.messageStatus || 'FAIL',
  });
  messageList.value = [...messageList.value, message];
  selectedResultMessageId.value = message.messageId;
  void scrollMessageListToBottom('smooth');
  return message;
}

function clearActiveThinkingMessage(messageId) {
  if (activeThinkingMessageId.value === messageId) {
    activeThinkingMessageId.value = null;
  }
}

function updateLocalAssistantMessage(messageId, updater) {
  messageList.value = messageList.value.map((item) => {
    if (item.messageId !== messageId) {
      return item;
    }
    const next = { ...item };
    updater(next);
    return next;
  });
}

function appendStageMessageContent(currentContent, stageText) {
  if (!stageText) {
    return currentContent || '';
  }
  if (!currentContent) {
    return stageText;
  }
  if (currentContent.includes(stageText)) {
    return currentContent;
  }
  return `${currentContent}\n${stageText}`;
}

function buildLocalSessionTitle(question) {
  const text = (question || '').replace(/\s+/g, ' ').trim();
  if (!text) {
    return t('ai.chat.index.748794-6');
  }
  return text.length > 24 ? `${text.slice(0, 24)}...` : text;
}

function resolveLocalSessionTitle(existingTitle, question) {
  const title = (existingTitle || '').trim();
  const timeTitle = /^(\d{4}[-/]\d{1,2}[-/]\d{1,2}|\d{1,2}:\d{2})/.test(title);
  if (!title || title === t('ai.chat.index.748794-6') || timeTitle) {
    return buildLocalSessionTitle(question);
  }
  return title;
}

function resolveSessionModeSnapshot(payload = {}, fallbackMode = 'AUTO') {
  return resolveSessionModeValue(payload, fallbackMode);
}

function upsertLocalSessionAfterStream(question, payload = {}, requestedAbilityType = composerMode.value) {
  const sessionId = payload.sessionId || currentSessionId.value;
  if (!sessionId) {
    return;
  }
  const existing = sessionList.value.find((item) => item.sessionId === sessionId) || {};
  const mergedSession = { ...existing, ...payload };
  const sessionMode = resolveSessionModeValue(
    mergedSession,
    existing.chatMode || (requestedAbilityType === 'AUTO' ? 'AUTO' : selectedMode.value)
  );
  const sessionModePolicy = resolveSessionModePolicySnapshot(mergedSession);
  const sessionPinnedMode = resolveSessionPinnedModeSnapshot(mergedSession);
  const nextItem = {
    ...existing,
    sessionId,
    sessionCode: payload.sessionCode || existing.sessionCode || currentSessionCode.value || '',
    sessionTitle: resolveLocalSessionTitle(existing.sessionTitle, question),
    chatMode: sessionMode,
    modePolicy: sessionModePolicy,
    pinnedMode: sessionPinnedMode,
    lastEffectiveMode:
      payload.lastEffectiveMode !== undefined ? payload.lastEffectiveMode || '' : existing.lastEffectiveMode || '',
    modelCode: payload.modelCode || existing.modelCode || selectedModel.value || defaultRoute.value.modelCode || '',
    providerCode: payload.providerCode || existing.providerCode || defaultRoute.value.providerCode || '',
    lastMessageTime: new Date().toLocaleString(),
  };
  sessionList.value = [nextItem, ...sessionList.value.filter((item) => item.sessionId !== sessionId)];
  currentSessionId.value = sessionId;
  currentSessionCode.value = nextItem.sessionCode || currentSessionCode.value || '';
  newSessionDraft.value = false;
}

function removeLocalMessages(messageIds = []) {
  const idSet = new Set(messageIds);
  messageList.value = messageList.value.filter((item) => !idSet.has(item.messageId));
}

function getSessionDistanceDays(item) {
  const timeText = item?.lastMessageTime || item?.updateTime || item?.createTime || '';
  const time = timeText ? new Date(String(timeText).replace(/-/g, '/')).getTime() : 0;
  if (!time || Number.isNaN(time)) {
    return 999;
  }
  const now = new Date();
  const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
  const itemStart = new Date(
    new Date(time).getFullYear(),
    new Date(time).getMonth(),
    new Date(time).getDate()
  ).getTime();
  return Math.max(0, Math.floor((todayStart - itemStart) / 86400000));
}

function ensureSelectedModel() {
  ensureDefaultRouteModelOption();
  if (!hasModelOption(selectedModel.value)) {
    selectedModel.value = resolveDefaultModelCode();
  }
}

async function refreshModelOptions() {
  const response = await listAiModelGrouped();
  groupedModels.value = normalizeGroupedModels(response.data || []);
  ensureSelectedModel();
}

async function handleModelDropdownVisibleChange(visible) {
  if (!visible) {
    return;
  }
  try {
    await refreshModelOptions();
  } catch (error) {
    ElMessage.warning(t('ai.chat.index.748794-313'));
  }
}

async function loadMeta() {
  const [routeResponse, groupResponse, skillResponse] = await Promise.all([
    getDefaultChatRoute(),
    listAiModelGrouped(),
    listAiSkill(),
  ]);
  defaultRoute.value = routeResponse.data || {};
  groupedModels.value = normalizeGroupedModels(groupResponse.data || []);
  skillList.value = skillResponse.data || [];
  ensureSelectedModel();

  if (!modeOptions.value.some((item) => item.value === selectedMode.value)) {
    selectedMode.value = 'AUTO';
    pendingModeOverride.value = '';
  }
}

function loadRouteDebugPreference() {
  try {
    return window.localStorage.getItem(ROUTE_DEBUG_PREFERENCE_KEY) === '1';
  } catch (_error) {
    return false;
  }
}

function loadSelectedModelPreference() {
  try {
    return window.localStorage.getItem(MODEL_PREFERENCE_KEY) || '';
  } catch (_error) {
    return '';
  }
}

function persistSelectedModelPreference(modelCode) {
  try {
    if (modelCode) {
      window.localStorage.setItem(MODEL_PREFERENCE_KEY, modelCode);
    } else {
      window.localStorage.removeItem(MODEL_PREFERENCE_KEY);
    }
  } catch (_error) {
    // 忽略本地持久化异常，不影响当前会话发送
  }
}

function persistRouteDebugPreference(enabled) {
  try {
    window.localStorage.setItem(ROUTE_DEBUG_PREFERENCE_KEY, enabled ? '1' : '0');
  } catch (_error) {
    // 忽略本地持久化异常，不影响当前页面使用
  }
}

function getPageStateStorageKey() {
  return `${PAGE_STATE_KEY_PREFIX}:${route.path || 'default'}`;
}

function getRuntimeVisitedRouteKeys() {
  if (!window[RUNTIME_VISITED_ROUTE_KEY]) {
    window[RUNTIME_VISITED_ROUTE_KEY] = new Set();
  }
  return window[RUNTIME_VISITED_ROUTE_KEY];
}

function markCurrentRouteVisitedInRuntime() {
  getRuntimeVisitedRouteKeys().add(route.path || 'default');
}

function hasCurrentRouteVisitedInRuntime() {
  return getRuntimeVisitedRouteKeys().has(route.path || 'default');
}

function clearCurrentRouteVisitedInRuntime() {
  getRuntimeVisitedRouteKeys().delete(route.path || 'default');
}

function isCurrentRouteBrowserReload() {
  try {
    const entries = window.performance?.getEntriesByType?.('navigation') || [];
    const navigation = entries[0];
    const isReload = navigation?.type === 'reload' || window.performance?.navigation?.type === 1;
    if (!isReload) {
      return false;
    }
    if (navigation?.name) {
      const loadedPath = new URL(navigation.name).pathname;
      return loadedPath === route.path;
    }
    return window.location.pathname === route.path;
  } catch (_error) {
    return false;
  }
}

function shouldRestorePageState() {
  if (isCurrentRouteBrowserReload()) {
    return true;
  }
  return Boolean(settingsStore.tagsView && hasCurrentRouteVisitedInRuntime());
}

function readPageStateSnapshot() {
  if (!shouldRestorePageState()) {
    return null;
  }
  try {
    const raw = window.sessionStorage.getItem(getPageStateStorageKey());
    if (!raw) {
      return null;
    }
    const snapshot = JSON.parse(raw);
    if (!snapshot || snapshot.version !== 1 || snapshot.routePath !== route.path) {
      return null;
    }
    return snapshot;
  } catch (_error) {
    return null;
  }
}

function createPageStateSnapshot() {
  return {
    version: 1,
    routePath: route.path,
    savedAt: Date.now(),
    newSessionDraft: newSessionDraft.value,
    currentSessionId: currentSessionId.value,
    currentSessionCode: currentSessionCode.value,
    selectedMode: selectedMode.value,
    pendingModeOverride: pendingModeOverride.value,
    selectedModel: selectedModel.value,
    sidebarCollapsed: sidebarCollapsed.value,
    rightPanelVisible: rightPanelVisible.value,
    rightPanelType: rightPanelType.value,
    selectedResultMessageId: selectedResultMessageId.value,
    draftMessage: draftMessage.value,
    lastEffectiveMode: lastEffectiveMode.value,
    lastExecutedSkill: lastExecutedSkill.value,
  };
}

function persistPageStateSnapshot() {
  try {
    window.sessionStorage.setItem(getPageStateStorageKey(), JSON.stringify(createPageStateSnapshot()));
  } catch (_error) {
    // 忽略页面状态快照异常，避免影响对话主链路
  }
}

function applyPageStateSnapshot(snapshot) {
  if (!snapshot) {
    return;
  }
  newSessionDraft.value = snapshot.newSessionDraft !== false;
  currentSessionId.value = snapshot.newSessionDraft === false ? snapshot.currentSessionId || null : null;
  currentSessionCode.value = snapshot.newSessionDraft === false ? snapshot.currentSessionCode || '' : '';
  selectedMode.value = snapshot.selectedMode || 'AUTO';
  pendingModeOverride.value = snapshot.pendingModeOverride || '';
  if (snapshot.selectedModel) {
    selectedModel.value = snapshot.selectedModel;
  }
  sidebarCollapsed.value = Boolean(snapshot.sidebarCollapsed);
  rightPanelVisible.value = Boolean(snapshot.rightPanelVisible);
  rightPanelType.value = snapshot.rightPanelType || 'result';
  selectedResultMessageId.value = snapshot.selectedResultMessageId || null;
  draftMessage.value = snapshot.draftMessage || '';
  lastEffectiveMode.value = snapshot.lastEffectiveMode || '';
  lastExecutedSkill.value = snapshot.lastExecutedSkill || '';
}

function applyPageStateSnapshotAfterLoad(snapshot) {
  if (!snapshot) {
    return;
  }
  if (snapshot.selectedModel) {
    selectedModel.value = snapshot.selectedModel;
    ensureSelectedModel();
  }
  if (snapshot.newSessionDraft !== false || !currentSessionId.value) {
    newSessionDraft.value = true;
    currentSessionId.value = null;
    currentSessionCode.value = '';
    messageList.value = [];
    selectedResultMessageId.value = null;
  } else {
    newSessionDraft.value = false;
    selectedResultMessageId.value = snapshot.selectedResultMessageId || null;
  }
  selectedMode.value = snapshot.selectedMode || selectedMode.value || 'AUTO';
  pendingModeOverride.value = snapshot.pendingModeOverride || '';
  sidebarCollapsed.value = Boolean(snapshot.sidebarCollapsed);
  rightPanelVisible.value = Boolean(snapshot.rightPanelVisible);
  rightPanelType.value = snapshot.rightPanelType || 'result';
  draftMessage.value = snapshot.draftMessage || '';
}

function isCurrentRouteStillInTagsView() {
  return tagsViewStore.visitedViews.some((item) => item.path === route.path || item.fullPath === route.fullPath);
}

function handleRouteDebugPreferenceChange(value) {
  const nextValue = Boolean(value);
  localRouteDebugEnabled.value = nextValue;
  persistRouteDebugPreference(nextValue);
  if (!nextValue) {
    routeAuditRawExpanded.value = false;
  }
  ElMessage.success(nextValue ? t('ai.chat.index.748794-314') : t('ai.chat.index.748794-315'));
}

async function loadSessionList() {
  sessionLoading.value = true;
  try {
    const response = await listChatSession({
      pageNum: 1,
      pageSize: 50,
    });
    sessionList.value = response.rows || [];
    if (currentSessionId.value) {
      const exists = sessionList.value.some((item) => item.sessionId === currentSessionId.value);
      if (!exists) {
        currentSessionId.value = null;
        currentSessionCode.value = '';
        messageList.value = [];
        selectedResultMessageId.value = null;
        newSessionDraft.value = true;
      }
    }
  } finally {
    sessionLoading.value = false;
  }
}

async function loadMessageList(sessionId) {
  if (!sessionId) {
    messageList.value = [];
    currentSessionCode.value = '';
    lastEffectiveMode.value = '';
    lastExecutedSkill.value = '';
    return;
  }
  messageLoading.value = true;
  try {
    const response = await listChatMessage({ sessionId });
    messageList.value = response.data || [];
    const currentSession = sessionList.value.find((item) => item.sessionId === sessionId);
    currentSessionCode.value = currentSession?.sessionCode || '';
    selectedMode.value = resolveSessionModeValue(currentSession, 'AUTO');
    pendingModeOverride.value = '';
    const latestAssistant = [...messageList.value].reverse().find((item) => item.roleType === 'assistant');
    lastEffectiveMode.value = currentSession?.lastEffectiveMode || latestAssistant?.abilityType || '';
    lastExecutedSkill.value = latestAssistant?.toolName || '';
    selectedResultMessageId.value = null;
    await scrollMessageListToBottomStable();
  } finally {
    messageLoading.value = false;
  }
}

async function loadMetaAndSessions() {
  await loadMeta();
  await loadSessionList();
  if (currentSessionId.value) {
    await loadMessageList(currentSessionId.value);
  }
}

async function loadObservabilityStats() {
  observabilityLoading.value = true;
  observabilityLoadError.value = '';
  try {
    const response = await getChatObservabilityStats({ days: observabilityDays.value });
    observabilityStats.value = {
      ...createDefaultObservabilityStats(observabilityDays.value),
      ...(response?.data || {}),
    };
    observabilityLoaded.value = true;
  } catch (error) {
    observabilityStats.value = createDefaultObservabilityStats(observabilityDays.value);
    observabilityLoaded.value = false;
    observabilityLoadError.value = error?.message || t('ai.chat.index.748794-316');
  } finally {
    observabilityLoading.value = false;
  }
}

function handleOpenUsageGuidePanel() {
  if (rightPanelVisible.value && rightPanelType.value === 'usageGuide') {
    closeRightPanel();
    return;
  }
  rightPanelType.value = 'usageGuide';
  rightPanelVisible.value = true;
  observabilityDrawerVisible.value = false;
  skillExpanded.value = true;
}

async function handleOpenObservabilityPanel() {
  if (rightPanelVisible.value && rightPanelType.value === 'observability') {
    closeRightPanel();
    return;
  }
  rightPanelType.value = 'observability';
  rightPanelVisible.value = true;
  observabilityDrawerVisible.value = false;
  if (!observabilityLoaded.value) {
    await loadObservabilityStats();
  }
}

function handleOpenResultPanel(item) {
  const target = resolvePreviewAssistantMessage(item);
  if (!target) {
    ElMessage.info(t('ai.chat.index.748794-310'));
    return;
  }
  if (
    rightPanelVisible.value &&
    rightPanelType.value === 'result' &&
    currentResultMessage.value?.messageId === target.messageId
  ) {
    closeRightPanel();
    return;
  }
  selectedResultMessageId.value = target.messageId;
  rightPanelType.value = 'result';
  rightPanelVisible.value = true;
}

function closeRightPanel() {
  rightPanelVisible.value = false;
}

async function handleRefreshObservabilityStats() {
  await loadObservabilityStats();
}

async function handleObservabilityDaysChange() {
  if (rightPanelType.value !== 'observability' && !observabilityLoaded.value) {
    return;
  }
  await loadObservabilityStats();
}

async function maybeRefreshObservabilityStats() {
  if (rightPanelType.value !== 'observability' && !observabilityLoaded.value) {
    return;
  }
  await loadObservabilityStats();
}

function handleNewSession() {
  currentSessionId.value = null;
  currentSessionCode.value = '';
  messageList.value = [];
  selectedResultMessageId.value = null;
  newSessionDraft.value = true;
  draftMessage.value = '';
  selectedMode.value = 'AUTO';
  pendingModeOverride.value = '';
  lastEffectiveMode.value = '';
  lastExecutedSkill.value = '';
  clearComposerUploadFiles();
}

function handleSelectAutoMode() {
  selectedMode.value = 'AUTO';
  pendingModeOverride.value = '';
}

function handleComposerFileChange(file) {
  if (!file?.uid) {
    return;
  }
  selectedUploadFiles.value = [
    {
      uid: file.uid,
      name: file.name || t('ai.chat.index.748794-317'),
      raw: file.raw,
    },
  ];
}

function handleComposerFileExceed(files = []) {
  const file = files[0];
  if (!file) {
    return;
  }
  composerUploadRef.value?.clearFiles?.();
  const uid = Date.now();
  file.uid = uid;
  composerUploadRef.value?.handleStart?.(file);
}

function handleRemoveComposerFile(uid) {
  selectedUploadFiles.value = selectedUploadFiles.value.filter((item) => item.uid !== uid);
  composerUploadRef.value?.clearFiles?.();
}

function clearComposerUploadFiles() {
  selectedUploadFiles.value = [];
  composerUploadRef.value?.clearFiles?.();
}

function createChatAbortController() {
  activeChatAbortController.value?.abort();
  const controller = new AbortController();
  activeChatAbortController.value = controller;
  return controller;
}

function clearChatAbortController(controller) {
  if (activeChatAbortController.value === controller) {
    activeChatAbortController.value = null;
  }
}

function releaseSendingForController(controller) {
  if (!controller || activeChatAbortController.value === controller) {
    clearChatAbortController(controller);
    sending.value = false;
  }
}

function isChatAbortError(error) {
  return error?.name === 'AbortError' || error?.name === 'CanceledError' || error?.message === 'canceled';
}

function resolveClientStreamErrorMessage(error, fallback = t('ai.chat.index.748794-318')) {
  const message = String(error?.message || fallback || '').trim();
  return message || fallback;
}

function handleStopSending() {
  const controller = activeChatAbortController.value;
  if (!controller || controller.signal.aborted) {
    return;
  }
  controller.abort();
}

function handleComposerSendAction() {
  if (sending.value) {
    handleStopSending();
    return;
  }
  handleSend();
}

async function handleSelectSession(item) {
  newSessionDraft.value = false;
  currentSessionId.value = item.sessionId;
  currentSessionCode.value = item.sessionCode || '';
  if (item.modelCode) {
    selectedModel.value = item.modelCode;
  }
  selectedMode.value = resolveSessionModeValue(item, 'AUTO');
  pendingModeOverride.value = '';
  await loadMessageList(item.sessionId);
}

async function submitQuestionSyncByPayload(requestPayload) {
  const response = await sendChatMessage(requestPayload);
  const data = response?.data || {};
  currentSessionId.value = data.sessionId || currentSessionId.value;
  currentSessionCode.value = data.sessionCode || currentSessionCode.value || '';
  newSessionDraft.value = false;
  lastEffectiveMode.value = data.lastEffectiveMode || data.effectiveChatMode || data.chatMode || '';
  lastExecutedSkill.value = data.executedSkill || '';
  await loadSessionList();
  if (currentSessionId.value) {
    await loadMessageList(currentSessionId.value);
  }
  await maybeRefreshObservabilityStats();
}

async function submitQuestionSync(question, overrides = {}) {
  await submitQuestionSyncByPayload(buildChatRequestPayload(question, overrides));
}

async function consumeChatStream(requestData, question, streamSender = sendChatMessageStream, streamOptions = {}) {
  const abilityType = requestData?.chatMode || composerMode.value;
  const delaySessionBinding = newSessionDraft.value && !requestData?.sessionId;
  let streamSessionId = requestData?.sessionId || currentSessionId.value || null;
  let streamSessionCode = currentSessionCode.value || '';
  const { userMessage, assistantMessage } = appendLocalStreamMessages(question, abilityType);
  await scrollMessageListToBottom('smooth');
  let receivedEvent = false;
  let receivedTerminalEvent = false;
  let receivedAssistantContent = false;
  let streamErrorMessage = '';
  let latestStreamPayload = {};
  let bufferedAssistantContent = '';
  let latestChunkPayload = {};
  let chunkRenderTimer = null;
  let protocolTaskId = null;
  let visualCompleteNotified = false;
  const normalizeStreamText = (value) =>
    String(value || '')
      .replace(/\r\n/g, '\n')
      .trim();
  const targetQuestionText = normalizeStreamText(question);
  const targetFileKeyword = normalizeStreamText(streamOptions?.protocolTaskLookupKeyword);
  const hasProtocolRecoveryHint = () => Boolean(protocolTaskId || targetFileKeyword);

  const clearChunkRenderTimer = () => {
    if (chunkRenderTimer) {
      window.clearTimeout(chunkRenderTimer);
      chunkRenderTimer = null;
    }
  };
  const notifyVisualComplete = () => {
    if (visualCompleteNotified) {
      return;
    }
    visualCompleteNotified = true;
    if (typeof streamOptions?.onVisualComplete === 'function') {
      streamOptions.onVisualComplete();
    }
  };
  const flushBufferedAssistantContent = (scroll = true) => {
    if (!bufferedAssistantContent) {
      return;
    }
    clearChunkRenderTimer();
    const payload = latestChunkPayload || {};
    updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
      item.abilityType = payload.effectiveChatMode || item.abilityType;
      item.toolName = payload.executedSkill || item.toolName;
      item.messageStatus = 'RUNNING';
      item.messageContent = bufferedAssistantContent;
      delete item.streamClarifyPayload;
    });
    if (scroll) {
      void scrollMessageListToBottom('smooth');
    }
  };
  const captureProtocolTaskId = (payload = {}) => {
    const candidateTaskId = resolveProtocolTaskIdFromToolResult(payload.toolResult);
    if (candidateTaskId) {
      protocolTaskId = candidateTaskId;
    }
  };
  const lookupProtocolTaskIdByFile = async () => {
    if (protocolTaskId) {
      return protocolTaskId;
    }
    if (!targetFileKeyword) {
      return null;
    }
    try {
      const response = await listProtocolAdaptationTask({
        taskName: targetFileKeyword,
        pageNum: 1,
        pageSize: 10,
      });
      const rows = response?.rows || response?.data?.rows || [];
      const matchedTask = rows.find((item) => {
        if (!item?.taskId) {
          return false;
        }
        const remark = String(item?.remark || '');
        const taskText = `${item?.taskName || ''} ${item?.protocolName || ''} ${remark}`;
        const sessionMatched =
          !streamSessionId ||
          !remark.includes(t('ai.chat.index.748794-319')) ||
          remark.includes(`会话ID=${streamSessionId}`);
        return sessionMatched && taskText.includes(targetFileKeyword);
      });
      if (matchedTask?.taskId) {
        protocolTaskId = matchedTask.taskId;
      }
      return protocolTaskId;
    } catch (error) {
      console.warn(t('ai.chat.index.748794-320'), error);
      return null;
    }
  };
  const scheduleBufferedAssistantContentFlush = () => {
    if (chunkRenderTimer) {
      return;
    }
    chunkRenderTimer = window.setTimeout(() => {
      flushBufferedAssistantContent(true);
    }, STREAM_CHUNK_RENDER_INTERVAL_MS);
  };
  const syncAssistantMessageAfterStream = async () => {
    try {
      const targetQuestion = targetQuestionText;
      const baseRecoveryAttempts = streamSessionId ? 3 : 5;
      const optionRecoveryAttempts = Number(streamOptions?.recoveryAttempts);
      const recoveryAttempts = Math.max(optionRecoveryAttempts || baseRecoveryAttempts, receivedEvent ? 3 : 8);
      const recoveryDelay = Math.max(Number(streamOptions?.recoveryDelay) || (receivedEvent ? 700 : 1000), 0);
      const recoverySessionPageSize = Math.max(
        Number(streamOptions?.recoverySessionPageSize) || (receivedEvent ? 10 : 20),
        1
      );
      const waitRecovery = (delay) => new Promise((resolve) => window.setTimeout(resolve, delay));
      const isMatchedUserQuestion = (content) => {
        const normalized = normalizeStreamText(content);
        if (!targetQuestion || !normalized) {
          return false;
        }
        if (targetFileKeyword && !normalized.includes(targetFileKeyword)) {
          return false;
        }
        return (
          normalized === targetQuestion || normalized.includes(targetQuestion) || targetQuestion.includes(normalized)
        );
      };
      const resolveLatestAssistantForQuestion = (messages) => {
        const matchedUserIndex = [...messages]
          .map((item, index) => ({ item, index }))
          .reverse()
          .find(({ item }) => item?.roleType === 'user' && isMatchedUserQuestion(item.messageContent))?.index;
        if (matchedUserIndex === undefined) {
          return null;
        }
        return (
          [...messages.slice(matchedUserIndex + 1)].reverse().find((item) => item?.roleType === 'assistant') || null
        );
      };
      const loadRemoteMessages = async (sessionId) => {
        if (!sessionId) {
          return [];
        }
        const response = await listChatMessage({ sessionId });
        return response.data || [];
      };
      const hasNewerLocalMessageAfterAssistant = () => {
        const assistantIndex = messageList.value.findIndex((item) => item.messageId === assistantMessage.messageId);
        return assistantIndex >= 0 && assistantIndex < messageList.value.length - 1;
      };
      const applyRemoteMessages = async (sessionCandidate, remoteMessages, latestAssistant) => {
        if (hasNewerLocalMessageAfterAssistant()) {
          return false;
        }
        streamSessionId = sessionCandidate?.sessionId || streamSessionId;
        streamSessionCode = sessionCandidate?.sessionCode || streamSessionCode;
        messageList.value = remoteMessages;
        currentSessionId.value = streamSessionId || currentSessionId.value;
        currentSessionCode.value = streamSessionCode || currentSessionCode.value || '';
        newSessionDraft.value = false;
        lastEffectiveMode.value =
          latestAssistant?.abilityType || latestStreamPayload.effectiveChatMode || lastEffectiveMode.value;
        lastExecutedSkill.value =
          latestAssistant?.toolName || latestStreamPayload.executedSkill || lastExecutedSkill.value;
        selectedResultMessageId.value = latestAssistant?.messageId || selectedResultMessageId.value;
        await scrollMessageListToBottomStable();
        return true;
      };
      const buildSessionCandidates = async () => {
        const candidates = [];
        const pushCandidate = (item) => {
          const sessionId = item?.sessionId || item?.id;
          if (!sessionId || candidates.some((candidate) => candidate.sessionId === sessionId)) {
            return;
          }
          candidates.push({
            ...item,
            sessionId,
          });
        };
        pushCandidate({ sessionId: streamSessionId, sessionCode: streamSessionCode });
        if (!streamSessionId || delaySessionBinding) {
          const response = await listChatSession({ pageNum: 1, pageSize: recoverySessionPageSize });
          const rows = response.rows || [];
          if (rows.length) {
            sessionList.value = rows;
          }
          rows.forEach(pushCandidate);
        }
        return candidates;
      };
      const trySyncCandidate = async (sessionCandidate) => {
        const remoteMessages = await loadRemoteMessages(sessionCandidate?.sessionId);
        if (!remoteMessages.length) {
          return false;
        }
        const latestAssistant = resolveLatestAssistantForQuestion(remoteMessages);
        if (!latestAssistant || latestAssistant.messageStatus === 'RUNNING') {
          return false;
        }
        return await applyRemoteMessages(sessionCandidate, remoteMessages, latestAssistant);
      };

      let candidates = await buildSessionCandidates();
      for (let attempt = 0; attempt < recoveryAttempts; attempt += 1) {
        if (!candidates.length || attempt > 0) {
          candidates = await buildSessionCandidates();
        }
        for (const candidate of candidates) {
          const synced = await trySyncCandidate(candidate);
          if (synced) {
            return true;
          }
        }
        if (attempt < recoveryAttempts - 1 && recoveryDelay > 0) {
          await waitRecovery(recoveryDelay);
        }
      }
      return false;
    } catch (error) {
      console.warn(t('ai.chat.index.748794-321'), error);
      return false;
    }
  };
  const waitProtocolTaskRecovery = async () => {
    const recoveryAttempts = Math.max(Number(streamOptions?.protocolTaskRecoveryAttempts) || 120, 1);
    const recoveryDelay = Math.max(Number(streamOptions?.protocolTaskRecoveryDelay) || 1000, 0);
    const lookupAttempts = Math.max(Number(streamOptions?.protocolTaskLookupAttempts) || 20, 1);
    const waitRecovery = (delay) => new Promise((resolve) => window.setTimeout(resolve, delay));
    let lookupMissCount = 0;

    for (let attempt = 0; attempt < recoveryAttempts; attempt += 1) {
      if (!hasProtocolRecoveryHint()) {
        await lookupProtocolTaskIdByFile();
      }
      if (!protocolTaskId) {
        lookupMissCount += 1;
        if (lookupMissCount >= lookupAttempts) {
          return false;
        }
        if (attempt < recoveryAttempts - 1 && recoveryDelay > 0) {
          await waitRecovery(recoveryDelay);
        }
        continue;
      }
      try {
        const response = await getProtocolAdaptationTask(protocolTaskId);
        const task = response?.data || {};
        if (task?.taskId) {
          const progressContent = buildProtocolTaskProgressMessage(task);
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = 'PROTOCOL_PARSE';
            item.toolName = 'protocol_parse_chat';
            item.toolResult = buildProtocolTaskProgressToolResult(task);
            if (isProtocolTaskTerminal(task)) {
              item.messageStatus = task.taskStatus === 'FAILED' ? 'FAIL' : 'SUCCESS';
            } else {
              item.messageStatus = 'RUNNING';
            }
            item.messageContent = progressContent;
            delete item.streamClarifyPayload;
          });
          selectedResultMessageId.value = assistantMessage.messageId;
          void scrollMessageListToBottom('smooth');
          if (isProtocolTaskTerminal(task)) {
            notifyVisualComplete();
            const synced = await syncAssistantMessageAfterStream();
            return synced || true;
          }
        }
      } catch (error) {
        console.warn(t('ai.chat.index.748794-322'), error);
        return false;
      }
      if (attempt < recoveryAttempts - 1 && recoveryDelay > 0) {
        await waitRecovery(recoveryDelay);
      }
    }
    return false;
  };

  try {
    await streamSender(requestData, {
      signal: streamOptions.signal,
      idleTimeout: STREAM_IDLE_COMPLETE_MS,
      shouldCloseOnIdle: ({ data }) =>
        data?.eventType === 'chunk' && Boolean(String(data.content || data.delta || '').trim()),
      onEvent: ({ data }) => {
        const payload = data || {};
        receivedEvent = true;
        latestStreamPayload = {
          ...latestStreamPayload,
          ...payload,
        };
        captureProtocolTaskId(payload);
        streamSessionId = payload.sessionId || streamSessionId;
        streamSessionCode = payload.sessionCode || streamSessionCode;
        if (!delaySessionBinding) {
          currentSessionId.value = streamSessionId || currentSessionId.value;
          currentSessionCode.value = streamSessionCode || currentSessionCode.value || '';
        }
        if (payload.lastEffectiveMode || payload.effectiveChatMode) {
          lastEffectiveMode.value = payload.lastEffectiveMode || payload.effectiveChatMode;
        }
        if (payload.executedSkill) {
          lastExecutedSkill.value = payload.executedSkill;
        }

        if (payload.eventType === 'start') {
          if (payload.routeAudit) {
            updateLocalAssistantMessage(userMessage.messageId, (item) => {
              item.toolName = 'auto_router_analyze';
              item.toolResult = payload.routeAudit;
            });
          }
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = payload.effectiveChatMode || payload.chatMode || abilityType;
            item.modelCode = payload.modelCode || item.modelCode;
            item.providerCode = payload.providerCode || item.providerCode;
            delete item.streamClarifyPayload;
          });
          return;
        }

        if (payload.eventType === 'resume' || payload.eventType === 'stage') {
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = payload.effectiveChatMode || item.abilityType;
            item.toolName = payload.executedSkill || item.toolName;
            item.toolResult = payload.toolResult || item.toolResult;
            item.messageStatus = 'RUNNING';
            item.messageContent = appendStageMessageContent(
              item.messageContent,
              payload.content || payload.delta || ''
            );
            delete item.streamClarifyPayload;
          });
          void scrollMessageListToBottom('smooth');
          return;
        }

        if (payload.eventType === 'chunk') {
          receivedAssistantContent = true;
          const currentAssistant = messageList.value.find((item) => item.messageId === assistantMessage.messageId);
          const baseContent = bufferedAssistantContent || currentAssistant?.messageContent || '';
          bufferedAssistantContent = payload.content || `${baseContent}${payload.delta || ''}`;
          latestChunkPayload = payload;
          scheduleBufferedAssistantContentFlush();
          return;
        }

        if (payload.eventType === 'clarify') {
          flushBufferedAssistantContent(false);
          receivedTerminalEvent = true;
          if (payload.routeAudit) {
            updateLocalAssistantMessage(userMessage.messageId, (item) => {
              item.toolName = 'auto_router_analyze';
              item.toolResult = payload.routeAudit;
            });
          }
          upsertLocalSessionAfterStream(
            question,
            {
              ...payload,
              sessionId: payload.sessionId || streamSessionId,
              sessionCode: payload.sessionCode || streamSessionCode,
            },
            abilityType
          );
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = payload.effectiveChatMode || item.abilityType;
            item.toolName = payload.executedSkill || item.toolName;
            item.toolResult = payload.toolResult || item.toolResult;
            item.messageStatus = 'SUCCESS';
            item.messageContent = payload.content || item.messageContent;
            item.streamClarifyPayload = {
              clarifyType: payload.clarifyType,
              clarifyKey: payload.clarifyKey,
              clarifyTitle: payload.clarifyTitle,
              clarifyOptions: Array.isArray(payload.clarifyOptions) ? payload.clarifyOptions : [],
              resumeToken: payload.resumeToken || '',
              resumeQuestion: payload.resumeQuestion || '',
            };
          });
          void scrollMessageListToBottomStable('smooth');
          notifyVisualComplete();
          return false;
        }

        if (payload.eventType === 'done') {
          flushBufferedAssistantContent(false);
          receivedTerminalEvent = true;
          if (payload.routeAudit) {
            updateLocalAssistantMessage(userMessage.messageId, (item) => {
              item.toolName = 'auto_router_analyze';
              item.toolResult = payload.routeAudit;
            });
          }
          upsertLocalSessionAfterStream(
            question,
            {
              ...payload,
              sessionId: payload.sessionId || streamSessionId,
              sessionCode: payload.sessionCode || streamSessionCode,
            },
            abilityType
          );
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = payload.effectiveChatMode || item.abilityType;
            item.toolName = payload.executedSkill || item.toolName;
            item.toolResult = payload.toolResult || item.toolResult;
            item.messageStatus = 'SUCCESS';
            item.messageContent = payload.content || item.messageContent;
            delete item.streamClarifyPayload;
          });
          void scrollMessageListToBottomStable('smooth');
          notifyVisualComplete();
          return false;
        }

        if (payload.eventType === 'error') {
          flushBufferedAssistantContent(false);
          receivedTerminalEvent = true;
          if (payload.routeAudit) {
            updateLocalAssistantMessage(userMessage.messageId, (item) => {
              item.toolName = 'auto_router_analyze';
              item.toolResult = payload.routeAudit;
            });
          }
          streamErrorMessage = payload.content || payload.errorMessage || t('ai.chat.index.748794-323');
          upsertLocalSessionAfterStream(
            question,
            {
              ...payload,
              sessionId: payload.sessionId || streamSessionId,
              sessionCode: payload.sessionCode || streamSessionCode,
            },
            abilityType
          );
          updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
            item.abilityType = payload.effectiveChatMode || item.abilityType;
            item.toolName = payload.executedSkill || item.toolName;
            item.modelCode = payload.modelCode || item.modelCode;
            item.providerCode = payload.providerCode || item.providerCode;
            item.messageStatus = 'FAIL';
            item.messageContent = streamErrorMessage;
            delete item.streamClarifyPayload;
          });
          void scrollMessageListToBottomStable('smooth');
          notifyVisualComplete();
          return false;
        }
      },
    });
    if (!receivedTerminalEvent && receivedAssistantContent) {
      flushBufferedAssistantContent(false);
      upsertLocalSessionAfterStream(
        question,
        {
          ...latestStreamPayload,
          sessionId: streamSessionId,
          sessionCode: streamSessionCode,
        },
        abilityType
      );
      if (!protocolTaskId) {
        updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
          item.abilityType = latestStreamPayload.effectiveChatMode || item.abilityType;
          item.toolName = latestStreamPayload.executedSkill || item.toolName;
          item.toolResult = latestStreamPayload.toolResult || item.toolResult;
          item.messageStatus = 'SUCCESS';
          item.messageContent = item.messageContent || latestStreamPayload.content || '';
          delete item.streamClarifyPayload;
        });
        void scrollMessageListToBottomStable('smooth');
        notifyVisualComplete();
      }
      const synced = await syncAssistantMessageAfterStream();
      if (synced) {
        notifyVisualComplete();
      } else {
        if (hasProtocolRecoveryHint()) {
          const recovered = await waitProtocolTaskRecovery();
          if (recovered) {
            return;
          }
        }
        updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
          item.abilityType = latestStreamPayload.effectiveChatMode || item.abilityType;
          item.toolName = latestStreamPayload.executedSkill || item.toolName;
          item.toolResult = latestStreamPayload.toolResult || item.toolResult;
          item.messageStatus = 'SUCCESS';
          item.messageContent = item.messageContent || latestStreamPayload.content || '';
          delete item.streamClarifyPayload;
        });
        void scrollMessageListToBottomStable('smooth');
        notifyVisualComplete();
      }
    }
  } catch (error) {
    flushBufferedAssistantContent(false);
    if (isChatAbortError(error)) {
      updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
        item.messageStatus = 'CANCELLED';
        item.messageContent = item.messageContent || t('ai.chat.index.748794-324');
        delete item.streamClarifyPayload;
      });
      void scrollMessageListToBottomStable('smooth');
      notifyVisualComplete();
      return;
    }
    if (!receivedEvent) {
      updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
        item.messageStatus = 'RUNNING';
        item.messageContent = streamOptions?.recoveringMessage || t('ai.chat.index.748794-325');
        delete item.streamClarifyPayload;
      });
      void scrollMessageListToBottom('smooth');
    }
    const synced = await syncAssistantMessageAfterStream();
    if (synced) {
      notifyVisualComplete();
      return;
    }
    if (hasProtocolRecoveryHint()) {
      const recovered = await waitProtocolTaskRecovery();
      if (recovered) {
        return;
      }
    }
    if (!receivedEvent) {
      const errorMessage = resolveClientStreamErrorMessage(
        error,
        streamOptions?.fallbackErrorMessage || t('ai.chat.index.748794-326')
      );
      updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
        item.messageStatus = 'FAIL';
        item.messageContent = errorMessage;
        delete item.streamClarifyPayload;
      });
      void scrollMessageListToBottomStable('smooth');
      notifyVisualComplete();
      return;
    }
    const errorMessage = resolveClientStreamErrorMessage(error, streamErrorMessage || t('ai.chat.index.748794-327'));
    updateLocalAssistantMessage(assistantMessage.messageId, (item) => {
      item.messageStatus = 'FAIL';
      item.messageContent = item.messageContent || errorMessage;
      delete item.streamClarifyPayload;
    });
    void scrollMessageListToBottomStable('smooth');
    notifyVisualComplete();
    return;
  } finally {
    clearChunkRenderTimer();
    clearActiveThinkingMessage(assistantMessage.messageId);
  }

  await maybeRefreshObservabilityStats();
}

async function submitQuestionStream(question, overrides = {}, streamOptions = {}) {
  await consumeChatStream(buildChatRequestPayload(question, overrides), question, sendChatMessageStream, streamOptions);
}

async function submitClarifyResume(sourceMessage, clarifyPayload, option, streamOptions = {}) {
  const originalQuestionMessage = resolveUserMessageForAssistant(sourceMessage);
  const resumeQuestion = clarifyPayload?.resumeQuestion || originalQuestionMessage?.messageContent || '';
  if (!resumeQuestion) {
    throw new Error(t('ai.chat.index.748794-328'));
  }
  const selectedLabel = option?.label || option?.value || t('ai.chat.index.748794-329');
  const targetType =
    clarifyPayload?.clarifyType === 'THING_MODEL'
      ? t('ai.chat.index.748794-330')
      : clarifyPayload?.clarifyType === 'DEVICE'
        ? t('ai.chat.index.748794-224')
        : clarifyPayload?.clarifyType === 'CONFIRM'
          ? t('ai.chat.index.748794-331')
          : t('ai.chat.index.748794-329');
  const displayMessage = t('ai.chat.index.748794-332') + targetType + '：' + selectedLabel;
  await consumeChatStream(
    buildChatRequestPayload(displayMessage, {
      chatMode: sourceMessage?.abilityType || composerMode.value,
      resumeToken: clarifyPayload?.resumeToken || '',
      clarifyKey: clarifyPayload?.clarifyKey || '',
      selectedValue: option?.value || '',
      selectedLabel,
      resumeQuestion,
    }),
    displayMessage,
    resumeChatMessageStream,
    streamOptions
  );
}

async function handleClarifyOptionSelect(sourceMessage, clarifyPayload, option) {
  if (!clarifyPayload || !option?.value) {
    appendLocalConversationNotice(t('ai.chat.index.748794-333'));
    return;
  }
  sending.value = true;
  const abortController = createChatAbortController();
  const releaseSendingState = () => releaseSendingForController(abortController);
  try {
    await submitClarifyResume(sourceMessage, clarifyPayload, option, {
      signal: abortController.signal,
      onVisualComplete: releaseSendingState,
    });
  } catch (error) {
    if (!isChatAbortError(error)) {
      appendLocalConversationNotice(resolveClientStreamErrorMessage(error, t('ai.chat.index.748794-334')));
    }
  } finally {
    releaseSendingState();
  }
}

async function submitQuestion(question, overrides = {}, streamOptions = {}) {
  if (chatStreamEnabled.value) {
    await submitQuestionStream(question, overrides, streamOptions);
    return;
  }
  await submitQuestionSync(question, overrides);
}

async function submitFileMessage(question, streamOptions = {}) {
  const uploadFile = selectedUploadFiles.value[0];
  if (!uploadFile?.raw) {
    throw new Error(t('ai.chat.index.748794-335'));
  }
  const displayQuestion = buildFileDisplayQuestion(question, uploadFile.name);
  const requestPayload = buildChatRequestPayload(displayQuestion);
  const formData = new FormData();
  Object.entries(requestPayload).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      formData.append(key, value);
    }
  });
  formData.append('file', uploadFile.raw);
  clearComposerUploadFiles();

  const fileStreamOptions = {
    ...streamOptions,
    recoveryAttempts: Number(streamOptions?.recoveryAttempts) || 120,
    recoveryDelay: Number(streamOptions?.recoveryDelay) || 1000,
    recoverySessionPageSize: Number(streamOptions?.recoverySessionPageSize) || 20,
    protocolTaskRecoveryAttempts: Number(streamOptions?.protocolTaskRecoveryAttempts) || 180,
    protocolTaskRecoveryDelay: Number(streamOptions?.protocolTaskRecoveryDelay) || 1000,
    protocolTaskLookupAttempts: Number(streamOptions?.protocolTaskLookupAttempts) || 20,
    protocolTaskLookupKeyword: streamOptions?.protocolTaskLookupKeyword || String(uploadFile.name || '').slice(0, 50),
    recoveringMessage: streamOptions?.recoveringMessage || t('ai.chat.index.748794-336'),
    fallbackErrorMessage: streamOptions?.fallbackErrorMessage || t('ai.chat.index.748794-337'),
  };
  await consumeChatStream(
    requestPayload,
    displayQuestion,
    (_requestData, options) => uploadChatFileStream(formData, options),
    fileStreamOptions
  );
  const latestProtocolAssistant = [...messageList.value]
    .reverse()
    .find(
      (item) =>
        item.roleType === 'assistant' &&
        (item.toolName === 'protocol_parse_chat' ||
          item.abilityType === 'PROTOCOL_PARSE' ||
          item.toolName === 'thing_model_generate' ||
          item.abilityType === 'THING_MODEL_GENERATE' ||
          item.toolName === 'requirement_evaluation' ||
          item.abilityType === 'REQUIREMENT_EVALUATION')
    );
  if (latestProtocolAssistant) {
    selectedResultMessageId.value = latestProtocolAssistant.messageId;
    rightPanelType.value = 'result';
    rightPanelVisible.value = true;
  }
  await maybeRefreshObservabilityStats();
}

function buildFileDisplayQuestion(question, fileName) {
  const safeFileName = fileName || t('ai.chat.index.748794-338');
  const content = String(question || '').trim();
  if (!content) {
    return t('ai.chat.index.748794-512', [safeFileName]);
  }
  return `${content}\n\n${t('ai.chat.index.748794-513', [safeFileName])}`;
}

function isAttachmentReferenceQuestion(question) {
  if (!question) {
    return false;
  }
  return /这个文件|该文件|附件|上传的文件|文档内容|这份文档|总结文件|解析文件|文件内容/.test(question);
}

async function handleSend() {
  if (sending.value) {
    return;
  }
  const question = draftMessage.value.trim();
  const hasUploadFile = selectedUploadFiles.value.length > 0;
  if (!hasUploadFile && isQuickPromptTrigger(question)) {
    refreshQuickPromptVisible();
    return;
  }
  closeQuickPrompt();
  if (!hasUploadFile && isAttachmentReferenceQuestion(question)) {
    appendLocalConversationNotice(t('ai.chat.index.748794-339'));
    return;
  }
  if (!question && !hasUploadFile) {
    appendLocalConversationNotice(t('ai.chat.index.748794-340'));
    return;
  }
  sending.value = true;
  draftMessage.value = '';
  const abortController = createChatAbortController();
  const releaseSendingState = () => releaseSendingForController(abortController);
  try {
    if (hasUploadFile) {
      await submitFileMessage(question, {
        signal: abortController.signal,
        onVisualComplete: releaseSendingState,
      });
    } else {
      await submitQuestion(
        question,
        {},
        {
          signal: abortController.signal,
          onVisualComplete: releaseSendingState,
        }
      );
    }
    clearComposerUploadFiles();
    clearOneTurnModeOverride();
  } catch (error) {
    if (!isChatAbortError(error)) {
      appendLocalConversationNotice(
        resolveClientStreamErrorMessage(
          error,
          hasUploadFile ? t('ai.chat.index.748794-341') : t('ai.chat.index.748794-342')
        )
      );
    }
  } finally {
    releaseSendingState();
  }
}

async function handleSessionMenuCommand(command, item) {
  if (command === 'archive') {
    await handleArchiveSession(item);
    return;
  }
  if (command === 'rename') {
    await handleRenameSession(item);
  }
}

async function handleRenameSession(item) {
  if (!item?.sessionId) {
    ElMessage.warning(t('ai.chat.index.748794-343'));
    return;
  }
  try {
    const { value } = await ElMessageBox.prompt(t('ai.chat.index.748794-504'), t('ai.chat.index.748794-344'), {
      confirmButtonText: t('ai.chat.index.748794-345'),
      cancelButtonText: t('ai.chat.index.748794-119'),
      inputValue: item.sessionTitle || '',
      inputPattern: /\S+/,
      inputErrorMessage: t('ai.chat.index.748794-346'),
    });
    const nextTitle = String(value || '').trim();
    if (!nextTitle) {
      ElMessage.warning(t('ai.chat.index.748794-346'));
      return;
    }
    await renameChatRecord(item.sessionId, nextTitle);
    item.sessionTitle = nextTitle;
    const target = sessionList.value.find((session) => session.sessionId === item.sessionId);
    if (target) {
      target.sessionTitle = nextTitle;
    }
    ElMessage.success(t('ai.chat.index.748794-347'));
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error?.message || t('ai.chat.index.748794-348'));
    }
  }
}

async function handleArchiveSession(item) {
  if (!item?.sessionId) {
    ElMessage.warning(t('ai.chat.index.748794-349'));
    return;
  }
  try {
    await ElMessageBox.confirm(
      t('ai.chat.index.748794-505', [item.sessionTitle || item.sessionCode || item.sessionId]),
      t('ai.chat.index.748794-350'),
      {
        confirmButtonText: t('ai.chat.index.748794-8'),
        cancelButtonText: t('ai.chat.index.748794-119'),
        type: 'warning',
      }
    );
    await archiveChatRecord(item.sessionId);
    sessionList.value = sessionList.value.filter((session) => session.sessionId !== item.sessionId);
    if (currentSessionId.value === item.sessionId) {
      const nextSession = sessionList.value[0];
      if (nextSession) {
        await handleSelectSession(nextSession);
      } else {
        currentSessionId.value = null;
        currentSessionCode.value = '';
        messageList.value = [];
        selectedResultMessageId.value = null;
        lastEffectiveMode.value = '';
        lastExecutedSkill.value = '';
      }
    }
    ElMessage.success(t('ai.chat.index.748794-351'));
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(t('ai.chat.index.748794-352'));
    }
  }
}

async function handleRetryLastQuestion() {
  if (!latestUserQuestion.value) {
    appendLocalConversationNotice(t('ai.chat.index.748794-353'));
    return;
  }
  sending.value = true;
  const abortController = createChatAbortController();
  const releaseSendingState = () => releaseSendingForController(abortController);
  try {
    await submitQuestion(
      latestUserQuestion.value,
      {},
      {
        signal: abortController.signal,
        onVisualComplete: releaseSendingState,
      }
    );
  } catch (error) {
    if (!isChatAbortError(error)) {
      appendLocalConversationNotice(resolveClientStreamErrorMessage(error, t('ai.chat.index.748794-354')));
    }
  } finally {
    releaseSendingState();
  }
}

async function handleRetryQuestionByMode(mode) {
  if (!latestUserQuestion.value) {
    appendLocalConversationNotice(t('ai.chat.index.748794-353'));
    return;
  }
  if (!mode) {
    appendLocalConversationNotice(t('ai.chat.index.748794-355'));
    return;
  }
  sending.value = true;
  const abortController = createChatAbortController();
  const releaseSendingState = () => releaseSendingForController(abortController);
  try {
    await submitQuestion(
      latestUserQuestion.value,
      {
        chatMode: mode,
        modeOverride: mode,
        interactionSource: 'MODE_CORRECTION_RETRY',
        retrySourceMessageId: currentResultSnapshot.value.userMessageId || null,
      },
      {
        signal: abortController.signal,
        onVisualComplete: releaseSendingState,
      }
    );
  } catch (error) {
    if (!isChatAbortError(error)) {
      appendLocalConversationNotice(resolveClientStreamErrorMessage(error, t('ai.chat.index.748794-356')));
    }
  } finally {
    releaseSendingState();
  }
}

onMounted(async () => {
  const restoredSnapshot = readPageStateSnapshot();
  applyPageStateSnapshot(restoredSnapshot);
  try {
    await loadMetaAndSessions();
    applyPageStateSnapshotAfterLoad(restoredSnapshot);
    if (rightPanelVisible.value && rightPanelType.value === 'observability' && !observabilityLoaded.value) {
      await loadObservabilityStats();
    }
    if (messageList.value.length > 0) {
      await scrollMessageListToBottomStable();
    }
    persistPageStateSnapshot();
  } catch (error) {
    ElMessage.warning(t('ai.chat.index.748794-357'));
  } finally {
    if (settingsStore.tagsView) {
      markCurrentRouteVisitedInRuntime();
    }
  }
});

onActivated(() => {
  if (settingsStore.tagsView && messageList.value.length > 0) {
    void scrollMessageListToBottomStable();
  }
});

onBeforeUnmount(() => {
  persistPageStateSnapshot();
  if (settingsStore.tagsView && !isCurrentRouteStillInTagsView()) {
    clearCurrentRouteVisitedInRuntime();
  }
  clearQuickPromptBlurTimer();
  stopThinkingTimer();
});
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 90px);
  min-height: 0;
  overflow: hidden;
}

.top-card,
.session-card,
.message-card,
.result-card {
  border-radius: 10px;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-select {
  width: 220px;
}

.mode-strategy-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 999px;
  background: var(--el-fill-color-blank);
  flex-wrap: wrap;
}

.mode-strategy-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.stream-switch-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 999px;
  background: var(--el-fill-color-blank);
}

.stream-switch-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.result-debug-switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 999px;
  background: var(--el-fill-color-blank);
}

.result-debug-switch-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.mode-strategy-dialog {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mode-strategy-dialog-tip {
  padding: 12px 14px;
  border-radius: 10px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.mode-strategy-dialog-select {
  width: 100%;
}

.mode-strategy-dialog-summary {
  display: grid;
  gap: 8px;
  padding: 12px 14px;
  border: 1px dashed var(--el-border-color);
  border-radius: 10px;
  color: var(--el-text-color-regular);
}

.observability-drawer {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.observability-drawer__intro {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.observability-drawer__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.observability-drawer__title-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.observability-drawer__title {
  font-size: 16px;
  font-weight: 600;
  color: #243042;
}

.observability-drawer__description {
  line-height: 1.6;
  color: var(--el-text-color-secondary);
}

.observability-drawer__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.observability-window-switch {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  background: var(--el-fill-color-light);
}

.observability-window-switch__label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.observability-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.observability-summary-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f7f9fc 0%, #ffffff 100%);
  border: 1px solid var(--el-border-color-lighter);
}

.observability-summary-card__label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.observability-summary-card__value {
  font-size: 24px;
  font-weight: 600;
  color: #243042;
}

.observability-summary-card__tip {
  line-height: 1.6;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.observability-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.observability-section__title {
  font-size: 14px;
  font-weight: 600;
  color: #243042;
}

.usage-guide-panel {
  display: flex;
  min-height: 0;
  overflow-y: auto;
  flex: 1;
  flex-direction: column;
  gap: 18px;
}

.usage-guide-hero {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: #f7f9fc;
}

.usage-guide-hero__title {
  font-size: 16px;
  font-weight: 650;
  color: #243042;
}

.usage-guide-hero__description {
  line-height: 1.7;
  color: var(--el-text-color-secondary);
}

.usage-guide-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.usage-guide-section__title {
  font-size: 14px;
  font-weight: 650;
  color: #243042;
}

.usage-guide-feature-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.usage-guide-feature {
  padding: 12px;
  border: 1px solid #e8edf4;
  border-radius: 10px;
  background: #ffffff;
}

.usage-guide-feature__title {
  font-size: 13px;
  font-weight: 650;
  color: #243042;
}

.usage-guide-feature__desc {
  margin-top: 6px;
  line-height: 1.6;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.usage-guide-note-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  list-style: none;
  margin: 0;
  padding-left: 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.usage-guide-note-item {
  display: grid;
  grid-template-columns: 68px minmax(0, 1fr);
  gap: 8px;
  align-items: start;
  padding-left: 0;
  line-height: 1.7;
}

.usage-guide-emphasis {
  color: #243042;
  font-weight: 650;
  white-space: nowrap;
}

.usage-guide-note-text {
  min-width: 0;
}

.usage-guide-shortcut-table {
  display: flex;
  overflow: hidden;
  flex-direction: column;
  border: 1px solid #e8edf4;
  border-radius: 10px;
  background: #ffffff;
}

.usage-guide-shortcut-row {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr);
  gap: 12px;
  align-items: flex-start;
  padding: 11px 12px;
  border-bottom: 1px solid #eef2f7;
}

.usage-guide-shortcut-row:last-child {
  border-bottom: 0;
}

.usage-guide-shortcut-keys {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-width: 0;
}

.keyboard-key {
  display: inline-flex;
  min-height: 22px;
  align-items: center;
  padding: 1px 7px;
  border: 1px solid #d5dce8;
  border-bottom-color: #bfc8d8;
  border-radius: 6px;
  background: #f8fafc;
  color: #243042;
  font-size: 12px;
  font-weight: 650;
  line-height: 18px;
  box-shadow: inset 0 -1px 0 rgba(148, 163, 184, 0.22);
}

.usage-guide-shortcut-content {
  min-width: 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.usage-guide-skill-section {
  margin-top: 0;
}

.observability-note-list {
  margin: 0;
  padding-left: 18px;
  color: var(--el-text-color-regular);
  line-height: 1.8;
}

.chat-layout {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) 360px;
  gap: 16px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.session-card,
.message-card,
.result-card {
  min-height: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-card {
  display: flex;
  flex-direction: column;
}

.session-card :deep(.el-card__body),
.message-card :deep(.el-card__body),
.result-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.card-title {
  font-weight: 600;
  color: #243042;
}

.message-header,
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.session-empty,
.welcome-panel {
  display: flex;
  flex: 1;
  min-height: 0;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  color: #5b6472;
  text-align: center;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.session-scroll,
.result-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 4px;
}

.session-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  padding: 12px;
  border-radius: 8px;
  background: #f7f9fc;
  cursor: pointer;
  border: 1px solid transparent;
}

.session-item.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.session-main {
  flex: 1;
  min-width: 0;
}

.session-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.session-archive-button {
  color: #7f8a99;
}

.session-archive-button:hover {
  color: #e6a23c;
  background: #fdf6ec;
}

.session-title {
  font-weight: 600;
  color: #243042;
}

.session-meta,
.session-time {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  color: #7f8a99;
  font-size: 12px;
}

.message-list {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 18px;
  min-height: 0;
  overflow-y: auto;
  padding-right: 4px;
}

.message-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-group-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-group-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  cursor: pointer;
}

.message-item.user {
  justify-content: flex-end;
}

.message-item.active .message-content {
  box-shadow: 0 0 0 1px #409eff inset;
}

.message-item.active .message-role {
  background: #dbeafe;
}

.message-role {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #eef3ff;
  color: #3155cc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 12px;
}

.message-bubble {
  max-width: calc(100% - 88px);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding-top: 28px;
}

.message-copy-button {
  color: #7f8a99;
}

.message-copy-button:hover {
  color: #3155cc;
  background: #eef3ff;
}

.message-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: #7f8a99;
  font-size: 12px;
}

.message-content {
  background: #f7f9fc;
  padding: 12px 14px;
  border-radius: 12px;
  line-height: 1.6;
  color: #243042;
  white-space: pre-wrap;
  word-break: break-word;
}

.clarify-box {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #fff8eb;
  border: 1px solid #f3dfb4;
}

.clarify-title {
  color: #8a5a00;
  font-weight: 600;
  line-height: 1.6;
}

.clarify-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.clarify-tip {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.clarify-tip-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.72);
}

.clarify-tip-label {
  color: #243042;
  font-weight: 600;
}

.clarify-tip-value {
  color: #6a7380;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item.user .message-content {
  background: #e8f3ff;
}

.composer {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.result-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-meta-detail {
  margin-top: 12px;
}

.result-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px;
  border-radius: 8px;
  background: #f7f9fc;
}

.result-label {
  color: #7f8a99;
  flex-shrink: 0;
}

.result-value {
  color: #243042;
  font-weight: 600;
  text-align: right;
}

.result-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.answer-section,
.result-section,
.skill-section,
.result-empty {
  margin-top: 18px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.section-summary-text {
  color: #7f8a99;
  font-size: 12px;
}

.section-title {
  font-weight: 600;
  color: #243042;
  margin-bottom: 12px;
}

.section-title.no-margin {
  margin-bottom: 0;
}

.section-subtitle {
  font-weight: 600;
  color: #5b6472;
  margin-bottom: 10px;
}

.section-subtitle.no-margin {
  margin-bottom: 0;
}

.answer-content,
.summary-box {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fc;
  color: #243042;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.answer-content.is-markdown {
  white-space: normal;
}

.summary-box.is-markdown {
  white-space: normal;
}

.answer-content.is-markdown :deep(p),
.summary-box.is-markdown :deep(p) {
  margin: 0 0 8px;
}

.answer-content.is-markdown :deep(p:last-child),
.summary-box.is-markdown :deep(p:last-child) {
  margin-bottom: 0;
}

.answer-content.is-markdown :deep(a) {
  color: #2563eb;
  text-decoration: none;
}

.answer-content.is-markdown :deep(a:hover) {
  text-decoration: underline;
}

.route-debug-fallback {
  margin-top: 12px;
  background: #fff8eb;
  color: #8a5a00;
}

.route-debug-error {
  margin-top: 12px;
  background: #fff2f0;
  color: #c45656;
}

.answer-content.collapsed {
  max-height: 160px;
  overflow: hidden;
  position: relative;
}

.answer-content.collapsed::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 42px;
  background: linear-gradient(180deg, rgba(247, 249, 252, 0) 0%, #f7f9fc 100%);
}

.sql-section,
.table-section {
  margin-top: 14px;
}

.code-block {
  margin: 0;
  padding: 14px;
  border-radius: 10px;
  background: #17212b;
  color: #eaf2ff;
  font-size: 12px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-x: auto;
}

.json-block {
  max-height: 320px;
}

.stats-row {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.nl2sql-highlight-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-top: 12px;
}

.result-highlight-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-top: 12px;
}

.nl2sql-highlight-card,
.result-highlight-card {
  position: relative;
  overflow: hidden;
  padding: 12px 14px;
  border: 1px solid #dbe3ef;
  border-radius: 10px;
  background: #f7f9fc;
}

.nl2sql-highlight-card::before,
.result-highlight-card::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 4px;
  background: #1e3a8a;
}

.nl2sql-highlight-card.is-success,
.nl2sql-highlight-card.is-warning,
.nl2sql-highlight-card.is-danger,
.nl2sql-highlight-card.is-info {
  border-color: #d8e0ea;
  background: #f7f9fc;
}

.nl2sql-highlight-card.is-success::before,
.nl2sql-highlight-card.is-warning::before,
.nl2sql-highlight-card.is-danger::before,
.nl2sql-highlight-card.is-info::before {
  background: #1e3a8a;
}

.nl2sql-highlight-label,
.result-highlight-label {
  color: #667085;
  font-size: 12px;
  line-height: 18px;
}

.nl2sql-highlight-value,
.result-highlight-value {
  margin-top: 4px;
  color: #111827;
  font-size: 22px;
  font-weight: 700;
  line-height: 30px;
  overflow-wrap: anywhere;
}

.nl2sql-highlight-unit,
.result-highlight-unit {
  margin-left: 3px;
  color: #667085;
  font-size: 13px;
  font-weight: 500;
}

.nl2sql-highlight-tip,
.result-highlight-tip {
  margin-top: 4px;
  color: #7b8494;
  font-size: 12px;
  line-height: 18px;
  overflow-wrap: anywhere;
}

.nl2sql-key-cell {
  display: inline-flex;
  max-width: 100%;
  align-items: center;
  padding: 2px 7px;
  border-radius: 999px;
  background: #eef4ff;
  color: #1e3a8a;
  font-weight: 600;
  line-height: 20px;
}

.nl2sql-key-cell.is-success,
.nl2sql-key-cell.is-warning,
.nl2sql-key-cell.is-danger,
.nl2sql-key-cell.is-info {
  background: #eef4ff;
  color: #1e3a8a;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  padding: 12px 14px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: #f7f9fc;
  color: #243042;
  line-height: 1.6;
  word-break: break-word;
}

.info-item.is-key,
.result-summary-emphasis {
  border-color: #dbe3ef;
  background: #f7f9fc;
  color: #111827;
  font-weight: 650;
}

.skill-empty {
  color: #7f8a99;
  font-size: 13px;
}

.skill-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-item {
  padding: 12px;
  border-radius: 10px;
  background: #f7f9fc;
  border: 1px solid #e8edf4;
}

.skill-item.disabled {
  opacity: 0.72;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.skill-badges {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

.skill-name {
  font-weight: 600;
  color: #243042;
}

.skill-mode {
  margin-top: 8px;
  color: #3155cc;
  font-size: 12px;
}

.skill-desc {
  margin-top: 8px;
  color: #5b6472;
  font-size: 13px;
  line-height: 1.6;
}

.skill-case-list {
  display: flex;
  margin-top: 10px;
  flex-direction: column;
  gap: 6px;
}

.skill-case-title {
  color: #7b8494;
  font-size: 12px;
  font-weight: 600;
}

.skill-case-item {
  display: flex;
  width: 100%;
  gap: 8px;
  align-items: flex-start;
  padding: 7px 8px;
  border: 1px solid #e6ebf2;
  border-radius: 8px;
  background: #ffffff;
  color: #334155;
  font-size: 12px;
  line-height: 1.5;
  text-align: left;
  cursor: pointer;
  transition:
    border-color 0.16s ease,
    background 0.16s ease,
    color 0.16s ease;
}

.skill-case-item:hover {
  border-color: #bfdbfe;
  background: #eff6ff;
  color: #2563eb;
}

.skill-case-code {
  flex-shrink: 0;
  color: #64748b;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace;
}

.skill-case-question {
  min-width: 0;
}

.ai-chat-page {
  gap: 0;
  height: calc(100vh - 84px);
  background: #ffffff;
}

.ai-chat-page .chat-layout {
  grid-template-columns: 248px minmax(0, 1fr);
  gap: 0;
  background: #ffffff;
}

.ai-chat-page.has-right-panel .chat-layout {
  grid-template-columns: 248px minmax(0, 1fr) 420px;
}

.ai-chat-page.is-sidebar-collapsed .chat-layout {
  grid-template-columns: 72px minmax(0, 1fr);
}

.ai-chat-page.is-sidebar-collapsed.has-right-panel .chat-layout {
  grid-template-columns: 72px minmax(0, 1fr) 420px;
}

.ai-chat-page .session-card,
.ai-chat-page .message-card,
.ai-chat-page .result-card {
  border: 0;
  border-radius: 0;
  box-shadow: none;
}

.ai-chat-page .session-card {
  background: #f7f8fb;
  border-right: 1px solid #edf0f5;
}

.ai-chat-page .message-card {
  background: #ffffff;
}

.ai-chat-page .result-card {
  border-left: 1px solid #edf0f5;
  font-size: 15px;
  line-height: 1.7;
}

.ai-chat-page .session-card :deep(.el-card__header),
.ai-chat-page .message-card :deep(.el-card__header),
.ai-chat-page .result-card :deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom: 0;
  background: transparent;
}

.ai-chat-page:not(.is-sidebar-collapsed) .session-card :deep(.el-card__header) {
  padding-right: 12px;
}

.ai-chat-page .session-card :deep(.el-card__body) {
  padding: 0 12px 14px;
}

.ai-chat-page .message-card :deep(.el-card__body) {
  padding: 0;
}

.ai-chat-page .result-card :deep(.el-card__body) {
  padding: 0 16px 16px;
}

.session-shell-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-brand {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 8px;
}

.session-brand-logo,
.welcome-logo {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  margin-left: -4px;
}

.session-brand-name {
  min-width: 6em;
  max-width: 6em;
  flex: 0 0 6em;
  margin-left: 4px;
  overflow: hidden;
  color: #1f2937;
  font-size: 18px;
  font-weight: 600;
  line-height: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: -1px;
}

.session-header-actions {
  display: inline-flex;
  align-items: center;
  gap: 0;
  margin-left: 10px;
  flex-shrink: 0;
  margin-right: -5px;
}

.session-header-actions :deep(.el-button) {
  width: 24px;
  height: 24px;
}

.session-header-actions :deep(.el-button + .el-button) {
  margin-left: -1px;
}

.session-new-row {
  padding: 6px 0 10px;
}

.session-new-button {
  width: 100%;
  border-color: #e3e8f1;
  background: #ffffff;
  color: #1f2937;
  font-weight: 600;
}

.session-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ai-chat-page .session-list {
  gap: 0;
}

.session-section + .session-section {
  margin-top: 12px;
}

.session-section-title {
  padding: 0 4px;
  color: #9aa4b2;
  font-size: 12px;
}

.ai-chat-page .session-item {
  position: relative;
  align-items: center;
  min-height: 36px;
  padding: 7px 8px;
  border: 0;
  border-radius: 8px;
  background: transparent;
}

.ai-chat-page .session-item:hover,
.ai-chat-page .session-item.active {
  background: #eaf1ff;
}

.ai-chat-page .session-title {
  overflow: hidden;
  font-size: 14px;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ai-chat-page .session-meta {
  margin-top: 3px;
  justify-content: flex-start;
  color: #6b7280;
}

.session-time {
  display: none;
}

.session-more-dropdown {
  opacity: 0;
  transition: opacity 0.16s ease;
}

.session-item:hover .session-more-dropdown,
.session-item.active .session-more-dropdown {
  opacity: 1;
}

.session-more-button {
  color: #8b95a5;
}

.ai-chat-page.is-sidebar-collapsed .session-card :deep(.el-card__body) {
  padding: 0;
}

.ai-chat-page.is-sidebar-collapsed .session-card :deep(.el-card__header) {
  padding: 10px 6px;
}

.ai-chat-page.is-sidebar-collapsed .session-shell-header {
  justify-content: center;
  flex-direction: column;
  gap: 2px;
}

.ai-chat-page.is-sidebar-collapsed .session-brand {
  justify-content: center;
}

.ai-chat-page.is-sidebar-collapsed .session-header-actions {
  gap: 2px;
}

.welcome-panel {
  min-height: 100%;
  padding: 24px;
}

.message-card.is-empty-chat .welcome-panel {
  position: absolute;
  top: calc(50% - 145px);
  right: 0;
  left: 0;
  z-index: 2;
  flex: none;
  min-height: auto;
  height: auto;
  padding: 0 24px;
  pointer-events: none;
}

.welcome-center {
  display: flex;
  align-items: center;
  width: min(640px, 92%);
  margin-top: 8vh;
  flex-direction: column;
  gap: 18px;
}

.message-card.is-empty-chat .welcome-center {
  margin-top: 0;
  gap: 12px;
  pointer-events: auto;
}

.welcome-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-title-row h3 {
  margin: 0;
  color: #111827;
  font-size: 22px;
  font-weight: 700;
}

.welcome-mode-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-center p {
  max-width: 540px;
  margin: 0;
  color: #6b7280;
  line-height: 1.7;
}

.message-header {
  min-height: 28px;
}

.conversation-title-block {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 4px;
}

.conversation-title {
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

.conversation-subtitle {
  color: #2563eb;
  font-size: 12px;
}

.ai-chat-page .message-list {
  max-width: 880px;
  width: 100%;
  margin: 0 auto;
  padding: 16px 24px var(--ai-chat-composer-space, 24px);
}

.ai-chat-page .message-item {
  gap: 8px;
  cursor: default;
}

.ai-chat-page .message-role {
  display: none;
}

.ai-chat-page .message-bubble {
  max-width: min(760px, 78%);
  gap: 4px;
}

.ai-chat-page .message-item.user .message-bubble {
  align-items: flex-end;
}

.ai-chat-page .message-meta {
  align-items: center;
  gap: 6px;
  color: #7b8494;
}

.ai-chat-page .message-item.user .message-meta {
  justify-content: flex-end;
}

.ai-chat-page .message-content {
  border-radius: 18px;
  background: transparent;
  box-shadow: none;
  font-size: 15px;
  line-height: 1.75;
}

.ai-chat-page .message-item.assistant .message-content {
  padding: 0;
  background: transparent;
}

.ai-chat-page .message-item.assistant.active .message-content {
  box-shadow: none;
}

.ai-chat-page .message-item.assistant .message-content.is-thinking {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
}

.ai-chat-page .message-item.assistant .message-content.is-markdown {
  white-space: normal;
}

.ai-chat-page .message-content.is-markdown :deep(p) {
  margin: 0 0 12px;
}

.ai-chat-page .message-content.is-markdown :deep(p:last-child) {
  margin-bottom: 0;
}

.ai-chat-page .message-content.is-markdown :deep(h1),
.ai-chat-page .message-content.is-markdown :deep(h2),
.ai-chat-page .message-content.is-markdown :deep(h3),
.ai-chat-page .message-content.is-markdown :deep(h4) {
  margin: 18px 0 10px;
  color: #111827;
  font-weight: 700;
  line-height: 1.45;
}

.ai-chat-page .message-content.is-markdown :deep(h1:first-child),
.ai-chat-page .message-content.is-markdown :deep(h2:first-child),
.ai-chat-page .message-content.is-markdown :deep(h3:first-child),
.ai-chat-page .message-content.is-markdown :deep(h4:first-child) {
  margin-top: 0;
}

.ai-chat-page .message-content.is-markdown :deep(h1) {
  font-size: 22px;
}

.ai-chat-page .message-content.is-markdown :deep(h2) {
  font-size: 20px;
}

.ai-chat-page .message-content.is-markdown :deep(h3) {
  font-size: 18px;
}

.ai-chat-page .message-content.is-markdown :deep(h4) {
  font-size: 16px;
}

.ai-chat-page .message-content.is-markdown :deep(ul),
.ai-chat-page .message-content.is-markdown :deep(ol) {
  margin: 0 0 12px;
  padding-left: 22px;
}

.ai-chat-page .message-content.is-markdown :deep(li) {
  margin: 4px 0;
}

.ai-chat-page .message-content.is-markdown :deep(blockquote) {
  margin: 12px 0;
  padding: 8px 12px;
  border-left: 3px solid #c7d2fe;
  border-radius: 8px;
  background: #f8fafc;
  color: #4b5563;
}

.ai-chat-page .message-content.is-markdown :deep(pre) {
  overflow-x: auto;
  margin: 12px 0;
  padding: 12px;
  border-radius: 10px;
  background: #111827;
  color: #f9fafb;
  line-height: 1.6;
}

.ai-chat-page .message-content.is-markdown :deep(code) {
  padding: 2px 5px;
  border-radius: 5px;
  background: #eef2ff;
  color: #334155;
  font-family: Consolas, Monaco, 'Courier New', monospace;
  font-size: 0.92em;
}

.ai-chat-page .message-content.is-markdown :deep(pre code) {
  padding: 0;
  background: transparent;
  color: inherit;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight) {
  display: inline;
  margin: 0 1px;
  padding: 1px 4px;
  border: 1px solid #dbe3ef;
  border-radius: 5px;
  background: #f3f6fb;
  box-decoration-break: clone;
  -webkit-box-decoration-break: clone;
  color: #111827;
  font-weight: 650;
  line-height: 1.65;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--path),
.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--package),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--path),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--package) {
  border-color: #cbd5e1;
  background: #f8fafc;
  color: #1f2937;
  font-family: Consolas, Monaco, 'Courier New', monospace;
  font-size: 0.93em;
  overflow-wrap: anywhere;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--module),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--module) {
  border-color: #d7e3ff;
  background: #eef4ff;
  color: #1e3a8a;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--endpoint),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--endpoint) {
  border-color: #d7e3ff;
  background: #eef4ff;
  color: #1e3a8a;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--layer),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--layer) {
  border-color: transparent;
  background: transparent;
  color: #111827;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--class),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--class) {
  border-color: #d7e3ff;
  background: #eef4ff;
  color: #1e3a8a;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(.codebase-highlight--method),
.ai-chat-page .answer-content.is-markdown :deep(.codebase-highlight--method) {
  border-color: #d7e3ff;
  background: #eef4ff;
  color: #1e3a8a;
}

.ai-chat-page .message-content.is-markdown :deep(.ai-result-highlight),
.ai-chat-page .answer-content.is-markdown :deep(.ai-result-highlight),
.summary-box.is-markdown :deep(.ai-result-highlight) {
  display: inline;
  box-decoration-break: clone;
  -webkit-box-decoration-break: clone;
}

.ai-chat-page .message-content.is-markdown :deep(.ai-result-highlight--label),
.ai-chat-page .answer-content.is-markdown :deep(.ai-result-highlight--label),
.summary-box.is-markdown :deep(.ai-result-highlight--label) {
  color: #111827;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(.ai-result-highlight--value),
.ai-chat-page .message-content.is-markdown :deep(.ai-result-highlight--status),
.ai-chat-page .answer-content.is-markdown :deep(.ai-result-highlight--value),
.ai-chat-page .answer-content.is-markdown :deep(.ai-result-highlight--status),
.summary-box.is-markdown :deep(.ai-result-highlight--value),
.summary-box.is-markdown :deep(.ai-result-highlight--status) {
  margin: 0 1px;
  padding: 1px 4px;
  border: 1px solid #d7e3ff;
  border-radius: 5px;
  background: #eef4ff;
  color: #1e3a8a;
  font-weight: 700;
  line-height: 1.65;
}

.ai-chat-page .requirement-match-highlight,
.ai-chat-page .message-content.is-markdown :deep(.requirement-match-highlight),
.ai-chat-page .answer-content.is-markdown :deep(.requirement-match-highlight),
.summary-box.is-markdown :deep(.requirement-match-highlight) {
  display: inline-block;
  padding: 1px 6px;
  border: 1px solid #d7e3ff;
  border-radius: 5px;
  background: #f5f8ff;
  color: #1e3a8a;
  font-weight: 700;
  line-height: 1.6;
}

.ai-chat-page .requirement-match-highlight--develop,
.ai-chat-page .requirement-match-highlight--unknown,
.ai-chat-page .requirement-match-highlight--default,
.ai-chat-page .message-content.is-markdown :deep(.requirement-match-highlight--develop),
.ai-chat-page .message-content.is-markdown :deep(.requirement-match-highlight--unknown),
.ai-chat-page .message-content.is-markdown :deep(.requirement-match-highlight--default),
.ai-chat-page .answer-content.is-markdown :deep(.requirement-match-highlight--develop),
.ai-chat-page .answer-content.is-markdown :deep(.requirement-match-highlight--unknown),
.ai-chat-page .answer-content.is-markdown :deep(.requirement-match-highlight--default),
.summary-box.is-markdown :deep(.requirement-match-highlight--develop),
.summary-box.is-markdown :deep(.requirement-match-highlight--unknown),
.summary-box.is-markdown :deep(.requirement-match-highlight--default) {
  border-color: #d1d5db;
  background: #f8fafc;
  color: #111827;
}

.ai-chat-page .message-content.is-markdown :deep(.requirement-disclaimer-highlight),
.ai-chat-page .answer-content.is-markdown :deep(.requirement-disclaimer-highlight),
.summary-box.is-markdown :deep(.requirement-disclaimer-highlight) {
  color: #111827;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(a) {
  color: #2563eb;
  text-decoration: none;
}

.ai-chat-page .message-content.is-markdown :deep(a:hover) {
  text-decoration: underline;
}

.ai-chat-page .message-content.is-markdown :deep(a[href^='fastbee://thing-model-workbook']),
.ai-chat-page .message-content.is-markdown :deep(a[href^='fastbee://requirement-evaluation-report']),
.ai-chat-page .answer-content.is-markdown :deep(a[href^='fastbee://thing-model-workbook']),
.ai-chat-page .answer-content.is-markdown :deep(a[href^='fastbee://requirement-evaluation-report']),
.summary-box.is-markdown :deep(a[href^='fastbee://thing-model-workbook']),
.summary-box.is-markdown :deep(a[href^='fastbee://requirement-evaluation-report']) {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 2px 8px;
  border: 1px solid #c7d7fe;
  border-radius: 6px;
  background: #f5f8ff;
  color: #1d4ed8;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(table),
.ai-chat-page .answer-content.is-markdown :deep(table) {
  width: 100%;
  margin: 12px 0;
  border-collapse: collapse;
  font-size: 14px;
}

.ai-chat-page .message-content.is-markdown :deep(th),
.ai-chat-page .message-content.is-markdown :deep(td),
.ai-chat-page .answer-content.is-markdown :deep(th),
.ai-chat-page .answer-content.is-markdown :deep(td) {
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  line-height: 1.65;
  text-align: left;
  vertical-align: top;
}

.ai-chat-page .message-content.is-markdown :deep(th),
.ai-chat-page .answer-content.is-markdown :deep(th) {
  background: #f8fafc;
  font-weight: 700;
}

.ai-chat-page .message-content.is-markdown :deep(hr) {
  margin: 16px 0;
  border: 0;
  border-top: 1px solid #e5e7eb;
}

.message-thinking-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: #4168f6;
  animation: ai-thinking-pulse 1.2s ease-in-out infinite;
}

@keyframes ai-thinking-pulse {
  0%,
  100% {
    opacity: 0.35;
    transform: scale(0.86);
  }

  50% {
    opacity: 1;
    transform: scale(1.12);
  }
}

.ai-chat-page .message-item.user .message-content {
  padding: 12px 16px;
  background: #eef4ff;
}

.ai-chat-page .message-actions {
  display: flex;
  gap: 2px;
  margin-top: 2px;
  padding-top: 0;
}

.ai-chat-page .message-item.user .message-actions {
  justify-content: flex-end;
}

.ai-chat-page .message-item.assistant .message-actions {
  justify-content: flex-start;
}

.message-result-button,
.message-copy-button {
  color: #6b7280;
}

.ai-chat-page .message-actions :deep(.el-button) {
  width: 28px;
  height: 28px;
  padding: 5px;
}

.ai-chat-page .message-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

.message-result-button:hover,
.message-copy-button:hover {
  color: #2563eb;
  background: #eef4ff;
}

.scroll-bottom-button {
  position: absolute;
  left: 50%;
  bottom: 150px;
  z-index: 4;
  width: 40px;
  height: 40px;
  border: 1px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 8px 22px rgba(31, 41, 55, 0.12);
  transform: translateX(-50%);
}

.scroll-bottom-button:hover,
.scroll-bottom-button:focus {
  border-color: #c7d2fe;
  color: #2563eb;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(37, 99, 235, 0.16);
}

.scroll-bottom-button :deep(.el-icon) {
  font-size: 19px;
}

.ai-chat-page .composer {
  z-index: 3;
  width: min(880px, calc(100% - 48px));
  margin: 0 auto 24px;
  flex-shrink: 0;
}

.message-card:not(.is-empty-chat) {
  --ai-chat-composer-space: 24px;
}

.message-card.is-empty-chat .composer {
  position: absolute;
  right: 50%;
  top: calc(50% + 55px);
  bottom: auto;
  width: min(720px, calc(100% - 96px));
  margin: 0;
  transform: translate(50%, -50%);
}

.message-card {
  position: relative;
}

.composer-card {
  position: relative;
  display: flex;
  min-height: 96px;
  flex-direction: column;
  gap: 6px;
  padding: 10px 12px;
  border: 1px solid #e7ebf2;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 20px 60px rgba(31, 41, 55, 0.08);
}

.composer-input-wrap {
  position: relative;
}

.composer-card :deep(.el-textarea__inner) {
  min-height: 50px !important;
  padding: 0 0 0 5px;
  border: 0;
  box-shadow: none;
  color: #1f2937;
  font-size: 15px;
  line-height: 1.7;
}

.composer-quick-prompt {
  position: absolute;
  right: 0;
  bottom: calc(100% + 12px);
  left: 0;
  z-index: 12;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #dbe3ef;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0 18px 48px rgba(31, 41, 55, 0.16);
}

.composer-quick-prompt__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-bottom: 1px solid #eef2f7;
  color: #6b7280;
  font-size: 12px;
  line-height: 18px;
}

.composer-quick-prompt__header span:first-child {
  color: #1f2937;
  font-size: 13px;
  font-weight: 600;
}

.composer-quick-prompt__item {
  display: flex;
  width: 100%;
  min-height: 42px;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 9px 12px;
  border: 0;
  border-bottom: 1px solid #f1f5f9;
  background: transparent;
  color: #1f2937;
  font: inherit;
  line-height: 20px;
  text-align: left;
  cursor: pointer;
}

.composer-quick-prompt__item:last-child {
  border-bottom: 0;
}

.composer-quick-prompt__item:hover,
.composer-quick-prompt__item.active {
  background: #f5f8ff;
}

.composer-quick-prompt__text {
  min-width: 0;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.composer-quick-prompt__tag {
  flex: 0 0 auto;
  padding: 2px 8px;
  border-radius: 999px;
  background: #eef4ff;
  color: #3157ea;
  font-size: 12px;
  line-height: 18px;
  white-space: nowrap;
}

.composer-bottom,
.composer-left-actions,
.composer-right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.composer-bottom {
  justify-content: space-between;
  min-height: 30px;
}

.composer-right-actions {
  gap: 12px;
}

.composer-right-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

.composer-file-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.composer-model-select {
  width: 126px;
  max-width: min(240px, 45vw);
  flex-shrink: 0;
}

.composer-model-select :deep(.el-select__wrapper) {
  min-height: 30px;
  gap: 2px;
  justify-content: flex-end;
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none !important;
}

.composer-model-select :deep(.el-select__wrapper.is-focused),
.composer-model-select :deep(.el-select__wrapper:hover) {
  box-shadow: none !important;
}

.composer-model-select :deep(.el-input__wrapper) {
  padding: 0;
  background: transparent;
  border-radius: 999px;
  box-shadow: none;
}

.composer-model-select :deep(.el-input__wrapper.is-focus),
.composer-model-select :deep(.el-input__wrapper:hover) {
  box-shadow: none;
}

.composer-model-select :deep(.el-input__inner) {
  text-align: right;
  caret-color: transparent;
}

.composer-model-select :deep(.el-select__input) {
  caret-color: transparent;
}

.composer-model-select :deep(.el-select__selection) {
  flex: 1 1 auto;
  min-width: 0;
  justify-content: flex-end;
}

.composer-model-select :deep(.el-select__selected-item),
.composer-model-select :deep(.el-select__placeholder) {
  flex: 1 1 auto;
  min-width: 0;
  overflow: hidden;
  color: #606266;
  font-size: 14px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.composer-model-select :deep(.el-select__suffix) {
  flex: 0 0 auto;
  width: 12px;
  margin-left: 0;
}

.composer-model-select :deep(.el-select__caret) {
  width: 12px;
  color: #9097a6;
}

.composer-upload-button,
.composer-send-button {
  width: 30px;
  height: 30px;
  margin-left: -4px;
}

.composer-upload-button {
  color: #5b6472;
}

.composer-upload-button:hover {
  color: #2563eb;
  background: #eef4ff;
}

.composer-send-button {
  width: 36px;
  height: 36px;
  border: 0;
  background: #4168f6;
  box-shadow: 0 8px 18px rgba(65, 104, 246, 0.24);
}

.composer-send-button:hover,
.composer-send-button:focus {
  background: #3157ea;
}

.composer-send-button :deep(.el-icon) {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
}

.composer-stop-icon {
  display: inline-block;
  width: 11px;
  height: 11px;
  border-radius: 2px;
  background: currentColor;
}

.result-card .result-header {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
}

.result-card .result-header-row {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 8px;
}

.result-card .result-header-main .card-title {
  min-width: 64px;
  line-height: 28px;
  white-space: nowrap;
}

.result-card .result-header-main > .el-button:last-child {
  margin-left: auto;
  flex-shrink: 0;
}

.result-card .result-header-secondary {
  flex-wrap: wrap;
  gap: 6px;
}

.result-card .result-header-row :deep(.el-button) {
  height: 28px;
  padding: 6px 10px;
  font-size: 12px;
}

.result-card .result-header-row :deep(.el-button + .el-button) {
  margin-left: 0;
}

.result-card .result-header-row :deep(.el-button.is-circle) {
  width: 28px;
  padding: 6px;
}

.result-card .result-debug-switch {
  height: 28px;
  align-items: center;
}

.ai-chat-page .result-card .section-title,
.ai-chat-page .result-card .observability-section__title {
  font-size: 15px;
  line-height: 1.65;
}

.ai-chat-page .result-card .section-subtitle {
  font-size: 14px;
  line-height: 1.65;
}

.ai-chat-page .result-card .answer-content,
.ai-chat-page .result-card .summary-box,
.ai-chat-page .result-card .info-item,
.ai-chat-page .result-card .result-item,
.ai-chat-page .result-card .result-label,
.ai-chat-page .result-card .result-value,
.ai-chat-page .result-card .observability-note-list {
  font-size: 15px;
  line-height: 1.7;
}

.ai-chat-page .result-card .observability-window-switch__label,
.ai-chat-page .result-card .observability-summary-card__label,
.ai-chat-page .result-card .observability-summary-card__tip {
  font-size: 14px;
  line-height: 1.65;
}

.ai-chat-page .result-card :deep(.el-descriptions__label),
.ai-chat-page .result-card :deep(.el-descriptions__content) {
  font-size: 15px;
  line-height: 1.7;
}

.ai-chat-page .result-card .code-block {
  font-size: 13px;
  line-height: 1.7;
}

.observability-panel {
  display: flex;
  min-height: 0;
  overflow-y: auto;
  flex: 1;
  flex-direction: column;
  gap: 16px;
}

@media (max-width: 1280px) {
  .ai-chat-page {
    height: auto;
    overflow: visible;
  }

  .observability-summary-grid {
    grid-template-columns: 1fr;
  }

  .chat-layout {
    grid-template-columns: 1fr;
    overflow: visible;
  }

  .session-card,
  .message-card,
  .result-card {
    min-height: auto;
    height: auto;
  }

  .message-list {
    max-height: none;
  }

  .session-scroll,
  .result-scroll {
    overflow: visible;
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

</style>
