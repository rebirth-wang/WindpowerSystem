import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartPercentAreaConfig: ConfigType = {
  key: 'VChartPercentArea',
  chartKey: 'VVChartPercentArea',
  conKey: 'VCVChartPercentArea',
  title: i18n.global.t('visualBigScreen.management-674210-230'),
  category: ChatCategoryEnum.AREA,
  categoryName: ChatCategoryEnumName.AREA,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_percent_area.png'
}
