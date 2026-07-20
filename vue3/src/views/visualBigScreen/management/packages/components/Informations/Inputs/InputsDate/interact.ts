import { InteractEventOn, InteractActionsType } from '@vb/enums/eventEnum'
import i18n from '@/lang'

// 时间组件类型
export enum ComponentInteractEventEnum {
  DATE = 'date',
  DATE_TIME = 'datetime',
  DATE_RANGE = 'daterange',
  DATE_TIME_RANGE = 'datetimerange',
  MONTH = 'month',
  MONTH_RANGE = 'monthrange',
  YEAR = 'year',
  YEAR_RANGE = 'yearrange',
  QUARTER = 'quarter',
  QUARTER_RANGE = 'quarterrange'
}

// 联动参数
export enum ComponentInteractParamsEnum {
  DATE = 'date',
  DATE_START = 'dateStart',
  DATE_END = 'dateEnd',
  DATE_RANGE = 'daterange'
}

export enum DefaultTypeEnum {
  NONE = 'none',
  STATIC = 'static',
  DYNAMIC = 'dynamic'
}

export enum DifferUnitEnum {
  DAY = 'd',
  WEEK = 'w',
  MONTH = 'M',
  QUARTER = 'Q',
  YEAR = 'y',
  HOUR = 'h',
  MINUTE = 'm',
  SECOND = 's',
  MILLISECOND = 'ms'
}

export const DifferUnitObject = {
  // https://day.js.org/docs/en/manipulate/add
  [DifferUnitEnum.DAY]: i18n.global.t('visualBigScreen.management-674210-423'),
  [DifferUnitEnum.WEEK]: i18n.global.t('visualBigScreen.management-674210-424'),
  [DifferUnitEnum.MONTH]: i18n.global.t('visualBigScreen.management-674210-425'),
  [DifferUnitEnum.QUARTER]: i18n.global.t('visualBigScreen.management-674210-283'),
  [DifferUnitEnum.YEAR]: i18n.global.t('visualBigScreen.management-674210-426'),
  [DifferUnitEnum.HOUR]: i18n.global.t('visualBigScreen.management-674210-427'),
  [DifferUnitEnum.MINUTE]: i18n.global.t('visualBigScreen.management-674210-428'),
  [DifferUnitEnum.SECOND]: i18n.global.t('visualBigScreen.management-674210-429'),
  [DifferUnitEnum.MILLISECOND]: i18n.global.t('visualBigScreen.management-674210-315')
}

const time = [
  {
    value: ComponentInteractParamsEnum.DATE,
    label: i18n.global.t('visualBigScreen.management-674210-276')
  }
]

const timeRange = [
  {
    value: ComponentInteractParamsEnum.DATE_START,
    label: i18n.global.t('visualBigScreen.management-674210-387')
  },
  {
    value: ComponentInteractParamsEnum.DATE_END,
    label: i18n.global.t('visualBigScreen.management-674210-388')
  },
  {
    value: ComponentInteractParamsEnum.DATE_RANGE,
    label: i18n.global.t('visualBigScreen.management-674210-278')
  }
]

// 定义组件触发回调事件
export const interactActions: InteractActionsType[] = [
  {
    interactType: InteractEventOn.CHANGE,
    interactName: i18n.global.t('visualBigScreen.management-674210-385'),
    componentEmitEvents: {
      [ComponentInteractEventEnum.DATE]: time,
      [ComponentInteractEventEnum.DATE_TIME]: time,
      [ComponentInteractEventEnum.DATE_RANGE]: timeRange,
      [ComponentInteractEventEnum.MONTH]: time,
      [ComponentInteractEventEnum.MONTH_RANGE]: timeRange,
      [ComponentInteractEventEnum.QUARTER]: time,
      [ComponentInteractEventEnum.QUARTER_RANGE]: timeRange,
      [ComponentInteractEventEnum.YEAR]: time,
      [ComponentInteractEventEnum.YEAR_RANGE]: timeRange
    }
  }
]
