import cloneDeep from 'lodash/cloneDeep'
import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { chartInitConfig } from '@vb/settings/designSetting'
import { COMPONENT_INTERACT_EVENT_KET } from '@vb/enums/eventEnum'
import { interactActions, ComponentInteractEventEnum } from './interact'
import { InputsTabConfig } from './index'
import i18n from '@/lang'

const createOption = () => ({
  // 时间组件展示类型，必须和 interactActions 中定义的数据一致
  [COMPONENT_INTERACT_EVENT_KET]: ComponentInteractEventEnum.DATA,
  // 默认值
  tabLabel: i18n.global.t('visualBigScreen.management-674210-390'),
  // 样式
  tabType: 'segment',
  // 暴露配置内容给用户
  dataset: [
    {
      label: i18n.global.t('visualBigScreen.management-674210-390'),
      value: '1'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-391'),
      value: '2'
    },
    {
      label: i18n.global.t('visualBigScreen.management-674210-392'),
      value: '3'
    }
  ]
})

export const option = createOption()

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = InputsTabConfig.key
  public attr = { ...chartInitConfig, w: 460, h: 32, zIndex: -1 }
  public chartConfig = cloneDeep(InputsTabConfig)
  public interactActions = interactActions
  public option = createOption()
}
