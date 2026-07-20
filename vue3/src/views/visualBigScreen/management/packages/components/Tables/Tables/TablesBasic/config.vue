<template>
  <collapse-item :name="t('visualBigScreen.management-674210-360')" :expanded="true">
    <n-tag type="primary">{{ t('visualBigScreen.management-674210-361') }}</n-tag>
    <setting-item-box :alone="true" :name="t('visualBigScreen.management-674210-362')">
      <setting-item :alone="true">
        <n-select v-model:value="optionData.align" size="small" :options="alignOptions" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :alone="false" :name="t('visualBigScreen.management-674210-301')">
      <setting-item :name="t('visualBigScreen.management-674210-302')" :alone="true">
        <n-input-number
          v-model:value="optionData.pagination.page"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-302')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-218')" :alone="true">
        <n-select v-model:value="optionData.pagination.pageSize" size="small" :options="page" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :alone="false" :name="t('visualBigScreen.management-674210-363')">
      <SettingItem :name="t('visualBigScreen.management-674210-364')" class="form_name">
        <div style="width: 260px">
          <n-input
            v-model:value="header"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-406')"
          ></n-input>
        </div>
      </SettingItem>
    </setting-item-box>
    <setting-item-box :alone="false" :name="t('visualBigScreen.management-674210-365')">
      <SettingItem :name="t('visualBigScreen.management-674210-366')" :alone="true">
        <n-select v-model:value="(optionData as any).style.border" size="small" :options="borderFlag" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-367')" :alone="true">
        <n-select
          v-model:value="(optionData as any).style.bottomBordered"
          size="small"
          :options="bottom_borderedFlag"
        />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-368')" :alone="true">
        <n-select v-model:value="(optionData as any).style.singleLine" size="small" :options="columnFlag" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-369')" :alone="true">
        <n-select v-model:value="(optionData as any).style.singleColumn" size="small" :options="lineFlag" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-370')" :alone="true">
        <n-select v-model:value="(optionData as any).style.striped" size="small" :options="stripedFlag" />
      </SettingItem>
      <setting-item :name="t('visualBigScreen.management-674210-243')" :alone="true">
        <n-input-number
          v-model:value="optionData.style.fontSize"
          :min="12"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-371')" :alone="true">
        <n-input-number
          v-model:value="optionData.style.borderWidth"
          :min="0"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-371')"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-372')" :alone="true">
        <n-color-picker size="small" :modes="['rgb']" v-model:value="optionData.style.borderColor"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-373')" :alone="true">
        <n-select v-model:value="optionData.style.borderStyle" size="small" :options="borderStyleFlag" />
      </setting-item>
      <SettingItem :name="t('visualBigScreen.management-674210-374')" :alone="true">
        <n-select v-model:value="optionData.inputShow" size="small" :options="inputSelect" />
      </SettingItem>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, watch, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { option } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

const page = [
  { label: '2', value: 2 },
  { label: '5', value: 5 },
  { label: '10', value: 10 },
  { label: '15', value: 15 },
  { label: '30', value: 30 }
]
const alignOptions = [
  { label: t('visualBigScreen.management-674210-375'), value: 'left' },
  { label: t('visualBigScreen.management-674210-376'), value: 'center' },
  { label: t('visualBigScreen.management-674210-377'), value: 'right' }
]
const borderFlag = [
  { label: t('visualBigScreen.management-674210-323'), value: 'on' },
  { label: t('visualBigScreen.management-674210-378'), value: 'off' }
]
const columnFlag = [
  { label: t('visualBigScreen.management-674210-323'), value: 'off' },
  { label: t('visualBigScreen.management-674210-378'), value: 'on' }
]
const lineFlag = [
  { label: t('visualBigScreen.management-674210-323'), value: 'off' },
  { label: t('visualBigScreen.management-674210-378'), value: 'on' }
]
const bottom_borderedFlag = [
  { label: t('visualBigScreen.management-674210-323'), value: 'on' },
  { label: t('visualBigScreen.management-674210-378'), value: 'off' }
]
const stripedFlag = [
  { label: t('visualBigScreen.management-674210-323'), value: 'on' },
  { label: t('visualBigScreen.management-674210-378'), value: 'off' }
]
const borderStyleFlag = [
  { label: t('visualBigScreen.management-674210-379'), value: 'solid' },
  { label: t('visualBigScreen.management-674210-380'), value: 'dashed' },
  { label: t('visualBigScreen.management-674210-381'), value: 'dotted' },
  { label: t('visualBigScreen.management-674210-382'), value: 'double' }
]
const inputSelect = [
  { label: t('visualBigScreen.management-674210-383'), value: 'none' },
  { label: t('visualBigScreen.management-674210-384'), value: 'flex' }
]
const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const header = ref()
const median = ref<string[]>([])

//转string
watch(
  () => props.optionData,
  () => {
    median.value = []
    props.optionData.dataset.dimensions.forEach(item => {
      median.value.push(item.title)
    })
    header.value = median.value.toString()
  },
  {
    deep: false,
    immediate: true
  }
)

//更新columns
watch([header], ([headerNew], [headerOld]) => {
  if (headerNew !== headerOld) {
    const headers = headerNew.split(',')
    headers.forEach((_, index: number) => {
      if (index + 1 <= props.optionData.dataset.dimensions.length) {
        props.optionData.dataset.dimensions[index].title = headers[index]
      }
    })
  }
})
</script>
