<template>
  <!-- 组件配置 -->
  <n-divider class="go-my-3" title-placement="left"></n-divider>
  <setting-item-box
    :itemRightStyle="{
      gridTemplateColumns: '6fr 2fr'
    }"
    style="padding-right: 25px"
  >
    <template #name>
      {{ t('visualBigScreen.management-674210-844') }}
      <n-tooltip trigger="hover" v-if="isDev()">
        <template #trigger>
          <n-icon size="21" :depth="3">
            <help-outline-icon></help-outline-icon>
          </n-icon>
        </template>
        <ul class="go-pl-0">
          {{
            t('visualBigScreen.management-674210-859')
          }}
          <li v-for="item in apiList" :key="item.value">
            <n-text type="info"> {{ item.value }} </n-text>
          </li>
        </ul>
      </n-tooltip>
    </template>
    <setting-item :name="t('visualBigScreen.management-674210-860')">
      <n-input-group>
        <n-select class="select-type-options" v-model:value="requestHttpType" :options="selectTypeOptions" />
        <n-input v-model:value.trim="requestUrl" :min="1" :placeholder="t('visualBigScreen.management-674210-861')">
          <template #prefix>
            <n-text>{{ requestOriginUrl }}</n-text>
            <n-divider vertical />
          </template>
        </n-input>
      </n-input-group>
      <!-- 组件url -->
    </setting-item>
    <setting-item :name="t('visualBigScreen.management-674210-855')">
      <n-input-group>
        <n-input-number
          v-model:value.trim="requestInterval"
          class="select-time-number"
          min="0"
          :show-button="false"
          :placeholder="t('visualBigScreen.management-674210-862')"
        >
        </n-input-number>
        <!-- 单位 -->
        <n-select class="select-time-options" v-model:value="requestIntervalUnit" :options="selectTimeOptions" />
      </n-input-group>
    </setting-item>
  </setting-item-box>
  <setting-item-box :name="t('visualBigScreen.management-674210-265')" class="go-mt-0">
    <request-header :targetDataRequest="targetDataRequest"></request-header>
  </setting-item-box>
</template>

<script setup lang="ts">
import { computed, PropType, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import { SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { useTargetData } from '@vb/views/chart/ContentConfigurations/components/hooks/useTargetData.hook'
import {
  selectTypeOptions,
  selectTimeOptions
} from '@vb/views/chart/ContentConfigurations/components/ChartData/index.d'
import { RequestConfigType } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import { RequestHeader } from '../RequestHeader'
import { isDev } from '@vb/utils'
import { icon } from '@vb/plugins/icon'
import {
  graphUrl,
  chartDataUrl,
  chartSingleDataUrl,
  rankListUrl,
  scrollBoardUrl,
  numberFloatUrl,
  numberIntUrl,
  textUrl,
  imageUrl,
  radarUrl,
  heatMapUrl,
  scatterBasicUrl,
  mapUrl,
  capsuleUrl,
  wordCloudUrl,
  treemapUrl,
  threeEarth01Url,
  sankeyUrl,
  vchartBarDataUrl
} from '@vb/api/mock'

const props = defineProps({
  targetDataRequest: Object as PropType<RequestConfigType>
})

const { HelpOutlineIcon } = icon.ionicons5
const { chartEditStore } = useTargetData()
const { t } = useI18n()
const { requestOriginUrl } = toRefs(chartEditStore.getRequestGlobalConfig)
const { requestInterval, requestIntervalUnit, requestHttpType, requestUrl } = toRefs(
  props.targetDataRequest as RequestConfigType
)

const apiList = computed(() => [
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-133'), chartDataUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-863'), chartSingleDataUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-149'), textUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-864'), numberIntUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-865'), numberFloatUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-866'), imageUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-867'), rankListUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-868'), scrollBoardUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-869'), radarUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-173'), heatMapUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-870'), scatterBasicUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-871'), mapUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-872'), capsuleUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-222'), wordCloudUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-873'), treemapUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-200'), threeEarth01Url])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-177'), sankeyUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-172'), graphUrl])
  },
  {
    value: t('visualBigScreen.management-674210-875', [t('visualBigScreen.management-674210-874'), vchartBarDataUrl])
  }
])
</script>

<style lang="scss" scoped>
.select-time-number {
  width: 100%;
}
.select-time-options {
  width: 100px;
}
.select-type-options {
  width: 120px;
}
</style>
