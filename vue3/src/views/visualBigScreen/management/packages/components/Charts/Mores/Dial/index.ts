import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const DialConfig: ConfigType = {
  key: 'Dial',
  chartKey: 'VDial',
  conKey: 'VCDial',
  title: i18n.global.t('visualBigScreen.management-674210-171'),
  category: ChatCategoryEnum.MORE,
  categoryName: ChatCategoryEnumName.MORE,
  package: PackagesCategoryEnum.CHARTS,
  chartFrame: ChartFrameEnum.COMMON,
  image: 'dial.png'
}
