import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const CirclePointConfig: ConfigType = {
  key: 'CirclePoint',
  chartKey: 'VCirclePoint',
  conKey: 'VCCirclePoint',
  title: i18n.global.t('visualBigScreen.management-674210-209'),
  category: ChatCategoryEnum.MORE,
  categoryName: ChatCategoryEnumName.MORE,
  package: PackagesCategoryEnum.DECORATES,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'flow-circle.png'
}
