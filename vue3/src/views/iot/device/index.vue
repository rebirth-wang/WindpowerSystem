<template>
  <div class="iot-device">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="75px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="deviceName">
          <el-input
            v-model="queryParams.deviceName"
            :placeholder="$t('device.index.105953-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="serialNumber">
          <el-input
            v-model="queryParams.serialNumber"
            :placeholder="$t('device.index.105953-3')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('device.index.105953-5')"
            clearable
            style="width: 192px"
          >
            <el-option v-for="dict in iot_device_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>

        <el-form-item prop="groupId" v-show="searchShow">
          <el-select
            v-model="queryParams.groupId"
            :placeholder="$t('device.index.105953-7')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="group in myGroupList"
              :key="group.groupId"
              :label="group.groupName"
              :value="group.groupId"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="deviceType" v-show="searchShow">
          <el-select
            v-model="queryParams.deviceType"
            :placeholder="$t('product.product-edit.473153-13')"
            filterable
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="dict in iot_device_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>

        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button link @click="searchChange">
            <span class="search-toggle-text">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon class="search-toggle-icon">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="device-card">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-dropdown v-if="checkPermi(['iot:device:add'])" @command="handleCommand">
            <el-button type="primary" plain :icon="Plus">
              {{ $t('device.index.105953-10') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="checkPermi(['iot:device:add'])" command="handleEditDevice">
                  {{ $t('device.index.105953-11') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:add'])" command="handleBatchImport">
                  {{ $t('device.index.105953-12') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown v-if="checkPermi(['iot:device:assignment'])" @command="handleCommand1">
            <el-button plain :icon="FolderOpened">
              {{ $t('device.index.105953-13') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="checkPermi(['iot:device:assignment'])" command="handleSelectAllot">
                  {{ $t('device.index.105953-14') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:assignment'])" command="handleImportAllot">
                  {{ $t('device.index.105953-15') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:assignment'])" command="handleUserAllot">
                  {{ $t('device.index.105953-63') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" @click="recycleDevice" v-hasPermi="['iot:device:recovery']" class="btn-wrap">
            {{ $t('device.index.105953-16') }}
          </el-button>
        </el-col>
        <el-col :span="1.5" v-if="showType == 'list'">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:device:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown
            @command="handleCommandMore"
            v-hasPermi="['iot:device:record:list', 'iot:device:batchGenerator']"
          >
            <el-button plain class="btn-wrap" :icon="MoreFilled">
              {{ $t('device.index.105953-48') }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="checkPermi(['iot:device:record:list'])" command="importRecord">
                  {{ $t('device.index.105953-49') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:batchGenerator'])" command="generateSerialNumber">
                  {{ $t('device.index.105953-50') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:record:list'])" command="recycleRecord">
                  {{ $t('device.index.105953-51') }}
                </el-dropdown-item>
                <el-dropdown-item v-if="checkPermi(['iot:device:record:list'])" command="allotRecord">
                  {{ $t('device.index.105953-52') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>
        <el-col :span="1.5">
          <el-dropdown @command="handleCommandSortType">
            <el-button plain class="btn-wrap" :icon="Sort">
              {{ sortType }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="dict in device_sort" :command="Number(dict.value)" :key="dict.value">
                  {{ dict.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-col>

        <div class="button-group">
          <el-tooltip effect="dark" :content="$t('device.index.105953-67')" placement="top">
            <el-button
              size="small"
              :icon="Menu"
              @click="handleShowCard"
              :class="['toggle-button card-button', { active: isListView }]"
            />
          </el-tooltip>
          <div class="separator"></div>
          <!-- 分割线 -->
          <el-tooltip effect="dark" :content="$t('device.index.105953-68')" placement="top">
            <el-button
              size="small"
              :icon="Grid"
              @click="handleShowList"
              :class="['toggle-button list-button', { active: !isListView }]"
            />
          </el-tooltip>
        </div>
        <right-toolbar style="margin-left: 0" v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
      <!-- list形式 -->
      <div v-if="showType == 'list'" style="margin-top: 16px">
        <el-table
          v-loading="loading"
          :data="deviceList"
          :border="false"
          ref="multipleTableRef"
          @selection-change="handleSelectionChange"
          :row-key="getRowKeys"
        >
          <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
          <el-table-column :label="$t('device.index.105953-20')" align="center" prop="deviceId" width="60" />
          <el-table-column :label="$t('device.index.105953-0')" align="left" prop="deviceName" min-width="200" />
          <el-table-column :label="$t('device.index.105953-2')" align="left" prop="serialNumber" min-width="180" />
          <el-table-column :label="$t('device.index.105953-21')" align="left" prop="productName" min-width="190" />
          <el-table-column :label="$t('device.index.105953-59')" align="center" prop="deptName" min-width="190" />
          <el-table-column :label="$t('device.index.105953-22')" align="center" prop="transport" min-width="90" />
          <el-table-column
            :label="$t('device.index.105953-23')"
            align="center"
            prop="protocolCode"
            :show-overflow-tooltip="true"
            min-width="120"
          />
          <el-table-column :label="$t('device.index.105953-25')" align="center" prop="isShadow" width="80">
            <template #default="scope">
              <el-tag type="success" size="small" v-if="scope.row.isShadow == 1">
                {{ $t('device.index.105953-26') }}
              </el-tag>
              <el-tag type="info" size="small" v-else>{{ $t('device.index.105953-27') }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('device.index.105953-28')" align="center" prop="status" width="80">
            <template #default="scope">
              <dict-tag :options="iot_device_status" :value="scope.row.status" size="small" />
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
          <el-table-column :label="$t('device.index.105953-30')" align="center" prop="locationWay" width="100">
            <template #default="scope">
              <dict-tag :options="iot_location_way" :value="scope.row.locationWay" size="small" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('device.index.105953-31')" align="center" prop="firmwareVersion" width="80">
            <template #default="scope">
              <el-tag size="small" type="info">Ver {{ scope.row.firmwareVersion }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('device.index.105953-32')" align="center" prop="activeTime" width="120">
            <template #default="scope">
              <span>{{ parseTime(scope.row.activeTime, '{y}-{m}-{d}') || 'xxxx-xx-xx' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="100" />
          <el-table-column :label="$t('device.index.105953-33')" align="center" prop="createTime" width="120">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>

          <el-table-column fixed="right" :label="$t('device.index.105953-34')" align="center" width="200">
            <template #default="scope">
              <el-button
                link
                size="small"
                :icon="View"
                @click="handleEditDevice(scope.row)"
                v-hasPermi="['iot:device:query']"
              >
                {{ $t('device.index.105953-60') }}
              </el-button>
              <el-tooltip :content="$t('device.index.105953-62')" placement="top" v-if="!scope.row.canSeeCode">
                <el-button
                  link
                  size="small"
                  :icon="Picture"
                  @click="openSummaryDialog(scope.row)"
                  v-hasPermi="['iot:device:query']"
                >
                  {{ $t('device.index.105953-37') }}
                </el-button>
              </el-tooltip>
              <el-button
                link
                size="small"
                :icon="Picture"
                @click="openSummaryDialog(scope.row)"
                v-if="scope.row.canSeeCode"
                v-hasPermi="['iot:device:query']"
              >
                {{ $t('device.index.105953-37') }}
              </el-button>
              <el-button
                link
                size="small"
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['iot:device:remove']"
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
          :pageSizes="[12, 24, 36, 60]"
          @pagination="getList"
        />
      </div>
      <!-- 卡片形式 -->
      <div v-if="showType == 'card'" style="margin-top: 16px">
        <el-row :gutter="20" v-loading="loading">
          <el-col
            :xs="24"
            :sm="12"
            :md="12"
            :lg="8"
            :xl="6"
            v-for="(item, index) in deviceList"
            :key="index"
            class="device-item"
          >
            <el-card :body-style="{ padding: '0px' }" shadow="always" class="card-item">
              <div class="item-title">
                <div>
                  <el-image
                    class="img"
                    :preview-src-list="[baseUrl + item.imgUrl]"
                    :src="baseUrl + item.imgUrl"
                    fit="cover"
                    v-if="item.imgUrl != null && item.imgUrl != ''"
                  ></el-image>
                  <el-image
                    class="img"
                    :preview-src-list="[gatewayImg]"
                    :src="gatewayImg"
                    fit="cover"
                    v-else-if="item.deviceType == 2"
                  ></el-image>
                  <el-image
                    class="img"
                    :preview-src-list="[videoImg]"
                    :src="videoImg"
                    fit="cover"
                    v-else-if="item.deviceType == 3"
                  ></el-image>
                  <el-image
                    class="img"
                    :preview-src-list="[productImg]"
                    :src="productImg"
                    fit="cover"
                    v-else
                  ></el-image>
                </div>
                <div class="title">
                  <div class="name" @click="handleDeviceDetail(item)">{{ item.deviceName }}</div>
                  <div class="tag-wrap">
                    <dict-tag
                      :options="iot_device_status"
                      :value="item.status"
                      size="small"
                      style="line-height: 15px"
                    />
                    <el-tag
                      class="tag-ellipsis"
                      style="margin-left: 6px"
                      v-if="item.protocolCode"
                      type="info"
                      size="small"
                    >
                      {{ item.protocolCode }}
                    </el-tag>
                    <el-tag
                      class="tag-ellipsis"
                      style="margin-left: 6px"
                      v-if="item.transport"
                      type="info"
                      size="small"
                    >
                      {{ item.transport }}
                    </el-tag>
                    <el-tag
                      class="tag-ellipsis"
                      style="margin-left: 6px"
                      v-if="item.alertCount"
                      type="danger"
                      size="small"
                    >
                      {{ $t('device.index.105953-53') }}{{ item.alertCount.unprocessedCount }}
                    </el-tag>
                  </div>
                </div>
                <div class="status">
                  <div class="icon-wrap">
                    <el-tooltip :content="$t('device.index.105953-62')" placement="top" v-if="!item.canSeeCode">
                      <svg-icon
                        class="item"
                        style="cursor: pointer"
                        icon-class="qrcode"
                        @click="openSummaryDialog(item)"
                        v-hasPermi="['iot:device:query']"
                      />
                    </el-tooltip>
                    <svg-icon
                      class="item"
                      style="cursor: pointer"
                      icon-class="qrcode"
                      @click="openSummaryDialog(item)"
                      v-if="item.canSeeCode"
                      v-hasPermi="['iot:device:query']"
                    />
                    <svg-icon class="item" v-if="item.status == 3 && item.rssi >= '-55'" icon-class="wifi_4" />
                    <svg-icon
                      class="item"
                      v-else-if="item.status == 3 && item.rssi >= '-70' && item.rssi < '-55'"
                      icon-class="wifi_3"
                    />
                    <svg-icon
                      class="item"
                      v-else-if="item.status == 3 && item.rssi >= '-85' && item.rssi < '-70'"
                      icon-class="wifi_2"
                    />
                    <svg-icon
                      class="item"
                      v-else-if="item.status == 3 && item.rssi >= '-100' && item.rssi < '-85'"
                      icon-class="wifi_1"
                    />
                    <svg-icon class="item" v-else icon-class="wifi_0" />
                  </div>
                </div>
              </div>

              <el-row :gutter="10">
                <el-col :span="14" style="padding: 0px 0 0 30px">
                  <div class="card-item-desc">
                    <span class="desc-label">{{ $t('device.index.105953-20') }}：</span>
                    <span class="desc-content">{{ item.serialNumber }}</span>
                  </div>
                </el-col>
                <el-col :span="10" style="padding: 0px 30px 0 0px">
                  <div class="card-item-desc">
                    <span class="desc-label">{{ $t('device.index.105953-59') }}：</span>
                    <span class="desc-content">{{ item.deptName }}</span>
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="14" style="padding: 0px 0 0 30px">
                  <div class="card-item-desc">
                    <span class="desc-label">{{ $t('device.index.105953-39') }}：</span>
                    <span class="desc-content">{{ item.productName }}</span>
                  </div>
                </el-col>
                <el-col :span="10" style="padding: 0px 30px 0 0px">
                  <div class="card-item-desc">
                    <span class="desc-label">{{ $t('device.index.105953-32') }}：</span>
                    <span class="desc-content">{{ parseTime(item.activeTime, '{y}-{m}-{d}') || 'xxxx-xx-xx' }}</span>
                  </div>
                </el-col>
              </el-row>
              <el-divider class="divider" />

              <div
                style="height: 39px; padding: 0 10px; display: flex; align-items: center; justify-content: space-evenly"
              >
                <el-button size="small" link @click="handleEditDevice(item, 'basic')" v-hasPermi="['iot:device:query']">
                  {{ $t('device.index.105953-36') }}
                </el-button>
                <span
                  style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                  v-hasPermi="['iot:device:query']"
                >
                  |
                </span>
                <el-button
                  size="small"
                  link
                  @click="handleRunDevice(item)"
                  v-hasPermi="['iot:device:query']"
                  v-if="item.deviceType !== 3"
                >
                  {{ $t('device.index.105953-40') }}
                </el-button>
                <el-button size="small" link @click="handleRunDevice(item)" v-hasPermi="['iot:device:query']" v-else>
                  {{ $t('device.device-edit.148398-44') }}
                </el-button>
                <span
                  style="width: 1px; margin: 0px 10px; font-size: 16px; color: #dcdfe6"
                  v-hasPermi="['iot:device:remove']"
                >
                  |
                </span>
                <el-button size="small" link @click="handleDelete(item)" v-hasPermi="['iot:device:remove']">
                  {{ $t('device.index.105953-35') }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty :description="$t('device.index.105953-41')" v-if="total == 0"></el-empty>
        <pagination
          style="margin: 0 0 20px 0"
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          :pageSizes="[12, 24, 36, 60]"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 二维码 -->
    <el-dialog class="qr-dialog" v-model="openSummary" width="300px" append-to-body>
      <template #header>
        <div class="title-wrap">
          <div class="name">{{ $t('device.index.105953-42') }}</div>
          <el-icon class="btn" v-print="{ id: 'qr', popTitle: '' }"><Printer /></el-icon>
        </div>
      </template>
      <div id="qr" style="text-align: center; margin: 0 auto">
        <vue-qr :text="qrText" :size="200"></vue-qr>
      </div>
    </el-dialog>

    <el-dialog :title="$t('device.index.105953-54')" v-model="openGenerate" width="500px" append-to-body>
      <el-form :model="elFormData">
        <el-form-item label-width="95px" :label="$t('device.index.105953-55')">
          <el-input
            v-model="elFormData.count"
            type="number"
            style="width: 310px"
            :max="200"
            oninput="
              if (value > 200) value = 200;
              if (value < 0) value = 0;
            "
          ></el-input>
        </el-form-item>
        <el-form-item label-width="180px" :label="$t('device.index.105953-64')">
          <el-radio v-model="elFormData.type" :value="3">{{ $t('device.index.105953-65') }}</el-radio>
          <el-radio v-model="elFormData.type" :value="1">{{ $t('device.index.105953-66') }}</el-radio>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-hasPermi="['iot:device:batchGenerator']">
            {{ $t('device.index.105953-56') }}
          </el-button>
          <el-button @click="openGenerate = false">{{ $t('device.index.105953-57') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 批量导入设备 -->
    <batchImport ref="batchImportRef" @save="saveDialog"></batchImport>
    <!-- 导入分配 -->
    <allotImport ref="allotImportRef" @save="saveAllotDialog"></allotImport>
    <!-- 导入记录 -->
    <importRecord ref="importRecordRef"></importRecord>
    <!-- 设备回收记录 -->
    <recycleRecord ref="recycleRecordRef"></recycleRecord>
    <!-- 设备分配记录 -->
    <allotRecord ref="allotRecordRef"></allotRecord>
    <!-- 新增设备 -->
    <deviceAdd ref="deviceAddRef" @success="getList"></deviceAdd>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onActivated, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import {
  Search,
  Refresh,
  Plus,
  FolderOpened,
  Delete,
  MoreFilled,
  Sort,
  Menu,
  Grid,
  View,
  Picture,
  ArrowDown,
  ArrowUp,
  Printer,
} from '@element-plus/icons-vue';
import { ElLoading, ElMessage, ElMessageBox } from 'element-plus';
import { listDeviceShort, deleteDevice } from '@/api/iot/device';
import { checkPermi } from '@/utils/permission';
import { parseTime } from '@/utils/ruoyi';
import { listGroup } from '@/api/iot/group';
import { deleteSipDevice } from '@/api/iot/sipdevice';
import gatewayImg from '@/assets/images/gateway.png';
import videoImg from '@/assets/images/video.png';
import productImg from '@/assets/images/product.png';
import useDict from '@/utils/dict/useDict';
import auth from '@/plugins/auth';
import vueQr from 'vue-qr';
import Treeselect from 'vue3-treeselect';
import 'vue3-treeselect/dist/vue3-treeselect.css';
import batchImport from './batch-import-dialog.vue';
import allotImport from './allot-import-dialog.vue';
import importRecord from './import-record.vue';
import recycleRecord from './recycle-record.vue';
import allotRecord from './allot-record.vue';
import deviceAdd from './device-add.vue';

const { iot_device_status, iot_is_enable, iot_location_way, iot_transport_type, iot_device_type, device_sort } =
  useDict(
    'iot_device_status',
    'iot_is_enable',
    'iot_location_way',
    'iot_transport_type',
    'iot_device_type',
    'device_sort'
  );

const { proxy } = getCurrentInstance()!;
const router = useRouter();

// 二维码内容
const qrText = ref('fastbee');
const openSummary = ref(false); // 打开设备配置对话框
const openGenerate = ref(false);
const showSearch = ref(true); // 显示搜索条件
const searchShow = ref(false); //搜索展开
const showType = ref('card'); // 展示方式
const ids = ref<number[]>([]); // 选中数组
const loading = ref(true); // 遮罩层
const single = ref(true); // 非单个禁用
const multiple = ref(true);
const total = ref(0); // 总条数
const deviceList = ref<any[]>([]); // 设备列表数据
const myGroupList = ref<any[]>([]); // 我的分组列表数据
const baseUrl = ref(import.meta.env.VITE_APP_BASE_API); // 根路径
const isListView = ref(true); // 默认选中列表视图
const sortType = ref(proxy?.$t('system.menu.034890-4'));

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  deviceName: null as string | null,
  productId: null as number | null,
  groupId: null as number | null,
  productName: null as string | null,
  userId: null as number | null,
  userName: null as string | null,
  tenantId: null as number | null,
  tenantName: null as string | null,
  serialNumber: null as string | null,
  status: null as number | null,
  networkAddress: null as string | null,
  activeTime: null as string | null,
  sortType: null as number | null,
  deviceType: null as number | null,
});

const elFormData = reactive({
  count: 1,
  type: 1,
});

// 批量导入参数
const isSubDev = ref(false);

const queryFormRef = ref();
const multipleTableRef = ref();
const batchImportRef = ref();
const allotImportRef = ref();
const importRecordRef = ref();
const recycleRecordRef = ref();
const allotRecordRef = ref();
const deviceAddRef = ref();

// 初始化表格数据
onMounted(() => {
  // 产品筛选
  let productId = router.currentRoute.value.query.productId;
  if (productId != null) {
    queryParams.productId = Number(productId);
    queryParams.groupId = null;
    queryParams.serialNumber = null;
  }
  // 分组筛选
  let groupId = router.currentRoute.value.query.groupId;
  if (groupId != null) {
    queryParams.groupId = Number(groupId);
    queryParams.productId = null;
    queryParams.serialNumber = null;
  }
  // 设备编号筛选
  let sn = router.currentRoute.value.query.sn;
  if (sn != null) {
    queryParams.serialNumber = sn as string;
    queryParams.productId = null;
    queryParams.groupId = null;
  }
  // 设备分组筛选
  let searchShowParam = router.currentRoute.value.query.searchShow;
  if (searchShowParam) {
    searchShow.value = !!searchShowParam;
  }
  connectMqtt();
  getGroupList();
});

// 页面激活时调用
onActivated(() => {
  const time = router.currentRoute.value.query.t;
  if (time != null) {
    // 页码筛选
    let pageNum = router.currentRoute.value.query.pageNum;
    if (pageNum != null) {
      queryParams.pageNum = Number(pageNum);
    }
    // 产品筛选
    let productId = router.currentRoute.value.query.productId;
    if (productId != null) {
      queryParams.productId = Number(productId);
      queryParams.groupId = null;
      queryParams.serialNumber = null;
    }
    // 分组筛选
    let groupId = router.currentRoute.value.query.groupId;
    if (groupId != null) {
      queryParams.groupId = Number(groupId);
      queryParams.productId = null;
      queryParams.serialNumber = null;
    }
    // 设备编号筛选
    let sn = router.currentRoute.value.query.sn;
    if (sn != null) {
      queryParams.serialNumber = sn as string;
      queryParams.productId = null;
      queryParams.groupId = null;
    }
    getList();
  }
});

async function connectMqtt() {
  if (proxy?.$mqttTool.client == null) {
    await proxy?.$mqttTool.connect();
  }
  // 添加message事件监听器
  mqttCallback();
  getList();
}

/* Mqtt回调处理  */
function mqttCallback() {
  proxy?.$mqttTool.client.on('message', (topic: string, message: any, buffer: any) => {
    let topics = topic.split('/');
    let productId = topics[1];
    let deviceNum = topics[2];
    message = JSON.parse(message.toString());
    if (!message) {
      return;
    }
    if (topics[3] == 'status') {
      console.log(proxy?.$t('device.index.105953-43'), topic);
      console.log(proxy?.$t('device.index.105953-44'), message);
      // 更新列表中设备的状态
      for (let i = 0; i < deviceList.value.length; i++) {
        if (deviceList.value[i].serialNumber == deviceNum) {
          deviceList.value[i].status = message.status;
          deviceList.value[i].isShadow = message.isShadow;
          deviceList.value[i].rssi = message.rssi;
          return;
        }
      }
    }
  });
}

// 新增设备更多操作触发
function handleCommand(command: string) {
  switch (command) {
    case 'handleEditDevice':
      handleAddDevice();
      break;
    case 'handleBatchImport':
      handleBatchImport();
      break;
    default:
      break;
  }
}

//新增设备
function handleAddDevice() {
  deviceAddRef.value.open = true;
  deviceAddRef.value.reset();
}

//批量导入设备
function handleBatchImport() {
  batchImportRef.value.upload.importDeviceDialog = true;
  batchImportRef.value.importForm.productName = null;
}

//导入分配设备
function handleImportAllot() {
  allotImportRef.value.upload.importAllotDialog = true;
  allotImportRef.value.allotForm.productName = null;
  allotImportRef.value.allotForm.deptId = null;
}

// dialog 保存响应
function saveDialog() {
  getList();
}

// dialog 保存响应
function saveAllotDialog() {
  getList();
}

// 分配设备更多操作触发
function handleCommand1(command: string) {
  switch (command) {
    case 'handleSelectAllot':
      handleSelectAllot();
      break;
    case 'handleUserAllot':
      handleUserAllot();
      break;
    case 'handleImportAllot':
      handleImportAllot();
      break;
    default:
      break;
  }
}

// 设备排序
function handleCommandSortType(command: number) {
  queryParams.sortType = command;
  getList();
  switch (command) {
    case 1:
      sortType.value = proxy?.$t('device.device-functionlog.399522-32');
      break;
    case 2:
      sortType.value = proxy?.$t('device.device-functionlog.399522-33');
      break;
    case 3:
      sortType.value = proxy?.$t('device.device-functionlog.399522-34');
      break;
    case 4:
      sortType.value = proxy?.$t('device.device-functionlog.399522-35');
      break;
    default:
      break;
  }
}

function nameSelect() {
  deviceList.value = [...deviceList.value].sort((a, b) => a.deviceName.localeCompare(b.deviceName));
  sortType.value = proxy?.$t('device.device-functionlog.399522-27');
}

function timeSelect() {
  deviceList.value = [...deviceList.value].sort((a, b) => b.createTime.localeCompare(a.createTime));
  console.log(deviceList.value);
  sortType.value = proxy?.$t('device.device-log.798283-14');
}

// 跳转选择分配设备页面
function handleSelectAllot() {
  router.push({
    path: '/iot/device/selectAllot',
  });
}

// 跳转用户分配设备页面
function handleUserAllot() {
  router.push({
    path: '/iot/device/userAllot',
  });
}

// 跳转回收设备页面
function recycleDevice() {
  router.push({
    path: '/iot/device/recycle',
  });
}

// 更多操作
function handleCommandMore(command: string) {
  switch (command) {
    case 'importRecord':
      handleImportRecord();
      break;
    case 'generateSerialNumber':
      generateSerialNumber();
      break;
    case 'recycleRecord':
      handleRecycleRecord();
      break;
    case 'allotRecord':
      handleAllotRecord();
      break;
    default:
      break;
  }
}

//导入记录
function handleImportRecord() {
  importRecordRef.value.open = true;
  importRecordRef.value.getList();
  importRecordRef.value.getProductList();
}

//设备回收记录
function handleRecycleRecord() {
  recycleRecordRef.value.open = true;
  recycleRecordRef.value.getList();
  recycleRecordRef.value.getProductList();
  recycleRecordRef.value.getDeptTree();
}

//设备分配记录
function handleAllotRecord() {
  allotRecordRef.value.open = true;
  allotRecordRef.value.getList();
  allotRecordRef.value.getProductList();
  allotRecordRef.value.getDeptTree();
}

// 生成设备编号
function generateSerialNumber() {
  openGenerate.value = true;
}

// 生成设备编号确定操作
function submitForm() {
  proxy?.$download(
    'iot/device/batchGenerator',
    {
      ...elFormData,
    },
    `batchGenerator_${new Date().getTime()}.xlsx`
  );
  openGenerate.value = false;
}

function openSummaryDialog(row: any) {
  if (hasPermission(row)) {
    let json = {
      type: 1, // 1=扫码关联设备
      deviceNumber: row.serialNumber,
      productId: row.productId,
      productName: row.productName,
    };
    qrText.value = JSON.stringify(json);
    openSummary.value = true;
  }
}

//判断权限
function hasPermission(item: any) {
  return item.canSeeCode;
}

/* 订阅消息 */
function mqttSubscribe(list: any[]) {
  // 订阅当前页面设备状态和实时监测
  let topics: string[] = [];
  for (let i = 0; i < list.length; i++) {
    let topicStatus = '/' + '+' + '/' + list[i].serialNumber + '/status/post';
    topics.push(topicStatus);
  }
  proxy?.$mqttTool.subscribe(topics);
}

// 查询设备分组列表
function getGroupList() {
  const params = {
    pageNum: 1,
    pageSize: 99999,
  };
  listGroup(params).then((res: any) => {
    if (res.code === 200) {
      myGroupList.value = res.rows;
    }
  });
}

/** 查询所有简短设备列表 */
function getList() {
  loading.value = true;
  listDeviceShort(queryParams).then((response: any) => {
    deviceList.value = response.rows;
    total.value = response.total;
    // 订阅消息
    if (deviceList.value && deviceList.value.length > 0) {
      mqttSubscribe(deviceList.value);
    }
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.productId = null;
  queryParams.groupId = null;
  queryParams.serialNumber = null;
  queryParams.sortType = null;
  sortType.value = proxy?.$t('system.menu.034890-4');
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

/** 切换显示方式 */
function handleChangeShowType() {
  showType.value = showType.value == 'card' ? 'list' : 'card';
}

function handleShowList() {
  isListView.value = false; // 切换到列表视图
  showType.value = 'list'; // 切换显示方式为列表视图
  console.log('切换到列表视图');
}

function handleShowCard() {
  isListView.value = true; // 切换到卡片视图
  showType.value = 'card'; // 切换显示方式为列表视图

  console.log('切换到卡片视图');
}

// 点击名称查看
function handleDeviceDetail(item: any) {
  if (auth.hasPermi('iot:device:query')) {
    handleEditDevice(item);
  }
}

/** 修改按钮操作 */
function handleEditDevice(row: any, activeName?: string) {
  let deviceId: number | number[] = 0;
  let isSubDev: number = 0;
  if (row != 0) {
    deviceId = row.deviceId || ids.value;
    isSubDev = row.subDeviceCount > 0 ? 1 : 0;
  }
  router.push({
    path: '/iot/device/edit',
    query: {
      deviceId: deviceId,
      isSubDev: isSubDev,
      pageNum: queryParams.pageNum,
      activeName: activeName,
    },
  });
}

/** 运行状态按钮操作 */
function handleRunDevice(row: any) {
  let deviceId: number | number[] = 0;
  let isSubDev: number = 0;
  if (row != 0) {
    deviceId = row.deviceId || ids.value;
    isSubDev = row.subDeviceCount > 0 ? 1 : 0;
  }
  router.push({
    path: '/iot/device/edit',
    query: {
      deviceId: deviceId,
      isSubDev: isSubDev,
      pageNum: queryParams.pageNum,
      activeName: row.deviceType === 3 ? 'sipChannel' : 'runningStatus',
    },
  });
}

/** 删除按钮操作 */
function handleDelete(row: any) {
  const { deviceType, serialNumber } = row;
  const loading = ElLoading.service({ lock: true });
  const deviceIds = row.deviceId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('device.index.105953-45', [deviceIds]))
    .then(() => {
      if (deviceType === 3) {
        return deleteSipDevice(serialNumber);
      } else {
        return deleteDevice(deviceIds);
      }
    })
    .then(() => {
      loading.close();
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.index.105953-47'));
      multipleTableRef.value.clearSelection();
    })
    .catch(() => {
      loading.close();
    });
}

/** 未启用设备影子*/
function shadowUnEnable(device: any, thingsModel: any) {
  // 1-未激活，2-禁用，3-在线，4-离线
  if (device.status != 3 && device.isShadow == 0) {
    return true;
  }
  if (thingsModel.isReadonly) {
    return true;
  }
  return false;
}

// 搜索展开隐藏
function searchChange() {
  searchShow.value = !searchShow.value;
}

// 多选框选中数据
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.deviceId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function getRowKeys(row: any) {
  return row.deviceId;
}
</script>

<style lang="scss" scoped>
.iot-device {
  padding: 20px;
  overflow-x: hidden;

  :deep(.el-upload-dragger) {
    width: 510px;
  }

  :deep(.el-dropdown-menu__item) {
    font-size: 12px;
  }

  .search-toggle-text {
    color: var(--el-color-primary);
    margin-left: 14px;
  }

  .search-toggle-icon {
    color: var(--el-color-primary);
    margin-left: 10px;
  }
  .device-card {
    overflow-x: hidden;
    :deep(.el-row) {
      margin-left: -10px !important;
      margin-right: -10px !important;
      padding-left: 10px;
      padding-right: 10px;
      box-sizing: border-box;
    }
  }

  .device-item {
    margin-bottom: 20px;
    text-align: center;

    .card-item {
      background: #ffffff;
      border-radius: 8px;
      border: 1px solid #dcdfe6;
      overflow: hidden;
      :deep(.el-card__body) {
        overflow: hidden;
      }

      .card-item-desc {
        display: flex;
        align-items: center;
        margin-top: 10px;
        .desc-label {
          flex-shrink: 0;
          white-space: nowrap;
          font-size: 14px;
          color: #909399;
        }
        .desc-content {
          min-width: 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          font-size: 14px;
          color: #606266;
        }
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
            white-space: nowrap; /* 不换行 */
            overflow: hidden; /* 隐藏超出部分 */
            text-overflow: ellipsis; /* 使用省略号 */
            cursor: pointer;
          }

          .tag-wrap {
            margin-top: 10px;
            display: flex;
            flex-direction: row;
            align-items: center;
            max-width: 95%;
            overflow: hidden;
            :deep(.dict-tag .el-tag) {
              margin-bottom: 0px;
            }
          }
          .tag-ellipsis {
            min-width: 0;
            flex-shrink: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
        .status {
          position: absolute;
          right: -1px;
          top: 24px;

          .icon-wrap {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding: 0 8px;

            .item {
              width: 22px;
              height: 22px;
              margin: 0 5px;
              color: #909399;
            }
          }
        }
      }
    }
  }

  .btn-wrap {
    background-color: #fff !important;
    color: #606266 !important;
    border-radius: 4px !important;
    border: 1px solid #dcdfe6 !important;
  }
}

.divider {
  margin: 20px 0 0;
  height: 1px;
  background: #dcdfe6;
}

.button-group {
  width: 65px;
  height: 32.5px;
  display: flex; /* 让按钮在同一行显示 */
  align-items: center; /* 垂直居中对齐 */
  margin-left: auto; /* 靠右对齐 */
  background: #ffffff;
  margin-right: 12px;

  .toggle-button {
    width: 32.5px;
    height: 32.5px;
    display: flex; /* 使用 flexbox */
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
    border: 1px solid #dcdfe6;
    background: #fff;
  }

  :deep(.el-button:hover),
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

.qr-dialog {
  width: 100%;

  .title-wrap {
    width: 100%;
    position: relative;

    .name {
      width: 100%;
    }

    .btn {
      position: absolute;
      top: 0px;
      right: 10px;
      cursor: pointer;
      font-size: 14px;
      color: #909399;
    }
  }

  :deep(.el-dialog__body) {
    padding: 2px 20px 20px;
  }
}
</style>
