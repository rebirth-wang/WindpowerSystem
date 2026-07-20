<template>
  <!-- 全局配置 -->
  <n-card class="n-card-shallow">
    <n-tag type="info" :bordered="false" style="border-radius: 5px">
      {{ t('visualBigScreen.management-674210-852') }}
    </n-tag>
    <setting-item-box
      :name="t('visualBigScreen.management-674210-853')"
      :itemRightStyle="{
        gridTemplateColumns: '5fr 2fr 1fr'
      }"
    >
      <!-- 源地址 -->
      <setting-item :name="t('visualBigScreen.management-674210-854')">
        <n-input
          v-model:value.trim="requestOriginUrl"
          :disabled="editDisabled"
          :placeholder="t('visualBigScreen.management-674210-856')"
        ></n-input>
      </setting-item>
      <setting-item :name="t('visualBigScreen.management-674210-855')">
        <n-input-group>
          <n-input-number
            class="select-time-number"
            v-model:value.trim="requestInterval"
            min="0"
            :show-button="false"
            :disabled="editDisabled"
            :placeholder="t('visualBigScreen.management-674210-857')"
          >
          </n-input-number>
          <!-- 单位 -->
          <n-select
            class="select-time-options"
            v-model:value="requestIntervalUnit"
            :options="selectTimeOptions"
            :disabled="editDisabled"
          />
        </n-input-group>
      </setting-item>
      <n-button v-show="editDisabled" type="primary" ghost @click="editDisabled = false">
        <template #icon>
          <n-icon>
            <pencil-icon />
          </n-icon>
        </template>
        {{ t('visualBigScreen.management-674210-797') }}
      </n-button>
    </setting-item-box>
    <!-- table 内容体 -->
    <n-collapse-transition :show="showTable">
      <request-global-header-table :editDisabled="editDisabled"></request-global-header-table>
    </n-collapse-transition>
    <!-- 箭头 -->
    <div v-if="showTable" class="go-flex-center go-mt-3 down" @click="showTable = false">
      <n-icon size="32">
        <chevron-up-outline-icon />
      </n-icon>
    </div>
    <div v-else class="go-flex-center go-mt-3 down" @click="showTable = true">
      <n-tooltip trigger="hover" placement="top" :keep-alive-on-hover="false">
        <template #trigger>
          <n-icon size="32">
            <chevron-down-outline-icon />
          </n-icon>
        </template>
        {{ t('visualBigScreen.management-674210-858') }}
      </n-tooltip>
    </div>
  </n-card>
</template>

<script setup lang="ts">
import { ref, toRefs, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'
import { SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { useTargetData } from '@vb/views/chart/ContentConfigurations/components/hooks/useTargetData.hook'
import { selectTimeOptions } from '@vb/views/chart/ContentConfigurations/components/ChartData/index.d'
import { RequestGlobalHeaderTable } from '../RequestGlobalHeaderTable'
import { icon } from '@vb/plugins/icon'

const { PencilIcon, ChevronDownOutlineIcon, ChevronUpOutlineIcon } = icon.ionicons5
const { chartEditStore } = useTargetData()
const { t } = useI18n()
const { requestOriginUrl, requestInterval, requestIntervalUnit } = toRefs(chartEditStore.getRequestGlobalConfig)
const editDisabled = ref(true)

const designStore = useDesignStore()

const showTable = ref(false)
// 颜色
const themeColor = computed(() => {
  return designStore.getAppTheme
})
</script>

<style lang="scss" scoped>
.n-card-shallow {
  &:hover {
    border-color: v-bind('themeColor');
  }
}
.down {
  cursor: pointer;
  &:hover {
    color: v-bind('themeColor');
  }
}
.select-time-number {
  width: 100%;
}
.select-time-options {
  width: 100px;
}
</style>
