import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { PieCommonConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import i18n from '@/lang'

export const includes = ['legend']

export enum PieTypeEnum {
  NORMAL = 'normal',
  RING = 'ring',
  ROSE = 'rose'
}

export const PieTypeObject = {
  [PieTypeEnum.NORMAL]: 'nomal',
  [PieTypeEnum.RING]: 'ring',
  [PieTypeEnum.ROSE]: 'rose'
}

export const PieTypeLabelObject = {
  [PieTypeEnum.NORMAL]: i18n.global.t('visualBigScreen.management-674210-1024'),
  [PieTypeEnum.RING]: i18n.global.t('visualBigScreen.management-674210-1025'),
  [PieTypeEnum.ROSE]: i18n.global.t('visualBigScreen.management-674210-1026')
}

// 其它配置
const otherConfig = {
  // 轮播动画
  isCarousel: false
}

const option = {
  ...otherConfig,
  type: 'ring',
  tooltip: {
    show: true,
    trigger: 'item'
  },
  legend: {
    show: true
  },
  dataset: { ...dataJson },
  series: [
    {
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '60%'],
      roseType: false,
      avoidLabelOverlap: false,
      itemStyle: {
        show: true,
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center',
        formatter: '{b}',
        fontWeight: 'normal',
        fontSize: 14,
        color: '#454E54',
        textBorderColor: '#ffffff',
        textBorderWidth: 1
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '40',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      }
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key: string = PieCommonConfig.key

  public chartConfig = cloneDeep(PieCommonConfig)

  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
