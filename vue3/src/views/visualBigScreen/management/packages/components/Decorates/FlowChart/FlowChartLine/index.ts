import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const FlowChartLineConfig: ConfigType = {
  key: 'FlowChartLine',
  chartKey: 'VFlowChartLine',
  conKey: 'VCFlowChartLine',
  title: i18n.global.t('visualBigScreen.management-674210-199'),
  category: ChatCategoryEnum.FlowChart,
  categoryName: ChatCategoryEnumName.FlowChart,
  package: PackagesCategoryEnum.DECORATES,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'flow-zhexian.png'
}
