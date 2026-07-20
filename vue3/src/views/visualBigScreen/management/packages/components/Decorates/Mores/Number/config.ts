import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { NumberConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import i18n from '@/lang'

export const option = {
  // 数据说明
  dataset: 100000,
  from: 0,
  dur: 3,
  precision: 0,
  showSeparator: true,
  numberSize: 34,
  numberColor: '#4a9ef8',
  prefixText: '￥',
  prefixColor: '#4a9ef8',
  suffixText: i18n.global.t('visualBigScreen.management-674210-990'),
  suffixColor: '#4a9ef8'
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = NumberConfig.key
  public chartConfig = cloneDeep(NumberConfig)
  public option = cloneDeep(option)
}
