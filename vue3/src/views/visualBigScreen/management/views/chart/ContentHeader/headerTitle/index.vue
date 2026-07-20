<template>
  <n-space>
    <template v-if="!focus">
      <n-text @mousedown.prevent="handleFocus">
        {{ t('visualBigScreen.management-674210-115') }} -
      </n-text>
      <n-button secondary size="tiny" @mousedown.prevent="handleFocus">
        <span class="title">
          {{ comTitle }}
        </span>
      </n-button>
    </template>

    <n-input
      v-else
      ref="inputInstRef"
      size="small"
      type="text"
      maxlength="16"
      show-count
      :placeholder="t('visualBigScreen.management-674210-16')"
      v-model:value="editTitle"
      @keydown.enter.prevent.stop="handleBlur"
      @mousedown.stop
      @click.stop
      @blur="handleBlur"
    ></n-input>
  </n-space>
</template>

<script setup lang="ts">
import { ref, nextTick, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { ResultEnum } from '@vb/enums/httpEnum'
import { fetchRouteParamsLocation, httpErrorHandle, setTitle } from '@vb/utils'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { ProjectInfoEnum, EditCanvasConfigEnum } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import { updateProjectApi } from '@vb/api/path'
import { useSync } from '../../hooks/useSync.hook'

const { t } = useI18n()

const chartEditStore = useChartEditStore()
const { dataSyncUpdate, ensureProjectData } = useSync()

const focus = ref<boolean>(false)
const inputInstRef = ref(null)
const editTitle = ref<string>('')
const isSaving = ref(false)

const comTitle = computed(() => {
  return (chartEditStore.getProjectInfo.projectName || '').replace(/\s/g, '') || t('visualBigScreen.management-674210-26')
})

watch(
  () => chartEditStore.getProjectInfo.projectName,
  newValue => {
    const normalizedTitle = (newValue || '').replace(/\s/g, '') || t('visualBigScreen.management-674210-26')
    setTitle(`${t('visualBigScreen.management-674210-115')}-${normalizedTitle}`)
    chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.PROJECT_NAME, normalizedTitle)
  },
  {
    immediate: true
  }
)

const handleFocus = () => {
  editTitle.value = chartEditStore.getProjectInfo.projectName || ''
  focus.value = true
  nextTick(() => {
    if (inputInstRef.value) {
      (inputInstRef.value as any).focus()
    }
  })
}

const handleBlur = async () => {
  if (isSaving.value) return
  isSaving.value = true

  try {
    const normalizedTitle = (editTitle.value || '').replace(/\s/g, '') || t('visualBigScreen.management-674210-26')
    focus.value = false
    editTitle.value = normalizedTitle
    chartEditStore.setProjectInfo(ProjectInfoEnum.PROJECT_NAME, normalizedTitle)

    // 确保项目数据存在
    const projectData = await ensureProjectData()
    if (!projectData) {
      ElMessage.error(t('visualBigScreen.management-674210-1652'))
      return
    }
    const res = await updateProjectApi({
      id: fetchRouteParamsLocation(),
      projectName: normalizedTitle,
      createBy: projectData.createBy,
      tenantId: projectData.tenantId
    })
    if (res && res.code === ResultEnum.SUCCESS) {
      dataSyncUpdate()
    } else {
      httpErrorHandle()
    }
  } finally {
    isSaving.value = false
  }
}
</script>
<style lang="scss" scoped>
.title {
  padding-left: 5px;
  padding-right: 5px;
  font-size: 15px;
}
</style>
