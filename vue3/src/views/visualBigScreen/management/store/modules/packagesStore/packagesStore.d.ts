import { PackagesType, ConfigType } from '@vb/packages/index.d'

export { ConfigType }

export { PackagesType }
export interface PackagesStoreType {
  packagesList: PackagesType
  newPhoto?: ConfigType
}
