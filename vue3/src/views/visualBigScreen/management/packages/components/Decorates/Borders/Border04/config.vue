<template>
  <CollapseItem :name="t('visualBigScreen.management-674210-150')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-919')" :alone="true">
      <n-input
        size="small"
        v-model:value="optionData.borderTitle"
        :minlength="1"
        type="text"
        :placeholder="t('visualBigScreen.management-674210-948')"
      />
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.borderTitleColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-926')">
        <n-input-number size="small" v-model:value="optionData.borderTitleSize" :min="12" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-336')">
        <n-input-number size="small" v-model:value="optionData.borderTitleHeight" :min="24" />
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number size="small" v-model:value="optionData.borderTitleWidth" :min="50" :step="10" />
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>

  <CollapseItem :name="t('visualBigScreen.management-674210-152')" :expanded="true">
    <SettingItemBox
      :name="`${t('visualBigScreen.management-674210-129')}-${index + 1}`"
      v-for="(item, index) in optionData.colors"
      :key="index"
    >
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.colors[index]"></n-color-picker>
      </SettingItem>
      <SettingItem>
        <n-button size="small" @click="optionData.colors[index] = option.colors[index]">
          {{ t('visualBigScreen.management-674210-947') }}
        </n-button>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>

  <CollapseItem :name="t('visualBigScreen.management-674210-252')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-129')">
      <SettingItem>
        <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.backgroundColor"></n-color-picker>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
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
</script>
