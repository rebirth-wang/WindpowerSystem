<template>
  <n-dropdown trigger="hover" @select="handleSelect" :show-arrow="true" :options="options">
    <div class="user-info-box">
      <person-icon v-if="fallback"></person-icon>
      <n-avatar
        v-if="!fallback"
        round
        object-fit="cover"
        size="medium"
        :src="avatarUrl"
        @error="errorHandle"
      ></n-avatar>
    </div>
  </n-dropdown>

  <!-- 系统设置 model -->
  <go-system-set v-model:modelShow="modelShow"></go-system-set>
  <!-- 关于软件 model -->
  <go-system-info v-model:modelShow="modelShowInfo"></go-system-info>
</template>

<script lang="ts" setup>
import { computed, h, ref, watch } from 'vue'
import { NAvatar, NText } from 'naive-ui'
import { renderIcon, getLocalStorage } from '@vb/utils'
import { SystemStoreEnum, SystemStoreUserInfoEnum } from '@vb/store/modules/systemStore/systemStore.d'
import { StorageEnum } from '@vb/enums/storageEnum'
import { renderLang } from '@vb/utils'
import { GoSystemSet } from '@vb/components/GoSystemSet/index'
import { GoSystemInfo } from '@vb/components/GoSystemInfo/index'
import { useUserStore } from '@/stores/modules/user'
import _Person from './person.png'

import { icon } from '@vb/plugins/icon'
const { PersonIcon, LogOutOutlineIcon, SettingsSharpIcon } = icon.ionicons5

const userStore = useUserStore()
const avatarUrl = computed(() => userStore.avatar || _Person)

const modelShowInfo = ref(false)
const modelShow = ref(false)

// 是否失败
const fallback = ref(false)

watch(avatarUrl, () => {
  fallback.value = false
})

// 用户图标渲染
const renderUserInfo = () => {
  return h(
    'div',
    {
      style: 'display: flex; align-items: center; padding: 8px 12px;'
    },
    [
      h(NAvatar, {
        round: true,
        style: 'margin-right: 12px;',
        src: avatarUrl.value
      }),
      h('div', null, [
        h('div', null, [
          h(
            NText,
            { depth: 2 },
            {
              default: () => {
                const info = getLocalStorage(StorageEnum.GO_SYSTEM_STORE)
                if (info) {
                  return info[SystemStoreEnum.USER_INFO][SystemStoreUserInfoEnum.USER_NAME]
                } else {
                  return 'admin'
                }
              }
            }
          )
        ])
      ])
    ]
  )
}
const options = ref([
  {
    label: renderLang('visualBigScreen.management-674210-18'),
    key: 'info',
    type: 'render',
    render: renderUserInfo
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: renderLang('visualBigScreen.management-674210-19'),
    key: 'sysSet',
    icon: renderIcon(SettingsSharpIcon)
  },
  // {
  //   label: renderLang('global.contact'),
  //   key: 'contact',
  //   icon: renderIcon(ChatboxEllipsesIcon)
  // },
  {
    type: 'divider',
    key: 'd3'
  },
  {
    label: renderLang('navbar.logout'),
    key: 'logout',
    icon: renderIcon(LogOutOutlineIcon)
  }
])

// 图片渲染错误
const errorHandle = () => {
  fallback.value = true
}

// 系统设置
const sysSetHandle = () => {
  modelShow.value = true
}

// 系统设置
const sysInfoHandle = () => {
  modelShowInfo.value = true
}

const logoutHandle = async () => {
  try {
    await userStore.LogOut()
  } catch {
    userStore.FedLogOut()
  } finally {
    window.onbeforeunload = null
    window.location.href = '/login'
  }
}

const handleSelect = (key: string) => {
  switch (key) {
    case 'contact':
      sysInfoHandle()
      break
    case 'sysSet':
      sysSetHandle()
      break
    case 'logout':
      logoutHandle()
      break
  }
}
</script>

<style lang="scss" scoped>
.user-info-box {
  cursor: pointer;
  transform: scale(0.7);
}
</style>
