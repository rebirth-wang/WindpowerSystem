<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"></global-setting>
  <CollapseItem
    v-for="(item, index) in seriesList"
    :key="index"
    :name="`${t('visualBigScreen.management-674210-913')}${index + 1}`"
    :expanded="true"
  >
    <template #header>
      <n-text class="go-fs-13" depth="3">
        {{
          item.type == 'bar'
            ? `「${t('visualBigScreen.management-674210-140')}」`
            : `「${t('visualBigScreen.management-674210-142')}」`
        }}
      </n-text>
    </template>
    <SettingItemBox :name="t('visualBigScreen.management-674210-268')">
      <SettingItem :name="t('visualBigScreen.management-674210-268')">
        <n-select
          :value="item.type"
          size="small"
          :options="[
            { label: t('visualBigScreen.management-674210-140'), value: 'bar' },
            { label: t('visualBigScreen.management-674210-142'), value: 'line' }
          ]"
          @update:value="
            (value: any) => {
              updateHandle(item, value)
            }
          "
        />
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-994')" v-if="item.type == 'bar'">
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="item.barWidth"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-251')">
        <n-input-number v-model:value="item.itemStyle.borderRadius" :min="0" size="small"></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-295')" v-if="item.type == 'line'">
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number
          v-model:value="item.lineStyle.width"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-268')">
        <n-select v-model:value="item.lineStyle.type" size="small" :options="lineConf.lineStyle.type"></n-select>
      </SettingItem>
      <setting-item>
        <n-space>
          <n-switch v-model:value="item.smooth" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-997') }}</n-text>
        </n-space>
      </setting-item>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-998')" v-if="item.type == 'line'">
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number
          v-model:value="item.symbolSize"
          :min="1"
          :max="100"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-995')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <setting-item-box :name="t('visualBigScreen.management-674210-447')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="item.label.show" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-996') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="item.label.fontSize" size="small" :min="1"></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="item.label.color"></n-color-picker>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select
          v-model:value="item.label.position"
          :options="[
            { label: t('visualBigScreen.management-674210-569'), value: 'top' },
            { label: t('visualBigScreen.management-674210-571'), value: 'left' },
            { label: t('visualBigScreen.management-674210-572'), value: 'right' },
            { label: t('visualBigScreen.management-674210-570'), value: 'bottom' }
          ]"
        />
      </setting-item>
    </setting-item-box>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed, toRaw } from 'vue'
import { useI18n } from 'vue-i18n'
import { merge, cloneDeep } from 'lodash'

import GlobalSetting from '@vb/components/Pages/ChartItemSetting/GlobalSetting.vue'
import CollapseItem from '@vb/components/Pages/ChartItemSetting/CollapseItem.vue'
import SettingItemBox from '@vb/components/Pages/ChartItemSetting/SettingItemBox.vue'
import SettingItem from '@vb/components/Pages/ChartItemSetting/SettingItem.vue'

import { lineConf } from '@vb/packages/chartConfiguration/echarts'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes'
import { barSeriesItem, lineSeriesItem } from './config'

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const seriesList = computed(() => {
  return props.optionData.series
})

const updateHandle = (item: any, value: string) => {
  const _label = cloneDeep(toRaw(item.label))
  lineSeriesItem.label = _label
  if (value === 'line') {
    merge(item, lineSeriesItem)
  } else {
    merge(item, barSeriesItem)
  }
}
</script>
