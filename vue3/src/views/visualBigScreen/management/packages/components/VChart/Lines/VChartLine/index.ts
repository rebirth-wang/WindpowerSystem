import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartLineConfig: ConfigType = {
  key: 'VChartLine',
  chartKey: 'VVChartLine',
  conKey: 'VCVChartLine',
  title: i18n.global.t('visualBigScreen.management-674210-232'),
  category: ChatCategoryEnum.LINE,
  categoryName: ChatCategoryEnumName.LINE,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_line.png'
}
