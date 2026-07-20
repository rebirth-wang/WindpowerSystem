import i18n from '@/lang'
import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { WaterPoloConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'

export const shapes = [
  {
    label: i18n.global.t('visualBigScreen.management-674210-416'),
    value: 'circle'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-942'),
    value: 'rect'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-943'),
    value: 'roundRect'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-929'),
    value: 'triangle'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-944'),
    value: 'diamond'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-945'),
    value: 'pin'
  },
  {
    label: i18n.global.t('visualBigScreen.management-674210-324'),
    value: 'arrow'
  }
]

export const includes = []

export const option = {
  dataset: 0.5,
  series: [
    {
      type: 'liquidFill',
      shape: shapes[0].value,
      radius: '90%',
      data: [0],
      center: ['50%', '50%'],
      color: [
        {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: '#446bf5'
            },
            {
              offset: 1,
              color: '#2ca3e2'
            }
          ],
          globalCoord: false
        }
      ],
      backgroundStyle: {
        borderWidth: 1,
        color: 'rgba(51, 66, 127, 0.7)'
      },
      label: {
        fontSize: 50,
        color: '#fff'
      },
      outline: {
        show: false,
        borderDistance: 10,
        itemStyle: {
          borderWidth: 2,
          borderColor: '#112165'
        }
      }
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = WaterPoloConfig.key
  public chartConfig = cloneDeep(WaterPoloConfig)
  public option = echartOptionProfixHandle(option, includes)
}
