<template>
  <div class="componen-management">
    <p class="prompt">{{ $t('components.DragEditor.rightslider.756540-0') }}</p>
    <!-- 拖拽 -->
    <VueDraggable v-model="data" item-key="_dragKey" v-bind="dragOptions">
      <template #item="{ element, index }">
        <div :class="element.text == '底部导航' ? 'item del-dragitem' : 'item'">
          <p>
            <el-icon>
              <Grid />
            </el-icon>
            {{ isI18nKey(element.text) ? $t(element.text) : element.text }}
          </p>
          <el-popconfirm
            :title="$t('components.DragEditor.rightslider.756540-1')"
            :icon="WarningFilled"
            icon-color="red"
            @confirm="onConfirms(index)"
          >
            <template #reference>
              <el-icon>
                <Delete />
              </el-icon>
            </template>
          </el-popconfirm>
        </div>
      </template>
    </VueDraggable>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import VueDraggable from 'vuedraggable';
import { Delete, Grid, WarningFilled } from '@element-plus/icons-vue';

interface DragItem {
  _dragKey?: string;
  component?: string;
  text?: string;
  [key: string]: any;
}

const props = defineProps<{ datas: DragItem[] }>();
const emit = defineEmits<{
  (event: 'componenmanagement', data: DragItem[]): void;
}>();

function normalizeData(list: DragItem[] = []) {
  return list.map((item, ind) => ({
    ...item,
    _dragKey: (item.component || item.text || 'item') + '-' + ind,
  }));
}

function stripDragKey(list: DragItem[] = []) {
  return list.map(({ _dragKey, ...rest }) => rest);
}

function isI18nKey(text?: string) {
  return /^dragEditor\./.test(text || '');
}

const data = ref<DragItem[]>(normalizeData(props.datas));
const dragOptions = {
  animation: 200,
  filter: '.del-dragitem',
};

function onConfirms(index: number) {
  data.value.splice(index, 1);
}

watch(
  () => props.datas,
  (newVal) => {
    data.value = normalizeData(newVal || []);
  },
  { deep: true }
);

watch(
  data,
  (newVal) => {
    emit('componenmanagement', stripDragKey(newVal));
  },
  { deep: true }
);
</script>

<style lang="scss" scoped>
.componen-management {
  width: 100%;
  height: 100%;
  padding: 20px 5px;
  box-sizing: border-box;

  .prompt {
    font-size: 12px;
    line-height: 24px;
    color: #969799;
    margin: 10px 0;
  }

  /* 选项 */
  .item {
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    cursor: all-scroll;
    color: #323233;
    background-color: #fff;
    border-bottom: 1px solid #f2f4f6;

    p {
      display: flex;
      align-items: center;
      gap: 14px;
      margin: 0;
    }

    i {
      color: #999;
    }
  }

  .del-dragitem {
    background-color: rgba(10, 42, 97, 0.2);
    cursor: no-drop;
  }
}
</style>
