<template>
  <div>
    <n-input
      :style="`width:${w}px;`"
      type="text"
      v-model:value="option.value.dataset"
      :placeholder="t('visualBigScreen.management-674210-299')"
      @change="onChange"
    >
    </n-input>
  </div>
</template>

<script lang="ts" setup>
import { PropType, toRefs, shallowReactive, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { CreateComponentType } from '@vb/packages/index.d'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { useChartInteract } from '@vb/hooks'
import { InteractEventOn } from '@vb/enums/eventEnum'
import { ComponentInteractParamsEnum } from './interact'

const { t } = useI18n()

const props = defineProps({
  chartConfig: {
    type: Object as PropType<CreateComponentType>,
    required: true
  }
})

const { w, h } = toRefs(props.chartConfig.attr)
const option = shallowReactive({
  value: {
    inputValue: props.chartConfig.option.inputValue,
    dataset: props.chartConfig.option.dataset
  }
})

const onChange = (v: string) => {
  if (v == undefined) return
  // 存储到联动数据
  useChartInteract(
    props.chartConfig,
    useChartEditStore,
    { [ComponentInteractParamsEnum.DATA]: v },
    InteractEventOn.CHANGE
  )
}

// 手动更新
watch(
  () => props.chartConfig.option,
  (newData: any) => {
    option.value = newData
    onChange(newData.inputValue)
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
