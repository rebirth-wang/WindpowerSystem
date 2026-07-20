import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { TextBarrageConfig } from './index'
import { chartInitConfig } from '@vb/settings/designSetting'
import cloneDeep from 'lodash/cloneDeep'
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

const createOption = () => ({
  dataset: i18n.global.t('visualBigScreen.management-674210-395'),
  fontSize: 32,
  fontColor: '#ffffff',
  fontWeight: 'normal',
  // 字间距
  letterSpacing: 5,
  //阴影
  showShadow: true,
  boxShadow: 'none',
  hShadow: 0,
  vShadow: 0,
  blurShadow: 8,
  colorShadow: '#0075ff',
  //动画
  animationTime: 0,
  animationSpeed: 50
})

export const option = createOption()

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TextBarrageConfig.key
  public attr = { ...chartInitConfig, w: 500, h: 70, zIndex: -1 }
  public chartConfig = cloneDeep(TextBarrageConfig)
  public option = createOption()
  public preview = { overFlowHidden: true }
}
