<template>
  <div>
    <CollapseItem :name="t('visualBigScreen.management-674210-177')" :expanded="true">
      <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
        <SettingItem :name="t('visualBigScreen.management-674210-457')">
          <n-select
            v-model:value="sankeyConfig.orient"
            size="small"
            :options="orientList"
            :placeholder="t('visualBigScreen.management-674210-410')"
          />
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-916')">
          <n-select
            v-model:value="optionData.tooltip.show"
            size="small"
            :options="toolTipSwitch"
            :placeholder="t('visualBigScreen.management-674210-917')"
          />
        </SettingItem>
      </SettingItemBox>
    </CollapseItem>
  </div>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option, orientList, toolTipSwitch } from './config'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const sankeyConfig = computed<typeof option.series>(() => {
  return props.optionData.series
})
</script>
