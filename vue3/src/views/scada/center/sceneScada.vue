<template>
  <div class="scada-center-scene">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.native.prevent
        :model="queryParams"
        ref="queryForm"
        :inline="true"
        label-width="46px"
        class="search-form"
      >
        <el-form-item prop="pageName">
          <el-input
            v-model="queryParams.pageName"
            :placeholder="$t('scada.center.indeieScada.373453-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['scada:center:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['scada:center:query']">
            {{ $t('scada.center.sceneScada.098325-0') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['scada:center:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['scada:center:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList">
          <template #default>
            <div class="button-group">
              <el-tooltip effect="dark" :content="$t('device.index.105953-67')" placement="top">
                <el-button
                  size="small"
                  :icon="Tickets"
                  @click="handleShowTypeCard"
                  :class="['toggle-button card-button', { active: isListView }]"
                />
              </el-tooltip>
              <div class="separator"></div>
              <el-tooltip effect="dark" :content="$t('device.index.105953-68')" placement="top">
                <el-button
                  size="small"
                  :icon="Grid"
                  @click="handleShowTypeList"
                  :class="['toggle-button list-button', { active: !isListView }]"
                />
              </el-tooltip>
            </div>
          </template>
        </right-toolbar>
      </el-row>

      <div v-if="showType == 'card'" style="margin-top: 20px">
        <el-row :gutter="20" v-loading="loading">
          <el-col
            :xs="24"
            :sm="12"
            :md="12"
            :lg="8"
            :xl="6"
            style="margin-bottom: 20px"
            v-for="item in centerList"
            :key="item.id"
          >
            <el-checkbox-group v-model="ids" @change="checkboxChange">
              <el-card class="card-wrap" :body-style="{ padding: '0px', overflow: 'hidden' }">
                <div class="img-wrap">
                  <el-image
                    style="width: 100%; height: 100%; cursor: pointer"
                    :src="baseApi + item.pageImage"
                    fit="cover"
                    v-hasPermi="['scada:center:query']"
                    @click="goToDetail(item)"
                  ></el-image>
                </div>
                <div class="tag-wrap">
                  <span>{{ item.pageResolution ? item.pageResolution : $t('scada.center.indeieScada.373453-3') }}</span>
                </div>
                <div class="title-wrap">
                  <div class="name-wrap">
                    <el-checkbox :value="item.id" :key="item.id">
                      <span v-show="false">{{ $t('scada.component.302923-4') }}</span>
                    </el-checkbox>
                    <div class="name">
                      {{ item.pageName }}
                    </div>
                  </div>
                  <div class="time">
                    {{ parseTime(item.updateTime, '{y}-{m}-{d}') }}
                  </div>
                </div>
                <el-button-group class="tools-wrap">
                  <el-button
                    size="small"
                    link
                    :icon="Edit"
                    @click="handleUpdate(item)"
                    v-hasPermi="['scada:center:query']"
                  >
                    {{ $t('scada.center.sceneScada.098325-0') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:center:query']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="SetUp"
                    @click="goToDetail(item)"
                    v-hasPermi="['scada:center:query']"
                  >
                    {{ $t('scada.center.sceneScada.098325-1') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:center:preview']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="View"
                    @click="handlePreview(item)"
                    v-hasPermi="['scada:center:preview']"
                  >
                    {{ $t('scada.center.sceneScada.098325-2') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:center:share']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="Share"
                    @click="handleShare(item)"
                    v-hasPermi="['scada:center:share']"
                  >
                    {{ $t('scada.center.sceneScada.098325-3') }}
                  </el-button>
                  <span
                    style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                    v-hasPermi="['scada:center:remove']"
                  >
                    |
                  </span>
                  <el-button
                    size="small"
                    link
                    :icon="Delete"
                    @click="handleDelete(item)"
                    v-hasPermi="['scada:center:remove']"
                  >
                    {{ $t('del') }}
                  </el-button>
                </el-button-group>
              </el-card>
            </el-checkbox-group>
          </el-col>
        </el-row>
        <el-empty :description="$t('noData')" v-if="total == 0"></el-empty>
        <pagination
          style="margin: 0 0 20px 0"
          v-show="total > 0"
          :total="total"
          :page="queryParams.pageNum"
          @update:page="queryParams.pageNum = $event"
          :limit="queryParams.pageSize"
          @update:limit="queryParams.pageSize = $event"
          :pageSizes="[12, 24, 36, 60]"
          @pagination="getList"
        />
      </div>
      <div v-if="showType == 'list'" style="margin-top: 16px">
        <el-table v-loading="loading" :data="centerList" :border="false" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column
            :label="$t('scada.center.indeieScada.373453-5')"
            align="left"
            prop="pageName"
            min-width="240"
          />
          <el-table-column :label="$t('scada.center.indeieScada.373453-4')" align="center" prop="pageImage" width="100">
            <template #default="scope">
              <image-preview :src="scope.row.pageImage" :width="48" :height="48" />
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('scada.center.indeieScada.373453-6')"
            align="center"
            prop="pageResolution"
            width="120"
          >
            <template #default="scope">
              <span>
                {{ scope.row.pageResolution ? scope.row.pageResolution : $t('scada.center.indeieScada.373453-3') }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150" />
          <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150" />
          <el-table-column :label="$t('updateTime')" align="center" prop="updateTime" width="120">
            <template #default="scope">
              {{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('opation')" align="center" width="305">
            <template #default="scope">
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['scada:center:edit']"
              >
                {{ $t('scada.center.sceneScada.098325-0') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Message"
                @click="goToDetail(scope.row)"
                v-hasPermi="['scada:center:query']"
              >
                {{ $t('scada.center.sceneScada.098325-1') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="SetUp"
                @click="handlePreview(scope.row)"
                v-hasPermi="['scada:center:preview']"
              >
                {{ $t('scada.center.sceneScada.098325-2') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Share"
                @click="handleShare(scope.row)"
                v-hasPermi="['scada:center:share']"
              >
                {{ $t('scada.center.sceneScada.098325-3') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['scada:center:remove']"
              >
                {{ $t('del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          style="margin-bottom: 20px"
          v-show="total > 0"
          :total="total"
          :page="queryParams.pageNum"
          @update:page="queryParams.pageNum = $event"
          :limit="queryParams.pageSize"
          @update:limit="queryParams.pageSize = $event"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 添加或修改组态信息对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.open" width="600px" append-to-body>
      <el-form ref="dialogForm" :model="dialog.form" :rules="dialog.rules" label-width="100px">
        <el-form-item :label="$t('scada.center.indeieScada.373453-4')" prop="pageImage">
          <imageUpload ref="imageUploadRef" :value="dialog.form.pageImage" :limit="1" :fileSize="1" />
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-5')" prop="pageName">
          <el-input
            v-model="dialog.form.pageName"
            :placeholder="$t('scada.center.indeieScada.373453-7')"
            clearable
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-11')" prop="sceneModelId">
          <el-select
            v-model="dialog.form.sceneModelId"
            :placeholder="$t('scada.center.indeieScada.373453-12')"
            clearable
            filterable
            style="width: 400px"
          >
            <el-option v-for="item in sceneList" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('scada.center.indeieScada.373453-8')" prop="remark">
          <el-input
            v-model="dialog.form.remark"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
            :placeholder="$t('scada.center.indeieScada.373453-9')"
            clearable
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleDialogSubmit">{{ $t('confirm') }}</el-button>
        <el-button @click="handleDialogCancel">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
    <!-- 分享对话框 -->
    <scada-share-dialog
      ref="scadaShareDialog"
      :guid="scadaGuid"
      :type="2"
      v-model:visible="isScadaShare"
    ></scada-share-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  Download,
  Tickets,
  Grid,
  SetUp,
  Share,
  View,
  Message,
} from '@element-plus/icons-vue';
import auth from '@/plugins/auth';
import { getSceneModelList } from '@/api/scene/list.js';
import { listCenter, getCenter, delCenter, addCenter, updateCenter } from '@/api/scada/center';
import { parseTime } from '@/utils/ruoyi';
import ScadaShareDialog from '@/views/scada/common/ScadaShareDialog/index.vue';
import imageUpload from '@/components/ImageUpload/index.vue';
import { getRouteQueryNumber, getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const router = useRouter();

const baseApi = import.meta.env.VITE_APP_BASE_API;
const loading = ref(true);
const isListView = ref(true);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const ids = ref<any[]>([]);
const centerList = ref<any[]>([]);
const total = ref(0);
const showType = ref('card');
const sceneList = ref<any[]>([]);
const isScadaShare = ref(false);
const scadaGuid = ref('');

const dialogForm = ref<any>(null);

const queryParams = reactive({
  type: 2,
  pageName: null as any,
  pageNum: 1,
  pageSize: 12,
});

const dialog = reactive({
  open: false,
  title: '',
  form: {
    type: 2,
    pageImage: '',
    pageName: '',
    sceneModelId: '',
    remark: '',
  } as any,
  rules: {
    pageName: [{ required: true, message: proxy.$t('scada.center.indeieScada.373453-10'), trigger: 'blur' }],
    sceneModelId: [{ required: true, message: proxy.$t('scada.center.indeieScada.373453-12'), trigger: 'change' }],
  },
});

const uploadDisabled = computed(() => dialog.form.pageImage !== '');

function getSceneList() {
  getSceneModelList({ pageNum: 1, pageSize: 9999 }).then((res: any) => {
    sceneList.value = res.rows.map((item: any) => ({ value: item.sceneModelId, label: item.sceneModelName }));
  });
}

function getList() {
  loading.value = true;
  listCenter(queryParams).then((response: any) => {
    if (response.code === 200) {
      centerList.value = response.rows;
      total.value = response.total;
    }
    loading.value = false;
  });
}

function handleQuery() {
  ids.value = [];
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  ids.value = [];
  proxy.resetForm('queryForm');
  handleQuery();
}

function reset() {
  dialog.form = { type: 2, pageImage: '', pageName: '', remark: '' };
  proxy.resetForm('dialogForm');
}

function handleAdd() {
  reset();
  dialog.open = true;
  dialog.title = proxy.$t('scada.center.indeieScada.373453-13');
}

function handleUpdate(row: any) {
  dialog.title = proxy.$t('scada.center.indeieScada.373453-14');
  const id = row.id || ids.value;
  getCenter(id).then((res: any) => {
    if (res.code === 200) {
      dialog.form = res.data;
      dialog.open = true;
    }
  });
}

function handleDialogSubmit() {
  dialogForm.value.validate((valid: boolean) => {
    if (valid) {
      if (dialog.form.id != null) {
        updateCenter(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
            dialog.open = false;
            getList();
          }
        });
      } else {
        addCenter(dialog.form).then((res: any) => {
          if (res.code === 200) {
            proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
            dialog.open = false;
            getList();
          }
        });
      }
    }
  });
}

function handleDialogCancel() {
  dialog.open = false;
}

function handleDelete(row: any) {
  const delIds = row.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('scada.center.indeieScada.373453-15', [delIds]))
    .then(() => {
      loading.value = true;
      return delCenter(delIds);
    })
    .then(() => {
      loading.value = true;
      getList();
      if (Array.isArray(delIds)) ids.value = [];
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {});
}

function goToDetail(row: any) {
  const routeUrl = router.resolve({
    path: '/scada/topo/editor',
    query: { id: row.id, guid: row.guid, type: 2, sceneModelId: row.sceneModelId },
  });
  window.open(routeUrl.href, '_blank');
}

function handleShare(row: any) {
  isScadaShare.value = true;
  scadaGuid.value = row.guid;
}

function handlePreview(row: any) {
  const routeUrl = router.resolve({
    path: '/scada/topo/fullscreen',
    query: { guid: row.guid, type: 2, sceneModelId: row.sceneModelId },
  });
  window.open(routeUrl.href, '_blank');
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleShowTypeList() {
  ids.value = [];
  isListView.value = false;
  showType.value = 'list';
}

function handleShowTypeCard() {
  ids.value = [];
  isListView.value = true;
  showType.value = 'card';
}

function handleExport() {
  proxy.download(
    'scada/center/export',
    { ...queryParams },
    `${proxy.$t('scada.center.export.764059-0')}${new Date().getTime()}.xlsx`
  );
}

function checkboxChange(selection: any[]) {
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

onMounted(() => {
  getSceneList();
  getList();
  if (getRouteQueryString(route.query, 'sceneModelId') && auth.hasPermi('scada:center:add')) {
    handleAdd();
    dialog.form.sceneModelId = getRouteQueryNumber(route.query, 'sceneModelId', 0);
  }
});
</script>

<style lang="scss" scoped>
.scada-center-scene {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }

  .button-group {
    width: 65px;
    height: 32.5px;
    display: flex; /* 让按钮在同一行显示 */
    align-items: center; /* 垂直居中对齐 */
    margin-right: 12px;
    background: #ffffff;

    .toggle-button {
      width: 32.5px;
      height: 32.5px;
      display: flex; /* 使用 flexbox */
      justify-content: center; /* 水平居中 */
      align-items: center; /* 垂直居中 */
      border: 1px solid #dcdfe6;
      background: #fff;
    }

    :deep(.el-button):hover,
    .el-button:focus {
      color: unset;
    }

    .active {
      background-color: #e0e0e0; /* 被选中按钮的背景色 */
      border-color: #dcdfe6; /* 被选中按钮的边框色 */
    }

    .card-button {
      border-radius: 4px 0px 0px 4px !important;
    }

    .list-button {
      border-radius: 0px 4px 4px 0px !important;
    }

    .separator {
      width: 1px; /* 分割线的宽度 */
      height: 32px; /* 分割线的高度 */
      background: #dcdfe6; /* 分割线的颜色 */
    }
  }

  .card-wrap {
    position: relative;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #dcdfe6;

    .img-wrap {
      height: 159px;
      width: 100%;
    }

    .tag-wrap {
      position: absolute;
      top: 15px;
      right: 0;
      background: #486ff2;
      opacity: 0.7;
      border-radius: 2px 0px 0px 2px;
      padding: 12px 8px;
      font-size: 12px;
      color: #fff;
    }

    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 15px 15px 7px;

      .name-wrap {
        flex: 1;
        display: flex;
        flex-direction: row;
        align-items: center;
        width: 100%;
        overflow: hidden;

        .name {
          flex: 1 1 auto;
          font-weight: 500;
          font-size: 16px;
          color: #303133;
          line-height: 22px;
          white-space: nowrap; /* 不换行 */
          overflow: hidden; /* 隐藏超出部分 */
          text-overflow: ellipsis; /* 使用省略号 */
        }
      }

      .time {
        font-weight: 400;
        font-size: 12px;
        color: #909399;
        line-height: 15px;
        margin-left: 10px;
      }
    }

    .tools-wrap {
      padding: 0 10px 10px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
  }
}

.disable {
  :deep(.el-upload--picture-card) {
    display: none !important;
  }
}
</style>
