<template>
  <div class="tool-bar">
    <div class="left-wrap">{{ productName }}</div>
    <div class="right-wrap">
      <el-button type="primary" link @click="handleReload">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/reset.png" />
          <span class="name">{{ $t('dragEditor.565720-10') }}</span>
        </div>
      </el-button>
      <el-button type="primary" link @click="handleExportJSON">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/export.png" />
          <span class="name">{{ $t('dragEditor.565720-11') }}</span>
        </div>
      </el-button>
      <el-button type="primary" link @click="fileRef?.click()">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/import.png" />
          <span class="name">{{ $t('dragEditor.565720-12') }}</span>
        </div>
      </el-button>
      <el-button type="primary" link @click="handleCatJson">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/cat_json.png" />
          <span class="name">{{ $t('dragEditor.565720-13') }}</span>
        </div>
      </el-button>
      <el-button type="primary" link @click="handlePreview">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/preview.png" />
          <span class="name">{{ $t('dragEditor.565720-14') }}</span>
        </div>
      </el-button>
      <el-button type="primary" link @click="handleSave">
        <div class="btn-wrap">
          <img width="22" height="22" src="@/assets/images/drag/save.png" />
          <span class="name">{{ $t('dragEditor.565720-15') }}</span>
        </div>
      </el-button>
      <input
        type="file"
        ref="fileRef"
        id="file"
        accept=".json"
        @change="handleImportJSONChange"
        style="display: none"
      />
    </div>
    <!-- 查看json -->
    <el-dialog :title="$t('dragEditor.565720-16')" v-model="dialogVisible" width="700px">
      <div class="JSONView">
        <div>
          {
          <br />
          "productId": {{ dragStore.id }},
          <br />
          "name": {{ dragStore.pageSetup.name }},
          <br />
          "templateJson": {{ JSON.stringify(dragStore.pageSetup) }},
          <br />
          "component": {{ JSON.stringify(dragStore.pageComponents) }},
          <br />
          }
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, getCurrentInstance } from 'vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import { saveAs } from 'file-saver';

const { proxy } = getCurrentInstance() as any;
const dragStore = useDragEditorStore();

const props = defineProps<{ productId: string }>();
const emit = defineEmits(['preview', 'save']);

const productName = ref(proxy.$t('dragEditor.565720-9'));
const dialogVisible = ref(false);
const fileRef = ref<HTMLInputElement | null>(null);

watch(
  () => props.productId,
  () => {
    dragStore.setId(props.productId);
  }
);

/** 重置 */
function handleReload() {
  proxy.$modal
    .confirm(proxy.$t('dragEditor.565720-17'))
    .then(async () => {
      await dragStore.resetPageComponents();
      dragStore.setRightcom('decorate');
    })
    .catch(() => {});
}

/** 查看json */
function handleCatJson() {
  dialogVisible.value = true;
}

/** 导入json */
function handleImportJSONChange() {
  const fileEl = document.getElementById('file') as HTMLInputElement;
  const file = fileEl?.files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.readAsText(file);
  reader.onload = function () {
    let ImportJSON = JSON.parse(this.result as string);
    const { productId, templateJson, component } = ImportJSON;
    dragStore.setId(productId);
    dragStore.setPageSetup(JSON.parse(templateJson));
    dragStore.setPageComponents(JSON.parse(component));
  };
}

/** 导出json */
function handleExportJSON() {
  const data = JSON.stringify({
    productId: dragStore.id,
    name: dragStore.pageSetup.name,
    templateJson: JSON.stringify(dragStore.pageSetup),
    component: JSON.stringify(dragStore.pageComponents),
  });
  const blob = new Blob([data], { type: '' });
  saveAs(blob, `${dragStore.pageSetup.name}.json`);
}

/** 预览 */
function handlePreview() {
  emit('preview', true);
}

/** 保存 */
function handleSave() {
  emit('save');
}
</script>

<style lang="scss" scoped>
.tool-bar {
  height: 100%;
  background: #ffffff;
  border-bottom: 1px solid #ebedf0;
  display: flex;
  justify-content: space-between;
  box-sizing: border-box;
  align-items: center;
  padding: 0 20px;

  .left-wrap {
    font-size: 18px;
    font-weight: 700;
    color: #555;
  }
  .right-wrap {
    display: flex;
    flex-direction: row;
    align-items: center;
    .btn-wrap {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 8px;
      .name {
        font-weight: 400;
        font-size: 12px;
        color: #323232;
        margin-top: 8px;
      }
    }
  }
}
.JSONView {
  width: 660px;
  padding: 0 20px 0 20px;
  height: 560px;
  overflow: auto;
}
</style>
