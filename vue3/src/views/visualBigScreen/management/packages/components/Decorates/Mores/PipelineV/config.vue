<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-973')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-974')">
      <SettingItem>
        <n-select v-model:value="optionData.color_type" :options="colorOptions" @update:value="handleColorChange" />
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-975')">
      <SettingItem>
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.o_color"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-976')">
      <SettingItem>
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.i_color"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-977')">
      <SettingItem>
        <n-select v-model:value="optionData.line_class" :options="options" />
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})
const { t } = useI18n()

const options = ref([
  {
    value: 'svg_ani_flow',
    label: t('visualBigScreen.management-674210-978')
  },
  {
    value: 'svg_ani_flow_back',
    label: t('visualBigScreen.management-674210-450')
  },
  {
    value: 'svg_ani_flow_stop',
    label: t('visualBigScreen.management-674210-979')
  }
])

const colorOptions = ref([
  {
    value: 1,
    label: t('visualBigScreen.management-674210-980')
  },
  {
    value: 2,
    label: t('visualBigScreen.management-674210-981')
  }
])

// 默认颜色
const handleColorChange = (e: number) => {
  switch (e) {
    case 1:
      props.optionData.o_color = '#0a7ae2'
      props.optionData.i_color = '#119bfa'
      break
    case 2:
      props.optionData.o_color = '#ff9d00'
      props.optionData.i_color = '#f7ea37'
      break
  }
}
</script>
