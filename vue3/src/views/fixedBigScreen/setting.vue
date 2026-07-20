<template>
  <transition name="yh-setting-fade">
    <div class="setting" :class="{ settingShow: settingShow }" v-show="settingShow">
      <div class="setting_dislog" @click="settingShow = false"></div>
      <div class="setting_inner">
        <div class="setting_header">设置</div>
        <div class="setting_body">
          <div class="left_shu">全局设置</div>
          <div class="setting_item">
            <span class="setting_label">
              是否进行自动适配
              <span class="setting_label_tip">(默认分辨率1920*1080)</span>
              :
            </span>
            <div class="setting_content">
              <el-radio-group v-model="isScaleradio" @change="(val: boolean) => radiochange(val, 'isScale')">
                <el-radio :value="true">是</el-radio>
                <el-radio :value="false">否</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="left_shu">实时监测</div>
          <div class="setting_item">
            <span class="setting_label">
              设备提醒自动轮询:
              <span class="setting_label_tip"></span>
            </span>
            <div class="setting_content">
              <el-radio-group v-model="sbtxradio" @change="(val: boolean) => radiochange(val, 'sbtxSwiper')">
                <el-radio :value="true">是</el-radio>
                <el-radio :value="false">否</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="setting_item">
            <span class="setting_label">实时预警轮播:</span>
            <div class="setting_content">
              <el-radio-group v-model="ssyjradio" @change="(val: boolean) => radiochange(val, 'ssyjSwiper')">
                <el-radio :value="true">是</el-radio>
                <el-radio :value="false">否</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="flex justify-center"></div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { useSettingsStore } from '@/stores/modules/settings';

const router = useRouter();
const settingsStore = useSettingsStore();

const settingShow = ref(false);
const sbtxradio = ref(true);
const ssyjradio = ref(true);
const isScaleradio = ref(true);

function init() {
  settingShow.value = true;
}

function radiochange(val: boolean, type: string) {
  settingsStore.updateSwiper(val, type);
  if (type === 'isScale') {
    router.go(0);
  }
}

onMounted(() => {
  // 将组件挂载到 body 上
  document.body.appendChild(getCurrentInstance()!.proxy!.$el);
});

onBeforeUnmount(() => {
  const el = getCurrentInstance()?.proxy?.$el;
  if (el && el.parentNode) {
    el.parentNode.removeChild(el);
  }
});

defineExpose({ init });
</script>

<style lang="scss" scoped></style>
