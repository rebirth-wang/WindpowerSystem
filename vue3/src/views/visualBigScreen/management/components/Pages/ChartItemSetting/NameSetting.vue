<template>
  <setting-item-box :name="t('visualBigScreen.management-674210-1077')" :alone="true">
    <n-input
      type="text"
      maxlength="12"
      minlength="1"
      :placeholder="t('visualBigScreen.management-674210-1078')"
      size="small"
      clearable
      show-count
      v-model:value="chartConfig.title"
      @focus="handleFocus"
      @blur="handleBlur"
    ></n-input>
  </setting-item-box>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { ConfigType } from '@vb/packages/index.d'

const { t } = useI18n()

const props = defineProps({
  chartConfig: {
    type: Object as PropType<ConfigType>,
    required: true
  }
})

let valueCatch = ''

const handleFocus = () => {
  valueCatch = props.chartConfig.title
}

const handleBlur = () => {
  if (!props.chartConfig.title.length) {
    ElMessage.warning(t('visualBigScreen.management-674210-1079'))
    props.chartConfig.title = valueCatch
  }
}
</script>
