import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const Decorates06Config: ConfigType = {
  key: 'Decorates06',
  chartKey: 'VDecorates06',
  conKey: 'VCDecorates06',
  title: i18n.global.t('visualBigScreen.management-674210-198'),
  category: ChatCategoryEnum.DECORATE,
  categoryName: ChatCategoryEnumName.DECORATE,
  package: PackagesCategoryEnum.DECORATES,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'decorates06.png'
}
