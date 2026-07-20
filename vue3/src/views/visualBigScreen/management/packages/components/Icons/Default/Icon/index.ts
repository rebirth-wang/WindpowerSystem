import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const IconConfig: ConfigType = {
  key: 'Icon',
  chartKey: 'VIcon',
  conKey: 'VCIcon',
  title: i18n.global.t('visualBigScreen.management-674210-138'),
  category: ChatCategoryEnum.DEFAULT,
  categoryName: ChatCategoryEnumName.DEFAULT,
  package: PackagesCategoryEnum.ICONS,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'icon.png'
}
