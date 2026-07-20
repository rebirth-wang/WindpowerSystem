<template>
  <el-row class="topo-select-image" :gutter="10">
    <el-col :span="5">
      <el-menu class="menu-wrap" :default-active="categoryTypes[0] && categoryTypes[0].dictValue">
        <el-menu-item index="1" @click="handleClick('我的收藏')">
          <el-icon><Star /></el-icon>
          <template #title>{{ $t('topo.topoSelectImage.034642-0') }}</template>
        </el-menu-item>
        <el-sub-menu index="2">
          <template #title>
            <div class="submenu-title">
              <el-icon><Location /></el-icon>
              <span>{{ $t('topo.topoSelectImage.034642-1') }}</span>
            </div>
          </template>
          <el-menu-item
            v-for="item in categoryTypes"
            :key="item.dictValue"
            :index="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
            @click="handleClick(item.dictValue)"
          >
            {{ item.dictLabel }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-col>
    <el-col :span="19">
      <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
        <el-form-item prop="fileName">
          <el-input
            style="width: 182px"
            v-model="queryParams.fileName"
            :placeholder="$t('topo.topoSelectImage.034642-3')"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('search') }}
          </el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('reset') }}</el-button>
          <el-button type="warning" :icon="Star" :disabled="multiple" @click="handleCollection">
            {{ $t('topo.topoSelectImage.034642-4') }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8" v-if="queryParams.categoryName == $t('topo.topoSelectImage.034642-0')">
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
            <el-button type="primary" plain :icon="Upload" @click="handleUploadFile">
              <i class="el-icon-upload el-icon--right" />
              {{ $t('topo.topoSelectImage.034642-5') }}
            </el-button>
          </el-upload>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleDelete">
            {{ $t('del') }}
          </el-button>
        </el-col>
      </el-row>
      <div v-loading="loading">
        <div class="data-box" v-if="total !== 0">
          <el-checkbox-group class="img-box-wrap" v-model="checkImages" @change="checkboxChange">
            <el-card
              class="img-card"
              :style="{ margin: '0 10px 10px 0' }"
              v-for="item in uploadList"
              :body-style="{ padding: '5px' }"
              :key="item.id"
            >
              <img class="img" :src="baseApi + item.resourceUrl" />
              <div class="name-wrap">
                <span>{{ item.fileName }}</span>
              </div>
              <el-checkbox class="checkbox" :value="item" :key="item.id">
                <span v-show="false">{{ $t('scada.component.302923-4') }}</span>
              </el-checkbox>
            </el-card>
          </el-checkbox-group>
        </div>
        <el-empty :description="$t('noData')" v-if="total == 0"></el-empty>
      </div>
      <pagination
        v-show="total > 0"
        :total="total"
        :page="queryParams.pageNum"
        @update:page="queryParams.pageNum = $event"
        :limit="queryParams.pageSize"
        @update:limit="queryParams.pageSize = $event"
        @pagination="paginationChange"
        :page-sizes="[24, 48, 72]"
        :page-size="5"
      />
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { getDicts } from '@/api/system/dict/data';
import { listGallery } from '@/api/scada/gallery';
import { getFavoriteGallerys, delFavoritesGallery, favoritesGallery } from '@/api/scada/topo';
import { getToken } from '@/utils/auth';
import { Search, Refresh, Upload, Delete, Star, Location } from '@element-plus/icons-vue';

const props = defineProps({
  categoryName: String,
  message: String,
});

const { proxy } = getCurrentInstance() as any;
const baseApi = import.meta.env.VITE_APP_BASE_API;

const loading = ref(true);
const categoryTypes = ref<any[]>([]);
const multiple = ref(true);
const uploadList = ref<any[]>([]);
const total = ref(0);
const checkImages = ref<any[]>([]);
const ids = ref<string>('');

const queryParams = reactive({
  pageNum: 1,
  pageSize: 24,
  categoryName: '',
  fileName: '',
  moduleGuid: proxy.$t('scada.gallery.309456-5'),
  orderByColumn: 'id',
  isAsc: 'desc',
});

const upload = reactive({
  headers: { Authorization: 'Bearer ' + getToken() },
  uploadUrl: '',
  imageList: [] as any[],
});

const queryFormRef = ref<any>(null);
const uploadRef = ref<any>(null);

async function getDatas() {
  const dictType = 'scada_gallery_type';
  const res = await getDicts(dictType);
  categoryTypes.value = res.data || [];
  queryParams.categoryName = res.data && res.data[0] ? res.data[0].dictValue : '';
  getList();
}

function getList() {
  loading.value = true;
  listGallery(queryParams).then((res: any) => {
    if (res.code === 200) {
      uploadList.value = res.rows;
      total.value = res.total;
      checkImages.value = [];
    }
    loading.value = false;
  });
}

function getFavorites() {
  loading.value = true;
  getFavoriteGallerys(queryParams).then((res: any) => {
    if (res.code === 200) {
      uploadList.value = res.rows;
      total.value = res.total;
      checkImages.value = [];
    }
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  if (queryParams.categoryName === proxy.$t('topo.topoSelectImage.034642-0')) {
    getFavorites();
  } else {
    getList();
  }
}

function handleResetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function paginationChange() {
  if (queryParams.categoryName === proxy.$t('topo.topoSelectImage.034642-0')) {
    getFavorites();
  } else {
    getList();
  }
}

function checkboxChange(selection: any[]) {
  multiple.value = !selection.length;
  ids.value = selection.map((item) => item.id).join(',');
}

function handleCollection() {
  checkImages.value.forEach(() => {
    favoritesGallery({ idStr: ids.value }).then(() => {});
  });
  proxy.$message({ message: proxy.$t('topo.topoSelectImage.034642-6'), type: 'success' });
  checkImages.value = [];
}

function handleClick(label: string) {
  queryParams.categoryName = label;
  multiple.value = true;
  checkImages.value = [];
  if (queryParams.categoryName === proxy.$t('topo.topoSelectImage.034642-0')) {
    getFavorites();
  } else {
    getList();
  }
}

function handleUploadFile() {
  upload.uploadUrl = baseApi + '/scada/center/uploadGalleryFavorites' + '?categoryName=' + queryParams.categoryName;
}

function beforeUpload(file: any) {
  if (queryParams.categoryName == '') {
    proxy.$message({ message: proxy.$t('scada.gallery.309456-6'), type: 'warning' });
    uploadRef.value?.abort();
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 5;
  if (!isLt2M) {
    proxy.$message.error(proxy.$t('topo.topoSelectImage.034642-7'));
  }
  return isLt2M;
}

function handleAvatarSuccess(res: any) {
  if (res.code == 200) {
    proxy.$message.success(proxy.$t('scada.gallery.309456-8'));
    uploadRef.value?.clearFiles();
    getFavorites();
  } else {
    proxy.$message.error(res.msg);
  }
}

function handleDelete() {
  const deleteIds: number[] = [];
  checkImages.value.forEach((item) => {
    deleteIds.push(item.id);
  });
  proxy
    .$confirm(proxy.$t('topo.topoSelectImage.034642-8'), proxy.$t('topo.topoSelectImage.034642-9'), {
      confirmButtonText: proxy.$t('confirm'),
      cancelButtonText: proxy.$t('cancel'),
      type: 'warning',
    })
    .then(() => {
      return delFavoritesGallery(deleteIds);
    })
    .then(() => {
      if (queryParams.categoryName === proxy.$t('topo.topoSelectImage.034642-0')) {
        getFavorites();
      } else {
        getList();
      }
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
    });
}

function handleChoice() {
  return checkImages.value;
}

function clearChoice() {
  checkImages.value = [];
}

onMounted(() => {
  getDatas();
});

defineExpose({ handleChoice, clearChoice });
</script>
<style lang="scss" scoped>
.topo-select-image {
  .menu-wrap {
    margin-top: 5px;
    height: 500px;
    overflow-x: hidden;
    overflow-y: auto;

    .submenu-title {
      padding: 0 20px;
    }
  }

  .data-box {
    height: 407px;
    overflow-y: auto;
    overflow-x: hidden;

    .img-box-wrap {
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      align-content: center;

      .img-card {
        width: 153px;
        height: auto;
        text-align: center;
        padding: 10px;
        position: relative;

        .img {
          width: 80px;
          height: 80px;
          margin-top: 10px;
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
          top: 8px;
          right: 0px;
        }
      }
    }
  }
}

::-webkit-scrollbar {
  width: 2px;
  height: 3px;
  position: absolute;
}

::-webkit-scrollbar-thumb {
  background-color: #cccccc59;
}
</style>
