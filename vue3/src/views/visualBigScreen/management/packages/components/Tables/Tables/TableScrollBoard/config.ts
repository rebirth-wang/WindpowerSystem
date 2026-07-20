import { PublicConfigClass } from '@vb/packages/public'
import { CreateComponentType } from '@vb/packages/index.d'
import { TableScrollBoardConfig } from './index'
import cloneDeep from 'lodash/cloneDeep'
import dataJson from './data.json'
import i18n from '@/lang'

const createOption = () => ({
  header: [
    i18n.global.t('visualBigScreen.management-674210-396'),
    i18n.global.t('visualBigScreen.management-674210-397'),
    i18n.global.t('visualBigScreen.management-674210-398')
  ],
  dataset: dataJson,
  index: true,
  columnWidth: [30, 100, 100],
  align: ['center', 'right', 'right', 'right'],
  rowNum: 5,
  waitTime: 2,
  headerHeight: 35,
  carousel: 'single',
  headerBGC: '#00BAFF',
  oddRowBGC: '#003B51',
  evenRowBGC: '#0A2732'
})

export const option = createOption()

export default class Config extends PublicConfigClass implements CreateComponentType {
  public key = TableScrollBoardConfig.key
  public chartConfig = cloneDeep(TableScrollBoardConfig)
  public option = createOption()
}
