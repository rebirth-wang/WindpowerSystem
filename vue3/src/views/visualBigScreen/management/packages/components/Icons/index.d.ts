import i18n from '@/lang'

export enum ChatCategoryEnum {
  ML = 'MaterialLine',
  COMMON = 'Common',
  WEATHER = 'Weather',
  DEFAULT = 'Default' // 这个仅用来表示组件分类目录，不要在 index.ts 中导入
}

export enum ChatCategoryEnumName {
  ML = i18n.global.t('visualBigScreen.management-674210-158'),
  COMMON = i18n.global.t('visualBigScreen.management-674210-159'),
  WEATHER = i18n.global.t('visualBigScreen.management-674210-160'),
  DEFAULT = i18n.global.t('visualBigScreen.management-674210-161')
}
