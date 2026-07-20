<template>
  <n-config-provider
    :theme="darkTheme"
    :theme-overrides="overridesTheme"
    :locale="locale"
    :date-locale="dateLocale"
  >
    <div class="go-edit">
      <n-layout>
        <n-layout-header class="go-edit-header go-px-5 go-flex-items-center" bordered>
          <div>
            <n-text class="go-edit-title go-mr-4">{{ t('visualBigScreen.management-674210-1655') }}</n-text>
            <n-button v-if="showOpenFilePicker" class="go-mr-3" size="medium" @click="importJSON">
              <template #icon>
                <n-icon>
                  <download-icon></download-icon>
                </n-icon>
              </template>
              {{ t('import') }}
            </n-button>
          </div>
          <n-space>
            <n-tag :bordered="false" type="warning"> {{ ` ${t('visualBigScreen.management-674210-1656')} ` }} </n-tag>
            <n-button v-if="showOpenFilePicker" class="go-mr-3" size="medium" @click="updateSync">
              <template #icon>
                <n-icon>
                  <analytics-icon></analytics-icon>
                </n-icon>
              </template>
              {{ t('save') }}
            </n-button>
          </n-space>
        </n-layout-header>
        <n-layout-content>
          <monaco-editor
            v-model:modelValue="content"
            language="json"
            :editorOptions="{
              lineNumbers: 'on',
              minimap: { enabled: true }
            }"
          />
        </n-layout-content>
      </n-layout>
    </div>
  </n-config-provider>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { NConfigProvider } from 'naive-ui'
import MonacoEditor from '@vb/components/Pages/MonacoEditor/index.vue'
import { SavePageEnum } from '@vb/enums/editPageEnum'
import { getSessionStorageInfo } from '../preview/utils'
import {
  setSessionStorage,
  fetchRouteParamsLocation,
  JSONStringify,
  JSONParse,
  setTitle,
  goDialog
} from '@vb/utils'
import { StorageEnum } from '@vb/enums/storageEnum'
import { icon } from '@vb/plugins/icon'
import { useSync } from '@vb/views/chart/hooks/useSync.hook'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { ProjectInfoEnum } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import { useDarkThemeHook, useThemeOverridesHook, useLang } from '@vb/hooks'
import type { ChartEditStorageType } from '../preview/index.d'

const chartEditStore = useChartEditStore()
const { dataSyncUpdate } = useSync()
const { t } = useI18n()
const darkTheme = useDarkThemeHook()
const overridesTheme = useThemeOverridesHook()
const { locale, dateLocale } = useLang()

const { DownloadIcon, AnalyticsIcon } = icon.ionicons5
const showOpenFilePicker: Function = (window as any).showOpenFilePicker
const content = ref('')

ElMessage.warning(t('visualBigScreen.management-674210-1646'))

// 从sessionStorage 获取数据
async function getDataBySession() {
  const localStorageInfo: ChartEditStorageType = (await getSessionStorageInfo()) as unknown as ChartEditStorageType
  setTitle(`编辑-${localStorageInfo.editCanvasConfig.projectName}`)
  content.value = JSONStringify(localStorageInfo)
}
setTimeout(getDataBySession)

// 导入json文本
function importJSON() {
  goDialog({
    message: t('visualBigScreen.management-674210-1631'),
    isMaskClosable: true,
    transformOrigin: 'center',
    onPositiveCallback: async () => {
      try {
        const files = await showOpenFilePicker()
        const file = await files[0].getFile()
        const fr = new FileReader()
        fr.readAsText(file)
        fr.onloadend = () => {
          content.value = (fr.result || '').toString()
        }
        ElMessage.success(t('visualBigScreen.management-674210-1635'))
      } catch {
        ElMessage.error(t('visualBigScreen.management-674210-1636'))
      }
    }
  })
}

// 同步数据编辑页
window.opener.addEventListener(SavePageEnum.CHART, (e: any) => {
  ElMessage.success(t('visualBigScreen.management-674210-1645'))
  setSessionStorage(StorageEnum.GO_CHART_STORAGE_LIST, [e.detail])
  content.value = JSONStringify(e.detail)
})

// 保存按钮同步数据
document.addEventListener('keydown', function (e) {
  if (e.keyCode == 83 && (navigator.platform.match('Mac') ? e.metaKey : e.ctrlKey)) {
    e.preventDefault()
    updateSync()
  }
})

// 同步更新
async function updateSync() {
  if (!window.opener) {
    return ElMessage.error(t('visualBigScreen.management-674210-1637'))
  }
  goDialog({
    message: t('visualBigScreen.management-674210-1632'),
    isMaskClosable: true,
    transformOrigin: 'center',
    onPositiveCallback: async () => {
      try {
        const detail = JSONParse(content.value)
        delete detail.id
        // 保持id不变
        // 带后端版本额外处理请求
        if (dataSyncUpdate) {
          chartEditStore.setProjectInfo(ProjectInfoEnum.PROJECT_ID, fetchRouteParamsLocation())
          await dataSyncUpdate(false) // JSON界面保存不上传缩略图
        }
        window.opener.dispatchEvent(new CustomEvent(SavePageEnum.JSON, { detail }))
        ElMessage.success(t('visualBigScreen.management-674210-1638'))
      } catch {
        ElMessage.error(t('visualBigScreen.management-674210-1639'))
      }
    }
  })
}

// 关闭页面发送关闭事件
window.onbeforeunload = () => {
  if (window.opener) {
    window.opener.dispatchEvent(new CustomEvent(SavePageEnum.CLOSE))
  }
}
</script>

<style lang="scss" scoped>
.go-edit {
  display: flex;
  flex-direction: column;
  height: 100vh;

  @include themeify {
    background-color: themed('background-color1') !important;
    color: themed('color') !important;

    :deep(.n-layout),
    :deep(.n-layout-content),
    :deep(.n-layout-header) {
      background-color: themed('background-color1') !important;
      color: themed('color') !important;
    }
  }

  .go-edit-header {
    display: flex;
    align-items: center;
    height: 60px;
    justify-content: space-between;
    .go-edit-title {
      position: relative;
      bottom: 3px;
      font-size: 18px;
      font-weight: bold;
    }
  }
  @include deep() {
    .go-editor-area {
      height: calc(100vh - 60px) !important;
    }
  }
}
</style>
