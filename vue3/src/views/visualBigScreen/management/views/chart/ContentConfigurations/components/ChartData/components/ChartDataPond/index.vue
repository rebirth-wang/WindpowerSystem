<template>
  <!-- 选中内容 -->
  <div class="go-chart-data-pond">
    <n-card class="n-card-shallow">
      <setting-item-box :name="t('visualBigScreen.management-674210-795')" :alone="true">
        <n-input
          size="small"
          :placeholder="pondData?.dataPondName || t('visualBigScreen.management-674210-692')"
          :disabled="true"
        >
          <template #prefix>
            <n-icon :component="FishIcon" />
          </template>
        </n-input>
      </setting-item-box>

      <setting-item-box :name="t('visualBigScreen.management-674210-796')" :alone="true">
        <n-input
          size="small"
          :placeholder="pondData?.dataPondRequestConfig.requestUrl || t('visualBigScreen.management-674210-692')"
          :disabled="true"
        >
          <template #prefix>
            <n-icon :component="FlashIcon" />
          </template>
        </n-input>
      </setting-item-box>

      <div class="edit-text" @click="controlModelHandle">
        <div class="go-absolute-center">
          <n-button type="primary" secondary>{{ t('visualBigScreen.management-674210-797') }}</n-button>
        </div>
      </div>
    </n-card>
  </div>

  <setting-item-box :alone="true">
    <template #name>
      {{ t('visualBigScreen.management-674210-798') }}
      <n-tooltip trigger="hover">
        <template #trigger>
          <n-icon size="21" :depth="3">
            <help-outline-icon></help-outline-icon>
          </n-icon>
        </template>
        {{ t('visualBigScreen.management-674210-799') }}
      </n-tooltip>
    </template>
    <n-button type="primary" ghost @click="sendHandle">
      <template #icon>
        <n-icon>
          <flash-icon />
        </n-icon>
      </template>
      {{ t('visualBigScreen.management-674210-800') }}
    </n-button>
  </setting-item-box>

  <!-- 底部数据展示 -->
  <chart-data-matching-and-show :show="showMatching && !loading" :ajax="true"></chart-data-matching-and-show>

  <!-- 骨架图 -->
  <go-skeleton :load="loading" :repeat="3"></go-skeleton>

  <!-- 编辑 / 新增弹窗 -->
  <chart-data-pond-control v-model:modelShow="controlModel" @sendHandle="sendHandle"></chart-data-pond-control>
</template>

<script setup lang="ts">
import { ref, toRefs, toRaw, onBeforeUnmount, computed, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'
import { customizeHttp } from '@vb/api/http'
import { SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { ChartDataPondControl } from './components/ChartDataPondControl'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'
import { useTargetData } from '../../../hooks/useTargetData.hook'
import { ChartDataMatchingAndShow } from '../ChartDataMatchingAndShow'
import { newFunctionHandle } from '@vb/utils'

const designStore = useDesignStore()
const { HelpOutlineIcon, FlashIcon, PulseIcon, FishIcon } = icon.ionicons5
const { targetData, chartEditStore } = useTargetData()
const { t } = useI18n()

const {
  requestDataPond,
  requestInterval: GlobalRequestInterval,
  requestIntervalUnit: GlobalRequestIntervalUnit
} = toRefs(chartEditStore.getRequestGlobalConfig)

const loading = ref(false)
const controlModel = ref(false)
const showMatching = ref(false)

let firstFocus = 0
let lastFilter: any = undefined

// 所选数据池
const pondData = computed(() => {
  const selectId = targetData.value.request.requestDataPondId
  if (!selectId) return undefined
  const data = requestDataPond.value.filter(item => {
    return selectId === item.dataPondId
  })
  return data[0]
})

// 颜色
const themeColor = computed(() => {
  return designStore.getAppTheme
})

// 请求配置 model
const controlModelHandle = () => {
  controlModel.value = true
}

// 发送请求
const sendHandle = async () => {
  if (!targetData.value?.request) {
    ElMessage.warning(t('visualBigScreen.management-674210-801'))
    return
  }
  loading.value = true
  try {
    const res = await customizeHttp(toRaw(targetData.value.request), toRaw(chartEditStore.getRequestGlobalConfig))
    loading.value = false
    if (res) {
      if (!res?.data && !targetData.value.filter) {
        ElMessage.warning(t('visualBigScreen.management-674210-802'))
        showMatching.value = true
        return
      }
      targetData.value.option.dataset = newFunctionHandle(res?.data, res, targetData.value.filter)
      showMatching.value = true
      return
    }
    ElMessage.warning(t('visualBigScreen.management-674210-789'))
  } catch (error) {
    console.error(error)
    loading.value = false
    ElMessage.warning(t('visualBigScreen.management-674210-790'))
  }
}

watchEffect(() => {
  const filter = targetData.value?.filter
  if (lastFilter !== filter && firstFocus) {
    lastFilter = filter
    sendHandle()
  }
  firstFocus++
})

onBeforeUnmount(() => {
  lastFilter = null
})
</script>

<style scoped lang="scss">
@include go('chart-data-pond') {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  .n-card-shallow {
    &.n-card {
      @extend .go-background-filter;
    }
    :deep(.n-card-content),
    :deep(.n-card__content) {
      padding: 10px !important;
    }
    .edit-text {
      position: absolute;
      top: 0px;
      left: 0px;
      width: 325px;
      height: 136px;
      cursor: pointer;
      opacity: 0;
      transition: all 0.3s;
      @extend .go-background-filter;
      backdrop-filter: blur(2px) !important;
    }
    &:hover {
      border-color: v-bind('themeColor');
      .edit-text {
        opacity: 1;
      }
    }
  }
}
</style>
