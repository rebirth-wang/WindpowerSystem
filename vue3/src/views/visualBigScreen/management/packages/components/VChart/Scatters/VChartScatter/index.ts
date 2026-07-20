import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartScatterConfig: ConfigType = {
  key: 'VChartScatter',
  chartKey: 'VVChartScatter',
  conKey: 'VCVChartScatter',
  title: i18n.global.t('visualBigScreen.management-674210-234'),
  category: ChatCategoryEnum.SCATTER,
  categoryName: ChatCategoryEnumName.SCATTER,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_scatter.png'
}
