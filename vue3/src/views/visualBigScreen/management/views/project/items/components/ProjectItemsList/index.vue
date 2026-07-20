<template>
  <div class="go-items-list">
    <el-card v-show="showSearch" class="search-card">
      <el-form @submit.prevent :model="queryParams" :inline="true" class="search-form">
        <el-form-item prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            clearable
            style="width: 192px"
            :placeholder="t('visualBigScreen.management-674210-16')"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('search') }}</el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" class="toolbar-row">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['goview:project:add']">
            {{ t('visualBigScreen.management-674210-20') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="deleteSelectedHandle" v-hasPermi="['goview:project:remove']">
            {{ t('visualBigScreen.management-674210-105') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="handleSearch"></right-toolbar>
      </el-row>

      <!-- 加载 -->
      <div v-show="loading">
        <go-loading></go-loading>
      </div>
      <!-- 列表 -->
      <div v-show="!loading">
        <n-grid
          v-if="list.length"
          :x-gap="20"
          :y-gap="20"
          cols="2 s:2 m:3 l:4 xl:4 xxl:4"
          responsive="screen"
        >
          <n-grid-item v-for="item in list" :key="item.id">
            <project-items-card
              :cardData="item"
              :selected="selectedIds.includes(item.id)"
              @preview="previewHandle"
              @resize="resizeHandle"
              @delete="deleteCardHandle"
              @release="releaseHandle"
              @edit="editHandle"
              @select="handleSelectionChange"
            ></project-items-card>
          </n-grid-item>
        </n-grid>
        <el-empty v-else />
      </div>

      <!-- 分页 -->
      <pagination
        style="margin: 20px 0 20px 0"
        v-show="paginat.count > 0"
        :total="paginat.count"
        v-model:page="paginat.page"
        v-model:limit="paginat.limit"
        :pageSizes="[12, 24, 36, 48]"
        @pagination="handleSearch"
      />
    </el-card>
  </div>

  <!-- model -->
  <project-items-modal-card
    v-if="modalData"
    :modalShow="modalShow"
    :cardData="modalData"
    @close="closeModal"
    @edit="editHandle"
  ></project-items-modal-card>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { ProjectItemsCard } from '../ProjectItemsCard/index'
import { ProjectItemsModalCard } from '../ProjectItemsModalCard/index'
import { useModalDataInit } from './hooks/useModal.hook'
import { useDataListInit } from './hooks/useData.hook'
import type { Chartype } from '../../index.d'

const { t } = useI18n()
const showSearch = ref(true)
const selectedIds = ref<Array<string | number>>([])
const multiple = computed(() => !selectedIds.value.length)

const { modalData, modalShow, closeModal, previewHandle, resizeHandle, editHandle } = useModalDataInit()
const {
  loading,
  queryParams,
  paginat,
  list,
  handleSearch,
  handleReset,
  handleAdd,
  releaseHandle,
  deleteHandle: removeProjectHandle
} = useDataListInit()

watch(list, value => {
  const currentIds = value.map(item => item.id)
  selectedIds.value = selectedIds.value.filter(id => currentIds.includes(id))
})

const handleSelectionChange = (cardData: Chartype, selected: boolean) => {
  if (!cardData) return
  if (selected) {
    if (!selectedIds.value.includes(cardData.id)) {
      selectedIds.value.push(cardData.id)
    }
    return
  }
  selectedIds.value = selectedIds.value.filter(id => id !== cardData.id)
}

const deleteSelectedHandle = async () => {
  if (!selectedIds.value.length) return
  const isDeleted = await removeProjectHandle(selectedIds.value)
  if (isDeleted) {
    selectedIds.value = []
  }
}

const deleteCardHandle = async (cardData: Chartype) => {
  const isDeleted = await removeProjectHandle(cardData)
  if (isDeleted) {
    selectedIds.value = selectedIds.value.filter(id => id !== cardData.id)
  }
}
</script>

<style lang="scss" scoped>
$contentHeight: 250px;
.go-items-list {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 120px);

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }

  .toolbar-row {
    margin-bottom: 20px;
  }

  .list-content {
    position: relative;
    height: $contentHeight;
  }
}
</style>
