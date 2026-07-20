import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { TextGradientConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import i18n from '@/lang'

const createOption = () => ({
  dataset: i18n.global.t('visualBigScreen.management-674210-394'),
  size: 20,
  gradient: {
    from: '#0000FFFF',
    to: '#00FF00FF',
    deg: 45
  }
})

export const option = createOption()

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TextGradientConfig.key
  public chartConfig = cloneDeep(TextGradientConfig)
  public option = createOption()
}
