<template>
  <n-collapse-item :title="t('visualBigScreen.management-674210-716')" name="3">
    <template #header-extra>
      <n-button type="primary" tertiary size="small" @click.stop="showModal = true">
        <template #icon>
          <n-icon>
            <pencil-icon />
          </n-icon>
        </template>
        {{ t('visualBigScreen.management-674210-4') }}
      </n-button>
    </template>
    <n-card class="collapse-show-box">
      <!-- 函数体 -->
      <div v-for="eventName in EventLife" :key="eventName">
        <p>
          <span class="func-annotate">// {{ EventLifeName[eventName] }}</span>
          <br />
          <span class="func-keyword">async {{ eventName }}</span> (e, components, echarts, node_modules) {
        </p>
        <p class="go-ml-4">
          <n-code :code="(targetData.events.advancedEvents || {})[eventName] || ''" language="typescript"></n-code>
        </p>
        <p>}<span>,</span></p>
      </div>
    </n-card>
  </n-collapse-item>

  <!-- 弹窗 -->
  <n-modal class="go-chart-data-monaco-editor" v-model:show="showModal" :mask-closable="false">
    <n-card :bordered="false" role="dialog" size="small" aria-modal="true" style="width: 1200px; height: 700px">
      <template #header>
        <n-space>
          <n-text>{{ t('visualBigScreen.management-674210-717') }}</n-text>
        </n-space>
      </template>

      <template #header-extra> </template>

      <n-layout has-sider sider-placement="right">
        <n-layout style="height: 580px; padding-right: 20px">
          <n-tabs v-model:value="editTab" type="card" tab-style="min-width: 100px;">
            <!-- 提示 -->
            <template #suffix>
              <n-text class="tab-tip" type="warning"
                >{{ t('visualBigScreen.management-674210-718') }}: {{ EventLifeTip[editTab] }}</n-text
              >
            </template>
            <n-tab-pane
              v-for="(eventName, index) in EventLife"
              :key="index"
              :tab="`${EventLifeName[eventName]}-${eventName}`"
              :name="eventName"
            >
              <!-- 函数名称 -->
              <p class="go-pl-3">
                <span class="func-keyword">async function &nbsp;&nbsp;</span>
                <span class="func-keyNameWord">{{ eventName }}(e, components, echarts, node_modules)&nbsp;&nbsp;{</span>
              </p>
              <!-- 编辑主体 -->
              <monaco-editor v-model:modelValue="advancedEvents[eventName]" height="480px" language="javascript" />
              <!-- 函数结束 -->
              <p class="go-pl-3 func-keyNameWord">}</p>
            </n-tab-pane>
          </n-tabs>
        </n-layout>
        <n-layout-sider
          :collapsed-width="14"
          :width="340"
          show-trigger="bar"
          collapse-mode="transform"
          content-style="padding: 12px 12px 0px 12px;margin-left: 3px;"
        >
          <n-tabs default-value="1" justify-content="space-evenly" type="segment">
            <!-- 验证结果 -->
            <n-tab-pane :tab="t('visualBigScreen.management-674210-667')" name="1" size="small">
              <n-scrollbar trigger="none" style="max-height: 505px">
                <n-collapse class="go-px-3" arrow-placement="right" :default-expanded-names="[1, 2, 3]">
                  <template v-for="error in [validEvents()]" :key="error">
                    <n-collapse-item :title="t('visualBigScreen.management-674210-668')" :name="1">
                      <n-text depth="3">{{ error.errorFn || t('visualBigScreen.management-674210-692') }}</n-text>
                    </n-collapse-item>
                    <n-collapse-item :title="t('visualBigScreen.management-674210-669')" :name="2">
                      <n-text depth="3">{{ error.name || t('visualBigScreen.management-674210-692') }}</n-text>
                    </n-collapse-item>
                    <n-collapse-item :title="t('visualBigScreen.management-674210-670')" :name="3">
                      <n-text depth="3">{{ error.message || t('visualBigScreen.management-674210-692') }}</n-text>
                    </n-collapse-item>
                  </template>
                </n-collapse>
              </n-scrollbar>
            </n-tab-pane>
            <!-- 辅助说明 -->
            <n-tab-pane :tab="t('visualBigScreen.management-674210-671')" name="2">
              <n-scrollbar trigger="none" style="max-height: 505px">
                <n-collapse class="go-px-3" arrow-placement="right" :default-expanded-names="[1, 2, 3, 4]">
                  <n-collapse-item title="e" :name="1">
                    <n-text depth="3">{{ t('visualBigScreen.management-674210-723') }}</n-text>
                  </n-collapse-item>
                  <n-collapse-item title="this" :name="2">
                    <n-text depth="3">{{ t('visualBigScreen.management-674210-724') }}</n-text>
                    <br />
                    <n-tag class="go-m-1" v-for="prop in ['refs', 'setupState', 'ctx', 'props', '...']" :key="prop">{{
                      prop
                    }}</n-tag>
                  </n-collapse-item>
                  <n-collapse-item title="components" :name="3">
                    <n-text depth="3">{{ t('visualBigScreen.management-674210-725') }}</n-text>
                    <n-code :code="`{\n  [id]: component\n}`" language="typescript"></n-code>
                  </n-collapse-item>
                  <n-collapse-item title="node_modules" :name="4">
                    <n-text depth="3">{{ t('visualBigScreen.management-674210-726') }}</n-text>
                    <br />
                    <n-tag class="go-m-1" v-for="pkg in Object.keys(npmPkgs || {})" :key="pkg">{{ pkg }}</n-tag>
                  </n-collapse-item>
                </n-collapse>
              </n-scrollbar>
            </n-tab-pane>
            <!-- 介绍案例 -->
            <n-tab-pane :tab="t('visualBigScreen.management-674210-727')" name="3">
              <n-scrollbar trigger="none" style="max-height: 505px">
                <n-collapse arrow-placement="right">
                  <n-collapse-item
                    v-for="(item, index) in templateList"
                    :key="index"
                    :title="t('visualBigScreen.management-674210-728', [index + 1, item.description])"
                    :name="index"
                  >
                    <n-code :code="item.code" language="typescript"></n-code>
                  </n-collapse-item>
                </n-collapse>
              </n-scrollbar>
            </n-tab-pane>
          </n-tabs>
        </n-layout-sider>
      </n-layout>

      <template #action>
        <n-space justify="space-between">
          <div class="go-flex-items-center">
            <n-tag :bordered="false" type="primary">
              <template #icon>
                <n-icon :component="DocumentTextIcon" />
              </template>
              {{ t('visualBigScreen.management-674210-661') }}
            </n-tag>
            <n-text class="go-ml-2" depth="2">{{ t('visualBigScreen.management-674210-729') }}</n-text>
          </div>

          <n-space>
            <n-button size="medium" @click="closeEvents">{{ t('visualBigScreen.management-674210-111') }}</n-button>
            <n-button size="medium" type="primary" @click="saveEvents">{{
              t('visualBigScreen.management-674210-104')
            }}</n-button>
          </n-space>
        </n-space>
      </template>
    </n-card>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import MonacoEditor from '@vb/components/Pages/MonacoEditor/index.vue'
import { useTargetData } from '../../../hooks/useTargetData.hook'
import { templateList } from './importTemplate'
import { npmPkgs } from '@vb/hooks'
import { icon } from '@vb/plugins/icon'
import { EventLife } from '@vb/enums/eventEnum'

const { targetData, chartEditStore } = useTargetData()
const { DocumentTextIcon, ChevronDownIcon, PencilIcon } = icon.ionicons5
const { t } = useI18n()

const EventLifeName = {
  [EventLife.VNODE_BEFORE_MOUNT]: t('visualBigScreen.management-674210-719'),
  [EventLife.VNODE_MOUNTED]: t('visualBigScreen.management-674210-720')
}

const EventLifeTip = {
  [EventLife.VNODE_BEFORE_MOUNT]: t('visualBigScreen.management-674210-721'),
  [EventLife.VNODE_MOUNTED]: t('visualBigScreen.management-674210-722')
}

// 受控弹窗
const showModal = ref(false)
// 编辑区域控制
const editTab = ref(EventLife.VNODE_MOUNTED)
// events 函数模板
let advancedEvents = ref({ ...targetData.value.events.advancedEvents })
// 事件错误标识
const errorFlag = ref(false)

// 验证语法
const validEvents = () => {
  let errorFn = ''
  let message = ''
  let name = ''

  errorFlag.value = Object.entries(advancedEvents.value).every(([eventName, str]) => {
    try {
      // 支持await，验证语法
      const AsyncFunction = Object.getPrototypeOf(async function () {}).constructor
      new AsyncFunction(str)
      return true
    } catch (error: any) {
      message = error.message
      name = error.name
      errorFn = eventName
      return false
    }
  })
  return {
    errorFn,
    message,
    name
  }
}

// 关闭事件
const closeEvents = () => {
  showModal.value = false
}

// 新增事件
const saveEvents = () => {
  if (validEvents().errorFn) {
    ElMessage.error(t('visualBigScreen.management-674210-674'))
    return
  }
  if (Object.values(advancedEvents.value).join('').trim() === '') {
    // 清空事件
    targetData.value.events.advancedEvents = {
      vnodeBeforeMount: undefined,
      vnodeMounted: undefined
    }
  } else {
    targetData.value.events.advancedEvents = { ...advancedEvents.value }
  }
  closeEvents()
}

watch(
  () => showModal.value,
  (newData: boolean) => {
    if (newData) {
      advancedEvents.value = { ...targetData.value.events.advancedEvents }
    }
  }
)
</script>

<style lang="scss" scoped>
@import '../index.scss';
p {
  margin: 0;
}
</style>
