import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const WaterPoloConfig: ConfigType = {
  key: 'WaterPolo',
  chartKey: 'VWaterPolo',
  conKey: 'VCWaterPolo',
  title: i18n.global.t('visualBigScreen.management-674210-179'),
  category: ChatCategoryEnum.MORE,
  categoryName: ChatCategoryEnumName.MORE,
  package: PackagesCategoryEnum.CHARTS,
  chartFrame: ChartFrameEnum.COMMON,
  image: 'water_WaterPolo.png'
}
