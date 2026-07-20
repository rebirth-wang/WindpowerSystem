import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartAreaConfig: ConfigType = {
  key: 'VChartArea',
  chartKey: 'VVChartArea',
  conKey: 'VCVChartArea',
  title: i18n.global.t('visualBigScreen.management-674210-229'),
  category: ChatCategoryEnum.AREA,
  categoryName: ChatCategoryEnumName.AREA,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_area.png'
}
