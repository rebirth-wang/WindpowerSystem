import { reactive, h } from 'vue'
import { renderIcon } from '@vb/utils'
import { RouterLink } from 'vue-router'
import { PageEnum } from '@vb/enums/pageEnum'
import { MenuOption, MenuGroupOption } from 'naive-ui'
import { icon } from '@vb/plugins/icon'
import i18n from '@/lang'

const { GridIcon, TvOutlineIcon } = icon.ionicons5
const { StoreIcon, ObjectStorageIcon, DevicesIcon } = icon.carbon
export const renderMenuLabel = (option: MenuOption | MenuGroupOption) => {
  return option.label
}

export const expandedKeys = () => ['all-project']

export const menuOptionsInit = () => {
  const t = i18n.global.t

  return reactive([
    {
      key: 'divider-1',
      type: 'divider'
    },
    {
      label: () => h('span', null, { default: () => t('visualBigScreen.management-674210-24') }),
      key: 'all-project',
      icon: renderIcon(DevicesIcon),
      children: [
        {
          type: 'group',
          label: () => h('span', null, { default: () => t('visualBigScreen.management-674210-25') }),
          key: 'my-project',
          children: [
            {
              label: () =>
                h(
                  RouterLink,
                  {
                    to: {
                      name: PageEnum.BASE_HOME_ITEMS_NAME
                    }
                  },
                  { default: () => t('visualBigScreen.management-674210-27') }
                ),
              key: PageEnum.BASE_HOME_ITEMS_NAME,
              icon: renderIcon(TvOutlineIcon)
            }
            // {
            //   label: () =>
            //     h(
            //       RouterLink,
            //       {
            //         to: {
            //           name: PageEnum.BASE_HOME_TEMPLATE_NAME,
            //         },
            //       },
            //       { default: () => t('project.my_template') }
            //     ),
            //   key: PageEnum.BASE_HOME_TEMPLATE_NAME,
            //   icon: renderIcon(ObjectStorageIcon),
            // },
          ]
        }
      ]
    }

    // {
    //   key: 'divider-2',
    //   type: 'divider',
    // },
    // {
    //   label: () =>
    //     h(
    //       RouterLink,
    //       {
    //         to: {
    //           name: PageEnum.BASE_HOME_TEMPLATE_MARKET_NAME,
    //         },
    //       },
    //       { default: () => t('project.template_market') }
    //     ),
    //   key: PageEnum.BASE_HOME_TEMPLATE_MARKET_NAME,
    //   icon: renderIcon(StoreIcon),
    // },
  ])
}
