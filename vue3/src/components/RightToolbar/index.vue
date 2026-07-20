<template>
  <div class="top-right-btn" :style="style">
    <slot></slot>
    <el-tooltip
      effect="dark"
      :content="showSearch ? $t('components.RightToolbar.390493-0') : $t('components.RightToolbar.390493-1')"
      placement="top"
      v-if="search"
    >
      <el-button class="custom-button" :icon="Search" @click="toggleSearch" />
    </el-tooltip>
    <el-tooltip effect="dark" :content="$t('components.RightToolbar.390493-2')" placement="top">
      <el-button class="custom-button" :icon="Refresh" @click="refresh" />
    </el-tooltip>
    <el-tooltip effect="dark" :content="$t('components.RightToolbar.390493-3')" placement="top" v-if="columns">
      <el-button class="custom-button" :icon="Setting" @click="showColumn" />
    </el-tooltip>
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <div class="isShow-dialog">
        <el-transfer
          :titles="[$t('components.RightToolbar.390493-4'), $t('components.RightToolbar.390493-5')]"
          v-model="value"
          :data="transferData"
          @change="dataChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Search, Refresh, Setting } from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
  showSearch: {
    type: Boolean,
    default: true,
  },
  columns: {
    type: Array as () => any[],
    default: undefined,
  },
  search: {
    type: Boolean,
    default: true,
  },
  gutter: {
    type: Number,
    default: 10,
  },
});

const emit = defineEmits(['update:showSearch', 'queryTable']);

const value = ref<number[]>([]);
const title = ref(t('components.RightToolbar.390493-6'));
const open = ref(false);

const style = computed(() => {
  const ret: Record<string, string> = {};
  if (props.gutter) {
    ret.marginRight = `${props.gutter / 2}px`;
  }
  return ret;
});

const transferData = computed(() => {
  if (!props.columns) return [];
  return props.columns.map((item: any) => ({
    key: item.key,
    label: item.label,
    disabled: false,
  }));
});

onMounted(() => {
  if (props.columns) {
    props.columns.forEach((item: any, index: number) => {
      if (item.visible === false) {
        value.value.push(item.key);
      }
    });
  }
});

function toggleSearch() {
  emit('update:showSearch', !props.showSearch);
}

function refresh() {
  emit('queryTable');
}

function dataChange(data: number[]) {
  if (props.columns) {
    props.columns.forEach((item: any) => {
      item.visible = !data.includes(item.key);
    });
  }
}

function showColumn() {
  open.value = true;
}
</script>

<style lang="scss" scoped>
.top-right-btn {
  display: flex;
  align-items: center;
  margin-left: auto;
  .custom-button {
    width: 32.5px;
    height: 32.5px;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    display: flex; /* 使用 flexbox */
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
    padding: 0; /* 移除默认内边距 */

    :deep(.el-icon) {
      font-size: 12px; /* 设置合适的图标大小 */
      color: #606266; /* 设置图标颜色 */
    }
  }
}

.isShow-dialog {
  display: flex;
  flex-direction: row;
  justify-content: center;

  :deep(.el-transfer__button) {
    border-radius: 50%;
    padding: 12px;
    display: block;
    margin-left: 0;
  }

  :deep(.el-transfer__button:first-child) {
    margin-bottom: 10px;
  }

  :deep(.el-button + .el-button) {
    margin-left: 0;
  }
}
</style>
