import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartBarStackConfig: ConfigType = {
  key: 'VChartBarStack',
  chartKey: 'VVChartBarStack',
  conKey: 'VCVChartBarStack',
  title: i18n.global.t('visualBigScreen.management-674210-228'),
  category: ChatCategoryEnum.BAR,
  categoryName: ChatCategoryEnumName.BAR,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_bar_x_stack.png'
}
