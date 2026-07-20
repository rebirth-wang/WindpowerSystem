import { defineStore } from 'pinia';
import { ref } from 'vue';
import Cookies from 'js-cookie';

export type DeviceType = 'desktop' | 'mobile';

export interface SidebarState {
  opened: boolean;
  withoutAnimation: boolean;
  hide: boolean;
}

export const useAppStore = defineStore('app', () => {
  const sidebar = ref<SidebarState>({
    opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
    withoutAnimation: false,
    hide: false,
  });
  const device = ref<DeviceType>('desktop');
  const size = ref(Cookies.get('size') || 'medium');

  // 对应 TOGGLE_SIDEBAR mutation
  function toggleSideBar() {
    if (sidebar.value.hide) {
      return false;
    }
    sidebar.value.opened = !sidebar.value.opened;
    sidebar.value.withoutAnimation = false;
    if (sidebar.value.opened) {
      Cookies.set('sidebarStatus', '1');
    } else {
      Cookies.set('sidebarStatus', '0');
    }
  }

  // 对应 CLOSE_SIDEBAR mutation
  function closeSideBar(withoutAnimation: boolean) {
    Cookies.set('sidebarStatus', '0');
    sidebar.value.opened = false;
    sidebar.value.withoutAnimation = withoutAnimation;
  }

  // 对应 TOGGLE_DEVICE mutation
  function toggleDevice(deviceType: DeviceType) {
    device.value = deviceType;
  }

  // 对应 SET_SIZE mutation
  function setSize(sizeType: string) {
    size.value = sizeType;
    Cookies.set('size', sizeType);
  }

  // 对应 SET_SIDEBAR_HIDE mutation
  function toggleSideBarHide(status: boolean) {
    sidebar.value.hide = status;
  }

  return {
    sidebar,
    device,
    size,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
    toggleSideBarHide,
  };
});
