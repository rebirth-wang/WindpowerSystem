<template>
  <div v-if="showContent" class="alert-box">
    <el-row class="alert-title">
      <svg-icon
        icon-class="device"
        size="25"
        class-name="card-panel-icon"
        style="color: white; margin-top: 3px; float: left; margin-right: 10px"
      />
      <el-col :span="12">
        <div>{{ title }}</div>
      </el-col>
      <el-icon class="close-icon" @click="closeAlert"><Close /></el-icon>
    </el-row>
    <div class="alert-content">{{ content }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import { Close } from '@element-plus/icons-vue';

const props = defineProps({
  message: {
    type: Object,
    default: null,
  },
  showTip: {
    type: Boolean,
    default: false,
  },
});

const title = ref('');
const content = ref('');
const showContent = ref(false);
const autoClose = ref<ReturnType<typeof setTimeout> | null>(null);

watch(
  () => props.message,
  (newVal: any) => {
    if (newVal) {
      if (newVal.showDialog === 1) {
        showContent.value = props.showTip;
        title.value = newVal.title;
        content.value = newVal.content;
      }
      autoClose.value = setTimeout(() => {
        closeAlert();
      }, 2000);
    }
  }
);

function closeAlert() {
  showContent.value = false;
  if (autoClose.value) clearTimeout(autoClose.value);
}

onMounted(() => {
  if (props.message) {
    if ((props.message as any).showDialog === 1) {
      showContent.value = props.showTip;
      title.value = (props.message as any).title;
      content.value = (props.message as any).content;
    }
    autoClose.value = setTimeout(() => {
      closeAlert();
    }, 3000);
  }
});

onBeforeUnmount(() => {
  if (autoClose.value) clearTimeout(autoClose.value);
});
</script>

<style lang="scss" scoped>
.alert-box {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 300px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid red;
  background: white;
  border-radius: 5px 5px 0 0px;
  z-index: 9999;

  .alert-title {
    background-color: red;
    color: white;
    padding: 10px;
    font-weight: bold;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;

    .close-icon {
      position: absolute;
      top: 10px;
      right: 10px;
      cursor: pointer;
      color: #eee;
      font-size: 16px;
    }
  }

  .alert-content {
    background-color: white;
    padding: 15px;
  }
}
</style>
