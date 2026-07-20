import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const MapBaseConfig: ConfigType = {
  key: 'MapBase',
  chartKey: 'VMapBase',
  conKey: 'VCMapBase',
  title: i18n.global.t('visualBigScreen.management-674210-170'),
  category: ChatCategoryEnum.MAP,
  categoryName: ChatCategoryEnumName.MAP,
  package: PackagesCategoryEnum.CHARTS,
  chartFrame: ChartFrameEnum.COMMON,
  image: 'map.png'
}
