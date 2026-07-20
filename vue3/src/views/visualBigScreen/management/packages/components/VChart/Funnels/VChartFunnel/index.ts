import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartFunnelConfig: ConfigType = {
  key: 'VChartFunnel',
  chartKey: 'VVChartFunnel',
  conKey: 'VCVChartFunnel',
  title: i18n.global.t('visualBigScreen.management-674210-233'),
  category: ChatCategoryEnum.FUNNEL,
  categoryName: ChatCategoryEnumName.FUNNEL,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_funnel.png'
}
