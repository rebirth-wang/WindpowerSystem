import i18n from '@/lang'

export enum ChatCategoryEnum {
  TEXT = 'Texts',
  TITLE = 'Titles',
  INPUTS = 'Inputs',
  MORE = 'Mores'
}

export enum ChatCategoryEnumName {
  TEXT = i18n.global.t('visualBigScreen.management-674210-149'),
  TITLE = i18n.global.t('visualBigScreen.management-674210-150'),
  // 控件 => 数据录入
  INPUTS = i18n.global.t('visualBigScreen.management-674210-151'),
  MORE = i18n.global.t('visualBigScreen.management-674210-5')
}
