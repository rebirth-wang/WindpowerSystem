<template>
  <el-image
    class="img-pre"
    :src="realSrc"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
    :preview-teleported="true"
  >
    <template #error>
      <div class="image-slot">
        <el-icon><Picture /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Picture } from '@element-plus/icons-vue';
import { isExternal } from '@/utils/validate';

const props = defineProps({
  src: {
    type: String,
    default: '',
  },
  width: {
    type: [Number, String],
    default: '',
  },
  height: {
    type: [Number, String],
    default: '',
  },
});

const realSrc = computed(() => {
  if (!props.src) return undefined;
  const realSrcVal = props.src.split(',')[0];
  if (isExternal(realSrcVal)) return realSrcVal;
  return import.meta.env.VITE_APP_BASE_API + realSrcVal;
});

const realSrcList = computed(() => {
  if (!props.src) return undefined;
  const realSrcListVal = props.src.split(',');
  const srcList: string[] = [];
  realSrcListVal.forEach((item) => {
    if (isExternal(item)) {
      srcList.push(item);
    } else {
      srcList.push(import.meta.env.VITE_APP_BASE_API + item);
    }
  });
  return srcList;
});

const realWidth = computed(() => (typeof props.width === 'string' ? props.width : `${props.width}px`));
const realHeight = computed(() => (typeof props.height === 'string' ? props.height : `${props.height}px`));
</script>

<style lang="scss" scoped>
.img-pre {
  border-radius: 4px;
  background-color: #dcdcdc;
  overflow: clip;

  :deep(.el-image__inner) {
    transition: all 0.3s;
    cursor: pointer;
    &:hover {
      transform: scale(1.2);
    }
  }

  :deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
