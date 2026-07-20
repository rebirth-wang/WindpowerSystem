import { PublicConfigClass } from '@vb/packages/public'
import { VChartBarCommonConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import { vChartOptionPrefixHandle } from '@vb/packages/public/vChart'
import data from './data.json'
import cloneDeep from 'lodash/cloneDeep'
import axisThemeJson from '@vb/settings/vchartThemes/axis.theme.json'
import { IBarOption } from '../../index.d'
import i18n from '@/lang'

export const includes = ['legends', 'tooltip']
export const option: IBarOption & { dataset?: any } = {
  // 图表配置
  type: 'bar',
  dataset: data,
  stack: true,
  xField: ['year', 'type'],
  yField: ['value'],
  seriesField: 'type',
  // 业务配置（后续会被转换为图表spec)
  category: VChartBarCommonConfig.category,
  xAxis: {
    name: i18n.global.t('visualBigScreen.management-674210-1056'),
    ...axisThemeJson,
    grid: {
      ...axisThemeJson.grid,
      visible: false
    }
  },
  yAxis: {
    name: i18n.global.t('visualBigScreen.management-674210-1057'),
    ...axisThemeJson,
    grid: {
      ...axisThemeJson.grid,
      style: {
        ...axisThemeJson.grid.style,
        lineDash: [3, 3]
      }
    }
  }
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = VChartBarCommonConfig.key
  public chartConfig = cloneDeep(VChartBarCommonConfig)
  // 图表配置项
  public option = vChartOptionPrefixHandle(option, includes)
}
