import i18n from '@/lang'
import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { FunnelConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'

export const includes = ['legend']

export const FunnelOrderEnumList = [
  { label: i18n.global.t('visualBigScreen.management-674210-928'), value: 'descending' },
  { label: i18n.global.t('visualBigScreen.management-674210-929'), value: 'ascending' }
]
export const FunnelLabelPositionEnumList = [
  { label: i18n.global.t('visualBigScreen.management-674210-585'), value: 'inside' },
  { label: i18n.global.t('visualBigScreen.management-674210-927'), value: 'outside' },
  { label: i18n.global.t('visualBigScreen.management-674210-930'), value: 'insideLeft' },
  { label: i18n.global.t('visualBigScreen.management-674210-931'), value: 'insideRight' }
]

export const option = {
  tooltip: {},
  legend: {},
  dataset: { ...dataJson },
  series: [
    {
      name: 'Funnel',
      type: 'funnel',
      top: 70,
      left: '10%',
      width: '80%',
      min: 0,
      minSize: '0%',
      maxSize: '100%',
      sort: 'descending', // descending | ascending
      gap: 5,
      label: {
        show: true,
        position: 'inside',
        fontSize: 12
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 0
      },
      emphasis: {
        label: {
          fontSize: 20
        }
      }
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key: string = FunnelConfig.key
  public chartConfig = cloneDeep(FunnelConfig)

  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
