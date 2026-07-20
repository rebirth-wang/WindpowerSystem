<template>
  <div class="topo-editor-wrap" :style="{ height: contentHeight + 'px' }">
    <el-container class="container-wrap">
      <el-header class="header-wrap" height="70">
        <div class="left-wrap">
          <el-button type="text" @click="handleClick(3)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_copy.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-0') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(4)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_delete.png" />
              <span class="name">{{ $t('del') }}</span>
            </div>
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" @click="handleClick(13)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_top.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-1') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(14)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_bottom.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-2') }}</span>
            </div>
          </el-button>
          <el-dropdown class="ml10" @command="handleCommand">
            <el-button type="text">
              <div class="btn-wrap">
                <img width="24" height="24" src="@/assets/images/scada/editor_revolve.png" />
                <span class="name">{{ $t('scada.topo.editor.023345-3') }}</span>
              </div>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="Plus" :command="$t('scada.topo.editor.023345-4')">
                  {{ $t('scada.topo.editor.023345-4') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="Minus" :command="$t('scada.topo.editor.023345-5')">
                  {{ $t('scada.topo.editor.023345-5') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="DArrowLeft" :command="$t('scada.topo.editor.023345-6')">
                  {{ $t('scada.topo.editor.023345-6') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="DArrowRight" :command="$t('scada.topo.editor.023345-7')">
                  {{ $t('scada.topo.editor.023345-7') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="Edit" :command="$t('scada.topo.editor.023345-8')">
                  {{ $t('scada.topo.editor.023345-8') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown class="ml10" @command="handleCommandAlign">
            <el-button type="text">
              <div class="btn-wrap">
                <img width="24" height="24" src="@/assets/images/scada/editor_align.png" />
                <span class="name">{{ $t('scada.topo.editor.023345-9') }}</span>
              </div>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="ArrowLeft" command="left_align">
                  {{ $t('scada.topo.editor.023345-10') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="ArrowRight" command="right_align">
                  {{ $t('scada.topo.editor.023345-11') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="ArrowUp" command="top_align">
                  {{ $t('scada.topo.editor.023345-12') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="ArrowDown" command="bottom_align">
                  {{ $t('scada.topo.editor.023345-13') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="DArrowLeft" command="horizontal_space">
                  {{ $t('scada.topo.editor.023345-14') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="DArrowRight" command="vertical_space">
                  {{ $t('scada.topo.editor.023345-15') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="Sort" command="center_horizontal">
                  {{ $t('scada.topo.editor.023345-38') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="Sort" command="center_vertical">
                  {{ $t('scada.topo.editor.023345-39') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown class="ml10" @command="handleCommandMakeUP">
            <el-button type="text">
              <div class="btn-wrap">
                <img width="24" height="24" src="@/assets/images/scada/editor_combination.png" />
                <span class="name">{{ $t('scada.topo.editor.023345-16') }}</span>
              </div>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="Connection" :command="$t('scada.topo.editor.023345-16')">
                  {{ $t('scada.topo.editor.023345-16') }}
                </el-dropdown-item>
                <el-dropdown-item :icon="Link" :command="$t('scada.topo.editor.023345-17')">
                  {{ $t('scada.topo.editor.023345-17') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <!-- 添加显隐控制按钮 -->
          <el-dropdown class="ml10" @command="handleVisibilityCommand">
            <el-button type="text">
              <div class="btn-wrap">
                <img width="24" height="24" src="@/assets/images/scada/editor_eye.png" />
                <span class="name">{{ $t('topo.components.propertyBar.index.038495-230') }}</span>
              </div>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="show">
                  {{ $t('topo.components.propertyBar.index.038495-67') }}
                </el-dropdown-item>
                <el-dropdown-item command="hide">
                  {{ $t('topo.components.propertyBar.index.038495-68') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-divider direction="vertical"></el-divider>
          <el-button v-if="isLock" type="text" @click="handleLock($t('scada.topo.editor.023345-18'))">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_unlock.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-18') }}</span>
            </div>
          </el-button>
          <el-button v-else type="text" @click="handleLock($t('scada.topo.editor.023345-19'))">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_lock.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-19') }}</span>
            </div>
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" @click="handleClick(6)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_gallery.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-20') }}</span>
            </div>
          </el-button>
          <el-button v-if="scadaType == 3" type="text" @click="handleClick(8)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_device_binding.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-21') }}</span>
            </div>
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" @click="handleClick(18)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_import.png" />
              <span class="name">{{ $t('import') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(19)">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_export.png" />
              <span class="name">{{ $t('export') }}</span>
            </div>
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="text" @click="handleClick(16)" :disabled="isRevoke">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_quash.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-22') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(17)" :disabled="isRecovery">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_reflesh.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-23') }}</span>
            </div>
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-dropdown class="ml10" @command="handleCommandZoom">
            <el-button type="text">
              <span class="btn-wrap">
                <i style="color: #303133; height: 20px; font-weight: 550" size="100">{{ zoom }}%</i>
                <span class="name">{{ $t('scada.topo.editor.023345-24') }}</span>
              </span>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="25">25%</el-dropdown-item>
                <el-dropdown-item command="50">50%</el-dropdown-item>
                <el-dropdown-item command="75">75%</el-dropdown-item>
                <el-dropdown-item command="100">100%</el-dropdown-item>
                <el-dropdown-item command="125">125%</el-dropdown-item>
                <el-dropdown-item command="150">150%</el-dropdown-item>
                <el-dropdown-item command="175">175%</el-dropdown-item>
                <el-dropdown-item command="200">200%</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="right-wrap">
          <el-button type="text" @click="handleClick(1)" v-hasPermi="['scada:center:edit']">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_save.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-42') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(2)" v-hasPermi="['scada:center:preview']">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_preview.png" />
              <span class="name">{{ $t('preview') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleClick(20)" v-hasPermi="['scada:center:share']">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_share.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-40') }}</span>
            </div>
          </el-button>
          <el-button type="text" @click="handleHelpClick">
            <div class="btn-wrap">
              <img width="24" height="24" src="@/assets/images/scada/editor_help.png" />
              <span class="name">{{ $t('scada.topo.editor.023345-25') }}</span>
            </div>
          </el-button>
        </div>
      </el-header>
      <el-container>
        <el-aside
          class="aside-wrap left-aside-open"
          :class="{ 'aside-close': !leftAsideStatus }"
          :style="{ height: asideHeight + 'px' }"
        >
          <topo-unit-lib @tabClick="handleTabClick"></topo-unit-lib>
        </el-aside>
        <el-container :style="{ height: asideHeight + 'px' }">
          <el-main class="main-wrap">
            <!-- 侧边框按钮 -->
            <div class="left-aside-close-btn" @click="leftAsideChange">
              <el-icon v-if="leftAsideClose"><ArrowLeft /></el-icon>
              <el-icon v-else><ArrowRight /></el-icon>
            </div>
            <div class="right-aside-close-btn" @click="rightAsideChange">
              <el-icon v-if="rightAsideClose"><ArrowRight /></el-icon>
              <el-icon v-else><ArrowLeft /></el-icon>
            </div>
            <topo-main
              ref="topoMain"
              @menuClick="menuClick"
              @lockStatusChange="handleLockStatus"
              @recoveryFlagClick="recoveryFlagClick"
              @revokeFlagClick="revokeFlagClick"
              @onLayerWheel="handleLayerWheel"
              @selectComponentChange="handleSelectComponen"
            />
          </el-main>
        </el-container>
        <el-aside
          class="aside-wrap right-aside-open"
          :class="{ 'aside-close': !rightAsideStatus }"
          :style="{ height: asideHeight + 'px' }"
        >
          <topo-properties
            ref="topoProperties"
            class="topoProperties"
            :layerActiveIndex="selectedIndex"
            @lockStatusChange="handleLockStatus"
            @layerItemChange="handleLayerItem"
          />
        </el-aside>
      </el-container>
    </el-container>

    <el-dialog
      :title="$t('scada.topo.editor.023345-26')"
      v-model="isImgDialog"
      width="1080px"
      :close-on-click-modal="false"
    >
      <topo-select-image ref="topoSelectImage" />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isImgDialog = false">{{ $t('scada.topo.editor.023345-41') }}</el-button>
          <el-button type="primary" @click="handleSelectImage">{{ $t('select') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 设备绑定 -->
    <el-dialog
      :title="$t('scada.topo.editor.023345-21')"
      v-model="isDeviceBindDialog"
      width="700px"
      :close-on-click-modal="false"
    >
      <device-bind ref="deviceBind" />
    </el-dialog>
    <!-- 分享对话框 -->
    <scada-share-dialog
      ref="scadaShareDialog"
      :guid="scadaGuid"
      :type="scadaType"
      :productId="productId"
      v-model:visible="isScadaShare"
    ></scada-share-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import {
  Minus,
  DArrowLeft,
  DArrowRight,
  ArrowLeft,
  ArrowRight,
  ArrowUp,
  Sort,
  Link,
  Plus,
  Edit,
  Connection,
  ArrowDown,
} from '@element-plus/icons-vue';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import { getScadaRouteType, getRouteQueryNumber, getRouteQueryString } from '@/utils/topo/topoUtil';

import TopoUnitLib from './components/unitLibBar/index.vue';
import TopoMain from './components/topoMain.vue';
import TopoProperties from './components/propertyBar/index.vue';
import TopoSelectImage from './components/topoSelectImage.vue';
import DeviceBind from './components/deviceBind/index.vue';
import ScadaShareDialog from '@/views/scada/common/ScadaShareDialog/index.vue';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const topoEditorStore = useTopoEditorStore();

const scadaGuid = ref(getRouteQueryString(route.query, 'guid')); // 组态guid
const scadaType = ref(getScadaRouteType(route.query)); // 组态类型，默认 3
const productId = ref(getRouteQueryNumber(route.query, 'productId', 0));
const leftAsideStatus = ref(true);
const leftAsideClose = ref(true);
const rightAsideStatus = ref(true);
const rightAsideClose = ref(true);
const asideHeight = ref(window.innerHeight);
const isLock = ref(false); // 锁定/解锁
const isImgDialog = ref(false); // 图库对话框
const isDeviceBindDialog = ref(false); // 设备对话框
const isRevoke = ref(true); // 撤销
const isRecovery = ref(true); // 恢复
const zoom = ref(100); // 缩放大小
const contentHeight = ref(window.innerHeight);
const isScadaShare = ref(false); // 是否分享组态
const selectedIndex = ref<number | null>(null); // 选中组件序号

const topoMain = ref<any>(null);
const topoProperties = ref<any>(null);
const topoSelectImage = ref<any>(null);

function calculateContentHeight() {
  const el = document.getElementById('app');
  if (el) contentHeight.value = parseFloat(String(el.offsetHeight));
}

// tab 切换
function handleTabClick(tab: any) {
  console.log(tab);
}

// 侧边栏收缩与展开
function leftAsideChange() {
  leftAsideStatus.value = !leftAsideStatus.value;
  if (leftAsideStatus.value) {
    setTimeout(() => {
      leftAsideClose.value = true;
      topoMain.value?.initRuler();
    }, 500);
  } else {
    setTimeout(() => {
      leftAsideClose.value = false;
      topoMain.value?.initRuler();
    }, 500);
  }
}

function rightAsideChange() {
  rightAsideStatus.value = !rightAsideStatus.value;
  if (rightAsideStatus.value) {
    setTimeout(() => {
      rightAsideClose.value = true;
      topoMain.value?.initRuler();
    }, 500);
  } else {
    setTimeout(() => {
      rightAsideClose.value = false;
      topoMain.value?.initRuler();
    }, 500);
  }
}

// 获取侧边高度
function calculateAsideHeight() {
  const el = document.getElementById('app');
  if (el) asideHeight.value = parseFloat(String(el.offsetHeight - 70));
}

// 工具点击事件
function handleClick(index: number) {
  if (index == 1) {
    //保存
    topoMain.value?.handlePrintScadaData();
  } else if (index == 2) {
    //预览
    if (scadaType.value == 1) {
      proxy.$modal.msgError(proxy.$t('scada.topo.editor.023345-37'));
    } else {
      topoMain.value?.screenPreview();
    }
  } else if (index == 3) {
    //复制
    topoMain.value?.copyItem();
    topoMain.value?.pasteItem();
  } else if (index == 4) {
    //删除
    topoMain.value?.removeItem();
  } else if (index == 5) {
    //测试
    topoMain.value?.alignClick();
  } else if (index == 6) {
    //图库
    isImgDialog.value = true;
  } else if (index == 7) {
    //全屏
    // clickFullscreen();
  } else if (index == 8) {
    //设备绑定
    isDeviceBindDialog.value = true;
  } else if (index == 9) {
    //顺时针旋转
    topoMain.value?.transform?.(proxy.$t('scada.topo.editor.023345-27'));
  } else if (index == 10) {
    //逆时针旋转
    topoMain.value?.transform?.(proxy.$t('scada.topo.editor.023345-28'));
  } else if (index == 11) {
    //水平镜像
    topoMain.value?.transform?.(proxy.$t('scada.topo.editor.023345-29'));
  } else if (index == 12) {
    //垂直镜像
    topoMain.value?.transform?.(proxy.$t('scada.topo.editor.023345-30'));
  } else if (index == 13) {
    // 置顶（z 序最前，与属性栏一致）
    topoMain.value?.stackOrder?.('置顶');
  } else if (index == 14) {
    // 置底（z 序最后）
    topoMain.value?.stackOrder?.('置底');
  } else if (index == 15) {
    // 自定义角度
    const firstId = topoEditorStore.selectedComponents[0];
    const firstComp = firstId ? topoEditorStore.selectedComponentMap[firstId] : null;
    if (!firstComp) {
      proxy.$modal.msgWarning(proxy.$t('scada.topoMain.320129-9'));
      return;
    }
    proxy.$modal
      .prompt('', proxy.$t('scada.topo.editor.023345-31'), {
        confirmButtonText: proxy.$t('confirm'),
        cancelButtonText: proxy.$t('cancel'),
        closeOnClickModal: false,
        closeOnPressEscape: false,
        inputValue: String(firstComp.style?.transform ?? 0),
        inputErrorMessage: proxy.$t('scada.topo.editor.023345-32'),
        inputValidator: (value: string) => {
          if (!value) return proxy.$t('scada.topo.editor.023345-33');
          if (isNaN(Number(value))) return proxy.$t('scada.topo.editor.023345-34');
          return true;
        },
      })
      .then(({ value }: { value: string }) => {
        topoMain.value?.transform?.(proxy.$t('scada.topo.editor.023345-35'), value);
      })
      .catch(() => {});
  } else if (index == 16) {
    // 撤销
    topoMain.value?.handleRevoke();
  } else if (index == 17) {
    // 恢复
    topoMain.value?.handleRecovery();
  } else if (index == 18) {
    // 导入
    topoMain.value?.handleImport();
  } else if (index == 19) {
    // 导出
    topoMain.value?.handleDownLoad();
  } else if (index == 20) {
    // 分享
    isScadaShare.value = true;
  }
}

// 旋转
function handleCommand(command: string) {
  if (command == proxy.$t('scada.topo.editor.023345-4')) handleClick(9);
  else if (command == proxy.$t('scada.topo.editor.023345-5')) handleClick(10);
  else if (command == proxy.$t('scada.topo.editor.023345-6')) handleClick(11);
  else if (command == proxy.$t('scada.topo.editor.023345-7')) handleClick(12);
  else if (command == proxy.$t('scada.topo.editor.023345-8')) handleClick(15);
}

// 对齐
function handleCommandAlign(command: string) {
  topoMain.value?.alignClick(command);
}

// 组合
function handleCommandMakeUP(command: string) {
  topoMain.value?.makeUpClick(command);
}

// 锁定/解锁
function handleLock(command: string) {
  topoMain.value?.handleLock(command);
}

// 显隐控制命令处理
function handleVisibilityCommand(command: string) {
  topoMain.value?.handleVisibilityCommand(command);
}

// 锁定/解锁状态
function handleLockStatus(val: boolean) {
  isLock.value = val;
}

// 图库选择
function handleSelectImage() {
  const selectImage = topoSelectImage.value?.handleChoice();
  if (!selectImage || selectImage.length === 0) {
    proxy.$modal.msgWarning(proxy.$t('scada.topo.editor.023345-36'));
    return;
  }
  selectImage.forEach((element: any) => {
    topoMain.value?.addImageData(element.resourceUrl);
  });
  proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
  isImgDialog.value = false;
  topoSelectImage.value?.clearChoice();
}

// 撤销标志位
function revokeFlagClick(flag: boolean) {
  isRevoke.value = flag;
}

// 恢复标志位
function recoveryFlagClick(flag: boolean) {
  isRecovery.value = flag;
}

// 缩放
function handleCommandZoom(command: string) {
  zoom.value = Number(command);
  topoMain.value?.handleZoom(parseInt(command));
}

// 帮助
function handleHelpClick() {
  window.open('https://fastbee.cn/doc/');
}

// 右键菜单点击了
function menuClick(val: string) {
  if (val == proxy.$t('scada.topo.editor.023345-4')) handleClick(9);
  else if (val == proxy.$t('scada.topo.editor.023345-5')) handleClick(10);
  else if (val == proxy.$t('scada.topo.editor.023345-6')) handleClick(11);
  else if (val == proxy.$t('scada.topo.editor.023345-7')) handleClick(12);
  else if (val == proxy.$t('scada.topo.editor.023345-8')) handleClick(15);
  else if (val == proxy.$t('scada.topo.editor.023345-1')) handleClick(13);
  else if (val == proxy.$t('scada.topo.editor.023345-2')) handleClick(14);
  else if (val == proxy.$t('preview')) handleClick(2);
  else if (val == proxy.$t('save')) handleClick(1);
  else if (val == proxy.$t('scada.topo.editor.023345-20')) handleClick(6);
}

// ctrl + com 滚动鼠标
function handleLayerWheel(event: WheelEvent) {
  if (event.deltaY < 0) {
    if (zoom.value < 200) {
      zoom.value = parseInt(String(zoom.value)) + 25;
      topoMain.value?.handleZoom(parseInt(String(zoom.value)));
    }
  } else {
    if (zoom.value > 25) {
      zoom.value = parseInt(String(zoom.value)) - 25;
      topoMain.value?.handleZoom(parseInt(String(zoom.value)));
    }
  }
}

// 按键按下监听
function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'F12' || event.keyCode === 123) {
    topoMain.value?.screenPreview();
    event.preventDefault();
  }
}

// 图层组件点击：同步列表高亮并在画布选中对应组件
function handleLayerItem(data: { item: any; index: number | string }) {
  const { item, index } = data;
  const indexNum = typeof index === 'number' ? index : Number.parseInt(String(index), 10);
  if (!Number.isFinite(indexNum)) return;
  selectedIndex.value = indexNum;
  topoMain.value?.clickItem(item, indexNum);
}

// 编辑器组件选择
function handleSelectComponen(data: { index: number }) {
  selectedIndex.value = data.index;
}

onMounted(() => {
  window.addEventListener('resize', calculateAsideHeight, true);
  window.addEventListener('keydown', handleKeyDown, false);
  window.addEventListener('resize', calculateContentHeight, true);
  calculateAsideHeight();
  calculateContentHeight();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', calculateAsideHeight, true);
  window.removeEventListener('keydown', handleKeyDown, false);
  window.removeEventListener('resize', calculateContentHeight, true);
});
</script>

<style lang="scss" scoped>
.topo-editor-wrap {
  margin: 0;
  padding: 0;
  height: calc(100vh - 84px);
  width: 100%;

  .container-wrap {
    height: 100%;
    background: #f1f3f4;

    .header-wrap {
      height: 70px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      background: #ffffff;
      border-bottom: 1px solid #dcdfe6;

      :deep(.el-button) {
        margin-left: 10px;

        &:first-child {
          margin: 0;
        }

        &:focus-visible {
          outline: unset;
        }
      }

      :deep(.el-divider) {
        height: 20px;
        margin: 0 0 0 10px;
      }

      .left-wrap,
      .right-wrap {
        display: flex;
        flex-direction: row;
        align-items: center;

        .btn-wrap {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 0 7px;

          .name {
            font-weight: 400;
            font-size: 14px;
            color: #323232;
            line-height: 22px;
          }
        }
      }
    }

    .aside-wrap {
      margin-bottom: 0;
      padding: 0;
      background: #ffffff;
    }

    .left-aside-open {
      width: 300px !important;
      transition: width 0.5s;
    }

    .right-aside-open {
      width: 270px !important;
      transition: width 0.5s;
    }

    .aside-close {
      width: 0px !important;
    }

    .main-wrap {
      position: relative;
      margin: 0;
      padding: 0;

      .left-aside-close-btn {
        position: absolute;
        left: 0;
        top: 50%;
        width: 14px;
        height: 50px;
        line-height: 50px;
        background-color: #c0c4cc8f;
        color: #fff;
        border-radius: 0 6px 6px 0;
        z-index: 1000;
        cursor: pointer;
        font-size: 14px;
        margin-top: -50px;
      }

      .left-aside-close-btn:hover {
        color: #ccc;
        background-color: #f1f3f4;
      }

      .right-aside-close-btn {
        position: absolute;
        right: 0;
        top: 50%;
        width: 14px;
        height: 50px;
        line-height: 50px;
        background-color: #c0c4cc8f;
        color: #fff;
        border-radius: 6px 0 0 6px;
        z-index: 1000;
        cursor: pointer;
        font-size: 14px;
        margin-top: -50px;
      }

      .right-aside-close-btn:hover {
        color: #ccc;
        background-color: #f1f3f4;
      }
    }
  }

  ::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }

  ::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }

  ::-webkit-scrollbar {
    width: 5px;
    height: 5px;
    position: absolute;
  }

  ::-webkit-scrollbar-track {
    background-color: #fff;
  }
}
</style>
