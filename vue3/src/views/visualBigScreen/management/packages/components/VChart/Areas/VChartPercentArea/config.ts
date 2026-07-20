import { PublicConfigClass } from '@vb/packages/public'
import { VChartPercentAreaConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import { vChartOptionPrefixHandle } from '@vb/packages/public/vChart'
import data from './data.json'
import cloneDeep from 'lodash/cloneDeep'
import axisThemeJson from '@vb/settings/vchartThemes/axis.theme.json'
import { IAreaOption } from '../../index.d'
import i18n from '@/lang'

export const includes = ['legends', 'tooltip']
export const option: IAreaOption & { dataset?: any } = {
  // 图表配置
  type: 'area',
  dataset: data,
  xField: 'type',
  yField: 'value',
  seriesField: 'country',
  stack: true,
  percent: true,
  // 业务配置（后续会被转换为图表spec)
  category: VChartPercentAreaConfig.category,
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
  public key = VChartPercentAreaConfig.key
  public chartConfig = cloneDeep(VChartPercentAreaConfig)
  // 图表配置项
  public option = vChartOptionPrefixHandle(option, includes)
}
