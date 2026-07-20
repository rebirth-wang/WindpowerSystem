<template>
  <n-grid :x-gap="8" :y-gap="8" :cols="2">
    <n-gi v-for="item in list" :key="item.value">
      <div
        class="theme-item"
        :class="{ active: item.value === vChartThemeName }"
        size="small"
        @click="selectThemeHandle(item)"
      >
        <n-ellipsis class="go-mr-1" style="text-align: left">{{ item.name }} </n-ellipsis>
        <n-space :wrap="false" :wrap-item="false" :size="2">
          <span
            class="theme-color-item"
            v-for="colorItem in item.colors"
            :key="colorItem"
            :style="{ backgroundColor: colorItem }"
          ></span>
        </n-space>
      </div>
    </n-gi>
  </n-grid>
  <div class="go-my-4">{{ t('visualBigScreen.management-674210-755') }}</div>
  <n-grid :x-gap="8" :y-gap="8" :cols="2">
    <n-gi v-for="item in industryList" :key="item.value">
      <div
        class="theme-item"
        :class="{ active: item.value === vChartThemeName }"
        size="small"
        @click="selectThemeHandle(item)"
      >
        <n-ellipsis class="go-mr-2" style="text-align: left">{{ item.name }} </n-ellipsis>
        <n-space :wrap="false" :wrap-item="false" :size="2">
          <span
            class="theme-color-item"
            v-for="colorItem in item.colors"
            :key="colorItem"
            :style="{ backgroundColor: colorItem }"
          ></span>
        </n-space>
      </div>
    </n-gi>
  </n-grid>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useVCharts } from '@vb/hooks'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { EditCanvasConfigEnum } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import { useDesignStore } from '@vb/store/modules/designStore/designStore'

const chartEditStore = useChartEditStore()
const designStore = useDesignStore()
const vCharts = useVCharts()
const themeMap = vCharts.getThemeMap()
const { t } = useI18n()

type ThemeOption = {
  name: string
  value: keyof typeof themeMap
  colors: string[]
}

type ThemeSource = Omit<ThemeOption, 'name'> & {
  nameKey: string
}

const vChartThemeName = computed(() => {
  return chartEditStore.getEditCanvasConfig.vChartThemeName
})

// 颜色
const themeColor = computed(() => {
  return designStore.getAppTheme
})

const themeDefinitions: ThemeSource[] = [
  {
    nameKey: 'visualBigScreen.management-674210-756',
    value: 'vScreenVolcanoBlue',
    colors: ['#2D64DD', '#284588', '#58B4B6']
  },
  {
    nameKey: 'visualBigScreen.management-674210-757',
    value: 'vScreenPartyRed',
    colors: ['#d3d3d4', '#d68a46', '#d74f3c']
  },
  {
    nameKey: 'visualBigScreen.management-674210-758',
    value: 'vScreenClean',
    colors: ['#94AF60', '#7696B8', '#d6837a']
  },
  {
    nameKey: 'visualBigScreen.management-674210-759',
    value: 'vScreenOutskirts',
    colors: ['#A7C4E6', '#e1bf99', '#c0bcbb']
  },
  {
    nameKey: 'visualBigScreen.management-674210-760',
    value: 'vScreenBlueOrange',
    colors: ['#acd5fa', '#cc896b', '#5ea4dd']
  },
  {
    nameKey: 'visualBigScreen.management-674210-761',
    value: 'vScreenFinanceYellow',
    colors: ['#d7d7d7', '#f09761', '#f7d177']
  },
  {
    nameKey: 'visualBigScreen.management-674210-762',
    value: 'vScreenWenLvCyan',
    colors: ['#63c6ba', '#dcb974', '#a34440']
  },
  {
    nameKey: 'visualBigScreen.management-674210-763',
    value: 'vScreenElectricGreen',
    colors: ['#75faf2', '#ee813e', '#f4ce7f']
  },
  {
    nameKey: 'visualBigScreen.management-674210-764',
    value: 'vScreenECommercePurple',
    colors: ['#6d4cf6', '#ed7266', '#5f83f7']
  },
  {
    nameKey: 'visualBigScreen.management-674210-765',
    value: 'vScreenRedBlue',
    colors: ['#2e6cf6', '#bc4741', '#c1e4fb']
  }
]

// 行业色版列表
const industryThemeDefinitions: ThemeSource[] = [
  {
    nameKey: 'visualBigScreen.management-674210-766',
    value: 'light',
    colors: ['#3063f6', '#5dc3f9', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-767',
    value: 'dark',
    colors: ['#3063f6', '#5dc3f9', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-768',
    value: 'veODesignLightFinance',
    colors: ['#dbba95', '#314b5e', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-769',
    value: 'veODesignDarkFinance',
    colors: ['#dbba95', '#314b5e', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-770',
    value: 'veODesignLightGovernment',
    colors: ['#c0403a', '#f6c552', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-771',
    value: 'veODesignDarkGovernment',
    colors: ['#c0403a', '#f6c552', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-772',
    value: 'veODesignLightConsumer',
    colors: ['#3f36ab', '#eb4854', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-773',
    value: 'veODesignDarkConsumer',
    colors: ['#3f36ab', '#eb4854', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-774',
    value: 'veODesignLightAutomobile',
    colors: ['#1515d1', '#abb6cd', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-775',
    value: 'veODesignDarkAutomobile',
    colors: ['#1515d1', '#abb6cd', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-776',
    value: 'veODesignLightCulturalTourism',
    colors: ['#77b897', '#3c5a4b', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-777',
    value: 'veODesignDarkCulturalTourism',
    colors: ['#77b897', '#3c5a4b', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-778',
    value: 'veODesignLightMedical',
    colors: ['#76d0d1', '#314787', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-779',
    value: 'veODesignDarkMedical',
    colors: ['#76d0d1', '#314787', '#414348']
  },
  {
    nameKey: 'visualBigScreen.management-674210-780',
    value: 'veODesignLightNewEnergy',
    colors: ['#64d886', '#1f3b76', '#f1f2f5']
  },
  {
    nameKey: 'visualBigScreen.management-674210-781',
    value: 'veODesignDarkNewEnergy',
    colors: ['#64d886', '#1f3b76', '#414348']
  }
]

const buildThemeList = (source: ThemeSource[]): ThemeOption[] => {
  return source.map(({ nameKey, ...item }) => ({
    ...item,
    name: t(nameKey)
  }))
}

const list = computed(() => buildThemeList(themeDefinitions))
const industryList = computed(() => buildThemeList(industryThemeDefinitions))

const selectThemeHandle = (item: { name: string; value: keyof typeof themeMap; colors: string[] }) => {
  vCharts.setTheme(item.value)
  chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.VCHART_THEME_NAME, item.value)
}
</script>

<style lang="scss" scoped>
$radius: 6px;
$itemRadius: 2px;
.theme-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 34px;
  padding: 0 8px;
  overflow: hidden;
  cursor: pointer;
  font-size: 13px;
  border-radius: $radius;
  @include fetch-bg-color('background-color4-shallow');
  &.active {
    color: v-bind('themeColor');
  }
  .theme-color-item {
    display: inline-block;
    width: 16px;
    height: 16px;
    border-radius: $itemRadius;
  }
}
</style>
