import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const Decorates03Config: ConfigType = {
  key: 'Decorates03',
  chartKey: 'VDecorates03',
  conKey: 'VCDecorates03',
  title: i18n.global.t('visualBigScreen.management-674210-195'),
  category: ChatCategoryEnum.DECORATE,
  categoryName: ChatCategoryEnumName.DECORATE,
  package: PackagesCategoryEnum.DECORATES,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'decorates03.png'
}
