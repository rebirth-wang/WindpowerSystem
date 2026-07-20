import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const VChartWordCloudConfig: ConfigType = {
  key: 'VChartWordCloud',
  chartKey: 'VVChartWordCloud',
  conKey: 'VCVChartWordCloud',
  title: i18n.global.t('visualBigScreen.management-674210-235'),
  category: ChatCategoryEnum.WORDCLOUD,
  categoryName: ChatCategoryEnumName.WORDCLOUD,
  package: PackagesCategoryEnum.VCHART,
  chartFrame: ChartFrameEnum.VCHART,
  image: 'vchart_word_cloud.png'
}
