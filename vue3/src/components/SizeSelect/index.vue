<template>
  <div class="size-select">
    <el-dropdown @command="handleSetSize">
      <div class="title-wrap">
        <svg-icon icon-class="font_size" />
        <span class="size">{{ getSizeName() }}</span>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="item of sizeOptions"
            :key="item.value"
            :disabled="size === item.value"
            :command="item.value"
          >
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAppStore } from '@/stores/modules/app';
import { useTagsViewStore } from '@/stores/modules/tagsView';

const { t } = useI18n();
const router = useRouter();

const appStore = useAppStore();
const tagsViewStore = useTagsViewStore();

const sizeOptions = [
  { label: t('components.sizeSelect.736906-1'), value: 'default' },
  { label: t('components.sizeSelect.736906-2'), value: 'medium' },
  { label: t('components.sizeSelect.736906-3'), value: 'small' },
  { label: t('components.sizeSelect.736906-4'), value: 'mini' },
];

const size = computed(() => appStore.size);

const handleSetSize = async (size: string) => {
  appStore.setSize(size);
  await refreshView();
  ElMessage({
    message: t('components.sizeSelect.736906-5'),
    type: 'success',
  });
};

const refreshView = async () => {
  const { fullPath } = router.currentRoute.value;
  const redirectPath = `/redirect${fullPath}`;

  // 先删除所有缓存视图
  await tagsViewStore.delAllCachedViews(router.currentRoute.value);

  // 导航到重定向页面
  await router.replace({ path: redirectPath });

  // 重定向完成后，再次确保移除redirect相关标签
  await tagsViewStore.delView({ path: redirectPath, meta: {}, name: '' });
};

const getSizeName = () => {
  const obj = sizeOptions.find((item) => item.value === size.value) || { label: '' };
  return obj.label;
};
</script>

<style lang="scss" scoped>
.size-select {
  line-height: 14px;

  .title-wrap {
    font-size: 14px;

    .size {
      margin-left: 6px;
    }
  }
}
</style>
