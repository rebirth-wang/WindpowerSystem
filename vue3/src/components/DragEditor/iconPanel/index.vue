<template>
  <el-drawer
    v-if="icon"
    v-model="visible"
    :show-close="false"
    :with-header="false"
    :close-on-click-modal="false"
    :size="360"
  >
    <div class="icon-panel">
      <el-alert :title="$t('components.DragEditor.iconPanel.756540-0')" type="info" :closable="false" />
      <div class="content">
        <div
          v-for="item in iconList"
          :key="item.icon"
          :class="['icon', { active: selectIcon === item.icon }]"
          @click="handleSelectIcon(item.icon)"
        >
          <el-icon :size="20">
            <component :is="item.component" />
          </el-icon>
        </div>
      </div>
    </div>
    <div class="drawer-footer">
      <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="handleSave">{{ $t('confirm') }}</el-button>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import { Edit, Tools, View, Document, Menu } from '@element-plus/icons-vue';

const dragStore = useDragEditorStore();

const props = defineProps<{
  data: { show: boolean };
  icon?: string;
  templateId?: number;
}>();

const iconList = [
  { name: 'el-icon-edit', icon: 'el-icon-edit', component: Edit },
  { name: 'el-icon-s-tools', icon: 'el-icon-s-tools', component: Tools },
  { name: 'el-icon-view', icon: 'el-icon-view', component: View },
  { name: 'el-icon-document', icon: 'el-icon-document', component: Document },
  { name: 'el-icon-menu', icon: 'el-icon-menu', component: Menu },
];

const selectIcon = ref('el-icon-edit');

const visible = computed({
  get: () => !!props.data?.show,
  set: (val: boolean) => {
    if (props.data) props.data.show = val;
  },
});

watch(
  () => props.icon,
  (val) => {
    selectIcon.value = val || 'el-icon-edit';
  },
  { immediate: true }
);

function handleSelectIcon(icon: string) {
  selectIcon.value = icon;
}

function handleSave() {
  const attrs = dragStore.currentproperties?.setConfig?.attributes || [];
  attrs.forEach((item: any) => {
    if (item.templateId === (props.templateId || 1)) {
      item.icon = selectIcon.value;
    }
  });
  visible.value = false;
  selectIcon.value = 'el-icon-edit';
}

function handleCancel() {
  visible.value = false;
  selectIcon.value = 'el-icon-edit';
}
</script>

<style lang="scss" scoped>
.icon-panel {
  padding: 20px;

  .content {
    margin-top: 20px;
    padding: 0 10px;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    overflow-y: auto;

    .icon {
      width: 50px;
      height: 50px;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      border-radius: 10px;
    }
    .active {
      background: #486ff2;
      color: #fff;
    }
  }
}

.drawer-footer {
  position: fixed;
  bottom: 20px;
  right: 10px;
}
</style>
