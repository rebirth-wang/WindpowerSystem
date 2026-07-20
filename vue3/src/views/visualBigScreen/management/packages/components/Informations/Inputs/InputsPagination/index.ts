import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const InputsPaginationConfig: ConfigType = {
  key: 'InputsPagination',
  chartKey: 'VInputsPagination',
  conKey: 'VCInputsPagination',
  title: i18n.global.t('visualBigScreen.management-674210-218'),
  category: ChatCategoryEnum.INPUTS,
  categoryName: ChatCategoryEnumName.INPUTS,
  package: PackagesCategoryEnum.INFORMATIONS,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'inputs_pagination.png'
}
