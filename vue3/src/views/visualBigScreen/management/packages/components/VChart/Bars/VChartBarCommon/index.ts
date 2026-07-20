import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartBarCommonConfig: ConfigType = {
  key: 'VChartBarCommon',
  chartKey: 'VVChartBarCommon',
  conKey: 'VCVChartBarCommon',
  title: i18n.global.t('visualBigScreen.management-674210-227'),
  category: ChatCategoryEnum.BAR,
  categoryName: ChatCategoryEnumName.BAR,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_bar_x.png'
}
