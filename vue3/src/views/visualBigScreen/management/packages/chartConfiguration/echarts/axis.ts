import i18n from '@/lang'

export const axisConfig = {
  // X轴位置
  xposition: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-569'),
      value: 'top'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-570'),
      value: 'bottom'
    }
  ],
  // Y轴位置
  yposition: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-571'),
      value: 'left'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-572'),
      value: 'right'
    }
  ],
  // 线条
  splitLint: {
    lineStyle: {
      type: [
        {
          label: i18n.global.t('visualBigScreen.management-674210-573'),
          value: 'solid'
        },
        {
          label: i18n.global.t('visualBigScreen.management-674210-574'),
          value: 'dashed'
        },
        {
          label: i18n.global.t('visualBigScreen.management-674210-575'),
          value: 'dotted'
        }
      ]
    }
  },
  // 视觉映射
  visualMap: {
    orient: [
      {
        label: i18n.global.t('visualBigScreen.management-674210-576'),
        value: 'vertical'
      },
      {
        label: i18n.global.t('visualBigScreen.management-674210-577'),
        value: 'horizontal'
      }
    ]
  }
}
