import { PublicConfigClass } from '@vb/packages/public'
import { chartInitConfig } from '@vb/settings/designSetting'
import { CreateComponentType } from '@vb/packages/index.d'
import { Decorates06Config } from './index'
import cloneDeep from 'lodash/cloneDeep'
import i18n from '@/lang'

export const option = {
  colors: ['#1DC1F533', '#1DC1F5FF'],
  dataset: i18n.global.t('visualBigScreen.management-674210-993'),
  textColor: '#fff',
  textSize: 32
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = Decorates06Config.key
  public attr = { ...chartInitConfig, w: 500, h: 70, zIndex: 1 }
  public chartConfig = cloneDeep(Decorates06Config)
  public option = cloneDeep(option)
}
