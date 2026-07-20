<template>
  <n-divider style="margin: 10px 0"></n-divider>
  <n-space :size="8" justify="space-between" style="margin-top: 10px">
    <n-button secondary v-for="item in positionList" :key="item.key" @click="positonHandle(item.key)">
      <template #icon>
        <n-icon>
          <component :is="item.icon"></component>
        </n-icon>
      </template>
    </n-button>
  </n-space>
  <setting-item-box :name="t('visualBigScreen.management-674210-1083')">
    <n-input-number v-model:value="chartAttr.y" :min="0" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">{{ t('visualBigScreen.management-674210-1084') }}</n-text>
      </template>
    </n-input-number>
    <n-input-number v-model:value="chartAttr.x" :min="0" size="small" placeholder="px">
      <template #prefix>
        <n-text depth="3">{{ t('visualBigScreen.management-674210-1085') }}</n-text>
      </template>
    </n-input-number>
  </setting-item-box>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { PickCreateComponentType } from '@vb/packages/index.d'
import { SettingItemBox } from '@vb/components/Pages/ChartItemSetting'
import { icon } from '@vb/plugins/icon'
import { EditCanvasConfigType } from '@vb/store/modules/chartEditStore/chartEditStore.d'

const { t } = useI18n()

const {
  AlignHorizontalLeftIcon,
  AlignVerticalCenterIcon,
  AlignVerticalTopIcon,
  AlignHorizontalCenterIcon,
  AlignHorizontalRightIcon,
  AlignVerticalBottomIcon
} = icon.carbon

const positionList = [
  {
    key: 'AlignHorizontalLeftIcon',
    lable: t('visualBigScreen.management-674210-1086'),
    icon: AlignHorizontalLeftIcon
  },
  {
    key: 'AlignVerticalCenterIcon',
    lable: t('visualBigScreen.management-674210-1087'),
    icon: AlignVerticalCenterIcon
  },
  {
    key: 'AlignHorizontalRightIcon',
    lable: t('visualBigScreen.management-674210-1088'),
    icon: AlignHorizontalRightIcon
  },
  {
    key: 'AlignVerticalTopIcon',
    lable: t('visualBigScreen.management-674210-1089'),
    icon: AlignVerticalTopIcon
  },
  {
    key: 'AlignHorizontalCenterIcon',
    lable: t('visualBigScreen.management-674210-1090'),
    icon: AlignHorizontalCenterIcon
  },
  {
    key: 'AlignVerticalBottomIcon',
    lable: t('visualBigScreen.management-674210-1091'),
    icon: AlignVerticalBottomIcon
  }
]

const props = defineProps({
  canvasConfig: {
    type: Object as PropType<EditCanvasConfigType>,
    required: true
  },
  chartAttr: {
    type: Object as PropType<PickCreateComponentType<'attr'>>,
    required: true
  }
})

const positonHandle = (key: string) => {
  switch (key) {
    // 局左
    case positionList[0]['key']:
      props.chartAttr.x = 0
      break
    // X轴居中
    case positionList[1]['key']:
      props.chartAttr.y = (props.canvasConfig.height - props.chartAttr.h) / 2
      break
    // 局右
    case positionList[2]['key']:
      props.chartAttr.x = props.canvasConfig.width - props.chartAttr.w
      break
    // 顶部
    case positionList[3]['key']:
      props.chartAttr.y = 0
      break
    // Y轴居中
    case positionList[4]['key']:
      props.chartAttr.x = (props.canvasConfig.width - props.chartAttr.w) / 2
      break
    // 底部
    case positionList[5]['key']:
      props.chartAttr.y = props.canvasConfig.height - props.chartAttr.h
      break
  }
}
</script>
