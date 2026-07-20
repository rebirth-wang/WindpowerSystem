import { InteractEventOn, InteractActionsType } from '@vb/enums/eventEnum'
import i18n from '@/lang'

// 时间组件类型
export enum ComponentInteractEventEnum {
  DATA = 'data'
}

// 联动参数
export enum ComponentInteractParamsEnum {
  DATA = 'data',
  DATA2 = 'data2'
}

// 定义组件触发回调事件
export const interactActions: InteractActionsType[] = [
  {
    interactType: InteractEventOn.CHANGE,
    interactName: i18n.global.t('visualBigScreen.management-674210-385'),
    componentEmitEvents: {
      [ComponentInteractEventEnum.DATA]: [
        {
          value: ComponentInteractParamsEnum.DATA,
          label: i18n.global.t('visualBigScreen.management-674210-303')
        },
        {
          value: ComponentInteractParamsEnum.DATA2,
          label: i18n.global.t('visualBigScreen.management-674210-389')
        }
      ]
    }
  }
]
