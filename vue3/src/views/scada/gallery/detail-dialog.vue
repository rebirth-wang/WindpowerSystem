<template>
  <el-dialog :title="!!id ? $t('update') : $t('add')" v-model="open" width="500px" append-to-body>
    <el-form v-loading="loading" :model="form" ref="formRef" :rules="rules" label-width="100px">
      <el-form-item :label="$t('scada.gallery.309456-13')" prop="fileName">
        <el-input
          v-model="form.fileName"
          :placeholder="$t('scada.gallery.309456-14')"
          clearable
          style="width: 300px"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('scada.gallery.309456-11')" prop="categoryName">
        <el-select
          v-model="form.categoryName"
          :placeholder="$t('scada.gallery.309456-12')"
          filterable
          clearable
          style="width: 300px"
        >
          <el-option
            v-for="item in dict.type.scada_gallery_type"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="handleSave">{{ $t('save') }}</el-button>
      <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, getCurrentInstance } from 'vue';
import { getGallery, updateGallery } from '@/api/scada/gallery';
import { useDict } from '@/utils/dict/useDict';

const props = defineProps({
  id: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits(['save']);

const { proxy } = getCurrentInstance() as any;
const { dict } = useDict('scada_gallery_type');

const loading = ref(false);
const open = ref(false);
const formRef = ref<any>(null);

const form = ref({
  categoryName: '',
  fileName: '',
});

const rules = {
  categoryName: [{ required: true, message: proxy.$t('scada.gallery.309456-12'), trigger: 'change' }],
  fileName: [{ required: true, message: proxy.$t('scada.gallery.309456-14'), trigger: 'change' }],
};

watch(
  () => props.id,
  (val) => {
    if (val) {
      getDetailData();
    }
  }
);

function getDetailData() {
  loading.value = true;
  getGallery(props.id)
    .then((res: any) => {
      if (res.code === 200) {
        form.value = res.data;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

function handleSave() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (props.id) {
        updateGallery(form.value).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            open.value = false;
            emit('save');
          } else {
            proxy.$modal.msgError(res.msg);
          }
        });
      }
    }
  });
}

function handleCancel() {
  open.value = false;
}

defineExpose({ open });
</script>
