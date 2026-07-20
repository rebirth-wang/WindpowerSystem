import i18n from '@/lang'

const t = i18n.global.t

export const legendsConfig = {
  // 位置
  orient: [
    {
      label: t('visualBigScreen.management-674210-1690'),
      value: 'top'
    },
    {
      label: t('visualBigScreen.management-674210-1691'),
      value: 'bottom'
    },
    {
      label: t('visualBigScreen.management-674210-1692'),
      value: 'left'
    },
    {
      label: t('visualBigScreen.management-674210-1693'),
      value: 'right'
    }
  ],
  // 对齐方式
  position: [
    {
      label: t('visualBigScreen.management-674210-1694'),
      value: 'start'
    },
    {
      label: t('visualBigScreen.management-674210-376'),
      value: 'middle'
    },
    {
      label: t('visualBigScreen.management-674210-1695'),
      value: 'end'
    }
  ],
  // 每一项的图例位置
  align: [
    {
      label: t('visualBigScreen.management-674210-1696'),
      value: 'left'
    },
    {
      label: t('visualBigScreen.management-674210-1697'),
      value: 'right'
    }
  ]
}

export const fontStyleConfig = {
  // 字重
  fontWeight: [
    {
      label: '100',
      value: 100
    },
    {
      label: '200',
      value: 200
    },
    {
      label: '300',
      value: 300
    },
    {
      label: '400',
      value: 400
    },
    {
      label: '500',
      value: 500
    },
    {
      label: '600',
      value: 600
    },
    {
      label: t('visualBigScreen.management-674210-587'),
      value: "normal"
    },
    {
      label: t('visualBigScreen.management-674210-415'),
      value: "bold"
    }
  ],
  fontFamily: [
    {
      label: t('visualBigScreen.management-674210-1698'),
      value: 'SimSun'
    },
    {
      label: t('visualBigScreen.management-674210-1699'),
      value: 'SimHei'
    },
    {
      label: t('visualBigScreen.management-674210-1700'),
      value: '楷体'
    }
  ]
}
