import i18n from '@/lang'
import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { GraphConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'

export const includes = []

export const GraphLayout = [
  { label: i18n.global.t('visualBigScreen.management-674210-287'), value: 'none' },
  { label: i18n.global.t('visualBigScreen.management-674210-932'), value: 'circular' },
  { label: i18n.global.t('visualBigScreen.management-674210-900'), value: 'force' }
]

export const LabelSwitch = [
  { label: i18n.global.t('visualBigScreen.management-674210-933'), value: 1 },
  { label: i18n.global.t('visualBigScreen.management-674210-934'), value: 0 }
]

export const LabelPosition = [
  { label: i18n.global.t('visualBigScreen.management-674210-571'), value: 'left' },
  { label: i18n.global.t('visualBigScreen.management-674210-572'), value: 'right' },
  { label: i18n.global.t('visualBigScreen.management-674210-569'), value: 'top' },
  { label: i18n.global.t('visualBigScreen.management-674210-570'), value: 'bottom' },
  { label: i18n.global.t('visualBigScreen.management-674210-585'), value: 'inside' }
]

export const LayoutAnimation = [
  { label: i18n.global.t('visualBigScreen.management-674210-933'), value: 1 },
  { label: i18n.global.t('visualBigScreen.management-674210-934'), value: 0 }
]

export const option = {
  dataset: { ...dataJson },
  tooltip: {},
  legend: {
    show: true,
    textStyle: {
      color: '#eee',
      fontSize: 14
    },
    data: dataJson.categories.map(function (a) {
      return a.name
    })
  },
  series: [
    {
      type: 'graph',
      layout: 'none', // none circular环形布局
      data: dataJson.nodes,
      links: dataJson.links,
      categories: dataJson.categories,
      label: {
        show: 1,
        position: 'right',
        formatter: '{b}'
      },
      labelLayout: {
        hideOverlap: true
      },
      lineStyle: {
        color: 'source', // 线条颜色
        curveness: 0.2 // 线条卷曲程度
      },
      force: {
        repulsion: 100,
        gravity: 0.1,
        edgeLength: 30,
        layoutAnimation: 1,
        friction: 0.6
      }
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = GraphConfig.key
  public chartConfig = cloneDeep(GraphConfig)
  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
