<template>
  <div class="scada-gallery">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card>
          <el-menu class="menu-wrap" :default-active="categoryTypes[0] && categoryTypes[0].dictValue">
            <el-sub-menu index="1">
              <template #title>
                <div class="submenu-title">
                  <el-icon><Menu /></el-icon>
                  <span>{{ $t('scada.gallery.309456-0') }}</span>
                </div>
              </template>
              <el-menu-item
                v-for="item in categoryTypes"
                :key="item.dictValue"
                :index="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
                @click="handleTypeClick(item.dictValue)"
              >
                {{ item.dictLabel }}
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="19">
        <el-card v-show="showSearch" class="search-card">
          <el-form
            @submit.prevent
            :model="queryParams"
            ref="queryForm"
            :inline="true"
            label-width="68px"
            class="search-form"
          >
            <el-form-item prop="fileName">
              <el-input
                v-model="queryParams.fileName"
                :placeholder="$t('scada.gallery.309456-2')"
                clearable
                style="width: 192px"
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item prop="sysFlag">
              <el-select
                v-model="queryParams.sysFlag"
                :placeholder="$t('template.index.891112-124')"
                clearable
                style="width: 192px"
                @keyup.enter="handleQuery"
              >
                <el-option
                  v-for="item in dict.type.system_type_status"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <div style="float: right">
              <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
              <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
            </div>
          </el-form>
        </el-card>

        <el-card>
          <el-row :gutter="10" style="margin-bottom: 10px">
            <el-col :span="1.5">
              <el-upload
                ref="uploadRef"
                :action="upload.uploadUrl"
                :headers="upload.headers"
                :before-upload="beforeUpload"
                :limit="500"
                :on-success="handleAvatarSuccess"
                :show-file-list="false"
                :file-list="upload.imageList"
                multiple
              >
                <el-button
                  type="primary"
                  plain
                  :icon="Upload"
                  @click="handleUploadFile"
                  v-hasPermi="['scada:gallery:add']"
                >
                  {{ $t('scada.gallery.309456-3') }}
                </el-button>
              </el-upload>
            </el-col>
            <el-col :span="1.5">
              <el-button plain :icon="Edit" :disabled="single" @click="handleEdit" v-hasPermi="['scada:gallery:query']">
                {{ $t('update') }}
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                plain
                :icon="Delete"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['scada:gallery:remove']"
              >
                {{ $t('del') }}
              </el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>

          <div class="content-wrap">
            <el-row :gutter="12" v-loading="loading" v-if="total !== 0">
              <el-col
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
                :xl="4"
                style="margin-bottom: 6px"
                v-for="item in galleryList"
                :key="item.id"
              >
                <el-checkbox-group class="img-box-wrap" v-model="checkImages" @change="checkboxChange">
                  <el-card class="img-card" :body-style="{ padding: 0 }">
                    <img class="img" :src="baseApi + item.resourceUrl" />
                    <div class="name-wrap">
                      <span>{{ item.fileName }}</span>
                    </div>
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$t('template.index.891112-21')"
                      placement="top"
                      v-if="item.sysFlag === 1 && !isAdmin"
                    >
                      <el-checkbox
                        class="checkbox"
                        :value="item.id"
                        :key="item.id"
                        :disabled="item.sysFlag === 1 && !isAdmin"
                      >
                        <span v-show="false">{{ $t('scada.gallery.309456-4') }}</span>
                      </el-checkbox>
                    </el-tooltip>
                    <el-checkbox class="checkbox" v-else :value="item.id" :key="item.id">
                      <span v-show="false">{{ $t('scada.gallery.309456-4') }}</span>
                    </el-checkbox>
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
              @pagination="getList"
              :page-sizes="[30, 60, 90]"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改文件 -->
    <DetailDialog ref="detailDialog" :id="fileId" @save="handleDialogSave"></DetailDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Upload, Edit, Delete, Menu } from '@element-plus/icons-vue';
import DetailDialog from './detail-dialog.vue';
import { getDicts } from '@/api/system/dict/data';
import { listGallery, delGallery } from '@/api/scada/gallery';
import { getToken } from '@/utils/auth';
import { useDict } from '@/utils/dict/useDict';
import { useUserStore } from '@/stores/modules/user';

const { proxy } = getCurrentInstance() as any;
const { dict } = useDict('system_type_status');
const userStore = useUserStore();
const baseApi = import.meta.env.VITE_APP_BASE_API;

const isAdmin = ref(false);
const loading = ref(true);
const categoryTypes = ref<any[]>([]);
const showSearch = ref(true);
const single = ref(true);
const multiple = ref(true);
const galleryList = ref<any[]>([]);
const total = ref(0);
const checkImages = ref<any[]>([]);
const fileId = ref<number | null>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 30,
  categoryName: '',
  fileName: '',
  sysFlag: undefined as string | undefined,
  deptIdStrs: null as any,
  moduleGuid: proxy.$t('scada.gallery.309456-5'),
  resourceUrl: null as any,
  orderByColumn: 'id',
  isAsc: 'desc',
});

const upload = reactive({
  headers: { Authorization: 'Bearer ' + getToken() },
  uploadUrl: '',
  imageList: [] as any[],
});

const detailDialog = ref<any>(null);
const queryForm = ref<any>(null);
const uploadRef = ref<any>(null);

function init() {
  if (userStore.roles.includes('admin')) {
    isAdmin.value = true;
  }
}

async function getDatas() {
  const dictType = 'scada_gallery_type';
  const res = await getDicts(dictType);
  categoryTypes.value = res.data || [];
  queryParams.categoryName = res.data && res.data[0] ? res.data[0].dictValue : '';
  getList();
}

function getList() {
  loading.value = true;
  listGallery(queryParams)
    .then((response: any) => {
      if (response.code === 200) {
        galleryList.value = response.rows;
        total.value = response.total;
        checkImages.value = [];
      }
    })
    .catch(() => {
      // Error handled silently, let UI handle empty state
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function handleResetQuery() {
  queryForm.value?.resetFields();
  handleQuery();
}

function handleTypeClick(type: string) {
  queryParams.pageNum = 1;
  queryParams.categoryName = type;
  checkImages.value = [];
  single.value = true;
  multiple.value = true;
  getList();
}

function checkboxChange(selection: any[]) {
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleUploadFile() {
  upload.uploadUrl = baseApi + '/scada/gallery/uploadFile' + '?categoryName=' + queryParams.categoryName;
}

function beforeUpload(file: any) {
  if (queryParams.categoryName === '') {
    proxy.$message({ message: proxy.$t('scada.gallery.309456-6'), type: 'warning' });
    uploadRef.value?.abort();
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 20;
  if (!isLt2M) {
    proxy.$message.error(proxy.$t('scada.gallery.309456-7'));
  }
  return isLt2M;
}

function handleAvatarSuccess(res: any) {
  if (res.code === 200) {
    proxy.$message.success(proxy.$t('uploadSuccess'));
    uploadRef.value?.clearFiles();
    getList();
  } else {
    proxy.$message.error(res.msg);
  }
}

function handleEdit(row: any) {
  fileId.value = row.id || checkImages.value[0];
  detailDialog.value.open = true;
}

function handleDialogSave() {
  getList();
}

function handleDelete() {
  const ids = checkImages.value;
  proxy
    .$confirm(proxy.$t('scada.gallery.309456-9'), proxy.$t('scada.gallery.309456-10'), {
      confirmButtonText: proxy.$t('confirm'),
      cancelButtonText: proxy.$t('cancel'),
      type: 'warning',
    })
    .then(() => {
      return delGallery(ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    })
    .catch(() => {
      // Error handled silently
    });
}

onMounted(() => {
  init();
  getDatas();
});
</script>

<style lang="scss" scoped>
.scada-gallery {
  padding: 20px;

  .menu-wrap {
    height: 970px;

    .submenu-title {
      padding: 0;
    }
  }

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }

  .content-wrap {
    padding: 0px;

    .img-box-wrap {
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      border-radius: 10px;
      align-content: center;
      padding: 0;
      margin-top: 10px;

      .img-card {
        width: 100%;
        height: auto;
        text-align: center;
        padding: 10px;
        position: relative;
        background: #ffffff;
        border-radius: 8px;
        border: 1px solid #dcdfe6;

        .img {
          width: 100px;
          height: 100px;
          margin-top: 5px;
        }

        .name-wrap {
          text-align: center;
          font-size: 11px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-top: 10px;
        }

        .checkbox {
          position: absolute;
          top: 0px;
          right: 0px;
        }
      }
    }
  }
}

::-webkit-scrollbar-thumb {
  background-color: #ddd;
}

::-webkit-scrollbar {
  width: 0px;
  height: 0px;
  position: absolute;
}

::-webkit-scrollbar-track {
  background-color: #ddd;
}

.el-menu {
  border-right: none;
}
</style>
