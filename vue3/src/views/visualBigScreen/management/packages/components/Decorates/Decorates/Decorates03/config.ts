import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { Decorates03Config } from './index'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@vb/settings/designSetting'
import i18n from '@/lang'

export const option = {
  dataset: i18n.global.t('visualBigScreen.management-674210-993'),
  textColor: '#fff',
  textSize: 32,
  colors: ['#1dc1f5', '#1dc1f5']
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = Decorates03Config.key
  public attr = { ...chartInitConfig, w: 500, h: 70, zIndex: 1 }
  public chartConfig = cloneDeep(Decorates03Config)
  public option = cloneDeep(option)
}
