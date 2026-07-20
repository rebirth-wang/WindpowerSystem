<template>
  <collapse-item :name="t('visualBigScreen.management-674210-305')" :expanded="true">
    <setting-item v-for="(item, index) in optionData.dataset" :key="index">
      <n-input-group>
        <n-input
          v-model:value="optionData.dataset[index]"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-400')"
        ></n-input>
        <n-button ghost @click="optionData.dataset.splice(index, 1)"> - </n-button>
      </n-input-group>
    </setting-item>
    <setting-item>
      <n-button size="small" @click="optionData.dataset.push('')">
        + {{ t('visualBigScreen.management-674210-413') }}</n-button
      >
    </setting-item>
  </collapse-item>
  <collapse-item :name="t('visualBigScreen.management-674210-311')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-312')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.autoplay" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-313') }}</n-text>
        </n-space>
      </setting-item>
      <!-- 开启自动播放时，设置间隔时间 -->
      <setting-item :name="t('visualBigScreen.management-674210-314')">
        <n-input-number v-model:value="optionData.interval" size="small" placeholder="">
          <template #suffix>{{ t('visualBigScreen.management-674210-315') }}</template>
        </n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-316')">
        <n-select
          v-model:value="optionData.direction"
          :options="directions"
          :placeholder="t('visualBigScreen.management-674210-410')"
        />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-317')">
        <n-select
          v-model:value="optionData.effect"
          :options="effects"
          :placeholder="t('visualBigScreen.management-674210-409')"
        />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-318')">
        <n-input-number v-model:value="optionData.slidesPerview" size="small" placeholder=""></n-input-number>
      </setting-item>
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.centeredSlides" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-319') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-320')">
        <n-select
          v-model:value="optionData.fit"
          :options="fitList"
          :placeholder="t('visualBigScreen.management-674210-411')"
        />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-321')">
      <setting-item :name="t('visualBigScreen.management-674210-242')">
        <n-select
          v-model:value="optionData.dotType"
          :options="dotTypes"
          :placeholder="t('visualBigScreen.management-674210-411')"
        />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-322')">
        <n-select
          v-model:value="optionData.dotPlacement"
          :options="dotPlacements"
          :placeholder="t('visualBigScreen.management-674210-412')"
        />
      </setting-item>
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.showDots" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-323') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.showArrow" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-324') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.draggable" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-325') }}</n-text>
        </n-space>
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { option } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

// 字典
const dotTypes = [
  {
    label: t('visualBigScreen.management-674210-326'),
    value: 'dot'
  },
  {
    label: t('visualBigScreen.management-674210-295'),
    value: 'line'
  }
]
const directions = [
  {
    label: t('visualBigScreen.management-674210-327'),
    value: 'horizontal'
  },
  {
    label: t('visualBigScreen.management-674210-328'),
    value: 'vertical'
  }
]
const effects = [
  {
    label: 'slide',
    value: 'slide'
  },
  {
    label: 'fade',
    value: 'fade'
  },
  {
    label: 'card',
    value: 'card'
  },
  {
    label: 'custom',
    value: 'custom'
  }
]
const dotPlacements = [
  {
    label: t('visualBigScreen.management-674210-329'),
    value: 'top'
  },
  {
    label: t('visualBigScreen.management-674210-330'),
    value: 'bottom'
  },
  {
    label: t('visualBigScreen.management-674210-331'),
    value: 'left'
  },
  {
    label: t('visualBigScreen.management-674210-332'),
    value: 'right'
  }
]

// 适应类型
const fitList = [
  {
    value: 'fill',
    label: 'fill'
  },
  {
    value: 'contain',
    label: 'contain'
  },
  {
    value: 'cover',
    label: 'cover'
  },
  {
    value: 'scale-down',
    label: 'scale-down'
  },
  {
    value: 'none',
    label: 'none'
  }
]
</script>
