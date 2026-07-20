<template>
  <div class="iot-product">
    <!-- 搜索栏 -->
    <el-card v-show="showSearch" class="card-search">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item prop="productName">
          <el-input
            v-model="queryParams.productName"
            :placeholder="$t('product.index.091251-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="categoryName">
          <el-input
            v-model="queryParams.categoryName"
            :placeholder="$t('product.index.091251-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('product.index.091251-5')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in iot_product_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="product-card">
      <el-row :gutter="10" class="product-header">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAddProduct" v-hasPermi="['iot:product:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="DocumentCopy" @click="selectProduct" v-hasPermi="['iot:product:edit']">
            {{ $t('product.product-things-model.142341-92') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Upload" @click="handleImport" v-hasPermi="['iot:product:add']">
            {{ $t('import') }}
          </el-button>
        </el-col>
        <el-col :span="1.5" class="product-checkbox">
          <el-checkbox v-model="queryParams.showSenior" @change="handleQuery">
            <div class="el-checkbox__label">{{ $t('product.index.091251-8') }}</div>
          </el-checkbox>
          <el-tooltip :content="$t('product.index.091251-9')" placement="top">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
      <!-- 产品卡片 -->
      <el-row :gutter="20" v-loading="loading">
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="8"
          :xl="6"
          v-for="(item, index) in productListData"
          :key="index"
          class="product-item"
        >
          <el-card :body-style="{ padding: '0px' }" shadow="always" style="border-radius: 8px" class="card-item">
            <div class="item-title">
              <div>
                <el-image
                  class="img"
                  :preview-src-list="[baseUrl + item.imgUrl]"
                  :src="baseUrl + item.imgUrl"
                  fit="cover"
                  v-if="item.imgUrl != null && item.imgUrl != ''"
                />
                <el-image
                  class="img"
                  :preview-src-list="[gatewayImg]"
                  :src="gatewayImg"
                  fit="cover"
                  v-else-if="item.deviceType == 2"
                />
                <el-image
                  class="img"
                  :preview-src-list="[videoImg]"
                  :src="videoImg"
                  fit="cover"
                  v-else-if="item.deviceType == 3"
                />
                <el-image class="img" :preview-src-list="[productImg]" :src="productImg" fit="cover" v-else />
              </div>
              <div class="title">
                <div class="name" @click="handleEditProduct(item)">{{ item.productName }}</div>
                <div style="display: flex; align-items: center; gap: 5px">
                  <div class="tag">
                    <el-tag type="info" size="small" v-if="item.isSys == 1">{{ $t('product.index.091251-11') }}</el-tag>
                    <el-tag type="info" size="small" v-else>{{ item.tenantName }}</el-tag>
                  </div>
                  <div class="tag">
                    <el-tag type="info" size="small">{{ item.transport }}</el-tag>
                  </div>
                </div>
              </div>
              <div style="width: 45px"></div>
              <div class="status">
                <el-tooltip
                  effect="dark"
                  :content="$t('product.index.091251-12')"
                  placement="top-start"
                  v-if="item.status == 2"
                >
                  <el-button
                    plain
                    size="small"
                    class="btn-published"
                    :disabled="item.isOwner === 0"
                    @click="changeProductStatusFn(item.productId, 1, item.deviceType)"
                  >
                    <svg-icon icon-class="check_circle" />
                    {{ $t('product.index.091251-13') }}
                  </el-button>
                </el-tooltip>
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$t('product.index.091251-14')"
                  placement="top-start"
                  v-if="item.status == 1"
                >
                  <el-button
                    type="info"
                    size="small"
                    class="btn-unpublished"
                    :disabled="item.isOwner === 0"
                    @click="changeProductStatusFn(item.productId, 2, item.deviceType)"
                  >
                    <svg-icon icon-class="exclamation_circle" />
                    {{ $t('product.index.091251-15') }}
                  </el-button>
                </el-tooltip>
              </div>
            </div>

            <el-row :gutter="10">
              <el-col :span="12" class="card-item-desc" style="padding-left: 24px">
                <el-descriptions :column="1" size="small" class="card-item-desc-item">
                  <el-descriptions-item :label="$t('product.index.091251-16')">
                    <el-link type="primary" underline="never" class="product-category">
                      {{ item.categoryName }}
                    </el-link>
                  </el-descriptions-item>
                </el-descriptions>
              </el-col>
              <el-col :span="12" class="card-item-desc" style="padding-right: 24px">
                <el-descriptions :column="1" size="small" class="card-item-desc-item">
                  <el-descriptions-item :label="$t('product.index.091251-18')">
                    <dict-tag
                      :options="item.deviceType == 4 ? sub_gateway_type : iot_network_method"
                      :value="item.networkMethod"
                    />
                  </el-descriptions-item>
                </el-descriptions>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12" style="padding-left: 24px">
                <el-descriptions :column="1" size="small" class="card-item-desc-item">
                  <el-descriptions-item :label="$t('product.index.091251-17')">
                    <dict-tag :options="iot_device_type" :value="item.deviceType" />
                  </el-descriptions-item>
                </el-descriptions>
              </el-col>
              <el-col :span="12" style="padding-right: 24px">
                <el-descriptions :column="1" size="small" class="card-item-desc-item">
                  <el-descriptions-item :label="$t('product.index.091251-19')">
                    <el-tag size="small" v-if="item.isAuthorize == 1" class="tag-item-success">
                      {{ $t('product.index.091251-20') }}
                    </el-tag>
                    <el-tag
                      size="small"
                      v-else
                      class="tag-item-error"
                      style="color: #c0c4cc; border-radius: 2px; border: 1px solid #c0c4cc; background-color: #ffffff"
                    >
                      {{ $t('product.index.091251-21') }}
                    </el-tag>
                  </el-descriptions-item>
                </el-descriptions>
              </el-col>
            </el-row>
            <el-divider class="divider" />

            <div class="card-item-btns">
              <el-button size="small" link @click="handleEditProduct(item)" v-hasPermi="['iot:product:query']">
                {{ $t('product.index.091251-22') }}
              </el-button>
              <span class="btn-item-line" v-hasPermi="['iot:product:query']">|</span>
              <el-button size="small" link @click="handleViewDevice(item.productId)" v-hasPermi="['iot:device:query']">
                {{ $t('product.index.091251-24') }}
              </el-button>
              <span
                class="btn-item-line"
                v-hasPermi="['scada:center:share']"
                v-if="item.deviceType !== 3 && isShowScada == true"
              >
                |
              </span>
              <el-button
                v-if="item.deviceType !== 3 && isShowScada == true"
                size="small"
                link
                @click="handleScadaShare(item)"
                v-hasPermi="['scada:center:share']"
              >
                {{ $t('product.index.091251-42') }}
              </el-button>
              <span
                class="btn-item-line"
                v-hasPermi="['scada:center:edit']"
                v-if="item.deviceType !== 3 && isShowScada == true"
              >
                |
              </span>
              <el-button
                v-if="item.deviceType !== 3 && isShowScada == true"
                size="small"
                link
                @click="handleGoToScada(item)"
                v-hasPermi="['scada:center:edit']"
              >
                {{ $t('product.index.091251-40') }}
              </el-button>
              <span
                class="btn-item-line"
                v-hasPermi="['iot:product:remove']"
                v-if="item.status == 1 && item.isOwner != 0"
              >
                |
              </span>
              <el-button
                size="small"
                link
                @click="handleDelete(item)"
                v-hasPermi="['iot:product:remove']"
                v-if="item.status == 1 && item.isOwner != 0"
              >
                {{ $t('del') }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty :description="$t('product.index.091251-25')" v-if="total == 0" />
      <pagination
        style="margin: 0 0 20px 0"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :pageSizes="[12, 24, 36, 60]"
        @pagination="getList"
      />
    </el-card>
    <!-- 下载SDK -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-link type="danger" style="padding-left: 10px" underline="never">{{ $t('product.index.091251-26') }}</el-link>
      <el-form label-width="80px">
        <el-form-item :label="$t('product.index.091251-27')">
          <el-radio-group v-model="form.datatype">
            <el-radio
              v-for="dict in iot_device_chip"
              :key="dict.value"
              :value="dict.value"
              style="margin-top: 15px; width: 160px"
            >
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="downloadSdk" disabled>{{ $t('product.index.091251-28') }}</el-button>
          <el-button @click="cancel">{{ $t('product.index.091251-29') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="uploadImport.title" v-model="uploadImport.open" width="500px" append-to-body>
      <el-upload
        ref="uploadImportRef"
        :limit="1"
        accept=".json"
        :headers="uploadImport.headers"
        :action="uploadImport.url"
        :disabled="uploadImport.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :before-upload="handleBeforeUpload"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          {{ $t('dragFileTips') }}
          <em>{{ $t('clickFileTips') }}</em>
        </div>
        <template #tip>
          <div class="el-upload__tip" style="color: red">{{ $t('scada.topoMain.320129-1') }}</div>
        </template>
      </el-upload>
      <el-link
        type="primary"
        underline="never"
        style="font-size: 14px; margin-top: 10px; vertical-align: baseline"
        @click="importTemplate"
      >
        <el-icon><Download /></el-icon>
        {{ $t('product.index.091251-43') }}
      </el-link>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">{{ $t('confirm') }}</el-button>
          <el-button @click="uploadImport.open = false">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 新增产品弹出层 -->
    <product-add ref="productAddRef" @success="getList" />
    <!-- 产品复制功能选择产品弹出层 -->
    <ProductListDialog
      ref="productListRef"
      :productId="form.productId"
      :showSenior="queryParams.showSenior"
      @productEvent="getProductData($event)"
    />
    <!-- 组态分享对话框，组态特有 -->
     <scada-share-dialog ref="scadaShareDialog" :guid="scadaGuid" :type="1" :productId="productId" v-model:visible="isScadaShare"></scada-share-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onActivated, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import {
  Search,
  Refresh,
  Plus,
  Upload,
  Download,
  QuestionFilled,
  DocumentCopy,
  UploadFilled,
} from '@element-plus/icons-vue';
import { listProduct, delProduct, changeProductStatus, deviceCount, copyProduct } from '@/api/iot/product';
import { delSipconfigByProductId } from '@/api/iot/sipConfig';
import { checkPermi } from '@/utils/permission';
import { getToken } from '@/utils/auth';
import defaultSettings from '@/settings';
import { useDict } from '@/utils/dict/useDict';
import productAdd from './product-add.vue';
import ProductListDialog from '../device/product-list.vue';
import ScadaShareDialog from '@/views/scada/common/ScadaShareDialog/index.vue'; // 组态特有

import gatewayImg from '@/assets/images/gateway.png';
import videoImg from '@/assets/images/video.png';
import productImg from '@/assets/images/product.png';

const { proxy } = getCurrentInstance() as any;
const {
  iot_yes_no,
  sub_gateway_type,
  iot_product_status,
  iot_device_type,
  iot_network_method,
  iot_vertificate_method,
  iot_device_chip,
} = useDict(
  'iot_yes_no',
  'sub_gateway_type',
  'iot_product_status',
  'iot_device_type',
  'iot_network_method',
  'iot_vertificate_method',
  'iot_device_chip'
);
const router = useRouter();
const route = useRoute();

const loading = ref(true);
const total = ref(0);
const productListData = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const showSearch = ref(true);
const isShowScada = defaultSettings.isShowScada;
const baseUrl = import.meta.env.VITE_APP_BASE_API;

const queryFormRef = ref<any>(null);
const uploadImportRef = ref<any>(null);
const productAddRef = ref<any>(null);
const productListRef = ref<any>(null);

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 12,
  showSenior: true,
  productName: null,
  categoryId: null,
  categoryName: null,
  tenantId: null,
  tenantName: null,
  isSys: null,
  status: null,
  deviceType: null,
  networkMethod: null,
});

const uploadImport = reactive<any>({
  open: false,
  title: '',
  isUploading: false,
  updateSupport: 0,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/iot/product/importJson',
});
const fileType = ['json'];
const form = reactive<any>({});
const isScadaShare = ref(false);
const productId = ref<any>(null);
const scadaGuid = ref('');
let uniqueId: any = null;

/** 查询产品列表 */
function getList() {
  loading.value = true;
  listProduct(queryParams).then((response: any) => {
    productListData.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleImport() {
  uploadImport.title = proxy.$t('product.index.091251-44');
  uploadImport.open = true;
}

function handleBeforeUpload(file: any) {
  if (fileType) {
    const fileName = file.name.split('.');
    const fileExt = fileName[fileName.length - 1];
    const isTypeOk = fileType.indexOf(fileExt) >= 0;
    if (!isTypeOk) {
      proxy.$modal.msgError(proxy.$t('product.index.091251-45'));
      return false;
    }
    return true;
  }
}

function handleFileUploadProgress() {
  uploadImport.isUploading = true;
}

function handleFileSuccess(res: any) {
  uploadImport.open = false;
  uploadImport.isUploading = false;
  uploadImportRef.value.clearFiles();
  ElMessageBox.alert(res.msg, proxy.$t('product.index.091251-46'), { dangerouslyUseHTMLString: true });
  getList();
}

function submitFileForm() {
  uploadImportRef.value.submit();
}

function importTemplate() {
  proxy.download.downloadProductJson();
}

function handleAddProduct() {
  productAddRef.value.openDialog();
}

function selectProduct() {
  productListRef.value.queryParams.status = '';
  productListRef.value.open = true;
  productListRef.value.getList();
}

function getProductData(product: any) {
  handleCopy(product.productId);
}

function handleCopy(pid: any) {
  copyProduct(pid)
    .then((response: any) => {
      proxy.$modal.msgSuccess(response.msg);
    })
    .catch(() => {});
  setTimeout(() => {
    getList();
  }, 2000);
}

async function getDeviceCountByProductId(pid: any) {
  return deviceCount(pid);
}

async function changeProductStatusFn(pid: any, status: number, deviceType: any) {
  let message = proxy.$t('product.index.091251-30');
  if (status == 2) {
    let hasPermission = checkPermi(['iot:product:add']);
    if (!hasPermission) {
      proxy.$modal.alertError(proxy.$t('product.index.091251-31'));
      return;
    }
    message = proxy.$t('product.index.091251-32');
  } else if (status == 1) {
    let hasPermission = checkPermi(['iot:product:edit']);
    if (!hasPermission) {
      proxy.$modal.alertError(proxy.$t('product.index.091251-31'));
      return;
    }
    let result = await getDeviceCountByProductId(pid);
    if ((result as any).data > 0) {
      message = proxy.$t('product.index.091251-33', [(result as any).data]);
    }
  }
  ElMessageBox.confirm(message, proxy.$t('product.index.091251-34'), {
    confirmButtonText: proxy.$t('product.index.091251-35'),
    cancelButtonText: proxy.$t('product.index.091251-29'),
    type: 'warning',
  })
    .then(() => {
      const data: any = { productId: pid, status, deviceType };
      changeProductStatus(data)
        .then((response: any) => {
          getList();
          proxy.$modal.alertSuccess(response.msg);
        })
        .catch(() => {});
    })
    .catch(() => {});
}

function handleViewDevice(pid: any) {
  router.push({ path: '/iot/device/list', query: { t: Date.now() as any, productId: pid } });
}

function handleGoToScada(row: any) {
  const { scadaId, guid, productId: pid } = row;
  if (guid) {
    const routeUrl = router.resolve({ path: '/scada/topo/editor', query: { id: scadaId, guid, type: 1 as any } });
    window.open(routeUrl.href, '_blank');
  } else {
    router.push({ path: '/scada/center/temp', query: { productId: pid } });
  }
}

function handleScadaShare(row: any) {
  if (row.guid) {
    isScadaShare.value = true;
    scadaGuid.value = row.guid;
    productId.value = row.productId;
  } else {
    ElMessage({ type: 'warning', message: proxy.$t('product.index.091251-41') });
  }
}

function cancel() {
  open.value = false;
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm(queryFormRef.value);
  handleQuery();
}

function handleGeneratorSDK() {
  title.value = proxy.$t('product.index.091251-38');
  open.value = true;
}

function downloadSdk() {
  proxy.download.zip('/iot/tool/genSdk?deviceChip=' + 1, 'fastbee-sdk');
}

function handleDelete(row: any) {
  const productIds = row.productId;
  let msg = '';
  proxy.$modal
    .confirm(proxy.$t('product.index.091251-39', [productIds]))
    .then(() => {
      delSipconfigByProductId(productIds).then(() => {});
      return delProduct(productIds).then((response: any) => {
        msg = response.msg;
      });
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(msg);
    })
    .catch(() => {});
}

function handleEditProduct(row: any) {
  let pid = 0;
  if (row != 0) {
    pid = row.productId;
  }
  router.push({ path: '/iot/product/edit', query: { productId: pid as any, pageNum: queryParams.pageNum as any } });
}

getList();

onActivated(() => {
  const time = route.query.t;
  if (time != null && time != uniqueId) {
    uniqueId = time;
    queryParams.pageNum = Number(route.query.pageNum);
    getList();
  }
});
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
.iot-product {
  padding: 20px;
  overflow-x: hidden;
  .card-search {
    margin-bottom: 15px;
    width: 100%;
    border-radius: 8px;
    .search-form {
      margin-bottom: -18px;
      padding: 3px 0 0 0;
    }
  }
  .product-card {
    border-radius: 8px;
    overflow-x: hidden;
    :deep(.el-row) {
      margin-left: -10px !important;
      margin-right: -10px !important;
      padding-left: 10px;
      padding-right: 10px;
      box-sizing: border-box;
    }
    .product-header {
      margin-bottom: 20px;
      .product-checkbox {
        line-height: 32px;
        margin: 0px 10px;
        .el-checkbox__label {
          color: #606266 !important;
          font-size: 14px;
          margin: 0px 5px 0 -10px;
        }
      }
    }
  }
  .product-item {
    margin-bottom: 20px;
    text-align: center;
  }
  .card-item {
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    overflow: hidden;
    :deep(.el-card__body) {
      overflow: hidden;
    }
    .item-title {
      display: flex;
      flex-direction: row;
      align-items: center;
      position: relative;
      padding: 18px;
      .img {
        height: 58px;
        width: 58px;
        border-radius: 10px;
      }
      .title {
        flex: 1;
        text-align: left;
        margin-left: 16px;
        .name {
          font-weight: 500;
          font-size: 16px;
          color: #303133;
          line-height: 22px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          cursor: pointer;
        }
        .tag {
          margin-top: 5px;
          span {
            font-weight: 200;
            border-radius: 2px;
            border: 1px solid #c0c4cc;
            background: #ffffff;
          }
        }
      }
      .status {
        position: absolute;
        right: -1px;
        top: 24px;
        .btn-published {
          padding: 3px !important;
          background: rgba(103, 194, 58, 0.2);
          color: #67c23a;
          border-radius: 2px 0px 0px 2px;
        }
        :deep(.is-plain:hover) {
          border-color: transparent !important;
        }
        .btn-unpublished {
          padding: 3px !important;
          background: rgba(192, 196, 204, 0.2);
          color: #c0c4cc;
          border-radius: 2px 0px 0px 2px;
          border: 1px solid #c0c4cc;
        }
      }
    }
    .card-item-desc {
      .card-item-desc-item {
        white-space: nowrap;
        .tag-item-success {
          color: #67c23a;
          border-radius: 2px;
          border: 1px solid #67c23a;
          background-color: #ffffff;
        }
        .tag-item-error {
          color: #c0c4cc;
          border-radius: 2px;
          border: 1px solid #c0c4cc;
          background-color: #ffffff;
        }
      }
    }
    .product-category {
      font-weight: 400;
      font-size: 13px;
      color: var(--el-color-primary);
      line-height: 18px;
      text-align: left;
    }
    .divider {
      margin: 10px 0 0;
      height: 1px;
      background: #dcdfe6;
    }
    .card-item-btns {
      height: 40px;
      display: flex;
      font-size: 12px;
      align-items: center;
      justify-content: space-evenly;
      .btn-item-line {
        width: 1px;
        font-size: 16px;
        color: #dcdfe6;
      }
    }
  }
}
</style>
