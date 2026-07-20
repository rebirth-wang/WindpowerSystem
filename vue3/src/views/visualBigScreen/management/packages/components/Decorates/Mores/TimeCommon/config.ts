import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { TimeCommonConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@vb/settings/designSetting'
import i18n from '@/lang'

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

export const option = {
  // 数据说明
  timeSize: 24,
  timeLineHeight: 50,
  timeTextIndent: 2,
  timeColor: '#E6F7FF',
  fontWeight: 'normal',

  //阴影
  showShadow: true,
  hShadow: 0,
  vShadow: 0,
  blurShadow: 8,
  colorShadow: '#0075ff'
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TimeCommonConfig.key
  public attr = { ...chartInitConfig, w: 300, h: 50, zIndex: -1 }
  public chartConfig = cloneDeep(TimeCommonConfig)
  public option = cloneDeep(option)
}
