<template>
  <el-dialog :title="$t('product.thimgs-mopdel-list.738493-0')" v-model="open" width="700px">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
      <el-form-item prop="modelName">
        <el-input
          v-model="queryParams.modelName"
          :placeholder="$t('product.thimgs-mopdel-list.738493-2')"
          clearable
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" size="small" @click="handleQuery">
          {{ $t('product.thimgs-mopdel-list.738493-3') }}
        </el-button>
        <el-button :icon="Refresh" size="small" @click="resetQuery">
          {{ $t('product.thimgs-mopdel-list.738493-4') }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="modelList"
      @selection-change="handleSelectionChange"
      highlight-current-row
      height="50vh"
      ref="thingsModelTableRef"
      size="small"
      :border="false"
    >
      <el-table-column type="selection" width="55" align="center" :selectable="selectable" />
      <el-table-column
        :label="$t('product.thimgs-mopdel-list.738493-5')"
        align="left"
        prop="modelName"
        min-width="160"
      />
      <el-table-column
        :label="$t('product.thimgs-mopdel-list.738493-6')"
        align="left"
        prop="identifier"
        min-width="120"
      />
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="confirmSelectProduct" type="primary">
          {{ $t('product.thimgs-mopdel-list.738493-7') }}
        </el-button>
        <el-button @click="closeDialog" type="info">{{ $t('product.thimgs-mopdel-list.738493-8') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { getlListModbus } from '@/api/iot/model';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['productEvent']);

const props = defineProps({
  productId: { type: Number, default: 0 },
  justiceSelect: { type: String, default: 'isSelectData' },
});

const loading = ref(false);
const ids = ref<any[]>([]);
const total = ref(0);
const modelList = ref<any[]>([]);
const open = ref(false);
const queryParams = ref({ pageNum: 1, pageSize: 20, productId: 0, modelName: undefined as string | undefined });
const selectedList = ref<any[]>([]);
const thingsModelTableRef = ref<any>(null);

function getList() {
  loading.value = true;
  queryParams.value.productId = props.productId;
  getlListModbus(queryParams.value).then((response: any) => {
    modelList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    nextTick(() => {
      selectedList.value.forEach((selected) => {
        const findIndex = modelList.value.findIndex((model) => model.identifier === selected.identifier);
        if (findIndex !== -1) {
          const model = modelList.value[findIndex];
          model.isSelectData = false;
          model.isSelectIo = false;
          thingsModelTableRef.value?.toggleRowSelection(model, true);
        }
      });
    });
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.value.modelName = undefined;
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.identifier);
}

function confirmSelectProduct() {
  emit('productEvent', ids.value);
  open.value = false;
}

function closeDialog() {
  open.value = false;
}

function selectable(item: any) {
  return item[props.justiceSelect];
}

defineExpose({ open, getList, selectedList });
</script>
