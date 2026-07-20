import i18n from '@/lang'
import { ConfigType, PackagesCategoryEnum, ChartFrameEnum } from '@vb/packages/index.d'
import { ChatCategoryEnum, ChatCategoryEnumName } from '../../index.d'

export const InputsInputConfig: ConfigType = {
  key: 'InputsInput',
  chartKey: 'VInputsInput',
  conKey: 'VCInputsInput',
  title: i18n.global.t('visualBigScreen.management-674210-214'),
  category: ChatCategoryEnum.INPUTS,
  categoryName: ChatCategoryEnumName.INPUTS,
  package: PackagesCategoryEnum.INFORMATIONS,
  chartFrame: ChartFrameEnum.STATIC,
  image: 'inputs_input.png'
}
