import i18n from '@/lang'
import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { SankeyConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'

export const includes = ['legend']

export const orientList = [
  { label: i18n.global.t('visualBigScreen.management-674210-577'), value: 'horizontal' },
  { label: i18n.global.t('visualBigScreen.management-674210-935'), value: 'vertical' }
]

export const toolTipSwitch = [
  { label: i18n.global.t('visualBigScreen.management-674210-933'), value: 1 },
  { label: i18n.global.t('visualBigScreen.management-674210-934'), value: 0 }
]

export const option = {
  dataset: { ...dataJson },
  tooltip: {
    show: 1,
    trigger: 'item',
    triggerOn: 'mousemove'
  },
  series: {
    type: 'sankey',
    layout: 'none',
    orient: 'horizontal',
    data: dataJson.label,
    links: dataJson.links,
    levels: dataJson.levels
  }
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = SankeyConfig.key
  public chartConfig = cloneDeep(SankeyConfig)
  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
