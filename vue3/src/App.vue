<template>
  <el-config-provider :locale="elLocale">
    <div id="fb-app">
      <router-view :key="language" />
      <notification v-if="showNotification" :message="notificationMessage" :showTip="showNotification" />
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useSettingsStore } from '@/stores/modules/settings';
import { useUserStore } from '@/stores/modules/user';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import en from 'element-plus/es/locale/lang/en';
import { getUserId } from '@/utils/auth';
import mqttTool from '@/utils/mqttTool';
import Notification from '@/components/Notification/index.vue';

const settingsStore = useSettingsStore();
const userStore = useUserStore();

const showNotification = ref(false);
const notificationMessage = ref<any>(null);
const appMqttListener = ref<any>(null);

const language = computed(() => settingsStore.language);
const token = computed(() => userStore.token);
const elLocale = computed(() => (language.value === 'zh-CN' ? zhCn : en));

onMounted(() => {
  if (token.value) {
    connectMqtt();
  }
});

watch(token, (newToken) => {
  if (newToken) {
    setTimeout(() => {
      connectMqtt();
    }, 5000);
  }
});

/*连接Mqtt消息服务器 */
function connectMqtt() {
  if (mqttTool.client == null) {
    mqttTool.connect();
  }
  if (mqttTool.client == null) {
    return;
  }
  mqttCallback();
  mqttTool.subscribe('/notify/alert/web/push/' + getUserId());
}

/** MQTT 消息监听器类型，附带唯一标识属性 */
type MqttAppListener = ((topic: string, message: any) => void) & { isAppListener?: boolean };

/* Mqtt回调处理 */
function mqttCallback() {
  const appListener: MqttAppListener = (topic: string, message: any) => {
    try {
      message = JSON.parse(message.toString());
      notificationMessage.value = message;
      if (message.showDialog === 1) {
        showNotification.value = true;
      }
      if (!message) {
        return;
      }
    } catch (e) {
      console.error('处理MQTT消息出错:', e);
    }
  };

  // 给监听器添加唯一标识
  appListener.isAppListener = true;

  // 保存监听器引用
  appMqttListener.value = appListener;
  // 绑定监听器
  mqttTool.client.on('message', appListener);
}

onBeforeUnmount(() => {
  if (mqttTool.client != null) {
    mqttTool.unsubscribe('/notify/alert/web/push/' + getUserId());
  }
});
</script>

<style scoped>
#fb-app {
  width: 100%;
  height: 100%;
}
</style>
