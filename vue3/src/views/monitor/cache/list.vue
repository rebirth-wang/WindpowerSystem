<template>
  <div class="monitor-cache-list">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card style="height: calc(100vh - 90px)">
          <template #header>
            <span>{{ $t('system.cache.list.093478-0') }}</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="primary"
              link
              :icon="RefreshRight"
              @click="refreshCacheNames()"
            ></el-button>
          </template>
          <el-table
            ref="cacheTableRef"
            v-loading="loading"
            :data="cacheNames"
            :border="false"
            :height="tableHeight"
            highlight-current-row
            @row-click="getCacheKeys"
            style="width: 100%"
          >
            <el-table-column
              :label="$t('system.cache.list.093478-1')"
              width="55"
              align="center"
              type="index"
            ></el-table-column>
            <el-table-column
              :label="$t('system.cache.list.093478-2')"
              align="left"
              prop="cacheName"
              :show-overflow-tooltip="true"
              :formatter="nameFormatter"
            ></el-table-column>
            <el-table-column :label="$t('remark')" align="left" prop="remark" :show-overflow-tooltip="true" />
            <el-table-column :label="$t('opation')" width="165" align="center">
              <template #default="scope">
                <el-button
                  size="small"
                  link
                  :icon="Delete"
                  @click.stop="handleClearCacheName(scope.row)"
                  v-hasPermi="['monitor:cache:remove']"
                >
                  {{ $t('del') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card style="height: calc(100vh - 90px)">
          <template #header>
            <span>{{ $t('system.cache.list.093478-3') }}</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="primary"
              link
              :icon="RefreshRight"
              @click="refreshCacheKeys()"
            ></el-button>
          </template>
          <el-table
            ref="keyTableRef"
            v-loading="subLoading"
            :data="cacheKeys"
            :border="false"
            :height="tableHeight"
            highlight-current-row
            @row-click="handleCacheValue"
            style="width: 100%"
          >
            <el-table-column
              :label="$t('system.cache.list.093478-1')"
              width="60"
              align="center"
              type="index"
            ></el-table-column>
            <el-table-column
              :label="$t('system.cache.list.093478-4')"
              align="left"
              :show-overflow-tooltip="true"
              :formatter="keyFormatter"
            ></el-table-column>
            <el-table-column :label="$t('opation')" width="165" align="center">
              <template #default="scope">
                <el-button
                  size="small"
                  link
                  :icon="Delete"
                  @click.stop="handleClearCacheKey(scope.row)"
                  v-hasPermi="['monitor:cache:remove']"
                >
                  {{ $t('del') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card :bordered="false" style="height: calc(100vh - 90px)">
          <template #header>
            <span>{{ $t('system.cache.list.093478-5') }}</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="primary"
              link
              :icon="Delete"
              @click="handleClearCacheAll()"
              v-hasPermi="['monitor:cache:remove']"
            >
              {{ $t('system.cache.list.093478-6') }}
            </el-button>
          </template>
          <el-form :model="cacheForm">
            <el-row :gutter="32">
              <el-col :offset="1" :span="22">
                <el-form-item :label="$t('system.cache.list.093478-7')" prop="cacheName">
                  <el-input v-model="cacheForm.cacheName" :readOnly="true" />
                </el-form-item>
              </el-col>
              <el-col :offset="1" :span="22">
                <el-form-item :label="$t('system.cache.list.093478-8')" prop="cacheKey">
                  <el-input v-model="cacheForm.cacheKey" :readOnly="true" />
                </el-form-item>
              </el-col>
              <el-col :offset="1" :span="22">
                <el-form-item :label="$t('system.cache.list.093478-9')" prop="cacheValue">
                  <el-input v-model="cacheForm.cacheValue" type="textarea" :rows="8" :readOnly="true" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import { RefreshRight, Delete } from '@element-plus/icons-vue';
import {
  listCacheName,
  listCacheKey,
  getCacheValue,
  clearCacheName,
  clearCacheKey,
  clearCacheAll,
} from '@/api/monitor/cache';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const cacheNames = ref<any[]>([]);
const cacheKeys = ref<any[]>([]);
const cacheForm = ref<any>({});
const loading = ref(true);
const subLoading = ref(false);
const nowCacheName = ref('');
const tableHeight = ref(window.innerHeight - 200);

const cacheTableRef = ref();
const keyTableRef = ref();

/** 查询缓存名称列表 */
function getCacheNames() {
  loading.value = true;
  listCacheName().then((res: any) => {
    if (res.code === 200) {
      cacheNames.value = res.data;
      if (cacheNames.value && cacheNames.value.length > 0) {
        cacheTableRef.value?.setCurrentRow(cacheNames.value[0]);
        getCacheKeys(cacheNames.value[0]);
      }
    }
    loading.value = false;
  });
}

/** 刷新缓存名称列表 */
function refreshCacheNames() {
  getCacheNames();
  proxy.$modal.msgSuccess(t('system.cache.list.093478-10'));
}

/** 清理指定名称缓存 */
function handleClearCacheName(row: any) {
  const cacheName = row.cacheName;
  proxy.$modal
    .confirm(t('system.cache.list.093478-16', [cacheName.replace(/:/g, '')]))
    .then(() => clearCacheName(cacheName))
    .then((res: any) => {
      if (res.code === 200) {
        getCacheKeys();
        proxy.$modal.msgSuccess(t('system.cache.list.093478-12'));
      } else {
        proxy.$modal.msgError(t('system.cache.list.093478-13'));
      }
    })
    .catch(() => {});
}

/** 查询缓存键名列表 */
function getCacheKeys(row?: any) {
  cacheKeys.value = [];
  cacheForm.value = {};
  const cacheName = row !== undefined ? row.cacheName : nowCacheName.value;
  if (cacheName === '') return;
  subLoading.value = true;
  listCacheKey(cacheName).then((res: any) => {
    if (res.code === 200) {
      cacheKeys.value = res.data;
      nowCacheName.value = cacheName;
      if (cacheKeys.value && cacheKeys.value.length > 0) {
        keyTableRef.value?.setCurrentRow(cacheKeys.value[0]);
        handleCacheValue(cacheKeys.value[0]);
      }
    }
    subLoading.value = false;
  });
}

/** 刷新缓存键名列表 */
function refreshCacheKeys() {
  getCacheKeys();
  proxy.$modal.msgSuccess(t('system.cache.list.093478-10'));
}

/** 清理指定键名缓存 */
function handleClearCacheKey(cacheKey: any) {
  proxy.$modal
    .confirm(t('system.cache.list.093478-16', [cacheKey]))
    .then(() => clearCacheKey(cacheKey))
    .then((res: any) => {
      if (res.code === 200) {
        getCacheKeys();
        proxy.$modal.msgSuccess(t('system.cache.list.093478-12'));
      } else {
        proxy.$modal.msgError(t('system.cache.list.093478-13'));
      }
    })
    .catch(() => {});
}

/** 列表前缀去除 */
function nameFormatter(row: any) {
  return row.cacheName.replace(':', '');
}

/** 键名前缀去除 */
function keyFormatter(cacheKey: any) {
  return cacheKey.replace(nowCacheName.value, '');
}

/** 查询缓存内容详细 */
function handleCacheValue(cacheKey: any) {
  getCacheValue(nowCacheName.value, cacheKey).then((response: any) => {
    cacheForm.value = response.data;
  });
}

/** 清理全部缓存 */
function handleClearCacheAll() {
  proxy.$modal
    .confirm(t('system.cache.list.093478-17'))
    .then(() => clearCacheAll())
    .then((res: any) => {
      if (res.code === 200) {
        proxy.$modal.msgSuccess(t('system.cache.list.093478-14'));
      } else {
        proxy.$modal.msgError(t('system.cache.list.093478-15'));
      }
    })
    .catch(() => {});
}

onMounted(() => {
  getCacheNames();
});
</script>

<style lang="scss" scoped>
.monitor-cache-list {
  padding: 20px;

  :deep(.el-table::before) {
    content: none;
  }
}
</style>
