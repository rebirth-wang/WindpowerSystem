import { ChartEditStorage } from '@vb/store/modules/chartEditStore/chartEditStore.d'

export interface ChartEditStorageType extends ChartEditStorage {
  id: string
  isRelease?: boolean
}
