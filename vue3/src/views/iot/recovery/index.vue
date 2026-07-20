<template>
  <div class="data-center-history">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('dataCenter.history.384934-0')" name="device">
          <div class="device-wrap">
            <el-form
              v-show="showDevSearch"
              @submit.prevent
              :model="queryParams"
              ref="devQueryFormRef"
              :inline="true"
              label-width="68px"
            >
              <el-form-item prop="deviceName">
                <el-input
                  v-model="queryParams.deviceName"
                  :placeholder="$t('device.index.105953-1')"
                  clearable
                  style="width: 192px"
                  @keyup.enter="handleDevQuery"
                />
              </el-form-item>
              <el-form-item prop="serialNumber">
                <el-input
                  v-model="queryParams.serialNumber"
                  :placeholder="$t('device.index.105953-3')"
                  clearable
                  @keyup.enter="handleDevQuery"
                  style="width: 192px"
                />
              </el-form-item>
              <el-form-item prop="productId">
                <el-select
                  v-model="queryParams.productId"
                  :placeholder="$t('firmware.index.222541-2')"
                  clearable
                  filterable
                  style="width: 192px"
                >
                  <el-option
                    v-for="product in productShortList"
                    :key="product.id"
                    :label="product.name"
                    :value="product.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <div style="float: right; margin-right: 0">
                <el-button type="primary" :icon="Search" @click="handleDevQuery">{{ $t('search') }}</el-button>
                <el-button :icon="Refresh" @click="handleDevResetQuery">{{ $t('reset') }}</el-button>
              </div>
            </el-form>
            <el-row>
              <el-col :span="1.5">
                <el-button
                  plain
                  :icon="Delete"
                  :disabled="deviceMultiple"
                  @click="handleDelete"
                  v-hasPermi="['iot:recovery:delete']"
                >
                  {{ $t('del') }}
                </el-button>
              </el-col>
              <right-toolbar v-model:showSearch="showDevSearch" @queryTable="handleDevResetQuery"></right-toolbar>
              <el-col :span="24">
                <div style="margin-top: 16px">
                  <el-table
                    style="margin-top: 16px"
                    v-loading="loading"
                    :data="devDeviceList"
                    :border="false"
                    @selection-change="handleDeviceSelectionChange"
                    ref="multipleTableRef"
                    :row-key="getRowKeysDevice"
                  >
                    <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
                    <el-table-column :label="$t('device.index.105953-20')" align="center" prop="deviceId" width="60" />
                    <el-table-column
                      :label="$t('device.index.105953-0')"
                      align="left"
                      prop="deviceName"
                      min-width="200"
                    />
                    <el-table-column
                      :label="$t('device.index.105953-2')"
                      align="left"
                      prop="serialNumber"
                      min-width="180"
                    />
                    <el-table-column
                      :label="$t('device.index.105953-21')"
                      align="left"
                      prop="productName"
                      min-width="200"
                    />
                    <el-table-column :label="$t('device.index.105953-25')" align="center" prop="isShadow" width="80">
                      <template #default="scope">
                        <el-tag type="success" size="small" v-if="scope.row.isShadow == 1">
                          {{ $t('device.index.105953-26') }}
                        </el-tag>
                        <el-tag type="info" size="small" v-else>{{ $t('device.index.105953-27') }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('device.index.105953-29')" align="center" prop="rssi" width="60">
                      <template #default="scope">
                        <svg-icon v-if="scope.row.status == 3 && scope.row.rssi >= '-55'" icon-class="wifi_4" />
                        <svg-icon
                          v-else-if="scope.row.status == 3 && scope.row.rssi >= '-70' && scope.row.rssi < '-55'"
                          icon-class="wifi_3"
                        />
                        <svg-icon
                          v-else-if="scope.row.status == 3 && scope.row.rssi >= '-85' && scope.row.rssi < '-70'"
                          icon-class="wifi_2"
                        />
                        <svg-icon
                          v-else-if="scope.row.status == 3 && scope.row.rssi >= '-100' && scope.row.rssi < '-85'"
                          icon-class="wifi_1"
                        />
                        <svg-icon v-else icon-class="wifi_0" />
                      </template>
                    </el-table-column>
                    <el-table-column
                      :label="$t('device.index.105953-31')"
                      align="center"
                      prop="firmwareVersion"
                      width="80"
                    >
                      <template #default="scope">
                        <el-tag size="small" type="info">Ver {{ scope.row.firmwareVersion }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('device.index.105953-32')" align="center" prop="activeTime" width="120">
                      <template #default="scope">
                        <span>{{ parseTime(scope.row.activeTime, '{y}-{m}-{d}') }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      :label="$t('template.index.891112-118')"
                      align="center"
                      prop="createBy"
                      width="120"
                    />
                    <el-table-column :label="$t('device.index.105953-33')" align="center" prop="createTime" width="120">
                      <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" :label="$t('device.index.105953-34')" align="center" width="140">
                      <template #default="scope">
                        <el-button
                          link
                          size="small"
                          :icon="RefreshLeft"
                          @click="handleRestoreDevice(scope.row)"
                          v-hasPermi="['iot:recovery:restore']"
                        >
                          {{ $t('device.allot-import-dialog.060657-18') }}
                        </el-button>
                        <el-button
                          link
                          size="small"
                          :icon="Delete"
                          @click="handleDelete(scope.row)"
                          v-hasPermi="['iot:recovery:delete']"
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
                    v-model:page="queryParams.pageNum"
                    v-model:limit="queryParams.pageSize"
                    :page-sizes="[12, 24, 36, 60]"
                    @pagination="getDevDeviceList"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('device.allot-import-dialog.060657-0')" name="product">
          <div class="product-wrap">
            <div v-show="showProductSearch" class="form-wrap">
              <el-form
                @submit.prevent
                :model="productQueryParams"
                ref="productQueryFormRef"
                :inline="true"
                label-width="68px"
              >
                <el-form-item prop="productName">
                  <el-input
                    v-model="productQueryParams.productName"
                    :placeholder="$t('product.index.091251-1')"
                    clearable
                    @keyup.enter="handleProductQuery"
                  />
                </el-form-item>
              </el-form>
              <div class="search-btn-group">
                <el-button type="primary" :icon="Search" @click="handleProductQuery">
                  {{ $t('dataCenter.history.384934-8') }}
                </el-button>
                <el-button :icon="Refresh" @click="handleProductResetQuery">
                  {{ $t('dataCenter.history.384934-9') }}
                </el-button>
              </div>
            </div>
            <el-row>
              <el-col :span="1.5">
                <el-button
                  plain
                  :icon="Delete"
                  :disabled="productMultiple"
                  @click="handleDeleteProduct"
                  v-hasPermi="['iot:recovery:delete']"
                >
                  {{ $t('del') }}
                </el-button>
              </el-col>
              <right-toolbar
                v-model:showSearch="showProductSearch"
                @queryTable="handleProductResetQuery"
              ></right-toolbar>
              <el-col :span="24">
                <div style="margin-top: 16px">
                  <el-table
                    style="margin-top: 16px"
                    v-loading="productLoading"
                    ref="singleTableRef"
                    :data="productList"
                    highlight-current-row
                    :border="false"
                    @selection-change="handleProductSelectionChange"
                    :row-key="getRowKeysProduct"
                  >
                    <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
                    <el-table-column
                      :label="$t('device.allot-record.155854-2')"
                      align="left"
                      prop="productName"
                      min-width="180"
                    />
                    <el-table-column
                      :label="$t('device.product-list.058448-6')"
                      align="left"
                      prop="categoryName"
                      min-width="150"
                    />
                    <el-table-column
                      :label="$t('device.product-list.058448-7')"
                      align="left"
                      prop="tenantName"
                      min-width="100"
                    />
                    <el-table-column
                      :label="$t('device.product-list.058448-8')"
                      align="left"
                      prop="status"
                      min-width="170"
                    >
                      <template #default="scope">
                        <el-tag type="success" v-if="scope.row.isAuthorize == 1">
                          {{ $t('device.product-list.058448-9') }}
                        </el-tag>
                        <el-tag type="info" v-if="scope.row.isAuthorize == 0">
                          {{ $t('device.product-list.058448-10') }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column fixed="right" :label="$t('device.index.105953-34')" align="center" width="200">
                      <template #default="scope">
                        <el-button
                          link
                          size="small"
                          :icon="RefreshLeft"
                          @click="handleRestoreProduct(scope.row)"
                          v-hasPermi="['iot:recovery:restore']"
                        >
                          {{ $t('device.allot-import-dialog.060657-18') }}
                        </el-button>
                        <el-button
                          link
                          size="small"
                          :icon="Delete"
                          @click="handleDeleteProduct(scope.row)"
                          v-hasPermi="['iot:recovery:delete']"
                        >
                          {{ $t('del') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <pagination
                    style="margin-bottom: 20px"
                    v-show="productTotal > 0"
                    :total="productTotal"
                    v-model:page="productQueryParams.pageNum"
                    v-model:limit="productQueryParams.pageSize"
                    :page-sizes="[12, 24, 36, 60]"
                    @pagination="getProductList"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('dataCenter.history.384934-14')" name="scene">
          <div class="product-wrap">
            <div v-show="showSceneSearch" class="form-wrap">
              <el-form
                @submit.prevent
                :model="sceneQueryParams"
                ref="sceneQueryFormRef"
                :inline="true"
                label-width="68px"
              >
                <el-form-item prop="sceneModelName">
                  <el-input
                    v-model="sceneQueryParams.sceneModelName"
                    :placeholder="$t('scene.edit.202832-5')"
                    clearable
                    @keyup.enter="handleSceneQuery"
                  />
                </el-form-item>
              </el-form>
              <div class="search-btn-group">
                <el-button type="primary" :icon="Search" @click="handleSceneQuery">
                  {{ $t('dataCenter.history.384934-8') }}
                </el-button>
                <el-button :icon="Refresh" @click="handleSceneResetQuery">
                  {{ $t('dataCenter.history.384934-9') }}
                </el-button>
              </div>
            </div>
            <el-row>
              <el-col :span="1.5">
                <el-button
                  plain
                  :icon="Delete"
                  :disabled="sceneMultiple"
                  @click="handleDeleteScene"
                  v-hasPermi="['iot:recovery:delete']"
                >
                  {{ $t('del') }}
                </el-button>
              </el-col>
              <right-toolbar v-model:showSearch="showSceneSearch" @queryTable="handleSceneResetQuery"></right-toolbar>
              <el-col :span="24">
                <div style="margin-top: 16px">
                  <el-table
                    style="margin-top: 16px"
                    v-loading="sceneLoading"
                    :data="sceneList"
                    highlight-current-row
                    :border="false"
                    @selection-change="handleSceneSelectionChange"
                    :row-key="getRowKeysScene"
                  >
                    <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
                    <el-table-column
                      :label="$t('scene.edit.202832-1')"
                      align="left"
                      prop="sceneModelName"
                      width="200"
                    />
                    <el-table-column :label="$t('scene.index.670805-8')" align="center" prop="status" width="100">
                      <template #default="scope">
                        <el-tag :type="scope.row.status === 0 ? 'danger' : 'success'">
                          {{ scope.row.status === 0 ? $t('scene.list.index.079839-2') : $t('scene.edit.202832-18') }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('scene.edit.202832-2')" align="center" prop="deptName" width="200" />
                    <el-table-column
                      :label="$t('scene.list.index.079839-3')"
                      align="center"
                      prop="deviceTotal"
                      width="100"
                    />
                    <el-table-column
                      :label="$t('scene.list.index.079839-4')"
                      align="left"
                      prop="sceneDesc"
                      width="210"
                    />
                    <el-table-column
                      :label="$t('scene.list.index.079839-5')"
                      align="center"
                      prop="createBy"
                      min-width="110"
                    />
                    <el-table-column
                      :label="$t('scene.list.index.079839-6')"
                      align="left"
                      prop="updateTime"
                      width="160"
                    />
                    <el-table-column fixed="right" :label="$t('device.index.105953-34')" align="center" width="200">
                      <template #default="scope">
                        <el-button
                          link
                          size="small"
                          :icon="RefreshLeft"
                          @click="handleRestoreScene(scope.row)"
                          v-hasPermi="['iot:recovery:restore']"
                        >
                          {{ $t('device.allot-import-dialog.060657-18') }}
                        </el-button>
                        <el-button
                          link
                          size="small"
                          :icon="Delete"
                          @click="handleDeleteScene(scope.row)"
                          v-hasPermi="['iot:recovery:delete']"
                        >
                          {{ $t('del') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <pagination
                    style="margin-bottom: 20px"
                    v-show="sceneTotal > 0"
                    :total="sceneTotal"
                    v-model:page="sceneQueryParams.pageNum"
                    v-model:limit="sceneQueryParams.pageSize"
                    :page-sizes="[12, 24, 36, 60]"
                    @pagination="getSceneList"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { Search, Refresh, Delete, RefreshLeft } from '@element-plus/icons-vue';
import {
  deletedDeviceList,
  restoreDevice,
  deldeletedDevice,
  deletedProductList,
  restoreProduct,
  deldeletedProduct,
  deletedSceneList,
  restoreScene,
  deldeletedScene,
} from '@/api/iot/recovery';
import { listShortProduct } from '@/api/iot/product';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;

const activeTab = ref('device');
const showDevSearch = ref(true);
const showProductSearch = ref(true);
const showSceneSearch = ref(true);
const devDeviceList = ref<any[]>([]);
const productList = ref<any[]>([]);
const sceneList = ref<any[]>([]);
const total = ref(0);
const productTotal = ref(0);
const sceneTotal = ref(0);
const productMultiple = ref(true);
const deviceMultiple = ref(true);
const sceneMultiple = ref(true);
const productIds = ref<any[]>([]);
const deviceIds = ref<any[]>([]);
const sceneIds = ref<any[]>([]);
const productShortList = ref<any[]>([]);
const loading = ref(false);
const productLoading = ref(false);
const sceneLoading = ref(false);
const multipleTableRef = ref<any>(null);
const singleTableRef = ref<any>(null);
const devQueryFormRef = ref<any>(null);

const queryParams = reactive({
  deviceName: null as any,
  serialNumber: null as any,
  productId: null as any,
  pageNum: 1,
  pageSize: 12,
});
const productQueryParams = reactive({ productName: null as any, pageNum: 1, pageSize: 12 });
const sceneQueryParams = reactive({ sceneModelName: null as any, pageNum: 1, pageSize: 12 });

onMounted(() => {
  getDevDeviceList();
  getProductList();
  getSceneList();
  getProductShortList();
});

function getDevDeviceList() {
  loading.value = true;
  deletedDeviceList(queryParams).then((res: any) => {
    if (res.code === 200) {
      devDeviceList.value = res.rows;
      total.value = res.total;
    }
    loading.value = false;
  });
}
function getProductList() {
  productLoading.value = true;
  deletedProductList(productQueryParams).then((res: any) => {
    if (res.code === 200) {
      productList.value = res.rows;
      productTotal.value = res.total;
    }
    productLoading.value = false;
  });
}
function getProductShortList() {
  listShortProduct().then((response: any) => {
    productShortList.value = response.data;
  });
}
function getSceneList() {
  sceneLoading.value = true;
  deletedSceneList(sceneQueryParams).then((res: any) => {
    if (res.code === 200) {
      sceneList.value = res.rows;
      sceneTotal.value = res.total;
    }
    sceneLoading.value = false;
  });
}

function handleRestoreDevice(item: any) {
  proxy.$modal
    .confirm(proxy.$t('device.device-functionlog.399522-31', [item.deviceId]))
    .then(() => {
      restoreDevice({ deviceId: item.deviceId }).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-30'));
          getDevDeviceList();
        }
      });
    })
    .catch(() => {});
}
function handleDelete(item?: any) {
  const deviceId = item?.deviceId || deviceIds.value;
  proxy.$modal
    .confirm(proxy.$t('device.index.105953-45', [deviceId]))
    .then(() => {
      deldeletedDevice(deviceId).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-26'));
          getDevDeviceList();
          multipleTableRef.value?.clearSelection();
        }
      });
    })
    .catch(() => {});
}
function handleRestoreProduct(item: any) {
  proxy.$modal
    .confirm(proxy.$t('device.device-functionlog.399522-31', [item.productId]))
    .then(() => {
      restoreProduct({ productId: item.productId }).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-30'));
          getProductList();
        }
      });
    })
    .catch(() => {});
}
function handleDeleteProduct(item?: any) {
  const productId = item?.productId || productIds.value;
  proxy.$modal
    .confirm(proxy.$t('product.product-sub.3843945-9', [productId]))
    .then(() => {
      deldeletedProduct(productId).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-26'));
          getProductList();
          singleTableRef.value?.clearSelection();
        }
      });
    })
    .catch(() => {});
}
function handleRestoreScene(item: any) {
  proxy.$modal
    .confirm(proxy.$t('device.device-functionlog.399522-31', [item.sceneModelId]))
    .then(() => {
      restoreScene({ sceneModelId: item.sceneModelId }).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-30'));
          getSceneList();
        }
      });
    })
    .catch(() => {});
}
function handleDeleteScene(item?: any) {
  const sceneId = item?.sceneModelId || sceneIds.value;
  proxy.$modal
    .confirm(proxy.$t('product.product-sub.3843945-9', [sceneId]))
    .then(() => {
      deldeletedScene(sceneId).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('device.device-functionlog.399522-26'));
          getSceneList();
        }
      });
    })
    .catch(() => {});
}

function handleProductSelectionChange(selection: any[]) {
  productIds.value = selection.map((item) => item.productId);
  productMultiple.value = !selection.length;
}
function handleDeviceSelectionChange(selection: any[]) {
  deviceIds.value = selection.map((item) => item.deviceId);
  deviceMultiple.value = !selection.length;
}
function handleSceneSelectionChange(selection: any[]) {
  sceneIds.value = selection.map((item) => item.sceneModelId);
  sceneMultiple.value = !selection.length;
}

function handleDevQuery() {
  queryParams.pageNum = 1;
  getDevDeviceList();
}
function handleDevResetQuery() {
  devQueryFormRef.value?.resetFields();
  queryParams.deviceName = null;
  queryParams.serialNumber = null;
  queryParams.productId = null;
  handleDevQuery();
}
function handleProductQuery() {
  getProductList();
}
function handleProductResetQuery() {
  proxy.resetForm('productQueryFormRef');
  productQueryParams.productName = null;
  getProductList();
}
function handleSceneQuery() {
  sceneQueryParams.pageNum = 1;
  getSceneList();
}
function handleSceneResetQuery() {
  proxy.resetForm('sceneQueryFormRef');
  sceneQueryParams.sceneModelName = null;
  getSceneList();
}

function getRowKeysDevice(row: any) {
  return row.deviceId;
}
function getRowKeysProduct(row: any) {
  return row.productId;
}
function getRowKeysScene(row: any) {
  return row.sceneModelId;
}
</script>

<style lang="scss" scoped>
.data-center-history {
  padding: 20px;
  .form-wrap {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-end;
    .search-btn-group {
      display: flex;
      flex-direction: row;
      margin-bottom: 22px;
    }
  }
  .device-wrap {
    margin-top: 5px;
  }
  .product-wrap {
    margin-top: 5px;
  }
}
</style>
