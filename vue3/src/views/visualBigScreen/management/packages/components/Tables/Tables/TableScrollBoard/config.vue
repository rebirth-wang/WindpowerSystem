<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-135')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-267')">
      <SettingItem :name="t('visualBigScreen.management-674210-339')">
        <n-input-number
          v-model:value="optionData.rowNum"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-403')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-340')">
        <n-input-number
          v-model:value="optionData.waitTime"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-404')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-349')">
        <n-input-number
          v-model:value="optionData.headerHeight"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-405')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-350')">
        <n-switch size="small" v-model:value="optionData.index" />
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-351')" :alone="true">
      <SettingItem :name="t('visualBigScreen.management-674210-352')">
        <n-input
          v-model:value="header"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-406')"
        ></n-input>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-353')">
        <n-input
          v-model:value="align"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-407')"
        ></n-input>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-354')">
        <n-input
          v-model:value="columnWidth"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-408')"
        ></n-input>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-430')">
        <n-select v-model:value="optionData.carousel" :options="carouselOptions" />
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-357')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.headerBGC"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-358')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.oddRowBGC"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-359')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.evenRowBGC"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'

const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const header = ref()
const align = ref()
const columnWidth = ref()
const carouselOptions = [
  { label: t('visualBigScreen.management-674210-355'), value: 'single' },
  { label: t('visualBigScreen.management-674210-356'), value: 'page' }
]

watch(
  () => props.optionData,
  newData => {
    header.value = props.optionData.header.toString()
    align.value = props.optionData.align.toString()
    columnWidth.value = props.optionData.columnWidth.toString()
  },
  {
    deep: false,
    immediate: true
  }
)

watch([header, align, columnWidth], ([headerNew, alignNew, columnWidthNew], [headerOld, alignOld, columnWidthOld]) => {
  if (headerNew !== headerOld) {
    props.optionData.header = headerNew.split(',')
  }
  if (alignNew !== alignOld) {
    props.optionData.align = alignNew.split(',')
  }
  if (columnWidthNew !== columnWidthOld) {
    // @ts-ignore
    props.optionData.columnWidth = columnWidthNew.split(',')
  }
})
</script>
