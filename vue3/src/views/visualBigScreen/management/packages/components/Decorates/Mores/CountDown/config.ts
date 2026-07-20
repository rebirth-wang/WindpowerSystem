import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { CountDownConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import { chartInitConfig } from '@vb/settings/designSetting'
import { FlipType } from '@vb/components/Pages/Flipper'

export enum CountdownStyleEnum {
  TIME = 'time',
  COLON = 'colon'
}

export const COUNTDOWN_LEGACY_STYLE_TIME = '\u65f6\u5206\u79d2'
export const COUNTDOWN_LEGACY_STYLE_COLON = '\u5192\u53f7'

type STYLE = CountdownStyleEnum | typeof COUNTDOWN_LEGACY_STYLE_TIME | typeof COUNTDOWN_LEGACY_STYLE_COLON

export interface OptionType {
  dataset: number
  useEndDate: boolean
  endDate: number
  style: STYLE
  showDay: boolean
  flipperBgColor: string
  flipperTextColor: string
  flipperWidth: number
  flipperHeight: number
  flipperRadius: number
  flipperGap: number
  flipperType: FlipType
  flipperSpeed: number
}

export const option: OptionType = {
  dataset: 10 * 60, // 10分钟
  useEndDate: false,
  endDate: new Date().getTime(), // 当前时间
  style: CountdownStyleEnum.TIME,
  showDay: false,
  flipperBgColor: '#16293E',
  flipperTextColor: '#4A9EF8FF',
  flipperWidth: 30,
  flipperHeight: 50,
  flipperRadius: 5,
  flipperGap: 10,
  flipperType: 'down',
  flipperSpeed: 450
}

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = CountDownConfig.key
  public attr = { ...chartInitConfig, w: 500, h: 100, zIndex: -1 }
  public chartConfig = cloneDeep(CountDownConfig)
  public option = cloneDeep(option)
}
