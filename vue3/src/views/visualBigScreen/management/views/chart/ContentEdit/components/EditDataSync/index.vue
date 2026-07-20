<template>
  <div class="go-edit-data-sync go-flex-items-center">
    <n-tooltip trigger="hover">
      <template #trigger>
        <n-text class="status-desc go-ml-2" :type="descType" depth="3">
          {{ statusDesc }}
        </n-text>
      </template>
      <span>{{ saveInterval }}s {{ t('visualBigScreen.management-674210-78') }}</span>
    </n-tooltip>
    <n-spin v-show="statusDesc === statusDescObj[1]['text']" class="go-ml-2" size="small">
      <template #icon>
        <n-icon size="13">
          <reload-icon />
        </n-icon>
      </template>
    </n-spin>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, toRefs, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'
import { SyncEnum } from '@vb/enums/editPageEnum'
import { icon } from '@vb/plugins/icon'
import { saveInterval } from '@vb/settings/designSetting'

const { t } = useI18n()

const { ReloadIcon } = icon.ionicons5

const chartEditStore = useChartEditStore()
const designStore = useDesignStore()

const { saveStatus } = toRefs(chartEditStore.getEditCanvas)
const themeColor = computed(() => designStore.getAppTheme)

const statusDesc = ref('')
const descType = ref('')
let setTimeoutIns: NodeJS.Timeout = setTimeout(() => {})

const statusDescObj = {
  [SyncEnum.PENDING]: {
    text: t('visualBigScreen.management-674210-79'),
    type: ''
  },
  [SyncEnum.START]: {
    text: t('visualBigScreen.management-674210-80'),
    type: 'success'
  },
  [SyncEnum.SUCCESS]: {
    text: t('visualBigScreen.management-674210-81'),
    type: 'success'
  },
  [SyncEnum.FAILURE]: {
    text: t('visualBigScreen.management-674210-82'),
    type: 'error'
  }
}

watch(
  () => saveStatus.value,
  newData => {
    clearTimeout(setTimeoutIns)
    statusDesc.value = statusDescObj[newData]['text']
    descType.value = statusDescObj[newData]['type']
    // 3秒重置展示
    setTimeoutIns = setTimeout(() => {
      statusDesc.value = statusDescObj[SyncEnum.PENDING]['text']
      descType.value = statusDescObj[SyncEnum.PENDING]['type']
    }, 3000)
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
.go-edit-data-sync {
  :deep(.n-spin) {
    width: 13px;
    height: 13px;
  }

  .status-desc {
    cursor: default;
    color: v-bind('themeColor');
    font-size: 12px;
    opacity: 0.8;
  }
}
</style>
