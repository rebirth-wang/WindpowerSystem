<template>
  <div>
    <div class="scene-list">
      <el-card class="tools-wrap" v-show="showSearch" style="margin-bottom: 15px; width: 100%">
        <el-form
          v-show="showSearch"
          :model="queryParams"
          ref="queryRef"
          :inline="true"
          label-width="78px"
          style="margin-bottom: -18px; padding: 3px 0 0 0"
        >
          <el-form-item prop="deptId">
            <el-tree-select
              v-model="queryParams.deptId"
              :data="deptOptions"
              :props="{ value: 'id', label: 'label', children: 'children' } as any"
              :clearable="true"
              :render-after-expand="false"
              check-strictly
              filterable
              :placeholder="$t('scene.list.index.079839-14')"
              style="width: 192px"
            />
          </el-form-item>
          <el-form-item prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="$t('scene.list.index.079839-1')"
              clearable
              @keyup.enter="handleQuery"
              style="width: 192px"
            >
              <el-option :label="$t('scene.list.index.079839-2')" :value="0"></el-option>
              <el-option :label="$t('scene.edit.202832-18')" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="sceneModelName">
            <el-input
              v-model="queryParams.sceneModelName"
              :placeholder="$t('scene.edit.202832-5')"
              clearable
              @keyup.enter="handleQuery"
              style="width: 192px"
            />
          </el-form-item>
          <div style="float: right">
            <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
            <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
          </div>
        </el-form>
      </el-card>

      <el-card>
        <el-row :gutter="10" style="margin-bottom: 16px">
          <el-col :span="1.5">
            <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['scene:model:add']">
              {{ $t('add') }}
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['scene:model:edit']">
              {{ $t('update') }}
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              plain
              :icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['scene:model:remove']"
            >
              {{ $t('del') }}
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getSceneListDatas"></right-toolbar>
        </el-row>

        <el-table
          v-loading="loading"
          :data="sceneList"
          :border="false"
          @selection-change="handleSelectionChange"
          ref="multipleTableRef"
          :row-key="getRowKeys"
        >
          <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
          <el-table-column :label="$t('scene.edit.202832-1')" align="left" prop="sceneModelName" width="200" />
          <el-table-column :label="$t('scene.index.670805-8')" align="center" prop="status" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'danger' : 'success'">
                {{ scope.row.status === 0 ? $t('scene.list.index.079839-2') : $t('scene.edit.202832-18') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('scene.edit.202832-2')" align="center" prop="deptName" width="200" />
          <el-table-column :label="$t('scene.list.index.079839-3')" align="center" prop="deviceTotal" width="100" />
          <el-table-column :label="$t('scene.list.index.079839-4')" align="left" prop="sceneDesc" width="210" />
          <el-table-column :label="$t('scene.list.index.079839-5')" align="center" prop="createBy" width="110" />
          <el-table-column :label="$t('scene.list.index.079839-6')" align="center" prop="updateTime" min-width="160" />
          <el-table-column
            fixed="right"
            :label="$t('opation')"
            align="center"
            class-name="small-padding fixed-width"
            width="350"
          >
            <template #default="scope">
              <el-button
                link
                :icon="View"
                size="small"
                @click="handleDetail(scope.row)"
                v-hasPermi="['scene:model:query']"
              >
                {{ $t('look') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleEdit(scope.row)"
                v-hasPermi="['scene:model:edit']"
              >
                {{ $t('edit') }}
              </el-button>
              <el-button
                size="small"
                link
                v-if="isShowScada"
                :icon="Box"
                @click="handleScadaDesign(scope.row)"
                v-hasPermi="['scene:model:scada:design']"
              >
                {{ $t('scene.list.index.079839-7') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="CreditCard"
                v-if="isShowScada"
                @click="handleScadaRun(scope.row)"
                v-hasPermi="['scene:model:scada:run']"
              >
                {{ $t('scene.list.index.079839-8') }}
              </el-button>
              <el-button size="small" link @click="handleDelete(scope.row)" v-hasPermi="['scene:model:remove']">
                {{ $t('del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          style="margin-bottom: 20px"
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getSceneListDatas"
        />
      </el-card>
    </div>

    <!-- 添加或修改场景对话框 -->
    <el-dialog class="scene-add-dialog" :title="dialog.title" v-model="dialog.open" width="600px" append-to-body>
      <el-form ref="dialogFormRef" :model="dialog.form" :rules="dialog.rules" label-width="100px">
        <el-form-item :label="$t('scene.edit.202832-1')" prop="sceneModelName">
          <el-input
            v-model="dialog.form.sceneModelName"
            :placeholder="$t('scene.edit.202832-5')"
            clearable
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('scene.edit.202832-4')" prop="sceneDesc">
          <el-input
            v-model="dialog.form.sceneDesc"
            :placeholder="$t('scene.edit.202832-7')"
            clearable
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('scene.list.index.079839-9')" prop="imgUrl">
          <image-upload v-model="dialog.form.imgUrl" :multiple="false" :class="{ disable: uploadDisabled }" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleDialogSubmit">{{ $t('confirm') }}</el-button>
          <el-button @click="handleDialogCancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Edit, Delete, View, Box, CreditCard } from '@element-plus/icons-vue';
import { deptsTreeSelect } from '@/api/system/user';
import {
  getSceneModelList,
  getSceneModelDetail,
  addSceneModel,
  updateSceneModel,
  deleteSceneModel,
} from '@/api/scene/list';
import defaultSettings from '@/settings';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const router = useRouter();

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const deptOptions = ref<any[]>([]);
const isShowScada = defaultSettings.isShowScada;
const sceneList = ref<any[]>([]);
const total = ref(0);
const multipleTableRef = ref<any>(null);
const dialogFormRef = ref<any>(null);
const queryRef = ref<any>(null);

const queryParams = reactive({
  deptId: null as any,
  status: null as any,
  sceneModelName: '',
  pageNum: 1,
  pageSize: 10,
});

const dialog = reactive<any>({
  open: false,
  title: '',
  form: { imgUrl: '', sceneModelName: '', deptId: null, sceneDesc: '' },
  rules: {
    sceneModelName: [{ required: true, message: t('scene.edit.202832-5'), trigger: 'blur' }],
  },
});

const uploadDisabled = computed(() => dialog.form.imgUrl !== '');

function getDeptTree() {
  deptsTreeSelect().then((res: any) => {
    if (res.code === 200) deptOptions.value = res.data;
    else proxy.$modal.msgError(res.msg);
  });
}

function getSceneListDatas() {
  loading.value = true;
  getSceneModelList(queryParams).then((res: any) => {
    if (res.code === 200) {
      sceneList.value = res.rows;
      total.value = res.total;
    } else proxy.$modal.msgError(res.msg);
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getSceneListDatas();
}
function handleResetQuery() {
  queryRef.value.resetFields();
  handleQuery();
}

function handleAdd() {
  reset();
  dialog.open = true;
  dialog.title = t('scene.list.index.079839-10');
}

function reset() {
  dialog.form = { imgUrl: '', sceneModelName: '', deptId: null, sceneDesc: '' };
  dialogFormRef.value?.resetFields();
}

function handleDialogSubmit() {
  dialogFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (dialog.form.sceneModelId != null) {
        updateSceneModel(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('updateSuccess'));
            dialog.open = false;
            getSceneListDatas();
          } else proxy.$modal.msgError(res.msg);
        });
      } else {
        addSceneModel(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(t('addSuccess'));
            dialog.open = false;
            getSceneListDatas();
          } else proxy.$modal.msgError(res.msg);
        });
      }
    }
  });
}

function handleDialogCancel() {
  dialog.open = false;
}

function handleUpdate() {
  dialog.title = t('scene.list.index.079839-11');
  const id = ids.value;
  getSceneModelDetail(id).then((res: any) => {
    if (res.code === 200) {
      dialog.form = res.data;
      dialog.open = true;
    } else proxy.$modal.msgError(res.msg);
  });
}

function handleDelete(row?: any) {
  const delIds = row?.sceneModelId || ids.value;
  proxy.$modal
    .confirm(t('scene.list.index.079839-12', [delIds]))
    .then(() => {
      return deleteSceneModel(delIds);
    })
    .then(() => {
      getSceneListDatas();
      if (Array.isArray(delIds)) ids.value = [];
      proxy.$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.sceneModelId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleDetail(item: any) {
  router.push({ path: '/scene/list/detail', query: { sceneModelId: item.sceneModelId } });
}

function handleEdit(item: any) {
  router.push({ path: '/scene/list/edit', query: { id: item.sceneModelId } });
}

function handleScadaDesign(item: any) {
  const { scadaId, guid, sceneModelId } = item;
  if (guid) {
    const routeUrl = router.resolve({
      path: '/scada/topo/editor',
      query: { id: scadaId, guid, type: 2, sceneModelId },
    });
    window.open(routeUrl.href, '_blank');
  } else {
    router.push({ path: '/scada/center/scene', query: { sceneModelId: item.sceneModelId } });
  }
}

function handleScadaRun(item: any) {
  if (item.guid) {
    const routeUrl = router.resolve({
      // 以下组态特有
      path: '/scada/topo/fullscreen',
      // 以上组态特有
      query: { guid: item.guid, type: 2, sceneModelId: item.sceneModelId },
    });
    window.open(routeUrl.href, '_blank');
  } else {
    proxy.$modal.msgWarning(t('scene.list.index.079839-13'));
  }
}

function getRowKeys(row: any) {
  return row.sceneModelId;
}

onMounted(() => {
  getDeptTree();
  getSceneListDatas();
});
</script>

<style lang="scss" scoped>
.scene-list {
  padding: 20px;
}

.disable {
  :deep(.el-upload--picture-card) {
    display: none !important;
  }
}
</style>
