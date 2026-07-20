<template>
  <!-- Echarts 全局设置 -->
  <global-setting :optionData="optionData"></global-setting>
  <CollapseItem :name="t('visualBigScreen.management-674210-144')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-489')">
      <SettingItem :name="t('visualBigScreen.management-674210-490')">
        <n-select
          size="small"
          v-model:value="mapRegion.adcode"
          :options="mapRegionOptions"
          value-field="adcode"
          label-field="name"
        />
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-491')">
      <SettingItem :name="t('visualBigScreen.management-674210-492')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].itemStyle.areaColor.colorStops[0].color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-493')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].itemStyle.areaColor.colorStops[1].color"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-259')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].itemStyle.shadowColor"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-494')">
        <n-input-number
          v-model:value="seriesList[1].itemStyle.shadowBlur"
          :min="0"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-495')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-496')">
        <n-input-number
          v-model:value="seriesList[1].itemStyle.shadowOffsetX"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-497')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-498')">
        <n-input-number
          v-model:value="seriesList[1].itemStyle.shadowOffsetY"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-499')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-500')">
      <SettingItem :name="t('visualBigScreen.management-674210-323')">
        <n-space>
          <n-switch v-model:value="seriesList[1].label.show" size="small"></n-switch>
        </n-space>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-501')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="seriesList[1].label.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="seriesList[1].label.fontSize"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-502')">
      <SettingItem :name="t('visualBigScreen.management-674210-503')">
        <n-space>
          <n-switch v-model:value="seriesList[1].emphasis.disabled" size="small"></n-switch>
        </n-space>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].emphasis.itemStyle.areaColor"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="seriesList[1].emphasis.label.fontSize"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-259')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].emphasis.itemStyle.shadowColor"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-504')">
        <n-input-number
          v-model:value="seriesList[1].emphasis.itemStyle.borderWidth"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-505')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-506')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].emphasis.label.color"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-507')">
      <SettingItem :name="t('visualBigScreen.management-674210-323')">
        <n-space>
          <n-switch v-model:value="seriesList[1].tooltip.show" size="small"></n-switch>
        </n-space>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number
          v-model:value="seriesList[1].tooltip.textStyle.fontSize"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-501')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].tooltip.textStyle.color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-253')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].tooltip.backgroundColor"
        ></n-color-picker>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-508')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[1].itemStyle.borderColor"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-504')">
        <n-input-number
          v-model:value="seriesList[1].itemStyle.borderWidth"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-505')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-509')" v-if="mapRegion.adcode === 'china'">
      <SettingItem>
        <n-checkbox v-model:checked="mapRegion.showHainanIsLands" size="small">{{
          t('visualBigScreen.management-674210-510')
        }}</n-checkbox>
      </SettingItem>
      <SettingItem v-if="seriesList[2]">
        <n-checkbox v-model:checked="mapRegion.enter" size="small">{{
          t('visualBigScreen.management-674210-511')
        }}</n-checkbox>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-512')" v-if="mapRegion.enter">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="mapRegion.backColor"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number
          v-model:value="mapRegion.backSize"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-243')"
        ></n-input-number>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
  <CollapseItem :name="t('visualBigScreen.management-674210-513')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="seriesList[0].symbolSize" size="small" :min="0"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="seriesList[0].itemStyle.color"></n-color-picker>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-149')">
      <SettingItem :name="t('visualBigScreen.management-674210-323')">
        <n-space>
          <n-switch v-model:value="seriesList[0].label.show" size="small"></n-switch>
        </n-space>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-243')">
        <n-input-number v-model:value="seriesList[0].label.fontSize" size="small" :min="0"></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-501')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="seriesList[0].label.color"></n-color-picker>
      </SettingItem>
    </SettingItemBox>

    <SettingItemBox :name="t('visualBigScreen.management-674210-514')">
      <SettingItem :name="t('visualBigScreen.management-674210-515')">
        <n-input-number
          v-model:value="seriesList[0].rippleEffect.scale"
          :min="1"
          size="small"
          :placeholder="t('visualBigScreen.management-674210-516')"
        ></n-input-number>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-517')">
        <n-color-picker size="small" :modes="['hex']" v-model:value="seriesList[0].rippleEffect.color"></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-518')">
        <n-select size="small" v-model:value="seriesList[0].rippleEffect.brushType" :options="rippleEffectOptions" />
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>

  <CollapseItem v-if="seriesList[2]" :name="t('visualBigScreen.management-674210-519')" :expanded="true">
    <SettingItemBox :name="t('visualBigScreen.management-674210-324')">
      <SettingItem :name="t('visualBigScreen.management-674210-520')">
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-input-number v-model:value="seriesList[2].effect.period" size="small" :min="0"></n-input-number>
          </template>
          {{ t('visualBigScreen.management-674210-521') }}
        </n-tooltip>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-522')">
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-input-number
              v-model:value="seriesList[2].effect.trailLength"
              size="small"
              :min="0"
              :max="1"
            ></n-input-number>
          </template>
          {{ t('visualBigScreen.management-674210-523') }}
        </n-tooltip>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-566')">
        <n-input-number v-model:value="seriesList[2].effect.symbolSize" size="small" :min="0"></n-input-number>
      </SettingItem>
    </SettingItemBox>
    <SettingItemBox :name="t('visualBigScreen.management-674210-351')">
      <SettingItem :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker
          size="small"
          :modes="['hex']"
          v-model:value="seriesList[2].lineStyle.normal.color"
        ></n-color-picker>
      </SettingItem>
      <SettingItem :name="t('visualBigScreen.management-674210-250')">
        <n-input-number v-model:value="seriesList[2].lineStyle.normal.width" size="small" :min="1"></n-input-number>
      </SettingItem>
    </SettingItemBox>
  </CollapseItem>
</template>

<script setup lang="ts">
import { PropType, computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'
import { GlobalSetting } from '@vb/components/Pages/ChartItemSetting'
import mapChinaJson from './mapGeojson/china.json'

const { t } = useI18n()
const mapRegionOptions = ref([
  {
    adcode: 'china',
    name: t('visualBigScreen.management-674210-524')
  }
])

const rippleEffectOptions = computed(() => [
  {
    value: 'fill',
    label: t('visualBigScreen.management-674210-525')
  },
  {
    value: 'stroke',
    label: t('visualBigScreen.management-674210-526')
  }
])

const props = defineProps({
  optionData: {
    type: Object as PropType<GlobalThemeJsonType>,
    required: true
  }
})

const initMapRegionOptions = () => {
  mapChinaJson.features.forEach((element: any) => {
    if (element.properties.name) {
      mapRegionOptions.value.push({ ...element.properties })
    }
  })
}
initMapRegionOptions()

const seriesList = computed(() => {
  return props.optionData.series
})

const mapRegion = computed(() => {
  return props.optionData.mapRegion
})
</script>
