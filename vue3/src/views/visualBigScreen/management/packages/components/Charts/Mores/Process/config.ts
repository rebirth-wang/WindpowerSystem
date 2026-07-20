import i18n from '@/lang'
import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { ProcessConfig } from './index'
import { chartInitConfig } from '@vb/settings/designSetting'
import cloneDeep from 'lodash/cloneDeep'

export const types = [
  {
    label: i18n.global.t('visualBigScreen.management-674210-936'),
    value: 'line'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-416'),
    value: 'circle'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-937'),
    value: 'dashboard'
  }
]

export const indicatorPlacements = [
  {
    label: i18n.global.t('visualBigScreen.management-674210-585'),
    value: 'inside'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-927'),
    value: 'outside'
  }
]

export const option = {
  dataset: 36,
  // 默认类型
  type: types[2].value,
  // 进行时效果
  processing: true,
  // 主颜色
  color: '#4992FFFF',
  // 轨道颜色
  railColor: '#3e3e3f',
  // 指标
  unit: '%',
  // 指标大小
  indicatorTextSize: 34,
  // 指标位置（线条时可用）
  indicatorPlacement: 'outside',
  // 指标颜色
  indicatorTextColor: '#FFFFFFFF',
  // 偏移角度
  offsetDegree: 0
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = ProcessConfig.key
  public attr = { ...chartInitConfig, h: 500, zIndex: -1 }
  public chartConfig = cloneDeep(ProcessConfig)
  public option = cloneDeep(option)
}
