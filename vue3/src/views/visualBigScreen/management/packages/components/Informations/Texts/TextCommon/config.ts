import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { TextCommonConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import i18n from '@/lang'

export enum WritingModeEnum {
  HORIZONTAL = 'horizontal',
  VERTICAL = 'vertical'
}

export const WritingModeObject = {
  [WritingModeEnum.HORIZONTAL]: 'horizontal-tb',
  [WritingModeEnum.VERTICAL]: 'vertical-rl'
}

export const WritingModeLabelObject = {
  [WritingModeEnum.HORIZONTAL]: i18n.global.t('visualBigScreen.management-674210-327'),
  [WritingModeEnum.VERTICAL]: i18n.global.t('visualBigScreen.management-674210-328')
}

export enum FontWeightEnum {
  NORMAL = 'normal',
  BOLD = 'bold'
}

export const FontWeightObject = {
  [FontWeightEnum.NORMAL]: FontWeightEnum.NORMAL,
  [FontWeightEnum.BOLD]: FontWeightEnum.BOLD
}

export const FontWeightLabelObject = {
  [FontWeightEnum.NORMAL]: i18n.global.t('visualBigScreen.management-674210-414'),
  [FontWeightEnum.BOLD]: i18n.global.t('visualBigScreen.management-674210-415')
}

const createOption = () => ({
  link: '',
  linkHead: 'http://',
  dataset: i18n.global.t('visualBigScreen.management-674210-393'),
  fontSize: 20,
  fontColor: '#ffffff',
  paddingX: 10,
  paddingY: 10,
  textAlign: 'center', // 水平对齐方式
  fontWeight: 'normal',

  // 边框
  borderWidth: 0,
  borderColor: '#ffffff',
  borderRadius: 5,

  // 字间距
  letterSpacing: 5,
  writingMode: 'horizontal-tb',
  backgroundColor: '#00000000'
})

export const option = createOption()

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TextCommonConfig.key
  public chartConfig = cloneDeep(TextCommonConfig)
  public option = createOption()
}
