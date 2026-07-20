<template>
  <collapse-item :name="t('visualBigScreen.management-674210-267')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-527')" :alone="true">
      <setting-item>
        <n-select size="small" v-model:value="optionData.mapOptions.lang" :options="langOptions" />
      </setting-item>
    </setting-item-box>
    <setting-item-box name="Key" :alone="true">
      <setting-item :name="t('visualBigScreen.management-674210-528')">
        <n-input
          :value="String(optionData.mapOptions.amapKey ?? '')"
          @update:value="value => (optionData.mapOptions.amapKey = value)"
          size="small"
        ></n-input>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-529')" :alone="true">
      <setting-item>
        <n-input
          size="small"
          :value="String(optionData.mapOptions.amapStyleKeyCustom ?? '')"
          @update:value="value => (optionData.mapOptions.amapStyleKeyCustom = value)"
        />
      </setting-item>
    </setting-item-box>
  </collapse-item>
  <collapse-item :name="t('visualBigScreen.management-674210-144')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-530')">
      <setting-item>
        <n-select size="small" v-model:value="optionData.mapOptions.amapStyleKey" :options="themeOptions" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-531')" :alone="true">
      <n-checkbox-group v-model:value="optionData.mapOptions.features">
        <n-space item-style="display: flex;">
          <n-checkbox :value="item.value" :label="item.label" v-for="(item, index) in featuresOptions" :key="index" />
        </n-space>
      </n-checkbox-group>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-532')" :alone="true">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.mapOptions.showLabel" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-533') }}</n-text>
        </n-space>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-322')">
      <setting-item :name="t('visualBigScreen.management-674210-534')">
        <n-input-number v-model:value="optionData.mapOptions.amapLon" :show-button="false" size="small">
          <template #suffix>°</template>
        </n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-535')">
        <n-input-number v-model:value="optionData.mapOptions.amapLat" :show-button="false" size="small">
          <template #suffix>°</template>
        </n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-536')">
        <n-input-number v-model:value="optionData.mapOptions.amapZindex" :min="0" size="small"></n-input-number>
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-537')" :alone="true">
      <setting-item>
        <n-radio-group v-model:value="optionData.mapOptions.viewMode" name="radiogroup">
          <n-space>
            <n-radio v-for="song in viewModeOptions" :key="song.value" :value="song.value">
              {{ song.label }}
            </n-radio>
          </n-space>
        </n-radio-group>
      </setting-item>
    </setting-item-box>
    <template v-if="optionData.mapOptions.viewMode === '3D'">
      <setting-item-box>
        <setting-item :name="t('visualBigScreen.management-674210-538')">
          <n-color-picker size="small" :modes="['hex']" v-model:value="optionData.mapOptions.skyColor"></n-color-picker>
        </setting-item>
        <setting-item :name="t('visualBigScreen.management-674210-539')">
          <n-input-number v-model:value="optionData.mapOptions.pitch" :min="0" :max="83" size="small"></n-input-number>
        </setting-item>
      </setting-item-box>
    </template>
  </collapse-item>
  <collapse-item :name="t('visualBigScreen.management-674210-513')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-242')">
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select size="small" v-model:value="optionData.mapOptions.mapMarkerType" :options="MarkerOptions" />
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-129')">
        <n-color-picker v-model:value="optionData.mapOptions.marker.fillColor" size="small"></n-color-picker>
      </setting-item>
    </setting-item-box>
  </collapse-item>
  <collapse-item :name="t('visualBigScreen.management-674210-540')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-541')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.mapOptions.satelliteTileLayer.show" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-533') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-542')">
        <n-input-number
          v-model:value="optionData.mapOptions.satelliteTileLayer.zIndex"
          :min="0"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-477')">
        <n-input-number
          v-model:value="optionData.mapOptions.satelliteTileLayer.opacity"
          :min="0"
          :max="1"
          step="0.1"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-543')">
        <n-slider v-model:value="optionData.mapOptions.satelliteTileLayer.zooms" range :step="1" :max="18" :min="3" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-544')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.mapOptions.roadNetTileLayer.show" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-533') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-542')">
        <n-input-number
          v-model:value="optionData.mapOptions.roadNetTileLayer.zIndex"
          :min="0"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-477')">
        <n-input-number
          v-model:value="optionData.mapOptions.roadNetTileLayer.opacity"
          :min="0"
          :max="1"
          step="0.1"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-543')">
        <n-slider v-model:value="optionData.mapOptions.roadNetTileLayer.zooms" range :step="1" :max="18" :min="3" />
      </setting-item>
    </setting-item-box>
    <setting-item-box :name="t('visualBigScreen.management-674210-545')">
      <setting-item>
        <n-space>
          <n-switch v-model:value="optionData.mapOptions.trafficTileLayer.show" size="small" />
          <n-text>{{ t('visualBigScreen.management-674210-533') }}</n-text>
        </n-space>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-542')">
        <n-input-number
          v-model:value="optionData.mapOptions.trafficTileLayer.zIndex"
          :min="0"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-477')">
        <n-input-number
          v-model:value="optionData.mapOptions.trafficTileLayer.opacity"
          :min="0"
          :max="1"
          step="0.1"
          size="small"
        ></n-input-number>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-543')">
        <n-slider v-model:value="optionData.mapOptions.trafficTileLayer.zooms" range :step="1" :max="18" :min="3" />
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { option, MarkerEnum, ThemeEnum, LangEnum, ViewModeEnum, FeaturesEnum } from './config'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'

const { t } = useI18n()

defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const themeOptions = computed(() => [
  {
    value: ThemeEnum.NORMAL,
    label: t('visualBigScreen.management-674210-546')
  },
  {
    value: ThemeEnum.DARK,
    label: t('visualBigScreen.management-674210-547')
  },
  {
    value: ThemeEnum.LIGHT,
    label: t('visualBigScreen.management-674210-548')
  },
  {
    value: ThemeEnum.WHITES_MOKE,
    label: t('visualBigScreen.management-674210-549')
  },
  {
    value: ThemeEnum.FRESH,
    label: t('visualBigScreen.management-674210-550')
  },
  {
    value: ThemeEnum.GREY,
    label: t('visualBigScreen.management-674210-551')
  },
  {
    value: ThemeEnum.GRAFFITI,
    label: t('visualBigScreen.management-674210-552')
  },
  {
    value: ThemeEnum.MACARON,
    label: t('visualBigScreen.management-674210-553')
  },
  {
    value: ThemeEnum.BLUE,
    label: t('visualBigScreen.management-674210-554')
  },
  {
    value: ThemeEnum.DARKBLUE,
    label: t('visualBigScreen.management-674210-555')
  },
  {
    value: ThemeEnum.WINE,
    label: t('visualBigScreen.management-674210-556')
  }
])

const langOptions = computed(() => [
  {
    value: LangEnum.ZH_CN,
    label: t('visualBigScreen.management-674210-557')
  },
  {
    value: LangEnum.EN,
    label: t('visualBigScreen.management-674210-558')
  },
  {
    value: LangEnum.ZH_EN,
    label: t('visualBigScreen.management-674210-559')
  }
])

const viewModeOptions = [
  {
    value: ViewModeEnum.PLANE,
    label: '2D'
  },
  {
    value: ViewModeEnum.STEREOSCOPIC,
    label: '3D'
  }
]

const featuresOptions = computed(() => [
  {
    value: FeaturesEnum.BG,
    label: t('visualBigScreen.management-674210-560')
  },
  {
    value: FeaturesEnum.POINT,
    label: t('visualBigScreen.management-674210-532')
  },
  {
    value: FeaturesEnum.ROAD,
    label: t('visualBigScreen.management-674210-561')
  },
  {
    value: FeaturesEnum.BUILDING,
    label: t('visualBigScreen.management-674210-562')
  }
])

const MarkerOptions = computed(() => [
  {
    value: MarkerEnum.CIRCLE_MARKER,
    label: t('visualBigScreen.management-674210-563')
  },
  {
    value: MarkerEnum.MARKER,
    label: t('visualBigScreen.management-674210-564')
  },
  {
    value: MarkerEnum.NONE,
    label: t('visualBigScreen.management-674210-565')
  }
])
</script>
