import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { CirclePointConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@vb/settings/designSetting'

export const option = {
  outCircle: 15,
  inCircle: 5,
  outCircleColor: '#3f5261',
  inCircleColor: '#fff',
  outCircleWidth: 2
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = CirclePointConfig.key
  public attr = { ...chartInitConfig, w: 97, h: 97, zIndex: 1 }
  public chartConfig = cloneDeep(CirclePointConfig)
  public option = cloneDeep(option)
}
