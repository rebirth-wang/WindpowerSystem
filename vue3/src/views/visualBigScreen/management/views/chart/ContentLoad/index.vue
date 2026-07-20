<template>
  <n-modal :show="showModal" :close-on-esc="false" transform-origin="center">
    <div>
      <span>{{ t('sip.splitview.998531-0') }}...</span>
      <n-progress type="line" :color="themeColor" :percentage="percentage" style="width: 300px" />
    </div>
  </n-modal>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useChartLayoutStore } from '@vb/store/modules/chartLayoutStore/chartLayoutStore'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'

const chartLayoutStore = useChartLayoutStore()
const designStore = useDesignStore()
const { t } = useI18n()
const showModal = ref(false)
const percentage = ref(0)

// 颜色
const themeColor = computed(() => {
  return designStore.getAppTheme
})

// 监听百分比
watch(
  () => chartLayoutStore.getPercentage,
  newValue => {
    if (newValue === 0) {
      setTimeout(() => {
        percentage.value = newValue
        showModal.value = false
      }, 500)
      return
    }
    percentage.value = newValue
    showModal.value = newValue > 0
  }
)
</script>
