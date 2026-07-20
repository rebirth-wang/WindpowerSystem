import i18n from '@/lang'
import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { RadarConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'

export const includes = ['legend']

export const RadarShapeEnumList = [
  { label: i18n.global.t('visualBigScreen.management-674210-915'), value: 'polygon' },
  { label: i18n.global.t('visualBigScreen.management-674210-416'), value: 'circle' }
]

export const option = {
  tooltip: {
    show: true
  },
  legend: {
    data: dataJson.seriesData.map(i => i.name)
  },
  dataset: { ...dataJson },
  radar: {
    shape: 'polygon',
    radius: ['0%', '60%'],
    center: ['50%', '55%'],
    splitArea: { show: true },
    splitLine: { show: true },
    axisName: { show: true, color: '#eee', fontSize: 12 },
    axisLine: { show: true },
    axisTick: { show: true },
    indicator: dataJson.radarIndicator
  },
  series: [
    {
      name: 'radar',
      type: 'radar',
      areaStyle: {
        opacity: 0.1
      },
      data: dataJson.seriesData
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = RadarConfig.key
  public chartConfig = cloneDeep(RadarConfig)
  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
