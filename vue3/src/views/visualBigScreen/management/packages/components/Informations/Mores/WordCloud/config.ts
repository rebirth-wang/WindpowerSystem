import { echartOptionProfixHandle, PublicConfigClass } from '@vb/packages/public'
import { WordCloudConfig } from './index'
import { CreateComponentType } from '@vb/packages/index.d'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import i18n from '@/lang'

export const includes = []

export const ShapeEnumList = [
  { label: i18n.global.t('visualBigScreen.management-674210-416'), value: 'circle' },
  { label: i18n.global.t('visualBigScreen.management-674210-417'), value: 'cardioid' },
  { label: i18n.global.t('visualBigScreen.management-674210-418'), value: 'diamond' },
  { label: i18n.global.t('visualBigScreen.management-674210-419'), value: 'triangle-forward' },
  { label: i18n.global.t('visualBigScreen.management-674210-420'), value: 'triangle' },
  { label: i18n.global.t('visualBigScreen.management-674210-421'), value: 'pentagon' },
  { label: i18n.global.t('visualBigScreen.management-674210-422'), value: 'star' }
]

export const option = {
  dataset: [...dataJson],
  tooltip: {},
  series: [
    {
      type: 'wordCloud',

      // “云”绘制的形状，可以是表示为回调函数，也可以是固定关键字。
      // 可用值有：circle|cardioid|diamond|triangle-forward|triangle|pentagon|star
      shape: 'circle',

      // 白色区域将被排除在绘制文本之外的剪影图像。
      // 随着云的形状生长，形状选项将继续应用。
      // maskImage: maskImage,

      // Folllowing left/top/width/height/right/bottom are used for positioning the word cloud
      // Default to be put in the center and has 75% x 80% size.
      left: 'center',
      top: 'center',
      width: '70%',
      height: '80%',
      right: null,
      bottom: null,

      // 文本大小范围，默认 [12,60]
      sizeRange: [12, 60],

      // 文本旋转范围和程度的步骤。 文本将通过旋转步骤45在[-90，90]中随机旋转
      rotationRange: [0, 0],
      rotationStep: 0,

      // size of the grid in pixels for marking the availability of the canvas
      // 网格大小越大，单词之间的差距就越大。
      gridSize: 8,

      // 设置为true，以允许单词在画布之外部分地绘制。允许绘制大于画布的大小
      drawOutOfBound: false,

      // If perform layout animation.
      // NOTE disable it will lead to UI blocking when there is lots of words.
      layoutAnimation: true,

      // Global text style
      textStyle: {
        fontFamily: 'sans-serif',
        fontWeight: 'bold'
        // 颜色可以是回调功能或颜色字符串
        // color: function () {
        //   // 随机颜色
        //   return (
        //     'rgb(' +
        //     [Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(
        //       ','
        //     ) +
        //     ')'
        //   )
        // }
      },
      emphasis: {
        focus: 'self',

        textStyle: {
          shadowBlur: 10,
          shadowColor: '#333'
        }
      },
      data: [...dataJson]
    }
  ]
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = WordCloudConfig.key
  public chartConfig = cloneDeep(WordCloudConfig)
  // 图表配置项
  public option = echartOptionProfixHandle(option, includes)
}
