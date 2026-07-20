<template>
  <collapse-item :name="t('visualBigScreen.management-674210-264')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-265')">
      <n-select v-model:value="optionData.isPanel" size="small" :options="panelOptions" />
    </setting-item-box>
  </collapse-item>

  <collapse-item :name="t('visualBigScreen.management-674210-266')" :expanded="true">
    <setting-item-box :name="t('visualBigScreen.management-674210-267')">
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select
          v-model:value="optionData.componentInteractEventKey"
          size="small"
          :options="datePickerTypeOptions"
          @update:value="datePickerTypeUpdate"
        />
      </setting-item>
    </setting-item-box>

    <setting-item-box :name="t('visualBigScreen.management-674210-269')">
      <setting-item :name="t('visualBigScreen.management-674210-268')">
        <n-select
          v-model:value="optionData.defaultType"
          size="small"
          :options="defaultTypeOptions"
          @update:value="defaultTypeUpdate"
        />
      </setting-item>
    </setting-item-box>
    <setting-item-box v-if="optionData.defaultType === DefaultTypeEnum.STATIC" :alone="true">
      <setting-item :name="t('visualBigScreen.management-674210-270')">
        <n-date-picker
          size="small"
          clearable
          v-model:value="optionData.dataset"
          :type="optionData.componentInteractEventKey"
        />
      </setting-item>
    </setting-item-box>
    <setting-item-box v-if="optionData.defaultType === DefaultTypeEnum.DYNAMIC">
      <template #name>
        <n-text></n-text>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-icon size="21" :depth="3">
              <help-outline-icon></help-outline-icon>
            </n-icon>
          </template>
          <span>{{ t('visualBigScreen.management-674210-271') }}</span>
        </n-tooltip>
      </template>
      <setting-item :name="differValueName">
        <n-input-number v-model:value="optionData.differValue[0]" class="input-num-width" size="small">
          <template #suffix>
            {{ DifferUnitObject[optionData.differUnit[0]] }}
          </template>
        </n-input-number>
      </setting-item>
      <setting-item :name="differUnitName">
        <n-select v-model:value="optionData.differUnit[0]" size="small" :options="differUnitOptions" />
      </setting-item>
      <setting-item v-if="isRange" :name="t('visualBigScreen.management-674210-272')">
        <n-input-number v-model:value="optionData.differValue[1]" class="input-num-width" size="small">
          <template #suffix>
            {{ DifferUnitObject[optionData.differUnit[1]] }}
          </template>
        </n-input-number>
      </setting-item>
      <setting-item v-if="isRange" :name="t('visualBigScreen.management-674210-273')">
        <n-select v-model:value="optionData.differUnit[1]" size="small" :options="differUnitOptions" />
      </setting-item>
    </setting-item-box>
  </collapse-item>
</template>

<script lang="ts" setup>
import { PropType, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'
import { CollapseItem, SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { option } from './config'
import { ComponentInteractEventEnum, DefaultTypeEnum, DifferUnitEnum, DifferUnitObject } from './interact'
import dayjs from 'dayjs'

const { HelpOutlineIcon } = icon.ionicons5
const { t } = useI18n()

const props = defineProps({
  optionData: {
    type: Object as PropType<typeof option>,
    required: true
  }
})

const panelOptions = [
  {
    label: t('visualBigScreen.management-674210-274'),
    value: 0
  },
  {
    label: t('visualBigScreen.management-674210-275'),
    value: 1
  }
]

const datePickerTypeOptions = [
  {
    label: t('visualBigScreen.management-674210-276'),
    value: ComponentInteractEventEnum.DATE
  },
  {
    label: t('visualBigScreen.management-674210-277'),
    value: ComponentInteractEventEnum.DATE_TIME
  },
  {
    label: t('visualBigScreen.management-674210-278'),
    value: ComponentInteractEventEnum.DATE_RANGE
  },
  {
    label: t('visualBigScreen.management-674210-279'),
    value: ComponentInteractEventEnum.MONTH
  },
  {
    label: t('visualBigScreen.management-674210-280'),
    value: ComponentInteractEventEnum.MONTH_RANGE
  },
  {
    label: t('visualBigScreen.management-674210-281'),
    value: ComponentInteractEventEnum.YEAR
  },
  {
    label: t('visualBigScreen.management-674210-282'),
    value: ComponentInteractEventEnum.YEAR_RANGE
  },
  {
    label: t('visualBigScreen.management-674210-283'),
    value: ComponentInteractEventEnum.QUARTER
  },
  {
    label: t('visualBigScreen.management-674210-284'),
    value: ComponentInteractEventEnum.QUARTER_RANGE
  }
]

const defaultTypeOptions = [
  {
    label: t('visualBigScreen.management-674210-285'),
    value: DefaultTypeEnum.STATIC
  },
  {
    label: t('visualBigScreen.management-674210-286'),
    value: DefaultTypeEnum.DYNAMIC
  },
  {
    label: t('visualBigScreen.management-674210-287'),
    value: DefaultTypeEnum.NONE
  }
]

const differUnitOptions = [
  // ManipulateType
  {
    value: DifferUnitEnum.DAY,
    label: DifferUnitObject[DifferUnitEnum.DAY]
  },
  {
    value: DifferUnitEnum.WEEK,
    label: DifferUnitObject[DifferUnitEnum.WEEK]
  },
  {
    value: DifferUnitEnum.MONTH,
    label: DifferUnitObject[DifferUnitEnum.MONTH]
  },
  {
    value: DifferUnitEnum.QUARTER,
    label: DifferUnitObject[DifferUnitEnum.QUARTER]
  },
  {
    value: DifferUnitEnum.YEAR,
    label: DifferUnitObject[DifferUnitEnum.YEAR]
  },
  {
    value: DifferUnitEnum.HOUR,
    label: DifferUnitObject[DifferUnitEnum.HOUR]
  },
  {
    value: DifferUnitEnum.MINUTE,
    label: DifferUnitObject[DifferUnitEnum.MINUTE]
  },
  {
    value: DifferUnitEnum.SECOND,
    label: DifferUnitObject[DifferUnitEnum.SECOND]
  },
  {
    value: DifferUnitEnum.MILLISECOND,
    label: DifferUnitObject[DifferUnitEnum.MILLISECOND]
  }
]

const isRange = computed(() => {
  return props.optionData.componentInteractEventKey.endsWith('range')
})

const differValueName = computed(() => {
  return isRange.value ? t('visualBigScreen.management-674210-288') : t('visualBigScreen.management-674210-289')
})

const differUnitName = computed(() => {
  return isRange.value ? t('visualBigScreen.management-674210-290') : t('visualBigScreen.management-674210-291')
})

const datePickerTypeUpdate = () => {
  props.optionData.dataset = isRange.value ? [dayjs().valueOf(), dayjs().valueOf()] : dayjs().valueOf()
}

const defaultTypeUpdate = (v: string) => {
  if (v === DefaultTypeEnum.STATIC) {
    datePickerTypeUpdate()
  } else {
    // DefaultTypeEnum.
    props.optionData.dataset = null
  }
}
</script>
