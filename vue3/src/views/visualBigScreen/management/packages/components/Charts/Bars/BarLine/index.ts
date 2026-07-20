import i18n from '@/lang'
// 公共类型声明
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
// 当前[信息模块]分类声明
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const BarLineConfig: ConfigType = {
  key: 'BarLine',
  chartKey: 'VBarLine',
  conKey: 'VCBarLine',
  title: i18n.global.t('visualBigScreen.management-674210-163'),
  category: ChatCategoryEnum.BAR,
  categoryName: ChatCategoryEnumName.BAR,
  package: PackagesCategoryEnum.CHARTS,
  chartFrame: ChartFrameEnum.ECHARTS,
  image: 'bar_line.png'
}
