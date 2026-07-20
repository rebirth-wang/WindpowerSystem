import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartPieConfig: ConfigType = {
  key: 'VChartPie',
  chartKey: 'VVChartPie',
  conKey: 'VCVChartPie',
  title: i18n.global.t('visualBigScreen.management-674210-231'),
  category: ChatCategoryEnum.PIE,
  categoryName: ChatCategoryEnumName.PIE,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_pie.png'
}
