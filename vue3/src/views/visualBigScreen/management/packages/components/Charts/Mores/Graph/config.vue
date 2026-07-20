<template>
  <div>
    <CollapseItem :name="t('visualBigScreen.management-674210-172')" :expanded="true">
      <SettingItemBox :name="t('visualBigScreen.management-674210-242')">
        <setting-item :name="t('visualBigScreen.management-674210-335')">
          <n-select v-model:value="graphConfig.layout" :options="GraphLayout" size="small" />
        </setting-item>
      </SettingItemBox>
      <SettingItemBox :name="t('visualBigScreen.management-674210-447')">
        <setting-item :name="t('visualBigScreen.management-674210-88')">
          <n-select v-model:value="graphConfig.label.show" :options="LabelSwitch" size="small" />
        </setting-item>
        <setting-item :name="t('visualBigScreen.management-674210-322')">
          <n-select v-model:value="graphConfig.label.position" :options="LabelPosition" size="small" />
        </setting-item>
      </SettingItemBox>
      <SettingItemBox :name="t('visualBigScreen.management-674210-295')">
        <SettingItem :name="t('visualBigScreen.management-674210-901')">
          <!-- 需要输入两位的小数才会变化 -->
          <n-input-number
            v-model:value="optionData.series[0].lineStyle.curveness"
            :min="0"
            :step="0.01"
            :placeholder="t('visualBigScreen.management-674210-901')"
            size="small"
          ></n-input-number>
        </SettingItem>
      </SettingItemBox>
      <SettingItemBox :name="t('visualBigScreen.management-674210-454')">
        <SettingItem :name="t('visualBigScreen.management-674210-129')">
          <n-color-picker
            size="small"
            :modes="['hex']"
            v-model:value="optionData.legend.textStyle.color"
          ></n-color-picker>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-149')">
          <n-input-number
            v-model:value="optionData.legend.textStyle.fontSize"
            :min="0"
            :step="1"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-926')"
          >
          </n-input-number>
        </SettingItem>
      </SettingItemBox>
      <SettingItemBox
        :name="t('visualBigScreen.management-674210-900')"
        v-if="optionData.series[0].force && graphConfig.layout == 'force'"
      >
        <SettingItem :name="t('visualBigScreen.management-674210-902')" v-if="optionData.series[0].force.repulsion">
          <n-input-number
            v-model:value="optionData.series[0].force.repulsion"
            :min="0"
            :step="1"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-903')"
          >
          </n-input-number>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-904')" v-if="optionData.series[0].force.gravity">
          <n-input-number
            v-model:value="optionData.series[0].force.gravity"
            :min="0"
            :step="0.1"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-904')"
          >
          </n-input-number>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-905')">
          <n-input-number
            v-model:value="optionData.series[0].force.edgeLength"
            :min="0"
            :step="1"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-905')"
          >
          </n-input-number>
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-941')">
          <n-select v-model:value="graphConfig.force.layoutAnimation" :options="LayoutAnimation" size="small" />
        </SettingItem>
        <SettingItem :name="t('visualBigScreen.management-674210-906')">
          <n-input-number
            v-model:value="optionData.series[0].force.friction"
            :min="0"
            :step="0.1"
            size="small"
            :placeholder="t('visualBigScreen.management-674210-906')"
          >
          </n-input-number>
        </SettingItem>
      </SettingItemBox>
    </CollapseItem>
  </div>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option, GraphLayout, LabelSwitch, LabelPosition, LayoutAnimation } from './config'
import { GlobalThemeJsonType } from '@vb/settings/chartThemes/index'

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option & GlobalThemeJsonType>,
    required: true
  }
})
const { t } = useI18n()

const graphConfig = computed<(typeof option.series)[0]>(() => {
  return props.optionData.series[0]
})
</script>
