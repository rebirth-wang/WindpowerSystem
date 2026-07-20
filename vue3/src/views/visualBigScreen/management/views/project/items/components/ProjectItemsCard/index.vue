<template>
  <div v-if="cardData" class="go-items-list-card">
    <el-card shadow="hover">
      <div class="list-content">
        <!-- 顶部按钮 -->
        <div class="list-content-top">
          <mac-os-control-btn
            class="top-btn"
            :hidden="controlHidden"
            @close="deleteHandle"
            @resize="resizeHandle"
          ></mac-os-control-btn>
          <el-checkbox
            v-hasPermi="permission.remove"
            :model-value="selected"
            @click.stop
            @change="selectHandle"
          ></el-checkbox>
        </div>
        <!-- 中间 -->
        <div class="list-content-img" @click="resizeHandle">
          <el-image fit="contain" style="height: 180px; width: 100%" :src="cardImageSrc">
            <template #error>
              <img class="image-error" :src="requireErrorImg()" />
            </template>
          </el-image>
        </div>
      </div>
      <template #footer>
        <div class="list-footer">
          <span class="list-footer-title">
            {{ cardData.title || cardData.id || t('visualBigScreen.management-674210-13') }}
          </span>
          <!-- 工具 -->
          <div class="list-footer-ri">
            <span class="list-footer-status">
              <span class="status-dot" :class="cardData.release ? 'is-release' : 'is-unrelease'"></span>
              {{
                cardData.release ? t('visualBigScreen.management-674210-0') : t('visualBigScreen.management-674210-1')
              }}
            </span>

            <el-tooltip :content="t('visualBigScreen.management-674210-4')" placement="bottom">
              <el-button v-hasPermi="permission.edit" size="small" @click="handleSelect('edit')">
                <el-icon><hammer-icon /></el-icon>
              </el-button>
            </el-tooltip>

            <el-dropdown v-if="hasMoreActions" trigger="hover" placement="bottom" @command="handleSelect">
              <el-button size="small">
                <el-icon><ellipsis-horizontal-circle-sharp-icon /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="hasPermission(permission.preview)" command="preview">
                    <el-icon><browsers-outline-icon /></el-icon>
                    <span>{{ t('visualBigScreen.management-674210-6') }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item v-if="hasPermission(permission.release)" command="release">
                    <el-icon><send-icon /></el-icon>
                    <span>
                      {{
                        cardData?.release
                          ? t('visualBigScreen.management-674210-9')
                          : t('visualBigScreen.management-674210-8')
                      }}
                    </span>
                  </el-dropdown-item>
                  <el-dropdown-item v-if="hasPermission(permission.remove)" command="delete">
                    <el-icon><trash-icon /></el-icon>
                    <span>{{ t('visualBigScreen.management-674210-7') }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <!-- end -->
          </div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue';
import { useI18n } from 'vue-i18n';
import { requireErrorImg, getLocalStorage, getCookie } from '@vb/utils';
import { icon } from '@vb/plugins/icon';
import { MacOsControlBtn } from '@vb/components/Tips/MacOsControlBtn';
import type { Chartype } from '../../index.d';
import { StorageEnum } from '@vb/enums/storageEnum';
import { useUserStore } from '@/stores/modules/user';
const { GO_SYSTEM_STORE } = StorageEnum;
const { EllipsisHorizontalCircleSharpIcon, BrowsersOutlineIcon, HammerIcon, SendIcon, TrashIcon } = icon.ionicons5;

const emit = defineEmits(['preview', 'delete', 'resize', 'edit', 'release', 'select']);

const props = defineProps({
  cardData: Object as PropType<Chartype>,
  selected: {
    type: Boolean,
    default: false,
  },
});

const { t } = useI18n();
const userStore = useUserStore();
const permission = {
  preview: ['goview:project:preview'],
  edit: ['goview:project:edit'],
  remove: ['goview:project:remove'],
  release: ['goview:project:release'],
};

// 获取 userId，用于判断是否可操作当前项目
const systemStore = getLocalStorage(GO_SYSTEM_STORE);
const userId = Number(systemStore?.userInfo?.userId || getCookie(StorageEnum.USER_ID) || 0);
const isAdmin = userId === 1;
const isOwner = computed(() => Number(props.cardData?.createId || 0) === userId);
const cardImageSrc = computed(() => {
  const imageUrl = props.cardData?.image || '';
  const version = props.cardData?.updateTime || props.cardData?.id || '';
  if (!imageUrl) return '';
  return imageUrl.includes('?') ? `${imageUrl}&v=${version}` : `${imageUrl}?v=${version}`;
});
const hasPermission = (perms: string[]) => {
  const permissions = userStore.permissions || [];
  return permissions.some((item: string) => item === '*:*:*' || perms.includes(item));
};
const controlHidden = computed(() => {
  const hidden = ['remove'];
  if (!hasPermission(permission.remove)) hidden.push('close');
  if (!hasPermission(permission.preview)) hidden.push('resize');
  return hidden;
});
const hasMoreActions = computed(() => {
  return hasPermission(permission.preview) || hasPermission(permission.release) || hasPermission(permission.remove);
});

const handleSelect = (key: string | number | object) => {
  const action = String(key);
  switch (action) {
    case 'preview':
      previewHandle();
      break;
    case 'delete':
      deleteHandle();
      break;
    case 'release':
      releaseHandle();
      break;
    case 'edit':
      editHandle();
      break;
  }
};

// 预览处理
const previewHandle = () => {
  if (!hasPermission(permission.preview)) return;
  emit('preview', props.cardData);
};

// 删除处理
const deleteHandle = () => {
  if (!hasPermission(permission.remove)) return;
  emit('delete', props.cardData);
};

// 编辑处理
const editHandle = () => {
  if (!hasPermission(permission.edit)) return;
  emit('edit', props.cardData);
};

// 发布处理
const releaseHandle = () => {
  if (!hasPermission(permission.release)) return;
  emit('release', props.cardData);
};

// 放大处理
const resizeHandle = () => {
  emit('resize', props.cardData);
};

const selectHandle = (value: string | number | boolean) => {
  emit('select', props.cardData, Boolean(value));
};
</script>

<style lang="scss" scoped>
$contentHeight: 180px;
.go-items-list-card {
  position: relative;
  border-radius: 12px;
  transition: all 0.25s ease;

  &:hover {
    transform: translateY(-2px);
  }

  :deep(.n-card) {
    border-radius: 12px;
  }

  :deep(.el-card) {
    border-radius: 12px;
  }

  :deep(.el-card__body) {
    padding: 14px;
  }

  :deep(.el-card__footer) {
    padding: 10px 14px;
  }

  .list-content {
    margin-top: 18px;
    margin-bottom: 10px;
    cursor: pointer;
    border-radius: 10px;
    background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);

    &-top {
      position: absolute;
      top: 10px;
      left: 10px;
      right: 10px;
      z-index: 2;
      height: 22px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }

    &-img {
      height: $contentHeight;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 10px;
      overflow: hidden;
      margin-top: 24px;

      :deep(.el-image) {
        width: 100%;
      }

      :deep(img) {
        border-radius: 10px;
        transition: transform 0.25s ease;
      }

      .image-error {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }
  }

  &:hover .list-content-img :deep(img) {
    transform: scale(1.02);
  }

  .list-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 2px 2px 0;

    &-title {
      flex: 1;
      min-width: 0;
      max-width: 50%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      color: #111827;
    }

    &-status {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      white-space: nowrap;
      font-size: 12px;
      color: #6b7280;
    }

    &-ri {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      flex-shrink: 0;
      min-width: 0;
      gap: 6px;
    }

    &-btn {
      width: 30px;
      height: 30px;
      border-radius: 8px;
    }

    :deep(.el-button:focus-visible) {
      outline: unset;
    }
  }
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-dot.is-release {
  background-color: #34c749;
}

.status-dot.is-unrelease {
  background-color: #fcbc40;
}
</style>
