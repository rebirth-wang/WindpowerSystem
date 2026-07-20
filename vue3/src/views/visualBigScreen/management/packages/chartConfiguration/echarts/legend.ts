import i18n from '@/lang'

export const legendConfig = {
  // X轴位置
  lengendX: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-375'),
      value: 'left'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-376'),
      value: 'center'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-377'),
      value: 'right'
    }
  ],
  // y轴位置
  lengendY: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-578'),
      value: 'top'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-376'),
      value: 'center'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-579'),
      value: 'bottom'
    }
  ],
  // 排列方向
  orient: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-577'),
      value: 'horizontal'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-576'),
      value: 'vertical'
    }
  ],
  // 形状
  shape: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-416'),
      value: 'circle'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-580'),
      value: 'rect'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-581'),
      value: 'roundRect'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-420'),
      value: 'triangle'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-582'),
      value: 'pin'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-583'),
      value: 'arrow'
    }
  ]
}
